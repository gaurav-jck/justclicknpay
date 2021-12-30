package com.justclick.clicknbook.Fragment.train

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.TextView
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse
import com.justclick.clicknbook.R
import kotlinx.android.synthetic.main.fragment_train_response.view.*
import kotlinx.android.synthetic.main.fragment_train_response.view.statusTv
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.*
import android.graphics.Bitmap

import com.itextpdf.text.pdf.PdfWriter;
import android.widget.Toast

import android.content.Intent

import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.itextpdf.text.*

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Canvas

import android.util.DisplayMetrics

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.FileUriExposedException
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.justclick.clicknbook.BuildConfig
import kotlinx.android.synthetic.main.fragment_train_response.*
import android.os.Environment





class TrainBookingResponseFragment : Fragment() {

    var trainResponse: PnrResponse?=null
    var trainPreBookResponse: TrainPreBookResponse?=null
    private var bitmap: Bitmap? = null

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
            fromStationTv.text=trainResponse!!.transactionDetails.get(0).boarding
            toStationTv.text=trainResponse!!.transactionDetails.get(0).resvUpto
            dateTv.text=trainResponse!!.transactionDetails.get(0).dateOfJourney

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
                        bitmap = loadBitmapFromView(root, root.getWidth(), root.getHeight());
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
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    bitmap = loadBitmapFromView(root, root.getWidth(), root.getHeight());
                    createPdf();
                    //            createandDisplayPdf("JCK pdf")
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        }

        return view
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap? {

        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    private fun createPdf() {
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
        viewPdf("jckFile.pdf", "Dir")
//        openGeneratedPDF(file)
    }

    private fun openGeneratedPDF(file: File) {
//        val file = File("/sdcard/pdffromlayout.pdf")
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    fun createandDisplayPdf(text: String?) {           //working
        val doc = Document()
        try {
            val path = requireContext().getExternalFilesDir(null)!!.absolutePath + "/Dir"
            val dir = File(path)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "newFile.pdf")
            val fOut = FileOutputStream(file)
            PdfWriter.getInstance(doc, fOut)

            //open the document
            doc.open()
            val p1 = Paragraph(text)
            val paraFont = Font(Font.FontFamily.COURIER)
            p1.alignment = Paragraph.ALIGN_CENTER
            p1.font = paraFont

            //add paragraph to document
            doc.add(p1)
        } catch (de: DocumentException) {
            Log.e("PDFCreator", "DocumentException:$de")
        } catch (e: IOException) {
            Log.e("PDFCreator", "ioException:$e")
        } finally {
            doc.close()
        }
        viewPdf("newFile.pdf", "Dir")
    }

    // Method for opening a pdf file
    private fun viewPdf(file: String, directory: String) {
        val pdfFile = File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/" + directory + "/" + file)
        val path: Uri = Uri.fromFile(pdfFile)
//        val path = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", pdfFile);
        // Setting the intent for pdf reader
        val pdfIntent = Intent(Intent.ACTION_VIEW)
//        pdfIntent.setDataAndType(path, "application/pdf")
        pdfIntent.setDataAndType(path, "application/pdf")
        pdfIntent.flags=Intent.FLAG_GRANT_READ_URI_PERMISSION
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Can't read pdf file", Toast.LENGTH_SHORT).show()
        }
        catch (e: FileUriExposedException) {
            Toast.makeText(context, "Can't read pdf file", Toast.LENGTH_SHORT).show()
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
        view.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
                journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
        view.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
                journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)
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