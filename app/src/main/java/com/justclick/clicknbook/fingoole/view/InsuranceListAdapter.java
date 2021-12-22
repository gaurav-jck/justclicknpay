package com.justclick.clicknbook.fingoole.view;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.fingoole.model.TxnListResponseModel;
import com.justclick.clicknbook.jctPayment.Models.TxnListResponse;

import java.util.ArrayList;
import java.util.List;

public class InsuranceListAdapter extends RecyclerView.Adapter<InsuranceListAdapter.ViewHolder> {
    private Context context;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private ArrayList<TxnListResponseModel.InsTransactions> arrayList;

    public InsuranceListAdapter(Context context, OnRecyclerItemClickListener onRecyclerItemClickListener, ArrayList<TxnListResponseModel.InsTransactions> list) {
        this.context=context;
        this.arrayList=list;
        this.onRecyclerItemClickListener=onRecyclerItemClickListener;
    }

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<TxnListResponseModel.InsTransactions> list, TxnListResponseModel data, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.insurance_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nameTv.setText(arrayList.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nameTv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nameTv =  view.findViewById(R.id.nameTv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTv.getText() + "'";
        }
    }
}
