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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.flights.FlightUtils;
import com.justclick.clicknbook.Fragment.flights.adapters.RecyclerFlightOneWayAdapter;
import com.justclick.clicknbook.Fragment.flights.requestModels.FlightSearchRequestModel;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightSearchResponseModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.utils.DateAndTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;


public class FlightAvailabilityOneWay extends Fragment implements View.OnClickListener {
    private final String journeyTypeOneWay="ONE_WAY", journeyTypeTwoWay="RETURN", journeyTypeMultiWay="MULTI_CITY";
    private final int DEPT_SORT=0, DURATION_SORT=1, PRICE_SORT=2, ARR_SORT=3;
    private Context context;
    private View view;
    private ProgressDialog progressDialog;
    private String str_token, str_min, str_max, journeyType;
    private SharedPreferences sharedPreferences;
    private String origin, destination, departureDate, arrivalDate, cabinClass;
    private int adultCount, childCount, infantCount, totalTraveller;

    private FlightSearchResponseModel flightSearchResponseModel;
    private FlightSearchRequestModel flightSearchRequestModel;
    private ArrayList<FlightSearchResponseModel.response.flights> flightArrayOne, flightArrayListTemp;
    private RecyclerView recycler_flight;
    private RecyclerFlightOneWayAdapter flightAdapter;
    boolean myClickDep = true, myClickArr = true, myClickDur = true, myClickPri = true;
    private LinkedHashSet<String> hashFlightObject = new LinkedHashSet<String>();
    private LinkedHashSet<String> hashFlightID = new LinkedHashSet<String>();

    private TextView tv_from , tv_dep_date, tv_total_flight;
    private ImageView deptArrow, durationArrow, priceArrow;
    private View deptView, durationView, priceView;
    private TextView tv_dep_sort, tv_dur_sort, tv_price_sort, selectFlightDestTv, totalFlightsTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();

        flightSearchResponseModel = (FlightSearchResponseModel) getArguments().getSerializable("SearchResponse");
        flightSearchRequestModel = (FlightSearchRequestModel) getArguments().getSerializable("SearchRequest");
//        journeyType =  getArguments().getString("journeyType");

        flightArrayOne=new ArrayList<>();
        flightArrayListTemp=new ArrayList<>();

        origin = flightSearchRequestModel.segments.get(0).origin;
        destination = flightSearchRequestModel.segments.get(0).destination;
        departureDate = flightSearchRequestModel.segments.get(0).departureDate;
      /*  if(journeyType.equalsIgnoreCase(journeyTypeTwoWay)){
            arrivalDate = flightSearchRequestModel.segments.get(0).returnDate;
        }*/
        adultCount = flightSearchRequestModel.adultCount;
        childCount = flightSearchRequestModel.childCount;
        infantCount = flightSearchRequestModel.infantCount;
        totalTraveller = adultCount+childCount+infantCount;
        cabinClass = flightSearchRequestModel.cabinClass;

        sharedPreferences = context.getSharedPreferences(FlightUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        str_token = sharedPreferences.getString(FlightUtils.KEY_LOGIN_TOKEN, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view =  inflater.inflate(R.layout.fragment_flight_return_result, container, false);

            initializeView();
        }

        setFlightAdapter();
        setFlightList();

        FilterPage.setOnUpdateListener(new FilterPage.OnUpdateListener() {
            @Override
            public void onUpdate(ArrayList<FlightSearchResponseModel.response.flights> flightArray, String s) {
                if(s.equals("reset")){
//                    Toast.makeText(context, "reset", Toast.LENGTH_SHORT).show();
                    flightArrayListTemp.clear();
                    flightArrayListTemp.addAll(flightArray);
                } else {
//                    Toast.makeText(context, "apply", Toast.LENGTH_SHORT).show();
                    flightArrayListTemp.clear();
                    flightArrayListTemp.addAll(flightArray);
                }
//                tv_total_flight.setText(flightArrayListTemp.size()+ " Flight"); // set total flight count
                flightAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void initializeView() {
        tv_from = view.findViewById(R.id.tv_from);
        tv_dep_date = view.findViewById(R.id.tv_dep_date);
        tv_total_flight = view.findViewById(R.id.tv_total_flight);

        recycler_flight = view.findViewById(R.id.recycler_flight);
        recycler_flight.setLayoutManager(new LinearLayoutManager(context));
        recycler_flight.setHasFixedSize(true);

        deptArrow=view.findViewById(R.id.deptArrow);
        durationArrow=view.findViewById(R.id.durationArrow);
        priceArrow=view.findViewById(R.id.priceArrow);
        deptView=view.findViewById(R.id.deptView);
        durationView=view.findViewById(R.id.durationView);
        priceView=view.findViewById(R.id.priceView);

        tv_dep_sort = view.findViewById(R.id.tv_dep_sort);
        tv_dur_sort = view.findViewById(R.id.tv_dur_sort);
        tv_price_sort = view.findViewById(R.id.tv_price_sort);
        selectFlightDestTv = view.findViewById(R.id.selectFlightDestTv);
        totalFlightsTv = view.findViewById(R.id.totalFlightsTv);

        tv_from.setText(flightSearchRequestModel.fromCity+" - "+flightSearchRequestModel.toCity);
        tv_dep_date.setText(FlightUtils.getFormatDate(departureDate)+" | "+ totalTraveller + " Traveller"+" | ");
        selectFlightDestTv.setText("Select flight to "+flightSearchRequestModel.toCity);

        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.filterImg).setOnClickListener(this);
        view.findViewById(R.id.editSearchImg).setOnClickListener(this);
        view.findViewById(R.id.priceLin).setOnClickListener(this);
        view.findViewById(R.id.durationLin).setOnClickListener(this);
        view.findViewById(R.id.deptLin).setOnClickListener(this);
    }


    //flight arraylist method
    private void setFlightAdapter() {
        flightAdapter = new RecyclerFlightOneWayAdapter(context,flightArrayListTemp,cabinClass, new RecyclerFlightOneWayAdapter.FlightAdapterListener() {
            @Override
            public void onClickAtAdapter(int position) {
                FlightDetails fragment2 = new FlightDetails();
                Bundle bundle = new Bundle();
                bundle.putSerializable("FlightModel", flightArrayListTemp.get(position));
                bundle.putSerializable("SessionId", flightSearchResponseModel.sessionId);
                fragment2.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment2);
            }
        });
        recycler_flight.setAdapter(flightAdapter);
    }

    private void setFlightList(){
        if(flightArrayOne.size()==0) {
            flightArrayOne.addAll(flightSearchResponseModel.response.flights);
            flightArrayListTemp.addAll(flightArrayOne);
        }
        flightAdapter.notifyDataSetChanged();
        tv_total_flight.setText(flightArrayListTemp.size()+ " Flight"); // set total flight count
        totalFlightsTv.setText("(We have found "+flightArrayListTemp.size()+ " Flights)"); // set total flight count
    }

    // filter method
    public void methodFilter(){
        methodMinMaxAirline();
        FilterPage fragment2 = new FilterPage();
        Bundle bundle = new Bundle();
        bundle.putString("type", "oneway");
        bundle.putString("str_min", str_min);
        bundle.putString("str_max", str_max);
        bundle.putSerializable("ArrayList", flightSearchResponseModel.response.flights);
        bundle.putSerializable("airlineList", hashFlightObject);
        bundle.putSerializable("airlineListID", hashFlightID);
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
                hashFlightObject.add(segmentModel.airline.name!=null?segmentModel.airline.name:segmentModel.airline.code);
                hashFlightID.add(segmentModel.airline.code);
            }
            str_min = String.valueOf(min);
            str_max = String.valueOf(max);
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
    }

    public void sortByPriceDesc(){
        Collections.sort(flightArrayListTemp, new Comparator<FlightSearchResponseModel.response.flights>() {
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
    }

}
