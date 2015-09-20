package com.tjl.fuse.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class VcrView extends LinearLayout {

  private PlayerManager playerManager;
  private Button previous;
  private Button playPause;
  private Button next;

  public VcrView(Context context) {
    super(context);
  }

  public VcrView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VcrView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public VcrView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    previous = (Button) this.findViewById(R.id.previous_button);
    playPause = (Button) this.findViewById(R.id.play_pause_button);
    next = (Button) this.findViewById(R.id.next_button);

    previous.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        PlayerManager.getInstance().previous();
      }
    });

    next.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        PlayerManager.getInstance().next();
      }
    });

    playPause.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (PlayerManager.getInstance().isPlaying()) {
          PlayerManager.getInstance().pause();
        } else {
          PlayerManager.getInstance().play();
        }
      }
    });

  }
}
