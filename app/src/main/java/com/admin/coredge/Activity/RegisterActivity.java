package com.admin.coredge.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.admin.coredge.MainActivity;
import com.admin.coredge.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edit_dob, edit_f_name, edit_l_name, edit_mob_no, edit_email, edit_password;
    private RadioGroup gender;
    private CheckBox agree;
    private ImageButton btn_reg;
    private ProgressBar loading;
    private Calendar current_date;
    private int day, month, year;
    private static  String URL_REG = "http://159.65.144.39:5000/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edit_f_name = findViewById(R.id.input_Fname);
        edit_l_name = findViewById(R.id.input_Lname);
        gender = findViewById(R.id.gender);
        edit_email =  findViewById(R.id.input_email);
        edit_password = findViewById(R.id.input_password);
        btn_reg = findViewById(R.id.btn_signup);
        loading =  findViewById(R.id.loading);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register(){

        final String f_name = this.edit_f_name.getText().toString().trim();
        final String l_name = this.edit_l_name.getText().toString().trim();
        final String gender = ((RadioButton)findViewById(this.gender.getCheckedRadioButtonId())).getText().toString();
        final String term = "1";
        final String email = this.edit_email.getText().toString().trim();
        final String password = this.edit_password.getText().toString().trim();

        //first we will do the validations

        if (TextUtils.isEmpty(f_name)) {
            edit_f_name.setError("Please enter your First Name");
            edit_f_name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(l_name)) {
            edit_l_name.setError("Please enter your Last Name");
            edit_l_name.requestFocus();
            return;
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Enter a valid email");
            edit_email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edit_password.setError("Enter a password");
            edit_password.requestFocus();
            return;
        }

        loading.setVisibility(View.VISIBLE);


        JSONObject jsonObject = new JSONObject();
        try {
            // user_id, comment_id,status
            jsonObject.put("firstName", f_name);
            jsonObject.put("lastName", l_name);
            jsonObject.put("gender", gender);
            jsonObject.put("username", email);
            jsonObject.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL_REG, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            loading.setVisibility(View.GONE);
                            try {
                                String description = response.getString("msg");
                                if (description.equals("Registration successful")) {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle("Registration successful!");
                                    builder.setMessage("please login ");
                                    builder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    builder.show();
                                }else {
                                    Toast.makeText(RegisterActivity.this, description, Toast.LENGTH_SHORT).show();
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



    public void OpenLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
