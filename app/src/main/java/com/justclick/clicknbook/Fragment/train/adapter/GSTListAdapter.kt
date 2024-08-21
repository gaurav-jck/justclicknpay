package com.justclick.clicknbook.Fragment.train.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.justclick.clicknbook.Fragment.train.TrainBookFragment
import com.justclick.clicknbook.Fragment.train.TrainListsFragment
import com.justclick.clicknbook.Fragment.train.model.GSTDetailResponse


import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.GstListItemBinding
import com.justclick.clicknbook.databinding.TrainRouteListItemBinding
import com.justclick.clicknbook.utils.CodeEnum


class GSTListAdapter(
    private val mValues: ArrayList<GSTDetailResponse.gstDetailsList>?,
    private val mListener: TrainBookFragment.OnGSTListListener?)
    : RecyclerView.Adapter<GSTListAdapter.ViewHolder>() {

//    private val mOnClickListener: View.OnClickListener
    var listPosition:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gst_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.gstNumber.text = item.gstin
        holder.gstName.text = item.nameOnGST
        holder.snoTv.text = (position+1).toString()+". "
        holder.gstAddress.text = item.flat+" "+item.city+"("+item.pincode+")"+", "+item.state

        if(position%2==0){
            holder.gstView.setBackgroundResource(R.color.gray_color_very_light)
        }else{
            holder.gstView.setBackgroundResource(R.color.transparent)
        }
        holder.deleteImg.setOnClickListener{
            mListener?.onGstListInteraction(mValues,holder.gstNumber.id,position, CodeEnum.GSTDelete)
        }
        holder.gstLin.setOnClickListener{
            mListener?.onGstListInteraction(mValues,holder.gstNumber.id,position, CodeEnum.GSTDetail)
        }

        /*holder.mView.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.stnNameTv.id,position, CodeEnum.TrainDetail)
        }
        holder.stnNameTv.setOnClickListener{
            mListener?.onListFragmentInteraction(mValues,holder.stnNameTv.id,position, CodeEnum.TrainRoute)
        }*/
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var binding= GstListItemBinding.bind(mView)
        val gstNumber: TextView = binding.gstTv
        val gstName: TextView = binding.gstNameTv
        val snoTv: TextView = binding.snoTv
        val deleteImg: ImageView = binding.deleteImg
        val gstAddress: TextView = binding.gstAddressTv
        val gstLin: LinearLayout = binding.gstLin
        val gstView:ConstraintLayout=binding.view
    }
}
