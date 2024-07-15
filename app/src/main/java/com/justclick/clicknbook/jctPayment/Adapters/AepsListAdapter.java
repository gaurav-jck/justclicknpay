package com.justclick.clicknbook.jctPayment.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.AepsListResponseModel;
import com.justclick.clicknbook.rapipayMatm.TxnListResponseModel;

import java.util.ArrayList;
import java.util.List;


public class AepsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    Context context;
    int count;
    List<AepsListResponseModel.transactionListDetail> infoList;
    OnRecyclerItemClickListener listener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<AepsListResponseModel.transactionListDetail> list, AepsListResponseModel.transactionListDetail data, int position);
    }

    public AepsListAdapter(Context activity, OnRecyclerItemClickListener listener, List<AepsListResponseModel.transactionListDetail> infoList) {

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
                .inflate(R.layout.list_matm_transaction, parent, false);

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
                final MyViewHolder myViewHolder = (MyViewHolder) holder;

                final AepsListResponseModel.transactionListDetail info = infoList.get(position);

                myViewHolder.jckTxnId.setText(info.jckTransactionId);
                myViewHolder.txnAmount.setText("Rs "+info.txnAmount);
                myViewHolder.bankName.setText(info.bankName);
                myViewHolder.mobile.setText( "M. "+info.mobile);
                myViewHolder.ifsc.setVisibility(View.GONE);
                myViewHolder.account.setText("Ac no. "+info.accountNo);
                myViewHolder.date.setText( info.createdDate);
                myViewHolder.txnType.setText(info.txnType);
                myViewHolder.status.setText( info.txnStatusDesc);

                if(info.bankName==null || info.bankName.length()==0){
                    myViewHolder.bankName.setVisibility(View.GONE);
                }else {
                    myViewHolder.bankName.setVisibility(View.VISIBLE);
                }
                if(info.mobile==null || info.mobile.length()==0){
                    myViewHolder.mobile.setVisibility(View.GONE);
                }else {
                    myViewHolder.mobile.setVisibility(View.VISIBLE);
                }
                if(info.accountNo==null || info.accountNo.length()==0){
                    myViewHolder.account.setVisibility(View.GONE);
                }else {
                    myViewHolder.account.setVisibility(View.VISIBLE);
                }
                if(info.apiStatusCode.equals("200") || info.txnStatusDesc.equalsIgnoreCase("Success")){
                    myViewHolder.status.setTextColor(context.getResources().getColor(R.color.green));
                }else {
                    myViewHolder.status.setTextColor(context.getResources().getColor(R.color.text_dark_gray));
                }

                myViewHolder.print_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRecyclerItemClick(myViewHolder.print_tv, (ArrayList<AepsListResponseModel.transactionListDetail>) infoList,info,position);
                    }
                });
                myViewHolder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRecyclerItemClick(myViewHolder.status, (ArrayList<AepsListResponseModel.transactionListDetail>) infoList,info,position);
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

        TextView jckTxnId, txnAmount, bankName, account, ifsc, mobile, date, status,txnType,print_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            jckTxnId = (TextView) itemView.findViewById(R.id.txnId);
            txnAmount = (TextView) itemView.findViewById(R.id.txnAmountTv);
            bankName = (TextView) itemView.findViewById(R.id.bankNameTv);
            ifsc = (TextView) itemView.findViewById(R.id.ifscTv);
            mobile = (TextView) itemView.findViewById(R.id.senderMobileTv);
            account = (TextView) itemView.findViewById(R.id.accountNoTv);
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
