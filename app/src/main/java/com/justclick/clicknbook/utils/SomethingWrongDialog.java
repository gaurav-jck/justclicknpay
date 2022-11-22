package com.justclick.clicknbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;


public class SomethingWrongDialog {
  private  Dialog dialog;
  public  TextView tvDone;
  private  Context contextMain;
  private static SomethingWrongDialog noResultFoundDialog;

  public static SomethingWrongDialog getInstance(){
    if(noResultFoundDialog==null){
      noResultFoundDialog=new SomethingWrongDialog();
    }
    return noResultFoundDialog;
  }

  public  void showCustomDialog(Context context){
    try {
      contextMain=context;
      dialog = new Dialog(context);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(R.layout.oops_dialog);
      final Window window= dialog.getWindow();
      window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);

      tvDone= dialog.findViewById(R.id.tvDone);
      tvDone.setOnClickListener(new View.OnClickListener() {
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
