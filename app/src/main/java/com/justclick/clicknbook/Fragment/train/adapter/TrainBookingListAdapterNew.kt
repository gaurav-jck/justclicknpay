package com.justclick.clicknbook.Fragment.train.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.TrainBookingListFragment
import com.justclick.clicknbook.Fragment.train.TrainBookingListNewFragment


import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainListResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.DateAndTimeUtils
import kotlinx.android.synthetic.main.train_booking_lists_item.view.*


class TrainBookingListAdapterNew(
        private val mValues: ArrayList<TrainListResponse.reservationlist>?,
        private val mListener: TrainBookingListNewFragment.OnListFragmentInteractionListener)
    : RecyclerView.Adapter<TrainBookingListAdapterNew.ViewHolder>() {
    var listPosition:Int=0
    var context:Context?=null
    var fareRuleResponse:FareRuleResponse?=null
    var loginModel:LoginModel?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.train_booking_lists_item, parent, false)
        context=parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.pnrTv.text = "PNR : "+item.pnRno
        holder.resIdTv.text = "ResId : "+item.reservationID
        holder.trainNameTv.text = item.trainname+" ("+item.trainNumber+")"
//        holder.trainNameTv.text = item.trainNumber
        holder.fromStnTv.text = item.source/*+"\n("+item.sourceCode+")"*/
        holder.toStnTv.text = item.destination/*+"\n("+item.destinationCode+")"*/
//        holder.deptDataTv.text = item.departDate
        holder.deptDataTv.text = DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
        DateAndTimeUtils.DateTrainOutput, item.departDate)
//        holder.arrivalDataTv.text = item.arriveDate
        holder.arrivalDataTv.text = DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
            DateAndTimeUtils.DateTrainOutput, item.boardingDate)
        holder.durationTv.text = item.journeyClass+" | "+item.journeyQuota
//        holder.durationTv.text = ""
        holder.boardingStn.text = "Boarding Stn : "+item.source

        if(item.pnRno!=null && !item.pnRno.isEmpty()){
            holder.changeBoarding.visibility=View.VISIBLE
            holder.operationLin.visibility=View.VISIBLE
        }else{
            holder.changeBoarding.visibility=View.GONE
            holder.operationLin.visibility=View.GONE
        }

        holder.statusTv.setText(item.status)
        if(item.status.equals("CNF")){
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.green))
        }else if(item.status.equals("CAN")){
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.app_new_red_color))
        }else{
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.black_text_color))
        }

        holder.view.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.pnrTv.id,position)
        }
        holder.cancelTicket.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.cancelTicket.id,position)
        }
        holder.changeBoarding.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.changeBoarding.id,position)
        }

    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val view: View = mView
        val pnrTv: TextView = mView.pnrTv
        val resIdTv: TextView = mView.resIdTv
        val trainNameTv: TextView = mView.trainNameTv
        val fromStnTv: TextView = mView.fromStnTv
        val toStnTv: TextView = mView.toStnTv
        val durationTv: TextView = mView.durationTv
        val deptDataTv: TextView = mView.deptDataTv
        val arrivalDataTv: TextView = mView.arrivalDataTv
        val boardingStn: TextView = mView.boardingStn
        val changeBoarding: TextView = mView.changeBoarding
        val cancelTicket: TextView = mView.cancelTicket
        val statusTv: TextView = mView.statusTv
        val operationLin: LinearLayout = mView.operationLin

        override fun toString(): String {
            return super.toString() + " '" + pnrTv.text + "'"
        }
    }
}
