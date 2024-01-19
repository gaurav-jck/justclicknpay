package com.justclick.clicknbook.jctPayment.Utilities;


import com.justclick.clicknbook.ApiConstants;


public class URLs {
    public static final String BASE_URL_NEW = ApiConstants.BASE_URL_AEPS;
    public static final String BASE_URL = ApiConstants.BASE_URL_AEPS_OLD;
//    public static final String BASE_URL_N = ApiConstants.BASE_URL_AEPS_N;
    public static final String BASE_URL_N = ApiConstants.BASE_URL_AEPS_N;
//    public static final String BASE_URL_N_Test = ApiConstants.BASE_URL_AEPS_N_test;
//    public static final String BASE_URL = "http://uataeps-api-yes.justclicknpay.com/";
//        public static final String BASE_URL = "https://aeps.justclicknpay.com/";
        public static final String AEPS_EXCEL_DOWNLOAD_URL = ApiConstants.AEPS_EXCEL_DOWNLOAD_URL;
    public static final String LIVE_BASE_URL = BASE_URL+"api/";
    public static final String BANK_LIST = LIVE_BASE_URL + "aeps/bankList";
//    public static final String BANK_LIST_NEW = "https://aeps.rapipay.com/iinList";
    public static final String BANK_LIST_NEW = "https://jckaeps.justclicknpay.com/B2B/AdharPayment/GetBankList";
    public static final String AepsInitTransaction = "AepsInitTransaction";
    public static final String UpdateTransaction =  "UpdateTransaction";
    public static final String TransactionList =  "TransactionList";
    public static final String REFRESHNES_FACTOR = LIVE_BASE_URL + "aeps/mobileLogin";
    public static final String MOBILE_TRANSACTION = LIVE_BASE_URL + "aeps/mobileTransaction";
    public static final String RECIEPT_DATA = LIVE_BASE_URL + "aeps/receipt/";
    public static final String TRANSACTION_DETAIL = LIVE_BASE_URL + "agent/mobileStats";
    public static final String AGENT_LOGIN = LIVE_BASE_URL + "login";
//    public static final String AGENT_LOGIN = LIVE_BASE_URL + "locallogin";
    public static final String FORGET_PASSWORD = LIVE_BASE_URL + "forgetPassword";
    public static final String CHANGE_PASSWORD = LIVE_BASE_URL + "changePassword";
    public static final String CHECK_SESSION = LIVE_BASE_URL + "aeps/jctSession";
    public static final String AEPSSessionclose = "AEPSSessionclose";
    public static final String checktxncompleted = "checktxncompleted";
    public static final String hasbankdetail =LIVE_BASE_URL +"hasbankdetail";
    public static final String AepsBalance =LIVE_BASE_URL +"AepsBalance";
    public static final String createcashpayout =LIVE_BASE_URL +"createcashpayout";
    public static final String cashpayout =LIVE_BASE_URL +"cashpayoutrequest/cashpayout?page=";
    public static final String addBankDetails =LIVE_BASE_URL +"addbankdetail";
    public static final String updatebankdetail =LIVE_BASE_URL +"updatebankdetail";
    public static final String getCancelCheck =LIVE_BASE_URL +"getCancelCheck?token=";

    public static final String GenerateToken ="GenerateToken";
    public static final String BalanceEnquiry ="BalanceEnquiry";
    public static final String CashWithdrawal ="CashWithdrawal";
    public static final String MiniStatment ="MiniStatment";
    public static final String TransactionListAeps ="TransactionList";
    public static final String AepsRegistration ="AepsRegistration";


    public static final String CheckKyc ="CheckKyc";
    public static final String InitiateOnBoard ="InitiateOnBoard";
    public static final String WithdrawCash =BASE_URL_N+"B2B/AdharPayment/"+"WithdrawCashAuthentication";
    public static final String WithdrawAuth =BASE_URL_N+"B2B/AdharPayment/"+"Authenticity";
    public static final String BalanceCheck =BASE_URL_N+"B2B/AdharPayment/"+"BalanceCheck";
    public static final String BankStatment =BASE_URL_N+"B2B/AdharPayment/"+"BankStatment";
    public static final String AadharPay =   BASE_URL_N+"B2B/AdharPayment/"+"AadharPay";
    public static final String AepsRegister =   BASE_URL_N+"B2B/AdharPayment/"+"Registration";
    public static final String AepsAuthenticate =   BASE_URL_N+"B2B/AdharPayment/"+"Authentication";
    public static final String StatusEnquiry ="StatusEnquiry";
}
