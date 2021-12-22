package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.MoneyTransferActivity;
import com.justclick.clicknbook.Activity.MoneyTransferNextActivity;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.rblDmt.MoneyTransferRegistrationFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.model.StateNameResponseModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.RblGetSenderRequest;
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
 * Created by Lenovo on 09/02/2017.
 */

public class ProductBalanceCheckFragment extends Fragment implements View.OnClickListener{
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    TextView mobile_balance_tv,mobile_balance_btn,
            rail_balance_tv,rail_balance_btn,air_balance_tv,air_balance_btn;
    Spinner airline_code_spinner,agent_id_spinner;
    private LoginModel loginModel;
    private String  airlineCode="",agentId="";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.product_balance_check_registration, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.nav_product_balance_check));
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        mobile_balance_tv =  view.findViewById(R.id.mobile_balance_tv);
        mobile_balance_btn =  view.findViewById(R.id.mobile_balance_btn);
        rail_balance_tv =  view.findViewById(R.id.rail_balance_tv);
        rail_balance_btn =  view.findViewById(R.id.rail_balance_btn);
        air_balance_tv =  view.findViewById(R.id.air_balance_tv);
        air_balance_btn =  view.findViewById(R.id.air_balance_btn);
        airline_code_spinner =  view.findViewById(R.id.airline_code_spinner);
        agent_id_spinner =  view.findViewById(R.id.agent_id_spinner);

        mobile_balance_btn.setOnClickListener(this);
        rail_balance_btn.setOnClickListener(this);
        air_balance_btn.setOnClickListener(this);

        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

        Typeface face = Common.TextViewTypeFace(context);
        mobile_balance_btn.setTypeface(face);
        rail_balance_btn.setTypeface(face);
        air_balance_btn.setTypeface(face);

        airline_code_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.state_array)));
        getStateCity(ApiConstants.STATELIST, "INDIA");
        airline_code_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position > 0 || !airline_code_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select"))) {
                    airlineCode = airline_code_spinner.getSelectedItem().toString();
                } else {
                    airlineCode = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        agent_id_spinner.setAdapter(getSpinnerAdapter(getResources().getStringArray(R.array.state_array)));
        getStateCity(ApiConstants.STATELIST,"India");
        agent_id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position > 0 || !agent_id_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select"))) {
                    agentId = agent_id_spinner.getSelectedItem().toString();
                } else {
                    airlineCode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getStateCity(final String methodName, String value) {
//        showCustomDialog();
        String valueToSend=Common.getDecodedString(value);
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<StateNameResponseModel> call = apiService.getState(methodName,valueToSend);
        call.enqueue(new Callback<StateNameResponseModel>() {
            @Override
            public void onResponse(Call<StateNameResponseModel>call, Response<StateNameResponseModel> response) {

                try{

                    if(response.body().Data!=null && response.body().Status.equalsIgnoreCase("Success")){
//                        hideCustomDialog();
                        String[] arr=new String[response.body().Data.size()+1];
                        arr[0]="select-state";

                        for (int p=0;p<response.body().Data.size();p++){
                            arr[p+1]=response.body().Data.get(p).StateName;
                        }
                        airline_code_spinner.setAdapter(getSpinnerAdapter(arr));
                        agent_id_spinner.setAdapter(getSpinnerAdapter(arr));

                    }
                    else
                    {
                        hideCustomDialog();
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StateNameResponseModel>call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }


    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }



    private void getCustomerDetail() {

        if(validate())
        {
            RblGetSenderRequest rblGetSenderRequest=new RblGetSenderRequest();
//            rblGetSenderRequest.RMobile =number_edt.getText().toString();
            rblGetSenderRequest.DoneCardUser=loginModel.Data.DoneCardUser;
            rblGetSenderRequest.DeviceId=Common.getDeviceId(context);
            rblGetSenderRequest.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            callGetDetail(rblGetSenderRequest);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobile_balance_btn:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(mobile_balance_btn);
                if(Common.checkInternetConnection(context)) {
                    getCustomerDetail();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

//                if(validate()) {
//                    Intent i = new Intent(context, MoneyTransferNextActivity.class);
//                    startActivity(i);
//                    Toast.makeText(context, "registered", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }

    private Boolean validate() {

//        if (number_edt.getText().toString().length() < 10)
//        {
//            Toast.makeText(context,R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }


    private void callGetDetail(RblGetSenderRequest rblGetSenderRequest) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClientRbl().create(ApiInterface.class);
        Call<RblGetSenderResponse> call = apiService.getSenderDetailPost(ApiConstants.GetSenderDetails,rblGetSenderRequest);
        call.enqueue(new Callback<RblGetSenderResponse>() {
            @Override
            public void onResponse(Call<RblGetSenderResponse>call, Response<RblGetSenderResponse> response) {
                try{
                    hideCustomDialog();

                    if(response.body().status ==1) {
                        Intent i = new Intent(context, MoneyTransferNextActivity.class);
                        i.putExtra("SenderDetails",response.body());
                        startActivity(i);
                    }
                    else
                    {
                        Bundle bundle=new Bundle();
//                        bundle.putString("SenderNumber", number_edt.getText().toString());
                        MoneyTransferRegistrationFragment moneyTransferRegistrationFragment=new MoneyTransferRegistrationFragment();
                        moneyTransferRegistrationFragment.setArguments(bundle);
                        ((MoneyTransferActivity)context).replaceFragmentWithBackStack(moneyTransferRegistrationFragment);
                        Toast.makeText(context,response.body().description,Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RblGetSenderResponse> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
