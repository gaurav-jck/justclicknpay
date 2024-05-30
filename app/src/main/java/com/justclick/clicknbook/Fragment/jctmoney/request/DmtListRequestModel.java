package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class DmtListRequestModel {
    private String AgentCode,UserType,Merchant= ApiConstants.MerchantId, Fromdate, Todate, RRn, JckTransactionId;
    private int RowStart, RowEnd;
    public String TxnType, DistCode, TxnDescription="", TxnStatus, LoggedinAgentCode, SenderMobile, AgentId;

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

    public void setRowStart(int rowStart) {
        RowStart = rowStart;
    }


    public void setRowEnd(int rowEnd) {
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
