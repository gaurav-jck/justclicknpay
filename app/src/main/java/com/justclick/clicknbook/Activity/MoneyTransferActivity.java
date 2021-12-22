package com.justclick.clicknbook.Activity;


import android.content.Context;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.justclick.clicknbook.Fragment.rblDmt.MoneyTransferHomeFragment;
import com.justclick.clicknbook.Fragment.rblDmt.MoneyTransferRegistrationFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;


public class MoneyTransferActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private LinearLayout lin_home, lin_registration, lin_exit, lin_logout;
    private Fragment moneyTransferHomeFragment, moneyTransferRegistrationFragment;
    private ImageView img_home, img_registration, img_exit, img_logout, back_arrow;
    private TextView home_tv,registration_tv,exit_tv,logout_tv, agentName;
    private LoginModel loginModel;
    private ColorFilter white, blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_money_transfer);
        context = this;
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        initializeViews();
    }

    public void replaceFragmentWithBackStack(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        if(!fragment.isVisible()) {
            fm.beginTransaction().replace(R.id.container, fragment, fragment.getTag()).addToBackStack(null).commit();
        }
    }

    private void initializeViews() {
        moneyTransferHomeFragment=new MoneyTransferHomeFragment();
        moneyTransferRegistrationFragment= new MoneyTransferRegistrationFragment();
        lin_home=  findViewById(R.id.lin_home);
        lin_registration=  findViewById(R.id.lin_registration);
        lin_exit=  findViewById(R.id.lin_exit);
        lin_logout=  findViewById(R.id.lin_logout);

        img_home=  findViewById(R.id.img_home);
        img_registration=  findViewById(R.id.img_registration);
        img_exit=  findViewById(R.id.img_exit);
        img_logout=  findViewById(R.id.img_logout);
        back_arrow=  findViewById(R.id.back_arrow);

        home_tv=  findViewById(R.id.home_tv);
        registration_tv=  findViewById(R.id.registration_tv);
        exit_tv=  findViewById(R.id.exit_tv);
        logout_tv=  findViewById(R.id.logout_tv);
        agentName=  findViewById(R.id.agentName);

        white = new LightingColorFilter( getResources().getColor(R.color.color_white),
                getResources().getColor(R.color.color_white) );
        blue = new LightingColorFilter( getResources().getColor(R.color.color_white),
                getResources().getColor(R.color.app_blue_color));

        Drawable homeIcon = ContextCompat.getDrawable(context, R.drawable.money_home);
        Drawable register = ContextCompat.getDrawable(context, R.drawable.money_register);
        Drawable exit = ContextCompat.getDrawable(context, R.drawable.money_exit);
        Drawable logout = ContextCompat.getDrawable(context, R.drawable.money_logout);

        setDefaultColors(blue,homeIcon,register, exit,logout);
        homeIcon.setColorFilter(white);
        img_home.setImageDrawable(homeIcon);
        home_tv.setTextColor(getResources().getColor(R.color.color_white));
        lin_home.setBackgroundResource(R.color.app_blue_color);

        lin_home.setOnClickListener(this);
        lin_registration.setOnClickListener(this);
        lin_exit.setOnClickListener(this);
        lin_logout.setOnClickListener(this);

        agentName.setText(loginModel.Data.AgencyName+"\n( "+loginModel.Data.DoneCardUser+" )");

        Typeface face = Common.TitleTypeFace(context);
        home_tv.setTypeface(face);

        Typeface face1 = Common.TitleTypeFace(context);
        registration_tv.setTypeface(face1);

        Typeface face2 = Common.TitleTypeFace(context);
        logout_tv.setTypeface(face2);

        Typeface face3 = Common.TitleTypeFace(context);
        exit_tv.setTypeface(face3);


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        replaceFragment(moneyTransferHomeFragment);
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container,fragment,fragment.getTag()).commit();
    }
    @Override
    public void onClick(View v) {

        Drawable homeIcon = ContextCompat.getDrawable(context, R.drawable.money_home);
        Drawable registration = ContextCompat.getDrawable(context, R.drawable.money_register);
        Drawable exit = ContextCompat.getDrawable(context, R.drawable.money_exit);
        Drawable logout = ContextCompat.getDrawable(context, R.drawable.money_logout);

        switch (v.getId()) {

            case R.id.lin_home:
                replaceFragment(moneyTransferHomeFragment);
                setDefaultColors(blue,homeIcon,registration, exit,logout);
                homeIcon.setColorFilter(white);
                img_home.setImageDrawable(homeIcon);
                home_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_home.setBackgroundResource(R.color.app_blue_color);
                break;
            case R.id.lin_registration:

                replaceFragment(moneyTransferRegistrationFragment);
                setDefaultColors(blue,homeIcon,registration, exit,logout);
                registration.setColorFilter(white);
                img_registration.setImageDrawable(registration);
                registration_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_registration.setBackgroundResource(R.color.app_blue_color);

                break;
            case R.id.lin_exit:
                setDefaultColors(blue,homeIcon,registration, exit,logout);
                exit.setColorFilter(white);
                img_exit.setImageDrawable(exit);
                exit_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_exit.setBackgroundResource(R.color.app_blue_color);
//                replaceFragment(moneyTransferHomeFragment);
                finish();
                break;
            case R.id.lin_logout:
                setDefaultColors(blue,homeIcon,registration, exit,logout);
                logout.setColorFilter(white);
                img_logout.setImageDrawable(logout);
                logout_tv.setTextColor(getResources().getColor(R.color.color_white));
                lin_logout.setBackgroundResource(R.color.app_blue_color);
//                replaceFragment(moneyTransferHomeFragment);
               finish();
                break;

        }
    }

    public void setRegistrationTab(){
        Drawable homeIcon = ContextCompat.getDrawable(context, R.drawable.money_home);
        Drawable registration = ContextCompat.getDrawable(context, R.drawable.money_register);
        Drawable exit = ContextCompat.getDrawable(context, R.drawable.money_exit);
        Drawable logout = ContextCompat.getDrawable(context, R.drawable.money_logout);
        setDefaultColors(blue,homeIcon,registration, exit,logout);
        registration.setColorFilter(white);
        img_registration.setImageDrawable(registration);
        registration_tv.setTextColor(getResources().getColor(R.color.color_white));
        lin_registration.setBackgroundResource(R.color.app_blue_color);
    }

    private void setDefaultColors(ColorFilter blue, Drawable homeIcon, Drawable registration, Drawable exit, Drawable logout) {

        homeIcon.setColorFilter(blue);
        img_home.setImageDrawable(homeIcon);
        home_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        registration.setColorFilter(blue);
        img_registration.setImageDrawable(registration);
        registration_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        exit.setColorFilter(blue);
        img_exit.setImageDrawable(exit);
        exit_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        logout.setColorFilter(blue);
        img_logout.setImageDrawable(logout);
        logout_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

        lin_home.setBackgroundResource(R.color.color_white);
        lin_registration.setBackgroundResource(R.color.color_white);
        lin_exit.setBackgroundResource(R.color.color_white);
        lin_logout.setBackgroundResource(R.color.color_white);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lin_home.performClick();
    }
}