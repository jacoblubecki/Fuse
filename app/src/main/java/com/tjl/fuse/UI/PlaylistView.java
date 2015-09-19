package com.tjl.fuse.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import java.util.ArrayList;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class PlaylistView extends LinearLayout {
  protected RecyclerView recyclerView;
  protected PlayerManager playerManager;

  public PlaylistView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();


    playerManager = PlayerManager.getInstance();

    if(playerManager.getQueue() != null && playerManager.getQueue().getSize() > 0) {
      LinearLayoutManager manager = new LinearLayoutManager(getContext());
      final SearchAdapter adapter = new SearchAdapter(playerManager.getQueue().getTracks());

      recyclerView = (RecyclerView) findViewById(R.id.playlist_view);
      recyclerView.setLayoutManager(manager);
      recyclerView.setAdapter(adapter);
    }
  }
}
