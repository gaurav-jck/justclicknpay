package com.justclick.clicknbook.Fragment.hotel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.HotelRoomDataAdapter;
import com.justclick.clicknbook.model.HotelCityListModel;
import com.justclick.clicknbook.model.HotelRoomDataResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.HotelCityRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelBookingInformationFragment extends Fragment implements View.OnClickListener{

    private ToolBarTitleChangeListener titleChangeListener;
    Context context;
    private LoginModel loginModel;
    private Calendar calendar;
    private int checkDOBDateDay, checkDOBDateMonth, checkDOBDateYear;
    private String checkDOBDate="";
    private SimpleDateFormat dateServerFormat;
    private HotelRoomDataResponseModel roomDataResponseModel;
    private RecyclerView roomRecyclerView;
    private ImageView backArrowImg,roomDetailArrowImg;
    private SimpleDraweeView hotelImage;
    private EditText promoCodeEdt;
    private LinearLayout linearAdd;
    private TextView hotelDetailsTv,roomDayInfoTv,checkInDateTv,checkOutDateTv,checkInTimeeTv,
            paxInfoTv,checkOutTimeTv,priceTv,totalFareGst,refundTv,submitBtn;
    private EditText[] first_last_name_edt,mobileEdt,emailEdt;
    private TextView[] date_of_birth_edt,paxDetailTv,no_of_roomTv;
    private ImageView[] roomArrowImg;
    private LinearLayout[] fillDetailLinear;
    String[] firstAndLastName=new String[20];
    String[] mobile=new String[20];
    String[] email=new String[20];
    String[] dateOfBirth=new String[20];
    int Room=1;

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
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        roomDataResponseModel =new HotelRoomDataResponseModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_hotel_booking_information, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.hotelBookingInfoFragmentTitle));

        if(getArguments()!=null && getArguments().getSerializable("HotelInfo")!=null){
            roomDataResponseModel = (HotelRoomDataResponseModel) getArguments().getSerializable("HotelInfo");
        }
        calendar = Calendar.getInstance();
        dateServerFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        checkDOBDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        checkDOBDateMonth = calendar.get(Calendar.MONTH);
        checkDOBDateYear = calendar.get(Calendar.YEAR);
        linearAdd= (LinearLayout) view.findViewById(R.id.linearAdd);
        submitBtn= (TextView) view.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
        view.findViewById(R.id.roomDetailArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view.findViewById(R.id.linearAdd).getVisibility()==View.VISIBLE){
                    view.findViewById(R.id.linearAdd).setVisibility(View.GONE);
                    view.findViewById(R.id.roomDetailArrowImg).setRotation(0);
                }else {
                    view.findViewById(R.id.linearAdd).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.roomDetailArrowImg).setRotation(180);
                }

            }
        });
//        initializeViews(view);
        setRoomDetailDynamically(view);
        setFont(view);
        return view;
    }

    private void setFont(View view) {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.TextViewTypeFace(context);
        Typeface face1 = Common.OpenSansRegularTypeFace(context);

//        titles
        ((TextView)view.findViewById(R.id.roomTypeTv)).setTypeface(face2);
        ((TextView)view.findViewById(R.id.submitBtn)).setTypeface(face2);
        ((TextView)view.findViewById(R.id.roomDetailsTitle)).setTypeface(face2);

//        contents
        ((TextView)view.findViewById(R.id.checkInDateTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.checkInTimeeTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.checkOutTimeTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.hotelDetailsTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.priceTv)).setTypeface(face1);

//        labels
        ((TextView)view.findViewById(R.id.checkInLabelTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.checkOutLabelTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.refundTv)).setTypeface(face1);
        ((TextView)view.findViewById(R.id.totalFareGst)).setTypeface(face1);

//        editTexts
        ((TextView)view.findViewById(R.id.promoCodeEdt)).setTypeface(face);


    }

    private void setRoomDetailDynamically(View view) {

        first_last_name_edt=new EditText[Room];
        mobileEdt=new EditText[Room];
        emailEdt=new EditText[Room];
        date_of_birth_edt=new TextView[Room];
        no_of_roomTv=new TextView[Room];
        paxDetailTv=new TextView[Room];
        roomArrowImg=new ImageView[Room];
        no_of_roomTv=new TextView[Room];
        fillDetailLinear=new LinearLayout[Room];

        for(int i = 0; i< Room; i++) {
            LinearLayout dynamicContent = (LinearLayout) view.findViewById(R.id.linearAdd);
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.hotel_room_info, dynamicContent, false);

            date_of_birth_edt[i] =(TextView) wizard.findViewById(R.id.date_of_birth_edt);
            paxDetailTv[i] =(TextView) wizard.findViewById(R.id.paxDetailTv);
            paxDetailTv[i].setTypeface(Common.OpenSansRegularTypeFace(context));
            no_of_roomTv[i] =(TextView) wizard.findViewById(R.id.no_of_roomTv);
            no_of_roomTv[i].setTypeface(Common.OpenSansRegularTypeFace(context));
            first_last_name_edt[i] =(EditText) wizard.findViewById(R.id.first_last_name_edt);
            mobileEdt[i] =(EditText) wizard.findViewById(R.id.mobileEdt);
            emailEdt[i] =(EditText) wizard.findViewById(R.id.emailEdt);
            roomArrowImg[i] =(ImageView) wizard.findViewById(R.id.roomArrowImg);
            roomArrowImg[i].setOnClickListener(this);
            fillDetailLinear[i] =(LinearLayout) wizard.findViewById(R.id.fillDetailLinear);

            final int finalI = i;
            wizard.findViewById(R.id.date_of_birth_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDOBDatePicker(date_of_birth_edt[finalI]);
                }
            });

            wizard.findViewById(R.id.arrowClickLinear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wizard.findViewById(R.id.fillDetailLinear).getVisibility()==View.VISIBLE){
                        wizard.findViewById(R.id.fillDetailLinear).setVisibility(View.GONE);
                        wizard.findViewById(R.id.roomArrowImg).setRotation(0);
                    }else {
                        wizard.findViewById(R.id.fillDetailLinear).setVisibility(View.VISIBLE);
                        wizard.findViewById(R.id.roomArrowImg).setRotation(180);
                    }
                }
            });
            dynamicContent.addView(wizard);
        }
    }

    private void openDOBDatePicker(final TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        checkDOBDate= dateServerFormat.format(calendar.getTime());
                        checkDOBDateDay =dayOfMonth;
                        checkDOBDateMonth =monthOfYear;
                        checkDOBDateYear =year;
                        textView.setText(checkDOBDate);

                    }

                }, checkDOBDateYear, checkDOBDateMonth, checkDOBDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    private void initializeViews(View view) {
        roomRecyclerView = (RecyclerView) view.findViewById(R.id.roomRecyclerView);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        roomRecyclerView.setAdapter(new HotelRoomDataAdapter(context, new HotelRoomDataAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<HotelRoomDataResponseModel.Rooms> list, int position) {
                switch (view.getId()) {
                    case R.id.cancellationPolicy_tv:
                        openCancellationDialog(list.get(position).CancellationPolicy);
                        break;
                }
            }
        }, roomDataResponseModel.Rooms));

    }

    public void hotelCity(HotelCityRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<HotelCityListModel> call = apiService.getHotelCityPost(ApiConstants.CityCountry, model);
        call.enqueue(new Callback<HotelCityListModel>() {
            @Override
            public void onResponse(Call<HotelCityListModel> call,Response<HotelCityListModel> response) {
                try {

                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(Call<HotelCityListModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCancellationDialog(String cancellationPolicy) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setMessage(cancellationPolicy);

        alertDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                for (int i = 0; i < Room; i++) {

                    firstAndLastName[i] = first_last_name_edt[i].getText().toString();
                    mobile[i] = mobileEdt[i].getText().toString();
                    dateOfBirth[i] = date_of_birth_edt[i].getText().toString();
                    email[i] = emailEdt[i].getText().toString();
                   if(validate(first_last_name_edt[i], mobileEdt[i], date_of_birth_edt[i], emailEdt[i])) {
                       if(i==Room-1)
                       Toast.makeText(context, "submit", Toast.LENGTH_SHORT).show();
                       ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(new HotelConfirmFragment());
                   }else {
                       break;
                   }
                }

        }
    }

    private Boolean validate(EditText name, EditText mobile, TextView dateOfBirth, EditText email) {
        if(!Common.isNameValid(name.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_first_name, Toast.LENGTH_SHORT).show();
            name.setError(getResources().getString(R.string.empty_and_invalid_first_name));
            return false;
        }else if(mobile.getText().toString().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
            return false;
        } else if(dateOfBirth.length()==0){
            Toast.makeText(context,R.string.empty_and_invalid_date_name,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!Common.isEmailValid(email.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_email,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

