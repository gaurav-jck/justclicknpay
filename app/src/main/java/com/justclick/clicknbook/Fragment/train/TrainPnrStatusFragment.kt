package com.justclick.clicknbook.Fragment.train

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.adapter.TrainBookingListAdapter
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.PnrStatusResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainPnrStatusBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class TrainPnrStatusFragment : Fragment(), View.OnClickListener {

    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    var adapter: TrainBookingListAdapter?=null
    var loginModel: LoginModel?=null
    var arrayList:ArrayList<TrainBookingListResponseModel.reservationlist>?=null
    var fragView:View?=null
    var binding:FragmentTrainPnrStatusBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(fragView==null){
            val view = inflater.inflate(R.layout.fragment_train_pnr_status, container, false)
            binding=FragmentTrainPnrStatusBinding.bind(view)
            fragView=view
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            arrayList= ArrayList()

            binding!!.backArrow.setOnClickListener(this)
            binding!!.getTv.setOnClickListener(this)
            binding!!.statusTv.setOnClickListener(this)

        }

        return fragView
    }

    private fun searchPnr(reservationID: String) {
        NetworkCall().callTrainServiceFinalGet(ApiConstants.GetPnrDetails, reservationID, "", "",
                context, loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody?, i: Int) {
        try {
            if (response != null) {
                val responseModel: PnrResponse = Gson().fromJson<PnrResponse>(response.string(), PnrResponse::class.java)
                if (responseModel != null) {
                    if (responseModel.statusCode == "00") {
                        val bundle = Bundle()
                        bundle.putSerializable("trainResponse", responseModel)
                        val fragment = TrainBookingDetailsFragment()
                        fragment.arguments = bundle
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                    } else {
                        Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainBookingListResponseModel.reservationlist>?, id: Int, listPosition: Int)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
            R.id.get_tv->{
                if(!binding!!.numberEdt.text.toString().isEmpty()){
                    Common.hideSoftKeyboard((context as Activity))
                    getPnrStatus(binding!!.numberEdt.text.toString())
                }else{
                    Toast.makeText(context, "Please enter pnr number", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.statusTv->{
                if(!binding!!.numberEdt.text.toString().isEmpty()){
                    Common.hideSoftKeyboard((context as Activity))
                    getPnrStatus(binding!!.numberEdt.text.toString())
                }else{
                    Toast.makeText(context, "Please enter pnr number", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun getPnrStatus(pnr:String) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getTrainPnrStatus(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/PnrEnquiry?PnrNumber="+pnr,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null) {
                        var responseString=response.body()!!.string()
//                        responseString=responseString.replace("passengerList\":{", "passengerList\":[{")
//                        responseString=responseString.replace("},\"pnrNumber", "}],\"pnrNumber")
                        val responseData=Gson().fromJson(responseString, PnrStatusResponse::class.java)
                        if(responseData!=null){
                            if(responseData.statusCode.equals("00")){
                                pnrStatusData(responseData)
                            }else{
                                if(responseData.pnrEnqueryresponse!=null){
                                    if(responseData.pnrEnqueryresponse.errorMessage!=null && !responseData.pnrEnqueryresponse.errorMessage.isEmpty()){
                                        Toast.makeText(context, responseData.pnrEnqueryresponse.errorMessage, Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(context, responseData.statusMessage, Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    Toast.makeText(context, responseData.statusMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                        }else{
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pnrStatusData(response: PnrStatusResponse?) {
        val pnrDetailFragment= TrainPnrStatusDetailFragment.newInstance(response!!,"")
        (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

}
