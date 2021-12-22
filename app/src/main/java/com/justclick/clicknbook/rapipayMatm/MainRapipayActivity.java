package com.justclick.clicknbook.rapipayMatm;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.MatmArrSyncActivity;
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.NewMatmArrActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import okhttp3.ResponseBody;


public class MainRapipayActivity extends AppCompatActivity {
    private final String BAL_INQ="Balance Enquiry",CASH_WITH="Cash Withdrawal", SYNC="sync",
            MerchantIdt="116036",SubMerchantIdt="116036",SaltDatat="2ABCCCA12796CC8C7296CF2330CE5114",
            MerchantId="181792",SubMerchantId="181792",SaltData="1706A98F834A4C20B3D3DF57BE7A30FD",
            Mobile1="9741684405", Mobile2="8527153104", Name="JCKTest",returnUrl="https://matm.justclicknpay.com/api_V1/PaymentEngine/CashWithDraw";
    //    MerchantId="156457",SubMerchantId="25391"
    protected BluetoothAdapter btAdapter;
    protected static final int REQUEST_BLUETOOTH = 101;
    EditText input_amount;
    RadioGroup radioGroup;
    String transactionType = null;
    private Context context;
    private boolean isInitiateTxn =false;
    private String clientRefId, smId;
    private View submitTxn, syncTxn,txn_btn;

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
        setContentView(R.layout.activity_main_rapipay);
        context=this;
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
        transactionType=CASH_WITH;

        submitTxn = findViewById(R.id.btn_submit_aeps);
        txn_btn = findViewById(R.id.txn_btn);
        input_amount = findViewById(R.id.input_amount);

        findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        syncTxn =findViewById(R.id.syc_btn);
        syncTxn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.preventFrequentClick(syncTxn);
                syncData();
            }
        });

        submitTxn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.preventFrequentClick(submitTxn);
                if(transactionType.equals(CASH_WITH) && input_amount.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter amount",Toast.LENGTH_SHORT).show();
                }else {
                    initiateMatmTxn();
                }
            }
        });

        txn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(txn_btn);
                getTxnList();
            }
        });

//        Button btn_status = (Button) findViewById(R.id.btn_status);
//        btn_status.setOnClickListener(new View.OnClickListener() {

//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("AGENTBALANCEerror", String.valueOf(error));
//                        if (error instanceof NetworkError) {
//                            Toast.makeText(MainActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(MainActivity.this, "Server Error ! Please try again later ", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(MainActivity.this, "AuthFailure...Please enter valid Username and Password.", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(MainActivity.this, "Parsing error! Please try again after some time!!", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof TimeoutError) {
//                            Toast.makeText(MainActivity.this, "Connection TimeOut! Please check your internet connection.", Toast.LENGTH_LONG).show();
//                        } else {
//                        }
//                    }
//                }) {
//

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
                        MATMResponse senderResponse = new Gson().fromJson(response, MATMResponse.class);
                        openReceipt(senderResponse);
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



    public void error_msg(final String response, String responsecode) {

        final Dialog dialog = new Dialog((MainRapipayActivity.this));
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
        dialog_msg.setText(response.replace(",","\n"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);



    }

    private void initiateMatmTxn() {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        InitiateMatmTxnRequest request=new InitiateMatmTxnRequest();
        request.setAgentCode(loginModel.Data.DoneCardUser);
        request.setAmount(Float.parseFloat(input_amount.getText().toString()));
        request.setTxnType(transactionType);

        new NetworkCall().callRapipayMatmService(request, ApiConstants.InitiateMatmTxn, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerCredential(response, 1);    //https://remittance.justclicknpay.com/api/payments/CheckCredential
                        }else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                            isInitiateTxn =true;
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, true);
    }

    private void responseHandlerCredential(ResponseBody response, int TYPE) {
        try {
            InitiateMatmTxnResponse senderResponse = new Gson().fromJson(response.string(), InitiateMatmTxnResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    clientRefId=senderResponse.getTransactionId();
                    smId=senderResponse.getSmId();
                    makeTxn();
                    Toast.makeText(context,senderResponse.getStatusMessage()+"\n"+senderResponse.getTransactionId(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void makeTxn() {
        try {
            if (!accessBluetoothDetails().isEmpty() && transactionType != null && !input_amount.getText().toString().isEmpty()) {
                Date date=Calendar.getInstance().getTime();
                String timestamp = new SimpleDateFormat("yyyymmddHH").format(date);
                String timestamps = new SimpleDateFormat("yyyymmddHHmm").format(date);
                Intent intent = new Intent(MainRapipayActivity.this, NewMatmArrActivity.class);


                String strhasdata= Utils.sha512(MerchantId, SubMerchantId, timestamps, SaltData);
                Log.d("strdata",strhasdata);


                Bundle b = new Bundle();
                b.putString("Merchantid", MerchantId);
                b.putString("Submerchantid", SubMerchantId);                                                           //2ABCCCA12796CC8C7296CF2330CE5114 uat
//                        b.putString("clientRefID", timestamps);
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
                Toast.makeText(MainRapipayActivity.this, "First pair bluetooth and select type", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncData() {
        try {
            if (!accessBluetoothDetails().isEmpty()) {
                String timestamp = new SimpleDateFormat("yyyymmddHH").format(new Date()).toString();
                String timestamps = new SimpleDateFormat("yyyymmddHHmm").format(new Date()).toString();
                Intent intent = new Intent(MainRapipayActivity.this, MatmArrSyncActivity.class);
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
                Toast.makeText(MainRapipayActivity.this, "First pair bluetooth and select type", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openReceipt(final MATMResponse senderResponse) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final TextView cardHolderTv= dialog.findViewById(R.id.cardHolderTv);
        final TextView bankNameTv=  dialog.findViewById(R.id.bankNameTv);
        final TextView accountNoTv= dialog.findViewById(R.id.accountNoTv);
        final TextView benIdTv=  dialog.findViewById(R.id.benIdTv);
        final TextView jctTxnIdTv= dialog.findViewById(R.id.jctTxnIdTv);
        final TextView bankRefNoTv=  dialog.findViewById(R.id.bankRefNoTv);
        final TextView remitAmountTv=  dialog.findViewById(R.id.remitAmountTv);
        final TextView availBalTv=  dialog.findViewById(R.id.availBalTv);
        final TextView txnTypeTv= dialog.findViewById(R.id.txnTypeTv);
        final TextView txnStatusTv=  dialog.findViewById(R.id.txnStatusTv);
        final TextView txnDateTv= dialog.findViewById(R.id.txnDateTv);

        final TextView errorTv= dialog.findViewById(R.id.errorTv);
        final LinearLayout contentLin= dialog.findViewById(R.id.contentLin);

        cardHolderTv.setText(senderResponse.getCardHolderName());
        bankNameTv.setText(senderResponse.getBankName());
        accountNoTv.setText(senderResponse.getAccountNo());
        benIdTv.setText("");
        jctTxnIdTv.setText(senderResponse.getClientRefID());
        bankRefNoTv.setText(senderResponse.getRRN());
        txnTypeTv.setText(senderResponse.getTransactionType());
        txnStatusTv.setText(senderResponse.getTransactionStatus());
        remitAmountTv.setText(senderResponse.getTxnAmt());
        availBalTv.setText(senderResponse.getAvailableBalance());
        txnDateTv.setText(senderResponse.getTransactionDatetime());

        dialog.findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void getTxnList() {

        String bookingDate= new SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().getTime());

        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        TxnListRequestModel request=new TxnListRequestModel();
        request.setAgentCode(loginModel.Data.DoneCardUser);
        request.setFromdate(bookingDate);
        request.setTodate(bookingDate);
        request.setRowStart("1");
        request.setRowEnd("10");

        new NetworkCall().callRapipayMatmService(request, ApiConstants.TransactionListMatm, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerList(response, 1);    //https://remittance.justclicknpay.com/api/payments/CheckCredential
                        }else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                            isInitiateTxn =true;
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, true);
    }

    private void responseHandlerList(ResponseBody response, int i) {
        try {
            TxnListResponseModel senderResponse = new Gson().fromJson(response.string(), TxnListResponseModel.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(context,TxnListActivity.class));
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
