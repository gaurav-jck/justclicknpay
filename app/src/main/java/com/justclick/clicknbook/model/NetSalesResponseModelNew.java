package com.justclick.clicknbook.model;

import java.util.ArrayList;


public class NetSalesResponseModelNew
{

    public String statusCode, statusMessage;
    public int totalCount;

    public ArrayList<aadharPayResponceList> aadharPayResponceList;

    public class aadharPayResponceList{
        public float totalRecharge, totalRechargeDistCom, totalNetRecharge, recharge, distCom,
                netAmount;
        public String agencyname, agencycode;
    }
}
