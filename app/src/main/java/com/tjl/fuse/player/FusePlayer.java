package com.tjl.fuse.player;

import android.support.annotation.NonNull;
import timber.log.Timber;

import static com.tjl.fuse.player.State.*;

/**
 * Created by Jacob on 9/18/15.
 */
public abstract class FusePlayer {
  protected State state = STOPPED;

  public void changeState(@NonNull State state) {
    this.state = state;
    Timber.i("Player state changed to: " + state.toString());
  }

  public abstract void play();

  public abstract void pause();

  public abstract void release();
}
