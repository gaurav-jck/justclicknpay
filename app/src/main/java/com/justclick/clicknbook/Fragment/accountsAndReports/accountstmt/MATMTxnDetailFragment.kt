package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.TrainCancelDetailsFragment
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentDmtDetailReceiptBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"

class MATMTxnDetailFragment : Fragment() {
    private var dmtResponse: MatmDetailReceiptResponse.data? = null
    private var param2: String? = null
    private var loginModel: LoginModel?=null
    var binding: FragmentDmtDetailReceiptBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dmtResponse = it.getSerializable(ARG_PARAM1, MatmDetailReceiptResponse.data::class.java)
            }else{
                dmtResponse = it.getSerializable(ARG_PARAM1) as MatmDetailReceiptResponse.data
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_dmt_detail_receipt, container, false)
        binding= FragmentDmtDetailReceiptBinding.bind(view)
        if(dmtResponse!=null){
//            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
    try {
        setDmtReceiptData(view,dmtResponse!!)
    }catch (e:Exception){}

        }

        binding!!.backTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        /*view.cancelTv.setOnClickListener{
            cancelTicket(pnrResponse!!.pnrEnqueryresponse.pnrNumber)
        }*/

        return view
    }

    private fun setDmtReceiptData(view: View, dmtResponse: MatmDetailReceiptResponse.data) {
        binding!!.agentCodeTv.text = dmtResponse.agentCode
        binding!!.bankNameTv.text = dmtResponse.bankName
        binding!!.accountNoTv.text = dmtResponse.accountNo
        binding!!.mobileNoTv.text = dmtResponse.mobile
        binding!!.apiTxnIdTv.text = dmtResponse.transactionId
        binding!!.jckTxnIdTv.text = dmtResponse.jckTransactionId
        binding!!.bankRefNoTv.text = dmtResponse.rrn
        binding!!.remitAmountTv.text = dmtResponse.txnAmount
        binding!!.availBalTv.text = ""
        binding!!.txnTypeTv.text = dmtResponse.txnType
        binding!!.txnStatusTv.text = dmtResponse.txnStatusDesc
        binding!!.txnDateTv.text = dmtResponse.createdDate
    }

    private fun cancelTicket(pnr: String) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(
            ApiConstants.BASE_URL_TRAIN +"apiV1/RailEngine/GetCancelDetail?TransactionId="
                +pnr,
            loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        val response = Gson().fromJson(responseString, TrainCancelTicketDetailResponse::class.java)
                        if(response.statusCode.equals("00")){
//                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putSerializable("cancelResponse", response)
                            val fragment = TrainCancelDetailsFragment()
                            fragment.arguments = bundle
                            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                        }else{
                            Toast.makeText(context, response.statusMessage, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(
                            context,
                            R.string.response_failure_message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: java.lang.Exception) {
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

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: MatmDetailReceiptResponse.data) =
                MATMTxnDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, data)
                    }
                }
    }
}