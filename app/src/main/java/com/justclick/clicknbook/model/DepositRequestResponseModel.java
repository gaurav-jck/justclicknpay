package com.justclick.clicknbook.model;

/**
 * Created by pc1 on 5/22/2017.
 */

public class DepositRequestResponseModel {

    public DepositRequestResult DepositRequestResult;
    public DepositRequestResult depositRequestResult;
    public class DepositRequestResult {

    public String Status, StatusCode;
    public Data Data;

    public class Data {
        public String Message;
        }
    }
}
