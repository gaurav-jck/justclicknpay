package com.justclick.clicknbook.Fragment.billpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.rapipayMatm.TxnListResponseModel;

import java.util.ArrayList;
import java.util.List;


public class UtilityRecyclerTransactionList extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    Context context;
    int count;
    List<TxnListResponseModel.transactionListDetail> infoList;
    OnRecyclerItemClickListener listener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<TxnListResponseModel.transactionListDetail> list, TxnListResponseModel.transactionListDetail data, int position);
    }

    public UtilityRecyclerTransactionList(Context activity, OnRecyclerItemClickListener listener, List<TxnListResponseModel.transactionListDetail> infoList) {

        this.context = activity;
        this.infoList = infoList;
        this.listener=listener;
    }

    public void setCount(int count){
        this.count=count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_utility_transaction, parent, false);

        if(viewType == TYPE_FOOTER) {
            View itemView2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_footer_item, parent, false);
            return new MyFooterViewHolder(itemView2);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            if (holder!=null && holder instanceof MyViewHolder) {
                final MyViewHolder myViewHolder = (MyViewHolder) holder;

                final TxnListResponseModel.transactionListDetail info = infoList.get(position);

                myViewHolder.jckTxnId.setText(info.getTransactionId());
                myViewHolder.txnAmount.setText("Rs "+info.getTxnAmount());
                myViewHolder.operatorIdTv.setText(info.getRechargeNumber());
                myViewHolder.date.setText( info.getCreatedDate());
                myViewHolder.txnType.setText(info.getRechargeType());
                myViewHolder.status.setText( info.getTxnStatusDesc());

                if(info.getTxnStatusDesc().equalsIgnoreCase("Success")){
                    myViewHolder.status.setTextColor(context.getResources().getColor(R.color.green));
                }else {
                    myViewHolder.status.setTextColor(context.getResources().getColor(R.color.text_dark_gray));
                }

                myViewHolder.print_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRecyclerItemClick(myViewHolder.print_tv, (ArrayList<TxnListResponseModel.transactionListDetail>) infoList,info,position);
                    }
                });
                myViewHolder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        listener.onRecyclerItemClick(myViewHolder.status, (ArrayList<TxnListResponseModel.transactionListDetail>) infoList,info,position);
                    }
                });

            }else if (holder!=null && holder instanceof MyFooterViewHolder) {
                MyFooterViewHolder vh = (MyFooterViewHolder) holder;
                if(position==0 || (position>0 && position>=count)){
                    vh.footer_lin.setVisibility(View.GONE);
                }else {
                    vh.footer_lin.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){}

    }

    @Override
    public int getItemViewType (int position) {
        if(position==infoList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return infoList.size()+1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView jckTxnId, txnAmount, operatorIdTv, date, status,txnType,print_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            jckTxnId = (TextView) itemView.findViewById(R.id.txnId);
            txnAmount = (TextView) itemView.findViewById(R.id.txnAmountTv);
            operatorIdTv = (TextView) itemView.findViewById(R.id.operatorIdTv);
            date = (TextView) itemView.findViewById(R.id.txnDateTv);
            status = (TextView) itemView.findViewById(R.id.statusTv);
            txnType = (TextView) itemView.findViewById(R.id.txnType);
            print_tv = (TextView) itemView.findViewById(R.id.print_tv);
        }
    }

    public class MyFooterViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout footer_lin;
        public MyFooterViewHolder(View view) {
            super(view);
            footer_lin = (LinearLayout) view.findViewById(R.id.footer_lin);
        }
    }

}
