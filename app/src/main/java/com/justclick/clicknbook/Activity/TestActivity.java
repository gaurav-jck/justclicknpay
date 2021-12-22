package com.justclick.clicknbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;

import okhttp3.ResponseBody;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.clickBtn).setOnClickListener(v -> {
            callApi();
        });
    }


    private void callApi() {
        String token="dm7vfuhv5diczbd9n4qhggn105d8vzj6";
        String request="{\"currentPage\":2,\"filter\":[{\"attribute_code\":\"category_id\",\n" +
                "\"value\":\"85\"}],\"pageSize\":8,\"querytxt\":\"\",\"sortBy\":{},\n" +
                "\"token\":\"Bearer dm7vfuhv5diczbd9n4qhggn105d8vzj6\"}";
//{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
        new NetworkCall().testHeader(request, ApiConstants.SenderDetail, this,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
//                            handle response
                        }else {
                            Toast.makeText(TestActivity.this, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, "", token);
    }
}