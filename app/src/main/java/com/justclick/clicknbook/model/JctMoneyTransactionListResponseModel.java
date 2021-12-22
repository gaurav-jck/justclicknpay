package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class JctMoneyTransactionListResponseModel
{
    public ArrayList<Data> Data;
    public String Status, StatusCode;

    public class Data{
        public String AccountNo, AgencyName, BankName, CustMobile, MerchantID, Name,
                ReservationId, Serial, TCount, TxnAMt, TxnCharges, TxnStatus, TxnType, TxnDate;
        public boolean isChildShow=false;
        public ArrayList<DataInner> Data;
        public class DataInner{
            public String BankRefNo, ReferenceID, ReservationID, TID, TxnAMt, TxnCharges, txstatus_desc;
            public boolean RefundF;
        }
    }
}

