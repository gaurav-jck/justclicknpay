package com.justclick.clicknbook.jctPayment.Models;

public class TransactionInfo {

    private String id;
    private String amount;
    private String productId;
    private String productName;
    private String productCode;
    private String transactionStatus;
    private String transactionMessage;

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    private String rbl_ref_id;
    private String rbl_request;

    public TransactionInfo(String id, String amount, String productId, String productName, String productCode, String transactionStatus,
                           String rbl_ref_id, String rbl_request, String transactionMessage) {
        this.id = id;
        this.amount = amount;
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.transactionStatus = transactionStatus;
        this.rbl_ref_id = rbl_ref_id;
        this.rbl_request = rbl_request;
        this.transactionMessage = transactionMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getRbl_ref_id() {
        return rbl_ref_id;
    }

    public void setRbl_ref_id(String rbl_ref_id) {
        this.rbl_ref_id = rbl_ref_id;
    }

    public String getRbl_request() {
        return rbl_request;
    }

    public void setRbl_request(String rbl_request) {
        this.rbl_request = rbl_request;
    }
}
