package com.justclick.clicknbook.Fragment.flights.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.HomeFragment;
import com.justclick.clicknbook.Fragment.flights.MyCustomDialog;
import com.justclick.clicknbook.Fragment.flights.requestModels.FareQuotesRequestModel;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightBookingRequest;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.CoPassengerListDataModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FareQuotesResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FareRulesResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightBookingResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.Constants;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;
import com.justclick.clicknbook.utils.DecodeLoginToken;
import com.justclick.clicknbook.utils.MyCustomWaitingDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.SomethingWrongDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;


public class TravellersDetails extends Fragment implements View.OnClickListener {
    private final String ADULT="ADULT", CHILD="CHILD", INFANT="INFANT";
    private final int FARE_QUOTES=1, FARE_RULES=2;
    private Context context;
    private View view;
    int adultCount, childCount, infantCount, totalPassengers;
    private ArrayList<FlightBookingRequest.passengers> passengersArrayList;
    private FlightBookingRequest flightBookingRequest;
    private TextView totalPriceTv, proceedTv;
//    flight details view
    private TextView departCityTv, arriveCityTv, departTimeTv, departCityCodeTv,
        durationTv, arriveTimeTv, arriveCityCodeTv, dateDetailTv, cancellationTv, baggageTv,loginTv, guestTv;
    private String sessionId, FlightId;
    private float totalFare;
    private EditText et_email, et_mobile, gstNoEdt, gstCompanyNameEdit, gstEmailEdit, gstMobileEdit;
    private LinearLayout passengerContainerLin, gstLayout, loginLin;
    private RelativeLayout gstCheckBoxRel;
    private ImageView getCoPassengersImg;
    private CheckBox gstCheckbox;
    private boolean isGST, isFareQuote;
    private int currentPassengerPosition=0;

    private FlightSearchRequestModel flightSearchRequestModel;
    private FlightSearchResponseModel.response.flights flightResponse, flightResponseReturn;
    private FareQuotesRequestModel fareQuotesRequestModel;
    private String[] paxType;

    private int checkInDateDay, checkInDateMonth, checkInDateYear,checkDOBDateDay, checkDOBDateMonth, checkDOBDateYear;
    private Calendar currentDateCalender, checkInDateCalendar, adultMinDate, childMinDate, infantMinDate ,calendar;;
    private SimpleDateFormat dateServerFormat,dateToSetFormat;
    private boolean isLoad;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        isLoad=true;

        flightBookingRequest=new FlightBookingRequest();
        passengersArrayList=new ArrayList<>();
        setFlightSearchData();
        if(getArguments()!=null) {
            flightResponse = (FlightSearchResponseModel.response.flights) getArguments().getSerializable("FlightModel");
            FlightId = getArguments().getString("FlightId");
            flightResponseReturn = (FlightSearchResponseModel.response.flights) getArguments().getSerializable("FlightModelReturn");
            fareQuotesRequestModel = (FareQuotesRequestModel) getArguments().getSerializable("FareQuotesRequestModel");
            totalFare = getArguments().getFloat("totalFare");
            sessionId=fareQuotesRequestModel.sessionId;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_travellers_details, container, false);
            // init all views
            initViews();
            proceedTv.setOnClickListener(this);

            view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().popBackStack();
                }
            });
        }
        if(isLoad){
            isLoad=false;
            // add dynamic passengers
            passengersArrayList.clear();
            addPassengers();
//        set flight details
            setFlightDetails();
        }

        return view;
    }

    private void setFlightDetails() {
        String depTime=flightResponse.segments.get(0).get(0).departureTime;
        String arrTime=flightResponse.segments.get(flightResponse.segments.size()-1).
                get(flightResponse.segments.get(flightResponse.segments.size()-1).size()-1).arrivalTime;
        String days;
        if(DateAndTimeUtils.getDaysBetweenTwoDates(arrTime,depTime)>0){
            days="+"+DateAndTimeUtils.getDaysBetweenTwoDates(arrTime,depTime)+" Day | ";
        }else {
            days="";
        }
        departCityTv.setText(flightSearchRequestModel.fromCity);
        arriveCityTv.setText(flightSearchRequestModel.toCity);
        departTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(depTime));
        departCityCodeTv.setText(flightSearchRequestModel.segments.get(0).origin);
        durationTv.setText(days+DateAndTimeUtils.getDurationBetweenTwoDates(arrTime, depTime));
        arriveTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(arrTime));
        arriveCityCodeTv.setText(flightSearchRequestModel.segments.get(flightSearchRequestModel.segments.size()-1).destination);
        dateDetailTv.setText(DateAndTimeUtils.getDayDDMMMYYDate(depTime)+"'"+ DateAndTimeUtils.getYear(depTime)+" | "+
        totalPassengers+" Passenger(s)");

    }

    private void setFlightSearchData() {
        try {
            flightSearchRequestModel= MyPreferences.getFlightSearchRequestData(context);
            adultCount = flightSearchRequestModel.adultCount;
            childCount = flightSearchRequestModel.childCount;
            infantCount = flightSearchRequestModel.infantCount;
            totalPassengers=adultCount+childCount+infantCount;
            paxType=new String[totalPassengers];
        }catch (Exception e){

        }
    }


    // init all view
    private void initViews() {

        departCityTv=view.findViewById(R.id.departCityTv);
        arriveCityTv=view.findViewById(R.id.arriveCityTv);
        departTimeTv=view.findViewById(R.id.departTimeTv);
        departCityCodeTv=view.findViewById(R.id.departCityCodeTv);
        durationTv=view.findViewById(R.id.durationTv);
        arriveTimeTv=view.findViewById(R.id.arriveTimeTv);
        arriveCityCodeTv=view.findViewById(R.id.arriveCityCodeTv);
        dateDetailTv=view.findViewById(R.id.dateDetailTv);
        cancellationTv=view.findViewById(R.id.cancellationTv);
        baggageTv=view.findViewById(R.id.baggageTv);

        passengerContainerLin=view.findViewById(R.id.passengerContainerLin);
        gstLayout=view.findViewById(R.id.gstLayout);
        gstCheckBoxRel=view.findViewById(R.id.gstCheckBoxRel);
        gstCheckbox=view.findViewById(R.id.gstCheckbox);
        et_email = view.findViewById(R.id.et_email);
        et_mobile = view.findViewById(R.id.et_mobile);

        gstNoEdt = view.findViewById(R.id.gstNoEdt);
        gstCompanyNameEdit = view.findViewById(R.id.gstCompanyNameEdit);
        gstEmailEdit = view.findViewById(R.id.gstEmailEdit);
        gstMobileEdit = view.findViewById(R.id.gstMobileEdit);
        loginLin = view.findViewById(R.id.loginLin);
        loginTv = view.findViewById(R.id.loginTv);
        guestTv = view.findViewById(R.id.guestTv);
        getCoPassengersImg = view.findViewById(R.id.getCoPassengersImg);
        getCoPassengersImg.setOnClickListener(this);
        loginTv.setOnClickListener(this);
//        spinner_passenger = view.findViewById(R.id.spinner_passenger);
        proceedTv = view.findViewById(R.id.proceedTv);
        totalPriceTv = view.findViewById(R.id.totalPriceTv);
        totalPriceTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+totalFare);

        cancellationTv.setOnClickListener(this);
        baggageTv.setOnClickListener(this);

        if(flightResponse.isGSTMandatory){
            gstLayout.setVisibility(View.VISIBLE);
            gstCheckBoxRel.setVisibility(View.VISIBLE);
        }else {
            gstLayout.setVisibility(View.GONE);
            gstCheckBoxRel.setVisibility(View.GONE);
        }

        gstCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if(check){
                    gstLayout.setVisibility(View.VISIBLE);
                    isGST=true;
                    gstNoEdt.requestFocus();
                }else {
                    gstLayout.setVisibility(View.GONE);
                    isGST=false;
                }
            }
        });

    }

    private void addPassengers() {
        setDefaultCalender();
        int pos=0;
        for(int i=0; i<adultCount; i++, pos++){
            FlightBookingRequest.passengers passengers=flightBookingRequest.new passengers();
            passengersArrayList.add(passengers);
            addTraveller(ADULT+" "+(i+1), pos);
            paxType[pos]=ADULT;
        }
        for(int i=0; i<childCount; i++, pos++){
            FlightBookingRequest.passengers passengers=flightBookingRequest.new passengers();
            passengersArrayList.add(passengers);
            addTraveller(CHILD+" "+(i+1), pos);
            paxType[pos]=CHILD;
        }
        for(int i=0; i<infantCount; i++, pos++){
            FlightBookingRequest.passengers passengers=flightBookingRequest.new passengers();
            passengersArrayList.add(passengers);
            addTraveller(INFANT+" "+(i+1), pos);
            paxType[pos]=INFANT;
        }
        showCurrentPassenger(0);
    }

    // valid mobile and email
    private boolean validMobileEmail(){
        String str_email = et_email.getText().toString();
        if (TextUtils.isEmpty(str_email)) {
            et_email.setError("Please enter email");
            et_email.requestFocus();
            return false;
        } else if (!Common.isEmailValid(str_email)) {
            et_email.setError("Please enter valid email");
            et_email.requestFocus();
            return false;
        }

        if(et_mobile.getText().toString().length()<10){
            et_mobile.setError("Please enter 10 digit mobile no.");
            et_mobile.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validPassengersDetails(){
        for (int i = 0; i < passengerContainerLin.getChildCount(); i++) {
            View view1 = passengerContainerLin.getChildAt(i);
            if(view1!= null) {
                EditText firstNameEdt = view1.findViewById(R.id.firstNameEdt);
                EditText lastNameEdt =  view1.findViewById(R.id.lastNameEdt);
                TextView dobTv = view1.findViewById(R.id.dobTv);

                if(!Common.isNameValid(firstNameEdt.getText().toString())){
                    Toast.makeText(context, R.string.invalidFirstName, Toast.LENGTH_SHORT).show();
                    firstNameEdt.setError(getResources().getString(R.string.invalidFirstName));
                    firstNameEdt.requestFocus();
                    firstNameEdt.setFocusable(true);
                    showCurrentPassenger(i);
                    return false;
                }
                if(!Common.isNameValid(lastNameEdt.getText().toString())){
                    Toast.makeText(context, R.string.invalidLastName, Toast.LENGTH_SHORT).show();
                    lastNameEdt.setError(getResources().getString(R.string.invalidLastName));
                    lastNameEdt.requestFocus();
                    lastNameEdt.setFocusable(true);
                    showCurrentPassenger(i);
                    return false;
                }
                if((!paxType[i].equals(ADULT) || !flightSearchRequestModel.isDomestic) && dobTv.getText().toString().length()==0){
                    Toast.makeText(context, R.string.selectDob, Toast.LENGTH_SHORT).show();
                    dobTv.setError(getResources().getString(R.string.selectDob));
                    dobTv.requestFocus();
                    dobTv.setFocusable(true);
                    showCurrentPassenger(i);
                    return false;
                }
             }if(!flightSearchRequestModel.isDomestic) {
                EditText passportNoEdt =  view1.findViewById(R.id.passportNoEdt);
                TextView passportExpTv =  view1.findViewById(R.id.passportExpTv);
                if(passportNoEdt.length()==0){
                    Toast.makeText(context, R.string.passportNumber, Toast.LENGTH_SHORT).show();
                    passportNoEdt.setError(getResources().getString(R.string.passportNumber));
                    passportNoEdt.requestFocus();
                    passportNoEdt.setFocusable(true);
                    showCurrentPassenger(i);
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreLin).setVisibility(View.VISIBLE);
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreImg).setRotation(180);
                    return false;
                }else if(passportExpTv. length()==0) {
                    Toast.makeText(context, R.string.passportExpiry, Toast.LENGTH_SHORT).show();
                    passportExpTv.setError(getResources().getString(R.string.passportExpiry));
                    passportExpTv.requestFocus();
                    passportExpTv.setFocusable(true);
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreLin).setVisibility(View.VISIBLE);
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreImg).setRotation(180);
                    return false;
                } else if(passportExpTv.length()>0){
                    Date startDate= null;
                    Date expiryDate= null;
                    try {
                        startDate = new SimpleDateFormat("yy-MM-dd", Locale.US).parse(flightResponse.segments.get(
                                flightResponse.segments.size()-1).get(flightResponse.segments.get(
                                        flightResponse.segments.size()-1).size()-1).arrivalTime);
                        expiryDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(passportExpTv.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTime(startDate);
//                    calendar.add(calendar.MONTH,6);
                    if(calendar.getTime().after(expiryDate)){
                        passportExpTv.setError(getResources().getString(R.string.passportExpiry));
                        passportExpTv.requestFocus();
                        passportExpTv.setFocusable(true);
                        Toast.makeText(context, R.string.passportExpire, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreLin).setVisibility(View.VISIBLE);
                    passengerContainerLin.getChildAt(i).findViewById(R.id.moreImg).setRotation(180);
                }
            }

        }
        return true;
    }

    private boolean validateGST(){
        if(isGST){
            if(gstNoEdt.getText().toString().trim().length()==0){
                Toast.makeText(context, R.string.invalidGstNo, Toast.LENGTH_SHORT).show();
                gstNoEdt.setError(getResources().getString(R.string.invalidGstNo));
                gstNoEdt.requestFocus();
                gstNoEdt.setFocusable(true);
//                showCurrentPassenger(i);
                return false;
            }else if(gstCompanyNameEdit.getText().toString().trim().length()==0){
                Toast.makeText(context, R.string.invalidGstCompanyName, Toast.LENGTH_SHORT).show();
                gstCompanyNameEdit.setError(getResources().getString(R.string.invalidGstCompanyName));
                gstCompanyNameEdit.requestFocus();
                gstCompanyNameEdit.setFocusable(true);
//                showCurrentPassenger(i);
                return false;
            }else if(!Common.isEmailValid(gstEmailEdit.getText().toString().trim())){
                Toast.makeText(context, R.string.invalidGstEmail, Toast.LENGTH_SHORT).show();
                gstEmailEdit.setError(getResources().getString(R.string.invalidGstEmail));
                gstEmailEdit.requestFocus();
                gstEmailEdit.setFocusable(true);
//                showCurrentPassenger(i);
                return false;
            }else if(gstMobileEdit.getText().toString().trim().length()<10){
                Toast.makeText(context, R.string.invalidGstMobile, Toast.LENGTH_SHORT).show();
                gstMobileEdit.setError(getResources().getString(R.string.invalidGstMobile));
                gstMobileEdit.requestFocus();
                gstMobileEdit.setFocusable(true);
//                showCurrentPassenger(i);
                return false;
            }
        }
        return true;
    }

    private void addTraveller(final String adultType, final int position) {

        View passengerView = getLayoutInflater().inflate(R.layout.list_traveller_detail, null);

        TextView headerTv=passengerView.findViewById(R.id.headerTv);
        TextView firstNameEdt=passengerView.findViewById(R.id.firstNameEdt);
        TextView lastNameEdt=passengerView.findViewById(R.id.lastNameEdt);
        final TextView mrTv=passengerView.findViewById(R.id.mrTv);
        final TextView mrsTv=passengerView.findViewById(R.id.mrsTv);
        final TextView msTv=passengerView.findViewById(R.id.msTv);
        final TextView masterTv=passengerView.findViewById(R.id.masterTv);

        final TextView dobTv=passengerView.findViewById(R.id.dobTv);
        final RelativeLayout dobRel=passengerView.findViewById(R.id.dobRel);

        final RelativeLayout passportExpRel=passengerView.findViewById(R.id.passportExpRel);
        final TextView passportExpTv = passengerView.findViewById(R.id.passportExpTv);

        RelativeLayout headerRel=passengerView.findViewById(R.id.headerRel);
        final LinearLayout moreLin=passengerView.findViewById(R.id.moreLin);
        final RelativeLayout moreRel=passengerView.findViewById(R.id.moreRel);
        final ImageView moreImg=passengerView.findViewById(R.id.moreImg);

        headerTv.setText(adultType);

        headerRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCurrentPassenger(position);
            }
        });

        moreRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(moreLin.getVisibility()== View.VISIBLE){
                    moreImg.setRotation(0);
                    moreLin.setVisibility(View.GONE);
                } else {
                    moreImg.setRotation(180);
                    moreLin.setVisibility(View.VISIBLE);
                }
            }
        });

        passengersArrayList.get(position).title= Constants.Salutation.MR;
        passengersArrayList.get(position).gender=Constants.Gender.MALE;
        mrTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MR);
                passengersArrayList.get(position).title=Constants.Salutation.MR;
                passengersArrayList.get(position).gender=Constants.Gender.MALE;
            }
        });
        mrsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MRS);
                passengersArrayList.get(position).title=Constants.Salutation.MRS;
                passengersArrayList.get(position).gender=Constants.Gender.FEMALE;
            }
        });
        msTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MS);
                passengersArrayList.get(position).title=Constants.Salutation.MS;
                passengersArrayList.get(position).gender=Constants.Gender.FEMALE;
            }
        });
        masterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MASTER);
                passengersArrayList.get(position).title=Constants.Salutation.MASTER;
                passengersArrayList.get(position).gender=Constants.Gender.MALE;
            }
        });

//        adult dob
        if(adultType.contains(ADULT) && flightSearchRequestModel.isDomestic){
            dobRel.setVisibility(View.INVISIBLE);
        }else {
            dobRel.setVisibility(View.VISIBLE);
        }

        if(adultType.contains(CHILD) || adultType.contains(INFANT)){
            mrTv.setVisibility(View.GONE);
            mrsTv.setVisibility(View.GONE);
            masterTv.setVisibility(View.VISIBLE);
            selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MASTER);
        }else {
            mrTv.setVisibility(View.VISIBLE);
            mrsTv.setVisibility(View.VISIBLE);
            masterTv.setVisibility(View.GONE);
            selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MR);
        }

        dobRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        R.style.DatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                checkInDateCalendar.set(year, monthOfYear, dayOfMonth);
                                dobTv.setText(dateServerFormat.format(checkInDateCalendar.getTime()));
                                dobTv.setError(null);
                            }
                        }, checkInDateYear, checkInDateMonth, checkInDateDay);

                if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                if(adultType.contains(ADULT)){
                    datePickerDialog.getDatePicker().setMinDate(adultMinDate.getTimeInMillis());
                }else if(adultType.contains(CHILD)){
                    datePickerDialog.getDatePicker().setMinDate(childMinDate.getTimeInMillis());
                }else if(adultType.contains(INFANT)){
                    datePickerDialog.getDatePicker().setMinDate(infantMinDate.getTimeInMillis());
                }
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });
        passportExpRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPassportDatePicker(passportExpTv);
            }
        });

        if(position==0){
            if(MyPreferences.getLoginToken(context)!=null&& MyPreferences.isUserLogin(context)){
                firstNameEdt.setText(DecodeLoginToken.getLoginFirstName(context));
                lastNameEdt.setText(DecodeLoginToken.getLoginLastName(context));
                et_email.setText(DecodeLoginToken.getLoginEmail(context));
                et_mobile.setText(DecodeLoginToken.getLoginMobileNumber(context));
            }
        }

        passengerContainerLin.addView(passengerView);

    }
    private void openPassportDatePicker(final TextView passportExpTv) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        checkDOBDateDay =dayOfMonth;
                        checkDOBDateMonth =monthOfYear;
                        checkDOBDateYear =year;
                        passportExpTv.setText(dateToSetFormat.format(calendar.getTime()));
                        passportExpTv.setError(null);

                    }

                }, checkDOBDateYear, checkDOBDateMonth, checkDOBDateDay);

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-(60*60*24*365*12));

        datePickerDialog.show();
    }

    private void setDefaultCalender() {
        currentDateCalender = Calendar.getInstance();
        checkInDateCalendar = Calendar.getInstance();
        checkInDateDay = checkInDateCalendar.get(Calendar.DAY_OF_MONTH);
        checkInDateMonth = checkInDateCalendar.get(Calendar.MONTH);
        checkInDateYear = checkInDateCalendar.get(Calendar.YEAR);
        dateServerFormat=new SimpleDateFormat("dd-MM-yyyy");

        calendar = Calendar.getInstance();
        Calendar calender= Calendar.getInstance();
        calender.add(Calendar.YEAR,-150);
        dateToSetFormat=new SimpleDateFormat("dd-MM-yyyy");


        adultMinDate= Calendar.getInstance();
        adultMinDate.add(Calendar.YEAR, -150);


        childMinDate= Calendar.getInstance();
        childMinDate.add(Calendar.YEAR, -12);
        childMinDate.add(Calendar.DAY_OF_MONTH, 1);

        infantMinDate= Calendar.getInstance();
        infantMinDate.add(Calendar.YEAR, -2);
        infantMinDate.add(Calendar.DAY_OF_MONTH, 1);

    }

    private void selectSalutation(TextView mrTv, TextView mrsTv, TextView msTv, TextView masterTv, int TYPE) {
        if(TYPE== Constants.Salutation.MR){
            mrTv.setBackgroundResource(R.drawable.solid_round_blue_background);
            mrsTv.setBackgroundResource(R.color.transparent);
            msTv.setBackgroundResource(R.color.transparent);
            masterTv.setBackgroundResource(R.color.transparent);
            mrTv.setTextColor(getResources().getColor(R.color.colorWhite));
            mrsTv.setTextColor(getResources().getColor(R.color.black_text_color));
            msTv.setTextColor(getResources().getColor(R.color.black_text_color));
            masterTv.setTextColor(getResources().getColor(R.color.black_text_color));
        }else if(TYPE==Constants.Salutation.MRS){
            mrTv.setBackgroundResource(R.color.transparent);
            mrsTv.setBackgroundResource(R.drawable.solid_round_blue_background);
            msTv.setBackgroundResource(R.color.transparent);
            masterTv.setBackgroundResource(R.color.transparent);
            mrTv.setTextColor(getResources().getColor(R.color.black_text_color));
            mrsTv.setTextColor(getResources().getColor(R.color.colorWhite));
            msTv.setTextColor(getResources().getColor(R.color.black_text_color));
            masterTv.setTextColor(getResources().getColor(R.color.black_text_color));
        }else if(TYPE==Constants.Salutation.MS) {
            mrTv.setBackgroundResource(R.color.transparent);
            mrsTv.setBackgroundResource(R.color.transparent);
            masterTv.setBackgroundResource(R.color.transparent);
            msTv.setBackgroundResource(R.drawable.solid_round_blue_background);
            mrTv.setTextColor(getResources().getColor(R.color.black_text_color));
            mrsTv.setTextColor(getResources().getColor(R.color.black_text_color));
            msTv.setTextColor(getResources().getColor(R.color.colorWhite));
            masterTv.setTextColor(getResources().getColor(R.color.black_text_color));
        }else if(TYPE==Constants.Salutation.MASTER) {
            mrTv.setBackgroundResource(R.color.transparent);
            mrsTv.setBackgroundResource(R.color.transparent);
            msTv.setBackgroundResource(R.color.transparent);
            masterTv.setBackgroundResource(R.drawable.solid_round_blue_background);
            mrTv.setTextColor(getResources().getColor(R.color.black_text_color));
            mrsTv.setTextColor(getResources().getColor(R.color.black_text_color));
            masterTv.setTextColor(getResources().getColor(R.color.colorWhite));
            msTv.setTextColor(getResources().getColor(R.color.black_text_color));
        }else {
            mrTv.setBackgroundResource(R.drawable.solid_round_blue_background);
            mrsTv.setBackgroundResource(R.color.transparent);
            msTv.setBackgroundResource(R.color.transparent);
            masterTv.setBackgroundResource(R.color.transparent);
            mrTv.setTextColor(getResources().getColor(R.color.colorWhite));
            mrsTv.setTextColor(getResources().getColor(R.color.black_text_color));
            msTv.setTextColor(getResources().getColor(R.color.black_text_color));
            masterTv.setTextColor(getResources().getColor(R.color.black_text_color));
        }
    }

    private void showCurrentPassenger(int position) {
        for(int i=0; i<passengerContainerLin.getChildCount(); i++){
            View view=passengerContainerLin.getChildAt(i);
            view.findViewById(R.id.passengerMainLin).setVisibility(View.GONE);
            ImageView headerImg=view.findViewById(R.id.headerImg);
            headerImg.setRotation(0);
        }
        passengerContainerLin.getChildAt(position).findViewById(R.id.passengerMainLin).setVisibility(View.VISIBLE);
        ImageView headerImg=passengerContainerLin.getChildAt(position).findViewById(R.id.headerImg);
        headerImg.setRotation(180);
        currentPassengerPosition=position;
    }

    // get travellers details
    private void getPassengerDetails(){
        for (int i = 0; i < passengersArrayList.size(); i++) {
            View view = passengerContainerLin.getChildAt(i);
            if(view!= null) {
                EditText firstNameEdt = view.findViewById(R.id.firstNameEdt);
                EditText lastNameEdt =  view.findViewById(R.id.lastNameEdt);
                TextView dobTv = view.findViewById(R.id.dobTv);
                EditText passportNoEdt = view.findViewById(R.id.passportNoEdt);
                EditText ffNumberEdt = view.findViewById(R.id.ffNumberEdt);
                TextView passportExpTv = view.findViewById(R.id.passportExpTv);

                try {
                    passengersArrayList.get(i).firstName= firstNameEdt.getText().toString().trim();
                    passengersArrayList.get(i).lastName= lastNameEdt.getText().toString().trim();
                    passengersArrayList.get(i).paxType= paxType[i];
                    if(dobTv.getText().toString().trim().length()==0){
                        passengersArrayList.get(i).dateOfBirth= dateServerFormat.format(currentDateCalender.getTime());
                    }else {
                        passengersArrayList.get(i).dateOfBirth= dobTv.getText().toString().trim();
                    }
                    passengersArrayList.get(i).passportNo= passportNoEdt.getText().toString().trim();
                    passengersArrayList.get(i).passportExpiry= passportExpTv.getText().toString().trim();
                    if(i==0){
                        passengersArrayList.get(i).isLeadPax= true;
                    }else {
                        passengersArrayList.get(i).isLeadPax= false;
                    }
                    passengersArrayList.get(i).FFAirline= null;
                    passengersArrayList.get(i).FFNumber= ffNumberEdt.getText().toString().trim();
                    passengersArrayList.get(i).contactNo= "";
                    passengersArrayList.get(i).email= "";

//                    passengersArrayList.add(passengersArrayList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setCoPassengerDetails(CoPassengerListDataModel.response response){
        View view = passengerContainerLin.getChildAt(currentPassengerPosition);
        if(view!= null) {
            EditText firstNameEdt = view.findViewById(R.id.firstNameEdt);
            EditText lastNameEdt = view.findViewById(R.id.lastNameEdt);
            TextView dobTv = view.findViewById(R.id.dobTv);
            EditText passportNoEdt = view.findViewById(R.id.passportNoEdt);
            final TextView mrTv=view.findViewById(R.id.mrTv);
            final TextView mrsTv=view.findViewById(R.id.mrsTv);
            final TextView msTv=view.findViewById(R.id.msTv);
            final TextView masterTv=view.findViewById(R.id.masterTv);

            firstNameEdt.setText(response.firstName);
            lastNameEdt.setText(response.lastName);
            dobTv.setText(response.dateOfBirth);
            passportNoEdt.setText(response.passportNumber);
            if(response.title.equalsIgnoreCase("MR")){
                if(mrTv.getVisibility()== View.VISIBLE){
                    selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MR);
                }else {
                    selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MASTER);
                }
            }else if(response.title.equalsIgnoreCase("MRS")){
                if(mrsTv.getVisibility()== View.VISIBLE){
                    selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MRS);
                }else {
                    selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MISS);
                }
            }else if(response.title.equalsIgnoreCase("MISS")){
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MS);
            }else if(response.title.equalsIgnoreCase("MASTER")){
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MASTER);
            }else {
                selectSalutation(mrTv, mrsTv, msTv, masterTv, Constants.Salutation.MR);
            }
        }
    }

    private void methodHoldBookingAPI() {
        flightBookingRequest.email=et_email.getText().toString().trim();
        flightBookingRequest.mobile=et_mobile.getText().toString().trim();
        flightBookingRequest.resultId=FlightId;
        flightBookingRequest.sessionId=sessionId;

        FlightBookingRequest.GST GST=flightBookingRequest.new GST();
        GST.GSTCompanyAddress="";
        GST.GSTCompanyContactNumber=gstMobileEdit.getText().toString().trim();
        GST.GSTCompanyName=gstCompanyNameEdit.getText().toString().trim();
        GST.GSTNumber=gstNoEdt.getText().toString().trim();
        GST.GSTCompanyEmail=gstEmailEdit.getText().toString().trim();

        flightBookingRequest.GST=GST;

        getPassengerDetails();
        flightBookingRequest.passengers=passengersArrayList;

        showCustomDialog();

        new NetworkCall().callFlightPostService(flightBookingRequest, ApiConstants.FLIGHT_HOLD, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomDialog();
                        if(response!=null){
                            responseHandler(response, responseCode);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandler(ResponseBody response, int responseCode) {
        if(response!=null){
            try {
                FlightBookingResponseModel senderResponse = new Gson().fromJson(response.string(), FlightBookingResponseModel.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status==200){
//                        Toast.makeText(context, "Booking Success", Toast.LENGTH_SHORT).show();

                      /*  Fragment fragment = new PaymentGatewayFlight();
                        Bundle bundle=new Bundle();
                        bundle.putString("flightID",flightResponse.id);
                        bundle.putString("sessionID",senderResponse.sessionId);
                        bundle.putFloat("amount", totalFare);
                        bundle.putString("bookingId", senderResponse.response.bookingId);
                        bundle.putSerializable("flightBookingRequest", flightBookingRequest);
                        bundle.putSerializable("FlightModel", flightResponse);
                        fragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);*/
                    }else if(responseCode==200 && senderResponse.status==203){
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity)context).customBackPress();
                    }else {
//                        Toast.makeText(context, senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        openResponseFailureDialog();
                    }
                }else {
//                    Toast.makeText(context, "Booking failed", Toast.LENGTH_SHORT).show();
                    openResponseFailureDialog();
                }
            } catch (IOException e) {
//                e.printStackTrace();
                SomethingWrongDialog.getInstance().showCustomDialog(context);
            }
        }else {
//            Toast.makeText(context, "Booking failed", Toast.LENGTH_SHORT).show();
            openResponseFailureDialog();
        }
    }

    private void getFareQuote(){
        showCustomWaitingDialog();
        new NetworkCall().callFlightPostService(fareQuotesRequestModel, ApiConstants.FARE_QUOTE, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomWaitiongDialog();
                        if(response!=null){
                            responseHandler(response, responseCode, 0);
                        }else {
                            Toast.makeText(context, R.string.fareQuoteError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int responseCode, int TYPE) {
        if(TYPE==FARE_RULES){
            try {
                FareRulesResponseModel senderResponse = new Gson().fromJson(response.string(), FareRulesResponseModel.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status == 200) {
                        Fragment fragment=new FareRulesFragment();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("FareRules", senderResponse);
                        bundle.putInt("SegmentSource", flightResponse.segmentSource);
                        if(flightResponseReturn!=null){
                            bundle.putString("SessionId", sessionId);
                            bundle.putString("FlightId", FlightId);
                            bundle.putInt("SegmentSourceReturn", flightResponseReturn.segmentSource);
                        }
                        fragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                    } else if(responseCode==200 && senderResponse.status==203){
                        hideCustomDialog();
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity)context).customBackPress();
                    }else {
//                        Toast.makeText(context,senderResponse.error.errorMessage,Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                FareQuotesResponseModel senderResponse = new Gson().fromJson(response.string(), FareQuotesResponseModel.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status == 200) {
                        isFareQuote=true;
                        sessionId=senderResponse.sessionId;
                        if(senderResponse.response.isPriceChanged){
                            updatePrice(senderResponse);
                        }
                        methodHoldBookingAPI();
                    } else if(responseCode==200 && senderResponse.status==203){
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity)context).customBackPress();
                    }else {
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.fareQuoteError, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context, R.string.fareQuoteException, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePrice(FareQuotesResponseModel senderResponse) {
        totalFare = senderResponse.response.netPayableAmount.netPayable;
        totalPriceTv.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.fare.currency,context)+" "+totalFare);
        Toast.makeText(context, "Fare has been updated please confirm.", Toast.LENGTH_LONG).show();
//        methodHoldBookingAPI();
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancellationTv:
                fareRules();
                break;
            case R.id.baggageTv:
                openBaggageDialog();
                break;
            case R.id.getCoPassengersImg:
                if(MyPreferences.isUserLogin(context)){
                    getCoPassengerList();
                }else {
                    Toast.makeText(context, R.string.youAreNotLogin, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.loginTv:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(HomeFragment.newInstance("",""));
                break;
            case R.id.proceedTv:
                if(validPassengersDetails() && validMobileEmail()  && validateGST()) {
                    Common.hideSoftKeyboard((Activity)context);
                    getFareQuote();
//                    methodHoldBookingAPI();
                }
                break;
        }
    }

    private void getCoPassengerList() {
            showCustomDialog();
            MyCustomDialog.setDialogMessage("Getting co-passengers...");

        new NetworkCall().getServiceWithHeader(ApiConstants.CoPassengers, MyPreferences.getLoginToken(context), context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(responseCode==200){
                            responseHandler(response);
                        } else{
                            hideCustomDialog();
                            Toast.makeText(context, "Unable to fetch co-passenger list.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response) {
        try {
            hideCustomDialog();
            CoPassengerListDataModel senderResponse;
            senderResponse = new Gson().fromJson(response.string(), CoPassengerListDataModel.class);
            if(senderResponse.response!=null && senderResponse.response.size()>0) {
                setPassengerDetails(senderResponse);}
        }catch (Exception e){
            hideCustomDialog();
            Toast.makeText(context, "Unable to fetch details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPassengerDetails(final CoPassengerListDataModel senderResponse) {
        final Dialog cancelDialog = new Dialog(context);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.hotel_cancellation_dialog);
        final Window window= cancelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView titleTv= cancelDialog.findViewById(R.id.titleTv);
        titleTv.setVisibility(View.GONE);
        TextView close_btn= cancelDialog.findViewById(R.id.close_btn);
        TextView cancel1Tv= cancelDialog.findViewById(R.id.cancel1Tv);
        cancel1Tv.setVisibility(View.GONE);
        LinearLayout coPassContainerLin= cancelDialog.findViewById(R.id.coPassContainerLin);
        coPassContainerLin.setGravity(View.FOCUS_LEFT);
        TextView[] name=new TextView[senderResponse.response.size()];

        close_btn.setText("Cancel");

        for (int i=0;i<senderResponse.response.size();i++){
            name[i] =new TextView(context);
            name[i].setTextColor(getResources().getColor(R.color.black_text_color));
            name[i].setPadding(30,5,5,3);
            name[i].setTextSize(20);

            name[i].setText(senderResponse.response.get(i).title+" "+senderResponse.response.get(i).firstName
                    +" "+senderResponse.response.get(i).lastName);
            final int finalI = i;
            name[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCoPassengerDetails(senderResponse.response.get(finalI));
                    cancelDialog.dismiss();
                }
            });
            coPassContainerLin.addView(name[i]);
        }
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancelDialog.dismiss();
            }
        });
        cancelDialog.show();
    }

    private void fareRules() {
        showCustomDialog();
        String url;
        HttpUrl.Builder urlBuilder;
        if(flightResponse.segmentSource==1){
//            url=ApiConstants.FLIGHT_FARE_RULE;
            urlBuilder=HttpUrl.parse(ApiConstants.FLIGHT_FARE_RULE).newBuilder();
            urlBuilder.addQueryParameter("sessionId", sessionId);
//            urlBuilder.addQueryParameter("flightId", flightResponse.id);
            urlBuilder.addQueryParameter("flightId", FlightId);
            urlBuilder.addQueryParameter("index", "0");  // 1 in case of inbound
            urlBuilder.addQueryParameter("src", "f");
            url = urlBuilder.build().toString();
//            url=ApiConstants.FLIGHT_FARE_RULE +SessionId+"&flightId="+flightResponse.id+"&index="+(-1)+"&src="+"s";
        }else {
//            url=ApiConstants.FLIGHT_MINI_FARE_RULE +SessionId+"&flightId="+flightResponse.id;
//            url=ApiConstants.FLIGHT_MINI_FARE_RULE;
            urlBuilder=HttpUrl.parse(ApiConstants.FLIGHT_MINI_FARE_RULE).newBuilder();
            urlBuilder.addQueryParameter("sessionId", sessionId);
            urlBuilder.addQueryParameter("flightId", FlightId);
            url = urlBuilder.build().toString();
        }

        new NetworkCall().getWithUrlHeaderFlight(url, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomDialog();
                        if(response!=null){
                            responseHandler(response, responseCode, FARE_RULES);
                        }else {
                            Toast.makeText(context,  R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openBaggageDialog() {
        final Dialog cancelDialog = new Dialog(context);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.hotel_cancellation_dialog);
        final Window window= cancelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView titleTv= cancelDialog.findViewById(R.id.titleTv);
        TextView close_btn= cancelDialog.findViewById(R.id.close_btn);
        TextView cancel1Tv= cancelDialog.findViewById(R.id.cancel1Tv);
        titleTv.setText("Baggage Information");

        String baggagePolicy="";
        String baggagePolicyReturn="";

        if(flightResponseReturn!=null){
            baggagePolicy="For Outbound Flight:\n\n";
            baggagePolicyReturn="\n\nFor Inbound Flight:\n\n";
            ArrayList<FlightSearchResponseModel.response.flights.segments.baggageInfo> baggageInfoArray=flightResponseReturn.segments.get(0).get(0).baggageInfo;
            if(baggageInfoArray!=null && baggageInfoArray.size()>0){
                baggagePolicyReturn=baggagePolicyReturn+"1 )  "+baggageInfoArray.get(0).text+
                        " ("+baggageInfoArray.get(0).paxType+")";
                for(int i=1; i<baggageInfoArray.size(); i++){
                    baggagePolicyReturn=baggagePolicyReturn+"\n"+(i+1)+" )  "+baggageInfoArray.get(i).text+ " ("+baggageInfoArray.get(i).paxType+")";
                }
            }else {
//                cancel1Tv.setText("No baggage info available.");
            }

        }
        ArrayList<FlightSearchResponseModel.response.flights.segments.baggageInfo> baggageInfoArray=flightResponse.segments.get(0).get(0).baggageInfo;
        if(baggageInfoArray!=null && baggageInfoArray.size()>0){
            baggagePolicy=baggagePolicy+"1 )  "+baggageInfoArray.get(0).text+
                    " ("+baggageInfoArray.get(0).paxType+")";
            for(int i=1; i<baggageInfoArray.size(); i++){
                baggagePolicy=baggagePolicy+"\n"+(i+1)+" )  "+baggageInfoArray.get(i).text+ " ("+baggageInfoArray.get(i).paxType+")";
            }
        }else {
//            cancel1Tv.setText("No baggage info available.");
        }


        cancel1Tv.setText(baggagePolicy+"\n"+baggagePolicyReturn+"\n");
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancelDialog.dismiss();
            }
        });
        cancelDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MyPreferences.getLoginToken(context)!=null&& MyPreferences.isUserLogin(context)){
            try {
                et_email.setText(DecodeLoginToken.getLoginEmail(context));
                et_mobile.setText(DecodeLoginToken.getLoginMobileNumber(context));
                loginLin.setVisibility(View.GONE);
                guestTv.setVisibility(View.GONE);
                View view=passengerContainerLin.getChildAt(0);
                TextView firstNameEdt=view.findViewById(R.id.firstNameEdt);
                TextView lastNameEdt=view.findViewById(R.id.lastNameEdt);
                firstNameEdt.setText(DecodeLoginToken.getLoginFirstName(context));
                lastNameEdt.setText(DecodeLoginToken.getLoginLastName(context));
                firstNameEdt.setError(null);
                lastNameEdt.setError(null);
                et_email.setError(null);
                et_mobile.setError(null);
            }catch (Exception e){

            }
        }else {
            getCoPassengersImg.setVisibility(View.GONE);
            loginLin.setVisibility(View.VISIBLE);
            guestTv.setVisibility(View.VISIBLE);
        }
    }

    private void openResponseFailureDialog() {
        final Dialog cancelDialog = new Dialog(context);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.hotel_cancellation_dialog);
        final Window window= cancelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView close_btn= (TextView) cancelDialog.findViewById(R.id.close_btn);
        TextView titleTv= (TextView) cancelDialog.findViewById(R.id.titleTv);
        TextView cancel1Tv= (TextView) cancelDialog.findViewById(R.id.cancel1Tv);
        ImageView cancel2Tv= (ImageView) cancelDialog.findViewById(R.id.cancel2Tv);
        cancel2Tv.setVisibility(View.VISIBLE);

        cancel1Tv.setText("We are unable to book Flight now, please try again later");
        titleTv.setText("Error in Booking Flight");
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancelDialog.dismiss();
            }
        });
        cancelDialog.show();
    }

    private void showCustomWaitingDialog() {
        MyCustomWaitingDialog.showCustomDialog(context,context.getResources().getString(R.string.fareConfirmingText));
    }
    private void hideCustomWaitiongDialog() {
        MyCustomWaitingDialog.hideCustomDialog();
    }
}
