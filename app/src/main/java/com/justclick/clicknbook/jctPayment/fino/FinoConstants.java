package com.justclick.clicknbook.jctPayment.fino;

public class FinoConstants {
    //    Fino constants
//    public static final String MERCHANT_ID = "9892393804";//(SpiceMoney QC)
//    public static final String MERCHANT_ID = "8468862808";//(SpiceMoney QC)
    public static final String MERCHANT_ID = "7428094454";//(SpiceMoney QC)
    public static final String CLIENTID = "97";//(Different)
//    public static final String AUTHKEY = "827348aa-52b6-45f9-9614-2af290ef2360";  // UAT
    public static final String AUTHKEY = "de529340-38e3-4313-8e06-492747364daf";   // Live
//    public static final String CLIENT_REQUEST_ENCRYPTION_KEY = "b65430a9-a10a-4ccc-9b98-af9f4a7cd445";  // UAT
    public static final String CLIENT_REQUEST_ENCRYPTION_KEY = "43c273e0-8f0f-4b97-b251-517a121b5c0e";  //Live

    /*QC FINO*/public static final String VERSION = "1000";// API VERSION(Common)
    /*QC FINO*/public static final String CLIENT_HEADER_ENCRYPTION_KEY = "982b0d01-b262-4ece-a2a2-45be82212ba1";//This key is use for HeaderData Encryption(Common)

    //    /*QC FINO*/ public static final String RETURN_URL = "https://www.justclicknpay.com";// This URL is provided by Client to send Response
//    /*QC FINO*/ public static final String RETURN_URL = "http://10.15.20.131:8023/PG/TestPage.aspx";// This URL is provided by Client to send Response
    /*QC FINO*/ public static final String RETURN_URL = "https://www.justclicknpay.com/Aeps/AepsResponse.aspx";// This URL is provided by Client to send Response


    //SERVICE_ID for AEPS Transaction
    public static final String SERVICE_AEPS_CW = "151";//AEPS_CashWithdrow
    public static final String SERVICE_AEPS_BE = "152";//AEPS_BalanceEnquiry
    public static final String SERVICE_AEPS_TS = "154";//AEPS_TransactionStatus

    //SERVICE_ID for MICRO ATM Transaction
    public static final String SERVICE_MICRO_CW = "156";//MicroATM_CashWithdraw
    public static final String SERVICE_MICRO_BE = "157";//MicroATM_BalanceEnquiry
    public static final String SERVICE_MICRO_TS = "158";//MicroATM_TransactionStatus
}
