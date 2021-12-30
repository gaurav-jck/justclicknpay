package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainCancelTicketDetailResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<paxCancelDetail> paxCancelDetail;
    public ArrayList<trainCancelDetail> trainCancelDetail;

    public class paxCancelDetail implements Serializable{
        public boolean paxCancelStatus;
        public int sno, age, refundAmount;
        public String name, gender, bookingStatus, currentStatus, refundStatus, cancellationId, paxPnr;
    }

    public class trainCancelDetail implements Serializable{
        public String transactionId, pnr, trainNo, trainName, doj, dob, fromStation, fromStationCode,
                toStation, toStationCode, quota, /*class,*/boardingStation, boardingStationCode,
                reservationUpto, reservationUptoCode, dateOfBoarding;
    }
}
