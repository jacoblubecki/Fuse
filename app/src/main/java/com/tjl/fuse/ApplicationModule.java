package com.tjl.fuse;

import android.content.Context;
import com.tjl.fuse.player.PlayerModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by Jacob on 9/18/15.
 */
@Module(includes = PlayerModule.class)
public class ApplicationModule {
  private final FuseApplication application;

  public ApplicationModule(FuseApplication application) {
    this.application = application;
  }

  @Provides @Singleton @AppContext Context provideApplicationContext() {
    return application;
  }
}
