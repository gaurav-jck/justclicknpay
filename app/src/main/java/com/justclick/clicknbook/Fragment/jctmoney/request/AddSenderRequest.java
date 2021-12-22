package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class AddSenderRequest{
    private String Mode="WEB", MerchantId= ApiConstants.MerchantId;
    private String AgentCode, Mobile,Name, Dob, Gender, State,SessionKey,SessionRefId,
    RequestFor, Pin, Address;
//    verify sender
    private String Otp,OtpRefId,FundTransferId;

    public String getFundTransferId() {
        return FundTransferId;
    }

    public void setFundTransferId(String fundTransferId) {
        FundTransferId = fundTransferId;
    }

    public String getOtp() {
        return Otp;
    }

    public void setOtp(String otp) {
        Otp = otp;
    }

    public String getOtpRefId() {
        return OtpRefId;
    }

    public void setOtpRefId(String otpRefId) {
        OtpRefId = otpRefId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getSessionKey() {
        return SessionKey;
    }

    public void setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
    }

    public String getSessionRefId() {
        return SessionRefId;
    }

    public void setSessionRefId(String sessionRefId) {
        SessionRefId = sessionRefId;
    }

    public String getRequestFor() {
        return RequestFor;
    }

    public void setRequestFor(String requestFor) {
        RequestFor = requestFor;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }
}
