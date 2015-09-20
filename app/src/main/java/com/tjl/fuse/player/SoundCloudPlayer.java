package com.tjl.fuse.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import java.io.IOException;
import timber.log.Timber;

import static com.tjl.fuse.player.State.*;

/**
 * Created by Jacob on 9/18/15.
 */
public class SoundCloudPlayer extends FusePlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

  private MediaPlayer player;

  public SoundCloudPlayer() {
    player = new MediaPlayer();
  }

  public void setListener(MediaPlayer.OnCompletionListener listener) {
    player.setOnCompletionListener(listener);
  }

  public void start(String uri) {

    uri += "/stream?client_id=55de8cc1d6246dd72e0a78b1c70fd91a";

    Timber.i("SoundcloudPlayer: " + uri);

    try {
      player.reset();
      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
      player.setDataSource(uri);
      player.prepareAsync();
      changeState(State.PREPARING);
      player.setOnPreparedListener(this);
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void reset() {
    changeState(STOPPED);
    player.reset();
  }

  public void stopPreparing() {
    changeState(STOPPED);
    player.reset();
  }

  @Override public void play() {
    switch (state) {
      case PLAYING:
        Timber.w("Tried to play but was already playing.");
        break;

      case PAUSED:
        changeState(PLAYING);
        player.start();
        break;

      case STOPPED:
        Timber.w("Tried to play a stopped player.");
        break;
    }
  }

  @Override public void pause() {
    switch (state) {
      case PLAYING:
        changeState(PAUSED);
        player.pause();
        break;

      case PAUSED:
        Timber.w("Tried to pause but was already paused.");
        break;

      case STOPPED:
        Timber.w("Tried to play a stopped player.");
        break;
    }
  }

  @Override public void release() {
    changeState(STOPPED);
    player.release();
  }

  @Override public void onCompletion(MediaPlayer mediaPlayer) {
     changeState(STOPPED);
  }

  @Override public void onPrepared(MediaPlayer mediaPlayer) {
    if(PlayerManager.getInstance().checkHasAudioFocus()){
      if (state != State.STOPPED) {
        player.start();
        changeState(State.PLAYING);
      }
    }
  }
}
