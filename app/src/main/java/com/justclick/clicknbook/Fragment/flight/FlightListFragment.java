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

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.FlightAvailabilityAdapter;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.MyflightDialog;

import java.util.ArrayList;

public class FlightListFragment extends Fragment implements View.OnClickListener{
    private final String REFUNDABLE="REF";
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private Context context;
    FlightListFragment fragment;
    private RecyclerView recyclerView;
    private FlightAvailabilityAdapter flightAvailabilityAdapter;
    private LinearLayoutManager layoutManager;
    private TextView fromCityTv,FromdateTv,ToCityTv,ToDateTv,travellerInfoTv,applyTv;
    private ArrayList<FlightSearchDataModel.OneWay> arrayList, tempArrayList;
    int totalPageCount=0;
    private ImageView flightFilter,refundTv,nonStopFlightImg,oneStopFlightImg,twoStopFlightImg,flightNameImg,cancelTv,backArrowImg;
    private LinearLayout refundLinear,nonStopFlightLin,oneStopFlightLin,twoStopFlightLin,flightNameLin,checkboxLinear;
    Dialog dialog;
    String refund="";
    private String flightNameFromDialog="", stopType="";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }
        catch (ClassCastException e){
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        arrayList=new ArrayList<>();
        tempArrayList=new ArrayList<>();
        fragment=this;

        //initialize date values
        if(getArguments().getSerializable("FlightList")!=null) {
            arrayList = (ArrayList<FlightSearchDataModel.OneWay>) getArguments().getSerializable("FlightList");
            tempArrayList.addAll(arrayList);
//            fromCityTv.setText();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_flight_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        titleChangeListener.onToolBarTitleChange(getString(R.string.flightSearchFragmentTitle));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        fromCityTv= (TextView) view.findViewById(R.id.fromCityTv);
        FromdateTv= (TextView) view.findViewById(R.id.FromdateTv);
        ToCityTv= (TextView) view.findViewById(R.id.ToCityTv);
        ToDateTv= (TextView) view.findViewById(R.id.ToDateTv);
        travellerInfoTv= (TextView) view.findViewById(R.id.travellerInfoTv);
        backArrowImg= (ImageView) view.findViewById(R.id.backArrowImg);

        String value = getArguments().getString("fromCityCode");
        String value1 = getArguments().getString("toCityCode");
        String value2 = getArguments().getString("checkInDate");
        String value3 = getArguments().getString("noOfTraveller");
        fromCityTv.setText(value);
        ToCityTv.setText(value1);
        FromdateTv.setText(value2);
        ToDateTv.setText(value2);
        travellerInfoTv.setText(value3);
        flightFilter= (ImageView) view.findViewById(R.id.flightFilter);
        flightFilter.setOnClickListener(fragment);
        backArrowImg.setOnClickListener(fragment);

        flightAvailabilityAdapter =new FlightAvailabilityAdapter(context, new FlightAvailabilityAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position) {
                switch (view.getId()){
                    case R.id.containerLin:
                        RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.innerRecycleView);
                        if(recyclerView.getVisibility()==View.VISIBLE){
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
//                        recyclerView.setVisibility(View.VISIBLE);
//                        Toast.makeText(context,"flight details",Toast.LENGTH_SHORT).show();
                }
            }

        },tempArrayList,totalPageCount);

        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(flightAvailabilityAdapter);
        MyflightDialog.hideCustomDialog();
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

                setAirlineCheckBoxListeners(categoriesValues());

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
            case R.id.refundLinear:
                if(refund.equalsIgnoreCase("")){
                    refundTv.setBackgroundResource(R.drawable.check_box_checked);
                    refund=REFUNDABLE;
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
                applyFilter();
//                flightAvailabilityAdapter.notifyDataSetChanged();
                flightAvailabilityAdapter =new FlightAvailabilityAdapter(context, new FlightAvailabilityAdapter.OnRecyclerItemClickListener() {
                    @Override
                    public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position) {
                        switch (view.getId()){
                            case R.id.containerLin:
                                RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.innerRecycleView);
                                if(recyclerView.getVisibility()==View.VISIBLE){
                                    recyclerView.setVisibility(View.GONE);
                                }else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
//                        recyclerView.setVisibility(View.VISIBLE);
//                        Toast.makeText(context,"flight details",Toast.LENGTH_SHORT).show();
                        }
                    }

                },tempArrayList,totalPageCount);
                recyclerView.setAdapter(flightAvailabilityAdapter);
                break;
            case R.id.backArrowImg:
                getFragmentManager().popBackStack();
                break;
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

        String flightName=arrayList.get(0).FlightName;
        ArrayList<FlightSearchDataModel.OneWay> category=new ArrayList<>();
        category.add(arrayList.get(0));
        for(int i = 1; i< arrayList.size(); i++){
            if(category.size()==0){
                category.add(arrayList.get(i));
                flightName=flightName+"#"+arrayList.get(i).FlightName;
            }else {
                int k=0;
                for(int j=0; j<category.size(); j++){
                    if(category.get(j).Flights.get(0).Carrier.
                            equalsIgnoreCase(arrayList.get(i).Flights.get(0).Carrier)){
                        break;
                    }
                    if(j==category.size()-1){
                        category.add(arrayList.get(i));
                        flightName=flightName+"#"+arrayList.get(i).FlightName;
                    }
                }
            }
        }
        return flightName;

    }

    private void applyFilter() {
        tempArrayList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if(stopType.contains("0") && arrayList.get(i).Flights.size() == 1 ) {
                if(refund.length()==0 || arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else if(stopType.contains("1") && arrayList.get(i).Flights.size() == 2 ) {
                if(refund.length()==0 || arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else if(stopType.contains("2") && arrayList.get(i).Flights.size() == 23 ) {
                if(refund.length()==0 || arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund)) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }else {
                if(stopType.length()==0 && (refund.length()==0 || arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund))) {
                    if(flightNameFromDialog.length()>0 &&
                            flightNameFromDialog.contains(arrayList.get(i).FlightName.toString())) {
                        tempArrayList.add(arrayList.get(i));
                    }else {
                        if(flightNameFromDialog.length()==0)
                            tempArrayList.add(arrayList.get(i));
                    }
                }
            }

//            else if(stopType == 1 && arrayList.get(i).Flights.size() == 2 ||
//                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
//               tempArrayList.add(arrayList.get(i));
//            } else if(stopType == 2 && arrayList.get(i).Flights.size() == 3 ||
//                    arrayList.get(i).Refundable.toString().equalsIgnoreCase(refund+"")){
//               tempArrayList.add(arrayList.get(i));
//            }
        }
    }

}

