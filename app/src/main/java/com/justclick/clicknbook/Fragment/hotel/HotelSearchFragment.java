package com.justclick.clicknbook.Fragment.hotel;

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

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.HotelCitySearchAdapter;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.model.HotelCityListModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.HotelAvailabilityRequestModel;
import com.justclick.clicknbook.requestmodels.HotelCityRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyBounceInterpolator;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class HotelSearchFragment extends Fragment implements View.OnClickListener ,AdapterView.OnItemSelectedListener {
    final int CHECK_IN=1, CHECK_OUT=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private TextView totalRoomsTv, paxInfoTv, search_Tv, roomCountTv;
    private ListView cityListView;
    private LinearLayout roomInfoRel;
    private String checkInDate="", checkOutDate="", hotelCode="";
    private SimpleDateFormat dateServerFormat, dateMonthFormat, dayFormat, yearFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear,
            checkOutDateDay, checkOutDateMonth, checkOutDateYear;
    private Calendar checkInDateCalendar, checkOutDateCalendar;
    private TextView departDateTv, returnDateTv, nightTv,
            fromNameTv, returnYearTv, returnDayTv, departDayTv, departYearTv,
            roomAdultCount1, roomAdultCount2, roomAdultCount3, roomAdultCount4,
            roomChildCount1, roomChildCount2, roomChildCount3, roomChildCount4;
    Context context;
    private LoginModel loginModel;
    private int NumberOfRooms=1, NumberOfAdult=1, NumberOfChild=0, NumberOfDays=1,
            Adults1=1, Children1=0, TotalRoom1=1,
            Adults2=1, Children2=0, TotalRoom2=1,
            Adults3=1, Children3=0, TotalRoom3=1,
            Adults4=1, Children4=0, TotalRoom4=1;
    private String  Ages1="", Ages2="", Ages3="", Ages4="";
    private RelativeLayout searchRel;
    private ImageView flight_img,adultImg11, adultImg12, adultImg13, adultImg14, childImg10, childImg11, childImg12, childImg13, childImg14,
            adultImg21, adultImg22, adultImg23, adultImg24, childImg20, childImg21, childImg22, childImg23, childImg24,
            adultImg31, adultImg32, adultImg33, adultImg34, childImg30, childImg31, childImg32, childImg33, childImg34,
            adultImg41, adultImg42, adultImg43, adultImg44, childImg40, childImg41, childImg42, childImg43, childImg44;
    private Spinner childAgeSpinner11, childAgeSpinner12,childAgeSpinner13, childAgeSpinner14,
            childAgeSpinner21, childAgeSpinner22,childAgeSpinner23, childAgeSpinner24,
            childAgeSpinner31, childAgeSpinner32,childAgeSpinner33,childAgeSpinner34,
            childAgeSpinner41, childAgeSpinner42, childAgeSpinner43, childAgeSpinner44;
    private LinearLayout roomTwoLin, roomThreeLin, roomFourLin, childAgeLin11, childAgeLin12, childAgeLin13, childAgeLin14,
            childAgeLin21, childAgeLin22,childAgeLin23, childAgeLin24, childAgeLin31, childAgeLin32,childAgeLin33, childAgeLin34,
            childAgeLin41, childAgeLin42, childAgeLin43, childAgeLin44;
    private HotelCitySearchAdapter hotelCitySearchAdapter;
    private HotelCityListModel hotelCityListModel;
    private HotelCityListModel.CityResponse citySelectedResponse;
    private String childAge11="",childAge12="",childAge21="",childAge22="",childAge31="",
            childAge32="",childAge41="",childAge42="";

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

        hotelCityListModel=new HotelCityListModel();
        citySelectedResponse=hotelCityListModel.new CityResponse();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_hotel_search_new, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.hotelSearchFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
        search_Tv = (TextView) view.findViewById(R.id.search_Tv);
        totalRoomsTv = (TextView) view.findViewById(R.id.totalRoomsTv);
        paxInfoTv = (TextView) view.findViewById(R.id.paxInfoTv);

        fromNameTv= (TextView) view.findViewById(R.id.fromNameTv);
        returnYearTv= (TextView) view.findViewById(R.id.returnYearTv);
        returnDayTv= (TextView) view.findViewById(R.id.returnDayTv);
        departDayTv= (TextView) view.findViewById(R.id.departDayTv);
        departYearTv= (TextView) view.findViewById(R.id.departYearTv);
        departDateTv= (TextView) view.findViewById(R.id.departDateTv);
        returnDateTv= (TextView) view.findViewById(R.id.returnDateTv);
        nightTv= (TextView) view.findViewById(R.id.nightTv);
        roomAdultCount1= (TextView) view.findViewById(R.id.roomAdultCount1);
        roomAdultCount2= (TextView) view.findViewById(R.id.roomAdultCount2);
        roomAdultCount3= (TextView) view.findViewById(R.id.roomAdultCount3);
        roomAdultCount4= (TextView) view.findViewById(R.id.roomAdultCount4);
        roomChildCount1= (TextView) view.findViewById(R.id.roomChildCount1);
        roomChildCount2= (TextView) view.findViewById(R.id.roomChildCount2);
        roomChildCount3= (TextView) view.findViewById(R.id.roomChildCount3);
        roomChildCount4= (TextView) view.findViewById(R.id.roomChildCount4);
        searchRel= (RelativeLayout) view.findViewById(R.id.searchRel);
        flight_img= (ImageView) view.findViewById(R.id.flight_img);

        cityListView = (ListView) view.findViewById(R.id.cityListView);
//        roomInfoRel = (LinearLayout) view.findViewById(R.id.roomInfoRel);

        openRoomInfoDialog(view);

        initializeDates();
        setFont(view);

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });


//        roomInfoRel.setOnClickListener(this);
        view.findViewById(R.id.returnDateLin).setOnClickListener(this);
        view.findViewById(R.id.departDateLin).setOnClickListener(this);
        view.findViewById(R.id.hotelCitySearchRel).setOnClickListener(this);
        view.findViewById(R.id.searchRel).setOnClickListener(this);

       /* hotelCitySearchAdapter = new HotelCitySearchAdapter(context, hotelCityListModel);
        cityListView.setAdapter(hotelCitySearchAdapter);

        cityListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        citySelectedResponse = hotelCitySearchAdapter.getItem(position);
//                        agent_name_tv.setText(agentName);
                        cityListView.setVisibility(View.GONE);
                        destinationCityEdt.setText(citySelectedResponse.CityName);
                        destinationCityEdt.setSelection(destinationCityEdt.getText().length());
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });*/
       /* destinationCityEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Common.checkInternetConnection(context)) {
                    if(s.length()>=2) {
                        HotelCityRequestModel model=new HotelCityRequestModel();
                        model.Name=s.toString();

                        if(citySelectedResponse.CityName==null ||
                                !citySelectedResponse.CityName.equals(s.toString().trim())){
                            hotelCity(model);
                        }
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        return view;
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

        //set default date
        departDateTv.setText(dateMonthFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        departDayTv.setText(dayFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        departYearTv.setText(yearFormat.format(checkInDateCalendar.getTime()).toUpperCase());
        returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
        returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
        returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());

    }

    private void setFont(View view) {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.OpenSansRegularTypeFace(context);
        departDateTv.setTypeface(face2);
        returnDateTv.setTypeface(face2);
        fromNameTv.setTypeface(face);
        search_Tv.setTypeface(face1);
        ((TextView)view.findViewById(R.id.fromTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.departLabel)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.roomInfoLabel)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.roomLabel1)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.roomLabel2)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.roomLabel3)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.roomLabel4)).setTypeface(face1);

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
//                        calculateNights();
                        nightTv.setText(getDays()+"\nNight");
                    }

                }, checkInDateYear, checkInDateMonth, checkInDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    private void calculateNights() {
//        long difference = today.getTimeInMillis() - calendar.getTimeInMillis();
//        int days = (int) (difference/ (1000*60*60*24));
    }

    private void openCheckOutDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        checkOutDateCalendar.set(year, monthOfYear, dayOfMonth);
                        checkOutDate= dateServerFormat.format(checkOutDateCalendar.getTime());
                        checkOutDateDay = dayOfMonth;
                        checkOutDateMonth = monthOfYear;
                        checkOutDateYear = year;
                        returnDateTv.setText(dateMonthFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
                        returnDayTv.setText(dayFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
                        returnYearTv.setText(yearFormat.format(checkOutDateCalendar.getTime()).toUpperCase());
                        nightTv.setText(getDays()+"\nNight");
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

    public HotelCityListModel hotelCity(HotelCityRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<HotelCityListModel> call = apiService.getHotelCityPost(ApiConstants.CityCountry, model);
        call.enqueue(new Callback<HotelCityListModel>() {
            @Override
            public void onResponse(Call<HotelCityListModel> call,Response<HotelCityListModel> response) {
                try {
                    hotelCityListModel = response.body();
                    if(hotelCityListModel!=null && hotelCityListModel.CityResponse.size()>0){
//                    if(hotelCityListModel.StatusCode.equalsIgnoreCase("0")) {
                        hotelCitySearchAdapter = new HotelCitySearchAdapter(context, hotelCityListModel);
                        cityListView.setAdapter(hotelCitySearchAdapter);
                        cityListView.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(Call<HotelCityListModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return hotelCityListModel;
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hotelCitySearchRel:
//                Intent i=new Intent(context,BusFromCityActivity.class);
//                startActivityForResult(i,1);
                Toast.makeText(context, "city can't search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.departDateLin:
                openCheckInDatePicker();
                break;
            case R.id.returnDateLin:
                openCheckOutDatePicker();
                break;
            case R.id.roomInfoRel:
                try{
//                    openRoomInfoDialog();
                }catch (Exception e){

                }
                break;
            case R.id.searchRel:
                Common.preventFrequentClick(searchRel);
                final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);

                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);

                flight_img.startAnimation(myAnim);

                Bundle bundle=new Bundle();
                bundle.putSerializable("HotelList",new HotelAvailabilityResponseModel().hotels);
                bundle.putSerializable("CheckInDateCalander",checkInDateCalendar);
                bundle.putSerializable("CheckOutDateCalander",checkOutDateCalendar);
                HotelListWithMapFragment fragment=new HotelListWithMapFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                hideCustomDialog();
                if(/*destinationCityEdt.getText().toString().length()<3 || */citySelectedResponse.CityName==null) {
//                    Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_LONG).show();
                }else{
                    HotelAvailabilityRequestModel hotelAvailabilityRequestModel=new HotelAvailabilityRequestModel();
                    hotelAvailabilityRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    hotelAvailabilityRequestModel.DeviceId=Common.getDeviceId(context);
                    hotelAvailabilityRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    hotelAvailabilityRequestModel.CheckInDate=checkInDate;
                    hotelAvailabilityRequestModel.CheckOutDate=checkOutDate;
                    hotelAvailabilityRequestModel.CountryName=citySelectedResponse.Country;
                    hotelAvailabilityRequestModel.DestnationName=citySelectedResponse.CityName;
                    hotelAvailabilityRequestModel.DestnationCode="";
                    hotelAvailabilityRequestModel.NumberOfAdult=NumberOfAdult+"";
                    hotelAvailabilityRequestModel.NumberOfChild=NumberOfChild+"";
                    hotelAvailabilityRequestModel.NumberOfDays=getDays();
                    hotelAvailabilityRequestModel.NumberOfRooms=NumberOfRooms+"";
                    hotelAvailabilityRequestModel.Supplier="TBO";
                    hotelAvailabilityRequestModel.StaRating="All";
                    hotelAvailabilityRequestModel.RoomOccupancy=getRoomArrayList();
//                    hotelSearch(hotelAvailabilityRequestModel);
                }

                break;

            case R.id.roomPlusTv:
                roomPlusClick();
                ObjectAnimator.ofObject(v.findViewById(R.id.roomPlusTv),  "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;
            case R.id.roomMinusTv:
                roomMinusClick();
                ObjectAnimator.ofObject(v.findViewById(R.id.roomMinusTv), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;
            case R.id.adultMinusImg1:
                adultMinusClick(1);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultMinusImg1), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;
            case R.id.adultPlusImg1:
                adultPlusClick(1, true);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultPlusImg1), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;
            case R.id.childMinusImg1:
                childImageMinusClick(1);
                ObjectAnimator.ofObject(v.findViewById(R.id.childMinusImg1), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;
            case R.id.childPlusImg1:
                childImagePlusClick(1);
                ObjectAnimator.ofObject(v.findViewById(R.id.childPlusImg1), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultMinusImg2:
                adultMinusClick(2);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultMinusImg2), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultPlusImg2:
                adultPlusClick(2, true);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultPlusImg2), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childMinusImg2:
                childImageMinusClick(2);
                ObjectAnimator.ofObject(v.findViewById(R.id.childMinusImg2), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childPlusImg2:
                childImagePlusClick(2);
                ObjectAnimator.ofObject(v.findViewById(R.id.childPlusImg2), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultMinusImg3:
                adultMinusClick(3);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultMinusImg3), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultPlusImg3:
                adultPlusClick(3, true);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultPlusImg3), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childMinusImg3:
                childImageMinusClick(3);
                ObjectAnimator.ofObject(v.findViewById(R.id.childMinusImg3), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childPlusImg3:
                childImagePlusClick(3);
                ObjectAnimator.ofObject(v.findViewById(R.id.childPlusImg3), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultMinusImg4:
                adultMinusClick(4);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultMinusImg4), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.adultPlusImg4:
                adultPlusClick(4, true);
                ObjectAnimator.ofObject(v.findViewById(R.id.adultPlusImg4), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childMinusImg4:
                childImageMinusClick(4);
                ObjectAnimator.ofObject(v.findViewById(R.id.childMinusImg4), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

            case R.id.childPlusImg4:
                childImagePlusClick(4);
                ObjectAnimator.ofObject(v.findViewById(R.id.childPlusImg4), "backgroundColor", (Object)new ArgbEvaluator(),
                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
                break;

        }


    }
    public String getDays()
    {
        return String.valueOf((int)( (checkOutDateCalendar.getTimeInMillis() - checkInDateCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24)));
    }

    private void adultPlusClick(int room, boolean isToast) {
        switch (room){
            case 1:
                if(Adults1==1){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    Adults1++;
                }else if(Adults1==2){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.VISIBLE);
                    Adults1++;
                }else if(Adults1==3){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.VISIBLE);
                    adultImg14.setVisibility(View.VISIBLE);
                    Adults1++;
                }else{
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.VISIBLE);
                    adultImg14.setVisibility(View.VISIBLE);
                    if(isToast){
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT).show();
                    }
                }
                roomAdultCount1.setText(Adults1+"");
                break;
            case 2:
                if(Adults2==1){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    Adults2++;
                }else if(Adults2==2){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    Adults2++;
                }else if(Adults2==3){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    adultImg24.setVisibility(View.VISIBLE);
                    Adults2++;
                }else if(Adults2==4){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    adultImg24.setVisibility(View.VISIBLE);
                    if(isToast){
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    adultImg24.setVisibility(View.VISIBLE);
                }
                roomAdultCount2.setText(Adults2+"");
                break;
            case 3:
                if(Adults3==1){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    Adults3++;
                }else if(Adults3==2){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    Adults3++;
                }else if(Adults3==3){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    adultImg34.setVisibility(View.VISIBLE);
                    Adults3++;
                }else if(Adults3==4){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    adultImg34.setVisibility(View.VISIBLE);
                    if(isToast){
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    adultImg34.setVisibility(View.VISIBLE);
                }
                roomAdultCount3.setText(Adults3+"");
                break;
            case 4:
                if(Adults4==1){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    Adults4++;
                }else if(Adults4==2){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    Adults4++;
                }else if(Adults4==3){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    adultImg44.setVisibility(View.VISIBLE);
                    Adults4++;
                }else if(Adults4==4){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    adultImg44.setVisibility(View.VISIBLE);
                    if(isToast){
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    adultImg44.setVisibility(View.VISIBLE);
                }
                roomAdultCount4.setText(Adults4+"");
                break;
        }
    }

    private void adultMinusClick(int room) {
        switch (room){
            case 1:
                if(Adults1==2){
                    adultImg12.setVisibility(View.GONE);
                    adultImg13.setVisibility(View.GONE);
                    adultImg14.setVisibility(View.GONE);
                    Adults1--;
                }else if(Adults1==3){
                    adultImg13.setVisibility(View.GONE);
                    adultImg14.setVisibility(View.GONE);
                    Adults1--;
                }else if(Adults1==4){
                    adultImg14.setVisibility(View.GONE);
                    Adults1--;
                }else {
                    roomAdultCount1.setText(Adults1 + "");
                }
                roomAdultCount1.setText(Adults1 + "");
                break;
            case 2:
                if(Adults2==2){
                    adultImg22.setVisibility(View.GONE);
                    adultImg23.setVisibility(View.GONE);
                    adultImg24.setVisibility(View.GONE);
                    Adults2--;
                }else if(Adults2==3){
                    adultImg23.setVisibility(View.GONE);
                    adultImg24.setVisibility(View.GONE);
                    Adults2--;
                }else if(Adults2==4){
                    adultImg24.setVisibility(View.GONE);
                    Adults2--;
                }else {
                    roomAdultCount2.setText(Adults2+"");
                }
                roomAdultCount2.setText(Adults2+"");
                break;
            case 3:
                if(Adults3==2){
                    adultImg32.setVisibility(View.GONE);
                    adultImg33.setVisibility(View.GONE);
                    adultImg34.setVisibility(View.GONE);
                    Adults3--;
                }else if(Adults3==3){
                    adultImg33.setVisibility(View.GONE);
                    adultImg34.setVisibility(View.GONE);
                    Adults3--;
                }else if(Adults3==4){
                    adultImg34.setVisibility(View.GONE);
                    Adults3--;
                }else {
                    roomAdultCount3.setText(Adults3+"");
                }
                roomAdultCount3.setText(Adults3+"");
                break;
            case 4:
                if(Adults4==2){
                    adultImg42.setVisibility(View.GONE);
                    adultImg43.setVisibility(View.GONE);
                    adultImg44.setVisibility(View.GONE);
                    Adults4--;
                }else if(Adults4==3){
                    adultImg43.setVisibility(View.GONE);
                    adultImg44.setVisibility(View.GONE);
                    Adults4--;
                }else if(Adults4==4){
                    adultImg44.setVisibility(View.GONE);
                    Adults4--;
                }else {
                    roomAdultCount4.setText(Adults4+"");
                }
                roomAdultCount4.setText(Adults4+"");
                break;
        }
    }

    private void roomMinusClick() {
        if(NumberOfRooms==2){
            roomTwoLin.setVisibility(View.GONE);
            NumberOfRooms--;
        }else if(NumberOfRooms==3){
            roomThreeLin.setVisibility(View.GONE);
            NumberOfRooms--;
        }else if(NumberOfRooms==4){
            roomFourLin.setVisibility(View.GONE);
            NumberOfRooms--;
        }
        roomCountTv.setText(NumberOfRooms+"");
    }

    private void roomPlusClick() {
        if(NumberOfRooms==1){
            roomTwoLin.setVisibility(View.VISIBLE);
            NumberOfRooms++;
        }else if(NumberOfRooms==2){
            roomThreeLin.setVisibility(View.VISIBLE);
            NumberOfRooms++;
        }else if(NumberOfRooms==3){
            roomFourLin.setVisibility(View.VISIBLE);
            NumberOfRooms++;
        }else {
            Toast.makeText(context, R.string.hotel_max_room, Toast.LENGTH_SHORT).show();
        }
        roomCountTv.setText(NumberOfRooms+"");
    }

    private void childImagePlusClick(int i) {
        switch (i){
            case 1:
                if(Children1==0) {
                    childImg10.setVisibility(View.GONE);
                    childImg11.setVisibility(View.VISIBLE);
                    childAgeLin11.setVisibility(View.VISIBLE);
                    childAgeLin12.setVisibility(View.GONE);
                    Children1++;
                }else if(Children1==1){
                    childImg12.setVisibility(View.VISIBLE);
                    childAgeLin12.setVisibility(View.VISIBLE);
                    Children1++;
                }else if(Children1==2){
                    childImg13.setVisibility(View.VISIBLE);
                    childAgeLin13.setVisibility(View.VISIBLE);
                    Children1++;
                }else if(Children1==3){
                    childImg14.setVisibility(View.VISIBLE);
                    childAgeLin14.setVisibility(View.VISIBLE);
                    Children1++;
                }
                roomChildCount1.setText(Children1+"");
                break;
            case 2:
                if(Children2==0) {
                    childImg20.setVisibility(View.GONE);
                    childImg21.setVisibility(View.VISIBLE);
                    childAgeLin21.setVisibility(View.VISIBLE);
                    childAgeLin22.setVisibility(View.GONE);
                    Children2++;
                }else if(Children2==1){
                    childImg22.setVisibility(View.VISIBLE);
                    childAgeLin22.setVisibility(View.VISIBLE);
                    Children2++;
                }else if(Children2==2){
                    childImg23.setVisibility(View.VISIBLE);
                    childAgeLin23.setVisibility(View.VISIBLE);
                    Children2++;
                }else if(Children2==3){
                    childImg24.setVisibility(View.VISIBLE);
                    childAgeLin24.setVisibility(View.VISIBLE);
                    Children2++;
                }
                roomChildCount2.setText(Children2+"");
                break;
            case 3:
                if(Children3==0) {
                    childImg30.setVisibility(View.GONE);
                    childImg31.setVisibility(View.VISIBLE);
                    childAgeLin31.setVisibility(View.VISIBLE);
                    childAgeLin32.setVisibility(View.GONE);
                    Children3++;
                }else if(Children3==1){
                    childImg32.setVisibility(View.VISIBLE);
                    childAgeLin32.setVisibility(View.VISIBLE);
                    Children3++;
                }else if(Children3==2){
                    childImg33.setVisibility(View.VISIBLE);
                    childAgeLin33.setVisibility(View.VISIBLE);
                    Children3++;
                }else if(Children3==3){
                    childImg34.setVisibility(View.VISIBLE);
                    childAgeLin34.setVisibility(View.VISIBLE);
                    Children3++;
                }
                roomChildCount3.setText(Children3+"");
                break;
            case 4:
                if(Children4==0) {
                    childImg40.setVisibility(View.GONE);
                    childImg41.setVisibility(View.VISIBLE);
                    childAgeLin41.setVisibility(View.VISIBLE);
                    childAgeLin42.setVisibility(View.GONE);
                    Children4++;
                }else if(Children4==1){
                    childImg42.setVisibility(View.VISIBLE);
                    childAgeLin42.setVisibility(View.VISIBLE);
                    Children4++;
                }else if(Children4==2){
                    childImg43.setVisibility(View.VISIBLE);
                    childAgeLin43.setVisibility(View.VISIBLE);
                    Children4++;
                }else if(Children4==3){
                    childImg44.setVisibility(View.VISIBLE);
                    childAgeLin44.setVisibility(View.VISIBLE);
                    Children4++;
                }
                roomChildCount4.setText(Children4+"");
                break;
        }
    }

    private void childImageMinusClick(int i) {
        switch (i){
            case 1:
                if(Children1==1) {
                    childImg10.setVisibility(View.VISIBLE);
                    childImg11.setVisibility(View.GONE);
                    childImg12.setVisibility(View.GONE);
                    childAgeLin11.setVisibility(View.GONE);
                    Children1--;
                }else if(Children1==2){
                    childImg10.setVisibility(View.GONE);
                    childImg11.setVisibility(View.VISIBLE);
                    childImg12.setVisibility(View.GONE);
                    childAgeLin11.setVisibility(View.VISIBLE);
                    childAgeLin12.setVisibility(View.GONE);
                    Children1--;
                }else if(Children1==3){
                    childImg13.setVisibility(View.GONE);
                    childAgeLin13.setVisibility(View.GONE);
                    Children1--;
                }else if(Children1==4){
                    childImg14.setVisibility(View.GONE);
                    childAgeLin14.setVisibility(View.GONE);
                    Children1--;
                }else {
                    childImg10.setVisibility(View.VISIBLE);
                    childImg11.setVisibility(View.GONE);
                    childImg12.setVisibility(View.GONE);
                    childAgeLin11.setVisibility(View.GONE);
                }
                roomChildCount1.setText(Children1+"");
                break;
            case 2:
                if(Children2==1) {
                    childImg20.setVisibility(View.VISIBLE);
                    childImg21.setVisibility(View.GONE);
                    childImg22.setVisibility(View.GONE);
                    childAgeLin21.setVisibility(View.GONE);
                    Children2--;
                }else if(Children2==2){
                    childImg20.setVisibility(View.GONE);
                    childImg21.setVisibility(View.VISIBLE);
                    childImg22.setVisibility(View.GONE);
                    childAgeLin21.setVisibility(View.VISIBLE);
                    childAgeLin22.setVisibility(View.GONE);
                    Children2--;
                }else if(Children2==3){
                    childImg23.setVisibility(View.GONE);
                    childAgeLin23.setVisibility(View.GONE);
                    Children2--;
                }else if(Children2==4){
                    childImg24.setVisibility(View.GONE);
                    childAgeLin24.setVisibility(View.GONE);
                    Children2--;
                }else {
                    childImg20.setVisibility(View.VISIBLE);
                    childImg21.setVisibility(View.GONE);
                    childImg22.setVisibility(View.GONE);
                    childAgeLin21.setVisibility(View.GONE);
                }
                roomChildCount2.setText(Children2+"");
                break;
            case 3:
                if(Children3==1) {
                    childImg30.setVisibility(View.VISIBLE);
                    childImg31.setVisibility(View.GONE);
                    childImg32.setVisibility(View.GONE);
                    childAgeLin31.setVisibility(View.GONE);
                    Children3--;
                }else if(Children3==2){
                    childImg30.setVisibility(View.GONE);
                    childImg31.setVisibility(View.VISIBLE);
                    childImg32.setVisibility(View.GONE);
                    childAgeLin31.setVisibility(View.VISIBLE);
                    childAgeLin32.setVisibility(View.GONE);
                    Children3--;
                }else if(Children3==3){
                    childImg33.setVisibility(View.GONE);
                    childAgeLin33.setVisibility(View.GONE);
                    Children3--;
                }else if(Children3==4){
                    childImg34.setVisibility(View.GONE);
                    childAgeLin34.setVisibility(View.GONE);
                    Children3--;
                }else {
                    childImg30.setVisibility(View.VISIBLE);
                    childImg31.setVisibility(View.GONE);
                    childImg32.setVisibility(View.GONE);
                    childAgeLin31.setVisibility(View.GONE);
                }
                roomChildCount3.setText(Children3+"");
                break;
            case 4:
                if(Children4==1) {
                    childImg40.setVisibility(View.VISIBLE);
                    childImg41.setVisibility(View.GONE);
                    childImg42.setVisibility(View.GONE);
                    childAgeLin41.setVisibility(View.GONE);
                    Children4--;
                }else if(Children4==2){
                    childImg40.setVisibility(View.GONE);
                    childImg41.setVisibility(View.VISIBLE);
                    childImg42.setVisibility(View.GONE);
                    childAgeLin41.setVisibility(View.VISIBLE);
                    childAgeLin42.setVisibility(View.GONE);
                    Children4--;
                }else if(Children4==3){
                    childImg43.setVisibility(View.GONE);
                    childAgeLin43.setVisibility(View.GONE);
                    Children4--;
                }else if(Children4==4){
                    childImg44.setVisibility(View.GONE);
                    childAgeLin44.setVisibility(View.GONE);
                    Children4--;
                }else {
                    childImg40.setVisibility(View.VISIBLE);
                    childImg41.setVisibility(View.GONE);
                    childImg42.setVisibility(View.GONE);
                    childAgeLin41.setVisibility(View.GONE);
                }
                roomChildCount4.setText(Children4+"");
                break;
        }
    }

    private void openRoomInfoDialog(View dialog) {

        roomCountTv= (TextView) dialog.findViewById(R.id.roomCountTv);
        roomTwoLin= (LinearLayout) dialog.findViewById(R.id.roomTwoLin);
        roomThreeLin= (LinearLayout) dialog.findViewById(R.id.roomThreeLin);
        roomFourLin= (LinearLayout) dialog.findViewById(R.id.roomFourLin);

        adultImg11= (ImageView) dialog.findViewById(R.id.adultImg11);
        adultImg12= (ImageView) dialog.findViewById(R.id.adultImg12);
        adultImg13= (ImageView) dialog.findViewById(R.id.adultImg13);
        adultImg14= (ImageView) dialog.findViewById(R.id.adultImg14);
        childImg10 = (ImageView) dialog.findViewById(R.id.childImg11);
        childImg11 = (ImageView) dialog.findViewById(R.id.childImg12);
        childImg12 = (ImageView) dialog.findViewById(R.id.childImg13);
        childImg13 = (ImageView) dialog.findViewById(R.id.childImg14);
        childImg14 = (ImageView) dialog.findViewById(R.id.childImg15);
        childAgeSpinner11= (Spinner) dialog.findViewById(R.id.childAgeSpinner11);
        childAgeSpinner12= (Spinner) dialog.findViewById(R.id.childAgeSpinner12);
        childAgeSpinner13= (Spinner) dialog.findViewById(R.id.childAgeSpinner13);
        childAgeSpinner14= (Spinner) dialog.findViewById(R.id.childAgeSpinner14);
        childAgeSpinner14.setOnItemSelectedListener(this);
        childAgeSpinner13.setOnItemSelectedListener(this);
        childAgeSpinner12.setOnItemSelectedListener(this);
        childAgeSpinner11.setOnItemSelectedListener(this);
        childAgeLin11 = (LinearLayout) dialog.findViewById(R.id.childAgeLin1);
        childAgeLin12= (LinearLayout) dialog.findViewById(R.id.childAgeLin12);
        childAgeLin13= (LinearLayout) dialog.findViewById(R.id.childAgeLin13);
        childAgeLin14= (LinearLayout) dialog.findViewById(R.id.childAgeLin14);


        adultImg21= (ImageView) dialog.findViewById(R.id.adultImg21);
        adultImg22= (ImageView) dialog.findViewById(R.id.adultImg22);
        adultImg23= (ImageView) dialog.findViewById(R.id.adultImg23);
        adultImg24= (ImageView) dialog.findViewById(R.id.adultImg24);
        childImg20 = (ImageView) dialog.findViewById(R.id.childImg21);
        childImg21 = (ImageView) dialog.findViewById(R.id.childImg22);
        childImg22 = (ImageView) dialog.findViewById(R.id.childImg23);
        childImg23 = (ImageView) dialog.findViewById(R.id.childImg24);
        childImg24 = (ImageView) dialog.findViewById(R.id.childImg25);
        childAgeSpinner21= (Spinner) dialog.findViewById(R.id.childAgeSpinner21);
        childAgeSpinner22= (Spinner) dialog.findViewById(R.id.childAgeSpinner22);
        childAgeSpinner23= (Spinner) dialog.findViewById(R.id.childAgeSpinner23);
        childAgeSpinner24= (Spinner) dialog.findViewById(R.id.childAgeSpinner24);
        childAgeSpinner21.setOnItemSelectedListener(this);
        childAgeSpinner22.setOnItemSelectedListener(this);
        childAgeSpinner23.setOnItemSelectedListener(this);
        childAgeSpinner24.setOnItemSelectedListener(this);
        childAgeLin21 = (LinearLayout) dialog.findViewById(R.id.childAgeLin2);
        childAgeLin22= (LinearLayout) dialog.findViewById(R.id.childAgeLin22);
        childAgeLin23= (LinearLayout) dialog.findViewById(R.id.childAgeLin23);
        childAgeLin24= (LinearLayout) dialog.findViewById(R.id.childAgeLin24);

        adultImg31= (ImageView) dialog.findViewById(R.id.adultImg31);
        adultImg32= (ImageView) dialog.findViewById(R.id.adultImg32);
        adultImg33= (ImageView) dialog.findViewById(R.id.adultImg33);
        adultImg34= (ImageView) dialog.findViewById(R.id.adultImg34);
        childImg30 = (ImageView) dialog.findViewById(R.id.childImg31);
        childImg31 = (ImageView) dialog.findViewById(R.id.childImg32);
        childImg32 = (ImageView) dialog.findViewById(R.id.childImg33);
        childImg33 = (ImageView) dialog.findViewById(R.id.childImg34);
        childImg34 = (ImageView) dialog.findViewById(R.id.childImg35);
        childAgeSpinner31= (Spinner) dialog.findViewById(R.id.childAgeSpinner31);
        childAgeSpinner32= (Spinner) dialog.findViewById(R.id.childAgeSpinner32);
        childAgeSpinner33= (Spinner) dialog.findViewById(R.id.childAgeSpinner33);
        childAgeSpinner34= (Spinner) dialog.findViewById(R.id.childAgeSpinner34);
        childAgeSpinner31.setOnItemSelectedListener(this);
        childAgeSpinner32.setOnItemSelectedListener(this);
        childAgeSpinner33.setOnItemSelectedListener(this);
        childAgeSpinner34.setOnItemSelectedListener(this);
        childAgeLin31 = (LinearLayout) dialog.findViewById(R.id.childAgeLin3);
        childAgeLin32= (LinearLayout) dialog.findViewById(R.id.childAgeLin32);
        childAgeLin33= (LinearLayout) dialog.findViewById(R.id.childAgeLin33);
        childAgeLin34= (LinearLayout) dialog.findViewById(R.id.childAgeLin34);

        adultImg41= (ImageView) dialog.findViewById(R.id.adultImg41);
        adultImg42= (ImageView) dialog.findViewById(R.id.adultImg42);
        adultImg43= (ImageView) dialog.findViewById(R.id.adultImg43);
        adultImg44= (ImageView) dialog.findViewById(R.id.adultImg44);
        childImg40 = (ImageView) dialog.findViewById(R.id.childImg41);
        childImg41 = (ImageView) dialog.findViewById(R.id.childImg42);
        childImg42 = (ImageView) dialog.findViewById(R.id.childImg43);
        childImg43 = (ImageView) dialog.findViewById(R.id.childImg44);
        childImg44 = (ImageView) dialog.findViewById(R.id.childImg45);
        childAgeSpinner41= (Spinner) dialog.findViewById(R.id.childAgeSpinner41);
        childAgeSpinner42= (Spinner) dialog.findViewById(R.id.childAgeSpinner42);
        childAgeSpinner43= (Spinner) dialog.findViewById(R.id.childAgeSpinner43);
        childAgeSpinner44= (Spinner) dialog.findViewById(R.id.childAgeSpinner44);
        childAgeSpinner41.setOnItemSelectedListener(this);
        childAgeSpinner42.setOnItemSelectedListener(this);
        childAgeSpinner43.setOnItemSelectedListener(this);
        childAgeSpinner44.setOnItemSelectedListener(this);
        childAgeLin41 = (LinearLayout) dialog.findViewById(R.id.childAgeLin4);
        childAgeLin42= (LinearLayout) dialog.findViewById(R.id.childAgeLin42);
        childAgeLin43= (LinearLayout) dialog.findViewById(R.id.childAgeLin43);
        childAgeLin44= (LinearLayout) dialog.findViewById(R.id.childAgeLin44);


        dialog.findViewById(R.id.roomPlusTv).setOnClickListener(this);
        dialog.findViewById(R.id.roomMinusTv).setOnClickListener(this);

        dialog.findViewById(R.id.adultMinusImg1).setOnClickListener(this);
        dialog.findViewById(R.id.adultPlusImg1).setOnClickListener(this);
        dialog.findViewById(R.id.childPlusImg1).setOnClickListener(this);
        dialog.findViewById(R.id.childMinusImg1).setOnClickListener(this);

        dialog.findViewById(R.id.adultMinusImg2).setOnClickListener(this);
        dialog.findViewById(R.id.adultPlusImg2).setOnClickListener(this);
        dialog.findViewById(R.id.childPlusImg2).setOnClickListener(this);
        dialog.findViewById(R.id.childMinusImg2).setOnClickListener(this);

        dialog.findViewById(R.id.adultMinusImg3).setOnClickListener(this);
        dialog.findViewById(R.id.adultPlusImg3).setOnClickListener(this);
        dialog.findViewById(R.id.childPlusImg3).setOnClickListener(this);
        dialog.findViewById(R.id.childMinusImg3).setOnClickListener(this);

        dialog.findViewById(R.id.adultMinusImg4).setOnClickListener(this);
        dialog.findViewById(R.id.adultPlusImg4).setOnClickListener(this);
        dialog.findViewById(R.id.childPlusImg4).setOnClickListener(this);
        dialog.findViewById(R.id.childMinusImg4).setOnClickListener(this);

        roomDefaultValues();

    }

    private void setValues() {
        if(NumberOfRooms==1){
            paxInfoTv.setText(Adults1+" Adult, "+Children1+ " Child");
            NumberOfAdult=Adults1;
            NumberOfChild=Children1;
        }else if(NumberOfRooms==2){
            paxInfoTv.setText((Adults1+Adults2)+" Adult, "+(Children1+Children2)+ " Child");
            NumberOfAdult=Adults1+Adults2;
            NumberOfChild=Children1+Children2;
        }else if(NumberOfRooms==3){
            paxInfoTv.setText((Adults1+Adults2+Adults3)+" Adult, "+(Children1+Children2+Children3)+ " Child");
            NumberOfAdult=Adults1+Adults2+Adults3;
            NumberOfChild=Children1+Children2+Children3;
        }else {
            paxInfoTv.setText((Adults1+Adults2+Adults3+Adults4)+" Adult, "+(Children1+Children2+Children3+Children4)+ " Child");
            NumberOfAdult=Adults1+Adults2+Adults3+Adults4;
            NumberOfChild=Children1+Children2+Children3+Children4;
        }
        if(NumberOfRooms==1) {
            totalRoomsTv.setText(NumberOfRooms + " Room");
        }else {
            totalRoomsTv.setText(NumberOfRooms + " Rooms");
        }
    }

    private void roomDefaultValues() {
        childAge11="1";
        childAge12="1";
        childAge21="1";
        childAge22="1";
        childAge31="1";
        childAge32="1";
        childAge41="1";
        childAge42="1";
        roomCountTv.setText(NumberOfRooms+"");
        adultDefaultValue(1);
        childDefaultValue(1);

        if(NumberOfRooms==2){
            adultDefaultValue(2);
            childDefaultValue(2);
            roomTwoLin.setVisibility(View.VISIBLE);
        }else if(NumberOfRooms==3){
            adultDefaultValue(2);
            adultDefaultValue(3);
            childDefaultValue(2);
            childDefaultValue(3);
            roomTwoLin.setVisibility(View.VISIBLE);
            roomThreeLin.setVisibility(View.VISIBLE);
        }else if(NumberOfRooms==4){
            adultDefaultValue(2);
            adultDefaultValue(3);
            adultDefaultValue(4);
            childDefaultValue(2);
            childDefaultValue(3);
            childDefaultValue(4);
            roomTwoLin.setVisibility(View.VISIBLE);
            roomThreeLin.setVisibility(View.VISIBLE);
            roomFourLin.setVisibility(View.VISIBLE);
        }


    }

    private void childDefaultValue(int room) {
        switch (room){
            case 1:
                if(Children1==0) {
                    childImg10.setVisibility(View.VISIBLE);
                    childImg11.setVisibility(View.GONE);
                    childImg12.setVisibility(View.GONE);
                    childAgeLin11.setVisibility(View.GONE);
                }else if(Children1==1){
                    childImg10.setVisibility(View.GONE);
                    childImg11.setVisibility(View.VISIBLE);
                    childImg12.setVisibility(View.GONE);
                    childAgeLin11.setVisibility(View.VISIBLE);
                    childAgeLin12.setVisibility(View.GONE);
                }else {
                    childImg10.setVisibility(View.GONE);
                    childImg11.setVisibility(View.VISIBLE);
                    childImg12.setVisibility(View.VISIBLE);
                    childAgeLin11.setVisibility(View.VISIBLE);
                    childAgeLin12.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if(Children2==0) {
                    childImg20.setVisibility(View.VISIBLE);
                    childImg21.setVisibility(View.GONE);
                    childImg22.setVisibility(View.GONE);
                    childAgeLin21.setVisibility(View.GONE);
                }else if(Children2==1){
                    childImg20.setVisibility(View.GONE);
                    childImg21.setVisibility(View.VISIBLE);
                    childImg22.setVisibility(View.GONE);
                    childAgeLin21.setVisibility(View.VISIBLE);
                    childAgeLin22.setVisibility(View.GONE);
                }else {
                    childImg20.setVisibility(View.GONE);
                    childImg21.setVisibility(View.VISIBLE);
                    childImg22.setVisibility(View.VISIBLE);
                    childAgeLin21.setVisibility(View.VISIBLE);
                    childAgeLin22.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                if(Children3==0) {
                    childImg30.setVisibility(View.VISIBLE);
                    childImg31.setVisibility(View.GONE);
                    childImg32.setVisibility(View.GONE);
                    childAgeLin31.setVisibility(View.GONE);
                }else if(Children3==1){
                    childImg30.setVisibility(View.GONE);
                    childImg31.setVisibility(View.VISIBLE);
                    childImg32.setVisibility(View.GONE);
                    childAgeLin31.setVisibility(View.VISIBLE);
                    childAgeLin32.setVisibility(View.GONE);
                }else {
                    childImg30.setVisibility(View.GONE);
                    childImg31.setVisibility(View.VISIBLE);
                    childImg32.setVisibility(View.VISIBLE);
                    childAgeLin31.setVisibility(View.VISIBLE);
                    childAgeLin32.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if(Children4==0) {
                    childImg40.setVisibility(View.VISIBLE);
                    childImg41.setVisibility(View.GONE);
                    childImg42.setVisibility(View.GONE);
                    childAgeLin41.setVisibility(View.GONE);
                }else if(Children4==1){
                    childImg40.setVisibility(View.GONE);
                    childImg41.setVisibility(View.VISIBLE);
                    childImg42.setVisibility(View.GONE);
                    childAgeLin41.setVisibility(View.VISIBLE);
                    childAgeLin42.setVisibility(View.GONE);
                }else {
                    childImg40.setVisibility(View.GONE);
                    childImg41.setVisibility(View.VISIBLE);
                    childImg42.setVisibility(View.VISIBLE);
                    childAgeLin41.setVisibility(View.VISIBLE);
                    childAgeLin42.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void adultDefaultValue(int room) {
        switch (room){
            case 1:
                if(Adults1==1){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.GONE);
                    adultImg13.setVisibility(View.GONE);
                    adultImg14.setVisibility(View.GONE);
                }else if(Adults1==2){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.GONE);
                    adultImg14.setVisibility(View.GONE);
                }else if(Adults1==3){
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.VISIBLE);
                    adultImg14.setVisibility(View.GONE);
                }else {
                    adultImg11.setVisibility(View.VISIBLE);
                    adultImg12.setVisibility(View.VISIBLE);
                    adultImg13.setVisibility(View.VISIBLE);
                    adultImg14.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if(Adults2==1){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.GONE);
                    adultImg23.setVisibility(View.GONE);
                    adultImg24.setVisibility(View.GONE);
                }else if(Adults2==2){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.GONE);
                    adultImg24.setVisibility(View.GONE);
                }else if(Adults2==3){
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    adultImg24.setVisibility(View.GONE);
                }else {
                    adultImg21.setVisibility(View.VISIBLE);
                    adultImg22.setVisibility(View.VISIBLE);
                    adultImg23.setVisibility(View.VISIBLE);
                    adultImg24.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                if(Adults3==1){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.GONE);
                    adultImg33.setVisibility(View.GONE);
                    adultImg34.setVisibility(View.GONE);
                }else if(Adults3==2){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.GONE);
                    adultImg34.setVisibility(View.GONE);
                }else if(Adults3==3){
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    adultImg34.setVisibility(View.GONE);
                }else {
                    adultImg31.setVisibility(View.VISIBLE);
                    adultImg32.setVisibility(View.VISIBLE);
                    adultImg33.setVisibility(View.VISIBLE);
                    adultImg34.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if(Adults4==1){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.GONE);
                    adultImg43.setVisibility(View.GONE);
                    adultImg44.setVisibility(View.GONE);
                }else if(Adults4==2){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.GONE);
                    adultImg44.setVisibility(View.GONE);
                }else if(Adults4==3){
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    adultImg44.setVisibility(View.GONE);
                }else {
                    adultImg41.setVisibility(View.VISIBLE);
                    adultImg42.setVisibility(View.VISIBLE);
                    adultImg43.setVisibility(View.VISIBLE);
                    adultImg44.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void hotelSearch(HotelAvailabilityRequestModel busRequestModel) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClientHotelAvail().create(ApiInterface.class);
        Call<HotelAvailabilityResponseModel> call = apiService.getHotelAvailPost(ApiConstants.HotelAvail, busRequestModel);
        call.enqueue(new Callback<HotelAvailabilityResponseModel>() {
            @Override
            public void onResponse(Call<HotelAvailabilityResponseModel>call, Response<HotelAvailabilityResponseModel> response) {
                try{
//                    arrayList.clear();
                    if(response!=null && response.body()!=null){
                        if(response.body().StatusCode.equalsIgnoreCase("0") && response.body().hotels.size()>0){
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("HotelList",response.body().hotels);
                            bundle.putSerializable("CheckInDateCalander",checkInDateCalendar);
                            bundle.putSerializable("CheckOutDateCalander",checkOutDateCalendar);
                            HotelSearchListFragment fragment=new HotelSearchListFragment();
                            fragment.setArguments(bundle);
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                            hideCustomDialog();
//                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }else if(response.body().StatusCode.equalsIgnoreCase("2")){
                            hideCustomDialog();
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }else {
                            hideCustomDialog();
//                            busSearchArrayList.clear();
//                            busSearchListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
//                        busSearchArrayList.clear();
//                        busSearchListAdapter.notifyDataSetChanged();
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HotelAvailabilityResponseModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.childAgeSpinner11:
                childAge11=childAgeSpinner11.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner12:
                childAge12=childAgeSpinner12.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner21:
                childAge21=childAgeSpinner21.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner22:
                childAge22=childAgeSpinner22.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner31:
                childAge31=childAgeSpinner31.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner32:
                childAge32=childAgeSpinner32.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner41:
                childAge41=childAgeSpinner41.getSelectedItem().toString().split("\\s")[0];
                break;
            case R.id.childAgeSpinner42:
                childAge42=childAgeSpinner42.getSelectedItem().toString().split("\\s")[0];
                break;


        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> getRoomArrayList() {
        ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> roomOccupancyArrayList=new ArrayList<>();
        for(int i=1;i<=NumberOfRooms;i++) {
            HotelAvailabilityRequestModel.RoomOccupancy roomOccupancy=new HotelAvailabilityRequestModel().new RoomOccupancy();
            if (i == 1) {

                roomOccupancy.Adults = Adults1;
                roomOccupancy.Children = Children1;
                if(   roomOccupancy.Children ==1)
                {
                    roomOccupancy.Ages = childAge11;
                }
                else {
                    roomOccupancy.Ages = childAge11+","+childAge12;

                }
//                        roomOccupancyArrayList.add(roomOccupancy);

            } else if (i == 2) {
//                        roomOccupancy.Ages = childAge12;
                roomOccupancy.Adults = Adults2;
                roomOccupancy.Children = Children2;
                if(   roomOccupancy.Children ==1)
                {
                    roomOccupancy.Ages = childAge21;
                }
                else {
                    roomOccupancy.Ages = childAge21+","+childAge22;

                }
//                        roomOccupancyArrayList.add(roomOccupancy);

            } else if (i == 3) {
//                        roomOccupancy.Ages = Ages3;
                roomOccupancy.Adults = Adults3;
                roomOccupancy.Children = Children3;
                if(   roomOccupancy.Children ==1)
                {
                    roomOccupancy.Ages = childAge31;
                }
                else {
                    roomOccupancy.Ages = childAge31+","+childAge32;

                }
//                        roomOccupancyArrayList.add(roomOccupancy);

            } else if (i == 4) {
//                        roomOccupancy.Ages = Ages4;
                roomOccupancy.Adults = Adults4;
                roomOccupancy.Children = Children4;
                if(   roomOccupancy.Children ==1)
                {
                    roomOccupancy.Ages = childAge41;
                }
                else {
                    roomOccupancy.Ages = childAge41+","+childAge42;
                }
//                        roomOccupancyArrayList.add(roomOccupancy);

            }
            roomOccupancyArrayList.add(roomOccupancy);

        }
        return roomOccupancyArrayList;
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
            hotelCode=message2;
            MyPreferences.setFlightFromCity(context,message1+" ("+message2+")");
//                fromCitytv.setText(message2);
        }

    }
}

