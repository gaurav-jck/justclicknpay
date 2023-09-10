package com.justclick.clicknbook.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtilsUpload {


    public static Uri getFilePathFromUri(Uri uri, Context context) throws IOException {
        String fileName = getFileName(uri, context);
        File file = new File(context.getExternalCacheDir(), fileName);
        file.createNewFile();
        return Uri.fromFile(file);
    }

    @SuppressLint("NewApi")
    public static void getSize(Intent data, File file, Context context){
        int dataSize=0;
        Uri uri  = data.getData();
        String scheme = uri.getScheme();
        System.out.println("Scheme type " + scheme);
        if(scheme.equals(ContentResolver.SCHEME_CONTENT))
        {
            try {
                InputStream fileInputStream=context.getContentResolver().openInputStream(uri);
                dataSize = fileInputStream.available();
//                File file = new File("c:\\test\\google.txt");
                // Java 1.7
                Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("File size in bytes"+dataSize);
            System.out.println("File size in bytes"+file.length());

        }
        else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File f = null;
            String path = uri.getPath();
            try {
                f = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("File size in bytes"+f.length());
        }

    }

    public static String getFileName(Uri uri, Context context) {
        String fileName = getFileNameFromCursor(uri, context);
        if (fileName == null) {
            String fileExtension = getFileExtension(uri, context);
            fileName = "temp_file" + (fileExtension != null ? "." + fileExtension : "");
        } else if (!fileName.contains(".")) {
            String fileExtension = getFileExtension(uri, context);
            fileName = fileName + "." + fileExtension;
        }
        return fileName;
    }

    public static String getFileExtension(Uri uri, Context context) {
        String fileType = context.getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType);
    }

    public static String getFileNameFromCursor(Uri uri, Context context) {
        Cursor fileCursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        String fileName = null;
        if (fileCursor != null && fileCursor.moveToFirst()) {
            int cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex);
            }
        }
        return fileName;
    }

    public static String getPath(Uri uri, Context context) {
        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

//    new reference 12
//    https://www.youtube.com/watch?v=uHX5NB6wHao
}
