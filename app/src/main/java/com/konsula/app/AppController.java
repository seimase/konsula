package com.konsula.app;

/**
 * Created by Owner on 7/20/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.Messages;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.zopim.android.sdk.api.ZopimChat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    DecimalFormat MONEY_FORMAT;
    private SessionManager sessionManager;
    private static AppController mInstance;

    MixpanelAPI mixpanel;
    AppEventsLogger logger;

    @Override
    public void onCreate() {
        super.onCreate();
       // Fabric.with(this, new Crashlytics());
        mInstance = this;
        sessionManager = new SessionManager(getApplicationContext());
        MONEY_FORMAT = new DecimalFormat("#,##0");
        MONEY_FORMAT.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ITALY));
        printhashkey();
        zopimChatApplication();


        String devLang = Locale.getDefault().getLanguage();
        String lang = sessionManager.getLanguage();
        if (lang.isEmpty()) {
            sessionManager.setLocale(devLang);
            sessionManager.setLanguage(devLang);
        } else {
            sessionManager.setLocale(lang);
            sessionManager.setLanguage(lang);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        AppEventsLogger.activateApp(this);
        logger = AppEventsLogger.newLogger(this);

        //picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        //Mixpanel
        mixpanel =
                MixpanelAPI.getInstance(this, AppConstant.MIXPANEL_TOKEN);
    }

    //Mixpanel ---------------------------------------------------------------
    public void setMixpanel(String eventName) {
        mixpanel.track(eventName);
    }

    public void setMixpanel(String eventName, JSONObject props) {
        mixpanel.track(eventName, props);
    }

    //Facebook Analytic ------------------------------------------------------
    public void setFacebookEvent(String eventName) {
        logger.logEvent(eventName);
    }

    public void setFacebookEvent(String eventName, Bundle parameters) {
        logger.logEvent(eventName, parameters);
    }


    public void setMixpanelPeople(String sUserName, JSONObject props) {
        //mixpanel.identify(sUserName);
        mixpanel.getPeople().identify(sUserName);
        mixpanel.alias(sUserName, mixpanel.getDistinctId());
        mixpanel.getPeople().set(props);


        mixpanel.registerSuperProperties(props);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String lang = sessionManager.getLanguage();
        sessionManager.setLocale(lang);
    }

    private void zopimChatApplication() {
        ZopimChat.trackEvent("Application created");
        ZopimChat.init(AppConstant.ACCOUNT_CHAT_KEY).build();
//        VisitorInfo emptyVisitorInfo = new VisitorInfo.Builder().build();
//        ZopimChat.setVisitorInfo(emptyVisitorInfo);
//        Log.v("Zopim Chat Sample", "Visitor info erased.");
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }


    public void printhashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.konsula.search",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static Context getAppContext() {
        return mInstance;
    }

    public static String appName() {
        return getAppContext().getString(R.string.app_name);
    }


    public void displayImage(Context context, String uri, ImageView imageView) {
        /*Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(false);
        picasso.load(uri)
                .into(imageView);*/

        /*Glide.with(context)
                .load(uri)
                .skipMemoryCache(true)
                .into(imageView);*/
        Glide.with(context)
                .load(uri)
                .into(imageView);

    }


    public void displayImagePicasso(Context context, String uri, ImageView imageView) {
        Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(false);
        picasso.load(uri)
                .into(imageView);
    }

    public String getDefaultPriceFormat(String currency, Double value) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(currency);
        stringBuffer.append(" ");
        stringBuffer.append(MONEY_FORMAT.format(value));
        return stringBuffer.toString();
    }

    public String getDefaultPriceFormatFloat(String currency, Float value) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(currency);
        stringBuffer.append(" ");
        stringBuffer.append(MONEY_FORMAT.format(value));
        return stringBuffer.toString();
    }


    public static boolean isGPSON(Context con) {
        LocationManager lm = (LocationManager) con
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public String getTimeFormat(String datetime, String separator) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH" + separator + "mm");
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateFormat(String datetime, boolean useDay, String locale) {
        Locale locale1 = new Locale(locale);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = (useDay) ? new SimpleDateFormat("EEE, d MMM", locale1) : new SimpleDateFormat("d MMM", locale1);
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String datehistory(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String timehistory(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String fulltime(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String fulldate(String datetime, String local) {
        Locale locale = new Locale(local);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy", locale);
        try {
            Date date = formatter.parse(datetime);
            return (newFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static boolean saveBitmapToFile(String path, Bitmap bitmap) {
        File file = new File(path);

        if (file.exists()) file.delete();


        boolean res = false;

        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);

                res = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                fos.close();
            } catch (Exception e) {
            }
        }

        return res;
    }

    public static String dateJoin(String date, String local) {
        Locale locale = new Locale(local);
        Date datejoin = null;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dts = new SimpleDateFormat("d MMM yyyy", locale);
        try {
            datejoin = dt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(dts.format(datejoin));
    }

    public static String setDatefull(String date) {
        Date datejoin = null;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dts = new SimpleDateFormat("d MMM yyyy HH:mm");
        try {
            datejoin = dt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(dts.format(datejoin));
    }

    public static String dateBirth(String date) {
        Date dateBirth = null;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dts = new SimpleDateFormat("d MMM yyyy");
        try {
            dateBirth = dt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(dts.format(dateBirth));
    }

    public void doDialog(Context context, String message) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("" + message).setPositiveButton(context.getResources().getString(R.string.general_text_dismiss), dialogClickListener).show();
    }

    public String firstWord(String string) {
        return (string + " ").split(" ")[0];
    }

    public boolean isTokenValid(Messages messages, Response response, final Context context, final RefreshTokenCallback callback) {
        if (messages.response_code == 403) {
            String refreshToken = getSessionManager().getRefreshToken();
            Log.d("refresh token", refreshToken);
            NetworkManager.getNetworkService(this).refreshToken(refreshToken, new Callback<AuthModel>() {
                @Override
                public void success(AuthModel authModel, Response response) {
                    if (authModel.messages.response_code == 201) {
                        getSessionManager().setUserAccount(authModel.results);
                        getSessionManager().setToken(authModel.results.token);
                        getSessionManager().setRefreshToken(authModel.results.refresh_token);
                        if (callback != null) {
                            callback.onRefreshTokenComplete();
                        }
                    } else {
                        getSessionManager().setGcmKey(null);
                        getSessionManager().setUserAccount(null);
                        getSessionManager().setToken(null);
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.putExtra("fromSignout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ((Activity) context).startActivityForResult(intent, 0);
//                        if (callback != null) {
//                            callback.onRefreshTokenFailed();
//                        }
                    }

                }

                @Override
                public void failure(RetrofitError error) {


                }
            });
            return false;
        }
        return true;
    }


    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = ".";
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }
    }

    public static String trimCommaOfString(String string) {
//        String returnString;
        if (string.contains(",")) {
            return string.replace(",", "");
        } else {
            return string;
        }

    }

    public String formatTime(long millis, String hour, String minute, String second, Boolean withsec) {

        String output = "00:00";
        try {
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = seconds / (60 * 60);
            long days = seconds / (3600 * 24);

            seconds = seconds % 60;
            minutes = minutes % 60;
            hours = hours % 24;
            days = days % 30;

            String sec = String.valueOf(seconds);
            String min = String.valueOf(minutes);
            String hur = String.valueOf(hours);
            String day = String.valueOf(days);

            if (seconds < 10)
                sec = "0" + seconds;
            if (minutes < 10)
                min = "0" + minutes;
            if (hours < 10)
                hur = "0" + hours;
            if (withsec) {
                output = hur + hour + min + minute + sec + second;
            } else {
                output = hur + hour + min + minute;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    public int getThemeColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    public static boolean checkConnection(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean ret = true;
        if (conMgr != null) {
            NetworkInfo i = conMgr.getActiveNetworkInfo();
            if (i != null) {
                if (!i.isConnected())
                    ret = false;
                if (!i.isAvailable())
                    ret = false;
            }

            if (i == null)
                ret = false;

        } else
            ret = false;

        return ret;
    }

    public Boolean CheckingString(String str) {
        String specialCharacters = "[a-zA-Z0-9.? ]*";
        if (str.matches(specialCharacters)) {
            return false;
        } else {
            return true;
        }
    }

}