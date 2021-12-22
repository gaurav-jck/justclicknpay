package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 05/23/2017.
 */

public class StateNameResponseModel
{
    public String Status,StatusCode;
    public ArrayList<StateData> Data;

    public class StateData
    {
        public String StateName,TCount;

    }
}
