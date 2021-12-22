package com.justclick.clicknbook.Fragment.flights.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAirlineAdapter extends RecyclerView.Adapter<RecyclerAirlineAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    ArrayList<String> arrayListID;
    private AirlineCheckChangeListener listener;

    public interface AirlineCheckChangeListener{
        void onCheckChange(String value, boolean check);
    }

    public RecyclerAirlineAdapter(Context activity, ArrayList<String> arrayList, ArrayList<String> arrayListID, AirlineCheckChangeListener listener) {

        this.context = activity;
        this.arrayList = arrayList;
        this.arrayListID = arrayListID;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        myViewHolder.tv_name.setText(arrayList.get(i));

        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onCheckChange(arrayList.get(i), b);
            }
        });

        String iurl = ApiConstants.FLIGHT_ICON_URL+arrayListID.get(i)+".png";
        Picasso.with(context).load(iurl).into(myViewHolder.img_ficon);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_ficon;
        TextView tv_name;
        CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            img_ficon = view.findViewById(R.id.img_ficon);
            tv_name = view.findViewById(R.id.tv_name);
            checkBox = view.findViewById(R.id.checkbox);
        }
    }

}
