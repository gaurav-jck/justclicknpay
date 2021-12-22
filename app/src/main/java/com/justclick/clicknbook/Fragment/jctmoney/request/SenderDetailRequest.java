package com.justclick.clicknbook.Fragment.jctmoney.request;

public class SenderDetailRequest extends CommonRapiRequest{
    private String Mobile,AgentCode,SessionKey,SessionRefId;

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
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
}
