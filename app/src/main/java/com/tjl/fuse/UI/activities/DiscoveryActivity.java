package com.tjl.fuse.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.tjl.fuse.R;
import com.tjl.fuse.models.Album;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.utils.preferences.StringPreference;
import com.tjl.fuse.web.HypemAPI;
import java.util.ArrayList;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryActivity extends AppCompatActivity {
  HypemAPI hypemAPI;
  SpotifyService spotify;
  PlayerManager playerManager;
  ArrayList<FuseTrack> fuseTracks;

  public static String EXTRA_PLAYLIST_VALUE = "EXTRA_PLAYLIST_VALUE";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.discovery_list_activity);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    hypemAPI = HypemAPI.getInstance(getApplicationContext());
    fuseTracks = new ArrayList<>();

    playerManager = PlayerManager.getInstance();
    //spotify
    String tokenKey = getApplicationContext().getString(R.string.spotify_token_key);
    String token = new StringPreference(getApplicationContext(), tokenKey).get();

    SpotifyApi api = new SpotifyApi();
    api.setAccessToken(token);

    spotify = api.getService();

    handleIntent(getIntent());
  }

  public void handleIntent(Intent intent) {
    setIntent(intent);

    String channel = getIntent().getStringExtra(EXTRA_PLAYLIST_VALUE);

    if (channel != null) {
      setTitle(channel);
      switch (channel) {
        case "Hype Machine": {

          hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
            @Override public void success(Album[] albums, Response response) {

              for (Album album : albums) {
                fuseTracks.add(new FuseTrack(album));
              }
            }

            @Override public void failure(RetrofitError error) {

            }
          });
          {

          }
          break;
        }
        case "Spotify": {
          spotify.getMe(new Callback<UserPrivate>() {
            @Override public void success(final UserPrivate userPrivate, Response response) {
              spotify.getPlaylists(userPrivate.id, new Callback<Pager<PlaylistSimple>>() {
                @Override
                public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {

                  //spotifyTracks.add();
                  Timber.e("this us the playlist name" + playlistSimplePager.items.get(0).name);
                  Timber.e(
                      "This is the track info? " + playlistSimplePager.items.get(0).tracks.href);
                  Timber.e("This is the track info? " + playlistSimplePager.items.get(0).uri);
                  spotify.getPlaylist(userPrivate.id, playlistSimplePager.items.get(0).id,
                      new Callback<Playlist>() {
                        @Override public void success(Playlist playlist, Response response) {
                          ArrayList<FuseTrack> spotifyTracks = new ArrayList<>();
                          for (PlaylistTrack playlistTrack : playlist.tracks.items) {
                            spotifyTracks.add(new FuseTrack(playlistTrack.track));
                          }
                        }

                        @Override public void failure(RetrofitError error) {

                        }
                      });
                }

                @Override public void failure(RetrofitError error) {

                }
              });
            }

            @Override public void failure(RetrofitError error) {

            }
          });
          break;
        }
      }
    }
  }

  @Override public void onNewIntent(Intent newIntent) {
    handleIntent(newIntent);
  }
}
