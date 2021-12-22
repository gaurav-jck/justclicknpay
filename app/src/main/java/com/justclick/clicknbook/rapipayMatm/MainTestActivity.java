package com.justclick.clicknbook.rapipayMatm;


import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.justclick.clicknbook.R;
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.MatmArrSyncActivity;
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.NewMatmArrActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class MainTestActivity extends AppCompatActivity {
    private final String BAL_INQ="Balance Enquiry",CASH_WITH="Cash Withdrawal", SYNC="sync",
//            MerchantId="116036",SubMerchantId="116036",SaltData="2ABCCCA12796CC8C7296CF2330CE5114",
            MerchantId = "27266" ,SubMerchantId = "246341", SaltData = "1706A98F834A4C20B3D3DF57BE7A30FD",
            Mobile1="9741684405", Mobile2="8527153111", Name="JCKTest",returnUrl="https://matm.justclicknpay.com/api_V1/PaymentEngine/CashWithDraw";
//    MerchantId="156457",SubMerchantId="25391"
    protected BluetoothAdapter btAdapter;
    protected static final int REQUEST_BLUETOOTH = 101;
    EditText input_amount;
    RadioGroup radioGroup;
    String transactionType = null;

    public void assignBundleValue(int position) {
        if (position == 0) {
            transactionType = CASH_WITH;
            input_amount.setText("");
            input_amount.setEnabled(true);
        } else if (position == 1) {
            transactionType = BAL_INQ;
            input_amount.setText("0");
            input_amount.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        bluetooth();
        radioGroup = findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cashid) {
                    assignBundleValue(0);
                } else if (checkedId == R.id.balanceid) {
                    assignBundleValue(1);
                }
            }
        });

        Button syc_btn =findViewById(R.id.syc_btn);
        syc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!accessBluetoothDetails().isEmpty()) {
                        String timestamp = new SimpleDateFormat("yyyymmddHH").format(new Date()).toString();
                        String timestamps = new SimpleDateFormat("yyyymmddHHmm").format(new Date()).toString();
                        Intent intent = new Intent(MainTestActivity.this, MatmArrSyncActivity.class);
                        Bundle b = new Bundle();
                        b.putString("Merchantid", MerchantId);
                        b.putString("Submerchantid", SubMerchantId);
                        b.putString("clientRefID", timestamps);
                        b.putString("HashData", Utils.sha512(MerchantId, SubMerchantId, timestamps, SaltData));
                        b.putString("Amount", "0");
                        b.putString("TransactionType", SYNC);

                        b.putString("BluetoothId", accessBluetoothDetails());
                        b.putString("CustomerMobile", Mobile2);
                        b.putString("CustomerName", Name);
                    //    b.putString("saltData", "2ABCCCA12796CC8C7296CF2330CE5114");
                        b.putString("reverseURLtoPostRes", returnUrl);
                        intent.putExtras(b);
                        startActivityForResult(intent, NEW_MATM_AEPS_Resposne);
                    } else {
                        Toast.makeText(MainTestActivity.this, "First pair bluetooth and select type", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button btn_submit_aeps = (Button) findViewById(R.id.btn_submit_aeps);
        input_amount = (EditText) findViewById(R.id.input_amount);
        btn_submit_aeps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!accessBluetoothDetails().isEmpty() && transactionType != null && !input_amount.getText().toString().isEmpty()) {
                        Date date=Calendar.getInstance().getTime();
                        String timestamp = new SimpleDateFormat("yyyymmddHH").format(date);
                        String timestamps = new SimpleDateFormat("yyyymmddHHmm").format(date);
                        Intent intent = new Intent(MainTestActivity.this, NewMatmArrActivity.class);


                        String strhasdata = Utils.sha512(MerchantId, SubMerchantId, timestamps, SaltData);
                        Log.d("strdata",strhasdata);


                        Bundle b = new Bundle();
                        b.putString("Merchantid", MerchantId);
                        b.putString("Submerchantid", SubMerchantId);                                                           //2ABCCCA12796CC8C7296CF2330CE5114 uat
                        b.putString("clientRefID", timestamps);
                        b.putString("timestamp", timestamp);
                        b.putString("HashData",strhasdata);
                        b.putString("Amount", input_amount.getText().toString());//
                        b.putString("TransactionType", transactionType);
//            04:23:33:2A:13:93
                        b.putString("BluetoothId", accessBluetoothDetails());
                        b.putString("CustomerMobile", Mobile2);
                        b.putString("CustomerName", Name);

                        b.putString("reverseURLtoPostRes", returnUrl);
                        intent.putExtras(b);
                        bundleToJSON(b);
                        startActivityForResult(intent, NEW_MATM_AEPS_Resposne);
                    } else {
                        Toast.makeText(MainTestActivity.this, "First pair bluetooth and select type", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
//        Button btn_status = (Button) findViewById(R.id.btn_status);
//        btn_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final RequestQueue requestQueue = Volley.newRequestQueue(MainTestActivity.this);
//                JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.GET, "https://uat.rapipay.com/APIV1/transactionEnq?MID=99&SMID=999999&URN=024417160320", null, new com.android.volley.Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Log.d("responceLog", String.valueOf(response));
//                            error_msg(response.toString() , response.getString("ResponseCode"));
//                        } catch (Exception e) {
//                        }
//
//
//                    }
//                }, new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("AGENTBALANCEerror", String.valueOf(error));
//                        if (error instanceof NetworkError) {
//                            Toast.makeText(MainTestActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(MainTestActivity.this, "Server Error ! Please try again later ", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(MainTestActivity.this, "AuthFailure...Please enter valid Username and Password.", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(MainTestActivity.this, "Parsing error! Please try again after some time!!", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof TimeoutError) {
//                            Toast.makeText(MainTestActivity.this, "Connection TimeOut! Please check your internet connection.", Toast.LENGTH_LONG).show();
//                        } else {
//                        }
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("HashData", "R0VORVJBVEUgSEFTIERBVEEg");
//
//                        return headers;
//                    }
//                };
//
//                jobReq.setRetryPolicy(new DefaultRetryPolicy(600000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                requestQueue.add(jobReq);
//            }
//        });

    }

    private String bundleToJSON(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch(JSONException e) {
                //Handle exception here
            }
        }
        return json.toString();
    }

    private void bluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Log.d("GoPosActivity", "bluetooth adapter is not null");
            if (!btAdapter.isEnabled()) {
                Log.d("GoPosActivity", "bluetooth is not enable");
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
            } else {
                Log.d("GoPosActivity", "bluetooth is enable");
                accessBluetoothDetails();
            }
        }
    }

    BluetoothDevice bluetoothDevice;
    String bluetoothName = null;

    private String accessBluetoothDetails() {
        if (btAdapter.getBondedDevices() != null)
            if (btAdapter.getBondedDevices().size() > 0) {
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                ArrayList<String> devices = new ArrayList<>();
                boolean isPosPaired = false;
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith("D180") || device.getName().startsWith("MP-")) {
                        bluetoothDevice = device;
                        isPosPaired = true;
                        if (device.getName().startsWith("MP-"))
                            bluetoothName = device.getName().substring(3);
                        else if (device.getName().startsWith("D180"))
                            bluetoothName = device.getName();
                        String bluetoothAddress = device.getAddress();
                        Log.d("GoPosActivity",
                                "bluetoothName: "
                                        + bluetoothName
                                        + " ,bluetoothAddress:"
                                        + bluetoothAddress);
                        if (!TextUtils.isEmpty(bluetoothAddress)) {
                            return bluetoothAddress;
                        }
                    } else {
                        isPosPaired = false;
                    }
                }
                if (!isPosPaired) {
                    new AlertDialog.Builder(this)
                            .setTitle("Bluetooth Enable")
                            .setMessage("Please Enable your Bluetooth by pressing OK")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("BlueTooth Pairing")
                        .setMessage("Your bluetooth is not paired with MATM device please pair")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentOpenBluetoothSettings = new Intent();
                                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(intentOpenBluetoothSettings);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        return null;
    }

    private static final int NEW_MATM_AEPS_Resposne = 150;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {

                case NEW_MATM_AEPS_Resposne:
                    String response = data.getStringExtra("Responce");
                    if (resultCode == -3) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            error_msg(jsonObject.optString("DisplayMessage") + " " + jsonObject.optString("ResponseCode"), "");
//                            updateDetails("" + jsonObject.optString("DisplayMessage") + " " + jsonObject.optString("ResponseCode"), jsonObject.optString("ResponseCode"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (resultCode == -2) {
                        error_msg(response, data.getStringExtra("ResponseCode"));
//
                    } else if (resultCode == -1) {
//
                        error_msg(response, "newmatm");
//
                    } else if (resultCode == -4) {
                        try {
                            error_msg(response, data.getStringExtra("ResponseCode"));
//
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        error_msg(response, data.getStringExtra("ResponseCode"));
//
                    }
//                    dialog.dismiss();
                    break;
            }
        }
    }



    public void error_msg(final String msg, String responsecode) {

        final Dialog dialog = new Dialog((MainTestActivity.this));
        dialog.setContentView(R.layout.errormsg_dailog);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_msg = dialog.findViewById(R.id.alertmsg);

        TextView dialog_currentdate_text = dialog.findViewById(R.id.dialog_currentdate);
//        TextView VersionName = dialog.findViewById(R.id.dialog_version);
        TextView dialog_service = dialog.findViewById(R.id.dialog_service);
        dialog_service.setText("MATM");
        dialog_currentdate_text.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        VersionName.setText("ver " + BuildConfig.VERSION_NAME);

        Button btn = dialog.findViewById(R.id.okbutton);
        dialog_title.setText(R.string.Alert);
        dialog_msg.setText(msg.replace(",","\n"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);



    }
}
