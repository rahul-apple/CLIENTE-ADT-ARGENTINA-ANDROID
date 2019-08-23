package com.zendesk.adtapp.push;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

import static zendesk.support.guide.HelpCenterFragment.LOG_TAG;


public class ZendeskFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        Log.d(LOG_TAG, "Firebase token updated");
        PushUtils.registerWithZendesk();
    }

}
