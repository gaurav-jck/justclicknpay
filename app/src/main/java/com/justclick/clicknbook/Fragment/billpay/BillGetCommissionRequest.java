package com.justclick.clicknbook.Fragment.billpay;

import com.justclick.clicknbook.ApiConstants;

public class BillGetCommissionRequest {
    public float BillAmount, Billnetamount;
    public String operatorid, Canumber, Lattitude="27.2211", Longitude="78.26511", Mode="APP", billdate,
            dueDate, acceptPayment, acceptPartPay, cellNumber, userName,
            Merchant= ApiConstants.MerchantId, Type="APP", AgentCode, Email,
            Suppliercode, category, fetchbillstring, userData;
    public String PayBillType, Paymentmode;
//    public String canumber;
}
