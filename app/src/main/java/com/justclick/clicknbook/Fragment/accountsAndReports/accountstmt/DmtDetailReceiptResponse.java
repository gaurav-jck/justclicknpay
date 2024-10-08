package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

import java.io.Serializable;
import java.util.ArrayList;

public class DmtDetailReceiptResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data implements Serializable{
        public long sid;
        public String transactionId, agentCode, distCode, agencyName, merchant, createdDate,
                updatedDate, updatedBy, txnStatus, txnDescription, txnType, mode, accountNumber,
                bankName, ifsc, mobile, name, txnAmt, nAmt, gAmt, agentComm, aGst, aTds,
                distComm, companyComm, companyTds, custFee, merchantComm, merchantTds,
                merchantGst, merchantNAmt, merchantGAmt, merchantMarkUp, supplierCharge,
                gst, supTds, remitterMobile, apiTxnStatus, distName, refId, aMarkup,
                rrn, apiTransactionId, apiTxnStatusDesc, jckTransactionId, benificieryId,
                apiBal, sdComm, refundDate, refundBy, responseType, apiOrderId, bankPipe;
    }
}
