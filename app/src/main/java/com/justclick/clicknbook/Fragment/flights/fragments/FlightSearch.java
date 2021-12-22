package com.justclick.clicknbook.Fragment.flights.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightCityModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchRoundDomesticResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.Constants;
import com.justclick.clicknbook.utils.DateAndTimeUtils;
import com.justclick.clicknbook.utils.HotelSearchDialog;
import com.justclick.clicknbook.utils.InternetConnectionOffDialog;
import com.justclick.clicknbook.utils.MyBounceInterpolator;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.SomethingWrongDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;

public class FlightSearch extends Fragment implements View.OnClickListener, PlacesSearch.OnFlightCitySearchListener {
    public static String FlightSearchTag="FlightSearchTag";
    private final String ANY_CLASS="ANY", ECONOMY="ECONOMY", BUSINESS="BUSINESS",
            PREMIUM_ECONOMY="PREMIUM ECONOMY", FIRST_CLASS="FIRST CLASS";
    private final int fromCityOne=11, toCityOne=12, fromCityTwo=21, toCityTwo=22,
            fromCityThree=31, toCityThree=32, fromCityFour=41, toCityFour=42,
            fromCityFive=51, toCityFive=52;
    private final int DEPT_DATE=0, RETURN_DATE=1, MULTI_DATE_ONE=2, MULTI_DATE_TWO=3, MULTI_DATE_THREE=4,
            MULTI_DATE_FOUR=5, MULTI_DATE_FIVE=6;
    private int Flight=2,adultCount = 1, childCount = 0, infantCount = 0, multiCityCount=2;
    private final int MAX_PASS =9;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private String searchType= Constants.FlightJourneyType.ONE_WAY;
            /*fromMultiCityNameTwo, fromMultiCityNameThree, fromMultiCityNameFour, fromMultiCityNameFive,
            toMultiCityNameTwo, toMultiCityNameThree, toMultiCityNameFour, toMultiCityNameFive,
            fromMultiCityCodeTwo, fromMultiCityCodeThree, fromMultiCityCodeFour, fromMultiCityCodeFive,
            toMultiCityCodeTwo, toMultiCityCodeThree, toMultiCityCodeFour, toMultiCityCodeFive*/;
    private View view;
    private Context context;
    private static FlightSearch fragment;
    private TextView oneWayTv, roundTripTv, multiCityTv, fromCityTvOne, toCityTvOne,
            fromDateTvOne, returnDateTvOne, passengersTv, cabinClassTv, searchTv,
            tvAdultCount, tvChildCount, tvInfantCount;
//    multicity textviews
    private TextView fromCityTvTwo, toCityTvTwo, fromCityTvThree, toCityTvThree,
        fromCityTvFour, toCityTvFour, fromCityTvFive, toCityTvFive,
        dateMultiOneTv, dateMultiTwoTv, dateMultiThreeTv, dateMultiFourTv, dateMultiFiveTv,
        removeMultiCityTv, addMultiCityTv;
    private RelativeLayout returnDateRel, dateMultiOneRel;
    private LinearLayout oneTwoDateLin, multiTwoLin, multiThreeLin, multiFourLin, multiFiveLin, addRemoveMultiCityLin;

    private FlightCityModel.response flightSourceDataModel, flightDestinationDataModel,
    flightSourceCityModelTwo, flightSourceCityModelThree, flightSourceCityModelFour, flightSourceCityModelFive,
            flightDestCityModelTwo, flightDestCityModelThree, flightDestCityModelFour, flightDestCityModelFive;

    private ArrayList<FlightCityModel.response> multiCitySourceDestArrayList;
    //    calender views
    private SimpleDateFormat dateServerFormat;
    private int fromDateDay, fromDateMonth, fromDateYear,
            returnDateDay, returnDateMonth, returnDateYear;
    private Calendar fromDateCalendar, returnDateCalendar, multiDateCalenderOne, multiDateCalenderTwo,
            multiDateCalenderThree, multiDateCalenderFour, multiDateCalenderFive;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        multiCitySourceDestArrayList=new ArrayList<>();
    }

    public static Fragment newInstance() {
        if(fragment==null) {
            fragment = new FlightSearch();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_flights_search, container, false);
            initializeViews();
            initializeDates();
            getAndSetShredPref(); // get and set value from session
        }
        
        return view;
    }

    private void initializeViews() {
        oneWayTv=view.findViewById(R.id.oneWayTv);
        roundTripTv=view.findViewById(R.id.roundTripTv);
        multiCityTv=view.findViewById(R.id.multiCityTv);

        fromCityTvOne=view.findViewById(R.id.fromCityTvOne);
        toCityTvOne=view.findViewById(R.id.toCityTvOne);
        fromDateTvOne =view.findViewById(R.id.fromDateTvOne);
        returnDateTvOne=view.findViewById(R.id.returnDateTvOne);
        passengersTv=view.findViewById(R.id.passengersTv);
        cabinClassTv=view.findViewById(R.id.cabinClassTv);
        searchTv=view.findViewById(R.id.searchTv);

        returnDateRel=view.findViewById(R.id.returnDateRel);
        oneTwoDateLin=view.findViewById(R.id.oneTwoDateLin);

//        multi city views
        dateMultiOneTv=view.findViewById(R.id.dateMultiOneTv);
        fromCityTvTwo=view.findViewById(R.id.fromCityTvTwo);
        toCityTvTwo=view.findViewById(R.id.toCityTvTwo);
        fromCityTvThree=view.findViewById(R.id.fromCityTvThree);
        toCityTvThree=view.findViewById(R.id.toCityTvThree);
        fromCityTvFour=view.findViewById(R.id.fromCityTvFour);
        toCityTvFour=view.findViewById(R.id.toCityTvFour);
        fromCityTvFive=view.findViewById(R.id.fromCityTvFive);
        toCityTvFive=view.findViewById(R.id.toCityTvFive);
        dateMultiOneTv=view.findViewById(R.id.dateMultiOneTv);
        dateMultiTwoTv=view.findViewById(R.id.dateMultiTwoTv);
        dateMultiThreeTv=view.findViewById(R.id.dateMultiThreeTv);
        dateMultiFourTv=view.findViewById(R.id.dateMultiFourTv);
        dateMultiFiveTv=view.findViewById(R.id.dateMultiFiveTv);

        dateMultiOneRel=view.findViewById(R.id.dateMultiOneRel);
        multiTwoLin=view.findViewById(R.id.multiTwoLin);
        multiThreeLin=view.findViewById(R.id.multiThreeLin);
        multiFourLin=view.findViewById(R.id.multiFourLin);
        multiFiveLin=view.findViewById(R.id.multiFiveLin);

        removeMultiCityTv =view.findViewById(R.id.removeMultiCityTv);
        addMultiCityTv =view.findViewById(R.id.addMultiCityTv);
        addRemoveMultiCityLin=view.findViewById(R.id.addRemoveMultiCityLin);

        cabinClassTv.setText(ANY_CLASS);
        oneWayClick();

//        click listeners
        oneWayTv.setOnClickListener(this);
        roundTripTv.setOnClickListener(this);
        multiCityTv.setOnClickListener(this);
        searchTv.setOnClickListener(this);
        addMultiCityTv.setOnClickListener(this);
        removeMultiCityTv.setOnClickListener(this);

        view.findViewById(R.id.backArrowImg).setOnClickListener(this);
        view.findViewById(R.id.swapImg).setOnClickListener(this);
        view.findViewById(R.id.passengerRel).setOnClickListener(this);
        view.findViewById(R.id.cabinClassRel).setOnClickListener(this);
        view.findViewById(R.id.fromCityOneRel).setOnClickListener(this);
        view.findViewById(R.id.toCityOneRel).setOnClickListener(this);
        view.findViewById(R.id.deptDateOneRel).setOnClickListener(this);
        view.findViewById(R.id.returnDateRel).setOnClickListener(this);
//        multi clicks
        dateMultiOneRel.setOnClickListener(this);
        view.findViewById(R.id.fromCityMultiTwoRel).setOnClickListener(this);
        view.findViewById(R.id.toCityMultiTwoRel).setOnClickListener(this);
        view.findViewById(R.id.dateMultiTwoRel).setOnClickListener(this);
        view.findViewById(R.id.fromCityMultiThreeRel).setOnClickListener(this);
        view.findViewById(R.id.toCityMultiThreeRel).setOnClickListener(this);
        view.findViewById(R.id.dateMultiThreeRel).setOnClickListener(this);
        view.findViewById(R.id.fromCityMultiFourRel).setOnClickListener(this);
        view.findViewById(R.id.toCityMultiFourRel).setOnClickListener(this);
        view.findViewById(R.id.dateMultiFourRel).setOnClickListener(this);
        view.findViewById(R.id.fromCityMultiFiveRel).setOnClickListener(this);
        view.findViewById(R.id.toCityMultiFiveRel).setOnClickListener(this);
        view.findViewById(R.id.dateMultiFiveRel).setOnClickListener(this);
    }

    private void initializeDates() {
        dateServerFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        //default from Date
        fromDateCalendar = Calendar.getInstance();
        fromDateDay = fromDateCalendar.get(Calendar.DAY_OF_MONTH);
        fromDateMonth = fromDateCalendar.get(Calendar.MONTH);
        fromDateYear = fromDateCalendar.get(Calendar.YEAR);

        //default return Date
        returnDateCalendar = Calendar.getInstance();
        returnDateCalendar.add(Calendar.DAY_OF_MONTH,1);
        returnDateDay = returnDateCalendar.get(Calendar.DAY_OF_MONTH);
        returnDateMonth = returnDateCalendar.get(Calendar.MONTH);
        returnDateYear = returnDateCalendar.get(Calendar.YEAR);

//        fromDateTvOne.setText(dateServerFormat.format(fromDateCalendar.getTime()));
        fromDateTvOne.setText(DateAndTimeUtils.getDay(fromDateCalendar.getTime())+"  "+
                DateAndTimeUtils.getMonth(fromDateCalendar.getTime())+"'"+
                DateAndTimeUtils.getYear(fromDateCalendar.getTime()));
//        returnDateTvOne.setText(dateServerFormat.format(returnDateCalendar.getTime()));
        returnDateTvOne.setText(DateAndTimeUtils.getDay(returnDateCalendar.getTime())+"  "+
                DateAndTimeUtils.getMonth(returnDateCalendar.getTime())+"'"+
                DateAndTimeUtils.getYear(returnDateCalendar.getTime()));

//        multiCity
        multiDateCalenderOne= Calendar.getInstance();
        multiDateCalenderTwo= Calendar.getInstance();
        multiDateCalenderThree= Calendar.getInstance();
        multiDateCalenderFour= Calendar.getInstance();
        multiDateCalenderFive= Calendar.getInstance();

//        dateMultiOneTv.setText(dateServerFormat.format(multiDateCalenderOne.getTime()));
//        dateMultiTwoTv.setText(dateServerFormat.format(multiDateCalenderTwo.getTime()));
//        dateMultiThreeTv.setText(dateServerFormat.format(multiDateCalenderThree.getTime()));
//        dateMultiFourTv.setText(dateServerFormat.format(multiDateCalenderFour.getTime()));
//        dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
        dateMultiOneTv.setText(DateAndTimeUtils.getDay(multiDateCalenderOne.getTime())+"  "+
                DateAndTimeUtils.getMonth(multiDateCalenderOne.getTime())+"'"+
                DateAndTimeUtils.getYear(multiDateCalenderOne.getTime()));
        dateMultiTwoTv.setText(DateAndTimeUtils.getDay(multiDateCalenderTwo.getTime())+"  "+
                DateAndTimeUtils.getMonth(multiDateCalenderTwo.getTime())+"'"+
                DateAndTimeUtils.getYear(multiDateCalenderTwo.getTime()));

        dateMultiThreeTv.setText(DateAndTimeUtils.getDay(multiDateCalenderThree.getTime())+"  "+
                DateAndTimeUtils.getMonth(multiDateCalenderThree.getTime())+"'"+
                DateAndTimeUtils.getYear(multiDateCalenderThree.getTime()));

        dateMultiFourTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFour.getTime())+"  "+
                DateAndTimeUtils.getMonth(multiDateCalenderFour.getTime())+"'"+
                DateAndTimeUtils.getYear(multiDateCalenderFour.getTime()));

        dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
    }

    // get and set ShredPref
    private void getAndSetShredPref() {
        flightSourceDataModel=MyPreferences.getFlightSourceCityData(context);
        flightDestinationDataModel= MyPreferences.getFlightDestinationCityData(context);
        if(flightSourceDataModel!=null){
//            fromCityTvOne.setText(flightSourceDataModel.cityCode);
            fromCityTvOne.setText(flightSourceDataModel.cityName+" ("+flightSourceDataModel.cityCode+")");
        }else {
            fromCityTvOne.setText("");
        }
        if(flightDestinationDataModel!=null){
//            toCityTvOne.setText(flightDestinationDataModel.cityCode);
            toCityTvOne.setText(flightDestinationDataModel.cityName+" ("+flightDestinationDataModel.cityCode+")");
            fromCityTvTwo.setText(flightDestinationDataModel.cityName+" ("+flightDestinationDataModel.cityCode+")");
            flightSourceCityModelTwo=flightDestinationDataModel;
        }else {
            toCityTvOne.setText("");
            fromCityTvTwo.setText("");
        }
    }

    private void addMultiCityData(int position, FlightCityModel.response data) {
        if(multiCitySourceDestArrayList.size()>position){
            //        update value at particular position
            multiCitySourceDestArrayList.set(position, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backArrowImg:
                getFragmentManager().popBackStack();
                break;

            case R.id.swapImg:
                swapSearch();
                break;

            case R.id.oneWayTv:
                oneWayClick();
                break;
            case R.id.roundTripTv:
                roundTripClick();
                break;
            case R.id.multiCityTv:
                multiCityClick();
                break;
            case R.id.addMultiCityTv:
                multiCityCount++;
                addOrRemoveMultiCity();
                break;
            case R.id.removeMultiCityTv:
                multiCityCount--;
                addOrRemoveMultiCity();
                break;
            case R.id.fromCityOneRel:
                searchCity(fromCityOne);
                break;
            case R.id.toCityOneRel:
                searchCity(toCityOne);
                break;
            case R.id.dateMultiOneRel:
                openMultiDateCalender(MULTI_DATE_ONE);
                break;
            case R.id.fromCityMultiTwoRel:
                searchCity(fromCityTwo);
                break;
            case R.id.toCityMultiTwoRel:
                searchCity(toCityTwo);
                break;
            case R.id.dateMultiTwoRel:
                openMultiDateCalender(MULTI_DATE_TWO);
                break;
            case R.id.fromCityMultiThreeRel:
                searchCity(fromCityThree);
                break;
            case R.id.toCityMultiThreeRel:
                searchCity(toCityThree);
                break;
            case R.id.dateMultiThreeRel:
                openMultiDateCalender(MULTI_DATE_THREE);
                break;
            case R.id.fromCityMultiFourRel:
                searchCity(toCityFour);
                break;
            case R.id.toCityMultiFourRel:
                searchCity(toCityFour);
                break;
            case R.id.dateMultiFourRel:
                openMultiDateCalender(MULTI_DATE_FOUR);
                break;
            case R.id.fromCityMultiFiveRel:
                searchCity(toCityFive);
                break;
            case R.id.toCityMultiFiveRel:
                searchCity(toCityFive);
                break;
            case R.id.dateMultiFiveRel:
                openMultiDateCalender(MULTI_DATE_FIVE);
                break;
            case R.id.deptDateOneRel:
                openFromDateCalender(DEPT_DATE);
                break;
            case R.id.returnDateRel:
                openReturnDateCalender();
                break;
            case R.id.passengerRel:
                methodTravellers();
                break;
            case R.id.cabinClassRel:
                methodCabinClass();
                break;

            case R.id.tvAdultPlus:
                methodAdultPlus();
                break;
            case R.id.tvAdultMinus:
                methodAdultMinus();
                break;
            case R.id.tvChildPlus:
                methodChildPlus();
                break;
            case R.id.tvChildMinus:
                methodChildMinus();
                break;
            case R.id.tvInfantPlus:
                methodInfantPlus();
                break;
            case R.id.tvInfantMinus:
                methodInfantMinus();
                break;

            case R.id.searchTv:
//                Crashlytics.getInstance().crash(); // Force a crash
                if(Common.checkInternetConnection(context)){
                    Common.preventFrequentClick(searchTv);
                    if(validateSearch()) {
                        searchFlights();
                    }
                }else {
                    showInternetCustomDialog();
                }
                break;
        }
    }

    private boolean validateSearch() {
        if(searchType.equals(Constants.FlightJourneyType.MULTI_CITY)){
            return validateMulticityCities();
        }else if(flightSourceDataModel==null || flightDestinationDataModel==null){
                Toast.makeText(context, "Please select source and destination", Toast.LENGTH_SHORT).show();
                return false;
        }else if(flightSourceDataModel.cityCode.equalsIgnoreCase(flightDestinationDataModel.cityCode)){
            Toast.makeText(context, "Source and Destination cant not be same!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateMulticityCities() {
        switch (multiCityCount){
            case 2:
                if(fromCityTvOne.getText().toString().length()==0 || toCityTvOne.getText().toString().length()==0 ||
                        fromCityTvTwo.getText().toString().length()==0 || toCityTvTwo.getText().toString().length()==0){
                    Toast.makeText(context, "Please select all cities.", Toast.LENGTH_SHORT).show();
                    return false;
                }else if(fromCityTvOne.getText().toString().equals(toCityTvOne.getText().toString()) ||
                        fromCityTvTwo.getText().toString().equals(toCityTvTwo.getText().toString())){
                    Toast.makeText(context, "Please select different cities for source and destination.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 3:
                if(fromCityTvOne.getText().toString().length()==0 || toCityTvOne.getText().toString().length()==0 ||
                        fromCityTvTwo.getText().toString().length()==0 || toCityTvTwo.getText().toString().length()==0 ||
                        fromCityTvThree.getText().toString().length()==0 || toCityTvThree.getText().toString().length()==0 ){
                    Toast.makeText(context, "Please select all cities.", Toast.LENGTH_SHORT).show();
                    return false;
                }else if(fromCityTvOne.getText().toString().equals(toCityTvOne.getText().toString()) ||
                        fromCityTvTwo.getText().toString().equals(toCityTvTwo.getText().toString()) ||
                        fromCityTvThree.getText().toString().equals(toCityTvThree.getText().toString())){
                    Toast.makeText(context, "Please select different cities for source and destination.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 4:
                if(fromCityTvOne.getText().toString().length()==0 || toCityTvOne.getText().toString().length()==0 ||
                        fromCityTvTwo.getText().toString().length()==0 || toCityTvTwo.getText().toString().length()==0 ||
                        fromCityTvThree.getText().toString().length()==0 || toCityTvThree.getText().toString().length()==0 ||
                        fromCityTvFour.getText().toString().length()==0 || toCityTvFour.getText().toString().length()==0 ){
                    Toast.makeText(context, "Please select all cities.", Toast.LENGTH_SHORT).show();
                    return false;
                }else if(fromCityTvOne.getText().toString().equals(toCityTvOne.getText().toString()) ||
                        fromCityTvTwo.getText().toString().equals(toCityTvTwo.getText().toString()) ||
                        fromCityTvThree.getText().toString().equals(toCityTvThree.getText().toString()) ||
                        fromCityTvFour.getText().toString().equals(toCityTvFour.getText().toString())){
                    Toast.makeText(context, "Please select different cities for source and destination.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 5:
                if(fromCityTvOne.getText().toString().length()==0 || toCityTvOne.getText().toString().length()==0 ||
                        fromCityTvTwo.getText().toString().length()==0 || toCityTvTwo.getText().toString().length()==0 ||
                        fromCityTvThree.getText().toString().length()==0 || toCityTvThree.getText().toString().length()==0 ||
                        fromCityTvFour.getText().toString().length()==0 || toCityTvFour.getText().toString().length()==0 ||
                        fromCityTvFive.getText().toString().length()==0 || toCityTvFive.getText().toString().length()==0){
                    Toast.makeText(context, "Please select all cities.", Toast.LENGTH_SHORT).show();
                    return false;
                }else if(fromCityTvOne.getText().toString().equals(toCityTvOne.getText().toString()) ||
                        fromCityTvTwo.getText().toString().equals(toCityTvTwo.getText().toString()) ||
                        fromCityTvThree.getText().toString().equals(toCityTvThree.getText().toString()) ||
                        fromCityTvFour.getText().toString().equals(toCityTvFour.getText().toString()) ||
                        fromCityTvFive.getText().toString().equals(toCityTvFive.getText().toString())){
                    Toast.makeText(context, "Please select different cities for source and destination.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    private void searchFlights() {
        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
        myAnim.setInterpolator(interpolator);
        searchTv.startAnimation(myAnim);
        final FlightSearchRequestModel requestModel=makeRequest();
        showCustomDialog();
//        MyCustomDialog.setDialogMessage("Searching Flights...");
        new NetworkCall().callFlightPostService(requestModel, ApiConstants.FLIGHT_SEARCH, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, responseCode, requestModel);
                            hideCustomDialog();
                        }else {
                            hideCustomDialog();
                            responseHandler(response, responseCode, requestModel);
//                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private FlightSearchRequestModel makeRequest() {
        FlightSearchRequestModel requestModel=new FlightSearchRequestModel();

        requestModel.fromCity=flightSourceDataModel.cityName;
        requestModel.toCity=flightDestinationDataModel.cityName;
        requestModel.adultCount=adultCount;
        requestModel.childCount=childCount;
        requestModel.infantCount=infantCount;
        requestModel.cabinClass=cabinClassTv.getText().toString().trim();
        requestModel.journeyType=searchType;
        requestModel.preferredAirlines=null;
        if(searchType.equals(Constants.FlightJourneyType.ONE_WAY)) {
            if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                    flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")){
                requestModel.isDomestic=true;
            }else {
                requestModel.isDomestic=false;
            }
            ArrayList<FlightSearchRequestModel.segments> segmentsArrayList = new ArrayList<>();
            FlightSearchRequestModel.segments segments = requestModel.new segments();
            segments.origin = flightSourceDataModel.cityCode;
            segments.destination = flightDestinationDataModel.cityCode;
            segments.originCityName = flightSourceDataModel.cityName;
            segments.destinationCityName = flightDestinationDataModel.cityName;
            segments.departureDate = dateServerFormat.format(fromDateCalendar.getTime());
            segmentsArrayList.add(segments);
            requestModel.segments = segmentsArrayList;
        }else if(searchType.equals(Constants.FlightJourneyType.RETURN)){
            if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                    flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")){
                requestModel.isDomestic=true;
            }else {
                requestModel.isDomestic=false;
            }
            ArrayList<FlightSearchRequestModel.segments> segmentsArrayList = new ArrayList<>();
            FlightSearchRequestModel.segments segments = requestModel.new segments();
            segments.origin = flightSourceDataModel.cityCode;
            segments.destination = flightDestinationDataModel.cityCode;
            segments.originCityName = flightSourceDataModel.cityName;
            segments.destinationCityName = flightDestinationDataModel.cityName;
            segments.departureDate = dateServerFormat.format(fromDateCalendar.getTime());
            segments.returnDate = dateServerFormat.format(returnDateCalendar.getTime());
            segmentsArrayList.add(segments);
            requestModel.segments = segmentsArrayList;
        }else {
            ArrayList<FlightSearchRequestModel.segments> segmentsArrayList = new ArrayList<>();
            FlightSearchRequestModel.segments segments = requestModel.new segments();
            segments.origin = flightSourceDataModel.cityCode;
            segments.destination = flightDestinationDataModel.cityCode;
            segments.originCityName = flightSourceDataModel.cityName;
            segments.destinationCityName = flightDestinationDataModel.cityName;
            segments.departureDate = dateServerFormat.format(multiDateCalenderOne.getTime());
            segmentsArrayList.add(segments);

            switch (multiCityCount){
                case 2:
                    if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                            flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelTwo.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelTwo.countryCode.equalsIgnoreCase("IN")){
                        requestModel.isDomestic=true;
                    }else {
                        requestModel.isDomestic=false;
                    }
                    addTwoMultiCitiesInRequest(requestModel, segmentsArrayList);
                    break;
                case 3:
                    if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                            flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelTwo.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelTwo.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelThree.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelThree.countryCode.equalsIgnoreCase("IN")){
                        requestModel.isDomestic=true;
                    }else {
                        requestModel.isDomestic=false;
                    }
                    addTwoMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addThreeMultiCitiesInRequest(requestModel, segmentsArrayList);
                    break;
                case 4:
                    if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                            flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelTwo.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelTwo.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelThree.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelThree.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelFour.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelFour.countryCode.equalsIgnoreCase("IN")){
                        requestModel.isDomestic=true;
                    }else {
                        requestModel.isDomestic=false;
                    }
                    addTwoMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addThreeMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addFourMultiCitiesInRequest(requestModel, segmentsArrayList);
                    break;
                case 5:
                    if(flightSourceDataModel.countryCode.equalsIgnoreCase("IN") &&
                            flightDestinationDataModel.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelTwo.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelTwo.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelThree.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelThree.countryCode.equalsIgnoreCase("IN")&&
                            flightSourceCityModelFour.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelFour.countryCode.equalsIgnoreCase("IN")&
                                    flightSourceCityModelFive.countryCode.equalsIgnoreCase("IN") &&
                            flightDestCityModelFive.countryCode.equalsIgnoreCase("IN")){
                        requestModel.isDomestic=true;
                    }else {
                        requestModel.isDomestic=false;
                    }
                    addTwoMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addThreeMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addFourMultiCitiesInRequest(requestModel, segmentsArrayList);
                    addFiveMultiCitiesInRequest(requestModel, segmentsArrayList);
                    break;
            }

            requestModel.segments=segmentsArrayList;
        }
//        save data
        saveFlightRequestToPreferences(requestModel);
        return requestModel;
    }

    private void addTwoMultiCitiesInRequest(FlightSearchRequestModel requestModel, ArrayList<FlightSearchRequestModel.segments> segmentsArrayList) {
        FlightSearchRequestModel.segments segmentsTwo = requestModel.new segments();
        segmentsTwo.origin = flightSourceCityModelTwo.cityCode;
        segmentsTwo.destination = flightDestCityModelTwo.cityCode;
        segmentsTwo.originCityName = flightSourceCityModelTwo.cityName;
        segmentsTwo.destinationCityName = flightDestCityModelTwo.cityName;
        requestModel.toCity=flightDestCityModelTwo.cityName;
        segmentsTwo.departureDate = dateServerFormat.format(multiDateCalenderTwo.getTime());
        segmentsArrayList.add(segmentsTwo);
    }

    private void addThreeMultiCitiesInRequest(FlightSearchRequestModel requestModel, ArrayList<FlightSearchRequestModel.segments> segmentsArrayList) {
        FlightSearchRequestModel.segments segments = requestModel.new segments();
        segments.origin = flightSourceCityModelThree.cityCode;
        segments.destination = flightDestCityModelThree.cityCode;
        segments.originCityName = flightSourceCityModelThree.cityName;
        segments.destinationCityName = flightDestCityModelThree.cityName;
        requestModel.toCity=flightDestCityModelThree.cityName;
        segments.departureDate = dateServerFormat.format(multiDateCalenderThree.getTime());
        segmentsArrayList.add(segments);
    }

    private void addFourMultiCitiesInRequest(FlightSearchRequestModel requestModel, ArrayList<FlightSearchRequestModel.segments> segmentsArrayList) {
        FlightSearchRequestModel.segments segments = requestModel.new segments();
        segments.origin = flightSourceCityModelFour.cityCode;
        segments.destination = flightDestCityModelFour.cityCode;
        segments.originCityName = flightSourceCityModelFour.cityName;
        segments.destinationCityName = flightDestCityModelFour.cityName;
        requestModel.toCity=flightDestCityModelFour.cityName;
        segments.departureDate = dateServerFormat.format(multiDateCalenderFour.getTime());
        segmentsArrayList.add(segments);
    }

    private void addFiveMultiCitiesInRequest(FlightSearchRequestModel requestModel, ArrayList<FlightSearchRequestModel.segments> segmentsArrayList) {
        FlightSearchRequestModel.segments segments = requestModel.new segments();
        segments.origin = flightSourceCityModelFive.cityCode;
        segments.destination = flightDestCityModelFive.cityCode;
        segments.originCityName = flightSourceCityModelFive.cityName;
        segments.destinationCityName = flightDestCityModelFive.cityName;
        requestModel.toCity=flightDestCityModelFive.cityName;
        segments.departureDate = dateServerFormat.format(multiDateCalenderFive.getTime());;
        segmentsArrayList.add(segments);
    }

    private void responseHandler(ResponseBody response, int responseCode, FlightSearchRequestModel requestModel) {
        try {
            FlightSearchResponseModel senderResponse=new FlightSearchResponseModel();
            if(requestModel.isDomestic && requestModel.journeyType.equals(Constants.FlightJourneyType.RETURN)){
//                String response1=HotelUtils.loadJSONFromAsset(context, "dummyJson/domestic_flights.json");
                FlightSearchRoundDomesticResponseModel senderResponseDomestic = new Gson().fromJson(response.string()/*response1*/, FlightSearchRoundDomesticResponseModel.class);
                if(senderResponseDomestic!=null){
                    senderResponse.status=senderResponseDomestic.status;
                    senderResponse.error=senderResponseDomestic.error;
                    senderResponse.sessionId=senderResponseDomestic.sessionId;
                    senderResponse.response=senderResponse.new response();
                    if(senderResponseDomestic.response.flights!=null && senderResponseDomestic.response.flights.size()>0) {
//                        senderResponse.response.flights.addAll(senderResponseDomestic.response.flights.get(0));
                        senderResponse.response.flights=senderResponseDomestic.response.flights.get(0);
                        senderResponse.response.flightsInbound=senderResponseDomestic.response.flights.get(1);
//                        senderResponse.response.flightsInbound.addAll(senderResponseDomestic.response.flights.get(1));
                    }
                }
            }else {
                senderResponse = new Gson().fromJson(response.string(), FlightSearchResponseModel.class);
            }
            if(senderResponse!=null){
                if(responseCode==200 && senderResponse.status == 200) {
                    Bundle bundle = new Bundle();
                    if(senderResponse.response.flights!=null && senderResponse.response.flights.size()>0) {
                        Fragment fragment;
                        fragment=new FlightAvailabilityOneWay();
                        if(searchType.equals(Constants.FlightJourneyType.ONE_WAY)){
                            fragment=new FlightAvailabilityOneWay();
                        }else if(searchType.equals(Constants.FlightJourneyType.RETURN)){
                            if(requestModel.isDomestic){
                                fragment=new FlightAvailabilityRoundDomestic();
                            }else {
                                fragment=new FlightAvailabilityRound();
                            }
                        }/*else {
                            fragment=new FlightAvailabilityMultiCity();
                        }*/
                        bundle.putSerializable("SearchRequest", requestModel);
                        bundle.putSerializable("SearchResponse", senderResponse);
                        bundle.putString("journeyType", searchType);
                        fragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                    }else {
                        Toast.makeText(context,R.string.noFlightsFound, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            showOopsCustomDialog();
//            Toast.makeText(context, R.string.error_in_fetching, Toast.LENGTH_SHORT).show();
        }
    }

    private void oneWayClick() {
        searchType=Constants.FlightJourneyType.ONE_WAY;
        fromCityTvOne.setHint(getResources().getString(R.string.leavingFrom));
        toCityTvOne.setHint(getResources().getString(R.string.goingTo));
        oneWayTv.setTextColor(getResources().getColor(R.color.colorWhite));
        oneWayTv.setBackgroundResource(R.drawable.circular_round_blue_icon);
        roundTripTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        roundTripTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        multiCityTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        multiCityTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        returnDateRel.setEnabled(false);
        returnDateRel.setAlpha(0.3f);
        hideMultiCity();
    }

    private void roundTripClick() {
        searchType=Constants.FlightJourneyType.RETURN;
        fromCityTvOne.setHint(getResources().getString(R.string.leavingFrom));
        toCityTvOne.setHint(getResources().getString(R.string.goingTo));
        oneWayTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        oneWayTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        roundTripTv.setTextColor(getResources().getColor(R.color.colorWhite));
        roundTripTv.setBackgroundResource(R.drawable.circular_round_blue_icon);
        multiCityTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        multiCityTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        returnDateRel.setEnabled(true);
        returnDateRel.setAlpha(1f);
        hideMultiCity();
    }

    private void multiCityClick() {
        searchType=Constants.FlightJourneyType.MULTI_CITY;
        oneWayTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        oneWayTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        roundTripTv.setTextColor(getResources().getColor(R.color.darkGrayColorFlight));
        roundTripTv.setBackgroundResource(R.drawable.circular_round_gray_icon);
        multiCityTv.setTextColor(getResources().getColor(R.color.colorWhite));
        multiCityTv.setBackgroundResource(R.drawable.circular_round_blue_icon);
        showMultiCityView();
    }

    private void hideMultiCity() {
        oneTwoDateLin.setVisibility(View.VISIBLE);
        dateMultiOneRel.setVisibility(View.GONE);
        multiTwoLin.setVisibility(View.GONE);
        multiThreeLin.setVisibility(View.GONE);
        multiFourLin.setVisibility(View.GONE);
        multiFiveLin.setVisibility(View.GONE);
        addRemoveMultiCityLin.setVisibility(View.GONE);
    }

    private void addOrRemoveMultiCity() {
        switch (multiCityCount){
            case 2:
                multiTwoLin.setVisibility(View.VISIBLE);
                multiThreeLin.setVisibility(View.GONE);
                multiFourLin.setVisibility(View.GONE);
                multiFiveLin.setVisibility(View.GONE);
                removeMultiCityTv.setVisibility(View.GONE);
                addMultiCityTv.setVisibility(View.VISIBLE);
                break;
            case 3:
                multiTwoLin.setVisibility(View.VISIBLE);
                multiThreeLin.setVisibility(View.VISIBLE);
                multiFourLin.setVisibility(View.GONE);
                multiFiveLin.setVisibility(View.GONE);
                removeMultiCityTv.setVisibility(View.VISIBLE);
                addMultiCityTv.setVisibility(View.VISIBLE);
                break;
            case 4:
                multiTwoLin.setVisibility(View.VISIBLE);
                multiThreeLin.setVisibility(View.VISIBLE);
                multiFourLin.setVisibility(View.VISIBLE);
                multiFiveLin.setVisibility(View.GONE);
                addMultiCityTv.setVisibility(View.VISIBLE);
                break;
            case 5:
                multiTwoLin.setVisibility(View.VISIBLE);
                multiThreeLin.setVisibility(View.VISIBLE);
                multiFourLin.setVisibility(View.VISIBLE);
                multiFiveLin.setVisibility(View.VISIBLE);
                addMultiCityTv.setVisibility(View.GONE);
                break;
        }
    }

    private void showMultiCityView() {
        dateMultiOneRel.setVisibility(View.VISIBLE);
        addRemoveMultiCityLin.setVisibility(View.VISIBLE);
        oneTwoDateLin.setVisibility(View.GONE);
        fromCityTvOne.setHint(getResources().getString(R.string.flightFrom));
        toCityTvOne.setHint(getResources().getString(R.string.flightTo));
        addOrRemoveMultiCity();
    }

    private void saveFlightRequestToPreferences(FlightSearchRequestModel requestModel) {
        MyPreferences.saveFlightSearchRequestData(requestModel, context);
    }

    private void swapSearch() {
        MyPreferences.setFlightSourceCityData(flightDestinationDataModel, context);
        MyPreferences.setFlightDestinationCityData(flightSourceDataModel, context);
        getAndSetShredPref();
    }

    // call fragment
    private void searchCity(int searchType) {
        if(Common.checkInternetConnection(context)){
            Fragment fragment=new PlacesSearch();
            Bundle bundle = new Bundle();
            bundle.putInt("searchType", searchType);
            fragment.setArguments(bundle);
            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
        }else {
            showInternetCustomDialog();
        }
    }

    private void openFromDateCalender(final int dep) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendarSelect= Calendar.getInstance();
                        calendarSelect.set(year, monthOfYear, dayOfMonth);
                        if (calendarSelect.before(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select this date.", Toast.LENGTH_SHORT).show();
                        }else {
                            fromDateCalendar.set(year, monthOfYear, dayOfMonth);
                            fromDateDay =dayOfMonth;
                            fromDateMonth =monthOfYear;
                            fromDateYear =year;
                            fromDateTvOne.setText(DateAndTimeUtils.getDay(fromDateCalendar.getTime())+"  "+
                                    DateAndTimeUtils.getMonth(fromDateCalendar.getTime())+"'"+
                                    DateAndTimeUtils.getYear(fromDateCalendar.getTime()));
                            setReturnDate();
                        }
                    }

                }, fromDateYear, fromDateMonth, fromDateDay);

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    private void setReturnDate() {
        if(fromDateCalendar.after(returnDateCalendar)){
            returnDateCalendar.setTime(fromDateCalendar.getTime());
            returnDateCalendar.add(Calendar.DAY_OF_MONTH,1);
            returnDateDay =returnDateCalendar.get(Calendar.DAY_OF_MONTH);;
            returnDateMonth =returnDateCalendar.get(Calendar.MONTH);
            returnDateYear =returnDateCalendar.get(Calendar.YEAR);
//            returnDateTvOne.setText(dateServerFormat.format(returnDateCalendar.getTime()));
            returnDateTvOne.setText(DateAndTimeUtils.getDay(returnDateCalendar.getTime())+"  "+
                    DateAndTimeUtils.getMonth(returnDateCalendar.getTime())+"'"+
                    DateAndTimeUtils.getYear(returnDateCalendar.getTime()));
        }
    }

    private void openReturnDateCalender() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendarSelect= Calendar.getInstance();
                        calendarSelect.set(year, monthOfYear, dayOfMonth);
                        if (calendarSelect.before(fromDateCalendar)) {
                            Toast.makeText(context, "Can't select this date.", Toast.LENGTH_SHORT).show();
                        }else {
                            returnDateCalendar.set(year, monthOfYear, dayOfMonth);
                            returnDateDay =dayOfMonth;
                            returnDateMonth =monthOfYear;
                            returnDateYear =year;
//                            returnDateTvOne.setText(dateServerFormat.format(returnDateCalendar.getTime()));
                            returnDateTvOne.setText(DateAndTimeUtils.getDay(returnDateCalendar.getTime())+"  "+
                                    DateAndTimeUtils.getMonth(returnDateCalendar.getTime())+"'"+
                                    DateAndTimeUtils.getYear(returnDateCalendar.getTime()));
                        }
                    }

                }, returnDateYear, returnDateMonth, returnDateDay);

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    private void openMultiDateCalender(final int dateType) {
        int dateYear, dateMonth, dateDay;
        Calendar previousCalender=multiDateCalenderOne, currentCalender=multiDateCalenderOne;
        switch (dateType) {
            case MULTI_DATE_ONE:
                previousCalender=multiDateCalenderOne;
                currentCalender=multiDateCalenderOne;
                break;
            case MULTI_DATE_TWO:
                previousCalender=multiDateCalenderOne;
                currentCalender=multiDateCalenderTwo;
                break;
            case MULTI_DATE_THREE:
                previousCalender=multiDateCalenderTwo;
                currentCalender=multiDateCalenderThree;
                break;
            case MULTI_DATE_FOUR:
                previousCalender=multiDateCalenderThree;
                currentCalender=multiDateCalenderFour;
                break;
            case MULTI_DATE_FIVE:
                previousCalender=multiDateCalenderFour;
                currentCalender=multiDateCalenderFive;
                break;
        }
        dateDay = currentCalender.get(Calendar.DAY_OF_MONTH);
        dateMonth = currentCalender.get(Calendar.MONTH);
        dateYear = currentCalender.get(Calendar.YEAR);
        final Calendar finalPreviousCalender = previousCalender;
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendarSelect= Calendar.getInstance();
                        calendarSelect.set(year, monthOfYear, dayOfMonth);
                        if (calendarSelect.before(finalPreviousCalender)) {
                            Toast.makeText(context, "Can't select this date.", Toast.LENGTH_SHORT).show();
                        }else {
                            switch (dateType){
                                case MULTI_DATE_ONE:
                                    multiDateCalenderOne.set(year, monthOfYear, dayOfMonth);
//                                    dateMultiOneTv.setText(dateServerFormat.format(multiDateCalenderOne.getTime()));
                                    dateMultiOneTv.setText(DateAndTimeUtils.getDay(multiDateCalenderOne.getTime())+"  "+
                                            DateAndTimeUtils.getMonth(multiDateCalenderOne.getTime())+"'"+
                                            DateAndTimeUtils.getYear(multiDateCalenderOne.getTime()));
                                    if(multiDateCalenderOne.after(multiDateCalenderTwo)){
                                        multiDateCalenderTwo.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiTwoTv.setText(dateServerFormat.format(multiDateCalenderTwo.getTime()));
                                        dateMultiTwoTv.setText(DateAndTimeUtils.getDay(multiDateCalenderTwo.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderTwo.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderTwo.getTime()));
                                    }
                                    if(multiDateCalenderOne.after(multiDateCalenderThree)){
                                        multiDateCalenderThree.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiThreeTv.setText(dateServerFormat.format(multiDateCalenderThree.getTime()));
                                        dateMultiThreeTv.setText(DateAndTimeUtils.getDay(multiDateCalenderThree.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderThree.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderThree.getTime()));
                                    }
                                    if(multiDateCalenderOne.after(multiDateCalenderFour)){
                                        multiDateCalenderFour.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFourTv.setText(dateServerFormat.format(multiDateCalenderFour.getTime()));
                                        dateMultiFourTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFour.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFour.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFour.getTime()));
                                    }
                                    if(multiDateCalenderOne.after(multiDateCalenderFive)){
                                        multiDateCalenderFive.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
                                        dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
                                    }
                                    break;
                                case MULTI_DATE_TWO:
                                    multiDateCalenderTwo.set(year, monthOfYear, dayOfMonth);
//                                    dateMultiTwoTv.setText(dateServerFormat.format(multiDateCalenderTwo.getTime()));
                                    dateMultiTwoTv.setText(DateAndTimeUtils.getDay(multiDateCalenderTwo.getTime())+"  "+
                                            DateAndTimeUtils.getMonth(multiDateCalenderTwo.getTime())+"'"+
                                            DateAndTimeUtils.getYear(multiDateCalenderTwo.getTime()));
                                    if(multiDateCalenderTwo.after(multiDateCalenderThree)){
                                        multiDateCalenderThree.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiThreeTv.setText(dateServerFormat.format(multiDateCalenderThree.getTime()));
                                        dateMultiThreeTv.setText(DateAndTimeUtils.getDay(multiDateCalenderThree.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderThree.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderThree.getTime()));
                                    }
                                    if(multiDateCalenderTwo.after(multiDateCalenderFour)){
                                        multiDateCalenderFour.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFourTv.setText(dateServerFormat.format(multiDateCalenderFour.getTime()));
                                        dateMultiFourTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFour.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFour.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFour.getTime()));
                                    }
                                    if(multiDateCalenderTwo.after(multiDateCalenderFive)){
                                        multiDateCalenderFive.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
                                        dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
                                    }
                                    break;
                                case MULTI_DATE_THREE:
                                    multiDateCalenderThree.set(year, monthOfYear, dayOfMonth);
//                                    dateMultiThreeTv.setText(dateServerFormat.format(multiDateCalenderThree.getTime()));
                                    dateMultiThreeTv.setText(DateAndTimeUtils.getDay(multiDateCalenderThree.getTime())+"  "+
                                            DateAndTimeUtils.getMonth(multiDateCalenderThree.getTime())+"'"+
                                            DateAndTimeUtils.getYear(multiDateCalenderThree.getTime()));
                                    if(multiDateCalenderThree.after(multiDateCalenderFour)){
                                        multiDateCalenderFour.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFourTv.setText(dateServerFormat.format(multiDateCalenderThree.getTime()));
                                        dateMultiFourTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFour.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFour.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFour.getTime()));
                                    }
                                    if(multiDateCalenderThree.after(multiDateCalenderFive)){
                                        multiDateCalenderFive.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
                                        dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
                                    }
                                    break;
                                case MULTI_DATE_FOUR:
                                    multiDateCalenderFour.set(year, monthOfYear, dayOfMonth);
//                                    dateMultiFourTv.setText(dateServerFormat.format(multiDateCalenderFour.getTime()));
                                    dateMultiFourTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFour.getTime())+"  "+
                                            DateAndTimeUtils.getMonth(multiDateCalenderFour.getTime())+"'"+
                                            DateAndTimeUtils.getYear(multiDateCalenderFour.getTime()));
                                    if(multiDateCalenderFour.after(multiDateCalenderFive)){
                                        multiDateCalenderFive.set(year, monthOfYear, dayOfMonth);
//                                        dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
                                        dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                                                DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                                                DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
                                    }
                                    break;
                                case MULTI_DATE_FIVE:
                                    multiDateCalenderFive.set(year, monthOfYear, dayOfMonth);
//                                    dateMultiFiveTv.setText(dateServerFormat.format(multiDateCalenderFive.getTime()));
                                    dateMultiFiveTv.setText(DateAndTimeUtils.getDay(multiDateCalenderFive.getTime())+"  "+
                                            DateAndTimeUtils.getMonth(multiDateCalenderFive.getTime())+"'"+
                                            DateAndTimeUtils.getYear(multiDateCalenderFive.getTime()));
                                    break;
                            }
                        }
                    }

                }, dateYear, dateMonth, dateDay);

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    // Dialog for Cabin Class
    private void methodCabinClass() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_class);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        RadioGroup radioGroup =  dialog.findViewById(R.id.rg_class);
        String str_rb = cabinClassTv.getText().toString().trim();
        if(str_rb.equalsIgnoreCase(ANY_CLASS)){
            ((RadioButton)dialog.findViewById(R.id.rb_any)).setChecked(true);
        } else if (str_rb.equalsIgnoreCase(ECONOMY)){
            ((RadioButton)dialog.findViewById(R.id.rb_economy)).setChecked(true);
        } else if (str_rb.equalsIgnoreCase(BUSINESS)){
            ((RadioButton)dialog.findViewById(R.id.rb_business)).setChecked(true);
        } else if (str_rb.equalsIgnoreCase(PREMIUM_ECONOMY)){
            ((RadioButton)dialog.findViewById(R.id.rb_pEconomy)).setChecked(true);
        } else if (str_rb.equalsIgnoreCase(FIRST_CLASS)){
            ((RadioButton)dialog.findViewById(R.id.rb_fClass)).setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_any:
                        cabinClassTv.setText(ANY_CLASS);
                        dialog.dismiss();
                        break;
                    case R.id.rb_economy:
                        cabinClassTv.setText(ECONOMY);
                        dialog.dismiss();
                        break;
                    case R.id.rb_business:
                        cabinClassTv.setText(BUSINESS);
                        dialog.dismiss();
                        break;
                    case R.id.rb_pEconomy:
                        cabinClassTv.setText(PREMIUM_ECONOMY);
                        dialog.dismiss();
                        break;
                    case R.id.rb_fClass:
                        cabinClassTv.setText(FIRST_CLASS);
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.show();
    }

    // Dialog for travellers
    private void methodTravellers() {
        final Dialog dialog_trav = new Dialog(context);
        dialog_trav.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_trav.setContentView(R.layout.dialog_traveller);
        final Window window= dialog_trav.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvAdultCount =  dialog_trav.findViewById(R.id.tvAdultCount);
        tvChildCount = dialog_trav.findViewById(R.id.tvChildCount);
        tvInfantCount =  dialog_trav.findViewById(R.id.tvInfantCount);

        // setting previous values
        tvAdultCount.setText("" + adultCount);
        tvChildCount.setText("" + childCount);
        tvInfantCount.setText("" + infantCount);

        dialog_trav.findViewById(R.id.tvAdultPlus).setOnClickListener(this);
        dialog_trav.findViewById(R.id.tvAdultMinus).setOnClickListener(this);
        dialog_trav.findViewById(R.id.tvChildPlus).setOnClickListener(this);
        dialog_trav.findViewById(R.id.tvChildMinus).setOnClickListener(this);
        dialog_trav.findViewById(R.id.tvInfantPlus).setOnClickListener(this);
        dialog_trav.findViewById(R.id.tvInfantMinus).setOnClickListener(this);

        dialog_trav.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_trav.dismiss();
            }
        });

        dialog_trav.findViewById(R.id.tvDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passengersTv.setText((adultCount + childCount + infantCount) + " Passenger(s)");
                dialog_trav.dismiss();
            }
        });
        dialog_trav.show();
    }

    public void methodAdultMinus() {
        if (adultCount > 1) {
            adultCount -= 1;
            tvAdultCount.setText("" + adultCount);
            if(infantCount > adultCount){
                infantCount = adultCount;
                tvInfantCount.setText("" + infantCount);
            }
        }
    }

    public void methodChildMinus() {
        if (childCount > 0) {
            childCount -= 1;
            tvChildCount.setText("" + childCount);
        }
    }

    public void methodInfantMinus() {
        if (infantCount > 0) {
            infantCount -= 1;
            tvInfantCount.setText("" + infantCount);
        }
    }

    public void methodAdultPlus() {
        if (adultCount+childCount+infantCount < MAX_PASS) {
            adultCount += 1;
            tvAdultCount.setText("" + adultCount);
        }
    }

    public void methodChildPlus() {
        if (adultCount+childCount+infantCount < MAX_PASS) {
            childCount += 1;
            tvChildCount.setText("" + childCount);
        }
    }

    public void methodInfantPlus() {
        if (infantCount < adultCount && adultCount+childCount+infantCount<MAX_PASS) {
            infantCount += 1;
            tvInfantCount.setText("" + infantCount);
        }
    }

   /* private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
    }

    @Override
    public void onCitySearch(FlightCityModel.response data, int searchType) {
        switch (searchType){
            case fromCityOne:
                MyPreferences.setFlightSourceCityData(data, context);
                getAndSetShredPref();
                break;
            case fromCityTwo:
                fromCityTvTwo.setText(data.cityName+" ("+data.cityCode+")");
//                fromMultiCityCodeTwo=data.cityCode;
//                fromMultiCityNameTwo=data.cityName;
                flightSourceCityModelTwo=data;
                break;
            case fromCityThree:
                fromCityTvThree.setText(data.cityName+" ("+data.cityCode+")");
//                fromMultiCityCodeThree=data.cityCode;
//                fromMultiCityNameThree=data.cityName;
                flightSourceCityModelThree=data;
                break;
            case fromCityFour:
                fromCityTvFour.setText(data.cityName+" ("+data.cityCode+")");
//                fromMultiCityCodeFour=data.cityCode;
//                fromMultiCityNameFour=data.cityName;
                flightSourceCityModelFour=data;
                break;
            case fromCityFive:
                fromCityTvFive.setText(data.cityName+" ("+data.cityCode+")");
//                fromMultiCityCodeFive=data.cityCode;
//                fromMultiCityNameFive=data.cityName;
                flightSourceCityModelFive=data;
                break;
            case toCityOne:
                MyPreferences.setFlightDestinationCityData(data, context);
                getAndSetShredPref();

                flightSourceCityModelTwo=data;
                break;
            case toCityTwo:
                toCityTvTwo.setText(data.cityName+" ("+data.cityCode+")");
                fromCityTvThree.setText(data.cityName+" ("+data.cityCode+")");
                flightDestCityModelTwo=data;
                flightSourceCityModelThree=data;
                break;
            case toCityThree:
                toCityTvThree.setText(data.cityName+" ("+data.cityCode+")");
                fromCityTvFour.setText(data.cityName+" ("+data.cityCode+")");
                flightDestCityModelThree=data;
                flightSourceCityModelFour=data;
                break;
            case toCityFour:
                toCityTvFour.setText(data.cityName+" ("+data.cityCode+")");
                fromCityTvFive.setText(data.cityName+" ("+data.cityCode+")");
                flightDestCityModelFour=data;
                flightSourceCityModelFive=data;
                break;
            case toCityFive:
                toCityTvFive.setText(data.cityName+" ("+data.cityCode+")");
                flightDestCityModelFive=data;
                break;

        }
    }
    private void showCustomDialog() {
        HotelSearchDialog.getInstance().showCustomDialog(context,Constants.ModuleType.FLIGHT);
    }
    private void hideCustomDialog() {
        HotelSearchDialog.getInstance().hideCustomDialog();
    }
    private void showInternetCustomDialog() {
        InternetConnectionOffDialog.getInstance().showCustomDialog(context);
    }
    private void showOopsCustomDialog() {
        SomethingWrongDialog.getInstance().showCustomDialog(context);
    }
}
