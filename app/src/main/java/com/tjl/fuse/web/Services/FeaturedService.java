package com.tjl.fuse.web.Services;

import com.tjl.fuse.models.Album;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public interface FeaturedService {
  @GET("/featured") void getMyFeed(Callback<Album []> cb);
}
