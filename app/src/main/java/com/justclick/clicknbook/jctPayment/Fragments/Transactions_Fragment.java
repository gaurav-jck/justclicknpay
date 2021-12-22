package com.justclick.clicknbook.jctPayment.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.jctPayment.Activities.ViewAllTransaction;
import com.justclick.clicknbook.jctPayment.Adapters.RecyclerRecentTransaction;
import com.justclick.clicknbook.jctPayment.Models.TransactionInfo;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.MyPreferences;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions_Fragment extends Fragment {

    private Context context;
    HorizontalScrollView hs_view;
    ProgressDialog progressDialog;
    TextView tv_toaday, tv_total, tv_commission, tv_all_transaction;
    List<TransactionInfo> infoList = new ArrayList<TransactionInfo>();
    RecyclerRecentTransaction recyclerRecentTransaction;
    RecyclerView recycler_recent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        context=getActivity();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions_old, container, false);

        progressDialog = new ProgressDialog(context);

        hs_view = (HorizontalScrollView) rootView.findViewById(R.id.hs_view);
        tv_toaday = (TextView)rootView.findViewById(R.id.tv_today);
        tv_total = (TextView)rootView.findViewById(R.id.tv_total);
        tv_commission = (TextView)rootView.findViewById(R.id.tv_commission);
        tv_all_transaction = (TextView)rootView.findViewById(R.id.tv_all_transaction);
        recycler_recent = (RecyclerView)rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler_recent.setLayoutManager(linearLayoutManager);
        recycler_recent.setHasFixedSize(true);


        hs_view.post(new Runnable() {
            public void run() {
                hs_view.scrollTo(0, 0);
            }
        });

        // get transaction data
//        getTransactionList();


        tv_all_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ViewAllTransaction.class));
            }
        });


        return rootView;

    }


    private void getTransactionList() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Fetching The Transaction List....");
        progressDialog.show();
        //String local_url = "https://aeps-api-30587079.ap-south-1.elb.amazonaws.com/api/agent/stats";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.TRANSACTION_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            /*tv_toaday.setText(obj.getString("todayTxn"));
                            tv_total.setText(obj.getString("totalTxn"));
                            tv_commission.setText(obj.getString("totalCommission"));*/

//                            new changes
                            tv_toaday.setText(obj.getString("todayTxn"));
                            tv_total.setText(obj.getString("totalMonthTxn"));
                            tv_commission.setText(obj.getString("todayCommission"));

                            JSONArray jsonArray = obj.getJSONArray("latestTxn");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject myObject = jsonArray.getJSONObject(i);
                                TransactionInfo info = new TransactionInfo(myObject.getString("id"),myObject.getString("amount"),
                                        myObject.getString("productId"),myObject.getString("productName"),myObject.getString("productCode"),
                                        myObject.getString("transactionStatus"),myObject.getString("bank_ref_id"),myObject.getString("bank_request_at")
                                        ,myObject.getString("transactionMessage"));
                                infoList.add(info);
                            }
                            // call adapter class
                            if(infoList.size()>0){
                                recyclerRecentTransaction = new RecyclerRecentTransaction(context,infoList);
                                recycler_recent.setAdapter(recyclerRecentTransaction);
                            } else {
                                Toast.makeText(context, "You haven't any transaction yet!", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            if(isAdded()){
                                Toast.makeText(context, getResources().getString(R.string.transactionListError), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if(isAdded()){
                            Toast.makeText(context, getResources().getString(R.string.transactionListError), Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                //pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("Authorization", str_token);
                return pars;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    // after swipe refresh data
   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            infoList.clear();
            getTransactionList();
            //Toast.makeText(context, "refresh", Toast.LENGTH_SHORT).show();
            hs_view.post(new Runnable() {
                public void run() {
                    hs_view.scrollTo(0, 0);
                }
            });
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        infoList.clear();
        getTransactionList();
        //Toast.makeText(context, "refresh", Toast.LENGTH_SHORT).show();
        hs_view.post(new Runnable() {
            public void run() {
                hs_view.scrollTo(0, 0);
            }
        });
    }
}
