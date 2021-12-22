package com.justclick.clicknbook.Fragment.rblDmt;

import android.content.Context;
import android.content.Intent;
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
import com.justclick.clicknbook.Activity.MoneyTransferActivity;
import com.justclick.clicknbook.Activity.MoneyTransferNextActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.RblGetSenderRequest;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;


/**
 * Created by Lenovo on 03/28/2017.
 */

public class MoneyTransferHomeFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    TextView get_tv;
    EditText number_edt;
    private LoginModel loginModel;

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
        View view = inflater.inflate(R.layout.fragment_money_transfer_home, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        get_tv = (TextView) view.findViewById(R.id.get_tv);
        number_edt = (EditText) view.findViewById(R.id.number_edt);
        get_tv.setOnClickListener(this);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        get_tv.setTypeface(face);
    }

    private void getCustomerDetail() {
        if(validate())
        {
            RblGetSenderRequest rblGetSenderRequest=new RblGetSenderRequest();
            rblGetSenderRequest.RMobile =number_edt.getText().toString();
            rblGetSenderRequest.DoneCardUser=loginModel.Data.DoneCardUser;
            rblGetSenderRequest.DeviceId=Common.getDeviceId(context);
            rblGetSenderRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

            new NetworkCall().callRblLongTimeService(rblGetSenderRequest, ApiConstants.GetSenderDetails, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, 1);
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },true);
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            RblGetSenderResponse senderResponse = new Gson().fromJson(response.string(), RblGetSenderResponse.class);
            if(senderResponse!=null){
                if(senderResponse.status ==1) {
                    Intent i = new Intent(context, MoneyTransferNextActivity.class);
                    i.putExtra("SenderDetails",senderResponse);
                    startActivity(i);
                } else {
                    Bundle bundle=new Bundle();
                    bundle.putString("SenderNumber", number_edt.getText().toString());
                    MoneyTransferRegistrationFragment moneyTransferRegistrationFragment=new MoneyTransferRegistrationFragment();
                    moneyTransferRegistrationFragment.setArguments(bundle);
                    ((MoneyTransferActivity)context).replaceFragmentWithBackStack(moneyTransferRegistrationFragment);
                    ((MoneyTransferActivity)context).setRegistrationTab();
                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
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
            case R.id.get_tv:
                Common.hideSoftKeyboard((MoneyTransferActivity)context);
                Common.preventFrequentClick(get_tv);
                if(Common.checkInternetConnection(context)) {
                    getCustomerDetail();
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

