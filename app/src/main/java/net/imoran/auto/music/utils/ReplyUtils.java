package net.imoran.auto.music.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.base.BaseSceneEntity;
import net.imoran.sdk.bean.bean.BaseBean;
import net.imoran.sdk.bean.bean.BaseReply;
import net.imoran.tv.common.lib.utils.LogUtils;

public class ReplyUtils {
    private static final String TAG = "ReplyUtils";
    private static Gson gson;

    public ReplyUtils() {
    }

    public static BaseContentEntity createResponseFromJson(String json) {
        BaseReply baseReply = null;
        BaseSceneEntity baseSceneEntity = null;
        BaseBean baseBean = (BaseBean) gson().fromJson(json, BaseBean.class);
        if (baseBean.getData() != null && baseBean.getData().getContent() != null) {
            if (baseBean.getData().getContent().getType() != null && !"chitchat".equals(baseBean.getData().getContent().getType())) {
                try {
                    String className = captureName(baseBean.getData().getContent().getType());
                    LogUtils.d("c", baseBean.getData().getContent().getType() + ":" + className);
                    Class clazz = Class.forName(className);
                    if (clazz != null) {
                        baseReply = (BaseReply) gson().fromJson(baseBean.getData().getContent().getReply(), clazz);
                    }
                } catch (JsonSyntaxException var6) {
                    LogUtils.e("ReplyUtils", "JsonSyntaxException :" + var6.getMessage());
                } catch (ClassNotFoundException var7) {
                    LogUtils.e("ReplyUtils", "run: ClassNotFoundException : " + var7.getMessage());
                }
            }

            baseSceneEntity = getBaseSceneEntity(baseBean);
            BaseContentEntity baseContentEntity = getBaseContentEntity(baseReply, baseSceneEntity, getSummary(baseBean), getSemantic(baseBean));
            return baseContentEntity;
        } else {
            return null;
        }
    }

    public static String getSummary(BaseBean baseBean) {
        String summary = "";
        if (baseBean != null && baseBean.getData() != null && baseBean.getData().getContent() != null && baseBean.getData().getContent().getSummary() != null) {
            summary = gson().toJson(baseBean.getData().getContent().getSummary());
        }

        return summary;
    }

    public static String getSemantic(BaseBean baseBean) {
        String semantic = "";
        if (baseBean != null && baseBean.getData() != null && baseBean.getData().getContent() != null && baseBean.getData().getContent().getSemantic() != null) {
            semantic = gson().toJson(baseBean.getData().getContent().getSemantic());
        }

        return semantic;
    }

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
        baseSceneEntity.setTotal_count(baseBean.getData().getContent().getTotal_count());
        String errorCode = baseBean.getData().getContent().getError_code();
        if (errorCode == null) {
            errorCode = "666666";

            try {
                throw new Exception("服务端返回的数据没有error_code 字段");
            } catch (Exception var4) {
                Log.e("ReplyUtils", "getBaseSceneEntity: error: " + var4.getMessage());
            }
        }

        baseSceneEntity.setError_code(errorCode);
        return baseSceneEntity;
    }

    public static BaseContentEntity getBaseContentEntity(BaseReply baseReply, BaseSceneEntity baseSceneEntity, String summary, String semantic) {
        BaseContentEntity baseContentEntity = new BaseContentEntity();
        baseContentEntity.setBaseReply(baseReply);
        baseContentEntity.setBaseSceneEntity(baseSceneEntity);
        baseContentEntity.setSummary(summary);
        baseContentEntity.setSemantic(semantic);
        return baseContentEntity;
    }

    private static Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }

        return gson;
    }

    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        if (cs.length == 0) {
            return "";
        } else {
            cs[0] = (char) (cs[0] - 32);

            for (int i = 0; i < cs.length; ++i) {
                if (cs[i] == '_' && i + 1 <= cs.length) {
                    cs[i + 1] = (char) (cs[i + 1] - 32);
                }
            }

            String s = "net.imoran.sdk.bean.bean." + String.valueOf(cs).replace("_", "") + "Bean";
            return s;
        }
    }
}
