package com.justclick.clicknbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.R;

public class HotelSearchDialog {
  private Dialog dialog;
  private ImageView hotel_art_img;
  private TextView cityNameTv,dateTv,noOfTravellerTv,tittleTv1, bottomTitleTv;
  private LinearLayout cityLinear;
  private RelativeLayout detail_tv;
  private View view;
  private Context context;
  private static HotelSearchDialog hotelSearchDialog;

  public static HotelSearchDialog getInstance(){
    if(hotelSearchDialog==null){
      hotelSearchDialog=new HotelSearchDialog();
    }
    return hotelSearchDialog;
  }

  public void showCustomDialog(Context context, int type){
    try {
      this.context=context;
      dialog = new Dialog(context);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(R.layout.hotel_search_dialog);
      final Window window= dialog.getWindow();
      window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);

      hotel_art_img= dialog.findViewById(R.id.hotel_art_img);
      cityNameTv=dialog.findViewById(R.id.cityNameTv);
      tittleTv1=dialog.findViewById(R.id.tittleTv1);
      dateTv=dialog.findViewById(R.id.dateTv);
      detail_tv=dialog.findViewById(R.id.detail_tv);
      bottomTitleTv=dialog.findViewById(R.id.bottomTitleTv);
      view=dialog.findViewById(R.id.view);
      cityLinear=dialog.findViewById(R.id.cityLinear);
      noOfTravellerTv=dialog.findViewById(R.id.noOfTravellerTv);
      setData(type);
      dialog.setCancelable(false);
      dialog.show();
    }catch (Exception e){
    }
  }


  private void setData(int type) {
    if(type==Constants.ModuleType.FLIGHT){
      FlightSearchRequestModel flightSearchRequestModel=MyPreferences.getFlightSearchRequestData(context);
      hotel_art_img.setImageResource(R.drawable.flight_img);
      tittleTv1.setText(R.string.Flight_search_title);
      bottomTitleTv.setText(R.string.flight_refund_title);
      cityNameTv.setText(flightSearchRequestModel.fromCity+" to "+
              flightSearchRequestModel.toCity);
      if(flightSearchRequestModel.journeyType.equals(Constants.FlightJourneyType.RETURN) &&
              flightSearchRequestModel.segments.get(0).returnDate!=null){
        dateTv.setText(DateAndTimeUtils.getDayOfWeek(flightSearchRequestModel.segments.get(0).departureDate) + "," +
                DateAndTimeUtils.getDay(flightSearchRequestModel.segments.get(0).departureDate) + " " +
                DateAndTimeUtils.getHotelMonth(flightSearchRequestModel.segments.get(0).departureDate) + " " +
                DateAndTimeUtils.getHotelYear(flightSearchRequestModel.segments.get(0).departureDate) + " - "+
                DateAndTimeUtils.getDayOfWeek(flightSearchRequestModel.segments.get(0).returnDate) + "," +
                DateAndTimeUtils.getDay(flightSearchRequestModel.segments.get(0).returnDate) + " " +
                DateAndTimeUtils.getHotelMonth(flightSearchRequestModel.segments.get(0).returnDate) + " " +
                DateAndTimeUtils.getHotelYear(flightSearchRequestModel.segments.get(0).returnDate) + " ");
      }else {
        dateTv.setText(DateAndTimeUtils.getDayOfWeek(flightSearchRequestModel.segments.get(0).departureDate) + "," +
                DateAndTimeUtils.getDay(flightSearchRequestModel.segments.get(0).departureDate) + " " +
                DateAndTimeUtils.getHotelMonth(flightSearchRequestModel.segments.get(0).departureDate) + " " +
                DateAndTimeUtils.getHotelYear(flightSearchRequestModel.segments.get(0).departureDate) + " ");
      }
      int NoOfPax = flightSearchRequestModel.adultCount +flightSearchRequestModel.childCount+flightSearchRequestModel.infantCount;
      noOfTravellerTv.setText(NoOfPax + " Traveller(s)");
    }
  }

  public void hideCustomDialog(){
    if(dialog!=null && dialog.isShowing()){
      dialog.dismiss();
    }
  }
}
