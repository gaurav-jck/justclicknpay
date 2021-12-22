package com.justclick.clicknbook.jctPayment.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.TransactionInfo;

import java.util.List;


public class RecyclerAllTransactions extends RecyclerView.Adapter<RecyclerAllTransactions.MyViewHolder>{

    Context context;
    List<TransactionInfo> infoList;


    public RecyclerAllTransactions(Context activity, List<TransactionInfo> infoList) {

        this.context = activity;
        this.infoList = infoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recent_transaction, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        TransactionInfo info = infoList.get(position);

        String s_name = info.getProductName();
        holder.tv_name.setText(s_name);
//        if(s_name.equals("Balance Enquiry")){
//            holder.tv_amount.setText("N/A");
//        } else {
//            holder.tv_amount.setText("Rs. " + info.getAmount());
//        }
        holder.tv_amount.setText("Rs. " + info.getAmount());
        holder.tv_rbl_request.setText(info.getRbl_request());
        if(info.getTransactionStatus().equals("00"))
        {
            holder.transaction_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle_icon));
//            holder.transaction_image.setRotation(0);
        }else
        {
            holder.transaction_image.setImageDrawable(context.getResources().getDrawable(R.drawable.transaction_fail_vector));
            holder.transaction_image.setRotation(45);
        }

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_amount, tv_rbl_request;
         ImageView transaction_image;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
            tv_rbl_request = (TextView) itemView.findViewById(R.id.tv_rbl_request);
            transaction_image = (ImageView) itemView.findViewById(R.id.transaction_image);
        }
    }
}
