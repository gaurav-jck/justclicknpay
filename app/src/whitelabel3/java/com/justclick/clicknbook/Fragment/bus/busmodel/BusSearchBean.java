package com.justclick.clicknbook.Fragment.bus.busmodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by influence on 9/29/2015.
 */
public class BusSearchBean implements Serializable{
    public float rating_bar;
    public String departureTime, arrivalTime, bustype, routid, totalTime, totalHours, rating,
            travelName, Operator, seatCategory, chargebus, totalSeats, cancellationPolicy,doj, partialCancellationAllowed;
    public ArrayList<BusBoardingDroppingTimeBean> boardingTimes;
    public ArrayList<BusBoardingDroppingTimeBean> droppingTimes;
    public ArrayList<fareDetails> fareDetails;
    public ArrayList<fares> fares;
    public boolean isNetFare=false;

    public class BusBoardingDroppingTimeBean implements Serializable{
        public String address, bpId, bpName, time, contactNumber,landmark,location,prime;
        public boolean isClick=false;
    }

//    public class BusDroppingTimeBean implements Serializable{
//        public String address, bpId, bpName, time;
//        public boolean isClick=false;
//    }

    public class fareDetails implements Serializable{
        public float AgMarkup,Commission, GST,GrossFare, NetTotalFare, ServiceCharge, TDS, bankTrexAmt, baseFare, bookingFee,
                childFare, levyFare, markupFareAbsolute, markupFarePercentage, operatorServiceChargeAbsolute,
                operatorServiceChargePercentage, serviceTaxAbsolute, serviceTaxPercentage, srtFee,
                tollFee, totalFare;
    }

    public class fares implements Serializable{
        public float Fare;
    }

}
