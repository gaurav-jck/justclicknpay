package com.justclick.clicknbook.requestmodels;

/**
 * Created by gaurav.singhal on 8/19/2017.
 */

public class RblTransactionRequest extends CommonRequestModel {
    public String AccNo, RMobile, TType, remarks;
    public int amount;
    public long TPin;
}
