package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class JioAmountData
{
    public String Status, StatusCode;
    public ArrayList<JioData> Data;

    public class JioData {
        public String Circle, Name, Id, Price, Description;
    }

}
