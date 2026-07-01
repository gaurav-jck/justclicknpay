package com.justclick.clicknbook.Fragment.billpayinsta.models;

import com.justclick.clicknbook.ApiConstants;

import java.io.Serializable;

public class BillpayFetchBillRequest implements Serializable {
    public String AgentCode,BillerId, Mode=ApiConstants.ModeApp, Merchant=ApiConstants.MerchantId,
            IPAddress, Latitude, Longitude, Param1, Param2, Param3, Param4, OperatorName;
}
