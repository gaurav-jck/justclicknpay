package com.justclick.clicknbook.Fragment.creditcard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class CreditCardFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private Spinner cardTypeSpinner;
    private boolean isCheckCredential;
    private String token, userData;
    private EditText name_edt, number_edt, card_number_edt, amount_edt, remarks_edt;

    public CreditCardFragment() {
        // Required empty public constructor
    }

    public static CreditCardFragment newInstance(String param1, String param2) {
        CreditCardFragment fragment = new CreditCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_credit_card, container, false);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        name_edt=view.findViewById(R.id.name_edt);
        number_edt=view.findViewById(R.id.number_edt);
        card_number_edt=view.findViewById(R.id.card_number_edt);
        amount_edt=view.findViewById(R.id.amount_edt);
        remarks_edt=view.findViewById(R.id.remarks_edt);

        cardTypeSpinner=view.findViewById(R.id.cardTypeSpinner);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        view.findViewById(R.id.submit_tv).setOnClickListener(this);

        setCardTypeAdapter();

        checkCredential();

        return view;
    }

    private void setCardTypeAdapter() {
//        String[] title1 = { "VISA", "MasterCard", "SBI", "HDFC", "American Express" };
        String[] title = { "VISA", "MASTERCARD"};
        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                title);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        cardTypeSpinner.setAdapter(ad);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                if(validate()){
                    if(isCheckCredential){
                        checkCredential();
                    }else {
                        generateOTP();
                    }
                }
                break;
        }
    }

    private void checkCredential() {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        CheckCredentialRequest request=new CheckCredentialRequest();
        request.setAgentCode(loginModel.Data.DoneCardUser);

        new NetworkCall().callService(NetworkCall.getCreditCardApiInterface().creditCard(ApiConstants.CheckCredential, request,
                        "", ""), context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerCredential(response, 1);
                    }else {
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
                    token=senderResponse.getCredentialData().get(0).getToken();
                    userData=senderResponse.getCredentialData().get(0).getUserData();
                    if(isCheckCredential){
                        isCheckCredential =false;
                        generateOTP();
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

    private void generateOTP() {
        CreditCardRequest request=new CreditCardRequest();
        request.agentcode= MyPreferences.getLoginData(new LoginModel(), context).Data.DoneCardUser;
        request.name= name_edt.getText().toString();
        request.mobile= number_edt.getText().toString();
        request.card_number= card_number_edt.getText().toString();
        request.amount= amount_edt.getText().toString();
        request.network= cardTypeSpinner.getSelectedItem().toString();
        request.remarks= remarks_edt.getText().toString();

        String json = new Gson().toJson(request);

        new NetworkCall().callService(NetworkCall.getCreditCardApiInterface().creditCard(ApiConstants.generateOTP, request,
                        userData, "Bearer "+token), context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerOTP(response, 1, request);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerOTP(ResponseBody response, int i, CreditCardRequest request) {
        try {
            GenerateOtpResponse senderResponse = new Gson().fromJson(response.string(), GenerateOtpResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("00")) {
                    Toast.makeText(context,senderResponse.statusmessage,Toast.LENGTH_SHORT).show();
                    request.transactionid= senderResponse.jcktransactionid;
                    Fragment fragment=new CreditCardTxnFragment();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("Request", request);
                    bundle.putString("token", token);
                    bundle.putString("userData", userData);
                    fragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragment(fragment);
                }else {
                    Toast.makeText(context,senderResponse.statusmessage,Toast.LENGTH_SHORT).show();
                   /* request.transactionid= senderResponse.jcktransactionid;
                    Fragment fragment=new CreditCardTxnFragment();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("Request", request);
                    bundle.putString("token", token);
                    bundle.putString("userData", userData);
                    fragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragment(fragment);*/
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        if(!Common.isNameValid(name_edt.getText().toString())){
           Toast.makeText(context, R.string.empty_and_invalid_card_holder, Toast.LENGTH_SHORT).show();
           return false;
        }else if(!Common.isMobileValid(number_edt.getText().toString())){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isCreditCardValid(card_number_edt.getText().toString())){
            Toast.makeText(context, R.string.empty_and_invalid_credit_card, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(amount_edt.getText().toString())){
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(Integer.parseInt(amount_edt.getText().toString())<100 || Integer.parseInt(amount_edt.getText().toString())>25000){
            Toast.makeText(context, "Please enter amount between 100 to 25000", Toast.LENGTH_SHORT).show();
            return false;
        }else if(remarks_edt.getText().toString().trim().length()==0){
            Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}