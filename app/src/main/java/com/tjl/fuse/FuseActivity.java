package com.tjl.fuse;

import android.os.Bundle;
import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.navigationliveo.NavigationLiveo;

public class FuseActivity extends NavigationLiveo implements OnItemClickListener {

  private HelpLiveo mHelpLiveo;

  @Override public void onInt(Bundle bundle) {

    mHelpLiveo = new HelpLiveo();
    mHelpLiveo.add("test");
    mHelpLiveo.addSubHeader("test2");
    mHelpLiveo.add("test3");
    mHelpLiveo.addSubHeader("test4");
    mHelpLiveo.addSeparator();
    mHelpLiveo.add("test5");
    mHelpLiveo.addSubHeader("test6");

    with(this).startingPosition(1) //Starting position in the list
        .addAllHelpItem(mHelpLiveo.getHelp())
        .build();

  }

  @Override public void onItemClick(int i) {

  }
}


