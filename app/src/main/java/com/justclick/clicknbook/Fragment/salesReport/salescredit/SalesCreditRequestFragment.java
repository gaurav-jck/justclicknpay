package com.justclick.clicknbook.Fragment.salesReport.salescredit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.AddBeneOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaAddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.databinding.FragmentSalesCreditRequestBinding;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;


public class SalesCreditRequestFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private FragmentSalesCreditRequestBinding binding;
    private String agentName,agencyName, agentCode;
    private LoginModel loginModel;
    private BankResponse ifscByCodeResponse;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSalesCreditRequestBinding.inflate(getLayoutInflater());
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        initializeViews();
        return binding.getRoot();
    }

    private void initializeViews() {

        binding.submitTv.setOnClickListener(this);
        binding.cancelTv.setOnClickListener(this);
        binding.backArrow.setOnClickListener(this);

        binding.cardView.setVisibility(View.GONE);

        setText();

//        getBankNames();
        getAgencyList();

        binding.atvAgent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < agentArrayList.size(); i++) {
                    if (selection.contains(agentArrayList.get(i).agencyName) &&
                            selection.contains(agentArrayList.get(i).agentCode)) {
                        pos = i;
                        break;
                    }
                }
//                if(pos>0){
                try{
//                    agentCode=agentArrayList.get(pos);
                    agencyName=agentArrayList.get(pos).agencyName;
                    agentCode=agentArrayList.get(pos).agentCode;
//                    Toast.makeText(context, agencyName+"\n"+agentCode, Toast.LENGTH_SHORT).show();
                    getAgentDetails();
                    Common.hideSoftKeyboard(requireActivity());
                }catch (Exception e){
//                        ifscByCodeResponse=null;
                }
                /*}else {
                    bankName="";
                    ifscEdt.setText("");
                    ifscByCodeResponse=null;
                    verifyAccountTv.setVisibility(View.GONE);
                }*/
            }
        });


        binding.atvAgent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) binding.atvAgent.showDropDown();
            }
        });

        binding.atvAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.atvAgent.showDropDown();
            }
        });

    }

    private class AgentDetailRequest{
        public String saleCode, agentcode;
    }
    private void getAgentDetails() {
        AgentDetailRequest request=new AgentDetailRequest();
        request.saleCode=loginModel.Data.DoneCardUser;
        request.agentcode=agentCode;
        new NetworkCall().callService(NetworkCall.getSalesApiInterface().getSalesPost(ApiConstants.getSaleAgentDetails,
                        request),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerAgentDetail(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    SalesAgentDetailResponse.data agentDetail;
    private void responseHandlerAgentDetail(ResponseBody response) {
        try {
            SalesAgentDetailResponse senderResponse = new Gson().fromJson(response.string(), SalesAgentDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("00")){
//                    Toast.makeText(context,senderResponse.status,Toast.LENGTH_SHORT).show();
                    agentDetail=senderResponse.data;
                    showOtpView();
                }else {
                    Common.showCommonAlertDialog(context, senderResponse.status, "Api Response");
                    binding.cardView.setVisibility(View.GONE);
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void getAgencyList() {
        new NetworkCall().callService(NetworkCall.getSalesApiInterface().getSalesQuery(ApiConstants.getAgenntList,
                        loginModel.Data.UserId),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerAgencyList(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    ArrayList<SalesAgentListResponse.data> agentArrayList=new ArrayList<>();
    private void responseHandlerAgencyList(ResponseBody response) {
        try {
            SalesAgentListResponse listResponse = new Gson().fromJson(response.string(), SalesAgentListResponse.class);
            if(listResponse!=null){
                if(listResponse.statusCode.equals("00")){
                    Toast.makeText(context,listResponse.status,Toast.LENGTH_SHORT).show();
                    if(listResponse.data!=null && listResponse.data.size()>0){
                        agentArrayList.addAll(listResponse.data);
                        setAdapter();
                    }
                }else {
                    Toast.makeText(context,listResponse.status,Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void setAdapter() {
        String[] arr=new String[agentArrayList.size()];
        for (int p=0;p<agentArrayList.size();p++){
            arr[p]=agentArrayList.get(p).agencyName+" ["+agentArrayList.get(p).agentCode+"]";
        }
        binding.atvAgent.setAdapter(getSpinnerAdapter(arr));
        binding.atvAgent.showDropDown();
    }


    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    private void setText() {
        Typeface face1 = Common.TitleTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);
        binding.amountEdt.setTypeface(face2);
        binding.remarksEdt.setTypeface(face2);

        binding.submitTv.setTypeface(face3);
        binding.cancelTv.setTypeface(face3);

    }

    private boolean validate() {
        Float creditLimit=Float.parseFloat(agentDetail.approvedLimit);
        if(binding.amountEdt.getText().toString().isEmpty() ){
            Toast.makeText(context,"Please enter amount",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isdecimalvalid(binding.amountEdt.getText().toString())){
            Toast.makeText(context,"Please enter valid amount",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(binding.amountEdt.getText().toString())==0){
            Toast.makeText(context,"Amount can not be 0",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Float.parseFloat(binding.amountEdt.getText().toString())>creditLimit){
            Toast.makeText(context,"Amount should be less than your approved limit.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.remarksEdt.getText().toString().isEmpty()){
            Toast.makeText(context,"Please enter remarks.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.submitTv:
                Common.preventFrequentClick(binding.submitTv);
                Common.hideSoftKeyboard(requireActivity());
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        salesAutoCreditCall();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancelTv:
                Common.preventFrequentClick(binding.cancelTv);
                getParentFragmentManager().popBackStack();
                break;
        }
    }

    private class AutoCreditRequest{
        public String DeviceId, LoginSessionId, TransactionType, AgentDoneCardUser, AddCredit,
                Remarks, saledonecarduser;
    }
    private void salesAutoCreditCall() {
        AutoCreditRequest requestModel=new AutoCreditRequest();
        requestModel.saledonecarduser=loginModel.Data.DoneCardUser;
        requestModel.AgentDoneCardUser=agentDetail.doneCardUser;
        requestModel.AddCredit=binding.amountEdt.getText().toString();
        requestModel.Remarks=binding.remarksEdt.getText().toString();
        requestModel.LoginSessionId=loginModel.LoginSessionId;
        requestModel.DeviceId=Common.getDeviceId(context);
        requestModel.TransactionType="Auto Credit";

        new NetworkCall().callService(NetworkCall.getSalesApiInterface().getSalesPost(ApiConstants.saleUpdateBalance,
                        requestModel),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerCredit(response);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    String beneId, stateRes;
    private void responseHandlerCredit(ResponseBody response) {
        try {
            SalesAgentDetailResponse senderResponse = new Gson().fromJson(response.string(), SalesAgentDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("0")){
                    getAgentDetails();
                    Common.showSuccessDialog(context,senderResponse.status);
                }else {
                    Common.showCommonAlertDialog(context, senderResponse.status, "Api Response");
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context,"Error in credit response.",Toast.LENGTH_LONG).show();
        }
    }

    private void showOtpView() {
        binding.cardView.setVisibility(View.VISIBLE);
        binding.salesPersonTv.setText(agentDetail.salesPersonName);
        binding.agencyNameTv.setText(agentDetail.agencyName);
        binding.maxCreditLimitTv.setText(agentDetail.approvedLimit);
        binding.availableLimitTv.setText(agentDetail.availableCredit);
        binding.balanceTv.setText(agentDetail.balance);
        if(agentDetail.autoCredit){
            if(!agentDetail.allowMinusStatus/*(if allowMinus is false then check availCredit)*/
                    && Float.parseFloat(agentDetail.availableCredit)<0){
                binding.creditAlertMessageTv.setText("You have pending due, please credit and continue.");
                binding.creditAlertMessageTv.setBackgroundResource(R.color.train_dashboard4);
                binding.creditAlertMessageTv.setTextColor(getResources().getColor(R.color.train_dashboard444, null));
                disableSubmitButton();
            }else {
                binding.creditAlertMessageTv.setText("You can give max "+agentDetail.approvedLimit+" credit.");
                binding.creditAlertMessageTv.setBackgroundResource(R.color.train_dashboard5);
                binding.creditAlertMessageTv.setTextColor(getResources().getColor(R.color.train_dashboard555, null));
                binding.submitTv.setEnabled(true);
            }
        }else {
            binding.creditAlertMessageTv.setText("You are not currently allowed to give credit to this agent.");
            binding.creditAlertMessageTv.setBackgroundResource(R.color.train_dashboard4);
            binding.creditAlertMessageTv.setTextColor(getResources().getColor(R.color.train_dashboard444, null));
            disableSubmitButton();
        }
    }

    private void disableSubmitButton() {
        binding.submitTv.setEnabled(false);
        binding.submitTv.setAlpha(0.5f);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(isBackPress) {
//            backPress.onJctDetailBackPress();
//        }
    }
}