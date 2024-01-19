package com.justclick.clicknbook.Fragment.fasttag

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.billpay.BillConfirmationFragment
import com.justclick.clicknbook.Fragment.billpay.BillPayFragment
import com.justclick.clicknbook.Fragment.billpay.BillPayFragment.FetchBillResponseModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.utils.MyPreferences
import java.lang.Exception
import java.util.ArrayList

class FasttagFragment : Fragment(), AdapterView.OnItemSelectedListener {
    var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null
    val rechargeType="FastTag"
    var operatorListData:ArrayList<BillPayFragment.OperatorResponseModel.operatorlistDetails>?= arrayListOf()
    var operatorData:BillPayFragment.OperatorResponseModel.operatorlistDetails?= null
    var loginModel:LoginModel= LoginModel()
    var userIdEdt:EditText?=null

    companion object {
        fun newInstance() = FasttagFragment()
    }

    private lateinit var viewModel: FasttagViewModel
    private var spinner_operator:Spinner?=null
    private var mView:View?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(this).get(FasttagViewModel::class.java)
        viewModel.getOperatorLiveData()!!.observe(this) { operatorList ->
            bindOperatorList(operatorList)
        }
        viewModel.fetchBillLiveData()!!.observe(this) { billDetails ->
            fetchBillDetails(billDetails)
        }
    }

    private fun fetchBillDetails(billDetails: BillPayFragment.FetchBillResponseModel?) {
        try {
            if (billDetails != null && billDetails.statusCode == "00") {
                if (billDetails.payfetchbilllist != null) {
                    showBillDetail(billDetails/*, rechargeType, operator*/)
                } else {
                    Toast.makeText(context, billDetails.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, billDetails!!.statusMessage, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(mView==null){
            mView= inflater.inflate(R.layout.fasttag_fragment, container, false)
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            spinner_operator=mView!!.findViewById(R.id.spinner_operator)
            userIdEdt=mView!!.findViewById(R.id.userIdEdt)
            spinner_operator!!.onItemSelectedListener = this
            viewModel.getOperatorList(context)

            mView!!.findViewById<TextView>(R.id.getBillTv).setOnClickListener{
                if(userIdEdt!!.getText().toString().trim().isEmpty()){
                    Toast.makeText(context, "Please enter vehicle number", Toast.LENGTH_SHORT).show()
                }else{
                    var billRequest= FetchBillRequest()
                    billRequest.Category = BillPayFragment.categoryFastTag
                    billRequest.canumber = userIdEdt!!.getText().toString().trim()
                    billRequest.operatorId = operatorData!!.operaterid
                    billRequest.AgentCode=MyPreferences.getLoginData(loginModel,context).Data.DoneCardUser
                    viewModel.fetchBillDetails(context, billRequest)
                }
            }

            mView!!.findViewById<ImageView>(R.id.back_arrow).setOnClickListener{
                parentFragmentManager.popBackStack()
            }
        }
        return mView!!
    }

    class FetchBillRequest {
        var Category: String? = null
        var AgentCode: String? = null
        var Merchant = ApiConstants.MerchantId
        var Type = "App"
        var mode="offline"
        var operatorId: String? = null
        var canumber: String? = null
        var ad1: String? = null
        var ad2: String? = null
        var ad3: String? = null
    /* {
    "operatorId": "20",
    "canumber": "60024474722",
    "Merchant": "JUSTCLICKTRAVELS",
    "AgentCode": "JC0A13387",
    "Type": "Web",
    "Category": "Electricity"
}*/
    }
    private fun bindOperatorList(operatorList: BillPayFragment.OperatorResponseModel?) {
        if(operatorList!=null && operatorList.operatorlistDetails!!.size>0){
            operatorListData!!.clear();
            operatorListData!!.addAll(operatorList.operatorlistDetails!!)
            spinner_operator!!.setAdapter(setSpinnerAdapter(operatorList.operatorlistDetails!!))
            spinner_operator!!.setSelection(0)
        }else{
            Toast.makeText(context, "No operator found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpinnerAdapter(data: ArrayList<BillPayFragment.OperatorResponseModel.operatorlistDetails>): ArrayAdapter<String?>? {
        val arr = arrayOfNulls<String>(data.size)
        for (i in data.indices) {
            arr[i] = data[i].name
        }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.mobile_operator_spinner_item, R.id.operator_tv, arr
        )
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun showBillDetail(
        responseModel: FetchBillResponseModel/*,
        rechargeType: Int,
        operator: operatorlistDetails*/
    ) {
        val bundle = Bundle()
        bundle.putInt("RechargeType", 1)
        bundle.putSerializable("BillDetailResponse", responseModel.payfetchbilllist[0])
        bundle.putSerializable("OperatorData", operatorData)
        val fragment = FastTagConfirmationFragment()
        fragment.arguments = bundle
        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        operatorData=operatorListData!!.get(position)
        userIdEdt!!.hint=operatorData!!.displayname
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
