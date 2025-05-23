package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainListResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainChangeBoardingBinding
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.MyCustomDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TrainChangeBoardingStnFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var listData: TrainListResponse.reservationlist? = null
    private var boardingArray: Array<String?> = emptyArray()
    var binding:FragmentTrainChangeBoardingBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            boardingArray = it.getSerializable(ARG_PARAM1) as Array<String?>
            listData = it.getSerializable(ARG_PARAM2) as TrainListResponse.reservationlist
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_train_change_boarding, container, false)
        binding=FragmentTrainChangeBoardingBinding.bind(view)
        if(listData!=null){
//            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            setPnrStatusData(view)
        }

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding!!.submitTv.setOnClickListener {
            changeBoardingStn()
        }

        return view
    }

    private fun setPnrStatusData(view: View) {
        binding!!.pnrTv.setText("PNR : "+listData!!.pnRno)
        binding!!.trainNameTv.setText(listData!!.trainname+" ("+listData!!.trainNumber+")")
        binding!!.fromStnTv.setText(listData!!.source.replace("(", "\n("))
        binding!!.toStnTv.setText(listData!!.destination.replace("(", "\n("))
        binding!!.durationTv.setText(listData!!.journeyClass+" | "+listData!!.journeyQuota)
        binding!!.boardingStn.setText("Boarding point - "+listData!!.source+
                "\nDate Of Journey - "+listData!!.departDate)

        binding!!.spinnerBoardingStn.adapter=getSpinnerAdapter(boardingArray)
    }

    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter(requireContext(),
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    class ChangeStnResponse{
        var newBoardingPoint:String?=null
        var oldBoardingPoint:String?=null
        var newBoardingDate:String?=null
        var pnr:String?=null
        var status:String?=null
        var statusCode:String?=null
        var statusMessage:String?=null
    }

    private fun changeBoardingStn() {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.changeBoardingStn(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/PostBoardingStationChange?StationCode="
                +getStnCode()+"&Pnr="+listData!!.pnRno+"&ReservationId="+listData!!.reservationID)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
//                        responseString=responseString.replace("boardingStationList\":{", "boardingStationList\":[{")
//                        responseString=responseString.replace("},\"mealChoiceenable", "}],\"mealChoiceenable")
                        val boardingStnResponse = Gson().fromJson(responseString, ChangeStnResponse::class.java)
                        if(boardingStnResponse!=null){
                            if(boardingStnResponse.status!=null && boardingStnResponse.status.equals("00")){
                                Toast.makeText(context, boardingStnResponse.statusMessage, Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(context, boardingStnResponse.statusMessage, Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getStnCode(): String? {
        var stn=binding!!.spinnerBoardingStn.selectedItem.toString()
        return stn.split("-")[1].trim()
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Array<String?>, param2: TrainListResponse.reservationlist) =
                TrainChangeBoardingStnFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                        putSerializable(ARG_PARAM2, param2)
                    }
                }
    }
}