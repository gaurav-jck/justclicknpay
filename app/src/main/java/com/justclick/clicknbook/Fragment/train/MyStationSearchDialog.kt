package com.justclick.clicknbook.Fragment.train

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody

class MyStationSearchDialog {
    private var dialog: Dialog? = null
    private var context: Context? = null
    private var city_edt: EditText? = null
    private var searchTitleTv: TextView? = null
    private var back_arrow: ImageView? = null
    private var cross: ImageView? = null
    private var list_agent: ListView? = null
    private var agentName = ""
    private var autocompleteAdapter: AutocompleteAdapter? = null
    private var loginModel: LoginModel? = null
    private var stationResponseModel: TrainStationModel? = null
    private var stationArray: ArrayList<TrainStationModel.Items>? = null
    private var onCityDialogResult: OnCityDialogResult? = null
    private var key:Int?=null

    fun showCustomDialog(context: Context?, msg: String, keyValue: Int, trainSearchFragment: TrainSearchFragment?) {
        try {
            onCityDialogResult = trainSearchFragment as OnCityDialogResult?
            dialog = Dialog(context!!, R.style.Theme_Design_Light)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.activity_bus_from_city)
            val window = dialog!!.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            this.context=context
            searchTitleTv = dialog!!.findViewById<TextView>(R.id.searchTitleTv)
            city_edt = dialog!!.findViewById<View>(R.id.city_edt) as EditText
            back_arrow = dialog!!.findViewById<View>(R.id.back_arrow) as ImageView
            cross = dialog!!.findViewById<View>(R.id.cross) as ImageView
            list_agent = dialog!!.findViewById<View>(R.id.list_agent) as ListView
            city_edt!!.hint = msg
            key=keyValue
            searchTitleTv!!.text = "Search Station"
            loginModel = LoginModel()
            loginModel = MyPreferences.getLoginData(loginModel, context)
            back_arrow!!.setOnClickListener {
                Common.hideSoftInputFromDialog(dialog, context)
                dialog!!.dismiss()
            }
            cross!!.setOnClickListener { city_edt!!.setText("") }
            list_agent!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
                agentName = autocompleteAdapter!!.getItem(position)
                city_edt!!.visibility = View.VISIBLE
                city_edt!!.setText(agentName)
                city_edt!!.setSelection(city_edt!!.text.length)
                val message = city_edt!!.text.toString()
                onCityDialogResult!!.setResult(key!!, stationArray!!.get(position))
                Common.hideSoftInputFromDialog(dialog, context)
                dialog!!.dismiss()
            }
            city_edt!!.setOnClickListener { city_edt!!.setText("") }
            city_edt!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (Common.checkInternetConnection(context)) {
                        if (s.length >= 2) {
                            citySearch(s.toString())
                        }
                    } else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
            dialog!!.setCancelable(true)
            dialog!!.show()
        } catch (e: Exception) {
        }
    }

    fun hideCustomDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    val isDialogShowing: Boolean
        get() = dialog != null && dialog!!.isShowing

    fun citySearch(city: String?): TrainStationModel? {
        NetworkCall().callTrainStationServiceGet(ApiConstants.StationSearch, city, context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                val trainSearchDataModel = Gson().fromJson(response.string(), TrainStationModel::class.java)
                if (trainSearchDataModel!=null && trainSearchDataModel.items != null && trainSearchDataModel!!.items!!.size> 0) {
                    var array= arrayOfNulls<String>(trainSearchDataModel!!.items!!.size)
                    for (i in 0 until trainSearchDataModel!!.items!!.size) {
                        array[i] = trainSearchDataModel!!.items!!.get(i).station_name
                    }
//                    val array = Array<String>(6)
//                    for (i in array.indices) {
//                        array[i] = trainSearchDataModel!!.items!!.get(i).station_name.toString()
//                    }
                    stationArray=trainSearchDataModel!!.items
                    autocompleteAdapter = AutocompleteAdapter(context!!, array)
                    list_agent!!.setAdapter(autocompleteAdapter);
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
        return stationResponseModel
    }

    interface OnCityDialogResult {
        fun setResult(value: Int, bundle: TrainStationModel.Items)
    }

    class AutocompleteAdapter(private val context: Context, private val model: Array<String?>) : BaseAdapter() {
        private val agent_URL = ""
        override fun getCount(): Int {
            return 5
        }

        override fun getItem(position: Int): String {
            return model[position]!!
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.bus_autocomplete_layout, parent, false)
            val countryName = view.findViewById<TextView>(R.id.countryName)
            val cityName = view.findViewById<TextView>(R.id.cityName)
//            countryName.text = model[position]
//            cityName.text = model[position]!!.substring(0, model[position]!!.indexOf("("))
            //            if(model.Data.get(position).AirPortName!=null&& agent.Data.get(position).AirPortName.length()>0){
//                cityCode.setText("( "+model.Data.get(position).CityCode +", "+ agent.Data.get(position).AirPortName+" )");
//            }else {
//                cityCode.setText("( "+model.Data.get(position).CityCode+" )");
//            }
            return view
        }
    }
}