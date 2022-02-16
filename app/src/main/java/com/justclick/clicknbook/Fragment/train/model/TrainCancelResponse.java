package com.justclick.clicknbook.Fragment.train.model;

import java.util.ArrayList;

public class TrainCancelResponse {
    public String statusCode, statusMessage;
    public ArrayList<paxCancel> paxCancel;
    public ArrayList<cancelIdDetail> cancelIdDetail;

    public class paxCancel{
        public String cancellationId, transactionId;
    }

    public class cancelIdDetail{
        public String cancellationId;
    }
}
