package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailModel;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class AgentSearchFragment extends Fragment
{
    private final int AGENT_DETAILS=1;
    private ToolBarTitleChangeListener titleChangeListener;
    private EditText agent_search_edt;
    private TextView search_tv, agent_name_tv;
    private ListView list_agent;
    private RelativeLayout agent_search_rel;
    private String agentName="", agentDoneCardFromName="", Active="False";
    private AgentNameModel agentNameModel;
    private AutocompleteAdapter autocompleteAdapter;

    Context context;
    private LoginModel loginModel;

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
        agentNameModel=new AgentNameModel();
        context=getActivity();

        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_agent_search, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.agentSearchFragmentTitle));
        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
        search_tv = (TextView) view.findViewById(R.id.search_tv);
        agent_name_tv = (TextView) view.findViewById(R.id.agent_name_tv);
        agent_search_rel = (RelativeLayout) view.findViewById(R.id.agent_search_rel);
        list_agent = (ListView) view.findViewById(R.id.list_agent);

        try {
            agent_name_tv.setText(loginModel.Data.AgencyName.toUpperCase()+"\n( "+loginModel.Data.DoneCardUser+" )");
        }catch (NullPointerException e){}

        if(agentNameModel!=null && agentNameModel.Data!=null && agentNameModel.Data.size()>0){
            autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
            list_agent.setAdapter(autocompleteAdapter);
        }

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });

        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                search(v);
            }
        });
        //when autocomplete is clicked
        list_agent.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agentName = autocompleteAdapter.getItem(position).AgencyName;
                        Active = autocompleteAdapter.getItem(position).Active;
                        list_agent.setVisibility(View.GONE);
//                        agent_name_tv.setText(agentName);
                        agent_search_rel.setVisibility(View.VISIBLE);
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
                Active="False";
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
                        model.MerchantID=loginModel.Data.MerchantID;
                        model.RefAgency=loginModel.Data.RefAgency;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;

                        call_agent(model);
//                    Toast.makeText(context, s.toString() + " " + start + " " + before + " " + count, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    public AgentNameModel call_agent(AgentNameRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
                try {
                    agentNameModel = response.body();
                    if(agentNameModel.StatusCode.equalsIgnoreCase("0")) {
                        autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
                        list_agent.setAdapter(autocompleteAdapter);
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

    private void callAgentDetails(AgentCreditDetailModel creditDetailModel) {
        showCustomDialog();
        new NetworkCall().callMobileService(creditDetailModel, ApiConstants.CreditDetails, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, AGENT_DETAILS);
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
                case AGENT_DETAILS:
                    AgentDetails agentModel = new Gson().fromJson(response
                            .string(), AgentDetails.class);
                    hideCustomDialog();
                    if(agentModel!=null){
                        if(agentModel.StatusCode.equalsIgnoreCase("0")) {
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("AgentDetail", agentModel);
                            bundle.putSerializable("AgentDoneCard", agentDoneCardFromName);
                            bundle.putSerializable("Active", Active);
                            UpdateCreditFragment cf = new UpdateCreditFragment();
                            cf.setArguments(bundle);
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(cf);
                        } else {
                            Toast.makeText(context, agentModel.Status, Toast.LENGTH_SHORT).show();
                        }
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


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    public void search(View v)
            {
                if (v == search_tv)
                {
                    if(Common.checkInternetConnection(context)) {
                        if (validatee(agentName))
                        {
                            try{
                                agentDoneCardFromName = agentName.substring(agentName.indexOf("(") + 1,
                                        agentName.indexOf(")"));
                                AgentCreditDetailModel creditDetailModel=new AgentCreditDetailModel();
                                creditDetailModel.AgentDoneCardUser =agentDoneCardFromName;
                                creditDetailModel.DoneCardUser = loginModel.Data.DoneCardUser;
//                                creditDetailModel.DeviceId = Common.getDeviceId(context);
                                creditDetailModel.DeviceId = EncryptionDecryptionClass.Encryption(Common.getDeviceId(context), context);
                                creditDetailModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

                                callAgentDetails(creditDetailModel);

                            }catch (Exception e){
                                Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private boolean validatee(String name)
            {

                if (TextUtils.isEmpty(name) || name.length() < 5)
                {
                    Toast.makeText(context, "Please Search and Select CompanyName/JCT_CODE", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;

            }

        }


