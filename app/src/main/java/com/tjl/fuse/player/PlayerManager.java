package com.tjl.fuse.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class PlayerManager
    implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

  private SpotifyPlayer spotify;
  private SoundCloudPlayer soundcloud;

  private Queue queue;
  private FuseTrack currentTrack;

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
    this.queue = queue;

    if (queue.getSize() > 0) {
      currentTrack = queue.current();
    } else {
      Timber.w("Nothing in queue to play.");
    }
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

  public void play() {
    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        switch (currentTrack.type) {
          case SPOTIFY:
            if (spotify.state == State.PAUSED) {
              spotify.play();
            } else {
              spotify.start(currentTrack.play_uri);
            }
            break;

          case SOUNDCLOUD:
            if (soundcloud.state == State.PAUSED) {
              soundcloud.play();
            } else {
              soundcloud.start(currentTrack.play_uri);
            }
            break;

          default:
            Timber.e("Unrecognized track type: " + currentTrack.type);
            break;
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
    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        currentTrack = queue.next();

        switch (currentTrack.type) {
          case SPOTIFY:
            spotify.start(currentTrack.play_uri);
            break;

          case SOUNDCLOUD:
            soundcloud.start(currentTrack.play_uri);
            break;

          default:
            Timber.e("Tried to play a track of unknown type.");
            break;
        }
      } else {
        Timber.e("Can't play next. Fuse doesn't have audio focus.");
      }
    } else {
      Timber.e("Tried to move next on null Queue.");
    }
  }

  public void previous() {
    if (queue != null && queue.getSize() > 0) {
      if (checkHasAudioFocus()) {
        currentTrack = queue.previous();

        switch (currentTrack.type) {
          case SPOTIFY:
            spotify.start(currentTrack.play_uri);
            break;

          case SOUNDCLOUD:
            soundcloud.start(currentTrack.play_uri);
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

  public void release() {
    soundcloud.release();
    spotify.release();
    instance = null;
  }

  public void notifyDataSetChanged() {
    if(queue.getSize() > 0) {
      queue.notifyDataSetChanged();
      currentTrack = queue.current();
      play();
    } else {
      pause();
    }
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
      if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
        Timber.i("FOCUS LOST TRANSIENT");
        if (isPlaying()) {
          pause();
          wasPlayingAtTransientLoss = isPlaying();
        }
      } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
        if (wasPlayingAtTransientLoss) {
          switch (currentTrack.type) {
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
        }
      } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        pause();
      }

      if (wasPlayingAtTransientLoss) {
        // Launch service
      }
    }
  }
}
