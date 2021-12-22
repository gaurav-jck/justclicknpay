package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.CreditReportListModel;
import com.justclick.clicknbook.model.JctMoneySenderDetailResponseModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class JctMoneyRecipientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<JctMoneySenderDetailResponseModel.BeneList> arrayList;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<JctMoneySenderDetailResponseModel.BeneList> list, int position);
    }

    public JctMoneyRecipientListAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<JctMoneySenderDetailResponseModel.BeneList> arrayList) {
        this.context = context;
        if(arrayList!=null && arrayList.size()>0){
            arrayList.get(0).isVisible=true;
        }
        this.arrayList=arrayList;
        itemClickListener= fragment;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jct_money_recipient_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            MyViewHolder vh = (MyViewHolder) holder;
            vh.recipientTv.setText(arrayList.get(position).recipient_name);
            vh.accountTv.setText(arrayList.get(position).account);
            vh.mobileTv.setText(arrayList.get(position).recipient_mobile);
            vh.bankNameTv.setText(arrayList.get(position).bank);
            vh.ifscTv.setText(arrayList.get(position).ifsc);

            Typeface face = Common.listAgencyNameTypeFace(context);
            vh.recipientTv.setTypeface(face);

            if(arrayList.get(position).isVisible){
                vh.arrowImg.setRotationX(180);
                vh.viewLinear.setVisibility(View.VISIBLE);
            }else {
                vh.arrowImg.setRotationX(0);
                vh.viewLinear.setVisibility(View.GONE);
            }

            vh.deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,position);
                }
            });
            vh.payNowTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,position);
                }
            });
            vh.accVerifyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,position);
                }
            });
            vh.recRel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.get(position).isVisible=!arrayList.get(position).isVisible;
                    notifyDataSetChanged();
                }
            });

            if(arrayList.get(position).is_verified.equalsIgnoreCase("1")){
                vh.benVerifyTv.setText("Account verified");
                vh.imageVerify.setImageResource(R.drawable.check_box_checked);
//                vh.accVerifyTv.setVisibility(View.VISIBLE);
            }else {
                vh.benVerifyTv.setText("Account not verified");
                vh.imageVerify.setImageResource(R.drawable.check_box_cross);
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
        return arrayList.size();
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
        public TextView recipientTv, accountTv, mobileTv, bankNameTv, ifscTv,
                deleteTv, payNowTv, benVerifyTv, accVerifyTv;
        public RelativeLayout recRel;
        public LinearLayout viewLinear;
        public ImageView arrowImg, imageVerify;
        public MyViewHolder(View view) {
            super(view);
            recipientTv = (TextView) view.findViewById(R.id.recipientTv);
            accountTv = (TextView) view.findViewById(R.id.accountTv);
            mobileTv = (TextView) view.findViewById(R.id.mobileTv);
            bankNameTv = (TextView) view.findViewById(R.id.bankNameTv);
            ifscTv = (TextView) view.findViewById(R.id.ifscTv);
            deleteTv = (TextView) view.findViewById(R.id.deleteTv);
            payNowTv = (TextView) view.findViewById(R.id.payNowTv);
            benVerifyTv = (TextView) view.findViewById(R.id.benVerifyTv);
            accVerifyTv = (TextView) view.findViewById(R.id.accVerifyTv);
            recRel = (RelativeLayout) view.findViewById(R.id.recRel);
            viewLinear = (LinearLayout) view.findViewById(R.id.viewLinear);
            arrowImg = (ImageView) view.findViewById(R.id.arrowImg);
            imageVerify = (ImageView) view.findViewById(R.id.imageVerify);
        }
    }

}