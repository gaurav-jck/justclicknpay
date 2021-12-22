package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.ManageAccountRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class UpdateCreditFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private final int UPDATE_REQUEST=1;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    TextView AvlCrd,AvlBal,bookBalEdt,Add_crd,agent, date_tv, activeTv;
    String agentDoneCard, transactionDate, Active;
    Button submit;
    private ImageView add_img, reduce_img;
    private ImageView clearTv,cancel,radioButton1,radioButton2,radioButton3,radioButton4;
    private TextView senderNameTv, senderMobileTv, senderKycTv, addRecTv, limitTv,
            tv0,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,
            jctIdTv,otpEdt;
    private Dialog pinDialog;
    private String Pin="", TPin="";
    private View view_date_lin;
    private LinearLayout add_lin, reduce_lin;
    private Spinner spinner_transaction_type;
    private EditText add_reduce_credit_edt, remark_edt;
    private int AddCredit =1, ReduceCredit =2;
    private int creditType = AddCredit;
    private AgentDetails agentDetails;
    private RelativeLayout date_lin;
    private SimpleDateFormat dateToServerFormat;
    private int startDateDay, startDateMonth, startDateYear;
    private Calendar startDateCalendar;
    private LoginModel loginModel;
    private ManageAccountRequestModel model;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel = MyPreferences.getLoginData(new LoginModel(),context);
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
        View view = inflater.inflate(R.layout.fragment_agent_sales, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agentSalesDetailFragmentTitle));
        initializeViews(view);

        if(loginModel.Data.CreditFlag!=null && loginModel.Data.CreditFlag.equalsIgnoreCase("1")){
            reduce_lin.setVisibility(View.VISIBLE);
        }else {
            reduce_lin.setVisibility(View.GONE);
        }
        initializeDates();
        return view;
    }

    private void initializeViews(View view) {
        AvlCrd= (TextView) view.findViewById(R.id.et_avlCrd);
        AvlBal= (TextView) view.findViewById(R.id.et_avlBala);
        bookBalEdt= (TextView) view.findViewById(R.id.bookBalEdt);
        Add_crd= (TextView) view.findViewById(R.id.eT_addcrd);

        date_tv = (TextView) view.findViewById(R.id.address_tv);
        view_date_lin=  view.findViewById(R.id.view_date_lin);
        date_lin= (RelativeLayout) view.findViewById(R.id.date_lin);
        date_lin.setOnClickListener(this);
        //  Red_Crd= (TextView) findViewById(R.Id.eT_redCrd);
        agent= (TextView) view.findViewById(R.id.name);
        activeTv= (TextView) view.findViewById(R.id.activeTv);
        add_reduce_credit_edt= (EditText) view.findViewById(R.id.add_reduce_credit_edt);
        spinner_transaction_type= (Spinner) view.findViewById(R.id.spinner_transaction_type);
        remark_edt= (EditText) view.findViewById(R.id.remark_edt);
        submit= (Button) view.findViewById(R.id.bt_submit);
        submit.setOnClickListener(this);

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });

        add_img = (ImageView) view.findViewById(R.id.add_img);
        reduce_img = (ImageView) view.findViewById(R.id.reduce_img);
        add_lin= (LinearLayout) view.findViewById(R.id.add_lin);
        reduce_lin= (LinearLayout) view.findViewById(R.id.reduce_lin);

        setRadioButtun();
        add_lin.setOnClickListener(this);
        reduce_lin.setOnClickListener(this);

        spinner_transaction_type.setOnItemSelectedListener(this);

        try {
            agentDetails= (AgentDetails) getArguments().getSerializable("AgentDetail");
            agentDoneCard= getArguments().getString("AgentDoneCard");
            Active= getArguments().getString("Active");
//            agent.setText(agentDetails.Data.AgencyName.replace("(","\n("));
            agent.setText(agentDetails.Data.AgencyName+"\n("+agentDoneCard+")");
            AvlBal.setText(getString(R.string.account_balance)+"  "+agentDetails.Data.ActualBalance);
            bookBalEdt.setText(getString(R.string.book_balance)+"  "+agentDetails.Data.BookBalance);
            AvlCrd.setText(getString(R.string.available_credit)+"  "+agentDetails.Data.Credit);
            ((EditText)view.findViewById(R.id.distributor_edt)).setText(agentDetails.Data.Distributor);
            ((EditText)view.findViewById(R.id.sales_person_edt)).setText(agentDetails.Data.SalesPerson);

            if(Active.equalsIgnoreCase("true")){
                activeTv.setText("Active");
                activeTv.setTextColor(getResources().getColor(R.color.green));
            }else {
                activeTv.setText("Not Active");
                activeTv.setTextColor(getResources().getColor(R.color.app_red_color_light));
            }

            //set transaction type adapter

            String type[]=new String[agentDetails.GetTransactionTypes.size()];
            for(int i=0; i< agentDetails.GetTransactionTypes.size(); i++){
                type[i]=agentDetails.GetTransactionTypes.get(i).TransactionType;
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<>(context,R.layout.agent_details_spinner_item_dropdown,R.id.operator_tv, type);
            adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
            spinner_transaction_type.setAdapter(adapter);

        }catch (Exception e){

        }
    }

    private void setRadioButtun() {
        if(creditType == AddCredit){
            add_img.setImageResource(R.drawable.radio_button_checked);
            reduce_img.setImageResource(R.drawable.radio_button_unchecked);
        }else {
            add_img.setImageResource(R.drawable.radio_button_unchecked);
            reduce_img.setImageResource(R.drawable.radio_button_checked);
        }
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();

        //default start Date
        startDateCalendar = Calendar.getInstance();
        startDateDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth = startDateCalendar.get(Calendar.MONTH);
        startDateYear = startDateCalendar.get(Calendar.YEAR);

        transactionDate=dateToServerFormat.format(startDateCalendar.getTime());

        //set default date
        date_tv.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String transactionType=spinner_transaction_type.getSelectedItem().toString();
        if (transactionType.equalsIgnoreCase("Air Booking")||
                transactionType.equalsIgnoreCase("Booking Using PG")||
                transactionType.equalsIgnoreCase("DMT Booking")||
                transactionType.equalsIgnoreCase("Offline Air Booking")||
                transactionType.equalsIgnoreCase("Rail Booking")||
                transactionType.equalsIgnoreCase("RBL Booking")||
                transactionType.equalsIgnoreCase("Hotel Booking")||
                transactionType.toLowerCase().contains("booking")||
                transactionType.equalsIgnoreCase("Recharge")){
            creditType=ReduceCredit;
            setRadioButtun();
            add_lin.setAlpha(0.5f);
            reduce_lin.setAlpha(1f);
            add_reduce_credit_edt.setHint(R.string.reduce_credit);
            add_lin.setEnabled(false);
        }else if (transactionType.equalsIgnoreCase("Air Refund")||
                transactionType.equalsIgnoreCase("Amex Upload")||
                transactionType.equalsIgnoreCase("Atom Upload")||
                transactionType.equalsIgnoreCase("Incentive")||
                transactionType.equalsIgnoreCase("DMT Refund")||
                transactionType.equalsIgnoreCase("Mobile Refund")||
                transactionType.equalsIgnoreCase("Hotel Refund")||
                transactionType.equalsIgnoreCase("RBL Refund")||
                transactionType.equalsIgnoreCase("Top-Up Refund")||
                transactionType.equalsIgnoreCase("PG Upload")||
                transactionType.toLowerCase().contains("refund")||
                transactionType.equalsIgnoreCase("Rail Refund")) {
            creditType=AddCredit;
            reduce_lin.setAlpha(0.5f);
            add_lin.setAlpha(1f);
            setRadioButtun();
            add_reduce_credit_edt.setHint(R.string.add_credit);
            reduce_lin.setEnabled(false);
        }else if (spinner_transaction_type.getSelectedItem().toString().equalsIgnoreCase("Credit")) {
            creditType=AddCredit;
            setRadioButtun();
            add_lin.setAlpha(1f);
            reduce_lin.setAlpha(1f);
            add_reduce_credit_edt.setHint(R.string.add_credit);
            add_lin.setEnabled(true);
            reduce_lin.setEnabled(true);
            date_lin.setVisibility(View.GONE);
            view_date_lin.setVisibility(View.VISIBLE);
        }else {
            creditType=AddCredit;
            setRadioButtun();
            add_lin.setAlpha(1f);
            reduce_lin.setAlpha(1f);
            add_reduce_credit_edt.setHint(R.string.add_credit);
            add_lin.setEnabled(true);
            reduce_lin.setEnabled(true);
            date_lin.setVisibility(View.GONE);
            view_date_lin.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    private boolean validate(String amount,String remark) {
        try {
            if((!Common.isdecimalvalid(amount))|| Float.parseFloat(amount)==0)
            {
                Toast.makeText(context, R.string.empty_and_invalid_credit, Toast.LENGTH_LONG).show();
                return false;
            }else if(Float.parseFloat(amount)>1000000)
            {
                Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_LONG).show();
                return false;
            }else if(spinner_transaction_type.getSelectedItem().toString().equalsIgnoreCase("Credit")&&
                    creditType==ReduceCredit &&
                    (Float.parseFloat(amount)>
                            Float.parseFloat(agentDetails.Data.Credit))) {
                Toast.makeText(context, R.string.invalid_reduce_amount, Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(remark.length()==0){
                Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }catch (Exception NullPointerException){
            return false;
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_submit:
                try{
                    Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    if(Active.equalsIgnoreCase("false")){
                        Toast.makeText(context, R.string.agent_inactive_message, Toast.LENGTH_LONG).show();
                        break;
                    }
                    else if(validate(add_reduce_credit_edt.getText().toString().trim(),
                            remark_edt.getText().toString())){
                        model=new ManageAccountRequestModel();
                        model.DoneCardUser= loginModel.Data.DoneCardUser;
                        model.AgentDoneCardUser=agentDoneCard;
                        model.DeviceId=Common.getDeviceId(context);
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.SalesPerson=agentDetails.Data.SalesPerson;
                        model.Distributor=agentDetails.Data.Distributor;
                        model.Sid=agentDetails.Data.Sid;
                        model.TransactionType=spinner_transaction_type.getSelectedItem().toString();

                        if(spinner_transaction_type.getSelectedItem().toString().equalsIgnoreCase("Credit")){
                            model.CreditValidateDate=transactionDate;
                        }else {
                            model.CreditValidateDate="";
                        }

                        if(creditType==AddCredit){
                            model.AddCredit=add_reduce_credit_edt.getText().toString().trim();
                            model.ReduceCredit="0";
                        }else {
                            model.ReduceCredit=add_reduce_credit_edt.getText().toString().trim();
                            model.AddCredit="0";
                        }

                        if(remark_edt.getText().toString()!=null){
                            model.Remarks=remark_edt.getText().toString();
                        }else{model.Remarks="";}

                        String a="";

                        if(Common.checkInternetConnection(context)) {
                            openOtpDialog();
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){

                }

                break;


            case R.id.add_lin:
                add_img.setImageResource(R.drawable.radio_button_checked);
                reduce_img.setImageResource(R.drawable.radio_button_unchecked);
                add_reduce_credit_edt.setHint(R.string.add_credit);
                creditType = AddCredit;
                break;
            case R.id.reduce_lin:
                add_img.setImageResource(R.drawable.radio_button_unchecked);
                reduce_img.setImageResource(R.drawable.radio_button_checked);
                add_reduce_credit_edt.setHint(R.string.reduce_credit);
                creditType = ReduceCredit;
                break;

            case R.id.date_lin:
//                try {
//                    openDatePicker();
//                }catch (Exception e){
//
//                }
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
//        model.Pin="";
        try {
            model.TPin=EncryptionDecryptionClass.computeHash(Pin);
        } catch (Exception e) {
            model.TPin="";
            e.printStackTrace();
        }
        addOrReduceCredit(model);
        Pin="";
    }


    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateCalendar.set(year, monthOfYear, dayOfMonth);
                        transactionDate=dateToServerFormat.format(startDateCalendar.getTime());
                        startDateDay=dayOfMonth;
                        startDateMonth=monthOfYear;
                        startDateYear=year;
                        date_tv.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));
                    }

                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    private void openOtpDialog() {
        pinDialog = new Dialog(context, R.style.Theme_Design_Light);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.pin_otp);
        final Window window= pinDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        jctIdTv= (TextView) pinDialog.findViewById(R.id.jctIdTv);
        otpEdt= (TextView) pinDialog.findViewById(R.id.otpEdt);
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

    private void addOrReduceCredit(ManageAccountRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.ManageAccounts, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, UPDATE_REQUEST);
                        }else {

                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case UPDATE_REQUEST:
                    CommonResponseModel commonResponseModel = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    hideCustomDialog();
                    if(commonResponseModel!=null){
                        if(commonResponseModel.StatusCode.equalsIgnoreCase("0")){
//                            Toast.makeText(context,commonResponseModel.Data.Message, Toast.LENGTH_LONG).show();
                            Common.showResponsePopUp(context, commonResponseModel.Data.Message);
                            pinDialog.dismiss();
                            getFragmentManager().popBackStack();
                        }else {
                            Toast.makeText(context,commonResponseModel.Status, Toast.LENGTH_LONG).show();
//                            Common.showResponsePopUp(context, commonResponseModel.Status);
                            pinDialog.dismiss();
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

    @Override
    public void onDetach() {
        super.onDetach();
        if(pinDialog!=null) {
            pinDialog.dismiss();
        }
    }
}