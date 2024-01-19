package com.justclick.clicknbook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightCityModel;
import com.justclick.clicknbook.firebase.ForceLoginData;
import com.justclick.clicknbook.model.LoginModel;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class MyPreferences {

  private static final String MyPrefName="MyPref";
  private static final String IsLogin="IsLogin";
  private static final String MyLoginDataClass="MyLoginDataClass";
  private static final String Password="Password";
  private static final String EmailId="emailId";
  private static final String fromCity="fromCity";
  private static final String toCity="toCity";
  private static final String fromBusCity="fromBusCity";
  private static final String toBusCity="toBusCity";
  private static final String AdultFlight="AdultFlight";
  private static final String ChildFlight="ChildFlight";
  private static final String InfantFlight="InfantFlight";
  private static final String VersionCode="VersionCode";
  private static final String ForceLogin="ForceLogin";
  private static final String ExpiredDate="ExpiredDate";
  private static final String FlightSearchRequestModelClass="FlightSearchRequestModelClass";
  private static final String FlightSearchSourceCityClass="FlightSearchSourceCityClass";
  private static final String FlightSearchDestinationCityClass="FlightSearchDestinationCityClass";
  private static final String JWTToken = "JWTToken";
  private static final String KEY_AUTH_TOKEN = "authkey";
  private static final String KEY_AEPS_TOKEN = "aepsToken";
  private static final String KEY_AGENT_ADHAR = "AGENT_ADHAR";
  private static final String KEY_AGENT_MOBILE = "AGENT_MOBILE";
  private static final String KEY_USER_DATA = "userData";
  private static final String KEY_SESSION_KEY= "sessionKey";
  private static final String KEY_SESSION_REF_NO= "sessionRefNo";

  public MyPreferences(){
  }

  public static SharedPreferences getPreferences(Context context){
    return context.getSharedPreferences(MyPrefName, context.MODE_PRIVATE);
  }

  public static void saveLoginData(LoginModel tClass, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    String json = new Gson().toJson(tClass);
    prefsEditor.putString(MyLoginDataClass, json);
    prefsEditor.commit();
  }

  public static void saveLoginId(Context context, String emailId){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(EmailId, emailId);
    prefsEditor.commit();
  }

  public static LoginModel getLoginData(LoginModel loginModel, Context context){
    String json = getPreferences(context).getString(MyLoginDataClass, "");
    LoginModel obj = new Gson().fromJson(json,loginModel.getClass());
    return obj;
  }

  public static void logoutUser(Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putBoolean(IsLogin, false);
//        prefsEditor.putString(EmailId, "");
    prefsEditor.putString(Password, "");
    prefsEditor.commit();
  }

  public static void loginUser(Context context,String email, String password){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putBoolean(IsLogin, true);
    prefsEditor.putString(EmailId, email);
    prefsEditor.putString(Password, password);
    prefsEditor.commit();
  }

  public static void saveEmailPassword(Context context,String email,String password){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(EmailId, email);
    prefsEditor.putString(Password, password);
    prefsEditor.commit();
  }

  public static Boolean isUserLogin(Context context){
    return getPreferences(context).getBoolean(IsLogin, false);
  }

  public static String getLoginId(Context context){
    return getPreferences(context).getString(EmailId, "");
  }

  public static String getLoginPassword(Context context){
    return getPreferences(context).getString(Password, "");
  }

  public static void setForceLoginData(Context context, ForceLoginData data){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putLong(VersionCode, data.versionCode);
    prefsEditor.putBoolean(ForceLogin, data.isForceLogin);
    prefsEditor.commit();
  }

  public static ForceLoginData getForceLoginData(Context context){
    ForceLoginData data=new ForceLoginData();
    data.isForceLogin=getPreferences(context).getBoolean(ForceLogin, false);
    data.versionCode=getPreferences(context).getLong(VersionCode, 1);
    return data;
  }

  public static void setAppCurrentTime(Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putLong(ExpiredDate, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60)); // change to 60
    prefsEditor.commit();
  }

  public static long getAppCurrentTime(Context context){
    return getPreferences(context).getLong(ExpiredDate, 0);
  }

  public static String getLoginToken(Context context){
    return getPreferences(context).getString(JWTToken, "");
  }

  public static void setFlightFromCity(Context context,String city){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(fromCity, city);
    prefsEditor.commit();
  }
  public static String getFlightFromCity(Context context){
    return getPreferences(context).getString(fromCity, "");
  }

  public static void setFlightToCity(Context context,String city){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(toCity, city);
    prefsEditor.commit();
  }

  public static void saveFlightSearchRequestData(FlightSearchRequestModel tClass, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    String json = new Gson().toJson(tClass);
    prefsEditor.putString(FlightSearchRequestModelClass, json);
    prefsEditor.commit();
  }
  public static FlightSearchRequestModel getFlightSearchRequestData(Context context){
    String json = getPreferences(context).getString(FlightSearchRequestModelClass, "");
    FlightSearchRequestModel obj = new Gson().fromJson(json,new FlightSearchRequestModel().getClass());
    return obj;
  }

  public static void setFlightSourceCityData(FlightCityModel.response tClass, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    String json = new Gson().toJson(tClass);
    prefsEditor.putString(FlightSearchSourceCityClass, json);
    prefsEditor.commit();
  }
  public static FlightCityModel.response getFlightSourceCityData(Context context){
    String json = getPreferences(context).getString(FlightSearchSourceCityClass, "");
    FlightCityModel.response obj = new Gson().fromJson(json,new FlightCityModel().new response().getClass());
    return obj;
  }

  public static String getFlightToCity(Context context){
    return getPreferences(context).getString(toCity, "");
  }

  public static void setFlightDestinationCityData(FlightCityModel.response tClass, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    String json = new Gson().toJson(tClass);
    prefsEditor.putString(FlightSearchDestinationCityClass, json);
    prefsEditor.commit();
  }

  public static FlightCityModel.response getFlightDestinationCityData(Context context){
    String json = getPreferences(context).getString(FlightSearchDestinationCityClass, "");
    FlightCityModel.response obj = new Gson().fromJson(json,new FlightCityModel().new response().getClass());
    return obj;
  }

  public static void setBusFromCity(Context context,String busCity){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(fromBusCity, busCity);
    prefsEditor.commit();
  }

  public static String getBusFromCity(Context context){
    return getPreferences(context).getString(fromBusCity, "");
  }

  public static void setBusToCity(Context context,String busCity){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(toBusCity, busCity);
    prefsEditor.commit();
  }

  public static String getBusToCity(Context context){
    return getPreferences(context).getString(toBusCity, "");
  }

  public static void setFlightAdult(Context context,int adult){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putInt(AdultFlight, adult);
    prefsEditor.commit();
  }
  public static int getFlightAdult(Context context){
    return getPreferences(context).getInt(AdultFlight, 1);
  }
  public static void setFlightChild(Context context,int child){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putInt(ChildFlight, child);
    prefsEditor.commit();
  }
  public static int getFlightChild(Context context){
    return getPreferences(context).getInt(ChildFlight, 0);
  }
  public static void setFlightInfant(Context context,int infant){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putInt(InfantFlight, infant);
    prefsEditor.commit();
  }
  public static int getFlightInfant(Context context){
    return getPreferences(context).getInt(InfantFlight, 0);
  }

  public static void saveToken(String token, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_AUTH_TOKEN, EncryptionDecryptionClass.Encryption(token, context));
    prefsEditor.commit();
  }


  public static String getToken(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_AUTH_TOKEN, ""), context);
  }

  public static void saveAepsToken(String token, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_AEPS_TOKEN, EncryptionDecryptionClass.Encryption(token, context));
    prefsEditor.commit();
  }


  public static String getAepsToken(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_AEPS_TOKEN, ""), context);
  }

  public static void saveAepsAgentData(String adhar, String mobile, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_AGENT_ADHAR, EncryptionDecryptionClass.Encryption(adhar, context));
    prefsEditor.putString(KEY_AGENT_MOBILE, EncryptionDecryptionClass.Encryption(mobile, context));
    prefsEditor.commit();
  }


  public static String getAgentAdhar(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_AGENT_ADHAR, ""), context);
  }
  public static String getAgentMobile(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_AGENT_MOBILE, ""), context);
  }

  public static void saveUserData(String token, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_USER_DATA, EncryptionDecryptionClass.Encryption(token, context));
    prefsEditor.commit();
  }

  public static String getUserData(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_USER_DATA, ""), context);
  }
  public static void saveSessionKey(String token, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_SESSION_KEY, EncryptionDecryptionClass.Encryption(token, context));
    prefsEditor.commit();
  }

  public static String getSessionKey(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_SESSION_KEY, ""), context);
  }
  public static void saveSessionRefNo(String token, Context context){
    SharedPreferences.Editor prefsEditor = getPreferences(context).edit();
    prefsEditor.putString(KEY_SESSION_REF_NO, EncryptionDecryptionClass.Encryption(token, context));
    prefsEditor.commit();
  }

  public static String getSessionRefNo(Context context){
    return EncryptionDecryptionClass.Decryption(getPreferences(context).getString(KEY_SESSION_REF_NO, ""), context);
  }
}
