package com.justclick.clicknbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;


public class InternetConnectionOffDialog {
        private Dialog dialog;
        public TextView okayTv;
        private Context context;
        private static InternetConnectionOffDialog internetConnectionOffDialog;

        public static InternetConnectionOffDialog getInstance(){
            if(internetConnectionOffDialog==null){
                internetConnectionOffDialog=new InternetConnectionOffDialog();
            }
            return internetConnectionOffDialog;
        }

        public  void showCustomDialog(Context context){
            try {
                this.context=context;
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.internet_check_dialog);
                final Window window= dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                okayTv= dialog.findViewById(R.id.okayTv);
                okayTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
               dialog.setCancelable(false);
                dialog.show();
            }catch (Exception e){
            }
        }
    public  void hideCustomDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        }

    }
