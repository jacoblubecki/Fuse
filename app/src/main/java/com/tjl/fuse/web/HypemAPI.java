package com.tjl.fuse.web;

import android.content.Context;
import com.tjl.fuse.web.Services.FeaturedService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by JoshBeridon on 9/19/15.
 */
public class HypemAPI {

  private static HypemAPI instance;
  private Context context;
  private RestAdapter restAdapter;
  public FeaturedService featuredService;

  public static HypemAPI getInstance(Context context) {
    if (instance == null) {
      instance = new HypemAPI(context);
    }
    return instance;
  }
  private HypemAPI(Context context){
    this.context = context;


    restAdapter = new RestAdapter.Builder().setEndpoint("https://api.hypem.com/v2")
        .setRequestInterceptor(new RequestInterceptor() {
          @Override public void intercept(RequestFacade request) {
            request.addEncodedQueryParam("type", "premieres");
            request.addEncodedQueryParam("key", "swagger");
          }
        })
        .setLogLevel(RestAdapter.LogLevel.FULL).build();
    featuredService = restAdapter.create(FeaturedService.class);

  }

  public FeaturedService getService(){
    return featuredService;
  }

}
