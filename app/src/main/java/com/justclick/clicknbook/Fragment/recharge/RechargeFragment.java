package com.justclick.clicknbook.Fragment.recharge;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.google.android.material.textfield.TextInputLayout;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.JioAmountData;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.model.RechargeDetailResponseModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.GetMobileOperatorModel;
import com.justclick.clicknbook.requestmodels.RechargeDetailModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.

 */
public class RechargeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int MOBILE_TYPE=1,DTH_TYPE=2,
            DATACARD_TYPE=3, ELECTRICITY_TYPE=4, LANDLINE_TYPE=5, GAS_TYPE=6;
    private Context context;
    private LoginModel loginModel;
    private View view;
    private TextView proceedBtn;
    private LinearLayout recharge_layout_mobile, recharge_layout_dth,
            recharge_layout_datacard, recharge_layout_electricity,
            recharge_layout_landline, recharge_layout_gas;
    private int rechargeType=MOBILE_TYPE;
    //    mobile_icon_black
    private Spinner spinner_mobile_operator, spinner_jio_amount;
    private EditText user_mobile_edt, mobile_amount_edt;
    private TextInputLayout amount_edt_parent;
    private ImageView prepaid_img, postpaid_img;
    private LinearLayout prepaid_lin, postpaid_lin;
    private View jio_spinner_view,view_spinner_landline_city,
            view_spinner_electricity_city;

    private TextInputLayout view_landline_account_edt,view_electricity_billing_unit_edt,
            view_electricity_processing_cycle_edt,view_gas_billGroup_edt;

    private String mobileOperator="";
    //    dth_icon
    private Spinner spinner_dth_operator;
    private EditText dth_amount_edt, dth_subscriberId_edt;

    private String dthOperator="";
    //    dataCard
    private Spinner spinner_datacard_operator;
    private EditText user_mobile_edt_datacard, amount_edt_datacard;
    private ImageView prepaid_img_datacard, postpaid_img_datacard;
    private LinearLayout prepaid_lin_datacard, postpaid_lin_datacard;

    private String datacardOperator="";
    //    Electricity
    private Spinner spinner_electricity_operator, spinner_electricity_city;
    private EditText electricity_amount_edt, electricity_account_edt,
            electricity_billing_unit_edt, electricity_processing_cycle_edt;

    private String electricityOperator="";
    //    landLine
    private Spinner spinner_landline_operator, spinner_landline_city;
    private EditText landline_amount_edt, landline_account_edt,
            landline_mobile_edt;
    private LinearLayout linear_jio_spinner;

    private String landlineOperator="";
    //    gas
    private Spinner spinner_gas_operator;
    private EditText gas_amount_edt, gas_account_edt,
            gas_billGroup_edt;
    private String gasOperator="";

    private ArrayList<OptModel.OptData> mobileOperatorArrayList, dthOperatorArrayList,
            datacardOperatorArrayList, elecricityOperatorArrayList,
            landlineOperatorArrayList, gasOperatorArrayList;
    private ArrayList<JioAmountData.JioData> jioAmountList;
    private static final int PREPAID=1,POSTPAID=2;
    private int mobileType=PREPAID, datacardType=PREPAID;
    private ToolBarTitleChangeListener titleChangeListener;
    private DataBaseHelper dataBaseHelper;
    private RechargeDetailModel rechargeDetailModel;

    public RechargeFragment()
    {
        // Required empty public constructor
    }

    public static RechargeFragment newInstance(Context context, int fragmentToShow) {

        Bundle args = new Bundle();
        args.putInt("FragmentToShow", fragmentToShow);
        RechargeFragment fragment = new RechargeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        mobileOperatorArrayList =new ArrayList<>();
        dthOperatorArrayList =new ArrayList<>();
        datacardOperatorArrayList =new ArrayList<>();
        elecricityOperatorArrayList =new ArrayList<>();
        landlineOperatorArrayList =new ArrayList<>();
        gasOperatorArrayList =new ArrayList<>();
        jioAmountList=new ArrayList<>();
        rechargeType=getArguments().getInt("FragmentToShow");
        dataBaseHelper=new DataBaseHelper(context);
        rechargeDetailModel=new RechargeDetailModel();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_mobile, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.rechargeFragmentTitle));
        initializeViews();

        fragmentToShow(rechargeType);

        if(dataBaseHelper.getAllOperatorNames(MOBILE_TYPE).size()>0) {
            mobileOperatorArrayList.clear();
            mobileOperatorArrayList.addAll(dataBaseHelper.getAllOperatorNames(MOBILE_TYPE));
        }
        if(mobileOperatorArrayList.size()==0) {
            callOperator("Mobile", MOBILE_TYPE, false);
        }else {
            spinner_mobile_operator.setAdapter(setSpinnerAdapter(mobileOperatorArrayList));
            spinner_dth_operator.setAdapter(setSpinnerAdapter(dthOperatorArrayList));
            spinner_datacard_operator.setAdapter(setSpinnerAdapter(datacardOperatorArrayList));
            spinner_electricity_operator.setAdapter(setSpinnerAdapter(elecricityOperatorArrayList));
            spinner_landline_operator.setAdapter(setSpinnerAdapter(landlineOperatorArrayList));
            spinner_gas_operator.setAdapter(setSpinnerAdapter(gasOperatorArrayList));
        }

        if(jioAmountList.size()==0) {
            callJioAmount();
        }else {
            spinner_jio_amount.setAdapter(setJioAmountAdapter(jioAmountList));
        }


        return view;
    }

    private void setTitle(int rechargeType) {
        switch (rechargeType){
            case MOBILE_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.mobileFragmentTitle));
                break;
            case DTH_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.dthFragmentTitle));
                break;
            case DATACARD_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.datacardFragmentTitle));
                break;
            case ELECTRICITY_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.electricityFragmentTitle));
                break;
            case LANDLINE_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.landline_type));
                break;
            case GAS_TYPE:
                titleChangeListener.onToolBarTitleChange(getString(R.string.gasFragmentTitle));
                break;
        }
    }

    private void initializeViews() {

        recharge_layout_mobile  = (LinearLayout) view.findViewById(R.id.recharge_layout_postpaid);
        recharge_layout_dth  = (LinearLayout) view.findViewById(R.id.recharge_layout_water);
        recharge_layout_datacard = (LinearLayout) view.findViewById(R.id.recharge_layout_datacard);
        recharge_layout_electricity = (LinearLayout) view.findViewById(R.id.recharge_layout_electricity);
        recharge_layout_landline = (LinearLayout) view.findViewById(R.id.recharge_layout_landline);
        recharge_layout_gas = (LinearLayout) view.findViewById(R.id.recharge_layout_gas);
        proceedBtn  = (TextView) view.findViewById(R.id.proceedBtn);
        proceedBtn.setOnClickListener(this);
        view.findViewById(R.id.rel_mobile).setOnClickListener(this);

        initializeMobileView();
        initializeDTHView();
        initializeDataCardView();
        initializeElectricityView();
        initializeLandLineView();
        initializeGasView();
    }

    private void initializeMobileView() {

        spinner_mobile_operator = (Spinner) view.findViewById(R.id.spinner_mobile_operator);
        spinner_jio_amount = (Spinner) view.findViewById(R.id.spinner_jio_amount);
        jio_spinner_view =  view.findViewById(R.id.jio_spinner_view);
        spinner_mobile_operator.setOnItemSelectedListener(this);
        user_mobile_edt = (EditText) view.findViewById(R.id.user_mobile_edt);
        mobile_amount_edt = (EditText) view.findViewById(R.id.amount_edt);
        amount_edt_parent = (TextInputLayout) view.findViewById(R.id.amount_edt_parent);
        prepaid_img = (ImageView) view.findViewById(R.id.prepaid_img);
        postpaid_img = (ImageView) view.findViewById(R.id.postpaid_img);
        prepaid_lin= (LinearLayout) view.findViewById(R.id.prepaid_lin);
        postpaid_lin= (LinearLayout) view.findViewById(R.id.postpaid_lin);
        linear_jio_spinner= (LinearLayout) view.findViewById(R.id.linear_jio_spinner);

        if(mobileType==PREPAID){
            prepaid_img.setImageResource(R.drawable.radio_check_red);
            postpaid_img.setImageResource(R.drawable.radio_uncheck_red);
        }else {
            prepaid_img.setImageResource(R.drawable.radio_uncheck_red);
            postpaid_img.setImageResource(R.drawable.radio_check_red);
        }
        if(mobileOperator.equalsIgnoreCase("JIO")){

        }
        prepaid_lin.setOnClickListener(this);
        postpaid_lin.setOnClickListener(this);

    }
    private void initializeDTHView() {
        spinner_dth_operator = (Spinner) view.findViewById(R.id.spinner_dth_operator);
        spinner_dth_operator.setOnItemSelectedListener(this);
        dth_amount_edt = (EditText) view.findViewById(R.id.dth_amount_edt);
        dth_subscriberId_edt = (EditText) view.findViewById(R.id.dth_subscriberId_edt);
    }
    private void initializeDataCardView() {
        spinner_datacard_operator = (Spinner) view.findViewById(R.id.spinner_datacard_operator);
        spinner_datacard_operator.setOnItemSelectedListener(this);
        user_mobile_edt_datacard = (EditText) view.findViewById(R.id.user_mobile_edt_datacard);
        amount_edt_datacard = (EditText) view.findViewById(R.id.amount_edt_datacard);
        prepaid_img_datacard = (ImageView) view.findViewById(R.id.prepaid_img_datacard);
        postpaid_img_datacard = (ImageView) view.findViewById(R.id.postpaid_img_datacard);
        prepaid_lin_datacard= (LinearLayout) view.findViewById(R.id.prepaid_lin_datacard);
        postpaid_lin_datacard= (LinearLayout) view.findViewById(R.id.postpaid_lin_datacard);

        if(datacardType==PREPAID){
            prepaid_img_datacard.setImageResource(R.drawable.radio_check_red);
            postpaid_img_datacard.setImageResource(R.drawable.radio_uncheck_red);
        }else {
            prepaid_img_datacard.setImageResource(R.drawable.radio_uncheck_red);
            postpaid_img_datacard.setImageResource(R.drawable.radio_check_red);
        }
        prepaid_lin_datacard.setOnClickListener(this);
        postpaid_lin_datacard.setOnClickListener(this);
    }
    private void initializeElectricityView() {
        spinner_electricity_operator = (Spinner) view.findViewById(R.id.spinner_electricity_operator);
        spinner_electricity_city = (Spinner) view.findViewById(R.id.spinner_electricity_city);
        spinner_electricity_operator.setOnItemSelectedListener(this);
        electricity_amount_edt = (EditText) view.findViewById(R.id.electricity_amount_edt);
        electricity_account_edt = (EditText) view.findViewById(R.id.electricity_account_edt);
        view_landline_account_edt= (TextInputLayout) view.findViewById(R.id.view_landline_account_edt);
        view_spinner_landline_city=view.findViewById(R.id.view_spinner_landline_city);
        view_gas_billGroup_edt= (TextInputLayout) view.findViewById(R.id.view_gas_billGroup_edt);
        view_electricity_billing_unit_edt= (TextInputLayout) view.findViewById(R.id.view_electricity_billing_unit_edt);
        view_electricity_processing_cycle_edt= (TextInputLayout) view.findViewById(R.id.view_electricity_processing_cycle_edt);
        view_spinner_electricity_city=view.findViewById(R.id.view_spinner_electricity_city);
        electricity_billing_unit_edt = (EditText) view.findViewById(R.id.electricity_billing_unit_edt);
        electricity_processing_cycle_edt = (EditText) view.findViewById(R.id.electricity_processing_cycle_edt);
        String[] arr={"(select city)", "AGRA", "AHMEDABAD", "BHIWANDI", "SURAT"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, arr);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        spinner_electricity_city.setAdapter(adapter);
    }
    private void initializeLandLineView() {
        spinner_landline_operator = (Spinner) view.findViewById(R.id.spinner_landline_operator);
        spinner_landline_operator.setOnItemSelectedListener(this);
        spinner_landline_city = (Spinner) view.findViewById(R.id.spinner_landline_city);
        landline_amount_edt = (EditText) view.findViewById(R.id.landline_amount_edt);
        landline_account_edt = (EditText) view.findViewById(R.id.landline_account_edt);
        landline_mobile_edt = (EditText) view.findViewById(R.id.landline_mobile_edt);

        String[] arr={"(select city)","LLC","LLI"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, arr);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        spinner_landline_city.setAdapter(adapter);
    }
    private void initializeGasView() {
        spinner_gas_operator = (Spinner) view.findViewById(R.id.spinner_gas_operator);
        spinner_gas_operator.setOnItemSelectedListener(this);
        gas_amount_edt = (EditText) view.findViewById(R.id.gas_amount_edt);
        gas_account_edt = (EditText) view.findViewById(R.id.gas_account_edt);
        gas_billGroup_edt = (EditText) view.findViewById(R.id.gas_billGroup_edt);
    }

    private void callOperator(String type, final int rechargeType, final boolean once) {
        GetMobileOperatorModel operatorModel=new GetMobileOperatorModel();
        if(loginModel!=null && loginModel.Data!=null){
            operatorModel.MerchantId=loginModel.Data.MerchantID;
            operatorModel.RechargeType=type;
        }
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);

        Call<OptModel> call = apiService.getOptPost(ApiConstants.GetOptName,operatorModel);
        call.request().headers("");
        call.enqueue(new Callback<OptModel>() {
            @Override
            public void onResponse(Call<OptModel>call, Response<OptModel> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                try{
                    switch (rechargeType){
                        case MOBILE_TYPE:
                            mobileOperatorArrayList.clear();
                            mobileOperatorArrayList.addAll(response.body().Data);
//                          dataBaseHelper.insertOperatorNames(MOBILE_TYPE, response.body().Data);
                            spinner_mobile_operator.setAdapter(setSpinnerAdapter(mobileOperatorArrayList));

                            /* code for store values in database
                            if(!once) {
                                getOperatorIfNotInDatabase(DTH_TYPE, false);
                            }*/
                            if(!once) {
                                callOperator("DTH", DTH_TYPE, false);
                            }
                            break;
                        case DTH_TYPE:
                            dthOperatorArrayList.clear();
                            dthOperatorArrayList.addAll(response.body().Data);
//                            dataBaseHelper.insertOperatorNames(DTH_TYPE, response.body().Data);
                            spinner_dth_operator.setAdapter(setSpinnerAdapter(dthOperatorArrayList));
                            /* if(!once) {
                                getOperatorIfNotInDatabase(DATACARD_TYPE, false);
                            }*/
                            if(!once) {
                                callOperator("Datacard", DATACARD_TYPE, false);
                            }
                            break;
                        case DATACARD_TYPE:
                            datacardOperatorArrayList.clear();
                            datacardOperatorArrayList.addAll(response.body().Data);
//                            dataBaseHelper.insertOperatorNames(DATACARD_TYPE, response.body().Data);
                            spinner_datacard_operator.setAdapter(setSpinnerAdapter(datacardOperatorArrayList));

                            /*if(!once) {
                                getOperatorIfNotInDatabase(ELECTRICITY_TYPE, false);
                            }*/
                            if(!once) {
                                callOperator("Electricity", ELECTRICITY_TYPE, false);
                            }
                            break;
                        case ELECTRICITY_TYPE:
                            elecricityOperatorArrayList.clear();
                            elecricityOperatorArrayList.addAll(response.body().Data);
//                            dataBaseHelper.insertOperatorNames(ELECTRICITY_TYPE, response.body().Data);
                            spinner_electricity_operator.setAdapter(setSpinnerAdapter(elecricityOperatorArrayList));

                            /*if(!once) {
                                getOperatorIfNotInDatabase(LANDLINE_TYPE, false);
                            }*/
                            if(!once) {
                                callOperator("Landline", LANDLINE_TYPE, false);
                            }
                            break;
                        case LANDLINE_TYPE:
                            landlineOperatorArrayList.clear();
                            landlineOperatorArrayList.addAll(response.body().Data);
//                            dataBaseHelper.insertOperatorNames(LANDLINE_TYPE, response.body().Data);
                            spinner_landline_operator.setAdapter(setSpinnerAdapter(landlineOperatorArrayList));

                            /*if(!once) {
                                getOperatorIfNotInDatabase(GAS_TYPE, false);
                            }*/
                            if(!once) {
                                callOperator("Gas", GAS_TYPE, false);
                            }
                            break;
                        case GAS_TYPE:
                            gasOperatorArrayList.clear();
                            gasOperatorArrayList.addAll(response.body().Data);
//                            dataBaseHelper.insertOperatorNames(GAS_TYPE, response.body().Data);
                            spinner_gas_operator.setAdapter(setSpinnerAdapter(gasOperatorArrayList));
                            break;
                    }

                }catch (Exception e){

                }

//                adapter=new DrawerSpinnerAdapter(getActivity(),response.body());
//                spinner_mobile_operator.setAdapter(adapter);
//                    List<Movie> movies = response.body().getResults();
            }

            @Override
            public void onFailure(Call<OptModel>call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOperatorIfNotInDatabase(int optType, boolean once) {
        if(optType==DTH_TYPE && dataBaseHelper.getAllOperatorNames(DTH_TYPE).size()==0){
            callOperator("DTH", DTH_TYPE, false);
        }else if(optType==DATACARD_TYPE && dataBaseHelper.getAllOperatorNames(DATACARD_TYPE).size()==0){
            callOperator("Datacard", DATACARD_TYPE, false);
        }else if(optType==ELECTRICITY_TYPE && dataBaseHelper.getAllOperatorNames(ELECTRICITY_TYPE).size()==0){
            callOperator("Electricity", ELECTRICITY_TYPE, false);
        }else if(optType==LANDLINE_TYPE && dataBaseHelper.getAllOperatorNames(LANDLINE_TYPE).size()==0){
            callOperator("Landline", LANDLINE_TYPE, false);
        }else if(optType==GAS_TYPE && dataBaseHelper.getAllOperatorNames(GAS_TYPE).size()==0){
            callOperator("Gas", GAS_TYPE, false);
        }else {
//            callOperator("Mobile", MOBILE_TYPE, true);
        }
    }

    private void callJioAmount() {
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<JioAmountData> call = apiService.getJioAmount(ApiConstants.GETJIOAMOUNT);
        call.enqueue(new Callback<JioAmountData>() {
            @Override
            public void onResponse(Call<JioAmountData>call, Response<JioAmountData> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                try{
                    if(response!=null && response.body().Data.size()>0){
                        jioAmountList.clear();
                        jioAmountList.addAll(response.body().Data);
                        spinner_jio_amount.setAdapter(setJioAmountAdapter(jioAmountList));
                    }
                }catch (Exception e){

                }

//                adapter=new DrawerSpinnerAdapter(getActivity(),response.body());
//                spinner_mobile_operator.setAdapter(adapter);
//                    List<Movie> movies = response.body().getResults();
            }

            @Override
            public void onFailure(Call<JioAmountData>call, Throwable t) {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayAdapter<String> setSpinnerAdapter(ArrayList<OptModel.OptData> data) {
        String[] arr=new String[data.size()];
        for(int i=0;i<data.size();i++){
            arr[i]=data.get(i).OptName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, arr);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }
    private ArrayAdapter<String> setJioAmountAdapter(ArrayList<JioAmountData.JioData> data) {
        String[] arr=new String[data.size()];
        for(int i=0;i<data.size();i++){
            arr[i]=data.get(i).Price;
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
                            proceedRecharge(rechargeType);
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
            case R.id.prepaid_lin:
                prepaid_img.setImageResource(R.drawable.radio_check_red);
                postpaid_img.setImageResource(R.drawable.radio_uncheck_red);
                mobileType=PREPAID;
                break;
            case R.id.postpaid_lin:
                prepaid_img.setImageResource(R.drawable.radio_uncheck_red);
                postpaid_img.setImageResource(R.drawable.radio_check_red);
                mobileType=POSTPAID;
                break;
            case R.id.prepaid_lin_datacard:
                prepaid_img_datacard.setImageResource(R.drawable.radio_check_red);
                postpaid_img_datacard.setImageResource(R.drawable.radio_uncheck_red);
                datacardType=PREPAID;
                break;
            case R.id.postpaid_lin_datacard:
                prepaid_img_datacard.setImageResource(R.drawable.radio_uncheck_red);
                postpaid_img_datacard.setImageResource(R.drawable.radio_check_red);
                datacardType=POSTPAID;
                break;
            case R.id.rel_mobile:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;
        }
    }

    private void getcommision(final Bundle bundle) {

        rechargeDetailModel.MobileNumber=bundle.getString("IdNumber");
        rechargeDetailModel.RechargeType=bundle.getString("Type");
        rechargeDetailModel.ConnectionType=bundle.getString("MobileType");
        rechargeDetailModel.OptName=bundle.getString("Operator");
        rechargeDetailModel.Amount=bundle.getString("RechargeAmount");
        rechargeDetailModel.OptionalPara1="test";
        rechargeDetailModel.OptionalPara2="test";
        rechargeDetailModel.OptionalPara3="test";
        rechargeDetailModel.DoneCardUser=loginModel.Data.DoneCardUser;
        rechargeDetailModel.DeviceId=Common.getDeviceId(context);
        rechargeDetailModel.LoginSessionId=EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
//        rechargeDetailModel.Password=bundle.getString("Operator");

            ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
            Call<RechargeDetailResponseModel> call = apiService.RechargeCommissionPost(ApiConstants.RechargeDetails , rechargeDetailModel);
            call.enqueue(new Callback<RechargeDetailResponseModel>() {
                @Override
                public void onResponse(Call<RechargeDetailResponseModel> call, Response<RechargeDetailResponseModel> response) {

                    try {
                        if(response.body().StatusCode.equalsIgnoreCase("0")){
                            bundle.putString("Commision", (response.body().Data.Commision));
                            bundle.putString("MarkUp",(response.body().Data.MarkUp));
                            RechargeConfirmationFragment cf = new RechargeConfirmationFragment();
                            cf.setArguments(bundle);
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(cf);
                        }else {
                            Toast.makeText(context,response.body().Status,Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                    }

                }

                @Override
                public void onFailure(Call<RechargeDetailResponseModel> call, Throwable t) {
                    Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                }


            });

    }

    private boolean validate(int rechargeType) {
        switch (rechargeType){
            case MOBILE_TYPE:
                return isMobileValid();
            case DTH_TYPE:
                return isDthValid();
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

    private boolean isMobileValid() {
        if(mobileOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;

        }else if(!mobileOperator.equalsIgnoreCase("JIO") &&
                (!Common.isdecimalvalid(mobile_amount_edt.getText().toString()))){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!mobileOperator.equalsIgnoreCase("JIO") &&
                mobile_amount_edt.getText().toString().trim().length()>0 &&
                Float.parseFloat(mobile_amount_edt.getText().toString().trim())==0){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(mobileOperator.equalsIgnoreCase("JIO") &&
                spinner_jio_amount.getSelectedItemPosition()==0 &&
                spinner_jio_amount.getSelectedItem().toString().trim().toLowerCase().contains("select")){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_mobile_edt.getText().toString().trim().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
            return false;
        }else  if (mobileOperator.trim().equalsIgnoreCase("IDEA") && mobileType == POSTPAID) {
            if (Float.parseFloat(mobile_amount_edt.getText().toString()) < 10 ||
                    Float.parseFloat(mobile_amount_edt.getText().toString()) > 10800)

            {
                Toast.makeText(context, "Please enter amount between 10 to 10800", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if ((mobileOperator.trim().equalsIgnoreCase("Tata TeleServices") ||
                mobileOperator.trim().equalsIgnoreCase("AIRTEL")) && mobileType == POSTPAID) {
            if (Float.parseFloat(mobile_amount_edt.getText().toString()) < 10 ||
                    Float.parseFloat(mobile_amount_edt.getText().toString()) > 10000)
            {
                Toast.makeText(context, "Please enter amount between 10 to 10000", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if ((mobileOperator.trim().equalsIgnoreCase("Reliance GSM") ||
                mobileOperator.trim().equalsIgnoreCase("Reliance CDMA")) && mobileType == POSTPAID) {
            if (Float.parseFloat(mobile_amount_edt.getText().toString()) < 50 ||
                    Float.parseFloat(mobile_amount_edt.getText().toString()) > 10000)
            {
                Toast.makeText(context, "Please enter amount between 50 to 10000", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    private boolean isDthValid() {
        if(dthOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if((!Common.isdecimalvalid(dth_amount_edt.getText().toString())) ||
                Float.parseFloat(dth_amount_edt.getText().toString().trim())==0){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(dth_subscriberId_edt.getText().toString().trim().length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_subscriberId,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isDatacardValid() {
        if(datacardOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if((!Common.isdecimalvalid(amount_edt_datacard.getText().toString())) ||
                Float.parseFloat(amount_edt_datacard.getText().toString().trim())==0){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_mobile_edt_datacard.getText().toString().trim().length()==0 ||
                user_mobile_edt_datacard.getText().toString().trim().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isElectricityValid() {
        if(electricityOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if((!Common.isdecimalvalid(electricity_amount_edt.getText().toString())) ||
                Float.parseFloat(electricity_amount_edt.getText().toString().trim())==0){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(electricity_account_edt.getText().toString().trim().length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_account_id,Toast.LENGTH_SHORT).show();
            return false;
        }else if (electricityOperator.equalsIgnoreCase("MSEDC Limited")) {
            if(electricity_billing_unit_edt.getText().toString().trim().length()<4){
                Toast.makeText(context,R.string.empty_and_invalid_billing,Toast.LENGTH_SHORT).show();
                return false;
            }else if(electricity_processing_cycle_edt.getText().toString().trim().length()<2){
                Toast.makeText(context,R.string.empty_and_invalid_processing_cycle,Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if (electricityOperator.equalsIgnoreCase("Reliance Energy")) {
            if(electricity_processing_cycle_edt.getText().toString().trim().length()<2){
                Toast.makeText(context,R.string.empty_and_invalid_cycle,Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if (electricityOperator.equalsIgnoreCase("Torrent Power")) {
            if(spinner_electricity_city.getSelectedItemPosition()==0){
                Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    private boolean isLandlineValid() {
        if(landlineOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(((!Common.isdecimalvalid(landline_amount_edt.getText().toString()))||
                Float.parseFloat(landline_amount_edt.getText().toString().trim())==0)){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(landline_mobile_edt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_landLine_number,Toast.LENGTH_SHORT).show();
            return false;
        }else if (landlineOperator.equals("AIRTEL Landline")) {
            if (Float.parseFloat(landline_amount_edt.getText().toString()) < 10 ||
                    Float.parseFloat(landline_amount_edt.getText().toString()) > 10000)
            {
                Toast.makeText(context,"Please enter amount between 10 to 10000",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if (landlineOperator.equals("MTNL Delhi Landline")) {
            if (landline_account_edt.getText().toString().trim().length()==0)
            {
                Toast.makeText(context,R.string.empty_and_invalid_account_id,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if (landlineOperator.equals("BSNL Landline")) {
            if (landline_account_edt.getText().toString().trim().length()==0)
            {
                Toast.makeText(context,R.string.empty_and_invalid_account_id,
                        Toast.LENGTH_SHORT).show();
                return false;
            }else if (spinner_landline_city.getSelectedItemPosition()==0)
            {
                Toast.makeText(context,R.string.empty_and_invalid_city,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    private boolean isGasValid() {
        if(gasOperator.length()==0){
            Toast.makeText(context,R.string.select_operator,Toast.LENGTH_SHORT).show();
            return false;
        }else if(((!Common.isdecimalvalid(gas_amount_edt.getText().toString()))||
                Float.parseFloat(gas_amount_edt.getText().toString().trim())==0)){
            Toast.makeText(context,R.string.empty_and_invalid_amount,Toast.LENGTH_SHORT).show();
            return false;
        }else if(gas_account_edt.getText().toString().trim().length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_customer_id,Toast.LENGTH_SHORT).show();
            return false;
        }else if(gasOperator.equalsIgnoreCase("Mahanagar Gas Limited")){
            if(gas_billGroup_edt.getText().toString().trim().length()==0){
                Toast.makeText(context,R.string.empty_and_invalid_billing_group,Toast.LENGTH_SHORT).show();
                return false;
            }else if(!(gas_account_edt.getText().toString().trim().length()==12)) {
                Toast.makeText(context, R.string.empty_and_invalid_customer_id, Toast.LENGTH_SHORT).show();
                return false;
                }}else if(gasOperator.equalsIgnoreCase("Indraprast Gas")||
                gasOperator.equalsIgnoreCase("ADANI GAS")){
                if(!(gas_account_edt.getText().toString().trim().length()==10)) {
                    Toast.makeText(context, R.string.empty_and_invalid_customer_id, Toast.LENGTH_SHORT).show();
                    return false;
                }  }else if(gasOperator.equalsIgnoreCase("Gujarat Gas company Limited")){
                    if(gas_account_edt.getText().toString().trim().length()==0) {
                        Toast.makeText(context, R.string.empty_and_invalid_customer_id, Toast.LENGTH_SHORT).show();
                        return false;
                    }}
        return true;
    }

    private void proceedRecharge(int rechargeType) {
        Bundle bundle=new Bundle();
        bundle.putInt("RechargeType",rechargeType);
        switch (rechargeType){
            case MOBILE_TYPE:
                bundle.putString("Type","MOBILE");
                bundle.putString("MobileType",mobileType==PREPAID?"Prepaid":"Postpaid");
                bundle.putString("IdNumber",user_mobile_edt.getText().toString());
                bundle.putString("Operator", spinner_mobile_operator.getSelectedItem().toString());
                bundle.putString("RechargeAmount",mobileOperator.equalsIgnoreCase("JIO") ?
                        spinner_jio_amount.getSelectedItem().toString():
                        mobile_amount_edt.getText().toString().trim());
                if(mobileOperator.equalsIgnoreCase("JIO") && jioAmountList.size()>0) {
                    bundle.putString("Value4", jioAmountList.get(spinner_jio_amount.getSelectedItemPosition()).Id);
                }else {
                    bundle.putString("Value4","");
                }

                break;
            case DTH_TYPE:
                bundle.putString("Type","DTH");
                bundle.putString("IdNumber",dth_subscriberId_edt.getText().toString());
                bundle.putString("Operator", spinner_dth_operator.getSelectedItem().toString());
                bundle.putString("RechargeAmount", dth_amount_edt.getText().toString().trim());
                break;
            case DATACARD_TYPE:
                bundle.putString("Type","DATACARD");
                bundle.putString("DatacardType",datacardType==PREPAID?"Prepaid":"Postpaid");
                bundle.putString("IdNumber",user_mobile_edt_datacard.getText().toString());
                bundle.putString("Operator", spinner_datacard_operator.getSelectedItem().toString());
                bundle.putString("RechargeAmount", amount_edt_datacard.getText().toString().trim());
                break;
            case ELECTRICITY_TYPE:
                bundle.putString("Type","ELECTRICITY");
                bundle.putString("IdNumber",electricity_account_edt.getText().toString());
                bundle.putString("Operator", spinner_electricity_operator.getSelectedItem().toString());
                bundle.putString("RechargeAmount", electricity_amount_edt.getText().toString().trim());
                bundle.putString("Value4", "");
                bundle.putString("Value5", "");

                if(electricityOperator.equalsIgnoreCase("MSEDC Limited"))
                {
                    bundle.putString("Value4", electricity_billing_unit_edt.getText().toString().trim());
                    bundle.putString("Value5", electricity_processing_cycle_edt.getText().toString().trim());
                }
                else if(electricityOperator.equalsIgnoreCase("Reliance Energy"))
                {
                    bundle.putString("Value4", electricity_processing_cycle_edt.getText().toString().trim());
                    bundle.putString("Value5", "");
                }
                else if (electricityOperator.equalsIgnoreCase("Torrent Power"))
                {
                    bundle.putString("Value4", spinner_electricity_city.getSelectedItem().toString().trim());
                    bundle.putString("Value5", "");
                }
                else
                {

                }

                break;
            case LANDLINE_TYPE:
                bundle.putString("Type","LANDLINE");
                bundle.putString("IdNumber",landline_mobile_edt.getText().toString());
                bundle.putString("Operator", spinner_landline_operator.getSelectedItem().toString());
                bundle.putString("RechargeAmount", landline_amount_edt.getText().toString().trim());
                bundle.putString("Value4", "");
                bundle.putString("Value5", "");

                if (landlineOperator.equalsIgnoreCase("MTNL Delhi Landline")) {
                    bundle.putString("Value4",landline_account_edt.getText().toString());
                    bundle.putString("Value5","");
                }else if (landlineOperator.equals("BSNL Landline")) {
                    bundle.putString("Value4",landline_account_edt.getText().toString());
                    bundle.putString("Value5",spinner_landline_city.getSelectedItem().toString());
                }
                break;
            case GAS_TYPE:
                bundle.putString("Type","GAS");
                bundle.putString("IdNumber",gas_account_edt.getText().toString());
                bundle.putString("Operator", gasOperator);
                bundle.putString("RechargeAmount", gas_amount_edt.getText().toString().trim());
                bundle.putString("Value4","");
                if (gasOperator.equalsIgnoreCase("Mahanagar Gas Limited")) {
                    bundle.putString("Value4",gas_billGroup_edt.getText().toString().trim());
                }
                break;
        }

        getcommision(bundle);
    }

    public void fragmentToShow(int type){
        switch (type){
            case MOBILE_TYPE:
                rechargeType = MOBILE_TYPE;
                setOtherVisibilityGone();
                recharge_layout_mobile.setVisibility(View.VISIBLE);
//                mobileOperatorArrayList=dataBaseHelper.getAllOperatorNames(MOBILE_TYPE);
                if(mobileOperatorArrayList.size()==0){
                    callOperator("Mobile", MOBILE_TYPE, true);
                }
                break;
            case DTH_TYPE:
                rechargeType = DTH_TYPE;
                setOtherVisibilityGone();
                recharge_layout_dth.setVisibility(View.VISIBLE);
//                dthOperatorArrayList=dataBaseHelper.getAllOperatorNames(DTH_TYPE);
                if(dthOperatorArrayList.size()==0){
                    callOperator("DTH", DTH_TYPE, true);
                }
                break;
            case DATACARD_TYPE:
                rechargeType = DATACARD_TYPE;
                setOtherVisibilityGone();
                recharge_layout_datacard.setVisibility(View.VISIBLE);
//                datacardOperatorArrayList=dataBaseHelper.getAllOperatorNames(DATACARD_TYPE);
                if(datacardOperatorArrayList.size()==0){
                    callOperator("Datacard", DATACARD_TYPE, true);
                }
                break;
            case ELECTRICITY_TYPE:
                rechargeType= ELECTRICITY_TYPE;
                setOtherVisibilityGone();
                recharge_layout_electricity.setVisibility(View.VISIBLE);
//                elecricityOperatorArrayList=dataBaseHelper.getAllOperatorNames(ELECTRICITY_TYPE);
                if(elecricityOperatorArrayList.size()==0){
                    callOperator("Electricity", ELECTRICITY_TYPE, true);
                }
                break;
            case LANDLINE_TYPE:
                rechargeType= LANDLINE_TYPE;
                setOtherVisibilityGone();
                recharge_layout_landline.setVisibility(View.VISIBLE);
//                landlineOperatorArrayList=dataBaseHelper.getAllOperatorNames(LANDLINE_TYPE);
                if(landlineOperatorArrayList.size()==0){
                    callOperator("Landline", LANDLINE_TYPE, true);
                }
                break;
            case GAS_TYPE:
                rechargeType= GAS_TYPE;
                setOtherVisibilityGone();
                recharge_layout_gas.setVisibility(View.VISIBLE);
//                gasOperatorArrayList=dataBaseHelper.getAllOperatorNames(GAS_TYPE);
                if(gasOperatorArrayList.size()==0){
                    callOperator("Gas", GAS_TYPE, true);
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

    private void showViews(int rechargeType) {
        switch (rechargeType){
            case MOBILE_TYPE:
                showViews(rechargeType);
                break;
            case DTH_TYPE:
                rechargeType =DTH_TYPE;
                break;
            case DATACARD_TYPE:
                rechargeType =DATACARD_TYPE;
                break;
            case ELECTRICITY_TYPE:
                rechargeType=ELECTRICITY_TYPE;
                break;
            case LANDLINE_TYPE:
                rechargeType=LANDLINE_TYPE;
                break;
            case GAS_TYPE:
                rechargeType=GAS_TYPE;
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_mobile_operator:
                mobileOperatorSelector(position,spinner_mobile_operator.getSelectedItem().toString());
                break;
            case R.id.spinner_dth_operator:
                dthOperatorSelector(position,spinner_dth_operator.getSelectedItem().toString());
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
    private void mobileOperatorSelector(int position, String selectedValue) {
        mobileOperator=selectedValue;
        if(selectedValue.equalsIgnoreCase("JIO")){
            linear_jio_spinner.setVisibility(View.VISIBLE);
            jio_spinner_view.setVisibility(View.VISIBLE);
            mobile_amount_edt.setVisibility(View.INVISIBLE);
            amount_edt_parent.setVisibility(View.INVISIBLE);
            postpaid_lin.setEnabled(false);
            postpaid_lin.setAlpha(0.3f);
            mobileType=PREPAID;
            prepaid_img.setImageResource(R.drawable.radio_check_red);
            postpaid_img.setImageResource(R.drawable.radio_uncheck_red);
        }else {
            linear_jio_spinner.setVisibility(View.INVISIBLE);
            jio_spinner_view.setVisibility(View.INVISIBLE);
            mobile_amount_edt.setVisibility(View.VISIBLE);
            amount_edt_parent.setVisibility(View.VISIBLE);
            postpaid_lin.setEnabled(true);
            postpaid_lin.setAlpha(1f);
        }
    }

    private void dthOperatorSelector(int position, String selectedValue) {
        dthOperator=selectedValue;
    }

    private void datacardOperatorSelector(int position, String selectedValue) {
        datacardOperator=selectedValue;
    }

    private void electricityOperatorSelector(int position, String selectedValue) {
        electricityOperator=selectedValue;
        if(electricityOperator.equalsIgnoreCase("MSEDC Limited"))
        {
            view_electricity_billing_unit_edt.setVisibility(View.VISIBLE);
            electricity_billing_unit_edt.setVisibility(View.VISIBLE);
//            electricity_processing_cycle_edt.setHint(R.string.enter_processing_cycle);
            view_electricity_processing_cycle_edt.setHint(getResources().getString(R.string.enter_processing_cycle));
            electricity_processing_cycle_edt.setVisibility(View.VISIBLE);
            view_electricity_processing_cycle_edt.setVisibility(View.VISIBLE);
            view_spinner_electricity_city.setVisibility(View.GONE);
            spinner_electricity_city.setVisibility(View.GONE);
        }
        else if(electricityOperator.equalsIgnoreCase("Reliance Energy"))
        {
            view_electricity_billing_unit_edt.setVisibility(View.GONE);
            electricity_billing_unit_edt.setVisibility(View.GONE);
//            electricity_processing_cycle_edt.setHint(R.string.enter_cycle_no);
            view_electricity_processing_cycle_edt.setHint(getResources().getString(R.string.enter_cycle_no));
            electricity_processing_cycle_edt.setVisibility(View.VISIBLE);
            view_electricity_processing_cycle_edt.setVisibility(View.VISIBLE);
            spinner_electricity_city.setVisibility(View.GONE);
            view_spinner_electricity_city.setVisibility(View.GONE);
        }
        else if (electricityOperator.equalsIgnoreCase("Torrent Power"))
        { view_spinner_electricity_city.setVisibility(View.VISIBLE);
            spinner_electricity_city.setVisibility(View.VISIBLE);
            electricity_billing_unit_edt.setVisibility(View.GONE);
            view_electricity_billing_unit_edt.setVisibility(View.GONE);
            view_electricity_processing_cycle_edt.setVisibility(View.GONE);
            electricity_processing_cycle_edt.setVisibility(View.GONE);
        }
        else
        {
            view_spinner_electricity_city.setVisibility(View.GONE);
            view_electricity_processing_cycle_edt.setVisibility(View.GONE);
            view_electricity_billing_unit_edt.setVisibility(View.GONE);
            spinner_electricity_city.setVisibility(View.GONE);
            electricity_billing_unit_edt.setVisibility(View.GONE);
            electricity_processing_cycle_edt.setVisibility(View.GONE);
        }

    }

    private void landlineOperatorSelector(int position, String selectedValue) {
        landlineOperator=selectedValue;
        if(landlineOperator.equalsIgnoreCase("AIRTEL Landline"))
        {
            landline_amount_edt.setVisibility(View.VISIBLE);
            landline_mobile_edt.setVisibility(View.VISIBLE);
            view_landline_account_edt.setVisibility(View.GONE);
            landline_account_edt.setVisibility(View.GONE);
            view_spinner_landline_city.setVisibility(View.GONE);
            spinner_landline_city.setVisibility(View.GONE);

        }
        else if(landlineOperator.equalsIgnoreCase("BSNL Landline"))
        {
            landline_amount_edt.setVisibility(View.VISIBLE);
            landline_mobile_edt.setVisibility(View.VISIBLE);
            view_landline_account_edt.setVisibility(View.VISIBLE);
            landline_account_edt.setVisibility(View.VISIBLE);
            spinner_landline_city.setVisibility(View.VISIBLE);
            view_spinner_landline_city.setVisibility(View.VISIBLE);
            spinner_landline_city.setVisibility(View.VISIBLE);
        }
        else
        {
            landline_amount_edt.setVisibility(View.VISIBLE);
            landline_mobile_edt.setVisibility(View.VISIBLE);
            landline_account_edt.setVisibility(View.VISIBLE);
            view_landline_account_edt.setVisibility(View.VISIBLE);
            spinner_landline_city.setVisibility(View.GONE);
            spinner_landline_city.setVisibility(View.GONE);
            view_spinner_landline_city.setVisibility(View.GONE);
        }
    }
    private void gasOperatorSelector(int position, String selectedValue) {
        gasOperator=selectedValue;

        if(gasOperator.equalsIgnoreCase("Mahanagar Gas Limited"))
        {
            gas_amount_edt.setVisibility(View.VISIBLE);
            gas_account_edt.setVisibility(View.VISIBLE);
            gas_billGroup_edt.setVisibility(View.VISIBLE);
            view_gas_billGroup_edt.setVisibility(View.VISIBLE);
        }
        else
        {
            gas_amount_edt.setVisibility(View.VISIBLE);
            gas_account_edt.setVisibility(View.VISIBLE);
            view_gas_billGroup_edt.setVisibility(View.INVISIBLE);
            gas_billGroup_edt.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onResume() {

        super.onResume();
//        Toast.makeText(context,"Resume",Toast.LENGTH_SHORT).show();
    }


}

