package com.tjl.fuse.player.tracks;

import lubecki.soundcloud.webapi.android.models.Track;

/**
 * Created by Jacob on 9/19/15.
 */
public class FuseTrack {
  public String title;
  public String primary_artist;
  public String artists;
  public String play_uri;
  public String image_url;
  public Type type;

  public FuseTrack(Track track) {
    this.title = track.title;
    this.primary_artist = track.user.username;
    this.play_uri = track.uri;
    this.image_url = track.artwork_url;
    this.type = Type.SOUNDCLOUD;
  }

  public FuseTrack(kaaes.spotify.webapi.android.models.Track track) {
    this.title = track.name;
    this.primary_artist = track.artists.get(0).name;

    StringBuilder artistList = new StringBuilder();
    for (int i = 0; i < track.artists.size(); i++) {
      artistList.append(track.artists.get(i).name);
      if (i < track.artists.size() - 1) {
        artistList.append(", ");
      }
    }
    this.artists = artistList.toString();

    this.play_uri = track.uri;
    this.image_url = track.album.images.get(0).url;
    this.type = Type.SPOTIFY;
  }

  public enum Type {
    SPOTIFY,
    SOUNDCLOUD
  }
}
