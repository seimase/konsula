package com.konsula.app.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AccessTeledocModel;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.ChatRetrictionModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.chatdokter.ChatDocterActivity;
import com.konsula.app.ui.activity.direktori.DirektoriActivity;
import com.konsula.app.ui.activity.estore.EstoreMainActivity;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.teledokter.TeledocRescheduleActivity;
import com.konsula.app.ui.activity.teledokter.TeledocReviewActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterDoneActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterFailActivity;
import com.konsula.app.ui.adapter.ImageBannerAdapter;
import com.konsula.app.ui.custom.widget.CircleIndicator;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;
import com.zopim.android.sdk.chatlog.ZopimChatLogFragment;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.widget.ChatWidgetService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 25/02/2016.
 */
public class MainMenuFragment extends Fragment {
    private static final int RP_ACCESS_LOCATION = 1;
    //private ImageView BtnDirektori, BtnTeledokter, BtnChatDokter, BtnEstore;
    private LinearLayout BtnDirektori, BtnTeledokter, BtnChatDokter, BtnEstore;
    private String currentLanguage;
    private CancelableCallback cancelableCallback;
    private ProgressBar progressBar;
    private RelativeLayout l_view;
    ArrayList<BannerHomeModel.Result> homeBanner;
    private CircleIndicator viewPagerIndicator;
    private ViewPager viewPagerHeader;
    private ImageView imageViewBanner;
    private Handler handler;
    ImageBannerAdapter adapter;
    private Runnable runnable;
    int position = 1;

    private android.app.AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate layout for this fragment
        final View parentview = inflater.inflate(R.layout.fragment_main_menu, container, false);
        BtnDirektori = (LinearLayout) parentview.findViewById(R.id.menu_direktori);
        BtnTeledokter = (LinearLayout) parentview.findViewById(R.id.menu_teledokter);
        BtnChatDokter = (LinearLayout) parentview.findViewById(R.id.menu_chatdokter);
        BtnEstore = (LinearLayout) parentview.findViewById(R.id.menu_estore);
        /*BtnDirektori = (ImageView) parentview.findViewById(R.id.image_direktori);
        BtnTeledokter = (ImageView) parentview.findViewById(R.id.image_teledokter);
        BtnChatDokter = (ImageView) parentview.findViewById(R.id.image_chatdokter);
        // imagePembiayaan = (ImageView) view.findViewById(R.id.image_pembiayaan);
        BtnEstore = (ImageView) parentview.findViewById(R.id.image_store);*/
        l_view = (RelativeLayout) parentview.findViewById(R.id.layout_main_menu);
        progressBar = (ProgressBar) parentview.findViewById(R.id.progressBar);
        viewPagerHeader = (ViewPager) parentview.findViewById(R.id.main_header_viewpager);
        viewPagerIndicator = (CircleIndicator) parentview.findViewById(R.id.indicator);
        imageViewBanner = (ImageView) parentview.findViewById(R.id.image_banner);
        progressBar.setProgress(20);
        progressBar.setSecondaryProgress(50);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        BtnDirektori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessLocation();
            }
        });
        BtnTeledokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdataTeledocRetriction();
            }
        });
        BtnChatDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdataChat();

            }
        });
        BtnEstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = new Intent(getActivity(), EstoreComingSoonActivity.class);
                  Intent i = new Intent(getActivity(), EstoreMainActivity.class);
                startActivity(i);
            }
        });
        handler = new Handler();
//        imagePembiayaan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), PembiayaanActivity.class);
//                startActivity(i);
//            }
//        });
        getBanner();

//        imageEstore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent i = new Intent(getActivity(), EstoreComingSoonActivity.class);
////                Intent i = new Intent(getActivity(), EstoreMainActivity.class);
//                Intent i = new Intent(getActivity(), EstoreTransactionActivity.class);
//                startActivity(i);
//            }
//        });
        return parentview;
    }

    private void resumeChat() {

        FragmentManager manager = getActivity().getSupportFragmentManager();
        // find the retained fragment
        if (manager.findFragmentByTag(ZopimChatLogFragment.class.getName()) == null) {
            Intent intent = new Intent(getActivity(), ZopimChatActivity.class);
            startActivity(intent);
        }
    }


    private void getdataTeledocRetriction() {
        doloading();
        cancelableCallback = new CancelableCallback<AccessTeledocModel>() {

            @Override
            protected void onSuccess(AccessTeledocModel accessTeledocModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(accessTeledocModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doneloading();
                        getdataTeledocRetriction();
                    }

                });
                if (isTokenValid) {
                    Intent intent = null;
                    doneloading();
                    if (accessTeledocModel.results.show_page != null) {
                        switch (accessTeledocModel.results.show_page) {
                            case "confirmation":
                                intent = new Intent(getActivity(), PaymentConfirmActivity.class);
                                intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                                intent.putExtra(PaymentConfirmActivity.payment_uuid, accessTeledocModel.results.data.payment_uuid);
                                intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "teledoctor");
                                startActivity(intent);
                                break;
                            case "payment":
                                intent = new Intent(getActivity(), TeledokterFailActivity.class);
                                intent.putExtra("payment_uuid", accessTeledocModel.results.data.payment_uuid);
                                intent.putExtra("name", accessTeledocModel.results.data.doctor_name);
                                intent.putExtra("date", accessTeledocModel.results.data.schedule);
                                intent.putExtra("duration", accessTeledocModel.results.data.duration);
                                intent.putExtra("reason", accessTeledocModel.results.data.reason);
                                intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                startActivity(intent);
                                break;

                            case "teledoctor":
                                intent = new Intent(getActivity(), TeledokterActivity.class);
                                startActivity(intent);
                                break;

                            case "thankyou":
                                intent = new Intent(getActivity(), TeledokterDoneActivity.class);
                                intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                intent.putExtra("phone", accessTeledocModel.results.data.contact);
                                intent.putExtra("status", accessTeledocModel.results.data.tele_status);
                                intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                startActivity(intent);
                                break;
                            case "review":
                                intent = new Intent(getActivity(), TeledocReviewActivity.class);
                                intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                intent.putExtra("uuid", accessTeledocModel.results.data.tele_uuid);
                                intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                intent.putExtra("keluhan", accessTeledocModel.results.data.reason);
                                intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                startActivity(intent);
                                break;
                            case "waiting_reschedule":
                                CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getActivity(), getResources().getColor(R.color.green_xxl));
                                View invoiceDetail = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
                                TextView textView = (TextView) invoiceDetail.findViewById(R.id.message);
                                TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                                btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                                btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                textView.setText(getResources().getString(R.string.text_reschedule_information));
                                builder.setView(invoiceDetail);
                                builder.setTitle(getResources().getString(R.string.title_teledokter));
                                dialog = builder.create();
                                dialog.show();
                                break;
                            case "pending_reschedule":
                                intent = new Intent(getActivity(), TeledocRescheduleActivity.class);
                                intent.putExtra(TeledocRescheduleActivity.TAG_TELE_UUID, accessTeledocModel.results.data.tele_uuid);
                                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_NAME, accessTeledocModel.results.data.doctor_name);
                                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SPECIALIZATION, accessTeledocModel.results.data.doctor_specialization);
                                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SCHEDULE, accessTeledocModel.results.data.schedule);
                                startActivity(intent);
                                break;

                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            protected void onFailure(RetrofitError error) {
                doneloading();
                Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();

            }
        };

        NetworkManager.getNetworkService(getActivity().getApplication())
                .getAccessTeledoc(((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), cancelableCallback);

    }

    private void getdataChat() {
        doloading();
        cancelableCallback = new CancelableCallback<ChatRetrictionModel>() {


            @Override
            protected void onSuccess(ChatRetrictionModel chatRetrictionModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(chatRetrictionModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doneloading();
                        getdataChat();
                    }


                });
                if (isTokenValid) {
                    doneloading();
                    ((AppController) getActivity().getApplication()).getSessionManager().setChatRetriction(chatRetrictionModel.results);
                    if (chatRetrictionModel.results.status.equals("online")) {
                        boolean widgetWasActive = getActivity().stopService(new Intent(getActivity(), ChatWidgetService.class));
                        if (widgetWasActive) {
                            resumeChat();
                            return;
                        } else {
                            ((AppController)getActivity().getApplication()).setMixpanel("Open Chat Page");
                            Intent intent = new Intent(getActivity(), ChatDocterActivity.class);
                            startActivity(intent);
                        }

                    } else {
                        Intent intent = new Intent(getActivity(), ChatDocterActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                doneloading();
                Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        };
        NetworkManager.getNetworkService(getActivity().getApplication())
                .getChatRetriction(((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), currentLanguage, cancelableCallback);
    }

    private void accessLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getActivity(), "Access Location dibutuhkan",
                        Toast.LENGTH_SHORT).show();

                String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), perm,
                        RP_ACCESS_LOCATION);
            } else {
                String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), perm,
                        RP_ACCESS_LOCATION);
            }
        } else {
            Intent intent = new Intent(getActivity(), DirektoriActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions,
                                           @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RP_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), DirektoriActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "permission ditolak user",
                            Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cancelableCallback.cancel();
        } catch (Exception e) {

        }
    }

    private void doloading() {
        setClickableRecursive(getView());
        progressBar.setVisibility(View.VISIBLE);
    }

    private void doneloading() {
        setClickableRecursive(getView());
        progressBar.setVisibility(View.INVISIBLE);
    }


    public static void setClickableRecursive(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setClickableRecursive(group.getChildAt(i));
            }
        } else {
            view.setClickable(!view.isClickable());
        }
    }

    private void getBanner() {
        cancelableCallback = new CancelableCallback<BannerHomeModel>() {
            @Override
            protected void onSuccess(BannerHomeModel bannerHomeModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(bannerHomeModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getBanner();
                    }


                });
                if (isTokenValid) {
                    if (bannerHomeModel.results != null) {
                        ((AppController) getActivity().getApplication()).getSessionManager().setBannerHome(bannerHomeModel.results);
                        homeBanner = new ArrayList<BannerHomeModel.Result>();
                        for (int i = 0; i < bannerHomeModel.results.size(); i++) {
                            if (bannerHomeModel.results.get(i).description != null)
                                homeBanner.add(bannerHomeModel.results.get(i));
                        }

                        if (bannerHomeModel.results != null) {
                            if (bannerHomeModel.results.size() > 0)
                                setupViewPagerHeader(viewPagerHeader, bannerHomeModel.results);
                        }
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                doneloading();
                Boolean isbanner = ((AppController) getActivity().getApplication()).getSessionManager().isBanner();
                if (!isbanner) {
                    List<BannerHomeModel.Result> data = ((AppController) getActivity().getApplication()).getSessionManager().getBannerHome();
                    if (data != null) {
                        ((AppController) getActivity().getApplication()).getSessionManager().setBannerHome(data);
                        homeBanner = new ArrayList<BannerHomeModel.Result>();
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).description != null)
                                homeBanner.add(data.get(i));
                        }
                        if (data != null) {
                            if (data.size() > 0)
                                setupViewPagerHeader(viewPagerHeader, data);
                        }
                    }
                } else {
                    imageViewBanner.setVisibility(View.VISIBLE);
                    viewPagerHeader.setVisibility(View.GONE);
                }
            }
        };
        NetworkManager.getNetworkService(getActivity().getApplication())
                .getHomeBanner(((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), cancelableCallback);
    }

    private void setupViewPagerHeader(ViewPager viewPager, List<BannerHomeModel.Result> resources) {
        ArrayList<PracticeModel.Others> coverImagesList = new ArrayList<>();
        for (BannerHomeModel.Result data : resources) {
            PracticeModel model = new PracticeModel();
            PracticeModel.Others temp = model.new Others();
            temp.medium_image = data.image_url;
            coverImagesList.add(temp);
        }
        adapter = new ImageBannerAdapter(getActivity().getApplicationContext(), coverImagesList, R.layout.item_estore_photos);
        viewPager.setAdapter(adapter);
        viewPagerIndicator.setViewPager(viewPagerHeader);
        for (int i = 0; i < adapter.getCount() - 1; i++)
            viewPagerHeader.setCurrentItem(i, true);
        runnable = new Runnable() {
            public void run() {
                if (position >= adapter.getCount()) {
                    position = 0;
                } else {
                    position = position + 1;
                }
                viewPagerHeader.setCurrentItem(position, true);
                handler.postDelayed(runnable, 5000);
            }
        };

    }


    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 5000);
    }

}
