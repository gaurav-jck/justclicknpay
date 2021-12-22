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
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.model.SalesVerificationModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 09/24/2017.
 */

public class ListForAgentVerificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<SalesVerificationModel.Data> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<SalesVerificationModel.Data> list, int position);
    }

    public ListForAgentVerificationAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<SalesVerificationModel.Data> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verification_list_item_for_agent, parent, false);

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
                final MyViewHolder vh = (MyViewHolder) holder;

                vh.name.setText(arrayList.get(position).AgencyName.toUpperCase().replace("(","\n(")+" ("+
                        arrayList.get(position).AgencyCode.toUpperCase().replace("(","\n(")+")" +
                        arrayList.get(position).Type.toUpperCase().replace("(","\n(")+"/" +
                        arrayList.get(position).ValidationCode.toUpperCase().replace("(","\n("));
                vh.doneCardUser_tv.setText(arrayList.get(position).DistributorId);
                vh.dateTime_tv.setText(arrayList.get(position).DateOfJoining);
                vh.email_tv.setText(arrayList.get(position).Email);
                vh.number_tv.setText(arrayList.get(position).Mobile);

                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.name.setTypeface(face);
                vh.approved_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);

                    }
                });
                vh.disapproved_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);

                    }
                });
                vh.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick(v,arrayList,position);

                    }
                });

            } else if (holder!=null && holder instanceof MyFooterViewHolder) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,doneCardUser_tv,dateTime_tv,email_tv,
                number_tv,approved_tv, disapproved_tv;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_tv);
            doneCardUser_tv = (TextView) view.findViewById(R.id.doneCardUser_tv);
            dateTime_tv = (TextView) view.findViewById(R.id.dateTime_tv);
            email_tv = (TextView) view.findViewById(R.id.email_tv);
            number_tv = (TextView) view.findViewById(R.id.number_tv);
            approved_tv = (TextView) view.findViewById(R.id.approved_tv);
            disapproved_tv = (TextView) view.findViewById(R.id.disapproved_tv);

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