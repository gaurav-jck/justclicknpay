package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class DeleteBeneRequest {
    private String Mode="WEB", MerchantId= ApiConstants.MerchantId;
    private String BeniId,AgentCode,SessionRefId,SessionKey;

    public String getBeniId() {
        return BeniId;
    }

    public void setBeniId(String beniId) {
        BeniId = beniId;
    }

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
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
}
