package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordRequest;
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordResponse;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams;
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.JctMoneySenderDetailResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.network.SaveLogs;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;


public class JctMoneyGetSenderFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    TextView get_tv;
    EditText number_edt;
    private LoginModel loginModel;
    private CommonParams commonParams;
    private String kycStatus;
    private boolean isCheckCredential =false;
    private View mView;
    private TextWatcher textWatcher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        commonParams=new CommonParams();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_jct_money_get_sender, container, false);
            toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
            if(commonParams.getToken()==null && commonParams.getUserData()==null){
                checkCredential();
            }
            initializeViews(mView);
//            tpinDialog();
        }
        return mView;
    }

    private void checkCredential() {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        CheckCredentialRequest request=new CheckCredentialRequest();
        request.setAgentCode(loginModel.Data.DoneCardUser);

        new NetworkCall().callRapipayService(request, ApiConstants.CheckCredential, context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerCredential(response, 1);    //https://remittance.justclicknpay.com/api/payments/CheckCredential
                    }else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                        isCheckCredential =true;
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerCredential(ResponseBody response, int TYPE) {
        try {
            CheckCredentialResponse senderResponse = new Gson().fromJson(response.string(), CheckCredentialResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
//                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    commonParams.setSessionKey(senderResponse.getCredentialData().get(0).getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getCredentialData().get(0).getSessionRefNo());
                    commonParams.setToken(senderResponse.getCredentialData().get(0).getToken());
                    commonParams.setUserData(senderResponse.getCredentialData().get(0).getUserData());
                    commonParams.setKycStatus(senderResponse.getCredentialData().get(0).getKycStatus());
                    commonParams.setApiService(senderResponse.apiServices);
                    commonParams.setAddress(senderResponse.getCredentialData().get(0).address);
                    commonParams.setPinCode(senderResponse.getCredentialData().get(0).pinCode);
                    commonParams.setState(senderResponse.getCredentialData().get(0).state);
                    commonParams.setCity(senderResponse.getCredentialData().get(0).city);
                    commonParams.setStatecode(senderResponse.getCredentialData().get(0).statecode);
                    commonParams.isBank2= senderResponse.getCredentialData().get(0).isBank2;
                    commonParams.isBank3= senderResponse.getCredentialData().get(0).isBank3;
                    if(isCheckCredential){
                        isCheckCredential =false;
                        getSenderDetail();
                    }
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                }
            }else {
                isCheckCredential =true;
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            isCheckCredential =true;
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews(View view) {
        get_tv = view.findViewById(R.id.get_tv);
        number_edt =  view.findViewById(R.id.number_edt);
        get_tv.setOnClickListener(this);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        get_tv.setTypeface(face);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length()==10){
                    getClicked();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

//        number_edt.addTextChangedListener(textWatcher);
    }

    private void tpinDialog(){
        Dialog otpDialog = new Dialog(context);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.set_tpin_layout);
        otpDialog.setCancelable(false);
        final EditText tpinEdt= otpDialog.findViewById(R.id.tpinEdt);
        final TextView confirmTpinEdt= otpDialog.findViewById(R.id.confirmTpinEdt);
        final TextView backTv= otpDialog.findViewById(R.id.backTv);
        final TextView submit_tv= otpDialog.findViewById(R.id.submit_tv);

        tpinEdt.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        submit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit_tv);
                Common.hideSoftKeyboard(requireActivity());

                String tpin=tpinEdt.getText().toString().trim();
                String tpinConfirm=confirmTpinEdt.getText().toString().trim();

                if(Common.checkInternetConnection(context)){
                    if(tpin.isEmpty()){
                        Toast.makeText(context, "Please enter TPIN", Toast.LENGTH_SHORT).show();
                    }
                    else if(tpinConfirm.isEmpty()){
                        Toast.makeText(context, "Please confirm your TPIN", Toast.LENGTH_SHORT).show();
                    }
                    else if(!tpinConfirm.equals(tpin)){
                        Toast.makeText(context, "TPIN you entered does not matched with confirm TPIN", Toast.LENGTH_LONG).show();
                    }else {
                        setTpin(tpin, otpDialog);
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                otpDialog.dismiss();
                getParentFragmentManager().popBackStack();
            }
        });
        otpDialog.show();
    }

    private void setTpin(String otp, Dialog otpDialog) {
        ChangePasswordRequest request=new ChangePasswordRequest();
        request.oldpassword= otp;
        request.BookUserID= loginModel.Data.UserId;
        request.MerchantId= ApiConstants.MerchantId;
        request.OTP= otp;

        String json = new Gson().toJson(request);

        Map<String,String> params = new HashMap<>();
        params.put("MPIN", otp);
        params.put("BookUserID", request.BookUserID);
        params.put("MerchantId", request.MerchantId);

        new NetworkCall().callService(NetworkCall.getTpinApiInterface().changePassword(ApiConstants.setnewmpin, params),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        otpDialog.dismiss();
                        responseHandlerPassword(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerPassword(ResponseBody response, int i) {
        try {
            ChangePasswordResponse senderResponse = new Gson().fromJson(response.string(),
                    ChangePasswordResponse.class);
            if(senderResponse!=null){
                if(senderResponse.status.equals("00")) {
                    Common.showResponsePopUp(context, senderResponse.description);
                }else if(senderResponse.description!=null){
                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"Error in setting tpin, please try after some time.",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void getSenderDetail() {
        if(validate())
        {
            SenderDetailRequest jctMoneySenderRequestModel=new SenderDetailRequest();
            jctMoneySenderRequestModel.setMobile(number_edt.getText().toString());
            jctMoneySenderRequestModel.setAgentCode(loginModel.Data.DoneCardUser);
            jctMoneySenderRequestModel.setSessionKey(commonParams.getSessionKey());
            jctMoneySenderRequestModel.setSessionRefId(commonParams.getSessionRefNo());
            jctMoneySenderRequestModel.setApiService(commonParams.getApiService());  //new change
            jctMoneySenderRequestModel.setIsBank2(commonParams.isBank2);  //new change
            jctMoneySenderRequestModel.setIsBank3(commonParams.isBank3);  //new change
            commonParams.agentCode=loginModel.Data.DoneCardUser;
            commonParams.mobile=number_edt.getText().toString();
            new NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, 1);
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, commonParams.getUserData(), commonParams.getToken());
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    commonParams.mobile=number_edt.getText().toString();
                    commonParams.agentCode=loginModel.Data.DoneCardUser;
                    bundle.putSerializable("commonParams", commonParams);
                    RapipaySenderDetailFragment senderDetailFragment=new RapipaySenderDetailFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithTag(senderDetailFragment, FragmentTags.jctMoneySenderDetailFragment);
                }else if(senderResponse.getStatusCode().equals("02")){
//                    add sender
//                    getsender ka response requestfor me dena h
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    commonParams.mobile=number_edt.getText().toString();
                    commonParams.agentCode=loginModel.Data.DoneCardUser;
                    bundle.putSerializable("commonParams", commonParams);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(DmtKycFragment.newInstance(senderResponse, commonParams));
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else if(senderResponse.getStatusCode().equals("03")){
//                    add sender
//                    getsender ka response requestfor me dena h
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    commonParams.setSessionKey(senderResponse.getSessionKey());
                    commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                    commonParams.mobile=number_edt.getText().toString();
                    commonParams.agentCode=loginModel.Data.DoneCardUser;
                    bundle.putSerializable("commonParams", commonParams);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(AddRemittanceFragment.newInstance(commonParams,
                            senderResponse.getSenderDetailInfo().get(0).getStateResp(), senderResponse.getSenderDetailInfo().get(0).getEkyc_id()));
                    Common.showResponsePopUp(requireContext(), senderResponse.getStatusMessage());
                }else {
                    Common.showResponsePopUp(requireContext(), senderResponse.getStatusMessage());
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;

            case R.id.get_tv:
                getClicked();
                break;
        }
    }

    private void getClicked() {
        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
        Common.preventFrequentClick(get_tv);
        if(Common.checkInternetConnection(context)) {
            if(isCheckCredential){
                checkCredential();
            }else {
                getSenderDetail();
            }
        }else {
            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validate() {

        if (number_edt.getText().toString().length() < 10)
        {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        number_edt.addTextChangedListener(textWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        number_edt.removeTextChangedListener(textWatcher);
    }
}

