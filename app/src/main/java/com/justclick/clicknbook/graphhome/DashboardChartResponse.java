package com.justclick.clicknbook.graphhome;

import java.util.ArrayList;

public class DashboardChartResponse {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data{
      public String proType, tCount, amount;
    }
}
