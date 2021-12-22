package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FareRuleResponse implements Serializable {
    public String baseFare, distance, dynamicFare, enqClass, from, to, quota, reqEnqParam,
            reservationCharge, serverId, serviceTax, superfastCharge, tatkalFare, timeStamp,
            totalFare, trainName, trainNo, errorMessage, availablityDate, availablityStatus;
    public ArrayList<avlDayList> avlDayList;
    public bkgCfg bkgCfg;

    public class avlDayList implements Serializable{
        public String availablityDate, availablityStatus;
    }

    public class bkgCfg implements Serializable{
        public String[] applicableBerthTypes, foodDetails;
        public String foodChoiceEnabled;
    }

    /*{"baseFare":"289","cateringCharge":"0","distance":"187","dynamicFare":"87","enqClass":"CC",
    "from":"NDLS","fuelAmount":"0.0","insuredPsgnCount":"0","nextEnqDate":"2021-10-28T00:00:00",
    "otherCharge":"0","otpAuthenticationFlag":"0","preEnqDate":"2021-10-21T00:00:00","quota":"GN",
    "reqEnqParam":"02017#NDLS#SRE#NDLS#CC","reservationCharge":"40","serverId":"DM07AP33MS1",
    "serviceTax":"24.0","superfastCharge":"45","tatkalFa":"0re,""timeStamp":"2021-10-21T23:18:12.138",
    "to":"SRE","totalConcession":"0","totalFare":"485","trainName":"DDN SHTABDI SPL","trainNo":"02017",
    "travelInsuranceCharge":"0.0","travelInsuranceServiceTax":"0.0","wpServiceCharge":"30.0",
    "wpServiceTax":"5.4","avlDayList":[{"availablityDate":"22-10-2021","availablityStatus":"CURR_AVBL-0141",
    "availablityType":"1","currentBkgFlag":"Y","reason":"","reasonType":"S","wlType":"0"},
    {"availablityDate":"23-10-2021","availablityStatus":"GNWL51/WL37","availablityType":"3",
    "currentBkgFlag":"N","probability":"0.8726539351366738","reason":"","reasonType":"S","wlType":"9"},
    {"availablityDate":"24-10-2021","availablityStatus":"AVAILABLE-0279","availablityType":"1",
    "currentBkgFlag":"N","reason":"","reasonType":"S","wlType":"0"},{"availablityDate":"25-10-2021",
    "availablityStatus":"AVAILABLE-0365","availablityType":"1","currentBkgFlag":"N","reason":"",
    "reasonType":"S","wlType":"0"},{"availablityDate":"26-10-2021","availablityStatus":"AVAILABLE-0536",
    "availablityType":"1","currentBkgFlag":"N","reason":"","reasonType":"S","wlType":"0"},
    {"availablityDate":"27-10-2021","availablityStatus":"AVAILABLE-0575","availablityType":"1",
    "currentBkgFlag":"N","reason":"","reasonType":"S","wlType":"0"}],"bkgCfg":{"acuralBooking":"false",
    "applicableBerthTypes":"WS","atasEnable":"false","bedRollFlagEnabled":"false","beyondArpBooking":"false",
    "bonafideCountryList":["NP","BT"],"captureAddress":"1","childBerthMandatory":"true",
    "foodChoiceEnabled":"false","forgoConcession":"false","gatimaanTrain":"false","gstDetailInputFlag":"true",
    "gstinPattern":"^([0]{1}[1-9]{1}|[1]{1}[0-9]{1}|[2]{1}[0-7]{1}|[2]{1}[9]{1}|[3]{1}[0-7]{1})
    [A-Za-z]{5}[0-9]{4}[A-Za-z]{1}[a-zA-Z0-9]{3}$","idRequired":"false","lowerBerthApplicable":"false",
    "maxARPDays":"120","maxChildAge":"11","maxIdCardLength":"16","maxInfants":"2","maxMasterListPsgn":"6",
    "maxNameLength":"16","maxPassengerAge":"125","maxPassengers":"6","maxPassportLength":"9",
    "maxRetentionDays":"5","minIdCardLength":"4","minNameLength":"3","minPassengerAge":"5",
    "minPassportLength":"6","newTimeTable":"true","pmfInputEnable":"false","pmfInputMandatory":"false",
    "pmfInputMaxLength":"0","redemptionBooking":"false","seniorCitizenApplicable":"false","specialTatkal":"false",
    "srctnwAge":"58","srctzTAge":"60","srctznAge":"60","suvidhaTrain":"false","trainsiteId":"D",
    "travelInsuranceEnabled":"true","travelInsuranceFareMsg":"Do you want to take Travel Insurance\n(₹0.49/person)?",
    "twoSSReleaseFlag":"false","uidMandatoryFlag":"0","uidVerificationMasterListFlag":"2",
    "uidVerificationPsgnInputFlag":"0","validIdCardTypes":["NULL_IDCARD","DRIVING_LICENSE","PASSPORT","PANCARD",
    "VOTER_ICARD","GOVT_ICARD","STUDENT_ICARD","BANK_PASSBOOK","CREDIT_CARD","UNIQUE_ICARD"]},
    "ftBookingMsgFlag":"false","informationMessage":[{"message":"Dynamic Pricing is applicable in this train.
     Fare may increase at the time of booking.\n","paramName":"AVL_DTLS","popup":"true"},{"message":"* Dynamic
     Pricing is applicable in this train. Fare may increase at the time of booking.\n* Rounding off  to next
     multiple of Rs. 5 is included in Base fare.\n","paramName":"FARE_BREAKUP","popup":"false"},
     {"message":"Full fare will be applicable in case of child passengers.","paramName":"ADD_PASSENGER_INIT",
     "popup":"true"},{"message":"Forgo Concession Opted","paramName":"FORGO_CONC","popup":"false"}],
     "lastUpdateTime":"29 Minutes and 4 Seconds","rdsTxnPwdFlag":"false","taRdsFlag":"false",
     "totalCollectibleAmount":"520.4","upiRdsFlag":"false"}*/
}
