package com.justclick.clicknbook.Fragment.qrupicash;

import com.justclick.clicknbook.ApiConstants;

public class UpiCashRequest {
    private String Merchant= ApiConstants.MerchantId, Mode=ApiConstants.ModeApp;
    public String Latitude, Longitude, AgentCode, Ip, Mobile;
    public int Amount;
}
