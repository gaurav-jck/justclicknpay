package com.justclick.clicknbook.utils;

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

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.BusSearchFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusCityResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusCityRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MybusCityDialog {
    private static Dialog dialog;
    private static Context context;
    private static EditText city_edt;
    private static ImageView back_arrow;
    private static ImageView cross;
    private static ListView list_agent;
    private static String agentName="";
    private static AutocompleteAdapter autocompleteAdapter;
    private static LoginModel loginModel;
    private static BusCityResponseModel busCityResponseModel;
    private static OnCityDialogResult onCityDialogResult;

    public interface OnCityDialogResult{
        void setResult(int value,Intent intent);
    }


    public static void showCustomDialog(final Context context, final Intent intent, final String key, BusSearchFragment busSearchFragment){
        try {
            onCityDialogResult= (OnCityDialogResult) busSearchFragment;
            dialog = new Dialog(context, R.style.Theme_Design_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_bus_from_city);
            final Window window= dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            MybusCityDialog.context=context;
            city_edt=((EditText) dialog.findViewById(R.id.city_edt));
            back_arrow=((ImageView) dialog.findViewById(R.id.back_arrow));
            cross=((ImageView) dialog.findViewById(R.id.cross));
            list_agent=((ListView) dialog.findViewById(R.id.list_agent));

            city_edt.setHint(intent.getStringExtra("msg"));
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
                            agentName = autocompleteAdapter.getItem(position);
                            city_edt.setVisibility(View.VISIBLE);
                            city_edt.setText(agentName);
                            city_edt.setSelection(city_edt.getText().length());

                            String message=city_edt.getText().toString();
                            Intent intent=new Intent();
                            intent.putExtra("MESSAGE",message);
                            onCityDialogResult.setResult(Integer.parseInt(key),intent);
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
                            String term = s.toString();
                            BusCityRequestModel model=new BusCityRequestModel();
                            model.DeviceId=Common.getDeviceId(context);
                            model.DoneCardUser=loginModel.Data.DoneCardUser;
                            model.Key = intent.getStringExtra("key");
                            model.LoginSessionId=EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            model.Name=term;
                            model.SourceID=term;
                            citySearch(model);
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

    public static BusCityResponseModel citySearch(BusCityRequestModel model) {

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<BusCityResponseModel> call = apiService.getBusCity(ApiConstants.Source, model);
        call.enqueue(new Callback<BusCityResponseModel>() {
            @Override
            public void onResponse(Call<BusCityResponseModel> call, Response<BusCityResponseModel> response) {
                try {
                    if(response.body()!=null && response.body().Data!=null) {
                        String[] array=new String[response.body().Data.length];
                        for(int i=0;i<array.length;i++){
                            array[i]=response.body().Data[i];
                        }
                        busCityResponseModel = response.body();
                        if (busCityResponseModel.Data != null && busCityResponseModel.Data.length > 0) {
                            autocompleteAdapter = new AutocompleteAdapter(context, busCityResponseModel);
                            list_agent.setAdapter(autocompleteAdapter);
                        }
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<BusCityResponseModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return busCityResponseModel;
    }

    public static class AutocompleteAdapter extends BaseAdapter {
        private BusCityResponseModel model;
        private String agent_URL = "";
        private Context context;

        public AutocompleteAdapter(Context context, BusCityResponseModel busCityResponseModel)
        {
            model = busCityResponseModel;
            this.context=context;
        }

        @Override
        public int getCount()
        {
            if(model.Data!=null)
                return model.Data.length;
            return 0;
        }
        @Override
        public String getItem(int position)
        {
            return model.Data[position];
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

            TextView countryName = (TextView) view.findViewById(R.id.countryName);
            TextView cityName = (TextView) view.findViewById(R.id.cityName);
            countryName.setText(model.Data[position]);
            cityName.setText(model.Data[position].substring(0,model.Data[position].indexOf("(")));
//            if(model.Data.get(position).AirPortName!=null&& agent.Data.get(position).AirPortName.length()>0){
//                cityCode.setText("( "+model.Data.get(position).CityCode +", "+ agent.Data.get(position).AirPortName+" )");
//            }else {
//                cityCode.setText("( "+model.Data.get(position).CityCode+" )");
//            }
            return view;
        }
    }
}
