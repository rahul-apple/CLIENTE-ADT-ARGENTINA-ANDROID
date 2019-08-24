package com.zendesk.adtapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zendesk.adtapp.model.UserProfile;
import com.zendesk.adtapp.push.PushUtils;
import com.zendesk.adtapp.storage.PushNotificationStorage;
import com.zendesk.adtapp.storage.UserProfileStorage;
import com.zendesk.adtapp.ui.SplashActivity;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChat;

import io.fabric.sdk.android.Fabric;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;


public class Global extends Application {

    private final static String LOG_TAG = Global.class.getSimpleName();
    public static final String CHANNEL_ONE_NAME = "CLIENTE ADT APP";
    public static final String CHANNEL_ONE_ID = "com.adtapp.android";
    public static final String FCM_CHANNEL_NAME = "notificaciones";
    public static Global Instance;
    public static Global getInstance() {
        return Instance;
    }
    private PushNotificationStorage mPushStorage;
    private UserProfileStorage mUserProfileStorage;



    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Fabric.with(this, new Crashlytics());
        mPushStorage = new PushNotificationStorage(this);
        mUserProfileStorage = new UserProfileStorage(this);

        if("replace_me_chat_account_id".equals(getResources().getString(R.string.zopim_account_id))){
            Log.w(LOG_TAG, "==============================================================================================================");
            Log.w(LOG_TAG, "Zopim chat is not connected to an account, if you wish to try chat please add your Zopim accountId to 'zd.xml'");
            Log.w(LOG_TAG, "==============================================================================================================");
        }


        Zendesk.INSTANCE.init(this, getResources().getString(R.string.zd_url), getResources().getString(R.string.zd_appid), getResources().getString(R.string.zd_oauth));
        Support.INSTANCE.init(Zendesk.INSTANCE);
        ZopimChat.init(getResources().getString(R.string.zopim_account_id));
        UserProfile userProfile = mUserProfileStorage.getProfile();
        String email = userProfile.getEmail();
        if (StringUtils.hasLength(email)){
            Identity identity = new AnonymousIdentity.Builder()
                    .withNameIdentifier(userProfile.getName().toString())
                    .withEmailIdentifier(userProfile.getEmail().toString())
                    .build();
            // Update identity in Zendesk Support SDK
            Zendesk.INSTANCE.setIdentity(identity);
            PushUtils.registerWithZendesk();
        }
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
    public void updateRefreshedTokenToWS(){
        UserProfile userProfile = mUserProfileStorage.getProfile();
        String code = userProfile.getAccountNumber();
        String email = userProfile.getEmail();
        if (StringUtils.hasLength(code) && StringUtils.hasLength(email)){
            String token = "";
            if (mPushStorage.hasFCMPushIdentifier()) {
                token = mPushStorage.getFCMPushIdentifier();
            }else{
//                Toast.makeText(this, "FCM TOKEN ERROR", Toast.LENGTH_SHORT).show();
            }
            String urlstring = "https://www.adtfindu.com/dashboard/clients/adt/ver_facturas.php?cliente=" + code + "&email=" + email + "&micuenta=1&push_id=" + token;
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(urlstring,new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    subscribeFCMChannel();
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                }
            });
        }

    }
    public void subscribeFCMChannel(){
        FirebaseMessaging.getInstance().subscribeToTopic(FCM_CHANNEL_NAME)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(LOG_TAG, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
