package com.zendesk.adtapp.ui;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.zendesk.adtapp.R;

public class WebActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    WebView webView;
    String url = null;
    String title = null;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        if (getIntent().hasExtra(KEY_URL)) {
            url = getIntent().getStringExtra(KEY_URL);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + KEY_URL);
        }
        if (getIntent().hasExtra(KEY_TITLE)) {
            actionBar = getSupportActionBar();
            title = getIntent().getStringExtra(KEY_TITLE);
            actionBar.setTitle(title);
        }
        webView = (WebView)findViewById(R.id._web_view);
        webView.setWebViewClient(new WebFragment.CustomWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(WebActivity.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                super.onReceivedError(view, request, error);
            }
        });

        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setDatabaseEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        if (url != null){
            webView.loadUrl(url);
        }

    }
}
