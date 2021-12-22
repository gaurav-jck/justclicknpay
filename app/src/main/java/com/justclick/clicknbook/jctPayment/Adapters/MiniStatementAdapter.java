package com.justclick.clicknbook.jctPayment.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.newaeps.Balance_Enquiry_Activity_N.AepsMiniResponse;

import org.w3c.dom.Text;

import java.util.List;

public class MiniStatementAdapter extends RecyclerView.Adapter<MiniStatementAdapter.ViewHolder> {

    Context context;
    int count;
    List<AepsMiniResponse.msDetails> detailsList;

    public MiniStatementAdapter(Context activity,  List<AepsMiniResponse.msDetails> infoList) {

        this.context = activity;
        this.detailsList = infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mini_stmt_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.amountTv.setText(detailsList.get(position).amount);
        holder.txnTypeTv.setText(detailsList.get(position).txnType);
        holder.dateTv.setText(detailsList.get(position).date);
        holder.narrationTv.setText(detailsList.get(position).narration);
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView amountTv, txnTypeTv, dateTv, narrationTv;
        public ViewHolder(View v) {
            super(v);
            amountTv=v.findViewById(R.id.amountTv);
            txnTypeTv=v.findViewById(R.id.txnTypeTv);
            dateTv=v.findViewById(R.id.dateTv);
            narrationTv=v.findViewById(R.id.narrationTv);
        }
    }

}
