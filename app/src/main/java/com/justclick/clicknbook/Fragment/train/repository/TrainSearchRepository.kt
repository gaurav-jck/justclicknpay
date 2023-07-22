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
import org.json.JSONArray
import org.json.JSONObject


class TrainSearchRepository {

    private val trainSearchResponseLiveData: MutableLiveData<TrainSearchDataModel>? = MutableLiveData<TrainSearchDataModel>()
    private val trainRouteResponseLiveData: MutableLiveData<TrainRouteModel>? = MutableLiveData<TrainRouteModel>()


    fun searchTrains(fromStn: String?, toStn: String?, doj: String?, context: Context, doneCardUser: String, userType: String) {
        NetworkCall().callTrainServiceGet(ApiConstants.Availablity,fromStn,toStn,doj, context, CodeEnum.TrainSearch, doneCardUser, userType,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                var responseString=response.string()

                var jsonObject=JSONObject(responseString)

                if(!jsonObject.has("trainBtwnStnsList")){
                    var errorMessage=jsonObject.getString("errorMessage")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }else{
                    var trainSearchDataModel=parseDataManually(responseString)

//                responseString=responseString.replace("avlClasses\":\"", "avlClasses\":[\"")
//                responseString=responseString.replace("\",\"departureTime", "\"],\"departureTime")
//
//                responseString=responseString.replace("trainBtwnStnsList\":{", "trainBtwnStnsList\":[{")
//                responseString=responseString.replace("},\"vikalpInSpecialTrainsAccomFlag", "}],\"vikalpInSpecialTrainsAccomFlag")

//                val trainSearchDataModel = Gson().fromJson(responseString, TrainSearchDataModel::class.java)
                    trainSearchResponseLiveData!!.postValue(trainSearchDataModel!!)
                }

            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseDataManually(responseString:String): TrainSearchDataModel? {
        var trainSearchDataModel=TrainSearchDataModel()
        var jsonObject=JSONObject(responseString)
        var trainBtwnStnsList:Any = jsonObject.get("trainBtwnStnsList")

        var trainBtwStnArray=JSONArray()
        if(trainBtwnStnsList is JSONObject){
            var trainBtwStn=jsonObject.getJSONObject("trainBtwnStnsList")
            trainBtwStnArray.put(trainBtwStn)
        }else{
            trainBtwStnArray=jsonObject.getJSONArray("trainBtwnStnsList")
        }

        var trainArray:ArrayList<TrainSearchDataModel.TrainBtwnStnsList> = ArrayList()
        for(i in 0 until trainBtwStnArray.length()){
            var trainData:TrainSearchDataModel.TrainBtwnStnsList= TrainSearchDataModel.TrainBtwnStnsList()
            val jsonObject = trainBtwStnArray.getJSONObject(i)

            trainData.trainNumber=jsonObject.getString("trainNumber")
            trainData.trainName=jsonObject.getString("trainName")
            trainData.fromStnCode=jsonObject.getString("fromStnCode")
            trainData.toStnCode=jsonObject.getString("toStnCode")
            trainData.arrivalTime=jsonObject.getString("arrivalTime")
            trainData.departureTime=jsonObject.getString("departureTime")
//            trainData.distance=jsonObject.getString("distance")
            trainData.duration=jsonObject.getString("duration")
            trainData.runningMon=jsonObject.getString("runningMon")
            trainData.runningTue=jsonObject.getString("runningTue")
            trainData.runningWed=jsonObject.getString("runningWed")
            trainData.runningThu=jsonObject.getString("runningThu")
            trainData.runningFri=jsonObject.getString("runningFri")
            trainData.runningSat=jsonObject.getString("runningSat")
            trainData.runningSun=jsonObject.getString("runningSun")

            var availClass:Any = jsonObject.get("avlClasses")
            var availArray=JSONArray()
            if(availClass is String){
                val avlClass = jsonObject.getString("avlClasses")
                availArray.put(avlClass)
            }else{
                availArray=jsonObject.getJSONArray("avlClasses")
            }
            var avlStringArray= Array(availArray.length()){""}
            for(i in 0 until avlStringArray.size){
                avlStringArray[i]=availArray.optString(i)
            }
            trainData.avlClasses=avlStringArray

            trainArray.add(trainData)

        }

        var quotaList:Any = jsonObject.get("quotaList")
        var quotaListArray=JSONArray()
        if(quotaList is String){
            val quotaClass = jsonObject.getString("quotaList")
            quotaListArray.put(quotaClass)
        }else{
            quotaListArray=jsonObject.getJSONArray("quotaList")
        }
        var quotaArray= Array(quotaListArray.length()){""}
        for(i in 0 until quotaArray.size){
            quotaArray[i]=quotaListArray.optString(i)
        }

        trainSearchDataModel.trainBtwnStnsList=trainArray
        trainSearchDataModel.quotaList=quotaArray

        return trainSearchDataModel
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