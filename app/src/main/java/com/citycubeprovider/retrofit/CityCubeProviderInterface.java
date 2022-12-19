package com.citycubeprovider.retrofit;


import com.citycubeprovider.model.BookingDetailModel;
import com.citycubeprovider.model.BookingModel;
import com.citycubeprovider.model.BrandListModel;
import com.citycubeprovider.model.CarListModel;
import com.citycubeprovider.model.CarListModel2;
import com.citycubeprovider.model.FeedbackModel;
import com.citycubeprovider.model.ModListModel;
import com.citycubeprovider.model.SignupModel;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CityCubeProviderInterface {



    @Multipart
    @POST("signup")
    Call<SignupModel> signupUser(
            @Part("user_name") RequestBody user_name,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody mobile,
            @Part("phone_code") RequestBody phone_code,
            @Part("password") RequestBody password,
            @Part("register_id") RequestBody register_id,
            @Part("type") RequestBody type,
            @Part("car_type_id") RequestBody car_type_id,
            @Part("vehicle_name") RequestBody vehicle_name,
            @Part("vehicle_number") RequestBody vehicle_number,
            @Part MultipartBody.Part file);







   /* @FormUrlEncoded
    @POST("signup")
    Call<SignupModel> signupUser(@FieldMap Map<String, String> params);*/


    @GET("car_list")
    Call<CarListModel> getCarList();

    @GET("car_list")
    Call<CarListModel2> getCarList2();

    @FormUrlEncoded
    @POST("brand_list")
    Call<BrandListModel> cardBrandList(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("model_list")
    Call<ModListModel> modelList(@FieldMap Map<String, String> params);


    @Multipart
    @POST("driver_signup2")
    Call<SignupModel> addVehicle(@Part("user_id") RequestBody user_id,
                                 @Part("car_type_id") RequestBody car_type_id,
                                 @Part("brand") RequestBody brand,
                                 @Part("car_model") RequestBody car_model,
                                 @Part("year_of_manufacture") RequestBody year_of_manufacture,
                                 @Part("car_number") RequestBody car_number,
                                 @Part("car_color") RequestBody car_color,
                                 @Part MultipartBody.Part file);


    @Multipart
    @POST("update_document_signup3")
    Call<SignupModel> addDocument(@Part("user_id") RequestBody user_id,
                                  @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("login")
    Call<SignupModel> userLogin(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("forgot_password")
    Call<Map<String, String>> forgotPass(@FieldMap Map<String, String> params);


    @Multipart
    @POST("update_profile")
    Call<SignupModel> profileUpdate(
            @Part("user_name") RequestBody username,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody mobile,
            @Part("phone_code") RequestBody country_code,
            @Part("user_id") RequestBody user_id,
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("change_password")
    Call<Map<String, String>> changePassword(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("logout")
    Call<Map<String, String>> logout(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking_driver")
    Call<BookingModel> getBookingCurt(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<Map<String, String>> bookingAcceptCancel(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_upcoming_booking_driver")
    Call<BookingModel> getBookingStatus(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_finish_booking_driver")
    Call<BookingModel> getBookingStatus2(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_online_status")
    Call<Map<String, String>> updateStatus(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_lat_lon")
    Call<Map<String, String>> updateLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_details")
    Call<BookingDetailModel> bookingDetails(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("chat_notification")
    Call<Map<String, String>> sendPushNotification(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_chat_count")
    Call<Map<String, String>> getChatCount(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("reset_chat_count")
    Call<Map<String, String>> resetChatCount(@FieldMap Map<String, String> params);


    @Multipart
    @POST("driver_accept_and_Cancel_request")
    Call<BookingDetailModel> driverChangedStatus(@Part("driver_id") RequestBody driver_id,
                                                 @Part("status") RequestBody status, @Part("request_id") RequestBody request_id,
                                                 @Part("picklaterdate") RequestBody picklaterdate,
                                                 @Part MultipartBody.Part file1,
                                                 @Part MultipartBody.Part file2,
                                                 @Part MultipartBody.Part file3);


    @FormUrlEncoded
    @POST("get_driver_rating")
    Call<FeedbackModel> getFeedback(@FieldMap Map<String, String> params);


}
