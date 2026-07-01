package com.justclick.clicknbook.Fragment.billpayinsta.models;

import com.justclick.clicknbook.ApiConstants;

public class InstaPaybillRequest {
    public String AgentCode,BillerId, Mode=ApiConstants.ModeApp, Merchant=ApiConstants.MerchantId,
            IPAddress, Latitude, Longitude, Param1, Param2, Param3, Param4,
            Amount, Category, OperatorName, EnquiryReferenceId, BillDate, DueDate, CustomerName;
}
