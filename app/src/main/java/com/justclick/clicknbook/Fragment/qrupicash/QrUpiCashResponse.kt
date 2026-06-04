package com.justclick.clicknbook.Fragment.qrupicash

class QrUpiCashResponse{
        var statusCode:String?=null
        var statusMessage:String?=null
        var data:Data?=null

        class Data{
                var qrMobile:String?=null
                var transactionValue:String?=null
        }
    }