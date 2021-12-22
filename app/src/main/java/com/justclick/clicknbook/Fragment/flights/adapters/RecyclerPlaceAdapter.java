package com.justclick.clicknbook.Fragment.flights.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.Fragment.flights.responseModel.FlightCityModel;
import com.justclick.clicknbook.R;

import java.util.ArrayList;

public class RecyclerPlaceAdapter extends RecyclerView.Adapter<RecyclerPlaceAdapter.MyViewHolder>{

    Context context;
    ArrayList<FlightCityModel.response> placesList;

    private ListAdapterListener mListener;
    public interface ListAdapterListener {
        void onClickAtOKButton(int position);
    }

    public RecyclerPlaceAdapter(Context activity, ArrayList<FlightCityModel.response> placesList, ListAdapterListener mListener) {
        this.mListener = mListener;
        this.context = activity;
        this.placesList = placesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_places, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        FlightCityModel.response data = placesList.get(position);

        holder.tv_code.setText(data.cityCode);
        holder.tv_airport.setText(data.airportName);
        holder.tv_country.setText(data.countryName);
        holder.tv_name.setText(data.cityName);


        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use callback function in the place you want
                mListener.onClickAtOKButton(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_code, tv_name, tv_airport, tv_country;
        RelativeLayout rl_main;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_code = (TextView) itemView.findViewById(R.id.tv_code);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_airport = (TextView) itemView.findViewById(R.id.tv_airport);
            tv_country = (TextView) itemView.findViewById(R.id.tv_country);
            rl_main = (RelativeLayout) itemView.findViewById(R.id.rl_main);
        }
    }


}
