package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 6/22/2018.
 */

public class JctMoneySenderDetailResponseModel implements Serializable{
    public String customer_id, message, currency, name, state;
    public long mobile;
    public int statusCode;
    public float Limit, remainingLimt;
    public ArrayList<BeneList> BeneList;

    public class BeneList implements Serializable{
        public String message, recipient_id, status, account, recipient_mobile, bank,
                recipient_name, ifsc, available_channel, is_verified;
        public boolean isVisible=false;
    }

}
