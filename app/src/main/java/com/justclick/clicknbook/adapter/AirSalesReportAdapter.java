package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.viewHolder.MyFooterViewHolder;
import com.justclick.clicknbook.model.AirSalesReportModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class AirSalesReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private ArrayList<AirSalesReportModel.Data> arrayList;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<AirSalesReportModel.Data> list, int position);
    }

    public AirSalesReportAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<AirSalesReportModel.Data> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.air_sales_report_list_item, parent, false);

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
                vh.name.setText(arrayList.get(position).AgencyName.toUpperCase().replace("(","\n("));
                vh.updated_by_tv.setText(arrayList.get(position).AgencyName);
                vh.request_date_tv.setText(arrayList.get(position).Basic);
                vh.request_amount_tv.setText(arrayList.get(position).NetFare);
                vh.credit_date_tv.setText(arrayList.get(position).Discount);
                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.name.setTypeface(face);

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
        public TextView name,updated_by_tv,request_date_tv,request_amount_tv,
                credit_date_tv,credit_amount_tv, remarksTv, statusTv;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_tv);
            updated_by_tv =  view.findViewById(R.id.updated_by_tv);
            request_date_tv =  view.findViewById(R.id.request_date_tv);
            request_amount_tv =  view.findViewById(R.id.request_amount_tv);
            credit_date_tv =  view.findViewById(R.id.credit_date_tv);
            credit_amount_tv = view.findViewById(R.id.credit_amount_tv);
            remarksTv = view.findViewById(R.id.remarksTv);
            statusTv = view.findViewById(R.id.statusTv);

        }
    }
}