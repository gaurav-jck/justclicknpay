package com.justclick.clicknbook.Fragment.jctmoney.response;

public class AddSenderResponse extends CommonRapiResponse{
    private String otpRefId, fundTransferId;

    public String getOtpRefId() {
        return otpRefId;
    }

    public void setOtpRefId(String otpRefId) {
        this.otpRefId = otpRefId;
    }

    public String getFundTransferId() {
        return fundTransferId;
    }

    public void setFundTransferId(String fundTransferId) {
        this.fundTransferId = fundTransferId;
    }
}
