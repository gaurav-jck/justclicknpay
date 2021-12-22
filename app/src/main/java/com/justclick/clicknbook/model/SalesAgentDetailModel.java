package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 10/03/2018.
 */

public class SalesAgentDetailModel
{
    public String Status, StatusCode;
    public ArrayList<Data> Data;

    public class Data {
        public String ActiveFlag, AgencyName, AvailableCredit,Avlamount,
                CreditValidDate,Serial,StatementBalance,donecarduser,TCount;
    }
}

