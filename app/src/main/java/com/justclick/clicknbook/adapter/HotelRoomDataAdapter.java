package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.HotelRoomDataResponseModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;
import java.util.Currency;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class HotelRoomDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<HotelRoomDataResponseModel.Rooms> arrayList;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
       void onRecyclerItemClick(View view, ArrayList<HotelRoomDataResponseModel.Rooms> list, int position);
    }

    public HotelRoomDataAdapter(Context context, OnRecyclerItemClickListener fragment,
                                ArrayList<HotelRoomDataResponseModel.Rooms> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_rooms_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.roomName_tv.setText(arrayList.get(position).RoomName);

        if(arrayList.get(position).CurrencyCode.equalsIgnoreCase("INR")){
            vh.price_tv.setText(context.getResources().getString(R.string.inr_symbol) + " "+
                    arrayList.get(position).RoomRate);
        }else {
            vh.price_tv.setText(Currency.getInstance(arrayList.get(position).CurrencyCode).getSymbol()+" "+
                    arrayList.get(position).RoomRate);
        }
        vh.cancellationPolicy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecyclerItemClick( v, arrayList, position);
            }
        });



//        vh.cancellationPolicy_tv.setText(arrayList.get(position).CancellationPolicy);
        vh.amenity_tv.setText(arrayList.get(position).RommAmenity);

        Typeface face = Common.listAgencyNameTypeFace(context);
        Typeface face1 = Common.homeSubMenuItemTypeFace(context);
        Typeface face2 = Common.ButtonTypeFace1(context);
        vh.roomName_tv.setTypeface(face);
        vh.cancellationPolicy_tv.setTypeface(face1);
        vh.book_tv.setTypeface(face2);

    }

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
        public TextView roomName_tv, price_tv, cancellationPolicy_tv, amenity_tv,book_tv;
        public MyViewHolder(View view) {
            super(view);
            roomName_tv = view.findViewById(R.id.roomName_tv);
            price_tv = view.findViewById(R.id.price_tv);
            cancellationPolicy_tv =  view.findViewById(R.id.cancellationPolicy_tv);
            amenity_tv = view.findViewById(R.id.amenity_tv);
            book_tv = view.findViewById(R.id.book_tv);
        }
    }

}