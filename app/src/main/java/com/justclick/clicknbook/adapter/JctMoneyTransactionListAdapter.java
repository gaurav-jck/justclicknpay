package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.JctMoneyTransactionListResponseModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class JctMoneyTransactionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<JctMoneyTransactionListResponseModel.Data> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    private Typeface face1, face2, face3;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<JctMoneyTransactionListResponseModel.Data> list,JctMoneyTransactionListResponseModel.Data data, int position);
    }

    public JctMoneyTransactionListAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<JctMoneyTransactionListResponseModel.Data> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
        face3 = Common.homeMenuMainItemTypeFace(context);
        face1 = Common.EditTextTypeFace(context);
        face2 = Common.EditTextTypeFace(context);
//        face2 = Common.ButtonTypeFace(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jctmoney_transaction_list_item, parent, false);

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

                vh.txnId.setText(arrayList.get(position).ReservationId);
                vh.agencyNameTv.setText(arrayList.get(position).AgencyName);
                vh.senderMobileTv.setText("M. "+arrayList.get(position).CustMobile);
                vh.customerNameTv.setText(arrayList.get(position).Name);
                vh.txnAmountTv.setText("Rs. "+arrayList.get(position).TxnAMt);
                vh.txnChargesTv.setText(arrayList.get(position).TxnCharges);
                vh.bankNameTv.setText(arrayList.get(position).BankName);
                vh.accountNoTv.setText("Act. "+arrayList.get(position).AccountNo);
                vh.txnTypeTv.setText(arrayList.get(position).TxnType);
                vh.txnDateTv.setText(arrayList.get(position).TxnDate);

//                Typeface face = Common.listAgencyNameTypeFace(context);
//                vh.name.setTypeface(face);

                vh.print_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,arrayList.get(position),position);
                    }
                });

                vh.txnId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.get(position).isChildShow=!arrayList.get(position).isChildShow;
                        notifyDataSetChanged();
//                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });

                vh.closeChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.get(position).isChildShow=false;
                        notifyDataSetChanged();
//                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });

                if(arrayList.get(position).isChildShow){
                    vh.childView.setVisibility(View.VISIBLE);
                }else {
                    vh.childView.setVisibility(View.GONE);
                }

                createChildView(vh,arrayList.get(position));

                /*vh.refund_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });*/
            }
            else if (holder!=null && holder instanceof MyFooterViewHolder) {
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

    private void createChildView(MyViewHolder vh, final JctMoneyTransactionListResponseModel.Data data) {
        vh.childContainer.removeAllViews();
        for(int i=0; i<data.Data.size(); i++){
            final View childItem = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.jctmoney_transaction_list_child_item, vh.childContainer, false);

            TextView refundTv=childItem.findViewById(R.id.refund_tv);
            TextView agency=childItem.findViewById(R.id.agency);
            TextView txnId=childItem.findViewById(R.id.txnId);
            TextView refId=childItem.findViewById(R.id.refId);
            TextView txnChr=childItem.findViewById(R.id.txnChr);
            TextView tid=childItem.findViewById(R.id.tid);
            TextView bankRef=childItem.findViewById(R.id.bankRef);
            TextView date=childItem.findViewById(R.id.date);
            TextView status=childItem.findViewById(R.id.status);
            TextView amount=childItem.findViewById(R.id.amount);

            txnId.setText(data.ReservationId);
            refId.setText(data.Data.get(i).ReferenceID);
            txnChr.setText(data.Data.get(i).TxnCharges);
            tid.setText(data.Data.get(i).TID);
            bankRef.setText(data.Data.get(i).BankRefNo);
            date.setText(data.TxnDate);
            status.setText(data.Data.get(i).txstatus_desc);
            amount.setText(data.Data.get(i).TxnAMt);

            if(data.Data.get(i).RefundF){
                refundTv.setVisibility(View.VISIBLE);
            }else {
                refundTv.setVisibility(View.GONE);
            }

            final int finalI = i;
            refundTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,data, finalI);
//                    Toast.makeText(context, "refund", Toast.LENGTH_SHORT).show();
                }
            });

            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,data, finalI);
//                    Toast.makeText(context, "refund", Toast.LENGTH_SHORT).show();
                }
            });

            vh.childContainer.addView(childItem);
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
        switch (v.getId()) {
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txnId, agencyNameTv, senderMobileTv, customerNameTv, txnAmountTv,
                txnChargesTv, bankNameTv, accountNoTv, txnTypeTv, txnDateTv, refund_tv, print_tv,
                textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8,
                textView9, textView10;
        public RelativeLayout childView;
        public LinearLayout childContainer;
        public ImageView closeChild;
        public MyViewHolder(View view) {
            super(view);
            txnId = (TextView) view.findViewById(R.id.txnId);
            agencyNameTv = (TextView) view.findViewById(R.id.agencyNameTv);
            senderMobileTv = (TextView) view.findViewById(R.id.senderMobileTv);
            customerNameTv = (TextView) view.findViewById(R.id.customerNameTv);
            txnAmountTv = (TextView) view.findViewById(R.id.txnAmountTv);
            txnChargesTv = (TextView) view.findViewById(R.id.txnChargesTv);
            bankNameTv = (TextView) view.findViewById(R.id.bankNameTv);
            accountNoTv = (TextView) view.findViewById(R.id.accountNoTv);
            txnTypeTv = (TextView) view.findViewById(R.id.txnTypeTv);
            txnDateTv = (TextView) view.findViewById(R.id.txnDateTv);
            print_tv = (TextView) view.findViewById(R.id.print_tv);

//            child
            childView = (RelativeLayout) view.findViewById(R.id.childView);
            childContainer = (LinearLayout) view.findViewById(R.id.childContainer);
            closeChild = (ImageView) view.findViewById(R.id.closeChild);

            //lable ids
//            textView1 = (TextView) view.findViewById(R.id.textView1);
//            textView2 = (TextView) view.findViewById(R.id.textView2);
//            textView3 = (TextView) view.findViewById(R.id.textView3);
//            textView4 = (TextView) view.findViewById(R.id.textView4);
//            textView5 = (TextView) view.findViewById(R.id.textView5);
//            textView6 = (TextView) view.findViewById(R.id.textView6);
//            textView7 = (TextView) view.findViewById(R.id.textView7);
//            textView8 = (TextView) view.findViewById(R.id.textView8);
//            textView9 = (TextView) view.findViewById(R.id.textView9);
//            textView10 = (TextView) view.findViewById(R.id.textView10);

//            reservation_id_tv.setTypeface(face2);
//            acount_tv.setTypeface(face2);
//            mobile_tv.setTypeface(face2);
//            update_date_tv.setTypeface(face2);
//            transaction_type_tv.setTypeface(face2);
//            amount_tv.setTypeface(face2);
//            transaction_id_tv.setTypeface(face2);
//            bank_ref_no_tv.setTypeface(face2);
//            refund_tv.setTypeface(face2);
//            textView1.setTypeface(face1);
//            textView2.setTypeface(face1);
//            textView3.setTypeface(face1);
//            textView4.setTypeface(face1);
//            textView5.setTypeface(face1);
//            textView6.setTypeface(face1);
//            textView7.setTypeface(face1);
//            textView8.setTypeface(face1);
//            textView9.setTypeface(face1);
//            textView10.setTypeface(face1);
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
