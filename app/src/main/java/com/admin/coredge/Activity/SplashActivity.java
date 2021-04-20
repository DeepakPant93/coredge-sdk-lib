package com.admin.coredge.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.admin.coredge.MainActivity;
import com.admin.coredge.R;
import com.admin.coredge.Services.MqttHelper;
import com.admin.coredge.Services.UStats;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private String sharedText;
    private Uri imageUri;
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mqttHelper.getClient(this);

        if (UStats.getUsageStatsList(this).isEmpty()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        UStats.printCurrentUsageStatus(SplashActivity.this);

        Handler handler = new Handler();
        final SessionManager sessionmanager = new SessionManager(SplashActivity.this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sessionmanager.getSno() != "") {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if (sharedText != null) {

                        intent.putExtra("username", sessionmanager.getSno());
//                        intent.putExtra("shareMsg", sharedText);
                        // Update UI to reflect text being shared
                    }else {
                        intent.putExtra("username", sessionmanager.getSno());
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1300);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

    }

    void handleSendImage(Intent intent) {
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);



    }
}