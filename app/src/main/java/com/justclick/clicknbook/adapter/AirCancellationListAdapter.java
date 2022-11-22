package com.justclick.clicknbook.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AirCancellationListModel;
import com.justclick.clicknbook.model.OptModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class AirCancellationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<AirCancellationListModel.AirCancelListData> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<AirCancellationListModel.AirCancelListData> list, int position);
    }

    public AirCancellationListAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<AirCancellationListModel.AirCancelListData> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.air_cancellation_list_item, parent, false);
        if(viewType == TYPE_FOOTER) {
            View itemView2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_footer_item, parent, false);
            return new AirCancellationListAdapter.MyFooterViewHolder(itemView2);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder!=null && holder instanceof MyViewHolder) {
                MyViewHolder vh = (MyViewHolder) holder;
                vh.name_tv.setText(arrayList.get(position).AgencyName.toUpperCase().replace("(", "\n("));
                vh.product_tv_value.setText(arrayList.get(position).TotalFare);
                vh.request_date_tv.setText(arrayList.get(position).CancellationDateId);
                vh.request_amount_tv.setText(arrayList.get(position).TotalFare);
                vh.referenceId_tv_value.setText(arrayList.get(position).ReservationDateId);
            }else if (holder!=null && holder instanceof MyFooterViewHolder) {
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
        public TextView name_tv,product_tv_value,request_date_tv,request_amount_tv,
                referenceId_tv_value;
        public MyViewHolder(View view) {
            super(view);
            name_tv =  view.findViewById(R.id.name_tv);
            product_tv_value =  view.findViewById(R.id.product_tv_value);
            request_date_tv =  view.findViewById(R.id.request_date_tv);
            request_amount_tv = view.findViewById(R.id.request_amount_tv);
            referenceId_tv_value =  view.findViewById(R.id.referenceId_tv_value);
        }
    }

    public class MyFooterViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout footer_lin;
        public MyFooterViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_tv);
            footer_lin = view.findViewById(R.id.footer_lin);
        }
    }

}