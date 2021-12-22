package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class AirRefundReportModel
{
    public String Status, StatusCode;
    public ArrayList<AirRefundReportData> Data;

    public class AirRefundReportData {
        public String AgencyName, AirLinePnr, Amount, CancelDate,
                CancellationCharges, CancellationDateId, ReferenceId, RefundDate,
                RefundedAmount, Serial,Remarks, TCount;
    }

}
