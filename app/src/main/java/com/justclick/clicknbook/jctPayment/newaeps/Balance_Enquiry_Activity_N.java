package com.justclick.clicknbook.jctPayment.newaeps;

import android.Manifest;
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
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Adapters.MiniStatementAdapter;
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

public class Balance_Enquiry_Activity_N extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    //https://developers.google.com/identity/sign-in/android/sign-in
    private final String MANTRA = "MANTRA", STARTEK = "STARTEK", MORPHO = "MORPHO", ADHAR_UID = "uid", VIRTUAL_ID = "vid";
    private final String MANTRA_PACKAGE = "com.mantra.rdservice", STARTEK_PACKAGE = "com.acpl.registersdk",
            MORPHO_PACKAGE = "com.scl.rdservice";
    private final int BAL_ENQ=1,MINI_STMT=2;
    private final int CAPTURE_REQUEST_CODE = 123, FINO_AEPS_CODE=12;
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button btn_capture, btn_submit;
    ArrayList<String> bankArray = new ArrayList<>();
    HashMap<String, String> bank_iin = new HashMap<>();
    AutoCompleteTextView atv_bank;
    ArrayAdapter arrayAdapter;
    EditText et_mobile, et_aadhar;
    private TextInputLayout aadhar_no;
    String str_b_name, str_mobile, str_aadhar;
    String pidDataXML = "";
    String d_type = MANTRA, adharType = ADHAR_UID;
    int TYPE=BAL_ENQ;
    String URL;
    private boolean isGetAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance__enquiry_);
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
// Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(getIntent().getStringExtra("TYPE").equals("BE")){
            TYPE=BAL_ENQ;
            getSupportActionBar().setTitle(R.string.title_activity_balance_enquiry);
            URL =URLs.BalanceCheck;
//            URL =URLs.AepsAuthenticate;
        }else {
            TYPE=MINI_STMT;
            getSupportActionBar().setTitle(R.string.title_activity_mini_stmt);
            URL =URLs.BankStatment;
        }
        // init views
        atv_bank = findViewById(R.id.atv_bank);
        btn_capture = findViewById(R.id.btn_capture);
        btn_submit = findViewById(R.id.btn_submit);
        et_mobile = findViewById(R.id.txt_mobileno);
        et_aadhar = findViewById(R.id.txt_aadharno);
        aadhar_no = findViewById(R.id.aadhar_no);

        findViewById(R.id.rb_virtual_id).setVisibility(View.GONE);

//        sessionCheckMethod(false);

        final InputFilter[] filter12 = new InputFilter[1];
        filter12[0] = new InputFilter.LengthFilter(12);

        final InputFilter[] filter16 = new InputFilter[1];
        filter16[0] = new InputFilter.LengthFilter(16);

        getBankListNew();
        hideCapture();
        et_aadhar.setFilters(filter12);

        ((RadioGroup)findViewById(R.id.radio_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mantra:
                        d_type = MANTRA;
                        break;
                    case R.id.rb_startek:
                        d_type = STARTEK;
                        break;
                    case R.id.rb_morpho:
                        d_type = MORPHO;
                        break;
                }
            }
        });
        ((RadioGroup)findViewById(R.id.radio_group_adhar)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_adhar:
                        adharType = ADHAR_UID;
                        aadhar_no.setHint(getResources().getString(R.string.aadharNoHint));
                        et_aadhar.setFilters(filter12);
//                        showCapture();
                        if(validation()){
                            showCapture();
                        }else {
                            hideCapture();
                        }
                        break;
                    case R.id.rb_virtual_id:
                        adharType = VIRTUAL_ID;
                        aadhar_no.setHint(getResources().getString(R.string.virtualIdHint));
                        et_aadhar.setFilters(filter16);
                        if(validation()){
                            showCapture();
                        }else {
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
//                startActivity(new Intent(context, Receipt_Activity.class));
                if(!isGetAgain) {
                    GetAepsCredential.checkAepsCredential(context);
                }else {
                    captureData();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(btn_submit);
                str_b_name = atv_bank.getText().toString().trim();
                str_mobile = et_mobile.getText().toString().trim();
                str_aadhar = et_aadhar.getText().toString().trim();
                if (validation()) {
                    checkPermissions();
//                    sendMobileTransaction();
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
                Common.hideSoftKeyboard((Balance_Enquiry_Activity_N)context);
            }
        });

        //validation for length , text and focus
        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 10){
                    hideCapture();
                } else{
                    et_aadhar.requestFocus();
                    if(validation()){
                        showCapture();
                    }else {
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
                if (!hasFocus && adharType.equals(ADHAR_UID)) {
                    if (et_aadhar.getText().toString().length() < 12) {
                        et_aadhar.setError(getResources().getString(R.string.aadharNoError));
                    }
                } else if (!hasFocus && adharType.equals(VIRTUAL_ID)) {
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
                if (adharType.equals(ADHAR_UID) && et_aadhar.getText().toString().length() < 12) {
                    hideCapture();
                } else if (adharType.equals(VIRTUAL_ID) && et_aadhar.getText().toString().length() < 16) {
                    hideCapture();
                }else{
                    if(validation()){
                        showCapture();
                    }else {
                        hideCapture();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void captureData() {
        isGetAgain=true;
        try {
            if (d_type.equals(STARTEK) && validation()) {
                if (searchPackageName(STARTEK_PACKAGE)) {
//                    String pidOptXML = createPidOptXMLStartek();
                    String pidOptXML = createPidOptXML();
                    capture(STARTEK_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            } else if (d_type.equals(MANTRA) && validation()) {
                if (searchPackageName(MANTRA_PACKAGE)) {
//                    String pidOptXML = getPIDOptions();
//                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"0\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    capture(MANTRA_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            } else if (d_type.equals(MORPHO) && validation()) {
                if (searchPackageName(MORPHO_PACKAGE)) {
                    String pidOptXML = createPidOptXML();  //old
//                    String pidOptXML = getPIDOptions();   // change
//                    pidOptXML="<PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" env=\"P\" wadh=\"\" posh=\"UNKNOWN\"/></PidOptions>";
                    capture(MORPHO_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                }
            }
        } catch (Exception e) {
            showMessageDialogue("EXCEPTION- " + e.getMessage(), "EXCEPTION");
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
        atv_bank.setError(null);
        et_mobile.setError(null);
        et_aadhar.setError(null);
    }

    protected static final int REQUEST_CHECK_SETTINGS = 0x1, REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            displayLocationSettingsRequest();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionLocation = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            displayLocationSettingsRequest();
        }
    }

    private void  displayLocationSettingsRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied.
                        // You can initialize location requests here.
                        int permissionLocation = ContextCompat
                                .checkSelfPermission(context,
                                        Manifest.permission.ACCESS_FINE_LOCATION);
                        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                            mCurrentLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient);

//                            Toast.makeText(context, mCurrentLocation.getLatitude()+"",
//                                    Toast.LENGTH_SHORT).show();
//                            sessionCheckMethod(true);
//                            sendMobileTransaction();
                            if(mCurrentLocation!=null){
//                                mobileTxn();
                                sendMobileTransaction();
                            }else {
                                Toast.makeText(context, "Please fetch your current location from google map.",Toast.LENGTH_LONG).show();
                                sendMobileTransaction();
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied.
                        // But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            // Ask to turn on GPS automatically
                            status.startResolutionForResult(Balance_Enquiry_Activity_N.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied.
                        // However, we have no way
                        // to fix the
                        // settings so we won't show the dialog.
                        // finish();
                        break;
                }
            }
        });
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

    private void logEvents(String str_token, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, MyPreferences.getLoginId(context));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, str_token);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BALANCE_ENQUIRY");
        bundle.putString(FirebaseAnalytics.Param.CONTENT, message);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public class AepsTxnRequest{
        public String AgentCode,Mode="APP", Merchant=ApiConstants.MerchantId,SessionKey,SessionRefNo,
                Lattitude,Longitude,DeviceId,TxnType="AADHAAR",AadharNumber,PId,
                BankIIN, BankName, Mobile, CustomerName="";
    }

    public class AepsResponse{
        public String statusCode,statusMessage, balanceAmount;
        public ArrayList<balEnQDetails> balEnqDetails;
        public class balEnQDetails{
            public String bankName, availableBalance, rrn, accountNumber,status,transactionId,
                    txnAmount,agentCode,timeStamp, jckTransactionId, apiTxnId,txnType;
        }
        //{"statusCode":"00","statusMessage":"Success","balEnQDetails":[{"bankName":"India Post Payment Bank","":"JC0A13387","availableBalance":"306.2","rrn":"113119689889","txnAmount":"00","":"BalanceEnquiry","":"5/11/2021 7:39:51 PM","accountNumber":"XXXXXXXX5016","status":"SUCCESS","":"MA11051TUEKJC0A13387","":"130707547032"}]}
    }
    public class AepsMiniResponse{
        public String statusCode,statusMessage;
        public ArrayList<msDetails> msDetails;
        public class msDetails{
            public String date, txnType, amount,narration;
        }
//        { "statusCode": "00", "statusMessage": "Success", "msDetails": [{ "date": "06/07/2021", "txnType": "Dr", "amount": "350.00", "narration": " UPI/11873111709" }
    }

    public void mobileTxn(){
        AepsTxnRequest request=new AepsTxnRequest();
        LoginModel loginModel=new LoginModel();
        request.AgentCode=MyPreferences.getLoginData(loginModel,context).Data.DoneCardUser;
        request.SessionKey=MyPreferences.getSessionKey(context);
        request.SessionRefNo=MyPreferences.getSessionRefNo(context);
        request.Lattitude=mCurrentLocation.getLatitude() + "";
        request.Longitude=mCurrentLocation.getLongitude() + "";
        request.DeviceId=Common.getDeviceId(context);
        request.AadharNumber=str_aadhar;
        request.Mobile=str_mobile;
        request.BankName=str_b_name;
        request.BankIIN=bank_iin.get(str_b_name);
        if(d_type.equals(MORPHO) || d_type.equals(STARTEK)){
            request.PId=Base64.encodeToString(pidDataXML.
                    getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).replace("\n","");
        }else {
            request.PId=Base64.encodeToString(("<?xml version=\"1.0\"?>"+pidDataXML).
                    getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).replace("\n","");
        }
        isGetAgain=false;
        new NetworkCall().callAepsServiceHeaderNew(request, URL, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        MyCustomDialog.hideCustomDialog();
                        if(response!=null){
                            try {
//                                Toast.makeText(context, "response", Toast.LENGTH_SHORT).show();
                                AepsResponse commonResponseModel = new Gson().fromJson(response.string(), AepsResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                                if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                    openReceipt(commonResponseModel);
                                }else {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){

                            }
                        }else {
                            Toast.makeText(context, "no response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },MyPreferences.getUserData(context),MyPreferences.getAepsToken(context));
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
                            if(TYPE==BAL_ENQ){
                                AepsResponse commonResponseModel = new Gson().fromJson(response, AepsResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                                if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                    openReceipt(commonResponseModel);
                                }else {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                AepsMiniResponse commonResponseModel = new Gson().fromJson(response, AepsMiniResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                                if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                    Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                                    if(commonResponseModel.msDetails!=null && commonResponseModel.msDetails.size()>0) {
                                        openMiniStatement(commonResponseModel);
                                    }else {
                                        Toast.makeText(context, "No transaction is showing for this account.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
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
                        logEvents(str_token,error.getMessage());
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
                params.put("AadharNumber", str_aadhar);
                params.put("AgentCode",MyPreferences.getLoginData(new LoginModel(),context).Data.DoneCardUser );
                params.put("Merchant",ApiConstants.MerchantId);
                params.put("Mode","APP");
//                params.put("Latitude", mCurrentLocation.getLatitude() + "");
//                params.put("Longitude", mCurrentLocation.getLongitude() + "");
                params.put("Latitude", 28.70 + "");
                params.put("Longitude", 77.11 + "");

                /*params.put("AgentCode", MyPreferences.getLoginData(new LoginModel(), context).Data.DoneCardUser);
                params.put("Mobile", MyPreferences.getLoginData(new LoginModel(), context).Data.Mobile);
                params.put("Merchant", ApiConstants.MerchantId);
                params.put("Mode", "APP");
//                params.put("Latitude", mCurrentLocation.getLatitude() + "");
//                params.put("Longitude", mCurrentLocation.getLongitude() + "");
                params.put("Latitude", 28.70 + "");
                params.put("Longitude", 77.1 + "");
                params.put("PId", pidDataXML);*/
                if(d_type.equals(MORPHO) || d_type.equals(STARTEK)){
                    params.put("PId", pidDataXML.replace("\n",""));  //.replace("\n","")
                }else {
                    params.put("PId", ("<?xml version=\"1.0\"?>"+pidDataXML).replace("\n",""));
                }
                /*if(d_type.equals(MORPHO)){
                    params.put("PId", Base64.encodeToString(pidDataXML.
                            getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).replace("\n",""));
                }else {
                    params.put("PId", Base64.encodeToString(("<?xml version=\"1.0\"?>"+pidDataXML).
                            getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).replace("\n",""));
                }*/
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void openMiniStatement(AepsMiniResponse responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mini_stmt_receipt_dialog);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        dialog.findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        RecyclerView recyclerView=dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(new MiniStatementAdapter(context, responseModel.msDetails));

        /*if(responseModel.msDetails!=null && responseModel.msDetails.size()>0){
            Toast.makeText(context, responseModel.msDetails.size()+"\n"+responseModel.msDetails.get(0).amount, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "No mini statement found for given data.", Toast.LENGTH_SHORT).show();
        }*/
        dialog.show();
    }

    private void openReceipt(AepsResponse responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView cardHolderTv,agentCodeTv;
        ((TextView)dialog.findViewById(R.id.title)).setText("AEPS Receipt");
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
        LinearLayout cardNameLin = dialog.findViewById(R.id.cardNameLin);
        cardNameLin.setVisibility(View.GONE);
//        cardHolderTv.setText("xxxxxxxx"+str_aadhar.substring(str_aadhar.length()-4));
        AepsResponse.balEnQDetails detail=responseModel.balEnqDetails.get(0);
        agentCodeTv.setText(detail.agentCode);
        bankNameTv.setText( detail.bankName);
        if(detail.accountNumber!=null && detail.accountNumber.length()>6){
            accountNoTv.setText("XXXXXXXX"+detail.accountNumber.substring(detail.accountNumber.length()-4));
        }else{
            accountNoTv.setText(detail.accountNumber);
        }
//        benIdTv.setText(detail.transactionId);
        apiTxnIdTv.setText( detail.apiTxnId);
        jckTxnIdTv.setText( detail.jckTransactionId);
        bankRefNoTv.setText( detail.rrn);
        txnTypeTv.setText(detail.txnType);
        txnStatusTv.setText( detail.status);
        remitAmountTv.setText( detail.txnAmount+"");
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
        str_mobile = et_mobile.getText().toString().trim();
        str_aadhar = et_aadhar.getText().toString().trim();

        if (TextUtils.isEmpty(str_b_name)) {
            atv_bank.setError("Please enter Bank Name");
//            atv_bank.requestFocus();
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
        if (adharType.equals(ADHAR_UID) && TextUtils.isEmpty(str_aadhar)) {
            et_aadhar.setError("Please enter Aadhar Number");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(ADHAR_UID) && str_aadhar.length() != 12) {
            et_aadhar.setError("Please enter 12 digit Aadhar Number");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(VIRTUAL_ID) && TextUtils.isEmpty(str_aadhar)) {
            et_aadhar.setError("Please enter Virtual Id");
//            et_aadhar.requestFocus();
            return false;
        } else if (adharType.equals(VIRTUAL_ID) && str_aadhar.length() != 16) {
            et_aadhar.setError("Please enter 16 digit Virtual Id");
//            et_aadhar.requestFocus();
            return false;
        }
        /*if (pidDataXML == null || pidDataXML.length() == 0) {
            Toast.makeText(this, "Please capture fingerprint before submit", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }

    // pid data xml for startek
    private String createPidOptXML() {
        String tmpOptXml = "";
        try {
            String fCount = "1";
//            String fType = "0";  //old
            String fType = "2";
            String iCount = "0";
            String iType = "0";
            String pCount = "0";
            String pType = "0";
//            String format = "1"; // old
            String format = "0";
            String pidVer = "2.0";
            String timeout = "20000";
            String otp = "";
//            String env = "PP";   //uat
            String env = "P";   //live
            String wath = "";
            String posh = "UNKNOWN";

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = null;

            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);

            Element rootElement = doc.createElement("PidOptions");
            doc.appendChild(rootElement);

            Element opts = doc.createElement("Opts");
            rootElement.appendChild(opts);

            Attr attr = doc.createAttribute("fCount");
            //attr.setValue(String.valueOf(fCountSel.getSelectedItem().toString()));
            attr.setValue(fCount);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("fType");
            attr.setValue(fType);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("iCount");
            attr.setValue(iCount);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("iType");
            attr.setValue(iType); //change
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pCount");
            attr.setValue(pCount);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pType");
            attr.setValue(pType); //change
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("format");
            attr.setValue(format);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pidVer");
            attr.setValue(pidVer);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("timeout");
            attr.setValue(timeout);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("otp");
            attr.setValue(otp);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("env");
            attr.setValue(env);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("wadh");
            //attr.setValue("ONLY USE FOR E-KYC.");
            attr.setValue(wath);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("posh");
            attr.setValue(posh);
            opts.setAttributeNode(attr);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            tmpOptXml = writer.getBuffer().toString().replaceAll("\n|\r", "");
            tmpOptXml = tmpOptXml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");

            return tmpOptXml;
        } catch (Exception ex) {
            showMessageDialogue("EXCEPTION- " + ex.getMessage(), "EXCEPTION");
            return "";
        }
    }

    // pid data xml
    private String createPidOptXMLStartek() {
        String tmpOptXml = "";
        try{
            String fTypeStr = "0";
            String timeOutStr = "20000";
            //            uat
            String formatStr = "1";
//            String envStr = "PP";
//            live
            String envStr = "P";


            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = null;

            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);

            Element rootElement = doc.createElement("PidOptions");
            doc.appendChild(rootElement);

            Element opts = doc.createElement("Opts");
            rootElement.appendChild(opts);

            Attr attr = doc.createAttribute("fCount");
            //attr.setValue(String.valueOf(fCountSel.getSelectedItem().toString()));
            attr.setValue("1");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("fType");
            attr.setValue(fTypeStr);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("iCount");
            attr.setValue("0");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("iType");
//            attr.setValue("");    // before
            attr.setValue("0");     // after
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pCount");
            attr.setValue("0");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pType");
//            attr.setValue("");    // before
            attr.setValue("0");    // after
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("format");
            attr.setValue(formatStr);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pidVer");
            attr.setValue("2.0");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("timeout");
            attr.setValue(timeOutStr);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("otp");
            attr.setValue("");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("env");
            attr.setValue(envStr);
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("wadh");
            attr.setValue("");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("posh");
            attr.setValue("UNKNOWN");
            opts.setAttributeNode(attr);

            Element demo = doc.createElement("Demo");
            demo.setTextContent("");
            rootElement.appendChild(demo);

            Element custotp = doc.createElement("CustOpts");
            rootElement.appendChild(custotp);

            Element param = doc.createElement("Param");
            custotp.appendChild(param);


            attr = doc.createAttribute("name");
            attr.setValue("ValidationKey");
            param.setAttributeNode(attr);

            attr = doc.createAttribute("value");
            attr.setValue("ONLY USE FOR LOCKED DEVICES.");
            param.setAttributeNode(attr);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            tmpOptXml = writer.getBuffer().toString().replaceAll("\n|\r", "");
            tmpOptXml = tmpOptXml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");

            return tmpOptXml;
        }catch(Exception ex){
            showMessageDialogue("EXCEPTION- " + ex.getMessage(),"EXCEPTION");
            return "";
        }
    }

    // pid data xml for mantra
    private String getPIDOptions() {
        try {

            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "0";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = "1";
            opts.pidVer = "2.0";
            opts.timeout = "10000";
            opts.otp = "";
            opts.wadh = "";
            opts.posh = "UNKNOWN";
            opts.env = "P";     // live
//            opts.env = "PP";      // uat

            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;

            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }

    // get result of pid data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
//                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
            case CAPTURE_REQUEST_CODE:
                try {
//                    isDataCaptured=false;
//                    showMessageDialogue("OnActivity result.  resultcode="+resultCode+" Message="+data.getData().getUserInfo(), "Message");
                    if (data == null) {
                        showMessageDialogue("Scan Failed/Aborted!", "Message");
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                        if (resultCode == Activity.RESULT_OK) {

                            if (requestCode == CAPTURE_REQUEST_CODE) {
                                pidDataXML = data.getStringExtra("PID_DATA");
                                if (pidDataXML != null) {
                                    // xml parsing
                                    readXMLData(pidDataXML);
                                } else {
                                    showMessageDialogue("NULL STRING RETURNED", "Fingerprint data status");
                                }
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

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
//                setButtonsEnabledState();
            }
        });
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
//                    isDataCaptured=true;
                    showTransactionAlert();
//                    showMessageDialogue("Data captured", "Fingerprint data status");
                } else {
                    String s_message = element2.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getNodeValue();
                    showMessageDialogue(s_message, "Fingerprint data status");
                }
            }

        } catch (Exception e) {
            Log.e("Jobs", "Exception parse xml :" + e);
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // show message
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
//                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show();
//                        checkPermissions();
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
//        PackageManager pm = this.getPackageManager();
        //String packageName = "com.acpl.registersdk";
//        String packageName = "com.mantra.rdservice";

        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        boolean isPackage=false;
        for(PackageInfo pl: packageList){
            if(pl.applicationInfo.packageName.equals(mPackageName)){
                isPackage=true;
            }
        }

//        String packageName = mPackageName;
//        Intent intent = new Intent();
//        intent.setPackage(packageName);
//        List listTemp = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);

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
        final ProgressDialog progressDialog = new ProgressDialog(context);
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
//                            JSONArray jsonArray = bankList.getJSONArray("RecordData");  //old
                            JSONObject bankList = obj.getJSONObject("banklist");
                            JSONArray jsonArray = bankList.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting the json object of the particular index inside the array
                                JSONObject myObject = jsonArray.getJSONObject(i);

                                //adding the bank to herolist
                                bankArray.add(myObject.getString("bankName").trim());
                                bank_iin.put(myObject.getString("bankName").trim(),myObject.getString("iinno")/*+"+"+myObject.getString("acquire_id")*/);
                            }
                            //spinner
                            arrayAdapter = new ArrayAdapter(context, R.layout.item_bank_list, bankArray);
                            atv_bank.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.bankListError), Toast.LENGTH_LONG).show();
                        }catch (Exception e) {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mCurrentLocation!=null){
//            Toast.makeText(context, mCurrentLocation.getLatitude()+"",
//                    Toast.LENGTH_SHORT).show();
            }
//            updateLocationUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mCurrentLocation=location;
                        }
                    }
                });
    }


    private String miniResponse="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Success\",\n" +
            "    \"msDetails\": [\n" +
            "        {\n" +
            "            \"date\": \"06/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"       350.00\",\n" +
            "            \"narration\": \" UPI/11873111709\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"05/07/2021\",\n" +
            "            \"txnType\": \"Cr\",\n" +
            "            \"amount\": \"       290.00\",\n" +
            "            \"narration\": \" UPI/11868230357\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"05/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"       200.00\",\n" +
            "            \"narration\": \" UPI/11857992248\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"05/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"       200.00\",\n" +
            "            \"narration\": \" UPI/11850495756\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"05/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"      6000.00\",\n" +
            "            \"narration\": \" MMT/IMPS/118515\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"05/07/2021\",\n" +
            "            \"txnType\": \"Cr\",\n" +
            "            \"amount\": \"      3500.00\",\n" +
            "            \"narration\": \" UPI/11851892617\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"02/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"        50.00\",\n" +
            "            \"narration\": \" AEP/Cash Wdl/02\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"02/07/2021\",\n" +
            "            \"txnType\": \"Dr\",\n" +
            "            \"amount\": \"      7423.00\",\n" +
            "            \"narration\": \" ACH/TPCAPFRST I\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"01/07/2021\",\n" +
            "            \"txnType\": \"Cr\",\n" +
            "            \"amount\": \"     10000.00\",\n" +
            "            \"narration\": \" MMT/IMPS/118222\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"date\": \"30/06/2021\",\n" +
            "            \"txnType\": \"Cr\",\n" +
            "            \"amount\": \"        42.00\",\n" +
            "            \"narration\": \" 135301507812:In\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
