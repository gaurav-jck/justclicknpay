package com.justclick.clicknbook.fingoole.model;

import com.justclick.clicknbook.requestmodels.CommonRequestModel;

public class TxnListRequestModel extends CommonRequestModel {
    private String ReservationId, FromDate, ToDate, BatchNo,
            Numberofday,Custname,Mobile, Emailid, Startdate, Enddate,
            Conid, Usertype, Refagency;

    public String getReservationId() {
        return ReservationId;
    }

    public void setReservationId(String reservationId) {
        ReservationId = reservationId;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getNumberofday() {
        return Numberofday;
    }

    public void setNumberofday(String numberofday) {
        Numberofday = numberofday;
    }

    public String getCustname() {
        return Custname;
    }

    public void setCustname(String custname) {
        Custname = custname;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmailid() {
        return Emailid;
    }

    public void setEmailid(String emailid) {
        Emailid = emailid;
    }

    public String getStartdate() {
        return Startdate;
    }

    public void setStartdate(String startdate) {
        Startdate = startdate;
    }

    public String getEnddate() {
        return Enddate;
    }

    public void setEnddate(String enddate) {
        Enddate = enddate;
    }

    public String getConid() {
        return Conid;
    }

    public void setConid(String conid) {
        Conid = conid;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    public String getRefagency() {
        return Refagency;
    }

    public void setRefagency(String refagency) {
        Refagency = refagency;
    }
}
