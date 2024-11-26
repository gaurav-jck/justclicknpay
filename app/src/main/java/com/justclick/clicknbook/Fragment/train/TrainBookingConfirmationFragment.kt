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
import com.justclick.clicknbook.databinding.FragmentTrainBookingConfirmationBinding
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import okhttp3.ResponseBody
import java.lang.Exception
import java.util.*


class TrainBookingConfirmationFragment : Fragment() {

    var trainBookingRequest: TrainBookingRequest?=null
    var trainPreBookResponse: TrainPreBookResponse?=null
    var binding:FragmentTrainBookingConfirmationBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_confirmation, container, false)
        binding=FragmentTrainBookingConfirmationBinding.bind(view)
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

        binding!!.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        binding!!.confirmBookingTv.setOnClickListener {
            makeBooking()
        }
        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun showHideFare(view: View) {
        if(binding!!.fareView.visibility== AdapterView.VISIBLE){
            binding!!.fareView.visibility= AdapterView.GONE
        }else{
            binding!!.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {
        var journeyDetail=trainBookingRequest!!.journeyDetails.get(0);
        var journeyDetail2=trainPreBookResponse!!.bookingDetails.journeyDetails.get(0)
        binding!!.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainNo+")"
        binding!!.startTimeTv.text = journeyDetail.departTime+"\n"+trainBookingRequest!!.dateToSet
        binding!!.endTimeTv.text = journeyDetail.arrivalTime+"\n"+getArrivalDate(journeyDetail.departTime,journeyDetail.duration)
        binding!!.fromStnTv.text = journeyDetail.fromStation+"\n("+journeyDetail.fromStationCode+")"
        binding!!.toStnTv.text = journeyDetail.reservationUpTo+"\n("+journeyDetail.reservationUpToCode+")"
        binding!!.durationTv.text = journeyDetail.duration!!.replace(":"," h ")+" m"
//        binding!!.classTv.text = "Class  "+journeyDetail.journeyClass
        binding!!.classTv.text = "Adult = "+trainPreBookResponse!!.bookingDetails.passengerAdditionalDetail.get(0).adultCount+" | "+
                "Child = "+trainPreBookResponse!!.bookingDetails.passengerAdditionalDetail.get(0).childCount+" | "+
                "Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        binding!!.boardingStn.text="Boarding point - "+journeyDetail.boardingStation+" ( "+journeyDetail.boardingStationCode+" )"

        var seats=trainBookingRequest!!.availableSeats
        binding!!.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            binding!!.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            binding!!.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        binding!!.passengerContainerLin!!.removeAllViews()
        for(list in trainBookingRequest!!.adultRequest.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_show_confirm, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.passengerName
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            var foodTv:TextView=child.findViewById(R.id.foodTv)
            ageTv.text= list.passengerAge
            genderTv.text= list.passengerGender
            count.text=(binding!!.passengerContainerLin!!.childCount+1).toString()
            val foodChoice=getFoodType(list.passengerFoodChoice)
            if(foodChoice!=null && foodChoice.isEmpty()){
                foodTv.text=""
            }else{
                foodTv.text= "Food- $foodChoice"
            }
            binding!!.passengerContainerLin!!.addView(child)
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
            count.text=(binding!!.passengerContainerLin!!.childCount+1).toString()

            delete.visibility=View.GONE
            binding!!.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainPreBookResponse!!.confirmFareDetail.get(0)
        binding!!.baseFareTv.setText(fareDetails.ticketFare.toString())
        binding!!.serviceChargeTv.setText(fareDetails.serviceCharge.toString())
        binding!!.agentChargeTv.setText(fareDetails.agentServiceCharge.toString())
        binding!!.insuranceChargeTv.setText(fareDetails.insurance.toString())
        binding!!.pgChargeTv.setText(fareDetails.pgCharge.toString())
        binding!!.concessionTv.setText(fareDetails.concession.toString())
        binding!!.totalFareTv.setText(fareDetails.totalFare.toString())

    }

    private fun getFoodType(value: String?): CharSequence? {
        if(value.equals("V")){
            return FoodChoice.Veg
        }else if(value.equals("N")){
            return FoodChoice.NonVeg
        }else if(value.equals("D")){
            return FoodChoice.DoNotSelect
        }else if(value.equals("E")){
            return FoodChoice.Snacks
        }else if(value.equals("J")){
            return FoodChoice.JainMeal
        }else if(value.equals("F")){
            return FoodChoice.VegDiabetic
        }else if(value.equals("G")){
            return FoodChoice.NonVegDiabetic
        }else if(value.equals("T")){
            return FoodChoice.TeaCoffee
        }else{
            return value!!
        }
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
        NetworkCall().callRailService(NetworkCall.getTrainApiInterface().finalBookTrain(trainBookingRequest, ApiConstants.Book,
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
//            var resp=response.string()
            val senderResponse = Gson().fromJson(response.string(), TrainBookingResponse::class.java)
            if(senderResponse!=null){
                if (senderResponse.statusCode.equals("00")) {
//                    Toast.makeText(context, senderResponse.statusCode, Toast.LENGTH_LONG).show()
                    if(senderResponse.railBookDetail!=null && senderResponse.railBookDetail.size>0){
//                        Toast.makeText(requireContext(), senderResponse.railBookDetail.get(0).trainUrl, Toast.LENGTH_LONG).show()
                        var bundle=Bundle()
                        senderResponse.railBookDetail.get(0).agent=agentCode
                        senderResponse.railBookDetail.get(0).userType=userType
                        senderResponse.railBookDetail.get(0).duration=binding!!.durationTv.text.toString()
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