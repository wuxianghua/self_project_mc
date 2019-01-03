package net.imoran.auto.music.utils;

import android.content.Context;
import android.content.Intent;

import net.imoran.auto.music.bean.SongsBean;
import net.imoran.auto.music.constant.BoardCastAction;


public class SysStatusBarUpdateUtils {
    /**
     * @param mContext
     * @param musicTitle
     * @param musicSwitch 是否要展示在车机的顶部
     */
    public static void updateSystemUiMusicTitle(Context mContext, String musicTitle, boolean musicSwitch) {
        try {
            Intent intent = new Intent(BoardCastAction.UPDATE_SYSTEM_UI_MUSIC_TITLE_ACTION);
            intent.putExtra("musicTitle", musicTitle);
            intent.putExtra("musicSwitch", musicSwitch);
            mContext.sendOrderedBroadcast(intent, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param mContext
     * @param songEntity
     * @param musicSwitch 是否要展示在车机的顶部
     */
    public static void updateSystemUiMusicTitle(Context mContext, SongsBean songEntity, boolean musicSwitch) {
        String musicTitle = "";
        if (songEntity != null) {
            if (songEntity.getSinger() != null && songEntity.getSinger().size() > 0
                    && !"".equals(songEntity.getSinger().get(0))
                    && !"".equals(songEntity.getSinger().get(0).trim())) {
                musicTitle = songEntity.getName() + " - " + songEntity.getSinger().get(0);
            } else {
                if (songEntity.getName() == null) {
                    musicTitle = "无标题";
                } else {
                    musicTitle = songEntity.getName();
                }
            }
        }
        try {
            Intent intent = new Intent(BoardCastAction.UPDATE_SYSTEM_UI_MUSIC_TITLE_ACTION);
            intent.putExtra("musicTitle", musicTitle);
            intent.putExtra("musicSwitch", musicSwitch);
            mContext.sendOrderedBroadcast(intent, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param mContext
     * @param playStatus playStatus int 取 0 或1 ，表示播放或者暂停
     */
    public static void updateSystemUiMusicPlayState(Context mContext, int playStatus) {
        try {
            Intent intent = new Intent(BoardCastAction.UPDATE_SYSTEM_UI_MUSIC_TITLE_ACTION);
            intent.putExtra("playStatus", playStatus);
            mContext.sendOrderedBroadcast(intent, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
