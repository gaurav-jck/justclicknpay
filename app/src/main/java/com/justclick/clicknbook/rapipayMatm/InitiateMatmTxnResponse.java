package com.justclick.clicknbook.rapipayMatm;

public class InitiateMatmTxnResponse {
    private String statusCode;
    private String statusMessage;
    private String transactionId,jckTransactionId,mobile;

    public String getJckTransactionId() {
        return jckTransactionId;
    }

    public String getMobile() {
        return mobile;
    }

    public String getSmId() {
        return smId;
    }

    private String smId;

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
