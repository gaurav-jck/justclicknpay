package com.justclick.clicknbook.Fragment.cashoutnew.registration;

import java.util.ArrayList;

public class StateCityResponse {
    CityData CityData;

    public class CityData{
       public ArrayList<CityInfo> CityInfo;
    }

    public class CityInfo{
        public String City, State;
    }
}
