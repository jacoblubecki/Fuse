package com.tjl.fuse.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.PlaylistAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import java.util.ArrayList;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class PlaylistView extends LinearLayout {
  private RecyclerView recyclerView;
  private PlayerManager playerManager;

  public PlaylistView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();


    playerManager = PlayerManager.getInstance();
    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    ArrayList<FuseTrack> items = new ArrayList<>();
    if(playerManager.getQueue()!=null) {
      items = (ArrayList<FuseTrack>) playerManager.getQueue().getTracks();
    }

    //items.add(new Track());
    PlaylistAdapter adapter = new PlaylistAdapter(items);

    recyclerView = (RecyclerView) findViewById(R.id.search_list_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);

    invalidate();

  }
}
