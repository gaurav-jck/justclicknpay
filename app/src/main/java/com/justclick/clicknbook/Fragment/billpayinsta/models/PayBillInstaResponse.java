package com.justclick.clicknbook.Fragment.billpayinsta.models;

import java.util.ArrayList;

public class PayBillInstaResponse {
        public String StatusCode,StatusMessage;
        public ArrayList<billDetails> billDetails;
        public class billDetails{
            public float amount;
            public String transactionId,acknowledgementNo,operatotrId,status;
        }
    }