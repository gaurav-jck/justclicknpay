package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class BusAvailabilityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<BusSearchBean> arrayList;
    float minPrice;
    private OnRecyclerItemClickListener itemClickListener;
    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<BusSearchBean> list, int position);
    }

    public BusAvailabilityAdapter(Context context, OnRecyclerItemClickListener fragment,
                                  ArrayList<BusSearchBean> arrayList, int totalPageCount)
    {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_search_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder vh = (MyViewHolder) holder;
        vh.BusNameTv.setText(arrayList.get(position).travelName);
        vh.BusTypeTv.setText(arrayList.get(position).bustype);
        vh.fromTimeTv.setText(Common.formatBusTime(arrayList.get(position).departureTime));
        vh.toTimeTv.setText(Common.formatBusTime(arrayList.get(position).arrivalTime));
        vh.seatTv.setText(arrayList.get(position).totalSeats + " Seats");
        vh.totalTime.setText(Common.getBusDuration(arrayList.get(position).departureTime, arrayList.get(position).arrivalTime));
        float minPrice = arrayList.get(position).fareDetails.get(0).GrossFare;
        for (int i = 1; arrayList.size() > (i - 1) && i < arrayList.get(position).fareDetails.size(); i++) {

            if (arrayList.get(position).fareDetails.get(i).GrossFare < minPrice) {
                minPrice = arrayList.get(position).fareDetails.get(i).GrossFare;

            }
        }
        vh.priceTv.setText(minPrice + "");

        if(arrayList.get(position).totalSeats.equalsIgnoreCase("0"))
        {
            vh.seatTv.setText("SOLD OUT");
            vh.seatTv.setTextColor(context.getResources().getColor(R.color.app_red_color));
        }
        else {
            vh.seatTv.setTextColor(context.getResources().getColor(R.color.grey));
            vh.seatTv.setText(arrayList.get(position).totalSeats + " Seats");

        }
        vh.itemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(vh.itemLin);
                if (arrayList.get(position).totalSeats.equalsIgnoreCase("0")) {
                    Toast.makeText(context, "sold out", Toast.LENGTH_SHORT).show();
                } else {
                    itemClickListener.onRecyclerItemClick(v, arrayList, position);
                }
            }

        });

        vh.cancellationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecyclerItemClick(v, arrayList, position);
            }
        });
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
        public TextView BusNameTv,BusTypeTv,fromTimeTv,toTimeTv,seatTv,totalTime,priceTv, cancellationTv;
        public LinearLayout itemLin;
        public MyViewHolder(View view) {
            super(view);
            BusNameTv = (TextView) view.findViewById(R.id.BusNameTv);
            BusTypeTv = (TextView) view.findViewById(R.id.BusTypeTv);
            fromTimeTv = (TextView) view.findViewById(R.id.fromTimeTv);
            toTimeTv = (TextView) view.findViewById(R.id.toTimeTv);
            seatTv = (TextView) view.findViewById(R.id.seatTv);
            totalTime = (TextView) view.findViewById(R.id.totalTime);
            priceTv = (TextView) view.findViewById(R.id.priceTv);
            cancellationTv = (TextView) view.findViewById(R.id.cancellationTv);
            itemLin = (LinearLayout) view.findViewById(R.id.itemLin);
            Typeface face = Common.EditTextTypeFace(context);
            Typeface face2 = Common.FlightCalenderTypeFace3(context);
            Typeface face1 = Common.OpenSansRegularTypeFace(context);


            BusNameTv.setTypeface(face);
            BusTypeTv.setTypeface(face);
            fromTimeTv.setTypeface(face);
            toTimeTv.setTypeface(face);
            seatTv.setTypeface(face);
            totalTime.setTypeface(face);
            priceTv.setTypeface(face);
        }
    }

}
