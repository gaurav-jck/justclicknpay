package com.justclick.clicknbook.Fragment.salesReport.salescredit;

import java.util.ArrayList;

public class SalesAgentListResponse {
    public String status, statusCode;
    public ArrayList<data> data;

    public class data{
        public String agencyName, agentCode;
    }
}
