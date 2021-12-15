package com.example.domiapp.channel;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID= "com.example.domiapp";
    private static final String CHANNEL_NAME= "domiapp";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
    }

    @RequiresApi (api = Build.VERSION_CODES.O)

    private  void  createChannels(){

    }
}
