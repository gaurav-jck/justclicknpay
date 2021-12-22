package com.justclick.clicknbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.justclick.clicknbook.R;

public class MyflightDialog {
    private static Dialog dialog;
    public static ImageView cloud1,cloud2,cloud3,cloud4,cloud5,plane;
    private static Animation animLeft;


    public static void showCustomDialog(Context context){
        try {
            dialog = new Dialog(context, R.style.Theme_Design_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.air_dialog);
            final Window window= dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            cloud1=((ImageView) dialog.findViewById(R.id.cloud1));
            cloud2=((ImageView) dialog.findViewById(R.id.cloud2));
            cloud3=((ImageView) dialog.findViewById(R.id.cloud3));
            cloud4=((ImageView) dialog.findViewById(R.id.cloud4));
            cloud5=((ImageView) dialog.findViewById(R.id.cloud5));
            plane=((ImageView) dialog.findViewById(R.id.plane));

            animLeft = AnimationUtils.loadAnimation(context,R.anim.slide);
            animLeft.setRepeatCount(Animation.INFINITE);
            animLeft.setRepeatMode(Animation.INFINITE);
            animLeft.setFillBefore(true);
            animLeft.setFillAfter(true);

            cloud1.startAnimation(animLeft);
            cloud2.startAnimation(animLeft);
            cloud3.startAnimation(animLeft);
            cloud4.startAnimation(animLeft);
            cloud5.startAnimation(animLeft);
            plane.startAnimation(animLeft);

            Animation mAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0.009f);
            mAnimation.setDuration(500);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new LinearInterpolator());
            plane.setAnimation(mAnimation);
//            plane.setImageResource(R.drawable.clouds);

            dialog.setCancelable(false);
            dialog.show();
        }catch (Exception e){
        }
    }
    public static void hideCustomDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public static Boolean isDialogShowing(){
        if(dialog!=null && dialog.isShowing())
            return true;
        return false;
    }
}
