package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class DepositReportListModel
{
    public String Status, StatusCode;
    public ArrayList<DepositReportData> Data;

    public class DepositReportData {
        public String Serial, AgencyName, ReceiptNo, BankName, Date, Sid, Amount,
                DepositDate, Remarks, TypeOfAmount, TCount, AvailableCredit;
    }

}
