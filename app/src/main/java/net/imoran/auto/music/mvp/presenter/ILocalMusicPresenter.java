package net.imoran.auto.music.mvp.presenter;

import android.content.Context;

public interface ILocalMusicPresenter {
    /**
     * 加载全部本地音乐
     *
     * @param context 上下文参数
     */
    void loadLocalMusicAll(Context context);


    /**
     * 根据页码加载本地音乐
     *
     * @param page 页码
     */
    void loadLocalMusicByPageNum(int page);

    /**
     * 搜索本地音乐
     *
     * @param keyWord 所搜关键字
     */
    void searchMusicByKeyWord(String keyWord);

    /**
     * 本地音乐搜索结果分页
     *
     * @param page 页码
     */
    void searchMusicByPageNum(int page);
}
