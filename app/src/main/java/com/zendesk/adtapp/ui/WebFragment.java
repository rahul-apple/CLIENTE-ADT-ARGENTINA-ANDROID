package com.zendesk.adtapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.asksira.webviewsuite.WebViewSuite;
import com.zendesk.adtapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String urlParam;
    private String mParam2;

    private WebView webView;
    private WebViewSuite webViewSuite;

    private OnFragmentInteractionListener mListener;

    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param urlString Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebFragment newInstance(String urlString, String param2) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, urlString);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            urlParam = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView)view.findViewById(R.id.web_frag_webview);
        webView.setWebViewClient(new CustomWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Toast.makeText(getActivity(), "Started Loading...", Toast.LENGTH_SHORT).show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                Toast.makeText(getActivity(), "Finished Loading", Toast.LENGTH_SHORT).show();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(getActivity(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                super.onReceivedError(view, request, error);
            }
        });
//        webView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
////                Toast.makeText(getActivity(), "Loading.." + newProgress, Toast.LENGTH_SHORT).show();
//                super.onProgressChanged(view, newProgress);
//            }
//        });
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


//        webViewSuite = (WebViewSuite)rootView.findViewById(R.id.webViewSuite);
//        webViewSuite.customizeClient(new WebViewSuite.WebViewSuiteCallback() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                //Do your own stuffs. These will be executed after default onPageStarted().
//                Toast.makeText(getActivity(), "Started Loading", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                //Do your own stuffs. These will be executed after default onPageFinished().
//                Toast.makeText(getActivity(), "Loading finished", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                //Override those URLs you need and return true.
//                //Return false if you don't need to override that URL.
//                return true;
//            }
//
//
//        });
//        webViewSuite.interfereWebViewSetup(new WebViewSuite.WebViewSetupInterference() {
//            @Override
//            public void interfereWebViewSetup(WebView webView) {
//                WebSettings webSettings = webView.getSettings();
//                webSettings.setJavaScriptEnabled(true);
//                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//                webSettings.setDomStorageEnabled(true);
//            }
//        });
        if (urlParam != null){
//            webViewSuite.startLoading(urlParam);
            webView.loadUrl(urlParam);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
