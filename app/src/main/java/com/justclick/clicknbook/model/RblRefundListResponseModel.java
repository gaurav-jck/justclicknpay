package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class RblRefundListResponseModel
{
    public ArrayList<RefundListModel> Data;
    public String Status, StatusCode;

    public class RefundListModel{
        public String  AccountNo, AgencyCode, Bankrefno, MobileNo, RBLTxnID, RefundDate, RefundStatus,
                RefundFlag, ReservationID, Serial, ServiceCh, TStatus, TCount, TxnAmt, TxnType, PrintFlag,
                UpdateDate;
    }
}
