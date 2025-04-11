package com.justclick.clicknbook.Fragment.rblDmt;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.justclick.clicknbook.Activity.MoneyTransferNextActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblAddressIFSCResponse;
import com.justclick.clicknbook.model.RblBankDetailByIFSCResponse;
import com.justclick.clicknbook.model.RblCommonResponse;
import com.justclick.clicknbook.model.RblCommonStateCityBranchResponse;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.model.RblIfscByCodeResponse;
import com.justclick.clicknbook.model.RblRelationResponse;
import com.justclick.clicknbook.model.RblTransactionResponse;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.RblAddressIFSCRequest;
import com.justclick.clicknbook.requestmodels.RblBenDeleteRequest;
import com.justclick.clicknbook.requestmodels.RblBenRegistrationRequest;
import com.justclick.clicknbook.requestmodels.RblCommonCityStateBranchRequest;
import com.justclick.clicknbook.requestmodels.RblGetBankDetailsByIFSCRequest;
import com.justclick.clicknbook.requestmodels.RblGetRelationRequest;
import com.justclick.clicknbook.requestmodels.RblGetSenderRequest;
import com.justclick.clicknbook.requestmodels.RblTransactionRequest;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by Lenovo on 03/28/2017.
 */

public class MoneyTransferNextHomeFragment extends Fragment implements View.OnClickListener {
    private final int BANK=0,STATE=1,CITY=2,BRANCH=3, BenReg=0, BenRegOtp=1, BenResendOtp=2, BenDel=3, Ben_Val=4;
    private final int BEN_REGISTRATION=0, BEN_DELETE_REQ =1, BEN_DELETE_OTP=2, GET_SENDER_DETAIL=3;
    final int TRANSACTION_LIMIT=500;
    private boolean canNEFT=true, canIMPS=true;
    private final String IMPS="IMPS", NEFT="NEFT";
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private ToolBarTitleChangeListener titleChangeListener;
    private int position=0;
    private LinearLayout linSenderMoreDetails, transactionLin;
    private RelativeLayout registeredBenfListRel;
    private Spinner beneficiarySpinner, relation_spinner, bank_spinner;
    private TextView sender_title_tv, remittance_tv,beneficiary_label,
            sender_tv,mobile_tv,pin_tv,city_name_tv,state_name_tv,month_tv,beneficiary_tv,kyc_tv,
            showHideTv, senderNameTv, senderMobileTv, pinCodeTv,
            cityNameTv, stateNameTv, monthLimitTv, beneficiaryLimitTv, KYCStatusTv,
            registeredBeneficiaryTv, newBeneficiaryTv, verifyAccountTv,
            searchIFSCTv, transactionTypeIMPSTv, transactionTypeNEFTTv,
            calculateCharge, payNowTv, registerNewBeneficiaryTv, benVerifyTv;
    private EditText accountEdt, bankNameEdt, beneficiaryNameEdt, IFSCCodeEdt,
            transferAmountEdt, remarkEdt, beneficiaryNumberEdt,NameAsPerBankEdt, otp_edt;
    private ImageView deleteBenImg;
    private TextInputLayout bankNameInput;
    private View relationView, bankView;
    private RblGetSenderResponse rblGetSenderResponse;
    private RblIfscByCodeResponse ifscByCodeResponse;
    private LoginModel loginModel;
    private RblCommonCityStateBranchRequest commonCityStateBranchRequest;
    private String relationKey="", bankName="", verifiedAccount="", TType=IMPS, TransactionResponse="";
    private static int noOfTransactionResponse=1;
    private Dialog paymentProgressDialog;
    private Thread thread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        commonCityStateBranchRequest=new RblCommonCityStateBranchRequest();
        dataBaseHelper=new DataBaseHelper(context);
        try{
            loginModel= MyPreferences.getLoginData(new LoginModel(),context);
            commonCityStateBranchRequest.DeviceId=Common.getDeviceId(context);
            commonCityStateBranchRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            commonCityStateBranchRequest.DoneCardUser=loginModel.Data.DoneCardUser;
        }catch (NullPointerException e){}

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
        View view = inflater.inflate(R.layout.fragment_money_transfer_next_home, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        showHideTv = (TextView) view.findViewById(R.id.showHideTv);
        linSenderMoreDetails = (LinearLayout) view.findViewById(R.id.linSenderMoreDetails);

        sender_title_tv = (TextView) view.findViewById(R.id.sender_title_tv);
        sender_tv = (TextView) view.findViewById(R.id.sender_tv);
        mobile_tv = (TextView) view.findViewById(R.id.mobile_tv);
        pin_tv = (TextView) view.findViewById(R.id.pin_tv);
        city_name_tv = (TextView) view.findViewById(R.id.city_name_tv);
        state_name_tv = (TextView) view.findViewById(R.id.state_name_tv);
        month_tv = (TextView) view.findViewById(R.id.month_tv);
        beneficiary_tv = (TextView) view.findViewById(R.id.beneficiary_tv);
        kyc_tv = (TextView) view.findViewById(R.id.kyc_tv);
        remittance_tv = (TextView) view.findViewById(R.id.remittance_tv);
        beneficiary_label = (TextView) view.findViewById(R.id.beneficiary_label);

        senderNameTv= (TextView) view.findViewById(R.id.senderNameTv);
        senderMobileTv= (TextView) view.findViewById(R.id.senderMobileTv);
        pinCodeTv= (TextView) view.findViewById(R.id.pinCodeTv);
        cityNameTv= (TextView) view.findViewById(R.id.cityNameTv);
        stateNameTv = (TextView) view.findViewById(R.id.stateNameTv);
        monthLimitTv= (TextView) view.findViewById(R.id.monthLimitTv);
        beneficiaryLimitTv= (TextView) view.findViewById(R.id.beneficiaryLimitTv);
        KYCStatusTv= (TextView) view.findViewById(R.id.KYCStatusTv);

        registeredBeneficiaryTv= (TextView) view.findViewById(R.id.registeredBeneficiaryTv);
        newBeneficiaryTv= (TextView) view.findViewById(R.id.newBeneficiaryTv);
        verifyAccountTv= (TextView) view.findViewById(R.id.verifyAccountTv);
        searchIFSCTv= (TextView) view.findViewById(R.id.searchIFSCTv);
        transactionTypeIMPSTv= (TextView) view.findViewById(R.id.transactionTypeIMPSTv);
        transactionTypeNEFTTv= (TextView) view.findViewById(R.id.transactionTypeNEFTTv);
        calculateCharge= (TextView) view.findViewById(R.id.calculateCharge);
        payNowTv= (TextView) view.findViewById(R.id.payNowTv);
        registerNewBeneficiaryTv= (TextView) view.findViewById(R.id.registerNewBeneficiaryTv);
        benVerifyTv= (TextView) view.findViewById(R.id.benVerifyTv);

        relationView= view.findViewById(R.id.relationView);
        bankView= view.findViewById(R.id.bankView);
        accountEdt= (EditText) view.findViewById(R.id.accountEdt);
        bankNameEdt= (EditText) view.findViewById(R.id.bankNameEdt);
        NameAsPerBankEdt= (EditText) view.findViewById(R.id.NameAsPerBankEdt);
        bankNameInput= (TextInputLayout) view.findViewById(R.id.bankNameInput);
        beneficiaryNameEdt= (EditText) view.findViewById(R.id.beneficiaryNameEdt);

        IFSCCodeEdt= (EditText) view.findViewById(R.id.IFSCCodeEdt);
        transferAmountEdt= (EditText) view.findViewById(R.id.transferAmountEdt);
        remarkEdt= (EditText) view.findViewById(R.id.remarkEdt);
        beneficiaryNumberEdt= (EditText) view.findViewById(R.id.beneficiaryNumberEdt);

        beneficiarySpinner= (Spinner) view.findViewById(R.id.beneficiarySpinner);
        relation_spinner= (Spinner) view.findViewById(R.id.relation_spinner);
        bank_spinner= (Spinner) view.findViewById(R.id.bank_spinner);
        deleteBenImg = (ImageView) view.findViewById(R.id.deleteBenImg);

        transactionLin = (LinearLayout) view.findViewById(R.id.transactionLin);
        registeredBenfListRel = (RelativeLayout) view.findViewById(R.id.registeredBenfListRel);

        setfont();

        if(dataBaseHelper.getAllRblRelationNames().size()==0) {
            getRelation();
        }else {
            String[] arr=new String[dataBaseHelper.getAllRblRelationNames().size()];
            for (int p=0; p<dataBaseHelper.getAllRblRelationNames().size(); p++){
                arr[p]=dataBaseHelper.getAllRblRelationNames().get(p).Name;
            }
            relation_spinner.setAdapter(getSpinnerAdapter(arr));
        }


        if(dataBaseHelper.getRblBankNamesWithIFSC().size()==0) {
            getBankNames();
        }else {
            String[] arr=new String[dataBaseHelper.getRblBankNamesWithIFSC().size()];
            for (int p=0; p<dataBaseHelper.getRblBankNamesWithIFSC().size(); p++){
                arr[p]=dataBaseHelper.getRblBankNamesWithIFSC().get(p).Name;
            }
            bank_spinner.setAdapter(getSpinnerAdapter(arr));
        }

        linSenderMoreDetails.setVisibility(View.GONE);
        showHideTv.setOnClickListener(this);
        registeredBeneficiaryTv.setOnClickListener(this);
        newBeneficiaryTv.setOnClickListener(this);
        verifyAccountTv.setOnClickListener(this);
        searchIFSCTv.setOnClickListener(this);
        transactionTypeIMPSTv.setOnClickListener(this);
        transactionTypeNEFTTv.setOnClickListener(this);
        calculateCharge.setOnClickListener(this);
        payNowTv.setOnClickListener(this);
        registerNewBeneficiaryTv.setOnClickListener(this);
        deleteBenImg.setOnClickListener(this);

        try
        {
            rblGetSenderResponse= (RblGetSenderResponse)getArguments().getSerializable("SenderDetails");
            senderNameTv.setText(rblGetSenderResponse.RBLRemitterDetails.SName);
            senderMobileTv.setText(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
            pinCodeTv.setText(rblGetSenderResponse.RBLRemitterDetails.PinCode);
            cityNameTv.setText(rblGetSenderResponse.RBLRemitterDetails.City);
            stateNameTv.setText(rblGetSenderResponse.RBLRemitterDetails.State);
            monthLimitTv.setText(rblGetSenderResponse.RBLRemitterDetails.ALimit);
            beneficiaryLimitTv.setText(rblGetSenderResponse.RBLRemitterDetails.ALimit);
            KYCStatusTv.setText(/*rblGetSenderResponse.RBLRemitterDetails.ALimit*/"");

            String[] arr=new String[rblGetSenderResponse.BenDetails.BenDetail.size()];
            for(int i=0;i<rblGetSenderResponse.BenDetails.BenDetail.size();i++){
                arr[i]=rblGetSenderResponse.BenDetails.BenDetail.get(i).Name;
            }

            if(rblGetSenderResponse.BenDetails.BenDetail.size()>0){
                if(rblGetSenderResponse.BenDetails.BenDetail.get(0).IMPSFlag==0){
                    canIMPS=false;
                }
                if(rblGetSenderResponse.BenDetails.BenDetail.get(0).NEFTFlag==0){
                    canNEFT=false;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.mobile_operator_spinner_item,R.id.operator_tv, arr);
            adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
            beneficiarySpinner.setAdapter(adapter);

            //show default beneficiary if available
            if(rblGetSenderResponse.BenDetails.BenDetail.size()>0){
                registerBeneficiaryClick();
            }else {
                newBeneficiaryClick();
            }

        }
        catch (Exception e){

        }

        accountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(ifscByCodeResponse!=null && IFSCCodeEdt.getText().toString().length()>=ifscByCodeResponse.Key.length()&&
                        IFSCCodeEdt.getText().toString().substring(0,ifscByCodeResponse.Key.length()).equals(ifscByCodeResponse.Key) &&
                        IFSCCodeEdt.getText().toString().length()<(Integer.parseInt(ifscByCodeResponse.Digit)+
                                ifscByCodeResponse.Key.length())){
                    IFSCCodeEdt.setText(ifscByCodeResponse.Key+charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        beneficiarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                position=pos;
                if(registeredBeneficiaryTv.isSelected()) {
                    if (rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")) {
                        verifyAccountTv.setText(R.string.rbl_ben_verify_text);
                    } else {
                        verifyAccountTv.setText(R.string.rbl_ben_non_verify_text);
                    }
                }else {
                    verifyAccountTv.setText(R.string.rbl_ben_non_verify_text);
                }
                registerBeneficiaryClick();
//                Toast.makeText(context,"position "+pos,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        relation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos>0){
                    relationKey=dataBaseHelper.getAllRblRelationNames().get(pos).Key;
                }else {
                    relationKey="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bank_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos>0){
                    try{
                        bankName=bank_spinner.getSelectedItem().toString();
                        int digit= Integer.parseInt(dataBaseHelper.getRblBankNamesWithIFSC().get(pos).Digit);
                        ifscByCodeResponse=dataBaseHelper.getRblBankNamesWithIFSC().get(pos);
                        if(accountEdt.getText().length()>digit){
                            IFSCCodeEdt.setText(dataBaseHelper.getRblBankNamesWithIFSC().get(pos).Key+
                                    accountEdt.getText().toString().substring(0,digit));
                        }else {
                            IFSCCodeEdt.setText(dataBaseHelper.getRblBankNamesWithIFSC().get(pos).Key+
                                    accountEdt.getText().toString());
                        }
                    }catch (Exception e){
                        IFSCCodeEdt.setText("");
                        ifscByCodeResponse=null;
                    }
                }else {
                    bankName="";
                    IFSCCodeEdt.setText("");
                    ifscByCodeResponse=null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getBankNames() {
        if(!MyCustomDialog.isDialogShowing()){
        showCustomDialog();}
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<ArrayList<RblIfscByCodeResponse>> call = apiService.getRblBankNamesPost(ApiConstants.IFSCByCode);
        call.enqueue(new Callback<ArrayList<RblIfscByCodeResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RblIfscByCodeResponse>>call, Response<ArrayList<RblIfscByCodeResponse>> response) {

                try{
                    if(response.body()!=null && response.body().size()>0){
                        hideCustomDialog();
                        RblIfscByCodeResponse relationResponse=new RblIfscByCodeResponse();
                        relationResponse.Key="";
                        relationResponse.Name="select-bank";
                        relationResponse.Digit="A";
                        String[] arr=new String[response.body().size()+1];
                        arr[0]=relationResponse.Name;
                        ArrayList<RblIfscByCodeResponse> arrayList=new ArrayList<>();
                        arrayList.add(relationResponse);
                        arrayList.addAll(response.body());
                        dataBaseHelper.insertRblBankNamesWithIFSC(arrayList);
                        for (int p=0;p<response.body().size();p++){
                            arr[p+1]=response.body().get(p).Name;
                        }
                        bank_spinner.setAdapter(getSpinnerAdapter(arr));
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
            public void onFailure(Call<ArrayList<RblIfscByCodeResponse>>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRelation() {
        RblGetRelationRequest relationRequest=new RblGetRelationRequest();
        relationRequest.DeviceId=Common.getDeviceId(context);
        relationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        relationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
        relationRequest.RID="";

        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<ArrayList<RblRelationResponse>> call = apiService.getRblRelationPost(ApiConstants.RelationID,relationRequest);
        call.enqueue(new Callback<ArrayList<RblRelationResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RblRelationResponse>>call, Response<ArrayList<RblRelationResponse>> response) {

                try{
                    if(response.body()!=null && response.body().size()>0){
//                        hideCustomDialog();
                        RblRelationResponse relationResponse=new RblRelationResponse();
                        relationResponse.Key="";
                        relationResponse.Name="select-relation";
                        String[] arr=new String[response.body().size()+1];
                        arr[0]=relationResponse.Name;
                        ArrayList<RblRelationResponse> arrayList=new ArrayList<RblRelationResponse>();
                        arrayList.add(relationResponse);
                        arrayList.addAll(response.body());
                        dataBaseHelper.insertRblRelationNames(arrayList);
                        for (int p=0;p<response.body().size();p++){
                            arr[p+1]=response.body().get(p).Name;
                        }
                        relation_spinner.setAdapter(getSpinnerAdapter(arr));
                    }
                    else
                    {
//                        hideCustomDialog();
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
//                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<RblRelationResponse>>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showHideTv:
                if(linSenderMoreDetails.getVisibility()==View.VISIBLE){
                    linSenderMoreDetails.setVisibility(View.GONE);
                    showHideTv.setText("Show Details");
                }else {
                    linSenderMoreDetails.setVisibility(View.VISIBLE);
                    showHideTv.setText("Hide Details");
                }
              break;

            case R.id.registeredBeneficiaryTv:
                try {
                    if(rblGetSenderResponse.BenDetails.BenDetail !=null &&
                            rblGetSenderResponse.BenDetails.BenDetail.size()>0){
                        registerBeneficiaryClick();
                    }else {
                        Toast.makeText(context, "No Beneficiary Available", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }
                break;

            case R.id.newBeneficiaryTv:
                newBeneficiaryClick();
                break;
            case R.id.deleteBenImg:
                Common.hideSoftKeyboard((MoneyTransferNextActivity)context);
                if(Common.checkInternetConnection(context)) {
                    RblBenDeleteRequest rblBenDeleteRequest=new RblBenDeleteRequest();
                    rblBenDeleteRequest.DeviceId =Common.getDeviceId(context);
                    rblBenDeleteRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    rblBenDeleteRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                    rblBenDeleteRequest.AccNo=rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo;
                    rblBenDeleteRequest.RMobile=rblGetSenderResponse.RBLRemitterDetails.MobileNo;
                    deleteBeneficiaryClick(rblBenDeleteRequest, null);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.verifyAccountTv:
                Common.hideSoftKeyboard((MoneyTransferNextActivity)context);
                if(Common.checkInternetConnection(context)) {
                    if(registeredBeneficiaryTv.isSelected() &&
                            rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show();
                    }else if(registeredBeneficiaryTv.isSelected() &&
                            !rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")){
                        if (validateVerifyAccount(rblGetSenderResponse.BenDetails.BenDetail.get(position).Bank,1)) {
                            RblBenRegistrationRequest validateRequest = new RblBenRegistrationRequest();
                            validateRequest.AccNo = accountEdt.getText().toString().trim();
                            validateRequest.BMobile = Long.parseLong("0"/*beneficiaryNumberEdt.getText().toString().trim()*/);
                            validateRequest.RMobile = Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            validateRequest.bank = rblGetSenderResponse.BenDetails.BenDetail.get(position).Bank;
                            validateRequest.DeviceId = Common.getDeviceId(context);
                            validateRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            validateRequest.DoneCardUser = loginModel.Data.DoneCardUser;
                            validateRequest.ifscode = IFSCCodeEdt.getText().toString().trim();
                            validateRequest.name = beneficiaryNameEdt.getText().toString().trim();
                            new VerifyAccountTask(validateRequest).execute();
//                            benValidate(validateRequest);
//                    Toast.makeText(context, "Verify", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if (validateVerifyAccount(bankName,0)) {
                            RblBenRegistrationRequest validateRequest = new RblBenRegistrationRequest();
                            validateRequest.AccNo = accountEdt.getText().toString().trim();
                            validateRequest.BMobile = Long.parseLong("0"/*beneficiaryNumberEdt.getText().toString().trim()*/);
                            validateRequest.RMobile = Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            validateRequest.bank = bankName;
                            validateRequest.DeviceId = Common.getDeviceId(context);
                            validateRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            validateRequest.DoneCardUser = loginModel.Data.DoneCardUser;
                            validateRequest.ifscode = IFSCCodeEdt.getText().toString().trim();
                            validateRequest.name = beneficiaryNameEdt.getText().toString().trim();
                            new VerifyAccountTask(validateRequest).execute();
//                            benValidate(validateRequest);

//                    Toast.makeText(context, "Verify", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.searchIFSCTv:
                Common.hideSoftKeyboard((MoneyTransferNextActivity)context);
                if(Common.checkInternetConnection(context)) {
                    try{
                        openSearchIFSCDialog("Bank Details","Select Verification Type","CANCEL","OK");
                    }catch (Exception e){
                        Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.transactionTypeIMPSTv:
                if(canIMPS) {
                    TType = IMPS;
                    transactionTypeIMPSTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                    transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.color_white));

                    transactionTypeNEFTTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                    transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
                }else {
                    Toast.makeText(context, "IMPS transaction can not be done at this moment", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.transactionTypeNEFTTv:
                if(canNEFT){
                    TType=NEFT;
                    transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                    transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

                    transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                    transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
                }else {
                    Toast.makeText(context, "NEFT transaction can not be done at this moment", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.calculateCharge:
                Toast.makeText(context, "Calculate charge", Toast.LENGTH_SHORT).show();
                break;

            case R.id.payNowTv:
                Common.hideSoftKeyboard((MoneyTransferNextActivity)context);
                try {
                    if(Common.checkInternetConnection(context)) {
                        if(rblGetSenderResponse.BenDetails.BenDetail.get(position).Ben_flag.equalsIgnoreCase("true")){
                            if(validatePayment()){
                                RblTransactionRequest transactionRequest=new RblTransactionRequest();
                                transactionRequest.AccNo=accountEdt.getText().toString();
                                transactionRequest.DeviceId=Common.getDeviceId(context);
                                transactionRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                                transactionRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                                transactionRequest.RMobile=rblGetSenderResponse.RBLRemitterDetails.MobileNo;
                                transactionRequest.TType=TType;
                                transactionRequest.amount= Integer.parseInt(transferAmountEdt.getText().toString());
                                transactionRequest.remarks= "test";
                                try {
                                    TransactionResponse="";
                                    noOfTransactionResponse=1;
                                    openTransactionPopUp(transactionRequest);
                                }catch (Exception e){
                                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                                }

//                    paymentService(transactionRequest);
//                    Toast.makeText(context, "Pay now", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            openOtpDialog(Ben_Val);
                            RblBenRegistrationRequest rblBenRegistrationRequest=new RblBenRegistrationRequest();
                            rblBenRegistrationRequest.DeviceId =Common.getDeviceId(context);
                            rblBenRegistrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            rblBenRegistrationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                            rblBenRegistrationRequest.AccNo =rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo;
                            rblBenRegistrationRequest.RMobile= Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            if(rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")){
                                rblBenRegistrationRequest.Val_Flag = 1;}
                            else {
                                rblBenRegistrationRequest.Val_Flag = 0;}
                            rblBenRegistrationRequest.rid= 0;
                            benRegistration(rblBenRegistrationRequest, BenResendOtp, ApiConstants.BenResendOTP, null);
                        }

                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.registerNewBeneficiaryTv:
                Common.hideSoftKeyboard((MoneyTransferNextActivity)context);
                if(Common.checkInternetConnection(context)) {
                    try {
                        if(validateRegistration()){
                            RblBenRegistrationRequest registrationRequest=new RblBenRegistrationRequest();
                            registrationRequest.AccNo =accountEdt.getText().toString().trim();
                            registrationRequest.BMobile = Long.parseLong("0");
                            registrationRequest.RMobile= Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            registrationRequest.bank=bankName;
                            registrationRequest.DeviceId =Common.getDeviceId(context);
                            registrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            registrationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                            registrationRequest.ifscode= IFSCCodeEdt.getText().toString().trim();
                            registrationRequest.name= beneficiaryNameEdt.getText().toString().trim();
                            if(registrationRequest.AccNo.equals(verifiedAccount)){
                                registrationRequest.Val_Flag = 1;}
                            else {
                                registrationRequest.Val_Flag = 0;}
                            registrationRequest.rid= Long.parseLong(relationKey);
                            benRegistration(registrationRequest, BenReg, ApiConstants.BenReg, null);
//                    Toast.makeText(context, "Register Beneficiary" , Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void openTransactionPopUp(final RblTransactionRequest transactionRequest) {
//        final float serviceCharge= Float.parseFloat(rblGetSenderResponse.Scharge);
        final Dialog transactionDialog = new Dialog(context);
        transactionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionDialog.setContentView(R.layout.transaction_popup_dialog_layout);
        final Window window= transactionDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView submit_tv= (TextView) transactionDialog.findViewById(R.id.submit_tv);
        final TextView cancel_tv= (TextView) transactionDialog.findViewById(R.id.cancel_tv);
        TextView customerAmountTv= (TextView) transactionDialog.findViewById(R.id.customerAmountTv);
        TextView accountNoTv= (TextView) transactionDialog.findViewById(R.id.accountNoTv);
        TextView ifscCodeTv= (TextView) transactionDialog.findViewById(R.id.ifscCodeTv);
        final TextView pin_edt= (TextView) transactionDialog.findViewById(R.id.pin_edt);

        /*float serviceChargeAmount=((float)transactionRequest.amount * serviceCharge)/100;
        float totalAmount=((float)transactionRequest.amount)+serviceChargeAmount;*/
        customerAmountTv.setText(transactionRequest.amount+"");
        accountNoTv.setText(transactionRequest.AccNo+"");
        ifscCodeTv.setText(IFSCCodeEdt.getText().toString().trim()+"");

        submit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.checkInternetConnection(context)){
                    Common.preventFrequentClick(submit_tv);
                    try {
                        if(pin_edt.getText().toString().trim().length()<4){
                            Toast.makeText(context, R.string.empty_and_invalid_pin, Toast.LENGTH_SHORT).show();
                        }else {
                            transactionRequest.TPin = Long.parseLong(pin_edt.getText().toString().trim());
                            transactionDialog.dismiss();
                            InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(transactionDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                            final int totalAmount=transactionRequest.amount;
                            int noOfTrans=totalAmount%TRANSACTION_LIMIT>0?
                                    totalAmount/TRANSACTION_LIMIT+1:
                                    totalAmount/TRANSACTION_LIMIT;
                            if(Common.checkInternetConnection(context)){
                                if(noOfTrans>1){
                                    transactionRequest.amount=TRANSACTION_LIMIT;
//                                paymentService(transactionRequest, totalAmount, noOfTrans, TRANSACTION_LIMIT,1);
                                    new AmountTransactionTask(transactionRequest, totalAmount, noOfTrans, TRANSACTION_LIMIT,1).execute();
                                }else {
//                                paymentService(transactionRequest, totalAmount, noOfTrans, TRANSACTION_LIMIT, 1);
                                    new AmountTransactionTask(transactionRequest, totalAmount, noOfTrans, TRANSACTION_LIMIT,1).execute();
                                }
                            }else {
                                Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                            }

                        }

                    }catch (NullPointerException e){

                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionDialog.dismiss();
            }
        });

        transactionDialog.show();
    }

    private class AmountTransactionTask extends AsyncTask<Object, Void, String> {
        RblTransactionRequest transactionRequest;
        int totalAmount, noOfTrans, transaction_limit, serialNo;
        TextView transactionTv, amountTv;
        ProgressBar progressBar, progressBar11;
        public AmountTransactionTask(RblTransactionRequest transactionRequest, int totalAmount,
                                     int noOfTrans, int transaction_limit, int serialNo) {
            this.transactionRequest=transactionRequest;
            this.totalAmount=totalAmount;
            this.noOfTrans=noOfTrans;
            this.transaction_limit=transaction_limit;
            this.serialNo=serialNo;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPreExecute() {

            if(paymentProgressDialog==null || !paymentProgressDialog.isShowing()){
                showPaymentCustomDialog();
            }

            switch (serialNo){
                case 1:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv1);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv1);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar1);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar11);
                    break;
                case 2:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv2);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv2);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar2);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar22);
                    transactionTv.setVisibility(View.VISIBLE);
                    amountTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv3);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv3);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar3);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar33);
                    transactionTv.setVisibility(View.VISIBLE);
                    amountTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv4);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv4);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar4);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar44);
                    transactionTv.setVisibility(View.VISIBLE);
                    amountTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv5);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv5);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar5);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar55);
                    transactionTv.setVisibility(View.VISIBLE);
                    amountTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    transactionTv=(TextView)paymentProgressDialog.findViewById(R.id.transactionTv1);
                    amountTv=(TextView)paymentProgressDialog.findViewById(R.id.amountTv1);
                    progressBar=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar1);
                    progressBar11=(ProgressBar) paymentProgressDialog.findViewById(R.id.progressBar11);
                    transactionTv.setVisibility(View.VISIBLE);
                    amountTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
            }

            transactionTv.setText("Transaction "+serialNo+":-");
            amountTv.setText("Amount :- "+ transactionRequest.amount+"");
            showProgress(progressBar);
        }

        @Override
        protected String doInBackground(Object... objects) {
            return NetworkCall.getDataFromServer(ApiConstants.RblPage,ApiConstants.BenT,transactionRequest);
        }

        protected void onPostExecute(String result) {
            try{
                if(result!=null){
                    RblTransactionResponse response=new GsonBuilder().create().fromJson(result,RblTransactionResponse.class);
                    if(response!=null){
                        /*Common.showResponsePopUp(context,"Bank ref no. - "+response.body().bankrefno+"\n"+
                                "Channel partner ref no. - "+response.body().channelpartnerrefno+"\n"+
                                "Description - "+response.body().description);*/
                        TransactionResponse=TransactionResponse+"Transaction "+noOfTransactionResponse+" :-\n"+
                                "Bank ref no. - "+response.bankrefno+"\n"+
                                "Channel partner ref no. - "+response.channelpartnerrefno+"\n"+
                                "Description - "+response.description+"\n\n";
                        if(response.status==1) {
                            noOfTransactionResponse++;
                            progressBar.setVisibility(View.GONE);
                            progressBar11.setVisibility(View.VISIBLE);
                            if(noOfTrans>1){

                                if(noOfTrans==2 && totalAmount%TRANSACTION_LIMIT>0){
                                    int tranAmt=totalAmount%TRANSACTION_LIMIT;
                                    transactionRequest.amount=tranAmt;
//                                paymentService(transactionRequest, totalAmount, noOfTrans-1, tranAmt, serialNo+1);
                                    new AmountTransactionTask(transactionRequest, totalAmount, noOfTrans-1, tranAmt, serialNo+1).execute();
                                }else {
//                                paymentService(transactionRequest, totalAmount, noOfTrans-1, TRANSACTION_LIMIT, serialNo+1);
                                    new AmountTransactionTask(transactionRequest, totalAmount, noOfTrans-1, TRANSACTION_LIMIT, serialNo+1).execute();
                                }
                            }else {
                                hideCustomDialog();
                                paymentProgressDialog.dismiss();
                                Common.showResponsePopUp(context,TransactionResponse);
                            }
                            Toast.makeText(context,response.description, Toast.LENGTH_LONG).show();
                        }else {
                            hideCustomDialog();
                            paymentProgressDialog.dismiss();
                            Common.showResponsePopUp(context,TransactionResponse);
                            Toast.makeText(context,response.description, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        paymentProgressDialog.dismiss();
                        if(TransactionResponse.length()>0) {
                            Common.showResponsePopUp(context, TransactionResponse);
                        }
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                }else {
                    hideCustomDialog();
                    paymentProgressDialog.dismiss();
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                paymentProgressDialog.dismiss();
                if(TransactionResponse.length()>0) {
                    Common.showResponsePopUp(context, TransactionResponse);
                }
                hideCustomDialog();
                Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
            }
        }
    }

    private class VerifyAccountTask extends AsyncTask<Object, Void, String> {
        RblBenRegistrationRequest request;

        public VerifyAccountTask(RblBenRegistrationRequest transactionRequest) {
            this.request=transactionRequest;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPreExecute() {
            showCustomDialog();
        }

        @Override
        protected String doInBackground(Object... objects) {
            return NetworkCall.getDataFromServer(ApiConstants.RblPage,ApiConstants.BenVaild,request);
        }

        protected void onPostExecute(String result) {
            try{
                if(result!=null){
                    RblCommonResponse response=new GsonBuilder().create().fromJson(result,RblCommonResponse.class);
                    hideCustomDialog();
                    if(response!=null) {
                        if(response.status==1){
                            verifyAccountTv.setText(R.string.rbl_ben_verify_text);
                            if(registeredBeneficiaryTv.isSelected()){
                                rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag ="true";
                            }
                            verifiedAccount=request.AccNo;
                            Toast.makeText(context,response.description, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context,response.description, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                hideCustomDialog();
                Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgress(final ProgressBar progressBar) {
        final Handler progressBarHandler = new Handler();
        final int[] progressBarStatus = {0};
        progressBar.setProgress(0);
        Runnable runnable=new Runnable() {
            public void run() {
                while (/*progressBarStatus[0] < 100 && */progressBar.getProgress()<100) {

                    // process some tasks
                    progressBarStatus[0]++;

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus[0]);
                        }
                    });
                }

               /* // ok, file is downloaded,
                if (progressBarStatus[0] >= 100) {

                    // sleep 2 seconds, so that you can see the 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        };
        thread=new Thread(runnable);
        thread.start();

//        thread.interrupt();
//        thread.stop();

    }

    private void showPaymentCustomDialog() {
        paymentProgressDialog=new Dialog(context);
        paymentProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentProgressDialog.setContentView(R.layout.rbl_transaction_progress_dialog);
        paymentProgressDialog.setCancelable(false);
        final Window window= paymentProgressDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        paymentProgressDialog.findViewById(R.id.transactionTv2).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.amountTv2).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.progressBar2).setVisibility(View.GONE);

        paymentProgressDialog.findViewById(R.id.transactionTv3).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.amountTv3).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.progressBar3).setVisibility(View.GONE);

        paymentProgressDialog.findViewById(R.id.transactionTv4).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.amountTv4).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.progressBar4).setVisibility(View.GONE);

        paymentProgressDialog.findViewById(R.id.transactionTv5).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.amountTv5).setVisibility(View.GONE);
        paymentProgressDialog.findViewById(R.id.progressBar5).setVisibility(View.GONE);


        paymentProgressDialog.show();
    }

    private void benRegistration(RblBenRegistrationRequest request, final int type, String method, final Dialog otpDialog) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblCommonResponse> call = apiService.rblBenRegistrationPost(method,request);
        call.enqueue(new Callback<RblCommonResponse>() {
            @Override
            public void onResponse(Call<RblCommonResponse>call, Response<RblCommonResponse> response) {
                try{
                    hideCustomDialog();
                    if(response!=null) {
                        if(response.body().status==1 && type==BenReg)
                        {
                            openOtpDialog(BenReg);
                            Toast.makeText(context, response.body().description, Toast.LENGTH_LONG).show();
                        }else if(response.body().status==1 && type==BenRegOtp){
                            getCustomerDetail();
                            Toast.makeText(context, response.body().description, Toast.LENGTH_LONG).show();
                            if(otpDialog!=null){
                                otpDialog.dismiss();}
                        }else if(response.body().status==1 && type==BenResendOtp){
                            Toast.makeText(context, response.body().description, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, response.body().description, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RblCommonResponse>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCustomerDetail() {

        RblGetSenderRequest rblGetSenderRequest=new RblGetSenderRequest();
        rblGetSenderRequest.RMobile =senderMobileTv.getText().toString();
        rblGetSenderRequest.DoneCardUser=loginModel.Data.DoneCardUser;
        rblGetSenderRequest.DeviceId=Common.getDeviceId(context);
        rblGetSenderRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

        if(Common.checkInternetConnection(context)){
            callGetDetail(rblGetSenderRequest);
        }else{
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }

    }

    private void callGetDetail(RblGetSenderRequest rblGetSenderRequest) {
        new NetworkCall().callRblLongTimeService(rblGetSenderRequest, ApiConstants.GetSenderDetails, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, GET_SENDER_DETAIL, null);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },true);
    }


    private void deleteBeneficiaryClick(RblBenDeleteRequest request, final Dialog otpDialog) {
        if(Common.checkInternetConnection(context)){
            new NetworkCall().callRblLongTimeService(request, ApiConstants.BenDeleteReq, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, BEN_DELETE_REQ, otpDialog);
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },true);
        }else{
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void openOtpDialog(final int type) {
        final Dialog otpDialog = new Dialog(context);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.send_otp_layout);
        otpDialog.setCancelable(false);
        otp_edt= (EditText) otpDialog.findViewById(R.id.otp_edt);
        final Button submit= (Button) otpDialog.findViewById(R.id.submit_btn);
        final TextView resendTv= (TextView) otpDialog.findViewById(R.id.resendOtpTv);
        ImageButton dialogCloseButton = (ImageButton) otpDialog.findViewById(R.id.close_btn);
        resendTv.setVisibility(View.VISIBLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(otpDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (NullPointerException e){

                }

                if(Common.checkInternetConnection(context)){
                    if(otp_edt.getText().toString().trim().length()>3) {
                        if(type==BenDel) {
                            RblBenDeleteRequest rblBenDeleteRequest = new RblBenDeleteRequest();
                            rblBenDeleteRequest.DeviceId = Common.getDeviceId(context);
                            rblBenDeleteRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            rblBenDeleteRequest.DoneCardUser = loginModel.Data.DoneCardUser;
                            rblBenDeleteRequest.AccNo = rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo;
                            rblBenDeleteRequest.RMobile = rblGetSenderResponse.RBLRemitterDetails.MobileNo;
                            rblBenDeleteRequest.OTP = Long.parseLong(otp_edt.getText().toString().trim());
                            deleteBenOtp(rblBenDeleteRequest, otpDialog);
                        }else if(type==Ben_Val){
                            RblBenRegistrationRequest rblBenRegistrationRequest=new RblBenRegistrationRequest();
                            rblBenRegistrationRequest.DeviceId =Common.getDeviceId(context);
                            rblBenRegistrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            rblBenRegistrationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                            rblBenRegistrationRequest.OTP= Long.parseLong(otp_edt.getText().toString().trim());
                            rblBenRegistrationRequest.AccNo =rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo;
                            rblBenRegistrationRequest.RMobile= Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            if(rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")){
                                rblBenRegistrationRequest.Val_Flag = 1;}
                            else {
                                rblBenRegistrationRequest.Val_Flag = 0;}
                            rblBenRegistrationRequest.rid= 0;
                            benRegistration(rblBenRegistrationRequest, BenRegOtp, ApiConstants.BenRegOTP, otpDialog);
                        }else {
                            RblBenRegistrationRequest rblBenRegistrationRequest=new RblBenRegistrationRequest();
                            rblBenRegistrationRequest.DeviceId =Common.getDeviceId(context);
                            rblBenRegistrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            rblBenRegistrationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                            rblBenRegistrationRequest.AccNo =accountEdt.getText().toString().trim();
                            rblBenRegistrationRequest.RMobile= Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                            rblBenRegistrationRequest.OTP= Long.parseLong(otp_edt.getText().toString().trim());
                            if(rblBenRegistrationRequest.AccNo.equals(verifiedAccount)){
                                rblBenRegistrationRequest.Val_Flag = 1;}
                            else {
                                rblBenRegistrationRequest.Val_Flag = 0;}
                            rblBenRegistrationRequest.rid= 0/*Long.parseLong(relationKey)*/;
                            benRegistration(rblBenRegistrationRequest, BenRegOtp, ApiConstants.BenRegOTP, otpDialog);
                        }
                    }else {
                        Toast.makeText(context,R.string.empty_and_invalid_otp,Toast.LENGTH_LONG).show();
                    }
//                    forgetDialog.dismiss();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        resendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(resendTv);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(otpDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (NullPointerException e){

                }
                if(Common.checkInternetConnection(context)){
                    if(type==BenDel){
                        RblBenDeleteRequest rblBenDeleteRequest=new RblBenDeleteRequest();
                        rblBenDeleteRequest.DeviceId =Common.getDeviceId(context);
                        rblBenDeleteRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        rblBenDeleteRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                        rblBenDeleteRequest.AccNo=rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo;
                        rblBenDeleteRequest.RMobile=rblGetSenderResponse.RBLRemitterDetails.MobileNo;
                        deleteBeneficiaryClick(rblBenDeleteRequest, otpDialog);
                    }else {
                        RblBenRegistrationRequest rblBenRegistrationRequest=new RblBenRegistrationRequest();
                        rblBenRegistrationRequest.DeviceId =Common.getDeviceId(context);
                        rblBenRegistrationRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        rblBenRegistrationRequest.DoneCardUser=loginModel.Data.DoneCardUser;
                        rblBenRegistrationRequest.AccNo =accountEdt.getText().toString().trim();
                        rblBenRegistrationRequest.RMobile= Long.parseLong(rblGetSenderResponse.RBLRemitterDetails.MobileNo);
                        if(rblBenRegistrationRequest.AccNo.equals(verifiedAccount)){
                            rblBenRegistrationRequest.Val_Flag = 1;}
                        else {
                            rblBenRegistrationRequest.Val_Flag = 0;}
                        rblBenRegistrationRequest.rid= 0/*Long.parseLong(relationKey)*/;
                        benRegistration(rblBenRegistrationRequest, BenResendOtp, ApiConstants.BenResendOTP, otpDialog);
                    }

                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                otpDialog.dismiss();
            }
        });
        otpDialog.show();
    }

    private void deleteBenOtp(RblBenDeleteRequest request, final Dialog otpDialog) {
        new NetworkCall().callRblLongTimeService(request, ApiConstants.BenDeleteOTP, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, BEN_DELETE_OTP, otpDialog);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },true);

    }

    private void responseHandler(ResponseBody response, int TYPE, Dialog otpDialog) {
        switch (TYPE){
            case BEN_DELETE_REQ:
                try {
                    RblCommonResponse commonResponse = new Gson().fromJson(response.string(), RblCommonResponse.class);
                    if(commonResponse!=null){
                        if(commonResponse.status==1){
                            if(otpDialog==null) {
                                openOtpDialog(BenDel);
                            }
                        }else {
                            Toast.makeText(context,commonResponse.description+"",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case BEN_DELETE_OTP:
                try {
                    RblCommonResponse commonResponse = new Gson().fromJson(response.string(), RblCommonResponse.class);
                    if(commonResponse!=null){
                        if(commonResponse.status==1){
                            position=0;
                            getCustomerDetail();
                            Toast.makeText(context,commonResponse.description+"",Toast.LENGTH_LONG).show();
                            if(otpDialog!=null){
                                otpDialog.dismiss();}
                        }else {
                            Toast.makeText(context,commonResponse.description+"",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case GET_SENDER_DETAIL:
                try {
                    RblGetSenderResponse senderResponse = new Gson().fromJson(response.string(), RblGetSenderResponse.class);
                    if(senderResponse!=null){
                        if (senderResponse.status == 1) {
                            rblGetSenderResponse = senderResponse;
                            String[] arr = new String[rblGetSenderResponse.BenDetails.BenDetail.size()];
                            for (int i = 0; i < rblGetSenderResponse.BenDetails.BenDetail.size(); i++) {
                                arr[i] = rblGetSenderResponse.BenDetails.BenDetail.get(i).Name;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                    R.layout.mobile_operator_spinner_item, R.id.operator_tv, arr);
                            adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
                            beneficiarySpinner.setAdapter(adapter);

                            //show default beneficiary if available
                            if (rblGetSenderResponse.BenDetails.BenDetail.size() > 0) {
                                registerBeneficiaryClick();
                            } else {
                                newBeneficiaryClick();
                            }
                            Toast.makeText(context, senderResponse.description, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, senderResponse.description, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private boolean validatePayment() {
        if(!Common.isNameValid(beneficiaryNameEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        } else if(accountEdt.length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_account_number,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(IFSCCodeEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(TType.length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_transaction_type,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(transferAmountEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(transferAmountEdt.getText().toString()) < 100){
            Toast.makeText(context,R.string.amount_less_than100,Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(transferAmountEdt.getText().toString()) >
                Float.parseFloat(rblGetSenderResponse.RBLRemitterDetails.ALimit)){
            Toast.makeText(context,"Maximum amount should be " +
                    rblGetSenderResponse.RBLRemitterDetails.ALimit,Toast.LENGTH_SHORT).show();
            return false;
        }else if((Float.parseFloat(transferAmountEdt.getText().toString())%TRANSACTION_LIMIT)>0 &&
                (Float.parseFloat(transferAmountEdt.getText().toString())%TRANSACTION_LIMIT)<100){
            Toast.makeText(context,R.string.amount_multiple100,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateRegistration() {
        if(!Common.isNameValid(beneficiaryNameEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(accountEdt.length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_account_id,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(IFSCCodeEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(bankName.length()==0){
            Toast.makeText(context,R.string.select_bank,Toast.LENGTH_SHORT).show();
            return false;
        }else if(relationKey.length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_relation,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateVerifyAccount(String bankName, int i) {
        if(!Common.isNameValid(beneficiaryNameEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(accountEdt.length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_account_id,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(IFSCCodeEdt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(bankName.length()==0 && i==0){
            Toast.makeText(context,R.string.select_bank,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void newBeneficiaryClick() {
        registeredBeneficiaryTv.setSelected(false);
        newBeneficiaryTv.setSelected(true);
        registeredBeneficiaryTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
        registeredBeneficiaryTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

        newBeneficiaryTv.setBackgroundResource(R.drawable.blue_rect_button_background);
        newBeneficiaryTv.setTextColor(getResources().getColor(R.color.color_white));

        benVerifyTv.setVisibility(View.INVISIBLE);
        payNowTv.setVisibility(View.GONE);
        registerNewBeneficiaryTv.setVisibility(View.VISIBLE);
        bankNameInput.setVisibility(View.GONE);
        bank_spinner.setVisibility(View.VISIBLE);
        bankView.setVisibility(View.VISIBLE);
        relationView.setVisibility(View.VISIBLE);
        relation_spinner.setVisibility(View.VISIBLE);

        registeredBenfListRel.setVisibility(View.GONE);
        transactionLin.setVisibility(View.GONE);
        searchIFSCTv.setVisibility(View.VISIBLE);

        accountEdt.setEnabled(true);
        beneficiaryNameEdt.setEnabled(true);
        bankNameEdt.setEnabled(true);
        NameAsPerBankEdt.setEnabled(true);
        IFSCCodeEdt.setEnabled(true);

        accountEdt.setText("");
        beneficiaryNameEdt.setText("");
        bankNameEdt.setText("");
        NameAsPerBankEdt.setText("");
        IFSCCodeEdt.setText("");
        verifyAccountTv.setText(R.string.rbl_ben_non_verify_text);

    }

    private void registerBeneficiaryClick() {
        registeredBeneficiaryTv.setSelected(true);
        newBeneficiaryTv.setSelected(false);
        registeredBeneficiaryTv.setBackgroundResource(R.drawable.blue_rect_button_background);
        registeredBeneficiaryTv.setTextColor(getResources().getColor(R.color.color_white));

        newBeneficiaryTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
        newBeneficiaryTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

        payNowTv.setVisibility(View.VISIBLE);
        registerNewBeneficiaryTv.setVisibility(View.GONE);
        bankNameInput.setVisibility(View.VISIBLE);
        bank_spinner.setVisibility(View.GONE);
        bankView.setVisibility(View.GONE);
        relationView.setVisibility(View.GONE);
        relation_spinner.setVisibility(View.GONE);

        registeredBenfListRel.setVisibility(View.VISIBLE);
        searchIFSCTv.setVisibility(View.INVISIBLE);
        transactionLin.setVisibility(View.VISIBLE);

        if(canNEFT && !canIMPS) {
            TType=NEFT;
            transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
            transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

            transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background);
            transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
        }else if(!canNEFT && !canIMPS){
            TType="";
            transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
            transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

            transactionTypeNEFTTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
            transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
        }

        benVerifyTv.setVisibility(View.VISIBLE);
        if(rblGetSenderResponse.BenDetails.BenDetail.get(position).Ben_flag.equalsIgnoreCase("true")){
            benVerifyTv.setText("OTP verified");
//            benVerifyTv.
            benVerifyTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_white, 0);
//            benVerifyTv.setCompoundDrawables(getResources().getDrawable(R.drawable.check_box_checked),null,null,null);
        }else {
            benVerifyTv.setText("OTP not verified");
            benVerifyTv.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.cross_white, 0);
//            benVerifyTv.setCompoundDrawables(getResources().getDrawable(R.drawable.check_box_unchecked),null,null,null);
        }

        if (rblGetSenderResponse.BenDetails.BenDetail.get(position).Val_flag.equalsIgnoreCase("true")) {
            verifyAccountTv.setText(R.string.rbl_ben_verify_text);
        } else {
            verifyAccountTv.setText(R.string.rbl_ben_non_verify_text);
        }

        accountEdt.setEnabled(false);
        beneficiaryNameEdt.setEnabled(false);
        bankNameEdt.setEnabled(false);
        NameAsPerBankEdt.setEnabled(false);
        IFSCCodeEdt.setEnabled(false);

        accountEdt.setText(rblGetSenderResponse.BenDetails.BenDetail.get(position).AccountNo);
        beneficiaryNameEdt.setText(rblGetSenderResponse.BenDetails.BenDetail.get(position).Name);
        bankNameEdt.setText(rblGetSenderResponse.BenDetails.BenDetail.get(position).Bank);
        NameAsPerBankEdt.setText(rblGetSenderResponse.BenDetails.BenDetail.get(position).NameAsPerBank);
        IFSCCodeEdt.setText(rblGetSenderResponse.BenDetails.BenDetail.get(position).IFSC);
    }

    private void openSearchIFSCDialog(String title, String message,
                                      String cancel, String delete) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_ifsc_dialog_layout);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final TextView byIFSCTv= (TextView) dialog.findViewById(R.id.byIFSCTv);
        final TextView byBranchTv= (TextView) dialog.findViewById(R.id.byBranchTv);
        final TextView search_tv= (TextView) dialog.findViewById(R.id.search_tv);

        final TextInputLayout ifscCodeInput= (TextInputLayout) dialog.findViewById(R.id.ifscCodeInput);
        final EditText ifscCodeEdt= (EditText) dialog.findViewById(R.id.ifscCodeEdt);
        ifscCodeEdt.setText(IFSCCodeEdt.getText().toString());

        final TextView bankNameTv= (TextView) dialog.findViewById(R.id.bankNameTv);
        final TextView stateNameTv= (TextView) dialog.findViewById(R.id.stateNameTv);
        final TextView cityNameTv= (TextView) dialog.findViewById(R.id.cityNameTv);
        final TextView branchNameTv= (TextView) dialog.findViewById(R.id.branchNameTv);

        final Spinner spinnerBankName= (Spinner) dialog.findViewById(R.id.spinnerBankName);
        final Spinner spinnerStateName= (Spinner) dialog.findViewById(R.id.spinnerStateName);
        final Spinner spinnerCityName= (Spinner) dialog.findViewById(R.id.spinnerCityName);
        final Spinner spinnerBranchName= (Spinner) dialog.findViewById(R.id.spinnerBranchName);

        spinnerBankName.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.select)));
        spinnerStateName.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.select)));
        spinnerCityName.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.select)));
        spinnerBranchName.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.select)));

        setNameTvGone(bankNameTv, stateNameTv, cityNameTv, branchNameTv);
        setNameTvVisible(bankNameTv, stateNameTv, cityNameTv, branchNameTv);
        setNameSpinnerGone(spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName);
        setNameSpinnerVisible(spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName);

        final LinearLayout byBranchLin = (LinearLayout) dialog.findViewById(R.id.byBranchLin);
        byBranchLin.setVisibility(View.GONE);

        ((TextView) dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView) dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView) dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView) dialog.findViewById(R.id.ok_tv)).setText(delete);

        commonCityStateBranchRequest.Bank="";
        getStateCity(ApiConstants.GetBankData,commonCityStateBranchRequest,spinnerBankName,BANK);

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ok_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isIFSCValid(ifscCodeEdt.getText().toString().trim())) {
                    RblGetBankDetailsByIFSCRequest request = new RblGetBankDetailsByIFSCRequest();
                    request.DeviceId = Common.getDeviceId(context);
                    request.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    request.DoneCardUser = loginModel.Data.DoneCardUser;
                    request.IFSCCode = ifscCodeEdt.getText().toString().trim();

                    if(Common.checkInternetConnection(context)) {
                        getBankDetailsByIfscCode(request, bankNameTv, stateNameTv, cityNameTv, branchNameTv,
                                spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName, byBranchLin);
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.empty_and_invalid_ifsc_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.byIFSCTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byBranchTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                byBranchTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

                byIFSCTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                byIFSCTv.setTextColor(getResources().getColor(R.color.color_white));

                byBranchLin.setVisibility(View.GONE);
                ifscCodeInput.setVisibility(View.VISIBLE);
                search_tv.setVisibility(View.VISIBLE);

                setNameTvVisible(bankNameTv, stateNameTv, cityNameTv, branchNameTv);
                setNameSpinnerGone(spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName);

            }
        });

        dialog.findViewById(R.id.byBranchTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byIFSCTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                byIFSCTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

                byBranchTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                byBranchTv.setTextColor(getResources().getColor(R.color.color_white));

                byBranchLin.setVisibility(View.VISIBLE);
                ifscCodeInput.setVisibility(View.GONE);
                search_tv.setVisibility(View.GONE);

                setNameTvGone(bankNameTv,stateNameTv, cityNameTv, branchNameTv);
                setNameSpinnerVisible(spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName);
            }
        });

        spinnerBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0 || !spinnerBankName.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    commonCityStateBranchRequest.Bank = spinnerBankName.getSelectedItem().toString();
                    getStateCity(ApiConstants.GetStateData,commonCityStateBranchRequest,spinnerStateName, STATE);
                } else {
                    commonCityStateBranchRequest.Bank="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerStateName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0 || !spinnerStateName.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    commonCityStateBranchRequest.State = spinnerStateName.getSelectedItem().toString();
                    getStateCity(ApiConstants.GetCityData,commonCityStateBranchRequest,spinnerCityName, CITY);
                } else {
                    commonCityStateBranchRequest.State="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCityName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0 || !spinnerCityName.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    commonCityStateBranchRequest.City = spinnerCityName.getSelectedItem().toString();
                    getStateCity(ApiConstants.GetBranchHData,commonCityStateBranchRequest,spinnerBranchName, BRANCH);
                } else {
                    commonCityStateBranchRequest.City="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerBranchName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0 || !spinnerBranchName.getSelectedItem().
                        toString().toLowerCase().contains("select")) {

                    RblAddressIFSCRequest ifscRequest=new RblAddressIFSCRequest();
                    ifscRequest.Bank=commonCityStateBranchRequest.Bank;
                    ifscRequest.State=commonCityStateBranchRequest.State;
                    ifscRequest.City=commonCityStateBranchRequest.City;
                    ifscRequest.Branch=spinnerBranchName.getSelectedItem().toString();
                    ifscRequest.DeviceId=Common.getDeviceId(context);
                    ifscRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    ifscRequest.DoneCardUser=loginModel.Data.DoneCardUser;

                    getIfscData(ApiConstants.GetAddressIFSCData,ifscRequest, bankNameTv,stateNameTv,cityNameTv,branchNameTv,
                            spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName, byBranchLin,ifscCodeInput, ifscCodeEdt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
    }

    private void setNameTvGone(TextView bankNameTv, TextView stateNameTv, TextView cityNameTv, TextView branchNameTv) {
        bankNameTv.setVisibility(View.INVISIBLE);
        stateNameTv.setVisibility(View.INVISIBLE);
        cityNameTv.setVisibility(View.INVISIBLE);
        branchNameTv.setVisibility(View.INVISIBLE);
    }
    private void setNameTvVisible(TextView bankNameTv, TextView stateNameTv, TextView cityNameTv, TextView branchNameTv) {
        bankNameTv.setVisibility(View.VISIBLE);
        stateNameTv.setVisibility(View.VISIBLE);
        cityNameTv.setVisibility(View.VISIBLE);
        branchNameTv.setVisibility(View.VISIBLE);
    }
    private void setNameSpinnerGone(Spinner bankNameSp, Spinner stateNameSp, Spinner cityNameSp, Spinner branchNameSp) {
        bankNameSp.setVisibility(View.INVISIBLE);
        stateNameSp.setVisibility(View.INVISIBLE);
        cityNameSp.setVisibility(View.INVISIBLE);
        branchNameSp.setVisibility(View.INVISIBLE);
    }
    private void setNameSpinnerVisible(Spinner bankNameSp, Spinner stateNameSp, Spinner cityNameSp, Spinner branchNameSp) {
        bankNameSp.setVisibility(View.VISIBLE);
        stateNameSp.setVisibility(View.VISIBLE);
        cityNameSp.setVisibility(View.VISIBLE);
        branchNameSp.setVisibility(View.VISIBLE);
    }

    private void getBankDetailsByIfscCode(RblGetBankDetailsByIFSCRequest request, final TextView bankNameTv,
                                          final TextView stateNameTv, final TextView cityNameTv,
                                          final TextView branchNameTv, final Spinner spinnerBankName,
                                          final Spinner spinnerStateName, final Spinner spinnerCityName,
                                          final Spinner spinnerBranchName, final LinearLayout byBranchLin) {

        new NetworkCall().callRblLongTimeService(request, ApiConstants.GetBankDetails, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            try {
                                RblBankDetailByIFSCResponse commonResponse = new Gson().fromJson(response.string(), RblBankDetailByIFSCResponse.class);
                                if(commonResponse!=null){
                                    if(commonResponse.Bank!=null){
                                        byBranchLin.setVisibility(View.VISIBLE);
                                        setNameTvVisible(bankNameTv, stateNameTv,cityNameTv,branchNameTv);
                                        setNameSpinnerGone(spinnerBankName,spinnerStateName,spinnerCityName,spinnerBranchName);

                                        bankNameTv.setText(commonResponse.Bank);
                                        stateNameTv.setText(commonResponse.State);
                                        cityNameTv.setText(commonResponse.City);
                                        branchNameTv.setText(commonResponse.Branch);
                                        IFSCCodeEdt.setText(commonResponse.IFSCCode);
                                    }
                                    else
                                    {
                                        hideCustomDialog();
                                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },true);
    }


    private void getStateCity(final String methodName, RblCommonCityStateBranchRequest request,
                              final Spinner spinner,final int TYPE) {

        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<ArrayList<RblCommonStateCityBranchResponse>> call = apiService.getRblCommonStateCityPost(methodName,request);
        call.enqueue(new Callback<ArrayList<RblCommonStateCityBranchResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RblCommonStateCityBranchResponse>>call,
                                   Response<ArrayList<RblCommonStateCityBranchResponse>> response) {

                try{

                    if(response.body()!=null && response.body().size()>0){
                        hideCustomDialog();
                        String[] arr=new String[response.body().size()+1];
                        arr[0]="-select-";
//                        dataBaseHelper.insertStateNames("select-state", null);
                        for (int p=0;p<response.body().size();p++){
                            arr[p+1]=response.body().get(p).Key;
//                            dataBaseHelper.insertStateNames(response.body().Data.get(p).StateName, null);
                        }
                        spinner.setAdapter(getSpinnerAdapter(arr));
                        /*switch (TYPE){
                            case BANK:
                                break;
                            case STATE:
                                break;
                            case CITY:
                                break;
                            case BRANCH:
                                break;
                        }*/

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
            public void onFailure(Call<ArrayList<RblCommonStateCityBranchResponse>>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getIfscData(final String methodName, RblAddressIFSCRequest request,
                             final TextView bankNameTv, final TextView stateNameTv, final TextView cityNameTv,
                             final TextView branchNameTv, final Spinner spinnerBankName, final Spinner spinnerStateName,
                             final Spinner spinnerCityName, final Spinner spinnerBranchName, LinearLayout byBranchLin,
                             final TextInputLayout ifscCodeInput, final EditText ifscCodeEdt) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblAddressIFSCResponse> call = apiService.getRblIfscDataPost(methodName,request);
        call.enqueue(new Callback<RblAddressIFSCResponse>() {
            @Override
            public void onResponse(Call<RblAddressIFSCResponse>call, Response<RblAddressIFSCResponse> response) {

                try{

                    if(response.body()!=null){
                        hideCustomDialog();
//                        setNameTvVisible(bankNameTv,stateNameTv, cityNameTv, branchNameTv);
//                        setNameSpinnerGone(spinnerBankName, spinnerStateName, spinnerCityName, spinnerBranchName);
                        bankNameTv.setText(response.body().Bank);
                        stateNameTv.setText(response.body().State);
                        cityNameTv.setText(response.body().City);
                        branchNameTv.setText(response.body().Branch);

                        ifscCodeInput.setVisibility(View.VISIBLE);
                        ifscCodeEdt.setText(response.body().IFSCCode);
                        IFSCCodeEdt.setText(response.body().IFSCCode);
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
            public void onFailure(Call<RblAddressIFSCResponse>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setfont() {
        Typeface face = Common.ButtonTypeFace(context);
        Typeface face1 = Common.TitleTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);

        sender_tv.setTypeface(face2);
        mobile_tv.setTypeface(face2);
        pin_tv.setTypeface(face2);
        city_name_tv.setTypeface(face2);
        state_name_tv.setTypeface(face2);
        month_tv.setTypeface(face2);
        beneficiary_tv.setTypeface(face2);
        kyc_tv.setTypeface(face2);
        sender_title_tv.setTypeface(face1);
        remittance_tv.setTypeface(face1);
        benVerifyTv.setTypeface(face1);
        registeredBeneficiaryTv.setTypeface(face1);
        newBeneficiaryTv.setTypeface(face1);
        beneficiary_label.setTypeface(face1);
        transactionTypeIMPSTv.setTypeface(face1);
        transactionTypeNEFTTv.setTypeface(face1);
        searchIFSCTv.setTypeface(face3);
        verifyAccountTv.setTypeface(face3);
        calculateCharge.setTypeface(face3);
        payNowTv.setTypeface(face3);
        registerNewBeneficiaryTv.setTypeface(face3);
        senderNameTv.setTypeface(face);
        senderMobileTv.setTypeface(face);
        pinCodeTv.setTypeface(face);
        cityNameTv.setTypeface(face);
        stateNameTv.setTypeface(face);
        monthLimitTv.setTypeface(face);
        beneficiaryLimitTv.setTypeface(face);
        KYCStatusTv.setTypeface(face);
        beneficiaryNameEdt.setTypeface(face);
        accountEdt.setTypeface(face);
        IFSCCodeEdt.setTypeface(face);
        transferAmountEdt.setTypeface(face);
        remarkEdt.setTypeface(face);
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    otp_edt.setText(message);
                }
            }catch (Exception e)
            {

            }
        }
    };
}

