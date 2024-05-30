package com.justclick.clicknbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.newaeps.AepsWebviewActivity;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

public class AirWebviewActivity extends AppCompatActivity {

    private WebView webView;
    Context mContext;
    private String homeUrl="https://travel.justclicknpay.com/Flight/Search";
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_webview);
        mContext=this;
        String url=getIntent().getStringExtra("url");

        webView=findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new MyWebviewClient());
        webView.loadUrl(url);
        MyCustomDialog.showCustomDialog(mContext, "Loading..");

    }

    @Override
    public void onBackPressed() {
        /*if(currentUrl.equals(homeUrl)){
            finish();
        } else*/ if(webView!= null && webView.canGoBack())
            webView.goBack();// if there is previous page open it
        else
            super.onBackPressed();//if there is no previous page, close app
    }

    class MyWebviewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            currentUrl=url;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            MyCustomDialog.hideCustomDialog();
        }
    }


    public static class AirSessionRequest{
        public String MemberCode, UserName, IPAddress="103.139.75.200", Mode="App", Sessionid;
    }

    public static void airSession(Context context) {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        AirSessionRequest request=new AirSessionRequest();
        request.MemberCode=loginModel.Data.DoneCardUser;
        request.UserName=MyPreferences.getLoginId(context);
        request.Sessionid=loginModel.LoginSessionId;
        MyCustomDialog.showCustomDialog(context, "Validating, please wait..");
        new NetworkCall().callMobileService(request, ApiConstants.TRAVELSESSION, context,
                (response, responseCode) -> {
            MyCustomDialog.hideCustomDialog();
                    if(response!=null){
                        try {
                            String url=response.string().replace("\"","").replace("\\", "");
                            if(!url.isEmpty() && url.contains("https")){
                                Intent intent=new Intent(context, AirWebviewActivity.class);
                                intent.putExtra("url", url);
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}