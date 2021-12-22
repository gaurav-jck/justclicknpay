package com.justclick.clicknbook.jctPayment.newaeps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.BankDetailsActivity;
import com.justclick.clicknbook.jctPayment.CashPayoutHistoryActivity;
import com.justclick.clicknbook.jctPayment.CashPayoutRequestActivity;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.paysprint.onboardinglib.activities.HostActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Services_Fragment_New extends Fragment implements View.OnClickListener {
    private final int CASH_OUT=0, BANK_DETAILS=1;
    private final int BAL_ENQ=0, WITHDRAW=1, MINISTMT=2, AadharPay=3;
    private Context context;
    ProgressDialog progressDialog;
    private String partnerKey = "UFMwMDY4YTEyODZiZmExZWVmYzVhNTQ1MDJjYTBhN2YxNjYwNjk=";
    private String partnerId = "PS0068";

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
        rootView.findViewById(R.id.img_adhar_pay).setOnClickListener(this);
        rootView.findViewById(R.id.img_cash_payout).setOnClickListener(this);
        rootView.findViewById(R.id.img_cash_payout_history).setOnClickListener(this);
        rootView.findViewById(R.id.img_bank_details).setOnClickListener(this);
        rootView.findViewById(R.id.img_onboard).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cash_withdrawl:
                checkKYC(WITHDRAW, URLs.CheckKyc);
//                startActivity(new Intent(getContext(), Cash_Withdrawl_Activity.class));
                break;
            case R.id.img_balance_enquiry:
                checkKYC(BAL_ENQ, URLs.CheckKyc);
//                onboardSdk();
//                openWebView("");
                /*Intent be=new Intent(getContext(),Balance_Enquiry_Activity.class);
                be.putExtra("TYPE","BE");
                startActivity(be);*/
                break;
            case R.id.img_mini_stmt:
                checkKYC(MINISTMT, URLs.CheckKyc);
                break;
            case R.id.img_adhar_pay:
                checkKYC(AadharPay, URLs.CheckKyc);
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
            case R.id.img_onboard:
                onboardSdk();
                break;
        }
    }

    public class CheckResponseClass{
        public String statusCode, statusMessage;
        public ArrayList<boardDetails> boardDetails;
        public class boardDetails{
            public String url;
        }
    }

    public static class CheckKycRequest{
        public String AgentCode, Mode="APP", Merchant= ApiConstants.MerchantId, UserType;
    }

    private void checkKYC(int TYPE, String method) {
        CheckKycRequest request=new CheckKycRequest();
        LoginModel loginModel=new LoginModel();
        request.AgentCode=MyPreferences.getLoginData(loginModel,context).Data.DoneCardUser;
        request.UserType=MyPreferences.getLoginData(loginModel,context).Data.UserType;
        String mobile=MyPreferences.getLoginData(loginModel,context).Data.Mobile;
        MyCustomDialog.showCustomDialog(context, "Please wait...");
        new NetworkCall().callAepsServiceN(request,method, context,
                (response, responseCode) -> {
                    if(response!=null){
                        try {
                            MyCustomDialog.hideCustomDialog();
                            CheckResponseClass commonResponseModel = new Gson().fromJson(response.string(), CheckResponseClass.class);
                            if(method.equals(URLs.CheckKyc)){
                                if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    if(TYPE==BAL_ENQ){
                                        Intent intent=new Intent(getContext(),Balance_Enquiry_Activity_N.class);
                                        intent.putExtra("TYPE","BE");
                                        startActivity(intent);
                                    }else if(TYPE==WITHDRAW){
                                        Intent intent=new Intent(getContext(),Cash_Withdrawl_Activity_N.class);
                                        intent.putExtra("TYPE","CW");
                                        startActivity(intent);
                                    }else if(TYPE==AadharPay){
                                        Intent intent=new Intent(getContext(),Cash_Withdrawl_Activity_N.class);
                                        intent.putExtra("TYPE","AP");
                                        startActivity(intent);
                                    }else {
                                        Intent intent=new Intent(getContext(),Balance_Enquiry_Activity_N.class);
                                        intent.putExtra("TYPE","MS");
                                        startActivity(intent);
                                    }
                                }else {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
//                                    onboardSdk();
//                                    checkKYC(TYPE, URLs.InitiateOnBoard);
                                }
                            }else {
                                if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    if(commonResponseModel.boardDetails!=null && commonResponseModel.boardDetails.size()>0)
                                    openWebView(commonResponseModel.boardDetails.get(0).url);
                                }else {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }catch (Exception e){
                            MyCustomDialog.hideCustomDialog();
                        }

                    }else {
                        MyCustomDialog.hideCustomDialog();
                        Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onboardSdk() {
        LoginModel loginModel=MyPreferences.getLoginData(new LoginModel(),context);
        Intent intent =new Intent(context, HostActivity.class);
//        Intent intent =new Intent(context, KycActivity.class);
        intent.putExtra("pId", partnerId);
        intent.putExtra("pApiKey", partnerKey);
        intent.putExtra("mCode",loginModel.Data.DoneCardUser);
//        intent.putExtra("mCode","JCG0125");
        intent.putExtra("mobile", loginModel.Data.Mobile);
//        intent.putExtra("mobile", "9012345688");
        intent.putExtra("lat", "42.10");
        intent.putExtra("lng", "76.00");
        intent.putExtra("firm", "JustClick");
        intent.putExtra("email", loginModel.Data.Email);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 999);
    }

    private void openWebView(String url) {
//        Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(context,KycActivity.class);
//        intent.putExtra("URL",url);
        startActivity(intent);

//        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        context.startActivity(browser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            if (resultCode == Activity.RESULT_OK) {
                boolean status = data.getBooleanExtra("status", false);
                int response = data.getIntExtra("response", 0);
                String message = data.getStringExtra("message");

                String detailedResponse = "Status: "+status+",  " +
                        "Response: " +response+ "Message: "+message ;

//                Toast.makeText(context, detailedResponse, Toast.LENGTH_SHORT).show();
                Snackbar.make(getView(), detailedResponse, Snackbar.LENGTH_SHORT).show();
                alertBox(detailedResponse);
//                Log.i("OnBoard: ", detailedResponse);
            }else {
                Toast.makeText(context, "Request cancelled.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void alertBox(String detailedResponse) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("KYC response");
        builder1.setMessage(detailedResponse);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Ok",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
