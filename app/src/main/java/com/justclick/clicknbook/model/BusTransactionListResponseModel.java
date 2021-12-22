package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class BusTransactionListResponseModel implements Serializable
{
    public ArrayList<BusListModel> Data;
    public String Status, StatusCode;

    public class BusListModel implements Serializable{
        public String  BusOpertor, BusType, DoneCarduser, FromStation, JourrnyDate, LeadMobile, PrintFlag,PNR,
          ReservationID, Serial, Status,TDS, TCount, ToStation;
        public Float Discount,NetFare,TotalFare;
    }
}
