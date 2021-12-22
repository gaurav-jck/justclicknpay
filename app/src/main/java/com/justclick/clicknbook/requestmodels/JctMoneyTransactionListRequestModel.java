package com.justclick.clicknbook.requestmodels;

/**
 * Created by pc1 on 5/22/2017.
 */

public class JctMoneyTransactionListRequestModel extends CommonRequestModel {
    public String AgencyName, AgentID, Bankref, BookingType, Branch="All", CustMobile, Distributor,
            EndPage="", FromDate="", Name, PnrNumber,
            ReferenceID, ReservationID, Resp, StartPage="0", Status, TID, ToDate="", TxnID,
            UID, UserType, refunddate, txnstatus;
}

