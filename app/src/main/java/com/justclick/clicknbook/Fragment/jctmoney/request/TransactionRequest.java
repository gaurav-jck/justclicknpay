package com.justclick.clicknbook.Fragment.jctmoney.request;

public class TransactionRequest extends CommonRapiRequest{
    private String Name,BeniId,SessionRefId,SessionKey,TransferType,MobileNumber,
            AgentCode, IFSC, BankName,AccountNumber, BankId,
            Bank1Value, Bank1Type, Bank2Value, Bank2Type, Bank3Value, Bank3Type;
    private String address, pinCode, state, city, statecode, gst_state;
    private int isBank2, isBank3;
    public String otp, stateresp, Transactionid, JCKTransactionid, lat, longitude;

    public void setBank1Value(String bank1Value) {
        Bank1Value = bank1Value;
    }

    public void setBank1Type(String bank1Type) {
        Bank1Type = bank1Type;
    }

    public void setBank2Value(String bank2Value) {
        Bank2Value = bank2Value;
    }

    public void setBank2Type(String bank2Type) {
        Bank2Type = bank2Type;
    }

    public void setBank3Value(String bank3Value) {
        Bank3Value = bank3Value;
    }

    public void setBank3Type(String bank3Type) {
        Bank3Type = bank3Type;
    }

    public void setIsBank2(int isBank2) {
        this.isBank2 = isBank2;
    }

    public void setIsBank3(int isBank3) {
        this.isBank3 = isBank3;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public void setGst_state(String gst_state) {
        this.gst_state = gst_state;
    }

    public void setBankId(String bankId) {
        BankId = bankId;
    }

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
