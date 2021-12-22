package com.justclick.clicknbook.utils;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.justclick.clicknbook.R;

/**
 * Created by pc1 on 4/24/2017.
 */
//uses link
//https://pankajchunchun.wordpress.com/2011/09/10/customization-of-spinner-progress/

public class MyCustomDialog {
    private static AlertDialog dialog;
    static TextView messageTv;

    public static void showCustomDialog(Context context, String message){
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            final View dialogView = LayoutInflater.from(context).
                    inflate(R.layout.custom_dialog_view, null);
            messageTv=dialogView.findViewById(R.id.tv_progressmsg);
            messageTv.setText(message);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            dialog = dialogBuilder.create();
            dialog.show();
        }catch (Exception e){

        }

    }

    public static void hideCustomDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public static void setDialogMessage(String message){
        if(messageTv!=null)
        messageTv.setText(message);
    }

    public static Boolean isDialogShowing(){
        if(dialog!=null && dialog.isShowing())
        return true;
        return false;
    }
}
