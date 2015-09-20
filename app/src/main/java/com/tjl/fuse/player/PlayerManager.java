package com.tjl.fuse.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.parse.ParseObject;
import com.tjl.fuse.Fuse;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import com.tjl.fuse.service.Constants;
import com.tjl.fuse.service.ForegroundService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class PlayerManager
    implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

  private SpotifyPlayer spotify;
  private SoundCloudPlayer soundcloud;

  private Queue queue;

  private AudioManager manager;
  private boolean wasPlayingAtTransientLoss = false;

  private static PlayerManager instance;

  private PlayerManager() {
    soundcloud = new SoundCloudPlayer();
    spotify = new SpotifyPlayer(FuseApplication.getApplication());

    manager =
        (AudioManager) FuseApplication.getApplication().getSystemService(Context.AUDIO_SERVICE);

    soundcloud.setListener(this);
    spotify.setListener(this);
  }

  public static PlayerManager getInstance() {
    if (instance == null) {
      instance = new PlayerManager();
    }
    return instance;
  }

  public void setQueue(Queue queue) {
    Timber.i("Queue was set at position " + queue.getCurrent());
    this.queue = queue;
  }

  public Queue getQueue() {
    return queue;
  }

  public void setShuffling(boolean shuffling) {
    if (queue != null) {
      queue.setShuffling(shuffling);
    } else {
      Timber.e("Tried to shuffle null Queue.");
    }
  }

  public void setLooping(boolean looping) {
    if (queue != null) {
      queue.setLooping(looping);
    } else {
      Timber.e("Tried to loop null Queue.");
    }
  }

  public FuseTrack getPreviousTrack() {
    return queue.getPrevious();
  }

  public FuseTrack getNextTrack() {
    return queue.getNext();
  }

  public void play() {
    pause();

    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        if (queue.current() != null) {
          switch (queue.current().type) {
            case SPOTIFY:
              if (spotify.state == State.PAUSED) {
                spotify.play();
              } else {
                spotify.start(queue.current().play_uri);

                ParseObject track = new ParseObject("Track");
                track.put("title", queue.current().title);
                track.put("artists", queue.current().artists);
                track.put("trackId", queue.current().play_uri);
                track.put("primary_artist", queue.current().primary_artist);
                track.put("type", queue.current().type.toString());
                track.put("image_url", queue.current().image_url);
                track.saveInBackground();
              }
              break;

            case SOUNDCLOUD:
              if (soundcloud.state == State.PAUSED) {
                soundcloud.play();
              } else {
                soundcloud.start(queue.current().play_uri);

                String[] uriParts = queue.current().play_uri.split("/");
                if (uriParts.length > 3) {
                  String uri = uriParts[4];

                  Timber.i(uri);
                  ParseObject track = new ParseObject("Track");
                  track.put("title", queue.current().title);
                  track.put("artists", queue.current().artists == null ? "" : queue.current().artists);
                  track.put("trackId", uri);
                  track.put("primary_artist", queue.current().primary_artist);
                  track.put("type", queue.current().type.toString());
                  track.put("image_url", queue.current().image_url);
                  track.saveInBackground();
                } else {
                  Timber.i("No match.");
                }
              }
              break;

            default:
              Timber.e("Unrecognized track type: " + queue.current().type);
              break;
          }
        } else {
          Timber.e("Current track was null.");
        }

        FuseApplication app = FuseApplication.getApplication();
        if (app.isServiceRunning(ForegroundService.class)) {
          Intent startIntent =
              new Intent(FuseApplication.getApplication(), ForegroundService.class);
          startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
          app.startService(startIntent);
        }
      } else {
        Timber.e("Can't play. Fuse doesn't have audio focus.");
      }
    } else {
      Timber.e("Tried to play on an empty queue.");
    }
  }

  public void pause() {
      spotify.pause();
      soundcloud.pause();
  }

  public void next() {
    pause();
    soundcloud.stopPreparing();

    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        queue.next();

        if (!queue.endReached()) {

          switch (queue.current().type) {
            case SPOTIFY:
              spotify.start(queue.current().play_uri);
              break;

            case SOUNDCLOUD:
              soundcloud.start(queue.current().play_uri);
              break;

            default:
              Timber.e("Tried to play a track of unknown type.");
              break;
          }
        } else {
          Timber.i("The end of the queue was reached.");
          queue.setCurrent(0);
        }
      } else {
        Timber.e("Can't play next. Fuse doesn't have audio focus.");
      }
    } else {
      Timber.e("Tried to move next on null Queue.");
    }
  }

  public void previous() {
    pause();
    soundcloud.stopPreparing();

    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        queue.previous();

        switch (queue.current().type) {
          case SPOTIFY:
            spotify.start(queue.current().play_uri);
            break;

          case SOUNDCLOUD:
            soundcloud.start(queue.current().play_uri);
            break;

          default:
            Timber.e("Tried to play a track of unknown type.");
            break;
        }
      } else {
        Timber.e("Can't play previous. Fuse doesn't have audio focus.");
      }
    } else {
      Timber.e("Tried to move previous on null Queue.");
    }
  }

  public boolean isPlaying() {
    return spotify.state == State.PLAYING || soundcloud.state == State.PLAYING;
  }

  public void reset() {
    soundcloud.reset();
    spotify.reset();
  }

  public void release() {
    soundcloud.release();
    spotify.release();
    instance = null;
  }

  public boolean checkHasAudioFocus() {
    int result =
        manager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
  }

  @Override public void onCompletion(MediaPlayer mediaPlayer) {
    next();
    Timber.i("Moved next.");
  }

  @Override public void onAudioFocusChange(int focusChange) {
    if (spotify.state != State.STOPPED) {

      switch (focusChange) {
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
          Timber.i("FOCUS LOST TRANSIENT");
          if (isPlaying()) {
            pause();
            wasPlayingAtTransientLoss = isPlaying();
          }
          break;

        case AudioManager.AUDIOFOCUS_GAIN:
          Timber.i("FOCUS GAINED");

          switch (queue.current().type) {
            case SPOTIFY:
              spotify.play();
              break;

            case SOUNDCLOUD:
              soundcloud.play();
              break;

            default:
              Timber.e("Tried to play a track of unknown type.");
              break;
          }
          break;

        case AudioManager.AUDIOFOCUS_LOSS:
          Timber.i("FOCUS LOST");
          pause();
          break;

        default:
          Timber.i("AUDIO STATE CHANGE NOT HANDLED.");
          break;
      }

      if (wasPlayingAtTransientLoss) {
        Intent startIntent = new Intent(FuseApplication.getApplication(), ForegroundService.class);
        startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
        FuseApplication.getApplication().startService(startIntent);
      }
    }
  }
}
