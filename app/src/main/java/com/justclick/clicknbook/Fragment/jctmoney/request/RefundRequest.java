package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class RefundRequest {

    public String TransactionId, MerchantId= ApiConstants.MerchantId, Mode="App",
            LoggedInAgentCode, otp, referenceid, ackno, AgentCode;
}
