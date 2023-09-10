package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

import java.util.ArrayList;

public class AccountStmtResponse {
    public String statusCode, statusMessage;
    public ArrayList<accountStatementList> accountStatementList;

    public class accountStatementList{
        public String agencyName, agencyCode, referenceid, txndate, txnAMTD, txnAMTC,
                balance, transactionType, currentCreditAmount, remarks, updatedBy, gds_code;
    }

    /*{
    "statusCode": "00",
    "statusMessage": "Success",
    "totalCount": 0,
    "accountStatementList": [
        {
            "agencyName": "HAPPY TELECOM",
            "agencyCode": "JC0A39969",
            "referenceid": "MA010837PKJJC0A39969",
            "txndate": "01/08/2023 20:35:46",
            "txnAMTD": "0.00",
            "txnAMTC": "1011.92",
            "balance": "1101.10",
            "transactionType": "MATM Booking",
            "currentCreditAmount": "0.00",
            "remarks": "",
            "updatedBy": "GURNAIB.S",
            "gds_code": ""
        }
    ]
}*/
}
