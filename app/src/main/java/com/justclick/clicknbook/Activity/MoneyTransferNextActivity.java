package com.justclick.clicknbook.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.rblDmt.MoneyTransferNextHomeFragment;
import com.justclick.clicknbook.Fragment.rblDmt.MoneyTransferRefundFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblCommonResponse;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.CommonRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class MoneyTransferNextActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int APP_SESSION = 1;
    private Context context;
    private LinearLayout lin_home,lin_refund,lin_transcation,lin_exit,lin_logout;
    private Fragment moneyTransferNextHomeFragment, moneyTransferRefundFragment;
    private ImageView img_home,img_refund,img_exit,img_logout,back_arrow, img_transaction;
    private TextView home_tv,refund_tv,transactionInquiry_tv,exit_tv,logout_tv, agentName;
    private RblGetSenderResponse rblGetSenderResponse;
    private LoginModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_next_money_transfer);
        context = this;
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        rblGetSenderResponse= (RblGetSenderResponse)getIntent().getSerializableExtra("SenderDetails");
        initializeViews();
    }
    public void replaceFragmentWithBackStack(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        if(!fragment.isVisible()) {
            fm.beginTransaction().replace(R.id.container, fragment, fragment.getTag()).addToBackStack(null).commit();
        }
    }
    private void initializeViews() {
        moneyTransferNextHomeFragment=new MoneyTransferNextHomeFragment();
        moneyTransferRefundFragment = new MoneyTransferRefundFragment();
        lin_home= findViewById(R.id.lin_home);
        lin_refund=  findViewById(R.id.lin_refund);
        lin_transcation=  findViewById(R.id.lin_transcation);
        lin_exit=  findViewById(R.id.lin_exit);
        lin_logout=  findViewById(R.id.lin_logout);

        img_home=  findViewById(R.id.img_home);
        img_refund=  findViewById(R.id.img_refund);
        img_transaction =  findViewById(R.id.img_transaction);
        img_exit=  findViewById(R.id.img_exit);
        img_logout=  findViewById(R.id.img_logout);
        back_arrow=  findViewById(R.id.back_arrow);

        home_tv=  findViewById(R.id.home_tv);
        refund_tv=  findViewById(R.id.refund_tv);
        transactionInquiry_tv=  findViewById(R.id.transactionInquiry_tv);
        exit_tv=  findViewById(R.id.exit_tv);
        logout_tv=  findViewById(R.id.logout_tv);
        agentName=  findViewById(R.id.agentName);


        ColorFilter white = new LightingColorFilter( getResources().getColor(R.color.color_white),
                getResources().getColor(R.color.color_white) );
        Drawable homeIcon = getResources().getDrawable( R.drawable.money_home );
        homeIcon.setColorFilter(white);
        img_home.setImageDrawable(homeIcon);
        home_tv.setTextColor(getResources().getColor(R.color.color_white));
        ColorFilter blue = new LightingColorFilter( getResources().getColor(R.color.color_white),
                getResources().getColor(R.color.app_blue_color) );
        Drawable refund = ContextCompat.getDrawable(context, R.drawable.refund);
        Drawable trantnEn = ContextCompat.getDrawable(context, R.drawable.money_transaction);
        Drawable exit = ContextCompat.getDrawable(context, R.drawable.money_exit);
        Drawable logout = ContextCompat.getDrawable(context, R.drawable.money_logout);
        refund.setColorFilter(blue);
        trantnEn.setColorFilter(blue);
        exit.setColorFilter(blue);
        logout.setColorFilter(blue);
        img_transaction.setImageDrawable(trantnEn);
        img_refund.setImageDrawable(refund);
        img_exit.setImageDrawable(exit);
        img_logout.setImageDrawable(logout);

        lin_home.setOnClickListener(this);
        lin_refund.setOnClickListener(this);
        lin_transcation.setOnClickListener(this);
        lin_exit.setOnClickListener(this);
        lin_logout.setOnClickListener(this);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        agentName.setText(loginModel.Data.AgencyName+"\n( "+loginModel.Data.DoneCardUser+" )");

        Bundle bundle=new Bundle();
        bundle.putSerializable("SenderDetails",rblGetSenderResponse);
        moneyTransferNextHomeFragment.setArguments(bundle);
        moneyTransferRefundFragment.setArguments(bundle);
        replaceFragment(moneyTransferNextHomeFragment);
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container,fragment,fragment.getTag()).commit();
    }
    @Override
    public void onClick(View v) {
        ColorFilter white = new LightingColorFilter( getResources().getColor(R.color.color_white),
                getResources().getColor(R.color.color_white) );

        ColorFilter blue = new LightingColorFilter( getResources().getColor(R.color.app_blue_color),
                getResources().getColor(R.color.app_blue_color) );


        Drawable home = ContextCompat.getDrawable(context, R.drawable.money_home);
        Drawable refund = ContextCompat.getDrawable(context, R.drawable.refund);
        Drawable transaction = ContextCompat.getDrawable(context, R.drawable.money_transaction);
        Drawable exit = ContextCompat.getDrawable(context, R.drawable.money_exit);
        Drawable logout = ContextCompat.getDrawable(context, R.drawable.money_logout);

        switch (v.getId()) {
            case R.id.lin_home:
                replaceFragment(moneyTransferNextHomeFragment);
                setDefaultColors(blue,home,refund,transaction, exit,logout);
                home.setColorFilter(white);
                img_home.setImageDrawable(home);
                home_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_home.setBackgroundResource(R.color.app_blue_color);
                break;
            case R.id.lin_refund:
                replaceFragment(moneyTransferRefundFragment);
                setDefaultColors(blue,home,refund,transaction, exit,logout);
                refund.setColorFilter(white);
                img_refund.setImageDrawable(refund);
                refund_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_refund.setBackgroundResource(R.color.app_blue_color);
                break;
            case R.id.lin_transcation:
                replaceFragment(moneyTransferRefundFragment);
                setDefaultColors(blue,home,refund,transaction, exit,logout);
                transaction.setColorFilter(white);
                img_transaction.setImageDrawable(transaction);
                transactionInquiry_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_transcation.setBackgroundResource(R.color.app_blue_color);
                break;
            case R.id.lin_exit:
                setDefaultColors(blue,home,refund,transaction, exit,logout);
                exit.setColorFilter(white);
                img_exit.setImageDrawable(exit);
                exit_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_exit.setBackgroundResource(R.color.app_blue_color);
                 finish();
                break;
            case R.id.lin_logout:
                setDefaultColors(blue,home,refund,transaction, exit,logout);
                logout.setColorFilter(white);
                img_logout.setImageDrawable(logout);
                logout_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_logout.setBackgroundResource(R.color.app_blue_color);
                 finish();
                break;

        }
    }

    private void setDefaultColors(ColorFilter blue, Drawable home, Drawable refund, Drawable transaction, Drawable exit, Drawable logout) {

        home.setColorFilter(blue);
        img_home.setImageDrawable(home);
        home_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        refund.setColorFilter(blue);
        img_refund.setImageDrawable(refund);
        refund_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        transaction.setColorFilter(blue);
        img_transaction.setImageDrawable(transaction);
        transactionInquiry_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        exit.setColorFilter(blue);
        img_exit.setImageDrawable(exit);
        exit_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        logout.setColorFilter(blue);
        img_logout.setImageDrawable(logout);
        logout_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        lin_home.setBackgroundResource(R.color.color_white);
        lin_refund.setBackgroundResource(R.color.color_white);
        lin_transcation.setBackgroundResource(R.color.color_white);
        lin_exit.setBackgroundResource(R.color.color_white);
        lin_logout.setBackgroundResource(R.color.color_white);
    }

    private void appSession() {
        CommonRequestModel requestModel=new CommonRequestModel();
        requestModel.DeviceId= Common.getDeviceId(context);
        requestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        requestModel.VersionCode=Common.getVersionCode(context);

        new NetworkCall().callRblLongTimeService(requestModel, ApiConstants.AppSession, context,
                new NetworkCall.RetrofitResponseListener() {
            @Override
            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                if(response!=null){
                    responseHandler(response, APP_SESSION);
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }
        },false);
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case APP_SESSION:
                    RblCommonResponse rblCommonResponse = new Gson().fromJson(response.string(), RblCommonResponse.class);
                    if(rblCommonResponse!=null && rblCommonResponse.status == 0) {
                        MyPreferences.logoutUser(context);
                        Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(context, R.string.appSession, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.checkInternetConnection(context))
            appSession();
    }


}