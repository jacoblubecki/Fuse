package com.tjl.fuse;

/**
 * Created by Jacob on 9/19/15.
 */
public class Fuse {

  public static void light(Flame source) {
    switch (source) {
      case ALL:
        loadAllDiscover();
        break;

      case AROUND_ME:
        loadAroundMeDiscover();
        break;

      case HYPEM:
        loadHypemDiscover();
        break;

      case SPOTIFY:
        loadSpotifyDiscover();
        break;

      case SOUNDCLOUD:
        loadSoundCloudDiscover();
        break;
    }
  }

  private static void loadAllDiscover() {

  }

  private static void loadAroundMeDiscover() {

  }

  private static void loadHypemDiscover() {

  }

  private static void loadSpotifyDiscover() {

  }

  private static void loadSoundCloudDiscover() {

  }

  public enum Flame {
    ALL,
    AROUND_ME,
    HYPEM,
    SPOTIFY,
    SOUNDCLOUD
  }
}
