package com.tjl.fuse.ui.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.spotify.sdk.android.authentication.LoginActivity;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.R;
import com.tjl.fuse.models.Album;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import com.tjl.fuse.service.Constants;
import com.tjl.fuse.service.ForegroundService;
import com.tjl.fuse.utils.preferences.StringPreference;
import com.tjl.fuse.web.HypemAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  private HypemAPI hypemAPI;
  private SpotifyService spotify;
  private PlayerManager playerManager;
  private ArrayList<FuseTrack> fuseTracks;

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
        case "Discover" :
          loadAll();
          break;

        case "Hype Machine": {
          loadHypeM();
          break;
        }
        case "Spotify": {
          loadSpotify();
          break;
        }

        case "Around Me":
          loadAroundMe();
          break;
      }
    }
  }

  @Override public void onNewIntent(Intent newIntent) {
    handleIntent(newIntent);
  }

  @Override public void onResume() {
    super.onResume();
    stopService();
  }

  @Override public void onPause() {
    super.onPause();
    FuseApplication.serializePlaylist();

    FuseApplication app = FuseApplication.getApplication();
    if (!(app.isServiceRunning(FuseActivity.class) || app.isServiceRunning(LoginActivity.class)) &&
        PlayerManager.getInstance().getQueue() != null &&
        PlayerManager.getInstance().getQueue().getSize() > 0 &&
        PlayerManager.getInstance().isPlaying()) {
      startService();
    }
  }

  @Override public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

    AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);

    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        manager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI);
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        manager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI);
        return true;

      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  public void startService() {
    Intent startIntent = new Intent(this, ForegroundService.class);
    startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
    startService(startIntent);
  }

  public void stopService() {
    Intent stopIntent = new Intent(this, ForegroundService.class);
    stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
    startService(stopIntent);

    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
  }

  private void loadHypeM() {
    hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
      @Override public void success(Album[] albums, Response response) {
        for (Album album : albums) {
          fuseTracks.add(new FuseTrack(album));
        }
      }

      @Override public void failure(RetrofitError error) {

      }
    });
  }

  private void loadSpotify() {
    spotify.getMe(new Callback<UserPrivate>() {
      @Override public void success(final UserPrivate userPrivate, Response response) {
        spotify.getPlaylists(userPrivate.id, new Callback<Pager<PlaylistSimple>>() {
          @Override
          public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
            //spotifyTracks.add();
            Timber.e("this us the playlist name" + playlistSimplePager.items.get(0).name);
            spotify.getPlaylist(playlistSimplePager.items.get(0).owner.id,
                playlistSimplePager.items.get(0).id, new Callback<Playlist>() {
                  @Override public void success(Playlist playlist, Response response) {
                    ArrayList<FuseTrack> spotifyTracks = new ArrayList<>();
                    for (PlaylistTrack playlistTrack : playlist.tracks.items) {
                      spotifyTracks.add(new FuseTrack(playlistTrack.track));
                      PlayerManager.getInstance().setQueue(new Queue(spotifyTracks));
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
  }

  private void loadAroundMe() {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(List<ParseObject> objects, ParseException e) {
        if (e == null) {
          ArrayList<FuseTrack> tracks = new ArrayList<>();
          for (ParseObject object : objects) {
            FuseTrack track = new FuseTrack();

            track.title = object.getString("title");
            track.artists = object.getString("artists");
            track.type = FuseTrack.Type.fromString(object.getString("type"));
            track.play_uri = object.getString("trackId");
            track.primary_artist = object.getString("primary_artist");
            track.image_url = object.getString("image_url");

            tracks.add(track);
          }

          updateView(tracks);
        } else {
          Timber.e(e, "Couldn't get Parse data.");
        }
      }
    });
  }

  private void updateView(ArrayList<FuseTrack> tracks) {
    Collections.shuffle(tracks);
  }

  private void loadAll() {
    final ArrayList<FuseTrack> tracks = new ArrayList<>();

    spotify.getMe(new Callback<UserPrivate>() {
      @Override public void success(final UserPrivate userPrivate, Response response) {
        spotify.getPlaylists(userPrivate.id, new Callback<Pager<PlaylistSimple>>() {
          @Override
          public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
            Timber.e("this us the playlist name" + playlistSimplePager.items.get(0).name);
            spotify.getPlaylist(playlistSimplePager.items.get(0).owner.id,
                playlistSimplePager.items.get(0).id, new Callback<Playlist>() {
                  @Override public void success(Playlist playlist, Response response) {
                    for (PlaylistTrack playlistTrack : playlist.tracks.items) {
                      tracks.add(new FuseTrack(playlistTrack.track));
                    }

                    hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
                      @Override public void success(Album[] albums, Response response) {
                        for (Album album : albums) {
                          tracks.add(new FuseTrack(album));
                        }

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                        query.findInBackground(new FindCallback<ParseObject>() {
                          public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                              for (ParseObject object : objects) {
                                FuseTrack track = new FuseTrack();

                                track.title = object.getString("title");
                                track.artists = object.getString("artists");
                                track.type = FuseTrack.Type.fromString(object.getString("type"));

                                track.play_uri = object.getString("trackId");

                                if(track.type == FuseTrack.Type.SOUNDCLOUD) {
                                  track.play_uri =
                                      "https://api.soundcloud.com/tracks/" + track.play_uri
                                          + "/stream";
                                }
                                track.primary_artist = object.getString("primary_artist");
                                track.image_url = object.getString("image_url");

                                tracks.add(track);
                              }

                              updateView(tracks);
                            }
                          }
                        });
                      }

                      @Override public void failure(RetrofitError error) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                        query.findInBackground(new FindCallback<ParseObject>() {
                          public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                              for (ParseObject object : objects) {
                                FuseTrack track = new FuseTrack();

                                track.title = object.getString("title");
                                track.artists = object.getString("artists");
                                track.type = FuseTrack.Type.fromString(object.getString("type"));
                                track.play_uri = object.getString("trackId");
                                track.primary_artist = object.getString("primary_artist");
                                track.image_url = object.getString("image_url");

                                tracks.add(track);
                              }

                              updateView(tracks);
                            } else {
                              Timber.e(e, "Couldn't get Parse data.");

                              updateView(tracks);
                            }
                          }
                        });
                      }
                    });
                  }

                  @Override public void failure(RetrofitError error) {
                    Timber.e(error, "Error getting Spotify.");
                    hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
                      @Override public void success(Album[] albums, Response response) {
                        for (Album album : albums) {
                          tracks.add(new FuseTrack(album));
                        }

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                        query.findInBackground(new FindCallback<ParseObject>() {
                          public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                              ArrayList<FuseTrack> tracks = new ArrayList<>();
                              for (ParseObject object : objects) {
                                FuseTrack track = new FuseTrack();

                                track.title = object.getString("title");
                                track.artists = object.getString("artists");
                                track.type = FuseTrack.Type.fromString(object.getString("type"));
                                track.play_uri = object.getString("trackId");
                                track.primary_artist = object.getString("primary_artist");
                                track.image_url = object.getString("image_url");

                                tracks.add(track);
                              }

                              updateView(tracks);
                            } else {
                              Timber.e(e, "Couldn't get Parse data.");

                              updateView(tracks);
                            }
                          }
                        });
                      }

                      @Override public void failure(RetrofitError error) {
                        Timber.e(error, "Error getting HypeM.");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                        query.findInBackground(new FindCallback<ParseObject>() {
                          public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                              ArrayList<FuseTrack> tracks = new ArrayList<>();
                              for (ParseObject object : objects) {
                                FuseTrack track = new FuseTrack();

                                track.title = object.getString("title");
                                track.artists = object.getString("artists");
                                track.type = FuseTrack.Type.fromString(object.getString("type"));
                                track.play_uri = object.getString("trackId");
                                track.primary_artist = object.getString("primary_artist");
                                track.image_url = object.getString("image_url");

                                tracks.add(track);
                              }

                              updateView(tracks);
                            } else {
                              Timber.e(e, "Couldn't get Parse data.");

                              updateView(tracks);
                            }
                          }
                        });
                      }
                    });
                  }
                });
          }

          @Override public void failure(RetrofitError error) {
            hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
              @Override public void success(Album[] albums, Response response) {
                for (Album album : albums) {
                  tracks.add(new FuseTrack(album));
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                query.findInBackground(new FindCallback<ParseObject>() {
                  public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                      for (ParseObject object : objects) {
                        FuseTrack track = new FuseTrack();

                        track.title = object.getString("title");
                        track.artists = object.getString("artists");
                        track.type = FuseTrack.Type.fromString(object.getString("type"));
                        track.play_uri = object.getString("trackId");
                        track.primary_artist = object.getString("primary_artist");
                        track.image_url = object.getString("image_url");

                        tracks.add(track);
                      }

                      updateView(tracks);
                    } else {
                      Timber.e(e, "Couldn't get Parse data.");

                      updateView(tracks);
                    }
                  }
                });
              }

              @Override public void failure(RetrofitError error) {
                hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
                  @Override public void success(Album[] albums, Response response) {
                    for (Album album : albums) {
                      tracks.add(new FuseTrack(album));
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                    query.findInBackground(new FindCallback<ParseObject>() {
                      public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                          for (ParseObject object : objects) {
                            FuseTrack track = new FuseTrack();

                            track.title = object.getString("title");
                            track.artists = object.getString("artists");
                            track.type = FuseTrack.Type.fromString(object.getString("type"));
                            track.play_uri = object.getString("trackId");
                            track.primary_artist = object.getString("primary_artist");
                            track.image_url = object.getString("image_url");

                            tracks.add(track);
                          }

                          updateView(tracks);
                        } else {
                          Timber.e(e, "Couldn't get Parse data.");

                          updateView(tracks);
                        }
                      }
                    });
                  }

                  @Override public void failure(RetrofitError error) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
                    query.findInBackground(new FindCallback<ParseObject>() {
                      public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                          for (ParseObject object : objects) {
                            FuseTrack track = new FuseTrack();

                            track.title = object.getString("title");
                            track.artists = object.getString("artists");
                            track.type = FuseTrack.Type.fromString(object.getString("type"));
                            track.play_uri = object.getString("trackId");
                            track.primary_artist = object.getString("primary_artist");
                            track.image_url = object.getString("image_url");

                            tracks.add(track);
                          }

                          updateView(tracks);
                        } else {
                          Timber.e(e, "Couldn't get Parse data.");

                          updateView(tracks);
                        }
                      }
                    });
                  }
                });
              }
            });
          }
        });
      }

      @Override public void failure(RetrofitError error) {
        hypemAPI.featuredService.getMyFeed(new Callback<Album[]>() {
          @Override public void success(Album[] albums, Response response) {
            for (Album album : albums) {
              tracks.add(new FuseTrack(album));
            }
          }

          @Override public void failure(RetrofitError error) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Track");
            query.findInBackground(new FindCallback<ParseObject>() {
              public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                  for (ParseObject object : objects) {
                    FuseTrack track = new FuseTrack();

                    track.title = object.getString("title");
                    track.artists = object.getString("artists");
                    track.type = FuseTrack.Type.fromString(object.getString("type"));
                    track.play_uri = object.getString("trackId");
                    track.primary_artist = object.getString("primary_artist");
                    track.image_url = object.getString("image_url");

                    tracks.add(track);
                  }

                  updateView(tracks);
                } else {
                  Timber.e(e, "Couldn't get Parse data.");

                  updateView(tracks);
                }
              }
            });
          }
        });
      }
    });
  }
}
