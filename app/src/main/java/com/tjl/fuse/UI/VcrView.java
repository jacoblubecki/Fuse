package com.tjl.fuse.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class VcrView extends LinearLayout {

  private PlayerManager playerManager;
  private ImageButton previous;
  private ImageButton playPause;
  private ImageButton next;

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

    previous = (ImageButton) this.findViewById(R.id.previous_button);
    playPause = (ImageButton) this.findViewById(R.id.play_pause_button);
    next = (ImageButton) this.findViewById(R.id.next_button);

    Drawable previousDrawable = MaterialDrawableBuilder.with(getContext())
        .setIcon(MaterialDrawableBuilder.IconValue.SKIP_PREVIOUS)
        .build();

    Drawable nextDrawable = MaterialDrawableBuilder.with(getContext())
        .setIcon(MaterialDrawableBuilder.IconValue.SKIP_NEXT)
        .build();

    final Drawable playDrawable = MaterialDrawableBuilder.with(getContext())
        .setIcon(MaterialDrawableBuilder.IconValue.PLAY_CIRCLE)
        .build();

    final Drawable pauseDrawable = MaterialDrawableBuilder.with(getContext())
        .setIcon(MaterialDrawableBuilder.IconValue.PAUSE_CIRCLE)
        .build();

    previous.setImageDrawable(previousDrawable);
    next.setImageDrawable(nextDrawable);
    playPause.setImageDrawable(playDrawable);

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
          playPause.setImageDrawable(playDrawable);
        } else {
          PlayerManager.getInstance().play();
          playPause.setImageDrawable(pauseDrawable);
        }
      }
    });
  }
}
