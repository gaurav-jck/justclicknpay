package com.justclick.clicknbook.jctPayment.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Adapters.RecyclerAllTransactions;
import com.justclick.clicknbook.jctPayment.Models.TransactionInfo;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAllTransaction extends AppCompatActivity {

    RecyclerView recycler_all_transaction;
    List<TransactionInfo> infoList = new ArrayList<TransactionInfo>();
    RecyclerAllTransactions recyclerAllTransactions;
    ProgressDialog progressDialog;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_transaction);
        context=this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(context);

        recycler_all_transaction = (RecyclerView)findViewById(R.id.recycler_all);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler_all_transaction.setLayoutManager(linearLayoutManager);
        recycler_all_transaction.setHasFixedSize(true);


        // get transaction data
        getTransactionList();


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
                            JSONArray jsonArray = obj.getJSONArray("latestTxn");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject myObject = jsonArray.getJSONObject(i);
                                TransactionInfo info = new TransactionInfo(myObject.getString("id"),myObject.getString("amount"),
                                        myObject.getString("productId"),myObject.getString("productName"),myObject.getString("productCode"),
                                        myObject.getString("transactionStatus"),myObject.getString("rbl_ref_id"),myObject.getString("rbl_request")
                                ,myObject.getString("transactionMessage"));
                                infoList.add(info);
                            }
                            // call adapter class
                            if(infoList.size()>0){
                                recyclerAllTransactions = new RecyclerAllTransactions(context,infoList);
                                recycler_all_transaction.setAdapter(recyclerAllTransactions);
                            } else {
                                Toast.makeText(context, "You haven't any transaction yet!", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Authorization", str_token);
                return pars;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


}
