package com.justclick.clicknbook.Fragment.flights.requestModels;

import com.justclick.clicknbook.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightBookingRequest implements Serializable {
    public String sessionId, resultId, email, mobile;
    public GST GST;
    public ArrayList<passengers> passengers;

    public class passengers implements Serializable {
        public int title= Constants.Salutation.MR;
        public String firstName, lastName, paxType, dateOfBirth, passportNo, passportExpiry,
                gender, FFAirline, FFNumber, contactNo, email;
        public boolean isLeadPax;
    }

    public class GST implements Serializable {
        public String GSTCompanyAddress, GSTCompanyContactNumber, GSTCompanyName, GSTNumber, GSTCompanyEmail;
    }

}