package com.justclick.clicknbook.jctPayment.aepsinsta;

import com.justclick.clicknbook.jctPayment.newaeps.AepsRegistrationActivity;

import java.util.ArrayList;

public class AepsInstaResponse {
        public String statusCode,statusMessage;
        public ArrayList<cashWithdrawal> cashWithdrawal;
        public ArrayList<cashWithdrawal> balEnqDetails;
        public ArrayList<cashWithdrawal> aadaharPayDetail;
        public ArrayList<cashWithdrawal> miniStateMentDetail;
        public class cashWithdrawal{
            public String bankName, availableBalance, rrn, accountNumber,status,transactionId,
                    txnAmount,agentCode,timeStamp, jckTransactionId, apiTxnId,txnType;
        } //        {"statusCode":"01","statusMessage":"Issuer bank is inoperative","cashWithdrawal":[{"bankName":"Punjab National Bank","availableBalance":"","rrn":"112518295010","accountNumber":"XXXXXXXX2683","status":"Failed","transactionId":"MA05051WS4SJC0A13387"}]}

    public ArrayList<msDetails> msDetails;

    public class msDetails {
        public String date, txnType, amount, narration;
    }
    }

