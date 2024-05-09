package com.justclick.clicknbook.Fragment.cashoutnew;

import java.util.ArrayList;

public class BankListResponse {
    public String statusCode, statusMessage;
    public ArrayList<data> data;

    public class data{
        public int srNo, id;
        public String agencyName, contactPerson, mobile, donecarduser, bankName,bankid, ifscCode,mobileNo,
                accountNo, filePath, mode, reqApprove, adminremark, createddate, isVerified, accountHolderName;
        public boolean isVisible;
    }
}
