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
import com.justclick.clicknbook.model.CreditReportListModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class CreditReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private ArrayList<CreditReportListModel.CreditReportData> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<CreditReportListModel.CreditReportData> list, int position );
    }

    public CreditReportAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<CreditReportListModel.CreditReportData> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_report_list_item, parent, false);

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
                vh.name.setText(arrayList.get(position).AgencyName.replace("(","\n("));
                vh.amount_tv.setText(arrayList.get(position).Amount);
                vh.date_tv.setText("Update Date  "+arrayList.get(position).LastUpdateDate);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_tv:

                break;

            case R.id.delete_tv:
                break;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, amount_tv, date_tv, update_tv, delete_tv;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_tv);
            amount_tv = view.findViewById(R.id.amount_tv);
            date_tv = view.findViewById(R.id.address_tv);
            update_tv =  view.findViewById(R.id.update_tv);
            delete_tv =  view.findViewById(R.id.delete_tv);
        }
    }
}