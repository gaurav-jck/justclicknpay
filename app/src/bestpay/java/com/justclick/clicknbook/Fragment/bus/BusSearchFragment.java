package com.justclick.clicknbook.Fragment.bus;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyBounceInterpolator;
import com.justclick.clicknbook.utils.MyBusDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.MybusCityDialog;
//import com.stacktips.view.utils.CalendarUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;


public class BusSearchFragment extends Fragment implements View.OnClickListener,MybusCityDialog.OnCityDialogResult {
    private final String ONE_WAY="ONE", ROUND_TRIP="ROUND";
    private String tripType=ONE_WAY;
    private Context context;
    private LoginModel loginModel;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private TextView departDateTv, returnDateTv,search_Tv, roundTripTv, oneWayTv,
            fromTv,toTv, returnYearTv, returnDayTv, departDayTv, departYearTv;
    private TextView departLabel,returnLabel,
            returnDateLabelTv,fromNameTv,fromCitytv,toNameTv,toCitytv;
    private ImageView flight_img;
    private LinearLayout fromLayout,toLayout,oneWayLin,roundTripLin;
    private RelativeLayout returnDateLin,searchRel;
    private String checkInDate="", checkOutDate="", fromCityCode="", toCityCode="",fromCity="",toCity="";
    private SimpleDateFormat dateServerFormat, dateMonthFormat, dayFormat, yearFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear,
            checkOutDateDay, checkOutDateMonth, checkOutDateYear;
    private Calendar checkInDateCalendar, checkOutDateCalendar, maxSearchCalender;
    private ObjectAnimator objectAnimatorOneWay, objectAnimatorRound;
    private AlertDialog flightSearchDialog;
    private int selectedDate,selectedMonth,selectedYear;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        initializeDates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bus_search_new, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.busSearchFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
        departDateTv= view.findViewById(R.id.departDateTv);
        returnDateTv=  view.findViewById(R.id.returnDateTv);
        search_Tv=  view.findViewById(R.id.search_Tv);
        fromNameTv= view.findViewById(R.id.fromNameTv);
        roundTripTv =  view.findViewById(R.id.roundTripTv);
        oneWayTv =  view.findViewById(R.id.oneWayTv);
        departLabel=  view.findViewById(R.id.departLabel);
        returnLabel=  view.findViewById(R.id.returnLabel);
        fromTv=  view.findViewById(R.id.fromTv);
        toTv=  view.findViewById(R.id.toTv);
        returnYearTv=  view.findViewById(R.id.returnYearTv);
        returnDayTv=  view.findViewById(R.id.returnDayTv);
        departDayTv=  view.findViewById(R.id.departDayTv);
        departYearTv=  view.findViewById(R.id.departYearTv);


//        fromCitytv=  view.findViewById(R.id.fromCitytv);
        toNameTv=  view.findViewById(R.id.toNameTv);
//        toCitytv= view.findViewById(R.id.toCitytv);

        returnDateLin= view.findViewById(R.id.returnDateLin);
        searchRel=  view.findViewById(R.id.searchRel);
        flight_img=  view.findViewById(R.id.flight_img);

        oneWayLin=  view.findViewById(R.id.oneWayLin);
        roundTripLin=  view.findViewById(R.id.roundTripLin);

//        setDefaultDialogValues();

//        fromLayout=  view.findViewById(R.id.fromLayout);
//        toLayout=  view.findViewById(R.id.toLayout);

//        classSpinner=  view.findViewById(R.id.classSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.flight_class_spinner_item, R.id.operator_tv, getResources().
                getStringArray(R.array.airClass));
        adapter.setDropDownViewResource(R.layout.flight_class_spinner_item);

        if(MyPreferences.getBusFromCity(context).length()>0){
            fromCityCode=MyPreferences.getBusFromCity(context).substring(MyPreferences.getBusFromCity(context).indexOf("(") + 1,
                    MyPreferences.getBusFromCity(context).indexOf(")"));
            fromCity=MyPreferences.getBusFromCity(context).substring( 0,
                    MyPreferences.getBusFromCity(context).indexOf("("));
        }

        if(MyPreferences.getBusToCity(context).length()>0){
            toCityCode=MyPreferences.getBusToCity(context).substring(MyPreferences.getBusToCity(context).indexOf("(") + 1,
                    MyPreferences.getBusToCity(context).indexOf(")"));
            toCity=MyPreferences.getBusToCity(context).substring( 0,
                    MyPreferences.getBusToCity(context).indexOf("("));
        }
        fromNameTv.setText(fromCity);
        toNameTv.setText(toCity);
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

        view.findViewById(R.id.returnDateLin).setOnClickListener(this);
        view.findViewById(R.id.departDateLin).setOnClickListener(this);
        view.findViewById(R.id.searchRel).setOnClickListener(this);
        view.findViewById(R.id.oneWayLin).setOnClickListener(this);
        view.findViewById(R.id.roundTripLin).setOnClickListener(this);
        view.findViewById(R.id.toLayout).setOnClickListener(this);
        view.findViewById(R.id.fromLayout).setOnClickListener(this);
        view.findViewById(R.id.flight_img).setOnClickListener(this);
        oneWayClicked();
        return view;
    }

    private void initializeAnimations() {
        objectAnimatorOneWay=ObjectAnimator.ofObject(oneWayLin, "backgroundColor", new ArgbEvaluator(),
                /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorRound=ObjectAnimator.ofObject(roundTripLin, "backgroundColor", new ArgbEvaluator(),
                /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);

    }

    private void setFont(){
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.FlightCalenderTypeFace3(context);
        departDateTv.setTypeface(face2);
        returnDateTv.setTypeface(face2);
        oneWayTv.setTypeface(face1);
        fromNameTv.setTypeface(face);
        toNameTv.setTypeface(face);
        departLabel.setTypeface(face1);
        returnLabel.setTypeface(face1);
        search_Tv.setTypeface(face1);
        fromTv.setTypeface(face1);
        toTv.setTypeface(face1);


    }

    private void initializeDates() {
        //Date formats
        dateServerFormat = Common.getServerDateFormat();
//            dateServerFormat = new SimpleDateFormat("ddMMMyyyy", Locale.US);
        dateMonthFormat = new SimpleDateFormat("dd MMM", Locale.US);
        dayFormat = Common.getFullDayFormat();
        yearFormat = new SimpleDateFormat("yyyy", Locale.US);

        maxSearchCalender=Calendar.getInstance();
        maxSearchCalender.add(Calendar.MONTH,6);

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

    private void openStartDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        checkInDateCalendar.set(year, monthOfYear, dayOfMonth);

                        /*if(CalendarUtils.isPastDay(checkInDateCalendar.getTime())){
                            Toast.makeText(context, "Can't search bus before current date", Toast.LENGTH_SHORT).show();
                        }else */if (checkInDateCalendar.after(maxSearchCalender)) {
                            Toast.makeText(context, "Can't search bus after six months", Toast.LENGTH_SHORT).show();
                        }else {
                            checkInDateDay=dayOfMonth;
                            checkInDateMonth=monthOfYear;
                            checkInDateYear=year;

                            departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()));
                            departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()));
                            departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());

                            checkInDate=dateServerFormat.format(checkInDateCalendar.getTime());
                            setCheckOutDate();
                        }

                    }

                },checkInDateYear, checkInDateMonth, checkInDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();


    }

    private void openCheckInDatePicker() {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        checkInDateCalendar.set(year, monthOfYear, dayOfMonth);
                        checkInDate = dateServerFormat.format(checkInDateCalendar.getTime());
                        checkInDateDay = dayOfMonth;
                        checkInDateMonth = monthOfYear;
                        checkInDateYear = year;
                        departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
//                            datePickerDialog.dismiss();

                        setCheckOutDate();
                    }

                }, checkInDateYear, checkInDateMonth, checkInDateDay);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 6);
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
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
            returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
            returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
        }
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }

    private void showCustomDialog() {
        MyBusDialog.showCustomDialog(context);
//            MyflightDialog.showCustomDialog(context);
    }

    private void hideCustomDialog() {
        MyBusDialog.hideCustomDialog();
//            MyflightDialog.hideCustomDialog();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.oneWayLin:
                oneWayClicked();
                break;
            case R.id.roundTripLin:
                Toast.makeText(context,"Not Available",Toast.LENGTH_SHORT).show();
//                    roundTripClicked();
                break;

            case R.id.departDateLin:

               /* MyCalendarDialog.showCustomDialog(context, new MyCalendarDialog.OnDateSelectListener(){

                    @Override
                    public void onDateSelect(Date date) {
//                            departDateTv.setText(dateMonthFormat.format(date.getTime()).toUpperCase());
//                            departDayTv.setText(dayFormat.format(date.getTime()).toUpperCase());
//                            departYearTv.setText(yearFormat.format(date.getTime()).toUpperCase());

                        checkInDateCalendar.setTime(date);
                        checkInDate = dateServerFormat.format(checkInDateCalendar.getTime());
                        checkInDateDay = checkInDateCalendar.DAY_OF_MONTH;
                        checkInDateMonth = checkInDateCalendar.MONTH;
                        checkInDateYear = checkInDateCalendar.YEAR;
                        departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
                        departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
//                            datePickerDialog.dismiss();
                        setCheckOutDate();
                    }
                });*/
                openStartDatePicker();

//                    openCheckInDatePicker();

                break;

            case R.id.returnDateLin:
                Toast.makeText(context,"Not Available",Toast.LENGTH_SHORT).show();
//                    returnDateLin.setAlpha(1);
//                    roundTripTv.setTextColor(getResources().getColor(R.color.color_white));
//                    oneWayTv.setTextColor(getResources().getColor(R.color.app_blue_color));
//                    oneWayLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
//                    ObjectAnimator.ofObject(roundTripLin, "backgroundColor", new ArgbEvaluator(),
//                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4)  .setDuration(1000).start();
////                    openCheckOutDatePicker();
//
//                    MyCalendarDialog.showCustomDialog(context, new MyCalendarDialog.OnDateSelectListener(){
//
//                        @Override
//                        public void onDateSelect(Date date) {
//
//                            checkOutDateCalendar.setTime(date);
//                            checkOutDate = dateServerFormat.format(checkOutDateCalendar.getTime());
//                            checkOutDateDay = checkInDateCalendar.DAY_OF_MONTH;
//                            checkOutDateMonth = checkInDateCalendar.MONTH;
//                            checkOutDateYear = checkInDateCalendar.YEAR;
//                            returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
//                            returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
//                            returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
////                            datePickerDialog.dismiss();
//                            setCheckOutDate();
//                        }
//                    });
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

                search_Tv.startAnimation(myAnim);

                if(Common.checkInternetConnection(context)){
                    if(validate()){
                        BusSearchRequestModel searchRequestModel=new BusSearchRequestModel();
//                            searchRequestModel.Destination="1322";
                        searchRequestModel.Destination=toCityCode;
                        searchRequestModel.DeviceId=Common.getDeviceId(context);
//                            searchRequestModel.Doj="20171225";
                        searchRequestModel.Doj=checkInDate;
                        searchRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                        searchRequestModel.Key="";
                        searchRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
//                            searchRequestModel.Source="1492";
                        searchRequestModel.Source=fromCityCode;
                        searchBus(searchRequestModel);
//                            makeDummyDataForSearch();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.fromLayout:
                String msg="Source City";
                String key="1";
                Intent i=new Intent(context,MybusCityDialog.class);
                i.putExtra("msg", msg);
                i.putExtra("key", key);
                MybusCityDialog.showCustomDialog(context,i,key, this);
//                    startActivityForResult(i,1);
                break;

            case R.id.toLayout:
                String key1="2";
                String msg1="Destination City";
                Intent i1=new Intent(context,MybusCityDialog.class);
                i1.putExtra("msg", msg1);
                i1.putExtra("key", key1);
                MybusCityDialog.showCustomDialog(context,i1, key1, this);
//                    startActivityForResult(i1,2);
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

    private void makeDummyDataForSearch() {
        ArrayList<BusSearchBean> arrayList=new ArrayList<>();
        for(int i=0; i<10; i++){
            BusSearchBean model=new BusSearchBean();
            model.bustype=("Volvo A/C Seater(UPSRTC)");
            model.travelName=("Uttar Pradesh State Road Transport Corp.");
            model.arrivalTime=("05:30 PM");
            model.departureTime=("01:00 PM");
            model.totalSeats=("21 Seat left");
            model.totalTime=("5h 30m");
            arrayList.add(model);
        }
        BusListFragment fragment=new BusListFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("BusList",arrayList);
        bundle.putString("fromCityCode", fromCityCode);
        bundle.putString("toCityCode", toCityCode);
        bundle.putString("checkInDate", checkInDate);
        bundle.putString("checkOutDate", checkOutDate);
        bundle.putString("tripType", tripType);
        fragment.setArguments(bundle);
        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
    }

    private void searchBus(Object requestModel) {
        showCustomDialog();
        new NetworkCall().callBusService(requestModel, ApiConstants.getAvailableTrips, context,
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

    public ArrayList<BusSearchBean> parseBusSearchList(String responsee) throws Exception {
        ArrayList<BusSearchBean> busSearchBeanArrayList=new ArrayList<>();
        JSONObject jsonObject = new JSONObject(responsee);
        JSONArray jsonArray = jsonObject.getJSONArray("availableTrips");
        BusSearchBean busSearchBean;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            busSearchBean = new BusSearchBean();
            busSearchBean.bustype=jsonObject1.getString("busType");
            busSearchBean.routid=(jsonObject1.getString("id"));
            busSearchBean.arrivalTime=(jsonObject1.getString("arrivalTime"));
            busSearchBean.totalSeats=(jsonObject1.getString("availableSeats"));
            busSearchBean.seatCategory=(jsonObject1.getString("busType"));
            busSearchBean.departureTime=(jsonObject1.getString("departureTime"));
            busSearchBean.chargebus=(jsonObject1.getString("fares"));
            busSearchBean.travelName=(jsonObject1.getString("travels"));
            busSearchBean.Operator=(jsonObject1.getString("Operator"));
//            busSearchBean.totalTime=(jsonObject1.getString("totalTime"));
            busSearchBean.doj=(jsonObject1.getString("doj"));
            busSearchBean.partialCancellationAllowed=(jsonObject1.getString("partialCancellationAllowed"));
            busSearchBean.cancellationPolicy=(jsonObject1.getString("cancellationPolicy"));

//                Boarding time array
            JSONArray boardingTimeArray=jsonObject1.getJSONArray("boardingTimes");
            BusSearchBean.BusBoardingDroppingTimeBean busBoardingTimeBean;
            ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> busBoardingTimeBeanArrayList=new ArrayList<>();
            for(int j=0;j<boardingTimeArray.length();j++) {
                JSONObject obj=boardingTimeArray.getJSONObject(j);
                busBoardingTimeBean = busSearchBean.new BusBoardingDroppingTimeBean();
                busBoardingTimeBean.bpName =(obj.getString("bpName"));
                busBoardingTimeBean.address=(obj.getString("address"));
                busBoardingTimeBean.bpId =(obj.getString("bpId"));
                busBoardingTimeBean.time=(obj.getString("time"));
                busBoardingTimeBean.contactNumber=(obj.getString("contactNumber"));
                busBoardingTimeBean.landmark=(obj.getString("landmark"));
                busBoardingTimeBean.location=(obj.getString("location"));
                busBoardingTimeBean.prime=(obj.getString("prime"));
                busBoardingTimeBeanArrayList.add(busBoardingTimeBean);
            }
            busSearchBean.boardingTimes =(busBoardingTimeBeanArrayList);

            //                Dropping time array
            JSONArray droppingTimeArray=jsonObject1.getJSONArray("droppingTimes");
            BusSearchBean.BusBoardingDroppingTimeBean busDroppingTimeBean;
            ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> busDroppingTimeBeanArrayList=new ArrayList<>();
            for(int j=0;j<droppingTimeArray.length();j++) {
                JSONObject obj=droppingTimeArray.getJSONObject(j);
                busDroppingTimeBean = busSearchBean.new BusBoardingDroppingTimeBean();
                busDroppingTimeBean.bpName =(obj.getString("bpName"));
                busDroppingTimeBean.address=(obj.getString("address"));
                busDroppingTimeBean.bpId =(obj.getString("bpId"));
                busDroppingTimeBean.time=(obj.getString("time"));
                busDroppingTimeBean.time=(obj.getString("time"));
                busDroppingTimeBean.contactNumber=(obj.getString("contactNumber"));
                busDroppingTimeBean.landmark=(obj.getString("landmark"));
                busDroppingTimeBean.location=(obj.getString("location"));
                busDroppingTimeBean.prime=(obj.getString("prime"));
                busDroppingTimeBeanArrayList.add(busDroppingTimeBean);
            }
            busSearchBean.droppingTimes =(busDroppingTimeBeanArrayList);

            //                fare details
            JSONArray fareDetailsArray=jsonObject1.getJSONArray("fareDetails");
            BusSearchBean.fareDetails fareDetailsModel;
            ArrayList<BusSearchBean.fareDetails> busFareDetailsArrayList=new ArrayList<>();
            for(int j=0;j<fareDetailsArray.length();j++) {
                JSONObject obj=fareDetailsArray.getJSONObject(j);
                fareDetailsModel = busSearchBean.new fareDetails();
                fareDetailsModel.totalFare =(obj.getInt("totalFare"));
                fareDetailsModel.AgMarkup =(obj.getInt("AgMarkup"));
                fareDetailsModel.Commission =(obj.getInt("Commission"));
                fareDetailsModel.GST =(obj.getInt("GST"));
                fareDetailsModel.GrossFare =(obj.getInt("GrossFare"));
                fareDetailsModel.ServiceCharge =(obj.getInt("ServiceCharge"));
                fareDetailsModel.TDS =(obj.getInt("TDS"));
                fareDetailsModel.bankTrexAmt =(obj.getInt("bankTrexAmt"));
                fareDetailsModel.baseFare =(obj.getInt("baseFare"));
                fareDetailsModel.bookingFee =(obj.getInt("bookingFee"));
                fareDetailsModel.childFare =(obj.getInt("childFare"));
                fareDetailsModel.levyFare =(obj.getInt("levyFare"));
                fareDetailsModel.markupFareAbsolute =(obj.getInt("markupFareAbsolute"));
                fareDetailsModel.markupFarePercentage =(obj.getInt("markupFarePercentage"));
                fareDetailsModel.operatorServiceChargeAbsolute =(obj.getInt("operatorServiceChargeAbsolute"));
                fareDetailsModel.operatorServiceChargePercentage =(obj.getInt("operatorServiceChargePercentage"));
                fareDetailsModel.serviceTaxAbsolute =(obj.getInt("serviceTaxAbsolute"));
                fareDetailsModel.serviceTaxPercentage =(obj.getInt("serviceTaxPercentage"));
                fareDetailsModel.srtFee =(obj.getInt("srtFee"));
                fareDetailsModel.tollFee =(obj.getInt("tollFee"));
                busFareDetailsArrayList.add(fareDetailsModel);
            }
            busSearchBean.fareDetails=(busFareDetailsArrayList);

            busSearchBeanArrayList.add(busSearchBean);
        }
        return busSearchBeanArrayList;
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {

            ArrayList<BusSearchBean> arrayList=parseBusSearchList(response.string());
            if(arrayList==null || arrayList.size()==0){
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }else {
                BusListFragment fragment=new BusListFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable("BusList",arrayList);
                bundle.putString("fromCityCode", fromCityCode);
                bundle.putString("toCityCode", toCityCode);
                bundle.putString("toCity", toCity);
                bundle.putString("fromCity", fromCity);
                bundle.putString("checkInDate", checkInDate);
                bundle.putString("checkOutDate", checkOutDate);
                bundle.putString("tripType", tripType);
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideCustomDialog();
                    }
                }, 1000);
            }

        }catch (Exception e){
            hideCustomDialog();
            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        setDates();
    }

    @Override
    public void setResult(int requestCode,Intent data) {

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && data!=null)
        {
            String message=data.getStringExtra("MESSAGE");
//                String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            fromCityCode=message2;
            fromCity=message.substring( 0, message.indexOf("("));
            MyPreferences.setBusFromCity(context,message+" ");
            fromNameTv.setText(fromCity);
//                fromCitytv.setText(message2);
        }

        if(requestCode==2 && data!=null)
        {
            String message=data.getStringExtra("MESSAGE");
//                String  message1=message.substring(0,message.indexOf(" "));
            String  message2=message.substring(message.indexOf("(") + 1,
                    message.indexOf(")"));
            toCityCode=message2;
            toCity=message.substring( 0, message.indexOf("("));;
            MyPreferences.setBusToCity(context,message+" ");
            toNameTv.setText(toCity);
//               toCitytv.setText(message2);
        }
    }
};
