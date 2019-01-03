package net.imoran.auto.music.utils;

import android.content.Context;
import android.content.Intent;

import net.imoran.auto.music.ui.MainActivity;


public class RestartCrashHandler implements Thread.UncaughtExceptionHandler {
    private static RestartCrashHandler crashHandler = new RestartCrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static RestartCrashHandler getInstance() {
        return crashHandler;
    }

    public void init(Context context) {
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
