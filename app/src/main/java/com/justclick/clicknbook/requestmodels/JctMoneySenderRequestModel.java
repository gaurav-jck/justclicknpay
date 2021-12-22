package com.justclick.clicknbook.requestmodels;

import java.io.Serializable;

/**
 * Created by gaurav.singhal on 9/12/2017.
 */

public class JctMoneySenderRequestModel extends CommonRequestModel implements Serializable{
    public String MobileNo, Name, OTP, recipient_id,customer_id, TxnType,
            customer_name, AccNo, ifsc, Pin, TPin;
    public boolean benreq;
    public float Amount;
}
