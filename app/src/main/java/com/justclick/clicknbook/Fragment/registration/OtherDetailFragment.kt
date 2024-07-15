package com.justclick.clicknbook.Fragment.registration

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.FileUtils
import com.justclick.clicknbook.utils.FileUtilsUpload
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OtherDetailFragment : Fragment(), View.OnClickListener {
    private val IMAGE_MAX_SIZE=1024 * 1024 * 3.5f
    private val ADDRESS = 1
    private val SHOP = 2
    private val SALES = 3
    private val AGENCY = 4
    private val PAN = 5

    private var uploadType = ADDRESS
    var imageUri: Uri? = null
    private val fileName: String? = null
    private var imageFile: File? = null
    private var addressImageFile:File? = null
    private var panImageFile:File? = null
    private var shopImageFile:File? = null
    private var salesImageFile:File? = null
    private var agencyImageFile:File? = null
    private var addressProofTv:TextView? = null
    private var panCardTv:TextView? = null
    private var shopTv:TextView? = null
    private var salesTv:TextView? = null
    private var agencyTv:TextView? = null

    private var registrationRequest: RegistrationRequest?=null
    private var mView: View? = null
    private var addressProofSpinner: Spinner? = null
    private var mContext: Context?=null
    private val addressProofArray = arrayOf("Adhar Card","Driving Licence", "Electricity Bill",
    "Rent Agreement", "Telephone Bill", "Trade Licence", "Voter Id")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registrationRequest =it.getParcelable(ARG_PARAM1, RegistrationRequest::class.java)
            }else{
                registrationRequest =it.getParcelable(ARG_PARAM1)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_other_detail, container, false)
        addressProofTv=mView!!.findViewById(R.id.addressProofTv)
        panCardTv=mView!!.findViewById(R.id.panCardTv)
        shopTv=mView!!.findViewById(R.id.shopTv)
        salesTv=mView!!.findViewById(R.id.salesTv)
        agencyTv=mView!!.findViewById(R.id.agencyTv)

        addressProofSpinner=mView!!.findViewById(R.id.addressProofSpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.registration_spinner_item,
            R.id.operator_tv,
            addressProofArray)
        adapter.setDropDownViewResource(R.layout.registration_spinner_item_dropdown)
        addressProofSpinner!!.adapter = adapter

        mView!!.findViewById<TextView>(R.id.continue_tv).setOnClickListener {
            continueClicked()
        }
        mView!!.findViewById<TextView>(R.id.backTv).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        mView!!.findViewById<LinearLayout>(R.id.chooseAddressProofLin).setOnClickListener(this)
        mView!!.findViewById<LinearLayout>(R.id.choosePancardLin).setOnClickListener(this)
        mView!!.findViewById<LinearLayout>(R.id.chooseShopLin).setOnClickListener(this)
        mView!!.findViewById<LinearLayout>(R.id.chooseSalesLin).setOnClickListener(this)
        mView!!.findViewById<LinearLayout>(R.id.chooseAgencyLin).setOnClickListener(this)
        mView!!.findViewById<TextView>(R.id.termsTv).setOnClickListener(this)

        return mView
    }

    private fun askSelfPermission() {
        val readImagePermission: String
        readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                requireContext(), readImagePermission
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.
            selectFile()
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                readImagePermission
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            selectFile()
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    private fun selectFile() {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "image/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        launchSomeActivity.launch(chooseFile)
    }

    var launchSomeActivity = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == Activity.RESULT_OK
        ) {
            val data = result.data

            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
                val selectedImageUri = data.data
                //                        imagePath=getPath(selectedImageUri);
                try {
                    getImage(data, selectedImageUri!!)
                    //                            setImageBitmap(selectedImageUri, data);
//                            image.setImageURI(imageUri);
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getImage(data: Intent, selectedImageUri: Uri) {
        val uri2: Uri = FileUtilsUpload.getFilePathFromUri(selectedImageUri, requireContext())
        //                        File file = new File(getPath(context,uri));
//        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageUri = uri2
            imageFile = File(FileUtilsUpload.getPath(uri2, context))
            FileUtilsUpload.getSize(data, imageFile, context)
        } else {
            imageUri = selectedImageUri
            val path: String = FileUtils.getPath(requireContext(), selectedImageUri)
            imageFile = File(path)
        }
//        val file_size: Int = (imageFile!!.length() / IMAGE_MAX_SIZE).toString().toInt()
        if (imageFile!!.length() > IMAGE_MAX_SIZE) {
            Toast.makeText(requireContext(), "Please select file size less than 3 MB", Toast.LENGTH_SHORT)
                .show()
        } else {
            setImageRequestData()
            //            setImageBitmap(selectedImageUri, data);
        }
        Log.d("fileUri: ", selectedImageUri.toString())
    }

    private fun setImageRequestData() {
        when (uploadType) {
            ADDRESS -> {
                addressProofTv!!.setText(imageFile!!.getName())
                addressImageFile = imageFile
            }

            PAN -> {
                panCardTv!!.setText(imageFile!!.getName())
                panImageFile = imageFile
            }

            SHOP -> {
                shopTv!!.setText(imageFile!!.getName())
                shopImageFile = imageFile
            }

            SALES -> {
                salesTv!!.setText(imageFile!!.getName())
                salesImageFile = imageFile
            }

            AGENCY -> {
                agencyTv!!.setText(imageFile!!.getName())
                agencyImageFile = imageFile
            }
        }
    }

    private fun continueClicked() {
        if(validateDetails()){
            registerAgent()
        }
    }

    private fun registerAgent() {
        registrationRequest!!.landline=""
        registrationRequest!!.visited="test"
        registrationRequest!!.salepersonjctid=""
        registrationRequest!!.bookuusertype="OOU"
        registrationRequest!!.bookuserid="00"
        registrationRequest!!.bookvalidationcode="00"
        registrationRequest!!.ipAddress="0000"
        registrationRequest!!.hostName="AppTest"
        NetworkCall().callService(
            NetworkCall.getPayoutNewApiInterface().agentRegistration(
                ApiConstants.newregistration,
                createRequestBody(registrationRequest!!.email),
                createRequestBody(registrationRequest!!.salutation),
                createRequestBody(registrationRequest!!.firstname),
                createRequestBody(registrationRequest!!.lastname),
                createRequestBody(registrationRequest!!.mobile),
                createRequestBody(registrationRequest!!.landline),
                createRequestBody(registrationRequest!!.usertype),
                createRequestBody(registrationRequest!!.companyname),
                createRequestBody(registrationRequest!!.state),
                createRequestBody(registrationRequest!!.city),
                createRequestBody(registrationRequest!!.country),
                createRequestBody(registrationRequest!!.pincode),
                createRequestBody(registrationRequest!!.address),
                createRequestBody(registrationRequest!!.userpin),
                createRequestBody(registrationRequest!!.gstnumber),
                createRequestBody(registrationRequest!!.addressproof),
                createRequestBody(registrationRequest!!.addproofnumber),
                createRequestBody(registrationRequest!!.pancardname),
                createRequestBody(registrationRequest!!.pannumber),
                createRequestBody(registrationRequest!!.visited),
                createRequestBody(registrationRequest!!.salepersonjctid),
                createRequestBody(registrationRequest!!.remark),
                createRequestBody(ApiConstants.MerchantId),
                createRequestBody(registrationRequest!!.bookuusertype),
                createRequestBody(registrationRequest!!.bookuserid),
                createRequestBody(registrationRequest!!.bookvalidationcode),
                createRequestBody(registrationRequest!!.ipAddress),
                createRequestBody(registrationRequest!!.hostName),
                createRequestBody("App"),
                sendFile("addproofdocument", addressImageFile),
                sendFile("panfile", panImageFile),
                sendFile("shopexterior", shopImageFile),
                sendFile("picwithsaleperson", salesImageFile),
                sendFile("agencyAddressProof", agencyImageFile),
                "", ""
            ),
            requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.response_failure_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(response.string(), PaytmWalletFragment.CommonResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    showResponsePopUp(senderResponse.statusMessage)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
//                    hideCustomDialog()
                }
            } else {
//                hideCustomDialog()
                Toast.makeText(context, "Error in registration", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
//            hideCustomDialog()
            Toast.makeText(context, "Exception occurs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResponsePopUp(message: String?) {
        val responseDialog = Dialog(requireContext())
        responseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        responseDialog.setContentView(R.layout.mobile_alert_dialog_layout)
        responseDialog.setCancelable(false)
        (responseDialog.findViewById<View>(R.id.detail_tv) as TextView).text = message
        (responseDialog.findViewById<View>(R.id.detail_tv) as TextView).movementMethod =
            ScrollingMovementMethod()
        responseDialog.findViewById<View>(R.id.submit_btn).setOnClickListener {
            responseDialog.dismiss()
            requireActivity().finish()
        }
        responseDialog.show()
    }

    private fun createRequestBody(userId: String): RequestBody {
        return userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    private fun sendFile(paramName: String, imageFile: File?): MultipartBody.Part? {
        if (imageFile == null) return null
        val requestFile: RequestBody = RequestBody.create(
            "image/jpg".toMediaType(),
            imageFile
        )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(paramName, imageFile.name, requestFile)
    }

    private fun validateDetails(): Boolean {
        var addressProofNo=mView!!.findViewById<TextView>(R.id.addressProofEdt).text.toString()
        var panName=mView!!.findViewById<TextView>(R.id.pan_card_name_edt).text.toString()
        var panNumber=mView!!.findViewById<TextView>(R.id.pan_card_edt).text.toString()
        var remarks=mView!!.findViewById<TextView>(R.id.remarkEdt).text.toString()
        var terms_checkbox=mView!!.findViewById<CheckBox>(R.id.terms_checkbox)
        if(addressProofNo.isEmpty()){
            Toast.makeText(requireContext(), "Please enter Address proof number", Toast.LENGTH_SHORT).show()
            return false
        }else if(addressProofNo.length<5){
            Toast.makeText(requireContext(), "Please enter valid address proof number", Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isNameValid(panName)){
            Toast.makeText(requireContext(), "Pan card name is empty or invalid", Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isPancardValid(panNumber)){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_pancard), Toast.LENGTH_SHORT).show()
            return false
        }else if(addressImageFile==null){
            Toast.makeText(requireContext(), "Please select address proof document", Toast.LENGTH_SHORT).show()
            return false
        }else if(panImageFile==null){
            Toast.makeText(requireContext(), "Please select pan-card image", Toast.LENGTH_SHORT).show()
            return false
        }else if(shopImageFile==null){
            Toast.makeText(requireContext(), "Please select shop exterior photo", Toast.LENGTH_SHORT).show()
            return false
        }else if(salesImageFile==null){
            Toast.makeText(requireContext(), "Please select image with sales person", Toast.LENGTH_SHORT).show()
            return false
        }else if(agencyImageFile==null){
            Toast.makeText(requireContext(), "Please select agency address proof", Toast.LENGTH_SHORT).show()
            return false
        }else if(agencyImageFile==null){
            Toast.makeText(requireContext(), "Please select agency address proof", Toast.LENGTH_SHORT).show()
            return false
        }else if(remarks.isEmpty()){
            Toast.makeText(requireContext(), "Please enter remarks", Toast.LENGTH_SHORT).show()
            return false
        }else if(!terms_checkbox.isChecked){
            Toast.makeText(requireContext(), "Please accept terms and conditions", Toast.LENGTH_SHORT).show()
            return false
        }else{
            registrationRequest!!.addressproof=addressProofSpinner!!.selectedItem.toString()
            registrationRequest!!.addproofnumber=addressProofNo
            registrationRequest!!.pancardname=panName
            registrationRequest!!.pannumber=panNumber
            registrationRequest!!.remark=remarks
            return true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: RegistrationRequest?) =
            OtherDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onClick(v: View?) {
        when{
            v!!.id==R.id.chooseAddressProofLin->{
                uploadType=ADDRESS
                askSelfPermission()
            }
            v!!.id==R.id.choosePancardLin->{
                uploadType=PAN
                askSelfPermission()
            }
            v!!.id==R.id.chooseShopLin->{
                uploadType=SHOP
                askSelfPermission()
            }
            v!!.id==R.id.chooseSalesLin->{
                uploadType=SALES
                askSelfPermission()
            }
            v!!.id==R.id.chooseAgencyLin->{
                uploadType=AGENCY
                askSelfPermission()
            }
            v!!.id==R.id.termsTv->{
                showTermsConditions()
            }

        }
    }

    private fun showTermsConditions() {
        val urlString = "https://justclicknpay.com/Registerterms.htm"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.setPackage("com.android.chrome")
        startActivity(intent)
    }
}