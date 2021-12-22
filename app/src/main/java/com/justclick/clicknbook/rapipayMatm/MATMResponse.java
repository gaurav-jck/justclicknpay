package com.justclick.clicknbook.rapipayMatm;

public class MATMResponse {
    private String BalanceEnquiryStatus;
    private String TransactionDatetime;
    private String AccountNo;
    private String TransactionStatus;
    private String BankName;
    private String DisplayMessage;
    private String ResponseCode;
    private String TerminalID;
    private String RRN;
    private String CardHolderName;
    private String CardBrandName;
    private String CardType,clientRefID,TransactionType,TxnAmt,AvailableBalance;

    public String getAvailableBalance() {
        return AvailableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        AvailableBalance = availableBalance;
    }

    public String getClientRefID() {
        return clientRefID;
    }

    public void setClientRefID(String clientRefID) {
        this.clientRefID = clientRefID;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getTxnAmt() {
        return TxnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        TxnAmt = txnAmt;
    }

    public String getDisplayMessage() {
        return DisplayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        DisplayMessage = displayMessage;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getRRN() {
        return RRN;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }

    public String getCardHolderName() {
        return CardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        CardHolderName = cardHolderName;
    }

    public String getCardBrandName() {
        return CardBrandName;
    }

    public void setCardBrandName(String cardBrandName) {
        CardBrandName = cardBrandName;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    private String CardNumber;

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getBalanceEnquiryStatus() {
        return BalanceEnquiryStatus;
    }

    public void setBalanceEnquiryStatus(String balanceEnquiryStatus) {
        BalanceEnquiryStatus = balanceEnquiryStatus;
    }

    public String getTransactionDatetime() {
        return TransactionDatetime;
    }

    public void setTransactionDatetime(String transactionDatetime) {
        TransactionDatetime = transactionDatetime;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        TransactionStatus = transactionStatus;
    }
    /*{"BalanceEnquiryStatus":"200","TransactionDatetime":"2\/17\/2021 12:28:30 PM",
    "AccountNo":"0582001500193800","TransactionStatus":"200","TxnAmt":"0",
    "AvailableBalance":"2249","CardHolderName":"",
    "CardBrandName":"RuPay Debit","CardType":"",
    "RRN":"104812426117","BankName":"Punjab National Bank",
    "TerminalID":"2104298D","CardNumber":"6070XXXXXXXX2358",
    "DisplayMessage":"SUCCESSFUL TRANSACTION ",
    "ResponseCode":"000","clientRefID":"202128171228","TransactionType":"BalanceEnquiry"}*/
}
