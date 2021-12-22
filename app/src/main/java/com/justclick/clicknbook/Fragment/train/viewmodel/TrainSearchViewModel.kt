package com.justclick.clicknbook.Fragment.train.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.Fragment.train.repository.TrainSearchRepository


class TrainSearchViewModel: ViewModel() {
    private var trainSearchRepository: TrainSearchRepository? = null
    private var trainSearchResponseLiveData: MutableLiveData<TrainSearchDataModel>? = null
    private var trainRouteResponseLiveData: MutableLiveData<TrainRouteModel>? = null

    init {
        trainSearchRepository = TrainSearchRepository()
        trainSearchResponseLiveData = trainSearchRepository!!.getTrainSearchResponseLiveData()
        trainRouteResponseLiveData = trainSearchRepository!!.getTrainRouteResponseLiveData()
    }

    fun searchTrains(fromStn: String?, toStn: String?, doj: String?, context: Context, doneCardUser: String, userType: String) {
        trainSearchRepository!!.searchTrains(fromStn,toStn,doj,context, doneCardUser, userType)
    }

    fun getTrainSearchResponseLiveData(): MutableLiveData<TrainSearchDataModel>? {
        return trainSearchResponseLiveData
    }

    fun findTrainRoute(doj: String?, trainNo: String?, stnCode: String?, context: Context, doneCardUser: String, userType: String) {
        trainSearchRepository!!.findTrainRoute(doj,trainNo,stnCode,context, doneCardUser, userType)
    }

    fun getTrainRouteResponseLiveData(): MutableLiveData<TrainRouteModel>? {
        return trainRouteResponseLiveData
    }
}