package com.justclick.clicknbook.Fragment.bus.requestModel;

import com.justclick.clicknbook.requestmodels.CommonRequestModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pc1 on 5/18/2017.
 */

public class BusBookingRequestModel extends CommonRequestModel implements Serializable{
    public BLReq BLReq;

    public class BLReq implements Serializable{
        public String DeviceId, DoneCardUser, LoginSessionId, availableTripId, boardingPointId,
                destination, source, BusOpertor, BusType, LeadPaxName, Remarks, Location,
                BTIme, DPoint, OPTNum, JourrnyDate,DTime, BPoint, CancellationPolicy,
                Address, LeadMail, LandMark;
        //        public inventoryItems inventoryItems;
        public ArrayList<inventoryItems> inventoryItems;
        public long LeadMobile;
        public boolean PartialFlag;

    }

   /* public class inventoryItems{
        public ArrayList<inventoryItem> inventoryItem;
    }*/

    public class inventoryItems implements Serializable{
        public float Commission, NetTFare, ServiceCharge, TDS, fare, AgMarkup, Basefare, BookingFee,
        ServiceFee,bankTrexAmt,childFare,levyFare,markupFareAbsolute,markupFarePercentage,
        operatorServiceChargeAbsolute,operatorServiceChargePercent,serviceTaxAbsolute,
        serviceTaxPercentage,srtFee,tollFee;
        public String seatName;
        public ArrayList<passenger> passenger;
        public boolean ladiesSeat;
    }

    /* public class passenger{
         public passengerS passengerS;
     }
 */
    public class passenger implements Serializable{
        public String address, gender, idNumber, idType, name, primary, title;
        public int age;
        public long mobile;
    }
}
