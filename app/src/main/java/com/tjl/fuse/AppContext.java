package com.tjl.fuse;

import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jacob on 9/18/15.
 */

@Qualifier @Retention(RUNTIME)
public @interface AppContext {
}
