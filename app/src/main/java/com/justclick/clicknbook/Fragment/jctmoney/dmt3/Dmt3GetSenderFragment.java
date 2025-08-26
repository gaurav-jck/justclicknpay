package com.justclick.clicknbook.Fragment.jctmoney.dmt3;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.dmt2.Dmt2SenderDetailFragment;
import com.justclick.clicknbook.Fragment.jctmoney.dmt3.requestModel.GetSenderRequest;
import com.justclick.clicknbook.Fragment.jctmoney.dmt3.responseModel.CheckMerchantResponse;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class Dmt3GetSenderFragment extends Fragment implements View.OnClickListener {
    private final String ARG_PARAM1 = "param1";
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    TextView get_tv;
    EditText number_edt;
    private LoginModel loginModel;
    private CheckCredentialResponse.credentialData credentialResponse;
    private boolean isCheckCredential =false;
    private View mView;
    private TextWatcher textWatcher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments().getSerializable("credentialResponse") != null) {
            credentialResponse = (CheckCredentialResponse.credentialData) getArguments().getSerializable("credentialResponse");
        }
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
            checkMerchant();
            initializeViews(mView);
//            tpinDialog();
        }
        return mView;
    }

    private void checkMerchant() {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        CheckCredentialRequest request=new CheckCredentialRequest();
        request.setAgentCode(loginModel.Data.DoneCardUser);

        new NetworkCall().callService(NetworkCall.getDmt3ApiInterface().getDmt3Header(ApiConstants.checkmerchant, request,
                        credentialResponse.getUserData(), "Bearer "+credentialResponse.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerCheckMerchant(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerCheckMerchant(ResponseBody response, int TYPE) {
        try {
            CheckMerchantResponse senderResponse = new Gson().fromJson(response.string(), CheckMerchantResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else if(senderResponse.getStatusCode().equals("05")){
//                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                    merchantAlert(senderResponse.getStatusMessage());
                }else {
//                    merchantAlert(senderResponse.getStatusMessage());
                    Common.showCommonAlertDialog(requireContext(), senderResponse.getStatusMessage(), "Api response");
//                    getParentFragmentManager().popBackStack();
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

    private void merchantAlert(String statusMessage) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Api response");
        builder.setMessage(statusMessage);
        builder.setCancelable(false);

        // add a button
        builder.setPositiveButton("Register now", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(Dmt3MerchantKycFragment.newInstance(credentialResponse));
            dialog.dismiss();
        });
        // add a button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            dialog.dismiss();
            getParentFragmentManager().popBackStack();
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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


    private void getSenderDetail() {
        if(validate())
        {
            GetSenderRequest senderRequestModel=new GetSenderRequest();
            senderRequestModel.Mobile=number_edt.getText().toString();
            senderRequestModel.AgentCode=loginModel.Data.DoneCardUser;
            senderRequestModel.Latitude="19.1641988";
            senderRequestModel.Longitude="72.8626135";
            new NetworkCall().callService(NetworkCall.getDmt3ApiInterface().getDmt3Header(ApiConstants.queryremitter,
                            senderRequestModel, credentialResponse.getUserData(), "Bearer "+credentialResponse.getToken()),
                    context,true,
                    (response, responseCode) -> {
                        if(response!=null){
                            responseHandler(response, 1);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    credentialResponse.setSessionKey(senderResponse.getSessionKey());
                    bundle.putSerializable("commonParams", credentialResponse);
                    Dmt3SenderDetailFragment senderDetailFragment=new Dmt3SenderDetailFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithTag(senderDetailFragment, FragmentTags.dmt2SenderDetailFragment);
                }else if(senderResponse.getStatusCode().equals("01")){
//                    add sender
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(Dmt3MerchantKycFragment.newInstance(credentialResponse));
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else if(senderResponse.getStatusCode().equals("05")){
//                    add sender
//                    getsender ka response requestfor me dena h
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    credentialResponse.setSessionKey(senderResponse.getSessionKey());
                    bundle.putSerializable("commonParams", credentialResponse);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(Dmt3KycFragment.newInstance( number_edt.getText().toString(), credentialResponse));
//                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(Dmt2FaceKycFragment.newInstance(senderResponse, commonParams));
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
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
            getSenderDetail();
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

