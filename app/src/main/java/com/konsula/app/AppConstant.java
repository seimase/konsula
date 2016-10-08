package com.konsula.app;

import id.co.veritrans.android.api.VTUtil.VTConfig;

/**
 * Created by user on 12/1/2015.
 */
public class AppConstant {

    // base api url
    //public static String DOMAIN_URL = "https://api.konsula.com";
    public static String DOMAIN_URL = "https://staging-api.konsula.com";
    public final static String API_VERSION = "/1.0/";
    public final static String API_URL = DOMAIN_URL + API_VERSION + "frontend/";
    public final static String API_DOCTOR_LIST = "search/doctor?";


    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    // KEY RESULT
    public final static int KEY_RESULT_SEARCH = 2201;
    public final static int KEY_RESULT_MORE_MENU = 2202;

    // KEY EXTRA
    public final static String KEY_EXTRA_SEARCH = "KEY_EXTRA_SEARCH";
    public final static String KEY_EXTRA_OPTION = "KEY_EXTRA_OPTION";
    public final static String KEY_EXTRA_MORE_MENU = "KEY_EXTRA_MORE_MENU";


    public static final String ACTION_PICK = "cunoraz.ACTION_PICK";
    public static final String ACTION_MULTIPLE_PICK = "cunoraz.ACTION_MULTIPLE_PICK";

    //    mixpanel
    //public static final String MIXPANEL_TOKEN = "5eacd4a4f80cbf068dfdc0a5b9753983";
    public static final String MIXPANEL_TOKEN = "ceb7edc1387089b6b3cb45c79693408e";

    //API KEY
    public static final String ACCOUNT_CHAT_KEY = "3cQHCwa6cg2kYHAGesQGJfToH5D3tjdg"; //cs
    //    public static final String ACCOUNT_CHAT_KEY = "3hjpicRZABnrH5BpQE1lTqU0N3oHReXx";
    //   public static final String ACCOUNT_CHAT_KEY = "3qTc66SyGWvnSuZ1lXWTVZqndJzzkWdI";   //docter

    //API VERITRANS
    public final static Boolean VERITRANS_IS_PRODUCTION = true;

    public static String getVeritransClientKey() {
        if (VTConfig.VT_IsProduction) {
            // production
            return "VT-client-g9NcVsK7cZc1qxrO";
        }
        // development
        return "VT-client-48zv_rFobxOwty4f";
    }

    public static String getPaymentApiUrl() {
        if (VTConfig.VT_IsProduction) {
            return "https://api.veritrans.co.id/v2/token";
        }
        return "https://api.sandbox.veritrans.co.id/v2/token";
    }
//    public final static String VERITRANS_PAYMENT_API = "https://api.sandbox.veritrans.co.id/v2/token";
//    public final static String VERITRANS_CLIENT_KEY = "VT-client-4QqpcXPmYGkrgPv2";

    //GLOBAL-VARIABLE
    public static boolean FROM_PROFILE;
    public static boolean FROM_MEMBERSHIP;
    public static boolean FROM_BACKMENU;
    public static int FROM_INDEX;
    public static int CURRENT_POSITION;
    public static boolean FROM_DOCTOR;

}
