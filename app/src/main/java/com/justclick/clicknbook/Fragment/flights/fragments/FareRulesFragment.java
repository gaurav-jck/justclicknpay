package com.justclick.clicknbook.Fragment.flights.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.MyCustomDialog;
import com.justclick.clicknbook.Fragment.flights.responseModel.FareRulesResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.CurrencyCode;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;


public class FareRulesFragment extends Fragment implements View.OnClickListener{
    private Context context;
    View view;
    private ScrollView tableScroll, textScroll;
    private TextView textDescTv, oneWayTv, roundTripTv, adultTv, childTv, infantTv;
    private LinearLayout cancelContainerLin, changeContainerLin, flightTypeLin, passTypeLin;
    private FareRulesResponseModel fareRuleModel, fareRulesResponseModel, fareRulesResponseModelReturn;
    private String SessionId, FlightId;
    private int segmentSource, segmentSourceOne, segmentSourceReturn, adultPosOut, childPosOut, infPosOut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        if(getArguments().getSerializable("FareRules")!=null){
            fareRulesResponseModel= (FareRulesResponseModel) getArguments().getSerializable("FareRules");
            segmentSourceOne= getArguments().getInt("SegmentSource");
            segmentSourceReturn= getArguments().getInt("SegmentSourceReturn");
            segmentSource=segmentSourceOne;
        }
        if(getArguments().getSerializable("FlightId")!=null){
            SessionId= getArguments().getString("SessionId");
            FlightId= getArguments().getString("FlightId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_fare_rules, container, false);
        initViews();
        if(fareRulesResponseModel!=null) {
            if(FlightId!=null){
                flightTypeLin.setVisibility(View.VISIBLE);
            }else {
                flightTypeLin.setVisibility(View.GONE);
            }
            oneWayClicked();
        }
        return view;

    }

    private void oneWayClicked() {
        oneWayTv.setTextColor(getResources().getColor(R.color.black_text_color));
        oneWayTv.setTypeface(Typeface.DEFAULT_BOLD);
        roundTripTv.setTextColor(getResources().getColor(R.color.colorWhite));
        roundTripTv.setTypeface(Typeface.DEFAULT);
        fareRuleModel=fareRulesResponseModel;
        segmentSource=segmentSourceOne;
        setValues(fareRulesResponseModel);
    }
    private void twoWayClicked() {
        oneWayTv.setTextColor(getResources().getColor(R.color.colorWhite));
        oneWayTv.setTypeface(Typeface.DEFAULT);
        roundTripTv.setTextColor(getResources().getColor(R.color.black_text_color));
        roundTripTv.setTypeface(Typeface.DEFAULT_BOLD);
        fareRuleModel=fareRulesResponseModelReturn;
        segmentSource=segmentSourceReturn;
        setValues(fareRulesResponseModelReturn);
    }

    // init views
    private void initViews() {
        oneWayTv=view.findViewById(R.id.oneWayTv);
        roundTripTv=view.findViewById(R.id.roundTripTv);
        adultTv=view.findViewById(R.id.adultTv);
        childTv=view.findViewById(R.id.childTv);
        infantTv=view.findViewById(R.id.infantTv);

        flightTypeLin=view.findViewById(R.id.flightTypeLin);
        passTypeLin=view.findViewById(R.id.passTypeLin);
        tableScroll=view.findViewById(R.id.tableScroll);
        textScroll=view.findViewById(R.id.textScroll);
        textDescTv=view.findViewById(R.id.textDescTv);
        cancelContainerLin=view.findViewById(R.id.cancelContainerLin);
        changeContainerLin=view.findViewById(R.id.changeContainerLin);

        adultTv.setOnClickListener(this);
        childTv.setOnClickListener(this);
        infantTv.setOnClickListener(this);
        view.findViewById(R.id.okTv).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.oneWayTv).setOnClickListener(this);
        view.findViewById(R.id.roundTripTv).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.okTv:
                getFragmentManager().popBackStack();
                break;
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.oneWayTv:
                oneWayClicked();
                break;
            case R.id.roundTripTv:
                segmentSource=segmentSourceReturn;
                if(fareRulesResponseModelReturn!=null){
                    twoWayClicked();
                }else {
                    callFareRuleReturn();
                }
                break;
            case R.id.adultTv:
                adultClicked();
                break;
            case R.id.childTv:
                adultTv.setBackgroundResource(R.color.grayColorFlight);
                childTv.setBackgroundResource(R.color.app_blue_color);
                infantTv.setBackgroundResource(R.color.grayColorFlight);
                String fareRuleChild = "";
                for (int i = 0; i < fareRuleModel.response.get(childPosOut).passengerType.fareRules.size(); i++) {
                    fareRuleChild = fareRuleChild+fareRuleModel.response.get(childPosOut).passengerType.fareRules.get(i).fareRule + "\n";
                }
                textDescTv.setText(fareRuleChild);
                break;
            case R.id.infantTv:
                adultTv.setBackgroundResource(R.color.grayColorFlight);
                childTv.setBackgroundResource(R.color.grayColorFlight);
                infantTv.setBackgroundResource(R.color.app_blue_color);
                String fareRuleInf = "";
                for (int i = 0; i < fareRuleModel.response.get(infPosOut).passengerType.fareRules.size(); i++) {
                    fareRuleInf = fareRuleInf+fareRuleModel.response.get(infPosOut).passengerType.fareRules.get(i).fareRule + "\n";
                }
                textDescTv.setText(fareRuleInf);
                break;
        }
    }

    private void adultClicked() {
        adultTv.setBackgroundResource(R.color.app_blue_color);
        childTv.setBackgroundResource(R.color.grayColorFlight);
        infantTv.setBackgroundResource(R.color.grayColorFlight);
        String fareRule = "";
        for (int i = 0; i < fareRuleModel.response.get(adultPosOut).passengerType.fareRules.size(); i++) {
            fareRule = fareRule+fareRuleModel.response.get(adultPosOut).passengerType.fareRules.get(i).fareRule + "\n";
        }
        textDescTv.setText(fareRule);
    }

    private void callFareRuleReturn() {
        showCustomDialog();
        String url;
        HttpUrl.Builder urlBuilder;
        if(segmentSource==1){
            urlBuilder=HttpUrl.parse(ApiConstants.FLIGHT_FARE_RULE).newBuilder();
            urlBuilder.addQueryParameter("sessionId", SessionId);
            urlBuilder.addQueryParameter("flightId", FlightId);
            urlBuilder.addQueryParameter("index", "1");  // 1 in case of inbound
            urlBuilder.addQueryParameter("src", "f");
            url = urlBuilder.build().toString();
        }else {
            urlBuilder=HttpUrl.parse(ApiConstants.FLIGHT_MINI_FARE_RULE).newBuilder();
            urlBuilder.addQueryParameter("sessionId", SessionId);
            urlBuilder.addQueryParameter("flightId", FlightId);
            url = urlBuilder.build().toString();
        }

        new NetworkCall().getWithUrlHeaderFlight(url, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomDialog();
                        if(response!=null){
                            responseHandler(response, responseCode);
                        }else {
                            Toast.makeText(context,  R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int responseCode) {
        try {
            FareRulesResponseModel senderResponse = new Gson().fromJson(response.string(), FareRulesResponseModel.class);
            if(senderResponse!=null){
                if(responseCode==200 && senderResponse.status == 200) {
                    fareRulesResponseModelReturn=senderResponse;
                    twoWayClicked();
                } else if(responseCode==200 && senderResponse.status==203){
                    hideCustomDialog();
                    Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                    ((NavigationDrawerActivity)context).customBackPress();
                }else {
//                        Toast.makeText(context,senderResponse.error.errorMessage,Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.fareRuleException, Toast.LENGTH_SHORT).show();
        }
    }

    private void setValues(FareRulesResponseModel fareRulesResponseModel) {
        try {
            passTypeLin.setVisibility(View.GONE);
            adultClicked();
            if(segmentSource==1){
                if(fareRulesResponseModel.response.get(0).passengerType!=null &&
                        fareRulesResponseModel.response.get(0).passengerType.fareRules!=null &&
                        fareRulesResponseModel.response.get(0).passengerType.fareRules.size()>0) {
                    if (fareRulesResponseModel.response.size() > 1) {
                        passTypeLin.setVisibility(View.VISIBLE);
//                        TextView textView = adultTv;
                        for (int j = 0; j < fareRulesResponseModel.response.size(); j++) {
                            if(fareRulesResponseModel.response.get(j).passengerType.paxType.toUpperCase().contains("AD")){
                               adultTv.setVisibility(View.VISIBLE);
                                adultPosOut=j;
                            }else if(fareRulesResponseModel.response.get(j).passengerType.paxType.toUpperCase().contains("CH")){
                                childTv.setVisibility(View.VISIBLE);
                                childPosOut=j;
                            }else if(fareRulesResponseModel.response.get(j).passengerType.paxType.toUpperCase().contains("IN")){
                                infantTv.setVisibility(View.VISIBLE);
                                infPosOut=j;
                            }
                        }
                    } else {
                        passTypeLin.setVisibility(View.GONE);
                    }
                    String fareRule = "";
                    for (int i = 0; i < fareRulesResponseModel.response.get(adultPosOut).passengerType.fareRules.size(); i++) {
                        fareRule = fareRule+fareRulesResponseModel.response.get(adultPosOut).passengerType.fareRules.get(i).fareRule + "\n";
                    }
                    textScroll.setVisibility(View.VISIBLE);
                    tableScroll.setVisibility(View.GONE);
                    textDescTv.setText(fareRule);
                }else {
                    textScroll.setVisibility(View.VISIBLE);
                    tableScroll.setVisibility(View.GONE);
                    textDescTv.setText("No fare rule found.");
                }
            }else {
                if(fareRulesResponseModel.response.get(0).cancelRules.get(0).text!=null){
                    textScroll.setVisibility(View.VISIBLE);
                    tableScroll.setVisibility(View.GONE);
                    textDescTv.setText(fareRulesResponseModel.response.get(0).cancelRules.get(0).text);
                }else {
                    textScroll.setVisibility(View.GONE);
                    tableScroll.setVisibility(View.VISIBLE);
                    setValuesForPass(fareRulesResponseModel);
                }
            }
        }catch (Exception e){
            textScroll.setVisibility(View.VISIBLE);
            tableScroll.setVisibility(View.GONE);
            textDescTv.setText("No fare rule found.");
        }
    }

    private void setValuesForPass(FareRulesResponseModel fareRulesResponseModel) throws Exception {
        cancelContainerLin.removeAllViews();
        changeContainerLin.removeAllViews();
        if(fareRulesResponseModel.response!=null &&
                fareRulesResponseModel.response.size()>0){
//            cancelContainerLin.setVisibility(View.VISIBLE);
            for(int i = 0; i< fareRulesResponseModel.response.size(); i++){
                final View cancelRuleView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.fare_rules_item_view, cancelContainerLin, false);
                TextView ruleTypeTv=cancelRuleView.findViewById(R.id.ruleTypeTv);
                TextView column1Tv=cancelRuleView.findViewById(R.id.column1Tv);
                TextView column2Tv=cancelRuleView.findViewById(R.id.column2Tv);
                TextView column3Tv=cancelRuleView.findViewById(R.id.column3Tv);

                ruleTypeTv.setText(fareRulesResponseModel.response.get(0).cancelRules.get(i).departureType);
                if(fareRulesResponseModel.response.size()>0 && fareRulesResponseModel.response.get(0).cancelRules!=null &&
                        fareRulesResponseModel.response.get(0).cancelRules.size()>i){
                    column1Tv.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(0).cancelRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(0).cancelRules.get(i).feeAmount);
                }else {
                    column1Tv.setText("NA");
                }
                if(fareRulesResponseModel.response.size()>1 && fareRulesResponseModel.response.get(1).cancelRules!=null &&
                        fareRulesResponseModel.response.get(1).cancelRules.size()>i){
                    column2Tv.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(1).cancelRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(1).cancelRules.get(i).feeAmount);
                }else {
                    column2Tv.setText("NA");
                }
                if(fareRulesResponseModel.response.size()>2 && fareRulesResponseModel.response.get(2).cancelRules!=null &&
                        fareRulesResponseModel.response.get(2).cancelRules.size()>i){
                    column3Tv.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(2).cancelRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(2).cancelRules.get(i).feeAmount);
                }else {
                    column3Tv.setText("NA");
                }

                cancelContainerLin.addView(cancelRuleView);

//                change rule
                final View changeRuleView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.fare_rules_item_view, cancelContainerLin, false);
                TextView ruleTypeTvCh=changeRuleView.findViewById(R.id.ruleTypeTv);
                TextView column1TvCh=changeRuleView.findViewById(R.id.column1Tv);
                TextView column2TvCh=changeRuleView.findViewById(R.id.column2Tv);
                TextView column3TvCh=changeRuleView.findViewById(R.id.column3Tv);
                ruleTypeTvCh.setText(fareRulesResponseModel.response.get(0).changeRules.get(i).departureType);
                if(fareRulesResponseModel.response.size()>0 && fareRulesResponseModel.response.get(0).changeRules!=null &&
                        fareRulesResponseModel.response.get(0).changeRules.size()>i){
                    column1TvCh.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(0).changeRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(0).changeRules.get(i).feeAmount);
                }else {
                    column1TvCh.setText("NA");
                }
                if(fareRulesResponseModel.response.size()>1 && fareRulesResponseModel.response.get(1).changeRules!=null &&
                        fareRulesResponseModel.response.get(1).changeRules.size()>i){
                    column2TvCh.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(1).changeRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(1).changeRules.get(i).feeAmount);
                }else {
                    column2TvCh.setText("NA");
                }
                if(fareRulesResponseModel.response.size()>2 && fareRulesResponseModel.response.get(2).changeRules!=null &&
                        fareRulesResponseModel.response.get(2).changeRules.size()>i){
                    column3TvCh.setText(CurrencyCode.getCurrencySymbol(fareRulesResponseModel.response.get(2).changeRules.get(i).feeAmount, context)+
                            fareRulesResponseModel.response.get(2).changeRules.get(i).feeAmount);
                }else {
                    column3TvCh.setText("NA");
                }

                changeContainerLin.addView(changeRuleView);
            }
        }
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }
}
