package com.tjl.fuse;

import android.app.Application;
import com.parse.Parse;
import com.squareup.leakcanary.LeakCanary;
import com.tjl.fuse.player.PlayerManager;
import dagger.Component;
import java.text.ParseException;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * Created by Jacob on 9/18/15.
 */
public class FuseApplication extends Application {

  @Singleton @Component(modules = ApplicationModule.class) public interface AppComponent {
    void inject(FuseApplication application);

    void inject(PlayerManager manager);
  }

  private static FuseApplication instance;

  @Override public void onCreate() {
    super.onCreate();

    instance = this;

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
}
