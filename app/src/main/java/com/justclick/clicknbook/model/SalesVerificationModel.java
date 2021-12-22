package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 10/03/2018.
 */

public class SalesVerificationModel
{
    public String Status, StatusCode;
    public ArrayList<Data> Data;

    public class Data {
        public String AgencyCode, AgencyName, DateOfJoining,DistributorId,
                Email,Mobile,Serial,TCount,Type,ValidationCode;
    }
}
