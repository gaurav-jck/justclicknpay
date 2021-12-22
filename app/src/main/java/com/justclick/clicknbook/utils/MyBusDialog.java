package com.justclick.clicknbook.utils;

import android.animation.ValueAnimator;
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

public class MyBusDialog {
    private static Dialog dialog;
    public static ImageView cloud1,cloud2,cloud22,cloud3,cloud4,cloud44,cloud5,cloud55,plane;
    public static LinearLayout cloudThreeLin;
    public static LinearLayout cloudThreeLin1;
    private static Animation animLeft;


    public static void showCustomDialog(Context context){
        try {
            dialog = new Dialog(context, R.style.Theme_Design_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bus_dialog);
            final Window window= dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            cloud1=((ImageView) dialog.findViewById(R.id.cloud1));
            cloud2=((ImageView) dialog.findViewById(R.id.cloud2));
            cloudThreeLin1=((LinearLayout) dialog.findViewById(R.id.cloudThreeLin1));
            cloudThreeLin=((LinearLayout) dialog.findViewById(R.id.cloudThreeLin));
            cloud22=((ImageView) dialog.findViewById(R.id.cloud22));
            cloud3=((ImageView) dialog.findViewById(R.id.cloud3));
            cloud4=((ImageView) dialog.findViewById(R.id.cloud4));
            cloud44=((ImageView) dialog.findViewById(R.id.cloud44));
            cloud55=((ImageView) dialog.findViewById(R.id.cloud55));
            cloud5=((ImageView) dialog.findViewById(R.id.cloud5));
            plane=((ImageView) dialog.findViewById(R.id.plane));

            final ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(5000L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float progress = (float) animation.getAnimatedValue();
                    final float width = cloud2.getWidth();
                    final float translationX = width * progress;
                    cloud2.setTranslationX(translationX);
                    cloud22.setTranslationX(translationX - width);
                }
            });
            animator.start();

            final ValueAnimator animator1 = ValueAnimator.ofFloat(1.0f, 0.0f);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(5000L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float progress = (float) animation.getAnimatedValue();
                    final float width = cloudThreeLin.getWidth();
                    final float translationX = width * progress;
                    cloudThreeLin.setTranslationX(translationX);
                    cloudThreeLin1.setTranslationX(translationX - width);
                }
            });
            animator1.start();


            animLeft = AnimationUtils.loadAnimation(context,R.anim.slide);
            animLeft.setRepeatCount(Animation.INFINITE);
            animLeft.setRepeatMode(Animation.INFINITE);
            animLeft.setFillBefore(true);
            animLeft.setFillAfter(true);

//            cloud1.startAnimation(animLeft);
//          cloud2.startAnimation(animLeft);
//            cloud3.startAnimation(animLeft);
//            cloud4.startAnimation(animLeft);
//            cloud5.startAnimation(animLeft);
//          plane.startAnimation(animLeft);

            Animation mAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0.009f);
            mAnimation.setDuration(500);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new LinearInterpolator());
//            plane.setAnimation(mAnimation);
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
