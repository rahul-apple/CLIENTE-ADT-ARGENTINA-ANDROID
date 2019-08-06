package com.zendesk.adtapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.zendesk.adtapp.ui.SplashActivity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zopim.android.sdk.api.ZopimChat;
import io.fabric.sdk.android.Fabric;

public class Global extends Application {

    private final static String LOG_TAG = Global.class.getSimpleName();
    public static final String CHANNEL_ONE_NAME = "CLIENTE ADT APP";
    public static final String CHANNEL_ONE_ID = "com.adtapp.android";
    public static Global Instance;
    public static Global getInstance() {
        return Instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Fabric.with(this, new Crashlytics());


        if("replace_me_chat_account_id".equals(getResources().getString(R.string.zopim_account_id))){
            Log.w(LOG_TAG, "==============================================================================================================");
            Log.w(LOG_TAG, "Zopim chat is not connected to an account, if you wish to try chat please add your Zopim accountId to 'zd.xml'");
            Log.w(LOG_TAG, "==============================================================================================================");
        }


        ZendeskConfig.INSTANCE.init(this, getResources().getString(R.string.zd_url), getResources().getString(R.string.zd_appid), getResources().getString(R.string.zd_oauth));

        ZopimChat.init(getResources().getString(R.string.zopim_account_id));
    }

    public void setupFCMNotification(String title, String message) { NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent requestIntent = new Intent(getApplicationContext(), SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, requestIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true);
            Notification notification = mBuilder.build();
//            notification.flags |= Notification.FLAG_ONGOING_EVENT;
//            notification.flags |= Notification.FLAG_NO_CLEAR;

            mBuilder.setContentIntent(contentIntent);
            int mNotificationId = 001;
            notificationManager.notify(mNotificationId, notification);
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ONE_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//            mBuilder.setOngoing(true);
            mBuilder.setChannelId(CHANNEL_ONE_ID);
            Notification notification = mBuilder.build();
//            notification.flags |= Notification.FLAG_ONGOING_EVENT;
//            notification.flags |= Notification.FLAG_NO_CLEAR;

            mBuilder.setContentIntent(contentIntent);
            int mNotificationId = 001;

            notificationManager.notify(mNotificationId, notification);
        }


    }
}
