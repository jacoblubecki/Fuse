package com.tjl.fuse.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jacob on 9/19/15.
 */
public class VcrBehavior extends CoordinatorLayout.Behavior<VcrView> {

  public VcrBehavior(Context context, AttributeSet attrs) {}

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, VcrView child, View dependency) {
    float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
    child.setTranslationY(translationY);
    return true;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, VcrView child, View dependency) {
    return dependency instanceof Snackbar.SnackbarLayout;
  }
}
