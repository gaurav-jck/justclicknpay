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
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.DateAndTimeUtils
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_booking_details.view.*
import kotlinx.android.synthetic.main.fragment_train_response.view.pdfTv
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.statusTv
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

    var trainResponse: PnrResponse?=null
    var loginModel: LoginModel?=null
    private var bitmap: Bitmap? = null
    private var scrollView:ScrollView?=null
    private var cancelTicket:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_details, container, false)
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

        view.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }
        view.agentLabelRel.setOnClickListener {
            if(view.agentView.visibility== AdapterView.VISIBLE){
                view.agentView.visibility= AdapterView.GONE
            }else{
                view.agentView.visibility= AdapterView.VISIBLE
            }
        }
        view.instructionLabelRel.setOnClickListener {
            if(view.instructionTv.visibility== AdapterView.VISIBLE){
                view.instructionTv.visibility= AdapterView.GONE
            }else{
                view.instructionTv.visibility= AdapterView.VISIBLE
            }
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.okTv.setOnClickListener {
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


        view.pdfTv.setOnClickListener {

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

        view.changeBoarding.setOnClickListener{

        }
        view.cancelTicket.setOnClickListener{
            cancelTicket(trainResponse!!.reservationid)
        }

        return view
    }

    private fun showHideFare(view: View) {
        if(view.fareView.visibility== AdapterView.VISIBLE){
            view.fareView.visibility= AdapterView.GONE
        }else{
            view.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {

        view.statusTv.text="ResId : "+trainResponse!!.reservationid
        view.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        view.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainnumber+")"
        try{
//            view.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
//                    journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
            view.startTimeTv.text =DateAndTimeUtils.formatDateFromDateString(
                DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledDeparture)
//            view.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
//                    journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)
            view.endTimeTv.text =DateAndTimeUtils.formatDateFromDateString(
                DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledArrival)
        }catch (e:Exception){

        }
        view.fromStnTv.text = journeyDetail.from
        view.toStnTv.text = journeyDetail.to
        view.durationTv.text = journeyDetail.duration
        view.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+journeyDetail.boarding

        var seats=""
        view.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        view.passengerContainerLin!!.removeAllViews()
        for(list in trainResponse!!.passengerDetails.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_seats_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            var currentStatusTv:TextView=child.findViewById(R.id.currentStatusTv)
            ageTv.text= list.age
            child.genderTv.text= list.sex
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            statusTv.text=list.bookingStatus
            currentStatusTv.text=list.currentStatus

            view.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainResponse!!.fareDetails.get(0)
        view.baseFareTv.setText(fareDetails.ticketFar.toString())
        view.cateringTv.setText(fareDetails.cateringCharge.toString())
        view.conFeeTv.setText(fareDetails.convenienceFee.toString())
        view.pgChargeTv.setText(fareDetails.pgCharges.toString())
        view.insuranceTv.setText(fareDetails.travelInsurancePremium.toString())
        view.serviceChargeTv.setText(fareDetails.travelAgentServiceCharge.toString())
        view.totalFareTv.setText(fareDetails.totalFare.toString())
//        Agent details
        var agentDetails=trainResponse!!.agentDetails.get(0)
        view.principleAgentTv.setText(agentDetails.principleAgent)
        view.agentEmailTv.setText(agentDetails.emailID)
        view.agentMobileTv.setText(agentDetails.contactNumber)
        view.rspNameTv.setText(agentDetails.agentName)
        view.rspIdTv.setText(agentDetails.corporateName)
        view.rspAddressTv.setText(agentDetails.address)

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

                            val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
                            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)

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

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }
}