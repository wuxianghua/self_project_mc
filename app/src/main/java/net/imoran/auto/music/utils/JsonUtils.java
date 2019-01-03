package net.imoran.auto.music.utils;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.base.BaseSceneEntity;
import net.imoran.sdk.bean.bean.BaseBean;
import net.imoran.sdk.bean.bean.BaseReply;
import net.imoran.tv.common.lib.gson.GsonObjectDeserializer;
import net.imoran.tv.common.lib.utils.LogUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by love on 2018/3/1.
 */

public class JsonUtils {

    public static final String BEAN_PACKAGE_NAME = "net.imoran.sdk.bean.bean.";
    public static final String BEAN = "Bean";
    private static final String TAG = "JsonUtils";
    private static Gson gson = null;

    private JsonUtils() {
    }


    private static class GsonHolder {
        private static final Gson INSTANCE = GsonObjectDeserializer.produceGson();
    }

    /**
     * 获取Gson实例，由于Gson是线程安全的，这里共同使用同一个Gson实例
     */
    public static Gson getGsonInstance() {
        return GsonHolder.INSTANCE;
    }

    /**
     * 解析所有实体类
     *
     * @param json
     * @return
     */
    @Deprecated
    public static BaseContentEntity getBaseContentEntity(String json) {
        BaseContentEntity contentEntity = new BaseContentEntity();
        BaseSceneEntity baseSceneEntity = null;
        String semantic = null;
        String summary = null;
        BaseReply baseReply = null;
        try {
            BaseBean baseBean = getGsonInstance().fromJson(json, BaseBean.class);
            baseSceneEntity = getBaseSceneEntity(baseBean);
            semantic = getSemantic(baseBean);
            summary = getSummary(baseBean);
            baseReply = getBaseReply(baseBean);
        } catch (Exception e) {
            LogUtils.e("bobge", "getBaseContentEntity error:" + e.getMessage());
        }
        contentEntity.setBaseSceneEntity(baseSceneEntity);
        contentEntity.setSemantic(semantic);
        contentEntity.setSummary(summary);
        contentEntity.setBaseReply(baseReply);
        return contentEntity;
    }

    /**
     * 给确定场景的类初始化数据
     *
     * @param baseBean
     * @return
     */
    public static BaseSceneEntity getBaseSceneEntity(BaseBean baseBean) {
        BaseSceneEntity baseSceneEntity = new BaseSceneEntity();
        baseSceneEntity.setDomain(baseBean.getData().getDomain());
        baseSceneEntity.setIntention(baseBean.getData().getIntention());
        baseSceneEntity.setAction(baseBean.getData().getAction());
        baseSceneEntity.setTts(baseBean.getData().getContent().getTts());
        baseSceneEntity.setType(baseBean.getData().getContent().getType());
        baseSceneEntity.setDisplay(baseBean.getData().getContent().getDisplay());
        baseSceneEntity.setDisplay_guide(baseBean.getData().getContent().getDisplay_guide());
        baseSceneEntity.setQueryid(baseBean.getData().getQueryid());
        baseSceneEntity.setQuery(baseBean.getData().getQuery());
        String errorCode = baseBean.getData().getContent().getError_code();
        if (errorCode == null) {
            // 表示服务端的返回数据有问题
            // 用一个非0的数字表示错误
            errorCode = "666666";
            try {
                throw new Exception("服务端返回的数据没有error_code 字段");
            } catch (Exception e) {

            }
        }
        baseSceneEntity.setError_code(errorCode);
        return baseSceneEntity;
    }


    /**
     * 获取semantic
     *
     * @param baseBean
     * @return
     */
    public static String getSemantic(BaseBean baseBean) {
        String semantic = "";
        if (baseBean != null && baseBean.getData() != null && baseBean.getData().getContent() != null && baseBean.getData().getContent().getSemantic() != null) {
            semantic = getGsonInstance().toJson(baseBean.getData().getContent().getSemantic());
        }
        return semantic;
    }

    /**
     * 获取summary
     *
     * @param baseBean
     * @return
     */
    public static String getSummary(BaseBean baseBean) {
        String summary = "";
        if (baseBean != null && baseBean.getData() != null && baseBean.getData().getContent() != null && baseBean.getData().getContent().getSummary() != null) {
            summary = getGsonInstance().toJson(baseBean.getData().getContent().getSummary());
        }
        return summary;
    }

    /**
     * 获取BaseReply
     *
     * @param baseBean
     * @return
     */
    private static BaseReply getBaseReply(BaseBean baseBean) {
        BaseReply baseReply = null;
        try {
            String className = captureName(baseBean.getData().getContent().getType());
            LogUtils.d("c", baseBean.getData().getContent().getType() + ":" + className);
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                baseReply = (BaseReply) getGsonInstance().fromJson(baseBean.getData().getContent().getReply(), clazz);
            }
        } catch (Exception e) {
            LogUtils.e("bobge", "getBaseReply error:" + e.getMessage());
        }
        return baseReply;
    }


    public static String captureName(String name) {
        char[] cs = name.toCharArray();

        if (cs.length == 0) {
            return "";
        }
        cs[0] -= 32;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == '_' && i + 1 <= cs.length) {
                cs[i + 1] -= 32;
            }
        }
        String s = BEAN_PACKAGE_NAME + String.valueOf(cs).replace("_", "") + BEAN;
        return s;
    }

    public static Gson getGson() {
        return gson;
    }

    public static String objectToJson(Object object) {
        String jsonStr = null;
        try {
            if (gson != null) {
                jsonStr = gson.toJson(object);
            }
        } catch (Exception var3) {
            Log.e("JsonUtils", "object to json string error >>" + var3.getMessage());
        }

        return jsonStr;
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        ArrayList list = null;
        try {
            if (gson != null) {
                List<T> tempList = Arrays.asList((T[]) gson.fromJson(s, clazz));
                list = new ArrayList(tempList);
            }
        } catch (Exception var4) {
            Log.e("JsonUtils", "json string to list<?> error >>" + var4.getMessage());
        }

        return list;
    }

    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map map = null;

        try {
            if (gson != null) {
                Type type = (new TypeToken<Map<?, ?>>() {
                }).getType();
                map = (Map) gson.fromJson(jsonStr, type);
            }

            return map;
        } catch (Exception var3) {
            Log.e("JsonUtils", "json string to map error >>" + var3.getMessage());
            return map;
        }
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> cl) {
        T t = null;

        try {
            if (gson != null) {
                t = gson.fromJson(jsonStr, cl);
            }

            return t;
        } catch (Exception var4) {
            Log.e("JsonUtils", "json string to bean object error >>" + jsonStr);
            return t;
        }
    }

    public static boolean jsonToBoolean(String tempStr, String tempParam) {
        try {
            return (new JSONObject(tempStr)).optBoolean(tempParam, false);
        } catch (Exception var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public static String jsonToString(String tempStr, String tempParam) {
        String tempString = "";

        try {
            tempString = (new JSONObject(tempStr)).optString(tempParam, "");
            if ("null".equals(tempString)) {
                tempString = "";
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return tempString;
    }

    public static int jsonToInteger(String tempStr, String tempParam) {
        int tempInt = 0;

        try {
            tempInt = (new JSONObject(tempStr)).optInt(tempParam, 0);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return tempInt;
    }

    static {
        if (gson == null) {
            gson = new Gson();
        }

    }


    /**
     * 保存List
     *
     * @param dataList
     */
    public static String convertBeanToString(List dataList) {
        if (null == dataList || dataList.size() <= 0)
            return "";
        return gson.toJson(dataList);
    }

}
