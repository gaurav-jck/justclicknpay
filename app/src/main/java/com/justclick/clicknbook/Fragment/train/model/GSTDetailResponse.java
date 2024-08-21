package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GSTDetailResponse {
    public String statusCode, statusMessage;
    public ArrayList<gstDetailsList> gstDetailsList;

    public class gstDetailsList implements Serializable {
        public int rowNum, sid;
        public String gstin, flat, nameOnGST, pincode, state, city;
    }
}
