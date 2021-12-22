package com.justclick.clicknbook;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.justclick.clicknbook.firebase.ForceUpdateChecker;

import java.util.HashMap;
import java.util.Map;

//import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by gaurav.singhal on 9/8/2017.
 */

public class MyJckApplication extends MultiDexApplication {
    private static final String TAG = MyJckApplication.class.getSimpleName();
    public static MyJckApplication instance;
    private RequestQueue mRequestQueue;
    public static FirebaseRemoteConfig firebaseRemoteConfig;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        Fresco.initialize(this);
       // FinoApplication.init(this);
        FirebaseApp.initializeApp(MyJckApplication.this);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, BuildConfig.VERSION_NAME);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                ApiConstants.GOOGLE_PLAY_STORE_URL);

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activate();
                        }
                    }
                });

        /*firebaseRemoteConfig.fetch(60)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyApplication.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MyApplication.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });
*/
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyJckApplication getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void     addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }

}