package com.justclick.clicknbook.Fragment.hotel;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.HotelAvailabilityAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.model.HotelMoreInfoResponseModel;
import com.justclick.clicknbook.model.HotelRoomDataResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.HotelAvailabilityRequestModel;
import com.justclick.clicknbook.requestmodels.HotelRoomInfoRequestModel;
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

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class HotelSearchListFragment extends Fragment implements View.OnClickListener{
    private final int START_DATE=1, END_DATE=2;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentBackPressListener fragmentBackPressListener;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend,agentDoneCardUser;
    private ArrayList<HotelAvailabilityResponseModel.Hotels> creditReportDataArrayList;
    private HotelAvailabilityAdapter hotelAvailabilityAdapter;
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
    private HotelAvailabilityRequestModel hotelAvailabilityRequestModel;
    private LoginModel loginModel;
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;
    private EditText agent_search_edt;
    private ListView agencyList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
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
        hotelAvailabilityRequestModel =new HotelAvailabilityRequestModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        agentNameModel=new AgentNameModel();
        try {
            initializeDates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDates() throws Exception{
        startDateCalendar= (Calendar) getArguments().getSerializable("CheckInDateCalander");
        endDateCalendar= (Calendar) getArguments().getSerializable("CheckOutDateCalander");
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();
        dateFormat = Common.getToFromDateFormat();
        dayFormat = Common.getShortDayFormat();

        //default start Date
//        startDateCalendar=Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        //default end Date
//        endDateCalendar = Calendar.getInstance();
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
        View view= inflater.inflate(R.layout.fragment_hotel_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        agencyList = (ListView) view.findViewById(R.id.agencyList);

        titleChangeListener.onToolBarTitleChange(getString(R.string.hotelSearchListFragmentTitle));
        fragmentBackPressListener.onFragmentBackPress(agencyList);

        view.findViewById(R.id.lin_selfRequest).setOnClickListener(this);
        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);

        //initialize date values
        setDates();

        if(getArguments().getSerializable("HotelList")!=null) {
            creditReportDataArrayList = (ArrayList<HotelAvailabilityResponseModel.Hotels>) getArguments().getSerializable("HotelList");
        }

        hotelAvailabilityAdapter=new HotelAvailabilityAdapter(context, new HotelAvailabilityAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<HotelAvailabilityResponseModel.Hotels> list, int position) {
                switch (view.getId()){
                    case R.id.itemLin:
                        HotelMoreInfoFragment hotelMoreInfoFragment=new HotelMoreInfoFragment();
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(
                                hotelMoreInfoFragment);
//                        HotelRoomInfoRequestModel model=new HotelRoomInfoRequestModel();
//                        model.DataFileName=list.get(position).DataFileName;
//                        model.HotelCode=list.get(position).HotelCode;
//                        model.ResultIndex=list.get(position).RoomIndex;
//                        model.Supplier="TBO";
//                        hotelMoreInfo(model);
                        break;

                    case R.id.bookHotelTv:
                        HotelRoomInfoRequestModel model2=new HotelRoomInfoRequestModel();
                        model2.DataFileName=list.get(position).DataFileName;
                        model2.HotelCode=list.get(position).HotelCode;
                        model2.ResultIndex=list.get(position).RoomIndex;
                        model2.Supplier="TBO";
                        hotelRoomData(model2);
                        break;
                }
            }
        },creditReportDataArrayList,totalPageCount);

        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(hotelAvailabilityAdapter);

        if(creditReportDataArrayList!=null && creditReportDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            hotelAvailabilityRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            hotelAvailabilityRequestModel.DeviceId=Common.getDeviceId(context);
            hotelAvailabilityRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            hotelAvailabilityRequestModel.CheckInDate=startDateToSend;
            hotelAvailabilityRequestModel.CheckOutDate=endDateToSend;
            hotelAvailabilityRequestModel.CountryName="Singapore";
            hotelAvailabilityRequestModel.DestnationName="Singapore";
            hotelAvailabilityRequestModel.DestnationCode="Singapore";
            hotelAvailabilityRequestModel.NumberOfAdult="1";
            hotelAvailabilityRequestModel.NumberOfChild="0";
            hotelAvailabilityRequestModel.NumberOfDays="1";
            hotelAvailabilityRequestModel.NumberOfRooms="1";
            hotelAvailabilityRequestModel.Supplier="TBO";
            hotelAvailabilityRequestModel.StaRating="All";
            ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> roomOccupancyArrayList=new ArrayList<>();
            HotelAvailabilityRequestModel.RoomOccupancy roomOccupancy=new HotelAvailabilityRequestModel().new RoomOccupancy();
            roomOccupancy.Ages="1,1";
            roomOccupancy.Adults=1;
            roomOccupancy.Children=1;
            roomOccupancy.TotalRoom=1;
            roomOccupancyArrayList.add(roomOccupancy);
            hotelAvailabilityRequestModel.RoomOccupancy=roomOccupancyArrayList;

            if(Common.checkInternetConnection(context)) {
                hotelSearch(hotelAvailabilityRequestModel);

            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            hotelAvailabilityAdapter.notifyDataSetChanged();
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
                        hotelAvailabilityRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                        hotelAvailabilityRequestModel.DeviceId=Common.getDeviceId(context);
                        hotelAvailabilityRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        hotelAvailabilityRequestModel.CheckInDate=startDateToSend;
                        hotelAvailabilityRequestModel.CheckOutDate=endDateToSend;
                        hotelAvailabilityRequestModel.CountryName="Singapore";
                        hotelAvailabilityRequestModel.DestnationName="Singapore";
                        hotelAvailabilityRequestModel.DestnationCode="Singapore";
                        hotelAvailabilityRequestModel.NumberOfAdult="1";
                        hotelAvailabilityRequestModel.NumberOfChild="0";
                        hotelAvailabilityRequestModel.NumberOfDays="1";
                        hotelAvailabilityRequestModel.NumberOfRooms="1";
                        hotelAvailabilityRequestModel.Supplier="TBO";
                        hotelAvailabilityRequestModel.StaRating="All";
                        ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> roomOccupancyArrayList=new ArrayList<>();
                        HotelAvailabilityRequestModel.RoomOccupancy roomOccupancy=new HotelAvailabilityRequestModel().new RoomOccupancy();
                        roomOccupancy.Ages="1,1";
                        roomOccupancy.Adults=1;
                        roomOccupancy.Children=1;
                        roomOccupancy.TotalRoom=1;
                        roomOccupancyArrayList.add(roomOccupancy);
                        hotelAvailabilityRequestModel.RoomOccupancy=roomOccupancyArrayList;

                        if(Common.checkInternetConnection(context)) {
                            creditReportDataArrayList.clear();
                            hotelAvailabilityAdapter.notifyDataSetChanged();
                            hotelSearch(hotelAvailabilityRequestModel);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }

                    }
                });
        return view;
    }

    private void hotelMoreInfo(HotelRoomInfoRequestModel model) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClientHotel().create(ApiInterface.class);
        Call<HotelMoreInfoResponseModel> call = apiService.getHotelMoreInfoPost(ApiConstants.MoreInfo, model);
        call.enqueue(new Callback<HotelMoreInfoResponseModel>() {
            @Override
            public void onResponse(Call<HotelMoreInfoResponseModel> call, Response<HotelMoreInfoResponseModel> response) {
                try{
                    hideCustomDialog();
                    if(response!=null && response.body()!=null && response.body().DataFileName!=null){
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("HotelInfo",response.body());
                        HotelMoreInfoFragment hotelMoreInfoFragment=new HotelMoreInfoFragment();
                        hotelMoreInfoFragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(
                                hotelMoreInfoFragment);
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<HotelMoreInfoResponseModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void hotelRoomData(HotelRoomInfoRequestModel model) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClientHotel().create(ApiInterface.class);
        Call<HotelRoomDataResponseModel> call = apiService.getHotelRoomDataPost(ApiConstants.RoomData, model);
        call.enqueue(new Callback<HotelRoomDataResponseModel>() {
            @Override
            public void onResponse(Call<HotelRoomDataResponseModel> call, Response<HotelRoomDataResponseModel> response) {
                try{
                    hideCustomDialog();
                    if(response!=null && response.body()!=null){
                        if(response.body().Rooms!=null && response.body().Rooms.size()>0){
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("HotelInfo",response.body());
                            HotelRoomDataFragment hotelRoomDataFragment=new HotelRoomDataFragment();
                            hotelRoomDataFragment.setArguments(bundle);
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(
                                    hotelRoomDataFragment);
                        }else {
                            Toast.makeText(context, "No room detail found", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<HotelRoomDataResponseModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
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
                        && dy>0) {

                    if(!(pageEnd>totalItemCount)) {
                        pageStart = pageStart + 10;
                        pageEnd = pageEnd + 10;
                        hotelAvailabilityRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                        hotelAvailabilityRequestModel.DeviceId=Common.getDeviceId(context);
                        hotelAvailabilityRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(                 EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        hotelAvailabilityRequestModel.CheckInDate=startDateToSend;
                        hotelAvailabilityRequestModel.CheckOutDate=endDateToSend;
                        hotelAvailabilityRequestModel.CountryName="Singapore";
                        hotelAvailabilityRequestModel.DestnationName="Singapore";
                        hotelAvailabilityRequestModel.DestnationCode="Singapore";
                        hotelAvailabilityRequestModel.NumberOfAdult="1";
                        hotelAvailabilityRequestModel.NumberOfChild="0";
                        hotelAvailabilityRequestModel.NumberOfDays="1";
                        hotelAvailabilityRequestModel.NumberOfRooms="1";
                        hotelAvailabilityRequestModel.Supplier="TBO";
                        hotelAvailabilityRequestModel.StaRating="All";
                        ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> roomOccupancyArrayList=new ArrayList<>();
                        HotelAvailabilityRequestModel.RoomOccupancy roomOccupancy=new HotelAvailabilityRequestModel().new RoomOccupancy();
                        roomOccupancy.Ages="1,1";
                        roomOccupancy.Adults=1;
                        roomOccupancy.Children=1;
                        roomOccupancy.TotalRoom=1;
                        hotelSearch(hotelAvailabilityRequestModel);
                    }


                }
//            }
        }
    };

    public void hotelSearch(HotelAvailabilityRequestModel busRequestModel) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClientHotel().create(ApiInterface.class);
        Call<HotelAvailabilityResponseModel> call = apiService.getHotelAvailPost(ApiConstants.HotelAvail, busRequestModel);
        call.enqueue(new Callback<HotelAvailabilityResponseModel>() {
            @Override
            public void onResponse(Call<HotelAvailabilityResponseModel>call, Response<HotelAvailabilityResponseModel> response) {
                try{
                    creditReportDataArrayList.clear();
                    hideCustomDialog();
                    if(response!=null){
                        if(response.body().StatusCode.equalsIgnoreCase("0")){
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                            creditReportDataArrayList.addAll(response.body().hotels);
                        }else if(response.body().StatusCode.equalsIgnoreCase("2")){
//                            creditReportDataArrayList.addAll(response.body().Data);
//                            hotelAvailabilityAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }else {
//                            busSearchArrayList.clear();
//                            busSearchListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
//                        busSearchArrayList.clear();
//                        busSearchListAdapter.notifyDataSetChanged();
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    hotelAvailabilityAdapter.notifyDataSetChanged();

                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<HotelAvailabilityResponseModel> call, Throwable t) {
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
                creditReportDataArrayList.clear();
                hotelAvailabilityAdapter.notifyDataSetChanged();

                //set date to fragment
                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );
                pageStart=1;
                pageEnd=10;

                pageStart=1;
                pageEnd=10;
                hotelAvailabilityRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                hotelAvailabilityRequestModel.DeviceId=Common.getDeviceId(context);
                hotelAvailabilityRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                hotelAvailabilityRequestModel.CheckInDate=startDateToSend;
                hotelAvailabilityRequestModel.CheckOutDate=endDateToSend;
                hotelAvailabilityRequestModel.CountryName="Singapore";
                hotelAvailabilityRequestModel.DestnationName="Singapore";
                hotelAvailabilityRequestModel.DestnationCode="Singapore";
                hotelAvailabilityRequestModel.NumberOfAdult="1";
                hotelAvailabilityRequestModel.NumberOfChild="0";
                hotelAvailabilityRequestModel.NumberOfDays="1";
                hotelAvailabilityRequestModel.NumberOfRooms="1";
                hotelAvailabilityRequestModel.Supplier="TBO";
                hotelAvailabilityRequestModel.StaRating="All";
                ArrayList<HotelAvailabilityRequestModel.RoomOccupancy> roomOccupancyArrayList=new ArrayList<>();
                HotelAvailabilityRequestModel.RoomOccupancy roomOccupancy=new HotelAvailabilityRequestModel().new RoomOccupancy();
                roomOccupancy.Ages="1,1";
                roomOccupancy.Adults=1;
                roomOccupancy.Children=1;
                roomOccupancy.TotalRoom=1;

                if(Common.checkInternetConnection(context)) {
                    hotelSearch(hotelAvailabilityRequestModel);
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


        }
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

                if (startDateCalendar.before(Calendar.getInstance())) {
                    Toast.makeText(context, "Can't select date before current date", Toast.LENGTH_SHORT).show();
                }else {
                    startDateDay=dayOfMonth;
                    startDateMonth=monthOfYear;
                    startDateYear=year;

                    start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
                    start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));

                    startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
                    setCheckOutDate();
                }

            }

        },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();


    }

    private void openEndDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDateCalendar = Calendar.getInstance();
                        endDateCalendar.set(year, monthOfYear, dayOfMonth);

                        if (endDateCalendar.before(startDateCalendar)) {
                            Toast.makeText(context, "Can't select date before check-In data", Toast.LENGTH_SHORT).show();
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
        datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        datePickerDialog.show();


    }

    private void setCheckOutDate() {
        if(startDateCalendar.after(endDateCalendar)){
            endDateCalendar=startDateCalendar;
            endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
            endDateDay =endDateCalendar.DAY_OF_MONTH;
            endDateMonth =endDateCalendar.MONTH;
            endDateYear =endDateCalendar.YEAR;

            end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
            end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));

            endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
        }
    }

}


