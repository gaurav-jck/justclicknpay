package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

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


public class AccountStatementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    Context context;
    int count;
    List<AccountStmtResponse.accountStatementList> infoList;
    OnRecyclerItemClickListener listener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<AccountStmtResponse.accountStatementList> list,
                                        AccountStmtResponse.accountStatementList data, int position);
    }

    public AccountStatementAdapter(Context activity, OnRecyclerItemClickListener listener, List<AccountStmtResponse.accountStatementList> infoList) {

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
                .inflate(R.layout.list_account_stmt, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
//            if (holder!=null && holder instanceof MyViewHolder) {
//
//            }
            final MyViewHolder myViewHolder = (MyViewHolder) holder;

            final AccountStmtResponse.accountStatementList info = infoList.get(position);

            myViewHolder.agentIdTv.setText(info.agencyCode);
            myViewHolder.agencyTv.setText(info.agencyName);
            myViewHolder.confirmIdTv.setText(info.referenceid);
            myViewHolder.dateTv.setText(info.txndate);
            myViewHolder.debitTv.setText(info.txnAMTD);
            myViewHolder.creditTv.setText(info.txnAMTC);
            myViewHolder.balanceTv.setText(info.balance);
            myViewHolder.typeTv.setText(info.transactionType);
            myViewHolder.remarksTv.setText(info.remarks);
            myViewHolder.updatedByTv.setText(info.updatedBy);
        }catch (Exception e){}

    }

    @Override
    public int getItemViewType (int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView agentIdTv, agencyTv, confirmIdTv, refNoTv, dateTv, debitTv, creditTv, balanceTv, typeTv,
                remarksTv, updatedByTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            agentIdTv =  itemView.findViewById(R.id.agentIdTv);
            agencyTv =  itemView.findViewById(R.id.agencyTv);
            confirmIdTv =  itemView.findViewById(R.id.confirmIdTv);
            refNoTv =  itemView.findViewById(R.id.refNoTv);
            dateTv =  itemView.findViewById(R.id.dateTv);
            debitTv =  itemView.findViewById(R.id.debitTv);
            creditTv =  itemView.findViewById(R.id.creditTv);
            balanceTv =  itemView.findViewById(R.id.balanceTv);
            typeTv =  itemView.findViewById(R.id.typeTv);
            remarksTv =  itemView.findViewById(R.id.remarksTv);
            updatedByTv =  itemView.findViewById(R.id.updatedByTv);
        }
    }

}
