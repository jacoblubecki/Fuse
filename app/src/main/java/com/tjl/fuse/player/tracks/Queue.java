package com.tjl.fuse.player.tracks;

import java.util.Iterator;
import java.util.List;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class Queue implements Iterable<FuseTrack> {

  private List<FuseTrack> tracks;
  private int index = 0;
  private boolean looping = false;
  private boolean shuffling = false;

  private boolean reachedEnd = false;

  public Queue(List<FuseTrack> tracks) {
    this.tracks = tracks;
  }

  public void setShuffling(boolean shuffling) {
    this.shuffling = shuffling;
  }

  public void setLooping(boolean looping) {
    this.looping = looping;
  }

  public int getSize() {
    return tracks.size();
  }

  public FuseTrack current() {
    if(tracks.size() > index) {
      return tracks.get(index);
    } else {
      return null;
    }
  }

  public List<FuseTrack> getTracks() {
    return tracks;
  }

  public void addTrackNextInQueue(FuseTrack track) {
    tracks.add(1, track);
  }

  public FuseTrack previous() {
    previousTrack();

    reachedEnd = false;

    return tracks.get(index);
  }

  public FuseTrack next() {
    nextTrack();
    return tracks.get(index);
  }

  public FuseTrack getPrevious() {
    int tempIndex = index - 1;
    tempIndex = tempIndex < 0 ? 0 : tempIndex;
    return tracks.get(tempIndex);
  }

  public FuseTrack getNext() {
    int tempIndex = index + 1;
    tempIndex = tempIndex > getSize() - 1 ? (looping ? 0 : getSize() - 1) : tempIndex;
    return tracks.get(tempIndex);
  }

  private void previousTrack() {
    if (shuffling) {
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
    if (shuffling) {
      index = (int) (Math.random() * tracks.size());
    } else {
      index++;
    }

    if (index > tracks.size() - 1) {
      if (looping) {
        index = 0;
      } else {
        index = tracks.size() - 1;
        reachedEnd = true;
      }
    }

    Timber.i("Moved to queue index: " + index);
  }

  public boolean endReached() {
    return reachedEnd;
  }

  @Override public Iterator<FuseTrack> iterator() {
    return tracks.iterator();
  }

  public void setCurrent(int current) {
    if (index >= 0 && index < tracks.size()) {
      Timber.i("Setting current index of the queue to " + current);

      this.index = current;

      reachedEnd = false;
    }
  }

  public int getCurrent() {
    return index;
  }
}
