package com.mecha.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProfileUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.mecha.app.UPDATE_PROFILE_HEADER".equals(intent.getAction())) {
            String avatarUrl = intent.getStringExtra("avatarUrl");
            String nickName = intent.getStringExtra("nickName");
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateProfileHeader(avatarUrl, nickName);
            } else {
                Log.e("ProfileUpdateReceiver", "Context is not an instance of MainActivity");
            }
        }
    }
}
