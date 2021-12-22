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
import com.justclick.clicknbook.adapter.viewHolder.MyFooterViewHolder;
import com.justclick.clicknbook.model.DepositReportListModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class DepositReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private ArrayList<DepositReportListModel.DepositReportData> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<DepositReportListModel.DepositReportData> list, int position );
    }

    public DepositReportAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<DepositReportListModel.DepositReportData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        itemClickListener = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deposit_report_list_item, parent, false);

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
                MyViewHolder vh = (MyViewHolder) holder;
                vh.name.setText(arrayList.get(position).AgencyName.replace("(","\n(").toUpperCase());
                vh.amount_tv.setText(arrayList.get(position).Amount);
                vh.date_tv.setText(arrayList.get(position).Date);
                vh.date1_tv.setText(arrayList.get(position).DepositDate);
                vh.type_id.setText(arrayList.get(position).TypeOfAmount);
                vh.receipt_no_tv.setText(arrayList.get(position).ReceiptNo);

                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.name.setTypeface(face);

                vh.update_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });
                vh.delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });
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
        public TextView name, amount_tv, date_tv,date1_tv, update_tv, delete_tv,type_id,receipt_no_tv;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_tv);
            amount_tv = view.findViewById(R.id.amount_tv);
            date_tv =  view.findViewById(R.id.address_tv);
            date1_tv =  view.findViewById(R.id.date1_tv);
            update_tv =  view.findViewById(R.id.update_tv);
            delete_tv =  view.findViewById(R.id.delete_tv);
            type_id =  view.findViewById(R.id.type_id);
            receipt_no_tv =  view.findViewById(R.id.receipt_no_tv);
        }
    }

}
