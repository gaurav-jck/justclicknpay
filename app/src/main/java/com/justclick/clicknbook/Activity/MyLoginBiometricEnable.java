package com.justclick.clicknbook.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.biometric.CryptographyManager;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.LoginRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class MyLoginBiometricEnable extends AppCompatActivity implements View.OnClickListener {
    private final int LOGIN_SERVICE=1;
    private Context context;
    private TextView login_tv;
    private EditText email_edt, password_edt;
    private CheckBox remember_me_checkbox;
    private Dialog otpDialog;
    private boolean otpFlag=false;
    private String TAG = "EnableBiometricLogin";
    private CryptographyManager cryptographyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_biometric);
        context = this;

        initializeViews();

        LoginModel loginModel = new LoginModel();
        if (MyPreferences.getLoginData(loginModel, context) != null) {
            loginModel = MyPreferences.getLoginData(loginModel, context);
        }
        email_edt.setText("");
        password_edt.setText("");

        if (MyPreferences.isUserLogin(context) && loginModel.Data != null) {
            remember_me_checkbox.setChecked(true);
            email_edt.setText(loginModel.Data.Email);
        } else {
            if (loginModel.Data != null) {
                email_edt.setText(loginModel.Data.Email);
            }
        }

    }

    private void initializeViews() {
        email_edt = findViewById(R.id.email_edt);
        password_edt =  findViewById(R.id.password_edt);
        login_tv =  findViewById(R.id.login_tv);

        remember_me_checkbox = findViewById(R.id.remember_me_checkbox);
        findViewById(R.id.scrollView).setOnClickListener(this);
        login_tv.setOnClickListener(this);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.appVerTv)).setText("Ver "+ pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            ((TextView)findViewById(R.id.appVerTv)).setText("Ver 1.7.0+"/*+ BuildConfig.VERSION_NAME*/);
        }

        setFont();

    }

    private void setFont() {
        Typeface face = Common.ButtonTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace1(context);
        Typeface face3 = Common.EditTextTypeFace(context);
        login_tv.setTypeface(face);
        ((TextView) findViewById(R.id.remember_tv)).setTypeface(face2);
        email_edt.setTypeface(face3);
    }

    private void login(String otp) {
        Common.hideSoftKeyboard((MyLoginBiometricEnable)context);
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
                                MyPreferences.logoutUserRemember(context);
                            }
                            MyPreferences.setAppCurrentTime(context);
                            MyPreferences.saveEmailPassword(context,email_edt.getText().toString(), password_edt.getText().toString().trim());

                        }else if(loginModel.StatusCode.equalsIgnoreCase("2")){
                            otpDialog();
                        } else {
                            Toast.makeText(context,loginModel.Status,Toast.LENGTH_SHORT).show();
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
        }
        return true;
    }

    private void otpDialog(){
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
                        login(otp_edt.getText().toString().trim());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_tv:
//                takePdfAndUpload();
                Common.preventFrequentClick(login_tv);
                Common.hideSoftKeyboard((MyLoginBiometricEnable)context);
                if(Common.checkInternetConnection(context)) {
                    login(null);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.close_btn:
                otpDialog.dismiss();
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(otpFlag && otpDialog!=null){
            otpDialog.show();
        }
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