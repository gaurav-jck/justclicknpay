package com.justclick.clicknbook.retrofit;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

//    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    private final static long NORMAL_SERVICE_TIME =60;
    private final static long MEDIUM_SERVICE_TIME =90;
    private final static long RBL_SERVICE_TIME =140;
    private final static long HOTEL_SERVICE_TIME =120;
    private final static long HOTEL_AVAIL_SERVICE_TIME =120;
    private final static long SMALL_SERVICE_TIME =60;

    public static Retrofit getClient() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    public static Retrofit getClientBus() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getLoginClient() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_LOGIN)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().
                    connectTimeout(SMALL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(SMALL_SERVICE_TIME, TimeUnit.SECONDS).
                    build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_LOGIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientAir() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getHotelBookingClient() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_HOTELS_BOOKING)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(SMALL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(SMALL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_HOTELS_BOOKING)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getFlightClient() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.FLIGHT_BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.FLIGHT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientRbl() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_RBL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(RBL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(RBL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_RBL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientJctMoney() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_JCT_MONEY)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(RBL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_JCT_MONEY)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    public static Retrofit getClientHotel() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(HOTEL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(HOTEL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientHotelAvail() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(HOTEL_AVAIL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(HOTEL_AVAIL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAepsClient() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(URLs.BASE_URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAepsClientNew() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(URLs.BASE_URL_NEW)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL_NEW)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientRapipay() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_RAPIPAY)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_RAPIPAY)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient(String URL) {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(URL)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientRapipayMatm() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_RAPIPAY_MATM)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_RAPIPAY_MATM)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientTrain() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_TRAIN)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_TRAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientInsurance() {
        if(retrofit!=null && !retrofit.baseUrl().toString().equals(ApiConstants.BASE_URL_INSURANCE)){
            retrofit=null;
        }
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(NORMAL_SERVICE_TIME, TimeUnit.SECONDS).
                    readTimeout(MEDIUM_SERVICE_TIME, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL_INSURANCE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }


}