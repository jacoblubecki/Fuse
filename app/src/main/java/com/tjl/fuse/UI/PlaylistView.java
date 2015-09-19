package com.tjl.fuse.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
import com.tjl.fuse.utils.preferences.StringPreference;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class PlaylistView extends LinearLayout {
  Button search;
  SpotifyService spotify;

  public PlaylistView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void onFinishInflate() {
    super.onFinishInflate();

    String tokenKey = getContext().getString(R.string.spotify_token_key);
    String token = new StringPreference(getContext(), tokenKey).get();

    search = (Button) findViewById(R.id.search_button);
    SpotifyApi api = new SpotifyApi();
    api.setAccessToken(token);

    spotify = api.getService();

    search.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        search();
      }
    });
  }

  public void search() {
    spotify.searchTracks("magic", new Callback<TracksPager>() {
      @Override public void success(TracksPager tracksPager, Response response) {
        Timber.e(tracksPager.tracks.items.get(0).name);
      }

      @Override public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
      }
    });
  }
}
