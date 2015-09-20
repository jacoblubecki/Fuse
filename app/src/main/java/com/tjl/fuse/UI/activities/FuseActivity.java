package com.tjl.fuse.ui.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.spotify.sdk.android.authentication.LoginActivity;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.service.Constants;
import com.tjl.fuse.service.ForegroundService;
import com.tjl.fuse.soundcloud.SoundCloudAuth;
import com.tjl.fuse.spotify.SpotifyAuth;
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
        PlayerManager.getInstance().setQueue(FuseApplication.getPlaylist());

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

  @Override public void onBackPressed() {
    Timber.i("back button pressed");

    if (!isDrawerOpen()) {
      switch (layoutId) {
        case R.layout.discover_view:
        case R.layout.search_view:
        case R.layout.settings_view:
          Timber.i("Display view 1.");
          mDrawerLayout.openDrawer(drawerWrapper);
          break;

        case R.layout.playlist_view:
          moveTaskToBack(false);
          break;

        default:
          Timber.w("Unknown layout ID when going back.");
          super.onBackPressed();
          break;
      }
    } else {
      switch (layoutId) {
        case R.layout.discover_view:
        case R.layout.search_view:
        case R.layout.settings_view:
          Timber.i("Display view 1.");
          displayView(1);
          break;

        case R.layout.playlist_view:
          displayView(1);
          break;

        default:
          Timber.w("Unknown layout ID when going back.");
          super.onBackPressed();
          break;
      }
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

  @Override public void onResume() {
    super.onResume();
    stopService();
  }

  @Override public void onPause() {
    super.onPause();
    FuseApplication.serializePlaylist();

    if (new StringPreference(this, getString(R.string.spotify_token_key)).isSet()) {

      FuseApplication app = FuseApplication.getApplication();
      if (!(app.isServiceRunning(DiscoveryActivity.class) ||
          app.isServiceRunning(LoginActivity.class)) &&
          PlayerManager.getInstance().getQueue() != null &&
          PlayerManager.getInstance().getQueue().getSize() > 0 &&
          PlayerManager.getInstance().isPlaying()) {
        startService();
      }
    }
  }

  @Override public void onDestroy() {
    Timber.i("Destroy.");
    stopService();
    PlayerManager.getInstance().release();

    super.onDestroy();
  }
}


