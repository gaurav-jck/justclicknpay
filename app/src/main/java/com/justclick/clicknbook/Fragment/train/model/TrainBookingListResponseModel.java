package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainBookingListResponseModel {
    public String statusCode, statusMessage;
    public int count;
    public ArrayList<reservationlist> reservationlist;

    public class reservationlist implements Serializable {
        public String reservationID, trainName, trainNumber, source, destination, boardingDate,
                totalFare, totalPassenger, reservationDate, status, journeyClass, journeyQuota,
                departDate, arriveDate, distance, pnRno, boardingStn, mobile, agentMobile,
                sourceCode, destinationCode;
    }

}
