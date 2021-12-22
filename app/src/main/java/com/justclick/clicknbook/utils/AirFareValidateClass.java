package com.justclick.clicknbook.utils;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flight.FlightDetailFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FareValidateModel;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.FlightFareValidateRequestModel;

import java.util.ArrayList;

import okhttp3.ResponseBody;


public class AirFareValidateClass {

   public Context context;


    public void airFareValidation(final ArrayList<FlightSearchDataModel.OneWay> list, final Bundle bundle, final Context context,
                                         String TRIP, String SplRound) {
        this.context=context;
        final FlightFareValidateRequestModel model=makeRequest(list, TRIP, SplRound);
        showCustomDialog();
        new NetworkCall().callAirService(model, ApiConstants.AirFareValidate, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            hideCustomDialog();
                            responseHandler(response, 1,model,bundle);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private FlightFareValidateRequestModel makeRequest(ArrayList<FlightSearchDataModel.OneWay> list, String TRIP, String SplRound) {
        FlightSearchDataModel.OneWay outBoundModel=list.get(0);
        FlightFareValidateRequestModel model =new FlightFareValidateRequestModel();
        model.AgentID=MyPreferences.getLoginData(new LoginModel(),context).Data.DoneCardUser;
        model.AdultCount= MyPreferences.getFlightAdult(context)+"";
        model.Cache="N";
        model.ChildCount=MyPreferences.getFlightChild(context)+"";
        model.FromDate=outBoundModel.DDate;
        model.FromSector=outBoundModel.SCode;
        model.InfantCount=MyPreferences.getFlightInfant(context)+"";
        model.SplRound=SplRound;
        model.SplRoundCarrierCode=outBoundModel.Flights.get(0).Carrier;
        model.ToDate=outBoundModel.ADate;
        model.ToSector=outBoundModel.DCode;
        model.Trip=TRIP;
        model.TripType=outBoundModel.IsIntl;

        ArrayList<FlightFareValidateRequestModel.AirLineList> AirLineList=new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            FlightSearchDataModel.OneWay inBoundModel=list.get(i);
            ArrayList<FlightFareValidateRequestModel.Segments> Segments=new ArrayList<>();
            FlightFareValidateRequestModel.AirLineList airline=new FlightFareValidateRequestModel(). new AirLineList();
            airline.AdultFare=inBoundModel.AdtPrice+"";
            airline.FareKey=inBoundModel.FareKey;
            airline.Supplier=inBoundModel.Supplier;
            airline.TotalFare=inBoundModel.FinalPrice+"";
            airline.Yq=inBoundModel.AdtYq+"";
            for (int j=0;j<inBoundModel.Flights.size();j++) {
                FlightFareValidateRequestModel.Segments segment = new FlightFareValidateRequestModel().new Segments();

                segment.ADate = inBoundModel.Flights.get(j).ADate;
                segment.ATime = inBoundModel.Flights.get(j).ATime;
                segment.CFareKey = inBoundModel.Flights.get(j).CFareKey;
                segment.Carrier = inBoundModel.Flights.get(j).Carrier;
                segment.Class = inBoundModel.Flights.get(j).Class;
                segment.DCode = inBoundModel.Flights.get(j).DCode;
                segment.DDate = inBoundModel.Flights.get(j).DDate;
                segment.DTime = inBoundModel.Flights.get(j).DTime;
                segment.FName = inBoundModel.Flights.get(j).FName;
                segment.FNumber = inBoundModel.Flights.get(j).FNumber;
                segment.FareBasis = inBoundModel.Flights.get(j).FareBasis;
                segment.Group = inBoundModel.Flights.get(j).Group;
                segment.SCode = inBoundModel.Flights.get(j).SCode;
                Segments.add(segment);
            }
            airline.Segments=Segments;
            AirLineList.add(airline);
        }

        model.AirLineList=AirLineList;
        return model;
    }

    private void responseHandler(ResponseBody response, int TYPE, FlightFareValidateRequestModel airFareValidationRequestModel, Bundle bundle) {
        try {
//            hideCustomDialog();
            FareValidateModel fareValidateModel = new Gson().fromJson(response.string(), FareValidateModel.class);
            if(fareValidateModel!=null && fareValidateModel.Status!=null && fareValidateModel.Status.equalsIgnoreCase("Success") &&
                    fareValidateModel.Fare!=null && fareValidateModel.Fare.size()>0){
//                    bundle.putSerializable("busBookingRequestModel",busBookingRequestModel);
                FlightSearchDataModel.OneWay model= (FlightSearchDataModel.OneWay) bundle.getSerializable("outBoundModel");
                int prevPrice=model.FinalPrice;
                int newPrice=prevPrice;
                for(int i=0; i<fareValidateModel.Fare.get(0).PaxFares.size(); i++){
                    if(fareValidateModel.Fare.get(0).PaxFares.get(i).PaxType.toUpperCase().contains("AD")){
                        model.AdtPrice= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Basic);
                        model.AdtOT= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).OTax);
                        model.AdtYq = Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                        model.AdtTax= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Tax)-
                                Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                    }else if(fareValidateModel.Fare.get(0).PaxFares.get(i).PaxType.toUpperCase().contains("CH")){
                        model.ChdPrice= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Basic);
                        model.ChdOT= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).OTax);
                        model.ChdYq = Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                        model.ChdTax= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Tax)-
                                Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                    }else if(fareValidateModel.Fare.get(0).PaxFares.get(i).PaxType.toUpperCase().contains("INF")){
                        model.InfPrice= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Basic);
                        model.InfOT= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).OTax);
                        model.InfYq = Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                        model.InfTax= Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Tax)-
                                Integer.parseInt(fareValidateModel.Fare.get(0).PaxFares.get(i).Yq);
                    }
                    int noOfAdt= Integer.parseInt(airFareValidationRequestModel.AdultCount);
                    int noOfChd = Integer.parseInt(airFareValidationRequestModel.ChildCount);
                    int noOfInf= Integer.parseInt(airFareValidationRequestModel.InfantCount);
                    newPrice=noOfAdt*(model.AdtPrice+model.AdtOT+model.AdtTax+model.AdtYq)+
                            noOfChd*(model.ChdPrice+model.ChdOT+model.ChdTax+model.ChdYq) +
                            noOfInf*(model.InfPrice+model.InfOT+model.InfTax+model.InfYq);
                    model.FinalPrice=newPrice;
                }

                if(prevPrice>newPrice)
                    bundle.putSerializable("outBoundModel", model);
                FlightDetailFragment flightDetailFragment=new FlightDetailFragment();
                flightDetailFragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(flightDetailFragment);
//                Toast.makeText(context,fareValidateModel.Status, Toast.LENGTH_SHORT).show();

            }else if(fareValidateModel!=null && fareValidateModel.Status!=null && fareValidateModel.Status.equalsIgnoreCase("NA")){
                Toast.makeText(context, "Flight fare not found OR flight not available.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
//            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

}
