package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.AddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams;
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.JctVerifyAccountResponse;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RapipayAddBeneFragment extends Fragment implements View.OnClickListener {
    private final int AddRecipient=1,VerifyAccount=3;
    private Context context;
    private Activity activity;
    private FragmentBackPressListener backPress;
    private TextView submit_tv, verifyAccountTv;
    private EditText user_mobile_edt, ifscEdt,
            name_edt,account_no_edt,confirmAccountEdt;
    private TextInputLayout Address;
    private String bankName, Mobile;
    private DataBaseHelper dataBaseHelper;
    private LoginModel loginModel;
    private BankResponse ifscByCodeResponse;
    private ArrayList<BankResponse> bankArray;
    private CommonParams commonParams;
    AutoCompleteTextView atv_bank;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataBaseHelper=new DataBaseHelper(context);
        bankArray=new ArrayList<>();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

        if(getArguments()!=null && getArguments().getString("Mobile")!=null){
//            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
            Mobile=  getArguments().getString("Mobile");
            commonParams= (CommonParams) getArguments().getSerializable("commonParams");
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
        View view = inflater.inflate(R.layout.fragment_jct_money_add_recipient, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        submit_tv =  view.findViewById(R.id.submit_tv);
        verifyAccountTv = view.findViewById(R.id.verifyAccountTv);
        user_mobile_edt = view.findViewById(R.id.user_mobile_edt);
        ifscEdt =  view.findViewById(R.id.ifscEdt);
        Address =  view.findViewById(R.id.Address);
        Address.setHint("Bank IFSC");
        name_edt =  view.findViewById(R.id.name_edt);
        account_no_edt =  view.findViewById(R.id.account_no_edt);
        confirmAccountEdt =  view.findViewById(R.id.confirmAccountEdt);

        atv_bank =  view.findViewById(R.id.atv_bank);
        submit_tv.setOnClickListener(this);
        verifyAccountTv.setOnClickListener(this);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

        user_mobile_edt.setText(Mobile);
        user_mobile_edt.setEnabled(false);

        setText();

        if(dataBaseHelper.getJctBankNamesWithIFSC()!=null && dataBaseHelper.getJctBankNamesWithIFSC().size()>0) {
            String[] arr=new String[dataBaseHelper.getJctBankNamesWithIFSC().size()];
            for (int p=0; p<dataBaseHelper.getJctBankNamesWithIFSC().size(); p++){
                arr[p]=dataBaseHelper.getJctBankNamesWithIFSC().get(p).Name;
            }
            atv_bank.setAdapter(getSpinnerAdapter(arr));
        }else {
            getBankNames();
        }

        atv_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    bankName=atv_bank.getText().toString();
                    ifscEdt.setText(ifscByCodeResponse.getMASTER_IFSC_CODE());

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


        atv_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) atv_bank.showDropDown();
            }
        });

        atv_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atv_bank.showDropDown();
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
        ApiInterface apiService = APIClient.getClientRapipay().create(ApiInterface.class);
        Call<ArrayList<BankResponse>> call = apiService.getService(ApiConstants.BASE_URL_RAPIPAY+"api/payments/"+ApiConstants.GetBankName);
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

                        atv_bank.setAdapter(getSpinnerAdapter(arr));
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
        user_mobile_edt.setTypeface(face2);
        ifscEdt.setTypeface(face2);
        account_no_edt.setTypeface(face2);
        confirmAccountEdt.setTypeface(face2);

        name_edt.setTypeface(face2);
        submit_tv.setTypeface(face3);
        verifyAccountTv.setTypeface(face3);

    }

    private boolean validate() {
        if(atv_bank.getText().toString().trim().length()==0 ){
            Toast.makeText(context,R.string.empty_and_invalid_bank,Toast.LENGTH_SHORT).show();
            return false;
        }else if(account_no_edt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_account_number,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!confirmAccountEdt.getText().toString().trim().equals(account_no_edt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_account_confirmation,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(ifscEdt.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isNameValid(name_edt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_mobile_edt.getText().toString().length()<10){
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
            case R.id.submit_tv:
                Common.preventFrequentClick(submit_tv);
                if(Common.checkInternetConnection(context)){
                    addOrValidateBeneficiary(ApiConstants.AddBenificiary,AddRecipient);
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verifyAccountTv:
                Common.preventFrequentClick(verifyAccountTv);
                if(Common.checkInternetConnection(context)){
                    addOrValidateBeneficiary(ApiConstants.ValidateAccount, VerifyAccount);
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void addOrValidateBeneficiary(String method, final int TYPE) {
        if(validate())
        {
            AddBeneRequest requestModel=new AddBeneRequest();
            requestModel.setAgentCode(loginModel.Data.DoneCardUser);
            requestModel.setSessionKey(commonParams.getSessionKey());
            requestModel.setSessionRefId(commonParams.getSessionRefNo());
            requestModel.setBankName(atv_bank.getText().toString());
            requestModel.setAccountHolderName(name_edt.getText().toString());
            requestModel.setAccountNumber(account_no_edt.getText().toString());
            requestModel.setConfirmAccountNumber(confirmAccountEdt.getText().toString());
            requestModel.setIfscCode(ifscEdt.getText().toString());
            requestModel.setMobile(user_mobile_edt.getText().toString());
            //  https://remittance.justclicknpay.com/api/payments/AddBenificiary
//{"Mobile": "8468862808","SessionRefId": "V015838345","AccountNumber": "135301507755","ConfirmAccountNumber": "135301507755","IfscCode": "ICIC0000001",
//    "AccountHolderName": "apptest","BankName": "ICICI BANK LIMITED","Mode": "WEB","AgentCode": "JC0A13387","MerchantId": "JUSTCLICKTRAVELS","SessionKey": "DBS210103115407S725580372557"}
            new NetworkCall().callRapipayServiceHeader(requestModel, method, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, TYPE);
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, commonParams.getUserData(), commonParams.getToken());
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            if(TYPE==AddRecipient){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")){
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        backPress.onJctDetailBackPress(CodeEnum.Rapipay);
                        getParentFragmentManager().popBackStack();
                    }else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VerifyAccount){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                        verifyAccountTv.setText("Account Verified");
                        verifyAccountTv.setEnabled(false);
                        verifyAccountTv.setAlpha(0.6f);

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

    @Override
    public void onStop() {
        super.onStop();
//        if(isBackPress) {
//            backPress.onJctDetailBackPress();
//        }
    }
}