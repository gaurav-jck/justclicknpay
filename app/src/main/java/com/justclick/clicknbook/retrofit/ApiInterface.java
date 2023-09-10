package com.justclick.clicknbook.retrofit;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse;
import com.justclick.clicknbook.Fragment.train.TrainBookFragment;
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.AirCancellationListModel;
import com.justclick.clicknbook.model.AirRefundReportModel;
import com.justclick.clicknbook.model.AirSalesReportModel;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusCityResponseModel;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusSearchModel;
import com.justclick.clicknbook.model.CheckTrainPnrResponseModel;
import com.justclick.clicknbook.model.CityNameResponseModel;
import com.justclick.clicknbook.model.CreditListForAgentModel;
import com.justclick.clicknbook.model.CreditReportListModel;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.DepositListForAgentModel;
import com.justclick.clicknbook.model.DepositReportListModel;
import com.justclick.clicknbook.model.FlightCityNameModel;
import com.justclick.clicknbook.model.ForgetPasswordModel;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.model.HotelCityListModel;
import com.justclick.clicknbook.model.HotelMoreInfoResponseModel;
import com.justclick.clicknbook.model.HotelRoomDataResponseModel;
import com.justclick.clicknbook.model.JctIfscByCodeResponse;
import com.justclick.clicknbook.model.JioAmountData;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.model.OptModelRecharge;
import com.justclick.clicknbook.model.RblAddressIFSCResponse;
import com.justclick.clicknbook.model.RblBankDetailByIFSCResponse;
import com.justclick.clicknbook.model.RblCommonResponse;
import com.justclick.clicknbook.model.RblCommonStateCityBranchResponse;
import com.justclick.clicknbook.model.RblGetSenderResponse;
import com.justclick.clicknbook.model.RblIfscByCodeResponse;
import com.justclick.clicknbook.model.RblPinDetailResponseModel;
import com.justclick.clicknbook.model.RblPrintResponse;
import com.justclick.clicknbook.model.RblRefundListResponseModel;
import com.justclick.clicknbook.model.RblRelationResponse;
import com.justclick.clicknbook.model.RblSenderRegistrationResponse;
import com.justclick.clicknbook.model.RblTransactionResponse;
import com.justclick.clicknbook.model.RechargeDetailResponseModel;
import com.justclick.clicknbook.model.RechargeListModel;
import com.justclick.clicknbook.model.RechargeModel;
import com.justclick.clicknbook.model.RegistrationModel;
import com.justclick.clicknbook.model.StateNameResponseModel;
import com.justclick.clicknbook.model.TrainRouteMapResponseModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {

    @GET("Msapp.aspx?")
    Call<LoginModel> login(@Query("mn") String mn, @Query("vl") String value);

    @POST("MobileServices.svc/{methodName}")
    Call<LoginModel> loginPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<ResponseBody> getMobileCommonData(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    @Multipart
    Call<ResponseBody> getMobileFormCommonData(@Part("Image\"; filename=\"file\" ") RequestBody file);

    @Multipart
    @POST("MobileServices.svc/{methodName}")
    Call<ResponseBody> uploadFileWithPartMap(@Path("methodName") String methodName,

                                             @Part("RequestData") RequestBody partMap,
                                             @Part MultipartBody.Part file);

//    Call<ResponseBody> getMobileFormCommonData(@Path("methodName") String method,
//                                               @Part("file\"; Image=\"pp.png\" ") RequestBody image,
//                                               @Part("RequestData") RequestBody requestData);

//  @POST("MobileServices.svc/{methodName}")
//    @FormUrlEncoded
//    Call<ResponseBody> getMobileFormCommonData(@Path("methodName") String method,@FieldMap Map<String,String> params);


    @POST("MobileServices.svc/{methodName}")
    Call<RegistrationModel> registrationPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<ResponseBody> uploadFile(@Path("methodName") String method, @Query("fileName") String fileName, @Body ByteArrayOutputStream data);

    @POST("MobileServices.svc/{methodName}")
    Call<ForgetPasswordModel> forgetPasswordPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<StateNameResponseModel> getState(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CityNameResponseModel> getCity(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<AgentNameModel> agentNamePost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<AgentDetails> agentdetailPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<RechargeModel> mobileRechargePost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<RechargeDetailResponseModel> RechargeCommissionPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<OptModel> getOptPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<JioAmountData> getJioAmount(@Path("methodName") String method);

    @POST("MobileServices.svc/{methodName}")
    Call<CreditReportListModel> getCreditReportListPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CreditListForAgentModel> getCreditListForAgentPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<DepositListForAgentModel> getDepositListForAgentPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<RechargeListModel> getRechargeListPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<AirCancellationListModel> getAirCancelListPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<AirRefundReportModel> getAirRefundReportPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<AirSalesReportModel> getAirSalesReportPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<TrainRouteMapResponseModel> trainRouteMapRequestPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CommonResponseModel> updateCreditPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CommonResponseModel> selfCreditPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CommonResponseModel> updateDepositPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CommonResponseModel> selfDepositPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<CheckTrainPnrResponseModel> checkTrainPnrPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<ResponseBody> checkTrainPnrCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("MobileServices.svc/{methodName}")
    Call<DepositReportListModel> getDepositReportListPost(@Path("methodName") String method, @Body Object data);

    @POST("apiAccounts/Accounts/{methodName}")
    Call<ResponseBody> accountStmtPost(@Path("methodName") String method, @Body Object data);


    //Rbl Services

    @POST("RBLService.svc/{methodName}")
    Call<ResponseBody> getRblCommonData(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblCommonResponse> appSession(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblGetSenderResponse> getSenderDetailPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<ArrayList<RblCommonStateCityBranchResponse>> getRblCommonStateCityPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblAddressIFSCResponse> getRblIfscDataPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblBankDetailByIFSCResponse> getBankDetailByIfscPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblSenderRegistrationResponse> rblSenderRegistrationPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblCommonResponse> rblBenDeletePost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblCommonResponse> rblBenRegistrationPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<ArrayList<RblRelationResponse>> getRblRelationPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<ArrayList<RblIfscByCodeResponse>> getRblBankNamesPost(@Path("methodName") String method);

    @POST("RBLService.svc/{methodName}")
    Call<RblCommonResponse> rblBenValidatePost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblTransactionResponse> rblTransactionPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblRefundListResponseModel> rblRefundListPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblCommonResponse> rblgetRefundPost(@Path("methodName") String method, @Body Object data);

    @POST("HotelWebService.svc/{methodName}")
    Call<HotelCityListModel> getHotelCityPost(@Path("methodName") String method, @Body Object data);

    @POST("HotelWebService.svc/{methodName}")
    Call<HotelAvailabilityResponseModel> getHotelAvailPost(@Path("methodName") String method, @Body Object data);

    @POST("HotelWebService.svc/{methodName}")
    Call<HotelMoreInfoResponseModel> getHotelMoreInfoPost(@Path("methodName") String method, @Body Object data);

    @POST("HotelWebService.svc/{methodName}")
    Call<HotelRoomDataResponseModel> getHotelRoomDataPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblPrintResponse> rblPrintPost(@Path("methodName") String method, @Body Object data);

    @POST("RBLService.svc/{methodName}")
    Call<RblPinDetailResponseModel> getPinDetail(@Path("methodName") String method, @Body Object data);


    //Bus Services
    @POST("BusService.svc/{methodName}")
    Call<BusCityResponseModel> getBusCity(@Path("methodName") String method, @Body Object data);

    @POST("BusService.svc/{methodName}")
    Call<ResponseBody> getBusCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("BusService.svc/{methodName}")
    Call<BusSearchModel> getBusListPost(@Path("methodName") String method, @Body Object data);


    // Air Service
    @GET("FlightData.aspx?")
    Call<ResponseBody> getAirCommonGet(@Query("method") String method, @Query("value") Object value);

    @POST("AirServices.svc/{methodName}")
    Call<FlightCityNameModel> getFlightCity(@Path("methodName") String method, @Body Object data);

    @DELETE()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> deleteWithHeader(@Url String url, @Header("token") String token);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getFlightWithHeader(@Url String url, @Header("token") String token);

    @GET()
    Call<ResponseBody> getProfileDetails(@Url String url, @Header("token") String token);

    @POST("AirServices.svc/{methodName}")
    Call<ResponseBody> getFlightCityPost(@Path("methodName") String method, @Body Object data);

    @POST("{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> flightPostService(@Path("methodName") String method, @Body Object data, @Header("token") String token);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> flightGetCityService(@Url String url);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getHotelTransaction(@Url String url, @Header("token") String token);


    // JctMoney Service
    @POST("api/JCTMoney/{methodName}")
    Call<ResponseBody> getJctMoneyCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("api/JCTMoney/{methodName}")
    Call<ArrayList<JctIfscByCodeResponse>> getJctBankNamesPost(@Path("methodName") String method , @Body Object data);


    //    Bus service
    @GET("BusData.aspx?")
    Call<ResponseBody> getBusCommonGet(@Query("method") String method, @Query("value") Object value);

    //    AEPS services php
    @POST("jct/{methodName}")
    Call<ResponseBody> aepsPostServicephp(@Path("methodName") String method, @Body Object data);

    //    AEPS services
    @POST("jct/{methodName}")
    Call<ResponseBody> aepsPostService(@Path("methodName") String method, @Body Object data);

    //    AEPS N services
    @POST("B2B/AdharPayment/{methodName}")
    Call<ResponseBody> aepsPostServiceN(@Path("methodName") String method, @Body Object data);

    //    AEPS services new
    @POST("b2b/AdharPayment/{methodName}")
    Call<ResponseBody> aepsPostServiceNew(@Path("methodName") String method, @Body Object data);

    @POST("b2b/AdharPayment/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getAepsWithHeaderNew(@Path("methodName") String method, @Body Object data,
                                            @Header("userData") String userData, @Header("Authorization") String token);

    @Multipart
    @POST("b2b/AdharPayment/{methodName")
    Call<ResponseBody> aepsMultipartForm(@Path("methodName") String methodName,

                                         @Part("RequestData") RequestBody partMap,
                                         @Part MultipartBody.Part file);

    //    train
    @GET("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getTrainGet(@Path("methodName") String method, @Query("FromStation") Object FromStation,
                                   @Query("ToStation") Object ToStation,@Query("Doj") Object Doj,
                                   @Header("Identifier") String doneCard, @Header("LoggedInUserType") String type,
                                   @Header("Merchant") String merchant, @Header("Mode") String mode);
    @GET("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getTrainRouteGet(@Path("methodName") String method, @Query("journeyDate") Object FromStation,
                                        @Query("trainNumber") Object ToStation,@Query("fromstation") Object Doj,
                                        @Header("Identifier") String doneCard, @Header("LoggedInUserType") String type,
                                        @Header("Merchant") String merchant, @Header("Mode") String mode);
//    startingStationCode was used in place of

    @POST("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getTrainCredentials(@Path("methodName") String method,@Header("Identifier") String doneCard,
                                           @Header("LoggedInUserType") String type,
                                           @Header("Merchant") String merchant, @Header("Mode") String mode);

    @POST("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> bookTrain(@Body Object data,@Path("methodName") String method,@Header("Identifier") String doneCard,
                                 @Header("LoggedInUserType") String type,
                                 @Header("Merchant") String merchant, @Header("Mode") String mode,
                                 @Header("Token") String token, @Header("UserData") String userData);

    @POST("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> finalBookTrain(@Body Object data,@Path("methodName") String method,@Header("Identifier") String doneCard,
                                      @Header("LoggedInUserType") String type,
                                      @Header("Merchant") String merchant, @Header("Mode") String mode,
                                      @Header("Token") String token, @Header("UserData") String userData);

    @GET("apiV1/RailEngine/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> finalBookResponseTrain(@Path("methodName") String method,@Query("JckReferenceid") String jckid,
                                              @Query("PnrNumber") String pnr,@Header("uid") String token,
                                              @Header("Identifier") String doneCard,
                                              @Header("LoggedInUserType") String type,
                                              @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET("api/{methodName}")
    Call<ResponseBody> getStation(@Path("methodName") String method, @Query("q") Object FromStation,
                                  @Query("hide_city") Object Doj);
    @GET("api/{methodName}")
    Call<ResponseBody> getStationNew(@Path("methodName") String method, @Query("City") Object FromStation,
                                     @Query("hide_city") Object Doj);

    @Headers({"Content-Type: application/json"})
    @POST("apiV1/RailEngine/{methodName}")
    Call<ResponseBody> trainFareRule(@Path("methodName") String method, @Body Object data,
                                     @Header("Identifier") String doneCard,
                                     @Header("LoggedInUserType") String type,
                                     @Header("Merchant") String merchant, @Header("Mode") String mode);

    @Headers({"Content-Type: application/json"})
    @POST("apiV1/RailEngine/{methodName}")
    Call<ResponseBody> trainCancelTicket(@Path("methodName") String method, @Body Object data,
                                         @Header("Identifier") String doneCard,
                                         @Header("LoggedInUserType") String type,
                                         @Header("Merchant") String merchant, @Header("Mode") String mode);
    @Headers({"Content-Type: application/json"})
    @POST("apiV1/RailEngine/{methodName}")
    Call<ResponseBody> trainRefundVerifyOtp(@Path("methodName") String method, @Body Object data,
                                            @Header("Identifier") String doneCard,
                                            @Header("LoggedInUserType") String type,
                                            @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<TrainBookFragment.BoardingStnResponse> getBoardingStn(@Url String url,
                                                               @Header("Identifier") String doneCard,
                                                               @Header("LoggedInUserType") String type,
                                                               @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getRefundOtp(@Url String url,
                                    @Header("Identifier") String doneCard,
                                    @Header("LoggedInUserType") String type,
                                    @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> changeBoardingStn(@Url String url);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getBoardingStnForChange(@Url String url,
                                               @Header("Identifier") String doneCard,
                                               @Header("LoggedInUserType") String type,
                                               @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getCancelTicketDetail(@Url String url,
                                             @Header("Identifier") String doneCard,
                                             @Header("LoggedInUserType") String type,
                                             @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getCancelIdRefund(@Url String url,
                                         @Header("Identifier") String doneCard,
                                         @Header("LoggedInUserType") String type,
                                         @Header("Merchant") String merchant, @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<TrainBookingListResponseModel> getTrainBookingList(@Url String url,
                                                            @Header("Identifier") String doneCard,
                                                            @Header("LoggedInUserType") String type,
                                                            @Header("Merchant") String merchant,
                                                            @Header("Mode") String mode);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getTrainPnrStatus(@Url String url,
                                         @Header("Identifier") String doneCard,
                                         @Header("LoggedInUserType") String type,
                                         @Header("Merchant") String merchant,
                                         @Header("Mode") String mode);

    //    fino
    @POST("api/Aeps/{methodName}")
    Call<ResponseBody> getFinoCommonPost(@Path("methodName") String method, @Body Object data);

    // rapipay Service
    @GET()
    Call<ArrayList<BankResponse>> getService(@Url String url);

    @GET()
    Call<ArrayList<PinCityResponse>> getServicePin(@Url String url);
    @GET()
    Call<ResponseBody> getServiceCityPin(@Url String url);
    @GET()
    Call<ResponseBody> getServiceCityPinResponse(@Url String url);

    @POST("api/payments/{methodName}")
    Call<ResponseBody> getRapipayCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("api/payments/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getRapipayWithHeader(@Path("methodName") String method, @Body Object data,
                                            @Header("userData") String userData, @Header("Authorization") String token);

    @GET()
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> testHeader(@Url String url, @Header("Authorization") String token);


    //    Payout
    @POST("api/Payment/{methodName}")
    Call<ResponseBody> getPayoutPost(@Path("methodName") String method, @Body Object data);

    @POST("B2B/AdharPayment/{methodName}")
    Call<ResponseBody> getPayoutListPost(@Path("methodName") String method, @Body Object data);

    @POST(ApiConstants.PATH_URL_PAYOUT+"{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getPayoutWithHeader(@Path("methodName") String method, @Body Object data,
                                           @Header("userData") String userData, @Header("Authorization") String token);

    @POST("api/Payment/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getPayoutTxnWithHeader(@Path("methodName") String method, @Body Object data,
                                              @Header("userData") String userData, @Header("Authorization") String token);

//    qr

    @POST("V2/Cashfree/{methodName}")
    Call<ResponseBody> getQRAuth(@Path("methodName") String method, @Body Object data);

    @GET("V2/Cashfree/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getVPACheck(@Path("methodName") String method,
                                   @Header("userData") String userData, @Header("Authorization") String token/*,
                                   @Header("Identifier") String doneCard, @Header("LoggedInUserType") String type,
                                   @Header("Merchant") String merchant, @Header("Mode") String mode*/);

    @GET("V2/Cashfree/VPAActive?Active=1")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> activeVPA(@Header("userData") String userData, @Header("Authorization") String token,
                                 @Header("Identifier") String doneCard, @Header("LoggedInUserType") String type,
                                 @Header("Merchant") String merchant, @Header("Mode") String mode);

    //MATM
    @POST("api_V1/PaymentEngine/{methodName}")
    Call<ResponseBody> getRapipayMatmCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("api_V1/PaymentEngine/{methodName}")
    Call<ResponseBody> getRapipayMatmCommonPost2(@Path("methodName") String method,@Query("transactionid") String id, @Body Object data);

    //    LIC
    @POST("Utility/BillPayment/{methodName}")
    Call<ResponseBody> getLicCommonPost(@Path("methodName") String method, @Body Object data);

    @POST("Utility/BillPayment/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getLicCommonPost(@Path("methodName") String method, @Body Object data,
                                        @Header("userData") String userData, @Header("Authorization") String token);

    @POST("Bill/Paybill/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getBillPayCommonPost(@Path("methodName") String method, @Body Object data,
                                            @Header("userData") String userData, @Header("Authorization") String token);

    @POST("List/UtilityReport/{methodName}")
    Call<ResponseBody> getUtilityList(@Path("methodName") String method, @Body Object data);

    @POST("api/Paytem/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> getPaytmCommonPostNew(@Path("methodName") String method, @Body Object data,
                                             @Header("userData") String userData, @Header("Authorization") String token);

    @GET("api/Utilityrecharge/GetOperator")
    Call<OptModelRecharge> getRechargeOperator(@Query("Category") String method);

    @POST("api/Utilityrecharge/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> recharge(@Path("methodName") String method, @Body Object data,
                                @Header("userData") String userData, @Header("Authorization") String token);


    // insurance Service
    @POST("API/Insurence/{methodName}")
    Call<ResponseBody> getInsuranceCommonPost(@Path("methodName") String method, @Body Object data);

    //    fasttag  api
    @GET("Api_v1/Fastag/Fastaglist")
    Call<ResponseBody> getFastTagOperator();

    @POST("Api_v1/Fastag/Fetchbilldetails")
    Call<ResponseBody> getFastTagBill(@Body Object data);

    @POST("Api_v1/Fastag/PayBillpayments")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> payFastTagBill(@Body Object data, @Header("userData") String userData, @Header("Authorization") String token);


//    Credit card api
    @POST("api/CreditCard/{methodName}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> creditCard(@Path("methodName") String method, @Body Object data,
                                  @Header("userData") String userData, @Header("Authorization") String token);

    @POST("V2.R/Payout/JustClick")
    Call<ResponseBody> forgetPass(@Body Object data);
}