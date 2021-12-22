package com.justclick.clicknbook.Fragment.flights.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerReturnFlightAdapter extends RecyclerView.Adapter<RecyclerReturnFlightAdapter.MyViewHolder> {

    Context context;
    ArrayList<FlightSearchResponseModel.response.flights> flightArray;

    private FlightAdapetrListener mlistener;
    public interface FlightAdapetrListener{
        void onClickAtAdapter(int position);
    }


    public RecyclerReturnFlightAdapter(Context activity, ArrayList<FlightSearchResponseModel.response.flights> flightArray, FlightAdapetrListener mlistener) {
        this.mlistener = mlistener;
        this.context = activity;
        this.flightArray = flightArray;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_return_flight, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        FlightSearchResponseModel.response.flights model = flightArray.get(position);
        ArrayList<FlightSearchResponseModel.response.flights.segments> segmentModelOne = model.segments.get(0);
        ArrayList<FlightSearchResponseModel.response.flights.segments> segmentModelTwo = model.segments.get(1);

        String depTime=segmentModelOne.get(0).departureTime;
        String arrTime=segmentModelOne.get(segmentModelOne.size()-1).arrivalTime;

        holder.tv_grand_total.setText(CurrencyCode.getCurrencySymbol(model.fare.currency, context)+" "+model.fare.publishedFare+"");
        if(segmentModelOne.get(0).isRefundable){
            holder.refundTv.setText("Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.green));
        }else {
            holder.refundTv.setText("Non-Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.app_red_color));
        }
        holder.tv_source.setText(segmentModelOne.get(0).origin);
        holder.tv_destination.setText(segmentModelOne.get(segmentModelOne.size()-1).destination);

        holder.tv_d_time.setText(DateAndTimeUtils.getSegmentDepArrTime(depTime));
        holder.tv_a_time.setText(DateAndTimeUtils.getSegmentDepArrTime(arrTime));
        holder.tv_stop.setText((segmentModelOne.size()-1)+" Stop");
        holder.tv_d_totalTime.setText(DateAndTimeUtils.getDurationBetweenTwoDates(arrTime, depTime));
        String iUrl = ApiConstants.FLIGHT_ICON_URL+model.segments.get(0).get(0).airline.code+".png";
        Picasso.with(context).load(iUrl).into(holder.deptImg);

//        flight return
        depTime=segmentModelTwo.get(0).departureTime;
        arrTime=segmentModelTwo.get(segmentModelTwo.size()-1).arrivalTime;
        holder.tv_rsource.setText(segmentModelTwo.get(0).origin);
        holder.tv_rdestination.setText(segmentModelTwo.get(segmentModelTwo.size()-1).destination);
        holder.tv_rd_time.setText(DateAndTimeUtils.getSegmentDepArrTime(
                depTime));
        holder.tv_ra_time.setText(DateAndTimeUtils.getSegmentDepArrTime(
                arrTime));
        holder.tv_rstop.setText((segmentModelTwo.size()-1)+" Stop");
        holder.tv_rd_totalTime.setText(DateAndTimeUtils.getDurationBetweenTwoDates(
                arrTime, depTime));
        String iUrlR = ApiConstants.FLIGHT_ICON_URL+segmentModelTwo.get(0).airline.code+".png";
        Picasso.with(context).load(iUrlR).into(holder.arrImg);

        // on click
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistener.onClickAtAdapter(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return flightArray.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_d_time, tv_d_totalTime, tv_a_time, tv_source, tv_stop, tv_destination, tv_grand_total, refundTv;
        TextView tv_rd_time, tv_rd_totalTime, tv_ra_time, tv_rsource, tv_rstop, tv_rdestination;
        LinearLayout ll_main;
        ImageView deptImg, arrImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            ll_main = itemView.findViewById(R.id.passengerMainLin);
            tv_d_time = itemView.findViewById(R.id.tv_d_time);
            tv_d_totalTime = itemView.findViewById(R.id.tv_d_totalTime);
            tv_a_time = itemView.findViewById(R.id.tv_a_time);
            tv_source = itemView.findViewById(R.id.tv_source);
            tv_stop = itemView.findViewById(R.id.tv_stop);
            tv_destination = itemView.findViewById(R.id.tv_destination);
            tv_grand_total = itemView.findViewById(R.id.tv_grand_total);
            refundTv = itemView.findViewById(R.id.refundTv);
            tv_rd_time = itemView.findViewById(R.id.tv_rd_time);
            tv_rd_totalTime = itemView.findViewById(R.id.tv_rd_totalTime);
            tv_ra_time = itemView.findViewById(R.id.tv_ra_time);
            tv_rsource = itemView.findViewById(R.id.tv_rsource);
            tv_rstop = itemView.findViewById(R.id.tv_rstop);
            tv_rdestination = itemView.findViewById(R.id.tv_rdestination);
            deptImg = itemView.findViewById(R.id.deptImg);
            arrImg = itemView.findViewById(R.id.arrImg);
        }
    }
}
