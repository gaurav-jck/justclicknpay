package com.justclick.clicknbook.Fragment.cashoutnew;

import java.util.List;

public class CheckCredentialResponse {
    private String statusCode, statusMessage;
    private List<credentialData> authvalidation;

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
        return authvalidation;
    }

    public void setCredentialData(List<CheckCredentialResponse.credentialData> credentialData) {
        this.authvalidation = credentialData;
    }

    public class credentialData{
        private String token,userData;
//        public String address, pinCode, state, city, statecode;
//        public int isBank2, isBank3;

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


}
