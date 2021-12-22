package com.justclick.clicknbook.jctPayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.Activity.MyLoginActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Activities.Receipt_Activity;
import com.justclick.clicknbook.jctPayment.Models.Opts;
import com.justclick.clicknbook.jctPayment.Models.PidOptions;
import com.justclick.clicknbook.jctPayment.Utilities.SessionManager;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
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

public class Cash_Deposit_Activity extends AppCompatActivity {

    private Context context;
    private Button btn_capture, btn_submit;
    ArrayList<String> bankArray = new ArrayList<>();
    HashMap<String, String> bank_iin = new HashMap<>();
    AutoCompleteTextView atv_bank;
    ArrayAdapter aa;
    EditText et_amount, et_mobile, et_aadhar;
    String str_b_name, str_amount, str_mobile, str_aadhar;
    public static final String FreshnessFactor = "freshnessfactor";
    public static final String PIDDATAXML = "piddataxml";
    SharedPreferences sharedPreferences;
    String pidDataXML;
    ProgressDialog progressDialog;
    RadioGroup radioGroup;
    String d_type = "mantra";
    private int fingerCount = 0;
    private Serializer serializer = null;
    private ArrayList<String> positions;
    LinearLayout ll_focus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash__deposit_);

        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        positions = new ArrayList<>();
        serializer = new Persister();

        progressDialog = new ProgressDialog(context);

        // init views
        atv_bank = (AutoCompleteTextView)findViewById(R.id.atv_bank);
        btn_capture=(Button)findViewById(R.id.btn_capture);
        btn_submit = (Button)findViewById(R.id.btn_submit);
        et_amount = (EditText)findViewById(R.id.txt_amount);
        et_mobile = (EditText)findViewById(R.id.txt_mobileno);
        et_aadhar = (EditText)findViewById(R.id.txt_aadharno);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        // get bank list
        getBankList();

        // get freshmess factor
//        getFreshnessFactor();

        //search package
        SearchPackageName();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mantra:
                        d_type = "mantra";
                        break;
                    case R.id.rb_startek:
                        d_type = "startek";
                        break;
                    case R.id.rb_morpho:
                        d_type = "morpho";
                        break;
                }
            }
        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (d_type.equals("startek")) {
                        String selectedPackage = "com.acpl.registersdk";
                        Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.CAPTURE", null);
                        String pidOptXML = createPidOptXML();
                        intent1.putExtra("PID_OPTIONS", pidOptXML);
                        intent1.setPackage(selectedPackage);
                        startActivityForResult(intent1, 2);
                    }
                    if (d_type.equals("mantra")) {
                        String selectedPackage = "com.mantra.rdservice";
                        Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.CAPTURE", null);
                        String pidOptXML = getPIDOptions();
                        intent1.putExtra("PID_OPTIONS", pidOptXML);
                        intent1.setPackage(selectedPackage);
                        startActivityForResult(intent1, 123);
                    }
                    if (d_type.equals("morpho")){
                        String selectedPackage = "com.scl.rdservice";
                        Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.CAPTURE", null);
                        String pidOptXML = getPIDOptions();
                        intent1.putExtra("PID_OPTIONS", pidOptXML);
                        intent1.setPackage(selectedPackage);
                        startActivityForResult(intent1, 3);
                    }
                } catch (Exception e) {
                    showMessageDialogue("EXCEPTION- " + e.getMessage(),"EXCEPTION");
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    sessionCheckMethod();
                }
            }
        });

        atv_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)atv_bank.showDropDown();
            }
        });

        //validation for length , text and focus
        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==10){
                    et_aadhar.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        et_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(et_mobile.getText().toString().length()<10){
                        et_mobile.setError("Please enter 10 digit Mobile Number");
                    }
                }
            }
        });
        et_aadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==12){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_aadhar.getWindowToken(), 0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        et_aadhar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(et_aadhar.getText().toString().length()<12){
                        et_aadhar.setError("Please enter 12 digit Aadhar Number");
                    }
                }
            }
        });


    }

    //session check api
    private void sessionCheckMethod() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Processing The Request....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_SESSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("StatusCode").equals("0")) {
                                Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_LONG).show();
                                SessionManager.getInstance(context).logout();
                                Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
//                                finish();
                            } else {
                                senMobleTransactin();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void senMobleTransactin() {
        final String str_token = MyPreferences.getToken(context);
        final String bank = bank_iin.get(str_b_name);
        progressDialog.setMessage("Sending The Request....");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MOBILE_TRANSACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getString("status").equals("true")) {
                                String string_factor = obj.getString("nextFreshnessFactor");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(FreshnessFactor, string_factor);
                                editor.commit();
                                // send to receipt form
                                if (obj.getString("responseCode").equals("00")) {
                                    Intent intent = new Intent(context, Receipt_Activity.class);
                                    intent.putExtra("TransactionID", obj.getString("aepsTransactionLogId"));
                                    intent.putExtra("t_type", "Cash Deposit");
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
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
                //pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("Authorization", str_token);
                return pars;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bank", bank);
                params.put("mobile", str_mobile);
                params.put("aadhar", str_aadhar);
                params.put("amount", str_amount);
                params.put("deviceType", "startek");
                params.put("transType", "210000");
                params.put("biometricData", pidDataXML);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    // validation for submit
    private boolean validation() {
        boolean validation = true;
        str_b_name = atv_bank.getText().toString().trim();
        str_amount = et_amount.getText().toString().trim();
        str_mobile = et_mobile.getText().toString().trim();
        str_aadhar = et_aadhar.getText().toString().trim();

        if (TextUtils.isEmpty(str_b_name)) {
            atv_bank.setError("Please enter Bank Name");
            atv_bank.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(str_amount)){
            et_amount.setError("Please enter Amount");
            et_amount.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(str_mobile)){
            et_mobile.setError("Please enter Mobile Number");
            et_mobile.requestFocus();
            return false;
        }
        else if (str_mobile.length() != 10){
            et_mobile.setError("Please enter 10 digit Mobile Number");
            et_mobile.requestFocus();
            return false;  
        }
        if(TextUtils.isEmpty(str_aadhar)){
            et_aadhar.setError("Please enter Aadhar Number");
            et_aadhar.requestFocus();
            return false;
        } else if (str_aadhar.length() != 12){
            et_aadhar.setError("Please enter 12 digit Aadhar Number");
            et_aadhar.requestFocus();
            return false;
        }
        if(pidDataXML.length()<1){
            Toast.makeText(this, "Please capture fingerprint before submit", Toast.LENGTH_LONG).show();
            return false;
        }

        return validation;
    }

    // pid data xml
    private String createPidOptXML() {
        String tmpOptXml = "";
        try{
            String fTypeStr = "0";
            String formatStr = "0";
            String timeOutStr = "20000";
            String envStr = "P";

            /*if(fTypeSel.getSelectedItem().toString().toUpperCase().equals("FMR")){
                fTypeStr = "0";
            }else if(fTypeSel.getSelectedItem().toString().toUpperCase().equals("FIR")){
                fTypeStr = "1";
            }

            if(formatSel.getSelectedItem().toString().toUpperCase().equals("XML")){
                formatStr = "0";
            }else if(formatSel.getSelectedItem().toString().toUpperCase().equals("PROTOBUF")){
                formatStr = "1";
            }

            if(txtTimeout.getText() != null && !txtTimeout.getText().toString().trim().equals("")){
                timeOutStr = txtTimeout.getText().toString().trim();
            }

            if(envSel.getSelectedItem().toString().toUpperCase().equals("PRODUCTION")){
                envStr = "P";
            }else if(envSel.getSelectedItem().toString().toUpperCase().equals("PRE-PRODUCTION")){
                envStr = "PP";
            }else if(envSel.getSelectedItem().toString().toUpperCase().equals("STAGING")){
                envStr = "S";
            }*/

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
            attr.setValue("");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pCount");
            attr.setValue("0");
            opts.setAttributeNode(attr);

            attr = doc.createAttribute("pType");
            attr.setValue("");
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
            attr.setValue("ONLY USE FOR E-KYC.");
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
            /*int fingerCount = spinnerTotalFingerCount.getSelectedItemPosition() + 1;
            int fingerType = spinnerTotalFingerType.getSelectedItemPosition();
            int fingerFormat = spinnerTotalFingerFormat.getSelectedItemPosition();
            String pidVer = edtxPidVer.getText().toString();
            String timeOut = edtxTimeOut.getText().toString();*/
            String posh = "UNKNOWN";
            if (positions.size() > 0) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }

            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "0";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = "0";
            opts.pidVer = "2.0";
            opts.timeout = "10000";
//            opts.otp = "123456";
            opts.wadh = "";
            opts.posh = posh;
            opts.env = "P";

            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
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
        try {
            if (data == null) {
                showMessageDialogue("Scan Failed/Aborted!", "Message");
            } else {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == Activity.RESULT_OK) {

                    if (requestCode == 123) {
                        pidDataXML = data.getStringExtra("PID_DATA");
                        if (pidDataXML != null) {
                            // xml parsing
                            readXMLData(pidDataXML);
                        } else {
                            showMessageDialogue("NULL STRING RETURNED", "Fingerprint data status");
                        }
                    } else if (requestCode == 2) {
                        //String pidDataXML = data.getStringExtra("PID_DATA");
                         pidDataXML = data.getStringExtra("PID_DATA");
                        if(pidDataXML!= null){
                            // xml parsing
                            readXMLData(pidDataXML);
                        }else{
                            showMessageDialogue("NULL STRING RETURNED","Fingerprint data status");
                        }
                    } else if (requestCode == 3){
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
                    showMessageDialogue("Scan Failed/Aborted!","CAPTURE RESULT");
                }
            }
        } catch (Exception ex) {
            showMessageDialogue("Error:-" + ex.getMessage(),"EXCEPTION");
            ex.printStackTrace();
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
                String s_message = element2.getElementsByTagName("Resp").item(0).getAttributes().getNamedItem("errInfo").getNodeValue();

                if(s_status.equals("0")){
                    showMessageDialogue("Data captured", "Fingerprint data status");
                } else {
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

    //serach package
    private void SearchPackageName(){
        PackageManager pm = this.getPackageManager();
        String packageName = "com.mantra.rdservice";
        Intent intent = new Intent();
        intent.setPackage(packageName);
        List listTemp = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);

        if(listTemp.size() <= 0 ){
            Toast toast = Toast.makeText(context,"Please install ` Registered Device` Service.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            Intent intentPlay = new Intent(Intent.ACTION_VIEW);
            intentPlay.setData(Uri.parse("market://details?id=com.mantra.rdservice"));
            startActivity(intentPlay);
        }
    }

    //get bank list
    private void getBankList() {
        final String str_token = MyPreferences.getToken(context);
        progressDialog.setMessage("Fetching The Bank List....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.BANK_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            JSONArray jsonArray = obj.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting the json object of the particular index inside the array
                                JSONObject myObject = jsonArray.getJSONObject(i);

                                //adding the bank to herolist
                                bankArray.add(myObject.getString("bank_name"));
                                bank_iin.put(myObject.getString("bank_name"),myObject.getString("bank_iin"));
                            }
                            //spinner
                            aa = new ArrayAdapter(context, R.layout.item_bank_list, bankArray);
                            atv_bank.setAdapter(aa);

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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //get freshness factor
    private void getFreshnessFactor(){
        final String str_token = MyPreferences.getToken(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REFRESHNES_FACTOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            String string_factor = obj.getString("nextFreshnessFactor");
                            // put data to prferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(FreshnessFactor, string_factor);
                            editor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
