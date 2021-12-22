package com.justclick.clicknbook.Fragment.jctmoney;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.JctCreateSenderResponse;
import com.justclick.clicknbook.model.JctMoneySenderDetailResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.JctMoneySenderRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;


public class JctMoneyCreateSenderFragment extends Fragment implements View.OnClickListener {
    private final int CREATE_SENDER=1, VALIDATE_OTP=2, RESEND_OTP=3, SENDER_DETAIL=4;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView get_tv, resendTv, titleTv, otpDetailTv;
    private EditText number_edt,name_edt, otpEdt;
    private LinearLayout otpLin;
    private LoginModel loginModel;
    private boolean isValidate=false, isVerify=false;
    private JctMoneySenderDetailResponseModel jctMoneySenderDetailResponseModel;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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
        View view = inflater.inflate(R.layout.fragment_jct_money_create_sender, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        if (getArguments().getSerializable("senderResponse") != null) {
            jctMoneySenderDetailResponseModel = (JctMoneySenderDetailResponseModel) getArguments().getSerializable("senderResponse");
        }
        String senderNumber=getArguments().getString("SenderNumber", "");
        titleTv =  view.findViewById(R.id.titleTv);
        get_tv =  view.findViewById(R.id.get_tv);
        resendTv = view.findViewById(R.id.resendTv);
        otpDetailTv =  view.findViewById(R.id.otpDetailTv);
        otpLin =  view.findViewById(R.id.otpLin);
        number_edt = view.findViewById(R.id.number_edt);
        otpEdt =  view.findViewById(R.id.otpEdt);
        number_edt.setText(senderNumber);
        number_edt.setEnabled(true);
        name_edt =  view.findViewById(R.id.name_edt);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        get_tv.setOnClickListener(this);
        resendTv.setOnClickListener(this);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        get_tv.setTypeface(face);

        if(jctMoneySenderDetailResponseModel!=null){
            number_edt.setText(jctMoneySenderDetailResponseModel.customer_id);
            name_edt.setText(jctMoneySenderDetailResponseModel.name);
            number_edt.setEnabled(false);
            name_edt.setEnabled(false);
            otpLin.setVisibility(View.VISIBLE);
            otpDetailTv.setVisibility(View.VISIBLE);
            name_edt.setEnabled(false);
            number_edt.setEnabled(false);
            titleTv.setText("Validate Sender");
            isVerify=true;
        }
    }

    private void getCustomerDetail(String methodName, String otp, final int responseType) {
        JctMoneySenderRequestModel jctMoneySenderRequestModel=new JctMoneySenderRequestModel();
        jctMoneySenderRequestModel.Name =name_edt.getText().toString().trim();
        jctMoneySenderRequestModel.MobileNo =number_edt.getText().toString();
        jctMoneySenderRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        jctMoneySenderRequestModel.OTP=otp;
        jctMoneySenderRequestModel.benreq=true;
        jctMoneySenderRequestModel.DeviceId=Common.getDeviceId(context);
        jctMoneySenderRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

        new NetworkCall().callJctMoneyService(jctMoneySenderRequestModel, methodName, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, responseType);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            if(TYPE==CREATE_SENDER){
                JctCreateSenderResponse senderResponse = new Gson().fromJson(response.string(), JctCreateSenderResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode==1) {
                        isValidate=true;
                        changeVisibility();
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VALIDATE_OTP){
                JctCreateSenderResponse senderResponse = new Gson().fromJson(response.string(), JctCreateSenderResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode==1) {
//                        isValidate=true;
//                        changeVisibility();
                        getCustomerDetail(ApiConstants.GetSenderDetails, "", SENDER_DETAIL);
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==RESEND_OTP){
                JctCreateSenderResponse senderResponse = new Gson().fromJson(response.string(), JctCreateSenderResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode==1) {
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                        Toast.makeText(context,"Otp resend successfully",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else{
                JctMoneySenderDetailResponseModel senderResponse = new Gson().fromJson(response.string(), JctMoneySenderDetailResponseModel.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode ==1) {
                        getFragmentManager().popBackStack();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("senderResponse", senderResponse);
                        JctMoneySenderDetailFragment jctMoneySenderDetailFragment=new JctMoneySenderDetailFragment();
                        jctMoneySenderDetailFragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithTag(jctMoneySenderDetailFragment, FragmentTags.jctMoneySenderDetailFragment);
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        getFragmentManager().popBackStack();
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e){
            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeVisibility() {
        if(isValidate){
            otpLin.setVisibility(View.VISIBLE);
            otpDetailTv.setVisibility(View.VISIBLE);
            name_edt.setFocusable(false);
            number_edt.setEnabled(false);
            titleTv.setText("Validate Sender");
        }else {
            otpLin.setVisibility(View.GONE);
            otpDetailTv.setVisibility(View.GONE);
            name_edt.setFocusable(true);
            number_edt.setEnabled(true);
            titleTv.setText("Create Sender");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                if(isValidate && !isVerify) {
                    isValidate = false;
                    changeVisibility();
                }else {
                    getFragmentManager().popBackStack();
                }
                break;

            case R.id.get_tv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(get_tv);
                if(Common.checkInternetConnection(context)) {
                    if(isValidate){
                        if(otpEdt.getText().toString().length()>=3){
                            getCustomerDetail(ApiConstants.ValidateSOTP,otpEdt.getText().toString(),VALIDATE_OTP);
                        }else {
                            Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(validate()){
                            getCustomerDetail(ApiConstants.CreateSender, "",CREATE_SENDER);
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

                        getCustomerDetail(ApiConstants.ResendSOTP, "", RESEND_OTP);
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }}
                break;
        }
    }
    private Boolean validate() {
        if(!Common.isNameValid(name_edt.getText().toString().trim())) {
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (number_edt.getText().toString().length() < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

