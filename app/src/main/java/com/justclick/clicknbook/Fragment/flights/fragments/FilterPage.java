package com.justclick.clicknbook.Fragment.flights.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.justclick.clicknbook.Fragment.flights.adapters.RecyclerAirlineAdapter;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;


public class FilterPage extends Fragment implements View.OnClickListener {
    private Context context;
    View view;
    TextView tv_price_stop, tv_time, tv_airline, tv_p_min, tv_p_max, tv_nonStop, tv_oneStop, tv_moreStop;
    TextView tv_morning, tv_afternoon, tv_evening, tv_night, tv_apply, tv_reset;
    LinearLayout ll_price_stop, ll_time, ll_airline;
    private CrystalRangeSeekbar rangeSeekbar;
    String s_time = "", str_min, str_max;
    int s_Stop = 0, t_min = 0, t_max = 0;
    float minPrice = 0, maxPrice = 0;
    double selectedMinPrice=0, selectedMaxPrice=0;
    RecyclerView recycler_airline;
    RecyclerAirlineAdapter recyclerAirlineAdapter;
    //AirLineAdapter airLineAdapter;
    LinkedHashSet<String> hashFlightObject = new LinkedHashSet<String>();
    LinkedHashSet<String> hashFlightID = new LinkedHashSet<String>();
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayListID = new ArrayList<>();
    ArrayList<String> arrayListIDFilter = new ArrayList<>();
    private String airlineFilterString;
    ArrayList<String> airlineList = new ArrayList<>();
    ArrayList<FlightSearchResponseModel.response.flights> flightArray, flightArrayTemp;


    private static OnUpdateListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        flightArray=new ArrayList<>();
        flightArrayTemp=new ArrayList<>();

        minPrice = Float.parseFloat(getArguments().getString("str_min"));
        maxPrice = Float.parseFloat(getArguments().getString("str_max"));
        hashFlightObject = (LinkedHashSet<String>) getArguments().getSerializable("airlineList");
        hashFlightID = (LinkedHashSet<String>) getArguments().getSerializable("airlineListID");
        flightArray = (ArrayList<FlightSearchResponseModel.response.flights>) getArguments().getSerializable("ArrayList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter_page, container, false);

//        arrayListIDFilter.addAll(hashFlightID);

        // init all views
        initAllViews();
        resetAllValues();

        rangeSeekbar = view.findViewById(R.id.rangeSeekbar);
        rangeSeekbar.setMinValue(minPrice);
        rangeSeekbar.setMaxValue(maxPrice);
        tv_p_min.setText(CurrencyCode.getCurrencySymbol("INR", context) + minPrice+"");
        tv_p_max.setText(CurrencyCode.getCurrencySymbol("INR", context) + maxPrice+"");

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tv_p_min.setText(CurrencyCode.getCurrencySymbol("INR", context)  + minValue);
                tv_p_max.setText(CurrencyCode.getCurrencySymbol("INR", context) + maxValue);
                selectedMinPrice = minValue.doubleValue();
                selectedMaxPrice = maxValue.doubleValue();
            }
        });

        Iterator itr = hashFlightObject.iterator();
        while(itr.hasNext())
        {
            arrayList.add((String) itr.next());
        }
        Iterator itrID = hashFlightID.iterator();
        while(itrID.hasNext())
        {
            arrayListID.add((String) itrID.next());
        }
        recyclerAirlineAdapter = new RecyclerAirlineAdapter(context, arrayList, arrayListID, new RecyclerAirlineAdapter.AirlineCheckChangeListener() {
            @Override
            public void onCheckChange(String value, boolean check) {
                if(check){
                    arrayListIDFilter.add(value);
                }else {
                    arrayListIDFilter.remove(value);
                }
            }
        });
        recycler_airline.setAdapter(recyclerAirlineAdapter);
        recycler_airline.setItemViewCacheSize(arrayList.size());

        return view;
    }


    // init all views
    private void initAllViews() {
        tv_price_stop = view.findViewById(R.id.tv_price_stop);
        tv_time = view.findViewById(R.id.tv_time);
        tv_airline = view.findViewById(R.id.tv_airline);
        tv_p_min = view.findViewById(R.id.tv_p_min);
        tv_p_max = view.findViewById(R.id.tv_p_max);
        tv_nonStop = view.findViewById(R.id.tv_nonStop);
        tv_oneStop = view.findViewById(R.id.tv_oneStop);
        tv_moreStop = view.findViewById(R.id.tv_moreStop);
        tv_morning = view.findViewById(R.id.tv_morning);
        tv_afternoon = view.findViewById(R.id.tv_afternoon);
        tv_evening = view.findViewById(R.id.tv_evening);
        tv_night = view.findViewById(R.id.tv_night);
        tv_apply = view.findViewById(R.id.tv_apply);
        tv_reset = view.findViewById(R.id.tv_reset);
        ll_price_stop =  view.findViewById(R.id.ll_price_stop);
        ll_time =  view.findViewById(R.id.ll_time);
        ll_airline = view.findViewById(R.id.ll_airline);

        recycler_airline = view.findViewById(R.id.recycler_airline);
        recycler_airline.setLayoutManager(new LinearLayoutManager(context));
        recycler_airline.setHasFixedSize(true);

        tv_price_stop.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_airline.setOnClickListener(this);
        tv_nonStop.setOnClickListener(this);
        tv_oneStop.setOnClickListener(this);
        tv_moreStop.setOnClickListener(this);
        tv_morning.setOnClickListener(this);
        tv_afternoon.setOnClickListener(this);
        tv_evening.setOnClickListener(this);
        tv_night.setOnClickListener(this);
        tv_apply.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }

    // getAirline name
    private void getAirlineName(){
        for (int i = 0; i < arrayList.size(); i++) {
            View view1 = recycler_airline.getChildAt(i);
            if (view1 != null) {
                ImageView img_ficon = view1.findViewById(R.id.img_ficon);
                TextView tv_name = view1.findViewById(R.id.tv_name);
                CheckBox checkBox = view1.findViewById(R.id.checkbox);

                if(checkBox.isChecked()){
                    airlineList.add(tv_name.getText().toString().trim());
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_apply:
                getAirlineName();
                applyFilter();
                break;

            case R.id.tv_reset:
                resetAllValues();
                getFragmentManager().popBackStack();
                break;

            case R.id.tv_price_stop:
                tv_price_stop.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_price_stop.setTextColor(Color.parseColor("#d53439"));
                tv_time.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_time.setTextColor(Color.parseColor("#000000"));
                tv_airline.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_airline.setTextColor(Color.parseColor("#000000"));
                ll_price_stop.setVisibility(View.VISIBLE);
                ll_time.setVisibility(View.GONE);
                ll_airline.setVisibility(View.GONE);
                break;

            case R.id.tv_time:
                tv_time.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_time.setTextColor(Color.parseColor("#d53439"));
                tv_price_stop.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_price_stop.setTextColor(Color.parseColor("#000000"));
                tv_airline.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_airline.setTextColor(Color.parseColor("#000000"));
                ll_price_stop.setVisibility(View.GONE);
                ll_time.setVisibility(View.VISIBLE);
                ll_airline.setVisibility(View.GONE);
                break;

            case R.id.tv_airline:
                tv_airline.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_airline.setTextColor(Color.parseColor("#d53439"));
                tv_price_stop.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_price_stop.setTextColor(Color.parseColor("#000000"));
                tv_time.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_time.setTextColor(Color.parseColor("#000000"));
                ll_price_stop.setVisibility(View.GONE);
                ll_time.setVisibility(View.GONE);
                ll_airline.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_nonStop:
                s_Stop = 0;
                tv_nonStop.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_oneStop.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_moreStop.setBackgroundColor(Color.parseColor("#ffffff"));
                break;

            case R.id.tv_oneStop:
                s_Stop = 1;
                tv_nonStop.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_oneStop.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_moreStop.setBackgroundColor(Color.parseColor("#ffffff"));
                break;

            case R.id.tv_moreStop:
                s_Stop = 2;
                tv_nonStop.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_oneStop.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_moreStop.setBackgroundColor(Color.parseColor("#dbdbdb"));
                break;

            case R.id.tv_morning:
                s_time = "filter";
                t_min = 0;
                t_max = 6;
                tv_morning.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_afternoon.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_evening.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_night.setBackgroundColor(Color.parseColor("#ffffff"));
                break;

            case R.id.tv_afternoon:
                s_time = "filter";
                t_min = 6;
                t_max = 12;
                tv_morning.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_afternoon.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_evening.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_night.setBackgroundColor(Color.parseColor("#ffffff"));
                break;

            case R.id.tv_evening:
                s_time = "filter";
                t_min = 12;
                t_max = 18;
                tv_morning.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_afternoon.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_evening.setBackgroundColor(Color.parseColor("#dbdbdb"));
                tv_night.setBackgroundColor(Color.parseColor("#ffffff"));
                break;

            case R.id.tv_night:
                s_time = "filter";
                t_min = 18;
                t_max = 24;
                tv_morning.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_afternoon.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_evening.setBackgroundColor(Color.parseColor("#ffffff"));
                tv_night.setBackgroundColor(Color.parseColor("#dbdbdb"));
                break;
        }
    }

    private void applyFilter() {
        flightArrayTemp.clear();
        for (int i = 0; i < flightArray.size(); i++) {
            if(s_Stop<0 && s_time.length()==0 && arrayListIDFilter.size()==0){
                if(flightArray.get(i).fare.publishedFare>=selectedMinPrice && flightArray.get(i).fare.publishedFare<=selectedMaxPrice) {
                    flightArrayTemp.add(flightArray.get(i));
                }
            }else {
                if(flightArray.get(i).fare.publishedFare>=selectedMinPrice && flightArray.get(i).fare.publishedFare<=selectedMaxPrice) {
                    checkFilter(i);
                }
            }
        }

        if (mListener != null) {
            mListener.onUpdate(flightArrayTemp, "");
        }
        getFragmentManager().popBackStack();
    }

    private void resetAllValues() {
        s_Stop=-1;
        s_time="";
        selectedMaxPrice =minPrice;
        selectedMaxPrice=maxPrice;

        flightArrayTemp.clear();
        flightArrayTemp.addAll(flightArray);
        if (mListener != null) {
            mListener.onUpdate(flightArrayTemp, "");
        }
    }

    private void checkFilter(int i){
        if(arrayListIDFilter.size()==0) {
            if (s_time.length() > 0 && s_Stop > -1) {
                timeAndStopFilter(i);
            } else if (s_time.length() > 0 && s_Stop < 0) {
                timeFilter(i);
            } else if (s_time.length() == 0 && s_Stop > -1) {
                stopFilter(i);
            }
        }else {
            timeStopAirlineFilter(i);
        }
    }

    private void timeStopAirlineFilter(int i) {
        airlineFilterString="";
        for(int a=0; a<arrayListIDFilter.size(); a++){
            airlineFilterString=airlineFilterString+arrayListIDFilter.get(a)+",";
        }

        if(airlineFilterString.length()>0){
            if(airlineFilterString.contains(flightArray.get(i).segments.get(0).get(0).airline.name)){
                if (s_time.length() > 0 && s_Stop > -1) {
                    timeAndStopFilter(i);
                } else if (s_time.length() > 0 && s_Stop < 0) {
                    timeFilter(i);
                } else if (s_time.length() == 0 && s_Stop > -1) {
                    stopFilter(i);
                }else {
                    flightArrayTemp.add(flightArray.get(i));
                }
            }
        }else {
            if (s_time.length() > 0 && s_Stop > -1) {
                timeAndStopFilter(i);
            } else if (s_time.length() > 0 && s_Stop < 0) {
                timeFilter(i);
            } else if (s_time.length() == 0 && s_Stop > -1) {
                stopFilter(i);
            }else {
                flightArrayTemp.add(flightArray.get(i));
            }
        }

    }

    private void stopFilter(int i) {
        if(s_Stop==0){
            boolean stop=true;
            for(int j=0; j<flightArray.get(i).segments.size(); j++){
                if(flightArray.get(i).segments.get(j).size()>1){
                    stop=false;
                    break;
                }
            }
            if(stop){
                flightArrayTemp.add(flightArray.get(i));
            }
        }else if(s_Stop==1){
            boolean stop=true;
            for(int j=0; j<flightArray.get(i).segments.size(); j++){
                if(flightArray.get(i).segments.get(j).size()!=2){
                    stop=false;
                    break;
                }
            }
            if(stop){
                flightArrayTemp.add(flightArray.get(i));
            }
        }else if(s_Stop==2){
            boolean stop=true;
            for(int j=0; j<flightArray.get(i).segments.size(); j++){
                if(flightArray.get(i).segments.get(j).size()<3){
                    stop=false;
                    break;
                }
            }
            if(stop){
                flightArrayTemp.add(flightArray.get(i));
            }
        }
    }

    private void timeFilter(int i) {
        if (DateAndTimeUtils.getHourFromDate(flightArray.get(i).segments.get(0).get(0).departureTime) >= t_min
                && (DateAndTimeUtils.getHourFromDate(flightArray.get(i).segments.get(0).get(0).departureTime) <= t_max)) {
            flightArrayTemp.add(flightArray.get(i));
        }
    }

    private void timeAndStopFilter(int i) {
        boolean timeFilter=DateAndTimeUtils.getHourFromDate(flightArray.get(i).segments.get(0).get(0).departureTime) >= t_min
                && (DateAndTimeUtils.getHourFromDate(flightArray.get(i).segments.get(0).get(0).departureTime) <= t_max);
        if(timeFilter){
            stopFilter(i);
        }
    }

    public static void setOnUpdateListener(OnUpdateListener listener) {
        mListener = listener;
    }

    public interface OnUpdateListener {
        void onUpdate(ArrayList<FlightSearchResponseModel.response.flights> flightArray, String s);
    }

}
