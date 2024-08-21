package com.justclick.clicknbook.Fragment.accountsAndReports

import android.Manifest.permission
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.DepositRequestResponseModel
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.requestmodels.AgentDepositRequestRequestModel
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.ImageCompressor
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

//https://developer.android.com/training/data-storage/shared/photopicker
class AgentDepositRequestFragmentNewww : Fragment(), View.OnClickListener{
    private val REQUEST_AMOUNT = 1
    private val MY_PERMISSIONS_REQUEST_STORAGE = 2
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 1
    private val IMAGE_MAX_SIZE_8MB = 1024 * 1024 * 8.0f
    private val IMAGE_MAX_SIZE_1MB = 1024 * 1024 * 1.0f
    private var context: Context? = null
    private var titleChangeListener: ToolBarTitleChangeListener? = null
    private var agency_name_tv: TextView? = null
    private var date_tv: TextView? = null
    private var agent_name_edt: EditText? = null
    private var amount_edt: EditText? = null
    private var mobile_edt: EditText? = null
    private var receipt_no_edt: EditText? = null
    private var remark_edt: EditText? = null
    private var spinner_submit_type: Spinner? = null
    private var spinner_bank_name: Spinner? = null
    private var date_lin: RelativeLayout? = null
    private var submit: Button? = null
    private var loginModel: LoginModel? = null
    private var transactionDate: String? = null
    private var type: String? = null
    private val response = ""
    private var dateToServerFormat: SimpleDateFormat? = null
    private var startDateDay = 0
    private var startDateMonth = 0
    private var startDateYear = 0
    private var startDateCalendar: Calendar? = null
    private var view_agent_name_edt: TextInputLayout? = null
    private var imagepath: String? = null
    private val encodedImage: String? = null
    private var fileImageView: ImageView? = null
    private var receiptFile: File? = null
    private var imageUri: Uri? = null
    private var networkOutputStream: ByteArrayOutputStream? = null
    private var viewFinder: PreviewView?=null
    private val isStorage = false
    private val isCamera = false
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private var depositView:CardView?=null
    private var cameraView:ConstraintLayout?=null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null
    lateinit var observer : MyLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity
        loginModel = MyPreferences.getLoginData(LoginModel(), context)
        cameraExecutor = Executors.newSingleThreadExecutor()
        observer = MyLifecycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(observer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
            titleChangeListener = context as ToolBarTitleChangeListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agent_deposit_request, container, false)
        titleChangeListener!!.onToolBarTitleChange(getString(R.string.agentDepositRequestFragmentTitle))
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        initializeViews(view)
        initializeDates()
        return view
    }

    private fun initializeViews(view: View) {
        agency_name_tv = view.findViewById(R.id.agency_name_tv)
        date_tv = view.findViewById(R.id.address_tv)
        agent_name_edt = view.findViewById(R.id.agent_name_edt)
        amount_edt = view.findViewById(R.id.amount_edt)
        mobile_edt = view.findViewById(R.id.mobile_edt)
        remark_edt = view.findViewById(R.id.remark_edt)
        receipt_no_edt = view.findViewById(R.id.receipt_no_edt)
        view_agent_name_edt = view.findViewById(R.id.view_agent_name_edt)
        spinner_submit_type = view.findViewById(R.id.spinner_submit_type)
        spinner_bank_name = view.findViewById(R.id.spinner_bank_name)
        date_lin = view.findViewById(R.id.date_lin)
        submit = view.findViewById(R.id.bt_submit)
        fileImageView = view.findViewById(R.id.imageFile)
        viewFinder = view.findViewById(R.id.viewFinder)
        submit!!.setOnClickListener(this)
        date_lin!!.setOnClickListener(this)
        mobile_edt!!.setText(loginModel!!.Data.Mobile)
        agent_name_edt!!.setText(loginModel!!.Data.Name)
        depositView=view.findViewById(R.id.depositView)
        cameraView=view.findViewById(R.id.cameraView)
        depositView!!.setOnClickListener(this)
        view.findViewById<View>(R.id.chooseFileLin).setOnClickListener(this)
        view.findViewById<View>(R.id.imageCaptureButton).setOnClickListener(this)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        agency_name_tv!!.setOnClickListener(this)
        spinner_submit_type!!.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (parent?.id) {
                    R.id.spinner_submit_type -> typeSelector(
                        position,
                        spinner_submit_type!!.selectedItem.toString()
                    )
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        spinner_submit_type!!.setAdapter(
            setSpinnerAdapter(
                arrayOf(
                    "-Select-",
                    "Cash",
                    "Cheque",
                    "OnlineTransfer"
                )
            )
        )
        spinner_bank_name!!.setAdapter(
            setSpinnerAdapter(
                loginModel!!.Data.BankNames.split("#".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()))
        try {
            agency_name_tv!!.setText(
                """
    ${loginModel!!.Data.AgencyName.uppercase(Locale.getDefault())}
    (${loginModel!!.Data.DoneCardUser})
    """.trimIndent()
            )
        } catch (e: NullPointerException) {
        }
    }

    private fun initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat()

        //default start Date
        startDateCalendar = Calendar.getInstance()
        startDateDay = startDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        startDateMonth = startDateCalendar!!.get(Calendar.MONTH)
        startDateYear = startDateCalendar!!.get(Calendar.YEAR)
        transactionDate = dateToServerFormat!!.format(startDateCalendar!!.getTime())

        //set default date
        date_tv!!.text = Common.getShowInTVDateFormat()
            .format(startDateCalendar!!.getTime())
    }

    private fun validate(): Boolean {
        try {
            if (spinner_submit_type!!.selectedItem.toString().contains("Select")) {
                Toast.makeText(context, R.string.select_submit_type, Toast.LENGTH_LONG).show()
                return false
            } else if (!Common.isdecimalvalid(amount_edt!!.text.toString().trim { it <= ' ' }) ||
                ((amount_edt!!.text.toString().trim { it <= ' ' }.toFloat()) == 0f )
            ) {
                Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_LONG).show()
                return false
            } else if (amount_edt!!.text.toString().trim { it <= ' ' }.toFloat() > 1000000) {
                Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show()
                return false
            } else if (mobile_edt!!.text.toString().trim { it <= ' ' }.length < 10) {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_LONG).show()
                return false
            } else if (spinner_bank_name!!.selectedItem.toString().contains("Select")) {
                Toast.makeText(context, R.string.select_bank, Toast.LENGTH_LONG).show()
                return false
            } else if (receipt_no_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(context, R.string.empty_and_invalid_receipt, Toast.LENGTH_LONG)
                    .show()
                return false
            } else if (!Common.isNameValid(
                    agent_name_edt!!.text.toString()
                        .trim { it <= ' ' }) && spinner_submit_type!!.selectedItem == "Cash"
            ) {
                Toast.makeText(context, R.string.empty_and_invalid_agent_name, Toast.LENGTH_LONG)
                    .show()
                return false
            } else if (remark_edt!!.text.toString().length == 0) {
                Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_LONG).show()
                return false
            } /*else if (networkOutputStream==null && spinner_submit_type.getSelectedItem().toString().equals("Cash")) {
                Toast.makeText(context, "please choose receipt image file", Toast.LENGTH_LONG).show();
                return false;
            }*/ else if (imagepath == null /* && spinner_submit_type.getSelectedItem().toString().equals("Cash")*/) {
                Toast.makeText(context, "please choose receipt image file", Toast.LENGTH_LONG)
                    .show()
                return false
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
        }
        return true
    }

    private fun typeSelector(position: Int, selectedValue: String) {
        type = selectedValue
        if (selectedValue.equals("Cash", ignoreCase = true)) {
            agent_name_edt!!.visibility = View.VISIBLE
            view_agent_name_edt!!.visibility = View.VISIBLE
        } else {
            agent_name_edt!!.visibility = View.INVISIBLE
            view_agent_name_edt!!.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_submit -> {
                Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
                if (Common.checkInternetConnection(context)) {
                    if (validate()) {
                        val model = AgentDepositRequestRequestModel()
                        model.TotalAmount = amount_edt!!.text.toString().trim { it <= ' ' }
                        model.DeviceId = Common.getDeviceId(context)
                        model.LoginSessionId = EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(
                                loginModel!!.LoginSessionId,
                                context
                            ), context
                        )
                        model.DoneCardUser = loginModel!!.Data.DoneCardUser
                        model.EmpName = agent_name_edt!!.text.toString().trim { it <= ' ' }
                        model.BankName = spinner_bank_name!!.selectedItem.toString()
                        model.TypeOfAmount = spinner_submit_type!!.selectedItem.toString()
                        model.MobileNumber = mobile_edt!!.text.toString()
                        model.ReceiptNo = receipt_no_edt!!.text.toString()
                        model.TransactionDate = transactionDate
                        if (remark_edt!!.text.toString().trim { it <= ' ' } != null) {
                            model.Remarks = remark_edt!!.text.toString().trim { it <= ' ' }
                        } else {
                            model.Remarks = ""
                        }
                        if (Common.checkInternetConnection(context)) {
                            requestAmount(model)
                        } else {
                            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show()
                }
            }

            R.id.date_lin -> try {
                openDatePicker()
            } catch (e: Exception) {
            }

            R.id.depositView, R.id.agency_name_tv -> Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
            R.id.chooseFileLin -> //                askSelfPermission();
            {
                selectOption()
            }
            R.id.imageCaptureButton ->
            {
                takePhoto()
            }
            R.id.back_arrow ->
            {
                if(cameraView?.visibility==View.VISIBLE){
                    cameraView!!.visibility=View.GONE
                    depositView!!.visibility=View.VISIBLE
                }else{
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun selectOption() {
//        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.choose_photo_dialog)
        var galleryImg=dialog.findViewById<ImageView>(R.id.galleryImg)
        var cameraImg=dialog.findViewById<ImageView>(R.id.cameraImg)

        galleryImg.setOnClickListener {
            askSelfPermission2()
            dialog.dismiss()
        }
        cameraImg.setOnClickListener {
            if (allPermissionsGranted()) {
                startCamera()
                depositView!!.visibility=View.GONE
                cameraView!!.visibility=View.VISIBLE
            } else {
                requestPermissions()
            }
            dialog.dismiss()
        }

        dialog.show()
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
                depositView!!.visibility=View.GONE
                cameraView!!.visibility=View.VISIBLE
            }
        }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val REQUIRED_PERMISSIONS =
        mutableListOf (
            permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder!!.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    imageUri=output.savedUri
                    getImagePath()
                    depositView!!.visibility=View.VISIBLE
                    cameraView!!.visibility=View.GONE
//                    setImageBitmap(output.savedUri)
                    Log.d(TAG, msg)
                }
            }
        )
    }


    var readImagePermission: String? = null
    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback<Map<String, Boolean>> { result: Map<String, Boolean> ->
            if (result.size > 0) {
                var allGranted = true
                for (key in result.keys) {
                    allGranted = allGranted && result[key]!!
                }
                if (allGranted) {
                    selectFile()
                }
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        })
    private val storagePermissionLauncher2 =
        registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                selectFile()
            } else {
//                    requestPermission();
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun askSelfPermission2() {
        readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permission.READ_MEDIA_IMAGES else permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
                requireContext(),
                permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
            selectFile()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            (ContextCompat.checkSelfPermission(
                requireContext(),
                permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        permission.READ_MEDIA_VISUAL_USER_SELECTED
                    ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    permission.READ_MEDIA_IMAGES,
                    permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                readImagePermission!!
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            selectFile()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                readImagePermission!!
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            storagePermissionLauncher2.launch(permission.READ_MEDIA_IMAGES)
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                readImagePermission!!
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            selectFile()
        } else {
            storagePermissionLauncher2.launch(permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun getImagePath() {
        imagepath = getPath(imageUri)
        var imageType = ""
        if (imagepath != null) {
            imageType = imagepath!!.substring(imagepath!!.lastIndexOf("."))
        }
        if (imagepath != null && (imageType.equals(".jpg", ignoreCase = true) ||
                    imageType.equals(".png", ignoreCase = true)) ||
            imageType.equals(".jpeg", ignoreCase = true)
        ) {
            if (File(imagepath).length() < IMAGE_MAX_SIZE_8MB) {
                if (File(imagepath).length() > IMAGE_MAX_SIZE_1MB) {
//                Toast.makeText(context, "Image too large. Please select image less than 3 MB", Toast.LENGTH_LONG).show();
                    try {
                        val compressedImageFile =
                            ImageCompressor.getCompressed(requireContext(), imagepath)
                        receiptFile =
                            File(requireContext().externalCacheDir, compressedImageFile.name)
                        //                    Toast.makeText(requireContext(), compressedImageFile.length()/(1024 * 1024)+"", Toast.LENGTH_SHORT).show();
                        val bmOptions = BitmapFactory.Options()
                        val bitmap =
                            BitmapFactory.decodeFile(receiptFile!!.canonicalPath, bmOptions)
                        val display = requireActivity().windowManager.defaultDisplay
                        val size = Point()
                        display.getSize(size)
                        val width = size.x
                        val height = size.y
                        val scaleHt = width.toFloat() / bitmap.width
                        Log.e("Scaled percent ", " \$scaleHt")
                        val scaled = Bitmap.createScaledBitmap(
                            bitmap,
                            width,
                            (bitmap.width * scaleHt).toInt(),
                            true
                        )
                        fileImageView!!.setImageBitmap(scaled)
                        networkOutputStream = ByteArrayOutputStream()
                    } catch (e: IOException) {
                        Toast.makeText(
                            context,
                            "Image too large. Please select image less than 3 MB",
                            Toast.LENGTH_LONG
                        ).show()
                        throw RuntimeException(e)
                    }
                } else {
                    receiptFile = File(imagepath)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(imagepath, bmOptions)
                    fileImageView!!.setImageBitmap(bitmap)
                    networkOutputStream = ByteArrayOutputStream()
                }
            }else{
                Toast.makeText(context, "Image too large. Please select image less than 8 MB", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                context,
                "Please select image in JPEG or PNG format",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var pickMedia =
        registerForActivityResult<PickVisualMediaRequest, Uri>(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
                getImagePath()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun selectFile() {
//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.setType("image/*");
//        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//        launchSomeActivity.launch(chooseFile);
//        launchSomeActivity.launch(getPickImageIntent(context));

        observer.selectImage(this)
//        pickMedia.launch(
//            PickVisualMediaRequest.Builder()
//                .setMediaType(ImageOnly)
//                .build()
//        )
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "Deposit_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        imagepath = image.absolutePath
        return image
    }

    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri!!, projection, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                startDateCalendar!![year, monthOfYear] = dayOfMonth
                transactionDate = dateToServerFormat!!.format(startDateCalendar!!.time)
                startDateDay = dayOfMonth
                startDateMonth = monthOfYear
                startDateYear = year
                date_tv!!.text = Common.getShowInTVDateFormat().format(
                    startDateCalendar!!.time
                )
            }, startDateYear, startDateMonth, startDateDay
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun requestAmount(model: AgentDepositRequestRequestModel) {
        addAccount(model)
    }

    private fun setSpinnerAdapter(data: Array<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.mobile_operator_spinner_item, R.id.operator_tv, data
        )
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    private fun addAccount(model: AgentDepositRequestRequestModel) {
        NetworkCall().callService(
            NetworkCall.getDepositRequestInterface().depositRequest(
                ApiConstants.AgentDepositRequest,
                createRequestBody(Gson().toJson(model)),
                sendFile("File", receiptFile)
            ),
            requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerNew(response)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.response_failure_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun responseHandlerNew(response: ResponseBody) {
        try {
            val responseModel =
                Gson().fromJson(response.string(), DepositRequestResponseModel::class.java)
            hideCustomDialog()
            if (responseModel != null) {
                if (responseModel.DepositRequestResult.StatusCode == "0") {
                    Toast.makeText(
                        context,
                        responseModel.DepositRequestResult.Data.Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    amount_edt!!.setText("")
                    receipt_no_edt!!.setText("")
                    networkOutputStream = null
                    fileImageView!!.setImageResource(R.drawable.choose_file)
                    Common.showResponsePopUp(
                        context,
                        responseModel.DepositRequestResult.Data.Message
                    )
                } else {
                    Toast.makeText(
                        context,
                        responseModel.DepositRequestResult.Status,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createRequestBody(userId: String): RequestBody {
        return RequestBody.create(
            ("multipart/form-data").toMediaTypeOrNull(),
            userId
        )
        //        return RequestBody.create(MediaType.parse("text/plain"),
//                userId);
    }

    private fun sendFile2(paramName: String, imageFile: File?): MultipartBody.Part? {
        if (imageFile == null) return null
        var type: String? =requireContext().contentResolver.getType(imageUri!!)
        val requestFile = RequestBody.create(
            type!!.toMediaTypeOrNull(),
            imageFile
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(paramName, imageFile.name, requestFile)
    }

    private fun sendFile(paramName: String, imageFile: File?): MultipartBody.Part? {
        if (imageFile == null) return null
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            imageFile
        )
        val requestFile = RequestBody.create(
            requireContext().contentResolver.getType(uri)!!.toMediaTypeOrNull(),
            imageFile
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(paramName, imageFile.name, requestFile)
    }

    override fun onPause() {
        super.onPause()
        hideCustomDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        fun getFileDataFromDrawable(context: Context?, drawable: Drawable): ByteArray {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    class MyLifecycleObserver(private val registry : ActivityResultRegistry)
        : DefaultLifecycleObserver {
        lateinit var getContent : ActivityResultLauncher<PickVisualMediaRequest>
        lateinit var context:AgentDepositRequestFragmentNewww
        override fun onCreate(owner: LifecycleOwner) {
            getContent = registry.register("key", owner, ActivityResultContracts.PickVisualMedia()) { uri ->
                // Handle the returned Uri
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    context.imageUri=uri
                    context.getImagePath()
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        }

        fun selectImage(agentDepositRequestFragmentNewww: AgentDepositRequestFragmentNewww) {
            context=agentDepositRequestFragmentNewww
            getContent.launch(PickVisualMediaRequest.Builder()
                .setMediaType(ImageOnly)
                .build())
        }
    }
}