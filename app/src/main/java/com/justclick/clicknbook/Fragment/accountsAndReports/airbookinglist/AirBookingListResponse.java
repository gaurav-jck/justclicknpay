package com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist;

import java.util.ArrayList;

public class AirBookingListResponse {
    public String statusCode, statusMessage;
    public int totalCount;
    public ArrayList<travelsListDetail> travelsListDetail;

    public class travelsListDetail{
        public String srno, createdDate, statusDate, id, doneCardUser, mobileNo, orderno,
                reforderid, amount, paxMobileNumber, paxName, travelType, tripType,
                bookingType, refundAmt, rowscount;
        public int totalCount;
    }

    /*{"statusCode": "00",
  "statusMessage": "Success",
  "totalCount": 83,
  "travelsListDetail": [
    {
      "srno": "1",
      "createdDate": "2023-09-22 19:34:25",
      "statusDate": "9/22/2023 7:34:25 PM",
      "id": "29582",
      "doneCardUser": "JC0A42730 (ANAND TRAVEL)",
      "mobileNo": "8436796593",
      "orderno": "TRVL220923K496193425283",
      "reforderid": "FBB62VWP",
      "amount": "15794.340",
      "paxMobileNumber": "8436796593",
      "paxName": "SURESH KUMAR/SHAW",
      "travelType": "DOMESTIC",
      "tripType": "ONE_WAY",
      "bookingType": "Airline",
      "refundAmt": "0.000",
      "rowscount": null
    }]}*/
}
