package com.justclick.clicknbook.Fragment.bus;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.BusAvailabilityAdapter;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.ResponseBody;

public class BusListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final int FARE=0,DEPARTURE=1,NONE=2;
    private final String TIME0_6="3", TIME6_12="4", TIME12_18="5", TIME18_00="6";
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private Context context;
    private BusListFragment fragment;
    private RecyclerView recyclerView;
    private BusAvailabilityAdapter busAvailabilityAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<BusSearchBean> arrayList, tempArrayList;
    private ImageView backArrow;
    private TextView fromCityTv,fromDateTv,ToCityTv,toDateTv;
    private LinearLayout lin_dateFilter;
    private String tripType="";
    int totalPageCount=0;
    LoginModel loginModel;
    //    Dialog
    Dialog dialog;
    boolean isNetFare=false;
    private int sortBy=FARE;
    private TextView applyTv;
    private CheckBox departureTime6Lin,departureTime11Lin,departureTime17Lin,departureTime23Lin,netFareCheckbox;
    private LinearLayout operatorLinear, busTypeLinear, fareLin,departureLin,noneLin;
    private String refund="", travelerNameDialog ="",busTypeDialog="", travelName="", busType="",
            timeFilter="", boardingType="",droppingType="";
    private ImageView flightFilter,cancelTv,fareImg,departureImg,noneImg;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        fragment=this;
        arrayList=new ArrayList<>();
        tempArrayList=new ArrayList<>();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bus_list, container, false);
        recyclerView =  view.findViewById(R.id.recyclerView);
        lin_dateFilter =  view.findViewById(R.id.lin_dateFilter);
        titleChangeListener.onToolBarTitleChange(getString(R.string.busSearchFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        //initialize date values
        backArrow=  view.findViewById(R.id.backArrow);
        fromCityTv=  view.findViewById(R.id.fromCityTv);
        fromDateTv=  view.findViewById(R.id.fromDateTv);
        ToCityTv=  view.findViewById(R.id.ToCityTv);
        toDateTv=  view.findViewById(R.id.toDateTv);
        backArrow.setOnClickListener(this);
        lin_dateFilter.setOnClickListener(this);

        setFont();
        if(getArguments().getSerializable("BusList")!=null) {
            arrayList = (ArrayList<BusSearchBean>) getArguments().getSerializable("BusList");
            tempArrayList.addAll(arrayList);
            fromCityTv.setText(getArguments().getString("fromCity"));
            ToCityTv.setText(getArguments().getString("toCity"));
            tripType=getArguments().getString("tripType");
            Date date,date1=null;
            String checkInDate= getArguments().getString("checkInDate");
            String checkOutDate= getArguments().getString("checkOutDate");
            try {
                date=Common.getServerDateFormat().parse(checkInDate);
                date1=Common.getServerDateFormat().parse(checkOutDate);
                String newDate=Common.getShowInTVDateFormat().format(date);
                String newDate1=Common.getShowInTVDateFormat().format(date1);
                fromDateTv.setText(newDate);
                toDateTv.setText(newDate1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        busAvailabilityAdapter=new BusAvailabilityAdapter(context, new BusAvailabilityAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<BusSearchBean> list, int position) {
                switch (view.getId()){
                    case R.id.itemLin:
                        if(Common.checkInternetConnection(context)){
                            BusSearchRequestModel searchRequestModel=new BusSearchRequestModel();
                            searchRequestModel.DeviceId=Common.getDeviceId(context);
                            searchRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            searchRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            searchRequestModel.TripID=list.get(position).routid;
                            searchRequestModel.Travel=list.get(position).travelName;
                            searchRequestModel.OpertorID=list.get(position).Operator;
//                            searchRequestModel.TripID="1000011925501687352";
                            busSeat(searchRequestModel, list.get(position));

                        }else {
                            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(context,"bus details",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cancellationTv:
                        openCancellationDialog(list.get(position));
                        break;
                }

            }

        },tempArrayList,totalPageCount);

        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(busAvailabilityAdapter);
//        recyclerView.getLayoutManager().scrollToPosition(10);
        applyFilter();

        return view;

    }

    private void openCancellationDialog(BusSearchBean busSearchBean) {
        final Dialog cancelDialog = new Dialog(context);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.bus_cancellation_dialog);
        final Window window= cancelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

//        Toast.makeText(context, busSearchBean.cancellationPolicy, Toast.LENGTH_SHORT).show();

        TextView close_btn=  cancelDialog.findViewById(R.id.close_btn);
        TextView cancel1Tv=  cancelDialog.findViewById(R.id.cancel1Tv);
        TextView cancel11Tv=  cancelDialog.findViewById(R.id.cancel11Tv);
        TextView cancel2Tv= cancelDialog.findViewById(R.id.cancel2Tv);
        TextView cancel21Tv= cancelDialog.findViewById(R.id.cancel21Tv);
        TextView cancel3Tv= cancelDialog.findViewById(R.id.cancel3Tv);
        TextView cancel31Tv= cancelDialog.findViewById(R.id.cancel31Tv);
        TextView cancel4Tv= cancelDialog.findViewById(R.id.cancel4Tv);
        TextView cancel41Tv= cancelDialog.findViewById(R.id.cancel41Tv);
        TextView cancel5Tv= cancelDialog.findViewById(R.id.cancel5Tv);
        TextView cancel51Tv= cancelDialog.findViewById(R.id.cancel51Tv);
        TextView specialTv= cancelDialog.findViewById(R.id.specialTv);

        if(busSearchBean.partialCancellationAllowed.equalsIgnoreCase("false")) {
            specialTv.setVisibility(View.VISIBLE);
            specialTv.setText("Note: Partial cancellation not allowed.");

        }else {
            specialTv.setVisibility(View.GONE);
        }

        String cancelPolicy=busSearchBean.cancellationPolicy; //0:12:100:0;12:24:50:0;24:-1:10:0
        String arr[]=cancelPolicy.split(";");
            try {
                cancel1Tv.setText(arr[0].split(":")[0]+" hour to "+arr[0].split(":")[1]+" hour ");
                cancel11Tv.setText(arr[0].split(":")[2]);
//                cancel2Tv.setText(arr[1].split(":")[0]+" hour to "+arr[1].split(":")[1]+" hour");
                cancel2Tv.setText(arr[1].split(":")[0]+" hour to "+(Integer.parseInt(arr[1].split(":")[1])<0?"Before ":arr[1].split(":")[1]));
                cancel21Tv.setText(arr[1].split(":")[2]);
                cancel3Tv.setText(arr[2].split(":")[0]+" hour to "+(Integer.parseInt(arr[2].split(":")[1])<0?"Before ":arr[2].split(":")[1]));
                cancel31Tv.setText(arr[2].split(":")[2]);
                cancel4Tv.setText(arr[3].split(":")[0]+" hour to "+(Integer.parseInt(arr[3].split(":")[1])<0?"Before ":arr[3].split(":")[1]));
                cancel41Tv.setText(arr[3].split(":")[2]);
                cancel5Tv.setText(arr[4].split(":")[0]+" hour to "+(Integer.parseInt(arr[4].split(":")[1])<0?"Before ":arr[4].split(":")[1]));
                cancel51Tv.setText(arr[4].split(":")[2]);
            }catch (Exception e){

        }
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancelDialog.dismiss();
            }
        });
        cancelDialog.show();
    }

    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.FlightCalenderTypeFace3(context);
        fromCityTv.setTypeface(face1);
        ToCityTv.setTypeface(face1);
        fromDateTv.setTypeface(face);
        toDateTv.setTypeface(face);
    }

    private void busSeat(Object requestModel, final BusSearchBean busSearchBean) {
        showCustomDialog();
        new NetworkCall().callBusService(requestModel, ApiConstants.TripDetails, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 1, busSearchBean);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandler(ResponseBody response, int i, BusSearchBean busSearchBean) {
        try{
            hideCustomDialog();
            ArrayList<SeatMapBean> arrayListSeatMap=parseBusSeatList(response.string());
            if(arrayListSeatMap!=null && arrayListSeatMap.size()>0){
                Bundle bundle = new Bundle();
                bundle.putSerializable("BusSeat", arrayListSeatMap);
                bundle.putSerializable("BusSearchBean", busSearchBean);
                bundle.putSerializable("List", arrayList);
                bundle.putString("tripType", tripType);
                BusSeatFragment fragment=new BusSeatFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<SeatMapBean> parseBusSeatList(String response) throws Exception {
        ArrayList<SeatMapBean> busSeatBeanArrayList=new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("Seats");
        busSeatBeanArrayList.clear();
        SeatMapBean seatMapBean;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            seatMapBean = new SeatMapBean();
            seatMapBean.available=(jsonObject1.getString("available"));
            seatMapBean.AgMarkup=(jsonObject1.getString("AgMarkup"));
            seatMapBean.Commission=(jsonObject1.getString("Commission"));
            seatMapBean.GrossFare=(jsonObject1.getString("GrossFare"));
            seatMapBean.NetTFare=(jsonObject1.getString("NetTFare"));
            seatMapBean.ServiceCharge=(jsonObject1.getString("ServiceCharge"));
            seatMapBean.TDS=(jsonObject1.getString("TDS"));
            seatMapBean.bankTrexAmt=(jsonObject1.getString("bankTrexAmt"));
            seatMapBean.bookingFee=(jsonObject1.getString("bookingFee"));
            seatMapBean.childFare=(jsonObject1.getString("childFare"));
            seatMapBean.concession=(jsonObject1.getString("concession"));
            seatMapBean.levyFare=(jsonObject1.getString("levyFare"));
            seatMapBean.tollFee=(jsonObject1.getString("tollFee"));
            seatMapBean.srtFee=(jsonObject1.getString("srtFee"));
//            seatMapBean.seatPosition=(jsonObject1.getString("seatPosition"));
            seatMapBean.baseFare=(jsonObject1.getString("baseFare"));
            seatMapBean.column=(jsonObject1.getString("column"));
            seatMapBean.fare=(jsonObject1.getString("fare"));
            seatMapBean.ladiesSeat=(jsonObject1.getString("ladiesSeat"));
            seatMapBean.length=(jsonObject1.getString("length"));
            seatMapBean.markupFareAbsolute=(jsonObject1.getString("markupFareAbsolute"));
            seatMapBean.markupFarePercentage=(jsonObject1.getString("markupFarePercentage"));
            seatMapBean.name=(jsonObject1.getString("name"));
            seatMapBean.operatorServiceChargeAbsolute=(jsonObject1.getString("operatorServiceChargeAbsolute"));
            seatMapBean.operatorServiceChargePercent=(jsonObject1.getString("operatorServiceChargePercent"));
            seatMapBean.row=(jsonObject1.getString("row"));
            seatMapBean.serviceTaxAbsolute=(jsonObject1.getString("serviceTaxAbsolute"));
            seatMapBean.serviceTaxPercentage=(jsonObject1.getString("serviceTaxPercentage"));
            seatMapBean.width=(jsonObject1.getString("width"));
            seatMapBean.zIndex=(jsonObject1.getString("zIndex"));
            busSeatBeanArrayList.add(seatMapBean);
        }
        return busSeatBeanArrayList;
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;

            case R.id.lin_dateFilter:
//                recyclerView.getLayoutManager().scrollToPosition(10);
                if(dialog==null){
                    openFilterDialog();
                }else{
                    dialog.show();
                }
                break;

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){

            case R.id.departureTime6Lin:
                if (isChecked) {
                    timeFilter=timeFilter+TIME0_6;
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    timeFilter=timeFilter.replace(TIME0_6,"");
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.departureTime11Lin:
                if (isChecked) {
                    timeFilter=timeFilter+TIME6_12;
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    timeFilter=timeFilter.replace(TIME6_12,"");
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.departureTime17Lin:
                if (isChecked) {
                    timeFilter=timeFilter+TIME12_18;
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    timeFilter=timeFilter.replace(TIME12_18,"");
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.departureTime23Lin:
                if (isChecked) {
                    timeFilter=timeFilter+TIME18_00;
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }else {
                    timeFilter=timeFilter.replace(TIME18_00,"");
//                    Toast.makeText(context, buttonView.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.netFareCheckbox:
                if (isChecked) {
                    isNetFare=true;
                }else {
                    isNetFare=false;
                }
                break;
        }
    }

    private void openFilterDialog() {
        refund="";
        travelerNameDialog ="";
        busTypeDialog ="";
        timeFilter="";
        isNetFare=false;
        dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bus_filter);
        operatorLinear = dialog.findViewById(R.id.operatorLinear);
        busTypeLinear = dialog.findViewById(R.id.busTypeLinear);
        cancelTv=  dialog.findViewById(R.id.cancelTv);
        applyTv=  dialog.findViewById(R.id.applyTv);
        departureTime6Lin= dialog.findViewById(R.id.departureTime6Lin);
        departureTime11Lin= dialog.findViewById(R.id.departureTime11Lin);
        departureTime17Lin= dialog.findViewById(R.id.departureTime17Lin);
        departureTime23Lin= dialog.findViewById(R.id.departureTime23Lin);
        netFareCheckbox=  dialog.findViewById(R.id.netFareCheckbox);
        departureTime6Lin.setOnCheckedChangeListener(fragment);
        departureTime11Lin.setOnCheckedChangeListener(fragment);
        departureTime17Lin.setOnCheckedChangeListener(fragment);
        departureTime23Lin.setOnCheckedChangeListener(fragment);
        netFareCheckbox.setOnCheckedChangeListener(fragment);

        fareImg=  dialog.findViewById(R.id.fareImg);
        departureImg=  dialog.findViewById(R.id.departureImg);
        noneImg=  dialog.findViewById(R.id.noneImg);
        noneImg.setBackgroundResource(R.drawable.radio_button_checked);
        fareLin=  dialog.findViewById(R.id.fareLin);
        departureLin=  dialog.findViewById(R.id.departureLin);
        noneLin=  dialog.findViewById(R.id.noneLin);

        fareLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fareImg.setBackgroundResource(R.drawable.radio_button_checked);
                departureImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                noneImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                sortBy=FARE;
            }
        });
        departureLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fareImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                departureImg.setBackgroundResource(R.drawable.radio_button_checked);
                noneImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                sortBy=DEPARTURE;
            }
        });
        noneLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fareImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                departureImg.setBackgroundResource(R.drawable.radio_button_unchecked);
                noneImg.setBackgroundResource(R.drawable.radio_button_checked);
                sortBy=NONE;
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        applyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                applyFilter();
                busAvailabilityAdapter.notifyDataSetChanged();
            }
        });

//        cancelTv.setOnClickListener(fragment);

        setCheckBoxValues();
        setAirlineCheckBoxListeners();

        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void applyFilter() {
        tempArrayList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if(travelerNameDialog.length()==0 && busTypeDialog.length()==0 && timeFilter.length()==0){
                tempArrayList.addAll(arrayList);
                break;
            }else {
                checkFilter(i);
            }


        }

        if(sortBy==FARE){
            Collections.sort(tempArrayList, new Comparator<BusSearchBean>() {
                @Override
                public int compare(BusSearchBean lhs, BusSearchBean rhs) {
                    return (int) (lhs.fareDetails.get(0).totalFare-rhs.fareDetails.get(0).GrossFare);
                }
            });
        }else if(sortBy==DEPARTURE){
            Collections.sort(tempArrayList, new Comparator<BusSearchBean>() {
                @Override
                public int compare(BusSearchBean lhs, BusSearchBean rhs) {
                    return Integer.parseInt(lhs.departureTime)-Integer.parseInt(rhs.departureTime);
                }
            });
            /*Collections.sort(tempArrayList, new Comparator<BusSearchBean>() {
                @Override
                public int compare(BusSearchBean lhs, BusSearchBean rhs) {
                    return lhs.travelName.toUpperCase().compareTo(rhs.travelName.toUpperCase());
                }
            });*/
        }
    }

    private void checkFilter(int i){

        if(travelerNameDialog.length()>0 && travelerNameDialog.contains(arrayList.get(i).travelName.toString())
                && busTypeDialog.length()>0 && busTypeDialog.contains(arrayList.get(i).bustype.toString()))
        {
            if(timeFilter.length()==0){
                tempArrayList.add(arrayList.get(i));
            }else {
                if((timeFilter.contains(TIME0_6) && (Integer.parseInt(arrayList.get(i).departureTime)<360)) ||
                        (timeFilter.contains(TIME6_12) && (Integer.parseInt(arrayList.get(i).departureTime)>=360) && (Integer.parseInt(arrayList.get(i).departureTime)<720)) ||
                        (timeFilter.contains(TIME12_18) && (Integer.parseInt(arrayList.get(i).departureTime)>=720) && (Integer.parseInt(arrayList.get(i).departureTime)<1008))||
                        (timeFilter.contains(TIME18_00) && (Integer.parseInt(arrayList.get(i).departureTime)>=1008) && (Integer.parseInt(arrayList.get(i).departureTime)<1440))){
                    tempArrayList.add(arrayList.get(i));
                }
            }

        } else if (busTypeDialog.length() > 0 &&
                busTypeDialog.contains(arrayList.get(i).bustype.toString())&& travelerNameDialog.length()== 0) {
            if(timeFilter.length()==0){
                tempArrayList.add(arrayList.get(i));
            }else {
                if((timeFilter.contains(TIME0_6) && (Integer.parseInt(arrayList.get(i).departureTime)<360)) ||
                        (timeFilter.contains(TIME6_12) && (Integer.parseInt(arrayList.get(i).departureTime)>=360) && (Integer.parseInt(arrayList.get(i).departureTime)<720)) ||
                        (timeFilter.contains(TIME12_18) && (Integer.parseInt(arrayList.get(i).departureTime)>=720) && (Integer.parseInt(arrayList.get(i).departureTime)<1008))||
                        (timeFilter.contains(TIME18_00) && (Integer.parseInt(arrayList.get(i).departureTime)>=1008) && (Integer.parseInt(arrayList.get(i).departureTime)<1440))){
                    tempArrayList.add(arrayList.get(i));
                }
            }

        } else if (travelerNameDialog.length() > 0 &&
                travelerNameDialog.contains(arrayList.get(i).travelName.toString())&&busTypeDialog.length()==0) {
            if(timeFilter.length()==0){
                tempArrayList.add(arrayList.get(i));
            }else {
                if((timeFilter.contains(TIME0_6) && (Integer.parseInt(arrayList.get(i).departureTime)<360)) ||
                        (timeFilter.contains(TIME6_12) && (Integer.parseInt(arrayList.get(i).departureTime)>=360) && (Integer.parseInt(arrayList.get(i).departureTime)<720)) ||
                        (timeFilter.contains(TIME12_18) && (Integer.parseInt(arrayList.get(i).departureTime)>=720) && (Integer.parseInt(arrayList.get(i).departureTime)<1008))||
                        (timeFilter.contains(TIME18_00) && (Integer.parseInt(arrayList.get(i).departureTime)>=1008) && (Integer.parseInt(arrayList.get(i).departureTime)<1440))){
                    tempArrayList.add(arrayList.get(i));
                }
            }
        }
        else if (travelerNameDialog.length() == 0 && busTypeDialog.length()==0)  {
            if(timeFilter.length()==0){
                tempArrayList.add(arrayList.get(i));
            }else {
                if((timeFilter.contains(TIME0_6) && (Integer.parseInt(arrayList.get(i).departureTime)<360)) ||
                        (timeFilter.contains(TIME6_12) && (Integer.parseInt(arrayList.get(i).departureTime)>=360) && (Integer.parseInt(arrayList.get(i).departureTime)<720)) ||
                        (timeFilter.contains(TIME12_18) && (Integer.parseInt(arrayList.get(i).departureTime)>=720) && (Integer.parseInt(arrayList.get(i).departureTime)<1008))||
                        (timeFilter.contains(TIME18_00) && (Integer.parseInt(arrayList.get(i).departureTime)>=1008) && (Integer.parseInt(arrayList.get(i).departureTime)<1440))){
                    tempArrayList.add(arrayList.get(i));
                }
            }
        }
    }

    private void setAirlineCheckBoxListeners() {
        int lengthTraveler=travelName.split("#").length;
        int lengthBusType=busType.split("#").length;

        AppCompatCheckBox checkBoxTraveler[]=new AppCompatCheckBox[lengthTraveler];
        AppCompatCheckBox checkBoxBusType[]=new AppCompatCheckBox[lengthBusType];

        for(int i=0; i<lengthTraveler; i++){
            checkBoxTraveler[i]=new AppCompatCheckBox(context);
            checkBoxTraveler[i].setText(travelName.split("#")[i]);
            checkBoxTraveler[i].setButtonDrawable(R.drawable.checkbox_custom);
            checkBoxTraveler[i].setPadding((int) getResources().getDimension(R.dimen.small_margin),0,0,0);
            final int finalI = i;
            checkBoxTraveler[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        travelerNameDialog = travelerNameDialog +buttonView.getText().toString();
                    }else {
                        travelerNameDialog = travelerNameDialog.replace(buttonView.getText().toString(),"");
                    }
                }
            });
            operatorLinear.addView(checkBoxTraveler[i]);
        }

        for(int i=0; i<lengthBusType; i++){
            checkBoxBusType[i]=new AppCompatCheckBox(context);
            checkBoxBusType[i].setText(busType.split("#")[i]);
            checkBoxBusType[i].setButtonDrawable(R.drawable.checkbox_custom);
            checkBoxBusType[i].setPadding((int) getResources().getDimension(R.dimen.small_margin),0,0,0);
            final int finalI = i;
            checkBoxBusType[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        busTypeDialog = busTypeDialog +buttonView.getText().toString();
                    }else {
                        busTypeDialog = busTypeDialog.replace(buttonView.getText().toString(),"");
                    }
                }
            });
            busTypeLinear.addView(checkBoxBusType[i]);
        }
    }
    private void setCheckBoxValues() {
        travelName=arrayList.get(0).travelName;
        busType=arrayList.get(0).bustype;
        ArrayList<BusSearchBean> travelerArray=new ArrayList<>();
        ArrayList<BusSearchBean> busTypeArray=new ArrayList<>();
        ArrayList<BusSearchBean> boardingPointArray=new ArrayList<>();
        ArrayList<BusSearchBean> droppingPointArray=new ArrayList<>();
        travelerArray.add(arrayList.get(0));
        busTypeArray.add(arrayList.get(0));
        boardingPointArray.add(arrayList.get(0));

        for(int i = 1; i< arrayList.size(); i++){
            if(travelerArray.size()==0){
                travelerArray.add(arrayList.get(i));
                travelName=travelName+"#"+arrayList.get(i).travelName;
            }else {
                int k=0;
                for(int j=0; j<travelerArray.size(); j++){
                    if(travelerArray.get(j).travelName.
                            equalsIgnoreCase(arrayList.get(i).travelName)){
                        break;
                    }
                    if(j==travelerArray.size()-1){
                        travelerArray.add(arrayList.get(i));
                        travelName=travelName+"#"+arrayList.get(i).travelName;
                    }
                }
            }

            if(busTypeArray.size()==0){
                busTypeArray.add(arrayList.get(i));
                busType=busType+"#"+arrayList.get(i).bustype;
            }else {
                int k=0;
                for(int j=0; j<busTypeArray.size(); j++){
                    if(busTypeArray.get(j).bustype.
                            equalsIgnoreCase(arrayList.get(i).bustype)){
                        break;
                    }
                    if(j==busTypeArray.size()-1){
                        busTypeArray.add(arrayList.get(i));
                        busType=busType+"#"+arrayList.get(i).bustype;
                    }
                }
            }

//            if(boardingPointArray.size()==0){
//                boardingPointArray.add(arrayList.get(i));
//                boardingType=boardingType+"#"+arrayList.get(i).boardingTimes.get(i).bpName;
//            }else {
//                for(int j=0; j<boardingPointArray.size(); j++){
//                    if(boardingPointArray.get(j).bustype.
//                            equalsIgnoreCase(arrayList.get(i).boardingTimes.get(i).bpName)){
//                        break;
//                    }
//                    if(j==boardingPointArray.size()-1){
//                        boardingPointArray.add(arrayList.get(i));
//                        boardingType=boardingType+"#"+arrayList.get(i).boardingTimes.get(i).bpName;
//                    }
//                }
//            }

        }
    }

    @Override
    public void onResume() {
        applyFilter();
        super.onResume();
    }
}

