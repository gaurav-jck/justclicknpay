package com.justclick.clicknbook.jctPayment.Utilities;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.jctPayment.Balance_Enquiry_Activity;
import com.justclick.clicknbook.jctPayment.Cash_Withdrawl_Activity;
import com.justclick.clicknbook.jctPayment.newaeps.AepsRegistrationActivity;
import com.justclick.clicknbook.jctPayment.newaeps.Balance_Enquiry_Activity_N;
import com.justclick.clicknbook.jctPayment.newaeps.Cash_Withdrawl_Activity_N;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;

public class GetAepsCredential {
    private static boolean isGet;
    private static void saveData(CheckResponseClass responseModel, Context context) {
        MyPreferences.saveAepsToken(responseModel.credentialData.get(0).token,context);
        MyPreferences.saveUserData(responseModel.credentialData.get(0).userData,context);
        MyPreferences.saveSessionKey(responseModel.credentialData.get(0).sessionKey,context);
        MyPreferences.saveSessionRefNo(responseModel.credentialData.get(0).sessionRefNo,context);
    }

    public class CheckResponseClass{
        public String statusCode, statusMessage;
        public ArrayList<credentialData> credentialData;
        public class credentialData{
            public String sessionKey, sessionRefNo, token, userData;
        }
    }

    public static class CheckCredentialRequest{
        public String AgentCode, Mode="APP", Merchant= ApiConstants.MerchantId;
    }

    public static boolean checkAepsCredential(final Context context){
        CheckCredentialRequest request=new CheckCredentialRequest();
        LoginModel loginModel=new LoginModel();
        request.AgentCode=MyPreferences.getLoginData(loginModel,context).Data.DoneCardUser;
        MyCustomDialog.showCustomDialog(context, "Please wait...");
        new NetworkCall().callAepsServiceNew(request,URLs.GenerateToken, context,
                (response, responseCode) -> {
                    isGet=false;
                    if(response!=null){
                        try {
                            CheckResponseClass commonResponseModel = new Gson().fromJson(response.string(), CheckResponseClass.class);
                            if(commonResponseModel!=null && commonResponseModel.statusCode.equalsIgnoreCase("00")) {
                                saveData(commonResponseModel, context);
                                MyCustomDialog.hideCustomDialog();
                                if(context instanceof Balance_Enquiry_Activity){
                                    ((Balance_Enquiry_Activity)context).captureData();
                                }else if(context instanceof Balance_Enquiry_Activity_N){
                                    ((Balance_Enquiry_Activity_N)context).captureData();
                                }else if(context instanceof Cash_Withdrawl_Activity_N){
                                    ((Cash_Withdrawl_Activity_N)context).captureData();
                                }else if(context instanceof AepsRegistrationActivity){
                                    ((AepsRegistrationActivity)context).captureData();
                                }else {
                                    ((Cash_Withdrawl_Activity)context).captureData();
                                }
                                isGet=true;
                            }else {
                                Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            MyCustomDialog.hideCustomDialog();
                        }
                    }else {
                        MyCustomDialog.hideCustomDialog();
                    }
                });
        return isGet;
    }
}
