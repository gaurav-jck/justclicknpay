package com.justclick.clicknbook.Fragment.billpay;

public class BillGetCommissionResponse {
    public String statusCode, statusMessage;
    public rechargeCommsion rechargeCommsion;

    public class  rechargeCommsion{
        public float netAmount, agentComm, agentTds, agentGst, agentCommFix, agentCommPerc, agentservicechage;
    }
    /*{
    "statusCode": "00",
    "statusMessage": "Commmission Fetched Successfully",
    "rechargeCommsion": {
        "amount": 0,
        "netAmount": 1475.10,
        "agentComm": 0.63,
        "agentTds": 0.03,
        "agentGst": 0,
        "distComm": 0.63,
        "superDistComm": 0.00,
        "jckCommFix": 2.50,
        "jckComm": 2.50,
        "jckCommPerc": 0.000,
        "jckGst": 0.00,
        "jckTds": 0.12,
        "agentCommFix": 0.63,
        "agentCommPerc": 0.00,
        "distFix": 0.63,
        "distPerc": 0.00,
        "superDistFix": 0.00,
        "superDistPerc": 0.00,
        "merchantFix": 0.00,
        "merchantPerc": 0.00,
        "jckServiceChargeFix": 0.00,
        "jckServiceChargePerc": 0.00,
        "jckServiceCharge": 0.00,
        "agentservicechage": 0.00
    }
}*/
}
