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
import android.os.Environment
import android.os.FileUriExposedException
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.utils.DateAndTimeUtils
import kotlinx.android.synthetic.main.fragment_train_response.view.back_arrow
import kotlinx.android.synthetic.main.fragment_train_response.view.baseFareTv
import kotlinx.android.synthetic.main.fragment_train_response.view.boardingStn
import kotlinx.android.synthetic.main.fragment_train_response.view.cateringTv
import kotlinx.android.synthetic.main.fragment_train_response.view.classTv
import kotlinx.android.synthetic.main.fragment_train_response.view.conFeeTv
import kotlinx.android.synthetic.main.fragment_train_response.view.durationTv
import kotlinx.android.synthetic.main.fragment_train_response.view.endTimeTv
import kotlinx.android.synthetic.main.fragment_train_response.view.fareLabelRel
import kotlinx.android.synthetic.main.fragment_train_response.view.fareView
import kotlinx.android.synthetic.main.fragment_train_response.view.fromStnTv
import kotlinx.android.synthetic.main.fragment_train_response.view.insuranceTv
import kotlinx.android.synthetic.main.fragment_train_response.view.okTv
import kotlinx.android.synthetic.main.fragment_train_response.view.passengerContainerLin
import kotlinx.android.synthetic.main.fragment_train_response.view.pdfTv
import kotlinx.android.synthetic.main.fragment_train_response.view.pgChargeTv
import kotlinx.android.synthetic.main.fragment_train_response.view.pnrTv
import kotlinx.android.synthetic.main.fragment_train_response.view.seatsTv
import kotlinx.android.synthetic.main.fragment_train_response.view.serviceChargeTv
import kotlinx.android.synthetic.main.fragment_train_response.view.startTimeTv
import kotlinx.android.synthetic.main.fragment_train_response.view.statusTv
import kotlinx.android.synthetic.main.fragment_train_response.view.toStnTv
import kotlinx.android.synthetic.main.fragment_train_response.view.totalFareTv
import kotlinx.android.synthetic.main.fragment_train_response.view.trainNameTv
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.genderTv
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class TrainBookingResponseFragment : Fragment() {

    var trainResponse: PnrResponse?=null
    var trainPreBookResponse: TrainPreBookResponse?=null
    private var bitmap: Bitmap? = null
    private var scrollView:ScrollView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_response, container, false)

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

        view.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.okTv.setOnClickListener {
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
                    }
                }


        view.pdfTv.setOnClickListener {

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
        if(view.fareView.visibility== AdapterView.VISIBLE){
            view.fareView.visibility= AdapterView.GONE
        }else{
            view.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {

        view.statusTv.text=trainResponse!!.statusMessage
        view.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        var trainDetail=trainPreBookResponse!!.bookingDetails.journeyDetails.get(0);
        view.trainNameTv.text = journeyDetail.trainName
       /* view.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
                journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
        view.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
                journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)*/

        try{
            view.startTimeTv.text =DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
            DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledDeparture)
            view.endTimeTv.text =DateAndTimeUtils.formatDateFromDateString(DateAndTimeUtils.DateTrainInput,
                DateAndTimeUtils.DateTrainOutput, journeyDetail.scheduledArrival)
        }catch (e:Exception){}
        view.fromStnTv.text = trainDetail.fromStation+"\n("+trainDetail.fromStationCode+")"
        view.toStnTv.text = trainDetail.toStation+"\n("+trainDetail.toStationCode+")"
        view.durationTv.text = journeyDetail.duration
        view.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+trainDetail.boardingStation+" ( "+trainDetail.boardingStationCode+" )"

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
            var seatNoTv:TextView=child.findViewById(R.id.seatNoTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            ageTv.text= list.age
            child.genderTv.text= list.sex
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            seatNoTv.text="Seat no."/*+list.sNo*/
            statusTv.text=list.bookingStatus

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

    }
}