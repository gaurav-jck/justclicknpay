package com.justclick.clicknbook.jctPayment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.Activity.MyLoginActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Utilities.SessionManager;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CashPayoutRequestActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private TextView aepsAmountTv, chargesTv, transactionTypeIMPSTv, transactionTypeNEFTTv;
    private EditText amountRequestedEdt;
    private Button submitBtn;
    private float amount;
    private final int IMPSChargesToShow=10, NEFTChargesToShow=5;
    final String IMPS="IFC", NEFT="NFT";
    private String TType=NEFT;
    private Bundle extras;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payout_request);
        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aepsAmountTv=findViewById(R.id.aepsAmountTv);
        chargesTv=findViewById(R.id.chargesTv);
        transactionTypeIMPSTv= (TextView) findViewById(R.id.transactionTypeIMPSTv);
        transactionTypeNEFTTv= (TextView) findViewById(R.id.transactionTypeNEFTTv);
        submitBtn=findViewById(R.id.submitBtn);
        amountRequestedEdt=findViewById(R.id.amountRequestedEdt);
        submitBtn.setOnClickListener(this);
        findViewById(R.id.backBtn).setOnClickListener(this);
        extras = getIntent().getExtras();
        if(extras != null) {
            amount= Common.roundOffDecimalValue(Float.parseFloat((extras.getString("amount"))));
            aepsAmountTv.setText("AEPS Amount Rs "+ amount);
        } else {
            amount= 0;
            aepsAmountTv.setText("AEPS Amount Rs "+ amount);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        transactionTypeIMPSTv.setOnClickListener(this);
        transactionTypeNEFTTv.setOnClickListener(this);
        neftClicked();
    }
    private boolean validateAmount() {
        try {
            if(amount<500){
                Toast.makeText(context, "Minimum AEPS amount should be 500 or more.", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(Float.parseFloat(amountRequestedEdt.getText().toString().trim()) < 500){
                Toast.makeText(context, "Minimum amount should be 500.", Toast.LENGTH_SHORT).show();
                return false;
            }else if(Float.parseFloat(amountRequestedEdt.getText().toString()) > amount){
                Toast.makeText(context, "Enter amount less than "+amount, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            Toast.makeText(context, "amount is empty or invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backBtn:
                finish();
                break;
            case R.id.submitBtn:
                Common.preventFrequentClick(submitBtn);
                if(validateAmount()){
//                    sessionCheckMethod();
                    progressDialog=new ProgressDialog(context);
                    progressDialog.setMessage("Sending The Request....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    getCashPayout();
                }
                break;
            case R.id.transactionTypeIMPSTv:
                impsClicked();
                break;
            case R.id.transactionTypeNEFTTv:
                neftClicked();
                break;
        }

    }

    private void neftClicked() {
        TType=NEFT;
        transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
        transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.app_blue_color));
        transactionTypeIMPSTv.setTypeface(null, Typeface.NORMAL);
        transactionTypeNEFTTv.setTypeface(null, Typeface.BOLD);
        transactionTypeNEFTTv.setBackgroundResource(R.color.app_blue_color_light);
        transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
        chargesTv.setText("Charge -> Rs "+NEFTChargesToShow+"+GST per transaction");
    }

    private void impsClicked() {
        TType=IMPS;
        transactionTypeIMPSTv.setBackgroundResource(R.color.app_blue_color_light);
        transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.color_white));
        transactionTypeIMPSTv.setTypeface(null, Typeface.BOLD);
        transactionTypeNEFTTv.setTypeface(null, Typeface.NORMAL);
        transactionTypeNEFTTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
        transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.app_blue_color));
        chargesTv.setText("Charge -> Rs "+IMPSChargesToShow+"+GST per transaction");
    }

    private void getCashPayout() {
        final String str_token = MyPreferences.getToken(context);
//        Map<String, String> postParam= new HashMap<>();
//        postParam.put("RequestedAmount", amountRequestedEdt.getText().toString().trim());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("RequestedAmount", amountRequestedEdt.getText().toString().trim());
            jsonObject.put("RequestedMethod", TType);
            jsonObject.put("DeviceId", Common.getDeviceId(context));
        }catch (JSONException e){
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URLs.createcashpayout,
                jsonObject,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                           /* {
                                "ErrorCode": "1002",
                                    "Message": "Current Limit is - 100.00"
                            }*/
                           /*{"data":true}*/
                            //converting response to json object
//                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if(obj.has("data") && obj.getBoolean("data")){
                                Toast.makeText(context, "Cash Payout Request Success", Toast.LENGTH_SHORT).show();
                                amountRequestedEdt.setText("");
                                getBankAmount();
                            }else if(obj.getString("Message")!=null){
                                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else if(obj.getString("ErrorCode")!=null && obj.getString("ErrorCode").equals("1002")){
                                Toast.makeText(context, "Error in Cash Payout", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else {
                                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, getResources().getString(R.string.aeps_response_failure), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<>();
//                pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("Content-Type", "application/json; charset=utf-8");
                pars.put("Authorization", str_token);
                return pars;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getBankAmount() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Fetching The Bank Amount....");
//        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.AepsBalance,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            String amountRemaining = new String(response);
                            amount= Common.roundOffDecimalValue(Float.parseFloat((amountRemaining)));
                            aepsAmountTv.setText("AEPS Amount Rs "+ amount);
                        } catch (Exception e) {
                            progressDialog.dismiss();
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
                }) {
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

    //session check api
    private void sessionCheckMethod() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Sending The Request....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_SESSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("StatusCode").equals("1")) {
                                getCashPayout();
                            } else {
                                Toast.makeText(getApplicationContext(), "AEPS session expire", Toast.LENGTH_LONG).show();
                                SessionManager.getInstance(context).logout();
                                Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
//                                finish();
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
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
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
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
    }
}
