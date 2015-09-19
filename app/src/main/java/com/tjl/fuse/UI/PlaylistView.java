package com.tjl.fuse.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tjl.fuse.R;
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

    search = (Button) findViewById(R.id.search_button);
    SpotifyApi api = new SpotifyApi();
    api.setAccessToken(getResources().getString(R.string.spotify_token_key));

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
        Timber.e(response.toString());
      }

      @Override public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
      }
    });
  }
}
