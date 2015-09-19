package com.tjl.fuse.activities;

import android.os.Bundle;
import com.tjl.fuse.R;
import com.tjl.fuse.ui.NavDrawerActivity;
import timber.log.Timber;

public class FuseActivity extends NavDrawerActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setUpNavDrawer(R.id.drawer_layout_home, R.id.list_slidermenu_home);
    Timber.plant(new Timber.DebugTree());
  }

  @Override protected void displayView(int position) {
    super.displayView(position);
    Timber.e("Position is " + position);
    switch (position) {
      case 0: {
        Timber.e("case 0");
        setContentView(R.layout.playlist_view);
        break;
      }
      case 1: {
        Timber.e("case 1");
        setContentView(R.layout.activity_main);
        break;
      }
      case 2: {
        Timber.e("case 2");
        setContentView(R.layout.activity_main);
        break;
      }
    }
    setUpNavDrawer(R.id.drawer_layout_home, R.id.list_slidermenu_home);
  }
}


