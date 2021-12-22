package com.justclick.clicknbook.Fragment.flights.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.flights.FlightUtils;
import com.justclick.clicknbook.Fragment.flights.adapters.RecyclerFlightRoundDomesticAdapter;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DateAndTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;


public class FlightAvailabilityRoundDomestic extends Fragment implements View.OnClickListener {
    private final int DEPT_SORT=0, DURATION_SORT=1, PRICE_SORT=2, ARR_SORT=3;
    private final int OUTBOUND_LIST=0, INBOUND_LIST=1;
    private Context context;
    private View view;
    private ProgressDialog progressDialog;
    private String str_token, str_min, str_max;
    private double minPriceInbound, maxPriceInbound;
    private SharedPreferences sharedPreferences;
    private String origin, destination, departureDate, returnDate, cabinClass;
    private int adultCount, childCount, infantCount, totalTraveller;

    private FlightSearchResponseModel flightSearchResponseModel;
    private FlightSearchRequestModel flightSearchRequestModel;
    private ArrayList<FlightSearchResponseModel.response.flights> flightArrayOne, flightArrayListTemp,
            flightArrayTwo, flightArrayListTempTwo;
    private FlightSearchResponseModel.response.flights departureFlightModel, returnFlightModel;
    private RecyclerView recyclerViewOutBound, recyclerViewInBound;
    private RecyclerFlightRoundDomesticAdapter flightAdapter, flightAdapterInbound;
    private TextView tv_from , tv_dep_date, tv_total_flight;
    private boolean myClickDep = true, myClickArr = true, myClickDur = true, myClickPri = true, isListReload=false;
    private LinkedHashSet<String> hashFlightObject ,hashFlightID, hashFlightObjectInbound ,hashFlightIDInbound ;

    private ImageView deptArrow, durationArrow, priceArrow;
    private View deptView, durationView, priceView;
    private TextView tv_dep_sort, tv_dur_sort, tv_price_sort,
            outBoundCityCodeTv, outboundDateTv, inBoundCityCodeTv, inBoundDateTv,
            departurePriceTv, returnPriceTv, totalPriceTv, bookTv;
    private LinearLayout outBoundLin, inBoundLin;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();

        flightSearchResponseModel = (FlightSearchResponseModel) getArguments().getSerializable("SearchResponse");
        flightSearchRequestModel = (FlightSearchRequestModel) getArguments().getSerializable("SearchRequest");

        flightArrayOne=new ArrayList<>();
        flightArrayListTemp=new ArrayList<>();
        flightArrayTwo=new ArrayList<>();
        flightArrayListTempTwo=new ArrayList<>();
//hash map
        hashFlightObject = new LinkedHashSet<>();
        hashFlightID = new LinkedHashSet<>();
        hashFlightObjectInbound = new LinkedHashSet<>();
        hashFlightIDInbound = new LinkedHashSet<>();

        origin = flightSearchRequestModel.segments.get(0).origin;
        destination = flightSearchRequestModel.segments.get(0).destination;
        departureDate = flightSearchRequestModel.segments.get(0).departureDate;
        returnDate = flightSearchRequestModel.segments.get(0).returnDate;
        adultCount = flightSearchRequestModel.adultCount;
        childCount = flightSearchRequestModel.childCount;
        infantCount = flightSearchRequestModel.infantCount;
        totalTraveller = adultCount+childCount+infantCount;
        cabinClass = flightSearchRequestModel.cabinClass;

        sharedPreferences = context.getSharedPreferences(FlightUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        str_token = sharedPreferences.getString(FlightUtils.KEY_LOGIN_TOKEN, "");
        isListReload=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view==null){
            view =  inflater.inflate(R.layout.fragment_flight_results_round_domestic, container, false);

            initializeView();
            FilterPageDomesticRound.setOnUpdateListener(new FilterPageDomesticRound.OnUpdateListener() {
                @Override
                public void onUpdate(ArrayList<FlightSearchResponseModel.response.flights> flightArray, String s, int FILTER_TYPE) {
                    if(FILTER_TYPE==OUTBOUND_LIST){
                        if(s.equals("reset")){
                            flightArrayListTemp.clear();
                            flightArrayListTemp.addAll(flightArray);
                        } else {
                            flightArrayListTemp.clear();
                            flightArrayListTemp.addAll(flightArray);
                        }
//                tv_total_flight.setText(flightArrayListTemp.size()+ " Flight"); // set total flight count
                        flightAdapter.notifyDataSetChanged();
                    }else {
                        if(s.equals("reset")){
                            flightArrayListTempTwo.clear();
                            flightArrayListTempTwo.addAll(flightArray);
                        } else {
                            flightArrayListTempTwo.clear();
                            flightArrayListTempTwo.addAll(flightArray);
                        }
//                tv_total_flight.setText(flightArrayListTemp.size()+ " Flight"); // set total flight count
                        flightAdapterInbound.notifyDataSetChanged();
                    }

                }
            });
        }

        if(isListReload){
            setFlightAdapter();
            setFlightList();
            isListReload=false;
        }

        return view;
    }

    private void initializeView() {

        outBoundLin= view.findViewById(R.id.outBoundLin);
        inBoundLin= view.findViewById(R.id.inBoundLin);

        tv_from = view.findViewById(R.id.tv_from);
        tv_dep_date = view.findViewById(R.id.tv_dep_date);
        tv_total_flight = view.findViewById(R.id.tv_total_flight);

        recyclerViewOutBound = view.findViewById(R.id.recyclerViewOutBound);
        recyclerViewOutBound.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewOutBound.setHasFixedSize(true);
        recyclerViewInBound = view.findViewById(R.id.recyclerViewInBound);
        recyclerViewInBound.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewInBound.setHasFixedSize(true);

        deptArrow=view.findViewById(R.id.deptArrow);
        durationArrow=view.findViewById(R.id.durationArrow);
        priceArrow=view.findViewById(R.id.priceArrow);
        deptView=view.findViewById(R.id.deptView);
        durationView=view.findViewById(R.id.durationView);
        priceView=view.findViewById(R.id.priceView);

        tv_dep_sort = view.findViewById(R.id.tv_dep_sort);
        tv_dur_sort = view.findViewById(R.id.tv_dur_sort);
        tv_price_sort = view.findViewById(R.id.tv_price_sort);
        outBoundCityCodeTv = view.findViewById(R.id.outBoundCityCodeTv);
        inBoundCityCodeTv = view.findViewById(R.id.inBoundCityCodeTv);
        outboundDateTv = view.findViewById(R.id.outboundDateTv);
        inBoundDateTv = view.findViewById(R.id.inBoundDateTv);

        departurePriceTv = view.findViewById(R.id.departurePriceTv);
        returnPriceTv = view.findViewById(R.id.returnPriceTv);
        totalPriceTv = view.findViewById(R.id.totalPriceTv);
        bookTv = view.findViewById(R.id.bookTv);

        tv_from.setText(flightSearchRequestModel.fromCity+" - "+flightSearchRequestModel.toCity);
        outBoundCityCodeTv.setText(flightSearchRequestModel.segments.get(0).origin+" - "+flightSearchRequestModel.segments.get(0).destination);
        inBoundCityCodeTv.setText(flightSearchRequestModel.segments.get(0).destination+" - "+flightSearchRequestModel.segments.get(0).origin);
        tv_dep_date.setText(FlightUtils.getFormatDate(departureDate)+" - "+FlightUtils.getFormatDate(returnDate)+" | "+ totalTraveller + " Traveller");

        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.filterImg).setOnClickListener(this);
        view.findViewById(R.id.editSearchImg).setOnClickListener(this);
        view.findViewById(R.id.priceLin).setOnClickListener(this);
        view.findViewById(R.id.durationLin).setOnClickListener(this);
        view.findViewById(R.id.deptLin).setOnClickListener(this);
        bookTv.setOnClickListener(this);

        outBoundLin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                final int historySize = ev.getHistorySize();
                final int pointerCount = ev.getPointerCount();
                for (int h = 0; h < historySize; h++) {
                    System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
                    for (int p = 0; p < pointerCount; p++) {
                        System.out.printf("  pointer %d: (%f,%f)",
                                ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
                    }
                }
                System.out.printf("At time %d:", ev.getEventTime());
                for (int p = 0; p < pointerCount; p++) {
                    System.out.printf("  pointer %d: (%f,%f)",
                            ev.getPointerId(p), ev.getX(p), ev.getY(p));
                }
                return true;
            }
        });
    }


    //flight arraylist method
    private void setFlightAdapter() {
        flightAdapter = new RecyclerFlightRoundDomesticAdapter(context,flightArrayListTemp,cabinClass, new RecyclerFlightRoundDomesticAdapter.FlightAdapterListener() {
            @Override
            public void onClickAtAdapter(int position) {
                departureFlightModel=flightArrayListTemp.get(position);
                departurePriceTv.setText(CurrencyCode.getCurrencySymbol(flightArrayListTemp.get(position).fare.currency, context)+
                        flightArrayListTemp.get(position).fare.publishedFare);
                setTotalPrice();
            }
        });
        flightAdapterInbound = new RecyclerFlightRoundDomesticAdapter(context,flightArrayListTempTwo,cabinClass, new RecyclerFlightRoundDomesticAdapter.FlightAdapterListener() {
            @Override
            public void onClickAtAdapter(int position) {
                returnFlightModel=flightArrayListTempTwo.get(position);
                returnPriceTv.setText(CurrencyCode.getCurrencySymbol(flightArrayListTempTwo.get(position).fare.currency, context)+
                        flightArrayListTempTwo.get(position).fare.publishedFare);
                setTotalPrice();
            }
        });
//        recyclerViewOutBound
        recyclerViewOutBound.setAdapter(flightAdapter);
        recyclerViewInBound.setAdapter(flightAdapterInbound);
    }

    private void setTotalPrice() {
        try {
            if(departureFlightModel!=null && returnFlightModel!=null){
                totalPriceTv.setText(CurrencyCode.getCurrencySymbol(departureFlightModel.fare.currency, context)+
                        (departureFlightModel.fare.publishedFare+returnFlightModel.fare.publishedFare));
            }else if(departureFlightModel!=null){
                totalPriceTv.setText(CurrencyCode.getCurrencySymbol(departureFlightModel.fare.currency, context)+
                        (departureFlightModel.fare.publishedFare));
            }else {
                totalPriceTv.setText(CurrencyCode.getCurrencySymbol(returnFlightModel.fare.currency, context)+
                        (returnFlightModel.fare.publishedFare));
            }
        }catch (NullPointerException e){
            totalPriceTv.setText("");
        }
    }

    private void setFlightList(){
        if(flightArrayOne.size()==0) {
            flightArrayOne.addAll(flightSearchResponseModel.response.flights);
            flightArrayListTemp.addAll(flightArrayOne);
        }
        if(flightArrayTwo.size()==0) {
            flightArrayTwo.addAll(flightSearchResponseModel.response.flightsInbound);
            flightArrayListTempTwo.addAll(flightArrayTwo);
        }
        flightAdapter.notifyDataSetChanged();
        flightAdapterInbound.notifyDataSetChanged();
        outboundDateTv.setText(FlightUtils.getFormatDate(departureDate)+" | "+flightArrayListTemp.size()+ " Flights"); // set total flight count
        inBoundDateTv.setText(FlightUtils.getFormatDate(returnDate)+" | "+flightArrayListTempTwo.size()+ " Flights"); // set total flight count
    }

    // filter method
    public void methodFilter(){
        methodMinMaxAirline();
        methodMinMaxAirlineInbound();
        FilterPageDomesticRound fragment2 = new FilterPageDomesticRound();
        Bundle bundle = new Bundle();
        bundle.putString("type", "oneway");
        bundle.putString("str_min", str_min);
        bundle.putString("str_max", str_max);
        bundle.putDouble("minPriceInbound", minPriceInbound);
        bundle.putDouble("maxPriceInbound", maxPriceInbound);
        bundle.putSerializable("ArrayList", flightSearchResponseModel.response.flights);
        bundle.putSerializable("ArrayListInbound", flightSearchResponseModel.response.flightsInbound);
        bundle.putSerializable("airlineList", hashFlightObject);
        bundle.putSerializable("airlineListID", hashFlightID);
        bundle.putSerializable("hashFlightObjectInbound", hashFlightObjectInbound);
        bundle.putSerializable("hashFlightIDInbound", hashFlightIDInbound);
        fragment2.setArguments(bundle);
        ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment2);
    }

    private void methodMinMaxAirline() {
        if(flightArrayOne.size()>0) {
            double min = flightArrayOne.get(0).fare.publishedFare;
            double max = flightArrayOne.get(0).fare.publishedFare;
            for (int i = 0; i < flightArrayOne.size(); i++) {
                FlightSearchResponseModel.response.flights model = flightArrayOne.get(i);
                double d_price = model.fare.publishedFare;
                if (d_price < min) {
                    min = d_price;
                }
                if (d_price > max) {
                    max = d_price;
                }
                // flight filter array
                FlightSearchResponseModel.response.flights.segments segmentModel = model.segments.get(0).get(0);
                hashFlightObject.add(segmentModel.airline.name);
                hashFlightID.add(segmentModel.airline.code);
            }
            str_min = String.valueOf(min);
            str_max = String.valueOf(max);
        }
    }

    private void methodMinMaxAirlineInbound() {
        if(flightArrayTwo.size()>0) {
            double min = flightArrayTwo.get(0).fare.publishedFare;
            double max = flightArrayTwo.get(0).fare.publishedFare;
            for (int i = 0; i < flightArrayTwo.size(); i++) {
                double d_price = flightArrayTwo.get(i).fare.publishedFare;
                if (d_price < min) {
                    min = d_price;
                }
                if (d_price > max) {
                    max = d_price;
                }
                // flight filter array
                FlightSearchResponseModel.response.flights.segments segmentModel = flightArrayTwo.get(i).segments.get(0).get(0);
                hashFlightObjectInbound.add(segmentModel.airline.name);
                hashFlightIDInbound.add(segmentModel.airline.code);
            }
            minPriceInbound = min;
            maxPriceInbound =max;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.filterImg:
                methodFilter();
                break;
            case R.id.editSearchImg:
                methodEditSearch();
                break;
            case R.id.priceLin:
                priceSorting();
                break;
            case R.id.deptLin:
                departureSorting();
                break;
            case R.id.durationLin:
                durationSorting();
                break;
            case R.id.bookTv:
                if(departureFlightModel==null && returnFlightModel==null){
                    Toast.makeText(context, "Please select departure and return flights.", Toast.LENGTH_SHORT).show();
                }else if(departureFlightModel==null){
                    Toast.makeText(context, "Please select departure flight.", Toast.LENGTH_SHORT).show();
                }else if(returnFlightModel==null){
                    Toast.makeText(context, "Please select return flight.", Toast.LENGTH_SHORT).show();
                }else {
                    FlightDetails fragment2 = new FlightDetails();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FlightModel", departureFlightModel);
                    bundle.putSerializable("FlightModelReturn", returnFlightModel);
                    bundle.putSerializable("SessionId", flightSearchResponseModel.sessionId);
                    fragment2.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment2);
                }
                break;

        }
    }

    private void durationSorting() {
        selectSorting(DURATION_SORT);
        if(myClickDur){
            myClickDur = false;
            sortByDuration();
        } else {
            myClickDur = true;
            sortByDurationDesc();
        }
        flightAdapter.notifyDataSetChanged();
        flightAdapterInbound.notifyDataSetChanged();
        myClickDep = true; myClickArr = true; myClickPri = true;
    }

    private void departureSorting() {
        selectSorting(DEPT_SORT);
        if(myClickDep){
            myClickDep = false;
            sortByDeparture();
        } else {
            myClickDep = true;
            sortByDepartureDesc();
        }
        flightAdapter.notifyDataSetChanged();
        flightAdapterInbound.notifyDataSetChanged();
        myClickArr = true; myClickDur = true; myClickPri = true;
    }

    private void priceSorting() {
        selectSorting(PRICE_SORT);
        if(myClickPri){
            myClickPri = false;
            sortByPrice();
        } else {
            myClickPri = true;
            sortByPriceDesc();
        }
        flightAdapter.notifyDataSetChanged();
        flightAdapterInbound.notifyDataSetChanged();
        myClickDep = true; myClickArr = true; myClickDur = true;
    }

    private void methodEditSearch() {

    }

    public void selectSorting(int string){
        if(string==DEPT_SORT){
            tv_dep_sort.setTextColor(getResources().getColor(R.color.app_blue_color));
            tv_dur_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            tv_price_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            deptArrow.setVisibility(View.VISIBLE);
            durationArrow.setVisibility(View.INVISIBLE);
            priceArrow.setVisibility(View.INVISIBLE);
            deptView.setVisibility(View.VISIBLE);
            durationView.setVisibility(View.INVISIBLE);
            priceView.setVisibility(View.INVISIBLE);
        }else if(string==DURATION_SORT){
            tv_dep_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            tv_dur_sort.setTextColor(getResources().getColor(R.color.app_blue_color));
            tv_price_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            deptArrow.setVisibility(View.INVISIBLE);
            durationArrow.setVisibility(View.VISIBLE);
            priceArrow.setVisibility(View.INVISIBLE);
            deptView.setVisibility(View.INVISIBLE);
            durationView.setVisibility(View.VISIBLE);
            priceView.setVisibility(View.INVISIBLE);
        } else if(string==PRICE_SORT){
            tv_dep_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            tv_dur_sort.setTextColor(getResources().getColor(R.color.grayColorFlight));
            tv_price_sort.setTextColor(getResources().getColor(R.color.app_blue_color));
            deptArrow.setVisibility(View.INVISIBLE);
            durationArrow.setVisibility(View.INVISIBLE);
            priceArrow.setVisibility(View.VISIBLE);
            deptView.setVisibility(View.INVISIBLE);
            durationView.setVisibility(View.INVISIBLE);
            priceView.setVisibility(View.VISIBLE);
        }
    }

    public void sortByPrice(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                return (int) (lhs.fare.publishedFare-rhs.fare.publishedFare);
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                return (int) (lhs.fare.publishedFare-rhs.fare.publishedFare);
            }
        });
    }

    public void sortByPriceDesc(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                return (int) (rhs.fare.publishedFare-lhs.fare.publishedFare);
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                return (int) (rhs.fare.publishedFare-lhs.fare.publishedFare);
            }
        });
    }

    public void sortByDeparture(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                double dTimeLhs = Double.parseDouble(lhs.segments.get(0).get(0).departureTime.substring(11,13));
                double dTimeRhs = Double.parseDouble(rhs.segments.get(0).get(0).departureTime.substring(11,13));
                return (int)(dTimeLhs-dTimeRhs);
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                double dTimeLhs = Double.parseDouble(lhs.segments.get(0).get(0).departureTime.substring(11,13));
                double dTimeRhs = Double.parseDouble(rhs.segments.get(0).get(0).departureTime.substring(11,13));
                return (int)(dTimeLhs-dTimeRhs);
            }
        });
    }

    public void sortByDepartureDesc(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                double dTimeLhs = Double.parseDouble(lhs.segments.get(0).get(0).departureTime.substring(11,13));
                double dTimeRhs = Double.parseDouble(rhs.segments.get(0).get(0).departureTime.substring(11,13));
                return (int)(dTimeRhs-dTimeLhs);
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                double dTimeLhs = Double.parseDouble(lhs.segments.get(0).get(0).departureTime.substring(11,13));
                double dTimeRhs = Double.parseDouble(rhs.segments.get(0).get(0).departureTime.substring(11,13));
                return (int)(dTimeRhs-dTimeLhs);
            }
        });
    }

    public void sortByDuration(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                int totalDurationLhs= DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(lhs.segments.get(0).get(0).arrivalTime,
                        lhs.segments.get(0).get(0).departureTime);
                int totalDurationRhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(rhs.segments.get(0).get(0).arrivalTime,
                        rhs.segments.get(0).get(0).departureTime);
                return totalDurationLhs-totalDurationRhs;
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                int totalDurationLhs= DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(lhs.segments.get(0).get(0).arrivalTime,
                        lhs.segments.get(0).get(0).departureTime);
                int totalDurationRhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(rhs.segments.get(0).get(0).arrivalTime,
                        rhs.segments.get(0).get(0).departureTime);
                return totalDurationLhs-totalDurationRhs;
            }
        });
    }

    public void sortByDurationDesc() {
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                int totalDurationLhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(lhs.segments.get(0).get(0).arrivalTime,
                        lhs.segments.get(0).get(0).departureTime);
                int totalDurationRhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(rhs.segments.get(0).get(0).arrivalTime,
                        rhs.segments.get(0).get(0).departureTime);
                return totalDurationRhs-totalDurationLhs;
            }
        });
        Collections.sort(flightArrayListTempTwo, new Comparator<FlightSearchResponseModel.response.flights>() {
            @Override
            public int compare(FlightSearchResponseModel.response.flights lhs, FlightSearchResponseModel.response.flights rhs) {
                int totalDurationLhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(lhs.segments.get(0).get(0).arrivalTime,
                        lhs.segments.get(0).get(0).departureTime);
                int totalDurationRhs=DateAndTimeUtils.getDurationInMinutesBetweenTwoDates(rhs.segments.get(0).get(0).arrivalTime,
                        rhs.segments.get(0).get(0).departureTime);
                return totalDurationRhs-totalDurationLhs;
            }
        });
    }

}
