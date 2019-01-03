package net.imoran.auto.music.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gejiangbo on 16/6/12.
 * 权限校验工具类
 */
public class PermissionUtils {

    public static final String[] REQUIRE_PERMISSIONS = new String[]{"android.permission.RECORD_AUDIO",
            "android.permission.READ_PHONE_STATE",
            "android.permission.WRITE_CONTACTS", "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static PermissionUtils instance;

    private PermissionUtils() {
    }

    public static PermissionUtils getInstance() {
        if (instance == null) {
            synchronized (PermissionUtils.class) {
                if (instance == null)
                    instance = new PermissionUtils();
            }
        }
        return instance;
    }

    public void needCheckPremission(Activity activity, int requestCode, String[] premissions, CheckPre checkPre) {
        requestPremission(activity, requestCode, premissions, checkPre);
    }

    public void needCheckPremission(Fragment fragment, int requestCode, String[] premissions, CheckPre checkPre) {
        requestPremission(fragment, requestCode, premissions, checkPre);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPremission(Object obj, int requestCode, String[] premissions, CheckPre checkPre) {
        if (!isVersionM()) {
            checkPre.checkPermSuccess();
            return;
        }
        List<String> premission = findDefindedPre(getActivity(obj), premissions);
        if (premission != null && premission.size() > 0) {
            if (obj instanceof Activity) {
                ((Activity) obj).requestPermissions(premission.toArray(new String[premission.size()]), requestCode);
            } else if (obj instanceof Fragment) {
                ((Fragment) obj).requestPermissions(premission.toArray(new String[premission.size()]), requestCode);
            }
        } else {
            checkPre.checkPermSuccess();
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
                                                  int[] grantResults, CheckPre checkPremission) {
        requestResult(activity, requestCode, permissions, grantResults, checkPremission);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                  int[] grantResults, CheckPre checkPremission) {
        requestResult(fragment, requestCode, permissions, grantResults, checkPremission);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions,
                                      int[] grantResults, CheckPre checkPremission) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.size() > 0) {
        } else {
            //成功
            if (checkPremission != null) {
                checkPremission.checkPermSuccess();
            }
        }
    }


    //查找
    @TargetApi(Build.VERSION_CODES.M)
    public List<String> findDefindedPre(Context context, String[] premissions) {
        List<String> defindPremissions = new ArrayList<>();
        for (String pre : premissions) {
            if (context.checkSelfPermission(pre) != PackageManager.PERMISSION_GRANTED) {
                Log.i("bobge", pre);
                defindPremissions.add(pre);
            }
        }
        return defindPremissions;

    }

    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        }
        return null;
    }

    public static boolean isVersionM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public boolean isNeedCheckPermission(Context context, String[] premissions) {
        List<String> deniedPer = findDefindedPre(context, premissions);
        if (deniedPer != null && deniedPer.size() > 0) {
            return true;
        }
        return false;
    }

    public interface CheckPre {
        void checkPermSuccess();
    }


    private void gotoSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    /**
     * 如果需要判断 WRITE_SETTINGS 权限,请使用hasPermission
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            int permissionState = ContextCompat.checkSelfPermission(context,
                    permission);
            boolean notHave = permissionState != PackageManager.PERMISSION_GRANTED;
            if (notHave) {
                return false;
            }
        }
        return true;
    }
}
