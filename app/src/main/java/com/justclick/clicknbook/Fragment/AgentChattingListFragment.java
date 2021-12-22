package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.chatting.ChattingActivity;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
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
 * Created by Lenovo on 03/25/2017.
 */

public class AgentChattingListFragment extends Fragment
{
    private ToolBarTitleChangeListener titleChangeListener;
    private EditText agent_search_edt;
    private ListView list_agent;
    private RelativeLayout agent_search_rel;
    private String agentName="";
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
        View view= inflater.inflate(R.layout.fragment_agent_chatting_list, container, false);

        titleChangeListener.onToolBarTitleChange(getString(R.string.nav_chatting));

        agent_search_edt = (EditText) view.findViewById(R.id.agent_search_edt);
//        search_tv = (TextView) view.findViewById(R.Id.search_tv);
//        agent_name_tv = (TextView) view.findViewById(R.Id.agent_name_tv);
        agent_search_rel = (RelativeLayout) view.findViewById(R.id.agent_search_rel);
        list_agent = (ListView) view.findViewById(R.id.list_agent);

        if(agentNameModel!=null && agentNameModel.Data!=null && agentNameModel.Data.size()>0){
            autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
            list_agent.setAdapter(autocompleteAdapter);
        }else {
            AgentNameRequestModel model=new AgentNameRequestModel();
            model.AgencyName="";
            model.MerchantID=loginModel.Data.MerchantID;
            model.RefAgency=loginModel.Data.RefAgency;
            model.DeviceId=Common.getDeviceId(context);
            model.DoneCardUser=loginModel.Data.DoneCardUser;
            model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            model.Type=loginModel.Data.UserType;

            call_agent(model, true);
        }

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
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
                        agent_search_rel.setVisibility(View.VISIBLE);
//                        agent_search_edt.setText(agentName);
                        agent_search_edt.setSelection(agent_search_edt.getText().length());
                        String an=agentNameModel.Data.get(position).AgencyName;
                        String jct=an.substring(an.indexOf("(")+1, an.indexOf(")"));
                        String agency=an.substring(0, an.indexOf("("));
                        startActivity(new Intent(context, ChattingActivity.class)
                                .putExtra("JCT",jct)
                                .putExtra("AGENCY",agency));
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });

        agent_search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

                    call_agent(model, false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    public AgentNameModel call_agent(AgentNameRequestModel model, boolean isDialog) {
//        agent.DATA.clear();

        if(isDialog){
            showCustomDialog();
        }
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
//                Toast.makeText(context, "response ", Toast.LENGTH_LONG).show();
                hideCustomDialog();
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
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return agentNameModel;
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }


}


