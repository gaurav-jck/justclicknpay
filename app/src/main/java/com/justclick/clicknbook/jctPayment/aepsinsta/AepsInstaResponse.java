package com.justclick.clicknbook.jctPayment.aepsinsta;

import java.util.ArrayList;

public class AepsInstaResponse {
        public String statusCode,statusMessage;
        public ArrayList<cashWithdrawal> cashWithdrawal;
        public ArrayList<cashWithdrawal> balEnqDetails;
        public class cashWithdrawal{
            public String bankName, availableBalance, rrn, accountNumber,status,transactionId,
                    txnAmount,agentCode,timeStamp, jckTransactionId, apiTxnId,txnType;
        } //        {"statusCode":"01","statusMessage":"Issuer bank is inoperative","cashWithdrawal":[{"bankName":"Punjab National Bank","availableBalance":"","rrn":"112518295010","accountNumber":"XXXXXXXX2683","status":"Failed","transactionId":"MA05051WS4SJC0A13387"}]}
    }

