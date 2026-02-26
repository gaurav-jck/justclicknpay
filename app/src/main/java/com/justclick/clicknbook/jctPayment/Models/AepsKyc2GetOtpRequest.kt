package com.justclick.clicknbook.jctPayment.Models

import com.justclick.clicknbook.ApiConstants

class AepsKyc2GetOtpRequest {
    var AgentCode: String? = null
    var Mode: String? ="App"
    var Latitude: String? = null
    var Longitude: String?=null
    var Merchant: String=ApiConstants.MerchantId
    var Transactionid: String?=null
    var otpreqid: String?=null
    var otp: String?=null
}
