package com.justclick.clicknbook.jctPayment.fino.finomvvm.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.finopaytech.finosdk.encryption.AES_BC;
//import com.finopaytech.finosdk.helpers.Utils;
//import com.finopaytech.finosdk.models.ErrorSingletone;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Models.InitTxn;
import com.justclick.clicknbook.jctPayment.Models.InitTxnResponse;
import com.justclick.clicknbook.jctPayment.Models.UpdateTxnRequest;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.fino.FinoConstants;
import com.justclick.clicknbook.jctPayment.fino.FinoUtils;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;

public class FinoCashWithdrawActivity extends AppCompatActivity {
    private Context context;
    private String reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fino_cash_withdraw);
        context=this;
// implementation files('libs/finosdk.aar')
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=((TextView)findViewById(R.id.txt_amount)).getText().toString();
                if(Common.isdecimalvalid(amount)) {
                    initTransaction(Float.parseFloat(amount));
                }
            }
        });
    }

    private void initTransaction(float amount) {
        InitTxn initTxn=new InitTxn();
        initTxn.Amount= (int) amount;
        initTxn.TxnType= "CW";
        LoginModel loginModel= MyPreferences.getLoginData(new LoginModel(),context);
        initTxn.LoginSessionId= loginModel.LoginSessionId;
        initTxn.DoneCardUser= loginModel.Data.DoneCardUser;
        new NetworkCall().callFinoService(initTxn, URLs.AepsInitTransaction, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 1);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int i) {
        try{
            if(i==1){
                InitTxnResponse senderResponse = new Gson().fromJson(response.string(), InitTxnResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.StatusCode ==0 && senderResponse.ReservationId!=null){
                        reservationId=senderResponse.ReservationId;
                        new FinoUtils().callSDKActivity(context, String.valueOf(senderResponse.Amount),
                                senderResponse.ReservationId, FinoConstants.SERVICE_AEPS_CW);
                    }else {
                        Toast.makeText(context , senderResponse.StausMessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, response.string(), Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        case FINO_AEPS_CODE:
        String response;
        if (data != null & resultCode == RESULT_OK && requestCode == 1)
        {
            Bundle bundle = data.getExtras();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Toast.makeText(context, "Key:"+key+"\nvalue:"+value.toString(), Toast.LENGTH_SHORT).show();
            }
            if (data.hasExtra("ClientResponse")) {//This is for encrypted client response from finosdk in case of SUCCESS
//                response = data.getStringExtra("ClientResponse");
//                String strDecryptResponse = AES_BC.getInstance().decryptDecode(Utils.replaceNewLine(response), FinoConstants.CLIENT_REQUEST_ENCRYPTION_KEY);
//                String status="";
//                try {
//                    JSONObject object=new JSONObject(strDecryptResponse);
//                    status=object.getString("Status");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Utils.showOneBtnDialog(context, getString(R.string.STR_INFO), strDecryptResponse, false);
//                updateTxn(response,status);
            } else if (data.hasExtra("ErrorDtls"))
            {//This is plain text error in case of FAILED
                response = data.getStringExtra("ErrorDtls");
                String errorMsg = "", errorDtlsMsg = "";
                if (!response.equalsIgnoreCase("")) {
                    try {
                        String[] error_dtls = response.split("\\|");
                        if (error_dtls.length > 0) {
                            errorMsg = error_dtls[0];
//                            Utils.showOneBtnDialog(context, getString(R.string.STR_INFO), "Error Message : " + errorMsg, false);
                        }
                    } catch (ArrayIndexOutOfBoundsException exp) {
                    }
                }
            }

//            ErrorSingletone.getFreshInstance();
        }else {
            Toast.makeText(context, "Error in result", Toast.LENGTH_SHORT).show();
        }
//        break;
    }

    private void updateTxn(String response, String status) {
        UpdateTxnRequest updateTxnRequest=new UpdateTxnRequest();
        LoginModel loginModel= MyPreferences.getLoginData(new LoginModel(),context);
        updateTxnRequest.LoginSessionId= loginModel.LoginSessionId;
        updateTxnRequest.DoneCardUser= loginModel.Data.DoneCardUser;
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("ClientRefID", reservationId);
            jsonObject.put("DisplayMessage", status);
            jsonObject.put("ResponseCode", "1002");
            jsonObject.put("ClientRes", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateTxnRequest.ApiResponse= jsonObject.toString();
        Toast.makeText(context, "Api response: "+updateTxnRequest.ApiResponse, Toast.LENGTH_LONG).show();
        new NetworkCall().callFinoService(updateTxnRequest, URLs.UpdateTransaction, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 2);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
