package com.justclick.clicknbook.Fragment.salesReport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.ListForSalesAccountAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.SalesAgentDetailModel;
import com.justclick.clicknbook.model.SalesAgentInfoModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailForAgentModel;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 09/24/2017.
 */

public class SalesAccountListFragment extends Fragment implements View.OnClickListener{
    private final int CALL_AGENT=1,AGENT_DETAIL=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<SalesAgentDetailModel.Data> salesAgentDataArrayList;
    private LinearLayoutManager layoutManager;
    private ListForSalesAccountAdapter listForSalesAccountAdapter;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private AgentCreditDetailForAgentModel reportListRequestModel;
    private LoginModel loginModel;
    private Dialog openAgentFilterDialog;
    private EditText agent_search_edt;
    private ListView list_agent;
    private String agentName="", agentDoneCard="";
    private AgentNameModel agentNameModel;
    AutocompleteAdapter autocompleteAdapter;

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
        salesAgentDataArrayList =new ArrayList<>();
        reportListRequestModel=new AgentCreditDetailForAgentModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account_list_for_agent, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        titleChangeListener.onToolBarTitleChange(getString(R.string.sales_account_statement));
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        view.findViewById(R.id.lin_sales_type_Filter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                agentName="";
                agentDoneCard="";
                openAgentFilterDialog();
            }
        });

        listForSalesAccountAdapter=new ListForSalesAccountAdapter(context, new ListForSalesAccountAdapter.OnRecyclerItemClickListener() {

            @Override
            public void onRecyclerItemClick(View view, ArrayList<SalesAgentDetailModel.Data> list, int position) {
                switch (view.getId()){
                    case R.id.infoTv:
                        if(Common.checkInternetConnection(context)){

                            reportListRequestModel.AgentDoneCardUser=list.get(position).donecarduser;
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
        }, salesAgentDataArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listForSalesAccountAdapter);

        if(salesAgentDataArrayList!=null && salesAgentDataArrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            reportListRequestModel.AgentDoneCardUser ="";
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
            listForSalesAccountAdapter.notifyDataSetChanged();
        }
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        return view;
    }

    private void openAgentFilterDialog() {
        openAgentFilterDialog = new Dialog(context,R.style.Theme_Design_Light);
        openAgentFilterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openAgentFilterDialog.setContentView(R.layout.agent_list_filter);

        agent_search_edt = (EditText) openAgentFilterDialog.findViewById(R.id.agent_search_edt);
        list_agent = (ListView) openAgentFilterDialog.findViewById(R.id.list_agent);

        openAgentFilterDialog.findViewById(R.id.applyTv).setOnClickListener(this);
        openAgentFilterDialog.findViewById(R.id.cancelTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgentFilterDialog.dismiss();
            }
        });

        if(agentNameModel!=null && agentNameModel.Data!=null && agentNameModel.Data.size()>0){
            autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
            list_agent.setAdapter(autocompleteAdapter);
        }

        list_agent.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agentName = autocompleteAdapter.getItem(position).AgencyName;
                        agentDoneCard=agentName.substring(agentName.indexOf("(")+1,agentName.indexOf(")"));
//                        Active = autocompleteAdapter.getItem(position).Active;
                        list_agent.setVisibility(View.GONE);
//                        agent_name_tv.setText(agentName);
//                        agent_search_rel.setVisibility(View.VISIBLE);
                        agent_search_edt.setText(agentName);
                        agent_search_edt.setSelection(agent_search_edt.getText().length());
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });

        agent_search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agent_search_edt.setText("");
                agentName="";
                agentDoneCard="";
//                Active="False";
                list_agent.setVisibility(View.VISIBLE);
            }
        });

        agent_search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Common.checkInternetConnection(context)) {
                    if(s.length()>=2) {
                        String term = s.toString();

                        AgentNameRequestModel model=new AgentNameRequestModel();
                        model.AgencyName=term;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        model.RequiredType=loginModel.Data.UserType;
                        call_agent(model,list_agent);

                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        openAgentFilterDialog.findViewById(R.id.applyTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgentFilterDialog.dismiss();
                agentName=agent_search_edt.getText().toString().trim();
                applyFilter();
            }
        });

        openAgentFilterDialog.show();
    }

    private void applyFilter() {
        salesAgentDataArrayList.clear();
        listForSalesAccountAdapter.notifyDataSetChanged();
        //set date to fragment
        pageStart=1;
        pageEnd=10;
        reportListRequestModel.AgentDoneCardUser =agentDoneCard;
        reportListRequestModel.EndPage =pageEnd+"";
        reportListRequestModel.StartPage =pageStart+"";
//        reportListRequestModel.StartPage =pageStart+"";
        reportListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        reportListRequestModel.DeviceId=Common.getDeviceId(context);
        reportListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

        if(Common.checkInternetConnection(context)) {
            callAgent( reportListRequestModel, NO_PROGRESS);
        }else {
            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
        }
    }

    public AgentNameModel call_agent(AgentNameRequestModel model, final ListView agencyList) {
//        agent.DATA.clear();
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
                try {
                    agentNameModel = response.body();
                    if(agentNameModel.StatusCode.equalsIgnoreCase("0")) {
                        agencyList.setVisibility(View.VISIBLE);
//                        agencyListRel.setVisibility(View.VISIBLE);
                        autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
                        agencyList.setAdapter(autocompleteAdapter);
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<AgentNameModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return agentNameModel;
    }

    private void agentInfoDialog(SalesAgentInfoModel agentInfoModel) {
            final Dialog infoDialog = new Dialog(context);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window window= infoDialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
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
        validityTv.setText(agentInfoModel.Validity);
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
                    reportListRequestModel.AgentDoneCardUser ="";
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
        new NetworkCall().callMobileService(reportListRequestModel, ApiConstants.SalesAgentDetail, context,
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
                    SalesAgentDetailModel agentModel = new Gson().fromJson(response.string(), SalesAgentDetailModel.class);
                    hideCustomDialog();
                    if(agentModel!=null){

                        if(agentModel.StatusCode.equalsIgnoreCase("0")) {
                            if(agentModel.Data.size()>0) {
                                salesAgentDataArrayList.addAll(agentModel.Data);
                                listForSalesAccountAdapter.notifyDataSetChanged();
                                totalPageCount = Integer.parseInt(agentModel.Data.get(0).TCount);
                            }else {
                                Toast.makeText(context,"No record Found", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            salesAgentDataArrayList.clear();
                            listForSalesAccountAdapter.notifyDataSetChanged();
                            Toast.makeText(context,agentModel.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        salesAgentDataArrayList.clear();
                        listForSalesAccountAdapter.notifyDataSetChanged();
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;

                case AGENT_DETAIL:
                    SalesAgentInfoModel agentInfoModel = new Gson().fromJson(response.string(), SalesAgentInfoModel.class);
                    hideCustomDialog();
                    if(agentInfoModel!=null){
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
}


