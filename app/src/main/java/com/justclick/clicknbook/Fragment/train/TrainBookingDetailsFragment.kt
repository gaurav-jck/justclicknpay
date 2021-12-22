package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.R
import kotlinx.android.synthetic.main.fragment_train_booking_details.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.statusTv


class TrainBookingDetailsFragment : Fragment() {

    var trainResponse: PnrResponse?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_details, container, false)

        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("trainResponse") as PnrResponse
            var fromStationTv: TextView =view.findViewById(R.id.fromStationTv)
            var toStationTv: TextView =view.findViewById(R.id.toStationTv)
            var dateTv: TextView =view.findViewById(R.id.dateTv)
            fromStationTv.text=trainResponse!!.transactionDetails.get(0).boarding
            toStationTv.text=trainResponse!!.transactionDetails.get(0).resvUpto
            dateTv.text=trainResponse!!.transactionDetails.get(0).dateOfJourney

            setData(view)
        }

        view.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.okTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun showHideFare(view: View) {
        if(view.fareView.visibility== AdapterView.VISIBLE){
            view.fareView.visibility= AdapterView.GONE
        }else{
            view.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {

        view.statusTv.text="ResId : "+trainResponse!!.reservationid
        view.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        view.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainnumber+")"
        try{
            view.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
                    journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
            view.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
                    journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)
        }catch (e:Exception){

        }
        view.fromStnTv.text = journeyDetail.from
        view.toStnTv.text = journeyDetail.to
        view.durationTv.text = journeyDetail.duration
        view.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+journeyDetail.boarding

        var seats=""
        view.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        view.passengerContainerLin!!.removeAllViews()
        for(list in trainResponse!!.passengerDetails.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_seats_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var seatNoTv:TextView=child.findViewById(R.id.seatNoTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            ageTv.text= list.age
            child.genderTv.text= list.sex
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            seatNoTv.text="Seat no."/*+list.sNo*/
            statusTv.text=list.bookingStatus

            view.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainResponse!!.fareDetails.get(0)
        view.baseFareTv.setText(fareDetails.ticketFar.toString())
        view.cateringTv.setText(fareDetails.cateringCharge.toString())
        view.conFeeTv.setText(fareDetails.convenienceFee.toString())
        view.pgChargeTv.setText(fareDetails.pgCharges.toString())
        view.insuranceTv.setText(fareDetails.travelInsurancePremium.toString())
        view.serviceChargeTv.setText(fareDetails.travelAgentServiceCharge.toString())
        view.totalFareTv.setText(fareDetails.totalFare.toString())

    }
}