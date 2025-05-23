package com.justclick.clicknbook.Fragment.train.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants


import com.justclick.clicknbook.Fragment.train.TrainListsFragment.OnListFragmentInteractionListener
import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.TrainListsItemBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.MyPreferences

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject


class TrainListAdapter(
        private val doj: String?,
        private val mValues: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?,
        private val quotaSpinner: AppCompatSpinner,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<TrainListAdapter.ViewHolder>() {
    companion object val General="Genaral Quota"
    val Ladies="Ladies Quota"
    public val SrCitizen="Sr. Citizen"
    public val Tatkal="Tatkal Quota"
    public val Handicapped="Handicapped"
//    private val mOnClickListener: View.OnClickListener
    var listPosition:Int=0
    var context:Context?=null
    var fareRuleResponse:FareRuleResponse?=null
    var loginModel:LoginModel?=null

    /*init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ArrayList<TrainSearchDataModel.TrainBtwnStnsList>
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item,v.id, listPosition)
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.train_lists_item, parent, false)
        context=parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.trainName.text = item.trainName
        holder.trainNumber.text = item.trainNumber
        holder.startTimeTv.text = item.departureTime
        holder.endTimeTv.text = item.arrivalTime
        holder.durationTv.text = item.duration!!.replace(":"," h ")+" m"
        holder.fromStnTv.text = item.fromStnCode
        holder.toStnTv.text = item.toStnCode

        holder.mView.setOnClickListener{
//            mListener?.onListFragmentInteraction(mValues,holder.trainName.id,position, CodeEnum.TrainDetail)
        }
        holder.trainNumber.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.trainName.id,position, CodeEnum.TrainRoute, fareRuleResponse)
        }
        holder.classLin.removeAllViews()
        for(value in item.avlClasses!!){
            var view:View= LayoutInflater.from(context).inflate(R.layout.train_class,null)
//            var textView:TextView= TextView(context)
            var textView:TextView= view.findViewById(R.id.classTv)
//            var layoutParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//            layoutParam.leftMargin=25
//            textView.layoutParams= layoutParam
//            textView.setPadding(15,7,15,7)
//            textView.setTextColor(context!!.resources.getColor(R.color.text_dark_gray))
            textView.setText(value)
//            textView.setTextSize(14f)
//            textView.setBackgroundResource(R.drawable.edittext_border_hotel)
            textView.setOnClickListener {
                fareRule(holder,item, value, position)
//                for(view in holder.classLin.children){
//                    var tv:TextView= view.findViewById(R.id.classTv)
//                    tv.setTextColor(context!!.resources.getColor(R.color.text_dark_gray))
////                    tv.setBackgroundResource(R.color.app_blue_color)
//                }
                textView.setBackgroundResource(R.color.app_blue_color)
                textView.setTextColor(context!!.resources.getColor(R.color.white))
            }
            holder.classLin.addView(view)
        }

        if(item.runningSun.equals("Y")){
            holder.sunday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.sunday.alpha=1f
        }else{
            holder.sunday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.sunday.alpha=0.4f
        }
        if(item.runningMon.equals("Y")){
            holder.monday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.monday.alpha=1f
        }else{
            holder.monday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.monday.alpha=0.4f
        }
        if(item.runningTue.equals("Y")){
            holder.tuesday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.tuesday.alpha=1f
        }else{
            holder.tuesday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.tuesday.alpha=0.4f
        }
        if(item.runningWed.equals("Y")){
            holder.wednesday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.wednesday.alpha=1f
        }else{
            holder.wednesday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.wednesday.alpha=0.4f
        }
        if(item.runningThu.equals("Y")){
            holder.thursday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.thursday.alpha=1f
        }else{
            holder.thursday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.thursday.alpha=0.4f
        }
        if(item.runningFri.equals("Y")){
            holder.friday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.friday.alpha=1f
        }else{
            holder.friday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.friday.alpha=0.4f
        }
        if(item.runningSat.equals("Y")){
            holder.saturday.setTextColor(context!!.resources.getColor(R.color.green_dark))
            holder.saturday.alpha=1f
        }else{
            holder.saturday.setTextColor(context!!.resources.getColor(R.color.train_red))
            holder.saturday.alpha=0.4f
        }

        if(mValues.get(position).fareRuleResponse!=null){
            holder.fareContainerLin.visibility=View.VISIBLE
            holder.fareTv.visibility=View.VISIBLE
            fareRuleResponse(mValues.get(position).fareRuleResponse!!, holder, position)
        }else{
            holder.fareContainerLin.visibility=View.GONE
            holder.fareTv.visibility=View.GONE
        }

//        if(holder.fareContainerLin.childCount>0){
//            holder.fareTv.visibility=View.VISIBLE
//        }else{
//            holder.fareTv.visibility=View.GONE
//        }

        /*with(holder.mView) {
            tag = mValues
            listPosition=position
            setOnClickListener(mOnClickListener)
        }*/
    }

    inner class FareRuleRequest{
        var FromStation:String?=null
        var ToStation:String?=null
        var JourneyDate:String?=null
        var JourneyClass:String?=null
        var JourneyQuota:String?=null
        var Identifier:String?=null
        var PaymentEnquiry:String?="N"
        var TrainNumber:String?=null
        var reservationChoice:String?="99"
        var moreThanOneDay:String?="true"
        var masterId:String?="WMATRIX00000"
    }

    private fun fareRule(holder: ViewHolder, item: TrainSearchDataModel.TrainBtwnStnsList, value: String, position: Int) {
//        Toast.makeText(context, quotaSpinner.selectedItem.toString(), Toast.LENGTH_SHORT).show()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        var fareRuleRequest=FareRuleRequest()
        fareRuleRequest.FromStation=item.fromStnCode
        fareRuleRequest.ToStation=item.toStnCode
        fareRuleRequest.JourneyClass=value
        fareRuleRequest.TrainNumber=item.trainNumber
        fareRuleRequest.JourneyDate=doj
        if(quotaSpinner.selectedItem!=null){
            fareRuleRequest.JourneyQuota=getQuotaChoice(quotaSpinner.selectedItem.toString())
        }else{
            fareRuleRequest.JourneyQuota="GN"
        }
        var req=Gson().toJson(fareRuleRequest)
        NetworkCall().callService(NetworkCall.getTrainApiInterface().trainFareRule(ApiConstants.FareAvailability, fareRuleRequest,
        loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App"),
                context,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                var responseString=response.string()
                /*if(responseString.contains("applicableBerthTypes")){
                    responseString=responseString.replace("applicableBerthTypes\":\"", "applicableBerthTypes\":[\"")
                    responseString=responseString.replace("\",\"atasEnable", "\"],\"atasEnable")
                }
                if(responseString.contains("avlDayList") && responseString.contains("bkgCfg")){
                    responseString=responseString.replace("avlDayList\":{", "avlDayList\":[{")
                    responseString=responseString.replace("},\"bkgCfg", "}],\"bkgCfg")
                }*/

                val fareRuleResponse = parseDataManually(responseString)
//                val fareRuleResponse = Gson().fromJson(responseString, FareRuleResponse::class.java)
                mValues!!.get(position).fareRuleResponse=fareRuleResponse
//                notifyDataSetChanged()
                notifyItemChanged(position)
//                fareRuleResponse(fareRuleResponse, holder, position)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseDataManually(responseString:String): FareRuleResponse? {
        var fareRuleResponse=FareRuleResponse()

        var jsonObject= JSONObject(responseString)

        fareRuleResponse.trainName=jsonObject.getString("trainName")
        fareRuleResponse.distance=jsonObject.getString("distance")
        fareRuleResponse.reqEnqParam=jsonObject.getString("reqEnqParam")
        fareRuleResponse.quota=jsonObject.getString("quota")
        fareRuleResponse.enqClass=jsonObject.getString("enqClass")
        fareRuleResponse.from=jsonObject.getString("from")
        fareRuleResponse.to=jsonObject.getString("to")
        fareRuleResponse.trainNo=jsonObject.getString("trainNo")
        fareRuleResponse.baseFare=jsonObject.getString("baseFare")
        fareRuleResponse.reservationCharge=jsonObject.getString("reservationCharge")
        fareRuleResponse.superfastCharge=jsonObject.getString("superfastCharge")
        fareRuleResponse.tatkalFare=jsonObject.getString("tatkalFare")
        fareRuleResponse.serviceTax=jsonObject.getString("serviceTax")
        fareRuleResponse.dynamicFare=jsonObject.getString("dynamicFare")
        fareRuleResponse.totalFare=jsonObject.getString("totalFare")
        if(jsonObject.has("serverId")){
            fareRuleResponse.serverId=jsonObject.getString("serverId")
        }else{
            fareRuleResponse.serverId=""
        }
        fareRuleResponse.timeStamp=jsonObject.getString("timeStamp")


        var avlDayList:Any = jsonObject.get("avlDayList")

        var avlDayListArray= JSONArray()
        if(avlDayList is JSONObject){
            var avlDay=jsonObject.getJSONObject("avlDayList")
            avlDayListArray.put(avlDay)
        }else{
            avlDayListArray=jsonObject.getJSONArray("avlDayList")
        }

        var avlDayArray:ArrayList<FareRuleResponse.avlDayList> = ArrayList()
        for(i in 0 until avlDayListArray.length()){
            var avlData:FareRuleResponse.avlDayList= fareRuleResponse.avlDayList()
            val jsonObject = avlDayListArray.getJSONObject(i)

            avlData.availablityDate=jsonObject.getString("availablityDate")
            avlData.availablityStatus=jsonObject.getString("availablityStatus")

            avlDayArray.add(avlData)
        }

        fareRuleResponse.avlDayList=avlDayArray

        var bkgData: FareRuleResponse.bkgCfg=fareRuleResponse.bkgCfg()
        var bkgObj=jsonObject.getJSONObject("bkgCfg")

        if(bkgObj.has("applicableBerthTypes")){
            var berthList:Any = bkgObj.get("applicableBerthTypes")
            var berthListArray= JSONArray()
            if(berthList is String){
                val quotaClass = bkgObj.getString("applicableBerthTypes")
                berthListArray.put(quotaClass)
            }else{
                berthListArray=bkgObj.getJSONArray("applicableBerthTypes")
            }
            var berthArray= Array(berthListArray.length()){""}
            for(i in 0 until berthArray.size){
                berthArray[i]=berthListArray.optString(i)
            }
            bkgData.applicableBerthTypes=berthArray
        }

        bkgData.foodChoiceEnabled=bkgObj.getString("foodChoiceEnabled")

        if(bkgData.foodChoiceEnabled.equals("true", true)){
//
            var foodDetails:Any = bkgObj.get("foodDetails")
            var foodDetailsArray= JSONArray()
            if(foodDetails is String){
                val foodDetails = bkgObj.getString("foodDetails")
                foodDetailsArray.put(foodDetails)
            }else{
                foodDetailsArray=bkgObj.getJSONArray("foodDetails")
            }
            var foodArray= Array(foodDetailsArray.length()){""}
            for(i in 0 until foodArray.size){
                foodArray[i]=foodDetailsArray.optString(i)
            }
            bkgData.foodDetails=foodArray
        }



        fareRuleResponse.bkgCfg=bkgData

        return fareRuleResponse
    }

    private fun getQuotaChoice(berthValue: String): String? {
        var berth=""
        when (berthValue) {
            General -> berth="GN"
            SrCitizen -> berth="SS"
            Ladies -> berth="LD"
            Handicapped -> berth="HP"
            Tatkal -> berth="TQ"
            else -> { // Note the block
                berth="GN"
            }
        }
        return berth
    }

    private fun fareRuleResponse(fareRuleResponse: FareRuleResponse, holder: ViewHolder, position: Int) {
        if(fareRuleResponse.errorMessage==null){
            holder.fareTv.text="Total fare "+context!!.getString(R.string.rupeeSymbolRs)+" "+fareRuleResponse.totalFare+" for class "+fareRuleResponse.enqClass
            holder.fareContainerLin.removeAllViews()
            for(availability in fareRuleResponse.avlDayList){
                val view = LayoutInflater.from(context)
                        .inflate(R.layout.train_list_fare_item,null, false)
                var dateTv=view.findViewById<TextView>(R.id.dateTv)
                var availableTv=view.findViewById<TextView>(R.id.availableTv)
                var bookTv=view.findViewById<TextView>(R.id.bookTv)
                dateTv.text = availability.availablityDate
                availableTv.text=availability.availablityStatus

                if(availability.availablityStatus.contains("NOT")||
                        availability.availablityStatus.contains("DEPART") ||
                        availability.availablityStatus.contains("CANCEL")){
                    availableTv.setTextColor(context!!.resources.getColor(R.color.red))
                    bookTv.isEnabled=false
                    bookTv.alpha=0.5f
                }else if(availability.availablityStatus.contains("AVAIL")||
                        availability.availablityStatus.contains("AVBL")){
                    availableTv.setTextColor(context!!.resources.getColor(R.color.green))
                    bookTv.isEnabled=true
                }else{
                    availableTv.setTextColor(context!!.resources.getColor(R.color.blue))
                    bookTv.isEnabled=true
                }
                bookTv.setOnClickListener{
//                    Toast.makeText(context, availability.availablityStatus, Toast.LENGTH_SHORT).show()
                    fareRuleResponse.availablityDate=availability.availablityDate
                    fareRuleResponse.availablityStatus=availability.availablityStatus
                    mListener?.onListFragmentInteraction(mValues,holder.trainName.id,position, CodeEnum.TrainDetail, fareRuleResponse)
                }
                holder.fareContainerLin.addView(view)
            }
        }else{
            Toast.makeText(context, fareRuleResponse.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var binding=TrainListsItemBinding.bind(mView)
        val trainNumber: TextView = binding.trainNoTv
        val trainName: TextView = binding.trainNameTv
        val startTimeTv: TextView = binding.startTimeTv
        val endTimeTv: TextView = binding.endTimeTv
        val durationTv: TextView = binding.durationTv
        val fromStnTv: TextView = binding.fromStnTv
        val toStnTv: TextView = binding.toStnTv
        val sunday: TextView = binding.sunday
        val monday: TextView = binding.monday
        val tuesday: TextView = binding.tuesday
        val wednesday: TextView = binding.wednesday
        val thursday: TextView = binding.thursday
        val friday: TextView = binding.friday
        val saturday: TextView = binding.saturday
        val classLin:LinearLayout=binding.classLin
        val fareContainerLin:LinearLayout=binding.fareContainerLin
        val fareTv:TextView=binding.fareTv

        override fun toString(): String {
            return super.toString() + " '" + trainName.text + "'"
        }
    }
}
