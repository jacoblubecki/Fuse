package com.tjl.fuse.soundcloud;

import android.content.Context;
import android.content.Intent;
import com.tjl.fuse.R;
import com.tjl.fuse.utils.preferences.StringPreference;
import lubecki.soundcloud.webapi.android.SoundCloudAuthenticator;
import lubecki.soundcloud.webapi.android.models.AuthenticationResponse;
import lubecki.soundcloud.webapi.android.models.Authenticator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import static lubecki.soundcloud.webapi.android.models.AuthenticationResponse.*;

/**
 * Created by Jacob on 9/18/15.
 */
public class SoundCloudAuth {

  private SoundCloudAuth() {
    // No instances.
  }
  public static void startSoundCloudAuthActivity(Context context) {
    String secretClientId = context.getString(R.string.soundcloud_client_id);
    String redirect = context.getString(R.string.soundcloud_redirect);

    SoundCloudAuthenticator.openLoginActivity(context, redirect, secretClientId);
  }

  public static void handleSoundCloudAuthCallback(final Context context, Intent intent) {
    String clientId = context.getString(R.string.soundcloud_client_id);
    String clientSecret = context.getString(R.string.soundcloud_client_secret);
    String redirect = context.getString(R.string.soundcloud_redirect);

    final Authenticator auth =
        SoundCloudAuthenticator.handleResponse(intent, redirect, clientId, clientSecret);

    SoundCloudAuthenticator.AuthService service = SoundCloudAuthenticator.getAuthService();

    service.authorize(auth, new Callback<AuthenticationResponse>() {
      @Override
      public void success(AuthenticationResponse authenticationResponse, Response response) {

        String tokenKey = context.getString(R.string.soundcloud_token_key);
        if (authenticationResponse.getType().equals(TOKEN)) {
          new StringPreference(context, tokenKey).set(authenticationResponse.access_token);
        } else {
          Timber.e("Error getting SoundCloud token. Response was: " + authenticationResponse.getType());
        }
      }

      @Override public void failure(RetrofitError error) {
        Timber.e("Error getting SoundCloud token.");
      }
    });
  }
}
