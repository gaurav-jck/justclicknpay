package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 03/30/2017.
 */

public class HotelCityListModel
{
    public String Status, StatusCode;
    public ArrayList<CityResponse> CityResponse;

    public class CityResponse {
        public String CityName, Country;
    }
}
