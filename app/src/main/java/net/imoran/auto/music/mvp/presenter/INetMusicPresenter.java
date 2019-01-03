package net.imoran.auto.music.mvp.presenter;

import android.content.Context;

import net.imoran.auto.music.player.model.SongModel;

public interface INetMusicPresenter {
    /**
     * 首次进入加载推荐音乐
     */
    void loadNetMusic();

    /**
     * 收藏 取消收藏 音乐
     *
     * @param songModel 音乐的
     * @param isCollect 是否收藏，true 收藏 false 取消收藏
     */
    void collectSong(SongModel songModel, boolean isCollect);


    /**
     * 得到音乐库的标签
     */
    void getNetType();

    /**
     * 根据标签加载音乐
     *
     * @param type 音乐标签
     */
    void loadNetMusicByType(String type);


    /**
     * 获得收藏列表
     */
    void loadCollectedMusic();


    /**
     * 保存本地搜索记录
     *
     * @param context 上下文参数
     * @param keyWord 搜索的关键字
     */
    void saveHistoryKeyword(Context context, String keyWord);


    /**
     * 加载本地历史搜索记录
     *
     * @param context 上下文参数
     */
    void loadHistoryKeyword(Context context);


    /**
     * 加载最热的搜索词
     */
    void loadHotSearchKeyword();

    /**
     * 根据关键字搜索音乐
     *
     * @param keyWord 搜索关键字
     */
    void searchMusicByKeyWord(String keyWord);


    /**
     * 根据页面加载音乐
     *
     * @param page   页码
     * @param pageId pageID
     */
    void loadMusicByPageNum(int page, String pageId, boolean isHeadPlay);


    void acrByPcmFile(String filePath);
}
