package com.zendesk.adtapp.push;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zendesk.adtapp.Global;
import com.zendesk.adtapp.R;
import com.zendesk.adtapp.storage.PushNotificationStorage;
import com.zendesk.util.StringUtils;

import zendesk.core.ProviderStore;
import zendesk.core.Zendesk;

import static zendesk.support.guide.HelpCenterFragment.LOG_TAG;

public class PushUtils {

    /**
     * Check if play services are installed and use able.
     *
     * @param activity An activity
     */
    public static boolean checkPlayServices(Activity activity) {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (errorCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(errorCode)) {
                apiAvailability.makeGooglePlayServicesAvailable(activity);
            } else {
                Toast.makeText(activity, R.string.push_error_device_not_compatible, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public static void registerWithZendesk() {
        final ProviderStore providerStore = Zendesk.INSTANCE.provider();

        if (providerStore == null) {
            Log.e(LOG_TAG, "Zendesk Support SDK is not initialized");
            return;
        }

        final String pushToken = FirebaseInstanceId.getInstance().getToken();
        if (StringUtils.hasLength(pushToken)) {
            PushNotificationStorage mPushStorage = new PushNotificationStorage(Global.getInstance().getApplicationContext());
            mPushStorage.storeFCMPushIdentifier(pushToken);
            Global.getInstance().updateRefreshedTokenToWS();
            providerStore.pushRegistrationProvider().registerWithDeviceIdentifier(pushToken, null);
        }
    }
}
