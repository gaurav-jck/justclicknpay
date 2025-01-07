package com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt.AccountStmtResponse;
import com.justclick.clicknbook.R;
import java.util.ArrayList;
import java.util.List;


public class AirBookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    Context context;
    int count;
    List<AirBookingListResponse.travelsListDetail> infoList;
    OnRecyclerItemClickListener listener;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<AccountStmtResponse.accountStatementList> list,
                                        AccountStmtResponse.accountStatementList data, int position);
    }

    public AirBookingListAdapter(Context activity, OnRecyclerItemClickListener listener,
                                 List<AirBookingListResponse.travelsListDetail> infoList) {

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
                .inflate(R.layout.list_air_booking_new, parent, false);

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

                final AirBookingListResponse.travelsListDetail info = infoList.get(position);

                myViewHolder.agentIdTv.setText(info.doneCardUser);
//                myViewHolder.agencyTv.setText(info.doneCardUser);
                myViewHolder.orderNoTv.setText(info.orderno);
                myViewHolder.orderIdTv.setText(info.reforderid);
                myViewHolder.dateTv.setText(info.createdDate);
//                myViewHolder.updateDateTv.setText(info.statusDate);
                myViewHolder.typeTv.setText(info.tripType+" ["+info.travelType+"]");
                myViewHolder.mobileTv.setText("M. "+info.paxMobileNumber);
                myViewHolder.nameTv.setText(info.paxName);
                myViewHolder.grossTv.setText(info.amount);

                if(Float.parseFloat(info.refundAmt)>0){
                    myViewHolder.refundTv.setText("Ref "+info.refundAmt);
                }else {
                    myViewHolder.refundTv.setText("");
                }


//                myViewHolder.refundLin.setOnClickListener(v -> listener.onRecyclerItemClick(myViewHolder.refundLin,
//                        (ArrayList<AccountStmtResponse.accountStatementList>) infoList,info,position));

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

        TextView agentIdTv, agencyTv, orderNoTv, orderIdTv, dateTv,updateDateTv, mobileTv, nameTv,grossTv,
                netTv, typeTv, refundTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            agentIdTv =  itemView.findViewById(R.id.agentIdTv);
            agencyTv =  itemView.findViewById(R.id.agencyTv);
            orderNoTv =  itemView.findViewById(R.id.orderNoTv);
            orderIdTv =  itemView.findViewById(R.id.orderIdTv);
            dateTv =  itemView.findViewById(R.id.dateTv);
            updateDateTv =  itemView.findViewById(R.id.updateDateTv);
            mobileTv =  itemView.findViewById(R.id.mobileTv);
            nameTv =  itemView.findViewById(R.id.nameTv);
            grossTv =  itemView.findViewById(R.id.grossTv);
            netTv =  itemView.findViewById(R.id.netTv);
            typeTv =  itemView.findViewById(R.id.typeTv);
            refundTv =  itemView.findViewById(R.id.refundTv);
        }
    }

    public class MyFooterViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout footer_lin;
        public MyFooterViewHolder(View view) {
            super(view);
            footer_lin = view.findViewById(R.id.footer_lin);
        }
    }

}
