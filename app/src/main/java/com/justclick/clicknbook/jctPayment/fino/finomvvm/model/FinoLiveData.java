package com.justclick.clicknbook.jctPayment.fino.finomvvm.model;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinoLiveData {

    /*public LiveData<List<Project>> getProjectList(String userId) {
        final MutableLiveData<List<Project>> data = new MutableLiveData<>();

        gitHubService.getProjectList(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                data.setValue(response.body());
            }

            // Error handling will be explained in the next article …
        });

        return data;
    }*/

    private final int SUCCESS_CODE_200=200;
    ResponseBody responseBody;
    Context context;
    NetworkCall.RetrofitResponseListener retrofitResponseListener;
    public void callMobileService(Object model, String methodName, final Context context, NetworkCall.RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
//        if(isDialog){
//            showCustomDialog();}
        ApiInterface service = APIClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.getMobileCommonData(methodName,model);
//        Call<ResponseBody> call = service.testUatService(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }
}
