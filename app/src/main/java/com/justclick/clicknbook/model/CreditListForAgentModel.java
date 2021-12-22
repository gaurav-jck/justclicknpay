package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class CreditListForAgentModel
{
    public String Status, StatusCode;
    public ArrayList<CreditReportData> Data;
    public ArrayList<DataAmt> DataAmt;

    public class CreditReportData {
        public String Serial,CreditedAmount,RequestedAmount,
                RequestedDate, UpdatedBy,AgencyName, UpdatedDate, TCount,
                TransactionStatus, Remarks;

//        CreditReportData(String Serial){
//
//        }

    }

    public class DataAmt {
        public String TotalCreditAmount, TotalPendingAmount, TotalRejectedAmount;
    }
}
