package com.justclick.clicknbook.rapipayMatm;


import android.util.Log;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import org.apache.commons.lang3.StringUtils;


public class Utils {

    public static String sha512(String merchantID,String subMerchantID,String clientrefid,String saltData){
        String sb = new String();
        String timestamp = new SimpleDateFormat("yyyymmddHH").format(new Date()).toString();
        String s = merchantID+"#"+subMerchantID+"#"+clientrefid+"#"+timestamp+"#"+saltData;
        Log.d("etmp",s);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(s.getBytes());
            byte  byteData[] = md.digest();
            sb = Utils.byte2hex(byteData);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("SecondHash", sb);
//        Log.d("SecondHashS", s);
        return sb;
    }



//    public static String getRefrence(String stan) {
//        int tempVar = 0;
//
//        try {
//            tempVar = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
//            String julianDay = StringUtils.leftPad(String.valueOf(tempVar), 3, "0");
//            tempVar = Calendar.getInstance().get(Calendar.YEAR);
//            String julianYear = String.valueOf(tempVar).substring(3);
//            tempVar = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//            String julianHours = StringUtils.leftPad(String.valueOf(tempVar), 2, "0");
//
//
//            stan= StringUtils.leftPad(String.valueOf(stan), 6, "0");
//
//            String rrn = julianYear + julianDay + julianHours+ stan ;
//
//            return rrn ;
//        }catch (Exception e) {
//
//          Log.d("error", String.valueOf(e));
//
//
//            return "";
//        }
//    }


    public static String getTagValue(String Tag, String emv) {
        try {
            String str;
            int tagIndex = emv.indexOf(Tag) + Tag.length();

            String hex = emv.substring(tagIndex, tagIndex + 2);
            int decimal = Integer.parseInt(hex, 16) * 2;

            str = emv.substring(tagIndex + 2, tagIndex + 2 + decimal);
            return str;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String diffrenceInseconds(String prevDateTime, String currDateTime) {
        try {
            String format = "yyyy-MM-dd hh:mm:ss";

            SimpleDateFormat sdf = new SimpleDateFormat(format);

            Date dateObj1 = sdf.parse(prevDateTime);
            Date dateObj2 = sdf.parse(currDateTime);
            System.out.println(dateObj1);
            System.out.println(dateObj2 + "\n");

            DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");

            long diff = dateObj2.getTime() - dateObj1.getTime();



            int diffsec = (int) (diff / (1000));
            return (crunchifyFormatter.format(diffsec));



        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getTagValue1(String Tag, String emv) {
        String result = new String();
        try {
            String str;
            int tagIndex = emv.indexOf(Tag) + Tag.length();

            String hex = emv.substring(tagIndex, tagIndex + 2);
            int decimal = Integer.parseInt(hex, 16) * 2;

            str = emv.substring(tagIndex + 2, tagIndex + 2 + decimal);
            if (Tag.equals("9F36"))
                return str;
            char[] charArray = str.toCharArray();
            for (int i = 0; i < charArray.length; i = i + 2) {
                String st = "" + charArray[i] + "" + charArray[i + 1];
                char ch = (char) Integer.parseInt(st, 16);
                result = result + ch;
            }
            result = result.replace("/", "").trim();
            return result;
        } catch (Exception e) {

        }
        return "";
    }

    public static String byte2hex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = (bytes[idx] & 0xF0) >>> 4;
            int lo = (bytes[idx] & 0x0F);
            hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
            hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
        }
        return new String(hex);
    }

}
