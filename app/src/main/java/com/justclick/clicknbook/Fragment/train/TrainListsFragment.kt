package com.justclick.clicknbook.Fragment.train

import android.app.Dialog
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.Fragment.train.adapter.TrainListAdapter
import com.justclick.clicknbook.Fragment.train.adapter.TrainRouteAdapter
import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel
import com.justclick.clicknbook.Fragment.train.viewmodel.TrainSearchViewModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainListBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.MyPreferences
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class TrainListsFragment : Fragment(), View.OnClickListener, ModifySearchDialog.OnCityDialogResult {

    // TODO: Customize parameters
    private var columnCount = 1
    public val General="Genaral Quota"
    val Ladies="Ladies Quota"
    public val SrCitizen="Sr. Citizen"
    public val Tatkal="Tatkal Quota"
//    public val Handicapped="Handicapped"
    private var listener: OnListFragmentInteractionListener? = null
    var viewModel:TrainSearchViewModel?=null
    var adapter: TrainListAdapter?=null
    var doj:String?=null
    var dateShow:String?=null
    var fromStnName:String?=null
    var fromStnCode:String?=null
    var toStnName:String?=null
    var toStnCode:String?=null
    var trainNumber:String?=null
    var trainName:String?=null
    var recycleView:RecyclerView?=null
    var quotaSpinner: AppCompatSpinner?=null
    var trainResponse:TrainSearchDataModel?=null
    var fromStationTv:TextView?=null
    var toStationTv:TextView?=null
    var dateTv:TextView?=null
    var totalTv:TextView?=null
    var arrayList:ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?=null
    var arrayListTemp:ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?= ArrayList()
    var className=""
    var fromName=""
    var toName=""
    var trainType=""
    var mView:View?=null
    var binding:FragmentTrainListBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(this).get(TrainSearchViewModel::class.java)
        viewModel!!.getTrainRouteResponseLiveData()!!.observe(this, Observer { trainResponse ->
            // update UI
            trainRouteResponseHandler(trainResponse)
        })

        viewModel!!.getTrainSearchResponseLiveData()!!.observe(this, Observer { trainResponse ->
            // update UI
            trainSearchResponseHandler(trainResponse)
        })
    }

    private fun trainRouteResponseHandler(trainResponse: TrainRouteModel?) {
        if(trainResponse!=null){
            if(trainResponse.stationList!=null && trainResponse.stationList!!.size>0){
                openTrainRouteDialog(trainResponse)
            }
        }else{

        }
    }

    private fun trainSearchResponseHandler(trainResponse: TrainSearchDataModel?) {
        if (trainResponse?.trainBtwnStnsList != null) {
            var layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this.trainResponse=trainResponse

            trainResponse!!.fromStnCode=fromStnCode
            trainResponse!!.fromStnName=fromStnName
            trainResponse!!.toStnCode=toStnCode
            trainResponse!!.toStnName=toStnName
            trainResponse!!.date=dateTv!!.text.toString()
            trainResponse!!.doj=doj
            trainResponse!!.quota=quotaSpinner!!.selectedItem.toString()

            arrayList = trainResponse.trainBtwnStnsList
            arrayListTemp!!.clear()
            arrayListTemp!!.addAll(arrayList!!)
            totalTv!!.setText("Total Train - "+arrayListTemp!!.size)
            adapter = TrainListAdapter(doj,arrayListTemp, quotaSpinner!!, object : OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(list: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?, id: Int, listPosition: Int, type: CodeEnum, fareRuleResponse: FareRuleResponse?) {
                    if(type.equals(CodeEnum.TrainDetail)){
                        var trainBookFragment=TrainBookFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("trainResponse", trainResponse)
                        bundle.putSerializable("fareRuleResponse", fareRuleResponse)
                        bundle.putString("DOJ", doj)
                        bundle.putInt("Position", listPosition)
                        trainBookFragment.arguments=bundle
                        (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(trainBookFragment)
                    }else{
                        trainName= list!![listPosition].trainName
                        trainNumber= list!![listPosition].trainNumber
                        var loginModel= LoginModel()
                        loginModel= MyPreferences.getLoginData(loginModel,requireContext())
                        viewModel!!.findTrainRoute(doj, list!![listPosition].trainNumber, list!![listPosition].fromStnCode,context!!,
                            loginModel.Data.DoneCardUser, loginModel.Data.UserType)
                    }
                }
            })

            recycleView!!.layoutManager=layoutManager
            recycleView!!.adapter=adapter
        }else{
            Toast.makeText(context, "No train found for given parameters", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_train_list, container, false)
            binding=FragmentTrainListBinding.bind(mView!!)
            recycleView = mView!!.findViewById(R.id.recycleView)
            quotaSpinner = mView!!.findViewById(R.id.quotaSpinner)
            fromStationTv = mView!!.findViewById(R.id.fromStationTv)
            toStationTv = mView!!.findViewById(R.id.toStationTv)
            dateTv = mView!!.findViewById(R.id.dateTv)
            totalTv = mView!!.findViewById(R.id.totalTv)

            var layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            if (arguments != null) {
                trainResponse =
                    requireArguments().getSerializable("trainResponse") as TrainSearchDataModel
                arrayList = trainResponse!!.trainBtwnStnsList
                arrayListTemp!!.clear()
                arrayListTemp!!.addAll(arrayList!!)
                totalTv!!.setText("Total Train - " + arrayListTemp!!.size)
                doj = requireArguments().getString("DOJ")

                //top title
                binding!!.fromStationTv.text = trainResponse!!.fromStnName
                binding!!.toStationTv.text = trainResponse!!.toStnName
                binding!!.dateTv.text = trainResponse!!.date

                fromStnCode = trainResponse!!.fromStnCode
                fromStnName = trainResponse!!.fromStnName
                toStnCode = trainResponse!!.toStnCode
                toStnName = trainResponse!!.toStnName

                var list = ArrayList<String>()
                if (trainResponse!!.quotaList != null && trainResponse!!.quotaList!!.size > 0) {
                    for (i in trainResponse!!.quotaList!!.indices) {
                        if (getQuota(trainResponse!!.quotaList!!.get(i)).isNotEmpty()) {
                            list.add(getQuota(trainResponse!!.quotaList!!.get(i)))
                        }
                    }
                } else {
                    list.add(General)
                }
                if (list.indexOf(General) != 0) {
                    var temp = list.get(0)
                    list[list.indexOf(General)] = temp
                    list[0] = General

                }
                var adapter =
                    ArrayAdapter(requireContext(), R.layout.spinner_item, R.id.name_tv, list)
//                var adapter= ArrayAdapter(requireContext(), R.layout.spinner_item, R.id.name_tv, trainResponse!!.quotaList!!)
                quotaSpinner!!.adapter = adapter

                binding!!.filterImg.setOnClickListener(this)
                binding!!.modifyImg.setOnClickListener(this)

            }

            adapter = TrainListAdapter(
                doj,
                arrayListTemp,
                binding!!.quotaSpinner,
                object : OnListFragmentInteractionListener {
                    override fun onListFragmentInteraction(
                        list: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?,
                        id: Int,
                        listPosition: Int,
                        type: CodeEnum,
                        fareRuleResponse: FareRuleResponse?
                    ) {
                        if (type.equals(CodeEnum.TrainDetail)) {
                            var trainBookFragment = TrainBookFragment()
                            val bundle = Bundle()
                            trainResponse!!.trainBtwnStnsList=list
                            bundle.putSerializable("trainResponse", trainResponse)
                            bundle.putSerializable("fareRuleResponse", fareRuleResponse)
                            bundle.putString("DOJ", doj)
                            bundle.putInt("Position", listPosition)
                            trainBookFragment.arguments = bundle
                            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(
                                trainBookFragment
                            )
                        } else {
                            trainName = list!![listPosition].trainName
                            trainNumber = list!![listPosition].trainNumber
                            var loginModel = LoginModel()
                            loginModel = MyPreferences.getLoginData(loginModel, requireContext())
                            viewModel!!.findTrainRoute(
                                doj,
                                list!![listPosition].trainNumber,
                                list!![listPosition].fromStnCode,
                                context!!,
                                loginModel.Data.DoneCardUser,
                                loginModel.Data.UserType
                            )
                        }
                    }
                })

            recycleView!!.layoutManager = layoutManager
            recycleView!!.adapter = adapter

            binding!!.backArrow.setOnClickListener(this)
            getData()   //read xml data for sttaion
        }

        /*viewModel!!.getTrainSearchResponseLiveData()!!.observe(viewLifecycleOwner, Observer { trainResponse ->
            // update UI
            trainSearchResponseHandler(trainResponse)
        })*/

        return mView
    }

    private fun getQuota(value: String): String {
        if(value.equals("GN")){
            return General
        }else if(value.equals("LD")){
            return Ladies
        }else if(value.equals("TQ")){
            return Tatkal
        }/*else if(value.equals("HP")){
            return Handicapped
        }*/else if(value.equals("SS")){
            return SrCitizen
        }else{
            return ""
        }
    }

    private fun openTrainRouteDialog(trainResponse: TrainRouteModel) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.train_route_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        var titleTv=dialog.findViewById<TextView>(R.id.titleTv)
        var back_arrow=dialog.findViewById<ImageView>(R.id.back_arrow)
        var routeRecycler=dialog.findViewById<RecyclerView>(R.id.routeRecycler)
        titleTv.text= "Train Route-"+trainNumber+"\n"+trainName
        routeRecycler.layoutManager=LinearLayoutManager(context)
        routeRecycler.adapter= TrainRouteAdapter(trainResponse.stationList,object : OnRouteListListener{
            override fun onListFragmentInteraction(item: ArrayList<TrainRouteModel.StationList>?, id: Int, listPosition: Int, type: CodeEnum) {
            }
        })

        back_arrow.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?, id: Int, listPosition: Int, type: CodeEnum, fareRuleResponse: FareRuleResponse?)
    }
    interface OnRouteListListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainRouteModel.StationList>?, id: Int, listPosition: Int, type: CodeEnum)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
            R.id.filterImg->
                if(filterDialog==null){
                    filterClick()
                }else{
                    filterDialog!!.show()
                }
            R.id.modifyImg->
                modifyClick()
        }
    }

    var filterDialog:Dialog?=null
    private fun filterClick() {
        className = ""
        filterDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        filterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog!!.setContentView(R.layout.train_list_filter)

        var classLinear:LinearLayout=filterDialog!!.findViewById(R.id.classLinear)
        var fromLinear:LinearLayout=filterDialog!!.findViewById(R.id.fromLinear)
        var toLinear:LinearLayout=filterDialog!!.findViewById(R.id.toLinear)
        var typeLinear:LinearLayout=filterDialog!!.findViewById(R.id.typeLinear)

        var classFilterItem=""
        var fromFilterItem=""
        var toFilterItem=""
        var typeFilterItem=""
        for(i in 0 until arrayList!!.size){
            for(c in 0 until arrayList!!.get(i).avlClasses!!.size){
                if(!classFilterItem.contains(arrayList!!.get(i).avlClasses!![c])){
                    classFilterItem+=arrayList!!.get(i).avlClasses!![c]
                    var checkBox= CheckBox(requireContext())
                    checkBox.text = arrayList!!.get(i).avlClasses!![c]
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            className += checkBox.text.toString()
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                        } else {
                            className=className.replace(checkBox.text.toString(), "")
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                        }
                    }
                    classLinear.addView(checkBox)
                }
            }
            if(!fromFilterItem.contains(arrayList!![i].fromStnCode.toString())){
                fromFilterItem+=arrayList!!.get(i).fromStnCode
                var checkBox= CheckBox(requireContext())
                checkBox.text = arrayList!!.get(i).fromStnCode
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        fromName += checkBox.text.toString()
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    } else {
                        fromName=fromName.replace(checkBox.text.toString(), "")
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    }
                }
                fromLinear.addView(checkBox)
            }
            if(!toFilterItem.contains(arrayList!![i].toStnCode.toString())){
                toFilterItem+=arrayList!!.get(i).toStnCode
                var checkBox= CheckBox(requireContext())
                checkBox.text = arrayList!!.get(i).toStnCode
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        toName += checkBox.text.toString()
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    } else {
                        toName=toName.replace(checkBox.text.toString(), "")
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    }
                }
                toLinear.addView(checkBox)
            }

            if(!typeFilterItem.contains(arrayList!![i].trainTypeName.toString())){
                typeFilterItem+=arrayList!!.get(i).trainTypeName
                var checkBox= CheckBox(requireContext())
                checkBox.text = arrayList!!.get(i).trainTypeName
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        trainType += checkBox.text.toString()
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    } else {
                        trainType=trainType.replace(checkBox.text.toString(), "")
//                            Toast.makeText(requireContext(), checkBox.text.toString()+"-"+className, Toast.LENGTH_SHORT).show()
                    }
                }
                typeLinear.addView(checkBox)
            }
        }
//        className += arrayList!!.get(i).avlClasses!![c]

        filterDialog!!.findViewById<TextView>(R.id.cancelTv).setOnClickListener {
            filterDialog!!.cancel()
        }
        filterDialog!!.findViewById<TextView>(R.id.applyTv).setOnClickListener {
            applyFilter()
            filterDialog!!.cancel()
        }

        filterDialog!!.show()
    }

    private fun applyFilter() {
        arrayListTemp!!.clear()
        if(className.isEmpty() && fromName.isEmpty() && toName.isEmpty() && trainType.isEmpty()){
            arrayListTemp!!.addAll(arrayList!!)
        }else {
            for (i in 0 until arrayList!!.size) {
                var temp = false
                for (c in 0 until arrayList!!.get(i).avlClasses!!.size) {
                    if (className.contains(arrayList!!.get(i).avlClasses!![c])) {
                        temp = true
                        break
                    }
                }
                if ((temp || className.isEmpty()) &&
                    (fromName.isEmpty() || fromName.contains(arrayList!![i].fromStnCode.toString())) &&
                    (toName.isEmpty() || toName.contains(arrayList!![i].toStnCode.toString())) &&
                    (trainType.isEmpty() || trainType.contains(arrayList!![i].trainTypeName.toString()))){
                    arrayListTemp!!.add(arrayList!!.get(i))
                }

            }
        }
        totalTv!!.setText("Total Train - "+arrayListTemp!!.size)
        adapter!!.notifyDataSetChanged()
    }

    private fun modifyClick() {
//        getData()
        trainResponse!!.fromStnCode=fromStnCode
        trainResponse!!.fromStnName=fromStnName
        trainResponse!!.toStnCode=toStnCode
        trainResponse!!.toStnName=toStnName
        trainResponse!!.date=dateTv!!.text.toString()
        trainResponse!!.doj=doj
        trainResponse!!.quota=quotaSpinner!!.selectedItem.toString()

        ModifySearchDialog.showCustomDialog(requireContext(),stnListHash,stationArray,doj,
            trainResponse!!.fromStnName+" [ "+trainResponse!!.fromStnCode +" ]",
            trainResponse!!.toStnName+" [ "+trainResponse!!.toStnCode+" ]", this)
    }

    override fun setResult(fromStn: TrainStationModel.Items, toStn: TrainStationModel.Items, date: String?, dateShow: String?) {
        var loginModel=LoginModel()
        loginModel=MyPreferences.getLoginData(loginModel,requireContext())

        fromStationTv!!.text=fromStn!!.station_name
        toStationTv!!.text=toStn!!.station_name
        dateTv!!.text=dateShow
        doj=date
        this.dateShow=dateShow

        fromStnCode=fromStn.station_code
        fromStnName=fromStn.station_name
        toStnCode=toStn.station_code
        toStnName=toStn.station_name

        viewModel!!.searchTrains(fromStn.station_code, toStn.station_code, date, requireContext(),
            loginModel.Data.DoneCardUser, loginModel.Data.UserType)


    }

    var stnList:ArrayList<TrainStationModel.Items> = ArrayList()
    var stnListHash:HashMap<String, TrainStationModel.Items> = HashMap()
    var stationArray:Array<String?> ?= null
    fun getData() {
        val istream: InputStream = requireContext().assets.open("trainstations.xml")

        // Steps to convert this input stream into a list
        val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder: DocumentBuilder = builderFactory.newDocumentBuilder()
        val doc: Document = docBuilder.parse(istream)
        val nList: NodeList = doc.getElementsByTagName("stationname")

        stationArray= arrayOfNulls(nList.length)
        // Iterating through this list
            for (i in 0 until nList.length) {
                if (nList.item(0).nodeType === Node.ELEMENT_NODE) {
                    var stn:TrainStationModel.Items= TrainStationModel.Items()
//                    val user: HashMap<String, String?> = HashMap()
                    val elm: Element = nList.item(i) as Element
                    stn.station_code = getNodeValue("stncode", elm)
                    stn.station_name = getNodeValue("stnname", elm)
                    val station=stn.station_name+" [ "+stn.station_code+" ]"
                    stnList.add(stn)
                    stnListHash.put(station, stn)
                    stationArray!![i]=station
                }
            }

    }

    // A function to get the node value while parsing
    fun getNodeValue(tag: String?, element: Element): String? {
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if (node != null) {
            if (node.hasChildNodes()) {
                val child = node.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE) {
                        return child.nodeValue
                    }
                }
            }
        }
        // Returns nothing if nothing was found
        return ""
    }

}
