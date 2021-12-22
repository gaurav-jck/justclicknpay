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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.DepositListForAgentAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.DepositListForAgentModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailForAgentModel;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class AgentDepositListFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int CALL_AGENT=1;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<DepositListForAgentModel.DepositListData> depositListDataArrayList;
    private DepositListForAgentAdapter creditReportAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv;
    private RelativeLayout filter_image_rel;
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private AgentCreditDetailForAgentModel reportListRequestModel;
    private LoginModel loginModel;
    private String BankNameFromDialog="",agentDoneCardUser;
    RadioGroup radioGroup;
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;
    private EditText agent_search_edt;
    private ListView agencyList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        depositListDataArrayList =new ArrayList<>();
        reportListRequestModel=new AgentCreditDetailForAgentModel();
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
        View view= inflater.inflate(R.layout.fragment_deposit_list_for_agent, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        agencyList = (ListView) view.findViewById(R.id.agencyList);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agentDepositListFragmentTitle));

        //initialize date values
        setDates();

        creditReportAdapter=new DepositListForAgentAdapter(context, new DepositListForAgentAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<DepositListForAgentModel.DepositListData> list, int position) {

            }
        }, depositListDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(creditReportAdapter);

        if(depositListDataArrayList!=null && depositListDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            reportListRequestModel.EndPage =pageEnd+"";
            reportListRequestModel.StartPage =pageStart+"";
            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
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

        view.findViewById(R.id.lin_dateFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

        view.findViewById(R.id.lin_bankFilter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openBankFilterDialog();
            }
        });


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
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
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
                reportListRequestModel.AgentCode="";
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
                        reportListRequestModel.AgentCode=agentDoneCardUser;
                        reportListRequestModel.DeviceId=Common.getDeviceId(context);
                        reportListRequestModel.FromDate=startDateToSend+"";
                        reportListRequestModel.ToDate=endDateToSend+"";

                        if(Common.checkInternetConnection(context)) {
                            depositListDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            callAgent(reportListRequestModel, SHOW_PROGRESS);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return view;

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

    private void openBankFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.bank_filter_dialog_layout);

//        bankRadio= (RadioButton) filterDialog.findViewById(R.id.radio);
        radioGroup= (RadioGroup) filterDialog.findViewById(R.id.radioGroup);
        filterDialog.findViewById(R.id.cancel_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.submitBank_tv).setOnClickListener(this);

//        for (int row = 0; row < 1; row++) {
//            RadioGroup ll = new RadioGroup(context);
//            ll.setOrientation(LinearLayout.VERTICAL);

        String bankNames="All#"+loginModel.Data.BankNames;
        for (int i = 0; i < bankNames.split("#").length; i++) {
            final RadioButton rdbtn = new RadioButton(context);
            rdbtn.setText(bankNames.split("#")[i]);
            rdbtn.setId(i);
            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rdbtn.getText().toString().equalsIgnoreCase("All")){
                        BankNameFromDialog="";
                    }else {
                        BankNameFromDialog= rdbtn.getText().toString();
                    }
//                    Toast.makeText(context,BankNameFromDialog,Toast.LENGTH_SHORT).show();
                }
            });

            radioGroup.addView(rdbtn);
        }
        radioGroup.check(0);
        BankNameFromDialog="";

        filterDialog.show();
    }


    private void openFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.filter_dialog_layout);

        start_date_value_tv= (TextView) filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv= (TextView) filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv= (TextView) filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv= (TextView) filterDialog.findViewById(R.id.end_day_value_tv);


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
                    && dy>0  ) {

                if(!(pageEnd>totalItemCount)) {
                    pageStart = pageStart + 10;
                    pageEnd = pageEnd + 10;
                    reportListRequestModel.EndPage =pageEnd+"";
                    reportListRequestModel.StartPage =pageStart+"";
                    reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    reportListRequestModel.DeviceId=Common.getDeviceId(context);
                    reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    reportListRequestModel.FromDate=startDateToSend+"";
                    reportListRequestModel.ToDate=endDateToSend+"";
                    callAgent( reportListRequestModel, NO_PROGRESS);
                }


            }
//            }
        }
    };

    public void callAgent(AgentCreditDetailForAgentModel reportListRequestModel, boolean progress) {
        if(progress){
            showCustomDialog();
        }
        new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.AgentDepositList, context,
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
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case CALL_AGENT:
                    DepositListForAgentModel agentModel = new Gson().fromJson(response.string(), DepositListForAgentModel.class);
                    hideCustomDialog();
                    if(agentModel!=null){
                        if(agentModel.StatusCode.equalsIgnoreCase("0")){
                            depositListDataArrayList.addAll(agentModel.Data);
                            creditReportAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(agentModel.Data.get(0).TCount);
                        }else if(agentModel.StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();

                        }else {
                            depositListDataArrayList.clear();
                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv:
                if(filterDialog!=null)
                    filterDialog.dismiss();
                break;
            case R.id.submit_tv:
                long diff = endDateCalendar.getTime().getTime() - startDateCalendar.getTime().getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if(days<0){
                    Toast.makeText(context, "you have selected wrong dates", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(days>=15){
                    Toast.makeText(context, "please select 15 days data", Toast.LENGTH_SHORT).show();
                    break;
                }
                filterDialog.dismiss();
                depositListDataArrayList.clear();
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

            case R.id.submitBank_tv:
                filterDialog.dismiss();
                depositListDataArrayList.clear();
                creditReportAdapter.notifyDataSetChanged();

                pageStart=1;
                pageEnd=10;
                reportListRequestModel.EndPage =pageEnd+"";
                reportListRequestModel.StartPage =pageStart+"";
                reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                reportListRequestModel.DeviceId=Common.getDeviceId(context);
                reportListRequestModel.BankName=BankNameFromDialog+"";
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
        }
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


