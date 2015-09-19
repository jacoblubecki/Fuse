package com.tjl.fuse.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.NavDrawerListAdapter;
import com.tjl.fuse.models.NavDrawerItem;
import java.util.ArrayList;

/**
 * Created by Jacob on 9/19/15.
 */
public class NavDrawerActivity extends AppCompatActivity {
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;

  protected void setUpNavDrawer(int drawerID, int listID) {


    mDrawerLayout = (DrawerLayout) findViewById(drawerID);
    mDrawerList = (ListView) findViewById(listID);

    ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

    // adding nav drawer items to array
    navDrawerItems.add(new NavDrawerItem("Test", R.drawable.ic_launcher)); //Home
    navDrawerItems.add(new NavDrawerItem("Test", R.drawable.ic_launcher)); //Home
    navDrawerItems.add(new NavDrawerItem("Test", R.drawable.ic_launcher)); //Home

    mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    // setting the nav drawer list adapter
    NavDrawerListAdapter adapter1 = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
    mDrawerList.setAdapter(adapter1);

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
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
    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
    return super.onPrepareOptionsMenu(menu);
  }

  /**
   * Displaying fragment view for selected nav drawer list item
   * */
  protected void displayView(int position) {
    // update selected item and title, then close the drawer
    mDrawerList.setItemChecked(position, true);
    mDrawerList.setSelection(position);
    mDrawerLayout.closeDrawer(mDrawerList);
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
