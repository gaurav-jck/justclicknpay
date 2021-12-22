package com.justclick.clicknbook.fingoole.model;

import java.util.ArrayList;

public class TxnListResponseModel {
    private int StatusCode;
    private String StausMessage;
    private ArrayList<InsTransactions> InsTransactions;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getStausMessage() {
        return StausMessage;
    }

    public void setStausMessage(String stausMessage) {
        StausMessage = stausMessage;
    }

    public ArrayList<InsTransactions> getInsTransactionsList() {
        return InsTransactions;
    }

    public void setInsTransactionsList(ArrayList<InsTransactions> insTransactionsList) {
        this.InsTransactions = insTransactionsList;
    }

    public class InsTransactions{
        private String Reservationid, Name, Mobile, PolicyPlan,Startdate,Enddate,
                Coin, PgURL,InsType;
        private String Amount;

        public String getReservationid() {
            return Reservationid;
        }

        public void setReservationid(String reservationid) {
            Reservationid = reservationid;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String mobile) {
            Mobile = mobile;
        }

        public String getPolicyPlan() {
            return PolicyPlan;
        }

        public void setPolicyPlan(String policyPlan) {
            PolicyPlan = policyPlan;
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

        public String getCoin() {
            return Coin;
        }

        public void setCoin(String coin) {
            Coin = coin;
        }

        public String getPgURL() {
            return PgURL;
        }

        public void setPgURL(String pgURL) {
            PgURL = pgURL;
        }

        public String getInsType() {
            return InsType;
        }

        public void setInsType(String insType) {
            InsType = insType;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }
    }
}

/*{
    "StatusCode": 0,
    "StausMessage": "Success",
    "InsTransactions": [
        {
            "Reservationid": "IN30110U3UB0JC0A13387",
            "Name": "",
            "Mobile": "9013916224",
            "Amount": 20.00,
            "PolicyPlan": null,
            "Startdate": "15-04-2021 00:00:00",
            "Enddate": "15-04-2021 00:00:00",
            "Coin": "9100391606697119",
            "PgURL": "http://fingooleuat.southindia.cloudapp.azure.com:8080/PACOI20.aspx?coi=9100391606697119",
            "InsType": ""
        },
        {
            "Reservationid": "IN30110IXLZ0JC0A13387",
            "Name": "jitender",
            "Mobile": "9013916224",
            "Amount": 4.00,
            "PolicyPlan": null,
            "Startdate": "20-04-2021 00:00:00",
            "Enddate": "20-04-2021 00:00:00",
            "Coin": "9100391606698461",
            "PgURL": "http://fingooleuat.southindia.cloudapp.azure.com:8080/PACOI20.aspx?coi=9100391606698461",
            "InsType": ""
        },
        {
            "Reservationid": "IN30110P5VM0JC0A13387",
            "Name": "jitender",
            "Mobile": "9013916224",
            "Amount": 4.00,
            "PolicyPlan": null,
            "Startdate": "21-04-2021 00:00:00",
            "Enddate": "21-04-2021 00:00:00",
            "Coin": "",
            "PgURL": "",
            "InsType": "Silver Plan"
        },
        {
            "Reservationid": "IN30110NFBR0JC0A13387",
            "Name": "jitender",
            "Mobile": "9013916224",
            "Amount": 4.00,
            "PolicyPlan": null,
            "Startdate": "22-04-2021 00:00:00",
            "Enddate": "22-04-2021 00:00:00",
            "Coin": "9100391606698861",
            "PgURL": "http://fingooleuat.southindia.cloudapp.azure.com:8080/PACOI20.aspx?coi=9100391606698861",
            "InsType": "Silver Plan"
        }
       ]
     }*/