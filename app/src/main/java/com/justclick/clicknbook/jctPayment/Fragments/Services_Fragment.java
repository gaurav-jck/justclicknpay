package com.justclick.clicknbook.jctPayment.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Balance_Enquiry_Activity;
import com.justclick.clicknbook.jctPayment.BankDetailsActivity;
import com.justclick.clicknbook.jctPayment.CashPayoutHistoryActivity;
import com.justclick.clicknbook.jctPayment.CashPayoutRequestActivity;
import com.justclick.clicknbook.jctPayment.Cash_Withdrawl_Activity;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.jctPayment.fino.FinoConstants;
import com.justclick.clicknbook.jctPayment.fino.FinoUtils;
import com.justclick.clicknbook.jctPayment.fino.finomvvm.view.FinoCashWithdrawActivity;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;

public class Services_Fragment extends Fragment implements View.OnClickListener {
    private final int CASH_OUT=0, BANK_DETAILS=1;
    private Context context;
    ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_services_, container, false);

        rootView.findViewById(R.id.img_cash_withdrawl).setOnClickListener(this);
        rootView.findViewById(R.id.img_balance_enquiry).setOnClickListener(this);
        rootView.findViewById(R.id.img_mini_stmt).setOnClickListener(this);
        rootView.findViewById(R.id.img_cash_payout).setOnClickListener(this);
        rootView.findViewById(R.id.img_cash_payout_history).setOnClickListener(this);
        rootView.findViewById(R.id.img_bank_details).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cash_withdrawl:
                startActivity(new Intent(getContext(), Cash_Withdrawl_Activity.class));
                break;
            case R.id.img_balance_enquiry:
                Intent be=new Intent(getContext(),Balance_Enquiry_Activity.class);
                be.putExtra("TYPE","BE");
                startActivity(be);
                break;
            case R.id.img_mini_stmt:
                Intent ms=new Intent(getContext(),Balance_Enquiry_Activity.class);
                ms.putExtra("TYPE","MS");
                startActivity(ms);
                break;
            case R.id.img_cash_payout:
                getBankDetailAndAmount(CASH_OUT);
                break;
            case R.id.img_cash_payout_history:
                startActivity(new Intent(getContext(), CashPayoutHistoryActivity.class));
                break;
            case R.id.img_bank_details:
                getBankDetailAndAmount(BANK_DETAILS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String response;
        if (data != null & resultCode == RESULT_OK && requestCode == 1)
        {

            /*if (data.hasExtra("ClientResponse")) {//This is for encrypted client response from finosdk in case of SUCCESS
                response = data.getStringExtra("ClientResponse");
                String strDecryptResponse = AES_BC.getInstance().decryptDecode(Utils.replaceNewLine(response), FinoConstants.CLIENT_REQUEST_ENCRYPTION_KEY);
                Utils.showOneBtnDialog(context, getString(com.finopaytech.finosdk.R.string.STR_INFO), strDecryptResponse, false);
            } else if (data.hasExtra("ErrorDtls"))
            {//This is plain text error in case of FAILED
                response = data.getStringExtra("ErrorDtls");
                String errorMsg = "", errorDtlsMsg = "";
                if (!response.equalsIgnoreCase("")) {
                    try {
                        String[] error_dtls = response.split("\\|");
                        if (error_dtls.length > 0) {
                            errorMsg = error_dtls[0];
                            Utils.showOneBtnDialog(context, getString(com.finopaytech.finosdk.R.string.STR_INFO), "Error Message : " + errorMsg, false);
                        }
                    } catch (ArrayIndexOutOfBoundsException exp) {
                    }
                }
            }

            ErrorSingletone.getFreshInstance();*/
        }
        else if (data != null & resultCode == RESULT_OK && requestCode == 2)
        {
            if (data.hasExtra("DeviceConnectionDtls"))
            {
                response = data.getStringExtra("DeviceConnectionDtls");
                String status = "", status_dtls = "";
                if (!response.equalsIgnoreCase(""))
                {
                    try {
                        String[] error_dtls = response.split("\\|");
                        if (error_dtls.length > 1)
                        {
                            status = error_dtls[0];
                            status_dtls = error_dtls[1];
//                            Utils.showOneBtnDialog(context, getString(com.finopaytech.finosdk.R.string.STR_INFO), "Status : " + status +"\n"+"Details : "+status_dtls, false);
                        }
                    } catch (ArrayIndexOutOfBoundsException exp) {
                    }
                }
            }
        }
    }

    private void getBankDetailAndAmount(final int TYPE) {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Fetching The Bank Detail....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.hasbankdetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            if(obj!=null){
                                if(obj.has("status")){
                                    try {
                                        progressDialog.dismiss();
                                        JSONObject jsonObject=obj.getJSONObject("status");
                                        if(TYPE==CASH_OUT) {
                                            if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("1")) {
                                                getBankAmount();
                                            } else if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("0")) {
                                                Toast.makeText(context, "Bank details are not approved.",
                                                        Toast.LENGTH_LONG).show();
                                            } else if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("2")) {
                                                if(jsonObject.has("rejection_reason") &&
                                                        jsonObject.getString("rejection_reason").length()>0){
                                                    Toast.makeText(context, "Your Bank Details are rejected due to : "+jsonObject.getString("rejection_reason"),
                                                            Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(context, "Your Bank Details are rejected due to Insufficient or Incorrect bank details.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Bank details not found.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }else {
                                            if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("1")) {
                                                showBankDetails(jsonObject);
                                            } else if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("0")) {
                                                Toast.makeText(context, "Bank details are not approved.", Toast.LENGTH_LONG).show();
                                                showBankDetails(jsonObject);
                                            } else if (jsonObject.has("verified") &&
                                                    jsonObject.getString("verified").equals("2")) {
                                                if(jsonObject.has("rejection_reason") &&
                                                        jsonObject.getString("rejection_reason").length()>0){
                                                    Toast.makeText(context, "Your Bank Details are rejected due to : "+jsonObject.getString("rejection_reason"),
                                                            Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(context, "Your Bank Details are rejected due to Insufficient or Incorrect bank details.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                startActivity(new Intent(context, BankDetailsActivity.class).putExtra("hasBankDetail", false)
                                                        .putExtra("verified",jsonObject.getString("verified")));
                                            } else {
                                                Toast.makeText(context, "Bank details not found.",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(context, BankDetailsActivity.class).putExtra("hasBankDetail", false));
                                            }
                                        }
                                    }catch (JSONException e){
                                        try {
                                            progressDialog.dismiss();
                                            if(obj.has("status") && obj.getInt("status")==4){
                                                if(TYPE==BANK_DETAILS){
                                                    startActivity(new Intent(context, BankDetailsActivity.class).putExtra("hasBankDetail", false));
                                                }else {
                                                    Toast.makeText(context, "Please submit your bank details to proceed.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }catch (JSONException e1){
                                            Toast.makeText(context, "Bank details not found.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Bank details not found.", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                            }
//                            Toast.makeText(context, obj.getJSONObject("status").getString("name"),
//                                    Toast.LENGTH_LONG).show();
                            /*{
    "status": {
        "id": 117,
        "FK_userMasterId": 1071,
        "account": "1111111111",
        "name": "Abhyudaya Cooperative Bank Limited",
        "path": "JC0A13387_1539345469_Screenshot from 2018-10-11 14-32-46.png",
        "ifsc": "SBIN0123456",
        "beneficiary_name": "Ankur",
        "verified": "0",
        "verified_by": 0,
        "verified_on": "2018-10-12 17:27:49",
        "deleted_at": null,
        "created_at": "2018-10-12 17:27:49",
        "updated_at": "2018-10-12 17:27:49"
    }
}*/
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.aeps_response_failure), Toast.LENGTH_LONG).show();
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

    private void showBankDetails(JSONObject jsonObject) throws JSONException {
        String verified=jsonObject.getString("verified");
        String account=jsonObject.getString("account");
        String name=jsonObject.getString("name");
        String path=jsonObject.getString("path");
        String ifsc=jsonObject.getString("ifsc");
        String beneficiary_name=jsonObject.getString("beneficiary_name");
        startActivity(new Intent(context, BankDetailsActivity.class).putExtra("account",account)
                .putExtra("name",name)
                .putExtra("path", path)
                .putExtra("ifsc", ifsc)
                .putExtra("beneficiary_name",beneficiary_name)
                .putExtra("hasBankDetail", true));
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
                            String amount = new String(response);
                            startActivity(new Intent(getContext(), CashPayoutRequestActivity.class)
                                    .putExtra("amount",amount));
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
                        Toast.makeText(context, getResources().getString(R.string.aeps_response_failure), Toast.LENGTH_LONG).show();
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
}
