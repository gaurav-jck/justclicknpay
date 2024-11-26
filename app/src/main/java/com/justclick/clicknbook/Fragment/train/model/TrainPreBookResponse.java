package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainPreBookResponse implements Serializable {
    public String statusCode, statusMessage;
    public bookingDetails bookingDetails;
    public ArrayList<journeyDetails> confirmJourneyDetail;
    public ArrayList<paymentDetails> paymentDetails;
    public ArrayList<availableblityDetail> availableblityDetail;
    public ArrayList<confirmPaxDetail> confirmPaxDetail;
    public ArrayList<confirmFareDetail> confirmFareDetail;
    public ArrayList<finalBookingFareAndJourneyDetail> finalBookingFareAndJourneyDetail;
    public class bookingDetails implements Serializable{
        public ArrayList<journeyDetails> journeyDetails;
        public ArrayList<destinationDetail> destinationDetail;
        public ArrayList<passengerAdditionalDetail> passengerAdditionalDetail;
        public ArrayList<gstDetails> gstDetails;
        public ArrayList<fareDetail> fareDetail;
        public ArrayList<adultRequest> adultRequest;
        public ArrayList<childRequest> childRequest;
        public String transactionId, merchant, mode, userData, loggedInUserType, loggedInAgentCode;
    }

    public class paymentDetails implements Serializable{
        public float availableAmount, payableAmount, remainingWalletAmount;
    }

    public class availableblityDetail implements Serializable{
        public String avaialbleSeats, avaialbleDate;
    }

    public class confirmPaxDetail implements Serializable{
        public String name, age, gender, birthPrefrence, seniorCitizen, senoirCitezenConsesion,
                optBirth, nationality="IN", idTypeNumber;
    }

    public class confirmFareDetail implements Serializable{
        public float ticketFare, agentServiceCharge, concession, insurance;
        public float serviceCharge, pgCharge, totalFare;
    }

    public class journeyDetails implements Serializable{
        public String trainName, trainNo, fromStation, toStation, fromStationCode, toStationCode, boardingStation, boardingStationCode,
                reservationUpTo, reservationUpToCode, journeyClass, quota, bookingFlag, departTime, arrivalTime, journeyDate, moreThanOneDay,
                enquiryType, reservationChoice, ticketType, reservationMode="MOBILE_ANDROID", travelInsuranceOpted;
        public Boolean seniorCitizenApplicablecheck;
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
        public int baseFare, serviceCharge, agentServiceCharge, concession, pgCharge, totalFare;
    }
    public class adultRequest implements Serializable{
        public String passengerName, passengerAge, passengerGender, currentBerthChoice, passengerCardNumber, passengerBerthChoice,
                passengerNationality="IN", passengerCardType, passengerConcession, forgoConcession, passengerIcardFlag,
                passengerBedrollChoice="false", passengerFoodChoice, passengerSerialNumber="1", concessionOpted="True";
        public int type, position;

    }
    public class childRequest implements Serializable{
        public String passengerName, passengerAge, passengerGender;
    }

    public class finalBookingFareAndJourneyDetail implements Serializable{
        public float totalFare, wpServiceTax, wpServiceCharge, travelInsuranceCharge, travelInsuranceServiceTax,
                cateringCharge;
        public String journeyClass;
    }
    /*{
    "bookingDetails": {
        "journeyDetails": [
            {
                "trainName": "SHRAMJIVI EXP SP",
                "trainNo": "12392",
                "fromStation": "NEW DELHI",
                "toStation": "PATNA JN",
                "fromStationCode": "NDLS",
                "toStationCode": "PNBE",
                "boardingStation": "NDLS",
                "boardingStationCode": "NDLS",
                "reservationUpTo": "PATNA JN",
                "reservationUpToCode": "PNBE",
                "journeyClass": "2S",
                "quota": "GN",
                "bookingFlag": "Y",
                "departTime": "13:10",
                "arrivalTime": "07:05",
                "journeyDate": "20211230",
                "moreThanOneDay": "True",
                "enquiryType": "3",
                "reservationChoice": "99",
                "ticketType": "E",
                "reservationMode": "MOBILE_ANDROID",
                "travelInsuranceOpted": "True",
                "seniorCitizenApplicablecheck": false
            }
        ],
        "passengerAdditionalDetail": [
            {
                "mobile": "8468862808",
                "coach": "string",
                "preference": "LB",
                "email": "iamrohitesh@gmail.com",
                "remarks": "test",
                "adultCount": 1,
                "childCount": 0,
                "totalPaxCount": 1
            }
        ],
        "gstDetails": [
            {
                "gst": "",
                "flat": "",
                "gstName": "",
                "pinCode": "",
                "stateCity": "",
                "city": ""
            }
        ],
        "fareDetail": [
            {
                "baseFare": 0,
                "serviceCharge": 0,
                "agentServiceCharge": 0,
                "concession": 0,
                "pgCharge": 0,
                "totalFare": 0
            }
        ],
        "destinationDetails": [
            {
                "address": "kurthaul",
                "pinCode": "804453",
                "stateName": "BIHAR",
                "city": "Patna",
                "postOffice": "Kurthaul B.O"
            }
        ],
        "adultDetails": [
            {
                "passengerName": "ROHITESH KUMAR",
                "passengerAge": "29",
                "passengerGender": "M",
                "currentBerthChoice": "",
                "passengerCardNumber": "",
                "passengerBerthChoice": "",
                "passengerNationality": "IN",
                "passengerCardType": "",
                "passengerConcession": "",
                "forgoConcession": null,
                "passengerIcardFlag": "",
                "passengerBedrollChoice": "false",
                "passengerFoodChoice": "",
                "passengerSerialNumber": "1",
                "concessionOpted": "True"
            }
        ],
        "childDetails": [
            {
                "passengerName": "",
                "passengerAge": "",
                "passengerGender": ""
            }
        ],
        "transactionId": "R27111Z6DTJC0A13387",
        "finalBookingFareAndJourneyDetailsDto": null,
        "merchant": "JUSTCLICKTRAVELS",
        "mode": "WEB",
        "userData": "7ZlJvRhRqbYOTEcL95+mQj/0rckjPSR+kxLdFASyrBbDWRcNoAKGLjvz2KXcTkeVnP9VXzqez07z3hTnJSDhi/bBcw0hoL2WyeQSUGUkBkL7cAxDTHv45XFERsx+bDB4gr0RfsZDIVBNWi0FRogZa+4TrjSqUDSN9+Ck/ARCCqFKcAM1whj2TwbgQXsUUMfi4uok6/ZPF30sEjm6uk2RY3IcXNCk4nlVhrdUMwvM4mpzHJePDSe0/nEP0GDeXm1B36Db4fvKgUwq3c78+IbIhuAw0JpDvr1vTjSx9uDmuuMP9ls8baQ865gEtZ5Pfr+jJusoHMJH4V8p3ilhWjI7gmc2cNFApwscZ6DlU76ou1DvYZm1kMkVCb99bsOxlgqB7GsxsN1fJ1n7UhR4Xdplr9NNEqMo4f61tplvq6jZYhfmRBp6U1Z6sEAWPuKnHs631uTV6H0aWYczbTbFI4sFf6Qi/SiGzt4cii3iliK5Y3+ww6jukI4HzC+kQMFBCh0gZ1GDcbP7rIOtuHs1xn/W/KMyFokPretD1Rmu7DAU8sYbweePBzoHR6CrcmOHAnzi0SrLWUQ1u8Q8gN+KNeR4SmnFsy3mX01fkTJU8kQxx1ZmMlgtysZ5Q1hCpFnFILG/Joler0m7509bl1LSPdCa6BQk5N96NK4KgdGEbyjnItU=",
        "loggedInUserType": "A",
        "loggedInAgentCode": "JC0A13387"
    },
    "confirmJourneyDetail": [
        {
            "trainNameNo": "12392/SHRAMJIVI EXP",
            "doj": "30-12-2021",
            "class": "",
            "fromStation": "NEW DELHI-NDLS",
            "toStation": "PATNA JN-PNBE",
            "quota": "General",
            "boardingStation": "NDLS-NDLS",
            "reservationUpTo": "PATNA JN-PNBE",
            "insurence": "True",
            "prefrence": "LB",
            "aotoUpgradeChoice": "Yes",
            "distance": "1004KM",
            "departTime": "NDLS -13:10",
            "arrivalTime": "PNBE/07:05",
            "duration": ""
        }
    ],
    "paymentDetails": [
        {
            "availableAmount": 13552.19,
            "payableAmount": 362.46,
            "remainingWalletAmount": 13189.73
        }
    ],
    "availableblityDetail": [
        {
            "avaialbleSeats": "AVAILABLE-0359",
            "avaialbleDate": "30-12-2021"
        }
    ],
    "confirmPaxDetail": [
        {
            "name": "ROHITESH KUMAR",
            "age": "29",
            "gender": "Male",
            "birthPrefrence": "",
            "seniorCitizen": "No",
            "senoirCitezenConsesion": "",
            "optBirth": "",
            "nationality": "IN",
            "idTypeNumber": " /"
        }
    ],
    "confirmFareDetail": [
        {
            "ticketFare": 310,
            "serviceCharge": 30.0,
            "agentServiceCharge": 20,
            "concession": 0,
            "pgCharge": 2.46,
            "totalFare": 362.46,
            "insurance": 0
        }
    ],
    "finalBookingFareAndJourneyDetail": [
        {
            "totalFare": 328.05,
            "wpServiceTax": 15.0,
            "wpServiceCharge": 15.0,
            "travelInsuranceCharge": 0.3,
            "travelInsuranceServiceTax": 0.05,
            "cateringCharge": 0,
            "class": "2S"
        }
    ],
    "statusCode": null,
    "statusMessage": null
}*/
}
