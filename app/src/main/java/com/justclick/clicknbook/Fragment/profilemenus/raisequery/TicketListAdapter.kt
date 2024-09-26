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
import com.justclick.clicknbook.databinding.TrainBookingListsItemBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.DateAndTimeUtils


class TicketListAdapter(
        private val mValues: ArrayList<TicketListResponse.Data>?,
        private val mListener: QueryListFragment.OnListFragmentInteractionListener)
    : RecyclerView.Adapter<TicketListAdapter.ViewHolder>() {
    var listPosition:Int=0
    var context:Context?=null
    var fareRuleResponse:FareRuleResponse?=null
    var loginModel:LoginModel?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.raise_ticket_lists_item, parent, false)
        context=parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.comNoTv.text = item.ComplaintNo
        holder.productTv.text = item.ProductType
        holder.dateTv.text = item.cDate
        holder.issueTypeTv.text = item.IssueType
        holder.agentIdTv.text = item.donecarduser.uppercase()
        holder.mobileTv.text = item.MobileNo
        holder.emailTv.text = item.EmailID
        holder.statusTv.text = item.statusname

        holder.statusTv.setText(item.statusname)
      /*  if(item.statusname.equals("Close")){
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.green))
        }else if(item.statusname.equals("Open")){
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.app_new_red_color))
        }else{
            holder.statusTv.setTextColor(context!!.resources.getColor(R.color.black_text_color))
        }*/

        holder.statusTv.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.statusTv.id,position)
        }
        holder.viewTv.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.viewTv.id,position)
        }
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val view: View = mView
        var binding= RaiseTicketListsItemBinding.bind(view)
        val comNoTv: TextView = binding.comNoTv
        val productTv: TextView = binding.productTv
        val agentIdTv: TextView = binding.agentIdTv
        val issueTypeTv: TextView = binding.issueTypeTv
        val mobileTv: TextView = binding.mobileTv
        val emailTv: TextView = binding.emailTv
        val dateTv: TextView = binding.dateTv
        val statusTv: TextView = binding.statusTv
        val viewTv: TextView = binding.viewTv

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
