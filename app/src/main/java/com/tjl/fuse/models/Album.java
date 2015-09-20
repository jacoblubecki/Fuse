package com.tjl.fuse.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class Album implements Serializable {

  @SerializedName("album") public String album;
  @SerializedName("artist") public String artist;
  @SerializedName("artwork_url") public String artworkUrl;
  @SerializedName("tracks") public HypemTrack[] HypemTracks;
}
