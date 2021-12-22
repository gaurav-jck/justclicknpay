package com.justclick.clicknbook.Fragment.cashout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Fragment.cashout.model.PayoutResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.TransactionResponse;
import com.justclick.clicknbook.R;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class PayoutTxnDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<PayoutResponse.TransactionDetails> arrayList;
    private OnRecyclerItemClickListener itemClickListener;
    private String mobile;

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<PayoutResponse.TransactionDetails> list, int position);
    }

    public PayoutTxnDetailAdapter(Context context, OnRecyclerItemClickListener fragment,
                                  ArrayList<PayoutResponse.TransactionDetails> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rapipay_txndetail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            MyViewHolder vh = (MyViewHolder) holder;
            vh.amountTv.setText(arrayList.get(position).getAmount()+"");
            vh.jctTxnIdTv.setText(arrayList.get(position).getJckRefId());
            vh.txnIdTv.setText(arrayList.get(position).getTransactionId());
            vh.bankRefNoTv.setText(arrayList.get(position).getBankRRN());
            vh.txnMessageTv.setText(arrayList.get(position).getTxnMessage());
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
        return arrayList.size();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_tv:

                break;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTv,jctTxnIdTv,txnIdTv,bankRefNoTv,txnMessageTv;
        public MyViewHolder(View view) {
            super(view);
            amountTv =  view.findViewById(R.id.amountTv);
            jctTxnIdTv =  view.findViewById(R.id.jctTxnIdTv);
            txnIdTv =  view.findViewById(R.id.txnIdTv);
            bankRefNoTv =  view.findViewById(R.id.bankRefNoTv);
            txnMessageTv =  view.findViewById(R.id.txnMessageTv);
        }
    }

}