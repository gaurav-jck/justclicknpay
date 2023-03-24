package com.justclick.clicknbook.Fragment.jctmoney;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
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
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

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
        View view = inflater.inflate(R.layout.fragment_jct_money_get_sender, container, false);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        if(commonParams.getToken()==null && commonParams.getUserData()==null){
            checkCredential();
        }
        initializeViews(view);
        return view;
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
//{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
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

    private void responseHandlerOld(ResponseBody response, int TYPE) {
        try {
            JctMoneySenderDetailResponseModel senderResponse = new Gson().fromJson(response.string(), JctMoneySenderDetailResponseModel.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode ==1 && (senderResponse.state.equalsIgnoreCase("2") ||
                        senderResponse.state.equalsIgnoreCase("4"))) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    JctMoneySenderDetailFragment jctMoneySenderDetailFragment=new JctMoneySenderDetailFragment();
                    jctMoneySenderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithTag(jctMoneySenderDetailFragment, FragmentTags.jctMoneySenderDetailFragment);
//                    Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                } else if(senderResponse.statusCode ==1 && senderResponse.state.equalsIgnoreCase("1")){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    JctMoneyCreateSenderFragment jctMoneyCreateSenderFragment=new JctMoneyCreateSenderFragment();
                    jctMoneyCreateSenderFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(jctMoneyCreateSenderFragment);
                    Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                }else{
                    Bundle bundle=new Bundle();
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    JctMoneyCreateSenderFragment jctMoneyCreateSenderFragment=new JctMoneyCreateSenderFragment();
                    jctMoneyCreateSenderFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(jctMoneyCreateSenderFragment);
                    Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
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
                break;
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

}

