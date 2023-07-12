package com.justclick.clicknbook.Fragment.train.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.CodeEnum
import okhttp3.ResponseBody
import org.json.JSONObject


class TrainSearchRepository {

    private val trainSearchResponseLiveData: MutableLiveData<TrainSearchDataModel>? = MutableLiveData<TrainSearchDataModel>()
    private val trainRouteResponseLiveData: MutableLiveData<TrainRouteModel>? = MutableLiveData<TrainRouteModel>()


    fun searchTrains(fromStn: String?, toStn: String?, doj: String?, context: Context, doneCardUser: String, userType: String) {
        NetworkCall().callTrainServiceGet(ApiConstants.Availablity,fromStn,toStn,doj, context, CodeEnum.TrainSearch, doneCardUser, userType,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                var responseString=response.string()
                responseString=responseString.replace("avlClasses\":\"", "avlClasses\":[\"")
                responseString=responseString.replace("\",\"departureTime", "\"],\"departureTime")

                var jsonObject=JSONObject(responseString)

                /*var trainBtwnStnsList=jsonObject.getJSONArray("trainBtwnStnsList")
                for(i in 0 until trainBtwnStnsList.length()){
                    var json=trainBtwnStnsList.getJSONObject(i)

                }*/

                responseString=responseString.replace("trainBtwnStnsList\":{", "trainBtwnStnsList\":[{")
                responseString=responseString.replace("},\"vikalpInSpecialTrainsAccomFlag", "}],\"vikalpInSpecialTrainsAccomFlag")

                val trainSearchDataModel = Gson().fromJson(responseString, TrainSearchDataModel::class.java)
                trainSearchResponseLiveData!!.postValue(trainSearchDataModel)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTrainSearchResponseLiveData(): MutableLiveData<TrainSearchDataModel>? {
        return trainSearchResponseLiveData
    }

    fun findTrainRoute(doj: String?, trainNo: String?, stnCode: String?, context: Context, doneCardUser: String, userType: String) {
        NetworkCall().callTrainServiceGet(ApiConstants.TrainRoute,doj,trainNo,stnCode, context, CodeEnum.TrainRoute, doneCardUser, userType,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                val trainSearchDataModel = Gson().fromJson(response.string(), TrainRouteModel::class.java)
                trainRouteResponseLiveData!!.postValue(trainSearchDataModel)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTrainRouteResponseLiveData(): MutableLiveData<TrainRouteModel>? {
        return trainRouteResponseLiveData
    }
}