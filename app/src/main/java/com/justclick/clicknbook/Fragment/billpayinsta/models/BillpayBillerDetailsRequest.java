package com.justclick.clicknbook.Fragment.billpayinsta.models;

import com.justclick.clicknbook.ApiConstants;

public class BillpayBillerDetailsRequest {
    public String AgentCode,BillerId, Mode=ApiConstants.ModeApp, Merchant=ApiConstants.MerchantId,
            IPAddress, Latitude, Longitude;
}
