package com.justclick.clicknbook.Fragment.profilemenus.raisequery

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.TrainBookingListNewFragment


import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainListResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.RaiseTicketListsItemBinding
import com.justclick.clicknbook.databinding.TicketLogsItemBinding
import com.justclick.clicknbook.databinding.TrainBookingListsItemBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.DateAndTimeUtils


class TicketLogsListAdapter(
        private val mValues: ArrayList<TicketLogsListResponse.Data>?,
        private val mListener: QueryListFragment.OnListFragmentInteractionListener)
    : RecyclerView.Adapter<TicketLogsListAdapter.ViewHolder>() {
    var listPosition:Int=0
    var context:Context?=null
    var fareRuleResponse:FareRuleResponse?=null
    var loginModel:LoginModel?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.ticket_logs_item, parent, false)
        context=parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.logIdTv.text = item.LogsID.toString()
        holder.createdByTv.text = item.CreatedBy
        holder.dateTv.text = item.cDate
        holder.remarksTv.text = item.Remarks
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val view: View = mView
        var binding= TicketLogsItemBinding.bind(view)
        val logIdTv: TextView = binding.logIdTv
        val createdByTv: TextView = binding.createdByTv
        val dateTv: TextView = binding.dateTv
        val remarksTv: TextView = binding.remarksTv

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
