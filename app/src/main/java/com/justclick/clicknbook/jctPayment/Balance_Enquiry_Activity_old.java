package com.justclick.clicknbook.jctPayment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.MyLoginActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Activities.Receipt_Activity;
import com.justclick.clicknbook.jctPayment.Models.Opts;
import com.justclick.clicknbook.jctPayment.Models.PidOptions;
import com.justclick.clicknbook.jctPayment.Utilities.GetAepsCredential;
import com.justclick.clicknbook.jctPayment.Utilities.SessionManager;
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

public class Balance_Enquiry_Activity_old extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
//https://developers.google.com/identity/sign-in/android/sign-in
    private final String MANTRA = "MANTRA", STARTEK = "STARTEK", MORPHO = "MORPHO", ADHAR_UID = "uid", VIRTUAL_ID = "vid";
    private final String MANTRA_PACKAGE = "com.mantra.rdservice", STARTEK_PACKAGE = "com.acpl.registersdk",
            MORPHO_PACKAGE = "com.scl.rdservice";
    private final int BAL_ENQ=1,MINI_STMT=2;
    private final int CAPTURE_REQUEST_CODE = 123, FINO_AEPS_CODE=12;
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
    String method;
    private boolean isGetAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance__enquiry_);
        context = this;
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

        // init views
        atv_bank = findViewById(R.id.atv_bank);
        btn_capture = findViewById(R.id.btn_capture);
        btn_submit = findViewById(R.id.btn_submit);
        et_mobile = findViewById(R.id.txt_mobileno);
        et_aadhar = findViewById(R.id.txt_aadharno);
        aadhar_no = findViewById(R.id.aadhar_no);

//        sessionCheckMethod(false);

        final InputFilter[] filter12 = new InputFilter[1];
        filter12[0] = new InputFilter.LengthFilter(12);

        final InputFilter[] filter16 = new InputFilter[1];
        filter16[0] = new InputFilter.LengthFilter(16);

        getBankList();
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
                try {
                    if (d_type.equals(STARTEK) && validation()) {
                        if (searchPackageName(STARTEK_PACKAGE)) {
                            String pidOptXML = createPidOptXML();
                            capture(STARTEK_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                        }
                    } else if (d_type.equals(MANTRA) && validation()) {
                        if (searchPackageName(MANTRA_PACKAGE)) {
                            String pidOptXML = getPIDOptions();
                            capture(MANTRA_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                        }
                    } else if (d_type.equals(MORPHO) && validation()) {
                        if (searchPackageName(MORPHO_PACKAGE)) {
                            String pidOptXML = createPidOptXML();
                            capture(MORPHO_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE);
                        }
                    }
                } catch (Exception e) {
                    showMessageDialogue("EXCEPTION- " + e.getMessage(), "EXCEPTION");
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
                Common.hideSoftKeyboard((Balance_Enquiry_Activity_old)context);
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
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
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
                            sendMobileTransaction();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied.
                        // But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            // Ask to turn on GPS automatically
                            status.startResolutionForResult(Balance_Enquiry_Activity_old.this,
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

    //session check api
    private void sessionCheckMethod(final boolean isTxn) {
        final String str_token = MyPreferences.getToken(context);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing The Request....");
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
//                            if (obj.getBoolean("status")) {
                                if (isTxn) {
                                    sendMobileTransaction();
                                }
                            } else {
//                                Toast.makeText(context, "AEPS session expire", Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, obj.getString("Status"), Toast.LENGTH_SHORT).show();
                                SessionManager.getInstance(context).logout();
                                Intent intent = new Intent(context, MyLoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
//                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "AEPS session expire", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.server_errorBal), Toast.LENGTH_LONG).show();

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

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void logEvents(String str_token, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, MyPreferences.getLoginId(context));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, str_token);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BALANCE_ENQUIRY");
        bundle.putString(FirebaseAnalytics.Param.CONTENT, message);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void sendMobileTransaction() {
        final String str_token = MyPreferences.getToken(context);
        final String bank = bank_iin.get(str_b_name);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending The Request....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MOBILE_TRANSACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
//                            if (obj.getString("status").equalsIgnoreCase("true")) {
                            if (obj.getBoolean("status")) {
                                // send to receipt form
                                if (obj.getString("responseCode").equals("00")) {
                                    Intent intent = new Intent(context, Receipt_Activity.class);
                                    intent.putExtra("TransactionID", obj.getString("aepsTransactionLogId"));
                                    intent.putExtra("t_type", "Balance Enquiry");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(context, obj.getString("message") + " Please retry..", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                    Toast.makeText(context, obj.getString("message") + " Please retry..", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
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
                pars.put("Authorization", str_token);
                return pars;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bank", bank);
                params.put("mobile", str_mobile);
//                params.put("aadhaar", EncryptionDecryptionClass.opensslEncrypt(str_aadhar));
//                params.put("virtualid", EncryptionDecryptionClass.opensslEncrypt(str_aadhar));
                params.put("aadhaar", str_aadhar);
                params.put("virtualid", str_aadhar);
                params.put("uidVidOption", adharType);
                params.put("Latitude", mCurrentLocation.getLatitude() + "");
                params.put("Longitude", mCurrentLocation.getLongitude() + "");
                params.put("deviceType", d_type);
                params.put("transType", "310000");
//                params.put("nextFreshnessFactor", str_freshness);
//                params.put("biometricData", Base64.encodeToString(pidDataXML.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
                params.put("biometricData", pidDataXML);
                params.put("nextFreshnessFactor", "1234");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
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
            attr.setValue("0"); //change
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pCount");
            attr.setValue("0");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pType");
            attr.setValue("0"); //change
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
            //attr.setValue("ONLY USE FOR E-KYC.");
            attr.setValue("");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("posh");
            attr.setValue("UNKNOWN");
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
                        checkPermissions();
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
        PackageManager pm = this.getPackageManager();
        //String packageName = "com.acpl.registersdk";
//        String packageName = "com.mantra.rdservice";
        String packageName = mPackageName;
        Intent intent = new Intent();
        intent.setPackage(packageName);
        List listTemp = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);

        if (listTemp.size() <= 0) {
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

    private void getBankList() {
        final String str_token = MyPreferences.getToken(context);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching The Bank List....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.BANK_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            JSONArray jsonArray = obj.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject myObject = jsonArray.getJSONObject(i);

                                bankArray.add(myObject.getString("bank_name"));
                                bank_iin.put(myObject.getString("bank_name"), myObject.getString("bank_iin")+"+"+myObject.getString("acquire_id"));
                            }
                            //spinner
                            arrayAdapter = new ArrayAdapter(context, R.layout.item_bank_list, bankArray);
                            atv_bank.setAdapter(arrayAdapter);
                            logEvents(str_token,"BANK LIST");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.bankListErrorBal), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.bankListErrorBal), Toast.LENGTH_SHORT).show();
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
}
