package com.justclick.clicknbook.Fragment.train.model;

import java.util.Date;

public class TrainFinalBookingResponse {

    private PsgnDtlList[] psgnDtlList;
    private MealTransaction mealTransaction;
    private GstCharge gstCharge;

    public Date boardingDate, journeyDate, timeStamp, bookingDate, destArrvDate;

    private String boardingStn, serverId, trainName, arrivalTime, boardingStnName, bookedQuota, clientTransactionId,
            departureTime, destStn, fromStn, fromStnName, journeyClass, journeyQuota;
    public String pnrNumber;
    public String[] informationMessage;
    private String reasonType;
    private String reservationId;
    private String resvnUptoStn;
    private String resvnUptoStnName;
    private String rrHbFlag;
    private double fuelAmount;
    private double wpServiceCharge, serviceTax;
    private double wpServiceTax;
    private boolean avlForVikalp;
    private boolean canSpouseFlag;
    private int cateringCharge;
    private int distance;
    private int insuredPsgnCount;
    private int otpAuthenticationFlag;
    private int reservationCharge;
    private int superfastCharge;
    private int tatkalFare;
    private int totalFare;
    private int trainOwner;
    private int complaintFlag;
    private int journeyLap;
    private int lapNumber;
    private boolean gstFlag;
    private double insuranceCharge;

    private boolean mahakalFlag;
    private boolean mealChoiceEnable;
    private boolean multiLapFlag;
    private int mlJourneyType;
    private int mlReservationStatus;
    private int mlTimeDiff;
    private int mlTransactionStatus;
    private int mlUserId;
    private int monthBkgTicket;
    private int numberOfAdults;
    private int numberOfChilds;
    private int numberOfpassenger, reasonIndex;
    private boolean sai;
    private boolean scheduleArrivalFlag;
    private boolean scheduleDepartureFlag;
    private boolean sectorId;
    private double serviceChargeTotal;
    private String ticketType;
    private int timeDiff;
    private int timeTableFlag;
    private double totalCollectibleAmount;
    private String tourismUrl;
    private String trainNumber;

    public class GstCharge {
        private double irctcCgstCharge;
        private double irctcIgstCharge;
        private double irctcSgstCharge;
        private double irctcUgstCharge;
        private double totalIrctcGst;
        private double totalPRSGst;


    }

    public class MealTransaction {
        private int bookingMode;
        private int bookingSource;
        private boolean mealBooked;
        private int reservationId;
        private int ticketId;
        private int tktCanStatus;
    }

    public class PsgnDtlList {
        private String bookingBerthNo;
        private String bookingCoachId;
        private String bookingStatus;
        private String bookingStatusIndex;
        private String childBerthFlag;
        private String currentBerthNo;
        private String currentCoachId;
        private String currentStatus;
        private String currentStatusIndex;
        private String dropWaitlistFlag;
        private String fareChargedPercentage;
        private String insuranceIssued;
        private String passengerAge;
        private String passengerBedrollChoice;
        private String passengerConcession;
        private String passengerGender;
        private String passengerIcardFlag;
        private String passengerName;
        private String passengerNationality;
        private String passengerNetFare;
        private String passengerSerialNumber;
        private String psgnwlType;
        private String validationFlag;

    }
}
