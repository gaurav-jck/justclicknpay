package com.justclick.clicknbook.Fragment.train

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainResponseBinding
import com.justclick.clicknbook.utils.DateAndTimeUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class TrainBookingResponseFragment : Fragment() {

    var trainResponse: PnrResponse?=null
    var trainPreBookResponse: TrainPreBookResponse?=null
    private var bitmap: Bitmap? = null
    private var scrollView:ScrollView?=null
    var binding:FragmentTrainResponseBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_response, container, false)
        binding=FragmentTrainResponseBinding.bind(view)
        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("trainResponse") as PnrResponse
            trainPreBookResponse = requireArguments().getSerializable("TrainPreBookResponse") as TrainPreBookResponse
            var fromStationTv: TextView =view.findViewById(R.id.fromStationTv)
            var toStationTv: TextView =view.findViewById(R.id.toStationTv)
            var dateTv: TextView =view.findViewById(R.id.dateTv)
            scrollView =view.findViewById(R.id.scrollView)
            fromStationTv.text=trainResponse!!.transactionDetails.get(0).boarding
            toStationTv.text=trainResponse!!.transactionDetails.get(0).resvUpto
//            dateTv.text=trainResponse!!.transactionDetails.get(0).dateOfJourney
            dateTv.text=DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, trainResponse!!.transactionDetails.get(0).dateOfJourney)

            setData(view)
        }

        binding!!.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding!!.okTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
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
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    bitmap = loadBitmapFromView(scrollView, scrollView!!.getWidth(), scrollView!!.getChildAt(0).getMeasuredHeight());
                    createPdf();
                    //            createandDisplayPdf("JCK pdf")
                }
                else -> {

                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                }
            }

        }

        return view
    }

    private fun createPdfOld() {
//        val wm = getSystemService<Any>(Context.WINDOW_SERVICE) as WindowManager?
        //  Display display = wm.getDefaultDisplay();
        val displaymetrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()
        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
        val document = PdfDocument()
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)
        val canvas: Canvas = page.getCanvas()
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)
        paint.setColor(Color.BLUE)
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)

        val path = requireContext().getExternalFilesDir(null)!!.absolutePath + "/Dir"
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "jckFile.pdf")

        // write the document content
        val targetPdf = "/sdcard/pdffromlayout.pdf"
        val filePath: File
        filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        Toast.makeText(context, "PDF is created!!!", Toast.LENGTH_SHORT).show()
//        viewPdf("jckFile.pdf", "Dir")
//        openGeneratedPDF(file)
    }
    fun loadBitmapFromView(v: ScrollView?, width: Int, height: Int): Bitmap? {
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
        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)
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

    private fun showHideFare(view: View) {
        if(binding!!.fareView.visibility== AdapterView.VISIBLE){
            binding!!.fareView.visibility= AdapterView.GONE
        }else{
            binding!!.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {

        binding!!.statusTv.text=trainResponse!!.statusMessage
        binding!!.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        var trainDetail=trainPreBookResponse!!.bookingDetails.journeyDetails.get(0);
        binding!!.trainNameTv.text = journeyDetail.trainName
       /* binding!!.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
                journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
        binding!!.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
                journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)*/

        try{
            binding!!.startTimeTv.text =DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
            DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledDeparture)
            binding!!.endTimeTv.text =DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledArrival)
        }catch (e:Exception){}
        binding!!.fromStnTv.text = trainDetail.fromStation+"\n("+trainDetail.fromStationCode+")"
        binding!!.toStnTv.text = trainDetail.toStation+"\n("+trainDetail.toStationCode+")"
        binding!!.durationTv.text = journeyDetail.duration
        binding!!.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        binding!!.boardingStn.text="Boarding point - "+trainDetail.boardingStation+" ( "+trainDetail.boardingStationCode+" )"

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
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var seatNoTv:TextView=child.findViewById(R.id.seatNoTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            ageTv.text= list.age
            genderTv.text= list.sex
            count.text=(binding!!.passengerContainerLin!!.childCount+1).toString()

            seatNoTv.text="Seat no."/*+list.sNo*/
            statusTv.text=list.bookingStatus

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

    }
}