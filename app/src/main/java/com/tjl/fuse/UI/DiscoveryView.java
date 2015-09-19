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
    items.add(new ChannelItem("Test", "http://albumartcollection.com/wp-content/uploads/2011/07/summer-album-art.jpg"));
    items.add(new ChannelItem("Test A", "http://albumartcollection.com/wp-content/uploads/2011/07/summer-album-art.jpg"));
    items.add(new ChannelItem("Test B", "http://albumartcollection.com/wp-content/uploads/2011/07/summer-album-art.jpg"));
    items.add(new ChannelItem("Test C", "http://albumartcollection.com/wp-content/uploads/2011/07/summer-album-art.jpg"));

    ChannelRecyclerAdapter adapter = new ChannelRecyclerAdapter(items);

    recyclerView = (RecyclerView) findViewById(R.id.channels_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);

    invalidate();
  }
}
