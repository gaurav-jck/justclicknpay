package com.justclick.clicknbook.Fragment.jctmoney.dmt3

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.AddRemittanceFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse.credentialData
import com.justclick.clicknbook.Fragment.jctmoney.response.DmtKycResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentDmt3MerchantKycBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class Dmt3MerchantKycFragment : Fragment() {
    private val FINGER_CAPTURE = 1
    private final val CAPTURE_REQUEST_CODE = 123
    private val MANTRA = "MANTRA"
    private val MANTRA_L1 = "MANTRA_L1"
    private val STARTEK = "STARTEK"
    private val STARTEK_L1 = "STARTEK_L1"
    private val MORPHO = "MORPHO"
    private val MORPHO_L1 = "MORPHO_L1"
    private val MANTRA_PACKAGE = "com.mantra.rdservice"
    private val MANTRA_L1_PACKAGE = "com.mantra.mfs110.rdservice"
    private val STARTEK_PACKAGE = "com.acpl.registersdk"
    private val STARTEK_PACKAGE_L1 = "com.acpl.registersdk_l1"
    private val MORPHO_PACKAGE = "com.scl.rdservice"
    private val MORPHO_PACKAGE_L1 = "com.idemia.l1rdservice"
    private val deviceArray = arrayOf("Mantra L1", "Mantra L0", "Morpho L1", "Morpho", "Startek", "Startek L1 (Access)")
    var d_type = MANTRA_L1
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var commonParams: credentialData? = null
    private var binding: FragmentDmt3MerchantKycBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, credentialData::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as credentialData?
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentDmt3MerchantKycBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)
        initializeDates()

        binding!!.btnCapture.setOnClickListener {
            if(validateFields()){
                captureData()
            }

        }
        binding!!.dobEdt.setOnClickListener {
            openDatePicker()
        }

        binding!!.spinnerDeviceType.adapter= Common.getSpinnerAdapter(deviceArray, requireContext())
        binding!!.spinnerDeviceType.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                when (i) {
                    0 -> d_type = MANTRA_L1
                    1 -> d_type = MANTRA
                    2 -> d_type = MORPHO_L1
                    3 -> d_type = MORPHO
                    4 -> d_type = STARTEK
                    5 -> d_type = STARTEK_L1
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding!!.addressEdt.setText(commonParams!!.address)
        binding!!.cityEdt.setText(commonParams!!.city)
        binding!!.pinEdt.setText(commonParams!!.pinCode)
        return binding!!.root
    }

    private fun validateFields(): Boolean {
        if(binding!!.aadharEdt.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Please enter aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.aadharEdt.text.toString().trim().length<12) {
            Toast.makeText(context, "Please enter valid aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding!!.numberEdt.text.toString().length < 10) {
            Toast.makeText(context, getString(R.string.empty_and_invalid_mobile), Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isEmailValid(binding!!.emailEdt.text.toString().trim())) {
            Toast.makeText(context, "Email is empty or invalid", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isNameValid(binding!!.nameEdt.text.toString().trim())) {
            Toast.makeText(context, "Pan name is empty or invalid", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isPancardValid(binding!!.panEdt.text.toString().trim())) {
            Toast.makeText(context, "Please enter valid pan number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.dobEdt.text.toString().isEmpty()) {
            Toast.makeText(context, "please select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.addressEdt.text.isEmpty()) {
            Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.cityEdt.text.isEmpty()) {
            Toast.makeText(context, "Please enter city name", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.pinEdt.text.toString().length < 6) {
            Toast.makeText(context, getString(R.string.empty_and_invalid_pincode), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true
    }

    fun captureData() {
        try {
            if (d_type == STARTEK) {
                if (searchPackageName(STARTEK_PACKAGE)) {
//                    String pidOptXML = createPidOptXMLStartek();
                    val pidOptXML: String = createPidOptXML()
                    capture(STARTEK_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
                }
            } else if (d_type == STARTEK_L1) {
                val pidOptXML =
                    "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                capture(STARTEK_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == MANTRA) {
                if (searchPackageName(MANTRA_PACKAGE)) {
//                    String pidOptXML = getPIDOptions();
//                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"0\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    val pidOptXML =
                        "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                    capture(MANTRA_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
                }
            } else if (d_type == MANTRA_L1) {
                val pidOptXML =
                    "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                capture(MANTRA_L1_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == MORPHO) {
                if (searchPackageName(MORPHO_PACKAGE)) {
//                    String pidOptXML = createPidOptXML();  //old
                    val pidOptXML: String = getPIDOptionsPay() //paysprint
                    //                    String pidOptXML = getPIDOptionsPay2();  //paysprint2
//                    pidOptXML="<PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" env=\"P\" wadh=\"\" posh=\"UNKNOWN\"/></PidOptions>";
                    capture(MORPHO_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
                }
            }else if (d_type == MORPHO_L1) {
                val pidOptXML =
                    "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                capture(MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
//                    isDataCaptured=false;
//                    showMessageDialogue("OnActivity result.  resultcode="+resultCode+" Message="+data.getData().getUserInfo(), "Message");
                if (data == null) {
                    showMessageDialogue("Scan Failed/Aborted!", "Message")
                } else {
                    pidDataXML = data.getStringExtra("PID_DATA")!!
                    if (pidDataXML != null) {
                        // xml parsing
                        captureType=FINGER_CAPTURE
                        readXMLData(pidDataXML, FINGER_CAPTURE)
                    } else {
                        showMessageDialogue(
                            "NULL STRING RETURNED",
                            "Fingerprint data status"
                        )
                    }
                }
            } catch (ex: java.lang.Exception) {
                showMessageDialogue("Error:-" + ex.message, "EXCEPTION")
                ex.printStackTrace()
            }
        }
    }

    private fun readXMLData(pidDataXML: String, type: Int) {
        try {
            val inputStream: InputStream =
                ByteArrayInputStream(pidDataXML.toByteArray(charset("UTF-8")))
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inputStream)
            val element = doc.documentElement
            element.normalize()
            val nList = doc.getElementsByTagName("PidData")
            val node = nList.item(0)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element2 = node as Element
                val s_status = element2.getElementsByTagName("Resp")
                    .item(0).attributes.getNamedItem("errCode").nodeValue
                if (s_status == "0") {
//                    isDataCaptured=true;
                    showTransactionAlert()
                    //                    showMessageDialogue("Data captured", "Fingerprint data status");
                } else {
                    val s_message = element2.getElementsByTagName("Resp")
                        .item(0).attributes.getNamedItem("errInfo").nodeValue
                    if(type==FINGER_CAPTURE)
                    {
                        showMessageDialogue(s_message, "Fingerprint data status")
                    }else{
                        showMessageDialogue(s_message, "Face capture data status")
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("Jobs", "Exception parse xml :$e")
            //            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private fun capture(packageName: String, pidOptXML: String, requestCode: Int) {
//        sessionCheckMethod(false);
        val intent1 = Intent("in.gov.uidai.rdservice.fp.CAPTURE", null)
        //String pidOptXML = getPIDOptions(); //working
        intent1.putExtra("PID_OPTIONS", pidOptXML)
        intent1.setPackage(packageName)
        startForResult.launch(intent1)
//        startActivityForResult(intent1, requestCode)
    }

    private fun getPIDOptionsPay(): String {

        return "<?xml version=\"1.0\"?><PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" posh=\"UNKNOWN\" env=\"P\" /><CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts></PidOptions>"
    }

    // pid data xml for startek
    private fun createPidOptXML(): String {
        var tmpOptXml = ""
        return try {
            val fCount = "1"
            val fType = "2"
            val iCount = "0"
            val iType = "0"
            val pCount = "0"
            val pType = "0"
            val format = "0"
            val pidVer = "2.0"
            val timeout = "20000"
            val otp = ""
            //            String env = "PP";   //uat
            val env = "P" //live
            val wath = ""
            val wadh = "18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw="
            val posh = "UNKNOWN"
            val docFactory = DocumentBuilderFactory.newInstance()
            docFactory.isNamespaceAware = true
            var docBuilder: DocumentBuilder? = null
            docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.newDocument()
            doc.xmlStandalone = true
            val rootElement = doc.createElement("PidOptions")
            doc.appendChild(rootElement)
            val opts = doc.createElement("Opts")
            rootElement.appendChild(opts)
            var attr = doc.createAttribute("fCount")
            //attr.setValue(String.valueOf(fCountSel.getSelectedItem().toString()));
            attr.value = fCount
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("fType")
            attr.value = fType
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("iCount")
            attr.value = iCount
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("iType")
            attr.value = iType //change
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("pCount")
            attr.value = pCount
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("pType")
            attr.value = pType //change
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("format")
            attr.value = format
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("pidVer")
            attr.value = pidVer
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("timeout")
            attr.value = timeout
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("otp")
            attr.value = otp
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("env")
            attr.value = env
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("wadh")
            //attr.setValue("ONLY USE FOR E-KYC.");
            attr.value = wadh
            opts.setAttributeNode(attr)
            attr = doc.createAttribute("posh")
            attr.value = posh
            opts.setAttributeNode(attr)
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes")
            val source = DOMSource(doc)
            val writer = StringWriter()
            val result = StreamResult(writer)
            transformer.transform(source, result)
            tmpOptXml = writer.buffer.toString().replace("\n|\r".toRegex(), "")
            tmpOptXml = tmpOptXml.replace("&lt;".toRegex(), "<").replace("&gt;".toRegex(), ">")
            tmpOptXml
        } catch (ex: java.lang.Exception) {
            showMessageDialogue("EXCEPTION- " + ex.message, "EXCEPTION")
            ""
        }
    }

    // show message
    private fun showMessageDialogue(messageTxt: String, argTitle: String) {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(argTitle)
            .setMessage(messageTxt)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> dialog.cancel() }
            .show()
    }

    // show message
    private fun showTransactionAlert() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("Please confirm your KYC")
            .setMessage("Do you want to do KYC with given details!")
            .setPositiveButton("CONFIRM") { dialog, which ->
                dialog.cancel()
                sendMobileTransaction()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, i -> dialog.cancel() }
            .show()
    }

    private fun sendMobileTransaction() {
        var loginModel= MyPreferences.getLoginData(LoginModel(),context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["Aadhar"] = binding!!.aadharEdt.text.toString()
        params["Email"] = binding!!.emailEdt.text.toString()
        params["Mobileno"] = binding!!.numberEdt.text.toString()
        params["Name"] = binding!!.nameEdt.text.toString()
        params["PAN"] = binding!!.panEdt.text.toString()
        params["Address"] = binding!!.addressEdt.text.toString()
        params["City"] = binding!!.cityEdt.text.toString()
//        params["StateCode"] = commonParams!!.statecode
        params["StateCode"] = "MH"
        params["PinCode"] = binding!!.pinEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Mode"] = "App"
        params["MerchantId"] = ApiConstants.MerchantId
        params["FirmName"] = loginModel.Data.AgencyName
        params["DOB"] = binding!!.dobEdt.text.toString()
        params["Lattitude"] = "20.593684"
        params["Longitude"] = "78.96288"
        if (d_type.equals(MANTRA)){
            pidDataXML=("<?xml version=\"1.0\"?>$pidDataXML").replace("\n", "")
            params["pid"]=pidDataXML
        }else{
            pidDataXML=pidDataXML.replace("\n", "")
            params["pid"]=pidDataXML
        }

        NetworkCall().callService(
            NetworkCall.getDmt3ApiInterface().getDmt3HeaderMap(
                ApiConstants.merchantkyc, params,
                commonParams!!.userData, "Bearer " + commonParams!!.token
            ),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), DmtKycResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                } else {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun searchPackageName(mPackageName: String): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val packageList = packageManager
            .getInstalledPackages(PackageManager.GET_PERMISSIONS)
        var isPackage = false
        for (pl in packageList) {
            if (pl.applicationInfo?.packageName == mPackageName) {
                isPackage = true
            }
        }

        if (!isPackage) {
            val toast = Toast.makeText(
                context,
                "Please install ` Registered Device` Service.",
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            val intentPlay = Intent(Intent.ACTION_VIEW)
            //intentPlay.setData(Uri.parse("market://details?id=com.acpl.registersdk"));
            intentPlay.data = Uri.parse("market://details?id=$mPackageName")
            startActivity(intentPlay)
            return false
        }
        return true
    }

    private var dateServerFormat: SimpleDateFormat? = null
    private var checkInDateDay :Int= 0
    private var checkInDateMonth: Int = 0
    private var checkInDateYear: Int = 0
    private var dobDateCalendar: Calendar? = null
    private var currentDate: Calendar? = null
    private fun initializeDates() {
        //Date formats
        dateServerFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        currentDate = Calendar.getInstance()
        dobDateCalendar = Calendar.getInstance()

        checkInDateDay = currentDate!!.get(Calendar.DAY_OF_MONTH)
        checkInDateMonth = currentDate!!.get(Calendar.MONTH)
        checkInDateYear = currentDate!!.get(Calendar.YEAR)
    }
    private fun openDatePicker() {
        //Date formats

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                dobDateCalendar!!.set(year, monthOfYear, dayOfMonth)
                binding!!.dobEdt.setText(dateServerFormat!!.format(dobDateCalendar!!.getTime()))
            }, checkInDateYear, checkInDateMonth, checkInDateDay
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = currentDate!!.timeInMillis
        datePickerDialog.show()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: CheckCredentialResponse.credentialData) =
            Dmt3MerchantKycFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    var responseData="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Kyc completed and OTP has been sent to remitter mobile number.\",\n" +
            "    \"data\": {\n" +
            "        \"fname\": \"NA\",\n" +
            "        \"lname\": \"NA\",\n" +
            "        \"mobile\": \"7452016171\",\n" +
            "        \"ekyc_id\": \"718266\",\n" +
            "        \"stateresp\": \"468343571\"\n" +
            "    }\n" +
            "}"
}