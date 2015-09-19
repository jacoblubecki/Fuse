package com.tjl.fuse;

import android.content.Intent;
import android.os.Bundle;
import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.navigationliveo.NavigationLiveo;
import timber.log.Timber;

public class FuseActivity extends NavigationLiveo implements OnItemClickListener {

  private HelpLiveo drawerItem;

  @Override public void onInt(Bundle bundle) {

    drawerItem = new HelpLiveo();
    drawerItem.add("Discover");
    drawerItem.add("Playlist");
    drawerItem.add("Search");
    drawerItem.addSeparator();
    drawerItem.add("Options");

    with(this).startingPosition(1) //Starting position in the list
        .addAllHelpItem(drawerItem.getHelp())
        .build();
    Timber.plant(new Timber.DebugTree());

  }

  @Override public void onItemClick(int i) {//TODO should make this better

    Timber.e("i is " + i);
    switch (i) {
      case 0:{
        Timber.e("0");
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
        break;
      }
      case 1:{
        Timber.e("1");
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
        break;
      }
      case 2:{
        Timber.e("2");
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
        break;
      }
      case 4: {
        Timber.e("3");
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
        break;
      }
    }
  }
}


