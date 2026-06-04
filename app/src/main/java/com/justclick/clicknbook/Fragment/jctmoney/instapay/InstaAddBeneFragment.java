package com.justclick.clicknbook.Fragment.jctmoney.instapay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Api;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.dmt3.requestModel.AddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.AddBeneOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaAddBeneOtpVerifyRequest;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaAddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.databinding.FragmentInstaAddBeneBinding;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InstaAddBeneFragment extends Fragment implements View.OnClickListener {
    private final int AddRecipient=1,VerifyAccount=3;
    private Context context;
    private Activity activity;
    private FragmentInstaAddBeneBinding binding;
    private FragmentBackPressListener backPress;
    private TextInputLayout Address;
    private String bankName, Mobile, bankId;
    private DataBaseHelper dataBaseHelper;
    private LoginModel loginModel;
    private BankResponse ifscByCodeResponse;
    private ArrayList<BankResponse> bankArray;
    private CheckCredentialResponse.credentialData commonParams;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataBaseHelper=new DataBaseHelper(context);
        bankArray=new ArrayList<>();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

        getIpAddress();

        if(getArguments()!=null && getArguments().getString("Mobile")!=null){
//            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
            Mobile=  getArguments().getString("Mobile");
            commonParams= (CheckCredentialResponse.credentialData) getArguments().getSerializable("commonParams");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
            backPress= (FragmentBackPressListener) context;
        }catch (ClassCastException e){
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInstaAddBeneBinding.inflate(getLayoutInflater());
        initializeViews();
        return binding.getRoot();
    }

    private void initializeViews() {
        binding.Address.setHint("Bank IFSC");

        binding.addBeneTv.setOnClickListener(this);
        binding.verifyAccountTv.setOnClickListener(this);
        binding.otpVerifyTv.setOnClickListener(this);
        binding.backArrow.setOnClickListener(this);

//        binding.userMobileEdt.setText(Mobile);
//        binding.userMobileEdt.setEnabled(false);

        binding.otp.setVisibility(View.GONE);
        binding.otpVerifyTv.setVisibility(View.GONE);

        setText();

        if(dataBaseHelper.getJctBankNamesWithIFSC()!=null && dataBaseHelper.getJctBankNamesWithIFSC().size()>0) {
            String[] arr=new String[dataBaseHelper.getJctBankNamesWithIFSC().size()];
            for (int p=0; p<dataBaseHelper.getJctBankNamesWithIFSC().size(); p++){
                arr[p]=dataBaseHelper.getJctBankNamesWithIFSC().get(p).Name;
            }
            binding.atvBank.setAdapter(getSpinnerAdapter(arr));
        }else {
            getBankNames();
        }

        binding.atvBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < bankArray.size(); i++) {
                    if (bankArray.get(i).getBANK_NAME().equals(selection)) {
                        pos = i;
                        break;
                    }
                }
//                if(pos>0){
                try{
                    ifscByCodeResponse=bankArray.get(pos);
                    bankName=binding.atvBank.getText().toString();
                    binding.ifscEdt.setText(ifscByCodeResponse.getMASTER_IFSC_CODE());

                }catch (Exception e){
//                        IFSCCodeEdt.setText("");
//                        ifscByCodeResponse=null;
                }
                /*}else {
                    bankName="";
                    ifscEdt.setText("");
                    ifscByCodeResponse=null;
                    verifyAccountTv.setVisibility(View.GONE);
                }*/
            }
        });


        binding.atvBank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) binding.atvBank.showDropDown();
            }
        });

        binding.atvBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.atvBank.showDropDown();
            }
        });

    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    private void getBankNames() {
        if(!MyCustomDialog.isDialogShowing()){
            showCustomDialog();}
        Call<ArrayList<BankResponse>> call = NetworkCall.getDmtInstaApiInterface().getService(ApiConstants.BASE_URL_INSTAPAY+"api/v2/DMT/"+ApiConstants.GetBankName);
        call.enqueue(new Callback<ArrayList<BankResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BankResponse>>call, Response<ArrayList<BankResponse>> response) {

                try{
                    if(response.body()!=null && response.body().size()>0){
                        hideCustomDialog();
//                        JctIfscByCodeResponse relationResponse=new JctIfscByCodeResponse();
//                        relationResponse.Key="";
//                        relationResponse.Name="select-bank";
//                        relationResponse.Digit="A";
                        String[] arr=new String[response.body().size()];
                        bankArray.addAll(response.body());
//                        dataBaseHelper.insertJctBankNamesWithIFSC(bankArray);
                        for (int p=0;p<response.body().size();p++){
                            arr[p]=response.body().get(p).getBANK_NAME();
                        }

                        binding.atvBank.setAdapter(getSpinnerAdapter(arr));
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
            public void onFailure(Call<ArrayList<BankResponse>> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setText() {
        Typeface face1 = Common.TitleTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);
        binding.userMobileEdt.setTypeface(face2);
        binding.ifscEdt.setTypeface(face2);
        binding.accountNoEdt.setTypeface(face2);
        binding.confirmAccountEdt.setTypeface(face2);

        binding.nameEdt.setTypeface(face2);
        binding.addBeneTv.setTypeface(face3);
        binding.verifyAccountTv.setTypeface(face3);
        binding.otpVerifyTv.setTypeface(face3);

    }

    private boolean validate() {
        if(binding.atvBank.getText().toString().trim().length()==0 ){
            Toast.makeText(context,R.string.empty_and_invalid_bank,Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.accountNoEdt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_account_number,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!binding.confirmAccountEdt.getText().toString().trim().equals(binding.accountNoEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_account_confirmation,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(binding.ifscEdt.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isNameValid(binding.nameEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.userMobileEdt.getText().toString().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.addBeneTv:
                Common.preventFrequentClick(binding.addBeneTv);
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        addBeneficiaryGetOtp();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.otpVerifyTv:
                String otp=binding.otpEdt.getText().toString();
                Common.preventFrequentClick(binding.otpVerifyTv);
                if(Common.checkInternetConnection(context)){
                    if(otp.isEmpty()){
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }else if(otp.length()<6){
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }else {
                        addBeneficiaryVerifyOtp();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verifyAccountTv:
                Common.preventFrequentClick(binding.verifyAccountTv);
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        validateBeneficiary(VerifyAccount, false);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void addBeneficiaryVerifyOtp() {
        InstaAddBeneOtpVerifyRequest requestModel=new InstaAddBeneOtpVerifyRequest();
        requestModel.AgentCode=loginModel.Data.DoneCardUser;
        requestModel.Mobile=Mobile;
        requestModel.BeneId=beneId;
        requestModel.StateResp=stateRes;
        requestModel.IPAddress=ip;
        requestModel.OTP=binding.otpEdt.getText().toString();

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.AddBenificiaryVerifyOTP,
                        requestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, AddRecipient);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addBeneficiaryGetOtp() {
        InstaAddBeneRequest requestModel=new InstaAddBeneRequest();
        requestModel.AgentCode=loginModel.Data.DoneCardUser;
        requestModel.BankName=binding.atvBank.getText().toString();
        requestModel.bankid=ifscByCodeResponse.getBankId();   // new change
        requestModel.AccountHolderName=binding.nameEdt.getText().toString();
        requestModel.AccountNumber=binding.accountNoEdt.getText().toString();
        requestModel.ConfirmAccountNumber=binding.accountNoEdt.getText().toString();
        requestModel.IfscCode=binding.ifscEdt.getText().toString();
        requestModel.BeneificiaryMobileNo=binding.userMobileEdt.getText().toString();
        requestModel.Mobile=Mobile;
        requestModel.Latitude="19.1641988";
        requestModel.Longitude="72.8626135";
        requestModel.IPAddress=ip;

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.AddBenificiaryOTP,
                        requestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerBeneOtp(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    String beneId, stateRes;
    private void responseHandlerBeneOtp(ResponseBody response) {
        try {
            AddBeneOtpResponse senderResponse = new Gson().fromJson(response.string(), AddBeneOtpResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("00")){
                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    beneId=senderResponse.beneId;
                    stateRes=senderResponse.stateResp;
                    showOtpView();
                }else {
                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void showOtpView() {
        binding.otp.setVisibility(View.VISIBLE);
        binding.otpVerifyTv.setVisibility(View.VISIBLE);
        binding.addBeneTv.setVisibility(View.GONE);
        binding.atvBank.setEnabled(false);
        binding.accountNoEdt.setEnabled(false);
        binding.confirmAccountEdt.setEnabled(false);
        binding.ifscEdt.setEnabled(false);
        binding.nameEdt.setEnabled(false);
        binding.userMobileEdt.setEnabled(false);
        binding.verifyAccountTv.setEnabled(false);
        binding.verifyAccountTv.setAlpha(0.6f);
        binding.atvBank.setAlpha(0.6f);
        binding.accountNoEdt.setAlpha(0.6f);
        binding.confirmAccountEdt.setAlpha(0.6f);
        binding.nameEdt.setAlpha(0.6f);
        binding.userMobileEdt.setAlpha(0.6f);
        binding.ifscEdt.setAlpha(0.6f);
    }

    private void validateBeneficiary(final int TYPE, boolean isVerify) {
        InstaAddBeneRequest requestModel=new InstaAddBeneRequest();
        requestModel.AgentCode=loginModel.Data.DoneCardUser;
        requestModel.BankName=binding.atvBank.getText().toString();
        requestModel.bankid=ifscByCodeResponse.getBankId();   // new change
        requestModel.AccountHolderName=binding.nameEdt.getText().toString();
        requestModel.AccountNumber=binding.accountNoEdt.getText().toString();
        requestModel.IfscCode=binding.ifscEdt.getText().toString();
        requestModel.BeneificiaryMobileNo=binding.userMobileEdt.getText().toString();
        requestModel.Mobile=Mobile;
        requestModel.Latitude="19.1641988";
        requestModel.Longitude="72.8626135";
        requestModel.IPAddress=ip;

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.ValidateAccount,
                        requestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, VerifyAccount);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class ValidateBeneResponse extends CommonRapiResponse{
        public String beneficiaryName;
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            if(TYPE==AddRecipient){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")){
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        backPress.onJctDetailBackPress(CodeEnum.DMTInsta);
                        getParentFragmentManager().popBackStack();
                    }else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VerifyAccount){
                ValidateBeneResponse senderResponse = new Gson().fromJson(response.string(), ValidateBeneResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                        binding.nameEdt.setText(senderResponse.beneficiaryName);
                        binding.verifyAccountTv.setText("Account Verified");
                        binding.verifyAccountTv.setEnabled(false);
                        binding.verifyAccountTv.setAlpha(0.6f);
                        addBeneficiaryGetOtp();
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
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
}