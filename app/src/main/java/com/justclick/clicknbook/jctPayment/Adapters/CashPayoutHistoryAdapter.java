package com.justclick.clicknbook.jctPayment.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.CashPayoutHistoryDataModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;


public class CashPayoutHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private ArrayList<CashPayoutHistoryDataModel.Data> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
       void onRecyclerItemClick(View view, ArrayList<CashPayoutHistoryDataModel.Data> list, int position);
    }

    public CashPayoutHistoryAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<CashPayoutHistoryDataModel.Data> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cash_payout_history, parent, false);

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
          //  Name,updated_by_tv,request_date_tv,request_amount_tv,credit_date_tv,credit_amount_tv;
            if (holder!=null && holder instanceof MyViewHolder) {
                final MyViewHolder vh = (MyViewHolder) holder;
                vh.amountTv.setText("Rs. "+arrayList.get(position).amount);
                vh.dateTv.setText(arrayList.get(position).created_at);
                vh.chargesTv.setText("Rs. "+arrayList.get(position).charges);
                vh.amountRequestedTv.setText("Rs. "+(arrayList.get(position).amount+arrayList.get(position).charges));
                vh.statusTv.setText(arrayList.get(position).isActive.equalsIgnoreCase("false")?"Transferred":"InProcess");
                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.amountTv.setTypeface(face);
                vh.requestLabelTv.setTypeface(face);

            } else if (holder!=null && holder instanceof MyFooterViewHolder) {
                MyFooterViewHolder vh = (MyFooterViewHolder) holder;
                if(position==0 || (position>0 && position>=arrayList.get(0).totalPageCount)){
                    vh.footer_lin.setVisibility(View.GONE);
                }else {
                    vh.footer_lin.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBindViewHolder(CreditReportAdapter.MyViewHolder holder, int position) {
//        holder.Name.setText(arrayList.get(position).Name);
//    }

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
        public TextView amountTv, dateTv, chargesTv, amountRequestedTv, statusTv, requestLabelTv;
        public MyViewHolder(View view) {
            super(view);
            amountTv = view.findViewById(R.id.amountTv);
            dateTv = view.findViewById(R.id.dateTv);
            chargesTv = view.findViewById(R.id.chargesTv);
            amountRequestedTv = view.findViewById(R.id.amountRequestedTv);
            statusTv = view.findViewById(R.id.statusTv);
            requestLabelTv = view.findViewById(R.id.requestLabelTv);

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