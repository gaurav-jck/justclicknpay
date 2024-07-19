package com.justclick.clicknbook.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Constants;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by gaurav.singhal on 8/11/2017.
 */

public class NetworkCall {
    private final int SUCCESS_CODE_200=200;
    ResponseBody responseBody;
    Context context;
    RetrofitResponseListener retrofitResponseListener;

    public void callService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString();
        showCustomDialog();
        ApiInterface service = APIClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.checkTrainPnrCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
//                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callMobileService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
//        if(isDialog){
//            showCustomDialog();}
        ApiInterface service = APIClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.getMobileCommonData(methodName,model);
//        Call<ResponseBody> call = service.testUatService(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }


    public void callFormMobileService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = "RequestData:"+new Gson().toJson(model)+"";
        String json2 = new Gson().toJson(model)+"";

        File file = new File("");
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imageBody = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        MediaType mediaTypeData = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody dataBody = RequestBody.create(mediaTypeData, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: " +
                "form-data; name=\"RequestData\"\r\n\r\n"+new Gson().toJson(model)+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        ApiInterface service = APIClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = service.getMobileFormCommonData(dataBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public String getFormDataFromServer(Context context, String page, String method, Object object,
                                        ByteArrayOutputStream networkOutputStream, File fileToSend, Uri fileUri, String encodedImage){
        try {
//            new Gson().toJson(model)
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            String url = ApiConstants.BASE_URL + page + method;
            URL obj = new URL(url);
            OkHttpClient client = new OkHttpClient();
            MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
//            MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            MediaType mediaType = MediaType.parse("multipart/form-data; boundary="+boundary);
          /*  RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: " +
                    "form-data; name=\"RequestData\"\r\n\r\n"+new Gson().toJson(object)+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--"+
                    "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: " +
                    "form-data; name=\"Image\"\r\n\r\n"+networkOutputStream+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
            RequestBody body2 = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: " +
                    "form-data; name=\"RequestData\"\r\n\r\n"+new Gson().toJson(object)+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--"+
                    "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: " +
                    "form-data; name=\"Image\"\r\n\r\n"+encodedImage+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");*/
//            RequestBody body3 = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"RequestData\"\r\n\r\n" +
//                    new Gson().toJson(object)+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
//                    "Content-Disposition: form-data; name=\"Image\""+encodedImage+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
            Bitmap bm = BitmapFactory.decodeFile(encodedImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos); // bm is the bitmap object
            byte[] b = baos.toByteArray();

            String encodedImage2 = Base64.encodeToString(b, Base64.DEFAULT);


            String str = twoHyphens + boundary + lineEnd;
            String str2 = "Content-Disposition: form-data; name=\"RequestData\"";
            String str3 = "Content-Type: application/json";
            String str4 = "Content-Disposition: form-data; name=\"Image\"";
            String str5 = "Content-Type: image/jpeg";
            String str6 = twoHyphens + boundary + twoHyphens;



            String StrTotal = str + str2 + "\r\n" + /*str3 + "\r\n" +"\r\n" +*/ new Gson().toJson(object) + "\r\n" + str
                    + str4 + "\r\n" + str5 + "\r\n"+"\r\n"+ encodedImage2 + "\r\n" + str6;

            RequestBody body4 = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"RequestData\"\r\n\r\n" +
                    new Gson().toJson(object)+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n");
            RequestBody body5 = RequestBody.create(mediaType, StrTotal);
            RequestBody body3 = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"RequestData\"\r\n\r\n" +
                    new Gson().toJson(object)+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; " +
                    "name=\"Image\"; filename="+encodedImage+"\r\nContent-Type: image/jpeg\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
//RequestBody body5 = RequestBody.create(mediaType, "\"------WebKitFormBoundary7MA4YWxkTrZu0gW\\r\\nContent-Disposition: form-data; name=\\\"RequestData\\\"\\r\\n\\r\\n" +
//        new Gson().toJson(object)+"\\r\\n------WebKitFormBoundary7MA4YWxkTrZu0gW\\r\\nContent-Disposition: form-data; name=\\\"Image\"\\r\\n\\r\\n\"+new Gson().toJson(object)+\"\\n\\r\\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
//Content-Disposition: form-data; name=\"Image\"; filename=\"D:\\JCT Designs\\color images\\icon2.png\"\r\nContent-Type: image/png\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--
            File file = new File(encodedImage);

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(context.getContentResolver().getType(fileUri)),
                            file
                    );
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body2 =
                    MultipartBody.Part.createFormData("Image", file.getName(), requestFile);

            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
                    .setType(mediaType)
//                    .addFormDataPart("RequestData", new Gson().toJson(object))
                    .addFormDataPart("RequestData", "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition:" +
                            "form-data; name=RequestData\r\n\r\n"+new Gson().toJson(object)+"\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
                    .addFormDataPart("Image", fileToSend.getName() ,
                            requestFile)
//                    .addPart(body4)
//                    .addPart(body2)
                    .build();


         /*   val file = File(image)
            val MEDIA_TYPE_JPEG : MediaType? = MediaType.parse("image/jpeg")
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uri", image, RequestBody.create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("name", image)
                    .build()*/

            /*  "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data;" +

                            " name=\"Image\"\r\n\r\n"+
                            " filename=\"" +
                    object+"\r\nContent-Type: image/jpeg"+*/

            /*{
"BankName" : "AXIS BANK",
"EmpName" : "ankur sharma",
"MobileNumber" : "9599171731",
"ReceiptNo" : "1234",
"Remarks" : "test",
"TotalAmount" : "10",
"TransactionDate" : "20180910",
"TypeOfAmount" : "Cash",
"DeviceId" : "7dbf315e44bc1d42",
"DoneCardUser" : "JC0A13387",
"LoginSessionId" : "NHBG8Fc1xtZ2f2/gNXJ5fPh0SYp1R/gjhh0DTgmVMNo="
}*/
            Request request = new Request.Builder()
                    .url("http://uatms.justclickkaro.com/MobileServices.svc/AgentDepositRequest")
                    .post(body3)
//                    .post(requestBody)
                    .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "5fdcfa09-a63a-d836-9ab8-8dc961db4fb9")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            //print result
//            System.out.println(response.body().string());

//            String response="";
////process the stream and store it in StringBuilder
//            while(inStream.hasNextLine())
//                response+=(inStream.nextLine());
//            hideCustomDialog();
            return response.body().string();

        }catch (Exception e){
            hideCustomDialog();
            e.printStackTrace();
        }
        return null;
    }


    public void callRblLongTimeService(Object model, String methodName, final Context context, RetrofitResponseListener listener, boolean isDialog) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString();
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClientRbl().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRblCommonData(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(response.body(),0);
                }catch (Exception e){
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRblShortTimeService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        showCustomDialog();
        ApiInterface service = APIClient.getClientRbl().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRblCommonData(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(response.body(),0);
                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callAirService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
//        if(isDialog){
//            showCustomDialog();}
        ApiInterface service = APIClient.getClientAir().create(ApiInterface.class);
        Call<ResponseBody> call = service.getFlightCityPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callFlightPostService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        String json = new Gson().toJson(model).toString();
        this.context=context;
        retrofitResponseListener= listener;
        ApiInterface service = APIClient.getFlightClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.flightPostService(methodName,model,"");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
//                    hideCustomDialog();
                    if(response.code()==SUCCESS_CODE_200){
                        retrofitResponseListener.onRetrofitResponse(response.body(),response.code());
                    }else {
                        retrofitResponseListener.onRetrofitResponse(response.errorBody(),response.code());
                    }
                }catch (Exception e){
                    responseBody=null;
//                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void getServiceWithHeader(String url, String token, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        ApiInterface service = APIClient.getLoginClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.getProfileDetails(url, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    if(response.code()==SUCCESS_CODE_200){
                        retrofitResponseListener.onRetrofitResponse(response.body(),response.code());
                    }else{
                        responseBody=response.errorBody();
                        retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                    }
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void deleteServiceWithHeader(String url, String token, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        ApiInterface service = APIClient.getLoginClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.deleteWithHeader(url, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    if(response.code()==SUCCESS_CODE_200){
                        retrofitResponseListener.onRetrofitResponse(response.body(),response.code());
                    }else{
                        responseBody=response.errorBody();
                        retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                    }
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void checkHotelTransactions(String url, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        ApiInterface service = APIClient.getHotelBookingClient().create(ApiInterface.class);

        Call<ResponseBody> call = service.getHotelTransaction(url,
                MyPreferences.getLoginToken(context));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    if(response.code()==SUCCESS_CODE_200){
                        responseBody=response.body();
                    }else{
                        responseBody=response.errorBody();
                    }
                    retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void getWithUrlHeaderFlight(String url, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        ApiInterface service = APIClient.getFlightClient().create(ApiInterface.class);

        Call<ResponseBody> call = service.getFlightWithHeader(url,
                MyPreferences.getLoginToken(context));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    if(response.code()==SUCCESS_CODE_200){
                        responseBody=response.body();
                    }else {
                        responseBody=response.errorBody();
//                        Toast.makeText(context, response.code()+"", Toast.LENGTH_SHORT).show();
                    }
                    retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callBusService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
//        showCustomDialog();
        ApiInterface service = APIClient.getClientBus().create(ApiInterface.class);
        Call<ResponseBody> call = service.getBusCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callJctMoneyService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getClientJctMoney().create(ApiInterface.class);
        Call<ResponseBody> call = service.getJctMoneyCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callJctMoneyTxnListService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString();
        ApiInterface service = APIClient.getClientJctMoney().create(ApiInterface.class);
        Call<ResponseBody> call = service.getJctMoneyCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
//                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callFinoService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
//        Toast.makeText(context, json, Toast.LENGTH_LONG).show();
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getClientJctMoney().create(ApiInterface.class);
        Call<ResponseBody> call = service.getFinoCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callAepsService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString(); // {"jctUserId":"JC0A13387"}
        ApiInterface service = APIClient.getAepsClient().create(ApiInterface.class);
        Call<ResponseBody> call = service.aepsPostService(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
//                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callAepsServiceNew(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString();
//        ApiInterface service = APIClient.getAepsClientNew().create(ApiInterface.class);
//        Call<ResponseBody> call = service.aepsPostServiceNew(methodName,model);
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_AEPS_N).create(ApiInterface.class);
        Call<ResponseBody> call = service.aepsPostServiceN(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
//                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callAepsServiceHeaderNew(Object model, String methodName, final Context context,
                                         RetrofitResponseListener listener, String userData,String token) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getAepsClientNew().create(ApiInterface.class);
        Call<ResponseBody> call = service.getAepsWithHeaderNew(methodName,model,userData,"Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRapipayService(Object model, String methodName, final Context context,boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClientRapipay().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRapipayCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRapipayServiceHeader(Object model, String methodName, final Context context,
                                         RetrofitResponseListener listener, String userData,String token) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getClientRapipay().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRapipayWithHeader(methodName,model,userData,"Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void testHeader(Object model, String methodName, final Context context,
                           RetrofitResponseListener listener, String userData,String token) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getClient("https://dev.shegastage.com/").create(ApiInterface.class);
        Call<ResponseBody> call = service.testHeader("https://dev.shegastage.com/rest/V1/products/555  Soap","Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    //    Aeps New
    public void callAepsServiceN(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model).toString();
        ApiInterface service = APIClient.getClient(URLs.BASE_URL_N).create(ApiInterface.class);
        Call<ResponseBody> call = service.aepsPostServiceN(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
//                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
//                retrofitResponseListener.onRetrofitResponse(responseBody,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callPayoutService(Object model, String methodName, final Context context,boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_PAYOUT).create(ApiInterface.class);
        Call<ResponseBody> call = service.getPayoutPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callPayoutServiceHeader(Object model, String methodName, final Context context,
                                        RetrofitResponseListener listener, String userData,String token, boolean isDialog) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_PAYOUT).create(ApiInterface.class);
        Call<ResponseBody> call = service.getPayoutWithHeader(methodName,model,userData,"Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callPayoutTxnServiceHeader(Object model, String methodName, final Context context,
                                           RetrofitResponseListener listener, String userData,String token, boolean isDialog) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_PAYOUT).create(ApiInterface.class);
        Call<ResponseBody> call = service.getPayoutTxnWithHeader(methodName,model,userData,"Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRapipayMatmService(Object model, String methodName, final Context context, RetrofitResponseListener listener,boolean isDialog) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog) showCustomDialog();
        ApiInterface service = APIClient.getClientRapipayMatm().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRapipayMatmCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRapipayMatmListService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        ApiInterface service = APIClient.getClientRapipayMatm().create(ApiInterface.class);
        Call<ResponseBody> call = service.getRapipayMatmCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callTrainServiceGet(String methodName, String param1, String param2, String param3,
                                    final Context context, CodeEnum type, String doneCard, String userType,boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClientTrain().create(ApiInterface.class);
        Call<ResponseBody> call;
        if(type.equals(CodeEnum.TrainSearch)){
            call = service.getTrainGet(methodName,param1,param2,param3, doneCard, userType, ApiConstants.MerchantId, "App");
        }else {
            call = service.getTrainRouteGet(methodName,param1,param2,param3, doneCard, userType, ApiConstants.MerchantId, "App");
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callTrainServiceFinalGet(String methodName, String jckId, String pnr, String uid,
                                         final Context context, String doneCard, String userType,boolean isDialog,
                                         RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClientTrain().create(ApiInterface.class);
        Call<ResponseBody> call;
        call = service.finalBookResponseTrain(methodName,jckId,pnr,uid, doneCard, userType, ApiConstants.MerchantId, "App");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callTrainStationServiceGet(String methodName, String param1,
                                           final Context context, boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient("https://api.railyatri.in/").create(ApiInterface.class);
        Call<ResponseBody> call=service.getStation(methodName,param1,true);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callService(Call<ResponseBody> responseBodyCall,
                            final Context context, boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callServiceWithError(Call<ResponseBody> responseBodyCall,
                            final Context context, boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
                    if(response.code()==401 || response.code()==400) {
                        responseBody=response.errorBody();
                    }else if(response.code()==500){
                        Toast.makeText(context, "Internal server error, api is not currently working.", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 200) {
                        responseBody=response.body();
                    }else {
                        responseBody=response.body();
                    }
                    retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callServiceWithoutDialog(Call<ResponseBody> responseBodyCall,
                            final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,response.code());
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callRailService(Call<ResponseBody> responseBodyCall,
                                final Context context, boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClientTrain().create(ApiInterface.class);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callLicService(Object model, String methodName, final Context context,String userData, String token,
                               boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_LIC).create(ApiInterface.class);
        Call<ResponseBody> call = service.getLicCommonPost(methodName,model,userData,token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callLicServicePaytm(Object model, String methodName, final Context context,String userData, String token,
                                    boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_PAYTM).create(ApiInterface.class);
        Call<ResponseBody> call = service.getLicCommonPost(methodName,model,userData,token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callBillPayService(Object model, String methodName, final Context context,String userData, String token,
                                   boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
        ApiInterface service;
//        if(userData.length()==0){
        service = APIClient.getClient(ApiConstants.BASE_URL_BILLPAY).create(ApiInterface.class);
//        }else {
//            service = APIClient.getClient("https://uatrechargepay.justclicknpay.com/").create(ApiInterface.class);
//        }
//        ApiInterface service = APIClient.getClient(ApiConstants.BASE_URL_BILLPAY).create(ApiInterface.class);
        Call<ResponseBody> call = service.getBillPayCommonPost(methodName,model,userData,token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callPaytmService(Object model, String methodName, final Context context,String userData, String token,
                                 boolean isDialog, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        if(isDialog){
            showCustomDialog();}
//        ApiInterface service= APIClient.getClient(ApiConstants.BASE_URL_BILLPAY).create(ApiInterface.class);
        ApiInterface service= APIClient.getClient(ApiConstants.BASE_URL_PAYTM).create(ApiInterface.class);

        Call<ResponseBody> call = service.getPaytmCommonPostNew(methodName,model,userData,"Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void callInsuranceService(Object model, String methodName, final Context context, RetrofitResponseListener listener) {
        this.context=context;
        retrofitResponseListener= listener;
        String json = new Gson().toJson(model);
        String toString=model.toString(); //className@110010
        showCustomDialog();
        ApiInterface service = APIClient.getClientInsurance().create(ApiInterface.class);
        Call<ResponseBody> call = service.getInsuranceCommonPost(methodName,model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    hideCustomDialog();
//                    if(response.code()==401) {
//                        JsonObject object = new JsonObject(response.body().toString());
//                        String body=object.getString("msgDesc");
//                    }
                    responseBody=response.body();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                }catch (Exception e){
                    responseBody=null;
                    hideCustomDialog();
                    retrofitResponseListener.onRetrofitResponse(responseBody,0);
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responseBody=null;
                retrofitResponseListener.onRetrofitResponse(null,0);
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    public static String getDataFromServer(String page, String method, Object request){
        try {
            String charset = "UTF-8";

            String url = ApiConstants.BASE_URL_RBL + page + method;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestProperty("Accept","application/json");
            con.setReadTimeout(150000);
            con.setConnectTimeout(150000);
            con.connect();
//            con.setRequestProperty("User-Agent", USER_AGENT);
//            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            DataOutputStream os = new DataOutputStream(con.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(new Gson().toJson(request));
            os.flush();
            os.close();

            // handle issues
            int statusCode = con.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
                return null;
            }
            //start listening to the stream
            Scanner inStream = new Scanner(con.getInputStream());

            String response="";
//process the stream and store it in StringBuilder
            while(inStream.hasNextLine())
                response+=(inStream.nextLine());

            return response;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String uploadFileToServer(String method, String fileName, ByteArrayOutputStream data){
        try {
            String url=ApiConstants.BASE_URL+ApiConstants.MobilePage+method+"?fileName="+fileName;
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, data.toByteArray());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "ef7db86d-2f62-dc6a-df0a-dc2008abd02b")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String getPostDataString(JSONObject request) throws Exception{
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = request.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = request.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public static ApiInterface getTrainApiInterface(){
        return APIClient.getClientTrain().create(ApiInterface.class);
    }

    public static ApiInterface getCashFreeQRApiInterfaceOld(){
        return APIClient.getClient(ApiConstants.BASE_URL_PAYOUT).create(ApiInterface.class);   //old qr code
//        return APIClient.getClient(ApiConstants.BASE_URL_QR_old).create(ApiInterface.class);  // new changes
    }
    public static ApiInterface getCashFreeQRApiInterface(){
//        return APIClient.getClient(ApiConstants.BASE_URL_PAYOUT).create(ApiInterface.class);   //old qr code
        return APIClient.getClient(ApiConstants.BASE_URL_QR).create(ApiInterface.class);  // new changes
    }

    public static ApiInterface getPayoutNewApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_PAYOUT_NEW).create(ApiInterface.class);  // new changes
    }
    public static ApiInterface getRegisterApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_PAYOUT_NEW).create(ApiInterface.class);  // new changes
    }

    public static ApiInterface getFastTagApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_BILLPAY).create(ApiInterface.class);
    }

    public static ApiInterface getMATMApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_RAPIPAY_MATM).create(ApiInterface.class);
    }

    public static ApiInterface getCreditCardApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_CREDIT).create(ApiInterface.class);
    }

    public static ApiInterface getForgetPassApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_FORGET).create(ApiInterface.class);
    }

    public static ApiInterface getAccountStmtApiInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_STMT).create(ApiInterface.class);
    }

    public static ApiInterface getDepositRequestInterface(){
        return APIClient.getClient(ApiConstants.BASE_URL_UAT_DEPOSIT).create(ApiInterface.class);
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    public interface RetrofitResponseListener{
        void onRetrofitResponse(ResponseBody response, int responseCode);
    }
}
