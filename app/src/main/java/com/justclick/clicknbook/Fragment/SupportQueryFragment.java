package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.media.tv.CommandResponse;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.creditcard.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.creditcard.CreditCardRequest;
import com.justclick.clicknbook.Fragment.creditcard.CreditCardTxnFragment;
import com.justclick.clicknbook.Fragment.creditcard.GenerateOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.SupportQueryRequest;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class SupportQueryFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private Spinner productTypeSpinner;
    private EditText name_edt, number_edt, email_edt, remarks_edt;

    public SupportQueryFragment() {
        // Required empty public constructor
    }

    public static SupportQueryFragment newInstance(String param1, String param2) {
        SupportQueryFragment fragment = new SupportQueryFragment();
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
        View view= inflater.inflate(R.layout.fragment_support_query, container, false);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        name_edt=view.findViewById(R.id.name_edt);
        number_edt=view.findViewById(R.id.number_edt);
        email_edt=view.findViewById(R.id.email_edt);
        remarks_edt=view.findViewById(R.id.remarks_edt);

        productTypeSpinner=view.findViewById(R.id.productTypeSpinner);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        view.findViewById(R.id.submit_tv).setOnClickListener(this);

        setCardTypeAdapter();

        return view;
    }

    private void setCardTypeAdapter() {
//bus
//flight
//account
        String[] title = { "DMT", "AEPS", "PAYOUT", "RAIL", "UTILITY", "BUS", "FLIGHT", "ACCOUNT"};
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
        productTypeSpinner.setAdapter(ad);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                if(validate()){
                    submitQuery();
                }
                break;
        }
    }

    private void submitQuery() {
        SupportQueryRequest request=new SupportQueryRequest();
        request.name= name_edt.getText().toString();
        request.mobileno= number_edt.getText().toString();
        request.emailid= email_edt.getText().toString();
        request.product= productTypeSpinner.getSelectedItem().toString();
        request.query= remarks_edt.getText().toString();

        String json = new Gson().toJson(request);

        new NetworkCall().callService(NetworkCall.getAccountStmtApiInterface().getSupportPost(ApiConstants.query, request),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response, int i) {
        try {
            PaytmWalletFragment.CommonResponse senderResponse = new Gson().fromJson(response.string(),
                    PaytmWalletFragment.CommonResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    resetData();
                }else if(senderResponse.getStatusMessage()!=null){
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"Error in submitting request, please try after some time.",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void resetData() {
        name_edt.setText("");
        number_edt.setText("");
        email_edt.setText("");
        remarks_edt.setText("");
    }

    private boolean validate() {
        if(!Common.isNameValid(name_edt.getText().toString())){
           Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show();
           return false;
        }else if(!Common.isMobileValid(number_edt.getText().toString())){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isEmailValid(email_edt.getText().toString())){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }else if(remarks_edt.getText().toString().trim().isEmpty()){
            Toast.makeText(context, "Please enter any query", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}