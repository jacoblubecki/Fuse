package com.tjl.fuse.player;

import android.content.Context;
import android.media.MediaPlayer;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.tjl.fuse.R;
import com.tjl.fuse.utils.preferences.StringPreference;
import timber.log.Timber;

import static com.tjl.fuse.player.State.PAUSED;
import static com.tjl.fuse.player.State.PLAYING;
import static com.tjl.fuse.player.State.STOPPED;

/**
 * Created by Jacob on 9/18/15.
 */
public class SpotifyPlayer extends FusePlayer
    implements Player.InitializationObserver, ConnectionStateCallback, PlayerNotificationCallback {

  private final Player player;
  private MediaPlayer.OnCompletionListener listener;

  public SpotifyPlayer(Context context) {
    String tokenKey =  context.getResources().getString(R.string.spotify_token_key);
    String clientId = context.getString(R.string.spotify_client_id);
    String authToken = new StringPreference(context, tokenKey).get();

    if (authToken != null) {
      Config config = new Config(context, authToken, clientId);
      player = Spotify.getPlayer(config, this, this);
    } else {
      throw new IllegalStateException("Spotify auth token was null.");
    }
  }

  public void setListener(MediaPlayer.OnCompletionListener listener) {
    this.listener = listener;
  }

  public void start(String uri) {
    changeState(State.PLAYING);
    player.play(uri);
    Timber.i("Playing track with uri %s.", uri);
  }

  @Override public void play() {
    switch (state) {
      case PLAYING:
        Timber.w("Tried to play but was already playing.");
        break;

      case PAUSED:
        changeState(PLAYING);
        player.resume();
        break;

      case STOPPED:
        Timber.w("Tried to play a stopped player.");
        break;
    }
  }

  @Override public void pause() {
    switch (state) {
      case PLAYING:
        changeState(PAUSED);
        player.pause();
        break;

      case PAUSED:
        Timber.w("Tried to pause but was already paused.");
        break;

      case STOPPED:
        Timber.w("Tried to play a stopped player.");
        break;
    }
  }

  @Override public void release() {
    changeState(STOPPED);
    Spotify.destroyPlayer(this);
  }

  @Override public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
    switch (eventType) {

      case END_OF_CONTEXT:
        pause();
        listener.onCompletion(null);
        break;

      case PAUSE:

      case LOST_PERMISSION:
        pause();
        changeState(State.PAUSED);

        //HarmonyApp app = (HarmonyApp) HarmonyActivity.getInstance().getApplication();
        //if (app.isServiceRunning(ForegroundService.class)) {
          //Intent startIntent = new Intent(HarmonyActivity.getInstance(), ForegroundService.class);
          //startIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
          //HarmonyActivity.getInstance().startService(startIntent);
        //}
        break;
    }

    Timber.i("DEFAULT: %s  %s  %s  %s", eventType.name(), playerState.playing, playerState.positionInMs, playerState.durationInMs);
  }

  @Override public void onPlaybackError(ErrorType errorType, String s) {
    Timber.e("Playback error: " + errorType.toString() + "\t" + s);
  }

  @Override public void onLoggedIn() {
    Timber.i("Logged in.");
  }

  @Override public void onLoggedOut() {
    Timber.w("Logged out.");
  }

  @Override public void onLoginFailed(Throwable throwable) {
    Timber.e("Logged failed.");
  }

  @Override public void onTemporaryError() {
    Timber.e("Temporary error playing Spotify.");
  }

  @Override public void onConnectionMessage(String s) {
    Timber.i("Connection message: " + s);
  }

  @Override public void onInitialized(Player player) {
    player.addConnectionStateCallback(this);
    player.addPlayerNotificationCallback(this);
    Timber.i("Initialized");
  }

  @Override public void onError(Throwable throwable) {
    Timber.e(throwable, "Error initializing Spotify.");
  }
}
