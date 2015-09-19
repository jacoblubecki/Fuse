package com.tjl.fuse.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jacob on 9/18/15.
 */
public class StringPreference {

  private final SharedPreferences preferences;
  private final String key;
  private final String defaultValue;

  public StringPreference(Context context, String key) {
    this(context, key, null);
  }

  public StringPreference(Context context, String key, String defaultValue) {
    this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public StringPreference(SharedPreferences preferences, String key) {
    this(preferences, key, null);
  }

  public StringPreference(SharedPreferences preferences, String key, String defaultValue) {
    this.preferences = preferences;
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public String get() {
    return preferences.getString(key, defaultValue);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(String value) {
    preferences.edit().putString(key, value).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }
}
