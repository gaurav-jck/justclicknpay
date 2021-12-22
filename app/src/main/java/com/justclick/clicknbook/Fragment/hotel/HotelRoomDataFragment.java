package com.justclick.clicknbook.Fragment.hotel;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.HotelRoomDataAdapter;
import com.justclick.clicknbook.model.HotelCityListModel;
import com.justclick.clicknbook.model.HotelRoomDataResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.HotelCityRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class HotelRoomDataFragment extends Fragment implements View.OnClickListener{

    private ToolBarTitleChangeListener titleChangeListener;
    Context context;
    private LoginModel loginModel;
    private HotelRoomDataResponseModel roomDataResponseModel;
    private RecyclerView roomRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        roomDataResponseModel =new HotelRoomDataResponseModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_hotel_room_data, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.hotelRoomInfoFragmentTitle));

        if(getArguments()!=null && getArguments().getSerializable("HotelInfo")!=null){
            roomDataResponseModel = (HotelRoomDataResponseModel) getArguments().getSerializable("HotelInfo");
        }

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        roomRecyclerView = (RecyclerView) view.findViewById(R.id.roomRecyclerView);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        roomRecyclerView.setAdapter(new HotelRoomDataAdapter(context, new HotelRoomDataAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<HotelRoomDataResponseModel.Rooms> list, int position) {
                switch (view.getId()) {
                    case R.id.cancellationPolicy_tv:
                        openCancellationDialog(list.get(position).CancellationPolicy);
                        break;
                }
            }
        }, roomDataResponseModel.Rooms));

    }

    public void hotelCity(HotelCityRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<HotelCityListModel> call = apiService.getHotelCityPost(ApiConstants.CityCountry, model);
        call.enqueue(new Callback<HotelCityListModel>() {
            @Override
            public void onResponse(Call<HotelCityListModel> call,Response<HotelCityListModel> response) {
                try {

                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(Call<HotelCityListModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCancellationDialog(String cancellationPolicy) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setMessage(cancellationPolicy);

        alertDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }


    }

}

