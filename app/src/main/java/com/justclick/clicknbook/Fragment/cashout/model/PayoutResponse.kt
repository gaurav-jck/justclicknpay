package com.justclick.clicknbook.Fragment.cashout.model

class PayoutResponse {
    var statusCode: String? = null
    var statusMessage:kotlin.String? = null
    var transactionDetails: ArrayList<TransactionDetails>? = null
    var bankDetails: List<BankDetails>? = null

    class TransactionDetails {
        var transactionId: String? = null
        var txnMessage: String? = null
        var bankRRN: String? = null
        var jckRefId: String? = null
        var amount: String? = null

    }

    class BankDetails{
        var beniName:String?=null
        var bank:String?=null
        var accountNumber:String?=null
        var ifscCode:String?=null
        var txnType:String?=null
        var amount:String?=null
        var txnMessage:String?=null
    }
}