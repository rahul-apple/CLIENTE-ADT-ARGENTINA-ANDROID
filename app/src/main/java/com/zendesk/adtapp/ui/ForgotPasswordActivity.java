package com.zendesk.adtapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.zendesk.adtapp.R;
import com.zendesk.adtapp.model.UserProfile;
import com.zendesk.adtapp.storage.UserProfileStorage;

/**
 * Created by rahulramachandra on 09/10/17.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
    private WebView webView;
    private UserProfileStorage mUserProfileStorage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_activity);
        mUserProfileStorage = new UserProfileStorage(this);
        webView = (WebView) findViewById(R.id.forgot_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        Boolean isM = getIntent().getBooleanExtra("IS_PASSWORD",false);
        if (isM){
            String imageUrl =  "https://www.adt.com.ar/?/residencial/servicio-al-cliente/numero-de-cliente"; //"https://www.adt.com.ar/numero-de-cliente";
            webView.loadUrl(imageUrl);
            getSupportActionBar().setTitle(R.string.fragment_forgot_pass);

        }else {
            Boolean isRobotChat = getIntent().getBooleanExtra("IS_OPEN_CHAT",false);
            if (isRobotChat){
                String imageUrl =  "https://embed.agentbot.net/adtapp/3adb4fba2c7c473e174c8c990363ed6e";
                webView.setWebChromeClient(new WebChromeClient());
                webView.loadUrl(imageUrl);
                getSupportActionBar().setTitle(R.string.chat_activity_title);
            }else{
                UserProfile userProfile = mUserProfileStorage.getProfile();
                String imageUrl =  "https://www.localizar-t.com/dashboard/clients/adt/ver_facturas.php?cliente="+userProfile.getAccountNumber().toString()+"&email="+userProfile.getEmail();
                webView.loadUrl(imageUrl);
                getSupportActionBar().setTitle(R.string.fragment_forgot_fact);
            }


        }


    }
}
