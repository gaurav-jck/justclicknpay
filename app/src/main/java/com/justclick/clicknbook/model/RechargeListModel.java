package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class RechargeListModel
{
    public String Status, StatusCode;
    public ArrayList<RechargeListData> Data;

    public class RechargeListData {
        public String AgencyName, Commission, Mobile, Operator, Product,
                RechargeAmt, RechargeDate, RechargeRefID, ReferenceId,
                Serial, Status, TCount;
    }

}
