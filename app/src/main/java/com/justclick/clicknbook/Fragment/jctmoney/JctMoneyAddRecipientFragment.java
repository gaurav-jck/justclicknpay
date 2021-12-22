package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.JctCreateAddRecipientResponse;
import com.justclick.clicknbook.model.JctIfscByCodeResponse;
import com.justclick.clicknbook.model.JctVerifyAccountResponse;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.JctMoneyAddRecipientRequestModel;
import com.justclick.clicknbook.requestmodels.JctMoneySenderRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 06/23/2018.
 */

public class JctMoneyAddRecipientFragment extends Fragment implements View.OnClickListener {
    private final int AddRecipient=1,VerifyAccount=3;
    private Context context;
    private Activity activity;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentBackPressListener backPress;
    private TextView submit_tv, verifyAccountTv;
    private EditText user_mobile_edt, ifscEdt,
            name_edt,account_no_edt;
    private TextInputLayout Address;
    private String bankName,bank_id,bank_Code, Mobile;
    private DataBaseHelper dataBaseHelper;
    private LoginModel loginModel;
    private JctIfscByCodeResponse ifscByCodeResponse;
    private ArrayList<JctIfscByCodeResponse> bankArray;
    private JctMoneySenderDetailFragment jctMoneySenderDetailFragment;
    private CharSequence charSequenceAct="";
    private boolean isBackPress=false;
    AutoCompleteTextView atv_bank;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataBaseHelper=new DataBaseHelper(context);
        bankArray=new ArrayList<>();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
            titleChangeListener= (ToolBarTitleChangeListener) context;
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
        if(getArguments()!=null && getArguments().getString("Mobile")!=null){
//            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
            Mobile=  getArguments().getString("Mobile");
//            jctMoneySenderDetailFragment= (JctMoneySenderDetailFragment) getArguments().getSerializable("Fragment");
        }
        return view;
    }

    private void initializeViews(View view) {
        isBackPress=false;
        submit_tv =  view.findViewById(R.id.submit_tv);
        verifyAccountTv = view.findViewById(R.id.verifyAccountTv);
        user_mobile_edt = view.findViewById(R.id.user_mobile_edt);
        ifscEdt =  view.findViewById(R.id.ifscEdt);
        Address =  view.findViewById(R.id.Address);
        Address.setHint("Bank IFSC");
        name_edt =  view.findViewById(R.id.name_edt);
        account_no_edt =  view.findViewById(R.id.account_no_edt);

        atv_bank =  view.findViewById(R.id.atv_bank);
        view.findViewById(R.id.dob_edt).setOnClickListener(this);
        submit_tv.setOnClickListener(this);
        verifyAccountTv.setOnClickListener(this);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

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
                    if (bankArray.get(i).Name.equals(selection)) {
                        pos = i;
                        break;
                    }
                }
//                if(pos>0){
                try{
                    ifscByCodeResponse=bankArray.get(pos);
                    bankName=atv_bank.getText().toString();
                    bank_id=bankArray.get(pos).bank_id;
                    bank_Code=bankArray.get(pos).bank_code;
                    if(bankArray.get(pos).AccountVF!=null && bankArray.get(pos).AccountVF.equalsIgnoreCase("yes")){
                        verifyAccountTv.setVisibility(View.VISIBLE);
                    }else {
                        verifyAccountTv.setVisibility(View.GONE);
                    }

                    if(ifscByCodeResponse!=null && (ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("1")||
                            ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("2")||
                            ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("3"))){
                        Address.setHint("Bank IFSC (Optional)");
                    }else {
                        Address.setHint("Bank IFSC (Required)");
                    }

                    try {
                        if(ifscByCodeResponse!=null){
                            if(ifscByCodeResponse.Key!=null && ifscByCodeResponse.Key.length()>1 && !ifscByCodeResponse.Key.equalsIgnoreCase("A")){
                                ifscEdt.setText(ifscByCodeResponse.Key);
                                ifscEdt.setText(ifscByCodeResponse.Key+account_no_edt.getText().toString().substring(0, Integer.parseInt(ifscByCodeResponse.Digit)));
                            }else {
                                ifscEdt.setText("");
                            }

                        }
                    }catch (NullPointerException e){

                    }catch (NumberFormatException e){

                    }

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

        account_no_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(ifscByCodeResponse!=null && account_no_edt.getText().toString().length()>=ifscByCodeResponse.Key.length()&&
//                        account_no_edt.getText().toString().substring(0,ifscByCodeResponse.Key.length()).equals(ifscByCodeResponse.Key) &&
//                        account_no_edt.getText().toString().length()<(Integer.parseInt(ifscByCodeResponse.Digit)+
//                                ifscByCodeResponse.Key.length())){
//                    ifscEdt.setText(ifscByCodeResponse.Key+charSequence);
//                }
                try {
                    if(ifscByCodeResponse!=null &&
                            (ifscEdt.getText().toString().length()<(Integer.parseInt(ifscByCodeResponse.Digit)+ifscByCodeResponse.Key.length()) ||
                                    account_no_edt.getText().toString().length()<Integer.parseInt(ifscByCodeResponse.Digit))){
                        ifscEdt.setText(ifscByCodeResponse.Key+charSequence);
                        charSequenceAct=charSequence;
                    }
                }catch (NullPointerException e){

                }catch (NumberFormatException e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        ApiInterface apiService = APIClient.getClientJctMoney().create(ApiInterface.class);
        Call<ArrayList<JctIfscByCodeResponse>> call = apiService.getJctBankNamesPost(ApiConstants.IFSCByCode, new Object());
        call.enqueue(new Callback<ArrayList<JctIfscByCodeResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<JctIfscByCodeResponse>>call, Response<ArrayList<JctIfscByCodeResponse>> response) {

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
                            arr[p]=response.body().get(p).Name;
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
            public void onFailure(Call<ArrayList<JctIfscByCodeResponse>> call, Throwable t) {
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

        name_edt.setTypeface(face2);
        submit_tv.setTypeface(face3);
        verifyAccountTv.setTypeface(face3);

    }

    private boolean validate() {
        if(atv_bank.getText().toString().trim().length()==0 ){
            Toast.makeText(context,R.string.empty_and_invalid_bank,Toast.LENGTH_SHORT).show();
            return false;
        }else if(account_no_edt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_account_id,Toast.LENGTH_SHORT).show();
            return false;
        }/*else if(!Common.isIFSCValid(ifscEdt.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }*/else if(!Common.isNameValid(name_edt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
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
                getFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                Common.preventFrequentClick(submit_tv);
                if(Common.checkInternetConnection(context)){
                    getCustomerDetail();
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verifyAccountTv:
                Common.preventFrequentClick(verifyAccountTv);
                if(Common.checkInternetConnection(context)){
                    if(account_no_edt.getText().toString().trim().length()<8){
                        Toast.makeText(context, R.string.empty_and_invalid_account_number, Toast.LENGTH_SHORT).show();
                    }/*else if(!Common.isIFSCValid(ifscEdt.getText().toString())){
                        Toast.makeText(context, R.string.empty_and_invalid_ifsc_code, Toast.LENGTH_SHORT).show();
                    }*/else if(!Common.isNameValid(name_edt.getText().toString().trim())){
                        Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show();
                    }/*else if(user_mobile_edt.getText().toString().trim().length()<10){
                        Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
                    }*/else {
                        verifyAccount();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void verifyAccount() {
        JctMoneySenderRequestModel jctMoneySenderRequestModel=new JctMoneySenderRequestModel();
        jctMoneySenderRequestModel.MobileNo =Mobile;
        jctMoneySenderRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        jctMoneySenderRequestModel.DeviceId=Common.getDeviceId(context);
        jctMoneySenderRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        jctMoneySenderRequestModel.AccNo=account_no_edt.getText().toString().trim();
//        jctMoneySenderRequestModel.ifsc=ifscEdt.getText().toString().trim();

        if(ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("1")||
                ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("2")||
                ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("3")){
            jctMoneySenderRequestModel.ifsc= ifscByCodeResponse.bank_code;
        }else {
            if(ifscEdt.getText().toString().trim().length()==0){
                Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
                return;
            }else {
                jctMoneySenderRequestModel.ifsc = ifscEdt.getText().toString().trim();
            }
        }
        new NetworkCall().callJctMoneyService(jctMoneySenderRequestModel, ApiConstants.VerifyAccount, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, VerifyAccount);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCustomerDetail( ) {
        if(validate())
        {
            JctMoneyAddRecipientRequestModel jctMoneyAddRecipientRequestModel=new JctMoneyAddRecipientRequestModel();
            jctMoneyAddRecipientRequestModel.recipient_name=name_edt.getText().toString();
            jctMoneyAddRecipientRequestModel.recipient_type="";
            jctMoneyAddRecipientRequestModel.DeviceId = Common.getDeviceId(context);
            jctMoneyAddRecipientRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            jctMoneyAddRecipientRequestModel.DoneCardUser =loginModel.Data.DoneCardUser;
            jctMoneyAddRecipientRequestModel.MobileNo = Mobile ;
            jctMoneyAddRecipientRequestModel.AccNo= account_no_edt.getText().toString();
//            jctMoneyAddRecipientRequestModel.IFSC= ifscEdt.getText().toString();
            jctMoneyAddRecipientRequestModel.bank_Code= bank_Code;
            jctMoneyAddRecipientRequestModel.bank_id= bank_id;
//            Toast.makeText(context,"registration called",Toast.LENGTH_SHORT).show();
            if(ifscByCodeResponse!=null && (ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("1")||
                    ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("2")||
                    ifscByCodeResponse.IFSC_Status.equalsIgnoreCase("3"))){
                jctMoneyAddRecipientRequestModel.IFSC= ifscByCodeResponse.bank_code;
            }else {
                if(ifscEdt.getText().toString().trim().length()==0){
                    Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    jctMoneyAddRecipientRequestModel.IFSC = ifscEdt.getText().toString().trim();
                }
            }

            new NetworkCall().callJctMoneyService(jctMoneyAddRecipientRequestModel,ApiConstants.AddRecipient,  context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, AddRecipient);
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            if(TYPE==AddRecipient){
                JctCreateAddRecipientResponse senderResponse = new Gson().fromJson(response.string(), JctCreateAddRecipientResponse.class);
                if(senderResponse!=null){
//                    ifscEdt.setText(senderResponse.ifsc);
                    if(senderResponse.statusCode == 1) {
                        verifyAccountTv.setVisibility(View.GONE);
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                        backPress.onJctDetailBackPress(CodeEnum.Rapipay);
                        getFragmentManager().popBackStack();
                    } else {
//                        verifyAccountTv.setVisibility(View.INVISIBLE);
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VerifyAccount){
                JctVerifyAccountResponse senderResponse = new Gson().fromJson(response.string(), JctVerifyAccountResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode ==1) {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_LONG).show();
                        verifyAccountTv.setText("Account Verified");
                        verifyAccountTv.setEnabled(false);
                        verifyAccountTv.setAlpha(0.6f);
                        name_edt.setText(senderResponse.data.recipient_name);
                        int pos = -1;

                        for(int i=0; i<bankArray.size(); i++){
                            if(senderResponse.data.bank.equalsIgnoreCase(bankArray.get(i).Name)){
                                atv_bank.setText(bankArray.get(i).Name);
                            }
                        }
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
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