package com.justclick.clicknbook.Fragment.flights.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.MyPreferences;

public class FareBreakUpFragment extends Fragment {
    private Context context;
    private View view;
    private TextView sourceCityTv, destCityTv, totalTv, adultTv, childTv, infantTv,
            adultBaseFareTv, childBaseFareTv, infBaseFareTv,
            adultTaxesTv, childTaxesTv, infTaxesTv,
            adultGSTTv, childGSTTv, infGSTTv,
            adultConvTv, childConvTv, infConvTv;
    private TextView sourceCityTvRet, destCityTvRet, totalTvRet, adultTvRet, childTvRet, infantTvRet,
            adultBaseFareTvRet, childBaseFareTvRet, infBaseFareTvRet,
            adultTaxesTvRet, childTaxesTvRet, infTaxesTvRet,
            adultGSTTvRet, childGSTTvRet, infGSTTvRet,
            adultConvTvRet, childConvTvRet, infConvTvRet;
    private LinearLayout childLin, infantLin, childLinRet, infantLinRet, fareLinRet;
    private RelativeLayout inboundRel, totalFareRelRet;
    private FlightSearchResponseModel.response.flights flightResponse, flightResponseReturn;
    private FlightSearchRequestModel flightRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        flightRequest = MyPreferences.getFlightSearchRequestData(context);
        if(getArguments().getSerializable("FlightResponse")!=null){
            flightResponse = (FlightSearchResponseModel.response.flights)
                    getArguments().getSerializable("FlightResponse");
            flightResponseReturn = (FlightSearchResponseModel.response.flights)
                    getArguments().getSerializable("FlightResponseReturn");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_fare_breakup, container, false);
        initViews();
        setValues();
        return view;

    }

    // init views
    private void initViews() {

        sourceCityTv=view.findViewById(R.id.sourceCityTv);
        destCityTv=view.findViewById(R.id.destCityTv);
        totalTv=view.findViewById(R.id.totalTv);
        childLin=view.findViewById(R.id.childLin);
        infantLin=view.findViewById(R.id.infantLin);
        adultTv=view.findViewById(R.id.adultTv);
        childTv=view.findViewById(R.id.childTv);
        infantTv=view.findViewById(R.id.infantTv);
        adultBaseFareTv=view.findViewById(R.id.adultBaseFareTv);
        childBaseFareTv=view.findViewById(R.id.childBaseFareTv);
        infBaseFareTv=view.findViewById(R.id.infBaseFareTv);
        adultTaxesTv=view.findViewById(R.id.adultTaxesTv);
        childTaxesTv=view.findViewById(R.id.childTaxesTv);
        infTaxesTv=view.findViewById(R.id.infTaxesTv);
//        adultGSTTv=view.findViewById(R.id.adultGSTTv);
//        childGSTTv=view.findViewById(R.id.childGSTTv);
//        infGSTTv=view.findViewById(R.id.infGSTTv);
        adultConvTv=view.findViewById(R.id.adultConvTv);
        childConvTv=view.findViewById(R.id.childConvTv);
        infConvTv=view.findViewById(R.id.infConvTv);

//        inbound
        sourceCityTvRet=view.findViewById(R.id.sourceCityTvRet);
        destCityTvRet=view.findViewById(R.id.destCityTvRet);
        totalFareRelRet=view.findViewById(R.id.totalFareRelRet);
        inboundRel=view.findViewById(R.id.inboundRel);
        fareLinRet=view.findViewById(R.id.fareLinRet);
        totalFareRelRet.setVisibility(View.GONE);inboundRel.setVisibility(View.GONE);fareLinRet.setVisibility(View.GONE);
        totalTvRet=view.findViewById(R.id.totalTvRet);
        childLinRet=view.findViewById(R.id.childLinRet);
        infantLinRet=view.findViewById(R.id.infantLinRet);
        adultTvRet=view.findViewById(R.id.adultTvRet);
        childTvRet=view.findViewById(R.id.childTvRet);
        infantTvRet=view.findViewById(R.id.infantTvRet);
        adultBaseFareTvRet=view.findViewById(R.id.adultBaseFareTvRet);
        childBaseFareTvRet=view.findViewById(R.id.childBaseFareTvRet);
        infBaseFareTvRet=view.findViewById(R.id.infBaseFareTvRet);
        adultTaxesTvRet=view.findViewById(R.id.adultTaxesTvRet);
        childTaxesTvRet=view.findViewById(R.id.childTaxesTvRet);
        infTaxesTvRet=view.findViewById(R.id.infTaxesTvRet);
//        adultGSTTvRet=view.findViewById(R.id.adultGSTTvRet);
//        childGSTTvRet=view.findViewById(R.id.childGSTTvRet);
//        infGSTTvRet=view.findViewById(R.id.infGSTTvRet);
        adultConvTvRet=view.findViewById(R.id.adultConvTvRet);
        childConvTvRet=view.findViewById(R.id.childConvTvRet);
        infConvTvRet=view.findViewById(R.id.infConvTvRet);

        view.findViewById(R.id.okTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setValues() {
        try {
            totalTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+
                    flightResponse.fare.publishedFare);
            setAdultValue();
            setChildValue();
            setInfantValue();

            sourceCityTv.setText(flightRequest.fromCity);
            destCityTv.setText(flightRequest.toCity);
            sourceCityTvRet.setText(flightRequest.toCity);
            destCityTvRet.setText(flightRequest.fromCity);

            if(flightResponseReturn!=null){
                totalFareRelRet.setVisibility(View.VISIBLE);inboundRel.setVisibility(View.VISIBLE);fareLinRet.setVisibility(View.VISIBLE);
                totalTvRet.setText(CurrencyCode.getCurrencySymbol(flightResponseReturn.fare.currency, context)+
                        flightResponseReturn.fare.publishedFare);
                setAdultValueRet();
                setChildValueRet();
                setInfantValueRet();
            }
        }catch (Exception e){

        }
    }

    private void setAdultValue() {
        adultTv.setText(flightRequest.adultCount+" x Adult");
        for(int i = 0; i< flightResponse.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponse.fareBreakDowns.get(i).passengerType.toUpperCase().contains("AD")){
                baseFare=baseFare+ flightResponse.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponse.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet){
                adultBaseFareTv.setText(getCurrency()+baseFare);
                adultTaxesTv.setText(getCurrency()+tax);
                adultConvTv.setText(getCurrency()+(baseFare+tax));
            }
        }
    }
    private void setChildValue() {
        childLin.setVisibility(View.GONE);
        childTv.setText(flightRequest.childCount+" x Child");
        for(int i = 0; i< flightResponse.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponse.fareBreakDowns.get(i).passengerType.toUpperCase().contains("C")){
                childLin.setVisibility(View.VISIBLE);
                baseFare=baseFare+ flightResponse.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponse.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet) {
                childBaseFareTv.setText(getCurrency()+baseFare);
                childTaxesTv.setText(getCurrency()+tax);
                childConvTv.setText(getCurrency()+(baseFare+tax));
            }
        }
    }
    private void setInfantValue() {
        infantLin.setVisibility(View.GONE);
        infantTv.setText(flightRequest.infantCount+" x Infant");
        for(int i = 0; i< flightResponse.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponse.fareBreakDowns.get(i).passengerType.toUpperCase().contains("IN")){
                infantLin.setVisibility(View.VISIBLE);
                baseFare=baseFare+ flightResponse.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponse.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet) {
                infBaseFareTv.setText(getCurrency()+baseFare);
                infTaxesTv.setText(getCurrency()+tax);
                infConvTv.setText(getCurrency()+(baseFare+tax));
            }
        }
    }

    private void setAdultValueRet() {
        adultTvRet.setText(flightRequest.adultCount+" x Adult");
        for(int i = 0; i< flightResponseReturn.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponseReturn.fareBreakDowns.get(i).passengerType.toUpperCase().contains("AD")){
                baseFare=baseFare+ flightResponseReturn.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponseReturn.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet){
                adultBaseFareTvRet.setText(getCurrency()+baseFare);
                adultTaxesTvRet.setText(getCurrency()+tax);
                adultConvTvRet.setText(getCurrency()+(baseFare+tax));
            }
        }
    }
    private void setChildValueRet() {
        childLinRet.setVisibility(View.GONE);
        childTvRet.setText(flightRequest.childCount+" x Child");
        for(int i = 0; i< flightResponseReturn.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponseReturn.fareBreakDowns.get(i).passengerType.toUpperCase().contains("C")){
                childLinRet.setVisibility(View.VISIBLE);
                baseFare=baseFare+ flightResponseReturn.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponseReturn.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet) {
                childBaseFareTvRet.setText(getCurrency()+baseFare);
                childTaxesTvRet.setText(getCurrency()+tax);
                childConvTvRet.setText(getCurrency()+(baseFare+tax));
            }
        }
    }
    private void setInfantValueRet() {
        infantLinRet.setVisibility(View.GONE);
        infantTvRet.setText(flightRequest.infantCount+" x Infant");
        for(int i = 0; i< flightResponseReturn.fareBreakDowns.size(); i++){
            float baseFare=0, tax=0;
            boolean isSet=false;
            if(flightResponseReturn.fareBreakDowns.get(i).passengerType.toUpperCase().contains("IN")){
                infantLinRet.setVisibility(View.VISIBLE);
                baseFare=baseFare+ flightResponseReturn.fareBreakDowns.get(i).baseFare;
                tax=tax+ flightResponseReturn.fareBreakDowns.get(i).totalTax;
                isSet=true;
            }
            if(isSet) {
                infBaseFareTvRet.setText(getCurrency()+baseFare);
                infTaxesTvRet.setText(getCurrency()+tax);
                infConvTvRet.setText(getCurrency()+(baseFare+tax));
            }
        }
    }



    private String getCurrency() {
        return CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context);
    }

}
