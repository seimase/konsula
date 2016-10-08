package com.konsula.app.service.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zopim.android.sdk.api.ChatSession;

public class ChatTimeoutReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent != null && ChatSession.ACTION_CHAT_SESSION_TIMEOUT.equals(intent.getAction())) {
        }
    }
}
