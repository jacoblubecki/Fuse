package com.tjl.fuse.player;

/**
 * Created by Jacob on 9/18/15.
 */
public enum State {
  /**
   * Playing music.
   */
  PLAYING("playing"),

  /**
   * Music loaded but not playing.
   */
  PAUSED("paused"),

  /**
   * Music not loaded.
   */
  STOPPED("stopped"),

  /**
   * Track has been requested to load but has not started playing.
   */
  PREPARING("preparing");

  private final String state;

  State(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }
}
