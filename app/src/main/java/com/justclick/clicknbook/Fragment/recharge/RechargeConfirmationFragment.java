package com.justclick.clicknbook.Fragment.recharge;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RechargeModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailModel;
import com.justclick.clicknbook.requestmodels.RechargeRequestModel;
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
 * A simple {@link Fragment} subclass.

 */
public class RechargeConfirmationFragment extends Fragment {

    private Context context;
    private RechargeRequestModel rechargeRequestModel;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView tv_recharge_type, tv_id_number, tv_type,
            tv_operator, tv_amount, tv_value4, tv_value5,commissionTv,markUpTv;
    private LinearLayout commissionLinear,markUpLinear;
    private View view_tv_value4, view_tv_value5, view_tv_type;
    private LoginModel loginModel;
    private float rechargeAmountValue=0f;

    public RechargeConfirmationFragment() {
        // Required empty public constructor
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_confirmation, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.mobileConfirmFragmentTitle));

        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);

        initializeView(view);

        Bundle bundle=getArguments();
        try {
//            if(bundle!=null){
                setValues(bundle.getInt("RechargeType"), bundle);
                rechargeAmountValue=Float.parseFloat(bundle.getString("RechargeAmount"));
//            }
        }catch (Exception e){

        }

        return view;
    }

    private void setValues(int rechargeType, Bundle bundle) {
        rechargeRequestModel=new RechargeRequestModel();
        rechargeRequestModel.DeviceId=Common.getDeviceId(context);
        rechargeRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        rechargeRequestModel.DoneCardUser =loginModel.Data.DoneCardUser;
        rechargeRequestModel.Password = MyPreferences.getLoginPassword(context);
        rechargeRequestModel.MobileNumber =bundle.getString("IdNumber");
        rechargeRequestModel.OptName=bundle.getString("Operator");
        rechargeRequestModel.Amount=bundle.getString("RechargeAmount");
        tv_amount.setText(bundle.getString("RechargeAmount"));
        tv_id_number.setText(bundle.getString("IdNumber"));
        tv_operator.setText(bundle.getString("Operator"));
        commissionTv.setText(( bundle.getString("Commision")));
        markUpTv.setText( bundle.getString("MarkUp"));
        switch (rechargeType){
            case RechargeFragment.MOBILE_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.mobile_type);
                rechargeRequestModel.ConnectionType =bundle.getString("MobileType");
                rechargeRequestModel.OptionalPara1=bundle.getString("Value4");
                rechargeRequestModel.OptionalPara2="test";
                rechargeRequestModel.OptionalPara3 ="test";

                tv_recharge_type.setText(context.getString(R.string.mobile_type));
                tv_type.setText(bundle.getString("MobileType"));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_value4.setVisibility(View.GONE);
                tv_value5.setVisibility(View.GONE);
                view_tv_value4.setVisibility(View.GONE);
                view_tv_value5.setVisibility(View.GONE);

                break;
            case RechargeFragment.DTH_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.dth_type);
                rechargeRequestModel.ConnectionType ="";
                rechargeRequestModel.OptionalPara1="test";
                rechargeRequestModel.OptionalPara2="test";
                rechargeRequestModel.OptionalPara3 ="test";


                tv_recharge_type.setText(context.getString(R.string.dth_type));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_type.setVisibility(View.GONE);
                view_tv_type.setVisibility(View.GONE);
                tv_value4.setVisibility(View.GONE);
                tv_value5.setVisibility(View.GONE);
                view_tv_value4.setVisibility(View.GONE);
                view_tv_value5.setVisibility(View.GONE);
                break;
            case RechargeFragment.DATACARD_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.datacard_type);
                rechargeRequestModel.ConnectionType =bundle.getString("DatacardType");
                rechargeRequestModel.OptionalPara1="test";
                rechargeRequestModel.OptionalPara2="test";
                rechargeRequestModel.OptionalPara3 ="test";

                tv_recharge_type.setText(context.getString(R.string.datacard_type));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_type.setText(bundle.getString("DatacardType"));
                tv_value4.setVisibility(View.GONE);
                tv_value5.setVisibility(View.GONE);
                view_tv_value4.setVisibility(View.GONE);
                view_tv_value5.setVisibility(View.GONE);
                break;
            case RechargeFragment.ELECTRICITY_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.electricity_type);
                rechargeRequestModel.ConnectionType ="";
                rechargeRequestModel.OptionalPara1=bundle.getString("Value4");
                rechargeRequestModel.OptionalPara2=bundle.getString("Value5");
                rechargeRequestModel.OptionalPara3 ="test";

                tv_recharge_type.setText(context.getString(R.string.electricity_type));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_type.setVisibility(View.GONE);
                view_tv_type.setVisibility(View.GONE);
                tv_value4.setText(bundle.getString("Value4"));
                tv_value5.setText(bundle.getString("Value5"));

                if(bundle.getString("Value4").length()==0){
                    tv_value4.setVisibility(View.GONE);
                    view_tv_value4.setVisibility(View.GONE);
                }else {
                    tv_value4.setVisibility(View.GONE);
                    view_tv_value4.setVisibility(View.GONE);
                }
                if(bundle.getString("Value5").length()==0){
                    tv_value5.setVisibility(View.GONE);
                    view_tv_value5.setVisibility(View.GONE);

                }else {
                    tv_value5.setVisibility(View.VISIBLE);
                    view_tv_value5.setVisibility(View.VISIBLE);
                }
                break;
            case RechargeFragment.LANDLINE_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.landline_type);
                rechargeRequestModel.ConnectionType ="";
                rechargeRequestModel.OptionalPara1=bundle.getString("Value4");
                rechargeRequestModel.OptionalPara2=bundle.getString("Value5");
                rechargeRequestModel.OptionalPara3 ="test";

                tv_recharge_type.setText(context.getString(R.string.landline_type));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_type.setVisibility(View.GONE);
                view_tv_type.setVisibility(View.GONE);
                tv_value4.setText(bundle.getString("Value4"));
                tv_value5.setText(bundle.getString("Value5"));

                if(bundle.getString("Value4").length()==0){
                    tv_value4.setVisibility(View.GONE);
                    view_tv_value4.setVisibility(View.GONE);
                }else {
                    tv_value4.setVisibility(View.VISIBLE);
                    view_tv_value4.setVisibility(View.VISIBLE);
                }
                if(bundle.getString("Value5").length()==0){
                    tv_value5.setVisibility(View.GONE);
                    view_tv_value5.setVisibility(View.GONE);

                }else {
                    tv_value5.setVisibility(View.VISIBLE);
                    view_tv_value5.setVisibility(View.VISIBLE);
                }
                break;
            case RechargeFragment.GAS_TYPE:

                rechargeRequestModel.RechargeType=getString(R.string.gas_type);
                rechargeRequestModel.ConnectionType ="";
                rechargeRequestModel.OptionalPara1=bundle.getString("Value4");
                rechargeRequestModel.OptionalPara2="test";
                rechargeRequestModel.OptionalPara3 ="test";


                tv_recharge_type.setText(context.getString(R.string.gas_type));
//                commissionTv.setText(bundle.getString("Commision"));
//                markUpTv.setText(bundle.getString("MarkUp"));
                tv_type.setVisibility(View.GONE);
                view_tv_type.setVisibility(View.GONE);

                tv_value4.setText(bundle.getString("Value4"));
                tv_value5.setVisibility(View.GONE);
                view_tv_value5.setVisibility(View.GONE);

                if(bundle.getString("Value4").length()==0){
                    tv_value4.setVisibility(View.GONE);
                    view_tv_value4.setVisibility(View.GONE);
                }else {
                    tv_value4.setVisibility(View.VISIBLE);
                    view_tv_value4.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void initializeView(View view) {
        tv_recharge_type=  view.findViewById(R.id.tv_recharge_type);
        tv_id_number=  view.findViewById(R.id.tv_id_number);
        tv_type=  view.findViewById(R.id.tv_type);
        tv_operator=  view.findViewById(R.id.tv_operator);
        tv_amount= view.findViewById(R.id.tv_amount);
        tv_value4=  view.findViewById(R.id.tv_value4);
        tv_value5=  view.findViewById(R.id.tv_value5);
        view_tv_value4= view.findViewById(R.id.view_tv_value4);
        view_tv_value5= view.findViewById(R.id.view_tv_value5);
        view_tv_type= view.findViewById(R.id.view_tv_type);
        commissionTv=  view.findViewById(R.id.commissionTv);
        markUpTv=  view.findViewById(R.id.markUpTv);

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RechargeConfirmationFragment.this.getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.checkInternetConnection(context)) {
                    checkBalanceAndRecharge();
//                    Toast.makeText(context,"Recharge",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void checkBalanceAndRecharge() {
        String valueToSend = loginModel.Data.Email + "," + loginModel.Data.UserType + "," +
                loginModel.Data.DoneCardUser + "," + loginModel.Data.DoneCardUser;

        AgentCreditDetailModel creditDetailModel=new AgentCreditDetailModel();
        creditDetailModel.DoneCardUser =loginModel.Data.DoneCardUser;
        creditDetailModel.DeviceId =Common.getDeviceId(context);
        creditDetailModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        creditDetailModel.AgentDoneCardUser = loginModel.Data.DoneCardUser;

        byte[] encodiedBytes = Base64.encode(valueToSend.getBytes(), Base64.DEFAULT);
            call_agentdetails(new String(encodiedBytes).replace("\n",""),creditDetailModel);

    }

    private void call_agentdetails(final String value, AgentCreditDetailModel creditDetailModel) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<AgentDetails> call = apiService.agentdetailPost(ApiConstants.CreditDetails, creditDetailModel);
        call.enqueue(new Callback<AgentDetails>() {
            @Override
            public void onResponse(Call<AgentDetails> call, Response<AgentDetails> response) {

                try {
                    if(response.body().StatusCode.equalsIgnoreCase("0")){
                        if(isSufficientBalance(response.body())){
                            MyCustomDialog.setDialogMessage("Recharging...");
//                            hideCustomDialog();
                        callRecharge(rechargeRequestModel);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context,"Your balance is low",Toast.LENGTH_LONG).show();
                        }

                    }else {
                        hideCustomDialog();
                        Toast.makeText(context,response.body().Status,Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    hideCustomDialog();
                }

            }

            @Override
            public void onFailure(Call<AgentDetails> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callRecharge(RechargeRequestModel rechargeRequestModel) {

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<RechargeModel> call = apiService.mobileRechargePost(ApiConstants.RECHARGE, rechargeRequestModel);
        call.enqueue(new Callback<RechargeModel>() {
            @Override
            public void onResponse(Call<RechargeModel> call, Response<RechargeModel> response) {
                try {
                    hideCustomDialog();
                    if(response!=null){
                       if(response.body().StatusCode.equalsIgnoreCase("0")){
                           Common.showResponsePopUp(context,response.body().Data.ResponseMessage);
                       }else {
//                           Toast.makeText(context,response.body().Status,Toast.LENGTH_LONG).show();
                           Common.showResponsePopUp(context,response.body().Data.ResponseMessage);
                       }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }
//                    Toast.makeText(context, response.body().Message+"\n"+
//                            response.body().Data,Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    hideCustomDialog();
                }


            }

            @Override
            public void onFailure(Call<RechargeModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }


        });
    }

    private boolean isSufficientBalance(AgentDetails agent) {
        try {
            if(Float.parseFloat(agent.Data.ActualBalance)+Float.parseFloat(agent.Data.Credit)>=rechargeAmountValue){
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }

    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Checking balance...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

}
