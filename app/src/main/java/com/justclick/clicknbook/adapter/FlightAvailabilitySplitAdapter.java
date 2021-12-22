package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class FlightAvailabilitySplitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener  {
    private Context context;
    private ArrayList<FlightSearchDataModel.OneWay> arrayList;
    //    private ArrayList<FlightSearchDataModelMain> arrayListMain;
    private OnRecyclerItemClickListener itemClickListener;
    private LinearLayout currentClick,lastClick;
    private int lastClickedPosition=0;
    private MyViewHolder lastView;

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(View view, ArrayList<FlightSearchDataModel.OneWay> list, int position);
    }

    public FlightAvailabilitySplitAdapter(Context context, OnRecyclerItemClickListener fragment,
                                          ArrayList<FlightSearchDataModel.OneWay> arrayList, int totalPageCount) {
        this.context = context;
        this.arrayList=arrayList;
//        setMainArray();
        itemClickListener= fragment;
    }

    private void setMainArray() {
       /* arrayListMain=new ArrayList<>();
        for(int i=0; i<arrayList.size(); i++){
            FlightSearchDataModelMain flightSearchDataModelMain=new FlightSearchDataModelMain();
            flightSearchDataModelMain.flightSearchDataModel=arrayList.get(i);
            arrayListMain.add(flightSearchDataModelMain);
        }*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_search_list_item_split, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder vh = (MyViewHolder) holder;
        vh.priceTv.setText("Rs. "+arrayList.get(position).FinalPrice);
        vh.airlineNoTv.setText(arrayList.get(position).Flights.get(0).Carrier+"-"+arrayList.get(position).Flights.get(0).FNumber);
        vh.airlineNameTv.setText(arrayList.get(position).Flights.get(0).FName);
        vh.timingTv.setText(arrayList.get(position).DTime+" - "+arrayList.get(position).ATime);
        vh.refundTypeTv.setText(arrayList.get(position).Refundable);
        if(arrayList.get(position).Flights.size()==1){
            vh.durationTv.setText("(Non-Stop) "+arrayList.get(position).Duration);
        }else {
            vh.durationTv.setText("("+(arrayList.get(position).Flights.size()-1)+" Stop) "+arrayList.get(position).Duration);
        }
        Uri uri=Uri.parse(arrayList.get(position).Flights.get(0).Image);
        vh.flightImage.setImageURI(ApiConstants.FlightImageBaseUrl+uri);
//        linear_main_menu.setOnClickListener(this);


        if(arrayList.get(position).isClick){
            vh.linear_main_menu.setBackgroundResource(R.color.flightListMainColor);
        }else {
            vh.linear_main_menu.setBackgroundResource(R.color.transparent);
        }
        vh.linear_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                itemClickListener.onRecyclerItemClick( v, arrayList, position);

//                ObjectAnimator.ofObject(vh.linear_main_menu, "backgroundColor", new ArgbEvaluator(),
//                        R.color.flightListMainColor,R.color.flightListSubMainColor1).setDuration(0).start();

                if(lastView!=null){
                lastView.linear_main_menu.setBackgroundResource(R.color.transparent);}
                vh.linear_main_menu.setBackgroundResource(R.color.flightListMainColor);
                arrayList.get(lastClickedPosition).isClick=false;
                arrayList.get(position).isClick=true;
                lastClickedPosition=position;
                lastView=vh;
//                notifyDataSetChanged();
//                notifyItemMoved(lastClickedPosition,position);
//                notifyItemChanged(position);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("FlightDetail",arrayList.get(position));
//                bundle.putInt("tripType",2);
//                FlightDetailFragment flightDetailFragment=new FlightDetailFragment();
//                flightDetailFragment.setArguments(bundle);
//                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(flightDetailFragment);


//                FlightDetailFragment flightDetailFragment=new FlightDetailFragment();
//                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(flightDetailFragment);
            }
        });


        Typeface face = Common.listAgencyNameTypeFace(context);
        Typeface face1 = Common.homeSubMenuItemTypeFace(context);
        Typeface face2 = Common.ButtonTypeFace1(context);

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
        switch (v.getId()) {
//            case  R.id.linear_main_menu:
//                ObjectAnimator.ofObject(linear_main_menu, "backgroundColor", new ArgbEvaluator(),
//                         /*Red*/0x88D40A0A, /*Blue*/0x00ffffff)  .setDuration(500).start();
//            break;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView airlineNoTv, airlineNameTv, timingTv, durationTv, refundTypeTv, priceTv;
        public LinearLayout linear_main_menu;

        //        public LinearLayout itemLin;
        public SimpleDraweeView flightImage;
        public MyViewHolder(View view) {
            super(view);
            airlineNoTv =  view.findViewById(R.id.airlineNoTv);
            airlineNameTv =  view.findViewById(R.id.airlineNameTv);
            timingTv = view.findViewById(R.id.timingTv);
            durationTv =  view.findViewById(R.id.durationTv);
            refundTypeTv =  view.findViewById(R.id.refundTypeTv);
            priceTv =  view.findViewById(R.id.priceTv);
//            itemLin =  view.findViewById(R.id.itemLin);
            flightImage =  view.findViewById(R.id.flightImage);
            linear_main_menu =  view.findViewById(R.id.linear_main_menu);
        }
    }

}
