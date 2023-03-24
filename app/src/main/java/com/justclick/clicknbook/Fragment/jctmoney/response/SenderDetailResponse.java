package com.justclick.clicknbook.Fragment.jctmoney.response;

import java.io.Serializable;
import java.util.ArrayList;

public class SenderDetailResponse implements Serializable {
    private String statusCode, statusMessage,sessionRefId,sessionKey,requestFor;
    private float remainingLimit;
    private ArrayList<senderDetailInfo> senderDetailInfo;
    private ArrayList<benificiaryDetailData> benificiaryDetailData;
    private String bank1, type1, bank2, type2, bank3, type3;

    public String getBank1() {
        return bank1;
    }

    public String getType1() {
        return type1;
    }

    public String getBank2() {
        return bank2;
    }

    public String getType2() {
        return type2;
    }

    public String getBank3() {
        return bank3;
    }

    public String getType3() {
        return type3;
    }

    public ArrayList<SenderDetailResponse.senderDetailInfo> getSenderDetailInfo() {
        return senderDetailInfo;
    }

    public void setSenderDetailInfo(ArrayList<SenderDetailResponse.senderDetailInfo> senderDetailInfo) {
        this.senderDetailInfo = senderDetailInfo;
    }

    public ArrayList<SenderDetailResponse.benificiaryDetailData> getBenificiaryDetailData() {
        return benificiaryDetailData;
    }

    public void setBenificiaryDetailData(ArrayList<SenderDetailResponse.benificiaryDetailData> benificiaryDetailData) {
        this.benificiaryDetailData = benificiaryDetailData;
    }

    public class senderDetailInfo implements Serializable{
        private String dob,gender,mobile,name,pin,state;

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public class benificiaryDetailData implements Serializable{
        private String isVerified,isNEFT,isIMPS,bankName,beneid,accountHolderName,
                accountNumber,ifsc, bankid;

        public String getBankid() {
            return bankid;
        }

        public boolean isVisible;

        public String getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(String isVerified) {
            this.isVerified = isVerified;
        }

        public String getIsNEFT() {
            return isNEFT;
        }

        public void setIsNEFT(String isNEFT) {
            this.isNEFT = isNEFT;
        }

        public String getIsIMPS() {
            return isIMPS;
        }

        public void setIsIMPS(String isIMPS) {
            this.isIMPS = isIMPS;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBeneid() {
            return beneid;
        }

        public void setBeneid(String beneid) {
            this.beneid = beneid;
        }

        public String getAccountHolderName() {
            return accountHolderName;
        }

        public void setAccountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfsc() {
            return ifsc;
        }

        public void setIfsc(String ifsc) {
            this.ifsc = ifsc;
        }
    }

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

    public String getSessionRefId() {
        return sessionRefId;
    }

    public void setSessionRefId(String sessionRefId) {
        this.sessionRefId = sessionRefId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getRequestFor() {
        return requestFor;
    }

    public void setRequestFor(String requestFor) {
        this.requestFor = requestFor;
    }

    public float getRemainingLimit() {
        return remainingLimit;
    }

    public void setRemainingLimit(float remainingLimit) {
        this.remainingLimit = remainingLimit;
    }
}
