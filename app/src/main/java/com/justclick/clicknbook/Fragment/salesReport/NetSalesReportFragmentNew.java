package com.justclick.clicknbook.Fragment.salesReport;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.NetSalesReportAdapter;
import com.justclick.clicknbook.adapter.NetSalesReportAdapterNew;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.NetSalesResponseModel;
import com.justclick.clicknbook.model.NetSalesResponseModelNew;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.NetSalesReportModel;
import com.justclick.clicknbook.requestmodels.NetSalesReportModelNew;
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
import retrofit2.Retrofit;

/**
 * Created by Lenovo on 09/27/2017.
 */

public class NetSalesReportFragmentNew extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int CALL_AGENT=1;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<NetSalesResponseModelNew.aadharPayResponceList> netSalesListDataArrayList;
    private NetSalesReportAdapterNew netSalesReportAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout airLin,dmtLin,mobileLin,railLin,busLin,hotelLin,jctMoneyLin,lin;
    private ImageView airImage,dmtImage,mobileImage,railImage,busImage,hotelImage,jctMoneyImage;
    private TextView startDateTv,tap, totalTv, totalCommTv, totalNetTv;
    private RelativeLayout filter_image_rel;
    private Dialog filterDialog,openAgentFilterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private NetSalesReportModelNew netSalesListRequestModel;
    private LoginModel loginModel;
    private String SalesTypeFromDialog="",agentDoneCardUser,agentName="", agentDoneCard="";
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;
    private EditText agent_search_edt;
    private ListView agencyList,list_agent;
    private String[] title = { "AadharPay", "AEPS", "Air", "BillPay", "Bus", "DMT", "DTH", "Hotel", "JCK Money",
            "Lic", "MicroAtm", "Mobile Recharge", "Rail", "Wallet"};
    private String productType="DMT";
    private Spinner productTypeSpinner;

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
        netSalesListDataArrayList =new ArrayList<>();
        netSalesListRequestModel=new NetSalesReportModelNew();
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
        View view= inflater.inflate(R.layout.fragment_net_sales_report_new, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        agencyList = (ListView) view.findViewById(R.id.agencyList);
        titleChangeListener.onToolBarTitleChange(getString(R.string.net_sales_report));
        tap= (TextView) view.findViewById(R.id.tap);
        totalNetTv= view.findViewById(R.id.totalNetTv);
        totalCommTv= view.findViewById(R.id.totalCommTv);
        totalTv= (TextView) view.findViewById(R.id.totalTv);
        lin= (LinearLayout) view.findViewById(R.id.lin);
        productTypeSpinner =  view.findViewById(R.id.productTypeSpinner);

        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                title);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        productTypeSpinner.setAdapter(ad);

        productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                productType=productTypeSpinner.getSelectedItem().toString();
                netSalesListDataArrayList.clear();
                netSalesReportAdapter.notifyDataSetChanged();
                callAgent(SHOW_PROGRESS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //initialize date values
        setDates();
        view.findViewById(R.id.lin_sales_type_Filter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                agentName="";
                agentDoneCard="";
                openAgentFilterDialog();
            }
        });

        netSalesReportAdapter=new NetSalesReportAdapterNew(context, (view1, list, position) -> {

        }, netSalesListDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(netSalesReportAdapter);

        /*if(netSalesListDataArrayList!=null && netSalesListDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;

            if(Common.checkInternetConnection(context)) {
                callAgent(SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            netSalesReportAdapter.notifyDataSetChanged();
        }*/

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        view.findViewById(R.id.lin_dateFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
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
//                netSalesListRequestModel.AgentCode="";
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

                        if(Common.checkInternetConnection(context)) {
                            netSalesListDataArrayList.clear();
                            netSalesReportAdapter.notifyDataSetChanged();
                            callAgent(SHOW_PROGRESS);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                });


        view.findViewById(R.id.tap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                lin.setVisibility(View.VISIBLE);
                if (lin.getVisibility() == View.VISIBLE) {
                    lin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_down));
//                        tap.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_down));
                    lin.setVisibility(View.GONE);
                } else {
                    openAmountView();
                }
            }
        });

        return view;
    }

    private void openAmountView() {
        lin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_up));
        tap.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_up));
        lin.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_down));
//                        tap.startAnimation(AnimationUtils.loadAnimation(context, R.anim.snackbar_down));
                lin.setVisibility(View.GONE);
                // do stuff
            }
        }, 5000);
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

    private void openAgentFilterDialog() {
        openAgentFilterDialog = new Dialog(context,R.style.Theme_Design_Light);
        openAgentFilterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openAgentFilterDialog.setContentView(R.layout.sales_report_list_filter);

        agent_search_edt = (EditText) openAgentFilterDialog.findViewById(R.id.agent_search_edt);
        Spinner productTypeSpinner =  openAgentFilterDialog.findViewById(R.id.productTypeSpinner);
        list_agent =  openAgentFilterDialog.findViewById(R.id.list_agent);
        openAgentFilterDialog.findViewById(R.id.cancelTv).setOnClickListener(this);
        openAgentFilterDialog.findViewById(R.id.applyTv).setOnClickListener(this);

        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                title);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        productTypeSpinner.setAdapter(ad);

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

                        call_agent(model,list_agent);

                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        openAgentFilterDialog.findViewById(R.id.applyTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgentFilterDialog.dismiss();
                agentName=agent_search_edt.getText().toString().trim();
                productType=productTypeSpinner.getSelectedItem().toString();
                applyFilter();
            }
        });

        openAgentFilterDialog.show();
    }

    private void applyFilter() {

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

                    callAgent(NO_PROGRESS);
                }
            }
//            }
        }
    };

    public void callAgent(boolean progress) {

        netSalesListRequestModel.Agent=loginModel.Data.DoneCardUser;
        netSalesListRequestModel.UserType=loginModel.Data.UserType;
        netSalesListRequestModel.FromD=startDateToSend+"";
        netSalesListRequestModel.ToD=endDateToSend+"";
        netSalesListRequestModel.Uid="";
        netSalesListRequestModel.DistDoneCardU="";
        netSalesListRequestModel.Ttype="EKO";
        netSalesListRequestModel.reportType="addharpay";
        netSalesListRequestModel.Product=productType;
        netSalesListRequestModel.TxnType="CW";

        String json = new Gson().toJson(netSalesListRequestModel);

        if(progress){
            showCustomDialog();
        }
        /*new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.NetSalesReport, context,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, CALL_AGENT);
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });*/
        new NetworkCall().callService(NetworkCall.getAccountStmtApiInterface().accountStmtPost(ApiConstants.SalesReport,
                        netSalesListRequestModel),
                context,false,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, CALL_AGENT);
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case CALL_AGENT:
                    hideCustomDialog();
                    NetSalesResponseModelNew agentModel = new Gson().fromJson(response.string(), NetSalesResponseModelNew.class);
                    if(agentModel!=null ){

                        if(agentModel.statusCode.equalsIgnoreCase("00")){
                            if(agentModel.aadharPayResponceList!=null && agentModel.aadharPayResponceList.size()>0){
                            netSalesListDataArrayList.addAll(agentModel.aadharPayResponceList);
                            netSalesReportAdapter.notifyDataSetChanged();
                            totalPageCount= agentModel.totalCount;

                            float total=0, net=0, comm=0;
                            for(int i=0; i<agentModel.aadharPayResponceList.size(); i++){
                                total=total+agentModel.aadharPayResponceList.get(i).totalRecharge;
                                net=net+agentModel.aadharPayResponceList.get(i).totalNetRecharge;
                                comm=comm+agentModel.aadharPayResponceList.get(i).totalRechargeDistCom;
                            }
                            totalTv.setText(total+"");
                            totalNetTv.setText(net+"");
                            totalCommTv.setText(comm+"");
                            }
                            else {
                                Toast.makeText(context,"No record found", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            netSalesListDataArrayList.clear();
                            netSalesReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.statusMessage, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
//            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
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
            case R.id.cancelTv:
                if(openAgentFilterDialog!=null)
                    openAgentFilterDialog.dismiss();
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
                netSalesListDataArrayList.clear();
                netSalesReportAdapter.notifyDataSetChanged();

                //set date to fragment
                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );

                pageStart=1;
                pageEnd=10;

                if(Common.checkInternetConnection(context)) {
                    callAgent(SHOW_PROGRESS);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.submitSales_tv:

                filterDialog.dismiss();
                netSalesListDataArrayList.clear();
                netSalesReportAdapter.notifyDataSetChanged();

                pageStart=1;
                pageEnd=10;

                if(Common.checkInternetConnection(context)) {
                    callAgent(SHOW_PROGRESS);
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
            case R.id.airLin:
                SalesTypeFromDialog="Air";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.dmtLin:
                SalesTypeFromDialog="DMT";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.mobileLin:
                SalesTypeFromDialog="Mobile";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.railLin:
                SalesTypeFromDialog="Rail";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.busLin:
                SalesTypeFromDialog="Bus";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.hotelLin:
                SalesTypeFromDialog="Hotel";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                break;
            case R.id.jctMoneyLin:
                SalesTypeFromDialog="JctMoney";
                airImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                dmtImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                mobileImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                railImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                busImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                hotelImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_unchecked));
                jctMoneyImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_button_checked));
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


