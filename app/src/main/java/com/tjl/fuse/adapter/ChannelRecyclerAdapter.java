package com.tjl.fuse.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jacob on 9/19/15.
 */
public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelViewHolder> {

  @Override public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override public void onBindViewHolder(ChannelViewHolder holder, int position) {

  }

  @Override public int getItemCount() {
    return 0;
  }

  public class ChannelViewHolder extends RecyclerView.ViewHolder {

    public ChannelViewHolder(View itemView) {
      super(itemView);
    }
  }
}
