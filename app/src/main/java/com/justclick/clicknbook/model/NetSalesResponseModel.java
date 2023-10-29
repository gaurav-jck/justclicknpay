package com.justclick.clicknbook.model;

import java.util.ArrayList;


public class NetSalesResponseModel
{
    public String Status, StatusCode,AirTotal,RailTotal,DMTTotal,MobileTotal,Totalsales,
            AepsTotal, MatmTotal, UtilityTotap;
    public ArrayList<Data> Data;

    public class Data {
        public String AgencyName, AirPay, DMTPay, MobilePay, RailPay,RailTotal,
                Serial, TCount,TotalPay,
                Aepspay, Matmpay, Utlitiypay;
    }

}
