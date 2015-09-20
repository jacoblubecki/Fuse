package com.tjl.fuse.service;

public class Constants {
    public interface ACTION {
        String PREV_ACTION = "com.tommista.foregroundservice.action.prev";
        String PLAY_ACTION = "com.tommista.foregroundservice.action.play";
        String NEXT_ACTION = "com.tommista.foregroundservice.action.next";
        String START_FOREGROUND_ACTION = "com.tommista.foregroundservice.action.startforeground";
        String STOP_FOREGROUND_ACTION = "com.tommista.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}