package com.tjl.fuse.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;

/**
 * Created by Jacob on 9/19/15.
 */
public class SwipeablePlaylistView extends PlaylistView {

  private SearchAdapter adapter;
  private SwipeCallback callback;

  public SwipeablePlaylistView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();

    playerManager = PlayerManager.getInstance();

    if (playerManager.getQueue() != null && playerManager.getQueue().getSize() > 0) {
      LinearLayoutManager manager = new LinearLayoutManager(getContext());
      adapter = new SearchAdapter(playerManager.getQueue().getTracks());

      recyclerView = (RecyclerView) findViewById(R.id.playlist_view);
      recyclerView.setLayoutManager(manager);
      recyclerView.setAdapter(adapter);

      SwipeableRecyclerViewTouchListener swipeTouchListener =
          new SwipeableRecyclerViewTouchListener(recyclerView,
              new SwipeableRecyclerViewTouchListener.SwipeListener() {
                @Override public boolean canSwipe(int position) {
                  return true;
                }

                @Override public void onDismissedBySwipeLeft(RecyclerView recyclerView,
                    int[] reverseSortedPositions) {
                  for (int position : reverseSortedPositions) {
                    if(callback != null) {
                      callback.swipedLeft(position, playerManager.getQueue().current());
                    }

                    playerManager.getQueue().getTracks().remove(position);
                    adapter.notifyItemRemoved(position);
                  }
                  adapter.notifyDataSetChanged();
                }

                @Override public void onDismissedBySwipeRight(RecyclerView recyclerView,
                    int[] reverseSortedPositions) {
                  for (int position : reverseSortedPositions) {
                    if(callback != null) {
                      callback.swipedRight(position, playerManager.getQueue().current());
                    }

                    playerManager.getQueue().getTracks().remove(position);
                    adapter.notifyItemRemoved(position);
                  }
                  adapter.notifyDataSetChanged();
                }
              });

      recyclerView.addOnItemTouchListener(swipeTouchListener);
    }
  }

  public void setCallback(SwipeCallback callback) {
    this.callback = callback;
  }

  public interface SwipeCallback {
    void swipedLeft(int position, FuseTrack track);
    void swipedRight(int position, FuseTrack track);
  }
}
