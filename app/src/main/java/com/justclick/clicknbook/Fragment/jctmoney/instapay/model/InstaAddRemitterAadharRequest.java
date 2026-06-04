package com.justclick.clicknbook.Fragment.jctmoney.instapay.model;

import com.justclick.clicknbook.ApiConstants;

public class InstaAddRemitterAadharRequest {
    private String Mode="APP", MerchantId= ApiConstants.MerchantId;
    public String Mobile,AgentCode, IPAddress, Aadhaar_no, stateResp;
    public String OTP;
}
