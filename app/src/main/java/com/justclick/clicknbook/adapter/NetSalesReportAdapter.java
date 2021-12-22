package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.NetSalesResponseModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 09/24/2017.
 */

public class NetSalesReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<NetSalesResponseModel.Data> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<NetSalesResponseModel.Data> list, int position);
    }

    public NetSalesReportAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<NetSalesResponseModel.Data> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.net_sales_report_list_item_for_agent, parent, false);

        if(viewType == TYPE_FOOTER) {
            View itemView2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_footer_item, parent, false);
            return new MyFooterViewHolder(itemView2);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder!=null && holder instanceof MyViewHolder) {
                final MyViewHolder vh = (MyViewHolder) holder;

                vh.agencyNameTv.setText(arrayList.get(position).AgencyName.toUpperCase().replace("(","\n("));
                vh.airTotalTv.setText(arrayList.get(position).AirPay);
                vh.dmtTotalTv.setText(arrayList.get(position).DMTPay);
                vh.railTotalTv.setText(arrayList.get(position).RailPay);
                vh.mobileTotalTv.setText(arrayList.get(position).MobilePay);
                vh.totalTv.setText(arrayList.get(position).TotalPay);
                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.agencyNameTv.setTypeface(face);

            } else if (holder!=null && holder instanceof MyFooterViewHolder) {
                MyFooterViewHolder vh = (MyFooterViewHolder) holder;
                if(position==0 || (position>0 && position>=Integer.parseInt(arrayList.get(0).TCount))){
                    vh.footer_lin.setVisibility(View.GONE);
                }else {
                    vh.footer_lin.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayList.size()+1;
    }

    @Override
    public int getItemViewType (int position) {
        if(position==arrayList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView agencyNameTv,airTotalTv,dmtTotalTv,railTotalTv,
                mobileTotalTv,totalTv,totalMobileTv,totalAgentTv,totalRailTv,totalDmtTv,totalAirTv;
        public MyViewHolder(View view) {
            super(view);
            agencyNameTv = (TextView) view.findViewById(R.id.agencyNameTv);
            airTotalTv = (TextView) view.findViewById(R.id.airTotalTv);
            dmtTotalTv = (TextView) view.findViewById(R.id.dmtTotalTv);
            railTotalTv = (TextView) view.findViewById(R.id.railTotalTv);
            mobileTotalTv = (TextView) view.findViewById(R.id.mobileTotalTv);
            totalAgentTv = (TextView) view.findViewById(R.id.totalAgentTv);
            totalRailTv = (TextView) view.findViewById(R.id.totalRailTv);
            totalAirTv = (TextView) view.findViewById(R.id.totalAirTv);
            totalDmtTv = (TextView) view.findViewById(R.id.totalDmtTv);
            totalMobileTv = (TextView) view.findViewById(R.id.totalMobileTv);
            totalTv = (TextView) view.findViewById(R.id.totalTv);
        }
    }

    public class MyFooterViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout footer_lin;
        public MyFooterViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_tv);
            footer_lin = (LinearLayout) view.findViewById(R.id.footer_lin);
        }
    }
}