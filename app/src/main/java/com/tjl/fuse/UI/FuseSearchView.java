package com.tjl.fuse.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tjl.fuse.FuseApplication;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.ui.activities.NavDrawerActivity;
import com.tjl.fuse.utils.preferences.StringPreference;
import java.util.ArrayList;
import java.util.List;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import lubecki.soundcloud.webapi.android.SoundCloudAPI;
import lubecki.soundcloud.webapi.android.SoundCloudService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by JoshBeridon on 9/19/15.
 */

public class FuseSearchView extends LinearLayout implements
    SwipeableRecyclerViewTouchListener.SwipeListener {
  EditText searchText;
  private EditText searchView;
  private Button searchButton;
  private SpotifyService spotify;
  private SoundCloudService soundCloud;
  private String songUri;
  private PlayerManager playerManager;
  private RecyclerView recyclerView;
  SearchAdapter adapter;
  Context context;
  ArrayList<FuseTrack> fuseTracks;

  public FuseSearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();

    String tokenKey = getContext().getString(R.string.spotify_token_key);
    String token = new StringPreference(getContext(), tokenKey).get();
    playerManager = PlayerManager.getInstance();

    searchView = (EditText) findViewById(R.id.search_view);
    fuseTracks = new ArrayList<>();

    //spotify
    SpotifyApi api = new SpotifyApi();
    api.setAccessToken(token);

    spotify = api.getService();

    String clientIdSC = getContext().getString(R.string.soundcloud_client_id);
    String tokenKeySC = getContext().getString(R.string.soundcloud_token_key);
    String tokenSC = new StringPreference(getContext(), tokenKeySC).get();

    // Sound Cloud
    SoundCloudAPI apiSC = new SoundCloudAPI(clientIdSC);
    apiSC.setToken(tokenSC);

    soundCloud = apiSC.getService();

    playerManager = PlayerManager.getInstance();
    SearchAdapter.SearchGridLayoutManager manager = new SearchAdapter.SearchGridLayoutManager(getContext(), 1);

    adapter = new SearchAdapter(fuseTracks);
    adapter.setHasStableIds(true);

    recyclerView = (RecyclerView) findViewById(R.id.search_list_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);


    SwipeableRecyclerViewTouchListener swipeTouchListener =
        new SwipeableRecyclerViewTouchListener(recyclerView, this);

    recyclerView.addOnItemTouchListener(swipeTouchListener);


    invalidate();



    searchView.setOnKeyListener(new OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
          recyclerView.setAdapter(null);
          fuseTracks.clear();
          search(searchView.getText().toString());
          ((NavDrawerActivity) context).hideSoftKeyboard();
          return true;

        }
        return false;
      }
    });
    //searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
    //  @Override public boolean onQueryTextSubmit(String query) {
    //    return false;
    //  }
    //
    //  @Override public boolean onQueryTextChange(String newText) {
    //    if (newText.compareTo("") == 0) recyclerView.setAdapter(null);
    //    return false;
    //  }
    //});
  }

  public void search(String query) {
    searchSpotify(query);
  }

  public void searchSC(String query) {

    soundCloud.searchTracks(query,
        new Callback<List<lubecki.soundcloud.webapi.android.models.Track>>() {
          @Override public void success(List<lubecki.soundcloud.webapi.android.models.Track> tracks,
              Response response) {
            Timber.e("response is " + response.getReason() + " " + tracks.get(0).title);
            for (lubecki.soundcloud.webapi.android.models.Track t : tracks) {
              FuseTrack fuseTrack = new FuseTrack(t);
              fuseTracks.add(fuseTrack);
            }

            adapter = new SearchAdapter(fuseTracks);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
          }

          @Override public void failure(RetrofitError error) {
            Timber.e("error is " + error.getMessage());
          }
        });
  }

  public void searchSpotify(final String query) {
    spotify.searchTracks(query, new Callback<TracksPager>() {
      @Override public void success(TracksPager tracksPager, Response response) {
        if (tracksPager.tracks.items.size() != 0) {

          for (Track t : tracksPager.tracks.items) {
            FuseTrack fuseTrack = new FuseTrack(t);
            fuseTracks.add(fuseTrack);
          }
        }

        searchSC(query);
      }

      @Override public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
      }
    });
  }

  @Override public boolean canSwipe(int position) {
    return true;
  }

  public void notifyUndo(final int position, int queuePosition, final FuseTrack track, Direction direction) {
    String message = "";

    if (direction == Direction.LEFT) {
      message += "Track removed from Discover.";
    } else if (direction == Direction.RIGHT) {
      message += "Track added to your playlist.";
    }

    Snackbar.make(((Activity) getContext()).findViewById(android.R.id.content), message,
        Snackbar.LENGTH_LONG).setAction("Undo", new OnClickListener() {
      @Override public void onClick(View view) {
        fuseTracks.add(position, track);
        adapter.notifyDataSetChanged();
      }
    }).setActionTextColor(Color.RED).show();
  }

  @Override
  public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
    for (int position : reverseSortedPositions) {
      notifyUndo(position, -1, fuseTracks.get(position), Direction.LEFT);

      fuseTracks.remove(position);
      adapter.notifyItemRemoved(position);
    }
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
    for (int position : reverseSortedPositions) {
      notifyUndo(position, FuseApplication.getPlaylist().getSize() - 1,  fuseTracks.get(position), Direction.RIGHT);

      FuseApplication.getPlaylist().getTracks().add(fuseTracks.get(position));

      fuseTracks.remove(position);
      adapter.notifyItemRemoved(position);
    }
    adapter.notifyDataSetChanged();
  }

  private enum Direction {
    LEFT,
    RIGHT
  }
}
