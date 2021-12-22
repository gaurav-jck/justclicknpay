package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class BusBoardingPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> arrayList;
    private OnRecyclerItemClickListener itemClickListener;
    public int arrayPosition=0;
    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> list, int position);
    }

    public BusBoardingPointAdapter(Context context, OnRecyclerItemClickListener fragment,
                                   ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_boarding_point_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.stopNameTv.setText(arrayList.get(position).address);
        vh.timeTv.setText(Common.formatBusTime(arrayList.get(position).time)+"\n");
        if(arrayPosition==position){
            arrayList.get(position).isClick=true;
        }else {
            arrayList.get(position).isClick=false;
        }
        if (position==arrayList.size()-1){
            vh.line.setVisibility(View.GONE);
        }else {
            vh.line.setVisibility(View.VISIBLE);
        }
        if(arrayList.get(position).isClick){
            vh.checkImg.setVisibility(View.VISIBLE);
            vh.stopNameTv.setTypeface(null, Typeface.BOLD);
        }else {
            vh.checkImg.setVisibility(View.INVISIBLE);
            vh.stopNameTv.setTypeface(null, Typeface.NORMAL);
        }

        vh.itemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecyclerItemClick( v, arrayList, position);
                arrayList.get(arrayPosition).isClick=false;
                arrayList.get(position).isClick=true;
                arrayPosition=position;
                notifyDataSetChanged();
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
        return arrayList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTv,stopNameTv;
        public ImageView checkImg;
        public LinearLayout itemLin;
        public View line;
        public MyViewHolder(View view) {
            super(view);
            timeTv = (TextView) view.findViewById(R.id.timeTv);
            stopNameTv = (TextView) view.findViewById(R.id.stopNameTv);
            checkImg = (ImageView) view.findViewById(R.id.checkImg);
            itemLin = (LinearLayout) view.findViewById(R.id.itemLin);
            line =  view.findViewById(R.id.line);
        }
    }

}
