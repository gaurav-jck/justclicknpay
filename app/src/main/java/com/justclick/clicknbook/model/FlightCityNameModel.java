package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 03/30/2017.
 */

public class FlightCityNameModel
{
    public ArrayList<FlightCityName> Data;

    public class FlightCityName {
        public String AirPortName, CityCode, CityName, CountryCode, CountryName;
    }
}
