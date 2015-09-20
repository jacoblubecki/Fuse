package com.tjl.fuse.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tjl.fuse.R;
import com.tjl.fuse.player.PlayerManager;
import com.tjl.fuse.player.tracks.FuseTrack;
import com.tjl.fuse.ui.activities.FuseActivity;
import timber.log.Timber;

public class ForegroundService extends Service {
  @Override public int onStartCommand(final Intent intent, int flags, int startId) {

    if (intent != null && intent.getAction() != null) {
      Timber.i("Action = " + intent.getDataString() + " " + intent.getAction());

      if (!intent.getAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {

        final PlayerManager player = PlayerManager.getInstance();

        Target target = new Target() {

          @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Timber.i("Bitmap loaded.");
            handleIntent(player, bitmap, intent);
          }

          @Override public void onBitmapFailed(Drawable errorDrawable) {
            Timber.i("Bitmap failed.");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            handleIntent(player, bitmap, intent);
          }

          @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

          }
        };

        FuseTrack notifcationTrack;

        if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
          notifcationTrack = player.getQueue().getNext();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
          notifcationTrack = player.getQueue().getPrevious();
        } else {
          notifcationTrack = player.getQueue().current();
        }

        Picasso.with(this).load(notifcationTrack.image_url).into(target);
      } else {
        Timber.i("Stopped.");
        stopForeground(true);
        stopSelf(startId);
      }
    } else {
      String source = null == intent ? "Intent" : "Action";
      Timber.e(source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
    }

    return START_STICKY;
  }

  public void handleIntent(PlayerManager player, Bitmap bitmap, Intent intent) {

    final String action = intent.getAction();

    switch (action) {
      case Constants.ACTION.START_FOREGROUND_ACTION:
        Timber.i("Received Start Foreground Intent");

        if (player.isPlaying()) {
          startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), true, bitmap));
        } else {
          stopForeground(false);
          NotificationManager manager =
              (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
          manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), false, bitmap));
        }

        break;

      case Constants.ACTION.PREV_ACTION:
        Timber.i("Clicked Previous.");
        player.previous();

        if (player.isPlaying()) {
          startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        } else {
          stopForeground(false);
          NotificationManager manager =
              (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
          manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        }

        break;

      case Constants.ACTION.PLAY_ACTION:
        Timber.i("Clicked Play/Pause.");
        if (player.isPlaying()) {
          player.pause();
          startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        } else {
          player.play();
          stopForeground(false);
          NotificationManager manager =
              (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
          manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        }

        break;

      case Constants.ACTION.NEXT_ACTION:
        Timber.i("Clicked Next.");
        player.next();

        if (player.isPlaying()) {
          startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        } else {
          stopForeground(false);
          NotificationManager manager =
              (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
          manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
              createNotification(player.getQueue().current(), player.isPlaying(), bitmap));
        }
        break;

      default:
        Timber.i("Unexpected action = " + intent.getAction());
        break;
    }
  }

  public Notification createNotification(FuseTrack track, boolean isPlaying, Bitmap image) {

    // Open App
    Intent notificationIntent = new Intent(this, FuseActivity.class);
    notificationIntent.setAction(Intent.ACTION_MAIN);
    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    // Press previous
    Intent previousIntent = new Intent(this, ForegroundService.class);
    previousIntent.setAction(Constants.ACTION.PREV_ACTION);
    PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

    // Press play/pause
    Intent playIntent = new Intent(this, ForegroundService.class);
    playIntent.setAction(Constants.ACTION.PLAY_ACTION);
    PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

    // Press next
    Intent nextIntent = new Intent(this, ForegroundService.class);
    nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
    PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

    Intent deleteIntent = new Intent(this, KillPlayerReceiver.class);
    PendingIntent pendingDeleteIntent =
        PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

    Notification notification;

    if (track == null) {
      notification =
          new NotificationCompat.Builder(this).setPriority(NotificationCompat.PRIORITY_HIGH)
              .setContentTitle("Fuse")
              .setTicker("Fuse")
              .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
              .setSmallIcon(R.drawable.ic_launcher)
              .setContentIntent(pendingIntent)
              .setAutoCancel(false)
              .addAction(android.R.drawable.ic_media_play, "", pendingPlayIntent)
              .setOngoing(true)
              .setDeleteIntent(pendingDeleteIntent)
              .build();
    } else {
      notification =
          new NotificationCompat.Builder(this).setPriority(NotificationCompat.PRIORITY_HIGH)
              .setTicker("Fuse - " + track.title)
              .setContentTitle(track.title)
              .setContentText(track.artists)
              .setLargeIcon(image)
              .setSmallIcon(R.drawable.ic_launcher)
              .setContentIntent(pendingIntent)
              .setAutoCancel(false)
              .addAction(android.R.drawable.ic_media_previous, "", pendingPreviousIntent)
              .addAction((isPlaying ? android.R.drawable.ic_media_pause
                  : android.R.drawable.ic_media_play), "", pendingPlayIntent)
              .addAction(android.R.drawable.ic_media_next, "", pendingNextIntent)
              .setOngoing(isPlaying)
              .setDeleteIntent(pendingDeleteIntent)
              .build();
    }

    return notification;
  }

  @Override public IBinder onBind(Intent intent) {
    // Used only in case of bound services.
    return null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.i("Destroyed.");
  }
}