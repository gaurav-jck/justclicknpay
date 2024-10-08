package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

import java.io.Serializable;
import java.util.ArrayList;

public class AepsDetailReceiptResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data implements Serializable{
        public long sid;
        public String reservationId;
        public String doneCardUser;
        public String txnAmount;
        public String agentComm;
        public String agentTds;
        public String agentGst;
        public String distComm;
        public String superDistComm;
        public String jckComm;
        public String netAmount;
        public String bankCharge;
        public String createdDate;
        public String status;
        public String refAgency;
        public String agencyName;
        public String paymentType;
        public String apiRefrenceId;
        public String mode;
        public String cashOutAmt;
        public String merchantId;
        public String bankName;
        public String rrn;
        public String customerMobile;
        public String avlBal;
        public String ledgerBal;
        public String apiStatusCode;
        public String apiMessage;
    }
}
