package net.imoran.auto.music.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.imoran.sdk.bean.bean.AudioAlbumBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhua.shi on 2018/4/18.
 */

public class SharedPreferencesUtil {

    //存储sharedpreferences文件名
    private static final String FILE_NAME = "moran_auto_music";
    private static final String FILE_NAME1 = "moran_auto_music1";


    /***
     * 保存数据到文件
     * @param context
     * @param data
     * @param key
     * */
    public static void saveData(Context context, String key, Object data) {
        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        }else if("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) data);
        }else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        }else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) data);
        }else if ("Long".equals(type)) {
            editor.putLong(key, (Long) data);
        }
        editor.commit();
    }

    /**
     * 从文件中读取数据
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String key, Object defValue){

        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (FILE_NAME, Context.MODE_PRIVATE);

        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)){
            return sharedPreferences.getInt(key, (Integer)defValue);
        }else if ("Boolean".equals(type)){
            return sharedPreferences.getBoolean(key, (Boolean)defValue);
        }else if ("String".equals(type)){
            return sharedPreferences.getString(key, (String)defValue);
        }else if ("Float".equals(type)){
            return sharedPreferences.getFloat(key, (Float)defValue);
        }else if ("Long".equals(type)){
            return sharedPreferences.getLong(key, (Long)defValue);
        }
        return null;
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public static <T> void setSearchDataList(Context context,String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    public static void clearDataList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 获取List
     * @param tag
     * @return
     */
    public static <T> List<T> getDataList(String tag,Context context) {
        List<T> datalist=new ArrayList<T>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;
    }

    /**
     * 获取List
     * @param tag
     * @return
     */
    public static  List<AudioAlbumBean.AudioAlbumEntity> getVuiDataList(String tag, Context context) {
        List<AudioAlbumBean.AudioAlbumEntity> datalist=new ArrayList<AudioAlbumBean.AudioAlbumEntity>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME1, Context.MODE_PRIVATE);
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<AudioAlbumBean.AudioAlbumEntity>>() {
        }.getType());
        return datalist;
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public static <T> void setDataList(Context context,String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }
}
