package com.justclick.clicknbook.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.viewHolder.MyFooterViewHolder;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class HotelAvailabilityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context context;
    private ArrayList<HotelAvailabilityResponseModel.Hotels> arrayList;
    private int totalCount;
    private OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<HotelAvailabilityResponseModel.Hotels> list, int position);
    }

    public HotelAvailabilityAdapter(Context context, OnRecyclerItemClickListener fragment,
                                    ArrayList<HotelAvailabilityResponseModel.Hotels> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
        totalCount=totalPageCount;
        itemClickListener= fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_avail_list_item, parent, false);

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
               /* vh.name.setText(arrayList.get(position).HotelName.replace("(","\n("));

                vh.address_tv.setText(arrayList.get(position).Address);

                Typeface face = Common.listAgencyNameTypeFace(context);
                vh.name.setTypeface(face);

                if(arrayList.get(position).Currency.equalsIgnoreCase("INR")){
                    vh.amount_tv.setText(context.getResources().getString(R.string.inr_symbol) + " "+
                            arrayList.get(position).MaxRate);
                }else {
                    vh.amount_tv.setText(Currency.getInstance(arrayList.get(position).Currency).getSymbol()+" "+
                            arrayList.get(position).MaxRate);
                }
//                vh.amount_tv.setText(arrayList.get(position).MaxRate);


                try {
//                    Glide.with(context)
//                            .load(arrayList.get(position).HotelImage)
//                            .into(vh.hotelImage);
                    Uri uri=Uri.parse(arrayList.get(position).HotelImage);
                    vh.hotelImage.setImageURI(uri);
                    vh.ratingBar.setRating(Float.parseFloat(arrayList.get(position).HotelStarRating));
                }catch (Exception e){

                }



                vh.bookHotelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick( v, arrayList, position);
                    }
                });*/

                vh.itemLin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecyclerItemClick( v, arrayList, position);
                    }
                });
            } else if (holder!=null && holder instanceof MyFooterViewHolder) {
                MyFooterViewHolder vh = (MyFooterViewHolder) holder;
               /* if(position==0 || (position>0 && position>=Integer.parseInt(arrayList.get(0).TCount))){
                    vh.footer_lin.setVisibility(View.GONE);
                }else {
                    vh.footer_lin.setVisibility(View.VISIBLE);
                }*/
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
//        return arrayList.size();
        return 10;
    }

    @Override
    public int getItemViewType (int position) {
//        if(position==arrayList.size()) {
//            return TYPE_FOOTER;
//        }
        return TYPE_ITEM;
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
        public TextView name, amount_tv, address_tv, bookHotelTv;
        public RatingBar ratingBar;
        public SimpleDraweeView hotelImage;
        public LinearLayout itemLin;
        public MyViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.name_tv);
            amount_tv = view.findViewById(R.id.amount_tv);
            address_tv =  view.findViewById(R.id.address_tv);
            bookHotelTv = view.findViewById(R.id.bookHotelTv);
            hotelImage =  view.findViewById(R.id.hotelImage);
            ratingBar =  view.findViewById(R.id.ratingBar);
            itemLin =  view.findViewById(R.id.itemLin);

        }
    }
}