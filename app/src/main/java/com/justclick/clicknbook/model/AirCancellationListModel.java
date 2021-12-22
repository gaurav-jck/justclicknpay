package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class AirCancellationListModel
{
    public String Status, StatusCode;
    public ArrayList<AirCancelListData> Data;

    public class AirCancelListData {
        public String AgencyName, AirLineInbound, AirLineTicketPnr, AirLineoutOutBound,
                CancellationDateId, FromTo, GdsPnr, ReservationDateId,
                TotalFare, Serial, status, Remarks, TCount;
    }

}
