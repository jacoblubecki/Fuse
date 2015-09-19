package com.tjl.fuse.player.tracks;

import com.tjl.fuse.FuseApplication;
import java.util.List;
import java.util.Random;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class Queue {

  private List<FuseTrack> tracks;
  private int index = 0;
  private boolean looping = false;
  private boolean shuffling = false;

  public Queue(List<FuseTrack> tracks) {
    this.tracks = tracks;
  }

  public void setShuffling(boolean shuffling) {
    this.shuffling = shuffling;
  }

  public void setLooping(boolean looping) {
    this.looping = looping;
  }

  public void addTrackNextInQueue(FuseTrack track) {
    tracks.add(1, track);
  }

  public FuseTrack previous() {
    previousTrack();
    return tracks.get(index);
  }

  public FuseTrack next() {
    nextTrack();
    return tracks.get(index);
  }

  private void previousTrack() {
    if(shuffling) {
      index = (int) (Math.random() * tracks.size());
    } else {
      index--;
    }

    if (index < 0) {
      if (looping) {
        index = tracks.size() - 1;
      } else {
        index = 0;
      }
    }

    Timber.i("Moved to queue index: " + index);
  }

  private void nextTrack() {
    if(shuffling) {
      index = (int) (Math.random() * tracks.size());
    } else {
      index++;
    }

    if (index > tracks.size() - 1) {
      if (looping) {
        index = 0;
      } else if (index == tracks.size() - 1) {
        index--;
      }
    }

    Timber.i("Moved to queue index: " + index);
  }
}
