package com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import jxl.write.biff.RowsExceededException
import kotlinx.android.synthetic.main.activity_txn_list.view.*
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AirBookingListFragment : Fragment(), View.OnClickListener {
    private var mView:View?=null
    private var recyclerView: RecyclerView? = null
    private var startDateToSend: String? = null
    private var endDateToSend: String? = null
    private var arrayList: ArrayList<AirBookingListResponse.travelsListDetail>? = null
    private var listAdapter: AirBookingListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var startDateTv: TextView? = null
    private var noRecordTv: TextView? = null
    private var filterDialog: Dialog? = null
    private var startDateCalendar: Calendar? = null
    private var endDateCalendar: Calendar? = null
    private var start_date_value_tv: TextView? = null
    private var end_date_value_tv: TextView? = null
    private var start_day_value_tv: TextView? = null
    private var end_day_value_tv: TextView? = null
    private var startDateDay = 0
    private var startDateMonth = 0
    private var startDateYear = 0
    private var endDateDay = 0
    private var endDateMonth = 0
    private var endDateYear = 0
    private var dateFormat: SimpleDateFormat? = null
    private var dayFormat: SimpleDateFormat? = null
    private var dateToServerFormat: SimpleDateFormat? = null
    private var bookingListRequest: AirBookingListRequest? = null
    private var loginModel: LoginModel? = null
    private var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    private var listFilterDialog:Dialog?=null
    private var orderNo=""
    private var refId=""
    private var paxName=""
    private var paxMobile=""
    private var pageStart = 1
    private var Length = 10
    private var totalCount = 0
    private val SHOW_PROGRESS = true
    private val NO_PROGRESS = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayList = ArrayList()
        bookingListRequest = AirBookingListRequest()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        initializeDates()
    }

    private fun initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat()
        dateFormat = Common.getToFromDateFormat()
        dayFormat = Common.getShortDayFormat()

        //default start Date
        startDateCalendar = Calendar.getInstance()
        startDateDay = startDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        startDateMonth = startDateCalendar!!.get(Calendar.MONTH)
        startDateYear = startDateCalendar!!.get(Calendar.YEAR)


        //default end Date
        endDateCalendar = Calendar.getInstance()
        endDateDay = endDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        endDateMonth = endDateCalendar!!.get(Calendar.MONTH)
        endDateYear = endDateCalendar!!.get(Calendar.YEAR)
        startDateToSend = dateToServerFormat!!.format(startDateCalendar!!.getTime())
        endDateToSend = dateToServerFormat!!.format(endDateCalendar!!.getTime())
    }

    private fun setDates() {
        //set default date
        startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                dayFormat!!.format(endDateCalendar!!.time) + " " +
                dateFormat!!.format(endDateCalendar!!.time)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_account_statement, container, false)
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            mView!!.noRecordTv.setVisibility(View.GONE)

            startDateTv = mView!!.findViewById(R.id.startDateTv)
            noRecordTv = mView!!.findViewById(R.id.noRecordTv)
            noRecordTv!!.setVisibility(View.GONE)

            mView!!.findViewById<TextView>(R.id.titleTv).setText("Air Booking List")
            mView!!.findViewById<View>(R.id.lin_dateFilter).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.linFilter).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.exportExcelTv).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.exportExcelTv).visibility=View.GONE


            //initialize date values
            setDates()

//        addValueToModel();
            listAdapter =
                AirBookingListAdapter(
                    context,
                    { view, list, data, position ->
                        when (view.id) {
                            R.id.print_tv -> try {
//                    Toast.makeText(context, "print", Toast.LENGTH_LONG).show()
//                    openReceipt(data)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Enable to print data", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    },
                    arrayList
                )
            layoutManager = LinearLayoutManager(context)
            mView!!.recyclerView!!.setLayoutManager(layoutManager)
            mView!!.recyclerView.setAdapter(listAdapter)
            mView!!.recyclerView.addOnScrollListener(recyclerViewOnScrollListener)
//            getTxnType()
            getBookingList(SHOW_PROGRESS)
        }

        return mView
    }

    //    https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b
    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()

//            if (!isLoading && !isLastPage) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount <= totalCount && dy > 0) {
                    if ((pageStart*Length) <= totalItemCount) {
                        pageStart += 1
                        getBookingList(NO_PROGRESS)
                    }
                }
                //            }
            }
        }

    private fun openFilterDialog() {
        filterDialog = Dialog(requireContext())
        filterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog!!.setContentView(R.layout.filter_dialog_layout)
        start_date_value_tv = filterDialog!!.findViewById(R.id.start_date_value_tv)
        start_day_value_tv = filterDialog!!.findViewById(R.id.start_day_value_tv)
        end_date_value_tv = filterDialog!!.findViewById(R.id.end_date_value_tv)
        end_day_value_tv = filterDialog!!.findViewById(R.id.end_day_value_tv)
        filterDialog!!.findViewById<View>(R.id.cancel_tv).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.submit_tv).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.startDateLinear).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.endDateLinear).setOnClickListener(this)
        start_date_value_tv!!.setText(dateFormat!!.format(startDateCalendar!!.time))
        start_day_value_tv!!.setText(dayFormat!!.format(startDateCalendar!!.time))
        end_date_value_tv!!.setText(dateFormat!!.format(endDateCalendar!!.time))
        end_day_value_tv!!.setText(dayFormat!!.format(endDateCalendar!!.time))
        filterDialog!!.show()
    }

    fun getBookingList(progress: Boolean) {
        bookingListRequest!!.FromDate=startDateToSend
        bookingListRequest!!.UptoDate=endDateToSend

        bookingListRequest!!.Distributor=""
        bookingListRequest!!.orderno=orderNo
        bookingListRequest!!.reforderid=refId
        bookingListRequest!!.PaxName=paxName
        bookingListRequest!!.PaxMobileNumber=paxMobile
        bookingListRequest!!.TravelType=""
        bookingListRequest!!.TripType=""
        bookingListRequest!!.BookingType=""
        bookingListRequest!!.Length=Length
//        bookingListRequest!!.RowCount=""
//        bookingListRequest!!.Result="0"
        bookingListRequest!!.Start=pageStart

        if(loginModel!!.Data.UserType.equals("O") || loginModel!!.Data.UserType.equals("OOU")){
            bookingListRequest!!.donecarduser=""
        }else if(loginModel!!.Data.UserType.equals("D")){
            bookingListRequest!!.Distributor=loginModel!!.Data.DoneCardUser
            bookingListRequest!!.donecarduser=null
        }else{
            bookingListRequest!!.donecarduser=loginModel!!.Data.DoneCardUser
        }

        val json = Gson().toJson(bookingListRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_STMT).create(ApiInterface::class.java)
        val call = apiService.airBookingList(ApiConstants.TravelBookingList, bookingListRequest)
        NetworkCall().callService(call,context,progress
        ) { response, responseCode ->
            if (response != null) {
                responseHandler(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), AirBookingListResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true) || commonResponse.travelsListDetail != null) {
                    if (commonResponse.travelsListDetail.size == 0) {
                        totalCount=0
                        noRecordTv!!.visibility = View.VISIBLE
                    }else{
                        noRecordTv!!.visibility = View.GONE
                        totalCount=commonResponse.totalCount
                        listAdapter!!.setCount(totalCount)
                        arrayList!!.addAll(commonResponse.travelsListDetail)
                        listAdapter!!.notifyDataSetChanged()
                    }
                }else {
                    noRecordTv!!.visibility = View.VISIBLE
                    arrayList!!.clear()
                    listAdapter!!.notifyDataSetChanged()
                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                noRecordTv!!.visibility = View.VISIBLE
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            noRecordTv!!.visibility = View.VISIBLE
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel_tv -> if (filterDialog != null) filterDialog!!.dismiss()
            R.id.submit_tv -> {
                val diff = endDateCalendar!!.time.time - startDateCalendar!!.time.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                if (days < 0) {
                    Toast.makeText(context, "you have selected wrong dates", Toast.LENGTH_SHORT).show()
                    return
                }
                if (days >= 15) {
                    Toast.makeText(context, "please select 15 days data", Toast.LENGTH_SHORT).show()
                    return
                }
                filterDialog!!.dismiss()
                arrayList!!.clear()
                listAdapter!!.notifyDataSetChanged()
                //set date to fragment
                startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                        dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                        dayFormat!!.format(endDateCalendar!!.time) + " " +
                        dateFormat!!.format(endDateCalendar!!.time)

//                bookingListRequest!!.FromDate=startDateToSend
//                bookingListRequest!!.UptoDate=endDateToSend
                pageStart=1
                getBookingList(SHOW_PROGRESS)
            }
            R.id.startDateLinear -> try {
                openStartDatePicker()
            } catch (e: Exception) {
            }
            R.id.endDateLinear -> try {
                openEndDatePicker()
            } catch (e: Exception) {
            }
            R.id.lin_dateFilter -> openFilterDialog()
            R.id.exportExcelTv -> {
                if(arrayList!!.size==0){
                    Toast.makeText(requireContext(), "No data for export to excel", Toast.LENGTH_SHORT).show()
                }else{
                    askSelfPermission()
                }
//                askSelfPermission()
            }
            R.id.linFilter -> {
                if(listFilterDialog==null){
                    openListFilterDialog()
                }else{
                    listFilterDialog!!.show()
                }
            }
            R.id.back_arrow -> parentFragmentManager.popBackStack()
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app
                createExcelSheet()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    lateinit var readImagePermission:String
    private fun askSelfPermission(){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                readImagePermission = Manifest.permission.READ_MEDIA_IMAGES
            else
                readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                readImagePermission
            ) -> {
                // You can use the API that requires the permission.
                createExcelSheet()
            }
            else -> {
                requestPermissionLauncher.launch(
                    readImagePermission
                )

            }
        }
    }

    private fun createExcelSheet() {
        val fileName = "jck_accountStmt" + System.currentTimeMillis() + ".xls"
        var myFile= File(requireContext().getExternalFilesDir(null)!!.path, fileName)
        val wbSettings = WorkbookSettings()
        wbSettings.setLocale(Locale("en", "EN"))
        val workbook: WritableWorkbook
        try {
            val a = 1
            workbook = Workbook.createWorkbook(myFile, wbSettings)
            //workbook.createSheet("Report", 0);
            val sheet: WritableSheet = workbook.createSheet("Account Statement", 0)

            var labelList:ArrayList<Label> = ArrayList()
            labelList.add(Label(0, 0, "SNo."))
            labelList.add(Label(1, 0, "Confirmation Id"))
            labelList.add(Label(2, 0, "Booking Ref No."))
            labelList.add(Label(3, 0, "Transaction Date"))
            labelList.add(Label(4, 0, "Debit"))
            labelList.add(Label(5, 0, "Credit"))
            labelList.add(Label(6, 0, "Balance"))
            labelList.add(Label(7, 0, "Transaction Type"))
            labelList.add(Label(8, 0, "Current Credit Amount"))
            labelList.add(Label(9, 0, "Remarks"))
            labelList.add(Label(10, 0, "Updated by"))

            for(i in 0 until arrayList!!.size){
                var r=i+1
                labelList.add(Label(0, r, r.toString()))
//                labelList.add(Label(1, r, arrayList!!.get(i).referenceid))
//                labelList.add(Label(2, r, ""))
//                labelList.add(Label(3, r, arrayList!!.get(i).txndate))
//                labelList.add(Label(4, r, arrayList!!.get(i).txnAMTD))
//                labelList.add(Label(5, r, arrayList!!.get(i).txnAMTC))
//                labelList.add(Label(6, r, arrayList!!.get(i).balance))
//                labelList.add(Label(7, r, arrayList!!.get(i).transactionType))
//                labelList.add(Label(8, r, arrayList!!.get(i).currentCreditAmount))
//                labelList.add(Label(9, r, arrayList!!.get(i).remarks))
//                labelList.add(Label(10, r, arrayList!!.get(i).updatedBy))
            }

            try {
                for(label in labelList){
                    sheet.addCell(label)
                }
            } catch (e: RowsExceededException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: WriteException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            workbook.write()
            try {
                workbook.close()
            } catch (e: WriteException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
//            Toast.makeText(requireContext(), "File is created!!!", Toast.LENGTH_SHORT).show()
            openGeneratedFile(myFile)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun openGeneratedFile(myFile: File) {
        if (myFile.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
//            val uri = Uri.fromFile(myFile)
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider", myFile
            )
            val myMime = MimeTypeMap.getSingleton()
            val mimeType =
                myMime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(photoURI.toString())) //It will return the mimetype

            intent.setDataAndType(photoURI, mimeType)
//            intent.setDataAndType(photoURI, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "No Application available to view excel file",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(requireContext(), "No file exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openListFilterDialog() {
//        listFilterDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        listFilterDialog = Dialog(requireContext())
        listFilterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        listFilterDialog!!.setContentView(R.layout.air_booking_filter)

        val orderNoEdt = listFilterDialog!!.findViewById<EditText>(R.id.orderNoEdt)
        val refIdEdt = listFilterDialog!!.findViewById<EditText>(R.id.refIdEdt)
        val nameEdt = listFilterDialog!!.findViewById<EditText>(R.id.nameEdt)
        val mobileEdt = listFilterDialog!!.findViewById<EditText>(R.id.mobileEdt)

        listFilterDialog!!.findViewById<View>(R.id.cancelTv).setOnClickListener { listFilterDialog!!.dismiss() }
        listFilterDialog!!.findViewById<View>(R.id.resetTv).setOnClickListener {
            orderNoEdt.setText("")
            refIdEdt.setText("")
            nameEdt.setText("")
            mobileEdt.setText("")
        }
        listFilterDialog!!.findViewById<View>(R.id.applyTv).setOnClickListener {
            listFilterDialog!!.dismiss()
            orderNo = orderNoEdt.text.toString().trim { it <= ' ' }
            refId = refIdEdt.text.toString().trim { it <= ' ' }
            paxName = nameEdt.text.toString().trim { it <= ' ' }
            paxMobile = mobileEdt.text.toString().trim { it <= ' ' }
            applyFilter()
        }

        val window = listFilterDialog!!.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        listFilterDialog!!.show()
    }

    private fun applyFilter() {
        arrayList!!.clear()
        listAdapter!!.notifyDataSetChanged()
        pageStart=1
        getBookingList(SHOW_PROGRESS)
    }

    private fun openStartDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(),
            R.style.DatePickerTheme,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                startDateCalendar!![year, monthOfYear] = dayOfMonth
                if (startDateCalendar!!.after(Calendar.getInstance())) {
                    Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show()
                } else {
                    startDateDay = dayOfMonth
                    startDateMonth = monthOfYear
                    startDateYear = year
                    start_date_value_tv!!.text = dateFormat!!.format(startDateCalendar!!.time)
                    start_day_value_tv!!.text = dayFormat!!.format(startDateCalendar!!.time)
                    startDateToSend = dateToServerFormat!!.format(startDateCalendar!!.time)
                }
            }, startDateYear, startDateMonth, startDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val cal = Calendar.getInstance()
        //        cal.add(Calendar.DAY_OF_MONTH, -15);
//        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun openEndDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(),
            R.style.DatePickerTheme,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                endDateCalendar = Calendar.getInstance()
                endDateCalendar!!.set(year, monthOfYear, dayOfMonth)
                if (endDateCalendar!!.after(Calendar.getInstance())) {
                    Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show()
                } else {
                    endDateDay = dayOfMonth
                    endDateMonth = monthOfYear
                    endDateYear = year
                    end_date_value_tv!!.text = dateFormat!!.format(endDateCalendar!!.getTime())
                    end_day_value_tv!!.text = dayFormat!!.format(endDateCalendar!!.getTime())
                    endDateToSend = dateToServerFormat!!.format(endDateCalendar!!.getTime())
                }
            }, endDateYear, endDateMonth, endDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, requireContext().resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    override fun onResume() {
        super.onResume()
    }

}