package com.justclick.clicknbook.Fragment.flights.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerFlightOneWayAdapter extends RecyclerView.Adapter<RecyclerFlightOneWayAdapter.MyViewHolder> {

    Context context;
    ArrayList<FlightSearchResponseModel.response.flights> flightArray;
    String cabinClass;

    private FlightAdapterListener mListener;
    public interface FlightAdapterListener {
        void onClickAtAdapter(int position);
    }


    public RecyclerFlightOneWayAdapter(Context activity, ArrayList<FlightSearchResponseModel.response.flights> flightArray, String cabinClass, FlightAdapterListener mlistener) {
        this.mListener = mlistener;
        this.context = activity;
        this.flightArray = flightArray;
        this.cabinClass = cabinClass;
    }

    @Override
    public RecyclerFlightOneWayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_flight, parent, false);
        RecyclerFlightOneWayAdapter.MyViewHolder vh = new RecyclerFlightOneWayAdapter.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerFlightOneWayAdapter.MyViewHolder holder, final int position) {

        FlightSearchResponseModel.response.flights model = flightArray.get(position);

        String depTime=model.segments.get(0).get(0).departureTime;
        String arrTime=model.segments.get(model.segments.size()-1).get(
                       model.segments.get(model.segments.size()-1).size()-1).arrivalTime;

        holder.tv_fare.setText(CurrencyCode.getCurrencySymbol(model.fare.currency, context)+ model.fare.publishedFare);
        if(model.segments.get(0).get(0).isRefundable){
            holder.refundTv.setText("Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.green));
        }else {
            holder.refundTv.setText("Non-Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.refundTv.setText(model.segments.get(0).get(0).isRefundable?"Refundable":"Non-Refundable");
        String iurl = ApiConstants.FLIGHT_ICON_URL+model.segments.get(0).get(0).airline.code+".png";
        Picasso.with(context).load(iurl).into(holder.img_ficon);
        holder.tv_airline_name.setText(model.segments.get(0).get(0).airline.name);
        holder.tv_fCode.setText(model.segments.get(0).get(0).airline.code + "-" + model.segments.get(0).get(0).flight.flightNumber);
        holder.tv_cabin_class.setText(cabinClass);

//        holder.tv_time.setText(FlightDateAndTimeUtils.getSegmentDuration(model.segments.get(0).get(0).duration));
        holder.tv_time.setText(DateAndTimeUtils.getDurationBetweenTwoDates(arrTime,depTime));
        holder.tv_arr_dep.setText(DateAndTimeUtils.getSegmentDepArrTime(depTime) + " - " +
                DateAndTimeUtils.getSegmentDepArrTime(arrTime));
        holder.tv_stop.setText((model.segments.get(0).size() - 1) + " Stop");

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickAtAdapter(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return flightArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fare, refundTv, tv_time, tv_fCode, tv_stop, tv_arr_dep, tv_airline_name, tv_cabin_class;
        RelativeLayout rl_main;
        ImageView img_ficon;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_arr_dep = (TextView) itemView.findViewById(R.id.tv_arr_dep);
            tv_stop = (TextView) itemView.findViewById(R.id.tv_stop);
            tv_fare = (TextView) itemView.findViewById(R.id.tv_fare);
            refundTv = (TextView) itemView.findViewById(R.id.refundTv);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_fCode = (TextView) itemView.findViewById(R.id.tv_fCode);
            tv_airline_name = (TextView) itemView.findViewById(R.id.tv_airline_name);
            tv_cabin_class = (TextView) itemView.findViewById(R.id.tv_cabin_class);
            rl_main = (RelativeLayout) itemView.findViewById(R.id.rl_main);
            img_ficon = itemView.findViewById(R.id.img_ficon);
        }
    }
}
