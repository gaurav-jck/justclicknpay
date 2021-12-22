package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 05/23/2017.
 */

public class CityNameResponseModel
{
    public String Status,StatusCode;
    public ArrayList<CityData> Data;

    public class CityData
    {
        public String CityName,TCount;
    }
}


