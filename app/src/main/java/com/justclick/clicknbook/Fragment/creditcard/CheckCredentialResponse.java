package com.justclick.clicknbook.Fragment.creditcard;

import java.util.List;

public class CheckCredentialResponse {
    private String statusCode, statusMessage;
    private List<credentialData> credentialData;

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public List<credentialData> getCredentialData() {
        return credentialData;
    }

    public class credentialData{
        private String token,userData;

        public String getToken() {
            return token;
        }

        public String getUserData() {
            return userData;
        }
    }
}
