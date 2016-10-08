/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.konsula.app.gcm;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.konsula.app.LoginActivity;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.ui.activity.teledokter.TeledocRescheduleActivity;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;

public class MyGcmListenerService extends GcmListenerService {
    private Bundle bundle;
    private String togo;


    private static final String TAG_STATUS = "status";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_PAGE = "page";
    private static final String TAG_TYPE = "type";
    private static final String TAG_SPECIALIZATION = "specialization";
    private static final String TAG_TELE_UUID = "tele_uuid";
    private static final String TAG_DOC_NAME = "doctor_name";
    private static final String TAG_MESSAGE = "message";
    private android.app.AlertDialog dialog;

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        togo = data.getString("type");
        bundle = data;
//        Settings.System.putString(this.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED, message);

        Log.d(TAG, String.valueOf(data));
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire(3000);
        wl.release();

        //kalo misalnya app kebuka, message diterima disini.

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        handleGCM(bundle);
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.togo, togo);
        intent.putExtra(MainActivity.bundle, bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Konsula")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setLights(0xff00ff00, 300, 100)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{1000, 1000})
                .setTicker("New messages from Konsula!")
                .setContentIntent(pendingIntent);

        //notificationBuilder.setOngoing(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }


    private void handleGCM(Bundle bundle) {
        try {
            Bundle data = bundle;
            String message = data.getString(TAG_MESSAGE, "");
            final String doc_name = data.getString(TAG_DOC_NAME, "");
            final String schedule = data.getString(TAG_SCHEDULE, "");
            final String specialization = data.getString(TAG_SPECIALIZATION, "");
            final String status = data.getString(TAG_STATUS, "");
            String tele_uuid = data.getString(TAG_TELE_UUID, "");
            String page = data.getString(TAG_PAGE, "");
            String type = data.getString(TAG_TYPE, "");
            if (type.equals("dialog")) {
                if (page.equals("teledoc-confirmed")) {
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_confirm, null);
                    TextView textViewname = (TextView) invoiceDetail.findViewById(R.id.tvNamaDokter);
                    TextView textViewspeciality = (TextView) invoiceDetail.findViewById(R.id.etSpeciality);
                    TextView textViewdate = (TextView) invoiceDetail.findViewById(R.id.tvDate);
                    TextView textViewstatus = (TextView) invoiceDetail.findViewById(R.id.text_status);
                    TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                    btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    textViewname.setText(doc_name);
                    textViewspeciality.setText(specialization);
                    textViewdate.setText(schedule);
                    textViewstatus.setText(status.toUpperCase());
                    builder.setView(invoiceDetail);
                    builder.setTitle(getResources().getString(R.string.title_teledokter));
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
                if (page.equals("teledoc-cancel")) {
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
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
                    textView.setText(getResources().getString(R.string.text_teledoc_cancel));
                    builder.setView(invoiceDetail);
                    builder.setTitle(getResources().getString(R.string.title_teledokter));
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
                if (page.equals("teledoc-reschedule")) {
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
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
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
            } else if (type.equals("intent") && (page.equals("teledoc-confirmation-reschedule"))) {
                Intent intent = new Intent(this, TeledocRescheduleActivity.class);
                intent.putExtra(TeledocRescheduleActivity.TAG_TELE_UUID, tele_uuid);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_NAME, doc_name);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SPECIALIZATION, specialization);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SCHEDULE, schedule);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


        } catch (Exception e) {
            Log.e("ee", e.getMessage());
            Log.e("ee", String.valueOf(e));


        }
    }
}
