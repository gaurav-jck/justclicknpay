package com.justclick.clicknbook.Fragment.rblDmt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.MoneyTransferActivity;
import com.justclick.clicknbook.Activity.MoneyTransferNextActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.CityNameResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.model.RblPinDetailResponseModel;
import com.justclick.clicknbook.model.RblSenderRegistrationResponse;
import com.justclick.clicknbook.model.StateNameResponseModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.CityNameRequest;
import com.justclick.clicknbook.requestmodels.RblGetSenderRequest;
import com.justclick.clicknbook.requestmodels.RblPinDetailModel;
import com.justclick.clicknbook.requestmodels.RblSenderRegistrationRequest;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class MoneyTransferRegistrationFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView remittance_tv,submit_tv,person_detail_tv,mr_tv,ms_tv,mrs_tv;
    private EditText user_mobile_edt,address_edt,
            pincode_edt,name_edt,dob_edt,age_edt;
    private Spinner country_spinner, state_spinner, city_spinner;
    private String countryName="INDIA", stateName="", cityName="", address="";
    private DataBaseHelper dataBaseHelper;
    private String DOB;
    private int age;
    private SimpleDateFormat dateToServerFormat;
    private int startDateDay, startDateMonth, startDateYear;
    private Calendar startDateCalendar;
    private LoginModel loginModel;
    private RblPinDetailResponseModel pinModel;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        activity=(MoneyTransferActivity)context;
        dataBaseHelper=new DataBaseHelper(context);
        loginModel=new LoginModel();
        pinModel=new RblPinDetailResponseModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_money_transfer_registration, container, false);
        initializeViews(view);
        if(getArguments()!=null && getArguments().getString("SenderNumber")!=null){
            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
        }
        initializeDates();
        return view;
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();

        //default start Date
        startDateCalendar= Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);

        DOB=dateToServerFormat.format(startDateCalendar.getTime());

        //set default date
        dob_edt.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));
        age_edt.setText(calculateAge(startDateCalendar)+"");
    }

    private void initializeViews(View view) {
        remittance_tv = (TextView) view.findViewById(R.id.remittance_tv);
        person_detail_tv = (TextView) view.findViewById(R.id.person_detail_tv);
        mr_tv = (TextView) view.findViewById(R.id.mr_tv);
        ms_tv = (TextView) view.findViewById(R.id.ms_tv);
        mrs_tv = (TextView) view.findViewById(R.id.mrs_tv);
        submit_tv = (TextView) view.findViewById(R.id.submit_tv);

        user_mobile_edt = (EditText) view.findViewById(R.id.user_mobile_edt);
        address_edt = (EditText) view.findViewById(R.id.address_edt);
        pincode_edt = (EditText) view.findViewById(R.id.pincode_edt);
        name_edt = (EditText) view.findViewById(R.id.name_edt);
        dob_edt = (EditText) view.findViewById(R.id.dob_edt);
        age_edt = (EditText) view.findViewById(R.id.age_edt);


        state_spinner = (Spinner)view.findViewById(R.id.state_spinner);
        city_spinner = (Spinner) view.findViewById(R.id.city_spinner);

        view.findViewById(R.id.dob_edt).setOnClickListener(this);
        submit_tv.setOnClickListener(this);
        mr_tv.setOnClickListener(this);
        ms_tv.setOnClickListener(this);
        mrs_tv.setOnClickListener(this);

        setText();


        state_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.state_array)));
        city_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.city_array)));

        city_spinner.setSelection(0);
        if(dataBaseHelper.getAllStateNames("").size()==0) {
            if(Common.checkInternetConnection(context)) {
                getStateCity(ApiConstants.STATELIST, "INDIA");
            }else {
                Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
            }
        }else {
            String[] arr=new String[dataBaseHelper.getAllStateNames("").size()];
            for (int p=0; p<dataBaseHelper.getAllStateNames("").size(); p++){
                arr[p]=dataBaseHelper.getAllStateNames("").get(p);
            }
            state_spinner.setAdapter(getSpinnerAdapter(arr));
        }

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position > 0 || !state_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select"))) {
                    stateName = state_spinner.getSelectedItem().toString();
                    if(Common.checkInternetConnection(context)) {
                        getCity(ApiConstants.CITYLIST, stateName);
                    }else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    stateName = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 || !city_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    cityName = city_spinner.getSelectedItem().toString();
                } else {
                    cityName = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        pincode_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==6){
                    RblPinDetailModel model=new RblPinDetailModel();
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.PinCode=charSequence.toString();
                    if(Common.checkInternetConnection(context)) {
                        getPinService(model);
                    }else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getPinService(RblPinDetailModel model) {
        if(!MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblPinDetailResponseModel> call = apiService.getPinDetail(ApiConstants.PinDetail,model);
        call.enqueue(new Callback<RblPinDetailResponseModel>() {
            @Override
            public void onResponse(Call<RblPinDetailResponseModel>call, Response<RblPinDetailResponseModel> response) {
                try{
                    hideCustomDialog();
                    if(response!=null && response.body()!=null){
                        if(response.body().Data!=null && response.body().StatusCode.equalsIgnoreCase("0")){
//                            Toast.makeText(context,response.body().Status,Toast.LENGTH_SHORT).show();
                            pinModel=response.body();
                            setValues(pinModel);
                        }else{
                            Toast.makeText(context,response.body().Status,Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RblPinDetailResponseModel>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValues(RblPinDetailResponseModel body) {
        address_edt.setText(body.Data.Districtname);
        for(int i=0; i<state_spinner.getAdapter().getCount(); i++){
            if(state_spinner.getItemAtPosition(i).toString().equalsIgnoreCase(body.Data.statename)){
                state_spinner.setSelection(i);
            }
        }
    }

    private void setText() {
        Typeface face1 = Common.TitleTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);

        remittance_tv.setTypeface(face1);
        person_detail_tv.setTypeface(face1);
        mr_tv.setTypeface(face1);
        ms_tv.setTypeface(face1);
        mrs_tv.setTypeface(face1);

        user_mobile_edt.setTypeface(face2);
        address_edt.setTypeface(face2);
        pincode_edt.setTypeface(face2);
        name_edt.setTypeface(face2);
        dob_edt.setTypeface(face2);
        age_edt.setTypeface(face2);
        submit_tv.setTypeface(face3);

    }

    private void getStateCity(final String methodName, String value) {
        showCustomDialog();
        String valueToSend=Common.getDecodedString(value);
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<StateNameResponseModel> call = apiService.getState(methodName,valueToSend);
        call.enqueue(new Callback<StateNameResponseModel>() {
            @Override
            public void onResponse(Call<StateNameResponseModel>call, Response<StateNameResponseModel> response) {

                try{

                    if(response.body().Data!=null && response.body().Status.equalsIgnoreCase("Success")){
                        hideCustomDialog();
                        String[] arr=new String[response.body().Data.size()+1];
                        arr[0]="select-state";
                        dataBaseHelper.insertStateNames("select-state", null);
                        for (int p=0;p<response.body().Data.size();p++){
                            arr[p+1]=response.body().Data.get(p).StateName;
                            dataBaseHelper.insertStateNames(response.body().Data.get(p).StateName, null);
                        }
                        state_spinner.setAdapter(getSpinnerAdapter(arr));
                    }
                    else
                    {
                        hideCustomDialog();
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StateNameResponseModel>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCity(final String methodName, String stateName) {
        if(!MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        CityNameRequest cityModel=new CityNameRequest();
        cityModel.StateName =stateName;
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<CityNameResponseModel> call = apiService.getCity(methodName,cityModel);
        call.enqueue(new Callback<CityNameResponseModel>() {
            @Override
            public void onResponse(Call<CityNameResponseModel>call, Response<CityNameResponseModel> response) {
                try{
                    hideCustomDialog();
                    if(response.body().Data!=null && response.body().Status.equalsIgnoreCase("Success")){
                        String[] arr=new String[response.body().Data.size()];
                        int selPos=0;
                        for (int p=0;p<arr.length;p++){
                            arr[p]=response.body().Data.get(p).CityName;
                            if(pinModel!=null && pinModel.Data.Districtname.equalsIgnoreCase(arr[p])){
                                selPos=p;
                            }
                        }
                        city_spinner.setAdapter(getSpinnerAdapter(arr));
                        city_spinner.setSelection(selPos);
                    }
                    else
                    {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CityNameResponseModel>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }
    private boolean validate() {
        if(!Common.isNameValid(name_edt.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_mobile_edt.getText().toString().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
            return false;
        }else if(stateName.length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_state,Toast.LENGTH_SHORT).show();
            return false;
        }else if(cityName.length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_SHORT).show();
            return false;
        }else if(address_edt.getText().toString().trim().length()<4){
            Toast.makeText(context,R.string.empty_and_invalid_address,Toast.LENGTH_SHORT).show();
            return false;
        }else if(pincode_edt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_pincode,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    private int calculateAge(Calendar time) {
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int dobYear = time.get(Calendar.YEAR);
        age=curYear-dobYear;
        return age;
    }

    private void openDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateCalendar.set(year, monthOfYear, dayOfMonth);
                        DOB=dateToServerFormat.format(startDateCalendar.getTime());
                        startDateDay=dayOfMonth;
                        startDateMonth=monthOfYear;
                        startDateYear=year;
                        dob_edt.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));
                        age_edt.setText(calculateAge(startDateCalendar)+"");
                    }

                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_tv:
                Common.hideSoftKeyboard((MoneyTransferActivity)context);
                if(Common.checkInternetConnection(context)) {
                    if(validate())
                    {
                        RblSenderRegistrationRequest registrationRequest=new RblSenderRegistrationRequest();
                        registrationRequest.Name=name_edt.getText().toString().trim();
                        registrationRequest.Address=address_edt.getText().toString().trim();
                        registrationRequest.DeviceId =Common.getDeviceId(context);
                        registrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        registrationRequest.statename=stateName;
                        registrationRequest.cityname=cityName;
                        registrationRequest.DoneCardUser =loginModel.Data.DoneCardUser;
                        registrationRequest.MobileNo= Long.parseLong(user_mobile_edt.getText().toString().trim());
                        registrationRequest.pincode= Long.parseLong(pincode_edt.getText().toString().trim());
                        registrationRequest.statusRes= Long.parseLong(12+"");
                        senderRegistration(registrationRequest);
//                        Toast.makeText(context,"registration called",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.mr_tv:
                mr_tv.setTextColor(getResources().getColor(R.color.color_white));
                ms_tv.setTextColor(getResources().getColor(R.color.app_blue_color));
                mrs_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

                mr_tv.setBackgroundResource(R.color.app_blue_color);
                ms_tv.setBackgroundResource(R.color.transparent);
                mrs_tv.setBackgroundResource(R.color.transparent);
                break;
            case R.id.ms_tv:

                ms_tv.setTextColor(getResources().getColor(R.color.color_white));
                mr_tv.setTextColor(getResources().getColor(R.color.app_blue_color));
                mrs_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

                ms_tv.setBackgroundResource(R.color.app_blue_color);
                mr_tv.setBackgroundResource(R.color.transparent);
                mrs_tv.setBackgroundResource(R.color.transparent);
                break;
            case R.id.mrs_tv:
                mrs_tv.setTextColor(getResources().getColor(R.color.color_white));
                ms_tv.setTextColor(getResources().getColor(R.color.app_blue_color));
                mr_tv.setTextColor(getResources().getColor(R.color.app_blue_color));

                mrs_tv.setBackgroundResource(R.color.app_blue_color);
                ms_tv.setBackgroundResource(R.color.transparent);
                mr_tv.setBackgroundResource(R.color.transparent);
                break;

            case R.id.dob_edt:
                openDatePicker();
                break;


        }
    }

    private void senderRegistration(RblSenderRegistrationRequest request) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblSenderRegistrationResponse> call = apiService.rblSenderRegistrationPost(ApiConstants.SenderRegistration,request);
        call.enqueue(new Callback<RblSenderRegistrationResponse>() {
            @Override
            public void onResponse(Call<RblSenderRegistrationResponse>call, Response<RblSenderRegistrationResponse> response) {
                try{
                    hideCustomDialog();
                    if(response!=null && response.body()!=null){
                        if(response.body().status==1) {
                            RblGetSenderRequest rblGetSenderRequest=new RblGetSenderRequest();
                            rblGetSenderRequest.RMobile =user_mobile_edt.getText().toString();
                            rblGetSenderRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                            rblGetSenderRequest.DeviceId=Common.getDeviceId(context);
                            rblGetSenderRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            callGetDetail(rblGetSenderRequest);
                            Toast.makeText(context,response.body().description, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, response.body().description, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e){
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RblSenderRegistrationResponse>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callGetDetail(RblGetSenderRequest rblGetSenderRequest) {

        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblGetSenderResponse> call = apiService.getSenderDetailPost(ApiConstants.GetSenderDetails,rblGetSenderRequest);
        call.enqueue(new Callback<RblGetSenderResponse>() {
            @Override
            public void onResponse(Call<RblGetSenderResponse>call, Response<RblGetSenderResponse> response) {
                try{
                    hideCustomDialog();

                    if(response.body().status ==1) {
                        Intent i = new Intent(context, MoneyTransferNextActivity.class);
                        i.putExtra("SenderDetails",response.body());
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(context,response.body().description,Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RblGetSenderResponse> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}