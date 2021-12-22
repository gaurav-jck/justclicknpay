package com.justclick.clicknbook.Fragment.train.model

import java.io.Serializable

//https://rail.justclicknpay.com/apiV1/RailEngine/TrainRoute?journeyDate=20210625&startingStationCode=08310&fromstation=SZM

class TrainRouteModel : Serializable{

    var stationList:ArrayList<StationList>?=null

    class StationList : Serializable{
        var arrivalTime:String?=null
        var dayCount:String?=null
        var departureTime:String?=null
        var distance:String?=null
        var duration:String?=null
        var haltTime:String?=null
        var routeNumber:String?=null
        var stationCode:String?=null
        var stationName:String?=null
        var stnSerialNumber:String?=null
    }
}