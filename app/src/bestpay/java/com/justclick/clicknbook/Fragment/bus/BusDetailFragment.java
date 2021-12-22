package com.justclick.clicknbook.Fragment.bus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusBookingResponseModel;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.Fragment.bus.busmodel.SeatMapBean;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusBookingRequestModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;

public class BusDetailFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private Animation animUp, animDown;
    private OnFragmentInteractionListener mListener;
    //  private FlightSearchDataModel flightSearchDataModel;
    private RelativeLayout totalFareRel,totalFareDetailRel,roundMainTitleRel;
    private ImageView fareArrowImg,sourceEnd;
    private int tripType=1;
    private TextView sourceCityTv,destCityTv,sourceNameFirstTv, passenger,
            destNameFirstTv,boardingTimeDateTv,deBoardingTimeDateTv,totalFareTv,durationFirstTv,
            boardingLabelTv, droppingLabelTv,fareTitleTv,farTv,continueTv,
            baseFare,agentMarkupTv,conveyanceTv,discount,tds,grossFareTv,payable;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private TextView tittleTv,busSupplierNameTv,busTypeTv;
    private Calendar calendar, adultCalender, adultMinCalender, adultMaxCalender;
    private int checkAdultDOBDateDay,checkDOBDateDay, checkAdultDOBDateMonth,
            checkDOBDateMonth,checkAdultDOBDateYear, checkDOBDateYear;
    private float totalBaseFare,totalAgentMarkupTv,totalConveyanceTv,totalDiscount,
            totalTds,totalGrossFareTv,totalpayable,totalServiceCharge;
    private String checkDOBDate="";
    private SimpleDateFormat dateServerFormat;
    private LinearLayout linearAdd,droppingLin;
    private EditText[] first_last_name_edt,mobileEdt,emailEdt;
    private Spinner[] salutation_spinner;
    private TextView[] date_of_birth_edt,no_of_roomTv;
    private ImageView[] roomArrowImg;
    private LinearLayout[] fillDetailLinear;
    String[] firstAndLastName=new String[20];
    String[] salutation=new String[20];
    String[] mobile=new String[20];
    String[] email=new String[20];
    String[] dateOfBirth=new String[20];
    int NoOfSeat =1;
    ArrayList<SeatMapBean> selectedArray;
    private BusSearchBean busSearchBean;
    private int boardingPointPosition=0, droppingPointPosition=0;
    private String from,to;
    String fromDate="", toDate="";
    private Dialog confirmationDialog;
    private ArrayList<SeatMapBean> arrayListSeatMap;
    public BusDetailFragment() {
        // Required empty public constructor
    }

    public static BusDetailFragment newInstance(String param1, String param2) {
        BusDetailFragment fragment = new BusDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        selectedArray=new ArrayList<>();
        busSearchBean=new BusSearchBean();
        if(getArguments() !=null && getArguments().getSerializable("SelectedPassengerArray")!=null) {
            selectedArray = (ArrayList<SeatMapBean>) getArguments().getSerializable("SelectedPassengerArray");
            arrayListSeatMap = (ArrayList<SeatMapBean>) getArguments().getSerializable("arrayListSeatMap");
            busSearchBean = (BusSearchBean) getArguments().getSerializable("busSearchBean");
            boardingPointPosition = getArguments().getInt("boardingPointPosition");
            droppingPointPosition = getArguments().getInt("droppingPointPosition");
            NoOfSeat =selectedArray.size();
        }


//        flightSearchDataModel=new FlightSearchDataModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bus_detail, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.busDetailFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        totalFareRel=  view.findViewById(R.id.totalFareRel);
        totalFareDetailRel=  view.findViewById(R.id.totalFareDetailRel);
        roundMainTitleRel= view.findViewById(R.id.roundMainTitleRel);
        fareArrowImg= view.findViewById(R.id.fareArrowImg);
        sourceEnd=  view.findViewById(R.id.sourceEnd);
        sourceCityTv=  view.findViewById(R.id.sourceCityTv);
        destCityTv=  view.findViewById(R.id.destCityTv);
        tittleTv=  view.findViewById(R.id.tittleTv);
        busSupplierNameTv=  view.findViewById(R.id.busSupplierNameTv);
        busTypeTv=  view.findViewById(R.id.busTypeTv);
        boardingLabelTv =  view.findViewById(R.id.boardingLabelTv);
        droppingLabelTv =  view.findViewById(R.id.droppingLabelTv);
        fareTitleTv=  view.findViewById(R.id.fareTitleTv);
        farTv= view.findViewById(R.id.farTv);
        continueTv=  view.findViewById(R.id.continueTv);
        sourceNameFirstTv=  view.findViewById(R.id.sourceNameFirstTv);
        destNameFirstTv=  view.findViewById(R.id.destNameFirstTv);
        droppingLin= view.findViewById(R.id.droppingLin);
        boardingTimeDateTv=  view.findViewById(R.id.boardingTimeDateTv);
        deBoardingTimeDateTv=  view.findViewById(R.id.deBoardingTimeDateTv);
        durationFirstTv=  view.findViewById(R.id.durationFirstTv);
        totalFareTv=  view.findViewById(R.id.totalFareTv);
        passenger=  view.findViewById(R.id.passenger);
        baseFare=  view.findViewById(R.id.baseFare);
        agentMarkupTv=  view.findViewById(R.id.agentMarkupTv);
        conveyanceTv=  view.findViewById(R.id.conveyanceTv);
        discount=  view.findViewById(R.id.discount);
        tds=  view.findViewById(R.id.tds);
        grossFareTv=  view.findViewById(R.id.grossFareTv);
        payable=  view.findViewById(R.id.payable);
        totalFareRel.setOnClickListener(this);
        animUp = AnimationUtils.loadAnimation(context,
                R.anim.translate_up);
        animDown = AnimationUtils.loadAnimation(context,
                R.anim.translate_down);
        from = MyPreferences.getBusFromCity(context);
        to = MyPreferences.getBusToCity(context);

        initializeView(view);
        setFont();
        passenger.setText(NoOfSeat +" PASSENGER");
        sourceCityTv.setText(MyPreferences.getBusFromCity(context).substring(0,MyPreferences.getBusFromCity(context).indexOf("(")));
        destCityTv.setText(MyPreferences.getBusToCity(context).substring(0,MyPreferences.getBusToCity(context).indexOf("(")));
        busSupplierNameTv.setText(busSearchBean.travelName);
        busTypeTv.setText(busSearchBean.bustype);
        sourceNameFirstTv.setText(busSearchBean.boardingTimes.get(boardingPointPosition).address);

        String boardingDate=busSearchBean.doj;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");/*yyyy-MM-dd HH:mm:ss*/

        Date date=null;
        try {
            date=df.parse(boardingDate);
            fromDate=Common.getShowInTVDateFormat().format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        set arrival date
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        date = c.getTime();

        boardingTimeDateTv.setText(Common.formatBusTime(busSearchBean.boardingTimes.get(boardingPointPosition).time)+" | "+fromDate);


        for(int i = 0; i < selectedArray.size(); i++) {
            totalBaseFare=totalBaseFare+Float.parseFloat(selectedArray.get(i).fare);
            totalAgentMarkupTv=totalAgentMarkupTv+Float.parseFloat(selectedArray.get(i).AgMarkup);
//            totalConveyanceTv=totalConveyanceTv+Float.parseFloat(selectedArray.get(i).levyFare)+
//                    Float.parseFloat(selectedArray.get(i).srtFee)+Float.parseFloat(selectedArray.get(i).tollFee)+
//                    Float.parseFloat(selectedArray.get(i).bookingFee)+Float.parseFloat(selectedArray.get(i).ServiceCharge);
            totalConveyanceTv=totalConveyanceTv+ Float.parseFloat(selectedArray.get(i).bookingFee)+Float.parseFloat(selectedArray.get(i).ServiceCharge);
            totalDiscount=totalDiscount+Float.parseFloat(selectedArray.get(i).Commission);
            totalTds=totalTds+Float.parseFloat(selectedArray.get(i).TDS);
            totalGrossFareTv=totalGrossFareTv+Float.parseFloat(selectedArray.get(i).GrossFare);
            totalpayable=totalpayable+Float.parseFloat(selectedArray.get(i).NetTFare);
//            totalServiceCharge=totalServiceCharge;

        }
        baseFare.setText(Common.roundOffDecimalValue(totalBaseFare) + "");
        agentMarkupTv.setText(Common.roundOffDecimalValue(totalAgentMarkupTv) + "");
        conveyanceTv.setText(Common.roundOffDecimalValue(totalConveyanceTv)  + "");
        discount.setText(Common.roundOffDecimalValue(totalDiscount)+"");
        tds.setText(Common.roundOffDecimalValue(totalTds) + "");
        grossFareTv.setText(Common.roundOffDecimalValue(totalGrossFareTv) + "");
        payable.setText(Common.roundOffDecimalValue(totalpayable) + "");
        totalFareTv.setText(Common.roundOffDecimalValue(totalpayable) + "");

        if(busSearchBean.droppingTimes.size() == 0)
        {
            droppingLin.setVisibility(View.GONE);
            durationFirstTv.setText(Common.getBusDuration(busSearchBean.arrivalTime,
                    busSearchBean.departureTime));
            sourceEnd.setVisibility(View.GONE);

        }else
        {
            c.add(Calendar.DATE, Common.getBusArrivalDate(busSearchBean.droppingTimes.get(droppingPointPosition).time));
            droppingLin.setVisibility(View.VISIBLE);
            durationFirstTv.setText(Common.getBusDuration(busSearchBean.boardingTimes.get(boardingPointPosition).time,
                    busSearchBean.droppingTimes.get(droppingPointPosition).time));
            destNameFirstTv.setText(busSearchBean.droppingTimes.get(droppingPointPosition).address);
            deBoardingTimeDateTv.setText(Common.formatBusTime(busSearchBean.droppingTimes.get(droppingPointPosition).time)+" | "+
                    Common.getShowInTVDateFormat().format(date));
        }
        return view;
    }

    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.FlightCalenderTypeFace3(context);
        tittleTv.setTypeface(face);
        sourceCityTv.setTypeface(face);
        destCityTv.setTypeface(face);
        busSupplierNameTv.setTypeface(face);
        busTypeTv.setTypeface(face);
        boardingLabelTv.setTypeface(face1);
        sourceNameFirstTv.setTypeface(face);
        boardingTimeDateTv.setTypeface(face);
        droppingLabelTv.setTypeface(face1);
        destNameFirstTv.setTypeface(face);
        deBoardingTimeDateTv.setTypeface(face);
        durationFirstTv.setTypeface(face);
        fareTitleTv.setTypeface(face);
        farTv.setTypeface(face);
        totalFareTv.setTypeface(face);
        passenger.setTypeface(face);
        continueTv.setTypeface(face1);
    }

    private void initializeView(View view) /*throws Exception*/{
        adultCalender=Calendar.getInstance();
        adultMinCalender=Calendar.getInstance();
        adultMinCalender.add(Calendar.YEAR,-150-5);
        adultCalender.add(Calendar.YEAR,-5);
        adultMaxCalender=Calendar.getInstance();
        adultMaxCalender.add(Calendar.YEAR,-5);
        calendar = Calendar.getInstance();
        dateServerFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        checkDOBDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        checkDOBDateMonth = calendar.get(Calendar.MONTH);
        checkDOBDateYear = calendar.get(Calendar.YEAR);
        checkAdultDOBDateDay = adultCalender.get(Calendar.DAY_OF_MONTH);
        checkAdultDOBDateMonth = adultCalender.get(Calendar.MONTH);
        checkAdultDOBDateYear = adultCalender.get(Calendar.YEAR);
        linearAdd= (LinearLayout) view.findViewById(R.id.linearAdd);
        setRoomDetailDynamically();
        view.findViewById(R.id.continueTv).setOnClickListener(this);
        view.findViewById(R.id.backArrow).setOnClickListener(this);
        view.findViewById(R.id.scrollView).setOnClickListener(this);
    }

    private void setRoomDetailDynamically() {

        salutation_spinner=new Spinner[NoOfSeat];
        first_last_name_edt=new EditText[NoOfSeat];
        mobileEdt=new EditText[NoOfSeat];
        emailEdt=new EditText[NoOfSeat];
        date_of_birth_edt=new TextView[NoOfSeat];
        no_of_roomTv=new TextView[NoOfSeat];
        roomArrowImg=new ImageView[NoOfSeat];
        no_of_roomTv=new TextView[NoOfSeat];
        fillDetailLinear=new LinearLayout[NoOfSeat];

        for(int i = 0; i< NoOfSeat; i++) {
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.bus_pass_info, linearAdd, false);

            date_of_birth_edt[i] = wizard.findViewById(R.id.date_of_birth_edt);
            no_of_roomTv[i] =wizard.findViewById(R.id.noOfPassTv);
            no_of_roomTv[i].setText("Passenger "+(i+1));
            no_of_roomTv[i].setTypeface(Common.FlightCalenderTypeFace3(context));
            first_last_name_edt[i] = wizard.findViewById(R.id.first_last_name_edt);
            salutation_spinner[i] = wizard.findViewById(R.id.salutation_spinner);
            mobileEdt[i] = wizard.findViewById(R.id.mobileEdt);
            emailEdt[i] = wizard.findViewById(R.id.emailEdt);
            mobileEdt[0].setVisibility(View.VISIBLE);
            emailEdt[0].setVisibility(View.VISIBLE);
            emailEdt[i] = wizard.findViewById(R.id.emailEdt);
            roomArrowImg[i] = wizard.findViewById(R.id.roomArrowImg);
//            roomArrowImg[i].setOnClickListener(this);
            fillDetailLinear[i] = wizard.findViewById(R.id.fillDetailLinear);
            if(Boolean.parseBoolean(selectedArray.get(i).ladiesSeat))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                        getStringArray(R.array.ladies_salutation_array));
                adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
                salutation_spinner[i].setAdapter(adapter);
            }else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                        getStringArray(R.array.salutation_array));
                adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
                salutation_spinner[i].setAdapter(adapter);
            }

            final int finalI = i;
            wizard.findViewById(R.id.date_of_birth_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalI==0) {
                        openAdultDOBDatePicker(date_of_birth_edt[finalI]);
                    }else
                    {
                        openDOBDatePicker(date_of_birth_edt[finalI]);
                    }
                }
            });

            wizard.findViewById(R.id.arrowClickLinear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.fillDetailLinear).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.fillDetailLinear).setVisibility(View.GONE);
                        wizard.findViewById(R.id.roomArrowImg).setRotation(0);
                    }else {
                        wizard.findViewById(R.id.fillDetailLinear).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.roomArrowImg).setRotation(180);
                    }
                }
            });
            linearAdd.addView(wizard);
        }
    }

    private void openAdultDOBDatePicker(final TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        adultCalender.set(year, monthOfYear, dayOfMonth);
                        if (adultCalender.after(adultMaxCalender)) {
                            Toast.makeText(context, "Can't select date before five year", Toast.LENGTH_SHORT).show();
                        }else {
                            checkDOBDate= dateServerFormat.format(adultCalender.getTime());
                            checkAdultDOBDateDay =dayOfMonth;
                            checkAdultDOBDateMonth =monthOfYear;
                            checkAdultDOBDateYear =year;
                            textView.setText(checkDOBDate);
                        }
                    }
                }, checkAdultDOBDateYear, checkAdultDOBDateMonth, checkAdultDOBDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(adultMaxCalender.getTimeInMillis()-1000);
//            datePickerDialog.getDatePicker().setMinDate(adultMinCalender.getTimeInMillis()-1000);
        datePickerDialog.show();

    }

    private void openDOBDatePicker(final TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        if (calendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Invalid Date of birth", Toast.LENGTH_SHORT).show();
                        }else {
                            checkDOBDate= dateServerFormat.format(calendar.getTime());
                            checkDOBDateDay =dayOfMonth;
                            checkDOBDateMonth =monthOfYear;
                            checkDOBDateYear =year;
                            textView.setText(checkDOBDate);
                            textView.setError(null);}
                    }

                }, checkDOBDateYear, checkDOBDateMonth, checkDOBDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.continueTv:
                try {
                    if(MyPreferences.getLoginData(new LoginModel(), context).Data.UserType.equalsIgnoreCase("D")){
                        Toast.makeText(context, R.string.un_authorise_message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    for (int i = 0; i < NoOfSeat; i++) {
                        salutation[i] = salutation_spinner[i].getSelectedItem().toString();
                        firstAndLastName[i] = first_last_name_edt[i].getText().toString();
                        mobile[i] = mobileEdt[i].getText().toString();
                        dateOfBirth[i] = date_of_birth_edt[i].getText().toString();
                        email[i] = emailEdt[i].getText().toString();

                        if(validate(first_last_name_edt[i], mobileEdt[i], date_of_birth_edt[i], emailEdt[i], fillDetailLinear[i])) {
                            if(i==NoOfSeat-1) {
                                confirmationDialog();
                            }
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;

            case R.id.scrollView:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;

            case R.id.totalFareRel:
                menuDownArrowClick(totalFareDetailRel, fareArrowImg);
                break;
        }
    }


    private void confirmationDialog(){
        confirmationDialog = new Dialog(context);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setContentView(R.layout.bus_confirmation_dialog);
        final Window window= confirmationDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final Button submit=  confirmationDialog.findViewById(R.id.submit_btn);
        Button dialogCloseButton =  confirmationDialog.findViewById(R.id.close_btn);
        TextView travel_deatail_tv =  confirmationDialog.findViewById(R.id.travel_deatail_tv);
        TextView price_tv =  confirmationDialog.findViewById(R.id.price_tv);
        TextView pax_tv =  confirmationDialog.findViewById(R.id.pax_tv);
        travel_deatail_tv.setText(MyPreferences.getBusFromCity(context).substring(0,MyPreferences.getBusFromCity(context).indexOf("("))+
                " to "+MyPreferences.getBusToCity(context).substring(0,MyPreferences.getBusToCity(context).indexOf("(")));
        price_tv.setText(Common.roundOffDecimalValue(totalpayable) + "");
        pax_tv.setText(NoOfSeat +"");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit);
                try {
                    confirmationDialog.dismiss();
                    continueBooking();
                } catch (Exception e) {
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();

    }
    private void continueBooking()/* throws Exception*/{
        LoginModel loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);

        BusBookingRequestModel requestModel=new BusBookingRequestModel();
        requestModel.DeviceId=Common.getDeviceId(context);
        requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        requestModel.LoginSessionId=EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

        BusBookingRequestModel.BLReq blReq=requestModel.new BLReq();
        blReq.DeviceId=Common.getDeviceId(context);
        blReq.DoneCardUser=loginModel.Data.DoneCardUser;
        blReq.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        blReq.availableTripId=busSearchBean.routid;
        blReq.boardingPointId=busSearchBean.boardingTimes.get(boardingPointPosition).bpId;
        blReq.destination= to.substring(to.indexOf("(")+1,to.indexOf(")"))+"("+to.substring(0,to.indexOf("("))+")";
//        blReq.source="1492(Delhi)";
        blReq.source=from.substring(from.indexOf("(")+1,from.indexOf(")"))+"("+from.substring(0,from.indexOf("("))+")";;
        blReq.BusOpertor=busSearchBean.travelName;
        blReq.BusType=busSearchBean.bustype;
        blReq.Remarks="test";
        blReq.Location=busSearchBean.boardingTimes.get(boardingPointPosition).bpName;
        blReq.BTIme=Common.formatBusTime(busSearchBean.boardingTimes.get(boardingPointPosition).time);
        blReq.BPoint=busSearchBean.boardingTimes.get(boardingPointPosition).bpName;
        blReq.DPoint=busSearchBean.droppingTimes.get(droppingPointPosition).bpName;
        blReq.OPTNum=busSearchBean.boardingTimes.get(boardingPointPosition).contactNumber;
        blReq.JourrnyDate=fromDate;
        blReq.DTime=Common.formatBusTime(busSearchBean.droppingTimes.get(droppingPointPosition).time);
        blReq.CancellationPolicy=busSearchBean.cancellationPolicy;
        blReq.Address=busSearchBean.boardingTimes.get(boardingPointPosition).address;
        blReq.LandMark=busSearchBean.boardingTimes.get(boardingPointPosition).landmark;//22
        try {
            blReq.PartialFlag= Boolean.parseBoolean(busSearchBean.partialCancellationAllowed);
        }catch (ClassCastException e){
            blReq.PartialFlag=false;
        }

        ArrayList<BusBookingRequestModel.inventoryItems> inventoryItemArrayList=new ArrayList<>();

        for (int i = 0; i < NoOfSeat; i++) {
            firstAndLastName[i] = first_last_name_edt[i].getText().toString();
            mobile[i] = mobileEdt[i].getText().toString();
            dateOfBirth[i] = date_of_birth_edt[i].getText().toString();
            email[i] = emailEdt[i].getText().toString();

            if(validate(first_last_name_edt[i], mobileEdt[i], date_of_birth_edt[i], emailEdt[i], fillDetailLinear[i])) {
                //                    set Passengers
                ArrayList<BusBookingRequestModel.passenger> passenger=new ArrayList<>();
                BusBookingRequestModel.passenger passengerS=requestModel.new passenger();
                if(salutation[i].equalsIgnoreCase("Mr")) {

                    passengerS.gender="Male";
                }else{
                    passengerS.gender="Female";
                }
                passengerS.name=firstAndLastName[i];
                passengerS.title=salutation[i];
                passengerS.mobile= Long.parseLong(mobile[0]);
                passengerS.address=email[0];
                if(i==0){
                    passengerS.primary="true";
                }else {
                    passengerS.primary="false";
                }
                passengerS.idNumber=busSearchBean.routid;
                try {
                    passengerS.age= Common.getAgeFromDOB(dateServerFormat.parse(dateOfBirth[i]));
                } catch (ParseException e) {
                    passengerS.age=0;
                    e.printStackTrace();
                }
                passenger.add(passengerS);
//                    set Inventory
                BusBookingRequestModel.inventoryItems inventoryItem=requestModel.new inventoryItems();
                inventoryItem.passenger=passenger;
                inventoryItem.fare= Float.parseFloat(selectedArray.get(i).fare);
                inventoryItem.ladiesSeat= Boolean.parseBoolean(selectedArray.get(i).ladiesSeat);
                inventoryItem.seatName=selectedArray.get(i).name;
                inventoryItem.Commission= Float.parseFloat(selectedArray.get(i).Commission);
                inventoryItem.NetTFare= Float.parseFloat(selectedArray.get(i).NetTFare);
                inventoryItem.ServiceCharge= Float.parseFloat(selectedArray.get(i).ServiceCharge);
                inventoryItem.TDS= Float.parseFloat(selectedArray.get(i).TDS);
                inventoryItem.AgMarkup= Float.parseFloat(selectedArray.get(i).AgMarkup);
                inventoryItem.Basefare= Float.parseFloat(selectedArray.get(i).baseFare);
                inventoryItem.BookingFee= Float.parseFloat(selectedArray.get(i).bookingFee);
                inventoryItem.bankTrexAmt= Float.parseFloat(selectedArray.get(i).bankTrexAmt);
                inventoryItemArrayList.add(inventoryItem);;

                if(i== NoOfSeat -1){
//                    BusBookingRequestModel.inventoryItems inventoryItems=requestModel.new inventoryItems();
//                    inventoryItems.inventoryItems=inventoryItemArrayList;
                    blReq.inventoryItems=inventoryItemArrayList;
                    blReq.LeadPaxName=firstAndLastName[0];
                    blReq.LeadMobile=Long.parseLong(mobile[0]);
                    blReq.LeadMail=email[0];
                    requestModel.BLReq=blReq;

                    if(Common.checkInternetConnection(context)) {
                        callBusBooking(requestModel);
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }

                }
            }else {
                break;
            }
        }

    }

    private void callBusBooking(final BusBookingRequestModel requestModel) {
        confirmationDialog.dismiss();
        showCustomDialog();
//        String json = new Gson().toJson(requestModel);
        new NetworkCall().callBusService(requestModel, ApiConstants.ConfirmBooking, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 1, requestModel);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandler(ResponseBody response, int TYPE, BusBookingRequestModel busBookingRequestModel) {
        try {
            hideCustomDialog();
            BusBookingResponseModel busModel = new Gson().fromJson(response.string(), BusBookingResponseModel.class);
            if(busModel!=null){
                if(busModel.Status==0){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("busSearchBean",busSearchBean);
                    bundle.putSerializable("busModel",busModel);
                    bundle.putSerializable("selectedArray",selectedArray);
                    bundle.putSerializable("busBookingRequestModel",busBookingRequestModel);
                    bundle.putSerializable("totalpayable",totalpayable);
                    bundle.putSerializable("boardingPointPosition",boardingPointPosition);
                    bundle.putSerializable("droppingPointPosition",droppingPointPosition);
                    BusConfirmFragment fragment=new BusConfirmFragment();
                    fragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
//                    Toast.makeText(context,busModel.Description, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,busModel.Description, Toast.LENGTH_LONG).show();
                }
               /* if(busModel.Status==1){
                    BusConfirmFragment fragment=new BusConfirmFragment();
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                    Toast.makeText(context,busModel.Status, Toast.LENGTH_LONG).show();
                }else {
                    BusConfirmFragment fragment=new BusConfirmFragment();
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                    Toast.makeText(context,busModel.Status, Toast.LENGTH_LONG).show();
                }*/
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }
    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }


    private Boolean validate(EditText name, EditText mobile, TextView dateOfBirth, EditText email, LinearLayout parent) {
        if(!Common.isNameValid(name.getText().toString())&& name.getText().length()<35){
            Toast.makeText(context,R.string.empty_and_invalid_first_name, Toast.LENGTH_SHORT).show();
            name.setError(getResources().getString(R.string.empty_and_invalid_first_name));
            parent.setVisibility(View.VISIBLE);
            return false;
        }else if(mobileEdt[0].getText().toString().length()<10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            mobile.setError(getResources().getString(R.string.empty_and_invalid_mobile));
            parent.setVisibility(View.VISIBLE);
            return false;
//        }else if(date_of_birth_edt[0].getText().toString().length()<10){
//                Toast.makeText(context,R.string.empty_and_invalid_date_name,Toast.LENGTH_SHORT).show();
//            dateOfBirth.setError(getResources().getString(R.string.empty_and_invalid_date_name));
//                return false;
        } else if( dateOfBirth.getText().length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_date_name,Toast.LENGTH_SHORT).show();
            dateOfBirth.setError(getResources().getString(R.string.empty_and_invalid_date_name));
            parent.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Common.isEmailValid(emailEdt[0].getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_email,Toast.LENGTH_SHORT).show();
            email.setError(getResources().getString(R.string.empty_and_invalid_email));
            parent.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private void menuDownArrowClick(RelativeLayout totalFareDetailRel, ImageView fareArrowImg) {
        if(totalFareDetailRel.getVisibility()==View.VISIBLE){
            totalFareDetailRel.startAnimation(animUp);
            totalFareDetailRel.setVisibility(View.GONE);
            fareArrowImg.setRotation(0);
        }else {
            totalFareDetailRel.startAnimation(animDown);
            totalFareDetailRel.setVisibility(View.VISIBLE);
            fareArrowImg.setRotation(180);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
