package net.imoran.auto.music.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by love on 2018/7/5.
 * 上传本地数据
 */

public class UpLoadDataUtils {

    /**
     * 上传本地数据
     *
     * @param mContext
     * @param pageid
     * @param musicList
     */
    public static void upLoadLocalMusicData(Context mContext, String pageid, String musicList) {
        Intent intent = new Intent("net.imoran.action.updatemusiclist");
        intent.putExtra("pageid", pageid);
        intent.putExtra("musicList", musicList);
        mContext.sendBroadcast(intent);
    }
}
