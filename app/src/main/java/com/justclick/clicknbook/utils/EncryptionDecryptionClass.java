package com.justclick.clicknbook.utils;

import android.content.Context;
import android.util.Base64;

import com.justclick.clicknbook.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gaurav.singhal on 9/28/2017.
 */

public class EncryptionDecryptionClass {

  private static String shak="fnsddfnasfnadfnkdmfnd";

  private static String getEncryptionKey() {
    String key="c9XAzmFaC4l5lmdsipTaJqMKjYu2lW0";
    return key;
  }
  private static String getEncryptionKeyNew() {
    String key="c9XAzmFaC4l5lmdsipTaJqMKjYu2lW00";
    return key;
  }

  private static String getSessionEncryptionKey(Context context) {
//        return context.getResources().getString(R.string.google_api_key).substring(2,34);
    return "nwA9gVUzpa0wFostuUWuPZmdiWci63o";
  }

  public static String Encryption(String text, Context context){
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      byte[] keyBytes = new byte[16];
      byte[] b = getEncryptionKey().getBytes("UTF-8");
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

  public static String EncryptionNew(String text, Context context){
    try {
      byte[] plaintextBytes = text.replace("\n","").getBytes("UTF-8");
      byte[] iv = new byte[12]; // GCM recommended IV size is 12 bytes
      new SecureRandom().nextBytes(iv);

      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit authentication tag
      SecretKeySpec key = new SecretKeySpec(getEncryptionKey().getBytes("UTF-8"), "AES");
      cipher.init(Cipher.ENCRYPT_MODE,key, spec);

      byte[] ciphertext = cipher.doFinal(plaintextBytes);

      // Combine IV and ciphertext/tag and encode to Base64 for storage/transmission
      // The tag is automatically appended to the ciphertext in Java's GCM implementation
      byte[] combined = new byte[iv.length + ciphertext.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

      return Base64.encodeToString(combined, Base64.DEFAULT);
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

  public static String EncryptSessionIdNew(String text, Context context){
    return Encryption(text, context);
  }

  public static String Decryption(String text, Context context){
    try {
      Cipher cipher = Cipher.getInstance
              ("AES/CBC/PKCS5Padding"); //this parameters should not be changed
      byte[] keyBytes = new byte[16];
      byte[] b = getEncryptionKey().getBytes("UTF-8");
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

  public static String DecryptionNew(String text, Context context){
    try {
      byte[] decodedData = Base64.decode(text.replace("\n",""), Base64.DEFAULT);

      // Extract IV (first 12 bytes)
      byte[] iv = new byte[12];
      System.arraycopy(decodedData, 0, iv, 0, iv.length);

      // Extract the actual ciphertext and tag
      int ciphertextLength = decodedData.length - 12;
      byte[] ciphertext = new byte[ciphertextLength];
      System.arraycopy(decodedData, 12, ciphertext, 0, ciphertextLength);

      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec spec = new GCMParameterSpec(128, iv);
      SecretKeySpec key = new SecretKeySpec(getEncryptionKey().getBytes("UTF-8"), "AES");
      cipher.init(Cipher.DECRYPT_MODE, key, spec);

      byte[] decryptedBytes = cipher.doFinal(ciphertext);
      return new String(decryptedBytes, "UTF-8");
    }catch (Exception e){
      return null;
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
