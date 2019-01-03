package net.imoran.auto.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.imoran.auto.music.vui.VUIManager;

public class FlyKeyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "FlyKeyBroadcastReceiver";
    public static final String ACTION_FLY = "net.imoran.fly.key.event.action";

    public FlyKeyBroadcastReceiver() {
    }

    public void registerSelf(@NonNull Context context) {
        IntentFilter filter = new IntentFilter(ACTION_FLY);
        context.registerReceiver(this, filter);
    }

    public void unregisterSelf(@NonNull Context context) {
        context.unregisterReceiver(this);
    }


    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("flyKeyEvent")) {
            int keyCode = bundle.getInt("flyKeyEvent", -1);
            VUIManager.getInstance().parseFlyContent(keyCode);
        }
    }
}
