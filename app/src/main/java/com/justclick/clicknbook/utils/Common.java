package com.justclick.clicknbook.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.justclick.clicknbook.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by pc1 on 4/14/2017.
 */

public final class Common {

  public static boolean checkInternetConnection(Context context) {
    ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo() != null &&
            cm.getActiveNetworkInfo().isConnectedOrConnecting();
  }

  public static String getVersionCode(Context context) {
    PackageManager manager = context.getPackageManager();
    PackageInfo info = null;
    int versionCode = 0;
    try {
      info = manager.getPackageInfo(
              context.getPackageName(), 0);
      versionCode = info.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionCode + "";
  }

  public static String getDeviceIMEI(Context context) {
    String deviceUniqueIdentifier = null;

    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (null != tm) {
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        deviceUniqueIdentifier = tm.getDeviceId();
      }

    }
    if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
      deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    return deviceUniqueIdentifier;
  }

  public static String getDeviceId(Context context) {
    return Settings.Secure.getString(context.getContentResolver(),
            Settings.Secure.ANDROID_ID);
  }

  public static void hideSoftKeyboard(Activity activity) {
    if(activity!=null && activity.getCurrentFocus()!=null) {
      InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
  }

  public static void hideSoftInputFromDialog(Dialog dialog, Context context) {
    try{
      InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getCurrentFocus().getWindowToken(), 0);}
    catch (NullPointerException e){}
  }
  public static void showSoftInput(Context context) {
    try {
      InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    catch (NullPointerException e){}
  }

  public static void showSoftKeyboard(Context context, View view) {
    if (view.requestFocus()) {
      InputMethodManager imm = context.getSystemService(InputMethodManager.class);
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
  }


  public static float roundOffDecimalValue(Float decimal)
  {
    DecimalFormat df = new DecimalFormat("#.##");
    return Float.parseFloat(df.format(decimal));
  }


  public static String getDecodedString(String value){
    byte[] encodiedBytes = Base64.encode(value.getBytes(), Base64.DEFAULT);
    return  new String(encodiedBytes).replace("\n","");
  }

  public static boolean isEmailValid(String email){
    String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    if(email.length()>0 && email.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }
  public static boolean isMobileValid(String email){
    String pattern = "[0-9]{10}";
    if(email.length()>0 && email.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isCreditCardValid(String email){
    String pattern = "[0-9]*";
    if(email.length()>0 && email.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isNameValid(String name){
    String pattern = "[a-zA-Z ]*";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isRegexValid(String name,String regex){
    if(name.length()>0 && name.matches(regex)){
      return true;
    }else {
      return false;
    }
  }

  public static void isFocusable(Context context,TextView textView)
  {
    textView.requestFocus();
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
  }

  public static boolean isFlightNameValid(String name){
    String pattern = "[a-zA-Z]*";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isPancardValid(String name){
    String pattern = "[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isIFSCValid(String name){
    String patternOld = "[A-Za-z]{4}[0-9]{7}";
    String pattern = "[A-Za-z]{4}[a-zA-Z0-9]{7}";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isGSTValid(String name){
    String pattern = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isTrainReferenceValid(String name){
    String pattern = "[a-zA-Z0-9]*";
    if(name.length()>0 && name.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static boolean isdecimalvalid(String decimal){
    String pattern = "[0-9]+([,.][0-9]{1,2})?";
    if(decimal.trim().length()>0 && decimal.matches(pattern)){
      return true;
    }else {
      return false;
    }
  }

  public static void showResponsePopUp(Context context, String message){
    final Dialog responseDialog = new Dialog(context);
    responseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    responseDialog.setContentView(R.layout.mobile_alert_dialog_layout);
    responseDialog.setCancelable(false);

    ((TextView) responseDialog.findViewById(R.id.detail_tv)).setText(message);
    ((TextView) responseDialog.findViewById(R.id.detail_tv)).setMovementMethod(new ScrollingMovementMethod());

    responseDialog.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        responseDialog.dismiss();
//                getFragmentManager().popBackStack();
      }
    });

//        forgetDialog.setCancelable(false);
    responseDialog.show();
  }
  public static int getAgeFromDOB(Date date){
    int age= Calendar.getInstance().getTime().getYear()-date.getYear();
    return age;
  }

  public static String formatBusTime(String timeValue){
    int time= Integer.parseInt(timeValue);
    int hours=time/60;
    int min=time%60;
    String hz="", mz="";

    hours=hours-(24*(hours/24));

    if(hours<10){
      hz="0";
    }
    if(min<10){
      mz="0";
    }
    return hz+hours+":"+mz+min+"";
  }

  public static int getBusArrivalDate(String timeValue){
    return (Integer.parseInt(timeValue)/60)/24;
  }


  public static String getBusDuration(String dTime, String aTime){
    int time=Integer.parseInt(aTime)-Integer.parseInt(dTime);
    int hours=time/60;
    int min=time%60;
    String hz="", mz="";

    if(hours<10){
      hz="0";
    }
    if(min<10){
      mz="0";
    }
    return hz+hours+"h "+mz+min+"m";
  }
  public static Bitmap cropImage(Context context, int drawable){
    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
    Matrix matrix = new Matrix();
    matrix.postScale(0.5f, 0.5f);
    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 115, bitmap.getWidth(), 35, matrix, true);//x,y,width,height
    return croppedBitmap;
  }

  public static Typeface homeMenuMainItemTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/quicksand_bold.otf");
  }
  public static Typeface homeSubMenuItemTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/museo_regular.otf");
  }
  public static Typeface listAgencyNameTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/anticslab_regular.otf");
  }
  public static Typeface ButtonTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/quattrocento_sans_regular.ttf");
  }
  public static Typeface EditTextTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/roboto_condensed.ttf");
  }

  public static Typeface FlightCalenderTypeFace3(Context context) {
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/swz.ttf");
  }

  public static Typeface OpenSansRegularTypeFace(Context context) {
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/open_sans_regular.ttf");
  }

  public static Typeface TitleTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/klinicSlabMedium.otf");

  }
  public static Typeface ralewayMedium(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/raleway_medium.ttf");
  }
  public static Typeface TextViewTypeFace(Context context){
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/raleway_semi_bold.ttf");
  }
  public static Typeface ButtonTypeFace1(Context context) {
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/afta_serif_thin_regular.otf");
  }
  public static Typeface EditTextTypeFace1(Context context) {
    return Typeface.createFromAsset(context.getAssets(),
            "fonts/alegreya_regular.otf");
  }


  public static SimpleDateFormat getServerDateFormat(){
    return new SimpleDateFormat("yyyyMMdd", Locale.US);
  }
  public static SimpleDateFormat getShowInTVDateFormat(){
    return new SimpleDateFormat("dd/MM/yyyy", Locale.US);
  }

  public static SimpleDateFormat getToFromDateFormat(){
    return new SimpleDateFormat("dd MMM yy", Locale.US);
  }
  public static SimpleDateFormat getDayFormat(){
    return new SimpleDateFormat("dd", Locale.US);
  }
  public static SimpleDateFormat getMonthYearFormat(){
    return new SimpleDateFormat("MMM yy", Locale.US);
  }
  public static SimpleDateFormat getShortDayFormat(){
    return new SimpleDateFormat("EEE", Locale.US);
  }
  public static SimpleDateFormat getFullDayFormat(){
    return new SimpleDateFormat("EEEE", Locale.US);
  }
  public static SimpleDateFormat getFullDateTimeFormat(){
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
  }

  public static Calendar getDateCalendarFromString(String date) {
    try {
      SimpleDateFormat mInputDateFormat =
              new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
      Date date1=mInputDateFormat.parse(date);
      Calendar calendar=Calendar.getInstance();
      calendar.setTime(date1);
      return calendar;
    }catch (ParseException e){
      return Calendar.getInstance();
    }
  }

  public static String getJCTMoneyTime(String time){
    Date date;
    try {
      date = getFullDateTimeFormat().parse(time);
    } catch (ParseException e) {
      return "";
    }
    return new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.US).format(date);
  }

  public static ArrayAdapter<String> getAutocompleteAdapter(String[] arr, Context context){
    ArrayAdapter<String> adapter= new ArrayAdapter<String>(
            context,
            R.layout.autocomplete_item, R.id.operator_tv, arr);
    adapter.setDropDownViewResource(R.layout.autocomplete_item_dropdown);

    return adapter;
  }


  public static void preventFrequentClick(final View view){
    view.setClickable(false);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        view.setClickable(true);
      }
    }, 600);
  }

  public static void preventCopyPaste(final EditText view){
    view.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
      @Override
      public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
      }

      @Override
      public void onDestroyActionMode(ActionMode mode) {

      }
    });
  }


  public static String loadJSONFromAsset(Context context, String fileName) {
    String json = null;
    try {
      InputStream is = context.getAssets().open(fileName);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
    return json;
  }

  static class Utils {
    public static SortedMap<Currency, Locale> currencyLocaleMap;

    static {
      currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
        public int compare(Currency c1, Currency c2) {
          return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
        }
      });
      for (Locale locale : Locale.getAvailableLocales()) {
        try {
          Currency currency = Currency.getInstance(locale);
          currencyLocaleMap.put(currency, locale);
        } catch (Exception e) {
        }
      }
    }

    public static String getCurrencySymbol(String currencyCode) {
      Currency currency = Currency.getInstance(currencyCode);
      System.out.println(currencyCode + ":-" + currency.getSymbol(currencyLocaleMap.get(currency)));
      return currency.getSymbol(currencyLocaleMap.get(currency));
    }

  }

}
