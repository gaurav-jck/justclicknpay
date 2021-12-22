package com.justclick.clicknbook.jctPayment.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Receipt_Activity extends AppCompatActivity {

    private Context context;
    TextView tv_date, tv_terminal, tv_aadhar, tv_agent_id, tv_rrn, tv_stan, tv_uidai, tv_txn_status, tv_txn_amount,
            tv_ac_balance, tv_resp_code, tv_resp_message;
    ProgressDialog progressDialog;
    String transaction_id;
    TextView tv_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_);
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

        Intent intent = getIntent();
        transaction_id = intent.getStringExtra("TransactionID");

        tv_type = findViewById(R.id.tv_type);
        tv_type.setText("Customer Copy - AEPS " + intent.getStringExtra("t_type"));

        // init views
        tv_date= findViewById(R.id.tv_date);
        tv_terminal= findViewById(R.id.tv_terminal);
        tv_aadhar= findViewById(R.id.tv_aadhar);
        tv_agent_id = findViewById(R.id.tv_agent_id);
        tv_rrn= findViewById(R.id.tv_rrn);
        tv_stan= findViewById(R.id.tv_stan);
        tv_uidai= findViewById(R.id.tv_uidai);
        tv_txn_status= findViewById(R.id.tv_txn_status);
        tv_txn_amount= findViewById(R.id.tv_txn_amount);
        tv_ac_balance= findViewById(R.id.tv_ac_balance);
        tv_resp_code= findViewById(R.id.tv_resp_code);
        tv_resp_message= findViewById(R.id.tv_resp_message);


        getReceiptData();


    }


    private void getReceiptData() {

        String url = URLs.RECIEPT_DATA + transaction_id;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching The Receipt Data....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String str_token = MyPreferences.getToken(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            JSONObject userJson = obj.getJSONObject("result");
                            String str_time = userJson.getString("timeStamp");
                            tv_date.setText(str_time);
                            tv_agent_id.setText(userJson.getString("agentId"));
                            tv_terminal.setText(userJson.getString("terminalId"));
                            tv_aadhar.setText(userJson.getString("aadharNumber"));
                            tv_rrn.setText(userJson.getString("referenceNumber"));
                            tv_stan.setText(userJson.getString("stanNumber"));
                            tv_uidai.setText(userJson.getString("uidaiAuthCode"));
                            tv_txn_status.setText(userJson.getString("txnStatus"));
                            tv_txn_amount.setText(userJson.getString("txnAmount"));
                            tv_ac_balance.setText(userJson.getString("accountBalance"));
                            tv_resp_code.setText(userJson.getString("transactionCode"));
                            tv_resp_message.setText(userJson.getString("transactionMessage"));

                            //if no error in response
                            //setdataToFields(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
