package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.AddSenderRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams;
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.AddSenderResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.StateList;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.JctCreateSenderResponse;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RapipayAddSenderFragment extends Fragment implements View.OnClickListener {
    private final String ADD_SENDER="05", UPDATE_SENDER="06";
    private final int CREATE_SENDER=1, VALIDATE_OTP=2, RESEND_OTP=3, SENDER_DETAIL=4;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView get_tv, resendTv, titleTv, otpDetailTv;
    private EditText number_edt,name_edt, last_name_edt, otpEdt,dob_edt,pin_edt, state_edt;
    private AutoCompleteTextView genderAtv,cityAtv;
    private LinearLayout otpLin;
    private LoginModel loginModel;
    private boolean isVerify=false, isNewApi=false;
    private SenderDetailResponse senderDetailResponse;
    private AddSenderResponse addSenderResponse;
    private ArrayList<PinCityResponse.PostOffice> pinCityResponseArrayList;
    private CommonParams commonParams;
    private String gender="", city="",  state="", address="", gstState="";
    private SimpleDateFormat dateServerFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear;
    private Calendar dobDateCalendar, currentDate;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        pinCityResponseArrayList=new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jct_money_create_sender_rapipay, container, false);
        initializeViews(view);
        initializeDates();
        return view;
    }

    private void initializeViews(View view) {
        if (getArguments().getSerializable("senderResponse") != null) {
            senderDetailResponse = (SenderDetailResponse) getArguments().getSerializable("senderResponse");
            commonParams = (CommonParams) getArguments().getSerializable("commonParams");
        }
        String senderNumber=getArguments().getString("SenderNumber", "");
        Typeface face = Common.TextViewTypeFace(context);
        titleTv =  view.findViewById(R.id.titleTv);
        get_tv =  view.findViewById(R.id.get_tv);
        resendTv = view.findViewById(R.id.resendTv);
        otpDetailTv =  view.findViewById(R.id.otpDetailTv);
        otpLin =  view.findViewById(R.id.otpLin);
        otpEdt =  view.findViewById(R.id.otpEdt);
        number_edt = view.findViewById(R.id.number_edt);
        name_edt =  view.findViewById(R.id.name_edt);
        last_name_edt =  view.findViewById(R.id.last_name_edt);
        genderAtv =  view.findViewById(R.id.genderAtv);
        cityAtv =  view.findViewById(R.id.cityAtv);
        dob_edt =  view.findViewById(R.id.dob_edt);
        pin_edt =  view.findViewById(R.id.pin_edt);
        state_edt =  view.findViewById(R.id.state_edt);

        number_edt.setText(senderNumber);

        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        dob_edt.setOnClickListener(this);
        get_tv.setOnClickListener(this);
        resendTv.setOnClickListener(this);
        cityAtv.setOnClickListener(this);
        get_tv.setTypeface(face);

        final String genderArray[]=new String[]{"MALE","FEMALE"};
        genderAtv.setOnClickListener(this);
        genderAtv.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, genderArray));
        genderAtv.setSelection(0);
        genderAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender=genderArray[position];
            }
        });

        cityAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city=pinCityResponseArrayList.get(position).getName();
                state=pinCityResponseArrayList.get(position).getState();
                address=pinCityResponseArrayList.get(position).getDistrict();
                state_edt.setText(state);
            }
        });

        otpDetailTv.setText("One Time Password ( OTP ) has been send to your mobile "+number_edt.getText().toString()+".\\nPlease enter the same here.");
        if(commonParams.getApiService().equals("1")){
            number_edt.setEnabled(false);
            otpLin.setVisibility(View.VISIBLE);         // new change
            otpDetailTv.setVisibility(View.VISIBLE);     // new change
            titleTv.setText("Add Sender");
            isNewApi=true;
        }else {
            if(senderDetailResponse !=null && senderDetailResponse.getStatusCode().equals(ADD_SENDER)){
                addSenderRequired();
            }else {
                updateRequired();
            }
        }

        pin_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(context, "count="+count+"  text="+s.toString(),Toast.LENGTH_SHORT).show();
                if(s.length()==6){
                    getCityState(s.toString());
                }else {
                    clearCityState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String getGstState(String state) {
        StateList stateList=new Gson().fromJson(StateList.StateListJson, StateList.class);
        for(int i=0; i<stateList.StateList.size(); i++){
            if(stateList.StateList.get(i).Name.equalsIgnoreCase(state)){
                return stateList.StateList.get(i).StateCode;
            }
        }
        return "";
    }

    private void getCityState(String pin) {
        if(!MyCustomDialog.isDialogShowing()){
            showCustomDialog();}
        ApiInterface apiService = APIClient.getClientRapipay().create(ApiInterface.class);
        Call<ArrayList<PinCityResponse>> call = apiService.getServicePin("https://api.postalpincode.in/pincode/"+pin);
        call.enqueue(new Callback<ArrayList<PinCityResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<PinCityResponse>>call, Response<ArrayList<PinCityResponse>> response) {

                try{
                    if(response!=null && response.body()!=null && response.body().size()>0){
                        hideCustomDialog();
                        if(response.body().get(0).getPostOffice()!=null){
                            pinCityResponseArrayList.addAll(response.body().get(0).getPostOffice());
                            String[] arr=new String[pinCityResponseArrayList.size()];
                            for (int p=0;p<pinCityResponseArrayList.size();p++){
                                arr[p]=pinCityResponseArrayList.get(p).getName();
                            }
                            cityAtv.setAdapter(getSpinnerAdapter(arr));
                            Common.hideSoftKeyboard((Activity) context);
                            cityAtv.showDropDown();
                        }else {
                            Toast.makeText(context,response.body().get(0).getMessage(),Toast.LENGTH_LONG).show();
                            clearCityState();
                        }
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
            public void onFailure(Call<ArrayList<PinCityResponse>> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void clearCityState() {
        city="";
        state="";
        pinCityResponseArrayList.clear();
        cityAtv.setAdapter(getSpinnerAdapter(new String[0]));
        cityAtv.clearListSelection();
        cityAtv.setText("");
        state_edt.setText("");
    }

    private void verifySender() {
        number_edt.setEnabled(false);
        otpLin.setVisibility(View.VISIBLE);
        otpDetailTv.setVisibility(View.VISIBLE);
        titleTv.setText("Verify Sender");
        isVerify=true;
    }

    private void addSenderRequired() {
        number_edt.setEnabled(false);
        otpLin.setVisibility(View.GONE);
        otpDetailTv.setVisibility(View.GONE);
        titleTv.setText("Add Sender");
    }
    private void updateRequired() {
        if(senderDetailResponse.getSenderDetailInfo()!=null && senderDetailResponse.getSenderDetailInfo().size()>0){
            number_edt.setEnabled(false);
            otpLin.setVisibility(View.GONE);
            otpDetailTv.setVisibility(View.GONE);
            name_edt.setText(senderDetailResponse.getSenderDetailInfo().get(0).getName());
            dob_edt.setText(senderDetailResponse.getSenderDetailInfo().get(0).getDob());
            titleTv.setText("Update Sender");
        }
    }

    private void addSender(String methodName, String otp, final int responseType) {
        AddSenderRequest jctMoneySenderRequestModel=new AddSenderRequest();
//        jctMoneySenderRequestModel.setName(name_edt.getText().toString().trim());
//        jctMoneySenderRequestModel.setLastName(last_name_edt.getText().toString().trim());
//        jctMoneySenderRequestModel.setMobile(number_edt.getText().toString());
//        jctMoneySenderRequestModel.setAgentCode(loginModel.Data.DoneCardUser);
//        jctMoneySenderRequestModel.setPin(pin_edt.getText().toString());
//        jctMoneySenderRequestModel.setAddress(city);
//        jctMoneySenderRequestModel.setState(state);
//        jctMoneySenderRequestModel.setDob(dob_edt.getText().toString());
//        jctMoneySenderRequestModel.setGender(gender);
//        jctMoneySenderRequestModel.setRequestFor(senderDetailResponse.getRequestFor());
//        jctMoneySenderRequestModel.setSessionKey(commonParams.getSessionKey());
//        jctMoneySenderRequestModel.setSessionRefId(commonParams.getSessionRefNo());
//        jctMoneySenderRequestModel.setApiService(commonParams.getApiService());
        jctMoneySenderRequestModel.otp=otp;
        jctMoneySenderRequestModel.gst_state=getGstState(state);

        new NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, methodName, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, responseType);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());

    }

    private void responseHandler(ResponseBody response, int TYPE) {
//        status code==09  update success(no otp) fundtransferid    code==10 (otp) otprefid fundtransferid inko request me wapis dena h
        try {
            if(TYPE==CREATE_SENDER){
                addSenderResponse = new Gson().fromJson(response.string(), AddSenderResponse.class);
                if(addSenderResponse!=null){
                    if(addSenderResponse.getStatusCode().equals("00")) {
                        getSenderDetail();
                        Toast.makeText(context,addSenderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }else if(addSenderResponse.getStatusCode().equals("09")) {
                        getSenderDetail();
                        Toast.makeText(context,addSenderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }else if(addSenderResponse.getStatusCode().equals("10")) {
                        verifySender();
                        Toast.makeText(context,addSenderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,addSenderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VALIDATE_OTP){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        getSenderDetail();
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e){
            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                isVerify=false;
                getParentFragmentManager().popBackStack();
                break;

            case R.id.get_tv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(get_tv);
                if(Common.checkInternetConnection(context)) {
                    if(isNewApi){
                        if(validate()){
                            if(otpEdt.getText().toString().length()>=4){
                                addSender(ApiConstants.AddSender, otpEdt.getText().toString(),VALIDATE_OTP);
                            }else {
                                Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if(isVerify){
                        if(otpEdt.getText().toString().length()>=4){
                            validateSender(ApiConstants.VerifySender,otpEdt.getText().toString(),VALIDATE_OTP);
                        }else {
                            Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(validate()){
                            addSender(ApiConstants.AddSender, "",CREATE_SENDER);
                        }
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.resendTv:
//                Common.hideSoftKeyboard((MoneyTransferActivity)context);
                Common.preventFrequentClick(get_tv);
                if(validate()){
                    if(Common.checkInternetConnection(context)) {
                        if(validate()){
                            addSender(ApiConstants.AddSender, "",CREATE_SENDER);
                        }
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }}
                break;
            case R.id.genderAtv:
                Common.hideSoftKeyboard((Activity) context);
                genderAtv.showDropDown();
                break;
            case R.id.dob_edt:
                Common.hideSoftKeyboard((Activity) context);
                openDatePicker();
                break;
            case R.id.cityAtv:
                Common.hideSoftKeyboard((Activity) context);
                cityAtv.showDropDown();
                break;
        }
    }

    private void validateSender(String verifySender, String otp,final int responseType) {
        AddSenderRequest jctMoneySenderRequestModel=new AddSenderRequest();
//        jctMoneySenderRequestModel.setMobile(number_edt.getText().toString());
//        jctMoneySenderRequestModel.setAgentCode(loginModel.Data.DoneCardUser);
//        jctMoneySenderRequestModel.setRequestFor(senderDetailResponse.getRequestFor());
//        jctMoneySenderRequestModel.setOtp(otp);
//        jctMoneySenderRequestModel.setSessionKey(commonParams.getSessionKey());
//        jctMoneySenderRequestModel.setSessionRefId(commonParams.getSessionRefNo());
//        jctMoneySenderRequestModel.setApiService(commonParams.getApiService());
//        if(addSenderResponse!=null){
//            jctMoneySenderRequestModel.setOtpRefId(addSenderResponse.getOtpRefId());
//            jctMoneySenderRequestModel.setFundTransferId(addSenderResponse.getFundTransferId());
//        }

        new NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, verifySender, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, responseType);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private Boolean validate() {
        if(!Common.isNameValid(name_edt.getText().toString().trim())) {
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isNameValid(last_name_edt.getText().toString().trim())) {
            Toast.makeText(context, R.string.empty_and_invalid_last_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (number_edt.getText().toString().length() < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }else if (gender.length() ==0) {
            Toast.makeText(context, "please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }else if (dob_edt.getText().toString().length() ==0) {
            Toast.makeText(context, "please select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }else if (pin_edt.getText().toString().length() < 6) {
            Toast.makeText(context, R.string.empty_and_invalid_pincode, Toast.LENGTH_SHORT).show();
            return false;
        }else if (city.length() ==0) {
            Toast.makeText(context, R.string.empty_and_invalid_city, Toast.LENGTH_SHORT).show();
            return false;
        }else if (state.length() ==0) {
            Toast.makeText(context, R.string.empty_and_invalid_state, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getSenderDetail() {
        SenderDetailRequest jctMoneySenderRequestModel=new SenderDetailRequest();
        jctMoneySenderRequestModel.setMobile(number_edt.getText().toString());
        jctMoneySenderRequestModel.setAgentCode(loginModel.Data.DoneCardUser);
        jctMoneySenderRequestModel.setSessionKey(commonParams.getSessionKey());
        jctMoneySenderRequestModel.setSessionRefId(commonParams.getSessionRefNo());
        jctMoneySenderRequestModel.setApiService(commonParams.getApiService());
//{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
        new NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerSenderDetail(response, SENDER_DETAIL);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private void responseHandlerSenderDetail(ResponseBody response, int TYPE) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                getParentFragmentManager().popBackStack();
                if(senderResponse.getStatusCode().equals("00")){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    bundle.putSerializable("commonParams", commonParams);
                    RapipaySenderDetailFragment senderDetailFragment=new RapipaySenderDetailFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithTag(senderDetailFragment, FragmentTags.jctMoneySenderDetailFragment);
                }else if(senderResponse.getStatusCode().equals("05")){
//                    add sender
//                    getsender ka response requestfor me dena h
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    bundle.putSerializable("commonParams", commonParams);
                    RapipayAddSenderFragment senderDetailFragment=new RapipayAddSenderFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(senderDetailFragment);
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else if(senderResponse.getStatusCode().equals("06")){
//                    update sender
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    bundle.putSerializable("commonParams", commonParams);
                    RapipayAddSenderFragment senderDetailFragment=new RapipayAddSenderFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(senderDetailFragment);
                    Toast.makeText(context, senderResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, senderResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void initializeDates() {
        //Date formats
        dateServerFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        currentDate =Calendar.getInstance();
        dobDateCalendar =Calendar.getInstance();

        checkInDateDay = currentDate.get(Calendar.DAY_OF_MONTH);
        checkInDateMonth = currentDate.get(Calendar.MONTH);
        checkInDateYear = currentDate.get(Calendar.YEAR);

    }

    private void openDatePicker() {
        //Date formats

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dobDateCalendar.set(year, monthOfYear, dayOfMonth);
                        dob_edt.setText(dateServerFormat.format(dobDateCalendar.getTime()));
                    }

                },checkInDateYear, checkInDateMonth, checkInDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

}

