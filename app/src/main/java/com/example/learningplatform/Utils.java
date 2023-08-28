package com.example.learningplatform;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String host = "http://192.168.8.109:3000";

    public void setUserSession(Context context, JSONObject obj){
        try{
            SharedPreferences.Editor sh = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
            sh.putString("id",obj.getString("id"));
            sh.putString("name",obj.getString("name"));
            sh.putString("email",obj.getString("email"));
            sh.putString("user_type",obj.getString("user_type"));
            sh.apply();

        }catch (JSONException ex){
            Log.d("JSONErr",ex.getMessage());
        }
    }
    public JSONObject getUserSession(Context context){
        JSONObject userInfo = new JSONObject();
        try{
            SharedPreferences sh = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
            userInfo.put("id",sh.getString("id",""));
            userInfo.put("email",sh.getString("email",""));
            userInfo.put("user_type",sh.getString("user_type",""));
        }catch (JSONException ex){
            Log.d("JSONErr",ex.getMessage());
        }
        return userInfo;
    }
    public static void logout(Context context){
            SharedPreferences.Editor sh = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
            sh.clear().apply();

    }
    public static String getUser(Context context,String fieldName){
        SharedPreferences sh = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        return sh.getString(fieldName,"");
    }
    public static String dateFormat(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return dateFormat.format(date);
    }
}
