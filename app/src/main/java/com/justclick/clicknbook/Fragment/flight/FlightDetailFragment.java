package com.justclick.clicknbook.Fragment.flight;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.FlightSearchDataModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.FlightSearchRequestModel;
import com.justclick.clicknbook.utils.MyPreferences;

public class FlightDetailFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private Animation animUp, animDown;
    private OnFragmentInteractionListener mListener;
    private FlightSearchDataModel.OneWay flightSearchDataModel;
    private FlightSearchRequestModel flightSearchRequestModel;
    private LinearLayout detail2Linear,detail3Linear;
    private LinearLayout roundDetail2Linear,roundDetail3Linear,roundDetail1Linear;
    private RelativeLayout totalFareRel,totalFareDetailRel,roundMainTitleRel;
    private ImageView fareArrowImg;
    private int tripType=1;
    private TextView sourceCityTv,destCityTv,sourceNameFirstTv;
    private ToolBarTitleChangeListener titleChangeListener;
    private int noOfAdult=1, noOfChild=0, noOfInf=0;


    public FlightDetailFragment() {
        // Required empty public constructor
    }

    public static FlightDetailFragment newInstance(String param1, String param2) {
        FlightDetailFragment fragment = new FlightDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        context=getActivity();
        flightSearchDataModel= (new FlightSearchDataModel()).new OneWay();
        flightSearchRequestModel=new FlightSearchRequestModel();
        noOfAdult=MyPreferences.getFlightAdult(context);
        noOfChild= MyPreferences.getFlightChild(context);
        noOfInf=MyPreferences.getFlightInfant(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_flight_detail, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.flightDetail));

        if(getArguments().getSerializable("outBoundModel")!=null) {
            flightSearchDataModel = (FlightSearchDataModel.OneWay) getArguments().getSerializable("outBoundModel");
            tripType = getArguments().getInt("tripType");
        }
//        try {
//            initializeView(view);
//        }catch (Exception e){
//
//        }
        totalFareRel= (RelativeLayout) view.findViewById(R.id.totalFareRel);
        totalFareRel= (RelativeLayout) view.findViewById(R.id.totalFareRel);
        roundDetail2Linear= (LinearLayout) view.findViewById(R.id.roundDetail2Linear);
        roundDetail3Linear= (LinearLayout) view.findViewById(R.id.roundDetail3Linear);
        roundDetail1Linear= (LinearLayout) view.findViewById(R.id.roundDetail1Linear);
        totalFareDetailRel= (RelativeLayout) view.findViewById(R.id.totalFareDetailRel);
        roundMainTitleRel= (RelativeLayout) view.findViewById(R.id.roundMainTitleRel);
        fareArrowImg= (ImageView) view.findViewById(R.id.fareArrowImg);
        sourceCityTv= (TextView) view.findViewById(R.id.sourceCityTv);
        destCityTv= (TextView) view.findViewById(R.id.destCityTv);
        sourceNameFirstTv= (TextView) view.findViewById(R.id.sourceNameFirstTv);
        totalFareRel.setOnClickListener(this);
        view.findViewById(R.id.backArrow).setOnClickListener(this);
        animUp = AnimationUtils.loadAnimation(context,
                R.anim.translate_up);
        animDown = AnimationUtils.loadAnimation(context,
                R.anim.translate_down);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) /*throws Exception*/{

        ((TextView)view.findViewById(R.id.sourceCityTv)).setText(flightSearchDataModel.SCity);

        ((TextView)view.findViewById(R.id.destCityTv)).setText(flightSearchDataModel.DCity);
        ((TextView)view.findViewById(R.id.totalFareTv)).setText(flightSearchDataModel.FinalPrice+"");
        ((TextView)view.findViewById(R.id.adultFare)).setText(noOfAdult*flightSearchDataModel.AdtPrice+
                noOfChild*flightSearchDataModel.ChdPrice+noOfInf*flightSearchDataModel.InfPrice+"");
        ((TextView)view.findViewById(R.id.yq)).setText((noOfAdult*flightSearchDataModel.AdtYq+noOfChild*flightSearchDataModel.ChdYq+
                noOfInf*flightSearchDataModel.InfYq)+"");
        ((TextView)view.findViewById(R.id.tax)).setText((noOfAdult*flightSearchDataModel.AdtTax+noOfChild*flightSearchDataModel.ChdTax+
                noOfInf*flightSearchDataModel.InfTax)+"");
        ((TextView)view.findViewById(R.id.markUp)).setText(0+"");
        ((TextView)view.findViewById(R.id.grossFare)).setText(0+"");
        ((TextView)view.findViewById(R.id.discount)).setText(0+"");
        ((TextView)view.findViewById(R.id.tds)).setText(0+"");
        ((TextView)view.findViewById(R.id.payable)).setText(flightSearchDataModel.FinalPrice+"");
        ((TextView)view.findViewById(R.id.passenger)).setText((noOfAdult+noOfChild+noOfInf)+" PASSENGERS");

        ((TextView)view.findViewById(R.id.flightCodeTv)).setText(flightSearchDataModel.Flights.get(0).Carrier
                +" - "+flightSearchDataModel.Flights.get(0).FNumber);
        ((TextView)view.findViewById(R.id.flightNameTv)).setText(flightSearchDataModel.Flights.get(0).FName);
        ((TextView)view.findViewById(R.id.airlineFirstDetail)).setText(flightSearchDataModel.Duration+" | "+
                (flightSearchDataModel.Flights.size()-1)+" Stop | "+flightSearchDataModel.Refundable+" | "+
                flightSearchDataModel.Flights.get(0).AirLineClass);

        ((TextView)view.findViewById(R.id.classType)).setText(flightSearchDataModel.Flights.get(0).AirLineClass.toUpperCase());

        ((TextView)view.findViewById(R.id.sourceNameFirstTv)).setText(flightSearchDataModel.Flights.get(0).SCity+" | "+
                flightSearchDataModel.Flights.get(0).DTime +" | "+
                flightSearchDataModel.Flights.get(0).DDate);

        ((TextView)view.findViewById(R.id.destNameFirstTv)).setText(flightSearchDataModel.Flights.get(0).DCity+" | "+
                flightSearchDataModel.Flights.get(0).ATime +" | "+
                flightSearchDataModel.Flights.get(0).ADate);

        ((TextView)view.findViewById(R.id.sourceAirportFirstTv)).setText("("+flightSearchDataModel.Flights.get(0).SCity+")");
        ((TextView)view.findViewById(R.id.destAirportFirstTv)).setText("("+flightSearchDataModel.Flights.get(0).DCity+")");
        ((TextView)view.findViewById(R.id.durationFirstTv)).setText(flightSearchDataModel.Flights.get(0).Duration);

        Uri uri=Uri.parse(flightSearchDataModel.Flights.get(0).Image);
        ((SimpleDraweeView)view.findViewById(R.id.sourceImg)).setImageURI(ApiConstants.FlightImageBaseUrl+uri);

        view.findViewById(R.id.continueTv).setOnClickListener(this);

        if(flightSearchDataModel.Flights.size()==2){
            setSecondSourceFlightDetail(view);
        }
        else if(flightSearchDataModel.Flights.size()==3) {
            setSecondSourceFlightDetail(view);
            setThirdSourceFlightDetail(view);
        }
        if(tripType==2){
            roundMainTitleRel.setVisibility(View.VISIBLE);
            roundDetail1Linear.setVisibility(View.VISIBLE);
            FlightSearchDataModel.OneWay inBoundModel= (FlightSearchDataModel.OneWay) getArguments().getSerializable("inBoundModel");
            ((TextView)view.findViewById(R.id.totalFareTv)).setText(flightSearchDataModel.FinalPrice+inBoundModel.FinalPrice+"");
            ((TextView)view.findViewById(R.id.adultFare)).setText(flightSearchDataModel.AdtPrice+inBoundModel.AdtPrice+"");
            ((TextView)view.findViewById(R.id.yq)).setText((flightSearchDataModel.AdtYq+flightSearchDataModel.ChdPrice+flightSearchDataModel.InfYq)+(inBoundModel.AdtYq+inBoundModel.ChdPrice+inBoundModel.InfYq)+"");
            ((TextView)view.findViewById(R.id.tax)).setText((flightSearchDataModel.AdtTax+flightSearchDataModel.ChdTax+flightSearchDataModel.InfTax)+(inBoundModel.AdtTax+inBoundModel.ChdTax+inBoundModel.InfTax)+"");
            ((TextView)view.findViewById(R.id.markUp)).setText(0+"");
            ((TextView)view.findViewById(R.id.grossFare)).setText(0+"");
            ((TextView)view.findViewById(R.id.discount)).setText(0+"");
            ((TextView)view.findViewById(R.id.tds)).setText(0+"");
            ((TextView)view.findViewById(R.id.payable)).setText(flightSearchDataModel.FinalPrice+inBoundModel.FinalPrice+"");



            ((TextView)view.findViewById(R.id.roundSourceCityTv)).setText(inBoundModel.SCity);
            ((TextView)view.findViewById(R.id.roundDestCityTv)).setText(inBoundModel.DCity);

            ((TextView)view.findViewById(R.id.roundFlightCodeTv)).setText(inBoundModel.Flights.get(0).Carrier
                    +" - "+inBoundModel.Flights.get(0).FNumber);
            ((TextView)view.findViewById(R.id.roundFlightNameTv)).setText(inBoundModel.Flights.get(0).FName);
            ((TextView)view.findViewById(R.id.roundAirlineFirstDetail)).setText(inBoundModel.Duration+" | "+
                    (inBoundModel.Flights.size()-1)+" Stop | "+inBoundModel.Refundable+" | "+
                    inBoundModel.Flights.get(0).AirLineClass);

            ((TextView)view.findViewById(R.id.roundSourceNameFirstTv)).setText(inBoundModel.Flights.get(0).SCity+" | "+
                    inBoundModel.Flights.get(0).DTime +" | "+
                    inBoundModel.Flights.get(0).DDate);

            ((TextView)view.findViewById(R.id.roundDestNameFirstTv)).setText(inBoundModel.Flights.get(0).DCity+" | "+
                    inBoundModel.Flights.get(0).ATime +" | "+
                    inBoundModel.Flights.get(0).ADate);

            ((TextView)view.findViewById(R.id.roundSourceAirportFirstTv)).setText("("+inBoundModel.Flights.get(0).SCity+")");
            ((TextView)view.findViewById(R.id.roundDestAirportFirstTv)).setText("("+inBoundModel.Flights.get(0).DCity+")");
            ((TextView)view.findViewById(R.id.roundDurationFirstTv)).setText(inBoundModel.Flights.get(0).Duration);

            Uri uri1=Uri.parse(inBoundModel.Flights.get(0).Image);
            ((SimpleDraweeView)view.findViewById(R.id.roundSourceImg)).setImageURI(ApiConstants.FlightImageBaseUrl+uri1);

            if(inBoundModel.Flights.size()==2){
                roundDetail2Linear.setVisibility(View.VISIBLE);
                setRoundSecondSourceFlightDetail(view,inBoundModel);
            }
            else if(inBoundModel.Flights.size()==3) {
                roundDetail3Linear.setVisibility(View.VISIBLE);
                setRoundSecondSourceFlightDetail(view, inBoundModel);
                setRoundThirdSourceFlightDetail(view,inBoundModel);
            }

        }

    }

    private void setRoundThirdSourceFlightDetail(View view, FlightSearchDataModel.OneWay flightSearchDataModel) {

        roundDetail3Linear.setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.roundFlightNameTv3)).setText(flightSearchDataModel.Flights.get(2).FName);
        ((TextView) view.findViewById(R.id.roundFlightCodeTv3)).setText(flightSearchDataModel.Flights.get(2).Carrier
                + " - " + flightSearchDataModel.Flights.get(2).FNumber);
        ((TextView) view.findViewById(R.id.roundSourceAirportFirstTv3)).setText("(" + flightSearchDataModel.Flights.get(2).SCity + ")");
        ((TextView) view.findViewById(R.id.roundDestAirportFirstTv3)).setText("(" + flightSearchDataModel.Flights.get(2).DCity + ")");
        ((TextView) view.findViewById(R.id.roundDurationFirstTv3)).setText(flightSearchDataModel.Flights.get(2).Duration);
        ((TextView)view.findViewById(R.id.roundSourceNameFirstTv3)).setText(flightSearchDataModel.Flights.get(2).SCity+" | "+
                flightSearchDataModel.Flights.get(2).DTime +" | "+
                flightSearchDataModel.Flights.get(2).DDate);
        ((TextView)view.findViewById(R.id.roundDestNameFirstTv3)).setText(flightSearchDataModel.Flights.get(2).DCity+" | "+
                flightSearchDataModel.Flights.get(2).ATime +" | "+
                flightSearchDataModel.Flights.get(2).ADate);
        Uri uri2=Uri.parse(flightSearchDataModel.Flights.get(2).Image);
        ((SimpleDraweeView)view.findViewById(R.id.roundSourceImg3)).setImageURI(ApiConstants.FlightImageBaseUrl+uri2);

    }

    private void setRoundSecondSourceFlightDetail(View view, FlightSearchDataModel.OneWay flightSearchDataModel) {
        roundDetail2Linear.setVisibility(View.VISIBLE);

        ((TextView)view.findViewById(R.id.roundFlightNameTv2)).setText(flightSearchDataModel.Flights.get(1).FName);
        ((TextView)view.findViewById(R.id.roundFlightCodeTv2)).setText(flightSearchDataModel.Flights.get(1).Carrier
                +" - "+flightSearchDataModel.Flights.get(1).FNumber);
        ((TextView)view.findViewById(R.id.roundSourceAirportFirstTv2)).setText("("+flightSearchDataModel.Flights.get(1).SCity+")");
        ((TextView)view.findViewById(R.id.roundDestAirportFirstTv2)).setText("("+flightSearchDataModel.Flights.get(1).DCity+")");
        ((TextView)view.findViewById(R.id.roundDurationFirstTv2)).setText(flightSearchDataModel.Flights.get(1).Duration);
        ((TextView)view.findViewById(R.id.roundSourceNameFirstTv2)).setText(flightSearchDataModel.Flights.get(1).SCity+" | "+
                flightSearchDataModel.Flights.get(1).DTime +" | "+
                flightSearchDataModel.Flights.get(1).DDate);
        ((TextView)view.findViewById(R.id.roundDestNameFirstTv2)).setText(flightSearchDataModel.Flights.get(1).DCity+" | "+
                flightSearchDataModel.Flights.get(1).ATime +" | "+
                flightSearchDataModel.Flights.get(1).ADate);
        Uri uri1=Uri.parse(flightSearchDataModel.Flights.get(1).Image);
        ((SimpleDraweeView)view.findViewById(R.id.roundSourceImg2)).setImageURI(ApiConstants.FlightImageBaseUrl+uri1);

    }

    private void setThirdSourceFlightDetail(View view) {
        detail3Linear= (LinearLayout) view.findViewById(R.id.detail3Linear);
        detail3Linear.setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.flightNameTv3)).setText(flightSearchDataModel.Flights.get(2).FName);
        ((TextView) view.findViewById(R.id.flightCodeTv3)).setText(flightSearchDataModel.Flights.get(2).Carrier
                + " - " + flightSearchDataModel.Flights.get(2).FNumber);
        ((TextView) view.findViewById(R.id.sourceAirportFirstTv3)).setText("(" + flightSearchDataModel.Flights.get(2).SCity + ")");
        ((TextView) view.findViewById(R.id.destAirportFirstTv3)).setText("(" + flightSearchDataModel.Flights.get(2).DCity + ")");
        ((TextView) view.findViewById(R.id.durationFirstTv3)).setText(flightSearchDataModel.Flights.get(2).Duration);
        ((TextView)view.findViewById(R.id.sourceNameFirstTv3)).setText(flightSearchDataModel.Flights.get(2).SCity+" | "+
                flightSearchDataModel.Flights.get(2).DTime +" | "+
                flightSearchDataModel.Flights.get(2).DDate);
        ((TextView)view.findViewById(R.id.destNameFirstTv3)).setText(flightSearchDataModel.Flights.get(2).DCity+" | "+
                flightSearchDataModel.Flights.get(2).ATime +" | "+
                flightSearchDataModel.Flights.get(2).ADate);
        Uri uri2=Uri.parse(flightSearchDataModel.Flights.get(2).Image);
        ((SimpleDraweeView)view.findViewById(R.id.sourceImg3)).setImageURI(ApiConstants.FlightImageBaseUrl+uri2);
    }

    private void setSecondSourceFlightDetail(View view) {
        detail2Linear= (LinearLayout) view.findViewById(R.id.detail2Linear);
        detail2Linear.setVisibility(View.VISIBLE);

        ((TextView)view.findViewById(R.id.flightNameTv2)).setText(flightSearchDataModel.Flights.get(1).FName);
        ((TextView)view.findViewById(R.id.flightCodeTv2)).setText(flightSearchDataModel.Flights.get(1).Carrier
                +" - "+flightSearchDataModel.Flights.get(1).FNumber);
        ((TextView)view.findViewById(R.id.sourceAirportFirstTv2)).setText("("+flightSearchDataModel.Flights.get(1).SCity+")");
        ((TextView)view.findViewById(R.id.destAirportFirstTv2)).setText("("+flightSearchDataModel.Flights.get(1).DCity+")");
        ((TextView)view.findViewById(R.id.durationFirstTv2)).setText(flightSearchDataModel.Flights.get(1).Duration);
        ((TextView)view.findViewById(R.id.sourceNameFirstTv2)).setText(flightSearchDataModel.Flights.get(1).SCity+" | "+
                flightSearchDataModel.Flights.get(1).DTime +" | "+
                flightSearchDataModel.Flights.get(1).DDate);
        ((TextView)view.findViewById(R.id.destNameFirstTv2)).setText(flightSearchDataModel.Flights.get(1).DCity+" | "+
                flightSearchDataModel.Flights.get(1).ATime +" | "+
                flightSearchDataModel.Flights.get(1).ADate);
        Uri uri1=Uri.parse(flightSearchDataModel.Flights.get(1).Image);
        ((SimpleDraweeView)view.findViewById(R.id.sourceImg2)).setImageURI(ApiConstants.FlightImageBaseUrl+uri1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.continueTv:
                Bundle bundle=new Bundle();
                bundle.putSerializable("model",flightSearchDataModel);
                Fragment fragment=new FlightPaxInfoFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                break;
            case R.id.totalFareRel:
                menuDownArrowClick(totalFareDetailRel, fareArrowImg);
                break;

            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
        }
    }

    private void menuDownArrowClick(RelativeLayout totalFareDetailRel, ImageView fareArrowImg) {
        if(totalFareDetailRel.getVisibility()==View.VISIBLE){
            totalFareDetailRel.startAnimation(animUp);
            totalFareDetailRel.setVisibility(View.GONE);
            fareArrowImg.setRotation(0);
        }else {
            totalFareDetailRel.startAnimation(animDown);
            totalFareDetailRel.setVisibility(View.VISIBLE);
            fareArrowImg.setRotation(180);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
