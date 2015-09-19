package com.tjl.fuse.adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tjl.fuse.R;
import com.tjl.fuse.models.ChannelItem;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.ui.activities.DiscoveryActivity;
import java.util.List;

/**
 * Created by Jacob on 9/19/15.
 */
public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelViewHolder> {

  private List<ChannelItem> channels;

  public ChannelRecyclerAdapter(List<ChannelItem> channels) {
    this.channels = channels;
  }

  @Override public ChannelViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_card, parent, false);

    return new ChannelViewHolder(view);
  }

  @Override public void onBindViewHolder(final ChannelViewHolder holder, final int position) {
    holder.text.setText(channels.get(position).title);
    holder.card.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        PlayerManager.getInstance().pause();

        Intent intent = new Intent(holder.card.getContext(), DiscoveryActivity.class);
        intent.putExtra(DiscoveryActivity.EXTRA_PLAYLIST_VALUE, channels.get(position).title);
        holder.card.getContext().startActivity(intent);
      }
    });
    Picasso.with(holder.card.getContext()).load(channels.get(position).image_url).into(holder.image);
  }

  @Override public int getItemCount() {
    return channels.size();
  }

  public class ChannelViewHolder extends RecyclerView.ViewHolder {
    CardView card;
    ImageView image;
    TextView text;

    public ChannelViewHolder(View itemView) {
      super(itemView);
      card = (CardView) itemView.findViewById(R.id.card);
      image = (ImageView) itemView.findViewById(R.id.channel_photo);
      text = (TextView) itemView.findViewById(R.id.channel_name);
    }
  }
}
