package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.HotelMoreInfoResponseModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class HotelImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private ArrayList<HotelMoreInfoResponseModel.HotelImages> arrayList;
    private HotelImageAdapter.OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
       void onRecyclerItemClick(View view, ArrayList<HotelMoreInfoResponseModel.HotelImages> list, int position);
    }

    public HotelImageAdapter(Context context, OnRecyclerItemClickListener onRecyclerItemClickListener, ArrayList<HotelMoreInfoResponseModel.HotelImages> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener=onRecyclerItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_image_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                MyViewHolder vh = (MyViewHolder) holder;
//                try {
//                    Uri uri=Uri.parse(arrayList.get(position).ImagePath);
//                    vh.hotelImage.setImageURI(uri);
//                }catch (Exception e){
//
//                }

                vh.hotelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemClickListener.onRecyclerItemClick(view,arrayList,position);
                    }
                });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
//        return arrayList.size();
        return 10;
    }

    @Override
    public int getItemViewType (int position) {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView hotelImage;
        public MyViewHolder(View view) {
            super(view);
            hotelImage = (SimpleDraweeView) view.findViewById(R.id.hotelImage);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((NavigationDrawerActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenWidth = displaymetrics.widthPixels;
            int screenHeight = displaymetrics.heightPixels;

            hotelImage.requestLayout();
            hotelImage.setLayoutParams(new LinearLayout.LayoutParams((screenWidth/4), (screenWidth/4)));

        }
    }

}