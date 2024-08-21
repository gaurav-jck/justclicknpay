package com.justclick.clicknbook.Fragment.train.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.TrainListsFragment


import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.TrainRouteListItemBinding



class TrainRouteAdapter(
        private val mValues: ArrayList<TrainRouteModel.StationList>?,
        private val mListener: TrainListsFragment.OnRouteListListener?)
    : RecyclerView.Adapter<TrainRouteAdapter.ViewHolder>() {

//    private val mOnClickListener: View.OnClickListener
    var listPosition:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.train_route_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!!.get(position)
        holder.stnNameTv.text = item.stationName+"\n("+item.stationCode+")"
        holder.dayTv.text = "Day "+item.dayCount
        holder.arrivalTv.text = item.arrivalTime
        holder.stopTimeTv.text = item.haltTime+" mins"
        holder.departureTv.text = item.departureTime

        if(position%2==0){
            holder.view.setBackgroundResource(R.color.gray_color_very_light)
        }else{
            holder.view.setBackgroundResource(R.color.transparent)
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
        var binding= TrainRouteListItemBinding.bind(mView)
        val stnNameTv: TextView = binding.stnNameTv
        val dayTv: TextView = binding.dayTv
        val arrivalTv: TextView = binding.arrivalTv
        val stopTimeTv: TextView = binding.stopTimeTv
        val departureTv: TextView = binding.departureTv
        val view: View = binding.view

        override fun toString(): String {
            return super.toString() + " '" + stnNameTv.text + "'"
        }
    }
}
