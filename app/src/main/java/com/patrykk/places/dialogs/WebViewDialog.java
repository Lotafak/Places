package com.patrykk.places.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * WebView DialogFragment for showing Foursquare pages associated with venues
 */
public class WebViewDialog extends DialogFragment {

    private String venueUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    private List<String> previous = new ArrayList<>();
    private String mLastUrl;

    public WebViewDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param venueUrl url to display
     * @return A new instance of fragment WebViewDialog.
     */
    public static WebViewDialog newInstance(String venueUrl) {
        WebViewDialog fragment = new WebViewDialog();
        Bundle args = new Bundle();
        args.putString(Constants.VENUE_URL, venueUrl);
        fragment.setArguments(args);
        return fragment;
    }



    /**
     * Called to do initial creation of a fragment. This is called after {@link #onAttach(Context)}
     * and before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}. Note that this can
     * be called while the fragment's activity is still in the process of being created. As such,
     * you can not rely on things like the activity's content view hierarchy being initialized at
     * this point. If you want to do work once the activity itself is created, see
     * {@link #onActivityCreated(Bundle)}. Any restored child fragments will be created before the base
     * Fragment.onCreate method returns.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            venueUrl = getArguments().getString(Constants.VENUE_URL);
        } else {
            Toast.makeText(getContext(), "Couldn't load page", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web_view_dialog, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.pB1);

        getDialog().setCanceledOnTouchOutside(true);

        mWebView = (WebView) v.findViewById(R.id.web_view);
        mWebView.loadUrl(venueUrl);
        mWebView.canGoBack();
        mWebView.canGoForward();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), error.getDescription().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("Debug", "onPageFinished " + url);
                mLastUrl = url;
                if (previous.isEmpty() || !previous.get(previous.size() - 1).equals(mLastUrl)) {
                    previous.add(mLastUrl);
                }
                mProgressBar.setVisibility(ProgressBar.GONE);
                mProgressBar.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(venueUrl);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                mProgressBar.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress < 100 && mProgressBar.getVisibility() == ProgressBar.GONE){
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }

                mProgressBar.setProgress(newProgress);
                if(newProgress == 100) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        return v;
    }

    /**
     * Overrides onBackPressed() Dialog's method with behavior that stops dialog from being
     * dismissed if there was another page opened before on stack.
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                int size = previous.size();
                if(size > 1 ){
                    mWebView.loadUrl(previous.get(size-2));
                    previous.remove(size-1);
                }
                else {
                    super.onBackPressed();
                }
            }
        };
    }
}
