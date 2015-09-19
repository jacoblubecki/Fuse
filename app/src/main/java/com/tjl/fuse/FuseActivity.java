package com.tjl.fuse;

import android.content.Intent;
import android.os.Bundle;
import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.navigationliveo.NavigationLiveo;

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

  }

  @Override public void onItemClick(int i) {//TODO should make this better
    switch (i) {
      case 0:{
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
      }
      case 1:{
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
      }
      case 2:{
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
      }
      case 3: {
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
      }

    }
  }
}


