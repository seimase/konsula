package com.konsula.app.service.net;

import com.konsula.app.service.model.AccessTeledocModel;
import com.konsula.app.service.model.AllPlanModel;
import com.konsula.app.service.model.AppointmentDataModel;
import com.konsula.app.service.model.AppointmentListModel;
import com.konsula.app.service.model.AppointmentMemberModel;
import com.konsula.app.service.model.AppointmentModel;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.AvailablePaymentMethodModel;
import com.konsula.app.service.model.BankListModel;
import com.konsula.app.service.model.BankTransferModel;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.BillingTransactionModel;
import com.konsula.app.service.model.CancelTeledocModel;
import com.konsula.app.service.model.ChangePasswordModel;
import com.konsula.app.service.model.ChatRetrictionModel;
import com.konsula.app.service.model.Chatmodel;
import com.konsula.app.service.model.CheckEmailModel;
import com.konsula.app.service.model.CheckPendingTransactionModel;
import com.konsula.app.service.model.CheckVersionModel;
import com.konsula.app.service.model.ContactUsModel;
import com.konsula.app.service.model.CreateTransactionModel;
import com.konsula.app.service.model.DataReviewAppintmentModel;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.model.DetailSummaryTransactionModel;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.EstoreCategoryTagModel;
import com.konsula.app.service.model.EstoreProductCatalogModel;
import com.konsula.app.service.model.EstoreProductItemModel;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.model.DoctorAppointmentModel;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.DoctorScheduleModel;
import com.konsula.app.service.model.ForgotPasswordModel;
import com.konsula.app.service.model.HistoryModel;
import com.konsula.app.service.model.HistoryTransactionModel;
import com.konsula.app.service.model.HomeModel;
import com.konsula.app.service.model.IdentityModel;
import com.konsula.app.service.model.InitSearchModel;
import com.konsula.app.service.model.InitSpecialityModel;
import com.konsula.app.service.model.InitVarLoanModel;
import com.konsula.app.service.model.LoanModel;
import com.konsula.app.service.model.LocationSearchModel;
import com.konsula.app.service.model.LoginSosmedModel;
import com.konsula.app.service.model.MembershipCreateTransactionModel;
import com.konsula.app.service.model.MembershipModel;
import com.konsula.app.service.model.MembershipSubModel;
import com.konsula.app.service.model.MembershipVerifyingModel;
import com.konsula.app.service.model.MultiSearchModel;
import com.konsula.app.service.model.MyVoucherModel;
import com.konsula.app.service.model.PaymentCreditCardModel;
import com.konsula.app.service.model.PaymentMethodModel;
import com.konsula.app.service.model.PaymentProfModel;
import com.konsula.app.service.model.PembiayaanModel;
import com.konsula.app.service.model.PhotoModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.model.PromoCodeModel;
import com.konsula.app.service.model.RegisterModel;
import com.konsula.app.service.model.RegisterSosmedModel;
import com.konsula.app.service.model.RescheduleConfirmTeledocModel;
import com.konsula.app.service.model.RescheduleRequestModel;
import com.konsula.app.service.model.ResendVerificationModel;
import com.konsula.app.service.model.RestrictionModel;
import com.konsula.app.service.model.ReviewTeledocModel;
import com.konsula.app.service.model.SaveOriginalReviewModel;
import com.konsula.app.service.model.SearchDoctorModel;
import com.konsula.app.service.model.SearchKlinikModel;
import com.konsula.app.service.model.SummaryEstoreModel;
import com.konsula.app.service.model.SummaryMembershipModel;
import com.konsula.app.service.model.SummaryTeledocModel;
import com.konsula.app.service.model.TeledoctorModel;
import com.konsula.app.service.model.TransactionCancelModel;
import com.konsula.app.service.model.UpcomingTeledocModel;
import com.konsula.app.service.model.UpdateAccountModel;
import com.konsula.app.service.model.VerificationModel;
import com.konsula.app.service.model.VerificationUserModel;
import com.konsula.app.service.model.ViewAccountModel;

import java.sql.Date;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Created by hiantohendry on 1/27/16.
 */
public interface NetworkService {

    @GET("/frontend/review/practice/{practiceId}")
    void getPracticeReview(@Header("token") String token, @Path("practiceId") int practiceId,@Query("offset") int offsetpage, @Header("language") String language, Callback<PracticeReviewModel> response);

    @GET("/frontend/profile/practice/{practiceUri}")
    void getPracticeProfile(@Header("token") String token,@Header("language") String language, @Path("practiceUri") String practiceUri, Callback<PracticeModel> response);

    @GET("/frontend/banner/home-apps")
    void getHomeBanner(@Header("token") String token, Callback<BannerHomeModel> response);


    @FormUrlEncoded
    @POST("/auth/member/register")
    void registerUser(@Header("language") String language,@Field("email") String email, @Field("password") String password, @Field("phone_number") String phone_number, @Field("birth_date") String birth_date, @Field("gender") String gender, @Field("fullname") String fullname, @Field("source") String source, @Field("verify_sms") String verify_sms, Callback<RegisterModel> response);

    @FormUrlEncoded
    @POST("/auth/member/register")
    void registerUsersosmed(@Header("language") String language,@Field("email") String email, @Field("password") String password, @Field("fullname") String fullname, @Field("phone_number") String phone_number, @Field("birth_date") String birth_date, @Field("gender") String gender, @Field("source") String source, @Field("verify_sms") String verify_sms, @Field("photo_url") String photourl, Callback<RegisterSosmedModel> response);

    @FormUrlEncoded
    @POST("/auth/member/sosmed/{source}")
    void loginUsersosmed(@Header("language") String language,@Path("source") String sources, @Field("email") String email, @Field("source") String source, @Field("signature_key") String signature_key, Callback<LoginSosmedModel> response);


    @GET("/frontend/account/history-list")
    void getHistory(@Header("token") String token, Callback<HistoryModel> response);

    @GET("/frontend/profile/doctor/{doctorUri}")
    void getDoctorProfile(@Header("token") String token, @Header("language") String language, @Path("doctorUri") String doctorUri, Callback<DoctorModel> response);

    @FormUrlEncoded
    @POST("/auth/member/generate-token")
    void getToken(@Header("language") String language,@Field("email") String email, @Field("password") String password, Callback<AuthModel> response);

    @FormUrlEncoded
    @PUT("/frontend/account/change-password")
    void changePassword(@Header("token") String token, @Header("language") String language, @Field("old_password") String oldpassword, @Field("new_password") String newpassword, Callback<ChangePasswordModel> response);

    @GET("/frontend/account/view-account")
    void getviewAccount(@Header("token") String token, @Header("language") String language, Callback<ViewAccountModel> response);

    @Multipart
    @POST("/frontend/account/upload-photo")
    void uploadImage(@Header("token") String token, @Part("file") TypedFile image, Callback<PhotoModel> response);

    @FormUrlEncoded
    @PUT("/frontend/account/update-account")
    void updateAccount(@Header("token") String token, @Header("language") String language, @Field("fullname") String fullname, @Field("gender") String gender, @Field("birth_date") String bithdate, @Field("phone_number") String phone_number, @Field("address") String address, @Field("height") String height, @Field("weight") String weight, @Field("blood_type") String blood_type, Callback<UpdateAccountModel> response);

    @FormUrlEncoded
    @POST("/auth/member/forgot-password")
    void forgotpassword(@Header("language") String language,@Field("email") String email, Callback<ForgotPasswordModel> response);

    @GET("/frontend/autocomplete/init-search")
    void initSearch(@Header("token") String token, @Header("language") String language, Callback<InitSearchModel> response);

    @GET("/frontend/autocomplete/location-search")
    void locationSearch(@Header("token") String token, @Header("language") String language, @Query("keyword") String keyword, Callback<LocationSearchModel> response);

    @GET("/frontend/autocomplete/multi-search")
    void multiSearch(@Header("token") String token, @Header("language") String language, @Query("search_type") String searchType, @Query("keyword") String keyword, @Query("location") String location, Callback<MultiSearchModel> response);

    @GET("/frontend/search/practice")
    void searchPractice(@Header("token") String token, @Query("location_state") String locationState, @Query("locality") String locality, @Query("keyword") String keyword, @Query("country") String country, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("page") int page, @Query("sortby") String sortBy, @Query("min_rate") int minRate, @Query("max_rate") int maxRate, @Query("min_hour") String minHour, @Query("max_hour") String maxHour,@Query("min_ops") Integer minOps, @Query("max_ops") Integer maxOps, @Query("days") String days, Callback<SearchKlinikModel> response);

    @GET("/frontend/search/doctor")
    void searchDoctor(@Header("token") String token, @Query("location_state") String locationState, @Query("locality") String locality, @Query("keyword") String keyword, @Query("country") String country, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("page") int page, @Query("sortby") String sortBy, @Query("min_rate") int minRate, @Query("max_rate") int maxRate, @Query("min_hour") String minHour, @Query("max_hour") String maxHour,@Query("min_exp") Integer minExp, @Query("max_exp") Integer maxExp, @Query("days") String days, Callback<SearchDoctorModel> response);

    @GET("/frontend/schedule/doctor/{doctorId}/practice/{practiceId}")
    void getScheduleDoctor(@Header("token") String token, @Header("language") String language, @Path("doctorId") Integer doctorId, @Path("practiceId") Integer practiceId, @Query("restriction") String space_hour, Callback<DoctorScheduleModel> response);

    @GET("/frontend/appointment/checking-schedule-validity")
    void checkScheduleValidity(@Header("token") String token, @Header("language") String language, @QueryMap Map<String, String> params, Callback<GeneralCheckModel> response);


    @GET("/frontend/home/mobile-category")
    void initHomeDoctor(@Header("token") String token, @Header("language") String language, Callback<HomeModel> response);


    @GET("/frontend/general/check-email/{email}")
    void checkEmail(@Header("token") String token, @Path("email") String email, Callback<CheckEmailModel> response);

    @GET("/frontend/appointment/doctor-profile")
    void getDoctorAppointment(@Header("token") String token, @Header("language") String language, @QueryMap Map<String, String> params, Callback<DoctorAppointmentModel> response);

    @FormUrlEncoded
    @POST("/auth/member/register")
    void doRegister(@Header("token") String token,
                    @Field("fullname") String fullname,
                    @Field("birth_date") String birth_date,
                    @Field("phone_number") String phone_number,
                    @Field("email") String email,
                    @Field("password") String password,
                    @Field("source") String source,
                    @Field("gender") String gender,
                    Callback<RegisterModel> response);

    @FormUrlEncoded
    @PUT("/frontend/appointment/update-account")
    void doUpdateProfile(@Header("token") String token,
                         @Header("language") String language,
                         @Field("fullname") String fullname,
                         @Field("birth_date") String birth_date,
                         @Field("phone_number") String phone_number,
                         @Field("gender") String gender,
                         Callback<GeneralCheckModel> response);

    @FormUrlEncoded
    @POST("/frontend/appointment/make-pending-appointment")
    void saveAppointment(@Header("token") String token,
                         @Field("member_id") String member_id,
                         @Field("doctor_id") String doctor_id,
                         @Field("practice_id") String practice_id,
                         @Field("schedule_date") String schedule_date,
                         @Field("hour_start") String hour_start,
                         @Field("hour_end") String hour_end,
                         @Field("time_zone") String time_zone,
                         @Field("phone_number") String phone_number,
                         @Field("reason") String reason,
                         Callback<AppointmentModel> response);

    @FormUrlEncoded
    @POST("/frontend/appointment/make-an-appointment")
    void makeAppointment(@Header("token") String token,
                         @Field("unique_id") String unique_id,
                         @Field("verification_code") String verification_code,
                         Callback<VerificationModel> response);

    @GET("/frontend/account/appointment-list")
    void getAppointmentMember(@Header("token") String token, Callback<AppointmentMemberModel> response);


    @GET("/frontend/appointment/resend-verification-code/{unique_id}")
    void resendVerification(@Header("token") String token, @Path("unique_id") String uniqueId, Callback<GeneralCheckModel> response);

    @FormUrlEncoded
    @POST("/frontend/review/member/appointment/{unique_key}")
    void saveAppointmentReview1(@Header("token") String token,
                                @Path("unique_key") String unique_id,
                                @Field("doctor_id") String doctorId,
                                @Field("practice_id") String practice_id,
                                @Field("unique_key") String uniqueid,
                                @Field("member_id") String member_id,
                                @Field("point_friendly") int point_friendly,
                                @Field("point_facility") int point_facility,
                                @Field("point_timing") int point_timing,
                                @Field("feedback_from_user") String feedback_from_user,
                                @Field("feedback_to_konsula") String feedback_to_konsula,
                                @Field("anonim") String anonim,
                                Callback<GeneralCheckModel> repsonse);

    @FormUrlEncoded
    @POST("/frontend/faq/submit-question")
    void doContact(@Header("token") String token,
                   @Field("fullname") String fullname,
                   @Field("email") String email,
                   @Field("phone") String phone_number,
                   @Field("message") String message,
                   Callback<ContactUsModel> response);

    @GET("/frontend/account/appointment-list")
    void appointmentList(@Header("token") String token, Callback<AppointmentListModel> response);

    @FormUrlEncoded
    @POST("/frontend/review/member/{memberId}/original-review/save")
    void saveOriginalReview(@Header("token") String token,
                            @Path("memberId") int memberId,
                            @Field("practice_id") int practiceId,
                            @Field("doctor_id") int doctorId,
                            @Field("member_id") int memberIdField,
                            @Field("point_friendly") float pointFriendly,
                            @Field("point_facility") float pointFacility,
                            @Field("point_timing") float pointTiming,
                            @Field("feedback_from_user") String feedbackFromUser,
                            @Field("feedback_to_konsula") String feedbackToKonsula,
                            @Field("anonim") String anonim,
                            Callback<SaveOriginalReviewModel> repsonse);

    @GET("/frontend/review/doctor/{doctorId}")
    void getDoctorReview(@Header("token") String token,@Header("language") String language, @Path("doctorId") int doctorId,@Query("offset") int offsetpage, Callback<DoctorReviewModel> response);

    @FormUrlEncoded
    @POST("/frontend/chat-doctor/create")
    void registerChat(@Header("token") String token, @Field("fullname") String fullname, @Field("module") String module, @Field("email") String email, @Field("phone_number") String phone_number, @Field("reason") String reason, Callback<Chatmodel> response);

    @FormUrlEncoded
    @POST("/auth/member/refresh-token")
    void refreshToken(@Field("refresh_token") String refreshToken, Callback<AuthModel> response);

    @GET("/frontend/review/member/appointment/{unique_id}")
    void getDataForAppointment(@Header("token") String token,@Header("language") String language, @Path("unique_id") String unique_id, Callback<AppointmentDataModel> response);

    @GET("/frontend/review/member/appointment/{unique_id}")
    void getDataReviewAppointment(@Header("token") String token,@Header("language") String language, @Path("unique_id") String unique_id, Callback<DataReviewAppintmentModel> response);

    @FormUrlEncoded
    @POST("/frontend/loan/calculator")
    void doCalculate(
            @Header("token") String token,
            @Field("limit_card") long limit_card,
            @Field("loan") long loan,
            @Field("tenor") long tenor,
            @Field("guarantee") String guarantee,
            Callback<LoanModel> response);

    @GET("/frontend/loan/init-var")
    void initVar(@Header("token") String token, Callback<InitVarLoanModel> response);

    @FormUrlEncoded
    @POST("/frontend/loan/request-applicant")
    void doLoan(@Header("token") String token,
                @Field("uuid") String memberId,
                @Field("loan") int loan,
                @Field("tenor") int tenor,
                @Field("guarantee") String guarantee,
                @Field("provider") String provider,
                @Field("installment") int installment,
                @Field("fullname") String fullname,
                @Field("email") String email,
                @Field("contact") String contact,
                Callback<PembiayaanModel> repsonse);


//    teledoc

    @Multipart
    @POST("/frontend/transaction/teledoc/create")
    void doRequestCall(@Header("token") String token,
                       @Header("language") String language,
                       @Part("doctor_id") int doctor_id,
                       @Part("practice_id") int practice_id,
                       @Part("contact") String contact,
                       @Part("schedule") String schedule,
                       @Part("currency") String currency,
                       @Part("rate") int rate,
                       @Part("reason") String reason,
                       @Part("promocode") String promocode,
                       @PartMap Map<String, TypedFile> params,
                       @Part("convenience_fee") String convenience_fee,
                       @Part("signature_key") String signature_key,
                       Callback<TeledoctorModel> response);

    @GET("/frontend/teledoctor/get-init-specialization")
    void initSpeciality(@Header("token") String token, @Header("language") String language, Callback<InitSpecialityModel> response);

    @Multipart
    @POST("/frontend/account/verification-register")
    void doVerificationUser(@Header("token") String token,
                            @Part("sms_code") String sms_code,
                            Callback<VerificationUserModel> response);

    @GET("/frontend/account/resend-verification-code")
    void doResedVerificationUser(@Header("token") String token,
                                 Callback<ResendVerificationModel> response);

    @GET("/frontend/membership/access-and-restriction")
    void getAccess(@Header("token") String token, Callback<RestrictionModel> response);

    @GET("/frontend/teledoctor/restriction")
    void getAccessTeledoc(@Header("token") String token, Callback<AccessTeledocModel> response);

    @GET("/general/check/latest-api-version/android")
    void initVersionApp(Callback<CheckVersionModel> response);

    @GET("/frontend/teledoctor/histories/upcoming")
    void getUpcomingTeledoc(@Header("token") String token, @Header("language") String language, @Query("page") int page, Callback<UpcomingTeledocModel> response);

    @GET("/frontend/teledoctor/histories/past")
    void getPastTeledoc(@Header("token") String token, @Header("language") String language, @Query("page") int page, Callback<UpcomingTeledocModel> response);


    @FormUrlEncoded
    @PUT("/frontend/teledoctor/cancel-teledoc")
    void cancelTeledoc(@Header("token") String token, @Header("language") String language, @Field("tele_uuid") String tele_uuid, Callback<CancelTeledocModel> response);

    @GET("/frontend/chat-doctor/restriction")
    void getChatRetriction(@Header("token") String token, @Header("language") String language, Callback<ChatRetrictionModel> response);

    @Multipart
    @POST("/frontend/teledoctor/submit-review")
    void doReviewTeledoc(@Header("token") String token,
                         @Part("language") String language,
                         @Part("tele_uuid") String tele_uuid,
                         @Part("star_review") float star_review,
                         @Part("text_review") String text_review,
                         Callback<ReviewTeledocModel> response);


    @Multipart
    @POST("/frontend/teledoctor/reschedule-request")
    void doReqReschedule(@Header("token") String token,
                         @Header("language") String language,
                         @Part("tele_uuid") String tele_uuid,
                         Callback<RescheduleRequestModel> response);

    @Multipart
    @POST("/frontend/teledoctor/reschedule-confirm")
    void doRescheduleConfirm(@Header("token") String token,
                             @Header("language") String language,
                             @Part("tele_uuid") String tele_uuid,
                             Callback<RescheduleConfirmTeledocModel> response);


    //ESTORE API
    @GET("/frontend/estore/product-catalog")
    void getProductCatalog(@Header("token") String token, Callback<EstoreProductCatalogModel> response);

    @GET("/frontend/estore/tags")
    void getCategoryTag(@Header("token") String token,@Query("category_uri") String categoryUri, Callback<EstoreCategoryTagModel> response);

    @GET("/frontend/estore/search-product")
    void getSearchProduct(@Header("token") String token, @QueryMap Map<String, String> params, Callback<EstoreSearchProductModel> response);


    @GET("/frontend/estore/product/{identity_uri}")
    void getProductDetail(@Header("token") String token, @Header("language") String language, @Path("identity_uri") String identity_uri, Callback<EstoreProductModel> response);

    @GET("/frontend/estore/product/{product_uuid}/items")
    void getProductItems(@Header("token") String token, @Header("language") String language, @Path("product_uuid") String product_uuid, Callback<EstoreProductItemModel> response);

    //    MEMBERSHIP
    @GET("/frontend/membership/current")
    void getCurrentPlan(@Header("token") String token, @Header("language") String language, Callback<MembershipModel> response);

    @GET("/frontend/membership/plans")
    void getAllPlan(@Header("token") String token, @Header("language") String language, Callback<AllPlanModel> response);

    @GET("/frontend/membership/plan/{plan_id}/subplans")
    void getSubPlan(@Header("token") String token, @Path("plan_id") Integer plan_id, Callback<MembershipSubModel> response);


    @GET("/frontend/payment/bank-transfer/bank-list")
    void getBankList(@Header("token") String token, Callback<BankListModel> response);

    @GET("/frontend/payment/available-payment-method")
    void getPaymentMethod(@Header("token") String token, Callback<PaymentMethodModel> response);

    @GET("/frontend/transaction/detail/{payment_uuid}")
    void getDetailTransaction(@Header("token") String token, @Header("language") String language, @Path("payment_uuid") String payment_uuid, Callback<DetailMembershipTransactionModel> response);

    @Multipart
    @POST("/frontend/payment/bank-transfer/upload-receipt")
    void uploadPaymentProf(@Header("token") String token, @Header("language") String language, @Part("payment_uuid") String payment_uuid, @Part("proof") TypedFile proof, Callback<PaymentProfModel> response);

//    transaction

    @Multipart
    @POST("/frontend/payment/process/bank_transfer")
    void doPayment(@Header("token") String token, @Part("payment_uuid") String payment_uuid,
                   @Part("bank_id") int bank_id, Callback<BillingTransactionModel> response);

    @Multipart
    @POST("/frontend/payment/process/credit_card")
    void doPaymentCC(@Header("token") String token, @Part("payment_uuid") String payment_uuid,
                     @Part("token_id") String token_id, @Part("total_payment") String total_payment,
                     @Part("card_number") String card_number, @Part("card_holdername") String card_holdername,
                     @Part("card_expiry_month") String card_expiry_month, @Part("card_expiry_year") String card_expiry_year,
                     @Part("currency") String currency, @Part("signature_key") String signature_key,
                     Callback<PaymentCreditCardModel> response);


    @GET("/frontend/transaction/histories")
    void getHistroryTransaction(@Header("token") String token, @Header("language") String language, Callback<HistoryTransactionModel> response);

    @GET("/frontend/payment/detail-invoice/{payment_uuid}")
    void getPaymentTransaction(@Header("token") String token,@Header("language") String language, @Path("payment_uuid") String payment_uuid, Callback<DetailSummaryTransactionModel> response);

    @Multipart
    @POST("/frontend/transaction/membership/create")
    void CreateTransaction(@Header("token") String token, @Header("language") String language,
                           @Part("plan_id") int plan_id, @Part("subplan_id") int subplan_id,
                           @Part("currency") String currency, @Part("price") int price,
                           @Part("convenience_fee") int convenience_fee, @Part("promocode") String promocode, @Part("signature_key") String signature_key,
                           Callback<MembershipCreateTransactionModel> response);

    @GET("/frontend/transaction/summary/membership/plan/{plan_id}/subplan/{subplan_id}")
    void getSummaryMembership(@Header("token") String token, @Header("language") String language, @Path("plan_id") Integer plan_id, @Path("subplan_id") Integer subplan_id, Callback<SummaryMembershipModel> response);

    @Multipart
    @POST("/frontend/transaction/summary/teledoc")
    void getSummaryTeledoc(@Header("token") String token, @Header("language") String language,
                           @Part("doctor_id") Integer doctor_id, @Part("practice_id") Integer practice_id, @Part("schedule") String schedule, @Part("currency") String currency, @Part("rate") Integer rate, Callback<SummaryTeledocModel> response);

    @Multipart
    @POST("/frontend/transaction/promocode/use")
    void getPromocode(@Header("token") String token, @Header("language") String language,
                      @Part("promocode") String promocode, @Part("type") String type, @Part("price") int price,
                      Callback<PromoCodeModel> response);

    @FormUrlEncoded
    @POST("/frontend/miscellaneous/save-credential-push-notification")
    void postInstance(@Header("token") String token, @Field("device_type") String device_type, @Field("instance_id") String instance_id,
                      @Field("auth_token") String auth_token, Callback<GeneralCheckModel> repsonse);

    @GET("/frontend/payment/available-payment-method")
    void availablePaymentMethod(@Header("token") String token, Callback<AvailablePaymentMethodModel> repsonse);

    @GET("/frontend/payment/process/bank_transfer")
    void bankTransfer(@Header("token") String token, @Field("payment_uuid") String paymentUUID, @Field("bank_id") int bankId, Callback<BankTransferModel> repsonse);

    @GET("/frontend/transaction/membership/check-pending-transaction")
    void getPendingMembershipTransaction(@Header("token") String token, @Header("language") String language, Callback<CheckPendingTransactionModel> response);

    @FormUrlEncoded
    @POST("/frontend/payment/bank-transfer/verifying")
    void PostVerifyMembership(@Header("token") String token, @Header("language") String device_type, @Field("payment_uuid") String payment_uuid, Callback<MembershipVerifyingModel> repsonse);

    @FormUrlEncoded
    @POST("/frontend/transaction/cancel")
    void PostCancelTransaction(@Header("token") String token, @Header("language") String language,
                               @Field("payment_uuid") String payment_uuid, Callback<TransactionCancelModel> response);

    @GET("/frontend/estore/vouchers")
    void getVoucher(@Header("token") String token, Callback<MyVoucherModel> response);

    @FormUrlEncoded
    @POST("/frontend/transaction/summary/estore")
    void transactionSummaryEstore(@Header("token") String token, @Header("language") String language, @Field("product_uuid") String productUuid,
                                  @Field("items_uuid[]") String[] itemsUuid, @Field("quantities[]") int[] quanitities, @Field("prices[]") Float[] prices,
                                  Callback<SummaryEstoreModel> response);

    @FormUrlEncoded
    @POST("/frontend/transaction/estore/create")
    void transactionEstoreCreate(@Header("token") String token, @Header("language") String language, @Field("product_uuid") String productUuid,
                                 @Field("items_uuid[]") String[] itemsUuid, @Field("quantities[]") int[] quantities, @Field("currencies[]") String[] currencies,
                                 @Field("prices[]") Float[] prices, @Field("price") Double price, @Field("convenience_fee")int convenienceFee,
                                 @Field("promocode") String promoCode, @Field("signature_key") String signatureKey,
                                 Callback<CreateTransactionModel> response);

}