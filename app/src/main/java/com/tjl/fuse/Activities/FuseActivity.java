package com.tjl.fuse.activities;

import android.os.Bundle;
import com.tjl.fuse.R;
import com.tjl.fuse.ui.NavDrawerActivity;

public class FuseActivity extends NavDrawerActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setUpNavDrawer(R.id.drawer_layout_home, R.id.list_slidermenu_home);
  }
}


