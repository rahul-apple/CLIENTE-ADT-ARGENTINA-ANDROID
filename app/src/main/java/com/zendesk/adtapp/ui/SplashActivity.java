package com.zendesk.adtapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zendesk.adtapp.R;

public class SplashActivity extends AppCompatActivity {
    final String PREFS_NAME = "MyPrefsFile";
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                if (settings.getBoolean("my_first_time", true)) {
                    //the app is being launched for first time, Showing the IntroView.
                    Log.d("Comments", "First time");
                    settings.edit().putBoolean("my_first_time", false).commit();
                    Intent intent=new Intent(getApplicationContext(),IntroActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(SplashActivity.this,RootViewActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        },3000);
    }
}
