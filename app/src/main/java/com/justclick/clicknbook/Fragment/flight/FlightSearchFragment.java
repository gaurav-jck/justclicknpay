package com.justclick.clicknbook.Fragment.flight;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.FlightSearchRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyBounceInterpolator;
import com.justclick.clicknbook.utils.MyFlightCityDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.MyflightDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class FlightSearchFragment extends Fragment implements View.OnClickListener, MyFlightCityDialog.OnCityDialogResult{
    final int CHECK_IN=1, CHECK_OUT=2;
    private final String ECONOMY="Y", FIRST_CLASS="F", BUSINESS="C", PREMIUM_ECONOMY="W";
    private final int MaxAdult=9, MaxChild=9, MaxInfant=9;
    private int NumberOfAdult=1, NumberOfChild=0, NumberOfInfants=0, NumberOfAdultTemp=1, NumberOfChildTemp=0, NumberOfInfantsTemp=0;
    private final String ONE_WAY="ONE", ROUND_TRIP="ROUND";
    private Context context;
    private LoginModel loginModel;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private ListView cityListView;
    private TextView departDateTv, returnDateTv,search_Tv, roundTripTv, oneWayTv,
            fromTv,toTv, returnYearTv, returnDayTv, departDayTv, departYearTv;
    private TextView adultTv, childTv, infantTv, adultValueTv, adultCountTv,departLabel,returnLabel,
            childValueTv, childCountTv, infantValueTv, infantCountTv, returnDateLabelTv,fromNameTv,fromCitytv,toNameTv,toCitytv
            , economyTv, businessTv, premiumTv, firstClassTv;
    private ImageView adultImg, childImg, infantImg, oneWayRadioImg, roundTripRadioImg,flight_img,
            adultPlusImg,adultMinusImg,childPlusImg,childMinusImg,infantPlusImg,infantMinusImg;
    private Spinner classSpinner;
    private LinearLayout fromLayout,toLayout,oneWayLin,roundTripLin;
    private RelativeLayout returnDateLin,searchRel;
    private String checkInDate="", checkOutDate="", tripType=ONE_WAY, fromCityCode=" ", toCityCode=" ", classType=ECONOMY;
    private SimpleDateFormat dateServerFormat, dateMonthFormat, dayFormat, yearFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear,
            checkOutDateDay, checkOutDateMonth, checkOutDateYear;
    private Calendar checkInDateCalendar, checkOutDateCalendar;
    private ObjectAnimator objectAnimatorOneWay, objectAnimatorRound,
            objectAnimatorEconomy, objectAnimatorBusiness, objectAnimatorPremium, objectAnimatorFirst;
    private AlertDialog flightSearchDialog;
    private static Fragment fragment;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){

        }
    }

    public static Fragment newInstance() {
        if(fragment==null) {
            fragment = new FlightSearchFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        tripType=ONE_WAY;
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        initializeDates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_flight_search_new, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.flightSearchFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
        cityListView = (ListView) view.findViewById(R.id.cityListView);

        adultTv= (TextView) view.findViewById(R.id.adultTv);
        childTv= (TextView) view.findViewById(R.id.childTv);
        infantTv= (TextView) view.findViewById(R.id.infantTv);
        departDateTv= (TextView) view.findViewById(R.id.departDateTv);
        returnDateTv= (TextView) view.findViewById(R.id.returnDateTv);
        search_Tv= (TextView) view.findViewById(R.id.search_Tv);
        fromNameTv= (TextView) view.findViewById(R.id.fromNameTv);
        roundTripTv = (TextView) view.findViewById(R.id.roundTripTv);
        economyTv = (TextView) view.findViewById(R.id.economyTv);
        businessTv = (TextView) view.findViewById(R.id.businessTv);
        premiumTv = (TextView) view.findViewById(R.id.premiumTv);
        firstClassTv = (TextView) view.findViewById(R.id.firstClassTv);
        oneWayTv = (TextView) view.findViewById(R.id.oneWayTv);
        departLabel= (TextView) view.findViewById(R.id.departLabel);
        returnLabel= (TextView) view.findViewById(R.id.returnLabel);
        fromTv= (TextView) view.findViewById(R.id.fromTv);
        toTv= (TextView) view.findViewById(R.id.toTv);
        returnYearTv= (TextView) view.findViewById(R.id.returnYearTv);
        returnDayTv= (TextView) view.findViewById(R.id.returnDayTv);
        departDayTv= (TextView) view.findViewById(R.id.departDayTv);
        departYearTv= (TextView) view.findViewById(R.id.departYearTv);


//        fromCitytv= (TextView) view.findViewById(R.id.fromCitytv);
        toNameTv= (TextView) view.findViewById(R.id.toNameTv);
//        toCitytv= (TextView) view.findViewById(R.id.toCitytv);

        oneWayRadioImg= (ImageView) view.findViewById(R.id.oneWayRadioImg);
        roundTripRadioImg= (ImageView) view.findViewById(R.id.roundTripRadioImg);
        returnDateLin= (RelativeLayout) view.findViewById(R.id.returnDateLin);
        searchRel= (RelativeLayout) view.findViewById(R.id.searchRel);
        flight_img= (ImageView) view.findViewById(R.id.flight_img);
        adultPlusImg= (ImageView) view.findViewById(R.id.adultPlusImg);
        adultMinusImg= (ImageView) view.findViewById(R.id.adultMinusImg);
        childPlusImg= (ImageView) view.findViewById(R.id.childPlusImg);
        childMinusImg= (ImageView) view.findViewById(R.id.childMinusImg);
        infantPlusImg= (ImageView) view.findViewById(R.id.infantPlusImg);
        infantMinusImg= (ImageView) view.findViewById(R.id.infantMinusImg);

        oneWayLin= (LinearLayout) view.findViewById(R.id.oneWayLin);
        roundTripLin= (LinearLayout) view.findViewById(R.id.roundTripLin);


        adultValueTv= (TextView) view.findViewById(R.id.adultValueTv);
        adultCountTv= (TextView) view.findViewById(R.id.adultCountTv);
        childValueTv= (TextView) view.findViewById(R.id.childValueTv);
        childCountTv= (TextView) view.findViewById(R.id.childCountTv);
        infantValueTv= (TextView) view.findViewById(R.id.infantValueTv);
        infantCountTv= (TextView) view.findViewById(R.id.infantCountTv);

        view.findViewById(R.id.adultMinusImg).setOnClickListener(this);
        view.findViewById(R.id.adultPlusImg).setOnClickListener(this);
        view.findViewById(R.id.childPlusImg).setOnClickListener(this);
        view.findViewById(R.id.childMinusImg).setOnClickListener(this);
        view.findViewById(R.id.infantMinusImg).setOnClickListener(this);
        view.findViewById(R.id.infantPlusImg).setOnClickListener(this);

//        setDefaultDialogValues();

//        fromLayout= (LinearLayout) view.findViewById(R.id.fromLayout);
//        toLayout= (LinearLayout) view.findViewById(R.id.toLayout);

//        classSpinner= (Spinner) view.findViewById(R.id.classSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.flight_class_spinner_item, R.id.operator_tv, getResources().
                getStringArray(R.array.airClass));
        adapter.setDropDownViewResource(R.layout.flight_class_spinner_item);

//        classSpinner.setAdapter(adapter);
//        ColorFilter blue = new LightingColorFilter( getResources().getColor(R.color.color_white),
//                getResources().getColor(R.color.app_blue_color));
//        Drawable homeIcon = ContextCompat.getDrawable(context, R.drawable.air_icon);
//        homeIcon.setColorFilter(blue);
//        ((ImageView)view.findViewById(R.id.flightImgDepart)).setImageDrawable(homeIcon);

        fromNameTv.setText(MyPreferences.getFlightFromCity(context));
        toNameTv.setText(MyPreferences.getFlightToCity(context));

        fromCityCode=fromNameTv.getText().toString();
        if(fromCityCode.length()>0){
            fromCityCode=fromCityCode.substring(fromCityCode.indexOf("(") + 1,
                    fromCityCode.indexOf(")"));}

        toCityCode=toNameTv.getText().toString();
        if(toCityCode.length()>0){
            toCityCode=toCityCode.substring(toCityCode.indexOf("(") + 1,
                    toCityCode.indexOf(")"));}
//       toNameTv.setText(toCityCode);
        setDates();
        initializeAnimations();
        setFont();
//        getStateCity(ApiConstants.STATELIST, "INDIA");

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });

        if(tripType.equals(ONE_WAY)){
            oneWayClicked();
        }else {
            roundTripClicked();
        }
        setClassType(classType);

        view.findViewById(R.id.passengerLin).setOnClickListener(this);
        view.findViewById(R.id.returnDateLin).setOnClickListener(this);
        view.findViewById(R.id.departDateLin).setOnClickListener(this);
        view.findViewById(R.id.searchRel).setOnClickListener(this);
        view.findViewById(R.id.oneWayLin).setOnClickListener(this);
        view.findViewById(R.id.roundTripLin).setOnClickListener(this);
        view.findViewById(R.id.toLayout).setOnClickListener(this);
        view.findViewById(R.id.fromLayout).setOnClickListener(this);
        view.findViewById(R.id.economyTv).setOnClickListener(this);
        view.findViewById(R.id.businessTv).setOnClickListener(this);
        view.findViewById(R.id.premiumTv).setOnClickListener(this);
        view.findViewById(R.id.firstClassTv).setOnClickListener(this);
        view.findViewById(R.id.flight_img).setOnClickListener(this);


        return view;
    }

    private void setClassType(String classType) {
        switch (classType){
            case ECONOMY:
                this.classType=ECONOMY;
                Common.preventFrequentClick(economyTv);
                objectAnimatorEconomy.start();
                objectAnimatorBusiness.end();
                objectAnimatorPremium.end();
                objectAnimatorFirst.end();
                businessTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                premiumTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                firstClassTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                economyTv.setTextColor(getResources().getColor(R.color.color_white));
                firstClassTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                businessTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                premiumTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                break;
            case BUSINESS:
                this.classType=BUSINESS;
                Common.preventFrequentClick(businessTv);
                objectAnimatorEconomy.end();
                objectAnimatorBusiness.start();
                objectAnimatorPremium.end();
                objectAnimatorFirst.end();
                economyTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
//                businessTv.setBackgroundResource(R.color.app_blue_color);
                premiumTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                firstClassTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                businessTv.setTextColor(getResources().getColor(R.color.color_white));
                economyTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                firstClassTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                premiumTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                break;
            case PREMIUM_ECONOMY:
                this.classType=PREMIUM_ECONOMY;
                Common.preventFrequentClick(premiumTv);
                objectAnimatorEconomy.end();
                objectAnimatorBusiness.end();
                objectAnimatorPremium.start();
                objectAnimatorFirst.end();

                economyTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                businessTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
//                premiumTv.setBackgroundResource(R.color.app_blue_color);
                firstClassTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                premiumTv.setTextColor(getResources().getColor(R.color.color_white));
                economyTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                businessTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                firstClassTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                break;
            case FIRST_CLASS:
                this.classType=FIRST_CLASS;
                Common.preventFrequentClick(firstClassTv);
                objectAnimatorEconomy.end();
                objectAnimatorBusiness.end();
                objectAnimatorPremium.end();
                objectAnimatorFirst.start();

                economyTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                businessTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                premiumTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
//                firstClassTv.setBackgroundResource(R.color.app_blue_color);
                firstClassTv.setTextColor(getResources().getColor(R.color.color_white));
                economyTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                businessTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                premiumTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                break;
            default:

        }
    }

    private void initializeAnimations() {
        objectAnimatorOneWay=ObjectAnimator.ofObject(oneWayLin, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorRound=ObjectAnimator.ofObject(roundTripLin, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorEconomy=ObjectAnimator.ofObject(economyTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorBusiness=ObjectAnimator.ofObject(businessTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorPremium=ObjectAnimator.ofObject(premiumTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorFirst=ObjectAnimator.ofObject(firstClassTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);

    }

    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.OpenSansRegularTypeFace(context);
        departDateTv.setTypeface(face2);
        returnDateTv.setTypeface(face2);
        oneWayTv.setTypeface(face1);
        fromNameTv.setTypeface(face);
        toNameTv.setTypeface(face);
        departLabel.setTypeface(face1);
        returnLabel.setTypeface(face1);
        economyTv.setTypeface(face1);
        businessTv.setTypeface(face1);
        premiumTv.setTypeface(face1);
        firstClassTv.setTypeface(face1);
        search_Tv.setTypeface(face1);
        fromTv.setTypeface(face1);
        toTv.setTypeface(face1);

    }

    public void didTapButton() {

        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        oneWayLin.startAnimation(myAnim);

    }

    private void initializeDates() {
        //Date formats
//        dateServerFormat = Common.getShowInTVDateFormat();
        dateServerFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        dateMonthFormat = new SimpleDateFormat("dd MMM", Locale.US);
        dayFormat = Common.getFullDayFormat();
        yearFormat = new SimpleDateFormat("yyyy", Locale.US);

        //default checkIn Date
        checkInDateCalendar = Calendar.getInstance();
        checkInDateDay = checkInDateCalendar.get(Calendar.DAY_OF_MONTH);
        checkInDateMonth = checkInDateCalendar.get(Calendar.MONTH);
        checkInDateYear = checkInDateCalendar.get(Calendar.YEAR);

        //default checkOut Date
        checkOutDateCalendar = Calendar.getInstance();
        checkOutDateCalendar.add(Calendar.DAY_OF_MONTH,1);
        checkOutDateDay = checkOutDateCalendar.get(Calendar.DAY_OF_MONTH);
        checkOutDateMonth = checkOutDateCalendar.get(Calendar.MONTH);
        checkOutDateYear = checkOutDateCalendar.get(Calendar.YEAR);

        checkInDate= dateServerFormat.format(checkInDateCalendar.getTime());
        checkOutDate= dateServerFormat.format(checkOutDateCalendar.getTime());

    }
    private void setDates() {
        //set default date
        departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
        returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
        returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
    }

    private void openCheckInDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        checkInDateCalendar.set(year, monthOfYear, dayOfMonth);
                        checkInDate= dateServerFormat.format(checkInDateCalendar.getTime());
                        checkInDateDay =dayOfMonth;
                        checkInDateMonth =monthOfYear;
                        checkInDateYear =year;
                        departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        setCheckOutDate();
                    }

                }, checkInDateYear, checkInDateMonth, checkInDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }


    private void openCheckOutDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        checkOutDateCalendar.set(year, monthOfYear, dayOfMonth);
                        checkOutDate= dateServerFormat.format(checkOutDateCalendar.getTime());
                        checkOutDateDay =dayOfMonth;
                        checkOutDateMonth =monthOfYear;
                        checkOutDateYear =year;
                        returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
                        returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
                        returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());

                    }

                }, checkOutDateYear, checkOutDateMonth, checkOutDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(checkInDateCalendar.getTimeInMillis()-1000);
        datePickerDialog.show();
    }

    private void setCheckOutDate() {
        if(checkInDateCalendar.after(checkOutDateCalendar)){
            checkOutDateCalendar.setTime(checkInDateCalendar.getTime());
            checkOutDateCalendar.add(Calendar.DAY_OF_MONTH,1);
            checkOutDate= dateServerFormat.format(checkOutDateCalendar.getTime());
            checkOutDateDay =checkOutDateCalendar.DAY_OF_MONTH;
            checkOutDateMonth =checkOutDateCalendar.MONTH;
            checkOutDateYear =checkOutDateCalendar.YEAR;
            returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
            returnDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
            returnYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        }
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }

    private void showCustomDialog() {
        MyflightDialog.showCustomDialog(context);
    }

    private void hideCustomDialog() {
        MyflightDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.oneWayLin:

//                returnLabel.setTextColor(getResources().getColor(R.color.flightSearchHintColor));
//                returnDateTv.setTextColor(getResources().getColor(R.color.flightSearchHintColor));
//                returnDayTv.setTextColor(getResources().getColor(R.color.flightSearchHintColor));
//                returnYearTv.setTextColor(getResources().getColor(R.color.flightSearchHintColor));
                oneWayClicked();
                break;
            case R.id.roundTripLin:
                roundTripClicked();
                break;

            case R.id.departDateLin:
                openCheckInDatePicker();
                break;

            case R.id.returnDateLin:
                returnDateLin.setAlpha(1);
                tripType = ROUND_TRIP;
//                returnLabel.setTextColor(getResources().getColor(R.color.flightSearchCityTitleColor));
//                returnDateTv.setTextColor(getResources().getColor(R.color.flightSearchDateColor));
//                returnDayTv.setTextColor(getResources().getColor(R.color.color_black_hint));
//                returnYearTv.setTextColor(getResources().getColor(R.color.flightSearchCityColor));
                roundTripTv.setTextColor(getResources().getColor(R.color.color_white));
                oneWayTv.setTextColor(getResources().getColor(R.color.app_blue_color));
                oneWayLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                ObjectAnimator.ofObject(roundTripLin, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4)  .setDuration(1000).start();
                openCheckOutDatePicker();
                break;

            case R.id.passengerLin:
                try{
//                    openRoomInfoDialog();
                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.searchRel:
                Common.preventFrequentClick(searchRel);
                final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);

                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);

                flight_img.startAnimation(myAnim);

                MyPreferences.setFlightAdult(context,NumberOfAdultTemp);
                MyPreferences.setFlightChild(context,NumberOfChildTemp);
                MyPreferences.setFlightInfant(context,NumberOfInfantsTemp);

//                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(new FlightConfirmFragment());

//                ObjectAnimator.ofObject(search_Tv, "backgroundColor", new ArgbEvaluator(),
//                         /*Red*/0xFFd53439, /*Blue*/0xFFd53439).setDuration(2000).start();
//                Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show();

                FlightSearchRequestModel requestModel=new FlightSearchRequestModel();
                requestModel.Adult=NumberOfAdult;
                requestModel.Child=NumberOfChild;
                requestModel.Infant=NumberOfInfants;
                requestModel.Class=classType;
                requestModel.DeviceId=Common.getDeviceId(context);
                requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                requestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);;
                requestModel.PAirLine="ANY";
                ArrayList<FlightSearchRequestModel.Sectors> sectorsArrayList=new ArrayList<>();
                FlightSearchRequestModel.Sectors sectors=(new FlightSearchRequestModel()).new Sectors();
                sectors.Date=checkInDate;
                sectors.Origin=fromCityCode;
                sectors.Dest=toCityCode;
                sectorsArrayList.add(sectors);

                if(tripType.equals(ROUND_TRIP)){
                    FlightSearchRequestModel.Sectors sectors2=(new FlightSearchRequestModel()).new Sectors();
                    sectors2.Date=checkOutDate;
                    sectors2.Origin=toCityCode;
                    sectors2.Dest=fromCityCode;
                    sectorsArrayList.add(sectors2);
                }

                requestModel.Sectors=sectorsArrayList;

                if(Common.checkInternetConnection(context)){
                    if(validate()){
                        searchFlight(requestModel);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.adultPlusImg:
                ObjectAnimator.ofObject(adultPlusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfAdultTemp+NumberOfChildTemp<MaxAdult){
                    NumberOfAdultTemp++;
//                    adultValueTv.setText(NumberOfAdultTemp+" Adult");
                    adultCountTv.setText(NumberOfAdultTemp+"");
                }else {

                }
                break;
            case R.id.adultMinusImg:
                ObjectAnimator.ofObject(adultMinusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfAdultTemp>1){
                    NumberOfAdultTemp--;
//                    adultValueTv.setText(NumberOfAdultTemp+" Adult");
                    adultCountTv.setText(NumberOfAdultTemp+"");
                    if(NumberOfInfantsTemp>NumberOfAdultTemp) {
                        NumberOfInfantsTemp=NumberOfAdultTemp;
                        infantCountTv.setText(NumberOfAdultTemp + "");
                    }
                }

                break;
            case R.id.childPlusImg:
                ObjectAnimator.ofObject(childPlusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfChildTemp+NumberOfAdultTemp<MaxChild){
                    NumberOfChildTemp++;
//                    childValueTv.setText(NumberOfChildTemp+" Child");
                    childCountTv.setText(NumberOfChildTemp+"");
                }else {
                }
                break;
            case R.id.childMinusImg:
                ObjectAnimator.ofObject(childMinusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfChildTemp>0){
                    NumberOfChildTemp--;
//                    childValueTv.setText(NumberOfChildTemp+" Adult");
                    childCountTv.setText(NumberOfChildTemp+"");
                }else {

                }
                break;
            case R.id.infantPlusImg:
                ObjectAnimator.ofObject(infantPlusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfInfantsTemp<NumberOfAdultTemp){
                    NumberOfInfantsTemp++;
//                    infantValueTv.setText(NumberOfInfantsTemp+" Infant");
                    infantCountTv.setText(NumberOfInfantsTemp+"");
                }else {

                }
                break;
            case R.id.infantMinusImg:
                ObjectAnimator.ofObject(infantMinusImg, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                if(NumberOfInfantsTemp>0){
                    NumberOfInfantsTemp--;
//                    infantValueTv.setText(NumberOfInfantsTemp+" Infant");
                    infantCountTv.setText(NumberOfInfantsTemp+"");
                }else {

                }
                break;

            case R.id.fromLayout:
//                String msg="Source City or Airport";
//                Intent i=new Intent(context,FlightFromCityActivity.class);
//                i.putExtra("msg", msg);
//                startActivityForResult(i,1);

                String msg="Source City";
                String key="1";
                Intent i=new Intent(context,MyFlightCityDialog.class);
                i.putExtra("msg", msg);
                i.putExtra("key", key);
                MyFlightCityDialog.showCustomDialog(context,i,key, this);

                break;

            case R.id.toLayout:
//                String msg1="Destination City or Airport";
//                Intent i1=new Intent(context,FlightFromCityActivity.class);
//                i1.putExtra("msg", msg1);
//                startActivityForResult(i1,2);

                String key1="2";
                String msg1="Destination City";
                Intent i1=new Intent(context,MyFlightCityDialog.class);
                i1.putExtra("msg", msg1);
                i1.putExtra("key", key1);
                MyFlightCityDialog.showCustomDialog(context,i1, key1, this);
                break;

            case R.id.economyTv:
                setClassType(ECONOMY);
                break;

            case R.id.businessTv:
                setClassType(BUSINESS);
                break;

            case R.id.premiumTv:
                setClassType(PREMIUM_ECONOMY);
                break;

            case R.id.firstClassTv:
                setClassType(FIRST_CLASS);
                break;
        }
    }

    private boolean validate() {
        if(fromNameTv.getText().length()==0) {
            Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_SHORT).show();
            return false;
        } else if(toNameTv.getText().length()==0) {
            Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void searchFlight(FlightSearchRequestModel requestModel) {

        String url="http://www.myhotelbox.com/mobile/FlightData.aspx?method=SEARCHFLIGHTJSON&value=ZXplZWliZSxJVEwwMDQ0MyxFWkVFSUJFIW9uZSxERUwsQk9NLDAzLzExLzIwMTcsLDIsMSwwLG9uZQ==";
        String data="ezeeibe,ITL00443,EZEEIBE!"+tripType+","+fromCityCode+","+
                toCityCode+","+checkInDate+","+checkOutDate+","+NumberOfAdult+","+NumberOfChild+","+
                NumberOfInfants+",Any";
        byte[] newvalue = data.getBytes();
//            Log.e("parameters :-",data+"\n");
        data = Base64.encodeToString(newvalue, Base64.DEFAULT);
        showCustomDialog();
        new NetworkCall().callAirService(requestModel, ApiConstants.AirAvailability, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 1);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            String IsIntl="D";
//            Toast.makeText(context,"Success", Toast.LENGTH_LONG).show();
            String data = new Gson().fromJson(response.string(), String.class);
            FlightSearchDataModel searchDataModels = new Gson().fromJson(data, FlightSearchDataModel.class);
            ArrayList<FlightSearchDataModel> dataModelArrayList = new ArrayList<>(Arrays.asList(searchDataModels));
//            ArrayList<FlightSearchRoundIntDataModel> dataModelArrayList2=new ArrayList<>();

            if(tripType.equals(ROUND_TRIP) && searchDataModels.RoundTrip!=null && searchDataModels.RoundTrip.size()>0 &&
                    dataModelArrayList.get(0).RoundTrip.get(0).IsIntl.equalsIgnoreCase("I")){
                IsIntl="I";
//                FlightSearchRoundIntDataModel searchDataModels2 = new Gson().fromJson(data, FlightSearchRoundIntDataModel.class);
//                dataModelArrayList2 = new ArrayList<>(Arrays.asList(searchDataModels2));
            }
            if(IsIntl.equals("I") && dataModelArrayList.get(0).RoundTrip!=null && dataModelArrayList.get(0).RoundTrip.size()>0){
                Bundle bundle = new Bundle();
                bundle.putSerializable("FlightList", dataModelArrayList);
                FlightListRoundIntlFragment fragment = new FlightListRoundIntlFragment();
                bundle.putString("fromCityCode", fromCityCode);
                bundle.putString("toCityCode", toCityCode);
                bundle.putString("checkInDate", checkInDate);
                bundle.putString("checkOutDate", checkOutDate);
                bundle.putString("noOfTraveller", String.valueOf(NumberOfAdultTemp + NumberOfChildTemp + NumberOfInfantsTemp + " TRAVELLER "));
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(fragment);
            }
            else if (IsIntl.equals("D") && tripType.equals(ONE_WAY) &&
                    dataModelArrayList.get(0).OneWay!=null && dataModelArrayList.get(0).OneWay.size()>0) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("FlightList", dataModelArrayList.get(0).OneWay);
                FlightListFragment fragment = new FlightListFragment();
                bundle.putString("fromCityCode", fromCityCode);
                bundle.putString("toCityCode", toCityCode);
                bundle.putString("checkInDate", checkInDate);
                bundle.putString("noOfTraveller", String.valueOf(NumberOfAdultTemp + NumberOfChildTemp + NumberOfInfantsTemp + " TRAVELLER "));
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(fragment);
            }else if (IsIntl.equals("D") && tripType.equals(ROUND_TRIP) &&
                    dataModelArrayList.get(0).TwoWay!=null && dataModelArrayList.get(0).TwoWay.size()>0){
                    ArrayList<FlightSearchDataModel.OneWay>  arrayListInBound=new ArrayList<>();
                    ArrayList<FlightSearchDataModel.OneWay>  arrayListOutBound=new ArrayList<>();
                    for(int i=0; i<dataModelArrayList.get(0).TwoWay.size(); i++){
                        if(dataModelArrayList.get(0).TwoWay.get(i).SCode.equalsIgnoreCase(fromCityCode)){
                            arrayListOutBound.add(dataModelArrayList.get(0).TwoWay.get(i));
                        }else {
                            arrayListInBound.add(dataModelArrayList.get(0).TwoWay.get(i));
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FlightList", dataModelArrayList);
                    bundle.putSerializable("arrayListOutBound", arrayListOutBound);
                    bundle.putSerializable("arrayListInBound", arrayListInBound);
                    bundle.putString("fromCityCode", fromCityCode);
                    bundle.putString("toCityCode", toCityCode);
                    bundle.putString("checkInDate", checkInDate);
                    bundle.putString("checkOutDate", checkOutDate);
                    bundle.putString("noOfTraveller", String.valueOf(NumberOfAdultTemp + NumberOfChildTemp + NumberOfInfantsTemp + " TRAVELLER "));
                    FlightListRoundFragment fragment = new FlightListRoundFragment();
                    fragment.setArguments(bundle);
                    ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(fragment);
            }else {
//                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideCustomDialog();
                }
            }, 1000);

        }catch (Exception e){
            hideCustomDialog();
            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && data!=null)
        {
            String message=data.getStringExtra("MESSAGE");
            String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            fromNameTv.setText(message1+" ("+message2+")");
            fromCityCode=message2;
            MyPreferences.setFlightFromCity(context,message1+" ("+message2+")");
//                fromCitytv.setText(message2);
        }

        if(requestCode==2 && data!=null)
        {
            String message=data.getStringExtra("MESSAGE");
            String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            toNameTv.setText(message1+" ("+message2+")");
            toCityCode=message2;
            MyPreferences.setFlightToCity(context,message1+" ("+message2+")");

//               toCitytv.setText(message2);
        }

    }


    private void oneWayClicked() {
        tripType = ONE_WAY;
        returnDateLin.setAlpha(0.2f);
        Common.preventFrequentClick(oneWayLin);
        objectAnimatorOneWay.start();
        objectAnimatorRound.end();
        roundTripTv.setTextColor(getResources().getColor(R.color.app_blue_color));
        oneWayTv.setTextColor(getResources().getColor(R.color.color_white));
//                oneWayLin.setBackgroundResource(R.color.app_blue_color);
        roundTripLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);

    }

    private void roundTripClicked() {
        tripType = ROUND_TRIP;
        tripType = ROUND_TRIP;
        returnDateLin.setAlpha(1);
        Common.preventFrequentClick(roundTripLin);
        objectAnimatorOneWay.end();
        objectAnimatorRound.start();

        roundTripTv.setTextColor(getResources().getColor(R.color.color_white));
        oneWayTv.setTextColor(getResources().getColor(R.color.app_blue_color));
//                roundTripLin.setBackgroundResource(R.color.app_blue_color);
        oneWayLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
    }
    private void openRoomInfoDialog() throws Exception{
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.air_passenger_detail_dialog);
        dialog.setCancelable(false);
        Window window= dialog.getWindow();
        if(window!=null){
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);}


        adultValueTv= (TextView) dialog.findViewById(R.id.adultValueTv);
        adultCountTv= (TextView) dialog.findViewById(R.id.adultCountTv);
        childValueTv= (TextView) dialog.findViewById(R.id.childValueTv);
        childCountTv= (TextView) dialog.findViewById(R.id.childCountTv);
        infantValueTv= (TextView) dialog.findViewById(R.id.infantValueTv);
        infantCountTv= (TextView) dialog.findViewById(R.id.infantCountTv);

        dialog.findViewById(R.id.adultMinusImg).setOnClickListener(this);
        dialog.findViewById(R.id.adultPlusImg).setOnClickListener(this);
        dialog.findViewById(R.id.childPlusImg).setOnClickListener(this);
        dialog.findViewById(R.id.childMinusImg).setOnClickListener(this);
        dialog.findViewById(R.id.infantMinusImg).setOnClickListener(this);
        dialog.findViewById(R.id.infantPlusImg).setOnClickListener(this);


        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                setPassValues();
            }
        });
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                NumberOfAdult=NumberOfAdultTemp;
                NumberOfChild=NumberOfChildTemp;
                NumberOfInfants=NumberOfInfantsTemp;
                setPassValues();
            }
        });

        setDefaultDialogValues();

        dialog.show();
    }

    private void setDefaultDialogValues() {
        NumberOfAdultTemp=NumberOfAdult;
        NumberOfChildTemp=NumberOfChild;
        NumberOfInfantsTemp=NumberOfInfants;

        adultValueTv.setText(NumberOfAdult+ " Adult");
        adultCountTv.setText(NumberOfAdult+ "");
        childValueTv.setText(NumberOfChild+ " Child");
        childCountTv.setText(NumberOfChild+ "");
        infantValueTv.setText(NumberOfInfants+ " Infant");
        infantCountTv.setText(NumberOfInfants+ "");
    }

    private void setPassValues() {
//        adultTv.setText(NumberOfAdult+ " Adult");
//        childTv.setText(NumberOfChild+ " Child");
//        infantTv.setText(NumberOfInfants+ " Infant");
    }

    @Override
    public void onResume() {
        super.onResume();
        adultCountTv.setText(NumberOfAdultTemp+"");
        childCountTv.setText(NumberOfChildTemp+"");
        infantCountTv.setText(NumberOfInfantsTemp+"");
        setDates();
    }

    @Override
    public void setResult(int requestCode,Intent data) {

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && data!=null)
        {
//            String message=data.getStringExtra("MESSAGE");
////                String  message1=message.substring(0,message.indexOf(" "));
//            String  message2=message.substring(message.indexOf("(") + 1,
//                    message.indexOf(")"));
//            fromNameTv.setText(message+"");
//            fromCityCode=message2;
//            fromCity=message.substring( 0, message.indexOf("("));
//            MyPreferences.setBusFromCity(context,message+" ");
//                fromCitytv.setText(message2);
            String message=data.getStringExtra("MESSAGE");
            String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            fromNameTv.setText(message1+" ("+message2+")");
            fromCityCode=message2;
            MyPreferences.setFlightFromCity(context,message1+" ("+message2+")");
        }

        if(requestCode==2 && data!=null)
        {
//            String message=data.getStringExtra("MESSAGE");
////                String  message1=message.substring(0,message.indexOf(" "));
//            String  message2=message.substring(message.indexOf("(") + 1,
//                    message.indexOf(")"));
//            toNameTv.setText(message+"");
//            toCityCode=message2;
//            toCity=message.substring( 0, message.indexOf("("));;
//            MyPreferences.setBusToCity(context,message+" ");

            String message=data.getStringExtra("MESSAGE");
            String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            toNameTv.setText(message1+" ("+message2+")");
            toCityCode=message2;
            MyPreferences.setFlightToCity(context,message1+" ("+message2+")");
        }

    }

}