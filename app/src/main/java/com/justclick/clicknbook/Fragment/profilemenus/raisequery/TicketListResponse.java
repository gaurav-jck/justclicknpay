package com.justclick.clicknbook.Fragment.profilemenus.raisequery;

import java.util.ArrayList;

public class TicketListResponse {
    public String Status, StatusCode;
    public ArrayList<Data> Data;

    public class Data{
        public int TicketID;
        public String ProductType, IssueType, Remarks, ComplaintNo, MobileNo, EmailID, ImageUpload,
        cDate, donecarduser, statusname;
    }
}
