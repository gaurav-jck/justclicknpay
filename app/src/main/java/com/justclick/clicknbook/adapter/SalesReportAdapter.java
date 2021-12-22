package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.DepositListForAgentModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class SalesReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private ArrayList<DepositListForAgentModel.DepositListData> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
       public void onRecyclerItemClick(View view, ArrayList<DepositListForAgentModel.DepositListData> list, int position);
    }

    public SalesReportAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<DepositListForAgentModel.DepositListData> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_report_list_item, parent, false);

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
                vh.name.setText(arrayList.get(position).AgencyName.toUpperCase().replace("(","\n("));
                vh.updated_by_tv.setText(arrayList.get(position).updatedBy);
                vh.request_date_tv.setText(arrayList.get(position).RequestedDate);
                vh.request_amount_tv.setText(arrayList.get(position).RequestedAmount);
                vh.credit_date_tv.setText(arrayList.get(position).updatedDate);
                vh.credit_amount_tv.setText(arrayList.get(position).UpdatedAmount);
                vh.statusTv.setText(arrayList.get(position).TransactionStatus);
                vh.remarksTv.setText(arrayList.get(position).Remark);
                vh.bankTv.setText(arrayList.get(position).BankName);
                vh.bankNarrationTv.setText(arrayList.get(position).BankNarration);
                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.name.setTypeface(face);


                vh.remarksTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout=new LinearLayout(context);
                        layout.setBackgroundResource(R.drawable.remark_back);
                        PopupWindow popUp=new PopupWindow(layout,ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, false);

                        TextView textView=new TextView(context);
                        textView.setTextColor(context.getResources().getColor(R.color.darkBlueColor));
                        layout.addView(textView);

                        textView.setText(arrayList.get(position).Remark);

                        popUp.setContentView(layout);
                        popUp.setBackgroundDrawable(new BitmapDrawable());
                        popUp.setOutsideTouchable(true);

                        if(vh.remarksTv.getText().toString().length()>12) {
                            popUp.showAsDropDown(vh.remarksTv, (int) vh.remarksTv.getX() - 40, (int) vh.remarksTv.getY() - 80);
                        }
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
        public TextView name,updated_by_tv,request_date_tv,request_amount_tv,
                credit_date_tv,credit_amount_tv, remarksTv, statusTv,bankTv,bankNarrationTv;
        public  LinearLayout bankLinear;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_tv);
            updated_by_tv = (TextView) view.findViewById(R.id.updated_by_tv);
            request_date_tv = (TextView) view.findViewById(R.id.request_date_tv);
            request_amount_tv = (TextView) view.findViewById(R.id.request_amount_tv);
            credit_date_tv = (TextView) view.findViewById(R.id.credit_date_tv);
            credit_amount_tv = (TextView) view.findViewById(R.id.credit_amount_tv);
            remarksTv = (TextView) view.findViewById(R.id.remarksTv);
            statusTv = (TextView) view.findViewById(R.id.statusTv);
            bankTv = (TextView) view.findViewById(R.id.bankTv);
            bankLinear = (LinearLayout) view.findViewById(R.id.bankLinear);
            bankLinear.setVisibility(View.VISIBLE);
            bankNarrationTv = (TextView) view.findViewById(R.id.bankNarrationTv);

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