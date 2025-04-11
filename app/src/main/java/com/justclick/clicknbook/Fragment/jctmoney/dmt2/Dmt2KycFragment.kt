package com.justclick.clicknbook.Fragment.jctmoney.dmt2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.justclick.clicknbook.Fragment.jctmoney.RapipaySenderDetailFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.request.Dmt2AdharValidateRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.DmtKycResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.FragmentTags
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentDmt2KycBinding
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import okhttp3.ResponseBody
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Dmt2KycFragment : Fragment() {
    private final val CAPTURE_REQUEST_CODE = 123
    private val FINGER_CAPTURE = 1
    private val FACE_CAPTURE = 2
    private val MANTRA = "MANTRA"
    private val MANTRA_L1 = "MANTRA_L1"
    private val STARTEK = "STARTEK"
    private val MORPHO = "MORPHO"
    private val MORPHO_L1 = "MORPHO_L1"
    private val MANTRA_PACKAGE = "com.mantra.rdservice"
    private val MANTRA_L1_PACKAGE = "com.mantra.mfs110.rdservice"
    private val STARTEK_PACKAGE = "com.acpl.registersdk"
    private val MORPHO_PACKAGE = "com.scl.rdservice"
    private val MORPHO_PACKAGE_L1 = "com.idemia.l1rdservice"
    private val deviceArray = arrayOf("Mantra L1", "Mantra L0", "Morpho L1", "Morpho L0", "Startek")
    var d_type = MANTRA_L1
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var senderDetailResponse: SenderDetailResponse? = null
    private var commonParams: CommonParams? = null
    private var binding: FragmentDmt2KycBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                senderDetailResponse = it.getSerializable(ARG_PARAM1, SenderDetailResponse::class.java)
                commonParams = it.getSerializable(ARG_PARAM2, CommonParams::class.java)
            }else{
                senderDetailResponse = it.getSerializable(ARG_PARAM1) as SenderDetailResponse?
                commonParams = it.getSerializable(ARG_PARAM2) as CommonParams?
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentDmt2KycBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)

        if (binding!!.aadharEdt.getText().toString().length < 12) {
            hideCapture()
        } else {
            showCapture()
        }
        binding!!.aadharEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (binding!!.aadharEdt.getText().toString().length < 12) {
                    hideCapture()
                } else {
                    showCapture()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        hideCaptureView()
//        showCaptureView()
        binding!!.btnCapture.setOnClickListener {
            captureData()

        }
        binding!!.btnFaceCapture.setOnClickListener {
//            if (searchPackageName("in.gov.uidai.facerd")) {
//                captureFaceData()
//            }
            captureFaceData()
        }
        binding!!.downloadImg.setOnClickListener {
            val intentPlay = Intent(Intent.ACTION_VIEW)
            intentPlay.data = Uri.parse("market://details?id=in.gov.uidai.facerd");
//            intentPlay.data = Uri.parse("market://details?id=$mPackageName")
            startActivity(intentPlay)
        }
        binding!!.validateAdhar.setOnClickListener {
            validateAdhar()
        }
        binding!!.btnSubmit.setOnClickListener {
            if(pidDataXML.isEmpty()){
                Toast.makeText(requireContext(), "Please capture finger data first", Toast.LENGTH_SHORT).show()
            }else if(binding!!.otpEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
            }else if(binding!!.otpEdt.text.toString().length<4){
                Toast.makeText(requireContext(), "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            }else{
                submitKyc()
            }
        }

       /* (binding!!.radioGroup).setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_mantra -> d_type = MANTRA
                R.id.rb_startek -> d_type = STARTEK
                R.id.rb_morpho -> d_type = MORPHO
            }
        }*/
        binding!!.spinnerDeviceType.adapter=Common.getSpinnerAdapter(deviceArray, requireContext())
        binding!!.spinnerDeviceType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })


        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding!!.root
    }

    private fun validateAdhar() {
        var dmt2AdharValidateRequest=Dmt2AdharValidateRequest()
        dmt2AdharValidateRequest.AgentCode=commonParams!!.agentCode
        dmt2AdharValidateRequest.Aadhaar_no=binding!!.aadharEdt.text.toString()
        dmt2AdharValidateRequest.Mobile=commonParams!!.mobile
        NetworkCall().callService(
            NetworkCall.getDmt2ApiInterface().getDmt2Header(ApiConstants.aadharverify, dmt2AdharValidateRequest,
                commonParams!!.userData, "Bearer " + commonParams!!.token
            ),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerValidate(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerValidate(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    senderDetailResponse=commonResponse
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                    showCaptureView()
                    hideSubmit()
                } else {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
//                    showCaptureView()
//                    hideSubmit()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun submitKyc() {
        sendMobileTransaction()
    }

    private fun hideCaptureView() {
        binding!!.captureLin.visibility=View.GONE
        binding!!.radioGroup.visibility=View.GONE
        binding!!.otpEdt.visibility=View.GONE
        binding!!.rdDownloadLin.visibility=View.GONE
    }
    private fun showCaptureView() {
        binding!!.captureLin.visibility=View.VISIBLE
        binding!!.radioGroup.visibility=View.VISIBLE
        binding!!.otpEdt.visibility=View.VISIBLE
        binding!!.rdDownloadLin.visibility=View.VISIBLE
        binding!!.validateLin.visibility=View.GONE
        binding!!.aadharEdt.isEnabled=false
    }

    fun captureData() {
        try {
            if (d_type == STARTEK) {
                if (searchPackageName(STARTEK_PACKAGE)) {
//                    String pidOptXML = createPidOptXMLStartek();
                    val pidOptXML: String = createPidOptXML()
                    capture(STARTEK_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
                }
            } else if (d_type == MANTRA) {
                if (searchPackageName(MANTRA_PACKAGE)) {
//                    String pidOptXML = getPIDOptions();
//                    String pidOptXML = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"0\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>";
                    val pidOptXML =
                        "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                    capture(MANTRA_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
                }
            }else if (d_type == MANTRA_L1) {
                val pidOptXML =
                    "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw=\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
                capture(MANTRA_L1_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
            }  else if (d_type == MORPHO) {
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

    private val startForFaceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                if (data != null) {
                    val bundle = data.extras
                    if (bundle != null) {
                        pidDataXML = bundle.getString("response").toString()
                        if (pidDataXML != null) {
                            captureType=FACE_CAPTURE
                            readXMLData(pidDataXML, FACE_CAPTURE)
//                            Common.showResponsePopUp(requireContext(), response)
                        }else{
//                    handleFailure()
                            Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
//                handleFailure()
                        Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
//            handleFailure()
                    Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: java.lang.Exception) {
                showMessageDialogue("Error:-" + ex.message, "EXCEPTION")
                ex.printStackTrace()
            }
        }
    }

    fun captureFaceData() {
        try {
            val intent = Intent("in.gov.uidai.rdservice.face.CAPTURE")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            intent.putExtra(
                "request",
                createPidOptionForKUA(getRandomNumber(), "P")
            )

            startForFaceResult.launch(intent)
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }
    fun getRandomNumber(): String {
        val start = 10000000
        val end = 99999999
        val number = Random(System.nanoTime()).nextInt(end - start + 1) + start
        return number.toString()
    }
    fun getWADH2(): String {
        return "sgydIC09zzy6f8Lb3xaAqzKquKe9lFcNR9uTvYxFp+A="
    }
    val LANGUAGE=""
    fun createPidOptionForKUA(txnId: String, buildType:String): String {
        return createPidOptionsKUA(txnId, "auth", getWADH2(), buildType)
    }
    private fun createPidOptionsKUA(txnId: String, purpose: String, wadh:String, buildType:String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PidOptions ver=\"1.0\" env=\"${buildType}\">\n" +
                "   <Opts fCount=\"1\" fType=\"1\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"\" otp=\"\" wadh=\"${wadh}\" posh=\"\" />\n" +
                "   <CustOpts>\n" +
                "      <Param name=\"txnId\" value=\"${txnId}\"/>\n" +
                "      <Param name=\"purpose\" value=\"$purpose\"/>\n" +
                "      <Param name=\"language\" value=\"${LANGUAGE}\"/>\n" +
                "   </CustOpts>\n" +
                "</PidOptions>"
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

        return "<?xml version=\"1.0\"?><PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\" posh=\"UNKNOWN\" env=\"P\" /><CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts></PidOptions>"
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
//        sendMobileTransaction()
        showSubmit()
//        AlertDialog.Builder(requireContext())
//            .setCancelable(false)
//            .setTitle("Please confirm your KYC")
//            .setMessage("Do you want to do KYC with given details!")
//            .setPositiveButton("CONFIRM") { dialog, which ->
//                dialog.cancel()
//                //                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show();
////                        checkPermissions();
//                sendMobileTransaction()
//            }
//            .setNegativeButton(
//                "CANCEL"
//            ) { dialog, i -> dialog.cancel() }
//            .show()
    }

    private fun sendMobileTransaction() {
//        var loginModel= MyPreferences.getLoginData(LoginModel(),context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["Mobile"] = commonParams!!.mobile
        params["AgentCode"] = commonParams!!.agentCode
        params["Mode"] = "App"
        params["MerchantId"] = ApiConstants.MerchantId
        params["lat"] = "20.593684"
        params["long"] = "78.96288"
        params["OTP"] = binding!!.otpEdt.text.toString()
        params["StateResp"] = senderDetailResponse?.senderDetailInfo?.get(0)!!.stateResp
        if(captureType==FINGER_CAPTURE){
            params["isiris"] = "false"
        }else{
            params["isiris"] = "true"
        }
/*//        pidDataXML="test"
        if (d_type.equals(MORPHO) || d_type.equals(STARTEK) || d_type.equals(MANTRA_L1)) {
//            params.put("Pid", pidDataXML.replace("\n", ""));  //.replace("\n","")
            pidDataXML=pidDataXML.replace("\n", "")
            params["Pid"]=pidDataXML
        } else {
//            params.put("Pid", ("<?xml version=\"1.0\"?>" + pidDataXML).replace("\n", ""));
            pidDataXML=("<?xml version=\"1.0\"?>$pidDataXML").replace("\n", "")
            params["Pid"]=pidDataXML
        }*/
        if (d_type.equals(MANTRA)){
            pidDataXML=("<?xml version=\"1.0\"?>$pidDataXML").replace("\n", "")
            params["Pid"]=pidDataXML
        }else{
            pidDataXML=pidDataXML.replace("\n", "")
            params["Pid"]=pidDataXML
        }

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_RAPIPAY).create(ApiInterface::class.java)
        val call = apiService.getDmt2HeaderMap(ApiConstants.registerRemitter, params, commonParams!!.userData, "Bearer "+commonParams!!.token)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
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
            val senderResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode.equals("00", ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    bundle.putSerializable("commonParams", commonParams)
                    val senderDetailFragment = Dmt2SenderDetailFragment()
                    senderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity).replaceFragmentWithTagNoBackStack(
                        senderDetailFragment,
                        FragmentTags.dmt2SenderDetailFragment
                    )
                }
                else {
                    showMessageDialogue(senderResponse.statusMessage, "Response Message")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Oops! Something went wrong in response.\n"+e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchPackageName(mPackageName: String): Boolean {
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

    private fun showCapture() {
        binding!!.validateAdhar.setEnabled(true)
        binding!!.validateAdhar.setTextColor(resources.getColor(R.color.color_white,null))
        binding!!.validateAdhar.setBackgroundResource(R.drawable.button_shep)
        binding!!.validateAdhar.setAlpha(1f)
    }

    private fun hideCapture() {
        binding!!.validateAdhar.setEnabled(false);
        binding!!.validateAdhar.setTextColor(getResources().getColor(R.color.black_text_color));
        binding!!.validateAdhar.setBackgroundResource(R.color.gray_color);
        binding!!.validateAdhar.setAlpha(0.4f);
    }

    private fun showSubmit() {
        binding!!.btnSubmit.setEnabled(true)
        binding!!.btnSubmit.setTextColor(resources.getColor(R.color.color_white,null))
        binding!!.btnSubmit.setBackgroundResource(R.drawable.button_shep)
        binding!!.btnSubmit.setAlpha(1f)
        binding!!.otp.visibility=View.VISIBLE
    }

    private fun hideSubmit() {
        binding!!.btnSubmit.setEnabled(false);
        binding!!.btnSubmit.setTextColor(getResources().getColor(R.color.black_text_color));
        binding!!.btnSubmit.setBackgroundResource(R.color.gray_color);
        binding!!.btnSubmit.setAlpha(0.4f);
        binding!!.otp.visibility=View.INVISIBLE
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: SenderDetailResponse, param2: CommonParams) =
            Dmt2KycFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                }
            }
    }

    var responseDataAdhar="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"aadhar validated successfully and otp has been sent to register remitter for registration.\",\n" +
            "    \"sessionRefId\": null,\n" +
            "    \"bank1\": 0,\n" +
            "    \"type1\": null,\n" +
            "    \"bank2\": 0,\n" +
            "    \"type2\": null,\n" +
            "    \"bank3\": 0,\n" +
            "    \"type3\": null,\n" +
            "    \"sessionKey\": null,\n" +
            "    \"requestFor\": null,\n" +
            "    \"remainingLimit\": 0,\n" +
            "    \"senderDetailInfo\": [\n" +
            "        {\n" +
            "            \"dob\": \"N/A\",\n" +
            "            \"gender\": \"Male\",\n" +
            "            \"mobile\": \"9389173616\",\n" +
            "            \"name\": \"N/A\",\n" +
            "            \"pin\": \"N/A\",\n" +
            "            \"city\": \"N/A\",\n" +
            "            \"state\": \"N/A\",\n" +
            "            \"stateResp\": \"42be5cb2-fadf-4398-9c94-ba06c4c34538\",\n" +
            "            \"ekyc_id\": \"\",\n" +
            "            \"limit\": null\n" +
            "        }\n" +
            "    ],\n" +
            "    \"benificiaryDetailData\": null\n" +
            "}"
}