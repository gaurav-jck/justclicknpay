package com.justclick.clicknbook.Fragment.flight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.FlightAvailabilityIntlRoundAdapter;
import com.justclick.clicknbook.adapter.FlightAvailabilitySplitAdapter;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.AirFareValidateClass;

import java.util.ArrayList;

public class FlightListRoundFragment extends Fragment implements View.OnClickListener{
    private ToolBarTitleChangeListener titleChangeListener;
    private Context context;
    private FlightListRoundFragment fragment;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerViewOutBound, recyclerViewInBound;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private FlightAvailabilitySplitAdapter flightInBoundAdapter, flightOutBoundAdapter;
    private ArrayList<FlightSearchDataModel> arrayList,tempArrayList;
    private FlightAvailabilityIntlRoundAdapter flightAvailabilityAdapter;
    private ArrayList<FlightSearchDataModel.OneWay> arrayListInBound, arrayListOutBound,
            arrayListInBoundTemp, arrayListOutBoundTemp;
    private FlightSearchDataModel.OneWay outBoundModel;
    private FlightSearchDataModel.OneWay inBoundModel;
    private TextView fromCityTv,ToCityTv,FromdateTv,ToDateTv,travellerInfoTv,
            outBoundCityCodeTv, inBoundCityCodeTv, outBoundDateTv, inBoundDateTv, applyTv,
            selectedCityTv,selectedAmount,roundSelectedCityTv,roundSelectedAmount,totalAmountTv, splitViewTv, roundViewTv;
    private String fromCityCode="", toCityCode="";
    private View view1;
    private int totalPageCount=0, outBoundPosition=-1, inBoundPosition=-1;
    private ImageView flightFilter,refundTv,nonStopFlightImg,oneStopFlightImg,twoStopFlightImg,flightNameImg,cancelTv,backArrowImg;
    private LinearLayout refundLinear,nonStopFlightLin,oneStopFlightLin,twoStopFlightLin,flightNameLin,checkboxLinear,llnear,linBottom;
    Dialog dialog;
    String refund="";
    private String flightNameFromDialog="", stopType="";
    private int outBoundAmount =0, inBoundAmount =0;
    private int categoriesValues= 0;
    private String SplRound="N";

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
        arrayList=new ArrayList<>();
        fragment=this;
        arrayListOutBound=new ArrayList<>();
        arrayListInBound =new ArrayList<>();
        arrayListOutBoundTemp=new ArrayList<>();
        arrayListInBoundTemp =new ArrayList<>();
        tempArrayList =new ArrayList<>();

        //initialize date values
        fromCityCode=getArguments().getString("fromCityCode");
        toCityCode=getArguments().getString("toCityCode");
        if(getArguments().getSerializable("arrayListOutBound")!=null) {
            arrayList = (ArrayList<FlightSearchDataModel>) getArguments().getSerializable("FlightList");
            arrayListOutBound = (ArrayList<FlightSearchDataModel.OneWay>) getArguments().getSerializable("arrayListOutBound");
            arrayListInBound = (ArrayList<FlightSearchDataModel.OneWay>) getArguments().getSerializable("arrayListInBound");
        }
       /* for(int i=0; i<arrayList.size(); i++){
            if(arrayList.get(i).SCode.equalsIgnoreCase(fromCityCode)){
                arrayListOutBound.add(arrayList.get(i));
            }else {
                arrayListInBound.add(arrayList.get(i));
            }
        }*/

        arrayListOutBoundTemp.addAll(arrayListOutBound);
        arrayListInBoundTemp.addAll(arrayListInBound);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_flight_list_round, container, false);
        recyclerViewInBound = (RecyclerView) view.findViewById(R.id.recyclerViewInBound);
        recyclerViewOutBound = (RecyclerView) view.findViewById(R.id.recyclerViewOutBound);
        titleChangeListener.onToolBarTitleChange(getString(R.string.flightSearchFragmentTitle));
        fromCityTv= (TextView) view.findViewById(R.id.fromCityTv);
        ToCityTv= (TextView) view.findViewById(R.id.ToCityTv);
        inBoundCityCodeTv = (TextView) view.findViewById(R.id.inBoundCityCodeTv);
        outBoundCityCodeTv = (TextView) view.findViewById(R.id.outBoundCityCodeTv);
        outBoundDateTv = (TextView) view.findViewById(R.id.outBoundDateTv);
        inBoundDateTv = (TextView) view.findViewById(R.id.inBoundDateTv);
        ToCityTv= (TextView) view.findViewById(R.id.ToCityTv);
        FromdateTv= (TextView) view.findViewById(R.id.FromdateTv);
        ToDateTv= (TextView) view.findViewById(R.id.ToDateTv);
        travellerInfoTv= (TextView) view.findViewById(R.id.travellerInfoTv);
        view.findViewById(R.id.flightFilter).setOnClickListener(this);
        view.findViewById(R.id.bookNowTv).setOnClickListener(this);
        selectedCityTv= (TextView) view.findViewById(R.id.selectedCityTv);
        selectedAmount= (TextView) view.findViewById(R.id.selectedAmount);
        roundSelectedCityTv= (TextView) view.findViewById(R.id.roundSelectedCityTv);
        roundSelectedAmount= (TextView) view.findViewById(R.id.roundSelectedAmount);
        totalAmountTv= (TextView) view.findViewById(R.id.totalAmountTv);
        backArrowImg= (ImageView) view.findViewById(R.id.backArrowImg);
        splitViewTv = (TextView) view.findViewById(R.id.splitViewTv);
        roundViewTv = (TextView) view.findViewById(R.id.roundViewTv);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        llnear= (LinearLayout) view.findViewById(R.id.llnear);
        linBottom= (LinearLayout) view.findViewById(R.id.linBottom);
        view1= (View) view.findViewById(R.id.view);
        roundViewTv.setOnClickListener(fragment);
        splitViewTv.setOnClickListener(fragment);
        backArrowImg.setOnClickListener(fragment);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        String checkInDate = getArguments().getString("checkInDate");
        String checkOutDate = getArguments().getString("checkOutDate");
        String noOfTraveller = getArguments().getString("noOfTraveller");

        outBoundCityCodeTv.setText(fromCityCode+"-"+toCityCode);
        inBoundCityCodeTv.setText(toCityCode+"-"+fromCityCode);
        fromCityTv.setText(fromCityCode);
        ToCityTv.setText(toCityCode);
        outBoundDateTv.setText(checkInDate);
        inBoundDateTv.setText(checkOutDate);
        FromdateTv.setText(checkInDate);
        ToDateTv.setText(checkOutDate);
        travellerInfoTv.setText(noOfTraveller);
        selectedCityTv.setText(fromCityCode+"-"+toCityCode);
        roundSelectedCityTv.setText(toCityCode+"-"+fromCityCode);

        flightOutBoundAdapter = new FlightAvailabilitySplitAdapter(context, new FlightAvailabilitySplitAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position) {
                switch (view.getId()){
                    case R.id.linear_main_menu:
                        outBoundModel=arrayListOutBound.get(position);
                        outBoundPosition=position;
                        outBoundAmount =arrayListOutBound.get(position).FinalPrice;
                        selectedAmount.setText(outBoundAmount +"");
                        totalAmountTv.setText(outBoundAmount + inBoundAmount +"");
                }
            }
        },arrayListOutBoundTemp,totalPageCount);
        recyclerViewOutBound.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewOutBound.setAdapter(flightOutBoundAdapter);

        flightInBoundAdapter = new FlightAvailabilitySplitAdapter(context, new FlightAvailabilitySplitAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position) {
                switch (view.getId()){
                    case R.id.linear_main_menu:
                        inBoundModel = arrayListInBound.get(position);
                        inBoundPosition=position;
                        inBoundAmount =arrayListInBound.get(position).FinalPrice;
                        roundSelectedAmount.setText(inBoundAmount +"");
                        totalAmountTv.setText(outBoundAmount + inBoundAmount +"");
                }
            }
        }, arrayListInBoundTemp,totalPageCount);
        recyclerViewInBound.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewInBound.setAdapter(flightInBoundAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bookNowTv:
                if(outBoundModel==null || inBoundModel==null){
                    Toast.makeText(context, R.string.select_source_dest, Toast.LENGTH_SHORT).show();
                }else{
                    bookFlight();
                }
                break;
            case R.id.flightFilter:
                refund="";
                flightNameFromDialog="";
                stopType="";
                dialog = new Dialog(context, R.style.Theme_Design_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.flight_filter);
                refundLinear= (LinearLayout) dialog.findViewById(R.id.refundLinear);
                nonStopFlightLin= (LinearLayout) dialog.findViewById(R.id.nonStopFlightLin);
                oneStopFlightLin= (LinearLayout) dialog.findViewById(R.id.oneStopFlightLin);
                twoStopFlightLin= (LinearLayout) dialog.findViewById(R.id.twoStopFlightLin);
                flightNameLin= (LinearLayout) dialog.findViewById(R.id.flightNameLin);
                checkboxLinear= (LinearLayout) dialog.findViewById(R.id.checkboxLinear);
                cancelTv= (ImageView) dialog.findViewById(R.id.cancelTv);
                refundTv= (ImageView) dialog.findViewById(R.id.refundTv);
                nonStopFlightImg= (ImageView) dialog.findViewById(R.id.nonStopFlightImg);
                oneStopFlightImg= (ImageView) dialog.findViewById(R.id.oneStopFlightImg);
                twoStopFlightImg= (ImageView) dialog.findViewById(R.id.twoStopFlightImg);
                flightNameImg= (ImageView) dialog.findViewById(R.id.flightNameImg);
                applyTv= (TextView) dialog.findViewById(R.id.applyTv);
                applyTv.setOnClickListener(fragment);
                refundLinear.setOnClickListener(fragment);
                cancelTv.setOnClickListener(fragment);
                nonStopFlightLin.setOnClickListener(fragment);
                oneStopFlightLin.setOnClickListener(fragment);
                twoStopFlightLin.setOnClickListener(fragment);
                flightNameLin.setOnClickListener(fragment);

//            final String flightNames=categoriesValues();

                if(categoriesValues==0) {
                    setAirlineCheckBoxListeners(categoriesValues());
                }else
                {
                    setAirlineCheckBoxListeners(roundCategoriesValues());
                }
         /*   for (int i = 0; i < flightNames.split("#").length; i++) {
                final CheckBox rdbtn = new CheckBox(context);
                rdbtn.setText(flightNames.split("#")[i]);
                rdbtn.setId(i);
                rdbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(rdbtn.getText().toString().equalsIgnoreCase("All")){
                            flightNameFromDialog=flightNames;
                            for(int j = 0; j < flightNames.split("#").length; j++){

                            }
                        }else {
                            flightNameFromDialog= rdbtn.getText().toString();
                        }
//                    Toast.makeText(context,BankNameFromDialog,Toast.LENGTH_SHORT).show();
                    }
                });

                checkboxLinear.addView(rdbtn);
            }*/

                final Window window= dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                dialog.show();
                break;
            case R.id.cancelTv:
                dialog.dismiss();
                break;

            case R.id.splitViewTv:
                SplRound="N";
                categoriesValues=0;
                splitViewTv.setBackgroundResource(R.color.dark_blue_color);
                roundViewTv.setBackgroundResource(R.color.app_blue_color);
                view1.setVisibility(View.VISIBLE);
                linBottom.setVisibility(View.VISIBLE);
                llnear.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                break;

            case R.id.roundViewTv:
                if(arrayList.get(0).RoundTrip!=null && arrayList.get(0).RoundTrip.size()>0){
                    SplRound="Y";
                    categoriesValues=1;
                    llnear.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    view1.setVisibility(View.GONE);
                    linBottom.setVisibility(View.GONE);
                    tempArrayList.addAll(arrayList);
                    flightAvailabilityAdapter =new FlightAvailabilityIntlRoundAdapter(context, new FlightAvailabilityIntlRoundAdapter.OnRecyclerItemClickListener() {
                        @Override
                        public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel> list, int position) {
                            switch (view.getId()){
                                case R.id.linear_main_menu:
                                    FlightSearchDataModel.OneWay outBoundModel;FlightSearchDataModel.OneWay inBoundModel;
                                    outBoundModel=list.get(0).RoundTrip.get(position);
                                    inBoundModel= getInboundModel(list.get(0).RoundTrip.get(position));

                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("outBoundModel",outBoundModel);
                                    bundle.putSerializable("inBoundModel",inBoundModel);
                                    bundle.putInt("tripType",2);
                                    ArrayList<FlightSearchDataModel.OneWay> airlineArray=new ArrayList<>();
                                    airlineArray.add(outBoundModel);
                                    airlineArray.add(inBoundModel);

                                    new AirFareValidateClass().airFareValidation(airlineArray,bundle,context, "TWO", SplRound);
                            }
                        }
                    },tempArrayList,totalPageCount);
                    layoutManager=new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(flightAvailabilityAdapter);
                    splitViewTv.setBackgroundResource(R.color.app_blue_color);
                    roundViewTv.setBackgroundResource(R.color.dark_blue_color);
                }else {
                    Toast.makeText(context, R.string.roundFareNotAvail, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.refundLinear:
                if(refund.equalsIgnoreCase("")){
                    refundTv.setBackgroundResource(R.drawable.check_box_checked);
                    refund="REF";
                }else {
                    refundTv.setBackgroundResource(R.drawable.check_box_unchecked);
                    refund="";
                }
                break;
            case R.id.nonStopFlightLin:
                if(!stopType.contains("0")){
                    nonStopFlightImg.setBackgroundResource(R.drawable.check_box_checked);
                    stopType=stopType+"0";
                }else {
                    nonStopFlightImg.setBackgroundResource(R.drawable.check_box_unchecked);
                    stopType=stopType.replace("0","");
                }
                break;
            case R.id.oneStopFlightLin:
                if(!stopType.contains("1")) {
                    oneStopFlightImg.setBackgroundResource(R.drawable.check_box_checked);
                    stopType=stopType+"1";
                }else {
                    oneStopFlightImg.setBackgroundResource(R.drawable.check_box_unchecked);
                    stopType=stopType.replace("1","");
                }

                break;
            case R.id.twoStopFlightLin:
                if(!stopType.contains("2")) {
                    twoStopFlightImg.setBackgroundResource(R.drawable.check_box_checked);
                    stopType=stopType+"2";
                }else {
                    twoStopFlightImg.setBackgroundResource(R.drawable.check_box_unchecked);
                    stopType=stopType.replace("2","");
                }
                break;
            case R.id.flightNameLin:
                flightNameImg.setBackgroundResource(R.drawable.check_box_checked);
                break;
            case R.id.applyTv:
                dialog.dismiss();
                if(categoriesValues==0) {
                    applyFilter();
                    flightOutBoundAdapter.notifyDataSetChanged();
                    flightInBoundAdapter.notifyDataSetChanged();
                }else {
                    applyFilterRound();
                    flightAvailabilityAdapter.notifyDataSetChanged();
                }


                if(outBoundPosition>0 && outBoundPosition<arrayListOutBoundTemp.size()){
                    outBoundModel=arrayListOutBoundTemp.get(outBoundPosition);
                }else {
                    outBoundModel=null;
                }
                if(inBoundPosition>0 && inBoundPosition<arrayListInBoundTemp.size()){
                    inBoundModel=arrayListInBoundTemp.get(inBoundPosition);
                }else {
                    inBoundModel=null;
                }
                break;
            case R.id.backArrowImg:
                getFragmentManager().popBackStack();
                break;

        }
    }

    private FlightSearchDataModel.OneWay getInboundModel(FlightSearchDataModel.OneWay flightSearchRoundIntDataModel) {
        FlightSearchDataModel.OneWay inBoundModel=(new FlightSearchDataModel()).new OneWay();
        inBoundModel.ATime=flightSearchRoundIntDataModel.ATime_r;
        inBoundModel.ADate=flightSearchRoundIntDataModel.ADate_r;
        inBoundModel.AdtOT=flightSearchRoundIntDataModel.AdtOT_r;
        inBoundModel.AdtPrice=flightSearchRoundIntDataModel.AdtPrice_r;
        inBoundModel.AdtTax=flightSearchRoundIntDataModel.AdtTax_r;
        inBoundModel.AdtYq=flightSearchRoundIntDataModel.AdtYq_r;
        inBoundModel.ChdPrice=flightSearchRoundIntDataModel.ChdPrice_r;
        inBoundModel.ChdOT=flightSearchRoundIntDataModel.ChdOT_r;
        inBoundModel.ChdTax=flightSearchRoundIntDataModel.ChdTax_r;
        inBoundModel.ChdYq=flightSearchRoundIntDataModel.ChdYq_r;
        inBoundModel.DCity=flightSearchRoundIntDataModel.DCity_r;
        inBoundModel.DCode=flightSearchRoundIntDataModel.DCode_r;
        inBoundModel.DDate=flightSearchRoundIntDataModel.DDate_r;
        inBoundModel.DTime=flightSearchRoundIntDataModel.DTime_r;
        inBoundModel.Duration=flightSearchRoundIntDataModel.Duration_r;
        inBoundModel.FlightId= Integer.parseInt(flightSearchRoundIntDataModel.FlightId_r);
        inBoundModel.FlightName=flightSearchRoundIntDataModel.FlightName_r;
        inBoundModel.IsIntl=flightSearchRoundIntDataModel.IsIntl_r;
        inBoundModel.SCode=flightSearchRoundIntDataModel.SCode_r;
        inBoundModel.SCity=flightSearchRoundIntDataModel.SCity_r;
        inBoundModel.Refundable=flightSearchRoundIntDataModel.Refundable_r;
        inBoundModel.Seats=flightSearchRoundIntDataModel.Seats_r;
        inBoundModel.Stop=flightSearchRoundIntDataModel.Stop_r;
        inBoundModel.Supplier=flightSearchRoundIntDataModel.Supplier_r;
        inBoundModel.FareKey=flightSearchRoundIntDataModel.FareKey_r;
        inBoundModel.Sequence=flightSearchRoundIntDataModel.Sequence_r;
        inBoundModel.InfPrice=flightSearchRoundIntDataModel.InfPrice_r;
        inBoundModel.InfTax=flightSearchRoundIntDataModel.InfTax_r;
        inBoundModel.InfOT=flightSearchRoundIntDataModel.InfOT_r;
        inBoundModel.InfYq=flightSearchRoundIntDataModel.InfYq_r;
        inBoundModel.FinalPrice=flightSearchRoundIntDataModel.FinalPrice_r;
        inBoundModel.Flights=flightSearchRoundIntDataModel.Flights_r;

        return inBoundModel;
    }

    private void bookFlight() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("outBoundModel",outBoundModel);
        bundle.putSerializable("inBoundModel",inBoundModel);
        bundle.putInt("tripType",2);
        ArrayList<FlightSearchDataModel.OneWay> airlineArray=new ArrayList<>();
        airlineArray.add(outBoundModel);
        airlineArray.add(inBoundModel);

        new AirFareValidateClass().airFareValidation(airlineArray,bundle,context, "TWO", SplRound);
    }

    private void applyFilter() {
        arrayListOutBoundTemp.clear();
        for (int i = 0; i < arrayListOutBound.size(); i++) {
            if (stopType.contains("0") && arrayListOutBound.get(i).Flights.size() == 1) {
                if (refund.length() == 0 || arrayListOutBound.get(i).Refundable.toString().equalsIgnoreCase(refund + "")) {
                    if (flightNameFromDialog.length() > 0 &&
                            flightNameFromDialog.contains(arrayListOutBound.get(i).FlightName.toString())) {
                        arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    } else {
                        if (flightNameFromDialog.length() == 0)
                            arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    }
                }
            } else if (stopType.contains("1") && arrayListOutBound.get(i).Flights.size() == 2) {
                if (refund.length() == 0 || arrayListOutBound.get(i).Refundable.toString().equalsIgnoreCase(refund)) {
                    if (flightNameFromDialog.length() > 0 &&
                            flightNameFromDialog.contains(arrayListOutBound.get(i).FlightName.toString())) {
                        arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    } else {
                        if (flightNameFromDialog.length() == 0)
                            arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    }
                }
            } else if (stopType.contains("2") && arrayListOutBound.get(i).Flights.size() == 3) {
                if (refund.length() == 0 || arrayListOutBound.get(i).Refundable.toString().equalsIgnoreCase(refund)) {
                    if (flightNameFromDialog.length() > 0 &&
                            flightNameFromDialog.contains(arrayListOutBound.get(i).FlightName.toString())) {
                        arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    } else {
                        if (flightNameFromDialog.length() == 0)
                            arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    }
                }
            } else {
                if (stopType.length() == 0 && (refund.length() == 0 || arrayListOutBound.get(i).Refundable.toString().equalsIgnoreCase(refund))) {
                    if (flightNameFromDialog.length() > 0 &&
                            flightNameFromDialog.contains(arrayListOutBound.get(i).FlightName.toString())) {
                        arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    } else {
                        if (flightNameFromDialog.length() == 0)
                            arrayListOutBoundTemp.add(arrayListOutBound.get(i));
                    }
                }
            }
        }
        arrayListInBoundTemp.clear();
        for (int j = 0; j < arrayListInBound.size(); j++) {
            if(stopType.contains("0") && arrayListInBound.get(j).Flights.size() == 1 ) {
                if(refund.length()==0 || arrayListInBound.get(j).Refundable.toString().equalsIgnoreCase(refund+"")) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayListInBound.get(j).FlightName.toString())) {
                        arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }
                }
            }else if(stopType.contains("1") && arrayListInBound.get(j).Flights.size() == 2 ) {
                if(refund.length()==0 || arrayListInBound.get(j).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayListInBound.get(j).FlightName.toString())) {
                        arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }
                }
            }else if(stopType.contains("2") && arrayListInBound.get(j).Flights.size() == 3 ) {
                if(refund.length()==0 || arrayListInBound.get(j).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayListInBound.get(j).FlightName.toString())) {
                        arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }
                }
            }else {
                if(stopType.length()==0 && (refund.length()==0 || arrayListInBound.get(j).Refundable.toString().equalsIgnoreCase(refund))) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayListInBound.get(j).FlightName.toString())) {
                        arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            arrayListInBoundTemp.add(arrayListInBound.get(j));
                    }
                }
            }
        }
    }


//    private void applyFilterRound() {
//        arrayList.clear();
//        for (int i = 0; i < arrayList.size(); i++) {
//            if(stopType.contains("0") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 1 ) {
//                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund+"")) {
//                    if(flightNameFromDialog.length()>0 &&
//                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
//                        arrayList.add(arrayList.get(i));
//                    }else {
//                        if(flightNameFromDialog.length()==0)
//                            arrayList.add(arrayList.get(i));
//                    }
//                }
//            }else if(stopType.contains("1") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 2 ) {
//                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund)) {
//                    if(flightNameFromDialog.length()>0 &&
//                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
//                        arrayList.add(arrayList.get(i));
//                    }else {
//                        if(flightNameFromDialog.length()==0)
//                            arrayList.add(arrayList.get(i));
//                    }
//                }
//            }else if(stopType.contains("2") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 3 ) {
//                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund)) {
//                    if(flightNameFromDialog.length()>0 &&
//                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
//                        arrayList.add(arrayList.get(i));
//                    }else {
//                        if(flightNameFromDialog.length()==0)
//                            arrayList.add(arrayList.get(i));
//                    }
//                }
//            }else {
//                if(stopType.length()==0 && (refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund))) {
//                    if(flightNameFromDialog.length()>0 &&
//                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
//                        arrayList.add(arrayList.get(i));
//                    }else {
//                        if(flightNameFromDialog.length()==0)
//                            arrayList.add(arrayList.get(i));
//                    }
//                }
//            }
////
////            else if(stopType == 1 && arrayList.get(i).Flights.size() == 2 ||
////                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
////               tempArrayList.add(arrayList.get(i));
////            } else if(stopType == 2 && arrayList.get(i).Flights.size() == 3 ||
////                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
////               tempArrayList.add(arrayList.get(i));
////            }
//        }
//    }


    private void applyFilterRound() {
        tempArrayList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if(stopType.contains("0") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 1 ) {
                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund+"")) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else if(stopType.contains("1") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 2 ) {
                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else if(stopType.contains("2") && arrayList.get(i).RoundTrip.get(0).Flights.size() == 3 ) {
                if(refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else {
                if(stopType.length()==0 && (refund.length()==0 || arrayList.get(i).RoundTrip.get(0).Refundable.toString().equalsIgnoreCase(refund))) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).RoundTrip.get(0).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }
//
//            else if(stopType == 1 && arrayList.get(i).Flights.size() == 2 ||
//                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
//               tempArrayList.add(arrayList.get(i));
//            } else if(stopType == 2 && arrayList.get(i).Flights.size() == 3 ||
//                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
//               tempArrayList.add(arrayList.get(i));
//            }
        }
    }

    private void setAirlineCheckBoxListeners(String values) {
        int length=values.split("#").length;
        CheckBox checkBox[]=new CheckBox[length];
        for(int i=0; i<length; i++){
            checkBox[i]=new CheckBox(context);
            checkBox[i].setText(values.split("#")[i]);
            final int finalI = i;
            checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        flightNameFromDialog=flightNameFromDialog+buttonView.getText().toString();
                    }else {
                        flightNameFromDialog=flightNameFromDialog.replace(buttonView.getText().toString(),"");
                    }
                }
            });
            checkboxLinear.addView(checkBox[i]);
        }
    }

    private String categoriesValues() {
        String flightName=arrayList.get(0).TwoWay.get(0).FlightName;
        ArrayList<FlightSearchDataModel.OneWay> category=new ArrayList<>();
        category.add(arrayList.get(0).TwoWay.get(0));
        for(int i = 1; i< arrayList.get(0).TwoWay.size(); i++){
            if(category.size()==0){
                category.add(arrayList.get(0).TwoWay.get(i));
                flightName=flightName+"#"+arrayList.get(0).TwoWay.get(i).FlightName;
            }else {
                int k=0;
                for(int j=0; j<category.size(); j++){
                    if(category.get(j).Flights.get(0).Carrier.
                            equalsIgnoreCase(arrayList.get(0).TwoWay.get(i).Flights.get(0).Carrier)){
                        break;
                    }
                    if(j==category.size()-1){
                        category.add(arrayList.get(0).TwoWay.get(i));
                        flightName=flightName+"#"+arrayList.get(0).TwoWay.get(i).FlightName;
                    }
                }
            }
        }
        return flightName;
    }

    private String roundCategoriesValues() {
        String flightName=arrayList.get(0).RoundTrip.get(0).FlightName;
        ArrayList<FlightSearchDataModel> category=new ArrayList<>();
        category.add(arrayList.get(0));
        for(int i = 1; i< arrayList.size(); i++){
            if(category.size()==0){
                category.add(arrayList.get(i));
                flightName=flightName+"#"+arrayList.get(i).RoundTrip.get(0).FlightName;
            }else {
                int k=0;
                for(int j=0; j<category.size(); j++){
                    if(category.get(j).RoundTrip.get(0).Flights.get(0).Carrier.
                            equalsIgnoreCase(arrayList.get(i).RoundTrip.get(0).Flights.get(0).Carrier)){
                        break;
                    }
                    if(j==category.size()-1){
                        category.add(arrayList.get(i));
                        flightName=flightName+"#"+arrayList.get(i).RoundTrip.get(0).FlightName;
                    }
                }
            }
        }
        return flightName;
    }
}

