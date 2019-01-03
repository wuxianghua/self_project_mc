package net.imoran.auto.music.net;

import android.content.Context;

import net.imoran.auto.music.app.MusicApi;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.personal.lib.SPHelper;
import net.imoran.tv.common.lib.utils.PhoneUtils;
import net.imoran.tv.common.lib.utils.QueryUtils;

import java.util.HashMap;

/**
 * Created by love on 2018/4/10.
 */

public class RequestParamUtils {

    private static HashMap<String, String> requestHashMap = new HashMap<>();
    private static String requestString = "";

    private static RequestParamUtils requestParamUtils;

    public static RequestParamUtils getInstance() {
        if (requestParamUtils == null) {
            synchronized (RequestParamUtils.class) {
                if (requestParamUtils == null) {
                    requestParamUtils = new RequestParamUtils();
                }
            }
        }

        if (requestHashMap == null) {
            synchronized (RequestParamUtils.class) {
                if (requestHashMap == null) {
                    requestHashMap = new HashMap<>();
                }
            }
        }
        requestString = "";
        requestHashMap.clear();
        return requestParamUtils;
    }

    /**
     * @return 加入通用的参数
     */
    public RequestParamUtils addCommonParamsToMap(Context mContext) {
        requestHashMap.put("key", MusicApi.key);
        requestHashMap.put("deviceid", PhoneUtils.generateDeviceId(mContext));
        requestHashMap.put("queryid", QueryUtils.genereateQueryId());
        requestHashMap.put("longitude", "116.339773");
        requestHashMap.put("latitude", "39.99830");

        if (SPHelper.GetUser(mContext) != null) {
            requestHashMap.put("moraccountid", (SPHelper.GetUser(mContext) == null ? "" : SPHelper.GetUser(mContext).uid));
        }
        return this;
    }

    /**
     * @param key
     * @param value
     * @return 加入特殊的参数
     */
    public RequestParamUtils addSingleParamToMap(String key, String value) {
        requestHashMap.put(key, value);
        return this;
    }

    public HashMap<String, String> getRequestHashMap() {
        return requestHashMap;
    }

    /**
     * @return 加入通用的参数
     */
    public RequestParamUtils addCommonParamsToString() {
        requestString += "?";
        requestString += ("key=" + MusicApi.key);
        requestString += ("&" + "deviceid=" + PhoneUtils.generateDeviceId(MusicApp.instance));
        requestString += ("&" + "queryid=" + QueryUtils.genereateQueryId());
        requestString += ("&" + "longitude=" + "116.339773");
        requestString += ("&" + "latitude=" + "39.99830");
        if (SPHelper.GetUser(MusicApp.instance) != null) {
            requestString += ("&" + "moraccountid=" + SPHelper.GetUser(MusicApp.instance).uid);
        }
        return this;
    }

    /**
     * @param key
     * @param value
     * @return 加入特殊的参数
     */
    public RequestParamUtils addSingleParamToString(String key, String value) {
        requestString += ("&" + key + "=" + value);
        return this;
    }

    public String getRequestString() {
        return requestString;
    }


}
