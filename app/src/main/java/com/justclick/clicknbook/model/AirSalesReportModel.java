package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class AirSalesReportModel
{
    public String Status, StatusCode;
    public ArrayList<Data> Data;

    public class Data {
        public String AgencyName, Basic, Discount, NetFare, NumberOfBooking, NumberOfRefund,
                Serial, TCount, TotalFare, TotalRefund, YQ;
    }

}
