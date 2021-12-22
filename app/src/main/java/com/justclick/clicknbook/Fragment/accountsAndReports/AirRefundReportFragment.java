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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AirRefundReportAdapter;
import com.justclick.clicknbook.model.AirRefundReportModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AirRefundReportRequestModel;
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

public class AirRefundReportFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<AirRefundReportModel.AirRefundReportData> airRefundReportDataArrayList;
    private AirRefundReportAdapter airRefundReportAdapter;
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
    private AirRefundReportRequestModel cancelListRequestModel;
    private LoginModel loginModel;

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
        airRefundReportDataArrayList =new ArrayList<>();
        cancelListRequestModel = new AirRefundReportRequestModel();
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
        View view= inflater.inflate(R.layout.fragment_recharge_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);

        titleChangeListener.onToolBarTitleChange(getString(R.string.airRefundReportFragmentTitle));

        //initialize date values
        setDates();

        airRefundReportAdapter =new AirRefundReportAdapter(context, new AirRefundReportAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<AirRefundReportModel.AirRefundReportData> list, int position) {

            }
        }, airRefundReportDataArrayList, totalPageCount-1);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(airRefundReportAdapter);

        if(airRefundReportDataArrayList !=null && airRefundReportDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            cancelListRequestModel.EndPage =pageEnd+"";
            cancelListRequestModel.StartPage =pageStart+"";
            cancelListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            cancelListRequestModel.DeviceId=Common.getDeviceId(context);
            cancelListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            cancelListRequestModel.FromDate=startDateToSend+"";
            cancelListRequestModel.ToDate=endDateToSend+"";

            if(Common.checkInternetConnection(context)) {
                serviceCall(cancelListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            airRefundReportAdapter.notifyDataSetChanged();
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        view.findViewById(R.id.lin_dateFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

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
                    cancelListRequestModel.EndPage =pageEnd+"";
                    cancelListRequestModel.StartPage =pageStart+"";
                    cancelListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    cancelListRequestModel.DeviceId=Common.getDeviceId(context);
                    cancelListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    cancelListRequestModel.FromDate=startDateToSend+"";
                    cancelListRequestModel.ToDate=endDateToSend+"";
                    serviceCall(cancelListRequestModel, NO_PROGRESS);
                }


            }
//            }
        }
    };

    public void serviceCall(AirRefundReportRequestModel reportListRequestModel, boolean progress) {
        if(progress){
            showCustomDialog();
        }
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<AirRefundReportModel> call = apiService.getAirRefundReportPost(ApiConstants.AirRefundReport, reportListRequestModel);
        call.enqueue(new Callback<AirRefundReportModel>() {
            @Override
            public void onResponse(Call<AirRefundReportModel>call, Response<AirRefundReportModel> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                try{
                    hideCustomDialog();
                    if(response!=null){
                        if(response.body().StatusCode.equalsIgnoreCase("0") ||
                                (response.body().Data!=null && response.body().Data.size()>0)){
                            airRefundReportDataArrayList.addAll(response.body().Data);
                            airRefundReportAdapter.notifyDataSetChanged();
                            totalPageCount= Integer.parseInt(response.body().Data.get(0).TCount)-1;
                        }else if(response.body().StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            airRefundReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();

                        }else {
//                            airRefundReportDataArrayList.clear();
//                            airRefundReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
//                    Toast. makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<AirRefundReportModel> call, Throwable t) {
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
                filterDialog.dismiss();
                airRefundReportDataArrayList.clear();
                airRefundReportAdapter.notifyDataSetChanged();

                //set date to fragment
                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );

                pageStart=1;
                pageEnd=10;
                cancelListRequestModel.EndPage =pageEnd+"";
                cancelListRequestModel.StartPage =pageStart+"";
                cancelListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                cancelListRequestModel.DeviceId=Common.getDeviceId(context);
                cancelListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                cancelListRequestModel.FromDate=startDateToSend+"";
                cancelListRequestModel.ToDate=endDateToSend+"";
                if(Common.checkInternetConnection(context)) {
                    serviceCall(cancelListRequestModel, SHOW_PROGRESS);
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


