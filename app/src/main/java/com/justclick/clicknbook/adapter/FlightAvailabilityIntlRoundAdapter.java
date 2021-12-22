package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class FlightAvailabilityIntlRoundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<FlightSearchDataModel> arrayList;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel> list, int position);
    }

    public FlightAvailabilityIntlRoundAdapter(Context context, OnRecyclerItemClickListener fragment,
                                              ArrayList<FlightSearchDataModel> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_search_international_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder vh = (MyViewHolder) holder;

        vh.flight_name.setText(arrayList.get(0).RoundTrip.get(position).FlightName);
        vh.priceTv.setText("Rs."+arrayList.get(0).RoundTrip.get(position).FinalPrice+"");
        vh.policyTv.setText(arrayList.get(0).RoundTrip.get(position).Refundable);
//        vh.agency_name_tv.setText(arrayList.get(position).Destination);
        if(arrayList.get(0).RoundTrip.get(position).Flights.size()==1) {
            vh.fromDurationTv.setText(arrayList.get(0).RoundTrip.get(position).Duration +"("+"Non-Stop"+")");
        }else {
            vh.fromDurationTv.setText(arrayList.get(0).RoundTrip.get(position).Duration +"("+(arrayList.get(0).RoundTrip.get(position).Flights.size()-1)+" Stop)");
        }

        if(arrayList.get(0).RoundTrip.get(position).Flights_r.size()==1) {
            vh.toDurationTv.setText(arrayList.get(0).RoundTrip.get(position).Duration_r +"("+"Non-Stop"+")");
        }else {
            vh.toDurationTv.setText(arrayList.get(0).RoundTrip.get(position).Duration_r +"("+(arrayList.get(0).RoundTrip.get(position).Flights_r.size()-1)+" Stop)");
        }
        vh.fromTimeTv.setText(arrayList.get(0).RoundTrip.get(position).DTime);
        vh.toTimeTv.setText(arrayList.get(0).RoundTrip.get(position).DTime_r);
        vh.fromArrivalTimeTv.setText(arrayList.get(0).RoundTrip.get(position).ATime);
        vh.toArrivalTimeTv.setText(arrayList.get(0).RoundTrip.get(position).ATime_r);
        vh.flight_Sr_no.setText(arrayList.get(0).RoundTrip.get(position).Flights.get(0).Carrier+" - "+arrayList.get(0).RoundTrip.get(position).Flights.get(0).FNumber);
//        vh.time_duration_tv.setText(arrayList.get(position).arrivalTime +" - "+ arrayList.get(position).departureTime);
        Uri uri=Uri.parse(arrayList.get(0).RoundTrip.get(position).Flights.get(0).Image);
        vh.flightImage.setImageURI(ApiConstants.FlightImageBaseUrl+uri);

        vh.linear_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecyclerItemClick( v, arrayList, position);
            }
        });

        Typeface face = Common.listAgencyNameTypeFace(context);
        Typeface face1 = Common.homeSubMenuItemTypeFace(context);
        Typeface face2 = Common.ButtonTypeFace1(context);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayList.get(0).RoundTrip.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView flight_Sr_no, fromTimeTv, fromDurationTv, priceTv, policyTv,flight_name,fromArrivalTimeTv,
                toTimeTv, toDurationTv, toArrivalTimeTv;
        public SimpleDraweeView flightImage;
        public LinearLayout linear_main_menu, containerLin;
        public RecyclerView innerRecycleView;

        public MyViewHolder(View view) {
            super(view);
            flight_name = view.findViewById(R.id.flight_name);
            flight_Sr_no =  view.findViewById(R.id.flight_Sr_no);
            fromTimeTv = view.findViewById(R.id.fromTimeTv);
            fromDurationTv =  view.findViewById(R.id.fromDurationTv);
            priceTv =  view.findViewById(R.id.priceTv);
            policyTv =  view.findViewById(R.id.policyTv);
            fromArrivalTimeTv =  view.findViewById(R.id.fromArrivalTimeTv);
            toTimeTv =  view.findViewById(R.id.toTimeTv);
            toDurationTv =  view.findViewById(R.id.toDurationTv);
            toArrivalTimeTv =  view.findViewById(R.id.toArrivalTimeTv);
            flightImage =  view.findViewById(R.id.flightImage);
            linear_main_menu = view.findViewById(R.id.linear_main_menu);
            containerLin =  view.findViewById(R.id.containerLin);
            innerRecycleView =  view.findViewById(R.id.innerRecycleView);
        }

    }
}
