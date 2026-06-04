package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class CommonRapiRequest {
    private String Mode="App", MerchantId= ApiConstants.MerchantId,
            Merchant= ApiConstants.MerchantId,ApiService;
    private String IPAddress="101.212.323.434";

    public void setApiService(String apiService) {
        ApiService = apiService;
    }
    public void setIPAddress(String ipAddress) {
        IPAddress = ipAddress;
    }
}
