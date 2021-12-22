package com.justclick.clicknbook.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.utils.Common;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HotelListMapHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Context context;
    private ArrayList<HotelAvailabilityResponseModel.Hotels> arrayList;

    private LayoutInflater mLayoutInflater;

    private boolean mIsSpaceVisible = true;
    OnRecyclerItemClickListener itemClickListener;

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(View view, ArrayList<HotelAvailabilityResponseModel.Hotels> list, int position);
    }

    private WeakReference<OnRecyclerItemClickListener> mCallbackRef;

    public HotelListMapHeaderAdapter(Context context, OnRecyclerItemClickListener fragment,
                                     ArrayList<HotelAvailabilityResponseModel.Hotels> arrayList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.arrayList = arrayList;
        mCallbackRef = new WeakReference<>(fragment);
        itemClickListener = fragment;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v =  mLayoutInflater.inflate(R.layout.hotel_avail_list_item, parent, false);
            return new MyItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_header_view, parent, false);
            return new HeaderItem(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyItem) {
            MyItem vh = (MyItem) holder;
            HotelAvailabilityResponseModel.Hotels dataItem = getItem(position);
//            ((MyItem) holder).mTitleView.setText(dataItem);
            ((MyItem) holder).mPosition = position;
            vh.itemLin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick( v, arrayList, position);
                }
            });
        } else if (holder instanceof HeaderItem) {
            ((HeaderItem) holder).mSpaceView.setVisibility(mIsSpaceVisible ? View.VISIBLE : View.GONE);
            ((HeaderItem) holder).mPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private HotelAvailabilityResponseModel.Hotels getItem(int position) {
        return arrayList.get(position-1);
    }

    class MyItem extends HeaderItem {
        TextView name_tv, address_tv, amount_tv;
        public LinearLayout itemLin;
        public MyItem(View itemView) {
            super(itemView);
            itemLin = itemView.findViewById(R.id.itemLin);
            name_tv =  itemView.findViewById(R.id.name_tv);
            address_tv =  itemView.findViewById(R.id.address_tv);
            amount_tv =  itemView.findViewById(R.id.amount_tv);

            name_tv.setTypeface(Common.TextViewTypeFace(context));
            address_tv.setTypeface(Common.OpenSansRegularTypeFace(context));
            amount_tv.setTypeface(Common.OpenSansRegularTypeFace(context));
        }
    }

    class HeaderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mSpaceView;
        int mPosition;

        public HeaderItem(View itemView) {
            super(itemView);
            mSpaceView = itemView.findViewById(R.id.space);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnRecyclerItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            itemClickListener.onRecyclerItemClick(v,arrayList,mPosition);
//            if (callback != null) {
//                callback.onRecyclerItemClick(v,arrayList,mPosition);
//            }
        }
    }

    public void hideSpace() {
        mIsSpaceVisible = false;
        notifyItemChanged(0);
    }

    public void showSpace() {
        mIsSpaceVisible = true;
        notifyItemChanged(0);
    }

}