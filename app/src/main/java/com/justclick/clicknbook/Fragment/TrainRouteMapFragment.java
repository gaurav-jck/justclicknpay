package com.justclick.clicknbook.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.TrainRouteMapResponseModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.TrainRouteMapRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class TrainRouteMapFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView date_tv,response_tv;
    private EditText from_station_edt, train_name_edt;
    private RelativeLayout date_lin;
    private Button submit;
    private LoginModel loginModel;
    private String transactionDate;
    private SimpleDateFormat dateToServerFormat;
    private int startDateDay, startDateMonth, startDateYear;
    private Calendar startDateCalendar;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel= MyPreferences.getLoginData(new LoginModel(),context);
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
        View view = inflater.inflate(R.layout.fragment_train_route_map, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.trainRouteMapFragmentTitle));
        initializeViews(view);
        initializeDates();
        return view;
    }

    private void initializeViews(View view) {

        date_tv =  view.findViewById(R.id.address_tv);
        from_station_edt=  view.findViewById(R.id.from_station_edt);
        train_name_edt=  view.findViewById(R.id.train_name_edt);
        date_lin=  view.findViewById(R.id.date_lin);
        submit=  view.findViewById(R.id.bt_submit);
        response_tv=  view.findViewById(R.id.response_tv);
        submit.setOnClickListener(this);
        date_lin.setOnClickListener(this);
        view.findViewById(R.id.card_view).setOnClickListener(this);
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat =Common.getServerDateFormat();

        //default start Date
        startDateCalendar= Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        transactionDate=dateToServerFormat.format(startDateCalendar.getTime());

        //set default date
        date_tv.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));

    }

    private boolean validate() {
        try {
            if(from_station_edt.getText().toString().trim().length()==0){
                Toast.makeText(context, R.string.empty_and_invalid_departure_city, Toast.LENGTH_LONG).show();
                return false;
            }else if (train_name_edt.getText().toString().length()==0)
            {
                Toast.makeText(context, R.string.empty_and_invalid_train_Number, Toast.LENGTH_LONG).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_submit:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                if(Common.checkInternetConnection(context)) {
                    if(validate()){
                        TrainRouteMapRequestModel model=new TrainRouteMapRequestModel();
                        model.FromStation=from_station_edt.getText().toString().trim();
                        model.DeviceId=Common.getDeviceId(context);
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.TrainNumber=train_name_edt.getText().toString().trim();
                        model.Date=transactionDate;
                        requestAmount(model);
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.date_lin:
                try {
                    openDatePicker();
                }catch (Exception e){

                }

                break;

            case R.id.card_view:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;
        }
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

    private void requestAmount(TrainRouteMapRequestModel model) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<TrainRouteMapResponseModel> call = apiService.trainRouteMapRequestPost(ApiConstants.TrainRouteMap, model);
        call.enqueue(new Callback<TrainRouteMapResponseModel>() {
            @Override
            public void onResponse(Call<TrainRouteMapResponseModel>call, Response<TrainRouteMapResponseModel> response) {
                try{
                    hideCustomDialog();
                    if(response!=null){
                        if(response.body().StatusCode.equals("0")) {
                            response_tv.setVisibility(View.VISIBLE);
                            response_tv.setText(response.body().Data.ServiceResponse);
                            //  Toast.makeText(context, response.body().Data.ServiceResponse, Toast.LENGTH_LONG).show();
                        }else {
                            response_tv.setText(response.body().Status);
                            Toast.makeText(context, response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        response_tv.setText("");
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TrainRouteMapResponseModel>call, Throwable t) {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}