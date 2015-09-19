package com.tjl.fuse.spotify;

import android.app.Activity;
import android.content.Intent;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.tjl.fuse.R;
import com.tjl.fuse.utils.preferences.StringPreference;
import timber.log.Timber;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.*;

/**
 * Created by Jacob on 9/18/15.
 */
public class SpotifyAuth {

  public static final int SPOTIFY_REQUEST_CODE = 1337;

  private SpotifyAuth() {
    // No instances.
  }

  public static void authenticate(Activity context, int requestCode) {
    String clientID = context.getResources().getString(R.string.spotify_client_id);
    String redirect = context.getResources().getString(R.string.spotify_redirect);

    String[] scopes = {
        "streaming",
        "playlist-modify-public",
        "playlist-modify-private",
        "playlist-read-private",
        "playlist-read-collaborative",
        "playlist-modify-public",
        "user-library-read",
        "user-library-modify"
    };

    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(clientID, TOKEN, redirect);
    builder.setScopes(scopes);
    AuthenticationRequest request = builder.build();

    AuthenticationClient.openLoginActivity(context, requestCode, request);
  }

  public static void handleResponse(Activity context, int resultCode, Intent intent) {

    String spotifyTokenKey = context.getResources().getString(R.string.spotify_token_key);

    AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
    if (response.getType() == TOKEN) {
      new StringPreference(context, spotifyTokenKey).set(response.getAccessToken());
    }
    else {
      Timber.i("Error Getting Spotify Token. Response was: " + response.getType().toString());
    }
  }
}
