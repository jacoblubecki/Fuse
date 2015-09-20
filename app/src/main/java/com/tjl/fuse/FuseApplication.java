package com.tjl.fuse;

import android.app.Application;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.Parse;
import com.squareup.leakcanary.LeakCanary;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.player.tracks.Queue;
import com.tjl.fuse.utils.preferences.StringPreference;
import dagger.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class FuseApplication extends Application {

  public static final String PREFS_NAME = "PrefFile";
  @Singleton @Component(modules = ApplicationModule.class) public interface AppComponent {
    void inject(FuseApplication application);

    void inject(PlayerManager manager);
  }

  private static FuseApplication instance;
  private static Queue queue;

  @Override public void onCreate() {
    super.onCreate();

    instance = this;
    queue = getPlaylist();


    // Leak detection
    LeakCanary.install(this);

    // Data
    String parseAppId = getString(R.string.parse_app_id);
    String parseClientKey = getString(R.string.parse_client_key);
    Parse.initialize(this, parseAppId, parseClientKey);

    // Logging
    if(BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  public static FuseApplication getApplication() {
    return instance;
  }

  public static void serializePlaylist() {
    Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    String json = gson.toJson(queue.getTracks().toArray());

    Timber.i(json + " ");

    new StringPreference(instance, "MAIN_QUEUE").set(json);
  }

  public static Queue getPlaylist() {
    if(queue == null) {
      Gson gson = new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
          .create();

      String json = new StringPreference(instance, "MAIN_QUEUE").get();

      Timber.i(json + " ");

      ArrayList<FuseTrack> trackList;
      if(json == null || json.isEmpty()){
        trackList = new ArrayList<>();
      }else{
        trackList = new ArrayList<>(Arrays.asList(gson.fromJson(json, FuseTrack[].class)));
      }
      queue = new Queue(trackList);
    }
    return queue;
  }
}
