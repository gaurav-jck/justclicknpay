package com.justclick.clicknbook.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagePicker {

    public static final int CAMERA=1, GALLERY=2;

    public static String getImagePath(Intent data){
        if(data!=null && data.getData()!=null){
            return data.getData().getPath();
        }else {
            return null;
        }

    }

    public static Bitmap getImageBitmap(Intent data, Context context){
        // Let's read picked image data - its URI
        Uri pickedImage = data.getData();
        // Let's read picked image path using content resolver
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        // Do something with the bitmap

        // At the end remember to close the cursor or you will end with the RuntimeException!
        cursor.close();
        return bitmap;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Bank_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

}
