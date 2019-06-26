package com.zendesk.adtapp.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zendesk.adtapp.BuildConfig;
import com.zendesk.adtapp.R;
import com.zendesk.adtapp.model.UserProfile;
import com.zendesk.adtapp.push.GcmUtil;
import com.zendesk.adtapp.push.RegistrationIntentService;
import com.zendesk.adtapp.storage.PushNotificationStorage;
import com.zendesk.adtapp.storage.UserProfileStorage;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.model.DeviceInfo;
import com.zendesk.sdk.model.MemoryInformation;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.request.CustomField;
import com.zendesk.sdk.network.impl.DefaultSdkOptions;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.util.FileUtils;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rahulramachandra on 24/07/17.
 */

public class RootViewActivity extends AppCompatActivity implements WebFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = RootViewActivity.class.getSimpleName();

    public static final String EXTRA_VIEWPAGER_POSITION = "extra_viewpager_pos";
    public static final int VIEWPAGER_POS_DATES = 0;
    public static final int VIEWPAGER_POS_HELP = 1;

    private static final long TICKET_FORM_ID = 62599l;
    private static final long TICKET_FIELD_APP_VERSION = 24328555l;
    private static final long TICKET_FIELD_OS_VERSION = 24273979l;
    private static final long TICKET_FIELD_DEVICE_MODEL = 24273989l;
    private static final long TICKET_FIELD_DEVICE_MEMORY = 24273999;
    private static final long TICKET_FIELD_DEVICE_FREE_SPACE = 24274009l;
    private static final long TICKET_FIELD_DEVICE_BATTERY_LEVEL = 24274019l;

    private UserProfileStorage mStorage;
    private PushNotificationStorage mPushStorage;

    private Button HomeButton,WebButton;
    private HelpFragment home;
    private WebFragment web;
    private ActionBar actionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zendesk.adtapp.R.layout.root_view_activity);
        HomeButton = (Button)findViewById(R.id.home_button);
        WebButton = (Button)findViewById(R.id.web_button);
        actionBar = getSupportActionBar();
        Logger.setLoggable(true);
        initialiseSdk();
        home = HelpFragment.newInstance(0);
        web = WebFragment.newInstance("http://www.google.com/","");
        // Set up the action bar.
        actionBar.show();
//        actionBar.hide();
        if (savedInstanceState == null) {
            HelpFragment frag = HelpFragment.newInstance(0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.root_frag_container,frag).commit();
            frag.getView();
        }
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.hide();
                loadFragment(home);
            }
        });
        WebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.show();
                loadFragment(web);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initialisePush();
    }

    void initialiseSdk() {
        mStorage = new UserProfileStorage(this);
        mPushStorage = new PushNotificationStorage(this);

        final UserProfile profile = mStorage.getProfile();
        if (StringUtils.hasLength(profile.getEmail())){
            Logger.i("Identity", "Setting identity");
            ZendeskConfig.INSTANCE.setIdentity(new AnonymousIdentity.Builder().withNameIdentifier(profile.getName()).withEmailIdentifier(profile.getEmail()).build());
            ZendeskConfig.INSTANCE.setSdkOptions(new MySdkOptions());
            // Init Zopim Visitor info
            final VisitorInfo.Builder build = new VisitorInfo.Builder()
                    .email(profile.getEmail());

            if(StringUtils.hasLength(profile.getName())){
                build.name(profile.getName());
            }

            ZopimChat.setVisitorInfo(build.build());
        }else{
            startActivity(new Intent(this, CreateProfileActivity.class));

        }

        ZendeskConfig.INSTANCE.setCustomFields(getCustomFields());
    }

    private List<CustomField> getCustomFields(){
        final DeviceInfo deviceInfo = new DeviceInfo(this);
        final MemoryInformation memoryInformation = new MemoryInformation(this);

        final String appVersion = String.format(
                Locale.US,
                "version_%s",
                BuildConfig.VERSION_NAME
        );

        final String osVersion = String.format(
                Locale.US,
                "Android %s, Version %s",
                deviceInfo.getVersionName(), deviceInfo.getVersionCode()
        );

        final String deviceModel = String.format(
                Locale.US,
                "%s, %s, %s",
                deviceInfo.getModelName(), deviceInfo.getModelDeviceName(), deviceInfo.getModelManufacturer()
        );

        final StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        final long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        final String freeSpace = FileUtils.humanReadableFileSize(bytesAvailable);

        final String batteryLevel = String.format(Locale.US, "%.1f %s", getBatteryLevel(), "%");

        final List<CustomField> customFields = new ArrayList<>();
        customFields.add(new CustomField(TICKET_FIELD_APP_VERSION, appVersion));
        customFields.add(new CustomField(TICKET_FIELD_OS_VERSION, osVersion));
        customFields.add(new CustomField(TICKET_FIELD_DEVICE_MODEL, deviceModel));
        customFields.add(new CustomField(TICKET_FIELD_DEVICE_MEMORY, memoryInformation.formatMemoryUsage()));
        customFields.add(new CustomField(TICKET_FIELD_DEVICE_FREE_SPACE, freeSpace));
        customFields.add(new CustomField(TICKET_FIELD_DEVICE_BATTERY_LEVEL, batteryLevel));

        return customFields;
    }

    public float getBatteryLevel() {
        final Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        final int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    void initialisePush(){
        // Check if we already saved the device' push identifier.
        // If not, enable push.
        if(!mPushStorage.hasPushIdentifier()) {
            enablePush();
        }
    }

    void enablePush(){
        if(GcmUtil.checkPlayServices(this)){
            RegistrationIntentService.start(this);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, uri.getUserInfo(), Toast.LENGTH_SHORT).show();
    }

    class MySdkOptions extends DefaultSdkOptions {
        @Override
        public boolean overrideResourceLoadingInWebview() {
            return true;
        }
    }


    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.root_frag_container, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}




