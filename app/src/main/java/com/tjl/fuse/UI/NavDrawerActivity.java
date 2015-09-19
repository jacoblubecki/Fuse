package com.tjl.fuse.ui;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.NavDrawerListAdapter;
import com.tjl.fuse.models.NavDrawerItem;
import java.util.ArrayList;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import timber.log.Timber;

/**
 * Created by Jacob on 9/19/15.
 */
public class NavDrawerActivity extends AppCompatActivity {
  private LinearLayout drawerWrapper;
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;

  private SlideMenuClickListener listener = new SlideMenuClickListener();
  private ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();
  private NavDrawerListAdapter adapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // adding nav drawer items to array
    Drawable discovery = MaterialDrawableBuilder.with(this).setIcon(
        MaterialDrawableBuilder.IconValue.HEADPHONES)
        .build();

    Drawable playlist = MaterialDrawableBuilder.with(this)
        .setIcon(MaterialDrawableBuilder.IconValue.FORMAT_LIST_BULLETED)
        .build();

    Drawable search = MaterialDrawableBuilder.with(this)
        .setIcon(MaterialDrawableBuilder.IconValue.MAGNIFY)
        .build();

    Drawable settings = MaterialDrawableBuilder.with(this)
        .setIcon(MaterialDrawableBuilder.IconValue.SETTINGS)
        .build();

    navDrawerItems.add(new NavDrawerItem("Discovery", discovery));
    navDrawerItems.add(new NavDrawerItem("Playlist", playlist));
    navDrawerItems.add(new NavDrawerItem("Search", search));
    navDrawerItems.add(new NavDrawerItem("Settings", settings));


    // setting the nav drawer list adapter
    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
  }

  protected void setUpNavDrawer(int drawerID, int listID) {
    drawerWrapper = (LinearLayout) findViewById(R.id.drawer_wrapper);
    mDrawerLayout = (DrawerLayout) findViewById(drawerID);
    mDrawerList = (ListView) findViewById(listID);

    mDrawerList.setAdapter(adapter);
    mDrawerList.setOnItemClickListener(listener);

    mDrawerToggle =
        new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
          public void onDrawerClosed(View view) {
            // calling onPrepareOptionsMenu() to show action bar icons
            invalidateOptionsMenu();
          }

          public void onDrawerOpened(View drawerView) {
            // calling onPrepareOptionsMenu() to hide action bar icons
            invalidateOptionsMenu();
          }
        };
    mDrawerLayout.setDrawerListener(mDrawerToggle);
  }

  protected void hideSoftKeyboard() {
    if(getCurrentFocus()!=null) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  /**
   * Slide menu item click listener
   * */
  private class SlideMenuClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      // display view for selected nav drawer item
      displayView(position);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // toggle nav drawer on selecting action bar app icon/title
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      hideSoftKeyboard();
      return true;
    }
    else
      return super.onOptionsItemSelected(item);
  }

  /* *
   * Called when invalidateOptionsMenu() is triggered
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    // if nav drawer is opened, hide the action items
    boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerWrapper);
    //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
    return super.onPrepareOptionsMenu(menu);
  }

  /**
   * Displaying fragment view for selected nav drawer list item
   * */
  protected void displayView(int position) {
    // update selected item and title, then close the drawer
    mDrawerList.setItemChecked(position, true);
    mDrawerList.setSelection(position);
    mDrawerLayout.closeDrawer(drawerWrapper);

    Timber.i("Display view.");
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggles
    mDrawerToggle.onConfigurationChanged(newConfig);
  }
}
