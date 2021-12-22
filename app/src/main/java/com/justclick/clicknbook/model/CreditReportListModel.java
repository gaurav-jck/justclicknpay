package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class CreditReportListModel
{
    public String Status, StatusCode;
    public ArrayList<CreditReportData> Data;

    public class CreditReportData {
        public String Serial, AgencyName, Sid, Amount, LastUpdateDate, TCount;
    }

}
