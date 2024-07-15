package com.justclick.clicknbook.rapipayMatm;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class TxnListResponseModel implements Serializable {
    private String statusCode, statusMessage;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    @Nullable
    private ArrayList<transactionListDetail> transactionListDetail;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ArrayList<TxnListResponseModel.transactionListDetail> getTransactionListDetail() {
        return transactionListDetail;
    }

    public void setTransactionListDetail(ArrayList<TxnListResponseModel.transactionListDetail> transactionListDetail) {
        this.transactionListDetail = transactionListDetail;
    }

    public class transactionListDetail{
        private String jckTransactionId;
        private String rowNum;
        private String transactionId;
        private String agentCode;
        private String createdDate;
        private String txnStatusDesc;
        private String txnStatus;
        private String distCode;
        private String distName;
        private String txnType;
        private String rechargeType;

        public String getRechargeType() {
            return rechargeType;
        }

        private String bankName;
        private String mobile;
        private String apiStatusMessage;
        private String apiStatusCode;
        private String accountNo;
        private String rrn;
        private String cardHolderName;

//        utility
        private String rechargeNumber;

        public String getRechargeNumber() {
            return rechargeNumber;
        }

        public String getServiceType() {
            return serviceType;
        }

        private String serviceType, balanceAmount;
        private float txnAmount, netAmount, agentComm;
        private int totalCount;

        public String getBalanceAmount() {
            return balanceAmount;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public String getJckTransactionId() {
            return jckTransactionId;
        }

        public void setJckTransactionId(String jckTransactionId) {
            this.jckTransactionId = jckTransactionId;
        }

        public String getRowNum() {
            return rowNum;
        }

        public void setRowNum(String rowNum) {
            this.rowNum = rowNum;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getAgentCode() {
            return agentCode;
        }

        public void setAgentCode(String agentCode) {
            this.agentCode = agentCode;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getTxnStatusDesc() {
            return txnStatusDesc;
        }

        public void setTxnStatusDesc(String txnStatusDesc) {
            this.txnStatusDesc = txnStatusDesc;
        }

        public String getTxnStatus() {
            return txnStatus;
        }

        public void setTxnStatus(String txnStatus) {
            this.txnStatus = txnStatus;
        }

        public String getDistCode() {
            return distCode;
        }

        public void setDistCode(String distCode) {
            this.distCode = distCode;
        }

        public String getDistName() {
            return distName;
        }

        public void setDistName(String distName) {
            this.distName = distName;
        }

        public String getTxnType() {
            return txnType;
        }

        public void setTxnType(String txnType) {
            this.txnType = txnType;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getApiStatusMessage() {
            return apiStatusMessage;
        }

        public void setApiStatusMessage(String apiStatusMessage) {
            this.apiStatusMessage = apiStatusMessage;
        }

        public String getApiStatusCode() {
            return apiStatusCode;
        }

        public void setApiStatusCode(String apiStatusCode) {
            this.apiStatusCode = apiStatusCode;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getRrn() {
            return rrn;
        }

        public void setRrn(String rrn) {
            this.rrn = rrn;
        }

        public float getTxnAmount() {
            return txnAmount;
        }

        public void setTxnAmount(float txnAmount) {
            this.txnAmount = txnAmount;
        }

        public float getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(float netAmount) {
            this.netAmount = netAmount;
        }

        public float getAgentComm() {
            return agentComm;
        }

        public void setAgentComm(float agentComm) {
            this.agentComm = agentComm;
        }
    }
}
