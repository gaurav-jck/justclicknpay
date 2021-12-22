package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class DepositListForAgentModel
{
    public String Status, StatusCode;
    public ArrayList<DepositListData> Data;

    public class DepositListData {
        public String Serial, RequestedAmount, RequestedDate, BankName, BankNarration,
                UpdatedAmount, updatedBy, AgencyName, updatedDate, TCount, TransactionStatus, Remark,
                BankCharges, GST;

    }

}
