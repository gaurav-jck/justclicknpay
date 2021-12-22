package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainBookingRequest
import com.justclick.clicknbook.Fragment.train.model.TrainBookingResponse
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import kotlinx.android.synthetic.main.fragment_train_booking_confirmation.*
import kotlinx.android.synthetic.main.fragment_train_booking_confirmation.view.*
import okhttp3.ResponseBody
import java.lang.Exception
import java.util.*


class TrainBookingConfirmationFragment : Fragment() {

    var trainBookingRequest: TrainBookingRequest?=null
    var trainPreBookResponse: TrainPreBookResponse?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_confirmation, container, false)

        if(arguments!=null) {
            trainBookingRequest = requireArguments().getSerializable("trainRequest") as TrainBookingRequest
            trainPreBookResponse = requireArguments().getSerializable("trainPreBookResponse") as TrainPreBookResponse
            var fromStationTv: TextView =view.findViewById(R.id.fromStationTv)
            var toStationTv: TextView =view.findViewById(R.id.toStationTv)
            var dateTv: TextView =view.findViewById(R.id.dateTv)
            fromStationTv.text=trainBookingRequest!!.journeyDetails.get(0).fromStation
            toStationTv.text=trainBookingRequest!!.journeyDetails.get(0).toStation
            dateTv.text=trainBookingRequest!!.dateToSet

            setData(view)
        }

        view.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        view.confirmBookingTv.setOnClickListener {
            makeBooking()
        }
        view.back_arrow.setOnClickListener {
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
        var journeyDetail=trainBookingRequest!!.journeyDetails.get(0);
        var journeyDetail2=trainPreBookResponse!!.bookingDetails.journeyDetails.get(0)
        view.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainNo+")"
        view.startTimeTv.text = journeyDetail.departTime+"\n"+trainBookingRequest!!.dateToSet
        view.endTimeTv.text = journeyDetail.arrivalTime+"\n"+getArrivalDate(journeyDetail.departTime,journeyDetail.duration)
        view.fromStnTv.text = journeyDetail.fromStation+"\n("+journeyDetail.fromStationCode+")"
        view.toStnTv.text = journeyDetail.reservationUpTo+"\n("+journeyDetail.reservationUpToCode+")"
        view.durationTv.text = journeyDetail.duration!!.replace(":"," h ")+" m"
//        view.classTv.text = "Class  "+journeyDetail.journeyClass
        view.classTv.text = "Adult = "+trainPreBookResponse!!.bookingDetails.passengerAdditionalDetail.get(0).adultCount+" | "+
                "Child = "+trainPreBookResponse!!.bookingDetails.passengerAdditionalDetail.get(0).childCount+" | "+
                "Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+journeyDetail.boardingStation+" ( "+journeyDetail.boardingStationCode+" )"

        var seats=trainBookingRequest!!.availableSeats
        view.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        view.passengerContainerLin!!.removeAllViews()
        for(list in trainBookingRequest!!.adultRequest.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.passengerName
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var delete:TextView=child.findViewById(R.id.delete)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            ageTv.text= list.passengerAge
            genderTv.text= list.passengerGender
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            delete.visibility=View.GONE
            view.passengerContainerLin!!.addView(child)
        }
        for(list in trainBookingRequest!!.childRequest.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.passengerName
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var delete:TextView=child.findViewById(R.id.delete)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            ageTv.text= list.passengerAge
            genderTv.text= list.passengerGender
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            delete.visibility=View.GONE
            view.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainPreBookResponse!!.confirmFareDetail.get(0)
        view.baseFareTv.setText(fareDetails.ticketFare.toString())
        view.serviceChargeTv.setText(fareDetails.serviceCharge.toString())
        view.agentChargeTv.setText(fareDetails.agentServiceCharge.toString())
        view.insuranceChargeTv.setText(fareDetails.insurance.toString())
        view.pgChargeTv.setText(fareDetails.pgCharge.toString())
        view.concessionTv.setText(fareDetails.concession.toString())
        view.totalFareTv.setText(fareDetails.totalFare.toString())

    }

    private fun getArrivalDate(departTime: String, duration: String): String {
        var journey=trainBookingRequest!!.journeyDetails.get(0).journeyDate
        var hour:Int=departTime.split(":")[0].toInt()+duration.split(":")[0].toInt()
        var min:Int=departTime.split(":")[1].toInt()+duration.split(":")[1].toInt()
        var addHour=min/60
        hour=hour+addHour
        var days=hour/24
        var date:Date=Common.getServerDateFormat().parse(journey)
        date.time.plus(days)

        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, days); //same with c.add(Calendar.DAY_OF_MONTH, 1);
//        c.add(Calendar.HOUR, 1);

        return Common.getShowInTVDateFormat().format(c.time).replace("/","-")
    }

    private fun makeBooking() {
        val json = Gson().toJson(trainBookingRequest)
        NetworkCall().callRailService(NetworkCall.getApiInterface().finalBookTrain(trainBookingRequest, ApiConstants.Book,
                trainBookingRequest!!.agentCode, trainBookingRequest!!.userType, ApiConstants.MerchantId, "App",
                trainBookingRequest!!.token, trainBookingRequest!!.userDate), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerFinal(response, 1, trainBookingRequest!!.agentCode, trainBookingRequest!!.userType)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerFinal(response: ResponseBody, i: Int, agentCode: String, userType: String) {
        try {
            val senderResponse = Gson().fromJson(response.string(), TrainBookingResponse::class.java)
            if(senderResponse!=null){
                if (senderResponse.statusCode.equals("00")) {
//                    Toast.makeText(context, senderResponse.statusCode, Toast.LENGTH_LONG).show()
                    if(senderResponse.railBookDetail!=null && senderResponse.railBookDetail.size>0){
//                        Toast.makeText(requireContext(), senderResponse.railBookDetail.get(0).trainUrl, Toast.LENGTH_LONG).show()
                        var bundle=Bundle()
                        senderResponse.railBookDetail.get(0).agent=agentCode
                        senderResponse.railBookDetail.get(0).userType=userType
                        senderResponse.railBookDetail.get(0).duration=durationTv.text.toString()
                        bundle.putSerializable("BookingData", senderResponse.railBookDetail.get(0))
                        bundle.putSerializable("TrainPreBookResponse", trainPreBookResponse)
                        val webview= TrainWebViewFragment()
                        webview.arguments=bundle
                        (context as NavigationDrawerActivity?)!!.replaceFragment(webview)
                    }
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }
}