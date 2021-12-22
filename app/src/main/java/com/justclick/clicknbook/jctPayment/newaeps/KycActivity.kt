package com.justclick.clicknbook.jctPayment.newaeps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.MyPreferences
import com.paysprint.onboardinglib.activities.HostActivity

class KycActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnSubmit: Button
    private val logTag = "mockMainActivity"
//    private val partnerKey = "UFMwMDMxYTFj"
    private val partnerKey = "UFMwMDY4YTEyODZiZmExZWVmYzVhNTQ1MDJjYTBhN2YxNjYwNjk="
//    private val partnerId = "PS0031"
    private val partnerId = "PS0068"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Aeps)
        setContentView(R.layout.activity_kyc)
        initView();
    }

    private fun initView() {
        btnSubmit = findViewById(R.id.btnSubmit)
        setListeners()
    }

    private fun setListeners() {
        btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        val loginModel = MyPreferences.getLoginData(LoginModel(), applicationContext)
        val intent = Intent(applicationContext, HostActivity::class.java)
//        val intent = Intent(applicationContext, KycActivity::class.java)
        intent.putExtra("pId", partnerId)
        intent.putExtra("pApiKey", partnerKey)
//        intent.putExtra("mCode",loginModel.Data.DoneCardUser);
//        intent.putExtra("mCode",loginModel.Data.DoneCardUser);
        intent.putExtra("mCode", "JCG0125")
//        intent.putExtra("mobile", loginModel.Data.Mobile)
        intent.putExtra("mobile", "9012345688")
        intent.putExtra("lat", "42.10")
        intent.putExtra("lng", "76.00")
        intent.putExtra("firm", "JustClick")
        intent.putExtra("email", loginModel.Data.Email)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, 999)

    }

    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999) {
            if (resultCode == Activity.RESULT_OK) {
                val status = data?.getBooleanExtra("status", false)
                val response = data?.getIntExtra("response", 0)
                val message = data?.getStringExtra("message")

                val detailedResponse = "Status: $status,  " +
                        "Response: $response, " +
                        "Message: $message "

//                Toast.makeText(applicationContext, detailedResponse, Toast.LENGTH_LONG).show()

                btnSubmit.snack(detailedResponse)
                Log.i(logTag, detailedResponse)
            }
        }
    }
}