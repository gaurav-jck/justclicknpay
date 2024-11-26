package com.justclick.clicknbook.network

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.model.DepositRequestResponseModel
import com.justclick.clicknbook.model.SaveLogsRequest
import okhttp3.ResponseBody
import java.net.InetAddress

class SaveLogs {

    private var context:Context?=null
    companion object{
         val LOGIN="Login"
         val ChangePassword="ChangePassword"
         val ForgotPassword="ForgotPassword"
    }
    fun saveLogs(context: Context, doneCard: String, action:String){
//        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        // Method to get the current connection info
//        val wInfo = wifiManager.connectionInfo
//
//        // Extracting the information from the received connection info
//        val ipAddress = Formatter.formatIpAddress(wInfo.ipAddress)
////        val ipAddress2 =  InetAddress().hostAddress
        var ipAddress3="11.11.0.0"
        val thread = Thread {
            try {
                // Your code goes here
                ipAddress3 =  InetAddress. getLocalHost().hostAddress
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

//        val ipAddress4 =  ipAddress3

        var saveLogsRequest:SaveLogsRequest= SaveLogsRequest()
        saveLogsRequest.donecarduser=doneCard
        saveLogsRequest.action=action
        saveLogsRequest.browser="APP"
        saveLogsRequest.ipaddress=ipAddress3
        this.context=context
        var json = Gson().toJson(saveLogsRequest);
//                showCustomDialog()
        NetworkCall().callService(
            NetworkCall.getAccountStmtApiInterface()
                .accountStmtPost(ApiConstants.savelogs, saveLogsRequest),
            context, false,
        ) { response, responseCode ->
//            if (response != null) {
//                responseHandler(response)
//            } else {
////                        hideCustomDialog()
//                Toast.makeText(context, "Response failure", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val common = Gson().fromJson(response.string(), DepositRequestResponseModel::class.java)
            if (common != null) {
//                Toast.makeText(context, common.depositRequestResult.Status, Toast.LENGTH_LONG).show()
            } else {
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){

        }
    }
}