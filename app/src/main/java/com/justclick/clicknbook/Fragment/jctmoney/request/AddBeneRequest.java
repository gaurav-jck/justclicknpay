package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class AddBeneRequest{
    private String Mode="WEB", MerchantId= ApiConstants.MerchantId;
    private String AccountHolderName,AccountNumber, ConfirmAccountNumber,BankName, BankId,
            IfscCode,Mobile,SessionRefId,SessionKey,AgentCode;

    public void setBankId(String bankId) {
        BankId = bankId;
    }

    public String getApiService() {
        return ApiService;
    }
    public void setApiService(String apiService) {
        ApiService = apiService;
    }
    private String ApiService;   // new change

    public void setMode(String mode){
        Mode=mode;
    }

    public String getAccountHolderName() {
        return AccountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        AccountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getConfirmAccountNumber() {
        return ConfirmAccountNumber;
    }

    public void setConfirmAccountNumber(String confirmAccountNumber) {
        ConfirmAccountNumber = confirmAccountNumber;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getIfscCode() {
        return IfscCode;
    }

    public void setIfscCode(String ifscCode) {
        IfscCode = ifscCode;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
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

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }
}
