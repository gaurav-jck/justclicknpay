package com.justclick.clicknbook.Fragment.jctmoney.dmt3.requestModel;

import com.justclick.clicknbook.ApiConstants;

public class AddBeneRequest{
    private String Mode="APP", MerchantId= ApiConstants.MerchantId;
    public String BeneName,AccountNo,BankName, Bankid,
            IFSCCode,Mobile,AgentCode, Latitude, Longitude;
    public boolean isVerified;

}
