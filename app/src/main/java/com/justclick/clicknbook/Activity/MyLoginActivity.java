package com.justclick.clicknbook.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.ByteBuffer;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.captcha.TextCaptcha;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.firebase.ForceUpdateChecker;
import com.justclick.clicknbook.model.ForgetPasswordModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.ForgetPasswordRequestModel;
import com.justclick.clicknbook.requestmodels.LoginRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class MyLoginActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, ForceUpdateChecker.OnUpdateNeededListener {
    private final int LOGIN_SERVICE=1, FORGET_PASSWORD_SERVICE=2;
    private Context context;
    private TextView login_tv, create_account_tv, forget_password_tv, reset_pin_tv;
    private EditText email_edt, password_edt, pin_edt, captchaEdt;
    private LinearLayout lin_id_container;
    private ImageView captchaImg;
    private CheckBox remember_me_checkbox;
    private DataBaseHelper dataBaseHelper;
    private LocationManager locationManager;
    private String provider;
    private Dialog forgetDialog, appUpdateDialog,otpDialog;
    private TextCaptcha textCaptcha;
    private boolean otpFlag=false;
    public static FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login2);
        context = this;
        dataBaseHelper = new DataBaseHelper(context);

        initializeViews();
//        FirebaseApp.initializeApp(context);
        initializeFirebase();
        ForceUpdateChecker.with(context).onUpdateNeeded(this).check();

        LoginModel loginModel = new LoginModel();
        if (MyPreferences.getLoginData(loginModel, context) != null) {
            loginModel = MyPreferences.getLoginData(loginModel, context);
        }
        email_edt.setText("");
        password_edt.setText("");
        pin_edt.setText("");

        if (MyPreferences.isUserLogin(context) && loginModel.Data != null) {
            remember_me_checkbox.setChecked(true);
            email_edt.setText(loginModel.Data.Email);
        } else {
            if (loginModel.Data != null) {
                email_edt.setText(loginModel.Data.Email);
            }
        }

    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(context);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, androidx.multidex.BuildConfig.VERSION_NAME);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                ApiConstants.GOOGLE_PLAY_STORE_URL);

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Remote config ", "remote config is fetched.");
                            firebaseRemoteConfig.activate();
                        }
                    }
                });
    }

    private void initializeViews() {
        email_edt = findViewById(R.id.email_edt);
        password_edt =  findViewById(R.id.password_edt);
        pin_edt =  findViewById(R.id.pin_edt);
        login_tv = findViewById(R.id.login_tv);
        forget_password_tv =  findViewById(R.id.forget_password_tv);
        reset_pin_tv =  findViewById(R.id.reset_pin_tv);
        create_account_tv =  findViewById(R.id.create_account_tv);
        lin_id_container =  findViewById(R.id.lin_id_container);

        captchaEdt = findViewById(R.id.captchaEdt);
        captchaImg =  findViewById(R.id.captchaImg);

        textCaptcha = new TextCaptcha(500, 130, 6, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
//        MathCaptcha mathCaptcha = new MathCaptcha(600, 150, MathCaptcha.MathOptions.PLUS_MINUS);

        captchaImg.setImageBitmap(textCaptcha.getImage());

        findViewById(R.id.refreshCaptchaImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textCaptcha = new TextCaptcha(500, 130, 6, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                captchaImg.setImageBitmap(textCaptcha.getImage());
            }
        });

//        imageView1.setImageBitmap(mathCaptcha.getImage());

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.appVerTv)).setText("Ver "+ pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            ((TextView)findViewById(R.id.appVerTv)).setText("Ver 1.5.7+"/*+ BuildConfig.VERSION_NAME*/);
        }
        remember_me_checkbox = findViewById(R.id.remember_me_checkbox);
        findViewById(R.id.scrollView).setOnClickListener(this);
        login_tv.setOnClickListener(this);
        forget_password_tv.setOnClickListener(this);
        reset_pin_tv.setOnClickListener(this);
        create_account_tv.setOnClickListener(this);

        setFont();

    }


    private void getLocation2() {
//        Location nwLocation = new AppLocationService(context)
//                .getLocation(LocationManager.NETWORK_PROVIDER);
        Location nwLocation=null;
        if (nwLocation != null) {
            double latitude = nwLocation.getLatitude();
            double longitude = nwLocation.getLongitude();
            Toast.makeText(
                    getApplicationContext(),
                    "Mobile Location (NW): \nLatitude: " + latitude
                            + "\nLongitude: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            showSettingsAlert("NETWORK");
        }
    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void openUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Setting Dialog Title
//        alertDialog.setTitle(R.string.GPSAlertDialogTitle);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.update_description);

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.update_settings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(ApiConstants.GOOGLE_PLAY_STORE_URL));
                startActivity(browse);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    private int getAppVersionCode(Context context) {
        int versionCode=0;

        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, e.getMessage());
        }

        return versionCode;
    }

    private void getLocation() throws Exception {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
    }

    private void setFont() {
        Typeface face = Common.ButtonTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace1(context);
        Typeface face3 = Common.EditTextTypeFace(context);
        create_account_tv.setTypeface(face);
        login_tv.setTypeface(face);
        ((TextView) findViewById(R.id.remember_tv)).setTypeface(face2);
        forget_password_tv.setTypeface(face2);
        reset_pin_tv.setTypeface(face2);
        email_edt.setTypeface(face3);
        captchaEdt.setTypeface(face2);
    }

    private void login(String otp) {
        Common.hideSoftKeyboard((MyLoginActivity)context);
        String uName= email_edt.getText().toString();
        String uPass= password_edt.getText().toString();
        String DID = Common.getDeviceId(context);

        if(validate(uName,uPass))
        {
            try {
                LoginRequestModel loginRequestModel=new LoginRequestModel();
                loginRequestModel.UserId=EncryptionDecryptionClass.Encryption(uName,context);
                loginRequestModel.Password=EncryptionDecryptionClass.Encryption(uPass, context);
                loginRequestModel.DeviceId=EncryptionDecryptionClass.Encryption(DID, context);
                loginRequestModel.VersionCode=Common.getVersionCode(context);
                if(otp!=null){
                    loginRequestModel.OTP=EncryptionDecryptionClass.Encryption(otp, context);
                }

                showCustomDialog();
                new NetworkCall().callMobileService(loginRequestModel, ApiConstants.LOGIN, context,
                        new NetworkCall.RetrofitResponseListener() {
                            @Override
                            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                                if(response!=null){
                                    responseHandler(response, LOGIN_SERVICE);
                                }else {
                                    hideCustomDialog();
                                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                call_login(loginRequestModel);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case LOGIN_SERVICE:
                    LoginModel loginModel = new Gson().fromJson(response.string(), LoginModel.class);
                    hideCustomDialog();
                    if(loginModel!=null){
                        if(loginModel.Data!=null && loginModel.StatusCode.equalsIgnoreCase("0")){
                            //store values to shared preferences
//                            dataBaseHelper.insertLoginIds(email_edt.getText().toString());
                            MyPreferences.saveLoginData(loginModel,context);
                            if(remember_me_checkbox.isChecked()){
                                MyPreferences.loginUser(context, email_edt.getText().toString(), password_edt.getText().toString().trim());
                            }else {
                                MyPreferences.logoutUser(context);
                            }
                            MyPreferences.setAppCurrentTime(context);
                            MyPreferences.saveEmailPassword(context,email_edt.getText().toString(), password_edt.getText().toString().trim());
                            Intent in = new Intent(context,NavigationDrawerActivity.class);
                            startActivity(in);
                            finish();
                        }else if(loginModel.StatusCode.equalsIgnoreCase("2")){
                            otpDialog(LOGIN_SERVICE);
                        } else {
//                            Toast.makeText(context,loginModel.Status,Toast.LENGTH_SHORT).show();
                            errorPopup(loginModel.Status);
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_SHORT).show();
                    }
                    break;

                case FORGET_PASSWORD_SERVICE:
                    ForgetPasswordModel forgetPasswordModel = new Gson().fromJson(response.string(), ForgetPasswordModel.class);
                    hideCustomDialog();
                    if(forgetPasswordModel!=null){
                        if(forgetPasswordModel.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context, forgetPasswordModel.Data.Message, Toast.LENGTH_SHORT).show();
                            forgetDialog.dismiss();
                            otpDialog(FORGET_PASSWORD_SERVICE);
                        }else {
                            Toast.makeText(context, forgetPasswordModel.Status, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void errorPopup(String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //set title for alert dialog
        builder.setTitle("Response message");
        //set message for alert dialog
        builder.setMessage(status);
//        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // Create the AlertDialog
        AlertDialog alertDialog=null;

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        email_edt.setText("");
                        password_edt.setText("");
                        dialog.cancel();
                    }
                });
        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.login_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

    private boolean validate(String uName, String uPass) {

        if(!Common.isEmailValid(uName) && !Common.isMobileValid(uName)){
            Toast.makeText(context,R.string.empty_and_invalid_userId,Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(uPass)) {
            Toast.makeText(context,"Please enter Password",Toast.LENGTH_SHORT).show();
            return false;
        }/*else if (!textCaptcha.checkAnswer(captchaEdt.getText().toString().trim())) {
            Toast.makeText(context,"Captcha is not matched",Toast.LENGTH_SHORT).show();
//            captchaEdt.setError("Captcha is not match");
            return false;
//            numberOfCaptchaFalse++;
        }else if (textCaptcha.checkAnswer(captchaEdt.getText().toString().trim())) {
//            captchaEdt.setError("Captcha is match");
//            numberOfCaptchaFalse++;
        }*/
        return true;
    }

    private void forgetPassword(){
        forgetDialog = new Dialog(context);
        forgetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgetDialog.setContentView(R.layout.forget_password_layout);
        final EditText email= (EditText) forgetDialog.findViewById(R.id.email_edt);
        Button submit= (Button) forgetDialog.findViewById(R.id.submit_btn);
        ImageButton dialogCloseButton = (ImageButton) forgetDialog.findViewById(R.id.close_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.checkInternetConnection(context)){
                    if(Common.isEmailValid(email.getText().toString().trim())) {
                        ForgetPasswordRequestModel requestModel=new ForgetPasswordRequestModel();
                        requestModel.Email=email.getText().toString().trim();
                        requestModel.MerchantID=ApiConstants.MerchantId;

                        showCustomDialog();
                        new NetworkCall().callMobileService(requestModel,ApiConstants.FORGETPASSWORD, context, new NetworkCall.RetrofitResponseListener() {
                            @Override
                            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                                if(response!=null){
                                    responseHandler(response, FORGET_PASSWORD_SERVICE);
                                }else {
                                    hideCustomDialog();
                                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(context,R.string.empty_and_invalid_email,Toast.LENGTH_LONG).show();
                    }
//                    forgetDialog.dismiss();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                forgetDialog.dismiss();
            }
        });
        forgetDialog.show();
    }

    private void otpDialog(final int TYPE){
        otpDialog = new Dialog(context);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.send_otp_layout);
        otpDialog.setCancelable(false);
        final EditText otp_edt= (EditText) otpDialog.findViewById(R.id.otp_edt);
        final Button submit= (Button) otpDialog.findViewById(R.id.submit_btn);
        ImageButton dialogCloseButton = (ImageButton) otpDialog.findViewById(R.id.close_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(otpDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (NullPointerException e){

                }

                if(Common.checkInternetConnection(context)){
                    if(otp_edt.getText().toString().trim().length()>3) {
                        if(TYPE==LOGIN_SERVICE){
                            login(otp_edt.getText().toString().trim());
                        }else {
                            forgetOtp(otp_edt.getText().toString().trim());
                        }

                    }else {
                        Toast.makeText(context,R.string.empty_and_invalid_otp,Toast.LENGTH_LONG).show();
                    }
//                    forgetDialog.dismiss();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                otpDialog.dismiss();
            }
        });
        otpDialog.show();
    }

    private void forgetOtp(String otp) {
        Common.hideSoftKeyboard((MyLoginActivity)context);
        String uName= email_edt.getText().toString();
        String uPass= password_edt.getText().toString();
        String DID = Common.getDeviceId(context);

        if(validate(uName,uPass))
        {
            try {
                LoginRequestModel loginRequestModel=new LoginRequestModel();
                loginRequestModel.UserId=EncryptionDecryptionClass.Encryption(uName,context);
                loginRequestModel.Password=EncryptionDecryptionClass.Encryption(uPass, context);
                loginRequestModel.DeviceId=EncryptionDecryptionClass.Encryption(DID, context);
                loginRequestModel.VersionCode=Common.getVersionCode(context);
                if(otp!=null){
                    loginRequestModel.OTP=EncryptionDecryptionClass.Encryption(otp, context);
                }

                showCustomDialog();
                new NetworkCall().callMobileService(loginRequestModel,ApiConstants.LOGIN, context,
                        new NetworkCall.RetrofitResponseListener() {
                            @Override
                            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                                if(response!=null){
                                    responseHandler(response, LOGIN_SERVICE);
                                }else {
                                    hideCustomDialog();
                                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                call_login(loginRequestModel);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void resetPin() {
        final Dialog forgetDialog = new Dialog(context);
        forgetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgetDialog.setContentView(R.layout.reset_pin_layout);
        final EditText currentPinEdt= (EditText) forgetDialog.findViewById(R.id.currentPinEdt);
        final EditText newPinEdt= (EditText) forgetDialog.findViewById(R.id.newPinEdt);
        final EditText confirmPinEdt= (EditText) forgetDialog.findViewById(R.id.confirmPinEdt);
        final Button submit= (Button) forgetDialog.findViewById(R.id.submit_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(forgetDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (NullPointerException e){

                }

                if(Common.checkInternetConnection(context)){
                    if(currentPinEdt.getText().toString().trim().length()>3 &&
                            newPinEdt.getText().toString().trim().length()>3 &&
                            confirmPinEdt.getText().toString().trim().length()>3 &&
                            newPinEdt.getText().toString().trim().equals(confirmPinEdt.getText().toString().trim())) {
                        Toast.makeText(context, "Reset TPin", Toast.LENGTH_SHORT).show();
                        forgetDialog.dismiss();
                    }else {
                        Toast.makeText(context,"Details are not valid",Toast.LENGTH_LONG).show();
                    }
//                    forgetDialog.dismiss();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });

        forgetDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_tv:
//                takePdfAndUpload();
                Common.preventFrequentClick(login_tv);
                Common.hideSoftKeyboard((MyLoginActivity)context);
                if(Common.checkInternetConnection(context)) {
                    login(null);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.create_account_tv:
                Common.preventFrequentClick(create_account_tv);
                if(Common.checkInternetConnection(context)) {
                    startActivity(new Intent(context, RegistrationActivity.class));
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.forget_password_tv:
                if(Common.checkInternetConnection(context)) {
                    forgetPassword();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset_pin_tv:
                if(Common.checkInternetConnection(context)) {
                    resetPin();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scrollView:
                lin_id_container.setVisibility(View.GONE);
                Common.hideSoftKeyboard(MyLoginActivity.this);
//                resetPin();
//                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                break;
            case R.id.close_btn:
                otpDialog.dismiss();
                break;

        }
    }

    private void takePdfAndUpload() {
        chooseFile();
//        convertFileToByteArray(new File(""));
    }
    private void chooseFile() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            try {
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri=data.getData();
        String url=data.getDataString();
        File pdfDir = new File(data.getData().getPath());
        try {
            String base64=convertFileToByteArray(pdfDir);
//            filePath = mSaveBit.getPath()
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(pdfDir.getAbsolutePath(),bmOptions);

           /* Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();


            bitmap = BitmapFactory.decodeFile(filePath);*/
//            iv_image.setImageBitmap(bitmap);


//            bitmap = Bitmap.createScaledBitmap(bitmap,captchaImg.getWidth(),captchaImg.getHeight(),true);
//            mImageView.setImageBitmap(bitmap);

//            Bitmap bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            File file = new File("/storage/extSdCard/Test.pdf");
            FileInputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            ByteBuffer buffer = new ByteBuffer();
            buffer.append(bytes);
//            String data = Base64.encodeToString(bytes, Base64.DEFAULT);
//            PdfReader pdf_file = new PdfReader(buffer);
//            PdfPage page = pdf_file.getPage(2, true);

//            RectF rect = new RectF(0, 0, (int) page.getBBox().width(),
//                    (int) page.getBBox().height());
//
//            Bitmap image = page.getImage((int)rect.width(), (int)rect.height(), rect);

            /*if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                byte[] img = PdfReader.getStreamBytesRaw((PRStream) stream);
                bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            }*/

            captchaImg.setImageBitmap(bitmap);
            Toast.makeText(context, base64.length()+"", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    public static String convertFileToByteArray(File f) throws Exception{
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();

            Log.e("Byte array", ">" + byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }


    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(context, location.getLatitude() + "," + location.getLongitude(),
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getAppVersionCode(context)<MyPreferences.getForceLoginData(context).versionCode){
            onUpdateNeeded(ApiConstants.GOOGLE_PLAY_STORE_URL, MyPreferences.getForceLoginData(context).isForceLogin);
        }
        if(otpFlag && otpDialog!=null){
            otpDialog.show();
        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl, Boolean isForceLogin) {
        appUpdateDialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reporting.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        appUpdateDialog.setCancelable(false);
        appUpdateDialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(otpDialog != null && otpDialog.isShowing()){
            otpDialog.dismiss();
            otpFlag=true;
        }else {
            otpFlag=false;
        }
    }
}