package com.tjl.fuse.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import java.util.ArrayList;
import kaaes.spotify.webapi.android.models.Track;

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
    for(FuseTrack track : playerManager.getQueue().){
      items.add(track);
    }
    //items.add(new Track());
    SearchAdapter adapter = new SearchAdapter(items);

    recyclerView = (RecyclerView) findViewById(R.id.playlist_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);

    invalidate();

  }
}
