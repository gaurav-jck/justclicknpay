package com.justclick.clicknbook.Fragment.jctmoney.response;

public class BankResponse {
    private String BANK_NAME,MASTER_IFSC_CODE;

    public String getBANK_NAME() {
        return BANK_NAME;
    }

    public void setBANK_NAME(String BANK_NAME) {
        this.BANK_NAME = BANK_NAME;
    }

    public String getMASTER_IFSC_CODE() {
        return MASTER_IFSC_CODE;
    }

    public void setMASTER_IFSC_CODE(String MASTER_IFSC_CODE) {
        this.MASTER_IFSC_CODE = MASTER_IFSC_CODE;
    }
}
