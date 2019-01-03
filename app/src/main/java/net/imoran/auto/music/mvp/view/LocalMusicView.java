package net.imoran.auto.music.mvp.view;

import android.content.Context;

import net.imoran.auto.music.mvp.base.BaseView;
import net.imoran.auto.music.player.model.SongModel;

import java.util.List;

/**
 * 本地音乐
 */
public interface LocalMusicView extends BaseView {

    /**
     * 加载音乐成功
     *
     * @param total       总数
     * @param currentPage 当前页码
     * @param list        音乐列表
     */
    void loadLocalMusicSuccess(int total, int currentPage, List<SongModel> list);

    /**
     * 加载音乐失败
     *
     * @param errorMsg 失败信息
     */
    void onLoadError(String errorMsg);

}
