package com.justclick.clicknbook.Fragment.flights.requestModels;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightSearchRequestModel implements Serializable{
    public int adultCount, childCount, infantCount;
    public boolean isDomestic;
    public String journeyType, preferredAirlines, cabinClass;
//    extra params from and to city
    public String fromCity, toCity;
    public ArrayList<segments> segments;
    public class segments implements Serializable {
        public String origin, destination, departureDate, returnDate,
        originCityName, destinationCityName;
    }
}
