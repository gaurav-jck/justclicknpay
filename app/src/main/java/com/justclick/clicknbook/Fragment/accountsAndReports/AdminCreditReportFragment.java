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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.CreditReportAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.CreditReportListModel;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.CreditReportListRequestModel;
import com.justclick.clicknbook.requestmodels.DeleteCreditRequestModel;
import com.justclick.clicknbook.requestmodels.SelfCreditRequestModel;
import com.justclick.clicknbook.requestmodels.UpdateCreditRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class AdminCreditReportFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int UPDATE_CREDIT=1, DELETE_CREDIT=2, CALL_AGENT=3, SELF_CREDIT=4;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentBackPressListener fragmentBackPressListener;
    private RecyclerView recyclerView;
    private String expiryDateToSend, startDateToSend, endDateToSend,agentDoneCardUser;
    private ArrayList<CreditReportListModel.CreditReportData> creditReportDataArrayList;
    private CreditReportAdapter creditReportAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv;
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar, expireDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
                endDateDay, endDateMonth, endDateYear,
            expireDateDay, expireDateMonth, expireDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private CreditReportListRequestModel reportListRequestModel;
    private LoginModel loginModel;
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;
    private EditText agent_search_edt;
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
        creditReportDataArrayList =new ArrayList<>();
        reportListRequestModel=new CreditReportListRequestModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        agentNameModel=new AgentNameModel();
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

        //default expire Date
        expireDateCalendar = Calendar.getInstance();
        expireDateDay=expireDateCalendar.get(Calendar.DAY_OF_MONTH);
        expireDateMonth=expireDateCalendar.get(Calendar.MONTH);
        expireDateYear=expireDateCalendar.get(Calendar.YEAR);

        startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
        endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
        expiryDateToSend=dateToServerFormat.format(expireDateCalendar.getTime());



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
        recyclerView = view.findViewById(R.id.recyclerView);
        startDateTv =  view.findViewById(R.id.startDateTv);
        agent_search_edt = view.findViewById(R.id.agent_search_edt);
        agencyList = view.findViewById(R.id.agencyList);

        titleChangeListener.onToolBarTitleChange(getString(R.string.agentCreditReportFragmentTitle));
        fragmentBackPressListener.onFragmentBackPress(agencyList);

        view.findViewById(R.id.lin_selfRequest).setOnClickListener(this);
        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);

        //initialize date values
        setDates();

        creditReportAdapter=new CreditReportAdapter(context, new CreditReportAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<CreditReportListModel.CreditReportData> list, int position) {
                switch (view.getId()){
                    case R.id.update_tv:
                        try {
                            openCreditUpdateDialog(list.get(position).AgencyName,list.get(position).Amount,list.get(position).Sid);
                        }catch (Exception e){

                        }
//                        Toast.makeText(context, "Update clicked.",Toast.LENGTH_SHORT).show();
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
        }, creditReportDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(creditReportAdapter);

        if(creditReportDataArrayList!=null && creditReportDataArrayList.size()==0) {
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
        }else {
            creditReportAdapter.notifyDataSetChanged();
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
                        AgentNameRequestModel model=new AgentNameRequestModel();
                        model.AgencyName=s.toString();
                        model.MerchantID=loginModel.Data.MerchantID;
                        model.RefAgency=loginModel.Data.RefAgency;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        searchAgent(model,agencyList);
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
                            creditReportDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel, SHOW_PROGRESS);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return view;
    }

    private void openCreditUpdateDialog(String agencyName, String amount, final String sid) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.credit_update_dialog);
        Window window= dialog.getWindow();
        if(window!=null){
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);}

        final EditText account_credit_edt= (EditText) dialog.findViewById(R.id.account_credit_edt);
        final EditText remark_edt= (EditText) dialog.findViewById(R.id.remark_edt);
        final TextView expire_date_tv= (TextView) dialog.findViewById(R.id.expire_date_tv);

//        remark_edt.setFilters(new InputFilter[]{Common.setRemarksFilter(Common.blockRemarksCharacterSet)});

        ((TextView) dialog.findViewById(R.id.agency_name_tv)).setText(agencyName.replace("(","\n("));
        ((TextView) dialog.findViewById(R.id.amount_request_tv)).setText(amount);
        account_credit_edt.setText(amount);

        expire_date_tv.setText(new SimpleDateFormat("dd/MM/yyyy",Locale.US).format(expireDateCalendar.getTime()));

        dialog.findViewById(R.id.expire_date_lin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpireDate();
            }

            private void setExpireDate() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        R.style.DatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                expireDateCalendar.set(year, monthOfYear, dayOfMonth);
                                if (expireDateCalendar.before(Calendar.getInstance())) {
                                    Toast.makeText(context, "Can't select date before current date", Toast.LENGTH_SHORT).show();
                                }else {
//                                    expireDateCalendar.set(year, monthOfYear, dayOfMonth);
                                    expiryDateToSend=dateToServerFormat.format(expireDateCalendar.getTime());

                                    expireDateDay=dayOfMonth;
                                    expireDateMonth=monthOfYear;
                                    expireDateYear=year;

                                    expire_date_tv.setText(new SimpleDateFormat("dd/MM/yyyy",Locale.US).format(expireDateCalendar.getTime()));
                                }

                            }

                        },expireDateYear, expireDateMonth, expireDateDay);

                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP && datePickerDialog.getWindow()!=null) {
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

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
                if(validateUpdateInputs(account_credit_edt.getText().toString().trim(),remark_edt.getText().toString()))
                {
                    UpdateCreditRequestModel model=new UpdateCreditRequestModel();
                    model.CreditNeeded=account_credit_edt.getText().toString().trim();
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.ExpiryDate=expiryDateToSend;
                    model.Sid = sid;
                    if(remark_edt.getText().toString()!=null){
                        model.Remarks=remark_edt.getText().toString();
                    }else{model.Remarks="";}

                    if(Common.checkInternetConnection(context)) {
                        updateCredit(model);
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                    }

                    Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void hideSoftInputFromDialog(Dialog dialog) {
        try{
        InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getCurrentFocus().getWindowToken(), 0);}
        catch (NullPointerException e){}
    }

    private boolean validateUpdateInputs(String amount,String remark) {
        if(!Common.isdecimalvalid(amount)) {
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(amount)>1000000){
                Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                return false;
        }  else if(remark.length()==0){
            Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateCredit(UpdateCreditRequestModel requestModel) {
        showCustomDialog();
        new NetworkCall().callMobileService(requestModel, ApiConstants.UpdateCredit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, UPDATE_CREDIT);
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
                case UPDATE_CREDIT:
                    CommonResponseModel rblCommonResponse = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    if(rblCommonResponse!=null){
                        if(rblCommonResponse.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,rblCommonResponse.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            creditReportDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context,rblCommonResponse.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DELETE_CREDIT:
                    CommonResponseModel rblCommonResponse2 = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    if(rblCommonResponse2!=null){
                        if(rblCommonResponse2.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,rblCommonResponse2.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            creditReportDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context,rblCommonResponse2.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case CALL_AGENT:
                    CreditReportListModel creditReportListModel = new Gson().fromJson(response.string(), CreditReportListModel.class);
                    hideCustomDialog();
                    if(creditReportListModel!=null){
                        if(creditReportListModel.StatusCode.equalsIgnoreCase("0")){
                            creditReportDataArrayList.addAll(creditReportListModel.Data);
                            creditReportAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(creditReportListModel.Data.get(0).TCount);
                        }else if(creditReportListModel.StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.clear();
//                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,creditReportListModel.Status, Toast.LENGTH_LONG).show();
                        }else {
                            creditReportDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,creditReportListModel.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case SELF_CREDIT:
                    CommonResponseModel commonResponseModel3 = new Gson().fromJson(response.string(), CommonResponseModel.class);
                    if(commonResponseModel3!=null){
                        if(commonResponseModel3.StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,commonResponseModel3.Data.Message, Toast.LENGTH_LONG).show();
                            pageStart=1;
                            pageEnd=10;
                            reportListRequestModel.StartPage=pageStart+"";
                            reportListRequestModel.EndPage=pageEnd+"";
                            creditReportDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel,SHOW_PROGRESS);
                        }else {
                            hideCustomDialog();
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

    private void deleteCredit(DeleteCreditRequestModel requestModel) {
        showCustomDialog();
        new NetworkCall().callMobileService(requestModel, ApiConstants.DeleteCredit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, DELETE_CREDIT);
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

        ((TextView) dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView) dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView) dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView) dialog.findViewById(R.id.submit_tv)).setText(delete);
        final EditText remark_edt = (EditText) dialog.findViewById(R.id.remark_edt);

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCreditRequestModel model = new DeleteCreditRequestModel();
                model.DoneCardUser = loginModel.Data.DoneCardUser;
                model.DeviceId = Common.getDeviceId(context);
                model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                model.Sid = sid;
                model.Remarks=remark_edt.getText().toString().trim();
                if(Common.checkInternetConnection(context)){
                    deleteCredit(model);
                    dialog.dismiss();
                    dialog.dismiss();
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void openFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.filter_dialog_layout);

        start_date_value_tv =  filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv  =  filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv   =  filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv    =  filterDialog.findViewById(R.id.end_day_value_tv);


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
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
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
                        callAgent(reportListRequestModel, NO_PROGRESS);
                    }


                }
//            }
        }
    };

    public void callAgent(CreditReportListRequestModel reportListRequestModel, boolean progress) {
        if(Common.checkInternetConnection(context)){
            if(progress && !MyCustomDialog.isDialogShowing()){
                showCustomDialog();
            }
            new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.AdminCreditReport, context,
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
                creditReportDataArrayList.clear();
                creditReportAdapter.notifyDataSetChanged();

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
                try {
                    openStartDatePicker();
                }catch (Exception e){

                }

                break;
            case R.id.endDateLinear:
                try {
                    openEndDatePicker();
                }catch (Exception e){

                }

                break;

            case R.id.lin_dateFilter:
                openFilterDialog();
                break;

            case R.id.lin_selfRequest:
                if(Common.checkInternetConnection(context)) {
                    try {
                        openSelfCreditDialog();
                    }catch (Exception e){

                    }

                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;

        }
    }

    private void openSelfCreditDialog() {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.self_credit_dialog);
            Window window= dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            final EditText agent_id_edt=  dialog.findViewById(R.id.agent_id_edt);
            final EditText amount_credit_edt=  dialog.findViewById(R.id.amount_credit_edt);
            final EditText mobile_no_edt= dialog.findViewById(R.id.mobile_no_edt);
            final EditText remark_edt=  dialog.findViewById(R.id.remark_edt);
            final ListView agencyList=  dialog.findViewById(R.id.agencyList);
            final LinearLayout lin_other_container= dialog.findViewById(R.id.lin_other_container);
//            final RelativeLayout agencyListRel=  dialog.findViewById(R.id.agencyListRel);
//            mobile_no_edt.setText(loginModel.Data.Mobile);

        dialog.findViewById(R.id.mainLayout).setOnClickListener(new View.OnClickListener() {
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
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
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
                        searchAgent(model,agencyList);
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
                    if (Common.checkInternetConnection(context)){
                        if(validateSelfCreditInputs(amount_credit_edt.getText().toString().trim(),
                                agent_id_edt.getText().toString().trim(),
                                mobile_no_edt.getText().toString().trim(),
                                remark_edt.getText().toString().trim())) {

                            SelfCreditRequestModel model=new SelfCreditRequestModel();
                            model.TotalAmount=amount_credit_edt.getText().toString().trim();
                            model.DoneCardUser=loginModel.Data.DoneCardUser;
                            model.DeviceId=Common.getDeviceId(context);
                            model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            model.AgentDoneCardUser=agent_id_edt.getText().toString().trim().
                                    substring(agent_id_edt.getText().toString().indexOf("(")+1,
                                            agent_id_edt.getText().toString().indexOf(")"));
                            model.MobileNumber =mobile_no_edt.getText().toString();
                            if(remark_edt.getText().toString()!=null){
                                model.Remarks=remark_edt.getText().toString();
                            }else{model.Remarks="";}

                            if(Common.checkInternetConnection(context)){
                                selfCredit(model);
                                dialog.dismiss();
                            }else {
                               Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                    }
                }
            });

            dialog.show();
    }

    public AgentNameModel searchAgent(AgentNameRequestModel model, final ListView agencyList) {
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

    private void selfCredit(SelfCreditRequestModel model) {
        showCustomDialog();
        new NetworkCall().callMobileService(model, ApiConstants.DirectCredit, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, SELF_CREDIT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateSelfCreditInputs(String amount, String agentId, String mobile,String remark) {
        if(!Common.isdecimalvalid(amount)) {
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        } if(Float.parseFloat(amount)>1000000){
            Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
            return false;
        }else if(agentId.length()==0){
            Toast.makeText(context, R.string.empty_and_invalid_agent_id, Toast.LENGTH_SHORT).show();
            return false;
        }else if(mobile.length()<10){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(remark.length()==0){
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
}


