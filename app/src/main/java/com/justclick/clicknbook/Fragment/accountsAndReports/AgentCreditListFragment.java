package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.CreditListForAgentAdapter;
import com.justclick.clicknbook.model.CreditListForAgentModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailForAgentModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class AgentCreditListFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final int CALL_AGENT=1;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<CreditListForAgentModel.CreditReportData> creditReportDataArrayList;

    private LinearLayoutManager layoutManager;
    private TextView startDateTv, tap;
    private RelativeLayout filter_image_rel;
    private LinearLayout totalAmountLin, lin;
    private Dialog filterDialog;
    private CreditListForAgentAdapter creditReportAdapter;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv, totalCreditAmount, totalPendingAmount, totalRejectedAmount;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private AgentCreditDetailForAgentModel reportListRequestModel;
    private LoginModel loginModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        creditReportDataArrayList =new ArrayList<>();
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
        View view= inflater.inflate(R.layout.fragment_credit_list_for_agent, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        startDateTv = view.findViewById(R.id.startDateTv);

        totalCreditAmount = view.findViewById(R.id.totalCreditAmount);
        totalPendingAmount =  view.findViewById(R.id.totalPendingAmount);
        totalRejectedAmount =  view.findViewById(R.id.totalRejectedAmount);

        titleChangeListener.onToolBarTitleChange(getString(R.string.agentCreditListFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        //initialize date values
        setDates();

        creditReportAdapter=new CreditListForAgentAdapter(context, new CreditListForAgentAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<CreditListForAgentModel.CreditReportData> list, int position) {

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

        view.findViewById(R.id.back_arrow).setOnClickListener(it->{
                getParentFragmentManager().popBackStack();
        });

        lin= (LinearLayout) view.findViewById(R.id.lin);
        totalAmountLin= (LinearLayout) view.findViewById(R.id.totalAmountLin);

        Typeface face = Common.listAgencyNameTypeFace(context);
        ((TextView) view.findViewById(R.id.tap1)).setTypeface(face);;
        ((TextView) view.findViewById(R.id.tap3)).setTypeface(face);;
        ((TextView) view.findViewById(R.id.tap5)).setTypeface(face);;

        tap= (TextView) view.findViewById(R.id.tap);
        view.findViewById(R.id.tap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                lin.setVisibility(View.VISIBLE);
                openAmountView();
            }
        });

        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.SnackbarLayout layout=new Snackbar.SnackbarLayout(LocationSettings.this);

                Snackbar bar = Snackbar.make(view, "Welcome", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                            }
                        });

                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) bar.getView();
                layout.setBackgroundResource(R.drawable.blue_border_white_background);
                TextView tv = (TextView) bar.getView().findViewById(R.id.snackbar_text);
                TextView tv2 = (TextView) bar.getView().findViewById(R.id.snackbar_action);
                tv.setMaxLines(3);
                tv.setPadding(2,2,2,2);
                tv.setTextColor(getResources().getColor(R.color.app_blue_color));
                tv2.setGravity(Gravity.RIGHT);
                tv2.setPadding(2,2,2,2);
                bar.setActionTextColor(getResources().getColor(R.color.app_red_color));
                bar.setDuration(5000);
                bar.show();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
                    reportListRequestModel.DeviceId=Common.getDeviceId(context);
                    reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    reportListRequestModel.FromDate=startDateToSend+"";
                    reportListRequestModel.ToDate=endDateToSend+"";
                    callAgent( reportListRequestModel, NO_PROGRESS);
                }

                /*reportListRequestModel.EndPage =pageEnd+"";
                reportListRequestModel.StartPage =pageStart+"";
                reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                reportListRequestModel.DeviceId=Common.getDeviceId(context);
                reportListRequestModel.FromDate=startDateToSend+"";
                reportListRequestModel.ToDate=endDateToSend+"";
                getBusBookingList( reportListRequestModel, NO_PROGRESS);*/

            }
//            }
        }
    };

    public void callAgent(AgentCreditDetailForAgentModel reportListRequestModel, boolean progress) {
        if(progress && !MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.AgentCreditList, context,
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
                    CreditListForAgentModel agentModel = new Gson().fromJson(response.string(), CreditListForAgentModel.class);
                    hideCustomDialog();
                    if(agentModel!=null){
                        if(agentModel.StatusCode.equalsIgnoreCase("0")){
                            totalAmountLin.setVisibility(View.VISIBLE);
                            creditReportDataArrayList.addAll(agentModel.Data);
                            creditReportAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(agentModel.Data.get(0).TCount);

                            totalCreditAmount.setText(agentModel.DataAmt.get(0).TotalCreditAmount);
                            totalPendingAmount.setText(agentModel.DataAmt.get(0).TotalPendingAmount);
                            totalRejectedAmount.setText(agentModel.DataAmt.get(0).TotalRejectedAmount);
                            openAmountView();

                        }else if(agentModel.StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            creditReportAdapter.notifyDataSetChanged();
                            totalAmountLin.setVisibility(View.GONE);
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();
                        }else {
                            creditReportDataArrayList.clear();
                            totalAmountLin.setVisibility(View.GONE);
                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        creditReportDataArrayList.clear();
                        totalAmountLin.setVisibility(View.GONE);
                        creditReportAdapter.notifyDataSetChanged();
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


