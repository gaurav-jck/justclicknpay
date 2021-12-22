package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.CheckTrainPnrResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.CheckPnrRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class TrainSearchFragment extends Fragment implements View.OnClickListener {
    private final int PNR=1;
    private final int REFERENCE=2;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView get_pnr_response_tv, get_reference_response_tv, response_tv;
    private EditText pnr_edt, reference_edt;
    private LoginModel loginModel;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getActivity();
        loginModel = MyPreferences.getLoginData(new LoginModel(),context);

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_train_search, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.trainBookCheckFragmentTitle));
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        get_pnr_response_tv=  view.findViewById(R.id.get_pnr_response_tv);
        get_reference_response_tv=  view.findViewById(R.id.get_reference_response_tv);
        response_tv=  view.findViewById(R.id.response_tv);

        pnr_edt=  view.findViewById(R.id.pnr_edt);
        reference_edt=  view.findViewById(R.id.reference_edt);

        get_pnr_response_tv.setOnClickListener(this);
        get_reference_response_tv.setOnClickListener(this);
    }

    private boolean validate(int type) {
        try {
            if(pnr_edt.getText().toString().trim().length()==0 && type==PNR){
                Toast.makeText(context, R.string.empty_and_invalid_pnr, Toast.LENGTH_LONG).show();
                return false;
            }else if(reference_edt.getText().toString().trim().length()==0 && type==REFERENCE) {
                Toast.makeText(context, R.string.empty_and_invalid_referenceId, Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.get_pnr_response_tv:
                if(Common.checkInternetConnection(context)) {
                    if(validate(PNR)){
                        CheckPnrRequestModel model=new CheckPnrRequestModel();
                        model.DeviceId=Common.getDeviceId(context);
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.PnrNumber=pnr_edt.getText().toString().trim();
                        model.ReferenceID="";
                        requestAmount(model);
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.get_reference_response_tv:
            if(Common.checkInternetConnection(context)) {
                if(validate(REFERENCE)){
                    CheckPnrRequestModel model=new CheckPnrRequestModel();
                    model.DeviceId=Common.getDeviceId(context);
                    model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                    model.DoneCardUser=loginModel.Data.DoneCardUser;
                    model.PnrNumber="";
                    model.ReferenceID=reference_edt.getText().toString().trim();
                    requestAmount(model);
                }
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }

            break;
        }
    }
    private void requestAmount(CheckPnrRequestModel model) {
        showCustomDialog();
        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);
        Call<CheckTrainPnrResponseModel> call = apiService.checkTrainPnrPost(ApiConstants.CheckPnr, model);
        call.enqueue(new Callback<CheckTrainPnrResponseModel>() {
            @Override
            public void onResponse(Call<CheckTrainPnrResponseModel>call, Response<CheckTrainPnrResponseModel> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                try{
                    hideCustomDialog();
                    if(response!=null){
                        if(response.body().StatusCode.equals("0")) {
                            response_tv.setVisibility(View.VISIBLE);
                            response_tv.setText(response.body().Data.ServiceResponse);
                        }else {
                            response_tv.setText(response.body().Status);
                            Toast.makeText(context, response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        response_tv.setText("");
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<CheckTrainPnrResponseModel>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

}