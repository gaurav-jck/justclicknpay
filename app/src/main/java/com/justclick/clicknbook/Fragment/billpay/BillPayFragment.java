package com.justclick.clicknbook.Fragment.billpay;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RechargeDetailResponseModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.RechargeDetailModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BillPayFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int POSTPAID_TYPE =1, WATER_TYPE =2,
            DATACARD_TYPE=3, ELECTRICITY_TYPE=4,
            LANDLINE_TYPE=5, GAS_TYPE=6, INSURANCE_TYPE=7, EMI_TYPE=8;
    public static final String categoryElectricity="Electricity",categoryGas="Gas",categoryPostpaid="Postpaid",
            categoryWater="Water",categoryInsurance="Insurance",categoryDatacardPrepaid="DatacardPrepaid",
            categoryLandline="Landline",categoryEMI="EMI", categoryFastTag="FastTag";
    private boolean isElectricityOperator, isGasOperator, isPostpaidOperator, isWaterOperator, isDatacardOperator,
            isLandlineOperator;
    private Context context;
    private LoginModel loginModel;
    private View view;
    private TextView proceedBtn;
    private LinearLayout recharge_layout_mobile, recharge_layout_dth,
            recharge_layout_datacard, recharge_layout_electricity,
            recharge_layout_landline, recharge_layout_gas;
    private int rechargeType= POSTPAID_TYPE;
    //    mobile_icon_black

    private TextInputLayout userIdInput, userIdInputWater, userIdInput2Water,
            userIdInputElectricity, userIdInput2Electricity,
            userIdInputDatacard, userIdInput2Datacard,
            userIdInputLandline, userIdInput2Landline,
            userIdInputGas, userIdInput2Gas;

    private String mobileOperator="";
    private Spinner spinner_postpaid_operator;
    private EditText userIdEdt;
    //    dth_icon
    private Spinner spinner_water_operator;
    private EditText userIdEdtWater, userIdEdt2Water;
    private String waterOperator ="";
    //    dataCard
    private Spinner spinner_datacard_operator;
    private String datacardOperator="";
    private EditText userIdEdtDatacard, userIdEdt2Datacard;

    //    Electricity
    private Spinner spinner_electricity_operator;
    private EditText userIdEdtElectricity, userIdEdt2Electricity;
    private String electricityOperator="";

    //    Landline
    private Spinner spinner_landline_operator;
    private EditText userIdEdtLandline, userIdEdt2Landline;
    private String landlineOperator="";

    //    gas
    private Spinner spinner_gas_operator;
    private EditText userIdEdtGas, userIdEdt2Gas;
    private String gasOperator="";

    private ArrayList<OperatorResponseModel.operatorlistDetails> postpaidOperatorArrayList, waterOperatorArrayList,
            datacardOperatorArrayList, elecricityOperatorArrayList,
            landlineOperatorArrayList, gasOperatorArrayList;
    private OperatorResponseModel.operatorlistDetails operatorPostpaidSelector, operatorWaterSelector, operatorDatacardSelector,
            operatorElectricitySelector, operatorLandlineSelector, operatorGasSelector;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private static BillPayFragment postpaidFragmentInstance,waterFragmentInstance, dataCardFragmentInstance,
            electricityFragmentInstance,landlineFragmentInstance, gasFragmentInstance;

    public BillPayFragment()
    {
        // Required empty public constructor
    }

    public static BillPayFragment getInstance(Context context, int fragmentToShow) {

        Bundle args = new Bundle();
        args.putInt("FragmentToShow", fragmentToShow);
        switch (fragmentToShow){
            case POSTPAID_TYPE:
                if(postpaidFragmentInstance==null){
                    postpaidFragmentInstance=new BillPayFragment();
                }
                postpaidFragmentInstance.setArguments(args);
                return postpaidFragmentInstance;
            case WATER_TYPE:
                if(waterFragmentInstance==null){
                    waterFragmentInstance =new BillPayFragment();
                }
                waterFragmentInstance.setArguments(args);
                return waterFragmentInstance;
            case DATACARD_TYPE:
                if(dataCardFragmentInstance==null){
                    dataCardFragmentInstance =new BillPayFragment();
                }
                dataCardFragmentInstance.setArguments(args);
                return dataCardFragmentInstance;
            case ELECTRICITY_TYPE:
                if(electricityFragmentInstance==null){
                    electricityFragmentInstance =new BillPayFragment();
                }
                electricityFragmentInstance.setArguments(args);
                return electricityFragmentInstance;
            case LANDLINE_TYPE:
                if(landlineFragmentInstance==null){
                    landlineFragmentInstance =new BillPayFragment();
                }
                landlineFragmentInstance.setArguments(args);
                return landlineFragmentInstance;
            case GAS_TYPE:
                if(gasFragmentInstance==null){
                    gasFragmentInstance =new BillPayFragment();
                }
                gasFragmentInstance.setArguments(args);
                return gasFragmentInstance;
        }
        return new BillPayFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener=(ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        postpaidOperatorArrayList =new ArrayList<>();
        waterOperatorArrayList =new ArrayList<>();
        datacardOperatorArrayList =new ArrayList<>();
        elecricityOperatorArrayList =new ArrayList<>();
        landlineOperatorArrayList =new ArrayList<>();
        gasOperatorArrayList =new ArrayList<>();
        rechargeType=getArguments().getInt("FragmentToShow");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_billpay, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.rechargeFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        initializeViews();

        fragmentToShow(rechargeType);

        if(postpaidOperatorArrayList.size()==0 || !isPostpaidOperator) {
            callOperator(categoryPostpaid, POSTPAID_TYPE, false);
        }else {
            spinner_postpaid_operator.setAdapter(setSpinnerAdapter(postpaidOperatorArrayList));
            spinner_water_operator.setAdapter(setSpinnerAdapter(waterOperatorArrayList));
            spinner_datacard_operator.setAdapter(setSpinnerAdapter(datacardOperatorArrayList));
            spinner_electricity_operator.setAdapter(setSpinnerAdapter(elecricityOperatorArrayList));
            spinner_landline_operator.setAdapter(setSpinnerAdapter(landlineOperatorArrayList));
            spinner_gas_operator.setAdapter(setSpinnerAdapter(gasOperatorArrayList));
        }


        return view;
    }

    private void setTitle(int rechargeType) {
        switch (rechargeType){
            case POSTPAID_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryPostpaid);
                break;
            case WATER_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryWater);
                break;
            case DATACARD_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryDatacardPrepaid);
                break;
            case ELECTRICITY_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryElectricity);
                break;
            case LANDLINE_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryLandline);
                break;
            case GAS_TYPE:
                titleChangeListener.onToolBarTitleChange(categoryGas);
                break;
        }
    }

    private void initializeViews() {

        recharge_layout_mobile  =  view.findViewById(R.id.recharge_layout_postpaid);
        recharge_layout_dth  = view.findViewById(R.id.recharge_layout_water);
        recharge_layout_datacard =  view.findViewById(R.id.recharge_layout_datacard);
        recharge_layout_electricity =  view.findViewById(R.id.recharge_layout_electricity);
        recharge_layout_landline = view.findViewById(R.id.recharge_layout_landline);
        recharge_layout_gas =  view.findViewById(R.id.recharge_layout_gas);
        proceedBtn  = view.findViewById(R.id.proceedBtn);
        proceedBtn.setOnClickListener(this);
        view.findViewById(R.id.rel_mobile).setOnClickListener(this);

        initializePostpaidView();
        initializeWaterView();
        initializeDataCardView();
        initializeElectricityView();
        initializeLandLineView();
        initializeGasView();
    }

    private void initializePostpaidView() {

        spinner_postpaid_operator = view.findViewById(R.id.spinner_postpaid_operator);
        spinner_postpaid_operator.setOnItemSelectedListener(this);
        userIdEdt = view.findViewById(R.id.userIdEdt);
        userIdInput = view.findViewById(R.id.userIdInput);
    }
    private void initializeWaterView() {
        spinner_water_operator = view.findViewById(R.id.spinner_water_operator);
        spinner_water_operator.setOnItemSelectedListener(this);

        userIdEdtWater = view.findViewById(R.id.userIdEdtWater);
        userIdInputWater = view.findViewById(R.id.userIdInputWater);
        userIdEdt2Water = view.findViewById(R.id.userIdEdt2Water);
        userIdInput2Water = view.findViewById(R.id.userIdInput2Water);
    }
    private void initializeDataCardView() {
        spinner_datacard_operator = view.findViewById(R.id.spinner_datacard_operator);
        spinner_datacard_operator.setOnItemSelectedListener(this);
        userIdEdtDatacard = view.findViewById(R.id.userIdEdtDatacard);
        userIdInputDatacard = view.findViewById(R.id.userIdInputDatacard);
        userIdEdt2Datacard = view.findViewById(R.id.userIdEdt2Datacard);
        userIdInput2Datacard = view.findViewById(R.id.userIdInput2Datacard);

    }
    private void initializeElectricityView() {
        spinner_electricity_operator = view.findViewById(R.id.spinner_electricity_operator);
        spinner_electricity_operator.setOnItemSelectedListener(this);
        userIdEdtElectricity=view.findViewById(R.id.userIdEdtElectricity);
        userIdEdt2Electricity=view.findViewById(R.id.userIdEdt2Electricity);
        userIdInputElectricity = view.findViewById(R.id.userIdInputElectricity);
        userIdInput2Electricity = view.findViewById(R.id.userIdInput2Electricity);

    }
    private void initializeLandLineView() {
        spinner_landline_operator =  view.findViewById(R.id.spinner_landline_operator);
        spinner_landline_operator.setOnItemSelectedListener(this);
        userIdEdtLandline=view.findViewById(R.id.userIdEdtLandline);
        userIdEdt2Landline=view.findViewById(R.id.userIdEdt2Landline);
        userIdInputLandline = view.findViewById(R.id.userIdInputLandline);
        userIdInput2Landline = view.findViewById(R.id.userIdInput2Landline);

    }
    private void initializeGasView() {
        spinner_gas_operator = view.findViewById(R.id.spinner_gas_operator);
        spinner_gas_operator.setOnItemSelectedListener(this);
        userIdEdtGas=view.findViewById(R.id.userIdEdtGas);
        userIdEdt2Gas=view.findViewById(R.id.userIdEdt2Gas);
        userIdInputGas = view.findViewById(R.id.userIdInputGas);
        userIdInput2Gas = view.findViewById(R.id.userIdInput2Gas);
    }

    class OperatorListRequest{
        String Category,AgentCode, Merchant=ApiConstants.MerchantId, Type="App";
       /* {
            "Category": "Electricity",
                "AgentCode": "JC0A13387",
                "Merchant": "JUSTCLICKTRAVELS",
                "Type": "WEB"
        }*/
    }

    public class OperatorResponseModel implements Serializable{
        public String StatusCode, StatusMessage;
        int OperaterCount;
        public ArrayList<operatorlistDetails> operatorlistDetails;

        public class operatorlistDetails implements Serializable {
            public String operaterid, name,category,regex, displayname, Suppliercode, ad1_name, ad1_regex;
            public int viewbill;
            public boolean ad1code, ad2code,ad3code;
        }
    }

    private void callOperator(String type, final int rechargeType, final boolean once) {
        OperatorListRequest operatorModel=new OperatorListRequest();
        if(loginModel!=null && loginModel.Data!=null){
            operatorModel.AgentCode=loginModel.Data.DoneCardUser;
            operatorModel.Category=type;
        }

        new NetworkCall().callBillPayService(operatorModel, ApiConstants.Operatorlist, context, "", "", false, (response, responseCode) -> {
            if(response!=null){
                operatorResponseHandler(response,rechargeType);
            }
        });
    }

    private void operatorResponseHandler(ResponseBody response, int type) {
        try{
            OperatorResponseModel responseModel=new Gson().fromJson(response.string(),OperatorResponseModel.class);
            if(responseModel!=null && responseModel.StatusCode.equals("00")){
                switch (type){
                    case POSTPAID_TYPE:
                        postpaidOperatorArrayList.clear();
                        postpaidOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_postpaid_operator.setAdapter(setSpinnerAdapter(postpaidOperatorArrayList));
                        isPostpaidOperator=true;
                        spinner_postpaid_operator.setSelection(0);
                        if(!isWaterOperator) {
                            callOperator(categoryWater, WATER_TYPE, false);
                        }
                        break;
                    case WATER_TYPE:
                        waterOperatorArrayList.clear();
                        waterOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_water_operator.setAdapter(setSpinnerAdapter(waterOperatorArrayList));
                        isWaterOperator=true;
                        if(!isDatacardOperator) {
                            callOperator(categoryDatacardPrepaid, DATACARD_TYPE, false);
                        }
                        break;
                    case DATACARD_TYPE:
                        datacardOperatorArrayList.clear();
                        datacardOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_datacard_operator.setAdapter(setSpinnerAdapter(datacardOperatorArrayList));
                        isDatacardOperator=true;
                        if(!isElectricityOperator) {
                            callOperator(categoryElectricity, ELECTRICITY_TYPE, false);
//                            callOperator(categoryLandline, LANDLINE_TYPE, false);
                        }
                        break;
                    case ELECTRICITY_TYPE:
                        elecricityOperatorArrayList.clear();
                        elecricityOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_electricity_operator.setAdapter(setSpinnerAdapter(elecricityOperatorArrayList));
                        isElectricityOperator=true;
                        if(!isLandlineOperator) {
                            callOperator(categoryLandline, LANDLINE_TYPE, false);
                        }
                        break;
                    case LANDLINE_TYPE:
                        landlineOperatorArrayList.clear();
                        landlineOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_landline_operator.setAdapter(setSpinnerAdapter(landlineOperatorArrayList));
                        isLandlineOperator=true;
                        if(!isGasOperator) {
                            callOperator(categoryGas, GAS_TYPE, false);
                        }
                        break;
                    case GAS_TYPE:
                        gasOperatorArrayList.clear();
                        gasOperatorArrayList.addAll(responseModel.operatorlistDetails);
                        spinner_gas_operator.setAdapter(setSpinnerAdapter(gasOperatorArrayList));
                        isGasOperator=true;
                        break;
                }
            }

        }catch (Exception e){

        }
    }

    private ArrayAdapter<String> setSpinnerAdapter(ArrayList<OperatorResponseModel.operatorlistDetails> data) {
        String[] arr=new String[data.size()];
        for(int i=0;i<data.size();i++){
            arr[i]=data.get(i).name;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, arr);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.proceedBtn:
                try {
                    if(Common.checkInternetConnection(context)) {
                        if(validate(rechargeType)) {
                            Common.hideSoftKeyboard((NavigationDrawerActivity)context);
//                            getcommision(bundle);
                            fetchBill(rechargeType);
                        }
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                }
//                if(validate(rechargeType)) {
//                    proceedRecharge(rechargeType);
//                }

                break;

            case R.id.rel_mobile:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;
        }
    }

    public class FetchBillRequest{
        public String Category,AgentCode, Merchant=ApiConstants.MerchantId, Type="App", operatorId, canumber, ad1;
 /* {
    "operatorId": "20",
    "canumber": "60024474722",
    "Merchant": "JUSTCLICKTRAVELS",
    "AgentCode": "JC0A13387",
    "Type": "Web",
    "Category": "Electricity"
}*/
    }

    public class FetchBillResponseModel implements Serializable {
        public String statusCode, statusMessage;
        float billAmount;
        public ArrayList<payfetchbilllist> payfetchbilllist;

        public class payfetchbilllist implements Serializable{
            public float billAmount;
            public String dueDate, customerName,billNumber,fetchbillstring, category,operatorid,canumber,cellNumber;
            /*"": 40,
            "billdate": null,
            "": "2021-08-01",
            "acceptPayment": "0",
            "acceptPartPay": null,
            "cellNumber": null,
            "": "Ms. SHIV KALA DEVI",
            "": "010803748394",
            "description": null,
            "partpayment": false,
            "": "{\r\n  \"CustomerName\": \"Ms. SHIV KALA DEVI\",\r\n  \"BillNumber\": \"010803748394\",\r\n  \"Billdate\": \"2021-08-01\",\r\n  \"Billamount\": 40,\r\n  \"Duedate\": \"2021-08-01\",\r\n  \"status\": 1\r\n}",
            "": "Electricity",
            "": "20",
            "": "60024474722"*/
        }
    }

    private void fetchBill(int rechargeType) {
//
        FetchBillRequest request=new FetchBillRequest();
        if(loginModel!=null && loginModel.Data!=null){
            request.AgentCode=loginModel.Data.DoneCardUser;
        }
        OperatorResponseModel.operatorlistDetails operatorData = null;
        switch (rechargeType){
            case POSTPAID_TYPE:
//                Toast.makeText(context, categoryPostpaid, Toast.LENGTH_SHORT).show();
                request.Category=categoryPostpaid;
                request.canumber=userIdEdt.getText().toString().trim();
                request.operatorId=operatorPostpaidSelector.operaterid;
                operatorData=operatorPostpaidSelector;
                break;
            case WATER_TYPE:
//                Toast.makeText(context, categoryWater, Toast.LENGTH_SHORT).show();
                request.Category=categoryWater;
                request.canumber=userIdEdtWater.getText().toString().trim();
                request.operatorId=operatorWaterSelector.operaterid;
                if(operatorWaterSelector.ad1code){
                request.ad1=userIdEdt2Water.getText().toString().trim();}
                operatorData=operatorWaterSelector;
                break;
            case DATACARD_TYPE:
//                Toast.makeText(context, categoryDatacardPrepaid, Toast.LENGTH_SHORT).show();
                request.Category=categoryDatacardPrepaid;
                request.canumber=userIdEdtDatacard.getText().toString().trim();
                request.operatorId=operatorDatacardSelector.operaterid;
                if(operatorDatacardSelector.ad1code){
                request.ad1=userIdEdt2Datacard.getText().toString().trim();}
                operatorData=operatorDatacardSelector;
                break;
            case ELECTRICITY_TYPE:
                request.Category=categoryElectricity;
                request.canumber=userIdEdtElectricity.getText().toString().trim();
                request.operatorId=operatorElectricitySelector.operaterid;
                if(operatorElectricitySelector.ad1code){
                request.ad1=userIdEdt2Electricity.getText().toString().trim();}
                operatorData=operatorElectricitySelector;
                break;
            case LANDLINE_TYPE:
//                Toast.makeText(context, categoryLandline, Toast.LENGTH_SHORT).show();
                request.Category=categoryLandline;
                request.canumber=userIdEdtLandline.getText().toString().trim();
                request.operatorId=operatorLandlineSelector.operaterid;
                if(operatorLandlineSelector.ad1code){
                request.ad1=userIdEdt2Landline.getText().toString().trim();}
                operatorData=operatorLandlineSelector;
                break;
            case GAS_TYPE:
//                Toast.makeText(context, categoryGas, Toast.LENGTH_SHORT).show();
                request.Category=categoryGas;
                request.canumber=userIdEdtGas.getText().toString().trim();
                request.operatorId=operatorGasSelector.operaterid;
                if(operatorGasSelector.ad1code){
                request.ad1=userIdEdt2Gas.getText().toString().trim();}
                operatorData=operatorGasSelector;
                break;
        }

        OperatorResponseModel.operatorlistDetails finalOperatorData = operatorData;
        new NetworkCall().callBillPayService(request, ApiConstants.Fetchbilldetails, context, "", "", true, (response, responseCode) -> {
            MyCustomDialog.hideCustomDialog();
            if(response!=null){
                fetchBillResponseHandler(response,rechargeType, finalOperatorData);
            }else {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBillResponseHandler(ResponseBody response, int rechargeType, OperatorResponseModel.operatorlistDetails operator) {
        try{
            FetchBillResponseModel responseModel=new Gson().fromJson(response.string(),FetchBillResponseModel.class);
            if(responseModel!=null && responseModel.statusCode.equals("00")){
                if(responseModel.payfetchbilllist!=null){
                    showBillDetail(responseModel,rechargeType, operator);
                }else {
                    Toast.makeText(context, responseModel.statusMessage/*+"\namount="+responseModel.billAmount*/, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){

        }
    }

    private void showBillDetail(FetchBillResponseModel responseModel, int rechargeType, OperatorResponseModel.operatorlistDetails operator) {
        Bundle bundle=new Bundle();
        bundle.putInt("RechargeType",rechargeType);
        bundle.putSerializable("BillDetailResponse",responseModel.payfetchbilllist.get(0));
        bundle.putSerializable("OperatorData",operator);
        BillConfirmationFragment fragment = new BillConfirmationFragment();
        fragment.setArguments(bundle);
        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);

    }

    private boolean validate(int rechargeType) {
        switch (rechargeType){
            case POSTPAID_TYPE:
                return isPostpaidValid();
            case WATER_TYPE:
                return isWaterValid();
            case DATACARD_TYPE:
                return isDatacardValid();
            case ELECTRICITY_TYPE:
                return isElectricityValid();
            case LANDLINE_TYPE:
                return isLandlineValid();
            case GAS_TYPE:
                return isGasValid();
            default:
                return false;
        }

    }

    private boolean isPostpaidValid() {
//        Toast.makeText(context, categoryPostpaid+"\n"+userIdEdt.getText().toString().trim()
//                +"\n"+operatorPostpaidSelector.operaterid+"-"+operatorPostpaidSelector.name+"\n"+
//                operatorPostpaidSelector.regex, Toast.LENGTH_SHORT).show();
        if(mobileOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;

        }else if(!Common.isRegexValid(userIdEdt.getText().toString().trim(), operatorPostpaidSelector.regex)){
            Toast.makeText(context, operatorPostpaidSelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isWaterValid() {
//        Toast.makeText(context, categoryWater+"\n"+userIdEdtWater.getText().toString().trim()
//                +"\n"+operatorWaterSelector.operaterid+"-"+operatorWaterSelector.name+"\n"+
//                operatorWaterSelector.regex, Toast.LENGTH_SHORT).show();
        if(waterOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isRegexValid(userIdEdtWater.getText().toString().trim(), operatorWaterSelector.regex)){
            Toast.makeText(context, operatorWaterSelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }else if(operatorWaterSelector.ad1code && !Common.isRegexValid(userIdEdt2Water.getText().toString().trim(), operatorPostpaidSelector.ad1_regex)){
            Toast.makeText(context, operatorWaterSelector.ad1_name+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isDatacardValid() {
//        Toast.makeText(context, categoryDatacardPrepaid+"\n"+userIdEdtDatacard.getText().toString().trim()
//                +"\n"+operatorDatacardSelector.operaterid+"-"+operatorDatacardSelector.name+"\n"+
//                operatorDatacardSelector.regex, Toast.LENGTH_SHORT).show();
        if(datacardOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isRegexValid(userIdEdtDatacard.getText().toString().trim(), operatorDatacardSelector.regex)){
            Toast.makeText(context, operatorDatacardSelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }else if(operatorDatacardSelector.ad1code && !Common.isRegexValid(userIdEdt2Datacard.getText().toString().trim(), operatorDatacardSelector.ad1_regex)){
            Toast.makeText(context, operatorDatacardSelector.ad1_name+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isElectricityValid() {
//        Toast.makeText(context, categoryElectricity+"\n"+userIdEdtElectricity.getText().toString().trim()
//                +"\n"+operatorElectricitySelector.operaterid+"-"+operatorElectricitySelector.name+"\n"+
//                operatorElectricitySelector.regex+"\n"+operatorElectricitySelector.ad1_regex, Toast.LENGTH_SHORT).show();
        if(electricityOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isRegexValid(userIdEdtElectricity.getText().toString().trim(), operatorElectricitySelector.regex)){
            Toast.makeText(context, operatorElectricitySelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }else if(operatorElectricitySelector.ad1code && !Common.isRegexValid(userIdEdt2Electricity.getText().toString().trim(), operatorElectricitySelector.ad1_regex)){
            Toast.makeText(context, operatorElectricitySelector.ad1_name+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isLandlineValid() {
//        Toast.makeText(context, categoryLandline+"\n"+userIdEdtLandline.getText().toString().trim()
//                +"\n"+operatorLandlineSelector.operaterid+"-"+operatorLandlineSelector.name+"\n"+
//                operatorLandlineSelector.regex, Toast.LENGTH_SHORT).show();
        if(landlineOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isRegexValid(userIdEdtLandline.getText().toString().trim(), operatorLandlineSelector.regex)){
            Toast.makeText(context, operatorLandlineSelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }else if(operatorLandlineSelector.ad1code && !Common.isRegexValid(userIdEdt2Landline.getText().toString().trim(), operatorLandlineSelector.ad1_regex)){
            Toast.makeText(context, operatorLandlineSelector.ad1_name+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isGasValid() {
//        Toast.makeText(context, categoryGas+"\n"+userIdEdtGas.getText().toString().trim()
//                +"\n"+operatorGasSelector.operaterid+"-"+operatorGasSelector.name+"\n"+
//                operatorGasSelector.regex, Toast.LENGTH_SHORT).show();
        if(gasOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isRegexValid(userIdEdtGas.getText().toString().trim(), operatorGasSelector.regex)){
            Toast.makeText(context, operatorGasSelector.displayname+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }else if(operatorGasSelector.ad1code && !Common.isRegexValid(userIdEdt2Gas.getText().toString().trim(), operatorGasSelector.ad1_regex)){
            Toast.makeText(context, operatorGasSelector.ad1_name+" "+getString(R.string.empty_and_invalid_regex),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void fragmentToShow(int type){
        switch (type){
            case POSTPAID_TYPE:
                rechargeType = POSTPAID_TYPE;
                setOtherVisibilityGone();
                recharge_layout_mobile.setVisibility(View.VISIBLE);
//                mobileOperatorArrayList=dataBaseHelper.getAllOperatorNames(MOBILE_TYPE);
                if(postpaidOperatorArrayList.size()==0){
                    callOperator(categoryPostpaid, POSTPAID_TYPE, true);
                }
                break;
            case WATER_TYPE:
                rechargeType = WATER_TYPE;
                setOtherVisibilityGone();
                recharge_layout_dth.setVisibility(View.VISIBLE);
//                dthOperatorArrayList=dataBaseHelper.getAllOperatorNames(DTH_TYPE);
                if(waterOperatorArrayList.size()==0){
                    callOperator(categoryWater, WATER_TYPE, true);
                }
                break;
            case DATACARD_TYPE:
                rechargeType = DATACARD_TYPE;
                setOtherVisibilityGone();
                recharge_layout_datacard.setVisibility(View.VISIBLE);
//                datacardOperatorArrayList=dataBaseHelper.getAllOperatorNames(DATACARD_TYPE);
                if(datacardOperatorArrayList.size()==0){
                    callOperator(categoryDatacardPrepaid, DATACARD_TYPE, true);
                }
                break;
            case ELECTRICITY_TYPE:
                rechargeType= ELECTRICITY_TYPE;
                setOtherVisibilityGone();
                recharge_layout_electricity.setVisibility(View.VISIBLE);
//                elecricityOperatorArrayList=dataBaseHelper.getAllOperatorNames(ELECTRICITY_TYPE);
                if(elecricityOperatorArrayList.size()==0){
                    callOperator(categoryElectricity, ELECTRICITY_TYPE, true);
                }
                break;
            case LANDLINE_TYPE:
                rechargeType= LANDLINE_TYPE;
                setOtherVisibilityGone();
                recharge_layout_landline.setVisibility(View.VISIBLE);
//                landlineOperatorArrayList=dataBaseHelper.getAllOperatorNames(LANDLINE_TYPE);
                if(landlineOperatorArrayList.size()==0){
                    callOperator(categoryLandline, LANDLINE_TYPE, true);
                }
                break;
            case GAS_TYPE:
                rechargeType= GAS_TYPE;
                setOtherVisibilityGone();
                recharge_layout_gas.setVisibility(View.VISIBLE);
//                gasOperatorArrayList=dataBaseHelper.getAllOperatorNames(GAS_TYPE);
                if(gasOperatorArrayList.size()==0){
                    callOperator(categoryGas, GAS_TYPE, true);
                }
                break;
        }
    }

    private void setOtherVisibilityGone() {
        recharge_layout_mobile.setVisibility(View.GONE);
        recharge_layout_dth.setVisibility(View.GONE);
        recharge_layout_datacard.setVisibility(View.GONE);
        recharge_layout_electricity.setVisibility(View.GONE);
        recharge_layout_landline.setVisibility(View.GONE);
        recharge_layout_gas.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_postpaid_operator:
                postpaidOperatorSelector(position, spinner_postpaid_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_water_operator:
                waterOperatorSelector(position, spinner_water_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_datacard_operator:
                datacardOperatorSelector(position,spinner_datacard_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_electricity_operator:
                electricityOperatorSelector(position,spinner_electricity_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_landline_operator:
                landlineOperatorSelector(position,spinner_landline_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_gas_operator:
                gasOperatorSelector(position,spinner_gas_operator.getSelectedItem().toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void postpaidOperatorSelector(int position, String selectedValue) {
        mobileOperator=selectedValue;
        operatorPostpaidSelector =postpaidOperatorArrayList.get(position);
        userIdInput.setHint(operatorPostpaidSelector.displayname);
//        Toast.makeText(context,mobileOperator+"\nregex="+ operatorPostpaidSelector.regex,Toast.LENGTH_SHORT).show();
    }

    private void waterOperatorSelector(int position, String selectedValue) {
        waterOperator =selectedValue;
        operatorWaterSelector=waterOperatorArrayList.get(position);
        userIdInputWater.setHint(operatorWaterSelector.displayname);
        if(operatorWaterSelector.ad1code){
            userIdInput2Water.setVisibility(View.VISIBLE);
            userIdInput2Water.setHint(operatorWaterSelector.ad1_name);
        }else {
            userIdInput2Water.setVisibility(View.GONE);
        }

    }

    private void datacardOperatorSelector(int position, String selectedValue) {
        datacardOperator=selectedValue;
        operatorDatacardSelector=datacardOperatorArrayList.get(position);
        userIdInputDatacard.setHint(operatorDatacardSelector.displayname);
        if(operatorDatacardSelector.ad1code){
            userIdInput2Datacard.setVisibility(View.VISIBLE);
            userIdInput2Datacard.setHint(operatorDatacardSelector.ad1_name);
        }else {
            userIdInput2Datacard.setVisibility(View.GONE);
        }
    }

    private void electricityOperatorSelector(int position, String selectedValue) {
        electricityOperator=selectedValue;
        operatorElectricitySelector=elecricityOperatorArrayList.get(position);
        userIdInputElectricity.setHint(operatorElectricitySelector.displayname);
        if(operatorElectricitySelector.ad1code){
            userIdInput2Electricity.setVisibility(View.VISIBLE);
            userIdInput2Electricity.setHint(operatorElectricitySelector.ad1_name);
        }else {
            userIdInput2Electricity.setVisibility(View.GONE);
        }
    }

    private void landlineOperatorSelector(int position, String selectedValue) {
        landlineOperator=selectedValue;
        operatorLandlineSelector=landlineOperatorArrayList.get(position);
        userIdInputLandline.setHint(operatorLandlineSelector.displayname);
        if(operatorLandlineSelector.ad1code){
            userIdInput2Landline.setVisibility(View.VISIBLE);
            userIdInput2Landline.setHint(operatorLandlineSelector.ad1_name);
        }else {
            userIdInput2Landline.setVisibility(View.GONE);
        }
    }
    private void gasOperatorSelector(int position, String selectedValue) {
        gasOperator=selectedValue;
        operatorGasSelector=gasOperatorArrayList.get(position);
        userIdInputGas.setHint(operatorGasSelector.displayname);
        if(operatorGasSelector.ad1code){
            userIdInput2Gas.setVisibility(View.VISIBLE);
            userIdInput2Gas.setHint(operatorGasSelector.ad1_name);
        }else {
            userIdInput2Gas.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {

        super.onResume();
//        Toast.makeText(context,"Resume",Toast.LENGTH_SHORT).show();
    }


}

