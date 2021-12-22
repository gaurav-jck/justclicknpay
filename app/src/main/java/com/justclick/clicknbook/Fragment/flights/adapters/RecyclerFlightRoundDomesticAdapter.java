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

public class RecyclerFlightRoundDomesticAdapter extends RecyclerView.Adapter<RecyclerFlightRoundDomesticAdapter.MyViewHolder> {

    Context context;
    ArrayList<FlightSearchResponseModel.response.flights> flightArray;
    String cabinClass;
    private int lastClickedPosition=0;
//    private MyViewHolder lastView;

    private FlightAdapterListener mListener;
    public interface FlightAdapterListener {
        void onClickAtAdapter(int position);
    }


    public RecyclerFlightRoundDomesticAdapter(Context activity, ArrayList<FlightSearchResponseModel.response.flights> flightArray, String cabinClass, FlightAdapterListener mlistener) {
        this.mListener = mlistener;
        this.context = activity;
        this.flightArray = flightArray;
        this.cabinClass = cabinClass;
    }

    @Override
    public RecyclerFlightRoundDomesticAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_domestic_round_list_item, parent, false);
        RecyclerFlightRoundDomesticAdapter.MyViewHolder vh = new RecyclerFlightRoundDomesticAdapter.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerFlightRoundDomesticAdapter.MyViewHolder holder, final int position) {

        FlightSearchResponseModel.response.flights model = flightArray.get(position);

        String depTime=model.segments.get(0).get(0).departureTime;
        String arrTime=model.segments.get(model.segments.size()-1).get(
                model.segments.get(model.segments.size()-1).size()-1).arrivalTime;

        holder.priceTv.setText(CurrencyCode.getCurrencySymbol(model.fare.currency, context)+ model.fare.publishedFare.intValue());
        if(model.segments.get(0).get(0).isRefundable){
            holder.refundTv.setText("Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.green));
        }else {
            holder.refundTv.setText("Non-Refundable");
            holder.refundTv.setTextColor(context.getResources().getColor(R.color.app_red_color));
        }
        String iurl = ApiConstants.FLIGHT_ICON_URL+model.segments.get(0).get(0).airline.code+".png";
        Picasso.with(context).load(iurl).into(holder.airlineImg);
        holder.airlineCodeTv.setText(model.segments.get(0).get(0).airline.code/* + "\n" + model.segments.get(0).get(0).flight.flightNumber*/);

        holder.durationTv.setText(DateAndTimeUtils.getDurationBetweenTwoDates(arrTime, depTime));
        holder.deptTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(depTime));
        holder.arrTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(arrTime));
        holder.stopsTv.setText((model.segments.get(0).size() - 1) + " Stop");

        if(flightArray.get(position).isClick){
            holder.mainLin.setBackgroundResource(R.color.domesticRoundItemClickColor);
            lastClickedPosition=position;
        }else {
            holder.mainLin.setBackgroundResource(R.color.transparent);
        }

        holder.mainLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickAtAdapter(position);

                /*flightArray.get(lastClickedPosition).isClick=false;
                flightArray.get(position).isClick=true;
                lastClickedPosition=position;
                notifyDataSetChanged();*/

//                if(lastView!=null){
//                    lastView.mainLin.setBackgroundResource(R.color.transparent);
//                }
                holder.mainLin.setBackgroundResource(R.color.domesticRoundItemClickColor);
                flightArray.get(lastClickedPosition).isClick=false;
                flightArray.get(position).isClick=true;
                notifyItemChanged(lastClickedPosition);
                lastClickedPosition=position;
//                lastView=holder;
                notifyItemChanged(lastClickedPosition);
            }
        });

    }


    @Override
    public int getItemCount() {
        return flightArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView airlineCodeTv, deptTimeTv, arrTimeTv, durationTv, stopsTv, priceTv, refundTv;
        LinearLayout mainLin;
        ImageView airlineImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            deptTimeTv = itemView.findViewById(R.id.deptTimeTv);
            arrTimeTv = itemView.findViewById(R.id.arrTimeTv);
            stopsTv =  itemView.findViewById(R.id.stopsTv);
            priceTv =  itemView.findViewById(R.id.priceTv);
            refundTv =  itemView.findViewById(R.id.refundTv);
            durationTv = itemView.findViewById(R.id.durationTv);
            airlineCodeTv = itemView.findViewById(R.id.airlineCodeTv);
            mainLin =  itemView.findViewById(R.id.mainLin);
            airlineImg = itemView.findViewById(R.id.airlineImg);
        }
    }
}
