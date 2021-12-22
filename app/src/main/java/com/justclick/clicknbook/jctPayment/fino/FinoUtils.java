package com.justclick.clicknbook.jctPayment.fino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//
//import com.finopaytech.finosdk.activity.MainTransactionActivity;
//import com.finopaytech.finosdk.encryption.AES_BC;
//import com.finopaytech.finosdk.helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class FinoUtils {

    public void callSDKActivity(Context context, String amount, String reservationId, String serviceId)
    {
//        Intent intent = new Intent(context, MainTransactionActivity.class);
//        intent.putExtra("RequestData", getEncryptedRequest(amount, reservationId,serviceId));
//        intent.putExtra("HeaderData", getEncryptedHeader());
//        intent.putExtra("ReturnTime", 5);// Application return time in second
//        ((Activity)context).startActivityForResult(intent, 1);
//        resetData(cbMicroATM.getId());
    }

    private String getServiceID() {
        return FinoConstants.SERVICE_AEPS_BE;
    }

    private String getEncryptedRequest(String amount, String reservationId, String serviceId) {
        String strRequestData = "";
        JSONObject jsonRequestDataObj = new JSONObject(); // inner object request
        try {
            jsonRequestDataObj.put("MerchantId", FinoConstants.MERCHANT_ID);
            jsonRequestDataObj.put("SERVICEID", serviceId);
            jsonRequestDataObj.put("RETURNURL", FinoConstants.RETURN_URL);
            jsonRequestDataObj.put("Version", FinoConstants.VERSION);

            jsonRequestDataObj.put("Amount", amount);
            jsonRequestDataObj.put("ClientRefID", reservationId);
//            jsonRequestDataObj.put("ClientRefID", Utils.generateRefID(FinoConstants.MERCHANT_ID));
           /* if (getServiceID().equals(FinoConstants.SERVICE_AEPS_TS) || getServiceID().equals(FinoConstants.SERVICE_MICRO_TS)) {
                jsonRequestDataObj.put("Amount", amount);
                jsonRequestDataObj.put("ClientRefID", reservationId);
            } else {
                jsonRequestDataObj.put("Amount", amount);
                jsonRequestDataObj.put("ClientRefID", reservationId);
                //jsonRequestDataObj.put("ClientInitiatorId", Constants.CLIENT_INITIATOR_ID);
            }*/
//            Log.i("RequestData:",jsonRequestDataObj.toString());
//            strRequestData = Utils.replaceNewLine(AES_BC.getInstance().encryptEncode(jsonRequestDataObj.toString(), FinoConstants.CLIENT_REQUEST_ENCRYPTION_KEY));
        } catch (Exception e) {
        }
        return strRequestData;
    }

    private static String getEncryptedHeader() {
        String strHeader = "";
        JSONObject header = new JSONObject();
        try {
            header.put("AuthKey", FinoConstants.AUTHKEY);
            header.put("ClientId", FinoConstants.CLIENTID);
//            strHeader = Utils.replaceNewLine(AES_BC.getInstance().encryptEncode(header.toString(), FinoConstants.CLIENT_HEADER_ENCRYPTION_KEY));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return strHeader;
    }
}
