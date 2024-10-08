package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt;

import java.io.Serializable;
import java.util.ArrayList;

public class MatmDetailReceiptResponse implements Serializable {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data implements Serializable{
        public long sid;
        public String transactionId,jckTransactionId, agentCode, txnAmount, agentComm, agentTds,
                agentGst, distComm, superDistComm, jckComm, netAmount, bankCharge, createdDate;

        public String txnStatus,txnStatusDesc,distCode,distname,agencyName,txnType,mode,apiRefrenceId,
                cashOutAmt,merchant,bankName,cardNumbr,mobile,transactionType,systemsTraceAuditNumber,
                apiStatusCode,apiMessage,balanceAmount,accountNo,cardBrandName,referenceNumber,
                apiAmount,authcode,cardHolderName,terminalID,arpc,updatedDate,cardType,rrn,
                txnDescription,ifsc, name,supplierCharge,supplierGST,gst,utr,purpose;
        public String fundAccountid;
        public String benificieryId;
        public String apiTxnStatus;
        public String created_at;
        public String apiTxnStatusDesc;
        public String refId;
        public String paymentType;
        public String txnAmt;
        public String updatedBy;
        public String serviceType;
        public String serviceCharge;
        public String recoFlag;
        public String refundDate;
        public String isThreeway;
        public String apiSource;
        public String adharNo;

    }
}
