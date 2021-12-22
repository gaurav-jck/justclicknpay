package com.justclick.clicknbook.Fragment.salesReport;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.SalesReportAdapter;
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

public class SalesReportFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int CALL_AGENT=1;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<DepositListForAgentModel.DepositListData> depositListDataArrayList;
    private SalesReportAdapter salesReportAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout airLin,dmtLin,mobileLin,railLin,busLin,hotelLin,jctMoneyLin;
    private ImageView airImage,dmtImage,mobileImage,railImage,busImage,hotelImage,jctMoneyImage;
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
    private String SalesTypeFromDialog="",agentDoneCardUser;
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
        View view= inflater.inflate(R.layout.fragment_sales_report, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        agencyList = (ListView) view.findViewById(R.id.agencyList);
        titleChangeListener.onToolBarTitleChange(getString(R.string.sales_report));

        //initialize date values
        setDates();

        salesReportAdapter=new SalesReportAdapter(context, new SalesReportAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<DepositListForAgentModel.DepositListData> list, int position) {

            }
        }, depositListDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(salesReportAdapter);

        if(depositListDataArrayList!=null && depositListDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            reportListRequestModel.EndPage =pageEnd+"";
            reportListRequestModel.StartPage =pageStart+"";
            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            reportListRequestModel.DeviceId=Common.getDeviceId(context);
            reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            reportListRequestModel.FromDate=startDateToSend+"";
            reportListRequestModel.ToDate=endDateToSend+"";

            if(Common.checkInternetConnection(context)) {
                callAgent(reportListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            salesReportAdapter.notifyDataSetChanged();
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        view.findViewById(R.id.lin_dateFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

        view.findViewById(R.id.lin_sales_type_Filter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSalesFilterDialog();
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
                            salesReportAdapter.notifyDataSetChanged();
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

    private void openSalesFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.sales_filter_dialog_layout);
        airImage = (ImageView) filterDialog.findViewById(R.id.airImage);
        dmtImage = (ImageView) filterDialog.findViewById(R.id.dmtImage);
        mobileImage = (ImageView) filterDialog.findViewById(R.id.mobileImage);
        railImage = (ImageView) filterDialog.findViewById(R.id.railImage);
        busImage = (ImageView) filterDialog.findViewById(R.id.busImage);
        hotelImage = (ImageView) filterDialog.findViewById(R.id.hotelImage);
        jctMoneyImage = (ImageView) filterDialog.findViewById(R.id.jctMoneyImage);
        airLin = (LinearLayout) filterDialog.findViewById(R.id.airLin);
        airLin.setOnClickListener(this);
        dmtLin = (LinearLayout) filterDialog.findViewById(R.id.dmtLin);
        dmtLin.setOnClickListener(this);
        mobileLin = (LinearLayout) filterDialog.findViewById(R.id.mobileLin);
        mobileLin.setOnClickListener(this);
        railLin = (LinearLayout) filterDialog.findViewById(R.id.railLin);
        railLin.setOnClickListener(this);
        busLin = (LinearLayout) filterDialog.findViewById(R.id.busLin);
        busLin.setOnClickListener(this);
        hotelLin = (LinearLayout) filterDialog.findViewById(R.id.hotelLin);
        hotelLin.setOnClickListener(this);
        jctMoneyLin = (LinearLayout) filterDialog.findViewById(R.id.jctMoneyLin);
        jctMoneyLin.setOnClickListener(this);
        filterDialog.findViewById(R.id.cancel_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.submitSales_tv).setOnClickListener(this);

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
                        if(agentModel.Data.size()>0){
                        if(agentModel.StatusCode.equalsIgnoreCase("0")){
                            depositListDataArrayList.addAll(agentModel.Data);
                            salesReportAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(agentModel.Data.get(0).TCount);
                        }else if(agentModel.StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();

                        }else {
                            depositListDataArrayList.clear();
                            salesReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();
                        }}else {
                            Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
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
                filterDialog.dismiss();
                depositListDataArrayList.clear();
                salesReportAdapter.notifyDataSetChanged();

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

            case R.id.submitSales_tv:

                filterDialog.dismiss();
                depositListDataArrayList.clear();
                salesReportAdapter.notifyDataSetChanged();

                pageStart=1;
                pageEnd=10;
                reportListRequestModel.EndPage =pageEnd+"";
                reportListRequestModel.StartPage =pageStart+"";
                reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                reportListRequestModel.DeviceId=Common.getDeviceId(context);
                reportListRequestModel.BankName=SalesTypeFromDialog;
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


