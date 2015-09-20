package com.tjl.fuse.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;

/**
 * Created by Jacob on 9/19/15.
 */
public class SwipeablePlaylistView extends PlaylistView
    implements SwipeableRecyclerViewTouchListener.SwipeListener {

  private SearchAdapter adapter;

  public SwipeablePlaylistView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();

    playerManager = PlayerManager.getInstance();

    if (playerManager.getQueue() != null && playerManager.getQueue().getSize() > 0) {
      SearchAdapter.SearchGridLayoutManager manager = new SearchAdapter.SearchGridLayoutManager(getContext(), 1);
      adapter = new SearchAdapter(playerManager.getQueue().getTracks());
      adapter.setHasStableIds(true);

      recyclerView = (RecyclerView) findViewById(R.id.playlist_view);
      recyclerView.setLayoutManager(manager);
      recyclerView.setAdapter(adapter);

      SwipeableRecyclerViewTouchListener swipeTouchListener =
          new SwipeableRecyclerViewTouchListener(recyclerView, this);

      recyclerView.addOnItemTouchListener(swipeTouchListener);
    }
  }

  public void notifyUndo(final int position, final FuseTrack track, Direction direction) {
    String message = "";

    if (direction == Direction.LEFT) {
      message += "Track removed from Discover.";
    } else if (direction == Direction.RIGHT) {
      message += "Track added to your playlist.";
    }

    Snackbar.make(((Activity) getContext()).findViewById(android.R.id.content), message,
        Snackbar.LENGTH_LONG).setAction("Undo", new OnClickListener() {
      @Override public void onClick(View view) {
        PlayerManager.getInstance().getQueue().getTracks().add(position, track);
        adapter.notifyDataSetChanged();
      }
    }).setActionTextColor(Color.RED).show();
  }

  @Override public boolean canSwipe(int position) {
    return true;
  }

  @Override
  public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
    for (int position : reverseSortedPositions) {
        notifyUndo(position, playerManager.getQueue().getTracks().get(position), Direction.LEFT);

      playerManager.getQueue().getTracks().remove(position);
      adapter.notifyItemRemoved(position);
    }
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
    for (int position : reverseSortedPositions) {
      notifyUndo(position, playerManager.getQueue().getTracks().get(position), Direction.RIGHT);

      playerManager.getQueue().getTracks().remove(position);
      adapter.notifyItemRemoved(position);
    }
    adapter.notifyDataSetChanged();
  }

  private enum Direction {
    LEFT,
    RIGHT
  }
}
