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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(instanceID.toString(), token);

            // Subscribe to topic channels
            subscribeTopics(token);
            ((AppController) getApplication()).getSessionManager().setGcmKey(token);
            sharedPreferences.edit().putBoolean(AppConstant.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            sharedPreferences.edit().putBoolean(AppConstant.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConstant.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String instanceID, final String token) {
        NetworkManager.getNetworkService(getApplicationContext()).postInstance(((AppController) getApplication()).getSessionManager().getToken(), "android", instanceID, token, new Callback<GeneralCheckModel>() {
            @Override
            public void success(GeneralCheckModel generalCheckModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(generalCheckModel.messages, response,getBaseContext(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        sendRegistrationToServer(instanceID,token);
                    }


                });

                if (isTokenValid) {
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
