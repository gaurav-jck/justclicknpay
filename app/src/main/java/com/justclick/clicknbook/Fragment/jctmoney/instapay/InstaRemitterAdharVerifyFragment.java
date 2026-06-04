package com.justclick.clicknbook.Fragment.jctmoney.instapay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.AddBeneOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaAddRemitterAadharRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.databinding.FragmentInstaRemitterAdharVerifyBinding;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import okhttp3.ResponseBody;


public class InstaRemitterAdharVerifyFragment extends Fragment implements View.OnClickListener {
    private final int AddRecipient=1,VerifyAccount=3;
    private Context context;
    private Activity activity;
    private FragmentInstaRemitterAdharVerifyBinding binding;
    private LoginModel loginModel;
    private String Mobile, bankName;
    private CheckCredentialResponse.credentialData commonParams;
    private SenderDetailResponse senderDetailResponse;
    private SenderDetailResponse.senderDetailInfo senderInfo;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

        if(getArguments()!=null && getArguments().getSerializable("senderResponse")!=null){
//            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
            senderDetailResponse = (SenderDetailResponse) getArguments().getSerializable("senderResponse");
            commonParams = (CheckCredentialResponse.credentialData) getArguments().getSerializable("commonParams");
            senderInfo = senderDetailResponse.getSenderDetailInfo().get(0);
            Mobile=  getArguments().getString("mobile");
            bankName=  getArguments().getString("bankName");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
        }catch (ClassCastException e){
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInstaRemitterAdharVerifyBinding.inflate(getLayoutInflater());
        getIpAddress();
        initializeViews();
        return binding.getRoot();
    }

    private void initializeViews() {

        binding.getOtpTv.setOnClickListener(this);
        binding.verifyOtpTv.setOnClickListener(this);
        binding.backArrow.setOnClickListener(this);

        binding.mobileEdt.setText(Mobile);

        binding.otpView.setVisibility(View.GONE);
        binding.verifyOtpTv.setVisibility(View.GONE);

        setText();

    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    private void setText() {
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);

        binding.aadharEdt.setTypeface(face2);
        binding.otpEdt.setTypeface(face2);

        binding.getOtpTv.setTypeface(face3);
        binding.verifyOtpTv.setTypeface(face3);

    }

    private boolean validate() {
        if(binding.aadharEdt.getText().toString().trim().isEmpty()){
            Toast.makeText(context,"Please enter aadhar number",Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.aadharEdt.getText().toString().length()<12){
            Toast.makeText(context,"Please enter valid aadhar number",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.getOtpTv:
                Common.preventFrequentClick(binding.getOtpTv);
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        getAadharOtp();
//                        responseHandlerOtp(null);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verifyOtpTv:
                String otp=binding.otpEdt.getText().toString();
                Common.preventFrequentClick(binding.verifyOtpTv);
                if(Common.checkInternetConnection(context)){
                    if(otp.isEmpty()){
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }else if(otp.length()<6){
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }else {
                        verifyAadharOtp();
//                        responseHandlerVerify(null);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getAadharOtp() {
        InstaAddRemitterAadharRequest requestModel=new InstaAddRemitterAadharRequest();
        requestModel.AgentCode=loginModel.Data.DoneCardUser;
        requestModel.Mobile=Mobile;
        requestModel.IPAddress=ip;
        requestModel.stateResp=senderInfo.getStateResp();
        requestModel.Aadhaar_no=binding.aadharEdt.getText().toString();

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.aadharverify,
                        requestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerOtp(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    String beneId, stateRes;
    String pidWadh;
    private void responseHandlerOtp(ResponseBody response) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    stateRes=senderResponse.getSenderDetailInfo().get(0).getStateResp();
                    pidWadh=senderResponse.getSenderDetailInfo().get(0).getPidOptionWadh();
                    showOtpView();
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void verifyAadharOtp() {
        InstaAddRemitterAadharRequest requestModel=new InstaAddRemitterAadharRequest();
        requestModel.AgentCode=loginModel.Data.DoneCardUser;
        requestModel.Mobile=Mobile;
        requestModel.IPAddress=ip;
        requestModel.stateResp=stateRes;
        requestModel.Aadhaar_no=binding.aadharEdt.getText().toString();
        requestModel.OTP=binding.otpEdt.getText().toString();

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.registerRemitterverify,
                        requestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerVerify(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerVerify(ResponseBody response) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    String stateRes=senderResponse.getSenderDetailInfo().get(0).getStateResp();
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("stateRes", stateRes);
                    bundle.putSerializable("commonParams", commonParams);
                    bundle.putString("mobile", Mobile);
                    bundle.putString("bankName", bankName);
                    bundle.putString("aadhar", binding.aadharEdt.getText().toString());
                    bundle.putString("pidWadh", senderInfo.getPidOptionWadh());
                    InstaRemitterKycFragment senderDetailFragment=new InstaRemitterKycFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragment(senderDetailFragment);
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showOtpView() {
        binding.otpView.setVisibility(View.VISIBLE);
        binding.verifyOtpTv.setVisibility(View.VISIBLE);
        binding.getOtpTv.setVisibility(View.GONE);
        binding.aadharEdt.setEnabled(false);
        binding.aadharEdt.setAlpha(0.6f);
    }

    String ip;
    void getIpAddress(){
        requireActivity().runOnUiThread(() -> {
            try {
                URL url = new URL("https://api.ipify.org");
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0/Chrome"); // Set a User-Agent to avoid HTTP 403 Forbidden error
                InputStream inputStream = connection.getInputStream();
                Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                ip = s.next();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                ip = "103.139.75.200";
            }
            // Update UI elements here
        });
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(isBackPress) {
//            backPress.onJctDetailBackPress();
//        }
    }

    String otpRes="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"OTP Successfully sent\",\n" +
            "    \"sessionRefId\": null,\n" +
            "    \"bank1\": 0,\n" +
            "    \"type1\": null,\n" +
            "    \"bank2\": 0,\n" +
            "    \"type2\": null,\n" +
            "    \"bank3\": 0,\n" +
            "    \"type3\": null,\n" +
            "    \"sessionKey\": null,\n" +
            "    \"requestFor\": null,\n" +
            "    \"remainingLimit\": 0,\n" +
            "    \"senderDetailInfo\": [\n" +
            "        {\n" +
            "            \"dob\": \"N/A\",\n" +
            "            \"gender\": \"Male\",\n" +
            "            \"mobile\": \"\",\n" +
            "            \"name\": \"\",\n" +
            "            \"pin\": \"\",\n" +
            "            \"city\": \"\",\n" +
            "            \"state\": \"N/A\",\n" +
            "            \"stateResp\": \"vA7GF3ZVxbhjgloduggmGtnJ7iKp0oEDRlin1gtQUqW0rWvaNcKyfwwfkYbrNpU0.v2.07cd1ff280584490-6111-4508-a824-f4802bda9725\",\n" +
            "            \"pidOptionWadh\": \"\",\n" +
            "            \"ekyc_id\": \"\",\n" +
            "            \"limit\": \"\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"benificiaryDetailData\": null\n" +
            "}";

    String otpVerifyResponse="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Mobile validated successfully please proceed for kyc\",\n" +
            "    \"sessionRefId\": null,\n" +
            "    \"bank1\": 0,\n" +
            "    \"type1\": null,\n" +
            "    \"bank2\": 0,\n" +
            "    \"type2\": null,\n" +
            "    \"bank3\": 0,\n" +
            "    \"type3\": null,\n" +
            "    \"sessionKey\": null,\n" +
            "    \"requestFor\": null,\n" +
            "    \"remainingLimit\": 0,\n" +
            "    \"senderDetailInfo\": null,\n" +
            "    \"benificiaryDetailData\": null\n" +
            "}";
}