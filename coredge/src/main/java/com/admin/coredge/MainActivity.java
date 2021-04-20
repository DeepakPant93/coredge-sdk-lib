package com.admin.coredge;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Consumer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.coredge.Activity.LoginActivity;
import com.admin.coredge.Activity.StartActivity;
import com.admin.coredge.Activity.SurveyActivity;
import com.admin.coredge.Activity.VideoActivity;
import com.admin.coredge.SpeedCalculation.NetworkConfig.SpeedConfig;
import com.admin.coredge.SpeedCalculation.NetworkConfig.TelConfig;
import com.admin.coredge.SpeedCalculation.SetupServer.ServerTestPoint;
import com.admin.coredge.SpeedCalculation.SpeedCalculation;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

import org.acra.annotation.AcraToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.net.InetAddress;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressbar, progressbar1;
    double lat, lon, PING, JITTER, DOWNLOAD, UPLOAD, DIST, rssi, rsrp, rsrq, rssnr, cqi, ta;
    String USERNAME, SERVER, PACKETLOSS, PACKETSEND, PACKETRECIVED, MDEV, LATENCY, IP, NETWORKINFO, timeduration,
            DEVICEINFO, countryName, countryCode, zip, region, regionName, city, isp, subAdmin, locality, serverResponse;
    String URL_PUSH = "http://159.65.144.39:5000/api/v1/5G/";
    TextView loadingText;
    private StringBuilder log;
    private static final int PERMISSION_REQUEST_CODE = 100;
    TelephonyManager telephonyManager;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressbar1 = (ProgressBar) findViewById(R.id.progress);
        loadingText = (TextView) findViewById(R.id.loading);

        new Thread(){
            public void run(){
                try{sleep(100);}catch (Throwable t){}
                getServer();

            }
        }.start();
//        menu = findViewById(R.id.menu);
//        USERNAME = getIntent().getExtras().getString("username");
//        lat = getIntent().getExtras().getDouble("lat");
//        lon = getIntent().getExtras().getDouble("lon");

        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE,ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            //  textView.setText(""+telephonyManager.getSignalStrength());
            Log.e("MGLogTag", "getSignalStrength : " + telephonyManager.getSignalStrength());
            DEVICEINFO=Build.BRAND;
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.page_1:
//                    return true;
//                case R.id.page_2:
//                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
//                    startActivity(intent);
//                    return true;
//                case R.id.page_3:
////                    if(PING==0) {
////                        Toast.makeText(MainActivity.this, "Please do speed test first", Toast.LENGTH_SHORT).show();
////                    }else {
////                        Intent intent1 = new Intent(MainActivity.this, SurveyActivity.class);
////                        intent1.putExtra("lat", lat);
////                        intent1.putExtra("lon", lon);
////                        intent1.putExtra("latency", PING);
////                        intent1.putExtra("regionCode", countryCode+"-"+region);
////                        intent1.putExtra("CountryCode", countryCode);
////                        startActivity(intent1);
////                    }
//                    Intent intent1 = new Intent(MainActivity.this, SurveyActivity.class);
//                        intent1.putExtra("lat", lat);
//                        intent1.putExtra("lon", lon);
//                        intent1.putExtra("latency", PING);
//                        intent1.putExtra("regionCode", countryCode+"-"+region);
//                        intent1.putExtra("CountryCode", countryCode);
//                        startActivity(intent1);
//                    return true;
            }
            return false;
        }
    };



    private static SpeedCalculation st=null;
    private void page_init(){

        new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transition(R.id.page_init,TRANSITION_LENGTH);
                    }
                });
                final TextView t=((TextView)findViewById(R.id.init_text));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.setText(R.string.init_init);
                    }
                });

                SpeedConfig config=null;
                TelConfig telemetryConfig=null;
                ServerTestPoint[] servers=null;
                try{
                    String c=readFileFromAssets("SpeedtestConfig.json");
                    JSONObject o=new JSONObject(c);
                    config=new SpeedConfig(o);
                    c=readFileFromAssets("TelemetryConfig.json");
                    o=new JSONObject(c);
                    telemetryConfig=new TelConfig(o);
                    if(telemetryConfig.getTelemetryLevel().equals(TelConfig.LEVEL_DISABLED)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideView(R.id.privacy_open);
                            }
                        });
                    }
                    if(st!=null){
                        try{st.abort();}catch (Throwable e){}
                    }
                    st=new SpeedCalculation();
                    st.setSpeedtestConfig(config);
                    st.setTelemetryConfig(telemetryConfig);
                    if(serverResponse.startsWith("\"")||serverResponse.startsWith("'")){
                        if(!st.loadServerList(serverResponse.subSequence(1,serverResponse.length()-1).toString())){
                            throw new Exception("Failed to load server list");
                        }
                    }else{ //use provided server list
                        JSONArray a=new JSONArray(serverResponse);
                        if(a.length()==0) throw new Exception("No test points");
                        ArrayList<ServerTestPoint> s=new ArrayList<>();
                        for(int i=0;i<a.length();i++) s.add(new ServerTestPoint(a.getJSONObject(i)));
                        servers=s.toArray(new ServerTestPoint[0]);
                        st.addTestPoints(servers);
                    }
                    final String testOrder=config.getTest_order();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!testOrder.contains("D")){
                                hideView(R.id.dlArea);
                            }
                            if(!testOrder.contains("U")){
                                hideView(R.id.ulArea);
                            }
                            if(!testOrder.contains("P")){
                                hideView(R.id.pingArea);
                            }
                            if(!testOrder.contains("I")){
                                hideView(R.id.ipInfo);
                            }
                        }
                    });
                }catch (final Throwable e){
                    System.err.println(e);
                    st=null;
                    transition(R.id.page_fail,TRANSITION_LENGTH);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.fail_text)).setText(getString(R.string.initFail_configError)+": "+e.getMessage());
                            final Button b=(Button)findViewById(R.id.fail_button);
                            b.setText(R.string.initFail_retry);
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    page_init();
                                    b.setOnClickListener(null);
                                }
                            });
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.setText(R.string.init_selecting);
                    }
                });
                st.selectServer(new SpeedCalculation.ServerSelectedHandler() {
                    @Override
                    public void onServerSelected(final ServerTestPoint server) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(server==null){
                                    transition(R.id.page_fail,TRANSITION_LENGTH);
                                    ((TextView)findViewById(R.id.fail_text)).setText(getString(R.string.initFail_noServers));
                                    final Button b=(Button)findViewById(R.id.fail_button);
                                    b.setText(R.string.initFail_retry);
                                    b.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            page_init();
                                            b.setOnClickListener(null);
                                        }
                                    });
                                }else{
                                    page_serverSelect(server,st.getTestPoints());
                                }
                            }
                        });
                    }
                });
            }
        }.start();
    }
    private void page_serverSelect(ServerTestPoint selected, ServerTestPoint[] servers){
        transition(R.id.page_serverSelect,TRANSITION_LENGTH);
        reinitOnResume=true;
        final ArrayList<ServerTestPoint> availableServers=new ArrayList<>();
        for(ServerTestPoint t:servers) {
            if (t.getPing() != -1) availableServers.add(t);
        }
        int selectedId=availableServers.indexOf(selected);
        final Spinner spinner=(Spinner)findViewById(R.id.serverList);
        ArrayList<String> options=new ArrayList<String>();
        for(ServerTestPoint t:availableServers){
            options.add(t.getName());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,options.toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedId);
        final ImageButton b=(ImageButton) findViewById(R.id.start);
        TextView word = (TextView)findViewById(R.id.worldText);
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinitOnResume=false;
                page_test(availableServers.get(spinner.getSelectedItemPosition()));
                b.setOnClickListener(null);
            }
        });
        TextView t=(TextView)findViewById(R.id.privacy_open);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_privacy();
            }
        });
    }

    private void page_privacy(){
        transition(R.id.page_privacy,TRANSITION_LENGTH);
        reinitOnResume=false;
        ((WebView)findViewById(R.id.privacy_policy)).loadUrl(getString(R.string.privacy_policy));
        TextView t=(TextView)findViewById(R.id.privacy_close);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transition(R.id.page_serverSelect,TRANSITION_LENGTH);
                reinitOnResume=true;
            }
        });
    }

    private void page_test(final ServerTestPoint selected){
        final long start=System.currentTimeMillis();
        transition(R.id.page_test,TRANSITION_LENGTH);
        st.setSelectedServer(selected);
        ((TextView)findViewById(R.id.serverName)).setText(selected.getName());
        SERVER = selected.getName();
        ((TextView)findViewById(R.id.dlText)).setText(format(0));
        ((TextView)findViewById(R.id.ulText)).setText(format(0));
        ((TextView)findViewById(R.id.pingText)).setText(format(0));
        ((TextView)findViewById(R.id.jitterText)).setText(format(0));
        ((TextView)findViewById(R.id.speedText)).setText(format(0));
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        ((ProgressView)findViewById(R.id.pingGauge)).setValue(0);
        ((ProgressView)findViewById(R.id.dlGauge)).setValue(0);
        ((ProgressView)findViewById(R.id.ulGauge)).setValue(0);
        ((TextView)findViewById(R.id.ipInfo)).setText("");
//        ((ImageView)findViewById(R.id.logo_inapp)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url=getString(R.string.logo_inapp_link);
//                if(url.isEmpty()) return;
//                Intent i=new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
        final View endTestArea=findViewById(R.id.endTestArea);
        final int endTestAreaHeight=endTestArea.getHeight();
        ViewGroup.LayoutParams p=endTestArea.getLayoutParams();
        p.height=0;
        endTestArea.setLayoutParams(p);
//        findViewById(R.id.shareButton).setVisibility(View.GONE);
        st.start(new SpeedCalculation.SpeedtestHandler() {
            @Override
            public void onDownloadUpdate(final double dl, final double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.dlText)).setText(progress==0?"...": format(dl));
                        ((TextView)findViewById(R.id.speedText)).setText(progress==0?"...": format(dl));
                        ((ProgressView)findViewById(R.id.dlGauge)).setValue(progress==0?0:mbpsToGauge(dl));
                        progressbar.setVisibility(View.VISIBLE);
                        DOWNLOAD = dl;
                    }
                });
            }

            @Override
            public void onUploadUpdate(final double ul, final double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.ulText)).setText(progress==0?"...": format(ul));
                        ((TextView)findViewById(R.id.speedText)).setText(progress==0?"...": format(ul));
                        ((ProgressView)findViewById(R.id.ulGauge)).setValue(progress==0?0:mbpsToGauge(ul));
                        progressbar.setVisibility(View.VISIBLE);
                        UPLOAD = ul;
                    }
                });

            }

            @Override
            public void onPingJitterUpdate(final double ping, final double jitter, final double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.pingText)).setText(progress==0?"...": format(ping));
                        ((ProgressView)findViewById(R.id.pingGauge)).setValue(progress==0?0:mbpsToGauge(ping));
                        ((TextView)findViewById(R.id.jitterText)).setText(progress==0?"...": format(jitter));
                        ((TextView)findViewById(R.id.speedText)).setText(progress==0?"...": format(jitter));
                        progressbar.setVisibility(View.VISIBLE);
                        PING = ping;
                        JITTER = jitter;
                    }
                });
            }

            @Override
            public void onIPInfoUpdate(final String ipInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.ipInfo)).setText(ipInfo);
                    }
                });
            }

            @Override
            public void onTestIDReceived(final String id, final String shareURL) {
                if(shareURL==null||shareURL.isEmpty()||id==null||id.isEmpty()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);

//                        Button shareButton=(Button)findViewById(R.id.shareButton);
//                        shareButton.setVisibility(View.VISIBLE);
//                        shareButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                                share.setType("text/plain");
//                                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                                share.putExtra(Intent.EXTRA_TEXT, shareURL);
//                                startActivity(Intent.createChooser(share, getString(R.string.test_share)));
//                            }
//                        });
                    }
                });
            }

            @Override
            public void onEnd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final long end=System.currentTimeMillis();
                         timeduration = String.valueOf(end - start);
                         pushNetworkData();
                         progressbar.setVisibility(View.GONE);
                        final ImageButton restartButton=(ImageButton) findViewById(R.id.restartButton);
                        restartButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                page_init();
                                restartButton.setOnClickListener(null);
                            }
                        });
                    }
                });
                final long startT=System.currentTimeMillis(), endT=startT+TRANSITION_LENGTH;
                new Thread(){
                    public void run(){
                        while(System.currentTimeMillis()<endT){
                            final double f=(double)(System.currentTimeMillis()-startT)/(double)(endT-startT);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ViewGroup.LayoutParams p=endTestArea.getLayoutParams();
                                    p.height=(int)(endTestAreaHeight*f);
                                    endTestArea.setLayoutParams(p);
                                }
                            });
                            try{sleep(10);}catch (Throwable t){}
                        }
                    }
                }.start();
            }

            @Override
            public void onCriticalFailure(String err) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transition(R.id.page_fail,TRANSITION_LENGTH);
                        ((TextView)findViewById(R.id.fail_text)).setText(getString(R.string.testFail_err));
                        final Button b=(Button)findViewById(R.id.fail_button);
                        b.setText(R.string.testFail_retry);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                page_init();
                                b.setOnClickListener(null);
                            }
                        });
                    }
                });
            }
        });
    }

    private void getISP(String ip) {
        String stringUrl = "http://ip-api.com/json/" + ip;
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, stringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            IP = ip;
                            isp = jsonObject.getString("isp");
                            lat = jsonObject.getDouble("lat");
                            lon = jsonObject.getDouble("lon");
                            zip = jsonObject.getString("zip");
                            countryName= jsonObject.getString("country");
                            countryCode = jsonObject.getString("countryCode");
                            region = jsonObject.getString("region");
                            regionName = jsonObject.getString("regionName");
                            city = jsonObject.getString("city");

//                            Log.e("MGLogTag", "GET IP1 : " + isp+lat+lon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private void pushNetworkData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

//        JSONArray array = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", "admin@coredge.io");
            jsonObject.put("ping", PING);
            jsonObject.put("jitter", JITTER);
            jsonObject.put("download", DOWNLOAD);
            jsonObject.put("upload", UPLOAD);
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("ip", IP);
            jsonObject.put("isp", isp);
            jsonObject.put("zipcode", zip);
            jsonObject.put("distance", DIST);
            jsonObject.put("servername", SERVER);
            jsonObject.put("timeduration", timeduration);
            jsonObject.put("countryName", countryName);
            jsonObject.put("countryCode", countryCode);
            jsonObject.put("region", regionName);
            jsonObject.put("reginCode", countryCode+"-"+region);
            jsonObject.put("Deviceinfo", DEVICEINFO);



//            jsonObject.put("packetloss", Double.parseDouble(PACKETLOSS));
//            jsonObject.put("packetsend", Double.parseDouble(PACKETSEND));
//            jsonObject.put("packetreceived", Double.parseDouble(PACKETRECIVED));
//            jsonObject.put("mdev", Double.parseDouble(mdevValue[0]));
//            jsonObject.put("networkinfo", NETWORKINFO);
//            jsonObject.put("location", ADDRESS);
//            jsonObject.put("countryName", countryName);
//            jsonObject.put("countryCode", countryCode);
//            jsonObject.put("state", adminArea);
//            jsonObject.put("subAdmin", subAdmin);
//            jsonObject.put("locality", locality);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        JSONObject jsonObject2 = new JSONObject();
//        try {
//            jsonObject2.put("value", jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        array.put(jsonObject2);
//
//        JSONObject jsonObject3 = new JSONObject();
//        try {
//            jsonObject3.put("records", array);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

       Log.e("MGLogTag", "json : " + jsonObject);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL_PUSH, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                           // speedTest.setVisibility(View.VISIBLE);
                            try {
                                String msg = response.getString("msg");
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/vnd.kafka.json.v2+json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    private void getServer() {
        progressbar1.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        String stringUrl = "http://ip-api.com/json/";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, stringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            isp = jsonObject.getString("isp");
                            lat = jsonObject.getDouble("lat");
                            lon = jsonObject.getDouble("lon");
                            zip = jsonObject.getString("zip");
                            countryName= jsonObject.getString("country");
                            countryCode = jsonObject.getString("countryCode");
                            region = jsonObject.getString("region");
                            regionName = jsonObject.getString("regionName");
                            city = jsonObject.getString("city");
                            IP = jsonObject.getString("query");
                            String stringUrl1 = "http://159.65.144.39:8888/host_ip/"+ IP;
                            RequestQueue queue1 = Volley.newRequestQueue(MainActivity.this);
                            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, stringUrl1,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response1) {
                                            String[] arrOfStr = response1.split("\"dist\":");
                                            String[] arrOfStr2 = arrOfStr[1].split("\\}");
                                            DIST = Double.parseDouble(arrOfStr2[0]);
                                            progressbar1.setVisibility(View.GONE);
                                            loadingText.setVisibility(View.GONE);
                                            serverResponse=response1;
                                            page_init();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error1) {
                                }
                            });
                            int socketTimeout = 10000;//30 seconds - change to what you want
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            stringRequest1.setRetryPolicy(policy);
                            queue1.add(stringRequest1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private String format(double d){
        Locale l=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            l = getResources().getConfiguration().getLocales().get(0);
        }else{
            l=getResources().getConfiguration().locale;
        }
        if(d<10) return String.format(l,"%.2f",d);
        if(d<100) return String.format(l,"%.1f",d);
        return ""+Math.round(d);
    }

    private int mbpsToGauge(double s){
        return (int)(1000*(1-(1/(Math.pow(1.3,Math.sqrt(s))))));
    }

    private String readFileFromAssets(String name) throws Exception{
        BufferedReader b=new BufferedReader(new InputStreamReader(getAssets().open(name)));
        String ret="";
        try{
            for(;;){
                String s=b.readLine();
                if(s==null) break;
                ret+=s;
            }
        }catch(EOFException e){}
        return ret;
    }

    private void hideView(int id){
        View v=findViewById(id);
        if(v!=null) v.setVisibility(View.GONE);
    }

    private boolean reinitOnResume=false;
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        DEVICEINFO=Build.BRAND;
        if(reinitOnResume){
            reinitOnResume=false;
            page_init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{st.abort();}catch (Throwable t){}
    }

//    @Override
//    public void onBackPressed() {
//        if(currentPage==R.id.page_privacy)
//            transition(R.id.page_serverSelect,TRANSITION_LENGTH);
//        else super.onBackPressed();
//    }


    private int currentPage=-1;
    private boolean transitionBusy=false; //TODO: improve mutex
    private int TRANSITION_LENGTH=300;
    private void transition(final int page, final int duration){
        if(transitionBusy){
            new Thread(){
                public void run(){
                    try{sleep(10);}catch (Throwable t){}
                    transition(page,duration);
                }
            }.start();
        }else transitionBusy=true;
        if(page==currentPage) return;
        final ViewGroup oldPage=currentPage==-1?null:(ViewGroup)findViewById(currentPage),
                newPage=page==-1?null:(ViewGroup)findViewById(page);
        new Thread(){
            public void run(){
                long t=System.currentTimeMillis(), endT=t+duration;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(newPage!=null){
                            newPage.setAlpha(0);
                            newPage.setVisibility(View.VISIBLE);
                        }
                        if(oldPage!=null){
                            oldPage.setAlpha(1);
                        }
                    }
                });
                while(t<endT){
                    t=System.currentTimeMillis();
                    final float f=(float)(endT-t)/(float)duration;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(newPage!=null) newPage.setAlpha(1-f);
                            if(oldPage!=null) oldPage.setAlpha(f);
                        }
                    });
                    try{sleep(10);}catch (Throwable e){}
                }
                currentPage=page;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(oldPage!=null){
                            oldPage.setAlpha(0);
                            oldPage.setVisibility(View.INVISIBLE);
                        }
                        if(newPage!=null){
                            newPage.setAlpha(1);
                        }
                        transitionBusy=false;
                    }
                });
            }
        }.start();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
//                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;

                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    //  textView.setText(""+telephonyManager.getSignalStrength());
                    Log.e("MGLogTag", "getSignalStrength : " + telephonyManager.getSignalStrength());
                    DEVICEINFO=Build.BRAND;
                }
        }
    }
}