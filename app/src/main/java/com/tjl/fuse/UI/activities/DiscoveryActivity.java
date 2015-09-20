package com.tjl.fuse.ui.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import com.spotify.sdk.android.authentication.LoginActivity;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.service.Constants;
import com.tjl.fuse.service.ForegroundService;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryActivity extends AppCompatActivity {

  public static String EXTRA_PLAYLIST_VALUE = "EXTRA_PLAYLIST_VALUE";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.discovery_list_activity);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    handleIntent(getIntent());
  }

  public void handleIntent(Intent intent) {
    setIntent(intent);

    String channel = getIntent().getStringExtra(EXTRA_PLAYLIST_VALUE);

    if (channel != null) {
      setTitle(channel);
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
    if (!(app.isServiceRunning(FuseActivity.class) ||
        app.isServiceRunning(LoginActivity.class)) &&
        PlayerManager.getInstance().getQueue() != null &&
        PlayerManager.getInstance().getQueue().getSize() > 0 &&
        PlayerManager.getInstance().isPlaying()) {
      startService();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

    AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);

    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_UP:
        manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        return true;
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
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
}
