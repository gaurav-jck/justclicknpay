package com.justclick.clicknbook.Fragment.bus.busResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 9/12/2017.
 */

public class BusPrintResponse implements Serializable{
    public String BPoint,BTIme,BookingDate,BusOpertor,BusType,CancellationPolicy,DOJ,DPoint,
            DTime,DoneCardUser,From,LandMark,LeadEmail,PNR,Remark,ReservationId
            ,Status,TicketNo,To,TripID,UpdatedBy,UpdatedDate,TDS,AgencyName,AgentMarkup,GST,LeadMobile
    ,NetFare,Refund,TxnMedium,BaseFare, PartialFlag;
    public Float Discount,ServiceCharge,TotalFare;
    public ArrayList<InventoryItems> inventoryItems;

    public class InventoryItems implements Serializable
    {
        public String ladiesSeat,seatName;
        public Float Commission,NetTFare,ServiceCharge,TDS,fare;
        public ArrayList<Passenger> passenger;

        public class Passenger implements Serializable
        {
            public String  address,gender,idNumber,idType,name,primary,title;
            public int age,mobile;
        }

    }
}
