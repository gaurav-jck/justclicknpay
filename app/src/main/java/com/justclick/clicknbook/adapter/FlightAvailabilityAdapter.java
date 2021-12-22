package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.utils.AirFareValidateClass;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

public class FlightAvailabilityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<FlightSearchDataModel.OneWay> arrayList=new ArrayList<>();
    private ArrayList<FlightSearchDataModel.OneWay> mainArrayList;
    private OnRecyclerItemClickListener itemClickListener;
    private int minPrice,maxPrice;
    private LoginModel loginModel;

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position);
    }

    public FlightAvailabilityAdapter(Context context, OnRecyclerItemClickListener fragment,
                                     ArrayList<FlightSearchDataModel.OneWay> arrayList, int totalPageCount) {
        this.context = context;
        this.mainArrayList =arrayList;
        categoriesValues();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        itemClickListener= fragment;
    }

    private void categoriesValues() {
        for(int i = 0; i< mainArrayList.size(); i++){
            if(arrayList.size()==0){
                arrayList.add(mainArrayList.get(i));
            }
            else {
                int k=0;
                for(int j=0; j<arrayList.size(); j++){
                    if(arrayList.get(j).Flights.get(0).Carrier.
                            equalsIgnoreCase(mainArrayList.get(i).Flights.get(0).Carrier)){
                        break;
                    }
                    if(j==arrayList.size()-1){
                        arrayList.add(mainArrayList.get(i));
                    }
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_search_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder vh = (MyViewHolder) holder;

//        vh.priceTv.setText(arrayList.get(position).FinalPrice+"");
        vh.policyTv.setText(arrayList.get(position).Refundable);
        vh.flight_name.setText(arrayList.get(position).FlightName+"");
//        vh.agency_name_tv.setText(arrayList.get(position).Destination);
        if(arrayList.get(position).Flights.size()==1) {
            vh.fromDurationTv.setText(arrayList.get(position).Duration +"("+"Non-Stop"+")");
        }else {
            vh.fromDurationTv.setText(arrayList.get(position).Duration +"("+(arrayList.get(position).Flights.size()-1)+" Stop)");
        }
        vh.fromArrivalTimeTv.setText(arrayList.get(position).ATime);
        vh.fromTimeTv.setText(arrayList.get(position).DTime);
        vh.flight_Sr_no.setText(arrayList.get(position).Flights.get(0).Carrier+" - "+arrayList.get(position).Flights.get(0).FNumber);
//        vh.time_duration_tv.setText(arrayList.get(position).arrivalTime +" - "+ arrayList.get(position).departureTime);
        Uri uri=Uri.parse(arrayList.get(position).Flights.get(0).Image);
        vh.flightImage.setImageURI(ApiConstants.FlightImageBaseUrl+uri);

        vh.containerLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecyclerItemClick( vh.containerLin, arrayList, position);
            }
        });

        Typeface face = Common.listAgencyNameTypeFace(context);
        Typeface face1 = Common.homeSubMenuItemTypeFace(context);
        Typeface face2 = Common.ButtonTypeFace1(context);


//        code for nested recycler view

        ArrayList<FlightSearchDataModel.OneWay> innerArrayList=getInnerArrayList(arrayList.get(position).Flights.get(0).Carrier);
        vh.priceTv.setText("Rs."+minPrice+"");
        vh.priceMaxTv.setText("Rs."+maxPrice+"");
        minPrice=0;
        FlightAvailabilityInnerAdapter adapter =new FlightAvailabilityInnerAdapter(context, new FlightAvailabilityInnerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position) {
                switch (view.getId()){
                    case R.id.linear_main_menu:
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("outBoundModel",list.get(position));
                        bundle.putInt("tripType",1);
                        ArrayList<FlightSearchDataModel.OneWay> airlineArray=new ArrayList<>();
                        airlineArray.add(list.get(position));

                        new AirFareValidateClass().airFareValidation(airlineArray,bundle,context, "ONE", "N");

                }
            }

        },innerArrayList,0);

        vh.innerRecycleView.setLayoutManager(new LinearLayoutManager(context));
        vh.innerRecycleView.setAdapter(adapter);
        vh.innerRecycleView.setHasFixedSize(true);

    }


    private ArrayList<FlightSearchDataModel.OneWay> getInnerArrayList(String code) {
        ArrayList<FlightSearchDataModel.OneWay> innerArrayList=new ArrayList<>();
        for(int i = 0; i< mainArrayList.size(); i++){
            if(mainArrayList.get(i).Flights.get(0).Carrier.
                    equalsIgnoreCase(code)){
                innerArrayList.add(mainArrayList.get(i));
                if(minPrice==0){
                    minPrice=mainArrayList.get(i).FinalPrice;
                    maxPrice=mainArrayList.get(i).FinalPrice;
                }else {
                    if(minPrice>mainArrayList.get(i).FinalPrice){
                        minPrice=mainArrayList.get(i).FinalPrice;
                    }
                    if(maxPrice<mainArrayList.get(i).FinalPrice){
                        maxPrice=mainArrayList.get(i).FinalPrice;
                    }
                }
            }
        }
        return innerArrayList;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView flight_Sr_no, fromTimeTv, fromDurationTv, priceTv,policyTv,priceMaxTv,
                flight_name,toTimeTv,toDurationTv,fromArrivalTimeTv,ToArrivalTimeTv;
        public SimpleDraweeView flightImage;
        public LinearLayout linear_main_menu, containerLin;
        public RecyclerView innerRecycleView;
        public MyViewHolder(View view) {
            super(view);
            flight_name =  view.findViewById(R.id.flight_name);
            flight_Sr_no =  view.findViewById(R.id.flight_Sr_no);
            fromTimeTv = view.findViewById(R.id.fromTimeTv);
            toTimeTv = view.findViewById(R.id.toTimeTv);
            fromDurationTv = view.findViewById(R.id.fromDurationTv);
            toDurationTv = view.findViewById(R.id.toDurationTv);
            priceTv = view.findViewById(R.id.priceTv);
            priceMaxTv = view.findViewById(R.id.priceMaxTv);
            policyTv =  view.findViewById(R.id.policyTv);
            fromArrivalTimeTv =  view.findViewById(R.id.fromArrivalTimeTv);
            ToArrivalTimeTv =  view.findViewById(R.id.toArrivalTimeTv);
            flightImage =  view.findViewById(R.id.flightImage);
            linear_main_menu =  view.findViewById(R.id.linear_main_menu);
            containerLin =  view.findViewById(R.id.containerLin);
            innerRecycleView =  view.findViewById(R.id.innerRecycleView);
        }
    }
}
