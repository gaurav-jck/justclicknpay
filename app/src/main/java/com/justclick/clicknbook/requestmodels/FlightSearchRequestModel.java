package com.justclick.clicknbook.requestmodels;

import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 11/7/2017.
 */

public class FlightSearchRequestModel extends CommonRequestModel {
    public String Class, PAirLine;
    public int Adult, Child, Infant;
    public ArrayList<Sectors> Sectors;

    public class Sectors{
        public String Date, Dest, Origin;
    }
}
