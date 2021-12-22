package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PnrStatusResponse implements Serializable {
    public String boardingPoint, bookingDate, bookingFare, chartStatus, connectingPnrFlag, dateOfJourney, delay,
            departureTime, destinationStation, journeyClass, linkPnr, linkPnrFlag, mobileNo, numberOfpassenger,
            pnrNumber, quota, reasonType, reservationUpto, serverId, sourceStation, ticketFare, ticketType,
            timeStamp, timeTableChangeFlag, trainCancelStatus, trainName, trainNumber, trainSiteId;
    public String[] informationMessage;
    public ArrayList<passengerList> passengerList;

    public class passengerList implements Serializable{
        public String bookingBerthCode, bookingBerthNo, bookingCoachId, bookingStatus, bookingStatusIndex,
                currentBerthChoice, currentBerthCode, currentBerthNo, currentCoachId, currentStatus, currentStatusIndex,
                dropWaitlistFlag, fareChargedPercentage, passengerAge, passengerBerthChoice, passengerIcardFlag,
                passengerNationality, passengerSerialNumber, psgnwlType, validationFlag;
    }

}
