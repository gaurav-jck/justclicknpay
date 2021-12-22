package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 03/30/2017.
 */

public class RblPinDetailResponseModel
{
    public String Status, StatusCode;
    public Data Data;

    public class Data {
        public String Districtname, statename;
    }
}
