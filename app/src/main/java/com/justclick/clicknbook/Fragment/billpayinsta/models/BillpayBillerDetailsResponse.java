package com.justclick.clicknbook.Fragment.billpayinsta.models;

import java.util.ArrayList;

public class BillpayBillerDetailsResponse {
    public String StatusCode, StatusMessage;
    public data data;

    public class data{
        public String billerId, mode, acceptsAdhoc, paymentAmountExactness, supportValidation,
                fetchRequirement;
        public billerInfo billerInfo;
        public ArrayList<parameters> parameters;
    }

    public class billerInfo{
        public String type, name, description, ownership, effectFrom, effectTo;
    }

    public class parameters{
         public String name, desc, minLength, maxLength, inputType, regex;
         public int mandatory;
    }
}
