package com.justclick.clicknbook.Fragment.train

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainBookingDetailsBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.DateAndTimeUtils
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TrainBookingDetailsFragment : Fragment() {

    private val WPS_PDF="cn.wps.moffice_eng"
    private val ADOBE_PDF="com.adobe.reader"
    private val DRIVE_PDF="com.google.android.apps.docs"
    var trainResponse: PnrResponse?=null
    var loginModel: LoginModel?=null
    private var bitmap: Bitmap? = null
    private var scrollView:ScrollView?=null
    private var cancelTicket:TextView?=null
    var binding:FragmentTrainBookingDetailsBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_details, container, false)
        binding=FragmentTrainBookingDetailsBinding.bind(view)
        cancelTicket =view.findViewById(R.id.cancelTicket)
        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("trainResponse") as PnrResponse
            var fromStationTv: TextView =view.findViewById(R.id.fromStationTv)
            var toStationTv: TextView =view.findViewById(R.id.toStationTv)
            var dateTv: TextView =view.findViewById(R.id.dateTv)

            scrollView =view.findViewById(R.id.scrollView)
            fromStationTv.text=trainResponse!!.transactionDetails.get(0).boarding
            toStationTv.text=trainResponse!!.transactionDetails.get(0).resvUpto
//            dateTv.text=trainResponse!!.transactionDetails.get(0).dateOfJourney
            dateTv.text=DateAndTimeUtils.formatDateFromDateString(
                DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, trainResponse!!.transactionDetails.get(0).dateOfJourney)

            try {
                setData(view)
            }catch (e:NullPointerException){

            }
        }

        binding!!.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }
        binding!!.agentLabelRel.setOnClickListener {
            if(binding!!.agentView.visibility== AdapterView.VISIBLE){
                binding!!.agentView.visibility= AdapterView.GONE
            }else{
                binding!!.agentView.visibility= AdapterView.VISIBLE
            }
        }
        binding!!.instructionLabelRel.setOnClickListener {
            if(binding!!.instructionTv.visibility== AdapterView.VISIBLE){
                binding!!.instructionTv.visibility= AdapterView.GONE
            }else{
                binding!!.instructionTv.visibility= AdapterView.VISIBLE
            }
        }

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding!!.okTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("test006", "${it.key} = ${it.value}")
                }
            }

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    bitmap = loadBitmapFromView(scrollView, scrollView!!.getWidth(), scrollView!!.getChildAt(0).getMeasuredHeight());
                    createPdf();
                    //            createandDisplayPdf("JCK pdf")
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    try {
                        bitmap = loadBitmapFromView(scrollView, scrollView!!.getWidth(), scrollView!!.getChildAt(0).getMeasuredHeight());
                        createPdf();
                    }catch (e:Exception){
                        Toast.makeText(requireContext(), "Unable to create PDF !", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        binding!!.pdfTv.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    bitmap = loadBitmapFromView(scrollView, scrollView!!.getWidth(), scrollView!!.getChildAt(0).getMeasuredHeight());
                    createPdf();
                    //            createandDisplayPdf("JCK pdf")
                }
                else -> {

                    requestPermissionLauncher.launch(
                        READ_EXTERNAL_STORAGE)

                }
            }
        }

        binding!!.changeBoarding.setOnClickListener{

        }
        binding!!.cancelTicket.setOnClickListener{
            cancelTicket(trainResponse!!.reservationid)
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

        binding!!.statusTv.text="ResId : "+trainResponse!!.reservationid
        binding!!.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        binding!!.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainnumber+")"
        try{
//            binding!!.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
//                    journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
            binding!!.startTimeTv.text =DateAndTimeUtils.formatDateFromDateString(
                DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledDeparture)
//            binding!!.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
//                    journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)
            binding!!.endTimeTv.text =DateAndTimeUtils.formatDateFromDateString(
                DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledArrival)
        }catch (e:Exception){

        }
        binding!!.fromStnTv.text = journeyDetail.from
        binding!!.toStnTv.text = journeyDetail.to
        binding!!.durationTv.text = journeyDetail.duration
        binding!!.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        binding!!.boardingStn.text="Boarding point - "+journeyDetail.boarding

        var seats=""
        binding!!.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            binding!!.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            binding!!.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        binding!!.passengerContainerLin!!.removeAllViews()
        for(list in trainResponse!!.passengerDetails.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_seats_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            var currentStatusTv:TextView=child.findViewById(R.id.currentStatusTv)
            ageTv.text= list.age
            genderTv.text= list.sex
            count.text=(binding!!.passengerContainerLin!!.childCount+1).toString()

            statusTv.text=list.bookingStatus
            currentStatusTv.text=list.currentStatus

            binding!!.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainResponse!!.fareDetails.get(0)
        binding!!.baseFareTv.setText(fareDetails.ticketFar.toString())
        binding!!.cateringTv.setText(fareDetails.cateringCharge.toString())
        binding!!.conFeeTv.setText(fareDetails.convenienceFee.toString())
        binding!!.pgChargeTv.setText(fareDetails.pgCharges.toString())
        binding!!.insuranceTv.setText(fareDetails.travelInsurancePremium.toString())
        binding!!.serviceChargeTv.setText(fareDetails.travelAgentServiceCharge.toString())
        binding!!.totalFareTv.setText(fareDetails.totalFare.toString())
//        Agent details
        var agentDetails=trainResponse!!.agentDetails.get(0)
        binding!!.principleAgentTv.setText(agentDetails.principleAgent)
        binding!!.agentEmailTv.setText(agentDetails.emailID)
        binding!!.agentMobileTv.setText(agentDetails.contactNumber)
        binding!!.rspNameTv.setText(agentDetails.agentName)
        binding!!.rspIdTv.setText(agentDetails.corporateName)
        binding!!.rspAddressTv.setText(agentDetails.address)

    }

    private fun journeyDate(departDate: String?): String? {
        if(departDate!!.contains("/")){
            return Common.getServerDateFormat().format(SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(departDate))
        }else{
            return Common.getServerDateFormat().format(SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(departDate))
        }
    }

    private fun changeBoardingStn(list: TrainBookingListResponseModel.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getBoardingStnForChange(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/BoardingStation?Trainno="
                +list!!.trainNumber+"&Date="+journeyDate(list.departDate)+"&fromStation="+
                list.sourceCode+ "&toStation="+list.destinationCode+"&className="+list.journeyClass,
            loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        responseString=responseString.replace("boardingStationList\":{", "boardingStationList\":[{")
                        responseString=responseString.replace("},\"mealChoiceenable", "}],\"mealChoiceenable")
                        val boardingStnResponse = Gson().fromJson(responseString, TrainBookFragment.BoardingStnResponse::class.java)
                        if(boardingStnResponse.boardingStationList!=null && boardingStnResponse.boardingStationList!!.size>0){

                            var arr: Array<String?> =arrayOfNulls<String>(boardingStnResponse.boardingStationList!!.size)
                            for(pos in boardingStnResponse.boardingStationList!!.indices){
                                arr[pos]=boardingStnResponse.boardingStationList!!.get(pos).stnNameCode
                            }

//                            val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
//                            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)

                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
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

    private fun cancelTicket(reservationId:String) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(
            ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelDetail?TransactionId="+reservationId,
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
                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
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

    fun loadBitmapFromView(v: ScrollView?, width: Int, height: Int): Bitmap? {

        cancelTicket!!.visibility=View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            cancelTicket!!.visibility=View.VISIBLE
        }, 4000)

        val b = Bitmap.createBitmap(width+20, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v!!.draw(c)
        return b
    }

    private fun createPdf() {
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //  Display display = wm.getDefaultDisplay();
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()
        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()+20

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
        val document = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap!!.width, bitmap!!.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
//        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)      // uncomment if you want it according to screen
        paint.color = Color.WHITE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)

        // write the document content
//        var pdfFile= File(Environment.getExternalStorageDirectory().path, "jck_irctc_pdf.pdf") //running
//        var pdfFile= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path, "jck_irctc_pdf.pdf")
        var pdfFile= File(requireContext().getExternalFilesDir(null)!!.path, "jck_irctc_pdf.pdf")
        //        String targetPdf = "/sdcard/pdffromlayout.pdf";
//        File filePath;
//        filePath = new File(targetPdf);
        try {
            document.writeTo(FileOutputStream(pdfFile))
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Something wrong in creating PDF: $e", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        Toast.makeText(requireContext(), "PDF is created!!!", Toast.LENGTH_SHORT).show()
        openGeneratedPDF2(pdfFile)
    }

    private fun openGeneratedPDF2(pdfFile: File) {

//        File file = new File("/sdcard/pdffromlayout.pdf");
        if (pdfFile.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.fromFile(pdfFile)
            if(isAppExists(DRIVE_PDF)){
                intent.setPackage(DRIVE_PDF)   // particular app to open pdf file
            }else if(isAppExists(ADOBE_PDF)){
                intent.setPackage(ADOBE_PDF)
            }else if(isAppExists(WPS_PDF)){
                intent.setPackage(WPS_PDF)
            }
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider", pdfFile
            )
            intent.setDataAndType(photoURI, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "No Application available to view pdf",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(requireContext(), "No file exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAppExists(mPackageName: String): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val packageList = packageManager
            .getInstalledPackages(PackageManager.GET_PERMISSIONS)
        var isPackage = false
        for (pl in packageList) {
            if (pl.applicationInfo.packageName == mPackageName) {
                isPackage = true
            }
        }
        if (!isPackage) {
            return false
        }
        return true
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }
}