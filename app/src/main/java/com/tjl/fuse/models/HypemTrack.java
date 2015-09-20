package com.tjl.fuse.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class HypemTrack implements Serializable{
  @SerializedName("track_num") public int trackNum;
  @SerializedName("sc_id") public int scId;
  @SerializedName("title") public String title;
  @SerializedName("stream_url") public String streamUrl;
}
