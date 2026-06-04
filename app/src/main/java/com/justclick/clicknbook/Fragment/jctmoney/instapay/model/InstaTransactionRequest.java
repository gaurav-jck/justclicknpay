package com.justclick.clicknbook.Fragment.jctmoney.instapay.model;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonRapiRequest;

public class InstaTransactionRequest {
    public String Name, BeneId, TransferType, MobileNumber, Mode="App", MerchantId= ApiConstants.MerchantId;
    public String AgentCode, AccountNumber, IFSC, BankName, stateResp, Bankid, Latitude, Longitude, IPAddress;
    public int Amount;

    //transaction params
    public String OTP, Transactionid, JCKTransactionid;
}
