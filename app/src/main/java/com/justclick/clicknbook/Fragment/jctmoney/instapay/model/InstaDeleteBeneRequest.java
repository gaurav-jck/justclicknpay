package com.justclick.clicknbook.Fragment.jctmoney.instapay.model;

import com.justclick.clicknbook.ApiConstants;

public class InstaDeleteBeneRequest {
    public String AgentCode, mobile, BeneId, IPAddress, Mode="App", MerchantId=ApiConstants.MerchantId;
    public String StateResp, OTP;
}
