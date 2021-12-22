package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class NetSalesResponseModel
{
    public String Status, StatusCode,AirTotal,RailTotal,DMTTotal,MobileTotal,Totalsales;
    public ArrayList<Data> Data;

    public class Data {
        public String AgencyName, AirPay, DMTPay, MobilePay, RailPay,RailTotal,
                Serial, TCount,TotalPay;
    }
}
