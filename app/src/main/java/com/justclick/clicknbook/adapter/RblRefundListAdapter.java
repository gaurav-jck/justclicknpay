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
import com.justclick.clicknbook.model.RblRefundListResponseModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class RblRefundListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<RblRefundListResponseModel.RefundListModel> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    private Typeface face1, face2, face3;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<RblRefundListResponseModel.RefundListModel> list, int position);
    }

    public RblRefundListAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<RblRefundListResponseModel.RefundListModel> arrayList) {
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
                .inflate(R.layout.money_transfer_refund_list_item, parent, false);

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
                vh.reservation_id_tv.setText(arrayList.get(position).ReservationID);
                vh.acount_tv.setText(arrayList.get(position).AccountNo);
                vh.mobile_tv.setText(arrayList.get(position).MobileNo);
                vh.update_date_tv.setText(arrayList.get(position).UpdateDate);
                vh.transaction_type_tv.setText(arrayList.get(position).TxnType);
                vh.amount_tv.setText(arrayList.get(position).TxnAmt);
                vh.serviceChargeTv.setText(arrayList.get(position).ServiceCh);
                vh.transaction_id_tv.setText(arrayList.get(position).RBLTxnID);
                vh.bank_ref_no_tv.setText(arrayList.get(position).Bankrefno);
                vh.statusTv.setText(arrayList.get(position).TStatus);

//                Typeface face = Common.listAgencyNameTypeFace(context);
//                vh.name.setTypeface(face);

                if(arrayList.get(position).PrintFlag.equalsIgnoreCase("1")){
                    vh.print_tv.setVisibility(View.VISIBLE);
                }else {
                    vh.print_tv.setVisibility(View.INVISIBLE);
                }

                vh.print_tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });

                if((arrayList.get(position).RefundStatus.equalsIgnoreCase("0") ||
                        arrayList.get(position).RefundStatus.equalsIgnoreCase("-1") &&
                                arrayList.get(position).RefundFlag.equalsIgnoreCase("1"))){
                    vh.refund_tv.setVisibility(View.VISIBLE);
                }else {
                    vh.refund_tv.setVisibility(View.INVISIBLE);
                }

                vh.refund_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
                    }
                });
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
            case R.id.refund_tv:
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView reservation_id_tv, acount_tv, mobile_tv,update_date_tv,serviceChargeTv,
                transaction_type_tv, amount_tv,transaction_id_tv,bank_ref_no_tv,refund_tv, print_tv,statusTv,
                textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8,
                textView9, textView10;
        public MyViewHolder(View view) {
            super(view);
            reservation_id_tv = (TextView) view.findViewById(R.id.reservation_id_tv);
            acount_tv = (TextView) view.findViewById(R.id.acount_tv);
            mobile_tv = (TextView) view.findViewById(R.id.mobile_tv);
            update_date_tv = (TextView) view.findViewById(R.id.update_date_tv);
            transaction_type_tv = (TextView) view.findViewById(R.id.transaction_type_tv);
            amount_tv = (TextView) view.findViewById(R.id.amount_tv);
            serviceChargeTv = (TextView) view.findViewById(R.id.serviceChargeTv);
            transaction_id_tv = (TextView) view.findViewById(R.id.transaction_id_tv);
            bank_ref_no_tv = (TextView) view.findViewById(R.id.bank_ref_no_tv);
            refund_tv = (TextView) view.findViewById(R.id.refund_tv);
            print_tv = (TextView) view.findViewById(R.id.print_tv);
            statusTv = (TextView) view.findViewById(R.id.statusTv);

            //lable ids
            textView1 = (TextView) view.findViewById(R.id.textView1);
            textView2 = (TextView) view.findViewById(R.id.textView2);
            textView3 = (TextView) view.findViewById(R.id.textView3);
            textView4 = (TextView) view.findViewById(R.id.textView4);
            textView5 = (TextView) view.findViewById(R.id.textView5);
            textView6 = (TextView) view.findViewById(R.id.textView6);
            textView7 = (TextView) view.findViewById(R.id.textView7);
            textView8 = (TextView) view.findViewById(R.id.textView8);
            textView9 = (TextView) view.findViewById(R.id.textView9);
            textView10 = (TextView) view.findViewById(R.id.textView10);

//            reservation_id_tv.setTypeface(face2);
//            acount_tv.setTypeface(face2);
//            mobile_tv.setTypeface(face2);
//            update_date_tv.setTypeface(face2);
//            transaction_type_tv.setTypeface(face2);
//            amount_tv.setTypeface(face2);
//            transaction_id_tv.setTypeface(face2);
//            bank_ref_no_tv.setTypeface(face2);
            refund_tv.setTypeface(face2);
            textView1.setTypeface(face1);
            textView2.setTypeface(face1);
            textView3.setTypeface(face1);
            textView4.setTypeface(face1);
            textView5.setTypeface(face1);
            textView6.setTypeface(face1);
            textView7.setTypeface(face1);
            textView8.setTypeface(face1);
            textView9.setTypeface(face1);
            textView10.setTypeface(face1);
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
