package com.tjl.fuse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import com.tjl.fuse.R;
import com.tjl.fuse.soundcloud.SoundCloudAuth;
import com.tjl.fuse.spotify.SpotifyAuth;
import com.tjl.fuse.ui.NavDrawerActivity;
import com.tjl.fuse.utils.preferences.StringPreference;
import timber.log.Timber;

public class FuseActivity extends NavDrawerActivity {

  private static final int SPOTIFY_REQUEST_CODE = 1337;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    if(getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    setUpNavDrawer(R.id.drawer_layout_home, R.id.list_slidermenu_home);

    SpotifyAuth.authenticate(this, SPOTIFY_REQUEST_CODE);


  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SPOTIFY_REQUEST_CODE && resultCode == RESULT_OK) {
      SpotifyAuth.handleResponse(this, resultCode, data);
      if(!(new StringPreference(this ,getString(R.string.soundcloud_token_key)).isSet())) {
        displayView(1);
        SoundCloudAuth.startSoundCloudAuthActivity(this);
      }
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    setIntent(intent);

    String redirect = getString(R.string.soundcloud_redirect);

    if (intent.getDataString().startsWith(redirect)) {
      SoundCloudAuth.handleSoundCloudAuthCallback(this, intent);
    } else {
      Timber.i("Unhandled new intent was intercepted.");
      // handle other new intents
    }
  }

  @Override protected void displayView(int position) {
    super.displayView(position);

    Timber.e("Position is " + position);

    View c = findViewById(R.id.container);
    ViewGroup v = (ViewGroup) c.getParent();
    int index = v.indexOfChild(c);
    v.removeViewAt(index);

    v.removeView(c);

    switch (position) {
      case 0:
        Timber.e("Discover selected.");
        c = getLayoutInflater().inflate(R.layout.discover_view, v, false);
        v.addView(c, index);
        break;

      case 1:
        Timber.e("Playlist selected.");

        c = getLayoutInflater().inflate(R.layout.playlist_view, v, false);
        v.addView(c, index);
        break;

      case 2:
        Timber.i("Search selected.");

        c = getLayoutInflater().inflate(R.layout.search_view, v, false);
        v.addView(c, index);
        break;

      case 3:
        Timber.i("Settings selected.");

        c = getLayoutInflater().inflate(R.layout.settings_view, v, false);
        v.addView(c, index);
        break;
    }
  }
}


