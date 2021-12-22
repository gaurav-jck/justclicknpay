package com.justclick.clicknbook.Fragment.jctmoney.response;

import java.util.ArrayList;

public class TransactionResponse {
    private String statusCode,statusMessage;
    private ArrayList<bankDetails> bankDetails;
    private ArrayList<transactionDetails> transactionDetails;

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

    public ArrayList<TransactionResponse.bankDetails> getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(ArrayList<TransactionResponse.bankDetails> bankDetails) {
        this.bankDetails = bankDetails;
    }

    public ArrayList<TransactionResponse.transactionDetails> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(ArrayList<TransactionResponse.transactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public class bankDetails{
        private String beniName, bank, accountNumber,ifscCode,txnType;
        private float amount;

        public String getBeniName() {
            return beniName;
        }

        public void setBeniName(String beniName) {
            this.beniName = beniName;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(String ifscCode) {
            this.ifscCode = ifscCode;
        }

        public String getTxnType() {
            return txnType;
        }

        public void setTxnType(String txnType) {
            this.txnType = txnType;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }

    public class transactionDetails{
        private String transactionId, txnMessage,bankRRN,jckRefId;
        private float amount;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getTxnMessage() {
            return txnMessage;
        }

        public void setTxnMessage(String txnMessage) {
            this.txnMessage = txnMessage;
        }

        public String getBankRRN() {
            return bankRRN;
        }

        public void setBankRRN(String bankRRN) {
            this.bankRRN = bankRRN;
        }

        public String getJckRefId() {
            return jckRefId;
        }

        public void setJckRefId(String jckRefId) {
            this.jckRefId = jckRefId;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }

//    {"statusCode":"00","statusMessage":"Trasaction Successful.",
//    "transactionDetails":[{"transactionId":"210105161709520","txnMessage":"Transfer Successful",
//    "bankRRN":"100516028491","jckRefId":"D05011FZCE0JC0A13387","amount":100}],
//    "bankDetails":[{"beniName":"JUST CLICK KARO SER","bank":"STATE BANK OF INDIA",
//    "accountNumber":"38647754554","ifscCode":"SBIN0001708","txnType":"IMPS","amount":100}]}
}
