package com.justclick.clicknbook.Fragment.flight;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class FlightPaxInfoFragment extends Fragment implements View.OnClickListener {

    private final int ADULT_DATE_PICKER=0, CHILD_DATE_PICKER=1, INFANT_DATE_PICKER=2, PASSPORT=3;
    Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private CheckBox checkbox;
    private TextView descriptionTv,continueTv,promoApplyTv,tourApply,travellerTv;
    private Calendar calendar;
    private String checkDOBDate="",checkPackageExpiryDate;
    private SimpleDateFormat dateServerFormat, dateToSetFormat;
    private int checkDOBDateDay, checkDOBDateMonth, checkDOBDateYear,
            adultDOBDateDay, adultDOBDateMonth, adultDOBDateYear,
            childDOBDateDay, childDOBDateMonth, childDOBDateYear,
            infantDOBDateDay, infantDOBDateMonth, infantDOBDateYear,
            passportDateDay, passportDateMonth, passportDateYear;
    private OnFragmentInteractionListener mListener;
    private LinearLayout linearAdd,term_linear;
    private FlightSearchDataModel.OneWay flightSearchDataModel;
    int adultCount, childCount, infantCount,traveller;
    EditText emailEdt,mobileEdt,promoCodeEdt,tourCodeEdt;
    TextView[] date_of_birth_edt,passportExpiryDate,infantPassportExpiryDate,childPassportExpiryDate,child_date_of_birth_edt,infant_date_of_birth_edt;

    String[] adultFirstName=new String[20];
    String[] adultLastname=new String[20];
    String[] adultDateOfBirth=new String[20];
    String[] adultFrequentFlyer=new String[20];
    String[] adultAirline=new String[20];
    String[] adultPassportNumber=new String[20];
    String[] adultPassportExpiryDate=new String[20];

    String[] childFirstName=new String[20];
    String[] childLastname=new String[20];
    String[] childDateOfBirth=new String[20];
    String[] childFrequentFlyer=new String[20];
    String[] childAirlines=new String[20];
    String[] childPassportNumbers=new String[20];
    String[] childPassportExpiryDates=new String[20];

    String[] infantFirstName=new String[20];
    String[] infantLastname=new String[20];
    String[] infantDateOfBirth=new String[20];
    String[] infantFrequentFlyer=new String[20];
    String[] infantAirlines=new String[20];
    String[] infantPassportNumber=new String[20];
    String[] infantPassportExpiryDates=new String[20];

    ImageView[] dobCalender,passportCalender,childCalender,childPassportCalender,infantCalender,infantPassportCalender;
    Spinner[] salutation_spinner,childSalutation_spinner, infant_salutation_spinner,childAirline,airlineTv,infantAirline;
    EditText[] first_name_edt,last_name_edt,frequentFlyerNumberTv,passportNumberTv,

    child_first_name_edt, child_last_name_edt,childFrequentFlyerNo,
            childPassportNumber,

    infant_name_edt, infant_last_name_edt,infantFrequentFlyerNumberTv,
            infantPassport;
    String name;
    private Date maxDateAdult,maxDateChild,maxDateInfant,minDateAdult,minDateChild, minDateInfant;

    public FlightPaxInfoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FlightPaxInfoFragment newInstance(String param1, String param2) {
        FlightPaxInfoFragment fragment = new FlightPaxInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_flight_pax_info, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.paxInfoTittle));
        if(getArguments().getSerializable("model")!=null) {
            flightSearchDataModel = (FlightSearchDataModel.OneWay) getArguments().getSerializable("model");
        }

        emailEdt= (EditText) view.findViewById(R.id.emailEdt);
        mobileEdt= (EditText) view.findViewById(R.id.mobileEdt);
        promoCodeEdt= (EditText) view.findViewById(R.id.promoCodeEdt);
        promoApplyTv= (TextView) view.findViewById(R.id.promoApplyTv);
        tourCodeEdt= (EditText) view.findViewById(R.id.tourCodeEdt);
        descriptionTv= (TextView) view.findViewById(R.id.descriptionTv);
        tourApply= (TextView) view.findViewById(R.id.tourApply);
        travellerTv= (TextView) view.findViewById(R.id.travellerTv);
        checkbox= (CheckBox) view.findViewById(R.id.checkbox);
        continueTv= (TextView) view.findViewById(R.id.continueTv);
        term_linear= (LinearLayout) view.findViewById(R.id.term_linear);
        promoApplyTv.setOnClickListener(this);
        tourCodeEdt.setOnClickListener(this);
        tourApply.setOnClickListener(this);
        checkbox.setOnClickListener(this);
        continueTv.setOnClickListener(this);
        view.findViewById(R.id.backArrow).setOnClickListener(this);

        dateServerFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        dateToSetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        calendar = Calendar.getInstance();
        Calendar calender=Calendar.getInstance();
        calender.add(Calendar.YEAR,-150);
        minDateAdult =calender.getTime();
        calender.add(Calendar.YEAR,150-12);
        maxDateAdult =calender.getTime();
        minDateChild =calender.getTime();
        calender.add(Calendar.YEAR,10);
        maxDateChild =calender.getTime();
        minDateInfant =calender.getTime();
        calender.add(Calendar.YEAR,2);
        maxDateInfant =calender.getTime();

        checkDOBDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        checkDOBDateMonth = calendar.get(Calendar.MONTH);
        checkDOBDateYear = calendar.get(Calendar.YEAR);
        adultDOBDateDay=passportDateDay=checkDOBDateDay;
        adultDOBDateMonth=passportDateMonth=checkDOBDateMonth;
        adultDOBDateYear=passportDateYear=checkDOBDateYear;

        calender.setTime(minDateChild);
        childDOBDateDay=calender.DAY_OF_MONTH;
        childDOBDateMonth=calender.MONTH;
        childDOBDateYear=calender.YEAR;

        calender.setTime(minDateInfant);
        infantDOBDateDay=calender.DAY_OF_MONTH;
        infantDOBDateMonth=calender.MONTH;
        infantDOBDateYear=calender.YEAR;

        adultCount =MyPreferences.getFlightAdult(context);
        childCount =MyPreferences.getFlightChild(context);
        infantCount =MyPreferences.getFlightInfant(context);
        traveller=adultCount+childCount+infantCount;
        travellerTv.setText(traveller+" Traveller");
        initializeAdults(view);
        initializeChildren(view);
        initializeInfants(view);

        return view;
    }

    private void initializeAdults(View view) {

        salutation_spinner=new Spinner[adultCount];
        first_name_edt=new EditText[adultCount];
        last_name_edt=new EditText[adultCount];
        date_of_birth_edt=new TextView[adultCount];
        frequentFlyerNumberTv=new EditText[adultCount];
        airlineTv=new Spinner[adultCount];
        passportNumberTv=new EditText[adultCount];
        passportExpiryDate=new TextView[adultCount];
        dobCalender=new ImageView[adultCount];
        passportCalender=new ImageView[adultCount];
        for(int i = 0; i< adultCount; i++) {
            LinearLayout dynamicContent = (LinearLayout) view.findViewById(R.id.linearAdd);
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.adult_pax_info, dynamicContent, false);

            salutation_spinner[i] =(Spinner) wizard.findViewById(R.id.salutation_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                    getStringArray(R.array.salutation_array));
            adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
            salutation_spinner[i].setAdapter(adapter);

            first_name_edt[i] =(EditText) wizard.findViewById(R.id.first_name_edt);
            last_name_edt[i] =(EditText) wizard.findViewById(R.id.last_name_edt);
            date_of_birth_edt[i] =(TextView) wizard.findViewById(R.id.date_of_birth_edt);
            frequentFlyerNumberTv[i] =(EditText) wizard.findViewById(R.id.frequentFlyerNumberTv);
            airlineTv[i] =(Spinner) wizard.findViewById(R.id.airlineTv);
            passportNumberTv[i] =(EditText) wizard.findViewById(R.id.passportNumberTv);
            passportExpiryDate[i] =(TextView) wizard.findViewById(R.id.passportExpiryDate);
            dobCalender[i] =(ImageView) wizard.findViewById(R.id.dobCalender);
            passportCalender[i] =(ImageView) wizard.findViewById(R.id.passportCalender);

            ((TextView)wizard.findViewById(R.id.passengerType)).setText((i+1)+" ADULT");
            ((ImageView)wizard.findViewById(R.id.passengerImg)).setImageResource(R.drawable.adult);

            final int finalI = i;
            wizard.findViewById(R.id.date_of_birth_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDOBDatePicker(date_of_birth_edt[finalI],maxDateAdult, minDateAdult, ADULT_DATE_PICKER);
                }
            });
//            final int finalI1 = i;
            wizard.findViewById(R.id.passportExpiryDateCalenderLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPassportDatePicker(passportExpiryDate[finalI]);
                }
            });

            wizard.findViewById(R.id.passengerTitleLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.passengerDetailRel).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.GONE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(0);
                    }else {
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(180);
                    }
                }
            });

            wizard.findViewById(R.id.moreInfoTitleLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.moreInfoLin).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.GONE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(0);

                    }else {
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(180);
                    }
                }
            });

            dynamicContent.addView(wizard);
        }
    }

    private void initializeChildren(View view) {
        childSalutation_spinner=new Spinner[childCount];
        child_first_name_edt=new EditText[childCount];
        child_last_name_edt =new EditText[childCount];
        child_date_of_birth_edt =new TextView[childCount];
        childFrequentFlyerNo=new EditText[childCount];
        childAirline=new Spinner[childCount];
        childPassportNumber=new EditText[childCount];
        childPassportExpiryDate=new TextView[childCount];
        childCalender=new ImageView[adultCount];
        childPassportCalender=new ImageView[adultCount];
        for(int i = 0; i< childCount; i++) {
            LinearLayout   dynamicContent1 = (LinearLayout) view.findViewById(R.id.linearChild);
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.adult_pax_info, dynamicContent1, false);

            childSalutation_spinner[i] =(Spinner) wizard.findViewById(R.id.salutation_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                    getStringArray(R.array.salutation_array));
            adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
//            childSalutation_spinner[i].setAdapter(adapter);

            child_first_name_edt[i] =(EditText) wizard.findViewById(R.id.first_name_edt);
            child_last_name_edt[i] =(EditText) wizard.findViewById(R.id.last_name_edt);
            child_date_of_birth_edt[i] =(TextView) wizard.findViewById(R.id.date_of_birth_edt);
            childFrequentFlyerNo[i] =(EditText) wizard.findViewById(R.id.frequentFlyerNumberTv);
            childAirline[i] =(Spinner) wizard.findViewById(R.id.airlineTv);
            childPassportNumber[i] =(EditText) wizard.findViewById(R.id.passportNumberTv);
            childPassportExpiryDate[i] =(TextView) wizard.findViewById(R.id.passportExpiryDate);
            childCalender[i] =(ImageView) wizard.findViewById(R.id.dobCalender);
            childPassportCalender[i] =(ImageView) wizard.findViewById(R.id.passportCalender);

            ((TextView)wizard.findViewById(R.id.passengerType)).setText((i+1)+" CHILD");
            ((ImageView)wizard.findViewById(R.id.passengerImg)).setImageResource(R.drawable.child);

            final int finalI = i;
            wizard.findViewById(R.id.date_of_birth_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDOBDatePicker(child_date_of_birth_edt[finalI], maxDateChild, minDateChild, CHILD_DATE_PICKER);
                }
            });
//            final int finalI1 = i;
            wizard.findViewById(R.id.passportExpiryDateCalenderLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPassportDatePicker(childPassportExpiryDate[finalI]);
                }
            });

            wizard.findViewById(R.id.passengerTitleLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.passengerDetailRel).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.GONE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(0);
                    }else {
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(180);
                    }
                }
            });

            wizard.findViewById(R.id.moreInfoTitleLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.moreInfoLin).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.GONE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(0);

                    }else {
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(180);
                    }
                }
            });

            dynamicContent1.addView(wizard);
        }
    }

    private void initializeInfants(View view) {

        infant_salutation_spinner =new Spinner[infantCount];
        infant_name_edt=new EditText[infantCount];
        infant_last_name_edt =new EditText[infantCount];
        infant_date_of_birth_edt=new TextView[infantCount];
        infantFrequentFlyerNumberTv=new EditText[infantCount];
        infantAirline=new Spinner[infantCount];
        infantPassport=new EditText[infantCount];
        infantPassportExpiryDate=new TextView[infantCount];
        infantCalender=new ImageView[infantCount];
        infantPassportCalender=new ImageView[infantCount];

        for(int i = 0; i< infantCount; i++) {
            LinearLayout dynamicContent2 = (LinearLayout) view.findViewById(R.id.linearInfant);
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.adult_pax_info, dynamicContent2, false);

            infant_salutation_spinner[i] =(Spinner) wizard.findViewById(R.id.salutation_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                    getStringArray(R.array.salutation_array));
            adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
//            infant_salutation_spinner[i].setAdapter(adapter);

            infant_name_edt[i] =(EditText) wizard.findViewById(R.id.first_name_edt);
            infant_last_name_edt[i] =(EditText) wizard.findViewById(R.id.last_name_edt);
            infant_date_of_birth_edt[i] =(TextView) wizard.findViewById(R.id.date_of_birth_edt);
            infantFrequentFlyerNumberTv[i] =(EditText) wizard.findViewById(R.id.frequentFlyerNumberTv);
            infantAirline[i] =(Spinner) wizard.findViewById(R.id.airlineTv);
            infantPassport[i] =(EditText) wizard.findViewById(R.id.passportNumberTv);
            infantPassportExpiryDate[i] =(TextView) wizard.findViewById(R.id.passportExpiryDate);
            infantCalender[i] =(ImageView) wizard.findViewById(R.id.dobCalender);
            infantPassportCalender[i] =(ImageView) wizard.findViewById(R.id.passportCalender);
            infantAirline[i].setVisibility(View.GONE);
            infantFrequentFlyerNumberTv[i].setVisibility(View.GONE);
            ((TextView)wizard.findViewById(R.id.passengerType)).setText((i+1)+" INFANT");
            ((ImageView)wizard.findViewById(R.id.passengerImg)).setImageResource(R.drawable.infant_new_vector);


            final int finalI = i;
            wizard.findViewById(R.id.date_of_birth_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDOBDatePicker(infant_date_of_birth_edt[finalI],maxDateInfant, minDateInfant,INFANT_DATE_PICKER);
                }
            });
//            final int finalI1 = i;
            wizard.findViewById(R.id.passportExpiryDateCalenderLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPassportDatePicker(infantPassportExpiryDate[finalI]);
                }
            });

            wizard.findViewById(R.id.passengerTitleLin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.passengerDetailRel).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.GONE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(0);
                    }else {
                        wizard.findViewById(R.id.passengerDetailRel).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.passengerArrowImg).setRotation(180);
                    }
                }
            });

            wizard.findViewById(R.id.moreInfoTitleLin).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.moreInfoLin).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.GONE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(0);

                    }else {
                        wizard.findViewById(R.id.moreInfoLin).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.moreInfoArrowImg).setRotation(180);
                    }
                }
            });

            dynamicContent2.addView(wizard);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        String names="";
        switch (v.getId())
        {
            case R.id.continueTv:
                boolean isAllFine=true, isValidate=false;
                for (int i = 0; i< adultCount; i++) {
                    adultFirstName[i]=  first_name_edt[i].getText().toString();
                    adultLastname[i]=  last_name_edt[i].getText().toString();
                    adultDateOfBirth[i]=  date_of_birth_edt[i].getText().toString();
                    adultFrequentFlyer[i]=  frequentFlyerNumberTv[i].getText().toString();
//                    adultAirline[i]=  airlineTv[i].getText().toString();
                    adultPassportNumber[i]=  passportNumberTv[i].getText().toString();
                    adultPassportExpiryDate[i]=  passportExpiryDate[i].getText().toString();
                    names=names+adultFirstName[i]+" "+adultLastname[i]+"\n";
                    if(!validate(first_name_edt[i], last_name_edt[i], adultDateOfBirth[i],
                            adultPassportNumber[i], adultPassportExpiryDate[i])){
                        isAllFine=false;
                        break;
                    }
                }
                for (int i = 0; i< childCount; i++) {
                    if(!isAllFine){
                        break;
                    }
                    childFirstName[i]=  child_first_name_edt[i].getText().toString();
                    childLastname[i]=  child_last_name_edt[i].getText().toString();
                    childDateOfBirth[i]=  child_date_of_birth_edt[i].getText().toString();
                    childFrequentFlyer[i]=  childFrequentFlyerNo[i].getText().toString();
//                    childAirlines[i]=  childAirline[i].getText().toString();
                    childPassportNumbers[i]=  childPassportNumber[i].getText().toString();
                    childPassportExpiryDates[i]=  childPassportExpiryDate[i].getText().toString();
                    names=names+childFirstName[i]+" "+childLastname[i]+"\n";
                    if(!validate(child_first_name_edt[i], child_last_name_edt[i], childDateOfBirth[i],
                            childPassportNumbers[i], childPassportExpiryDates[i])){
                        isAllFine=false;
                        break;
                    }
                }
                for (int i = 0; i< infantCount; i++) {
                    if(!isAllFine){
                        break;
                    }
                    infantFirstName[i]=  infant_name_edt[i].getText().toString();
                    infantLastname[i]=  infant_last_name_edt[i].getText().toString();
                    infantDateOfBirth[i]=  infant_date_of_birth_edt[i].getText().toString();
                    infantFrequentFlyer[i]=  infantFrequentFlyerNumberTv[i].getText().toString();
//                    infantAirlines[i]=  infantAirline[i].getText().toString();
                    infantPassportNumber[i]=  infantPassport[i].getText().toString();
                    infantPassportExpiryDates[i]=  infantPassportExpiryDate[i].getText().toString();
                    names=names+infantFirstName[i]+" "+infantLastname[i]+"\n";
                    if(!validate(infant_name_edt[i], infant_last_name_edt[i], infantDateOfBirth[i],
                            infantPassportNumber[i], infantPassportExpiryDates[i])){
                        isAllFine=false;
                        break;
                    }
                }

                Bundle bundle=new Bundle();
                bundle.putString("Names",names);
                bundle.putString("Email",emailEdt.getText().toString());
                bundle.putString("Number",mobileEdt.getText().toString());
                bundle.putString("FromTime",flightSearchDataModel.DTime);
                bundle.putString("ToTime",flightSearchDataModel.ATime);
                bundle.putString("Date",flightSearchDataModel.DDate);
                bundle.putString("FromCity",flightSearchDataModel.SCity);
                bundle.putString("ToCity",flightSearchDataModel.DCity);
                bundle.putString("FlightImage",flightSearchDataModel.Flights.get(0).Image);
                bundle.putString("FlightName",flightSearchDataModel.Flights.get(0).FName);
                bundle.putInt("AdultCount",adultCount);
                bundle.putInt("ChildCount",childCount);
                bundle.putInt("InfantCount",infantCount);
                bundle.putInt("FinalPrice",flightSearchDataModel.FinalPrice);

//                Fragment fragment=new FlightConfirmFragment();
//                fragment.setArguments(bundle);
                if(isAllFine) {
                    if(!Common.isEmailValid(emailEdt.getText().toString())){
                        Toast.makeText(context,R.string.empty_and_invalid_email,Toast.LENGTH_SHORT).show();
                    }else if(mobileEdt.getText().toString().length()<10){
                        Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
                    } else if(!checkbox.isChecked())
                    {
                        Toast.makeText(context,R.string.check_box,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Fragment fragment=new FlightConfirmFragment();
                        fragment.setArguments(bundle);
//                        Toast.makeText(context,"Thank u",Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(fragment);
                    }
                }
                break;

            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;

//            case R.id.descriptionTv:
//                if(Common.checkInternetConnection(context)) {
//                    term_linear.setVisibility(View.VISIBLE);
//                }else {
//                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
//                }
//
//                break;
        }

    }

    private void openPassportDatePicker(final TextView editText) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        checkPackageExpiryDate= dateServerFormat.format(calendar.getTime());
                        checkDOBDateDay =dayOfMonth;
                        checkDOBDateMonth =monthOfYear;
                        checkDOBDateYear =year;
                        editText.setText(dateToSetFormat.format(calendar.getTime()));

                    }

                }, checkDOBDateYear, checkDOBDateMonth, checkDOBDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-(60*60*24*365*12));

        datePickerDialog.show();
    }

    private void openDOBDatePicker(final TextView editText, Date maxDOB, Date minDate,
                                   int TYPE) {

        if(TYPE==ADULT_DATE_PICKER){
            checkDOBDateDay=adultDOBDateDay;
            checkDOBDateMonth=adultDOBDateMonth;
            checkDOBDateYear=adultDOBDateYear;
        }else if(TYPE==CHILD_DATE_PICKER){
            checkDOBDateDay=childDOBDateDay;
            checkDOBDateMonth=childDOBDateMonth;
            checkDOBDateYear=childDOBDateYear;
        }else if(TYPE==INFANT_DATE_PICKER){
            checkDOBDateDay=infantDOBDateDay;
            checkDOBDateMonth=infantDOBDateMonth;
            checkDOBDateYear=infantDOBDateYear;
        }else {
            checkDOBDateDay=passportDateDay;
            checkDOBDateMonth=passportDateMonth;
            checkDOBDateYear=passportDateYear;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
//                        calendar.add(Calendar.YEAR,-12);
                        checkDOBDate= dateServerFormat.format(calendar.getTime());
                        checkDOBDateDay =dayOfMonth;
                        checkDOBDateMonth =monthOfYear;
                        checkDOBDateYear =year;
                        editText.setText(dateToSetFormat.format(calendar.getTime()));

                    }

                }, checkDOBDateYear, checkDOBDateMonth, checkDOBDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(maxDOB.getTime());
        datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        datePickerDialog.show();
    }

    private boolean validate(EditText fname, EditText lName, String dob, String pNumber, String pExDate) {
        if(!Common.isFlightNameValid(fname.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_first_name, Toast.LENGTH_SHORT).show();
            fname.setError(getResources().getString(R.string.empty_and_invalid_first_name));
            return false;
        }
        else if(!Common.isFlightNameValid(lName.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_last_name,Toast.LENGTH_SHORT).show();
            lName.setError(getResources().getString(R.string.empty_and_invalid_last_name));
            return false;
        }
        else if(fname.getText().toString().equalsIgnoreCase(lName.getText().toString()))
        {
            Toast.makeText(context,R.string.first_name_and_last_name_equal,Toast.LENGTH_SHORT).show();
            fname.setError(getResources().getString(R.string.first_name_and_last_name_equal));
            return false;
        }

//        else if(dob.length()==0){
//            Toast.makeText(context,R.string.empty_and_invalid_date_name,Toast.LENGTH_SHORT).show();
//            return false;
//        }
        else if(flightSearchDataModel.IsIntl.equalsIgnoreCase("I")) {
            if(pNumber.length()==0){
                Toast.makeText(context, R.string.empty_and_invalid_passport_number, Toast.LENGTH_SHORT).show();
                return false;
            }else if(pExDate.length()==0) {
                Toast.makeText(context, R.string.empty_and_invalid_passport_expiry_date, Toast.LENGTH_SHORT).show();
                return false;
            } else if(pExDate.length()>0){
                Date startDate= null;
                Date expiryDate= null;
                try {
                    startDate = new SimpleDateFormat("dd-MM-yy", Locale.US).parse(flightSearchDataModel.DDate);
                    expiryDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(pExDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(calendar.MONTH,6);
                if( calendar.getTime().after(expiryDate)){
                    Toast.makeText(context, R.string.empty_and_invalid_passport_expiry_date, Toast.LENGTH_SHORT).show();
                    return false;
                }}

        }return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
