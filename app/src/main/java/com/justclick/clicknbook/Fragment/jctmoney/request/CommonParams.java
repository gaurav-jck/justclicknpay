package com.justclick.clicknbook.Fragment.jctmoney.request;

import java.io.Serializable;

public class CommonParams implements Serializable {
    private String sessionKey,sessionRefNo,token, userData;
    private String kycStatus, address, pinCode, state, city, statecode;
    public int isBank2, isBank3;
    public String agentCode, mobile;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getApiService() {
        return ApiService;
    }

    public void setApiService(String apiService) {
        ApiService = apiService;
    }

    private String ApiService;   // new change

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionRefNo() {
        return sessionRefNo;
    }

    public void setSessionRefNo(String sessionRefNo) {
        this.sessionRefNo = sessionRefNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }
}
