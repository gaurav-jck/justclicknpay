package com.justclick.clicknbook.utils;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DecodeLoginToken {

    private static JSONObject decodedJwtToken(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            String strHeader = getJson(split[0]);
            JSONObject jsonHeader = new JSONObject(strHeader); // {"alg":"HS512"}
            String strBody = getJson(split[1]);
            JSONObject jsonBody = new JSONObject(strBody);
            return jsonBody;
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return null;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
    public static String getLoginSalutation(Context context){
        String name="";
        try {
            name=decodedJwtToken(MyPreferences.getLoginToken(context)).getString("title");
        } catch (Exception e) {
            e.printStackTrace();
            name="";
        }
        return name;
    }
    public static String getLoginFirstName(Context context){
        String name="";
        try {
            name=decodedJwtToken(MyPreferences.getLoginToken(context)).getString("firstName");
        } catch (Exception e) {
            e.printStackTrace();
            name="";
        }
        return name;
    }
    public static String getLoginLastName(Context context){
        String name="";
        try {
            name=decodedJwtToken(MyPreferences.getLoginToken(context)).getString("lastName");
        } catch (Exception e) {
            e.printStackTrace();
            name="";
        }
        return name;
    }
    public static int getProfileId(Context context){
        int id=0;
        try {
            id=decodedJwtToken(MyPreferences.getLoginToken(context)).getInt("profileMasterId");
        } catch (Exception e) {
            e.printStackTrace();
            id=0;
        }
        return id;
    }
    public static String getLoginMobileNumber(Context context){
        String name="";
        try {
            name=decodedJwtToken(MyPreferences.getLoginToken(context)).getString("mobile");
        } catch (Exception e) {
            e.printStackTrace();
            name="";
        }
        return name;
    }
    public static String getLoginEmail(Context context){
        String name="";
        try {
            name=decodedJwtToken(MyPreferences.getLoginToken(context)).getString("email");
        } catch (Exception e) {
            e.printStackTrace();
            name="";
        }
        return name;
    }
}
