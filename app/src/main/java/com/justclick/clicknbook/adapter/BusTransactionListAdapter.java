package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.BusPrintFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusPrintResponse;
import com.justclick.clicknbook.model.BusTransactionListResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusPrintRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;

import java.util.ArrayList;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class BusTransactionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private OptModel OptModel;
    private BusTransactionListResponseModel busTransactionListResponseModel;
    private ArrayList<BusTransactionListResponseModel.BusListModel> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;
    private Typeface face1, face2, face3;
    private LoginModel loginModel;

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<BusTransactionListResponseModel.BusListModel> list, int position);
    }

    public BusTransactionListAdapter(Context context, OnRecyclerItemClickListener fragment,
                                     ArrayList<BusTransactionListResponseModel.BusListModel> arrayList) {
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
                .inflate(R.layout.bus_transaction_list_item, parent, false);

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

                vh.bookingIdTv.setText(arrayList.get(position).ReservationID);
                vh.pnrTv.setText(arrayList.get(position).PNR);
                vh.fareTv.setText(arrayList.get(position).TotalFare.toString());
                vh.srNumberTv.setText(position+1+"");

                vh.linear.setOnClickListener(new View.OnClickListener() {
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

    public void print(BusPrintRequestModel printRequestModel) {
        showCustomDialog();
        new NetworkCall().callBusService(printRequestModel, ApiConstants.TicketInfo, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerPrint(response);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandlerPrint(ResponseBody response) {
        try {
            hideCustomDialog();
            BusPrintResponse commonResponse = new Gson().fromJson(response.string(), BusPrintResponse.class);
            if (commonResponse != null) {

                Bundle bundle=new Bundle();
                bundle.putSerializable("model",commonResponse);
                BusPrintFragment fragment=new BusPrintFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
//                if(commonResponse.Status.equalsIgnoreCase("0")){
//                    Toast.makeText(context,commonResponse.Status,Toast.LENGTH_SHORT).show();
//
//                }else {

//                    Toast.makeText(context,commonResponse.BPoint, Toast.LENGTH_LONG).show();
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait");
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
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
        public TextView bookingIdTv,pnrTv,fareTv,srNumberTv;
        public LinearLayout linear;
        public MyViewHolder(View view) {
            super(view);

            bookingIdTv = view.findViewById(R.id.bookingIdTv);
            pnrTv = view.findViewById(R.id.pnrTv);
            fareTv = view.findViewById(R.id.fareTv);
            srNumberTv = view.findViewById(R.id.srNumberTv);
            linear = view.findViewById(R.id.linear);

//            textView8.setTypeface(face1);
//            textView9.setTypeface(face1);
//            textView10.setTypeface(face1);
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
