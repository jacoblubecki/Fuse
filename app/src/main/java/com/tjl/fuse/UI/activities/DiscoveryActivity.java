package com.tjl.fuse.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.tjl.fuse.R;
import com.tjl.fuse.models.Album;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.web.HypemAPI;
import java.util.ArrayList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryActivity extends AppCompatActivity {
  HypemAPI hypemAPI;
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

              for(Album album : albums){
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
      }
    }
  }

  @Override public void onNewIntent(Intent newIntent) {
    handleIntent(newIntent);
  }
}
