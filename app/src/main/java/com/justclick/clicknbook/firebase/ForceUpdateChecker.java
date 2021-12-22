package com.justclick.clicknbook.firebase;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.justclick.clicknbook.utils.MyPreferences;

public class ForceUpdateChecker {

    private static final String TAG = ForceUpdateChecker.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_CURRENT_VERSION_CODE = "current_version_code";
    public static final String KEY_UPDATE_URL = "force_update_store_url";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, Boolean isForceLogin);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            long currentVersionCode = remoteConfig.getLong(KEY_CURRENT_VERSION_CODE);
            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
            String appVersion = getAppVersion(context);
            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            ForceLoginData data=new ForceLoginData();
            data.versionCode=currentVersionCode;
            data.isForceLogin=true;
            MyPreferences.setForceLoginData(context, data);

//            if (!TextUtils.equals(currentVersion, appVersion)
//                    && onUpdateNeededListener != null) {
//                onUpdateNeededListener.onUpdateNeeded(updateUrl);
//            }
        }
    }

    private String getAppVersion(Context context) {
        String result = "";
        int versionCode;

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    private int getAppVersionCode(Context context) {
        String result = "";
        int versionCode=0;

        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return versionCode;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}