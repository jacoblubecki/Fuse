package com.tjl.fuse.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.ChannelRecyclerAdapter;
import com.tjl.fuse.models.ChannelItem;
import java.util.ArrayList;

/**
 * Created by Jacob on 9/19/15.
 */
public class DiscoveryView extends LinearLayout {

  private RecyclerView cards;

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
    items.add(new ChannelItem("Test", ""));

    ChannelRecyclerAdapter adapter = new ChannelRecyclerAdapter(items);

    cards = (RecyclerView) findViewById(R.id.channels_view);
    cards.setLayoutManager(manager);
    cards.setAdapter(adapter);
    cards.setHasFixedSize(true);
  }
}
