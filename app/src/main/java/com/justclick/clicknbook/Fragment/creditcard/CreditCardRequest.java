package com.justclick.clicknbook.Fragment.creditcard;

import com.justclick.clicknbook.ApiConstants;

import java.io.Serializable;

public class CreditCardRequest implements Serializable {
    public String name, mobile, card_number, amount, network, remarks, agentcode,
            mode= "APP", merchantid=ApiConstants.MerchantId;

    public String transactionid, otp;

}
