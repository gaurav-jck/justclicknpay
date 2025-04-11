package com.justclick.clicknbook.Activity;

/**
 * Created by Lenovo on 04/10/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Dashboard_New_Activity;
import com.justclick.clicknbook.utils.MyPreferences;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3500;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context=this;

        ImageView tv_logo=findViewById(R.id.tv_logo);
        Animation slideAnimation = AnimationUtils.loadAnimation(context, R.anim.splash_logo_animation);
        tv_logo.startAnimation(slideAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(context, MyLoginActivityAeps.class));
                if(MyPreferences.isUserLogin(context)){
                    startActivity(new Intent(context, NavigationDrawerActivity.class).
                            putExtra("SessionValidate",false));
                }else {
                    startActivity(new Intent(context, MyLoginActivityNew.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(context, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(context, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(context, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(context, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "onDestroy", Toast.LENGTH_SHORT).show();
    }*/
}
