package com.admin.coredge.Activity;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public Context context;


    public void remove(){
        sharedPreferences.edit().clear().commit();
    }

    public String getUsername() {
        username = sharedPreferences.getString("username", "");
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        sharedPreferences.edit().putString("username", username).commit();
    }

    public String getSno() {
        sno = sharedPreferences.getString("sno", "");
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
        sharedPreferences.edit().putString("sno", sno).commit();
    }

    private String sno;
    private String username;
    private String category_name;
    private String category_id;

//    public String getCategory_name() {
//        category_name = sharedPreferences.getString("category_name", "");
//        return category_name;
//    }

//    public void setCategory_name(String category_name) {
//        this.category_name = category_name;
//        sharedPreferences.edit().putString("category_name", category_name).commit();
//    }
//
//    public String getCategory_image() {
//        category_image = sharedPreferences.getString("category_image", "");
//        return category_image;
//    }

//    public void setCategory_image(String category_image) {
//        this.category_image = category_image;
//        sharedPreferences.edit().putString("category_image", category_image).commit();
//    }

//    private String category_image;
    SharedPreferences sharedPreferences;


    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }
}
