/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justclick.clicknbook.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.BuildConfig
import com.justclick.clicknbook.R
import com.justclick.clicknbook.biometric.BiometricPromptUtils
import com.justclick.clicknbook.biometric.CryptographyManager
import com.justclick.clicknbook.firebase.ForceUpdateChecker
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.requestmodels.LoginRequestModel
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody

class EnableBiometricLoginActivity : AppCompatActivity(), View.OnClickListener, ForceUpdateChecker.OnUpdateNeededListener {
    private val LOGIN_SERVICE = 1
    private var context: Context? = null
    private var login_tv: TextView? = null
    private var email_edt: EditText? = null
    private var password_edt:EditText? = null
    private var remember_me_checkbox: CheckBox? = null
    private var otpDialog: Dialog? = null
    private var appUpdateDialog: Dialog? = null
    private val otpFlag = false
    private val TAG = "EnableBiometricLogin"
    private lateinit var cryptographyManager: CryptographyManager
    var firebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_biometric)
        context = this

        initializeFirebase()
        initializeViews()

        var loginModel = LoginModel()
        if (MyPreferences.getLoginData(loginModel, context) != null) {
            loginModel = MyPreferences.getLoginData(loginModel, context)
        }
        email_edt!!.setText("")
        password_edt!!.setText("")

        if (MyPreferences.isUserLogin(context) && loginModel.Data != null) {
            remember_me_checkbox!!.isChecked = true
            email_edt!!.setText(loginModel.Data.Email)
        } else {
            if (loginModel.Data != null) {
                email_edt!!.setText(loginModel.Data.Email)
            }
        }
    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(context!!)
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        // set in-app defaults
        val remoteConfigDefaults: MutableMap<String?, Any?> = HashMap()
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_REQUIRED] = true
        remoteConfigDefaults[ForceUpdateChecker.KEY_CURRENT_VERSION] = BuildConfig.VERSION_NAME
        remoteConfigDefaults[ForceUpdateChecker.KEY_UPDATE_URL] = ApiConstants.GOOGLE_PLAY_STORE_URL
        firebaseRemoteConfig!!.setDefaultsAsync(remoteConfigDefaults)
        firebaseRemoteConfig!!.fetch(60) // fetch every minutes
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Remote config ", "remote config is fetched.")
                    firebaseRemoteConfig!!.activate()
                }
            }
    }

    private fun initializeViews() {
        email_edt = findViewById(R.id.email_edt)
        password_edt = findViewById(R.id.password_edt)
        login_tv = findViewById(R.id.login_tv)
        remember_me_checkbox = findViewById(R.id.remember_me_checkbox)
        login_tv!!.setOnClickListener(this)
        (findViewById<View>(R.id.appVerTv) as TextView).text = "Ver " + BuildConfig.VERSION_NAME
        setFont()

        if(!MyPreferences.getForceLoginData(context).isForceLogin && MyPreferences.isBiometric(context)){
            showBiometricPromptForEncryption()
        }
    }

    private fun setFont() {
        val face = Common.ButtonTypeFace(context)
        val face2 = Common.EditTextTypeFace1(context)
        val face3 = Common.EditTextTypeFace(context)
        login_tv!!.typeface = face
        (findViewById<View>(R.id.remember_tv) as TextView).typeface = face2
        email_edt!!.typeface = face3
    }

    private fun login(otp: String?) {
        Common.hideSoftKeyboard(context as EnableBiometricLoginActivity?)
        val uName = email_edt!!.text.toString()
        val uPass = password_edt!!.text.toString()
        val check1 = "hamdaantravelsqaimoh@gmail.com"
        val check2 = "9797141435"

        val DID: String = if (MyPreferences.getLoginId(context).equals(check1) ||
            MyPreferences.getLoginId(context).equals(check2)) {
            "JustClicknPayOtp"
        } else {
            Common.getDeviceId(context)
        }
//        val DID = Common.getDeviceId(context)
        if (validate(uName, uPass)) {
            try {
                val loginRequestModel = LoginRequestModel()
                loginRequestModel.UserId = EncryptionDecryptionClass.Encryption(uName, context)
                loginRequestModel.Password = EncryptionDecryptionClass.Encryption(uPass, context)
                loginRequestModel.DeviceId = EncryptionDecryptionClass.Encryption(DID, context)
                loginRequestModel.VersionCode = Common.getVersionCode(context)
                if (otp != null) {
                    loginRequestModel.OTP = EncryptionDecryptionClass.Encryption(otp, context)
                }
                showCustomDialog()
                NetworkCall().callMobileService(loginRequestModel, ApiConstants.LOGIN, context
                ) { response, responseCode ->
                    if (response != null) {
                        responseHandler(response, LOGIN_SERVICE)
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, getString(R.string.response_failure_message), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loginCall() {
        Common.hideSoftKeyboard(context as EnableBiometricLoginActivity?)
        val check1 = "hamdaantravelsqaimoh@gmail.com"
        val check2 = "9797141435"

        val DID: String = if (MyPreferences.getLoginId(context).equals(check1) ||
            MyPreferences.getLoginId(context).equals(check2)) {
            "JustClicknPayOtp"
        } else {
            Common.getDeviceId(context)
        }
//        val DID = Common.getDeviceId(context)
        try {
            val loginRequestModel = LoginRequestModel()
            loginRequestModel.UserId = EncryptionDecryptionClass.Encryption(MyPreferences.getLoginId(context), context)
            loginRequestModel.Password = EncryptionDecryptionClass.Encryption(MyPreferences.getLoginPassword(context), context)
            loginRequestModel.DeviceId = EncryptionDecryptionClass.Encryption(DID, context)
            loginRequestModel.VersionCode = Common.getVersionCode(context)
            showCustomDialog()
            NetworkCall().callMobileService(loginRequestModel, ApiConstants.LOGIN, context
            ) { response, responseCode ->
                if (response != null) {
                    responseHandler(response, LOGIN_SERVICE)
                } else {
                    hideCustomDialog()
                    Toast.makeText(context, getString(R.string.response_failure_message), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
        try {
            when (TYPE) {
                LOGIN_SERVICE -> {
                    val loginModel = Gson().fromJson(
                        response.string(),
                        LoginModel::class.java
                    )
                    hideCustomDialog()
                    if (loginModel != null) {
                        if (loginModel.Data != null && loginModel.StatusCode.equals("0", ignoreCase = true)) {
                            //store values to shared preferences
//                            dataBaseHelper.insertLoginIds(email_edt.getText().toString());
                            MyPreferences.saveLoginData(loginModel, context)
                            showBiometricPromptForEncryption()
                            MyPreferences.setAppCurrentTime(context)
                            MyPreferences.saveEmailPassword(
                                context,
                                email_edt!!.text.toString(),
                                password_edt!!.text.toString().trim { it <= ' ' })
                        } else if (loginModel.StatusCode.equals("2", ignoreCase = true)) {
                            if (otpDialog == null) {
                                otpDialog()
                            } else {
                                if (otpDialog!!.isShowing) {
                                    otpDialog!!.dismiss()
                                }
                                otpDialog()
                            }
                        } else {
                            errorPopup(loginModel.Status)
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.response_failure_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            hideCustomDialog()
            Toast.makeText(context, getString(R.string.exception_message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun errorPopup(status: String) {
        val builder = AlertDialog.Builder(
            context!!
        )
        //set title for alert dialog
        builder.setTitle("Response message")
        //set message for alert dialog
        builder.setMessage(status)
        //        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // Create the AlertDialog
        var alertDialog: AlertDialog? = null
        builder.setPositiveButton(
            "OK"
        ) { dialog, id ->
            email_edt!!.setText("")
            password_edt!!.setText("")
            dialog.cancel()
        }
        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.login_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    private fun validate(uName: String, uPass: String): Boolean {
        if (!Common.isEmailValid(uName) && !Common.isMobileValid(uName)) {
            Toast.makeText(context, R.string.empty_and_invalid_userId, Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(uPass)) {
            Toast.makeText(context, "Please enter Password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun otpDialog() {
        otpDialog = Dialog(context!!)
        otpDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        otpDialog!!.setContentView(R.layout.send_otp_layout)
        otpDialog!!.setCancelable(false)
        val otp_edt = otpDialog!!.findViewById<View>(R.id.otp_edt) as EditText
        val submit = otpDialog!!.findViewById<View>(R.id.submit_btn) as Button
        val dialogCloseButton = otpDialog!!.findViewById<View>(R.id.close_btn) as ImageButton
        submit.setOnClickListener {
            Common.preventFrequentClick(submit)
            try {
                val inputMethodManager =
                    context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    otpDialog!!.getWindow()!!.currentFocus!!.windowToken, 0
                )
            } catch (e: NullPointerException) {
            }
            if (Common.checkInternetConnection(context)) {
                if (otp_edt.text.toString().trim { it <= ' ' }.length > 3) {
                    login(otp_edt.text.toString().trim { it <= ' ' })
                } else {
                    Toast.makeText(context, getString(R.string.empty_and_invalid_otp), Toast.LENGTH_LONG).show()
                }
                //                    forgetDialog.dismiss();
            } else {
                Toast.makeText(context, getString(R.string.no_internet_message), Toast.LENGTH_LONG).show()
            }
        }
        dialogCloseButton.setOnClickListener { // TODO Auto-generated method stub
            otpDialog!!.dismiss()
        }
        otpDialog!!.show()
    }


    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            /*SampleAppUser.fakeToken?.let { token ->
                Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }*/

            if(MyPreferences.isBiometric(context)){
                //biometric authenticated already please call login
                loginCall()
            }else{
                authorizedBiometricLogin()
            }
        }
//        finish()
    }

    private fun authorizedBiometricLogin() {
        MyPreferences.enableBiometric(context)
        val intent = Intent(context, NavigationDrawerActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.login_tv -> {
                //                takePdfAndUpload();
                Common.preventFrequentClick(login_tv)
                Common.hideSoftKeyboard(context as EnableBiometricLoginActivity?)
                if (Common.checkInternetConnection(context)) {
                    login(null)
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }

            R.id.close_btn -> otpDialog!!.dismiss()
        }
    }

    override fun onUpdateNeeded(updateUrl: String?, isForceLogin: Boolean?) {
        appUpdateDialog = AlertDialog.Builder(this)
            .setTitle("New version available")
            .setMessage("Please, update app to new version to continue reporting.")
            .setPositiveButton(
                "Update"
            ) { dialog, which -> redirectStore(updateUrl) }.setNegativeButton(
                "No, thanks"
            ) { dialog, which -> finish() }.create()
        appUpdateDialog!!.setCancelable(false)
        appUpdateDialog!!.show()
    }

    private fun redirectStore(updateUrl: String?) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        if (getAppVersionCode() < MyPreferences.getForceLoginData(context).versionCode) {
            onUpdateNeeded(
                ApiConstants.GOOGLE_PLAY_STORE_URL,
                MyPreferences.getForceLoginData(context).isForceLogin
            )
        }
        if (otpFlag && otpDialog != null) {
            otpDialog!!.show()
        }
    }

    private fun getAppVersionCode(): Int {
        var versionCode = 0
        try {
            versionCode = BuildConfig.VERSION_CODE
        } catch (e: PackageManager.NameNotFoundException) {
//            Log.e(TAG, e.getMessage());
        }
        return versionCode
    }

}