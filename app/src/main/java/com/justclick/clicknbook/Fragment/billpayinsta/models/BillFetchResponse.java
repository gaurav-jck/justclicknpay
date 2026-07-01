package com.justclick.clicknbook.Fragment.billpayinsta.models;

import java.io.Serializable;
import java.util.ArrayList;

public class BillFetchResponse implements Serializable{
    public String StatusCode, StatusMessage;
    public data data;

    public class data implements Serializable {
        public String enquiryReferenceId, CustomerName, BillNumber, BillPeriod, BillDate,
                BillDueDate, BillAmount;
        public ArrayList<CustomerParamsDetails> CustomerParamsDetails;
        public ArrayList<BillDetails> BillDetails;

    }

    public class CustomerParamsDetails implements Serializable{
        public String name;
    }

    public class BillDetails implements Serializable{
         public String name;
    }
}
