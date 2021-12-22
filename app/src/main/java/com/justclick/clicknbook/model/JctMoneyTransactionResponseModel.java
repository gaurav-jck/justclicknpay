package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 6/22/2018.
 */

public class JctMoneyTransactionResponseModel implements Serializable{

    public String Message, Status;
    public int StatusCode;
    public ArrayList<Response> Response;

    public class Response{
        public String message, response_type_id, response_status_id, status, JCTTxnID;
        public int statusCode;
        public ArrayList<Data> data;

        public class Data implements Serializable{
            public String timestamp, tx_status, txstatus_desc, debit_user_id, balance, fee,
                    state, recipient_id, service_tax, transaction_id, JCTRefID, customer_id,
                    channel, currency, client_ref_id, tid, account, bank, bank_ref_num,
                    ifsc, TxnType;
            public float amount, DistCom, JCTCom, MarkUp, CGST, SupMkp;
        }
    }
}
