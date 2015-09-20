package com.tjl.fuse.player.tracks;

import com.tjl.fuse.models.Album;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lubecki.soundcloud.webapi.android.models.Track;
import timber.log.Timber;

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

  public FuseTrack() {
    // Do nothing. Only use to deserialize parse.
  }

  public FuseTrack(Track track) {
    this.title = track.title;
    this.primary_artist = track.user.username;
    this.play_uri = track.uri;
    this.image_url = track.artwork_url;

    if(image_url == null) {
      image_url = "http://icons.iconarchive.com/icons/xenatt/the-circle/256/App-Soundcloud-icon.png";
    }
    this.type = Type.SOUNDCLOUD;
  }


  public FuseTrack(Album hypemAlbum) {
    this.title = hypemAlbum.HypemTracks[0].title;
    this.primary_artist = hypemAlbum.artist;
    this.play_uri = hypemAlbum.HypemTracks[0].streamUrl;

    Pattern pattern = Pattern.compile("tracks/(.*?)/stream");
    Matcher matcher = pattern.matcher(play_uri);

    this.play_uri = matcher.matches() ? matcher.group(1) : play_uri;

    Timber.i("Stream URI = " + play_uri);

    this.image_url = hypemAlbum.artworkUrl;

    if(image_url == null) {
      image_url = "http://static.hypem.com/images/logos/heart-200.png";
    }
    this.type = Type.SOUNDCLOUD;
  }



  public FuseTrack(kaaes.spotify.webapi.android.models.Track track) {
    this.title = track.name;
    Timber.i(title);
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

    if(track.album.images.size() > 0) {
      this.image_url = track.album.images.get(0).url;
    } else {
      this.image_url =
          "http://www.icons101.com/icons/17/Mitu_icon_pack_2_by_scope66/128/spotify.png";
    }

    this.type = Type.SPOTIFY;
  }

  public enum Type {
    SPOTIFY("spotify"),
    SOUNDCLOUD("soundcloud");

    private final String value;

    Type(String value) {
      this.value = value;
    }

    private static HashMap<String, Type> types = new HashMap<>();

    static {
      for (Type type : values()) {
        types.put(type.value, type);
      }
    }

    public static Type fromString(String type) {
      return types.get(type);
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
