package com.zendesk.adtapp.ui;

import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
                final String imageUrl =  "https://embed.agentbot.net/adtapp/3adb4fba2c7c473e174c8c990363ed6e";
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        String message = "SSL Certificate error.";
                        switch (error.getPrimaryError()) {
                            case SslError.SSL_UNTRUSTED:
                                message = "The certificate authority is not trusted.";
                                break;
                            case SslError.SSL_EXPIRED:
                                message = "The certificate has expired.";
                                break;
                            case SslError.SSL_IDMISMATCH:
                                message = "The certificate Hostname mismatch.";
                                break;
                            case SslError.SSL_NOTYETVALID:
                                message = "The certificate is not yet valid.";
                                break;
                        }
                        message += " Do you want to continue anyway?";

                        builder.setTitle("SSL Certificate Error");
                        builder.setMessage(message);
                        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.proceed();
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        webView.loadUrl(imageUrl);
                        return true;
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(ForgotPasswordActivity.this, "" + description, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Toast.makeText(ForgotPasswordActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
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
