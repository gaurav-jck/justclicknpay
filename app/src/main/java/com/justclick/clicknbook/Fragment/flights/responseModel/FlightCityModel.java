package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.util.ArrayList;

public class FlightCityModel extends CommonFlightResponse{
        public ArrayList<response> response;
        public String sessionId;
        public class response{
            public int id;
            public String airportCode, airportName, cityCode, cityName, countryCode, countryName;
        }

    }