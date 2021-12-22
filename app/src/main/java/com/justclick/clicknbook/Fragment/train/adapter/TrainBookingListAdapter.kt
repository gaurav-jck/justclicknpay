package com.justclick.clicknbook.Fragment.train.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.TrainBookingListFragment


import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import kotlinx.android.synthetic.main.train_booking_lists_item.view.*


class TrainBookingListAdapter(
        private val mValues: ArrayList<TrainBookingListResponseModel.reservationlist>?,
        private val mListener: TrainBookingListFragment.OnListFragmentInteractionListener)
    : RecyclerView.Adapter<TrainBookingListAdapter.ViewHolder>() {
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
        holder.trainNameTv.text = item.trainName+" ("+item.trainNumber+")"
        holder.fromStnTv.text = item.source.replace("(","\n(")
        holder.toStnTv.text = item.destination.replace("(","\n(")
        holder.durationTv.text = item.journeyClass+" | "+item.journeyQuota
        holder.boardingStn.text = "Boarding Stn : "+item.boardingStn

        if(item.pnRno!=null && !item.pnRno.isEmpty()){
            holder.changeBoarding.visibility=View.VISIBLE
        }else{
            holder.changeBoarding.visibility=View.GONE
        }

        holder.view.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.pnrTv.id,position)
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
        val boardingStn: TextView = mView.boardingStn
        val changeBoarding: TextView = mView.changeBoarding

        override fun toString(): String {
            return super.toString() + " '" + pnrTv.text + "'"
        }
    }
}
