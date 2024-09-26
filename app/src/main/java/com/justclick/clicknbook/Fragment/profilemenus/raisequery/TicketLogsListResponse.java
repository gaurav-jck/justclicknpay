package com.justclick.clicknbook.Fragment.profilemenus.raisequery;

import java.util.ArrayList;

public class TicketLogsListResponse {
    public String Status, StatusCode;
    public ArrayList<Data> Data;

    public class Data{
        public int LogsID;
        public String Remarks, cDate, CreatedBy, IPAddress;
    }
}
