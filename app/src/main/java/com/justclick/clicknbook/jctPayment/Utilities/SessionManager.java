package com.justclick.clicknbook.jctPayment.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.justclick.clicknbook.jctPayment.Models.UserInfo;

public class SessionManager {
    //the constants
    private static final String SHARED_PREF_NAME = "jctsharedpref";
    private static final String KEY_USERID = "keyuserid";
    private static final String KEY_USERTYPE = "keyusertype";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_AUTH_TOKEN = "authkey";


    private static SessionManager mInstance;
    private static Context mCtx;

    private SessionManager(Context context) {
        mCtx = context;
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    public static void setAuthToken(String token){
        SharedPreferences.Editor editor = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(UserInfo user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERID, user.getId());
        editor.putString(KEY_USERTYPE, user.getType());
        editor.putString(KEY_USERNAME, user.getName());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERID, null) != null;
    }

    //this method will give the logged in user
    public UserInfo getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserInfo(
                sharedPreferences.getString(KEY_USERID, null),
                sharedPreferences.getString(KEY_USERTYPE, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString("iat", null),
                sharedPreferences.getString("exp", null),
                sharedPreferences.getString("iss", null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
//        MyPreferences.logoutUser(mCtx);
        //mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
//        Intent i = new Intent(mCtx, NavigationDrawerActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mCtx.startActivity(i);
    }

}
