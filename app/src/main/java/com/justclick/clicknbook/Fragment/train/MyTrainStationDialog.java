package com.justclick.clicknbook.Fragment.train;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.BusSearchFragment;
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusCityResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusCityRequestModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTrainStationDialog {
    private static Dialog dialog;
    private static Context context;
    private static EditText city_edt;
    private static ImageView back_arrow;
    private static ImageView cross;
    private static ListView list_agent;
    private static String agentName="";
    private static AutocompleteAdapter autocompleteAdapter;
    private static LoginModel loginModel;
    private static TrainStationModel stationResponseModel;
    private static OnCityDialogResult onCityDialogResult;

    public interface OnCityDialogResult{
        void setResult(int value,TrainStationModel.Items intent);
    }


    public static void showCustomDialog(final Context context, final String msg, final int key, TrainSearchFragment busSearchFragment){
        try {
            onCityDialogResult= (OnCityDialogResult) busSearchFragment;
            dialog = new Dialog(context, R.style.Theme_Design_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_bus_from_city);
            final Window window= dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            MyTrainStationDialog.context=context;
            TextView searchTitleTv= dialog.findViewById(R.id.searchTitleTv);
            searchTitleTv.setText("Search Station");
            city_edt=( dialog.findViewById(R.id.city_edt));
            back_arrow=( dialog.findViewById(R.id.back_arrow));
            cross=( dialog.findViewById(R.id.cross));
            list_agent=( dialog.findViewById(R.id.list_agent));

            city_edt.setHint(msg);
            loginModel=new LoginModel();
            loginModel= MyPreferences.getLoginData(loginModel,context);
            back_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.hideSoftInputFromDialog(dialog,context);
                    dialog.dismiss();
                }
            });
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    city_edt.setText("");
                }
            });


            list_agent.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                            agentName = autocompleteAdapter.getItem(position).getStation_name();
                            city_edt.setVisibility(View.VISIBLE);
                            city_edt.setText(agentName);
                            city_edt.setSelection(city_edt.getText().length());

                            String message=city_edt.getText().toString();
                            onCityDialogResult.setResult(key,autocompleteAdapter.getItem(position));
                            Common.hideSoftInputFromDialog(dialog,context);
                            dialog.dismiss();
                        }
                    });

            city_edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    city_edt.setText("");
                }
            });

            city_edt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(Common.checkInternetConnection(context)) {
                        if(s.length()>=2) {
                            citySearch(s.toString());
                        }
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            dialog.setCancelable(true);
            dialog.show();
        }catch (Exception e){
        }
    }
    public static void hideCustomDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public static Boolean isDialogShowing(){
        if(dialog!=null && dialog.isShowing())
            return true;
        return false;
    }

    public static void citySearch(String city) {

        new NetworkCall().callTrainStationServiceGet(ApiConstants.StationSearch, city, context, false, new NetworkCall.RetrofitResponseListener() {
            @Override
            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                if (response != null) {
                    try {
                        TrainStationModel trainSearchDataModel = new Gson().fromJson(response.string(), TrainStationModel.class);
                        if (trainSearchDataModel!=null && trainSearchDataModel.getItems() != null && trainSearchDataModel.getItems().size()> 0) {
                            String[] array= new String[trainSearchDataModel.getItems().size()];
                            for(int i=0; i<trainSearchDataModel.getItems().size(); i++){
                                array[i]=trainSearchDataModel.getItems().get(i).getStation_name();
                            }
//                    val array = Array<String>(6)
//                    for (i in array.indices) {
//                        array[i] = trainSearchDataModel!!.items!!.get(i).station_name.toString()
//                    }
                            autocompleteAdapter = new AutocompleteAdapter(context, trainSearchDataModel);
                            list_agent.setAdapter(autocompleteAdapter);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class AutocompleteAdapter extends BaseAdapter {
        private TrainStationModel model;
        private String agent_URL = "";
        private Context context;

        public AutocompleteAdapter(Context context, TrainStationModel busCityResponseModel)
        {
            model = busCityResponseModel;
            this.context=context;
        }

        @Override
        public int getCount()
        {
            if(model!=null)
                return model.getItems().size();
            return 0;
        }
        @Override
        public TrainStationModel.Items getItem(int position)
        {
            return model.getItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.bus_autocomplete_layout, parent, false);

            TextView countryName =  view.findViewById(R.id.countryName);
            TextView cityName =  view.findViewById(R.id.cityName);
            ImageView image = view.findViewById(R.id.image);
            image.setVisibility(View.GONE);
            countryName.setText(getItem(position).getStation_name());
            cityName.setText(getItem(position).getStation_code()+" - "+getItem(position).getStation_name());
//            if(model.Data.get(position).AirPortName!=null&& agent.Data.get(position).AirPortName.length()>0){
//                cityCode.setText("( "+model.Data.get(position).CityCode +", "+ agent.Data.get(position).AirPortName+" )");
//            }else {
//                cityCode.setText("( "+model.Data.get(position).CityCode+" )");
//            }
            return view;
        }
    }
}
