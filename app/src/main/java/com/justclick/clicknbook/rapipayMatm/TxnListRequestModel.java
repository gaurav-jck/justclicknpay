package com.justclick.clicknbook.rapipayMatm;

import com.justclick.clicknbook.ApiConstants;

public class TxnListRequestModel {
    private String AgentCode,UserType,Merchant= ApiConstants.MerchantId, Fromdate, Todate, RowStart, RowEnd, RRn, JckTransactionId;

    public void setUserType(String userType) {
        UserType = userType;
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
