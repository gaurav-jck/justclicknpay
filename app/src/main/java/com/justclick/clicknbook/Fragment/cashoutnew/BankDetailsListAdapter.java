package com.justclick.clicknbook.Fragment.cashoutnew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class BankDetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<BankListResponse.data> arrayList;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<BankListResponse.data> list, int position);
    }

    public BankDetailsListAdapter(Context context, OnRecyclerItemClickListener fragment,
                                  ArrayList<BankListResponse.data> arrayList) {
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
                .inflate(R.layout.payout_recipient_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            MyViewHolder vh = (MyViewHolder) holder;
            vh.recipientTv.setText(arrayList.get(position).accountHolderName);
            vh.accountTv.setText(arrayList.get(position).accountNo);
            vh.bankNameTv.setText(arrayList.get(position).bankName);
            vh.ifscTv.setText(arrayList.get(position).ifscCode);
            vh.mobileTv.setText(arrayList.get(position).mobileNo);

            Typeface face = Common.ralewayMedium(context);
            vh.recipientTv.setTypeface(face);

            /*if(arrayList.get(position).isVisible){
                vh.arrowImg.setRotationX(180);
                vh.viewLinear.setVisibility(View.VISIBLE);
            }else {
                vh.arrowImg.setRotationX(0);
                vh.viewLinear.setVisibility(View.GONE);
            }*/

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

            vh.recRel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.get(position).isVisible=!arrayList.get(position).isVisible;
                    notifyDataSetChanged();
                }
            });

            vh.benVerifyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,position);
                }
            });
            vh.benVerifyTv.setText(arrayList.get(position).isVerified.toUpperCase());
            if(arrayList.get(position).isVerified.toUpperCase().contains("NOT")){
                vh.benVerifyTv.setTextColor(context.getColor(R.color.red));
                vh.validateNoteTv.setVisibility(View.VISIBLE);
                vh.benVerifyTv.setClickable(true);
            }else {
                vh.benVerifyTv.setTextColor(context.getColor(R.color.green));
                vh.validateNoteTv.setVisibility(View.GONE);
                vh.benVerifyTv.setClickable(false);
            }

            vh.validateTv.setText(arrayList.get(position).reqApprove.toUpperCase());
            if(arrayList.get(position).reqApprove.equals("Pending")){
                vh.validateTv.setTextColor(context.getColor(R.color.dark_blue_color));
                vh.payNowTv.setVisibility(View.GONE);
                vh.deleteTv.setVisibility(View.VISIBLE);
            }else if(arrayList.get(position).reqApprove.equals("Rejected")){
                vh.validateTv.setTextColor(context.getColor(R.color.red));
                vh.payNowTv.setVisibility(View.GONE);
                vh.deleteTv.setVisibility(View.VISIBLE);
            }else {
                vh.validateTv.setTextColor(context.getColor(R.color.green));
                vh.payNowTv.setVisibility(View.VISIBLE);
                vh.deleteTv.setVisibility(View.GONE);
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
                deleteTv, payNowTv, benVerifyTv, validateTv, validateNoteTv;
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
            validateTv = (TextView) view.findViewById(R.id.validateTv);
            validateNoteTv = (TextView) view.findViewById(R.id.validateNoteTv);
            recRel = (RelativeLayout) view.findViewById(R.id.recRel);
            viewLinear = (LinearLayout) view.findViewById(R.id.viewLinear);
            arrowImg = (ImageView) view.findViewById(R.id.arrowImg);
            imageVerify = (ImageView) view.findViewById(R.id.imageVerify);
        }
    }

}