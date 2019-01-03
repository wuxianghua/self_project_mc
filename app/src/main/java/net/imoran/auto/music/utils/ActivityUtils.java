package net.imoran.auto.music.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by love on 2018/2/13.
 */

public class ActivityUtils {
    /**
     * @param mContext 上下文
     * @param action   应用的包名
     *                 这个时候要打开的activity必须有 <category android:name="android.intent.category.DEFAULT" />
     */
    public static void startActivityByAction(Context mContext, String action) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            // 启动目标应用
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "没有找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param mContext
     * @param intent
     */
    public static void startActivityByIntent(Context mContext, Intent intent) {
        try {
            // 启动目标应用
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "没有找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param mContext    上下文
     * @param packageName 应用的包名
     */
    public static void startActivityByPackageName(Context mContext, String packageName) {

        try {
            Intent resolveIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
            // 启动目标应用
            mContext.startActivity(resolveIntent);
        } catch (Exception e) {
            Toast.makeText(mContext, "没有找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param mContext
     * @param packageName  应用的包名
     * @param activityName 应用要启动的activityName
     */
    public static void startActivityByPackageNameAndActivityName(Context mContext, String packageName, String activityName) {

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, activityName);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "没有找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 回到桌面
     *
     * @param mContext
     */
    public static void backToHome(Context mContext) {
        Intent intentHome = new Intent();
        intentHome.setAction(Intent.ACTION_MAIN);
        intentHome.addCategory(Intent.CATEGORY_HOME);
        mContext.startActivity(intentHome);
    }


    /**
     * @param mContext
     * @param action   发送广播
     */
    public static void sendBroadcast(Context mContext, String action) {
        Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    /**
     * @param mContext 上下文
     * @param action   应用的包名
     *                 这个时候要打开的activity必须有 <category android:name="android.intent.category.DEFAULT" />
     */
    public static void startServerByAction(Context mContext, String action) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            // 启动目标应用
            mContext.startService(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "没有找到服务", Toast.LENGTH_SHORT).show();
        }
    }
}
