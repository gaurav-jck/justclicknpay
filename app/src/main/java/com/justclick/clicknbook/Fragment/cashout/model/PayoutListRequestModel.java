package com.justclick.clicknbook.Fragment.cashout.model;

import com.justclick.clicknbook.ApiConstants;

public class PayoutListRequestModel {
    private String AgentCode,UserType,Merchant= ApiConstants.MerchantId, Fromdate, Todate, RRn, JckTransactionId;
    private String RowStart, RowEnd;
    public String TxnType, DistCode, TxnDescription="", rechargeType="", AgentID="" , LoggedinAgentCode, mobile,
            transactionId, rechargeNumber;

    public void setUserType(String userType) {
        UserType = userType;
    }
    public String getUserType() {
        return UserType;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }


    public void setFromdate(String fromdate) {
        Fromdate = fromdate;
    }

    public void setTodate(String todate) {
        Todate = todate;
    }

    public void setRowStart(String rowStart) {
        RowStart = rowStart;
    }


    public void setRowEnd(String rowEnd) {
        RowEnd = rowEnd;
    }

    public void setRRn(String RRn) {
        this.RRn = RRn;
    }

    public void setJckTransactionId(String jckTransactionId) {
        JckTransactionId = jckTransactionId;
    }

    /*{
    "AgentCode": "JC0A13387",
    "Merchant": "JUSTCLICKTRAVELS",
    "Fromdate":"20210211",
    "Todate":"20210212",
    "RowStart":"1",
    "RowEnd":"4",
    "RRn":"",
    "JckTransactionId":""
}*/
}
