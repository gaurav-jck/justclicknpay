package com.justclick.clicknbook.utils;

import android.content.Context;
import android.util.Base64;

import com.justclick.clicknbook.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gaurav.singhal on 9/28/2017.
 */

public class EncryptionDecryptionClass {

  private static String shak="fnsddfnasfnadfnkdmfnd";

  private static String getEncryptionKey(Context context) {
    String key="c9XAzmFaC4l5lmdsipTaJqMKjYu2lW0";
    return key;
  }

  private static String getSessionEncryptionKey(Context context) {
//        return context.getResources().getString(R.String.google_api_key).subString(8);
    return "nwA9gVUzpa0wFostuUWuPZmdiWci63o";
  }

  public static String Encryption(String text, Context context){
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      byte[] keyBytes = new byte[16];
      byte[] b = getEncryptionKey(context).getBytes("UTF-8");
      int len = b.length;
      if (len > keyBytes.length)
        len = keyBytes.length;
      System.arraycopy(b, 0, keyBytes, 0, len);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

      byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

      return Base64.encodeToString(results,0).replace("\n",""); // it returns the result as a String
    }catch (Exception e){
      return null;
    }

  }

  public static String EncryptSessionId(String text, Context context){
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      byte[] keyBytes = new byte[16];
      byte[] b = getSessionEncryptionKey(context).getBytes("UTF-8");
      int len = b.length;
      if (len > keyBytes.length)
        len = keyBytes.length;
      System.arraycopy(b, 0, keyBytes, 0, len);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

      byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

      return Base64.encodeToString(results,0).replace("\n",""); // it returns the result as a String
    }catch (Exception e){
      return null;
    }

  }

  public static String Decryption(String text, Context context){
    try {
      Cipher cipher = Cipher.getInstance
              ("AES/CBC/PKCS5Padding"); //this parameters should not be changed
      byte[] keyBytes = new byte[16];
      byte[] b = getEncryptionKey(context).getBytes("UTF-8");
      int len = b.length;
      if (len > keyBytes.length)
        len = keyBytes.length;
      System.arraycopy(b, 0, keyBytes, 0, len);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      byte[] results = new byte[text.length()];
      try {
        results = cipher.doFinal(Base64.decode(text,0));
      } catch (Exception e) {
      }
      return new String(results, "UTF-8"); // it returns the result as a String
    }catch (Exception e){
      return null;
    }
  }

  public static final String deskey = "5b787793958d4f50d12c7da4";
  public static String opensslEncrypt(String data) {
    try {
      String iv = "01234567";
      byte[] key = deskey.getBytes();

      Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
      IvParameterSpec aaa = new IvParameterSpec(iv.getBytes());
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DESede"), aaa);

      byte[] results = cipher.doFinal(data.getBytes("UTF-8"));

      return Base64.encodeToString(results,0).replace("\n",""); // it returns the result as a String
//        return  java.util.Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }catch (Exception e){
      return "";
    }

  }

  static public String computeHash(String message) throws Exception {
    String secret = shak;
    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    sha256_HMAC.init(secret_key);
    byte b[]=sha256_HMAC.doFinal(message.getBytes());
    String sbinary = "";
    for (int i = 0; i < b.length; i++) {
      sbinary += String.format("%02X", b[i]);
    }
    return sbinary;
  }

}
