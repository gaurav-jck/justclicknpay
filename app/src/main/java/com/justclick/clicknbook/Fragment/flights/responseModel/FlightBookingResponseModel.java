package com.justclick.clicknbook.Fragment.flights.responseModel;


import java.io.Serializable;

public class FlightBookingResponseModel extends CommonFlightResponse implements Serializable {
    public String sessionId;
    public response response;
    public class response implements Serializable {
        public int userId;
        public String bookingId;
    }
}
