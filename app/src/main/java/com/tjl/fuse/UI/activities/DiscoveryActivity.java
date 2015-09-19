package com.tjl.fuse.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.ui.SwipeablePlaylistView;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryActivity extends AppCompatActivity implements
    SwipeablePlaylistView.SwipeCallback {

  public static String EXTRA_PLAYLIST_VALUE = "EXTRA_PLAYLIST_VALUE";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.discovery_list_activity);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if(getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    SwipeablePlaylistView view = (SwipeablePlaylistView) findViewById(R.id.swipe_view);
    view.setCallback(this);

    handleIntent(getIntent());
  }

  public void handleIntent(Intent intent) {
    setIntent(intent);

    String channel = getIntent().getStringExtra(EXTRA_PLAYLIST_VALUE);

    if(channel != null) {
      setTitle(channel);
    }
  }

  @Override public void onNewIntent(Intent newIntent) {
    handleIntent(newIntent);
  }

  @Override public void swipedLeft(int position, FuseTrack track) {
    notifyUndo(position, track, "Track removed from discover.");
  }

  @Override public void swipedRight(int position, FuseTrack track) {
    notifyUndo(position, track, "Track add to user playlist.");
  }

  private void notifyUndo(final int position, final FuseTrack restore, String message) {

    SuperActivityToast superActivityToast =
        new SuperActivityToast(this, SuperToast.Type.BUTTON);
    superActivityToast.setDuration(SuperToast.Duration.LONG);
    superActivityToast.setText("Some action performed.");
    superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
    superActivityToast.setOnClickWrapper(
        new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {

          @Override public void onClick(View view, Parcelable token) {
            PlayerManager.getInstance().getQueue().getTracks().add(position, restore);
          }
        }));
    superActivityToast.show();
  }
}
