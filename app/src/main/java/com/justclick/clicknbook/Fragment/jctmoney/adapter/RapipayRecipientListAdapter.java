package com.justclick.clicknbook.Fragment.jctmoney.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class RapipayRecipientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<SenderDetailResponse.benificiaryDetailData> arrayList;
    private OnRecyclerItemClickListener itemClickListener;
    private String mobile;

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<SenderDetailResponse.benificiaryDetailData> list, int position);
    }

    public RapipayRecipientListAdapter(Context context, OnRecyclerItemClickListener fragment,
                                       ArrayList<SenderDetailResponse.benificiaryDetailData> arrayList, String mobile) {
        this.context = context;
        if(arrayList!=null && arrayList.size()>0){
            arrayList.get(0).isVisible=true;
        }
        this.arrayList=arrayList;
        this.mobile=mobile;
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
            vh.recipientTv.setText(arrayList.get(position).getAccountHolderName());
            vh.accountTv.setText(arrayList.get(position).getAccountNumber());
            vh.mobileTv.setText(mobile);
            vh.bankNameTv.setText(arrayList.get(position).getBankName());
            vh.ifscTv.setText(arrayList.get(position).getIfsc());

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
            vh.validateTv.setOnClickListener(new View.OnClickListener() {
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

            if(arrayList.get(position).getIsVerified().equalsIgnoreCase("NOT-VERIFIED")){
                vh.benVerifyTv.setText(arrayList.get(position).getIsVerified());
                vh.imageVerify.setImageResource(R.drawable.check_box_cross);
                vh.validateTv.setVisibility(View.VISIBLE);
            }else {
                vh.benVerifyTv.setText("VERIFIED");
                vh.imageVerify.setImageResource(R.drawable.check_box_checked);
                vh.validateTv.setVisibility(View.GONE);
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
                deleteTv, payNowTv, benVerifyTv, validateTv;
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
            recRel = (RelativeLayout) view.findViewById(R.id.recRel);
            viewLinear = (LinearLayout) view.findViewById(R.id.viewLinear);
            arrowImg = (ImageView) view.findViewById(R.id.arrowImg);
            imageVerify = (ImageView) view.findViewById(R.id.imageVerify);
        }
    }

}