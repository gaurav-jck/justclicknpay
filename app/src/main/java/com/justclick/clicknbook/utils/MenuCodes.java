package com.justclick.clicknbook.utils;

/**
 * Created by Lenovo on 06/13/2017.
 */

public class MenuCodes {
    public static final String  ProductMainTitle="Products";
    public static final String  ProductMainCode="Products";

    //Admin
    public static final String  PendingBookingList="PendingBookingList";
    public static final String  CancellationRequestList="CancellationRequestList";
    public static final String  StaffList="StaffList";
    public static final String  SalesMapping="SalesMapping";
    public static final String  ApproveAgent="ApproveAgent";
    //PendingBookingList,CancellationRequestList,StaffList,SalesMapping

    //Agents
    public static final String  AddAgency="AddAgency";
    public static final String  AgencyList="AgencyList";
    public static final String  UpdateCredit="UpdateCredit";
    //AddAgency,AgencyList,UpdateCredit

    //BookingList
    public static final String  AirBookingList="AirBookingList";
    public static final String  TopUpDetails="Top-UpDetails";
    public static final String  TrainBookingList="TrainBookingList";
    public static final String  TrainFailedList="TrainFailedList";
    public static final String  TrainBookingcheck="TrainBookingcheck";
    public static final String  DMTTransactionList="DMTTransactionList";
    public static final String  GiftBookingList="GiftBookingList";
    public static final String  BusBookingList="BusBookingList";
    public static final String  JCTMoneyList="JCTMoneyList";
    public static final String  PayoutList="PayoutList";
    public static final String  UtilityList="UtilityList";
    //AirBookingList,Top-UpDetails,TrainBookingList,TrainFailedList,TrainBookingcheck,DMTTransactionList,GiftBookingList

    //Accounts
    public static final String  AccountStatement="AccountStatement";
    public static final String  SalesAccountStatement="SalesAccStatement";
    public static final String  DepositUpdateRequest="DepositUpdateRequest";
    public static final String  DepositList="DepositList";
    public static final String  CreditUpdateRequest="CreditUpdateRequest";
    public static final String  CreditList="CreditList";
    //AccountStatement,DepositUpdateRequest,DepositList,CreditUpdateRequest,CreditList

    //Reports
    public static final String  AirSalesReport="AirSalesReport";
    public static final String  AirCancellationReport="AirCancellationReport";
    public static final String  AirRefundReport="AirRefundReport";
    public static final String  WeeklySalesReport="WeeklySalesReport";
    public static final String  TrainCancellationList="TrainCancellationList";
    public static final String  TrainSalesReport="TrainSalesReport";
    public static final String  DMTSalesReport="DMTSalesReport";
    public static final String  MobileSalesreport="MobileSalesreport";
    public static final String  NetSalesReport="NetSalesReport";
    //AirSalesReport,AirCancellationReport,AirRefundReport,WeeklySalesReport,TrainCancellationList,TrainSalesReport,DMTSalesReport,MobileSalesreport,NetSalesReport

    //Discounts&Markup
    public static final String  UpdateMarkup="UpdateMarkup";
    public static final String  UpdateMarkupGlobally="UpdateMarkupGlobally";
    public static final String  IntlMarkup="IntlMarkup";
    public static final String  UpdateB2CMarkup="UpdateB2CMarkup";
    public static final String  UpdateCategoryMarkup="UpdateCategoryMarkup";
    public static final String  AddMarkupAfterBooking="AddMarkupAfterBooking";
    //UpdateMarkup,UpdateMarkupGlobally,IntlMarkup,UpdateB2CMarkup,UpdateCategoryMarkup,UpdateCategoryMarkup,AddMarkupAfterBooking

    //Misc
    public static final String  UpdateProfile="UpdateProfile";
    public static final String  ChangePassword="ChangePassword";
    public static final String  TDRFilingList="TDRFilingList";
    public static final String  RailCalender="RailCalender";
    public static final String  AddNotification="AddNotification";
    public static final String  ShowNotification="ShowNotification";
    public static final String  ShowCustomerQuery="ShowCustomerQuery";
    //UpdateProfile,ChangePassword,TDRFilingList,RailCalender,AddNotification,ShowNotification,ShowCustomerQuery

    //DMT
    public static final String  DualNameDeclarationForm="DualNameDeclarationForm";
    public static final String  RulesforMoneyTransfer="RulesforMoneyTransfer";
    public static final String  AgentActivationList="AgentActivationList";
    //DualNameDeclarationForm,RulesforMoneyTransfer,AgentActivationList

    //GSTNotification
    public static final String GST="GST";

    public static final String AllMenuCodes="PendingBookingList,CancellationRequestList,StaffList,SalesMapping,AddAgency,AgencyList," +
            "UpdateCredit,AirBookingList,Top-UpDetails,TrainBookingList,TrainFailedList,TrainBookingcheck,DMTTransactionList,GiftBookingList," +
            "AccountStatement,DepositUpdateRequest,DepositList,CreditUpdateRequest,CreditList," +
            "AirSalesReport,AirCancellationReport,AirRefundReport,WeeklySalesReport,TrainCancellationList,TrainSalesReport,DMTSalesReport,MobileSalesreport,NetSalesReport," +
            "UpdateMarkup,UpdateMarkupGlobally,IntlMarkup,UpdateB2CMarkup,UpdateCategoryMarkup,UpdateCategoryMarkup,AddMarkupAfterBooking," +
            "UpdateProfile,ChangePassword,TDRFilingList,RailCalender,AddNotification,ShowNotification,ShowCustomerQuery," +
            "DualNameDeclarationForm,RulesforMoneyTransfer,AgentActivationList,GST";

    //implemented
    public static final String ImplementedMenuCodesNew=",AirRefundReport, AirSalesReport, AirCancellationReport";
    public static final String MenuCodesToShowInApp="UpdateCredit,TrainBookingcheck," +
            "DepositUpdateRequest,DepositList,CreditUpdateRequest," +
            "CreditList";

    public static final String MenuCodesSales="SalesAccStatement,NetSalesReport,ApproveAgent";
    public static final String MenuCodesDistributor="UpdateCredit,DepositList,CreditList";
    public static final String ImplementedMenuCodes="JCTMoneyList"+","+MenuCodesDistributor+","+MenuCodesSales;    //,Top-UpDetails,BusBookingList

//    public static final String ImplementedMenuCodes="";

    public static final String  MobileFragment="Mobile";
    public static final String  DMT ="Dmt";
    public static final String  FlightSearch="Flight";
    public static final String  HotelSearch="Hotel";
    public static final String  BusSearch="Bus";
//    public static final String  AEPS="Aeps";
    public static final String  AEPS="AAEPS";
    public static final String  AEPS_OLD ="AepsYes";
    public static final String  MATM="MATM";
    public static final String  CREDOPAY="CREDOPAY";
    public static final String  FAST_TAG="FastTag";
    public static final String  TRAIN="IRCTC";
    public static final String  BILL_PAY="BILL PAY";
    public static final String  CASH_OUT="PayOut";
    public static final String  LIC="LIC";
    public static final String  PAYTM="Paytm Wallet";
    public static final String  CASHFREE_QR="QR CODE";
    public static final String  AgentChattingFragment="S0";
    public static final String  AgentDepositRequestFragment="DepositRequest";
    public static final String  AgentCreditRequestFragment="CreditRequest";


}
