package com.justclick.clicknbook.jctPayment.newaeps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.Opts;
import com.justclick.clicknbook.jctPayment.Models.PidOptions;
import com.justclick.clicknbook.jctPayment.Utilities.GetAepsCredential;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.ResponseBody;

public class Cash_Withdrawl_Activity_N extends AppCompatActivity  {
    private FusedLocationProviderClient fusedLocationClient;
    private final int CASH_WITH = 1, ADHAR_PAY = 2;
    private final int CAPTURE_REQUEST_CODE = 123;
    private final int LOCATION_REQUEST_CODE = 100;
    Context context;
    private Button btn_capture, btn_submit;
    ArrayList<String> bankArray = new ArrayList<>();
    HashMap<String, String> bank_iin = new HashMap<>();
    AutoCompleteTextView atv_bank;
    private TextInputLayout aadhar_no;
    ArrayAdapter aa;
    EditText et_amount, et_mobile, et_aadhar;
    String str_b_name, str_amount, str_mobile, str_aadhar;
    private Spinner spinnerDeviceType;
    String pidDataXML;
    ProgressDialog progressDialog;
    String d_type = AepsConstants.MANTRA_L1, adharType = AepsConstants.ADHAR_UID;
    private ArrayList<String> positions;
    int TYPE = CASH_WITH;
    String URL, AuthUrl=URLs.WithdrawAuth;
    Dialog authDialog;
    private boolean isGetAgain;
//    private String MerAuthTxnId;
//    private boolean isTxnAuthenticated;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String mLatitude="29.9319558", mLongitude="77.5334789";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash__withdrawl_);
        context = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().getStringExtra("TYPE").equals("CW")) {
            TYPE = CASH_WITH;
            getSupportActionBar().setTitle(R.string.title_activity_cash_withdraw);
            URL = URLs.WithdrawCash;
        } else {
            TYPE = ADHAR_PAY;
            getSupportActionBar().setTitle(R.string.title_activity_aadhar_pay);
            URL = URLs.AadharPay;
        }

        positions = new ArrayList<>();

        progressDialog = new ProgressDialog(context);

        // init views
        atv_bank = findViewById(R.id.atv_bank);
        btn_capture = findViewById(R.id.btn_capture);
        btn_submit = findViewById(R.id.btn_submit);
        et_amount = findViewById(R.id.txt_amount);
        et_mobile = findViewById(R.id.txt_mobileno);
        et_aadhar = findViewById(R.id.txt_aadharno);
        aadhar_no = findViewById(R.id.aadhar_no);
        spinnerDeviceType = findViewById(R.id.spinnerDeviceType);
        spinnerDeviceType.setAdapter(Common.getSpinnerAdapter(AepsConstants.deviceArray, context));

        findViewById(R.id.rb_virtual_id).setVisibility(View.GONE);

        //        sessionCheckMethod(false);

        final InputFilter[] filter12 = new InputFilter[1];
        filter12[0] = new InputFilter.LengthFilter(12);

        final InputFilter[] filter16 = new InputFilter[1];
        filter16[0] = new InputFilter.LengthFilter(16);
        getBankListNew();
        hideCapture();
        et_aadhar.setFilters(filter12);

        spinnerDeviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        d_type = AepsConstants.MANTRA_L1;
                        break;
                    case 1:
                        d_type = AepsConstants.MANTRA;
                        break;
                    case 2:
                        d_type = AepsConstants.MORPHO_L1;
                        break;
                    case 3:
                        d_type = AepsConstants.MORPHO;
                        break;
                    case 4:
                        d_type = AepsConstants.STARTEK;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((RadioGroup) findViewById(R.id.radio_group_adhar)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_adhar:
                        adharType = AepsConstants.ADHAR_UID;
                        aadhar_no.setHint(getResources().getString(R.string.aadharNoHint));
                        et_aadhar.setFilters(filter12);
                        if (validation()) {
                            showCapture();
                        } else {
                            hideCapture();
                        }
                        break;
                    case R.id.rb_virtual_id:
                        adharType = AepsConstants.VIRTUAL_ID;
                        aadhar_no.setHint(getResources().getString(R.string.virtualIdHint));
                        et_aadhar.setFilters(filter16);
                        if (validation()) {
                            showCapture();
                        } else {
                            hideCapture();
                        }
                        break;
                }
            }
        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(btn_capture);
                if (!isGetAgain) {
                    GetAepsCredential.checkAepsCredential(context);
                } else {
                    captureData();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(btn_submit);
                progressDialog.setCancelable(false);
                if (validation()) {
                    checkPermissions();
//                    sessionCheckMethod(true);
                }
            }
        });
        atv_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    atv_bank.showDropDown();
                    atv_bank.setError(null);
                }
            }
        });

        atv_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atv_bank.setError(null);
                atv_bank.showDropDown();
            }
        });

        atv_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Common.hideSoftKeyboard((Cash_Withdrawl_Activity_N) context);
            }
        });

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence) || !Common.isdecimalvalid(charSequence.toString()) || Float.parseFloat(charSequence.toString()) == 0) {
                    hideCapture();
                } else {
                    if (validation()) {
                        showCapture();
                    } else {
                        hideCapture();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validation for length , text and focus
        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 10) {
                    hideCapture();
                } else {
                    et_aadhar.requestFocus();
                    if (validation()) {
                        showCapture();
                    } else {
                        hideCapture();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_mobile.getText().toString().length() < 10) {
                        et_mobile.setError("Please enter 10 digit Mobile Number");
                    }
                }
            }
        });
        et_aadhar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && adharType.equals(AepsConstants.ADHAR_UID)) {
                    if (et_aadhar.getText().toString().length() < 12) {
                        et_aadhar.setError(getResources().getString(R.string.aadharNoError));
                    }
                } else if (!hasFocus && adharType.equals(AepsConstants.VIRTUAL_ID)) {
                    if (et_aadhar.getText().toString().length() < 16) {
                        et_aadhar.setError(getResources().getString(R.string.virtualIdError));
                    }
                }
            }
        });

        et_aadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adharType.equals(AepsConstants.ADHAR_UID) && et_aadhar.getText().toString().length() < 12) {
                    hideCapture();
                } else if (adharType.equals(AepsConstants.VIRTUAL_ID) && et_aadhar.getText().toString().length() < 16) {
                    hideCapture();
                } else {
                    if (validation()) {
                        showCapture();
                    } else {
                        hideCapture();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //        location find
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();
    }

    public void captureData() {
        isGetAgain = true;
        try {
            if (d_type.equals(AepsConstants.STARTEK) && validation()) {
                if (searchPackageName(AepsConstants.STARTEK_PACKAGE)) {
//                    String pidOptXML = createPidOptXMLStartek();
                    String pidOptXML = XMLGenerator.createPidOptXML();
                    capture(AepsConstants.STARTEK_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            } else if (d_type.equals(AepsConstants.MANTRA) && validation()) {
                if (searchPackageName(AepsConstants.MANTRA_PACKAGE)) {
//                    String pidOptXML = getPIDOptions();
//                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"0\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    capture(AepsConstants.MANTRA_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            }  else if (d_type.equals(AepsConstants.MANTRA_L1) && validation()) {
                String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                capture(AepsConstants.MANTRA_L1_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
            } else if (d_type.equals(AepsConstants.MORPHO) && validation()) {
                if (searchPackageName(AepsConstants.MORPHO_PACKAGE)) {
                    String pidOptXML = XMLGenerator.createPidOptXML();  //old
//                    String pidOptXML = getPIDOptions();   // change
//                    pidOptXML="<PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" env=\"P\" wadh=\"\" posh=\"UNKNOWN\"/></PidOptions>";
                    capture(AepsConstants.MORPHO_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            }else if (d_type.equals(AepsConstants.MORPHO_L1) && validation()) {
                String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
            showMessageDialogue(getString(R.string.captureError), "Capture Error");
        }
    }

    private void hideCapture() {
        btn_capture.setEnabled(false);
        btn_capture.setTextColor(getResources().getColor(R.color.black_text_color));
        btn_capture.setBackgroundResource(R.color.gray_color);
        btn_capture.setAlpha(0.4f);
    }

    private void showCapture() {
        btn_capture.setEnabled(true);
        btn_capture.setTextColor(getResources().getColor(R.color.color_white));
        btn_capture.setBackgroundResource(R.drawable.button_shep);
        btn_capture.setAlpha(1f);
        et_amount.setError(null);
        atv_bank.setError(null);
        et_mobile.setError(null);
        et_aadhar.setError(null);
    }


    private void capture(String packageName, String pidOptXML, int requestCode) {
//        sessionCheckMethod(false);
        String selectedPackage = packageName;
        Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.CAPTURE", null);
        //String pidOptXML = getPIDOptions(); //working
        intent1.putExtra("PID_OPTIONS", pidOptXML);
        intent1.setPackage(selectedPackage);
        startActivityForResult(intent1, requestCode);
    }

    public class AepsTxnRequest {
        public String AgentCode, Mode = "APP", Merchant = ApiConstants.MerchantId, SessionKey, SessionRefNo,
                Lattitude, Longitude, DeviceId, TxnType = "AADHAAR", AadharNumber, PId,
                BankIIN, BankName, Mobile, CustomerName = "", MerAuthTxnId;
        public int Amount;
    }

    public class AepsResponse {
        public String statusCode, statusMessage;
        public ArrayList<cashWithdrawal> cashWithdrawal;

        public class cashWithdrawal {
            public String bankName, availableBalance, rrn, accountNumber, status, transactionId,
                    txnAmount, agentCode, timeStamp, jckTransactionId, apiTxnId, txnType;
        } //        {"statusCode":"01","statusMessage":"Issuer bank is inoperative","cashWithdrawal":[{"bankName":"Punjab National Bank","availableBalance":"","rrn":"112518295010","accountNumber":"XXXXXXXX2683","status":"Failed","transactionId":"MA05051WS4SJC0A13387"}]}
    }

    public class AdharPayResponse {
        public String statusCode, statusMessage;
        public ArrayList<aadaharPayDetail> aadaharPayDetail;

        public class aadaharPayDetail {
            public String bankName, availableBalance, rrn, accountNumber, status, transactionId,
                    txnAmount, agentCode, timeStamp, jckTransactionId, apiTxnId, txnType;
        } //        {"statusCode":"01","statusMessage":"Issuer bank is inoperative","cashWithdrawal":[{"bankName":"Punjab National Bank","availableBalance":"","rrn":"112518295010","accountNumber":"XXXXXXXX2683","status":"Failed","transactionId":"MA05051WS4SJC0A13387"}]}
    }

    private void sendMobileTransaction() {
        final String str_token = MyPreferences.getToken(context);
        final String bank = bank_iin.get(str_b_name);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending The Request....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            if (TYPE == CASH_WITH) {
                                AepsResponse commonResponseModel = new Gson().fromJson(response, AepsResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                                if(commonResponseModel!=null){
                                    if (commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
//                                        MerAuthTxnId=null;
//                                        openTxnAuthDialog();
                                        openReceipt(commonResponseModel);
                                    } else if(commonResponseModel.cashWithdrawal!=null){
                                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
//                                        MerAuthTxnId=null;
//                                        openTxnAuthDialog();
                                    }else {
                                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(context, "Please check transaction history before retry..", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                AdharPayResponse commonResponseModel = new Gson().fromJson(response, AdharPayResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                                if (commonResponseModel != null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                    openReceiptAadharPay(commonResponseModel);
                                } else {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.transactionExceptionBal), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.transactionErrorBal)/*+"\n"+error.getMessage()*/, Toast.LENGTH_LONG).show();
//                        logEvents(str_token,error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                //pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("UserData", MyPreferences.getUserData(context));
                pars.put("token", MyPreferences.getAepsToken(context));
                return pars;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("BankIIN", bank);
                params.put("BankName", str_b_name);
                params.put("Mobile", str_mobile);
                params.put("Amount", str_amount);
                params.put("AadharNumber", str_aadhar);
                params.put("AgentCode", MyPreferences.getLoginData(new LoginModel(), context).Data.DoneCardUser);
                params.put("Merchant", ApiConstants.MerchantId);
                params.put("MerAuthTxnId", "");
                params.put("Mode", "APP");
//                params.put("Latitude", mCurrentLocation.getLatitude() + "");
//                params.put("Longitude", mCurrentLocation.getLongitude() + "");
                params.put("Latitude", mLatitude);
                params.put("Longitude", mLongitude);
                params.put("PId", pidDataXML);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void openReceiptAadharPay(AdharPayResponse responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView cardHolderTv, agentCodeTv;
        ((TextView) dialog.findViewById(R.id.title)).setText("AEPS Receipt");
        cardHolderTv = dialog.findViewById(R.id.cardHolderTv);
        agentCodeTv = dialog.findViewById(R.id.agentCodeTv);
        TextView bankNameTv = dialog.findViewById(R.id.bankNameTv);
        TextView accountNoTv = dialog.findViewById(R.id.accountNoTv);
        TextView benIdTv = dialog.findViewById(R.id.benIdTv);
        TextView apiTxnIdTv = dialog.findViewById(R.id.apiTxnIdTv);
        TextView jckTxnIdTv = dialog.findViewById(R.id.jckTxnIdTv);
        TextView bankRefNoTv = dialog.findViewById(R.id.bankRefNoTv);
        TextView remitAmountTv = dialog.findViewById(R.id.remitAmountTv);
        TextView availBalTv = dialog.findViewById(R.id.availBalTv);
        TextView txnTypeTv = dialog.findViewById(R.id.txnTypeTv);
        TextView txnStatusTv = dialog.findViewById(R.id.txnStatusTv);
        TextView txnDateTv = dialog.findViewById(R.id.txnDateTv);

        cardHolderTv.setText("");
        AdharPayResponse.aadaharPayDetail detail = responseModel.aadaharPayDetail.get(0);
        agentCodeTv.setText(detail.agentCode);
        bankNameTv.setText(detail.bankName);
        if (detail.accountNumber != null && detail.accountNumber.length() > 6) {
            accountNoTv.setText("xxxxxxx" + detail.accountNumber.substring(detail.accountNumber.length() - 4));
        } else {
            accountNoTv.setText(detail.accountNumber);
        }
        benIdTv.setText("");
        apiTxnIdTv.setText(detail.apiTxnId);
        jckTxnIdTv.setText(detail.transactionId);
        bankRefNoTv.setText(detail.rrn);
        txnTypeTv.setText(detail.txnType);
        txnStatusTv.setText(detail.status);
        remitAmountTv.setText(detail.txnAmount + "");
        availBalTv.setText(detail.availableBalance);
        txnDateTv.setText(detail.timeStamp);
        dialog.findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openReceipt(AepsResponse responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView cardHolderTv, agentCodeTv;
        ((TextView) dialog.findViewById(R.id.title)).setText("AEPS Receipt");
        cardHolderTv = dialog.findViewById(R.id.cardHolderTv);
        agentCodeTv = dialog.findViewById(R.id.agentCodeTv);
        TextView bankNameTv = dialog.findViewById(R.id.bankNameTv);
        TextView accountNoTv = dialog.findViewById(R.id.accountNoTv);
        TextView benIdTv = dialog.findViewById(R.id.benIdTv);
        TextView apiTxnIdTv = dialog.findViewById(R.id.apiTxnIdTv);
        TextView jckTxnIdTv = dialog.findViewById(R.id.jckTxnIdTv);
        TextView bankRefNoTv = dialog.findViewById(R.id.bankRefNoTv);
        TextView remitAmountTv = dialog.findViewById(R.id.remitAmountTv);
        TextView availBalTv = dialog.findViewById(R.id.availBalTv);
        TextView txnTypeTv = dialog.findViewById(R.id.txnTypeTv);
        TextView txnStatusTv = dialog.findViewById(R.id.txnStatusTv);
        TextView txnDateTv = dialog.findViewById(R.id.txnDateTv);

        cardHolderTv.setText("xxxxxxxx" + str_aadhar.substring(str_aadhar.length() - 4));
        AepsResponse.cashWithdrawal detail = responseModel.cashWithdrawal.get(0);
        agentCodeTv.setText(detail.agentCode);
        bankNameTv.setText(detail.bankName);
        if (detail.accountNumber != null && detail.accountNumber.length() > 6) {
            accountNoTv.setText("XXXXXXXX" + detail.accountNumber.substring(detail.accountNumber.length() - 4));
        } else {
            accountNoTv.setText(detail.accountNumber);
        }
        benIdTv.setText("");
        apiTxnIdTv.setText(detail.apiTxnId);
        jckTxnIdTv.setText(detail.jckTransactionId);
        bankRefNoTv.setText(detail.rrn);
        txnTypeTv.setText(detail.txnType);
        txnStatusTv.setText(detail.status);
        remitAmountTv.setText(detail.txnAmount + "");
        availBalTv.setText(detail.availableBalance);
        txnDateTv.setText(detail.timeStamp);
        dialog.findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // validation for submit
    private boolean validation() {
        str_b_name = atv_bank.getText().toString().trim();
        str_amount = et_amount.getText().toString().trim();
        str_mobile = et_mobile.getText().toString().trim();
        str_aadhar = et_aadhar.getText().toString().trim();

        if (TextUtils.isEmpty(str_b_name)) {
            atv_bank.setError("Please enter Bank Name");
            Toast.makeText(context, "Please enter Bank Name", Toast.LENGTH_LONG).show();
//            atv_bank.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(str_amount)) {
            et_amount.setError("Please enter Amount");
//            et_amount.requestFocus();
            return false;
        } else if (!Common.isdecimalvalid(str_amount) || Float.parseFloat(str_amount) == 0) {
            et_amount.setError("Please enter valid amount");
//            et_amount.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(str_mobile)) {
            et_mobile.setError("Please enter Mobile Number");
//            et_mobile.requestFocus();
            return false;
        } else if (str_mobile.length() != 10) {
            et_mobile.setError("Please enter 10 digit Mobile Number");
//            et_mobile.requestFocus();
            return false;
        }
        if (adharType.equals(AepsConstants.ADHAR_UID) && TextUtils.isEmpty(str_aadhar)) {
            et_aadhar.setError("Please enter Aadhar Number");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(AepsConstants.ADHAR_UID) && str_aadhar.length() != 12) {
            et_aadhar.setError("Please enter 12 digit Aadhar Number");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(AepsConstants.VIRTUAL_ID) && TextUtils.isEmpty(str_aadhar)) {
            et_aadhar.setError("Please enter Virtual Id");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(AepsConstants.VIRTUAL_ID) && str_aadhar.length() != 16) {
            et_aadhar.setError("Please enter 16 digit Virtual Id");
//            et_aadhar.requestFocus();
            return false;
        }
//        if(pidDataXML==null || pidDataXML.length()==0 ){
//            Toast.makeText(context, "Please capture fingerprint before submit", Toast.LENGTH_LONG).show();
//            return false;
//        }

        return true;
    }

    // get result of pid data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case CAPTURE_REQUEST_CODE:
                try {
//                    isDataCaptured=false;
                    if (data == null) {
                        showMessageDialogue("Scan Failed/Aborted!", "Message");
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                        if (resultCode == Activity.RESULT_OK) {
                            pidDataXML = data.getStringExtra("PID_DATA");
                            if (pidDataXML != null) {
                                // xml parsing
                                readXMLData(pidDataXML);
                            } else {
                                showMessageDialogue("NULL STRING RETURNED", "Fingerprint data status");
                            }
                        } else if (resultCode == Activity.RESULT_CANCELED) {
//                    btnAuthenticate.setEnabled(false);
                            showMessageDialogue("Scan Failed/Aborted!", "CAPTURE RESULT");
                        }
                    }
                } catch (Exception ex) {
                    showMessageDialogue("Error:-" + ex.getMessage(), "EXCEPTION");
                    ex.printStackTrace();
                }
                break;
        }
    }

    private void readXMLData(String pidDataXML) {
        try {

            InputStream is = new ByteArrayInputStream(pidDataXML.getBytes("UTF-8"));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("PidData");

            Node node = nList.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;

                String s_status = element2.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errCode").getNodeValue();

                if (s_status.equals("0")) {
                    /*if(!isTxnAuthenticated && TYPE==CASH_WITH){
                        sendAuthTransaction();
                    }else {
                        showTransactionAlert();
                    }*/
                    showTransactionAlert();
                } else {
                    String s_message = element2.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getNodeValue();
                    showMessageDialogue(s_message, "Fingerprint data status");
                }
            }

        } catch (Exception e) {
            Log.e("Jobs", "Exception parse xml :" + e);
        }
    }

    //show message
    private void showMessageDialogue(String messageTxt, String argTitle) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(argTitle)
                .setMessage(messageTxt)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    // show message
    private void showTransactionAlert() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Please confirm your transaction")
                .setMessage("Do you want to do transaction with given details!")
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sendMobileTransaction();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    // serach package
    private boolean searchPackageName(String mPackageName) {

        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        boolean isPackage = false;
        for (PackageInfo pl : packageList) {
            if (pl.applicationInfo.packageName.equals(mPackageName)) {
                isPackage = true;
            }
        }

//        if (listTemp.size() <= 0) {
        if (!isPackage) {
            Toast toast = Toast.makeText(context, "Please install ` Registered Device` Service.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            Intent intentPlay = new Intent(Intent.ACTION_VIEW);
            //intentPlay.setData(Uri.parse("market://details?id=com.acpl.registersdk"));
            intentPlay.setData(Uri.parse("market://details?id=" + mPackageName));
            startActivity(intentPlay);
            return false;
        }
        return true;
    }

    //get bank list
    private void getBankListNew() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Fetching The Bank List....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.BANK_LIST_NEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
//                            JSONArray jsonArray = obj.getJSONArray("RecordData");
                            JSONObject bankList = obj.getJSONObject("banklist");
                            JSONArray jsonArray = bankList.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting the json object of the particular index inside the array
                                JSONObject myObject = jsonArray.getJSONObject(i);

                                //adding the bank to herolist
                                bankArray.add(myObject.getString("bankName"));
                                bank_iin.put(myObject.getString("bankName").trim(), myObject.getString("iinno")/*+"+"+myObject.getString("acquire_id")*/);
                            }
                            //spinner
                            aa = new ArrayAdapter(context, R.layout.item_bank_list, bankArray);
                            atv_bank.setAdapter(aa);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.bankListError), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.bankListError), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.bankListError), Toast.LENGTH_LONG).show();
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
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            mLatitude=location.getLatitude()+"";
                            mLongitude=location.getLongitude()+"";
//                            Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mLatitude=mLastLocation.getLatitude()+"";
            mLongitude=mLastLocation.getLongitude()+"";
//            Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Toast.makeText(context, "On Back press called", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
