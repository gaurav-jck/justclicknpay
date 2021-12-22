package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.DepositReportAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.DepositReportListModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.CreditReportListRequestModel;
import com.justclick.clicknbook.requestmodels.DeleteCreditRequestModel;
import com.justclick.clicknbook.requestmodels.SelfDepositRequestModel;
import com.justclick.clicknbook.requestmodels.UpdateDepositRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class AdminDepositReportFragment extends Fragment implements
        View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int SORT_BY_AGENCY_NAME=1, SORT_BY_AMOUNT =2;
    private final int UPDATE_DEPOSIT=1, DELETE_DEPOSIT=2, CALL_AGENT=3, SELF_DEPOSIT=4;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentBackPressListener fragmentBackPressListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String valueToSend, startDateToSend, endDateToSend, agentDoneCardUser="";
    private ArrayList<DepositReportListModel.DepositReportData> depositReportDataArrayList;
    private DepositReportAdapter depositeReportAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv;
    private Spinner spinner_bank_name;
    private RelativeLayout filter_image_rel;
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private EditText agent_search_edt;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private CreditReportListRequestModel reportListRequestModel;
    private LoginModel loginModel;
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;
    private ListView agencyList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            fragmentBackPressListener= (FragmentBackPressListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        depositReportDataArrayList =new ArrayList<>();
        reportListRequestModel=new CreditReportListRequestModel();
        agentNameModel=new AgentNameModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);

        initializeDates();
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();
        dateFormat = Common.getToFromDateFormat();
        dayFormat = Common.getShortDayFormat();

        //default start Date
        startDateCalendar=Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        //default end Date
        endDateCalendar = Calendar.getInstance();
        endDateDay=endDateCalendar.get(Calendar.DAY_OF_MONTH);
        endDateMonth=endDateCalendar.get(Calendar.MONTH);
        endDateYear=endDateCalendar.get(Calendar.YEAR);

        startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
        endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
    }

    private void setDates() {
        //set default date
        startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                dayFormat.format(endDateCalendar.getTime())+" "+
                dateFormat.format(endDateCalendar.getTime())
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_agent_credit_report, container, false);
        recyclerView =  view.findViewById(R.id.recyclerView);
        startDateTv =  view.findViewById(R.id.startDateTv);

        agent_search_edt = view.findViewById(R.id.agent_search_edt);
        agencyList =  view.findViewById(R.id.agencyList);

        titleChangeListener.onToolBarTitleChange(getString(R.string.agentDepositReportFragmentTitle));
        fragmentBackPressListener.onFragmentBackPress(agencyList);

        view.findViewById(R.id.lin_selfRequest).setOnClickListener(this);
        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);
//        view.findViewById(R.id.lin_sortList).setOnClickListener(this);

        //initialize date values
        setDates();

        depositeReportAdapter=new DepositReportAdapter(context, new  DepositReportAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<DepositReportListModel.DepositReportData> list, int position) {
                switch (view.getId()){
                    case R.id.update_tv:
                        try {
                            openCreditUpdateDialog(list.get(position).AgencyName,list.get(position).Amount,
                                    list.get(position).Sid, list.get(position).AvailableCredit);
                        }catch (Exception e){

                        }

                        break;
                    case R.id.delete_tv:
                        if(Common.checkInternetConnection(context)) {
                            openDeleteConfirmationDialog("Confirm Delete Request","Please confirm," +
                                    " you want to delete this request.","Cancel","Delete", list.get(position).Sid);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }

                        break;
                }
            }
        }, depositReportDataArrayList);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(depositeReportAdapter);

        if(depositReportDataArrayList!=null && depositReportDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            reportListRequestModel.EndPage =pageEnd+"";
            reportListRequestModel.StartPage =pageStart+"";
            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            reportListRequestModel.AgentDoneCardUser=agentDoneCardUser;
            reportListRequestModel.DeviceId=Common.getDeviceId(context);
            reportListRequestModel.LoginSessionId=EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            reportListRequestModel.FromDate=startDateToSend+"";
            reportListRequestModel.ToDate=endDateToSend+"";

            if(Common.checkInternetConnection(context)) {
                callAgent(reportListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            depositeReportAdapter.notifyDataSetChanged();
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        agent_search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Common.checkInternetConnection(context)) {
                    if(s.length()>=2) {
                        String term = s.toString();

                        AgentNameRequestModel model=new AgentNameRequestModel();
                        model.AgencyName=term;
                        model.MerchantID=loginModel.Data.MerchantID;
                        model.RefAgency=loginModel.Data.RefAgency;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        call_agent(model,agencyList);
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        agent_search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agent_search_edt.setText("");
                agencyList.setVisibility(View.GONE);
                agentDoneCardUser="";
            }
        });

        agencyList.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agencyList.setVisibility(View.GONE);
//                        agencyListRel.setVisibility(View.GONE);
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
//                        lin_other_container.setVisibility(View.VISIBLE);
                        String agencyName=autocompleteAdapter.getItem(position).AgencyName;
                        agentDoneCardUser=agencyName.substring(agencyName.indexOf("(")+1,agencyName.indexOf(")"));
                        agent_search_edt.setText(agencyName);
                        agent_search_edt.setSelection(agent_search_edt.getText().toString().length());

                        pageStart=1;
                        pageEnd=10;
                        reportListRequestModel.EndPage =pageEnd+"";
                        reportListRequestModel.StartPage =pageStart+"";
                        reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                        reportListRequestModel.AgentDoneCardUser=agentDoneCardUser;
                        reportListRequestModel.DeviceId=Common.getDeviceId(context);
                        reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        reportListRequestModel.FromDate=startDateToSend+"";
                        reportListRequestModel.ToDate=endDateToSend+"";

                        if(Common.checkInternetConnection(context)) {
                            depositReportDataArrayList.clear();
                            depositeReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel, SHOW_PROGRESS);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return view;
    }

    private void openCreditUpdateDialog(String agencyName, String amount, final String sid, final String availableCredit) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.deposit_update_dialog);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText amount_edt=  dialog.findViewById(R.id.amount_edt);
        final EditText reduce_credit_edt=  dialog.findViewById(R.id.reduce_credit_edt);
        final EditText bank_ledger_reference_edt=  dialog.findViewById(R.id.bank_ledger_reference_edt);
        final EditText bank_narration_edt=  dialog.findViewById(R.id.bank_narration_edt);
        final EditText remark_edt=  dialog.findViewById(R.id.remark_edt);
        final Spinner spinner_submit_type=  dialog.findViewById(R.id.spinner_submit_type);
        final TextView agency_name_tv=  dialog.findViewById(R.id.agency_name_tv);
        final TextView amount_requested_tv=  dialog.findViewById(R.id.amount_requested_tv);
        final CheckBox auto_reduce_check=  dialog.findViewById(R.id.auto_reduce_check);
        final Spinner spinner_bank_name=  dialog.findViewById(R.id.spinner_bank_name);
        spinner_bank_name.setAdapter(setSpinnerAdapter(loginModel.Data.BankNames.split("#")));
        final View view_bank= dialog.findViewById(R.id.view_bank);
        final String[] bankName = {""};

        agency_name_tv.setText(agencyName.toUpperCase());
        amount_requested_tv.setText(amount);
        amount_edt.setText(amount);
        auto_reduce_check.setChecked(true);
        reduce_credit_edt.setText(availableCredit);
        reduce_credit_edt.setEnabled(false);

        spinner_bank_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankName[0] =spinner_bank_name.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_submit_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==3 || i==2){
                    spinner_bank_name.setVisibility(View.VISIBLE);
                    view_bank.setVisibility(View.VISIBLE);
                    bankName[0] =spinner_bank_name.getSelectedItem().toString();
                }else{
                    spinner_bank_name.setVisibility(View.GONE);
                    view_bank.setVisibility(View.GONE);
                    bankName[0] ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        auto_reduce_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    reduce_credit_edt.setText(availableCredit);
                    reduce_credit_edt.setEnabled(false);
                }else {
                    reduce_credit_edt.setEnabled(true);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, new String[]{"-Select-","Cash","Cheque","OnlineTransfer"});
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        spinner_submit_type.setAdapter(adapter);


        dialog.findViewById(R.id.mainLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputFromDialog(dialog);
            }
        });

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputFromDialog(dialog);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputFromDialog(dialog);
                Common.hideSoftKeyboard((NavigationDrawerActivity) context);
                if(Common.checkInternetConnection(context)) {
                    if(validateUpdateInputs(spinner_submit_type.getSelectedItem().toString(),
                        amount_edt.getText().toString().trim(),
                            reduce_credit_edt.getText().toString().trim(),
                        bank_ledger_reference_edt.getText().toString().trim(),
                        bank_narration_edt.getText().toString().trim(),
                    remark_edt.getText().toString().trim(),
                    auto_reduce_check.isChecked(),availableCredit)) {
                    UpdateDepositRequestModel model=new UpdateDepositRequestModel();
                    model.DepositAmtType=spinner_submit_type.getSelectedItem().toString();
                    model.DepositAmt=amount_requested_tv.getText().toString().trim();
                    model.AmountCredit=amount_edt.getText().toString().trim();
                    model.ReduceCredit=reduce_credit_edt.getText().toString().trim();
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.BankName=bankName[0];
                    model.BankLedgerRef=bank_ledger_reference_edt.getText().toString().trim();
                    model.BankNarration=bank_narration_edt.getText().toString().trim();
                    model.ToDate=startDateToSend;
                    model.FromDate=endDateToSend;

                    if(remark_edt.getText().toString().trim()!=null){
                    model.Remarks =remark_edt.getText().toString().trim();}
                    else {model.Remarks ="";}

                    model.Sid=sid;

                    if(Common.checkInternetConnection(context)) {
                        updateDeposit(model);
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                    }

                    Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    dialog.dismiss();
                }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }
    private boolean validateUpdateInputs(String type, String amount, String reduceCredit,
                                         String ledger, String narration, String remark,
                                         boolean reduceChecked, String availableCredit) {
        if(type.contains("Select")){
            Toast.makeText(context, R.string.select_submit_type, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(amount)) {
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(reduceCredit)) {
            Toast.makeText(context, R.string.empty_and_invalid_reduce_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(reduceCredit)>Float.parseFloat(availableCredit)) {
            Toast.makeText(context, R.string.invalid_reduce_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(amount)>1000000){
            Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(ledger.length()==0){
            Toast.makeText(context, R.string.empty_and_invalid_ledger, Toast.LENGTH_SHORT).show();
            return false;
        }else if(narration.length()==0){
            Toast.makeText(context, R.string.empty_and_invalid_narration, Toast.LENGTH_SHORT).show();
            return false;
        } else if(remark.length()==0){
            Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void updateDeposit(UpdateDepositRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.UpdateDeposit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, UPDATE_DEPOSIT);
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
                case UPDATE_DEPOSIT:
                    CommonResponseModel commonResponse = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    if(commonResponse!=null){
                        if(commonResponse.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,commonResponse.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            depositReportDataArrayList.clear();
                            depositeReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DELETE_DEPOSIT:
                    CommonResponseModel commonResponse2 = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    if(commonResponse2!=null){
                        if(commonResponse2.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,commonResponse2.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            depositReportDataArrayList.clear();
                            depositeReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context,commonResponse2.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case CALL_AGENT:
                    DepositReportListModel depositReportListModel = new Gson().fromJson(response.string(), DepositReportListModel.class);
                    hideCustomDialog();
                    if(depositReportListModel.StatusCode.equalsIgnoreCase("0")){
                        depositReportDataArrayList.addAll(depositReportListModel.Data);
                        totalPageCount = Integer.parseInt(depositReportListModel.Data.get(0).TCount);
                        depositeReportAdapter.notifyDataSetChanged();
                    }else if(depositReportListModel.StatusCode.equalsIgnoreCase("2")){
//                            depositReportDataArrayList.clear();
//                            depositeReportAdapter.notifyDataSetChanged();
                        Toast.makeText(context,depositReportListModel.Status, Toast.LENGTH_LONG).show();
                    }else {
                        depositReportDataArrayList.clear();
                        depositeReportAdapter.notifyDataSetChanged();
                        Toast.makeText(context,depositReportListModel.Status, Toast.LENGTH_LONG).show();
                    }
                    break;

                case SELF_DEPOSIT:
                    CommonResponseModel commonResponseModel3 = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    hideCustomDialog();
                    if(commonResponseModel3!=null){
                        if(commonResponseModel3.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,commonResponseModel3.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            depositReportDataArrayList.clear();
                            depositeReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            Toast.makeText(context,commonResponseModel3.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDeposit(DeleteCreditRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.DeleteDeposit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, DELETE_DEPOSIT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void openDeleteConfirmationDialog(String title, String message,
                                              String cancel, String delete,final String sid) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog_layout);
        ((TextView)dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView)dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView)dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView)dialog.findViewById(R.id.submit_tv)).setText(delete);
        final EditText remark_edt= (EditText) dialog.findViewById(R.id.remark_edt);

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remark_edt.getText().toString().trim().length()>0){
                    DeleteCreditRequestModel model= new DeleteCreditRequestModel();
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.Sid=sid;
                    model.Remarks=remark_edt.getText().toString().trim();

                    if(Common.checkInternetConnection(context)){
                        deleteDeposit(model);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.empty_remark,Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    private void openFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.filter_dialog_layout);
        start_date_value_tv=  filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv=  filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv=  filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv=  filterDialog.findViewById(R.id.end_day_value_tv);
        filterDialog.findViewById(R.id.cancel_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.submit_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.startDateLinear).setOnClickListener(this);
        filterDialog.findViewById(R.id.endDateLinear).setOnClickListener(this);
        start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
        start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));
        end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
        end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));
        filterDialog.show();
    }

//    https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount = layoutManager.getItemCount();
//            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = depositeReportAdapter.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//            if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount <= totalPageCount
                    && dy>0) {

                if(!(pageEnd>totalItemCount)) {
                    pageStart = pageStart + 10;
                    pageEnd = pageEnd + 10;
                    reportListRequestModel.EndPage =pageEnd+"";
                    reportListRequestModel.StartPage =pageStart+"";
                    reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    reportListRequestModel.AgentDoneCardUser=agentDoneCardUser;
                    reportListRequestModel.DeviceId=Common.getDeviceId(context);
                    reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    reportListRequestModel.FromDate=startDateToSend+"";
                    reportListRequestModel.ToDate=endDateToSend+"";
                    callAgent( reportListRequestModel, NO_PROGRESS);
                }


            }
        }
    };

    public void callAgent(CreditReportListRequestModel reportListRequestModel, boolean progress) {
        if(Common.checkInternetConnection(context)){
            if(progress && !MyCustomDialog.isDialogShowing()){
                showCustomDialog();
            }
            new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.AdminDepositReport, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, CALL_AGENT);
                            }else {
                                hideCustomDialog();
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv:
                if(filterDialog!=null)
                    filterDialog.dismiss();
                break;
            case R.id.submit_tv:
                filterDialog.dismiss();
                depositReportDataArrayList.clear();
                depositeReportAdapter.notifyDataSetChanged();
                //set date to fragment

                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );
                pageStart=1;
                pageEnd=10;
                reportListRequestModel.EndPage =pageEnd+"";
                reportListRequestModel.StartPage =pageStart+"";
                reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                reportListRequestModel.AgentDoneCardUser=agentDoneCardUser;
                reportListRequestModel.DeviceId=Common.getDeviceId(context);
                reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                reportListRequestModel.FromDate=startDateToSend+"";
                reportListRequestModel.ToDate=endDateToSend+"";

                if(Common.checkInternetConnection(context)) {
                    callAgent(reportListRequestModel, SHOW_PROGRESS);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.startDateLinear:
                openStartDatePicker();
                break;
            case R.id.endDateLinear:
                openEndDatePicker();
                break;

            case R.id.lin_dateFilter:
                openFilterDialog();
                break;

            case R.id.lin_selfRequest:
                if(Common.checkInternetConnection(context)) {
                    openSelfDepositDialog();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;


        }
    }

    private void sortListBy(int sortBy) {
        switch (sortBy){
            case SORT_BY_AGENCY_NAME:
                Collections.sort(depositReportDataArrayList, new Comparator<DepositReportListModel.DepositReportData>() {
                    @Override
                    public int compare(DepositReportListModel.DepositReportData lhs, DepositReportListModel.DepositReportData rhs) {
                        return lhs.AgencyName.toUpperCase().compareTo(rhs.AgencyName.toUpperCase());
                    }
                });
                break;
            case SORT_BY_AMOUNT:
                Collections.sort(depositReportDataArrayList, new Comparator<DepositReportListModel.DepositReportData>() {
                    @Override
                    public int compare(DepositReportListModel.DepositReportData lhs, DepositReportListModel.DepositReportData rhs) {
                        return (int) (Float.parseFloat(lhs.Amount)-Float.parseFloat(rhs.Amount));
                    }
                });
                break;


        }
    }

    private void openSelfDepositDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.self_deposit_dialog);
        Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText amount_edt=  dialog.findViewById(R.id.amount_edt);
        final EditText agent_id_edt=  dialog.findViewById(R.id.agent_id_edt);
        final EditText mobile_edt=  dialog.findViewById(R.id.mobile_edt);
        final EditText receipt_no_edt= dialog.findViewById(R.id.receipt_no_edt);
        final EditText remark_edt=  dialog.findViewById(R.id.remark_edt);
        final Spinner spinner_submit_type=  dialog.findViewById(R.id.spinner_submit_type);
        final View view_bank= dialog.findViewById(R.id.view_bank);
        final String[] bankName = {""};

        spinner_bank_name= dialog.findViewById(R.id.spinner_bank_name);
        spinner_bank_name.setAdapter(setSpinnerAdapter(loginModel.Data.BankNames.split("#")));
//        mobile_edt.setText(loginModel.Data.Mobile);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, new String[]{"-Select-","Cash","Cheque","OnlineTransfer"});
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        spinner_submit_type.setAdapter(adapter);

        spinner_bank_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankName[0] =spinner_bank_name.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_submit_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==3 || i==2){
                    spinner_bank_name.setVisibility(View.VISIBLE);
                    view_bank.setVisibility(View.VISIBLE);
                    bankName[0] =spinner_bank_name.getSelectedItem().toString();
                }else{
                    spinner_bank_name.setVisibility(View.GONE);
                    view_bank.setVisibility(View.GONE);
                    bankName[0] ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ListView agencyList=  dialog.findViewById(R.id.agencyList);
        final LinearLayout lin_other_container=  dialog.findViewById(R.id.lin_other_container);
        final LinearLayout popup=  dialog.findViewById(R.id.popup);
//            final RelativeLayout agencyListRel= (RelativeLayout) dialog.findViewById(R.id.agencyListRel);

        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        });

        agencyList.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agencyList.setVisibility(View.GONE);
//                        agencyListRel.setVisibility(View.GONE);
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                        lin_other_container.setVisibility(View.VISIBLE);
                        String agencyName=autocompleteAdapter.getItem(position).AgencyName;
                        agent_id_edt.setText(agencyName);
                        agent_id_edt.setSelection(agent_id_edt.getText().toString().length());
//                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });

        agent_id_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Common.checkInternetConnection(context)) {
                    if(s.length()>=2) {
                        String term = s.toString();
                        AgentNameRequestModel model=new AgentNameRequestModel();
                        model.AgencyName=term;
                        model.MerchantID=loginModel.Data.MerchantID;
                        model.RefAgency=loginModel.Data.RefAgency;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        call_agent(model,agencyList);
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputFromDialog(dialog);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputFromDialog(dialog);
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                if(validateSelfDeposit(spinner_submit_type.getSelectedItem().toString(),
                        amount_edt.getText().toString().trim(),
                        agent_id_edt.getText().toString().trim(),
                        mobile_edt.getText().toString().trim(),
                        receipt_no_edt.getText().toString().trim(),
                        remark_edt.getText().toString().trim())) {
                    SelfDepositRequestModel model=new SelfDepositRequestModel();
                    model.TypeOfAmount=spinner_submit_type.getSelectedItem().toString();
                    model.TotalAmount=amount_edt.getText().toString().trim();
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.AgentDoneCardUser=agent_id_edt.getText().toString().trim().
                            substring(agent_id_edt.getText().toString().indexOf("(")+1,
                                    agent_id_edt.getText().toString().indexOf(")"));
                    model.ReceiptNo=receipt_no_edt.getText().toString().trim();
                    model.MobileNumber=mobile_edt.getText().toString().trim();
                    model.BankName=bankName[0];

                    if(remark_edt.getText().toString().trim()!=null){
                        model.Remarks =remark_edt.getText().toString().trim();}
                    else {model.Remarks ="";}

                    if(Common.checkInternetConnection(context)){
                        selfDeposit(model);
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();


    }

    private ArrayAdapter<String> setSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }


    public AgentNameModel call_agent(AgentNameRequestModel model, final ListView agencyList) {
//        agent.DATA.clear();
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
                try {
                    agentNameModel = response.body();
                    if(agentNameModel.StatusCode.equalsIgnoreCase("0")) {
                        agencyList.setVisibility(View.VISIBLE);
//                        agencyListRel.setVisibility(View.VISIBLE);
                        autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
                        agencyList.setAdapter(autocompleteAdapter);
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<AgentNameModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return agentNameModel;
    }

    private void selfDeposit(SelfDepositRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.DirectDeposit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, SELF_DEPOSIT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validateSelfDeposit(String type, String amount, String agentId, String mobile,
                                        String receipt,String remark) {
        if(type.contains("Select")){
            Toast.makeText(context, R.string.select_submit_type, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(amount)) {
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }if(Float.parseFloat(amount)>1000000){
            Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(agentId.length()==0){
            Toast.makeText(context, R.string.empty_and_invalid_agent_id, Toast.LENGTH_SHORT).show();
            return false;
        }else if(mobile.length()<10){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }else if(receipt.length()==0){
            Toast.makeText(context, R.string.empty_and_invalid_receipt, Toast.LENGTH_SHORT).show();
            return false;
        }else if(remark.length()==0){
            Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openStartDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateCalendar.set(year, monthOfYear, dayOfMonth);
                        if (startDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            startDateDay=dayOfMonth;
                            startDateMonth=monthOfYear;
                            startDateYear=year;
                            start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
                            start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));
                            startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
                        }
                    }
                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void openEndDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDateCalendar = Calendar.getInstance();
                        endDateCalendar.set(year, monthOfYear, dayOfMonth);

                        if (endDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            endDateDay=dayOfMonth;
                            endDateMonth=monthOfYear;
                            endDateYear=year;

                            end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
                            end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));

                            endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
                        }
                    }
                },endDateYear, endDateMonth, endDateDay);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void hideSoftInputFromDialog(Dialog dialog) {
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getCurrentFocus().getWindowToken(), 0);}
        catch (NullPointerException e){}
    }

}
