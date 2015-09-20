package com.tjl.fuse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import java.util.List;
import timber.log.Timber;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.TrackViewHolder> {

  private List<FuseTrack> tracks;

  public SearchAdapter(List<FuseTrack> tracks) {
    this.tracks = tracks;
  }

  @Override public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.track_card, parent, false);

    return new TrackViewHolder(view);
  }

  @Override public void onBindViewHolder(TrackViewHolder holder, final int position) {
    if (tracks.get(position).type == (FuseTrack.Type.SPOTIFY)) {
      holder.card.setCardBackgroundColor(Color.parseColor("#94E8B1"));
    } else if (tracks.get(position).type == (FuseTrack.Type.SOUNDCLOUD)) {
      holder.card.setCardBackgroundColor(Color.parseColor("#FFC28D"));
    } else {
      holder.card.setCardBackgroundColor(Color.CYAN);
    }

    final Queue queue = new Queue(tracks);

    holder.text.setText(tracks.get(position).title);
    holder.card.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        queue.setCurrent(position);
        PlayerManager.getInstance().setQueue(queue);
        PlayerManager.getInstance().reset();

        Timber.i("Track title:  " + PlayerManager.getInstance().getQueue().current().title);
        PlayerManager.getInstance().play();

        Timber.i("Track at index " + (position));
      }
    });
    Picasso.with(holder.card.getContext()).load(tracks.get(position).image_url).into(holder.image);
  }

  @Override public long getItemId(int position) {
    return tracks.get(position).hashCode();
  }

  @Override public int getItemCount() {
    return tracks.size();
  }

  public class TrackViewHolder extends RecyclerView.ViewHolder {
    CardView card;
    ImageView image;
    TextView text;

    public TrackViewHolder(View itemView) {
      super(itemView);
      card = (CardView) itemView.findViewById(R.id.card);
      image = (ImageView) itemView.findViewById(R.id.track_photo);
      text = (TextView) itemView.findViewById(R.id.track_name);
    }
  }

  public static class SearchGridLayoutManager extends GridLayoutManager {

    public SearchGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
        int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SearchGridLayoutManager(Context context, int spanCount) {
      super(context, spanCount);
    }

    public SearchGridLayoutManager(Context context, int spanCount, int orientation,
        boolean reverseLayout) {
      super(context, spanCount, orientation, reverseLayout);
    }

    @Override public boolean supportsPredictiveItemAnimations() {
      return true;
    }
  }
}
