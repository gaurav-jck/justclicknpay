package com.justclick.clicknbook.jctPayment.aepsinsta;

public class TxnOtpResponse {
    public String statusCode, statusMessage;
    public boolean status;
    public data data;

    public class data{
        public String validity, referenceKey;
    }
}
