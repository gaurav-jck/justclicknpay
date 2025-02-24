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
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.jctPayment.Models.GetAadharRequest;
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.GetAadharResponse;
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

public class AepsRegistrationActivity extends AppCompatActivity{
    //https://developers.google.com/identity/sign-in/android/sign-in
    private final int BAL_ENQ = 1;
    private final int CAPTURE_REQUEST_CODE = 123, FINO_AEPS_CODE = 12;
    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button btn_capture, btn_submit;
    EditText et_aadhar, txt_mobileno;
    private Spinner spinnerDeviceType;
    private TextInputLayout aadhar_no;
    String str_aadhar, mobileNo;
    String pidDataXML = "";
    String d_type = AepsConstants.MANTRA_L1, adharType = AepsConstants.ADHAR_UID;
    int TYPE = BAL_ENQ;
    String URL;
    private boolean isGetAgain;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String mLatitude="29.9319558", mLongitude="77.5334789";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_registration);
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

        URL = URLs.AepsRegister;
//        URL =URLs.AepsAuthenticate;

        // init views
        btn_capture = findViewById(R.id.btn_capture);
        btn_submit = findViewById(R.id.btn_register);
        et_aadhar = findViewById(R.id.txt_aadharno);
        txt_mobileno = findViewById(R.id.txt_mobileno);
        aadhar_no = findViewById(R.id.aadhar_no);

        findViewById(R.id.rb_virtual_id).setVisibility(View.GONE);
        et_aadhar.setText(MyPreferences.getAgentAdhar(context));
        txt_mobileno.setText(MyPreferences.getAgentMobile(context));
        if(et_aadhar.getText().toString().length()==12){
            showCapture();
        }else {
            hideCapture();
        }


//        sessionCheckMethod(false);

        final InputFilter[] filter12 = new InputFilter[1];
        filter12[0] = new InputFilter.LengthFilter(12);

        final InputFilter[] filter16 = new InputFilter[1];
        filter16[0] = new InputFilter.LengthFilter(16);
        et_aadhar.setFilters(filter12);

        spinnerDeviceType = findViewById(R.id.spinnerDeviceType);
        spinnerDeviceType.setAdapter(Common.getSpinnerAdapter(AepsConstants.deviceArray, context));

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
                        d_type = AepsConstants.MORPHO;
                        break;
                    case 3:
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
//                        showCapture();
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

        btn_capture.setOnClickListener(v -> {
            Common.preventFrequentClick(btn_capture);
            str_aadhar = et_aadhar.getText().toString().trim();
            mobileNo = txt_mobileno.getText().toString().trim();
            URL = URLs.AepsAuthenticate;
            if (Common.isMobileValid(mobileNo)) {
                if (!isGetAgain) {
                    GetAepsCredential.checkAepsCredential(context);
                } else {
                    captureData();
                }
            } else {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            }
        });

        btn_submit.setOnClickListener(v -> {
            Common.preventFrequentClick(btn_submit);
            str_aadhar = et_aadhar.getText().toString().trim();
            mobileNo = txt_mobileno.getText().toString().trim();
            URL = URLs.AepsRegister;
            if (Common.isMobileValid(mobileNo)) {
                if (!isGetAgain) {
                    GetAepsCredential.checkAepsCredential(context);
                } else {
                    captureData();
                }
            } else {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
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

        getAdharNumber();

        //        location find
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

    }

    private void getAdharNumber() {
        LoginModel loginModel=new LoginModel();
        GetAadharRequest request=new GetAadharRequest();
        request.AgentCode= MyPreferences.getLoginData(loginModel,context).Data.DoneCardUser;

        String json = new Gson().toJson(request);

        new NetworkCall().callService(NetworkCall.getAepsInterface().aepsPostServiceN(URLs.getagentadhar, request),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response);
                    }else {
//                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response) {
        try {
            GetAadharResponse senderResponse = new Gson().fromJson(response.string(),
                    GetAadharResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("00")) {
//                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    et_aadhar.setText(senderResponse.adharno);
                    txt_mobileno.setText(senderResponse.mobileno);
                }
            }else {
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
//            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
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

        btn_submit.setEnabled(false);
        btn_submit.setTextColor(getResources().getColor(R.color.black_text_color));
        btn_submit.setBackgroundResource(R.color.gray_color);
        btn_submit.setAlpha(0.4f);
    }

    private void showCapture() {
        btn_capture.setEnabled(true);
        btn_capture.setTextColor(getResources().getColor(R.color.color_white));
        btn_capture.setBackgroundResource(R.drawable.button_shep);
        btn_capture.setAlpha(1f);

        btn_submit.setEnabled(true);
        btn_submit.setTextColor(getResources().getColor(R.color.color_white));
        btn_submit.setBackgroundResource(R.drawable.button_shep);
        btn_submit.setAlpha(1f);

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

       /* String pidOption = getPIDOptions();
        Intent intent2 = new Intent();
        intent2.setPackage(packageName);
        intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
        intent2.putExtra("PID_OPTIONS", pidOption);
        //startActivityForResult(intent2, 1);
        startActivityForScanDevice.launch(intent2);*/
    }

    private void logEvents(String str_token, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, MyPreferences.getLoginId(context));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, str_token);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BALANCE_ENQUIRY");
        bundle.putString(FirebaseAnalytics.Param.CONTENT, message);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public class AepsTxnRequest {
        public String AgentCode, Mode = "APP", Merchant = ApiConstants.MerchantId, SessionKey, SessionRefNo,
                Lattitude, Longitude, DeviceId, TxnType = "AADHAAR", AadharNumber, PId,
                BankIIN, BankName, Mobile, CustomerName = "";
    }

    public class AepsResponse {
        public String statusCode, statusMessage;
        public ArrayList<balEnQDetails> balEnqDetails;

        public class balEnQDetails {
            public String bankName, availableBalance, rrn, accountNumber, status, transactionId,
                    txnAmount, agentCode, timeStamp, jckTransactionId, apiTxnId, txnType;
        }
        //{"statusCode":"00","statusMessage":"Success","balEnQDetails":[{"bankName":"India Post Payment Bank","":"JC0A13387","availableBalance":"306.2","rrn":"113119689889","txnAmount":"00","":"BalanceEnquiry","":"5/11/2021 7:39:51 PM","accountNumber":"XXXXXXXX5016","status":"SUCCESS","":"MA11051TUEKJC0A13387","":"130707547032"}]}
    }

    public class AepsMiniResponse {
        public String statusCode, statusMessage;
        public ArrayList<msDetails> msDetails;

        public class msDetails {
            public String date, txnType, amount, narration;
        }
//        { "statusCode": "00", "statusMessage": "Success", "msDetails": [{ "date": "06/07/2021", "txnType": "Dr", "amount": "350.00", "narration": " UPI/11873111709" }
    }

    private void sendMobileTransaction() {
        final String str_token = MyPreferences.getToken(context);
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
                            AepsResponse commonResponseModel = new Gson().fromJson(response, AepsResponse.class);
//                                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
                            if (commonResponseModel != null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_LONG).show();
                                MyPreferences.saveAepsAgentData(str_aadhar, mobileNo, context);
//                                    openReceipt(commonResponseModel);
                            } else {
                                Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_LONG).show();
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
//                        logEvents(str_token, error.getMessage());
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

                params.put("AgentCode", MyPreferences. getLoginData(new LoginModel(), context).Data.DoneCardUser);
                params.put("Mobile", mobileNo);
                params.put("Merchant", ApiConstants.MerchantId);
                params.put("Mode", "APP");
                params.put("AadharNumber", str_aadhar);
//                params.put("Latitude", mCurrentLocation.getLatitude() + "");
//                params.put("Longitude", mCurrentLocation.getLongitude() + "");
                params.put("Lattitude", mLatitude);
//                params.put("Latitude", mLatitude);
                params.put("Longitude", mLongitude);
                params.put("PId", pidDataXML);
                if (d_type.equals(AepsConstants.MORPHO) || d_type.equals(AepsConstants.STARTEK)) {
                    params.put("PId", pidDataXML.replace("\n", ""));  //.replace("\n","")
                } else {
                    params.put("PId", ("<?xml version=\"1.0\"?>" + pidDataXML).replace("\n", ""));
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

    // validation for submit
    private boolean validation() {
        str_aadhar = et_aadhar.getText().toString().trim();
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
        /*if (pidDataXML == null || pidDataXML.length() == 0) {
            Toast.makeText(this, "Please capture fingerprint before submit", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }

    private String getPIDOptionsPay() {

        return "<?xml version=\"1.0\"?><PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"\" posh=\"UNKNOWN\" env=\"P\" /><CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts></PidOptions>";

    }


    // get result of pid data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
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
        boolean isPackage = false;
        for (PackageInfo pl : packageList) {
            if (pl.applicationInfo.packageName.equals(mPackageName)) {
                isPackage = true;
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
//            requestPermissions2();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void requestPermissions2() {

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {

//                            Boolean fineLocationGranted = null;
                            Boolean coarseLocationGranted = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                fineLocationGranted = result.getOrDefault(
//                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                                coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            }

                           /* if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else */if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                // No location access granted.
                                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                /*Manifest.permission.ACCESS_FINE_LOCATION,*/
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
