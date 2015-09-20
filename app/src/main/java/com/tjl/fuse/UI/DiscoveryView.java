package com.tjl.fuse.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.ChannelRecyclerAdapter;
import com.tjl.fuse.models.ChannelItem;
import java.util.ArrayList;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryView extends LinearLayout {

  private RecyclerView recyclerView;

  public DiscoveryView(Context context) {
    super(context);
  }

  public DiscoveryView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DiscoveryView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DiscoveryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();

    LinearLayoutManager manager = new LinearLayoutManager(getContext());

    ArrayList<ChannelItem> items = new ArrayList<>();
    items.add(new ChannelItem("Discover", "http://www.clker.com/cliparts/H/E/C/Q/w/R/green-flame-md.png"));
    items.add(new ChannelItem("Around Me", "https://cdn3.iconfinder.com/data/icons/iconic-1/32/map_pin_fill-512.png"));
    items.add(new ChannelItem("Hype Machine", "http://static.hypem.com/images/logos/heart-200.png"));
    items.add(new ChannelItem("Spotify", "http://www.icons101.com/icons/17/Mitu_icon_pack_2_by_scope66/128/spotify.png"));
    items.add(new ChannelItem("SoundCloud", "http://icons.iconarchive.com/icons/xenatt/the-circle/256/App-Soundcloud-icon.png"));

    ChannelRecyclerAdapter adapter = new ChannelRecyclerAdapter(items);

    recyclerView = (RecyclerView) findViewById(R.id.channels_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
  }
}
