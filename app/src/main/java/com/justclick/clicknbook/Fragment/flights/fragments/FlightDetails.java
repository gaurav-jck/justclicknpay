package com.justclick.clicknbook.Fragment.flights.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.FlightUtils;
import com.justclick.clicknbook.Fragment.flights.MyCustomDialog;
import com.justclick.clicknbook.Fragment.flights.requestModels.AddToCartRequest;
import com.justclick.clicknbook.Fragment.flights.requestModels.FareQuotesRequestModel;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.AddToCartResponse;
import com.justclick.clicknbook.Fragment.flights.responseModel.ApplyPromoCodeResponse;
import com.justclick.clicknbook.Fragment.flights.responseModel.FareQuotesResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FareRulesResponseModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;
import com.justclick.clicknbook.utils.InternetConnectionOffDialog;
import com.justclick.clicknbook.utils.MyCustomWaitingDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.SomethingWrongDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

public class FlightDetails extends Fragment implements View.OnClickListener{
    private final int ADD_TO_CART=0, FARE_QUOTES=1, FARE_RULES=2, APPLY=0, REMOVE=1;
    private Context context;
    private View view;
    private String str_token, searchKey, resultId, SessionId, FlightId, promocode="";
    private float totalFare, discountedTotalFare;
    private SharedPreferences sharedPreferences;
    private LinearLayout flightLegsContainer;
    private RelativeLayout promoCodeRelative;
    private TextView sourceTv, destinationTv, dateTravellerFlightCountTv, tv_total_price, tv_proceed,
                        adultFareTv, taxChargesTv, grossFareTv, convenienceFeeTv,totalFareTv;
    private int position, adultCount, childCount, infantCount;
    private FlightSearchResponseModel.response.flights flightResponse, flightResponseReturn;
    private FlightSearchRequestModel flightSearchRequestModel;
    private FareQuotesRequestModel fareQuotesRequestModel;
    private boolean isFareQuote;

//    promo views
    private EditText promoCodeEdt;
    private LinearLayout promoLin, promoContainerLin;
    private RelativeLayout promoListSelectionRel;
    private TextView promoApplyEdtTv,promoDiscountTv, startedFromTv, promoRemoveFromListTv,
            promoAppliedNameTv, promoAppliedDescriptionTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        setFlightSearchData();
        flightResponse = (FlightSearchResponseModel.response.flights) getArguments().getSerializable("FlightModel");
        flightResponseReturn = (FlightSearchResponseModel.response.flights) getArguments().getSerializable("FlightModelReturn");
        SessionId = getArguments().getString("SessionId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_flight_details, container, false);

            sharedPreferences = context.getSharedPreferences(FlightUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            str_token = sharedPreferences.getString(FlightUtils.KEY_LOGIN_TOKEN, "");

            // init all views
            initViews();

            if(flightResponseReturn!=null){
                setReturnData();
            }else {
                setAllDataToField();
            }
            discountedTotalFare=totalFare;

            // api Fare Quote
            if(!isFareQuote){
                addToCart();
            }

            promoCode();

        }
        return view;
    }

    private void setFlightSearchData() {
        try {
            flightSearchRequestModel= MyPreferences.getFlightSearchRequestData(context);
            adultCount = flightSearchRequestModel.adultCount;
            childCount = flightSearchRequestModel.childCount;
            infantCount = flightSearchRequestModel.infantCount;
        }catch (Exception e){

        }
    }

    // init all views
    private void initViews() {
        sourceTv = view.findViewById(R.id.sourceTv);
        destinationTv = view.findViewById(R.id.destinationTv);
        dateTravellerFlightCountTv = view.findViewById(R.id.dateTravellerFlightCountTv);

        tv_total_price = view.findViewById(R.id.tv_total_price);
        tv_proceed = view.findViewById(R.id.tv_proceed);

        adultFareTv = view.findViewById(R.id.adultFareTv);
        taxChargesTv = view.findViewById(R.id.taxChargesTv);
        grossFareTv = view.findViewById(R.id.grossFareTv);
        convenienceFeeTv = view.findViewById(R.id.convenienceFeeTv);
        totalFareTv = view.findViewById(R.id.totalFareTv);

        flightLegsContainer=view.findViewById(R.id.flightLegsContainer);

        tv_proceed.setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.fareRulesTv).setOnClickListener(this);
        view.findViewById(R.id.fareBreakUpTv).setOnClickListener(this);
        startedFromTv=view.findViewById(R.id.startedFromTv);
        promoCodeRelative=view.findViewById(R.id.promoCodeRelative);
        promoCodeRelative.setVisibility(View.GONE);
        promoDiscountTv=view.findViewById(R.id.promoDiscountTv);
    }

    private void promoCode() {
        String srvcCatId=(flightSearchRequestModel.isDomestic?1:2)+"";
        FragmentManager fm= getFragmentManager();
        PromoFragment promoFragment=new PromoFragment(new PromoFragment.OnPromoListener() {
            @Override
            public void onFragmentInteraction(int type, ApplyPromoCodeResponse senderResponse) {
                if(type==APPLY){
                    promoCodeRelative.setVisibility(View.VISIBLE);
                    promoDiscountTv.setTextColor(getResources().getColor(R.color.app_red_color));
                    promoDiscountTv.setText("- "+ CurrencyCode.getCurrencySymbol("INR", context)+
                            senderResponse.response.redeemAmount);
                    float amount = flightResponse.fare.publishedFare-senderResponse.response.redeemAmount;
                    tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+amount);
                    totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+amount);
                    discountedTotalFare =amount;
                    startedFromTv.setText(CurrencyCode.getCurrencySymbol("INR", context)+flightResponse.fare.publishedFare);
                    startedFromTv.setPaintFlags(startedFromTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } /*else if(type==REMOVE){
                    promoCodeRelative.setVisibility(View.GONE);
                    tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+flightResponse.fare.publishedFare);
                    totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+flightResponse.fare.publishedFare);
                    discountedTotalFare =flightResponse.fare.publishedFare;
                    startedFromTv.setText("");
                }*/else {
                    promoCodeRelative.setVisibility(View.GONE);
                    tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+flightResponse.fare.publishedFare);
                    totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+flightResponse.fare.publishedFare);
                    discountedTotalFare =flightResponse.fare.publishedFare;
                    startedFromTv.setText("");
                }
            }
        }, totalFare+"", SessionId, "1", srvcCatId);
        if(!promoFragment.isVisible()) {
            fm.beginTransaction().replace(R.id.promoFrame, promoFragment, promoFragment.getTag()).commit();
        }
    }

    private void hidePromoView() {
        promoLin.setVisibility(View.GONE);
    }

    private void setAllDataToField() {

        String depTime=flightResponse.segments.get(0).get(0).departureTime;
        String arrTime=flightResponse.segments.get(0).get(flightResponse.segments.get(0).size()-1).arrivalTime;

        sourceTv.setText(flightSearchRequestModel.fromCity);
        destinationTv.setText(flightSearchRequestModel.toCity);
//        destinationTv.setText(flightResponse.segments.get(flightResponse.segments.size()-1).
//                get(flightResponse.segments.get(flightResponse.segments.size()-1).size()-1).destination);
        dateTravellerFlightCountTv.setText(DateAndTimeUtils.getDayDDMMMYYDate(depTime));

        tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+" "+
                flightResponse.fare.publishedFare);

        for(int legs=0; legs<flightResponse.segments.size(); legs++){
            addLeg(flightResponse.segments.get(legs));
        }
        adultFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+flightResponse.fare.baseFare+"");
        taxChargesTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+flightResponse.fare.totalTaxes+"");
        grossFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+(flightResponse.fare.baseFare+flightResponse.fare.totalTaxes)+"");
        convenienceFeeTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" 0");
        totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+flightResponse.fare.publishedFare+"");
        totalFare =flightResponse.fare.publishedFare;
    }

    private void setReturnData() {
        String depTime=flightResponse.segments.get(0).get(0).departureTime;
        String arrTime=flightResponseReturn.segments.get(0).get(0).departureTime;

        sourceTv.setText(flightSearchRequestModel.fromCity);
        destinationTv.setText(flightSearchRequestModel.toCity);
        dateTravellerFlightCountTv.setText(DateAndTimeUtils.getDayDDMMMYYDate(depTime)+" - "+
                DateAndTimeUtils.getDayDDMMMYYDate(arrTime));

        for(int legs=0; legs<flightResponse.segments.size(); legs++){
            addLeg(flightResponse.segments.get(legs));
        }
        for(int legs=0; legs<flightResponseReturn.segments.size(); legs++){
            addLeg(flightResponseReturn.segments.get(legs));
        }

        totalFare =flightResponse.fare.publishedFare+flightResponseReturn.fare.publishedFare;
        tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+
                totalFare);

        adultFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+
                (flightResponse.fare.baseFare+flightResponseReturn.fare.baseFare)+"");
        taxChargesTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+
                (flightResponse.fare.totalTaxes+flightResponseReturn.fare.totalTaxes)+"");
        grossFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+
                (flightResponse.fare.baseFare+flightResponseReturn.fare.totalTaxes)+"");
        convenienceFeeTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" 0");
        totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency,context)+" "+
                totalFare+"");
    }

    private void addLeg(ArrayList<FlightSearchResponseModel.response.flights.segments> leg) {
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,10,5,0);
        View legView = getLayoutInflater().inflate(R.layout.fragment_flight_legs_view, null);
        legView.setLayoutParams(layoutParams);
        TextView sourceCityTv=legView.findViewById(R.id.sourceCityTv);
        TextView destCityTv=legView.findViewById(R.id.destCityTv);
        TextView airlineDetail=legView.findViewById(R.id.airlineDetail);
        RelativeLayout segmentRel=legView.findViewById(R.id.segmentRel);
        final ImageView legShowHideArrowImg=legView.findViewById(R.id.legShowHideArrowImg);
        final LinearLayout segmentsContainer = legView.findViewById(R.id.segmentsContainer);

        sourceCityTv.setText(leg.get(0).origin);
        destCityTv.setText(leg.get(leg.size()-1).destination);
        airlineDetail.setText(DateAndTimeUtils.getDurationBetweenTwoDates(leg.get(leg.size()-1).arrivalTime,
                leg.get(0).departureTime)+" | "+
                (leg.size()-1)+" Stop | "+(leg.get(0).isRefundable?"Refundable":"Non-Refundable"));

//        add segments
        for(int segmentPos=0; segmentPos<leg.size(); segmentPos++){
            addSegment(leg, segmentsContainer, segmentPos);
        }

        flightLegsContainer.addView(legView);

        if(flightLegsContainer.getChildCount()>1){
            segmentsContainer.setVisibility(View.GONE);
            legShowHideArrowImg.setRotation(-90);
        }else{
            segmentsContainer.setVisibility(View.VISIBLE);
            legShowHideArrowImg.setRotation(90);
        }

        segmentRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(segmentsContainer.getVisibility()== View.VISIBLE){
                    segmentsContainer.setVisibility(View.GONE);
                    legShowHideArrowImg.setRotation(-90);
                }else {
                    segmentsContainer.setVisibility(View.VISIBLE);
                    legShowHideArrowImg.setRotation(90);
                }
            }
        });

    }

    private void addSegment(ArrayList<FlightSearchResponseModel.response.flights.segments> leg, LinearLayout segmentsContainer, int segPos) {
        FlightSearchResponseModel.response.flights.segments segmentsModel=leg.get(segPos);
        View segment = getLayoutInflater().inflate(R.layout.fragment_flight_segment_view2, null);
        ImageView sourceImg=segment.findViewById(R.id.sourceImg);
        TextView flightNameTv=segment.findViewById(R.id.flightNameTv);
        TextView flightCodeTv=segment.findViewById(R.id.flightCodeTv);
        TextView sourceNameTv=segment.findViewById(R.id.sourceNameTv);
        TextView sourceCodeTv=segment.findViewById(R.id.sourceCodeTv);
        TextView destNameTv=segment.findViewById(R.id.destNameTv);
        TextView destCodeTv=segment.findViewById(R.id.destCodeTv);
        TextView deptTimeTv=segment.findViewById(R.id.deptTimeTv);
        TextView deptDateTv=segment.findViewById(R.id.deptDateTv);
        TextView arrDateTv=segment.findViewById(R.id.arrDateTv);
        TextView arrTimeTv=segment.findViewById(R.id.arrTimeTv);
        TextView deptAirportTv=segment.findViewById(R.id.deptAirportTv);
        TextView arrivalAirportTv=segment.findViewById(R.id.arrivalAirportTv);
        TextView deptTerminalTv=segment.findViewById(R.id.deptTerminalTv);
        TextView arrTerminalTv=segment.findViewById(R.id.arrTerminalTv);
        TextView durationTv=segment.findViewById(R.id.durationTv);
        TextView connectionTimeTv=segment.findViewById(R.id.connectionTimeTv);
        View connectionView=segment.findViewById(R.id.connectionView);
        TextView baggageTv=segment.findViewById(R.id.baggageTv);

        String imageUrl = ApiConstants.FLIGHT_ICON_URL+segmentsModel.airline.code+".png";
        Picasso.with(context).load(imageUrl).into(sourceImg);


        flightNameTv.setText(segmentsModel.airline.name);
        flightCodeTv.setText("("+segmentsModel.airline.code+")");

        sourceNameTv.setText(segmentsModel.originAirport.cityCode);
        sourceCodeTv.setText(segmentsModel.originAirport.cityName);
        deptTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(segmentsModel.departureTime));
        deptDateTv.setText(DateAndTimeUtils.getDayDDMMMYYDate(segmentsModel.departureTime));
        if(segmentsModel.originAirport.terminal!=null) {
            deptTerminalTv.setText("T" + segmentsModel.originAirport.terminal);
        }
        deptAirportTv.setText(segmentsModel.originAirport.name);

        durationTv.setText(DateAndTimeUtils.getSegmentDuration(segmentsModel.duration));

        destNameTv.setText(segmentsModel.destinationAirport.cityCode);
        destCodeTv.setText(segmentsModel.destinationAirport.cityName);
        arrTimeTv.setText(DateAndTimeUtils.getSegmentDepArrTime(segmentsModel.arrivalTime));
        arrDateTv.setText(DateAndTimeUtils.getDayDDMMMYYDate(segmentsModel.arrivalTime));
        if(segmentsModel.destinationAirport.terminal!=null) {
            arrTerminalTv.setText("T" + segmentsModel.destinationAirport.terminal);
        }
        arrivalAirportTv.setText(segmentsModel.destinationAirport.name);

        if(leg.size()>1 && segPos<leg.size()-1){
            connectionTimeTv.setText("Connection time..."+DateAndTimeUtils.getDurationBetweenTwoDates(leg.get(segPos+1).departureTime,
                    segmentsModel.arrivalTime)+"");
        }else {
            connectionTimeTv.setVisibility(View.INVISIBLE);
            connectionView.setVisibility(View.GONE);
        }
        if(segmentsModel.baggageInfo!=null && segmentsModel.baggageInfo.size()>0){
            String baggageInfo=segmentsModel.baggageInfo.get(0).text+" ("+segmentsModel.baggageInfo.get(0).paxType+")";
            for(int i=1; i<segmentsModel.baggageInfo.size(); i++){
                baggageInfo=baggageInfo+"\n"+segmentsModel.baggageInfo.get(i).text+" ("+segmentsModel.baggageInfo.get(i).paxType+")";
            }
            baggageTv.setText(baggageInfo);
        }else {
            baggageTv.setText("No baggage info available.");
        }

        segmentsContainer.addView(segment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;

            case R.id.fareRulesTv:
                fareRules();
                break;
            case R.id.fareBreakUpTv:
                fareBreakUp();
                break;

            case R.id.tv_proceed:
                if(isFareQuote){
                    Fragment fragment2 = new TravellersDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FlightModel", flightResponse);
                    bundle.putSerializable("FlightId", FlightId);
                    bundle.putSerializable("FlightModelReturn", flightResponseReturn);
                    bundle.putSerializable("FareQuotesRequestModel", fareQuotesRequestModel);
                    bundle.putFloat("totalFare", discountedTotalFare);
                    fragment2.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment2);
                }else {
                    showCustomWaitingDialog();
                    getFareQuote(null);
                }
                break;
        }
    }

    private void fareBreakUp() {
        Fragment fragment=new FareBreakUpFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("FlightResponse", flightResponse);
        bundle.putSerializable("FlightResponseReturn", flightResponseReturn);
        bundle.putSerializable("FlightRequest", flightSearchRequestModel);
        fragment.setArguments(bundle);
        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
    }

    private void fareRules() {
        showCustomDialog();
        String url;
        HttpUrl.Builder urlBuilder;
        if(flightResponse.segmentSource==1){
//            url=ApiConstants.FLIGHT_FARE_RULE;
            urlBuilder=HttpUrl.parse(ApiConstants.FLIGHT_FARE_RULE).newBuilder();
            urlBuilder.addQueryParameter("sessionId", SessionId);
//            urlBuilder.addQueryParameter("flightId", flightResponse.id);
            urlBuilder.addQueryParameter("flightId", FlightId);
            urlBuilder.addQueryParameter("index", "0");  // 1 in case of inbound
            urlBuilder.addQueryParameter("src", "f");
            url = urlBuilder.build().toString();
//            url=ApiConstants.FLIGHT_FARE_RULE +SessionId+"&flightId="+flightResponse.id+"&index="+(-1)+"&src="+"s";
        }else {
//            url=ApiConstants.FLIGHT_MINI_FARE_RULE +SessionId+"&flightId="+flightResponse.id;
//            url=ApiConstants.FLIGHT_MINI_FARE_RULE;
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
                            responseHandler(response, responseCode, FARE_RULES);
                        }else {
                            Toast.makeText(context,  R.string.fareRuleException, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addToCart(){
        showCustomWaitingDialog();
        AddToCartRequest requestModel=new AddToCartRequest();
        requestModel.sessionId=SessionId;
        if(flightResponseReturn!=null){
            requestModel.resultId=new String[2];
            requestModel.resultId[0]=flightResponse.id;
            requestModel.resultId[1]=flightResponseReturn.id;
        }else {
            requestModel.resultId=new String[1];
            requestModel.resultId[0]=flightResponse.id;
        }

        new NetworkCall().callFlightPostService(requestModel, ApiConstants.ADD_CART, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, responseCode, ADD_TO_CART);
                        }else {
                            hideCustomWaitiongDialog();
                            Toast.makeText(context, "Confirmation error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getFareQuote(AddToCartResponse senderResponse){
//        MyCustomDialog.showCustomDialog(context,"confirm");
//        MyCustomDialog.setDialogMessage(getResources().getString(R.string.please_wait));
        if(senderResponse!=null) {
            fareQuotesRequestModel = new FareQuotesRequestModel();
            fareQuotesRequestModel.sessionId = senderResponse.sessionId;
            fareQuotesRequestModel.resultId = senderResponse.response.flightId;
            FlightId =senderResponse.response.flightId;
        }
        new NetworkCall().callFlightPostService(fareQuotesRequestModel, ApiConstants.FARE_QUOTE, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomWaitiongDialog();
                        if(response!=null){
                            responseHandler(response, responseCode,FARE_QUOTES);
                        }else {
                            Toast.makeText(context, R.string.fareQuoteError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int responseCode, int TYPE) {
        if(TYPE==ADD_TO_CART){
            try {
                AddToCartResponse senderResponse = new Gson().fromJson(response.string(), AddToCartResponse.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status == 200) {
                        getFareQuote(senderResponse);
                    }else if(senderResponse.status==203){
                        hideCustomWaitiongDialog();
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity)context).customBackPress();
                    } else {
                        hideCustomWaitiongDialog();
//                        Toast.makeText(context,senderResponse.error.errorMessage,Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Confirmation error", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    hideCustomWaitiongDialog();
                    Toast.makeText(context, "Confirmation error", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                hideCustomWaitiongDialog();
                Toast.makeText(context, "Confirmation error", Toast.LENGTH_SHORT).show();
            }
        }else if(TYPE==FARE_QUOTES){
            try {
                FareQuotesResponseModel senderResponse = new Gson().fromJson(response.string(), FareQuotesResponseModel.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status == 200) {
                        isFareQuote=true;
                        SessionId=senderResponse.sessionId;
                        updatePrice(senderResponse);
                        if(senderResponse.response.isPriceChanged && senderResponse.response.isPriceUp){
                            Toast.makeText(context, "Fare updated, please check.", Toast.LENGTH_LONG).show();
                        }/*else {
                        Toast.makeText(context,"No change in fare.",Toast.LENGTH_SHORT).show();
                    }*/
                    } else if(responseCode==200 && senderResponse.status==203){
                        hideCustomWaitiongDialog();
                        Toast.makeText(context,senderResponse.error.errorMessage, Toast.LENGTH_SHORT).show();
                        ((NavigationDrawerActivity)context).customBackPress();
                    }else {
//                        Toast.makeText(context,senderResponse.error.errorMessage,Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, R.string.fareQuoteError, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.fareQuoteError, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context, R.string.fareQuoteException, Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                FareRulesResponseModel senderResponse = new Gson().fromJson(response.string(), FareRulesResponseModel.class);
                if(senderResponse!=null){
                    if(responseCode==200 && senderResponse.status == 200) {
                        Fragment fragment=new FareRulesFragment();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("FareRules", senderResponse);
                        bundle.putInt("SegmentSource", flightResponse.segmentSource);
                        if(flightResponseReturn!=null){
                            bundle.putString("SessionId", SessionId);
                            bundle.putString("FlightId", FlightId);
                            bundle.putInt("SegmentSourceReturn", flightResponseReturn.segmentSource);
                        }
                        fragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
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
    }

    private void updatePrice(FareQuotesResponseModel senderResponse) {
        totalFare = senderResponse.response.netPayableAmount.publishedFare;
        tv_total_price.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.netPayableAmount.currency,context)+" "+ totalFare);
        taxChargesTv.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.netPayableAmount.currency,context)+" "+
                senderResponse.response.netPayableAmount.totalTaxes);
        grossFareTv.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.netPayableAmount.currency,context)+" "+
                (senderResponse.response.netPayableAmount.baseFare+senderResponse.response.netPayableAmount.totalTaxes));
        convenienceFeeTv.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.netPayableAmount.currency,context)+" "+
                senderResponse.response.netPayableAmount.convenienceFee);
        totalFareTv.setText(CurrencyCode.getCurrencySymbol(senderResponse.response.netPayableAmount.currency,context)+" "+ totalFare);
//        Toast.makeText(context, "Fare updated", Toast.LENGTH_SHORT).show();
    }

    private void showOopsCustomDialog() {
        SomethingWrongDialog.getInstance().showCustomDialog(context);
    }
    private void showInternetCustomDialog() {
        InternetConnectionOffDialog.getInstance().showCustomDialog(context);
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }
    private void showCustomWaitingDialog() {
        MyCustomWaitingDialog.showCustomDialog(context,context.getResources().getString(R.string.fareConfirmingText));
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }
    private void hideCustomWaitiongDialog() {
        MyCustomWaitingDialog.hideCustomDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
