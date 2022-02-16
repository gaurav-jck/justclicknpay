package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PnrStatusResponse implements Serializable {
    public String statusCode, statusMessage;
    public pnrEnqueryresponse pnrEnqueryresponse;

    public class pnrEnqueryresponse{
        public String boardingPoint, bookingDate, bookingFare, chartStatus, connectingPnrFlag, dateOfJourney, delay,
                departureTime, destinationStation, journeyClass, linkPnr, linkPnrFlag, mobileNo, numberOfpassenger,
                pnrNumber, quota, reasonType, reservationUpto, serverId, sourceStation, ticketFare, ticketType,
                timeStamp, timeTableChangeFlag, trainCancelStatus, trainName, trainNumber, trainSiteId, errorMessage;
        public ArrayList<passengerinfo> passengerinfo;
    }

    public class passengerinfo{
        public String bookingBerthCode, bookingBerthNo, bookingCoachId, bookingStatus, bookingStatusIndex,
                currentBerthChoice, currentBerthCode, currentBerthNo, currentCoachId, currentStatus, currentStatusIndex,
                dropWaitlistFlag, fareChargedPercentage, passengerAge, passengerBerthChoice, passengerIcardFlag,
                passengerNationality, passengerSerialNumber, psgnwlType, validationFlag;
    }


    /*{"pnrEnqueryresponse":{"boardingPoint":"RK","bookingDate":"20220105","bookingFare":"45","chartStatus":null,
    "connectingPnrFlag":"N","dateOfJourney":"2022-01-28T00:00:00","delay":"0","departureTime":"83",
    "destinationStation":"SRE","informationMessage":null,"journeyClass":"2S","linkPnr":"","linkPnrFlag":"-1",
    "mobileNo":"","numberOfpassenger":"1","passengerinfo":[{"bookingBerthCode":null,"bookingBerthNo":"2",
    "bookingCoachId":"","bookingStatus":"RLWL","bookingStatusIndex":"0","currentBerthNo":"0","currentCoachId":"",
    "currentStatus":"CAN","currentStatusIndex":"0","dropWaitlistFlag":"false","fareChargedPercentage":"0.0",
    "passengerAge":"30","passengerBerthChoice":null,"passengerIcardFlag":"false","passengerNationality":"IN",
    "passengerSerialNumber":"1","psgnwlType":"12","validationFlag":"N"}],"pnrNumber":"6462094975","quota":"GN",
    "reasonType":"S","reservationUpto":"SRE","serverId":null,"sourceStation":"RK","ticketFare":"0","ticketType":"E",
    "timeStamp":"2022-01-06T17:32:19.927","timeTableChangeFlag":"0","trainCancelStatus":"GANGASUTLEJ EXP",
    "trainName":"GANGASUTLEJ EXP","trainNumber":"GANGASUTLEJ EXP","trainSiteId":"C"},"statusCode":"00","statusMessage":"Success"}*/

}
