package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.Common;

import java.text.DecimalFormat;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class BalanceCheckFragment extends Fragment {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    TextView AvlCrd,AvlBal,Add_crd,agent;
    String agentDoneCard;
    private AgentDetails agentDetails;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        try {
            agentDetails= (AgentDetails) getArguments().getSerializable("AgentDetail");
            agentDoneCard= getArguments().getString("AgentDoneCard");
        }catch (Exception e){

        }

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
        View view = inflater.inflate(R.layout.fragment_balance_check, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agentBalanceCheckFragmentTitle));
        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        AvlCrd= (TextView) view.findViewById(R.id.et_avlCrd);
        AvlBal= (TextView) view.findViewById(R.id.et_avlBala);
        Add_crd= (TextView) view.findViewById(R.id.eT_addcrd);

        agent= (TextView) view.findViewById(R.id.name);
        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });

        try {
            agent.setText(agentDetails.Data.AgencyName+"\n("+agentDoneCard+")");
            AvlBal.setText(agentDetails.Data.ActualBalance);
            AvlCrd.setText(agentDetails.Data.Credit);
            String distMob=agentDetails.Data.DistMobNo.isEmpty()?"":"\nM. "+agentDetails.Data.DistMobNo;
            ((EditText)view.findViewById(R.id.distributor_edt)).setText(agentDetails.Data.Distributor+distMob);
            String salesMob=agentDetails.Data.SalesMobNo.isEmpty()?"":"\nM. "+agentDetails.Data.SalesMobNo;
            ((EditText)view.findViewById(R.id.sales_person_edt)).setText(agentDetails.Data.SalesPerson+salesMob);

            DecimalFormat df = new DecimalFormat("#.##");
            String t=df.format(Double.parseDouble(agentDetails.Data.ActualBalance)+Double.parseDouble(agentDetails.Data.Credit));
            ((EditText)view.findViewById(R.id.et_total)).setText(t+"");

        }catch (Exception e){

        }
    }

   /* @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }*/
}