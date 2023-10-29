package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.NetSalesResponseModel;
import com.justclick.clicknbook.model.NetSalesResponseModelNew;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class NetSalesReportAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<NetSalesResponseModelNew.aadharPayResponceList> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<NetSalesResponseModel.Data> list, int position);
    }

    public NetSalesReportAdapterNew(Context context, OnRecyclerItemClickListener fragment, ArrayList<NetSalesResponseModelNew.aadharPayResponceList> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.net_sales_report_list_item, parent, false);

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

                vh.agencyNameTv.setText(arrayList.get(position).agencyname.toUpperCase()+"\n("+
                        arrayList.get(position).agencycode.toUpperCase()+")");
                vh.totalTv.setText(arrayList.get(position).totalRecharge+"");
                vh.distCommTv.setText(arrayList.get(position).totalRechargeDistCom+"");
                vh.netTv.setText(arrayList.get(position).totalNetRecharge+"");
                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.agencyNameTv.setTypeface(face);

            } else if (holder!=null && holder instanceof MyFooterViewHolder) {
                MyFooterViewHolder vh = (MyFooterViewHolder) holder;
                if(position==0 || (position>0 && position>=Integer.parseInt("0"))){
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
        public TextView agencyNameTv, totalTv, distCommTv, netTv;
        public MyViewHolder(View view) {
            super(view);
            agencyNameTv = view.findViewById(R.id.agencyNameTv);
            totalTv =  view.findViewById(R.id.totalTv);
            distCommTv = view.findViewById(R.id.distCommTv);
            netTv =  view.findViewById(R.id.netTv);
        }
    }

    public class MyFooterViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout footer_lin;
        public MyFooterViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.name_tv);
            footer_lin =  view.findViewById(R.id.footer_lin);
        }
    }
}