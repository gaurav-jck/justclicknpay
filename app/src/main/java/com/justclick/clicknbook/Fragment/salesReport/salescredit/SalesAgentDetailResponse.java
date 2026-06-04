package com.justclick.clicknbook.Fragment.salesReport.salescredit;

import java.util.ArrayList;

public class SalesAgentDetailResponse {
    public String status, statusCode;
    public data data;

    public class data{
        int srno;
        public boolean autoCredit, allowMinusStatus;
        public String agencyName, uid, doneCardUser, salesCode, salesPersonName,
                approvedLimit, crexpiredate, balance,
                availableCredit, lastTxnDate;

    }
}
