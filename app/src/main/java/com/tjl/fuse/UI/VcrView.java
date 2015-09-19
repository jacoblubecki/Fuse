package com.tjl.fuse.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class VcrView extends LinearLayout {

  private final Context context;

  public VcrView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

  }

  @Override
  protected void onFinishInflate(){
    super.onFinishInflate();
  }
}
