package net.imoran.auto.music.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class NetworkUtils {
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_UNKNOWN = 5;
    public static final int NETWORK_NO = -1;
    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN = 18;

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    public static void openWirelessSettings(Context context) {
        if (Build.VERSION.SDK_INT > 10) {
            context.startActivity(new Intent("android.settings.SETTINGS"));
        } else {
            context.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
        }

    }

    @SuppressLint("MissingPermission")
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        return cm.getActiveNetworkInfo();
    }

    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static boolean is4G(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == 13;
    }

    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        return cm != null && cm.getActiveNetworkInfo().getType() == 1;
    }

    public static String getNetworkOperatorName(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    public static int getPhoneType(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        return tm != null ? tm.getPhoneType() : -1;
    }

    public static int getNetWorkType(Context context) {
        int netType = -1;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            if (info.getType() == 1) {
                netType = 1;
            } else if (info.getType() == 0) {
                switch (info.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                    case 16:
                        netType = 2;
                        break;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                    case 17:
                        netType = 3;
                        break;
                    case 13:
                    case 18:
                        netType = 4;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (!subtypeName.equalsIgnoreCase("TD-SCDMA") && !subtypeName.equalsIgnoreCase("WCDMA") && !subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = 5;
                        } else {
                            netType = 3;
                        }
                }
            } else {
                netType = 5;
            }
        }

        return netType;
    }

    public static String getNetWorkTypeName(Context context) {
        switch (getNetWorkType(context)) {
            case -1:
                return "NETWORK_NO";
            case 0:
            default:
                return "NETWORK_UNKNOWN";
            case 1:
                return "NETWORK_WIFI";
            case 2:
                return "NETWORK_2G";
            case 3:
                return "NETWORK_3G";
            case 4:
                return "NETWORK_4G";
        }
    }
}
