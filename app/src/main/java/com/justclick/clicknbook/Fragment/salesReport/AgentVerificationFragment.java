package com.justclick.clicknbook.Fragment.salesReport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.ListForAgentVerificationAdapter;
import com.justclick.clicknbook.model.ForgetPasswordModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.SalesAgentInfoModel;
import com.justclick.clicknbook.model.SalesVerificationModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailForAgentModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;


/**
 * Created by Lenovo on 09/24/2017.
 */

public class AgentVerificationFragment extends Fragment implements View.OnClickListener{
    private final int CALL_AGENT=1,VERIFICATION=2,AGENT_DETAIL=3;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<SalesVerificationModel.Data> agentVerificationDataArrayList;
    private LinearLayoutManager layoutManager;
    private ListForAgentVerificationAdapter listForAgentVerificationAdapter;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private AgentCreditDetailForAgentModel reportListRequestModel;
    private LoginModel loginModel;
    /*Boolean callOnBackPress=false;*/
    private static AgentVerificationFragment agentVerificationFragment;

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
        agentVerificationDataArrayList =new ArrayList<>();
        reportListRequestModel=new AgentCreditDetailForAgentModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        /*callOnBackPress=false;*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static AgentVerificationFragment newInstance() {
        if(agentVerificationFragment==null){
            agentVerificationFragment= new AgentVerificationFragment();
        }
        return agentVerificationFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_list_for_agent_verification, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agent_verification_fragment));
        listForAgentVerificationAdapter=new ListForAgentVerificationAdapter(context, new ListForAgentVerificationAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<SalesVerificationModel.Data> list, int position) {
                Common.preventFrequentClick(view);
                switch (view.getId()){
                    case R.id.approved_tv:
                        if(Common.checkInternetConnection(context)){
                            reportListRequestModel.AgentDoneCardUser=list.get(position).AgencyCode;
                            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            reportListRequestModel.DeviceId=Common.getDeviceId(context);
                            reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

                            callAgentVerification( reportListRequestModel, SHOW_PROGRESS,ApiConstants.SalesApproves);
                        }else {
                            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.disapproved_tv:
                        if(Common.checkInternetConnection(context)){
                            reportListRequestModel.AgentDoneCardUser=list.get(position).AgencyCode;
                            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                        reportListRequestModel.DeviceId=Common.getDeviceId(context);
                        reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

                            callAgentVerification( reportListRequestModel, SHOW_PROGRESS, ApiConstants.SalesDispproves);
                        }else {
                            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case R.id.name_tv:
                            if(Common.checkInternetConnection(context)){
                                reportListRequestModel.AgentDoneCardUser=list.get(position).AgencyCode;
                                reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                                reportListRequestModel.DeviceId=Common.getDeviceId(context);
                                reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

                                AgentInfo(reportListRequestModel, SHOW_PROGRESS);
                            }else {
                                Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                            }
                            break;
                }
            }
        }, agentVerificationDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listForAgentVerificationAdapter);

//        if(agentVerificationDataArrayList!=null && agentVerificationDataArrayList.size()==0 /*&& !callOnBackPress*/) {
//            pageStart=1;
//            pageEnd=10;
//            reportListRequestModel.EndPage =pageEnd+"";
//            reportListRequestModel.StartPage =pageStart+"";
//            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
//            reportListRequestModel.DeviceId=Common.getDeviceId(context);
//            reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
//                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
//
//            if(Common.checkInternetConnection(context)) {
//                getBusBookingList(reportListRequestModel, SHOW_PROGRESS);
//            }else {
//                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
//            }
//        }else {
//            listForAgentVerificationAdapter.notifyDataSetChanged();
//        }
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        return view;
    }

//    https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount <= totalPageCount
                    && dy>0) {
                if(!(pageEnd>totalItemCount)) {
                    pageStart = pageStart + 10;
                    pageEnd = pageEnd + 10;
                    reportListRequestModel.EndPage =pageEnd+"";
                    reportListRequestModel.StartPage =pageStart+"";
                    reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                    reportListRequestModel.DeviceId=Common.getDeviceId(context);
                    reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

                    callAgent( reportListRequestModel, NO_PROGRESS);
                }
            }
        }
    };

    public void callAgent(AgentCreditDetailForAgentModel reportListRequestModel, boolean progress) {
        if(progress && !MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.SalesVerification, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, CALL_AGENT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void callAgentVerification(AgentCreditDetailForAgentModel reportListRequestModel, boolean progress,String method) {
        if(progress && !MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        new NetworkCall().callMobileService(reportListRequestModel, method, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, VERIFICATION);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void AgentInfo(AgentCreditDetailForAgentModel reportListRequestModel, boolean progress) {
        if(progress && !MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.AgentDetail, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, AGENT_DETAIL);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case CALL_AGENT:
                    SalesVerificationModel agentModel = new Gson().fromJson(response.string(), SalesVerificationModel.class);
                    hideCustomDialog();
                    if(agentModel!=null && agentModel.StatusCode.equalsIgnoreCase("0")){
                        /*callOnBackPress=true;*/
                        if(agentModel.Data.size()>0) {
                            agentVerificationDataArrayList.addAll(agentModel.Data);
                            listForAgentVerificationAdapter.notifyDataSetChanged();
                            totalPageCount = Integer.parseInt(agentModel.Data.get(0).TCount);
                        }else {
                            Toast.makeText(context,"No Record Found", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        agentVerificationDataArrayList.clear();
                        listForAgentVerificationAdapter.notifyDataSetChanged();
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;

                case VERIFICATION:
                    ForgetPasswordModel agentVerificationModel = new Gson().fromJson(response.string(), ForgetPasswordModel.class);
                    hideCustomDialog();
                    if(agentVerificationModel!=null && agentVerificationModel.StatusCode.equalsIgnoreCase("0")){
//                            getBusBookingList( reportListRequestModel, NO_PROGRESS);
//                            listForAgentVerificationAdapter.notifyDataSetChanged();
                        Toast.makeText(context,agentVerificationModel.Data.Message, Toast.LENGTH_LONG).show();
                        agentVerificationDataArrayList.clear();
                        pageStart=1;
                        pageEnd=10;
                        reportListRequestModel.EndPage =pageEnd+"";
                        reportListRequestModel.StartPage =pageStart+"";
                        callAgent( reportListRequestModel, SHOW_PROGRESS);

                    }else {
                        agentVerificationDataArrayList.clear();
                        listForAgentVerificationAdapter.notifyDataSetChanged();
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;
                case AGENT_DETAIL:
                    SalesAgentInfoModel agentInfoModel = new Gson().fromJson(response.string(), SalesAgentInfoModel.class);
                    hideCustomDialog();
                    if(agentInfoModel!=null && agentInfoModel.StatusCode.equalsIgnoreCase("0")){
                        agentInfoDialog(agentInfoModel);
                    }else {

                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }


    private void agentInfoDialog(SalesAgentInfoModel agentInfoModel) {
        final Dialog infoDialog = new Dialog(context);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window window= infoDialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
        infoDialog.setContentView(R.layout.agent_info_layout);
        WindowManager.LayoutParams params = infoDialog.getWindow().getAttributes();

        infoDialog.getWindow().setAttributes(params);
        final TextView name_tv= (TextView) infoDialog.findViewById(R.id.name_tv);
        final TextView validityTv= (TextView) infoDialog.findViewById(R.id.validityTv);
        final TextView contact_tv= (TextView) infoDialog.findViewById(R.id.contact_tv);
        final TextView addressTv= (TextView) infoDialog.findViewById(R.id.addressTv);
        final TextView city_tv= (TextView) infoDialog.findViewById(R.id.city_tv);
        final TextView stateTv= (TextView) infoDialog.findViewById(R.id.stateTv);
        final TextView pincode_tv= (TextView) infoDialog.findViewById(R.id.pincode_tv);
        final TextView mobileTv= (TextView) infoDialog.findViewById(R.id.mobileTv);
        final TextView email_tv= (TextView) infoDialog.findViewById(R.id.email_tv);
        final TextView panNoTv= (TextView) infoDialog.findViewById(R.id.panNoTv);
        name_tv.setText(agentInfoModel.AgencyName);
        validityTv.setText("");
        contact_tv.setText(agentInfoModel.ContactPerson);
        addressTv.setText(agentInfoModel.Address);
        city_tv.setText(agentInfoModel.City);
        stateTv.setText(agentInfoModel.State);
        pincode_tv.setText(agentInfoModel.PinCode);
        mobileTv.setText(agentInfoModel.Mobile);
        email_tv.setText(agentInfoModel.Email);
        panNoTv.setText(agentInfoModel.PANNo);
        ImageButton dialogCloseButton = (ImageButton) infoDialog.findViewById(R.id.close_btn);

        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                infoDialog.dismiss();
            }
        });
        infoDialog.show();
    }


    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(agentVerificationDataArrayList!=null  /*&& !callOnBackPress*/) {
            agentVerificationDataArrayList.clear();
            pageStart=1;
            pageEnd=10;
            reportListRequestModel.EndPage =pageEnd+"";
            reportListRequestModel.StartPage =pageStart+"";
            reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            reportListRequestModel.DeviceId=Common.getDeviceId(context);
            reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

            if(Common.checkInternetConnection(context)) {
                callAgent(reportListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            listForAgentVerificationAdapter.notifyDataSetChanged();
        }
    }
}


