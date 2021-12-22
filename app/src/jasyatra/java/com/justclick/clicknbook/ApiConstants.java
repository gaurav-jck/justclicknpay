package com.justclick.clicknbook;

/**
 * Created by Lenovo on 03/29/2017.
 */

public class ApiConstants
{
    public static final String GOOGLE_PLAY_STORE_URL ="https://play.google.com/store/apps/details?id=com.justclick.clicknbook";
    //    public static final String BASE_URL_LIVE_OLD ="https://ms.justclickkaro.com/";
    public static final String BASE_URL_TEST="http://uatms.justclickkaro.com/";
    public static final String BASE_URL_TEST2="http://uatms.justclicknpay.com:116/";
    public static final String BASE_URL_LIVE="https://ms.jasyatra.com/";
    public static final String BASE_URL=BASE_URL_LIVE;
    public static final String BASE_URL_AEPS="https://aeps-api-yes.justclicknpay.com/";   // AEPS URL
    public static final String AEPS_EXCEL_DOWNLOAD_URL="https://tinyurl.com/t4qe23w";   // AEPS Excel downloadURL
    public static final String BASE_URL_TEMP_AIR="http://www.myhotelbox.com/mobile/";
    public static final String BASE_URL_BUS_TEST="http://uatms.justclicknpay.com:116/";
    //        public static final String BASE_URL_JCT_MONEY="http://uatmb.justclicktravels.com/";  //test jct money url
//    public static final String BASE_URL_JCT_MONEY="http://uatmb.justclickkaro.com/";
    public static final String BASE_URL_JCT_MONEY="https://mb.jasyatra.com/";  //new live jct url (please uncomment this for live)
    public static final String BASE_URL_RBL_TEST="http://uatms.justclickkaro.com/";
    public static final String BASE_URL_RBL_LIVE="http://rbldmt.justclickkaro.com/";   //API for app session
    public static final String BASE_URL_RBL = BASE_URL_RBL_TEST;
    public static final String TermsAndConditionUrl="https://www.justclickkaro.com/Registerterms.htm";
    public static final String FlightImageBaseUrlOld="https://client.justclicktravels.in/jct/flightimage/";
    public static final String FlightImageBaseUrl="https://client.justclickkaro.com/jct/flightimage/";
    public static final String RblPage="RBLService.svc/";
    public static final String MobilePage="MobileServices.svc/";
    public static final String MerchantId="JASYATRA";
    public static final String AGENTLOGIN="AGENTLOGIN";
    public static final String LOGIN="LOGIN";
    public static final String REGISTRATION="REGISTRATION";
    public static final String UploadFile="UploadFile";
    public static final String FORGETPASSWORD="FORGETPASS";
    public static final String STATELIST="StateNameList";
    public static final String CITYLIST="CityNameList";
    public static final String GetAgentName ="GetAgentName";
    public static final String GetOptName ="GetOptName";
    public static final String RECHARGE ="Recharge";
    public static final String RechargeDetails  ="RechargeDetails ";
    public static final String GETJIOAMOUNT="GETJIOAMOUNT";
    public static final String CreditDetails ="CreditDetails";
    public static final String AdminCreditReport ="AdminCreditReport";
    public static final String UpdateCredit ="UpdateCredit";
    public static final String DirectCredit="DirectCredit";
    public static final String DeleteDeposit="DeleteDeposit";
    public static final String DeleteCredit="DeleteCredit";
    public static final String UpdateDeposit="UpdateDeposit";
    public static final String DirectDeposit="DirectDeposit";
    public static final String AgentCreditRequest ="AgentCreditRequest";
    public static final String AgentDepositRequest ="AgentDepositRequest";
    public static final String AgentCreditList ="AgentCreditList";
    public static final String MenuItems ="MenuItems";
    public static final String TrainRouteMap ="TrainRouteMap";
    public static final String AgentDepositList ="AgentDepositList";
    public static final String AdminDepositReport ="AdminDepositReport";
    public static final String ManageAccounts ="ManageAccounts";
    public static final String CheckPnr ="CheckPnr";
    public static final String MobileRechargeList ="MobileRechargeList";
    public static final String AirCancellationReport ="AirCancellationReport";
    public static final String AirRefundReport ="AirRefundReport";
    public static final String AirSalesReport ="AirSalesReport";

    //Sales Report
    public static final String SalesAgentDetail ="SalesAgentDetails";
    public static final String AgentDetail ="AgentDetails";
    public static final String SalesVerification ="SalesVerification";
    public static final String SalesApproves ="SalesApprove";
    public static final String SalesDispproves ="SalesDispprove";
    public static final String NetSalesReport ="NetSalesReport";

    //    money transfer services
    public static final String AppSession ="AppSession";
    public static final String GetSenderDetails ="GetSenderDetails";
    public static final String GetAddressIFSCData ="GetAddressIFSCData";
    public static final String GetBankData ="GetBankData";
    public static final String GetBankDetails ="GetBankDetails";
    public static final String GetBranchHData ="GetBranchHData";
    public static final String GetCityData ="GetCityData";
    public static final String GetStateData ="GetStateData";
    public static final String SenderRegistration ="SenderRegistration";
    public static final String RelationID ="RelationID";
    public static final String IFSCByCode ="IFSCByCode";
    public static final String BenReg ="BenReg";
    public static final String BenRegOTP ="BenRegOTP";
    public static final String BenResendOTP ="BenResendOTP";
    public static final String BenDeleteReq ="BenDeleteReq";
    public static final String BenDeleteOTP ="BenDeleteOTP";
    public static final String BenVaild ="BenVaild";
    public static final String BenT ="BenT";
    public static final String Refund ="Refund";
    public static final String RefundOTP ="RefundOTP";
    public static final String TransactionList ="TransactionList";
    public static final String PrintTicket ="PrintTicket";
    public static final String Mappping ="Mappping";
    public static final String PinDetail ="PinDetail";

    //hotel service
    public static final String HotelAvail ="HotelAvail";
    public static final String CityCountry ="CityCountry";
    public static final String MoreInfo ="MoreInfo";
    public static final String RoomData ="RoomData";

    //Bus services
    public static final String Source ="SourceAutoComplete";
    public static final String getAvailableTrips ="getAvailableTrips";
    public static final String TripDetails ="TripDetails";
    public static final String ConfirmBooking ="ConfirmBooking";
    public static final String TxnList ="TxnList";
    public static final String CancelBooking ="CancelBooking";
    public static final String TicketInfo ="TicketInfo";
    public static final String Destination ="Destination";

    //Air services
    public static final String FLIGHT_ICON_URL = "https://s3.ap-south-1.amazonaws.com/airline-images-jck/";
    public static final String BASE_URL_B2C ="http://13.232.248.121:8181/";
    public static final String FLIGHT_BASE_URL = BASE_URL_B2C+"api/v1.0/flight/";
    public static final String AIRPORT_SEARCH = "airport/search/";
    public static final String FLIGHT_SEARCH = "search";
    public static final String BASE_URL_LOGIN=BASE_URL_B2C+"api/v1.0/";
    public static final String ApplyPromoCode =BASE_URL_LOGIN+"promocode/redeem?promocode=";
    public static final String RemovePromoCode =BASE_URL+"api/v1.0/promocode/remove/";
    public static final String GetPromoList =BASE_URL_LOGIN+"promo/list?";
    public static final String BASE_URL_HOTELS_BOOKING=BASE_URL+"api/v1.0/hotel/";
    public static final String FLIGHT_MINI_FARE_RULE =FLIGHT_BASE_URL+"miniFareRules/search?";//=8552e136-37c&flightId=OB1
    public static final String FLIGHT_FARE_RULE =FLIGHT_BASE_URL+"fareRule?";//=8552e136-37c&flightId=OB1
    public static final String SEARCHFLIGHTJSON ="SEARCHFLIGHTJSON";
    public static final String AirAvailability ="AirAvailability";
    public static final String GetSectors ="GetSectors";
    public static final String AirFareValidate ="FareValidate";
    public static final String FARE_QUOTE = "fareQuote";
    public static final String ADD_CART = "cart";
    public static final String FLIGHT_HOLD = "book";
    public static final String CoPassengers =ApiConstants.BASE_URL_LOGIN+"user/CoPassengers";

    //JctMoney services
    public static final String CreateSender ="CreateSender";
    public static final String ValidateSOTP ="ValidateSOTP";
    public static final String ResendSOTP ="ResendSOTP";
    public static final String AddRecipient ="AddRecipient";
    public static final String DeleteRecipient ="DeleteRecipient";
    public static final String Transaction ="Transaction";
    public static final String VerifyAccount ="VerifyAccount";
    public static final String EkoBookingList ="EkoBookingList";
    public static final String JctMoneyTxnList ="TxnList";
    public static final String RRefundOTP ="RRefundOTP";
    public static final String GetRefund ="GetRefund";
    public static final String TransactionStatus ="TransactionStatus";
}
