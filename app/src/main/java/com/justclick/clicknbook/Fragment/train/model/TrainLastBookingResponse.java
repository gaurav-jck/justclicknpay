package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainLastBookingResponse implements Serializable{
    public String statusCode, statusMessage;
    public int count;
    public ArrayList<data> data;

    public class data implements Serializable {
        public String reservationid, source, destination, pnr, /*class,*/ reservationdate, status;
    }
}
