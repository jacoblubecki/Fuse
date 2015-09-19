package com.tjl.fuse.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import com.tjl.fuse.R;
import com.tjl.fuse.adapter.SearchAdapter;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import com.tjl.fuse.utils.preferences.StringPreference;
import java.util.ArrayList;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class FuseSearchView extends LinearLayout {
  EditText searchText;
  private SearchView searchView;
  private Button searchButton;
  private SpotifyService spotify;
  private String songUri;
  private PlayerManager playerManager;
  private RecyclerView recyclerView;
  SearchAdapter adapter;


  public FuseSearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();



    String tokenKey = getContext().getString(R.string.spotify_token_key);
    String token = new StringPreference(getContext(), tokenKey).get();
    playerManager = PlayerManager.getInstance();

    searchView = (SearchView) findViewById(R.id.search_view);
    searchButton = (Button) findViewById(R.id.fuse_search);
    SpotifyApi api = new SpotifyApi();
    api.setAccessToken(token);

    spotify = api.getService();

    playerManager = PlayerManager.getInstance();
    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    ArrayList<FuseTrack> items = new ArrayList<>();
    if(playerManager.getQueue()!=null) {

      for (FuseTrack track : playerManager.getQueue().getTracks()) {
        items.add(track);
      }
    }
   adapter = new SearchAdapter(items);

    recyclerView = (RecyclerView) findViewById(R.id.search_list_view);
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);

    invalidate();


    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        search(searchView.getQuery().toString());
      }
    });


  }

  public void search(String query) {

    spotify.searchTracks(query, new Callback<TracksPager>() {
      @Override public void success(TracksPager tracksPager, Response response) {
        Timber.e(tracksPager.tracks.items.get(0).name);
        Timber.e("size is " + tracksPager.tracks.items.size());
        ArrayList<FuseTrack> fuseTracks = new ArrayList<>();
        for (Track t : tracksPager.tracks.items) {
          FuseTrack fuseTrack = new FuseTrack(t);
          fuseTracks.add(fuseTrack);
          //TODO populate a list of shit

        }
        playerManager.setQueue(new Queue(fuseTracks));
        playerManager.play();
        adapter.notifyDataSetChanged();//TODO make this list show once it has been searched 
      }

      @Override public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
      }
    });
  }
}
