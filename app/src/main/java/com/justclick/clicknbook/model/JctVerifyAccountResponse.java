package com.justclick.clicknbook.model;

import java.io.Serializable;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class JctVerifyAccountResponse implements Serializable
{
    public String message, status;
    public int statusCode;
    public Data data;

    public class Data implements Serializable{
        public String client_ref_id, bank, verification_failure_refund, aadhar, recipient_name, ifsc, account, tid, fee;
        public float amount;
        public boolean is_name_editable, is_Ifsc_required;
    }
}
