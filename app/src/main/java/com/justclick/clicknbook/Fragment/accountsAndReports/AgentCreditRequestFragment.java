package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditRequestRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class AgentCreditRequestFragment extends Fragment implements View.OnClickListener {
    private final int REQUEST_AMOUNT=1;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView agent_name, tv0,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,
            jctIdTv;
    private ImageView clearTv,cancel,radioButton1,radioButton2,radioButton3,radioButton4;
    private EditText amount_request_edt, agent_id, mobile_edt, remark_edt;
    private Button submit;
    private LoginModel loginModel;
    private Dialog pinDialog;
    private String Pin="";
    private AgentCreditRequestRequestModel model=null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel= MyPreferences.getLoginData(new LoginModel(),context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agent_credit_request, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agentCreditRequestFragmentTitle));

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        agent_name= (TextView) view.findViewById(R.id.agent_name);
        agent_id= (EditText) view.findViewById(R.id.agent_id);
        amount_request_edt= (EditText) view.findViewById(R.id.amount_request_edt);
        mobile_edt= (EditText) view.findViewById(R.id.mobile_edt);
        remark_edt= (EditText) view.findViewById(R.id.remark_edt);
        submit= (Button) view.findViewById(R.id.bt_submit);
        submit.setOnClickListener(this);
        mobile_edt.setText(loginModel.Data.Mobile);

        try {
            agent_name.setText(loginModel.Data.AgencyName.toUpperCase()+"( "+loginModel.Data.DoneCardUser+" )");
            agent_id.setText(loginModel.Data.Email);
        }catch (NullPointerException e){

        }
    }
    private boolean validate() {
        try {
            if((!Common.isdecimalvalid(amount_request_edt.getText().toString().trim()))||
                    Float.parseFloat(amount_request_edt.getText().toString().trim())==0){
                Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
                return false;
            }else if(Float.parseFloat(amount_request_edt.getText().toString().trim())>1000000) {
                Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                return false;
            }else if (mobile_edt.getText().toString().length()<10) {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
                return false;
            }else if (remark_edt.getText().toString().length()==0) {
                Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_submit:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                if(Common.checkInternetConnection(context)) {
                    if(validate()){
                        model=new AgentCreditRequestRequestModel();
                        model.CreditNeeded=amount_request_edt.getText().toString().trim();
                        model.DeviceId=Common.getDeviceId(context);
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.Mobile=mobile_edt.getText().toString().trim();
                        model.Remarks=remark_edt.getText().toString().trim();
                        if(Common.checkInternetConnection(context)) {
//                            openOtpDialog();
                            requestAmount(model);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.clearTv:
                if(Pin.length()<2){
                    Pin="";
                    checkRadionButton(Pin);
                }else {
                    Pin=Pin.substring(0,Pin.length()-1);
                    checkRadionButton(Pin);
                }
                break;
            case R.id.tv0:
                Pin=Pin+0;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv1:
                Pin=Pin+1;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv2:
                Pin=Pin+2;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv3:
                Pin=Pin+3;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv4:
                Pin=Pin+4;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv5:
                Pin=Pin+5;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv6:
                Pin=Pin+6;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv7:
                Pin=Pin+7;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv8:
                Pin=Pin+8;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv9:
                Pin=Pin+9;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
        }
    }

    private void checkRadionButton(String Pin) {
        if(Pin.length()==0) {
            radioButton1.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==1) {
            radioButton1.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==2){
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==3){
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));

        }else if(Pin.length()==4){
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
        }

    }

    private void checkOtp() {
        Common.hideSoftInputFromDialog(pinDialog,context);
        model.Pin=EncryptionDecryptionClass.EncryptSessionId(Pin, context);
        requestAmount(model);
        Pin="";
    }

    private void openOtpDialog() {
        pinDialog = new Dialog(context, R.style.Theme_Design_Light);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.pin_otp);
        final Window window= pinDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        jctIdTv= (TextView) pinDialog.findViewById(R.id.jctIdTv);
        tv0= (TextView) pinDialog.findViewById(R.id.tv0);
        tv1= (TextView) pinDialog.findViewById(R.id.tv1);
        tv2= (TextView) pinDialog.findViewById(R.id.tv2);
        tv3= (TextView) pinDialog.findViewById(R.id.tv3);
        tv4= (TextView) pinDialog.findViewById(R.id.tv4);
        tv5= (TextView) pinDialog.findViewById(R.id.tv5);
        tv6= (TextView) pinDialog.findViewById(R.id.tv6);
        tv7= (TextView) pinDialog.findViewById(R.id.tv7);
        tv8= (TextView) pinDialog.findViewById(R.id.tv8);
        tv9= (TextView) pinDialog.findViewById(R.id.tv9);
        clearTv= (ImageView) pinDialog.findViewById(R.id.clearTv);
        cancel= (ImageView) pinDialog.findViewById(R.id.cancel);
        radioButton1= (ImageView) pinDialog.findViewById(R.id.radioButton1);
        radioButton2= (ImageView) pinDialog.findViewById(R.id.radioButton2);
        radioButton3= (ImageView) pinDialog.findViewById(R.id.radioButton3);
        radioButton4= (ImageView) pinDialog.findViewById(R.id.radioButton4);

        jctIdTv.setText(loginModel.Data.DoneCardUser);
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);

        clearTv.setOnClickListener(this);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pin="";
                pinDialog.cancel();
            }
        });
        pinDialog.show();
    }


    private void requestAmount(AgentCreditRequestRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.AgentCreditRequest, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, REQUEST_AMOUNT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case REQUEST_AMOUNT:
                    CommonResponseModel requestModel = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    hideCustomDialog();
                    if(requestModel!=null){
//                        pinDialog.dismiss();
                        if(requestModel.StatusCode.equals("0")) {
                            Toast.makeText(context, requestModel.Data.Message, Toast.LENGTH_SHORT).show();
                            amount_request_edt.setText("");
                            remark_edt.setText("");
                            Common.showResponsePopUp(context, requestModel.Data.Message);
                        }else {
                            Toast.makeText(context, requestModel.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }


    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

}