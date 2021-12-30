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
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.MyPreferences

import kotlinx.android.synthetic.main.train_lists_item.view.*
import okhttp3.ResponseBody


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
            holder.sunday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.sunday.alpha=1f
        }else{
            holder.sunday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.sunday.alpha=0.5f
        }
        if(item.runningMon.equals("Y")){
            holder.monday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.monday.alpha=1f
        }else{
            holder.monday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.monday.alpha=0.5f
        }
        if(item.runningTue.equals("Y")){
            holder.tuesday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.tuesday.alpha=1f
        }else{
            holder.tuesday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.tuesday.alpha=0.5f
        }
        if(item.runningWed.equals("Y")){
            holder.wednesday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.wednesday.alpha=1f
        }else{
            holder.wednesday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.wednesday.alpha=0.5f
        }
        if(item.runningThu.equals("Y")){
            holder.thursday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.thursday.alpha=1f
        }else{
            holder.thursday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.thursday.alpha=0.5f
        }
        if(item.runningFri.equals("Y")){
            holder.friday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.friday.alpha=1f
        }else{
            holder.friday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.friday.alpha=0.5f
        }
        if(item.runningSat.equals("Y")){
            holder.saturday.setTextColor(context!!.resources.getColor(R.color.green))
            holder.saturday.alpha=1f
        }else{
            holder.saturday.setTextColor(context!!.resources.getColor(R.color.gray))
            holder.saturday.alpha=0.5f
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
        NetworkCall().callService(NetworkCall.getTrainApiInterface().trainFareRule(ApiConstants.FareAvailability, fareRuleRequest,
        loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App"),
                context,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                var responseString=response.string()
                responseString=responseString.replace("applicableBerthTypes\":\"", "applicableBerthTypes\":[\"")
                responseString=responseString.replace("\",\"atasEnable", "\"],\"atasEnable")

                responseString=responseString.replace("avlDayList\":{", "avlDayList\":[{")
                responseString=responseString.replace("},\"bkgCfg", "}],\"bkgCfg")

                val fareRuleResponse = Gson().fromJson(responseString, FareRuleResponse::class.java)
                mValues!!.get(position).fareRuleResponse=fareRuleResponse
//                notifyDataSetChanged()
                notifyItemChanged(position)
//                fareRuleResponse(fareRuleResponse, holder, position)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
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
        val trainNumber: TextView = mView.trainNoTv
        val trainName: TextView = mView.trainNameTv
        val startTimeTv: TextView = mView.startTimeTv
        val endTimeTv: TextView = mView.endTimeTv
        val durationTv: TextView = mView.durationTv
        val fromStnTv: TextView = mView.fromStnTv
        val toStnTv: TextView = mView.toStnTv
        val sunday: TextView = mView.sunday
        val monday: TextView = mView.monday
        val tuesday: TextView = mView.tuesday
        val wednesday: TextView = mView.wednesday
        val thursday: TextView = mView.thursday
        val friday: TextView = mView.friday
        val saturday: TextView = mView.saturday
        val classLin:LinearLayout=mView.classLin
        val fareContainerLin:LinearLayout=mView.fareContainerLin
        val fareTv:TextView=mView.fareTv

        override fun toString(): String {
            return super.toString() + " '" + trainName.text + "'"
        }
    }
}
