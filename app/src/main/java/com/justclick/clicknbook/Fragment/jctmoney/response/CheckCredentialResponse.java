package com.justclick.clicknbook.Fragment.jctmoney.response;

import java.io.Serializable;
import java.util.List;

public class CheckCredentialResponse implements Serializable {
    private String statusCode, statusMessage;
    public List<credentialData> credentialData;
    public String apiServices;

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

    public List<CheckCredentialResponse.credentialData> getCredentialData() {
        return credentialData;
    }

    public void setCredentialData(List<CheckCredentialResponse.credentialData> credentialData) {
        this.credentialData = credentialData;
    }

    public class credentialData implements Serializable{
        private String kycStatus,sessionKey,sessionRefNo,token,userData,txnRefId;
        private float payoutlimit;
        public String address, pinCode, state, city, statecode;
        public int isBank2, isBank3;

        public float getPayoutlimit() {
            return payoutlimit;
        }

        public String getKycStatus() {
            return kycStatus;
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

        public String getTxnRefId() {
            return txnRefId;
        }
    }


}
