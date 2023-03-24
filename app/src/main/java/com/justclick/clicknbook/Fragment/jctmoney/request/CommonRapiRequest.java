package com.justclick.clicknbook.Fragment.jctmoney.request;

import com.justclick.clicknbook.ApiConstants;

public class CommonRapiRequest {
    private String Mode="App", MerchantId= ApiConstants.MerchantId, ApiService;

    public void setApiService(String apiService) {
        ApiService = apiService;
    }
}
