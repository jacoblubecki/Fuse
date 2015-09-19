package com.tjl.fuse.ui;

import android.content.Context;
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

  private final Context context;

  public VcrView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    previous = (Button) this.findViewById(R.id.previous_button);
    playPause = (Button) this.findViewById(R.id.play_pause_button);
    next = (Button) this.findViewById(R.id.next_button);

    playerManager = PlayerManager.getInstance();

    previous.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        playerManager.previous();
      }
    });

    next.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        playerManager.next();
      }
    });

    playPause.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (playerManager.isPlaying()) {
          playerManager.pause();
        } else {
          playerManager.play();
        }
      }
    });

  }
}
