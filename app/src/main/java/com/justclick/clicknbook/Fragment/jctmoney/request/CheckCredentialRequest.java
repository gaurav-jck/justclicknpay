package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class CheckCredentialRequest extends CommonRapiRequest{
    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }

    private String AgentCode;
}
