package com.justclick.clicknbook.fingoole.view;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.fingoole.model.InsuranceRequestModel;
import com.justclick.clicknbook.fingoole.model.InsuranceResponseModel;
import com.justclick.clicknbook.fingoole.viewmodel.InsuranceViewModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;


public class InsuranceFragment extends Fragment implements View.OnClickListener {

    private InsuranceViewModel mViewModel;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private Context context;
    private EditText nameEdt,mobileEdt, emailEdt, dateTv, amountTv;
    private AutoCompleteTextView planAtv, daysAtv, typeAtv;
    private TextView buyPolicyTv;
    private String[] plans={"Bronze Plan", "Silver Plan", "Gold Plan"};
    private String[] plansValue={"ODPA2L", "ODPA5L", "ODPA10L"};
    private String[] insuranceTypes={"Travel", "Doc on call wth OPD cover"};
    private int[] plansPrice={2,4,7};
    private String insurePlan=plans[0];
    private String insurePlanValue=plansValue[0];
    private int noOfDays=1, price=plansPrice[0];
    private String bookingDate="", PAStartDate="";
    private SimpleDateFormat dateServerFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear;
    private Calendar bookingDateCalendar, minDateCalender;

    public static InsuranceFragment newInstance() {
        return new InsuranceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        } catch (ClassCastException e) {
//            Toast.makeText(context,"exception", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.insurance_fragment, container, false);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        initializeViews(view);
        initializeDates();
        return view;
    }

    private void initializeViews(View view) {
        nameEdt=view.findViewById(R.id.nameEdt);
        mobileEdt=view.findViewById(R.id.mobileEdt);
        emailEdt=view.findViewById(R.id.emailEdt);
        planAtv=view.findViewById(R.id.planAtv);
        typeAtv=view.findViewById(R.id.typeAtv);
        daysAtv=view.findViewById(R.id.daysAtv);
        dateTv=view.findViewById(R.id.dateTv);
        amountTv=view.findViewById(R.id.amountTv);
        buyPolicyTv=view.findViewById(R.id.buyPolicyTv);

        buyPolicyTv.setOnClickListener(this);
        planAtv.setOnClickListener(this);
        daysAtv.setOnClickListener(this);
        typeAtv.setOnClickListener(this);
        view.findViewById(R.id.daysLin).setOnClickListener(this);
        view.findViewById(R.id.dateLin).setOnClickListener(this);
        view.findViewById(R.id.backArrow).setOnClickListener(this);

        planAtv.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, plans));
        planAtv.setSelection(0);
        planAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                insurePlan=plans[position];
                insurePlanValue=plansValue[position];
                price=plansPrice[position];
                setAmount();
            }
        });

        typeAtv.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, insuranceTypes));
        typeAtv.setSelection(0);
        typeAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final String daysArray[]=new String[365];
        for(int i=0;i<365;i++){
            daysArray[i]=(i+1)+"";
        }
        daysAtv.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, daysArray));
        daysAtv.setSelection(0);
        daysAtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noOfDays= Integer.parseInt(daysArray[position]);
                setAmount();
            }
        });
//        default value
        setAmount();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(InsuranceViewModel.class);
//        mViewModel = ViewModelProvider(;
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.planAtv:
                Common.hideSoftKeyboard((Activity) context);
                planAtv.showDropDown();
                break;
            case R.id.daysAtv:
                Common.hideSoftKeyboard((Activity) context);
                daysAtv.showDropDown();
                break;
            case R.id.typeAtv:
                Common.hideSoftKeyboard((Activity) context);
                typeAtv.showDropDown();
                break;
            case R.id.dateLin:
                openDatePicker();
                break;
            case R.id.backArrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.buyPolicyTv:
                buyPolicy();
                break;
        }
    }

    private void initializeDates() {
        //Date formats
        dateServerFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        minDateCalender=Calendar.getInstance();
        bookingDateCalendar=Calendar.getInstance();
        bookingDate= dateServerFormat.format(minDateCalender.getTime());
        minDateCalender.add(Calendar.DAY_OF_MONTH,1);
        bookingDateCalendar.add(Calendar.DAY_OF_MONTH,1);

        checkInDateDay = bookingDateCalendar.get(Calendar.DAY_OF_MONTH);
        checkInDateMonth = bookingDateCalendar.get(Calendar.MONTH);
        checkInDateYear = bookingDateCalendar.get(Calendar.YEAR);

        PAStartDate= dateServerFormat.format(bookingDateCalendar.getTime());

        dateTv.setText(PAStartDate);

    }


    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        bookingDateCalendar.set(year, monthOfYear, dayOfMonth);

                       /* if(CalendarUtils.isPastDay(checkInDateCalendar.getTime())){
                            Toast.makeText(context, "Can't search bus before current date", Toast.LENGTH_SHORT).show();
                        }else if (bookingDateCalendar.after(maxSearchCalender)) {
                            Toast.makeText(context, "Can't search bus after six months", Toast.LENGTH_SHORT).show();
                        }else {*/
                            /*checkInDateDay=dayOfMonth;
                            checkInDateMonth=monthOfYear;
                            checkInDateYear=year;*/

                            dateTv.setText(dateServerFormat.format(bookingDateCalendar.getTime()));
                        Toast.makeText(context, "bookingDate="+bookingDate+", pastart date="+PAStartDate,Toast.LENGTH_SHORT).show();

//                        }

                    }

                },checkInDateYear, checkInDateMonth, checkInDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(minDateCalender.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setAmount(){
        amountTv.setText((noOfDays*price)+"");
    }

    private void buyPolicy() {
        if(validate())
        {
            LoginModel loginModel=new LoginModel();
            loginModel= MyPreferences.getLoginData(loginModel,context);
            InsuranceRequestModel insuranceRequestModel=new InsuranceRequestModel();
            insuranceRequestModel.setDonecarduser(loginModel.Data.DoneCardUser);
            insuranceRequestModel.setInsureMobileNo(mobileEdt.getText().toString());
            insuranceRequestModel.setUserName(nameEdt.getText().toString());
            insuranceRequestModel.setUserEmail(emailEdt.getText().toString());
            insuranceRequestModel.setPolicyAmount(amountTv.getText().toString());
            insuranceRequestModel.setNoofDays(noOfDays+"");
            insuranceRequestModel.setPolicyPlane(insurePlan);
            insuranceRequestModel.setSchemeCode(insurePlanValue);
            insuranceRequestModel.setBookingDate(bookingDate);
            insuranceRequestModel.setPAStartDate(bookingDate);

            new NetworkCall().callInsuranceService(insuranceRequestModel, ApiConstants.InsValidateExePA, context,
                    new NetworkCall.RetrofitResponseListener() {
                        @Override
                        public void onRetrofitResponse(ResponseBody response, int responseCode) {
                            if(response!=null){
                                responseHandler(response, 1);    //http://uatmb.justclicknpay.com/API/Insurence/InsValidateExePA
                                /*{"BookingDate":"26/12/2020","Donecarduser":"JC0A13387","InsuranceType":"T","InsureMobileNo":"9099999999","MerchantId":"JUSTCLICKTRAVELS","Modules":"Train","NoofDays":"8","PAStartDate":"26/12/2020","PolicyAmount":"16","RefNo":"","SchemeCode":"ODPA2L","TxnMedium":"App","UserEmail":"test@test.com","UserName":"test","policyPlane":"Bronze PLan"}*/
                            }else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            InsuranceResponseModel senderResponse = new Gson().fromJson(response.string(), InsuranceResponseModel.class);
            if(senderResponse!=null){
                if(senderResponse.StatusCode.equals("1")) {
                    Toast.makeText(context,senderResponse.StausMessage,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context,"Status code="+senderResponse.StatusCode+"\nMessage="+senderResponse.StausMessage,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        if(!Common.isNameValid(nameEdt.getText().toString().trim())){
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mobileEdt.getText().toString().length()<10){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isEmailValid(emailEdt.getText().toString().trim())){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
