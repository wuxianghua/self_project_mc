package net.imoran.auto.music.utils;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.personal.lib.LoginResp;
import net.imoran.personal.lib.SPHelper;

/**
 * 替换UserDataClient的类，新的数据请求
 */
public class UseLoginUtils {
    private static final String TAG = "UserDataClient";
    // 当前登录用户的信息
    private String currentUid = "426457803473813504";
    private static UseLoginUtils instance = null;

    public static UseLoginUtils getInstance() {
        if (instance == null) {
            synchronized (UseLoginUtils.class) {
                if (instance == null) {
                    instance = new UseLoginUtils();
                }
            }
        }
        return instance;
    }

    private UseLoginUtils() {
        init();
    }

    private void init() {
        LoginResp loginResp = SPHelper.GetUser(MusicApp.instance.getBaseContext());
        if (loginResp != null && loginResp.user_info != null) {
            currentUid = SPHelper.GetUser(MusicApp.instance.getBaseContext()).uid;
        } else {
            currentUid = "";
        }
    }

    public String getCurrentUid() {
        LoginResp loginResp = SPHelper.GetUser(MusicApp.instance.getBaseContext());
        if (loginResp != null && loginResp.user_info != null) {
            currentUid = SPHelper.GetUser(MusicApp.instance.getBaseContext()).uid;
        } else {
            currentUid = "";
        }
        return currentUid;
    }
}
