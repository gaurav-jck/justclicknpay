package com.justclick.clicknbook.Fragment.train.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//https://rail.justclicknpay.com/apiV1/RailEngine/Availablity?FromStation=NDLS&ToStation=PNBE&Doj=20210625

class TrainSearchDataModel :Serializable{
    @SerializedName("trainBtwnStnsList")
    var trainBtwnStnsList:ArrayList<TrainBtwnStnsList>?=null
    var fromStnCode:String?=null
    var toStnCode:String?=null
    var fromStnName:String?=null
    var toStnName:String?=null
    var date:String?=null
    var doj:String?=null
    var quota:String?=null
    var quotaList:Array<String>?=null

    class TrainBtwnStnsList :Serializable{
        var fareRuleResponse:FareRuleResponse?=null
        var arrivalTime:String?=null
        var departureTime:String?=null
        var duration:String?=null
        var fromStnCode:String?=null
        var toStnCode:String?=null
        var trainName:String?=null
        var trainNumber:String?=null
        var runningSun:String?=null
        var runningMon:String?=null
        var runningTue:String?=null
        var runningWed:String?=null
        var runningThu:String?=null
        var runningFri:String?=null
        var runningSat:String?=null

        var trainType:String?=null
        var trainTypeName:String?=null

//        var avlClasses:String?=null
        var avlClasses:Array<String>?=null
        var avlClassesList:ArrayList<avlClass>?=null



       /* "atasOpted": "false",
        "avlClasses": "2S",
        "distance": "982",
        "flexiFlag": "false",
       */
    }

    class avlClass : Serializable{
        public var className:String?=null
        public var classSelect:Boolean?=false
    }

}
