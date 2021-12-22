package com.justclick.clicknbook.Fragment.flights.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.adapters.RecyclerPlaceAdapter;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightCityModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesSearch extends Fragment {
    private Context context;
    View view;
    RecyclerView recycler_place;
    RecyclerPlaceAdapter placeAdapter;
    ArrayList<FlightCityModel.response> placesList;
    EditText et_search;
    ProgressDialog progressDialog;
    private static final String SHARED_PREF_NAME = "flightsharedpref";
    private static final String KEY_LOGIN_TOKEN = "token";
    SharedPreferences sharedPreferences;
    String str_token;
    private int searchType;

    // create an interface
    OnFlightCitySearchListener someEventListener;
    public interface OnFlightCitySearchListener {
        void onCitySearch(FlightCityModel.response data, int myStr);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        someEventListener = (OnFlightCitySearchListener) context;
        this.context=context;
        placesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places_search, container, false);

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        str_token = sharedPreferences.getString(KEY_LOGIN_TOKEN, "");

        searchType = getArguments().getInt("searchType");

        et_search =  view.findViewById(R.id.et_search);
        recycler_place = view.findViewById(R.id.recycler_place);
        recycler_place.setLayoutManager(new LinearLayoutManager(context));
        recycler_place.setHasFixedSize(true);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>2) {
                    checkPlaceAPI(charSequence);
                }else {
                    showPopularCities();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // adapter on click
        placeAdapter = new RecyclerPlaceAdapter(context, placesList, new RecyclerPlaceAdapter.ListAdapterListener() {
            @Override
            public void onClickAtOKButton(int position) {
                FlightCityModel.response data = placesList.get(position);
                getFragmentManager().popBackStack();
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                someEventListener.onCitySearch(data, searchType);
            }
        });
        recycler_place.setAdapter(placeAdapter);

        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });

        showPopularCities();

        return view;
    }

    private void showPopularCities() {
        String response=Common.loadJSONFromAsset(context, "jsons/popular_city_flight.json");
        FlightCityModel flightCityModel = new Gson().fromJson(response, FlightCityModel.class);
        placesList.clear();
        placesList.addAll(flightCityModel.response);
        placeAdapter.notifyDataSetChanged();
    }

    private void checkPlaceAPI(CharSequence charSequence) {

        ApiInterface apiService = APIClient.getFlightClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.flightGetCityService(ApiConstants.AIRPORT_SEARCH+charSequence);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.code()==200) {
                        FlightCityModel flightCityModel = new Gson().fromJson(response.body().string(), FlightCityModel.class);
                        placesList.clear();
                        placesList.addAll(flightCityModel.response);
                        placeAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
