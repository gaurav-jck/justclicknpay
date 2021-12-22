package com.justclick.clicknbook.jctPayment.newaeps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.text.pdf.PdfFileSpecification.url
import com.justclick.clicknbook.R
import kotlinx.android.synthetic.main.activity_aeps_webview.*


class AepsWebviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aeps_webview)
        back_arrow.setOnClickListener {
            finish()
        }
        webView.webViewClient=MyWebViewClient(this)
        webView.settings.javaScriptEnabled=true
        webView.settings.loadWithOverviewMode=true
        webView.settings.useWideViewPort=true
        webView.settings.pluginState=WebSettings.PluginState.ON
        webView.settings.allowFileAccess=true

        webView.webChromeClient=WebChromeClient()
//        webView.settings.useWideViewPort=true
        try{
            webView.loadUrl(intent.getStringExtra("URL").toString())
//            webView.loadUrl("https://www.journaldev.com")
        }catch (e:Exception){

        }

    }

    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(webView: WebView?, request: WebResourceRequest?): Boolean {
            val url: String = request?.url.toString();
            webView?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}
