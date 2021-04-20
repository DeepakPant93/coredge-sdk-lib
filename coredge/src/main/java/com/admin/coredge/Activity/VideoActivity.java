package com.admin.coredge.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.admin.coredge.MainActivity;
import com.admin.coredge.R;
import com.admin.coredge.Services.InternetConnectivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

public class VideoActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    VideoView videoview;
    String vid_url ="http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private WebView wb;
    private ProgressBar progressBar;
    double PING = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.page_2);

//        playerView = (PlayerView) findViewById(R.id.video_view);

        wb=(WebView)findViewById(R.id.web_view1);
        progressBar = findViewById(R.id.h_load1);
        if (!InternetConnectivity.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();

        }else {
            wb.setWebViewClient(new HelloWebViewClient());
            WebSettings webSettings = wb.getSettings();
            wb.setWebChromeClient(new ChromeClient());
            wb.getSettings().setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setAllowFileAccess(true);
            wb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setMediaPlaybackRequiresUserGesture(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true); // allow pinch to zooom
            webSettings.setDisplayZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
            webSettings.setDatabaseEnabled(true);
            webSettings.setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
            try {
                wb.getClass().getMethod("onPause").invoke(wb, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                wb.getClass().getMethod("onResume").invoke(wb, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (savedInstanceState == null) {
                wb.loadUrl("https://demo.coredge.io/");
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        wb.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        wb.onResume();
    }
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // view.loadUrl(url);
            // return true;
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return false;
            } else {
                if (url.startsWith("intent://")) {
                    try {
                        Context context = wb.getContext();
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            PackageManager packageManager = context.getPackageManager();
                            ResolveInfo info = packageManager.resolveActivity(intent,
                                    PackageManager.MATCH_DEFAULT_ONLY);
                            if ((intent != null) && ((intent.getScheme().equals("https"))
                                    || (intent.getScheme().equals("http")))) {
                                String fallbackUrl = intent.getStringExtra(
                                        "browser_fallback_url");
                                wb.loadUrl(fallbackUrl);
                                return true;
                            }
                            if (info != null) {
                                context.startActivity(intent);
                            } else {
                                String fallbackUrl = intent.getStringExtra(
                                        "browser_fallback_url");
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(fallbackUrl));
                                context.startActivity(browserIntent);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    wb.getContext().startActivity(intent);
                    return true;
                }
            }

        }
        ProgressDialog progressDialog = new ProgressDialog(VideoActivity.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (wb.canGoBack())
            wb.goBack();
        else
            super.onBackPressed();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        wb.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        wb.restoreState(savedInstanceState);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }


    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.page_1:
//                    Intent intent1 = new Intent(VideoActivity.this, MainActivity.class);
//                    startActivity(intent1);
//                    return true;
//                case R.id.page_2:
////                    int s = 8;
////                    System.out.println(s/0);
//                    return true;
//                case R.id.page_3:
//                    if(PING==0) {
//                        Toast.makeText(VideoActivity.this, "Please do speed test first", Toast.LENGTH_SHORT).show();
//                    }
////                    Intent intent = new Intent(VideoActivity.this, SurveyActivity.class);
////                    startActivity(intent);
//                    return true;
            }
            return false;
        }
    };
}
