package com.justclick.clicknbook.Fragment.train.model;

import com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist.AirBookingListResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainListResponse implements Serializable{
    public String statusCode, statusMessage;
    public int count;
    public ArrayList<reservationlist> reservationlist;

    public class reservationlist implements Serializable {
        public String txnMedium, pnRno, trainNumber, paxname, boardingDate, source, destination, status,
                mobile, departDate, corporatename, reservationID, agencyName, supplerConfirmationID,
                reservation_Status, custId, reservationdate, sid, remark, donecarduser, journeyClass, journeyQuota, trainname;
        public int rowNum, totalCount, smSflag;
        public int totalPassenger, statusFlag;
        public float serviceCharge, ticketFare, agentMarkup, print_ServiceCharge, print_AgentMarkup,
                print_PGCharges, print_TotalFare, pgCharges, totalFare;

//        need params
//        journeyClass, journeyQuota, train name, sourceStation, destinationStation
    }
}
