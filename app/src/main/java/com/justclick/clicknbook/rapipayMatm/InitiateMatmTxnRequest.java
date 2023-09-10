package com.justclick.clicknbook.rapipayMatm;

import com.justclick.clicknbook.ApiConstants;

public class InitiateMatmTxnRequest {
    private String AgentCode, Merchant= ApiConstants.MerchantId, TxnType, Mode="APP", Mobile;
    private float Amount;

    public String getAgentCode() {
        return AgentCode;
    }

    public void setAgentCode(String agentCode) {
        AgentCode = agentCode;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getTxnType() {
        return TxnType;
    }

    public void setTxnType(String txnType) {
        TxnType = txnType;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }
}
