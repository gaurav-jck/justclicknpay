package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 7/31/2017.
 */

public class JctMoneyTransactionStatusResponse {
    public String message;
    public int statusCode;
    public ArrayList<RefID> RefID;

    public class RefID{
        public String recipient_id_type, recipient_id, ifsc_status, txstatus_desc, account,
                name, ifsc, tid, txnID, BankRefNo;
        public float amount;
    }
}
