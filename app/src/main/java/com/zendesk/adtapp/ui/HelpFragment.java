package com.zendesk.adtapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zendesk.adtapp.BuildConfig;
import com.zendesk.adtapp.model.UserProfile;
import com.zendesk.adtapp.storage.UserProfileStorage;
import com.zendesk.util.FileUtils;
import com.zendesk.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zendesk.commonui.UiConfig;
import zendesk.support.CustomField;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.request.RequestActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class HelpFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final long TICKET_FORM_ID = 62599L;
    private static final long TICKET_FIELD_APP_VERSION = 24328555L;
    private static final long TICKET_FIELD_DEVICE_FREE_SPACE = 24274009L;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HelpFragment newInstance(int sectionNumber) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HelpFragment() {
        // Intentionally empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.zendesk.adtapp.R.layout.fragment_main, container, false);
        final Context ctx = getActivity().getApplicationContext();

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_knowledge_base).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiConfig config = RequestActivity.builder()
                        .withTicketForm(TICKET_FORM_ID, getCustomFields())
                        .config();

                HelpCenterActivity.builder()
                        .show(getActivity(), config);
            }
        }, ctx));

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_my_app).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyApplicationActivity.class);
                startActivity(intent);
            }
        }, ctx));


        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_contact_us).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreateTicketActivity.class);
                startActivity(intent);
            }
        }, ctx));

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_my_tickets).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestActivity.class);
                startActivity(intent);
            }
        }, ctx));

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_facturas).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ForgotPasswordActivity.class);
                intent.putExtra("IS_PASSWORD", false);
                startActivity(intent);
            }
        }, ctx));

//        rootView.findViewById(R.id.login_button).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
//                startActivity(intent);
//            }
//        }, ctx));

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_rate_the_app).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new RateMyAppDialog.Builder(getActivity())
//                        .withAndroidStoreRatingButton()
//                        .withSendFeedbackButton(new ZendeskFeedbackConfiguration() {
//                            @Override
//                            public String getRequestSubject() {
//                                return "Remember the date feedback";
//                            }
//
//                            @Override
//                            public List<String> getTags() {
//                                return Arrays.asList("tag1", "tag2");
//                            }
//
//                            @Override
//                            public String getAdditionalInfo() {
//                                return "Additional info.";
//                            }
//                        })
//                        .withDontRemindMeAgainButton()
//                        .build()
//                        .showAlways(getActivity());
                startActivity(new Intent(getContext(), CreateProfileActivity.class));
            }
        }, ctx));

        rootView.findViewById(com.zendesk.adtapp.R.id.fragment_main_btn_chat).setOnClickListener(new AuthOnClickWrapper(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(getActivity(),ForgotPasswordActivity.class);
                intent.putExtra("IS_PASSWORD", false);
                intent.putExtra("IS_OPEN_CHAT", true);
                startActivity(intent);

                /*PreChatForm build = new PreChatForm.Builder()
                        .name(PreChatForm.Field.REQUIRED)
                        .email(PreChatForm.Field.REQUIRED)
                        .phoneNumber(PreChatForm.Field.OPTIONAL)
                        .message(PreChatForm.Field.OPTIONAL)
                        .build();

                ZopimChat.SessionConfig department = new ZopimChat.SessionConfig()
                        .preChatForm(build)
                        .department("The date");

                ZopimChatActivity.startActivity(getActivity(), department);*/
            }
        }, ctx));

        return rootView;
    }
    private List<CustomField> getCustomFields() {
        final String appVersion = String.format(
                Locale.US,
                "version_%s",
                BuildConfig.VERSION_NAME
        );

        final StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        final long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        final String freeSpace = FileUtils.humanReadableFileSize(bytesAvailable);


        final List<CustomField> customFields = new ArrayList<>();
        customFields.add(new CustomField(TICKET_FIELD_APP_VERSION, appVersion));
        customFields.add(new CustomField(TICKET_FIELD_DEVICE_FREE_SPACE, freeSpace));

        return customFields;
    }

    class AuthOnClickWrapper implements View.OnClickListener {

        private View.OnClickListener mOnClickListener;
        private UserProfileStorage mUserProfileStorage;
        private Context mContext;

        public AuthOnClickWrapper(View.OnClickListener onClickListener, Context context){
            this.mOnClickListener = onClickListener;
            this.mUserProfileStorage = new UserProfileStorage(context);
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {
            final UserProfile profile = mUserProfileStorage.getProfile();

            if(StringUtils.hasLength(profile.getEmail())){
                mOnClickListener.onClick(v);
            }else{
               showDialog();
//                mOnClickListener.onClick(v);
            }
        }

        private void showDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(com.zendesk.adtapp.R.string.dialog_auth_title)
                    .setPositiveButton(com.zendesk.adtapp.R.string.dialog_auth_positive_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(mContext, CreateProfileActivity.class));
                        }
                    })
                    .setNegativeButton(com.zendesk.adtapp.R.string.dialog_auth_negative_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Intentionally empty
                        }
                    });
            builder.create().show();
        }
    }
}
