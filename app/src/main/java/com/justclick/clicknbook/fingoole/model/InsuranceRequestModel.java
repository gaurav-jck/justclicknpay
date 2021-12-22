package com.justclick.clicknbook.fingoole.model;

import com.justclick.clicknbook.ApiConstants;

public class InsuranceRequestModel {
    private String InsureMobileNo,PolicyAmount,NoofDays,SchemeCode,UserName,policyPlane,BookingDate,PAStartDate,
            UserEmail,Modules="Train",TxnMedium="App",MerchantId= ApiConstants.MerchantId,Donecarduser,RefNo="",InsuranceType="T";

    public String getInsureMobileNo() {
        return InsureMobileNo;
    }

    public void setInsureMobileNo(String insureMobileNo) {
        InsureMobileNo = insureMobileNo;
    }

    public String getPolicyAmount() {
        return PolicyAmount;
    }

    public void setPolicyAmount(String policyAmount) {
        PolicyAmount = policyAmount;
    }

    public String getNoofDays() {
        return NoofDays;
    }

    public void setNoofDays(String noofDays) {
        NoofDays = noofDays;
    }

    public String getSchemeCode() {
        return SchemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        SchemeCode = schemeCode;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPolicyPlane() {
        return policyPlane;
    }

    public void setPolicyPlane(String policyPlane) {
        this.policyPlane = policyPlane;
    }

    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String bookingDate) {
        BookingDate = bookingDate;
    }

    public String getPAStartDate() {
        return PAStartDate;
    }

    public void setPAStartDate(String PAStartDate) {
        this.PAStartDate = PAStartDate;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getModules() {
        return Modules;
    }

    public void setModules(String modules) {
        Modules = modules;
    }

    public String getDonecarduser() {
        return Donecarduser;
    }

    public void setDonecarduser(String donecarduser) {
        Donecarduser = donecarduser;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public String getInsuranceType() {
        return InsuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        InsuranceType = insuranceType;
    }
/* {
        "": "9013916224",
            "": "7",
            "": "1",
            "": "ODPA10L",
            "": "Sovindra",
            "": "Gold Plan",
            "": "12/11/2020",
            "": "12/12/2020",
            "": "bhatiboy1986@gmail.com",
            "": "Train",
            "": "Web",
            "": "JUSTCLICKTRAVELS",
            "": "JC0A13387",
            "": "",
            "": "N"
    }*/
}
