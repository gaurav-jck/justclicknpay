package com.justclick.clicknbook.Fragment.train.model

import java.io.Serializable

class TrainStationModel :Serializable{
    var success:String?=null
    var items:ArrayList<Items>?=null

    class Items : Serializable {
        var station_name:String?=null
        var station_code:String?=null
        var state_name:String?=null
        var city_name:String?=null
        var city_id:String?=null
    }
}