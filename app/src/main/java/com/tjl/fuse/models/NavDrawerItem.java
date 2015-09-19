package com.tjl.fuse.models;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {

  private String title;
  private int icon = -1;
  private Drawable drawable;
  private String count = "0";
  // boolean to set visiblity of the counter
  private boolean isCounterVisible = false;

  public NavDrawerItem(){}

  public NavDrawerItem(String title, int icon){
    this.title = title;
    this.icon = icon;
  }

  public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
    this.title = title;
    this.icon = icon;
    this.isCounterVisible = isCounterVisible;
    this.count = count;
  }

  public NavDrawerItem(String title, Drawable drawable){
    this.title = title;
    this.drawable = drawable;
  }

  public NavDrawerItem(String title, Drawable drawable, boolean isCounterVisible, String count){
    this.title = title;
    this.drawable = drawable;
    this.isCounterVisible = isCounterVisible;
    this.count = count;
  }

  public String getTitle(){
    return this.title;
  }

  public int getIcon(){
    return this.icon;
  }

  public Drawable getDrawable() {
    return drawable;
  }

  public String getCount(){
    return this.count;
  }

  public boolean getCounterVisibility(){
    return this.isCounterVisible;
  }

  public void setTitle(String title){
    this.title = title;
  }

  public void setIcon(int icon){
    this.icon = icon;
  }

  public void setCount(String count){
    this.count = count;
  }

  public void setCounterVisibility(boolean isCounterVisible){
    this.isCounterVisible = isCounterVisible;
  }
}
