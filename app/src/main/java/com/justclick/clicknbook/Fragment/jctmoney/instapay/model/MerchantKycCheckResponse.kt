package com.justclick.clicknbook.Fragment.jctmoney.instapay.model

data class MerchantKycCheckResponse(
    var statusCode:String,
    var statusMessage:String,
    val data:KycData
)
