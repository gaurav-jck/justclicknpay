package com.justclick.clicknbook.Fragment.profilemenus.raisequery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment.CommonResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentRaiseQueryBinding
import com.justclick.clicknbook.model.DepositRequestResponseModel
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.requestmodels.SupportQueryRequest
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.ImageCompressor
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

class RaiseQueryFragment : Fragment(), View.OnClickListener {
    private var context: Context? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    private var productTypeSpinner: Spinner? = null
    private var binding: FragmentRaiseQueryBinding? = null

    //    image select
    private val IMAGE_MAX_SIZE_8MB = 1024 * 1024 * 8.0f
    private val IMAGE_MAX_SIZE_1MB = 1024 * 1024 * 1.0f
    lateinit var observer: MyLifecycleObserver
    private var imageUri: Uri? = null
    private var imagepath: String? = null
    private var receiptFile: File? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = MyLifecycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(observer)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_raise_query, container, false)
        binding = FragmentRaiseQueryBinding.bind(view)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        productTypeSpinner = view.findViewById(R.id.productTypeSpinner)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        view.findViewById<View>(R.id.submit_tv).setOnClickListener(this)
        binding!!.chooseFileLin.setOnClickListener(this)
        setProductTypeAdapter()
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_arrow -> parentFragmentManager.popBackStack()
            R.id.chooseFileLin -> askSelfPermission2()
            R.id.submit_tv -> if (validate()) {
                submitQuery()
            }
        }
    }

    var readImagePermission: String? = null
    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        if (result.isNotEmpty()) {
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
    }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
            selectFile()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
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
            storagePermissionLauncher2.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                readImagePermission!!
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            selectFile()
        } else {
            storagePermissionLauncher2.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun selectFile() {
        observer.selectImage(this)
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
                        var width = 0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val wm=requireActivity().windowManager.currentWindowMetrics
                            width=wm.bounds.width()
                        } else {
                            val display = requireActivity().windowManager.defaultDisplay
                            val size = Point()
                            display.getSize(size)
                            width=size.x
                        }
                        val scaleHt = width.toFloat() / bitmap.width
                        Log.e("Scaled percent ", " \$scaleHt")
                        val scaled = Bitmap.createScaledBitmap(
                            bitmap,
                            width,
                            (bitmap.width * scaleHt).toInt(),
                            true
                        )
                        binding!!.imageFile.setImageBitmap(scaled)
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
                    binding!!.imageFile.setImageBitmap(bitmap)
                }
            } else {
                Toast.makeText(
                    context,
                    "Image too large. Please select image less than 8 MB",
                    Toast.LENGTH_LONG
                ).show();
            }
        } else {
            Toast.makeText(
                context,
                "Please select image in JPEG or PNG format",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri!!, projection, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun submitQuery() {
        val request = RaiseQueryRequest()
        request.Donecarduser = MyPreferences.getLoginData(LoginModel(),requireContext()).Data.DoneCardUser
        request.MobileNo = binding!!.numberEdt.text.toString()
        request.EmailID = binding!!.emailEdt.text.toString()
        request.Producttype = binding!!.productTypeSpinner!!.selectedItem.toString()
        request.IssueType = binding!!.issueEdt.text.toString()
        request.Remarks = binding!!.remarksEdt.text.toString()
        val json = Gson().toJson(request)

        NetworkCall().callService(
            NetworkCall.getDepositRequestInterface().raiseQuery(
                ApiConstants.raiseticket,
                createRequestBody(Gson().toJson(request)),
                sendFile("file", receiptFile)
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
    private fun createRequestBody(userId: String): RequestBody {
        return RequestBody.create(
            ("multipart/form-data").toMediaTypeOrNull(),
            userId
        )
        //        return RequestBody.create(MediaType.parse("text/plain"),
//                userId);
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

    private fun responseHandler(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                DepositRequestResponseModel::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.DepositRequestResult.StatusCode.equals( "0")) {
//                    Toast.makeText(context, senderResponse.DepositRequestResult.Status, Toast.LENGTH_SHORT).show()
                    Common.showResponsePopUp(
                        context,
                        senderResponse.DepositRequestResult.Data.Message
                    )
                    resetData()
                } else if (senderResponse.DepositRequestResult.Status != null) {
                    Toast.makeText(context, senderResponse.DepositRequestResult.Status, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Error in submitting request, please try after some time.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetData() {
        binding!!.issueEdt!!.setText("")
        binding!!.numberEdt!!.setText("")
        binding!!.emailEdt!!.setText("")
        binding!!.remarksEdt!!.setText("")
    }

    private fun validate(): Boolean {
        if (!Common.isMobileValid(binding!!.numberEdt!!.text.toString())) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        } else if (!Common.isEmailValid(binding!!.emailEdt!!.text.toString())) {
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
            return false
        } else if (binding!!.issueEdt!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(context, "Please enter any query", Toast.LENGTH_SHORT).show()
            return false
        }else if (imagepath == null) {
            Toast.makeText(context, "please choose receipt image file", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun setProductTypeAdapter() {
        val title =
            arrayOf<String?>("DMT", "AEPS", "PAYOUT", "RAIL", "UTILITY", "BUS", "FLIGHT", "ACCOUNT")
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            title
        )

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        productTypeSpinner!!.adapter = ad
    }

    class MyLifecycleObserver(private val registry: ActivityResultRegistry) :
        DefaultLifecycleObserver {
        lateinit var getContent: ActivityResultLauncher<PickVisualMediaRequest>
        lateinit var context: RaiseQueryFragment
        override fun onCreate(owner: LifecycleOwner) {
            getContent =
                registry.register("key", owner, ActivityResultContracts.PickVisualMedia()) { uri ->
                    // Handle the returned Uri
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: $uri")
                        context.imageUri = uri
                        context.getImagePath()
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
        }

        fun selectImage(raiseQueryFragment: RaiseQueryFragment) {
            context = raiseQueryFragment
            getContent.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
            )
        }
    }
    companion object {
        fun newInstance(param1: String?, param2: String?): RaiseQueryFragment {
            val fragment = RaiseQueryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}