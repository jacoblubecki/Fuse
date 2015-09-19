package com.tjl.fuse;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.tjl.fuse.player.PlayerManager;
import dagger.Component;
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

    LeakCanary.install(this);

    Timber.plant(new Timber.DebugTree());
  }

  public static FuseApplication getApplication() {
    return instance;
  }
}
