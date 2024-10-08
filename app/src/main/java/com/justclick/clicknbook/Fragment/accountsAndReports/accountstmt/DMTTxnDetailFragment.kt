package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentDmtDetailReceiptBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences

/**
 * A simple [Fragment] subclass.
 * Use the [DMTTxnDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class DMTTxnDetailFragment : Fragment() {
    private var dmtResponse: DmtDetailReceiptResponse.data? = null
    private var param2: String? = null
    private var loginModel: LoginModel?=null
    var binding: FragmentDmtDetailReceiptBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dmtResponse = it.getSerializable(ARG_PARAM1, DmtDetailReceiptResponse.data::class.java)
            }else{
                dmtResponse = it.getSerializable(ARG_PARAM1) as DmtDetailReceiptResponse.data
            }
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_dmt_detail_receipt, container, false)
        binding= FragmentDmtDetailReceiptBinding.bind(view)
        binding!!.title.setText("DMT Receipt")
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

    private fun setDmtReceiptData(view: View, dmtResponse: DmtDetailReceiptResponse.data) {
        binding!!.agentCodeTv.text = dmtResponse.agentCode
        binding!!.bankNameTv.text = dmtResponse.bankName
        binding!!.accountNoTv.text = dmtResponse.accountNumber
        binding!!.mobileNoTv.text = dmtResponse.mobile
        binding!!.apiTxnIdTv.text = dmtResponse.transactionId
        binding!!.jckTxnIdTv.text = dmtResponse.jckTransactionId
        binding!!.bankRefNoTv.text = dmtResponse.rrn
        binding!!.remitAmountTv.text = dmtResponse.txnAmt
        binding!!.availBalTv.text = ""
        binding!!.txnTypeTv.text = dmtResponse.txnType
        binding!!.txnStatusTv.text = dmtResponse.txnDescription
        binding!!.txnDateTv.text = dmtResponse.createdDate
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
        fun newInstance(data: DmtDetailReceiptResponse.data) =
                DMTTxnDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, data)
                    }
                }
    }
}