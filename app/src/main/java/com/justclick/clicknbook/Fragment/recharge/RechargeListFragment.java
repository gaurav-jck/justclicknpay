package com.justclick.clicknbook.Fragment.recharge;

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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.RechargeListAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RechargeListModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.RechargeListRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class RechargeListFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<RechargeListModel.RechargeListData> rechargeListDataArrayList;
    private RechargeListAdapter rechargeListAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv;
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private RechargeListRequestModel rechargeListRequestModel;
    private LoginModel loginModel;
//    filter
    private String ReferenceId ="", txnStatus ="",agentName="", agentDoneCard="", productType="";
    private int txnStatusPosition=0, productTypePosition=0;
    private AutocompleteAdapter autocompleteAdapter;
    private AgentNameModel agentNameModel;
    private ListView list_agent;

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
        rechargeListDataArrayList =new ArrayList<>();
        rechargeListRequestModel = new RechargeListRequestModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        initializeDates();
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat = new SimpleDateFormat("dd MMM yy");
        dayFormat = new SimpleDateFormat("EEE");

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
        View view= inflater.inflate(R.layout.fragment_recharge_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);

        titleChangeListener.onToolBarTitleChange(getString(R.string.agentRechargeListFragmentTitle));

        //initialize date values
        setDates();

        rechargeListAdapter =new RechargeListAdapter(context, new RechargeListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<RechargeListModel.RechargeListData> list, int position) {

            }
        }, rechargeListDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rechargeListAdapter);

        if(rechargeListDataArrayList !=null && rechargeListDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            rechargeListRequestModel.EndPage =pageEnd+"";
            rechargeListRequestModel.StartPage =pageStart+"";
            rechargeListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            rechargeListRequestModel.DeviceId=Common.getDeviceId(context);
            rechargeListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            rechargeListRequestModel.FromDate=startDateToSend+"";
            rechargeListRequestModel.ToDate=endDateToSend+"";

            if(Common.checkInternetConnection(context)) {
                serviceCall(rechargeListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);
        view.findViewById(R.id.linFilter).setOnClickListener(this);

        return view;
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
                    rechargeListRequestModel.EndPage =pageEnd+"";
                    rechargeListRequestModel.StartPage =pageStart+"";
                    rechargeListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    rechargeListRequestModel.DeviceId=Common.getDeviceId(context);
                    rechargeListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    rechargeListRequestModel.FromDate=startDateToSend+"";
                    rechargeListRequestModel.ToDate=endDateToSend+"";
                    serviceCall(rechargeListRequestModel, NO_PROGRESS);
                }

            }
//            }
        }
    };

    public void serviceCall(RechargeListRequestModel reportListRequestModel, boolean progress) {
        if(progress){
            showCustomDialog();
        }
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<RechargeListModel> call = apiService.getRechargeListPost(ApiConstants.MobileRechargeList, reportListRequestModel);
        call.enqueue(new Callback<RechargeListModel>() {
            @Override
            public void onResponse(Call<RechargeListModel>call, Response<RechargeListModel> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                try{
                    hideCustomDialog();
                    if(response!=null && response.body()!=null){
                        if(response.body().StatusCode.equalsIgnoreCase("0")){
                            rechargeListDataArrayList.addAll(response.body().Data);
                            rechargeListAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(response.body().Data.get(0).TCount);
                        }else if(response.body().StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            rechargeListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();

                        }else {
                            rechargeListDataArrayList.clear();
                            rechargeListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<RechargeListModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }


        });
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
                rechargeListDataArrayList.clear();
                rechargeListAdapter.notifyDataSetChanged();

                //set date to fragment
                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );

                pageStart=1;
                pageEnd=10;
                rechargeListRequestModel.EndPage =pageEnd+"";
                rechargeListRequestModel.StartPage =pageStart+"";
                rechargeListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                rechargeListRequestModel.DeviceId=Common.getDeviceId(context);
                rechargeListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                rechargeListRequestModel.FromDate=startDateToSend+"";
                rechargeListRequestModel.ToDate=endDateToSend+"";
                if(Common.checkInternetConnection(context)) {
                    serviceCall(rechargeListRequestModel, SHOW_PROGRESS);
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

            case R.id.linFilter:
                openListFilterDialog();
                break;
        }
    }

    private void openListFilterDialog() {
        ReferenceId ="";
        txnStatus ="";
        agentDoneCard="";
        agentName="";
        productType="";
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.recharge_list_filter);

        final Spinner statusSpinner= dialog.findViewById(R.id.statusSpinner);
        final Spinner productSpinner= dialog.findViewById(R.id.productSpinner);
        final EditText txnEdt= dialog.findViewById(R.id.txnEdt);
        final EditText agent_search_edt = (EditText) dialog.findViewById(R.id.agent_search_edt);
        list_agent = (ListView) dialog.findViewById(R.id.list_agent);
//        final RelativeLayout agent_search_rel = (RelativeLayout) dialog.findViewById(R.id.agent_search_rel);
//        final EditText agentEdt= dialog.findViewById(R.id.agentEdt);

        if(loginModel.Data.UserType.equalsIgnoreCase("A")){
            agent_search_edt.setVisibility(View.GONE);
            dialog.findViewById(R.id.agentLabelTv).setVisibility(View.GONE);
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context,
                R.layout.agent_details_spinner_item_dropdown, R.id.operator_tv, getResources().
                getStringArray(R.array.rechargeStatusArray));
        statusAdapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        statusSpinner.setAdapter(statusAdapter);

        statusSpinner.setSelection(txnStatusPosition);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    txnStatus="";
                    txnStatusPosition=0;
                }else {
                    txnStatus = statusSpinner.getSelectedItem().toString();
                    txnStatusPosition=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(context,
                R.layout.agent_details_spinner_item_dropdown, R.id.operator_tv, getResources().
                getStringArray(R.array.rechargeProductArray));
        productAdapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        productSpinner.setAdapter(productAdapter);

        productSpinner.setSelection(productTypePosition);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    productType="";
                    productTypePosition=0;
                }else {
                    productType = productSpinner.getSelectedItem().toString();
                    productTypePosition=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.findViewById(R.id.cancelTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.applyTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ReferenceId =txnEdt.getText().toString().trim();
                applyFilter();
            }
        });

        if(agentNameModel!=null && agentNameModel.Data!=null && agentNameModel.Data.size()>0){
            autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
            list_agent.setAdapter(autocompleteAdapter);
        }

        list_agent.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agentName = autocompleteAdapter.getItem(position).AgencyName;
                        agentDoneCard=agentName.substring(agentName.indexOf("(")+1,agentName.indexOf(")"));
//                        Active = autocompleteAdapter.getItem(position).Active;
                        list_agent.setVisibility(View.GONE);
//                        agent_name_tv.setText(agentName);
//                        agent_search_rel.setVisibility(View.VISIBLE);
                        agent_search_edt.setText(agentName);
                        agent_search_edt.setSelection(agent_search_edt.getText().length());
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });

        agent_search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agent_search_edt.setText("");
                agentName="";
                agentDoneCard="";
//                Active="False";
                list_agent.setVisibility(View.VISIBLE);
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
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        model.RequiredType=loginModel.Data.UserType;

                        call_agent(model);
//                    Toast.makeText(context, s.toString() + " " + start + " " + before + " " + count, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void applyFilter() {
        rechargeListDataArrayList.clear();
        rechargeListAdapter.notifyDataSetChanged();
        //set date to fragment

        startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                dayFormat.format(endDateCalendar.getTime())+" "+
                dateFormat.format(endDateCalendar.getTime())
        );
        pageStart=1;
        pageEnd=10;
        rechargeListRequestModel.EndPage =pageEnd+"";
        rechargeListRequestModel.StartPage =pageStart+"";
        rechargeListRequestModel.FromDate=startDateToSend+"";
        rechargeListRequestModel.ToDate=endDateToSend+"";
        rechargeListRequestModel.ReferenceId= ReferenceId;
        rechargeListRequestModel.Product=productType;
        rechargeListRequestModel.AgentDoneCardUser=agentDoneCard;
        rechargeListRequestModel.Status=txnStatus;

        if(Common.checkInternetConnection(context)) {
            serviceCall(rechargeListRequestModel, SHOW_PROGRESS);
        }else {
            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
        }
    }

    public AgentNameModel call_agent(AgentNameRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
                try {
                    agentNameModel = response.body();
                    if(agentNameModel.StatusCode.equalsIgnoreCase("0")) {
                        autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
                        list_agent.setAdapter(autocompleteAdapter);
                        list_agent.setVisibility(View.VISIBLE);
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


