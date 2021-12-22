package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainBookingRequest implements Serializable {
    public String TransactionId, dateToSet, token, userDate, agentCode, userType, availableSeats;
    public ArrayList<journeyDetails> journeyDetails;
    public ArrayList<destinationDetail> destinationDetail;
    public ArrayList<passengerAdditionalDetail> passengerAdditionalDetail;
    public ArrayList<gstDetails> gstDetails;
    public ArrayList<fareDetail> fareDetail;
    public ArrayList<adultRequest> adultRequest;
    public ArrayList<childRequest> childRequest;
    public ArrayList<finalBookingFareAndJourneyDetails> finalBookingFareAndJourneyDetails;

    public class journeyDetails implements Serializable{
        public String trainName, trainNo, fromStation, toStation, fromStationCode, toStationCode, boardingStation, boardingStationCode,
                reservationUpTo, reservationUpToCode, journeyClass,JourneyClass, quota, bookingFlag, departTime, arrivalTime, journeyDate, moreThanOneDay,
                enquiryType, reservationChoice, ticketType, reservationMode="MOBILE_ANDROID", travelInsuranceOpted, duration;
    }
    public class destinationDetail implements Serializable{
        public String address, pinCode, stateName, city, postOffice;
    }
    public class passengerAdditionalDetail implements Serializable{
        public String mobile, coach, preference, email, remarks;
        public int  adultCount, childCount, totalPaxCount;
    }
    public class gstDetails implements Serializable{
        public String gst, flat, gstName, pinCode, stateCity, city;
    }
    public class fareDetail implements Serializable{
        public float baseFare, serviceCharge, agentServiceCharge, concession, pgCharge, totalFare;
    }
    public class adultRequest implements Serializable{
        public String passengerName, passengerAge, passengerGender, currentBerthChoice, passengerCardNumber, passengerBerthChoice,
                passengerNationality="IN", passengerCardType, passengerConcession, forgoConcession, passengerIcardFlag,
                passengerBedrollChoice="false", passengerFoodChoice="", passengerSerialNumber="1", concessionOpted="True";
        public int type, position;

    }
    public class childRequest implements Serializable{
        public String passengerName, passengerAge, passengerGender;
    }

    public class finalBookingFareAndJourneyDetails implements Serializable{
        public float totalFare, wpServiceTax, wpServiceCharge, travelInsuranceCharge, travelInsuranceServiceTax,
                cateringCharge;
        public String journeyClass;
    }

}
