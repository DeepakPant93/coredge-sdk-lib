package com.admin.coredge.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.admin.coredge.MainActivity;
import com.admin.coredge.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static  String LOGIN_URL = "http://159.65.144.39:5000/retrieve";
    private EditText username;
    private EditText password;
    private ImageButton buttonLogin;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.input_email1);
        password = findViewById(R.id.input_password);
        buttonLogin = findViewById(R.id.btn_login);
        loading = findViewById(R.id.loading);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String mEmail = username.getText().toString().trim();
                String mPass = password.getText().toString().trim();


                if(!mEmail.isEmpty() && !mPass.isEmpty()){
                    Login(mEmail, mPass);

                } else {
                    username.setError("Enter email address");
                    password.setError("Enter password");
                }


            }
        });
    }
    private void Login(final String username, final String password) {
        loading.setVisibility(View.VISIBLE);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                LOGIN_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            loading.setVisibility(View.GONE);
                            try {
                                String description = response.getString("status");
                                if (description.equals("200")) {
                                    String username = response.getString("username");

                                    SessionManager sessionmanager = new SessionManager(LoginActivity.this);
                                    sessionmanager.setUsername(username);
                                    sessionmanager.setSno(username);

                                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);


                                } else {
                                    String msg = response.getString("msg");
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }

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
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    public void OpenReg(View view) {
        startActivity(new Intent(this, RegisterActivity.class));

    }

}