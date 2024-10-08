package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

import java.io.Serializable;
import java.util.ArrayList;

public class RechargeDetailReceiptResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data implements Serializable{
        public long sid;
        public String transactionId;
        public String agentCode;
        public String agencyName;
        public String merchantId;
        public String distCode;
        public String sdCode;
        public String mode;
        public String rechargeType;
        public String operator;
        public String rechargeNumber;
        public String amount;
        public String netAmt;
        public String agentComm;
        public String aTds;
        public String agst;
        public String serviceCharge;
        public String adminComm;
        public String jckServiceCharge;
        public String adminTds;
        public String sdComm;
        public String distComm;
        public String txnStatus;
        public String txnStatusDesc;
        public String createdDate;
        public String updatedDate;
        public String updatedBy;
        public String refundDate;
        public String apiSource;
        public String apiCode;
        public String apiMessage;
        public String apiRefNumber;
        public String category;
        public String billNumber;
        public String billdate;
        public String dueBillDate;
        public String cName;
        public String operatorid;
        public String jckGst;
        public String panMode;
        public String panCardType;
        public String panUserId;
        public String apiTransactionId;
        public String cardNumber;
        public String paymentMode;

    }
}
