package com.justclick.clicknbook.Fragment.jctmoney.request;

public class TransactionRequest extends CommonRapiRequest{
    private String Name,BeniId,SessionRefId,SessionKey,TransferType,MobileNumber,
            AgentCode, IFSC, BankName,AccountNumber;
    private int Amount;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBeniId() {
        return BeniId;
    }

    public void setBeniId(String beniId) {
        BeniId = beniId;
    }

    public String getSessionRefId() {
        return SessionRefId;
    }

    public void setSessionRefId(String sessionRefId) {
        SessionRefId = sessionRefId;
    }

    public String getSessionKey() {
        return SessionKey;
    }

    public void setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
    }

    public String getTransferType() {
        return TransferType;
    }

    public void setTransferType(String transferType) {
        TransferType = transferType;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }
}
