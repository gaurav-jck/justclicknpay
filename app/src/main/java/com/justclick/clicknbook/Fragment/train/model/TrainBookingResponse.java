package com.justclick.clicknbook.Fragment.train.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainBookingResponse implements Serializable{
    public String statusCode, statusMessage;
    public ArrayList<railBookDetail> railBookDetail;

    public class railBookDetail implements Serializable {
        public String wsloginId, wsTxnId, wsReturnUrl, trainUrl, agent, userType, uid, duration;
    }
}
