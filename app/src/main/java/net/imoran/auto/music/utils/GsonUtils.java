package net.imoran.auto.music.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Horizony on 2018/3/14.
 */

public class GsonUtils {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    /**
     * <pre>
     * Json格式转换, 由JSON字符串转化到制定类型T
     *
     * @param json
     * @param cls
     * @return T
     *
     * <pre>
     */
    public static <T> T convertObj(String json, Class<T> cls) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, cls);
    }

    /**
     * <pre>
     * java对象转化JSON
     *
     * @return String
     *
     * <pre>
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return gson.toJson(obj);
    }

    public static String getJsonObjectAsString(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? null : jsonElement.getAsString();
    }

    public static JsonObject getJsonObjectChild(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? null : jsonElement.getAsJsonObject();
    }

    public static boolean getJsonObjectAsBoolean(JsonObject jsonObject, String name) {
        if (jsonObject == null || StringUtils.isEmpty(name)) {
            return false;
        }
        JsonElement jsonElement = jsonObject.get(name);
        return (jsonElement == null) ? false : jsonElement.getAsBoolean();
    }
}
