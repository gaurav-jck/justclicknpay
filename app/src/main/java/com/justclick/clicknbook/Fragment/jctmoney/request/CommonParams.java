package com.justclick.clicknbook.Fragment.jctmoney.request;

import java.io.Serializable;

public class CommonParams implements Serializable {
    private String sessionKey,sessionRefNo,token, userData;
    private String kycStatus;

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
