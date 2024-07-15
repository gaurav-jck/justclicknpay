package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerDetailResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<passengerList> passengerList;

    public class passengerList implements Serializable{
        public String sNo, name, age, sex, bookingStatus, currentStatus;
    }

}
