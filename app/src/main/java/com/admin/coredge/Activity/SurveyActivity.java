package com.admin.coredge.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.admin.coredge.Activity.LanguageSelection.LocaleHelper;
import com.admin.coredge.MainActivity;
import com.admin.coredge.R;
import com.admin.coredge.Services.Connectivity;
import com.admin.coredge.Services.MqttHelper;
import com.admin.coredge.Services.NotificationHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SurveyActivity extends AppCompatActivity {

    RadioGroup radiogrp1,radiogrp2,radiogrp3,radiogrp4, radiogrp5,radiogrp6,radiogrp7,radiogrp8;
    RadioButton que1,que2, que3, que4, que5, que6, que7, que8;
    String data1, law, report, finance, contryCode, regionCode;
    private static String URL_SERV = "http://159.65.144.39:9000/api/v1/survey/";
    RatingBar ratingBar;
    EditText comment;
    private ProgressDialog dialog;
    TelephonyManager telephonyManager;
    Double lat, lon, latency;
    private final int REQ_CODE = 100;
    String languagePref = "en";
    MqttHelper mqttHelper;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        lat = getIntent().getExtras().getDouble("lat");
        lon = getIntent().getExtras().getDouble("lon");
        latency = getIntent().getExtras().getDouble("latency");
        regionCode = getIntent().getExtras().getString("regionCode");
        contryCode = getIntent().getExtras().getString("CountryCode");
        Log.e("MGLogTag", "json123lat : " + lat+lon+latency+regionCode+contryCode);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.page_3);
        Button getRating = findViewById(R.id.getRating);
        Button mat = findViewById(R.id.dev);
        Button pushBtn = findViewById(R.id.push);
        pushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.publishMessage("Message from Android MQTT");
            }
        });
//        startMqtt();

        // Publish a sample messag
        //e



        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = Connectivity.isConnectedFast(SurveyActivity.this);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SurveyActivity.this);
                builder1.setTitle("metrix");
                builder1.setMessage(type+"\n"+"\n"+telephonyManager.getSignalStrength()+"\n"+"\n"+"Device "+Build.BRAND+",  HARDWARE "+Build.HARDWARE+", Board "+Build.BOARD+", manufacturer "+Build.MANUFACTURER);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onVisibleBehindCanceled();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });

        comment = findViewById(R.id.input_comment);
        dialog = new ProgressDialog(this);

        ratingBar = findViewById(R.id.rating);
       // ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));
        radiogrp1=(RadioGroup)findViewById(R.id.radiogrp1);
        radiogrp2=(RadioGroup)findViewById(R.id.radiogrp2);
        radiogrp3=(RadioGroup)findViewById(R.id.radiogrp3);
        radiogrp4=(RadioGroup)findViewById(R.id.radiogrp4);
        radiogrp5=(RadioGroup)findViewById(R.id.radiogrp5);
        radiogrp6=(RadioGroup)findViewById(R.id.radiogrp6);
        radiogrp7=(RadioGroup)findViewById(R.id.radiogrp7);
        radiogrp8=(RadioGroup)findViewById(R.id.radiogrp8);

        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String rating = "Rating is :" + ratingBar.getRating();
               // Toast.makeText(SurveyActivity.this, "under development", Toast.LENGTH_LONG).show();


                if(que1==null && que2==null && que3 == null && que4==null && que5==null && que6 == null & que7==null && que8==null ){
                    Toast.makeText(SurveyActivity.this, "Please fill all question", Toast.LENGTH_SHORT).show();
                }else if(ratingBar.getRating() == 0){
                    Toast.makeText(SurveyActivity.this, "Please rate the app", Toast.LENGTH_SHORT).show();
                }else {

                    String comments = comment.getText().toString().trim();
                    String rating = String.valueOf(ratingBar.getRating());


                    if (!comments.isEmpty() && !rating.isEmpty()) {
                        NLPOperation();
                    } else {
                        comment.setError("Enter comments");
                        Toast.makeText(SurveyActivity.this, "please fill all question", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ImageView speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
//                String lang = Locale.getDefault().getLanguage();
//
//                Toast.makeText(getApplicationContext(),
//                        lang,
//                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().getLanguage());
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, Locale.getDefault().getLanguage());
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    comment.setText((CharSequence) result.get(0));
                }
                break;
            }
        }
    }
    public void radiogrp1click(View v)
    {

        int radioid1 = radiogrp1.getCheckedRadioButtonId();
        que1 = findViewById(radioid1);
      //  data1 = que1.getText().toString();
        //  Toast.makeText(SurveyActivity.this, "Data: " + dataradio.getText(), Toast.LENGTH_SHORT).show();

    }

    public void radiogrp2click(View v)
    {

        int radioid2 = radiogrp2.getCheckedRadioButtonId();
        que2 = findViewById(radioid2);
       // law=que2.getText().toString();
      //  Toast.makeText(SurveyActivity.this, "call: " + lawradio.getText(), Toast.LENGTH_SHORT).show();

    }

    public void radiogrp3click(View v)
    {

        int radioid3 = radiogrp3.getCheckedRadioButtonId();
        que3 = findViewById(radioid3);
       // report=que3.getText().toString();
       // Toast.makeText(SurveyActivity.this, "Internet: " + reportradio.getText(), Toast.LENGTH_SHORT).show();

    }
    public void radiogrp4click(View v)
    {

        int radioid4 = radiogrp4.getCheckedRadioButtonId();
        que4 = findViewById(radioid4);
       // data1 = que4.getText().toString();
       // Toast.makeText(SurveyActivity.this, "Data: " + dataradio.getText(), Toast.LENGTH_SHORT).show();

    }

    public void radiogrp5click(View v)
    {

        int radioid5 = radiogrp5.getCheckedRadioButtonId();
        que5 = findViewById(radioid5);
       // law=que5.getText().toString();
      //  Toast.makeText(SurveyActivity.this, "call: " + lawradio.getText(), Toast.LENGTH_SHORT).show();

    }

    public void radiogrp6click(View v)
    {

        int radioid6 = radiogrp6.getCheckedRadioButtonId();
        que6 = findViewById(radioid6);
       // report=que6.getText().toString();
       // Toast.makeText(SurveyActivity.this, "Internet: " + reportradio.getText(), Toast.LENGTH_SHORT).show();

    }
    public void radiogrp7click(View v)
    {

        int radioid7 = radiogrp7.getCheckedRadioButtonId();
        que7 = findViewById(radioid7);
      //  data1 = que7.getText().toString();
      //  Toast.makeText(SurveyActivity.this, "Data: " + dataradio.getText(), Toast.LENGTH_SHORT).show();

    }

    public void radiogrp8click(View v)
    {

        int radioid8 = radiogrp8.getCheckedRadioButtonId();
        que8 = findViewById(radioid8);
       // law=que8.getText().toString();
      //  Toast.makeText(SurveyActivity.this, "call: " + lawradio.getText(), Toast.LENGTH_SHORT).show();

    }

    private void NLPOperation(){
        dialog.setMessage("Doing NLP Operation, please wait...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://twinword-sentiment-analysis.p.rapidapi.com/analyze/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                             String type = jsonObject.getString("type");
                                Double score = jsonObject.getDouble("score");
                                Double ratio = jsonObject.getDouble("ratio");
                        //    Log.e("MGLogTag", "type : " + type+score+ratio);

                            String [] nlpVal = response.split("author");
                            PushSurvey(nlpVal[0], type, score, ratio);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SurveyActivity.this, "register error" + e.toString(), Toast.LENGTH_LONG).show();
                            // button.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // button.setVisibility(View.VISIBLE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();

                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parsing error! Please try again after some time!", Toast.LENGTH_SHORT).show();
                        }else if(error!=null && error.getMessage() !=null){
                            Toast.makeText(getApplicationContext(),"error VOLLEY " + error.getMessage() ,Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("text", comment.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("content-type", "application/x-www-form-urlencoded");
                headers.put("x-rapidapi-key", "bff6a5f0c2msha80e31330bfb092p1bf1d3jsn34fe499d9c0c");
                headers.put("x-rapidapi-host", "twinword-sentiment-analysis.p.rapidapi.com");
                return headers;
            }


        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void PushSurvey(String nlp, String type, Double score, Double ratio) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ans1", que1.getText().toString());
            jsonObject.put("ans2", que2.getText().toString());
            jsonObject.put("ans3", que3.getText().toString());
            jsonObject.put("ans4", que4.getText().toString());
            jsonObject.put("ans5", que5.getText().toString());
            jsonObject.put("ans6", que6.getText().toString());
            jsonObject.put("ans7", que7.getText().toString());
            jsonObject.put("ans8", que8.getText().toString());
            jsonObject.put("ans9", ratingBar.getRating());
            jsonObject.put("ans10", comment.getText().toString().trim());
            jsonObject.put("type", type);
            jsonObject.put("score", score);
            jsonObject.put("ratio", ratio);
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("latency", latency);
            jsonObject.put("CountryCode", contryCode);
            jsonObject.put("regioncode", regionCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("MGLogTag", "json : " + jsonObject);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL_SERV, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            // speedTest.setVisibility(View.VISIBLE);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            try {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SurveyActivity.this);
                                builder1.setTitle("NLP Result:");
                                builder1.setMessage(nlp);
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                String msg = response.getString("result");
                               // Toast.makeText(SurveyActivity.this, "data pushed", Toast.LENGTH_SHORT).show();
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.page_1:
//                    Intent intent1 = new Intent(SurveyActivity.this, MainActivity.class);
//                    startActivity(intent1);
//                    return true;
//                case R.id.page_2:
//                    Intent intent = new Intent(SurveyActivity.this, VideoActivity.class);
//                    startActivity(intent);
//                    return true;
//                case R.id.page_3:
//                    return true;
            }
            return false;
        }
    };


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {

//            case R.id.radio_english:
//
//                if (checked)
//                    languagePref = "en";
//                break;
//
//            case R.id.radio_indonesian:
//
//                if (checked)
//
//                    languagePref = "hi";
//                break;
        }

        if (!languagePref.isEmpty()) {

            LocaleHelper.setLocale(SurveyActivity.this, languagePref);

            recreate();
        }
    }

//    private void startMqtt(){
//        mqttHelper = new MqttHelper(getApplicationContext());
//        mqttHelper.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean b, String s) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable throwable) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//                Log.w("mqtt",mqttMessage.toString());
//                 comment.setText(mqttMessage.toString());
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//            }
//        });
//    }
}