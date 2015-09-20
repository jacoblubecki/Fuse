package com.tjl.fuse.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tjl.fuse.player.PlayerManager;

public class KillPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlayerManager.getInstance().pause();

        Intent stopIntent = new Intent(context, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
        context.startService(stopIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }
}