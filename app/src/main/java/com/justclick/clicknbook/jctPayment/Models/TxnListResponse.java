package com.justclick.clicknbook.jctPayment.Models;

import java.util.ArrayList;

public class TxnListResponse {
    public int StatusCode;
    public String StausMessage;
    public ArrayList<AepsTransactions> AepsTransactions;

    public class AepsTransactions{
        public String TransactionId,TransactionDate, DoneCardUser, TxnAmount,
        CashOutAmount,  NetAmount, Status, PaymentType, ApiReferenceId, BankName, RRN,
        CustomerMobile;
    }
}
