package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PnrResponse implements Serializable {
    public String statusCode, statusMessage, reservationid, pnr;
    public ArrayList<transactionDetails> transactionDetails;
    public ArrayList<passengerDetails> passengerDetails;
    public ArrayList<agentDetails> agentDetails;
    public ArrayList<fareDetails> fareDetails;

    public class transactionDetails implements Serializable{
        public String pnrNo, transactionID, from, boarding, resvUpto, passengerMobileNo, passengerAddress,
                trainName, dateTimeOfBooking, dateOfJourney, dateOfBoarding, scheduledArrival, distance,
                quota, journeyClass, to, scheduledDeparture, adult, child, duration, trainnumber;
    }

    public class passengerDetails implements Serializable{
        public String sNo, name, age, sex, bookingStatus, currentStatus;
    }

    public class agentDetails implements Serializable{
        public String principleAgent, agentName, address, corporateName,
                contactNumber, emailID, ticketPrintingTime;
    }

    public class fareDetails implements Serializable{
        public String ticketFar, cateringCharge, convenienceFee, travelInsurancePremium,
                travelAgentServiceCharge, pgCharges, totalFare;
    }
}
