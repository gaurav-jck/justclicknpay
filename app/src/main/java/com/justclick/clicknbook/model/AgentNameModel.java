package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 03/30/2017.
 */

public class AgentNameModel
{
    public String Status, StatusCode;
    public ArrayList<AgentName> Data;

    public class AgentName {
        public String AgencyName, Active;
    }
}
