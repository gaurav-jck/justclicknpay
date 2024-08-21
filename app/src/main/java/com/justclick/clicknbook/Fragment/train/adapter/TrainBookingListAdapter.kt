package com.justclick.clicknbook.Fragment.train.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.TrainBookingListFragment


import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.TrainBookingListsItemBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.DateAndTimeUtils


class TrainBookingListAdapter(
        private val mValues: ArrayList<TrainBookingListResponseModel.reservationlist>?,
        private val mListener: TrainBookingListFragment.OnListFragmentInteractionListener)
    : RecyclerView.Adapter<TrainBookingListAdapter.ViewHolder>() {
    var listPosition:Int=0
    var context:Context?=null
    var fareRuleResponse:FareRuleResponse?=null
    var loginModel:LoginModel?=null
    var binding:TrainBookingListsItemBinding?=null

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
        holder.trainNameTv.text = item.trainName+" ("+item.trainNumber+")"
        holder.fromStnTv.text = item.source+"\n("+item.sourceCode+")"
        holder.toStnTv.text = item.destination+"\n("+item.destinationCode+")"
//        holder.deptDataTv.text = item.departDate
        holder.deptDataTv.text = DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
        DateAndTimeUtils.DateTrainOutput, item.departDate)
//        holder.arrivalDataTv.text = item.arriveDate
        holder.arrivalDataTv.text = DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
            DateAndTimeUtils.DateTrainOutput, item.arriveDate)
        holder.durationTv.text = item.journeyClass+" | "+item.journeyQuota
        holder.boardingStn.text = "Boarding Stn : "+item.boardingStn

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
        var binding=TrainBookingListsItemBinding.bind(view)
        val pnrTv: TextView = binding.pnrTv
        val resIdTv: TextView = binding.resIdTv
        val trainNameTv: TextView = binding.trainNameTv
        val fromStnTv: TextView = binding.fromStnTv
        val toStnTv: TextView = binding.toStnTv
        val durationTv: TextView = binding.durationTv
        val deptDataTv: TextView = binding.deptDataTv
        val arrivalDataTv: TextView = binding.arrivalDataTv
        val boardingStn: TextView = binding.boardingStn
        val changeBoarding: TextView = binding.changeBoarding
        val cancelTicket: TextView = binding.cancelTicket
        val statusTv: TextView = binding.statusTv
        val operationLin: LinearLayout = binding.operationLin

        override fun toString(): String {
            return super.toString() + " '" + pnrTv.text + "'"
        }
    }
}
