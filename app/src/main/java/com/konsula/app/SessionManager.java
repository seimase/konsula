package com.konsula.app;

/**
 * Created by hiantohendry on 2/1/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.BillingTransactionModel;
import com.konsula.app.service.model.ChatRetrictionModel;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.model.DoctorAppointmentModel;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.EstoreCategoryTagModel;
import com.konsula.app.service.model.EstoreProductCatalogModel;
import com.konsula.app.service.model.HomeBaseModel;
import com.konsula.app.service.model.LoginSosmedModel;
import com.konsula.app.service.model.MyVoucherModel;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.model.RegisterSosmedModel;
import com.konsula.app.service.model.TeledoctorModel;
import com.konsula.app.service.model.UpcomingTeledocModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SessionManager {

    public static final String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    private static final String EMPTY_STRING = "";

    // Shared key name
    public static final String GCM_REG_ID = "gcm_registration_id";
    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String LANGUAGE = "language";
    public static final String USER_ACCOUNT = "user_account";
    public static final String KLINIK_REVIEW = "klinik_review";
    public static final String DOCTOR_MODEL = "doctor_model";
    public static final String HOME_MENU_DOCTOR = "home_menu_doctor";
    public static final String HOME_MENU_DOCTOR_EN = "home_menu_doctor_en";
    public static final String HOME_MENU_PRACTICES = "home_menu_practices";
    public static final String HOME_MENU_PRACTICES_EN = "home_menu_practices_en";
    public static final String DOCTOR_APPOINTMENT = "doctor_appointment";
    public static final String DOCTOR_REVIEW = "doctor_review";
    public static final String TELEDOC_DATA = "teledoc_data";
    public static final String CHAT_RETRICTION = "chat_retriction";
    public static final String UPCOMING_TELEDOC_DATA = "upcomig_teledoc";
    public static final String DETAIL_MEMBERSHIP_TRANSACTION = "detail_membership_tranaction";
    public static final String DETAIL_MEMBERSHIP_TRANSFER = "detail_membership_transfer";
    public static final String TAG_BANNER = "banner_home";
    public static final String TAG_VOUCHER = "voucher";
    public static final String ESTORE_CATEGORY_TAGS = "estore_category_tags";
    public static final String ESTORE_CATALOG = "estore_catalog";
    public static final String TAG_SETTING = "setting_location";

    private final String SESSION_PREFERENCE = "session_pref";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(SESSION_PREFERENCE, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Clear all preference data
     */
    public void clearAllPreference() {
        editor.clear();
        editor.apply();
    }

    /**
     * Store string data to preferences
     *
     * @param key
     * @param value
     */
    public void putStringData(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get string data from preference
     *
     * @param key
     * @return
     */
    public String getStringData(String key) {
        return pref.getString(key, EMPTY_STRING);
    }


    /**
     * Remove string data from
     *
     * @param key
     */
    public void removeStringData(String key) {
        editor.remove(key);
        editor.apply();
    }

    /**
     * Put Boolean data to preference
     *
     * @param key
     * @param value
     */
    public void putBooleanData(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get Boolean data from preference
     * <br>Default value : FALSE
     *
     * @param key
     * @return
     */
    public Boolean getBooleanData(String key) {
        return pref.getBoolean(key, Boolean.FALSE);
    }

    /**
     * Put Integer data to preference
     *
     * @param key
     * @param value
     */
    public void putIntegerData(String key, Integer value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Get Integer data from preference
     * <br>Default value : 0
     *
     * @param key
     * @return
     */
    public Integer getIntegerData(String key) {
        return pref.getInt(key, 0);
    }


    /**
     * Put Float data to preference
     *
     * @param key
     * @param value
     */
    public void putFloatData(String key, Float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Get Float data from preference
     * <br>Default value : 0
     *
     * @param key
     * @return
     */
    public Float getFloatData(String key) {
        return pref.getFloat(key, 0);
    }

    /**
     * Remove data from preference
     *
     * @param key
     */
    public void removeData(String key) {
        editor.remove(key);
        editor.apply();
    }

    /**
     * Put list of object to shared preference
     * <br>list object must be converted to json string
     */
    public <T> void putListObject(String key, List<T> objList) {
        String json = new Gson().toJson(objList);
        editor.putString(key, json);
        editor.apply();
    }

    /**
     * Get gcm key
     */
    public String getGcmKey() {
        return getStringData(GCM_REG_ID);
    }

    public void setGcmKey(String GcmKey) {
        putStringData(GCM_REG_ID, GcmKey);
    }


    public boolean isUserLogon() {
        String userSession = getStringData(SessionManager.USER_ACCOUNT);
        if (userSession.equals("")) {
            return false;
        }

        return true;
    }


    public void setChatRetriction(ChatRetrictionModel.Results results) {
        String data = new Gson().toJson(results);
        putStringData(CHAT_RETRICTION, data);
    }

    public ChatRetrictionModel.Results getChatRetriction() {
        return new Gson().fromJson(getStringData(CHAT_RETRICTION), ChatRetrictionModel.Results.class);
    }


//    public void setCurrLocale(String currLocale) {
//        putStringData(LANGUAGE, currLocale);
//    }
//
//    public String getCurrLocale() {
//        return getStringData(LANGUAGE);
//    }

    public void setLanguage(String language) {

        putStringData(LANGUAGE, language);
    }


    public String getLanguage() {
        return getStringData(LANGUAGE);
    }

    public Boolean getReminder() {
        return getBooleanData(TAG_SETTING);
    }
    public void setReminder(Boolean setting) {
        putBooleanData(TAG_SETTING, setting);
    }


    public void setToken(String token) {
        putStringData(TOKEN, token);
    }

    public String getToken() {
        return getStringData(TOKEN);
    }

    public void setRefreshToken(String refreshToken) {
        putStringData(REFRESH_TOKEN, refreshToken);
    }

    public String getRefreshToken() {
        return getStringData(REFRESH_TOKEN);
    }


    public void setUserAccount(AuthModel.Results userData) {
        if (userData != null) {
            String data = new Gson().toJson(userData);
            putStringData(USER_ACCOUNT, data);
        } else {
            putStringData(USER_ACCOUNT, "");
        }
    }

    public void updateUserAccount(String name, String gender, String birthdate, String phone, int weight, int height, String blood_type) {
        Gson gson = new Gson();
        AuthModel.Results results = gson.fromJson(getStringData(USER_ACCOUNT), AuthModel.Results.class);
        results.fullname = name;
        results.gender = gender;
        results.birth_date = birthdate;
        results.phone_number = phone;
        results.weight = weight;
        results.height = height;
        results.blood_type = blood_type;
        String data = new Gson().toJson(results);
        putStringData(USER_ACCOUNT, data);
    }

    public void updateStatusUser(String authentication) {
        Gson gson = new Gson();
        AuthModel.Results results = gson.fromJson(getStringData(USER_ACCOUNT), AuthModel.Results.class);
        results.authentication = authentication;
        String data = new Gson().toJson(results);
        putStringData(USER_ACCOUNT, data);
    }

    public void updateImageUser(String image) {
        Gson gson = new Gson();
        AuthModel.Results results = gson.fromJson(getStringData(USER_ACCOUNT), AuthModel.Results.class);
        results.photo = image;
        String data = new Gson().toJson(results);
        putStringData(USER_ACCOUNT, data);
    }

    public void setUserSosmed(RegisterSosmedModel.Results userSosmed) {
        if (userSosmed != null) {
            String data = new Gson().toJson(userSosmed.data);
            putStringData(USER_ACCOUNT, data);
        } else {
            putStringData(USER_ACCOUNT, "");
        }
    }

    public void setLoginUserSosmed(LoginSosmedModel.Results userSosmed) {
        if (userSosmed != null) {
            String data = new Gson().toJson(userSosmed.data);
            putStringData(USER_ACCOUNT, data);
        } else {
            putStringData(USER_ACCOUNT, "");
        }
    }

    public AuthModel.Results getUserAccount() {
        return new Gson().fromJson(getStringData(USER_ACCOUNT), AuthModel.Results.class);
    }


    public void setBannerHome(List<BannerHomeModel.Result> menuList) {
        String data = new Gson().toJson(menuList);
        putStringData(TAG_BANNER, data);
    }

    public List<BannerHomeModel.Result> getBannerHome() {
        String json = getStringData(TAG_BANNER);
        Type type = new TypeToken<List<BannerHomeModel.Result>>() {
        }.getType();
        Gson gson = new Gson();
        List<BannerHomeModel.Result> menuList = gson.fromJson(json, type);
        return menuList;
    }

    public boolean isBanner() {
        String menudoc = getStringData(SessionManager.TAG_BANNER);
        if (menudoc.equals("")) {
            return true;
        }

        return false;
    }

    public void setVoucher(MyVoucherModel.Results voucher) {
        String data = new Gson().toJson(voucher);
        putStringData(TAG_VOUCHER, data);
    }

    public MyVoucherModel.Results getVoucher() {
        return new Gson().fromJson(getStringData(TAG_VOUCHER), MyVoucherModel.Results.class);
    }

    public void setKlinikReview(PracticeReviewModel.Results klinikReview) {
        String data = new Gson().toJson(klinikReview);
        putStringData(KLINIK_REVIEW, data);
    }

    public PracticeReviewModel.Results getKlinikReview() {
        return new Gson().fromJson(getStringData(KLINIK_REVIEW), PracticeReviewModel.Results.class);
    }

    public void setDoctorReview(DoctorReviewModel.Results doctorReview) {
        String data = new Gson().toJson(doctorReview);
        putStringData(DOCTOR_REVIEW, data);
    }

    public DoctorReviewModel.Results getDoctorReview() {
        return new Gson().fromJson(getStringData(DOCTOR_REVIEW), DoctorReviewModel.Results.class);
    }

    public void setTeledoc(TeledoctorModel.Results teledocdata) {
        String data = new Gson().toJson(teledocdata);
        putStringData(TELEDOC_DATA, data);
    }

    public TeledoctorModel.Results getTeledoc() {
        return new Gson().fromJson(getStringData(TELEDOC_DATA), TeledoctorModel.Results.class);
    }

    public DoctorModel.Results getDoctorProfile() {
        return new Gson().fromJson(getStringData(DOCTOR_MODEL), DoctorModel.Results.class);
    }

    public void setDoctorModel(DoctorModel.Results doctorModel) {
        String data = new Gson().toJson(doctorModel);
        putStringData(DOCTOR_MODEL, data);
    }

    public UpcomingTeledocModel.Datum getupcomingteledocdata() {
        return new Gson().fromJson(getStringData(UPCOMING_TELEDOC_DATA), UpcomingTeledocModel.Datum.class);
    }

    public void setupcomingteledocdata(UpcomingTeledocModel.Datum upcomingmodel) {
        String data = new Gson().toJson(upcomingmodel);
        putStringData(UPCOMING_TELEDOC_DATA, data);
    }

    public DetailMembershipTransactionModel.Results getDetailMembershipTransaction() {
        return new Gson().fromJson(getStringData(DETAIL_MEMBERSHIP_TRANSACTION), DetailMembershipTransactionModel.Results.class);
    }


    public void setDetailMembershipTransaction(DetailMembershipTransactionModel.Results upcomingmodel) {
        String data = new Gson().toJson(upcomingmodel);
        putStringData(DETAIL_MEMBERSHIP_TRANSACTION, data);
    }

    public BillingTransactionModel.Results getDetailMembershipTransfer() {
        return new Gson().fromJson(getStringData(DETAIL_MEMBERSHIP_TRANSFER), BillingTransactionModel.Results.class);
    }


    public void setDetailMembershipTransfer(BillingTransactionModel.Results billingtransactionmodel) {
        String data = new Gson().toJson(billingtransactionmodel);
        putStringData(DETAIL_MEMBERSHIP_TRANSFER, data);
    }


    public void setHomeMenuDoctor(ArrayList<HomeBaseModel> menuList) {
        String data = new Gson().toJson(menuList);
        putStringData(HOME_MENU_DOCTOR, data);
    }

    public void setHomeMenuDoctorEn(ArrayList<HomeBaseModel> menuList) {
        String data = new Gson().toJson(menuList);
        putStringData(HOME_MENU_DOCTOR_EN, data);
    }


    public void setLocale(String lang) {
        Locale myLocale;
        myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public ArrayList<HomeBaseModel> getHomeMenuDoctor() {
        String json = getStringData(HOME_MENU_DOCTOR);
        Type type = new TypeToken<ArrayList<HomeBaseModel>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<HomeBaseModel> menuList = gson.fromJson(json, type);
        return menuList;
    }

    public ArrayList<HomeBaseModel> getHomeMenuDoctorEn() {
        String json = getStringData(HOME_MENU_DOCTOR_EN);
        Type type = new TypeToken<ArrayList<HomeBaseModel>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<HomeBaseModel> menuList = gson.fromJson(json, type);
        return menuList;
    }

    public void setHomeMenuPractices(ArrayList<HomeBaseModel> menuList) {
        String data = new Gson().toJson(menuList);
        putStringData(HOME_MENU_PRACTICES, data);
    }

    public void setHomeMenuPracticesEn(ArrayList<HomeBaseModel> menuList) {
        String data = new Gson().toJson(menuList);
        putStringData(HOME_MENU_PRACTICES_EN, data);
    }

    public ArrayList<HomeBaseModel> getHomeMenuPractices() {
        String json = getStringData(HOME_MENU_PRACTICES);
        Type type = new TypeToken<ArrayList<HomeBaseModel>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<HomeBaseModel> menuList = gson.fromJson(json, type);

        return menuList;
    }

    public ArrayList<HomeBaseModel> getHomeMenuPracticesEn() {
        String json = getStringData(HOME_MENU_PRACTICES_EN);
        Type type = new TypeToken<ArrayList<HomeBaseModel>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<HomeBaseModel> menuList = gson.fromJson(json, type);

        return menuList;
    }

    public void setDoctorAppointment(DoctorAppointmentModel.Results results) {
        String data = new Gson().toJson(results);
        putStringData(DOCTOR_APPOINTMENT, data);
    }

    public DoctorAppointmentModel.Results getDoctorAppointment() {
        return new Gson().fromJson(getStringData(DOCTOR_APPOINTMENT), DoctorAppointmentModel.Results.class);
    }

    public void setEstoreCategoryTags(EstoreCategoryTagModel results) {
        String data = new Gson().toJson(results);
        putStringData(ESTORE_CATEGORY_TAGS, data);
    }

    public EstoreCategoryTagModel getEstoreCategoryTags() {
        return new Gson().fromJson(getStringData(ESTORE_CATEGORY_TAGS), EstoreCategoryTagModel.class);
    }

    public void setEstoreCatalog(EstoreProductCatalogModel.Results results) {
        String data = new Gson().toJson(results);
        putStringData(ESTORE_CATALOG, data);
    }

    public EstoreProductCatalogModel.Results getEstoreCatalog() {
        return new Gson().fromJson(getStringData(ESTORE_CATALOG), EstoreProductCatalogModel.Results.class);
    }
}

