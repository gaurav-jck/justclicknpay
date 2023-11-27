package com.justclick.clicknbook.Fragment.train;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.ResponseBody;

public class ModifySearchDialog {
//    private static Dialog dialog;
    private static Context context;
    private static AutoCompleteTextView fromAtv, toAtv;
    private static LoginModel loginModel;
    private static TrainStationModel stationResponseModel;
    private static OnCityDialogResult onCityDialogResult;
    private static HashMap<String, TrainStationModel.Items> hashMap;

    private static TextView departDayTv, departMonthTv, departDayNameTv;
    private static Calendar fromCalender, maxDateCalendar, currentDateCalender;
    private static String dateToSend, doj, fromStation, toStation;
    private static int fromDateDay, fromDateMonth, fromDateYear;
    private static SimpleDateFormat dateToServerFormat, dayFormat, monthFormat, dateFormat;

    public interface OnCityDialogResult{
        void setResult(TrainStationModel.Items fromStn, TrainStationModel.Items toStn, String date, String dateShow);
    }


    public static void showCustomDialog(final Context context, final HashMap<String, TrainStationModel.Items> hashMap, final String[] list, final String doj,
                                        final String fromStn, final String toStn, TrainListsFragment busSearchFragment){
        try {
            onCityDialogResult= (OnCityDialogResult) busSearchFragment;
//            dialog = new Dialog(context, R.style.Theme_Design_Light);
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.train_modify_search_dialog);
//            final Window window= dialog.getWindow();
//            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
            ModifySearchDialog.context=context;
            ModifySearchDialog.doj=doj;
            ModifySearchDialog.hashMap=hashMap;
            TextView searchTitleTv= dialog.findViewById(R.id.searchTitleTv);
            ImageView arrowImg= dialog.findViewById(R.id.arrowImg);
            searchTitleTv.setText("Modify Search");
            fromAtv= dialog.findViewById(R.id.fromAtv);
            toAtv= dialog.findViewById(R.id.toAtv);
            fromAtv.setText(fromStn);
            toAtv.setText(toStn);
            fromStation=fromStn;
            toStation=toStn;
            departDayNameTv= dialog.findViewById(R.id.departDayNameTv);
            departDayTv= dialog.findViewById(R.id.departDayTv);
            departMonthTv= dialog.findViewById(R.id.departMonthTv);

            ArrayAdapter adapter= new ArrayAdapter(context, R.layout.spinner_item, R.id.name_tv, list);
//                var adapter= ArrayAdapter(requireContext(), R.layout.spinner_item, R.id.name_tv, trainResponse!!.quotaList!!)
            fromAtv.setAdapter(adapter);
            toAtv.setAdapter(adapter);

            loginModel=new LoginModel();
            loginModel= MyPreferences.getLoginData(loginModel,context);

            initializeDates();

            arrowImg.setOnClickListener(v->{
                String temp=fromAtv.getText().toString();
                fromAtv.setText(toAtv.getText().toString());
                toAtv.setText(temp);
                fromAtv.dismissDropDown();
                toAtv.dismissDropDown();
                fromStation=fromAtv.getText().toString();
                toStation=toAtv.getText().toString();
            });
            dialog.findViewById(R.id.dateConst).setOnClickListener(v->{
                selectDate();
            });
            dialog.findViewById(R.id.cancelTv).setOnClickListener(v->{
                Common.hideSoftInputFromDialog(dialog,context);
                dialog.dismiss();
            });
            dialog.findViewById(R.id.submitTv).setOnClickListener(v->{
                if(fromStation.isEmpty()){
                    Toast.makeText(context, "Please select From station", Toast.LENGTH_SHORT).show();
                }else if(toStation.isEmpty()){
                    Toast.makeText(context, "Please select To station", Toast.LENGTH_SHORT).show();
                }else {
                    Common.hideSoftInputFromDialog(dialog,context);
                    String dateShow=departDayNameTv.getText().toString()+", "+departDayTv.getText().toString()+" "+
                            departMonthTv.getText().toString();
                    onCityDialogResult.setResult(hashMap.get(fromStation),
                            hashMap.get(toStation), dateToSend, dateShow);
                    dialog.dismiss();
                }
            });


            fromAtv.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        Common.hideSoftInputFromDialog(dialog,context);
                        fromStation=fromAtv.getText().toString();
                    });
            toAtv.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        Common.hideSoftInputFromDialog(dialog,context);
                        toStation=toAtv.getText().toString();
                    });

            fromAtv.setOnClickListener(v -> {
                fromAtv.setText("");
                fromStation="";
            });
            toAtv.setOnClickListener(v -> {
                toAtv.setText("");
                toStation="";
            });

            dialog.setCancelable(true);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void selectDate() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                (view, year, monthOfYear, dayOfMonth) -> {
                    fromCalender.set(year, monthOfYear, dayOfMonth);
                    fromDateDay = dayOfMonth;
                    fromDateMonth = monthOfYear;
                    fromDateYear = year;
                    departDayTv.setText(dateFormat.format(fromCalender.getTime()));
                    departMonthTv.setText(monthFormat.format(fromCalender.getTime()));
                    departDayNameTv.setText(dayFormat.format(fromCalender.getTime()));
                    dateToSend = dateToServerFormat.format(fromCalender.getTime());
                },fromDateYear, fromDateMonth, fromDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDateCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private static void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();
        dateFormat = Common.getDayFormat();
        monthFormat = Common.getMonthYearFormat();
        dayFormat = Common.getFullDayFormat();

        //default start Date
//        fromCalender= Calendar.getInstance();
        fromCalender= Common.getDateCalendarFromString(doj);
        fromDateDay=fromCalender.get(Calendar.DAY_OF_MONTH);
        fromDateMonth=fromCalender.get(Calendar.MONTH);
        fromDateYear=fromCalender.get(Calendar.YEAR);

        dateToSend = dateToServerFormat.format(fromCalender.getTime());

        maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.add(Calendar.MONTH,4);

//        default date set

//        currentDateCalender=Common.getDateCalendarFromString(doj);
        departDayTv.setText(dateFormat.format(fromCalender.getTime()));
        departMonthTv.setText(monthFormat.format(fromCalender.getTime()));
        departDayNameTv.setText(dayFormat.format(fromCalender.getTime()));
        dateToSend = dateToServerFormat.format(fromCalender.getTime());
    }


    /*public static void hideCustomDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public static Boolean isDialogShowing(){
        if(dialog!=null && dialog.isShowing())
            return true;
        return false;
    }*/

}
