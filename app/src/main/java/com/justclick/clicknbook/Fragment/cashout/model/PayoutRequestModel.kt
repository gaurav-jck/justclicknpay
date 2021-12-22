package com.justclick.clicknbook.Fragment.cashout.model

import com.justclick.clicknbook.ApiConstants

class PayoutRequestModel {
    var Name:String?=null
    var BeniId:String?=null
    var TransferType:String?=null
    var Amount:Int?=null
    var MobileNumber:String?=null
    val Mode:String?="APP"
    var AgentCode:String?=null
    var MerchantId:String?=ApiConstants.MerchantId
    var IFSC:String?=null
    var BankName:String?=null
    var AccountNumber:String?=null
    var Email:String?=null

}