package com.justclick.clicknbook.Fragment.jctmoney.response;


public class TransactionOtpResponse {
    private String statusCode,statusMessage;
    public data data;

    public class data{
        public String transactionId, txnMessage, stateresp, jckRefId;
        public int amount;
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


    /*
    * {
    "statusCode": "00",
    "statusMessage": "Transaction OTP Sent",
    "data": {
        "transactionId": "241114213532360",
        "txnMessage": "OTP has been send to the remitter mobile number.",
        "stateresp": "468894152",
        "jckRefId": "D14114UGQL0jc0a36575",
        "amount": 100
    },
    "txndetails": {
        "name": "JUST CLICK KARO SERVICES PRIVATE LIMITED",
        "beniId": "61738404",
        "sessionRefId": "PaySprint",
        "sessionKey": "PaySprint",
        "transferType": "IMPS",
        "amount": 100,
        "mobileNumber": "9012836576",
        "mode": "App",
        "agentCode": "jc0a36575",
        "merchantId": "JUSTCLICKTRAVELS",
        "userData": "WeJcQmqO+vGOadT6/VDzSljFL0OjM31OKvp4ZDw8/K//Ag+Ts69qvOai9SCMtw+Q3jZrlgyILNMJq195qLg/PEzr8HIAeykR7phAbAzg3Q1KHs1mg0WidwMGkjm1UZoRU9RH65sBMOUP6XYo+Nt2Anr52SE8+tTQ1P7/qj7gxcZULZusBBhI3B/ezvs+bX8+2RmmQKQEhV3djwDsvV+Ra4u9KPjNMX/PEb4auKBhkbIfW3Fx6tu39RvtfVaAjyrM/hOtG9W8oxAb5nIloQ/s7t96W7VhkbnMqyuK64VVno0r+bYrgYDDjNYlmGIT2LzigOYoPm990iJbws37LhccrRcphovjIK+Xa17xDS4WqzH2lZR8UeThe/aSD0sxRT2l/mUh8ArywZE5Co772lSKihvzYqvsLAQ5qYTgz8KASsuEggCw5ehPMI6QjvsiRczah2Q485sgjV7Z5AxZxQwL0uPavtVO8gNM2Vw/J6cxeSXTg060D+NI/D5od+LFyr5VSh7f4fTJRDIQcsIiC0W2wvDGgD7IAMDd9GRwv1EPJkJpUNg7E9VvUu+c2r5f9bUYmfAf/5aP3jR3Bv0QwzDwJSgqAYtpaciKifPkKOiKutMeApN3vzEFD2VopQ6tTMsdH/mX2f5ADwh3LuuTcab4dVzjDJqi2LEG/yZ40adgGGZFtlocq2hSdF9OBPbZDO6uzgM5IEyNjmYgvMSEsYZ5TAEVWpNfRkRUMSZPoF+kbE/mMRzUeBgYRHrus4ohT56qGYF3JFdwGO2GRWduqn+xd7Fwmun63JXvPtVXGIRYATMRfFtjcmzAjfNe6tnR7IdvekHV3wOMGw/INyEQuWdt+g==",
        "transactionId": "241114213532360",
        "accountNumber": "38647754554",
        "ifsc": "SBIN0001708",
        "bankName": "STATE BANK OF INDIA",
        "fundTransferId": null,
        "apiService": "1",
        "bankId": "1177",
        "beneName": null,
        "pipe": null,
        "isBank2": 1,
        "isBank3": 0,
        "pincode": "110027",
        "address": "New Delhi",
        "dob": "2004-11-14",
        "gst_state": "07",
        "bank1Value": "0",
        "bank1Type": null,
        "bank2Value": "0",
        "bank2Type": null,
        "bank3Value": "0",
        "bank3Type": null,
        "lat": "12.11111",
        "long": "74.12222",
        "longitude": "74.12222"
    }
}*/
}
