package com.tjl.fuse.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.soundcloud.SoundCloudAuth;
import com.tjl.fuse.spotify.SpotifyAuth;
import com.tjl.fuse.ui.activities.NavDrawerActivity;
import com.tjl.fuse.utils.preferences.StringPreference;
import timber.log.Timber;

public class FuseActivity extends NavDrawerActivity {

  private static final int SPOTIFY_REQUEST_CODE = 1337;

  private int layoutId;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    setUpNavDrawer(R.id.drawer_layout_home, R.id.list_slidermenu_home);

    SpotifyAuth.authenticate(this, SPOTIFY_REQUEST_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SPOTIFY_REQUEST_CODE && resultCode == RESULT_OK) {
      SpotifyAuth.handleResponse(this, resultCode, data);
      if (!(new StringPreference(this, getString(R.string.soundcloud_token_key)).isSet())) {
        displayView(1);
        SoundCloudAuth.startSoundCloudAuthActivity(this);
      }
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    setIntent(intent);
    if (intent.getDataString() != null) {

      String redirect = getString(R.string.soundcloud_redirect);

      if (intent.getDataString().startsWith(redirect)) {
        SoundCloudAuth.handleSoundCloudAuthCallback(this, intent);
      } else {
        Timber.i("Unhandled new intent was intercepted.");
        // handle other new intents
      }
    } else {
      Timber.w("Intent data was null.");
    }
  }

  @Override protected void displayView(int position) {
    Timber.e("Position is " + position);

    View c = findViewById(R.id.container);
    ViewGroup v = (ViewGroup) c.getParent();
    int index = v.indexOfChild(c);
    v.removeViewAt(index);

    v.removeView(c);

    switch (position) {
      case 0:
        Timber.e("Discover selected.");

        layoutId = R.layout.discover_view;
        c = getLayoutInflater().inflate(layoutId, v, false);
        v.addView(c, index);
        break;

      case 1:
        Timber.e("Playlist selected.");

        layoutId = R.layout.playlist_view;
        c = getLayoutInflater().inflate(layoutId, v, false);
        v.addView(c, index);
        break;

      case 2:
        Timber.i("Search selected.");

        layoutId = R.layout.search_view;
        c = getLayoutInflater().inflate(layoutId, v, false);
        v.addView(c, index);
        break;

      case 3:
        Timber.i("Settings selected.");

        layoutId = R.layout.settings_view;
        c = getLayoutInflater().inflate(layoutId, v, false);
        v.addView(c, index);
        break;
    }

    super.displayView(position);
  }

  @Override public void onBackPressed() {
    Timber.i("back button pressed");
    switch (layoutId) {
      case R.layout.discover_view:
      case R.layout.search_view:
      case R.layout.settings_view:
        Timber.i("Display view 1.");
        displayView(1);
        break;

      case R.layout.playlist_view:
        super.onBackPressed();
        break;

      default:
        Timber.w("Unknown layout ID when going back.");
        super.onBackPressed();
        break;
    }
  }

  @Override public void onDestroy() {
    PlayerManager.getInstance().release();

    super.onDestroy();
  }
}


