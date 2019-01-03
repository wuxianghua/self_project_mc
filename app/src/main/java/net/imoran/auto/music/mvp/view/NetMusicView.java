package net.imoran.auto.music.mvp.view;

import android.content.Context;

import net.imoran.auto.music.mvp.base.BaseView;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.bean.SearchKeyWordBean;
import net.imoran.auto.music.network.bean.AcrMusicBean;
import net.imoran.auto.music.player.model.SongModel;

import java.util.List;

/**
 * 网络音乐
 */
public interface NetMusicView extends BaseView {

    /**
     * 加载推荐音乐成功
     *
     * @param list 音乐列表
     */
    void getMusicSuccess(int total,List<SongModel> list);

    /**
     * 加载音乐失败
     *
     * @param errorMsg 错误信息
     */
    void getMusicError(String errorMsg);

    /**
     * 收藏操作成功
     */
    void collectSongSuccess(SongModel songModel, boolean isCollect);

    /**
     * 得到音乐库的标签
     *
     * @param languageList 语种
     * @param styleList    风格
     * @param EmotionList  感情
     */
    void loadNetTypeSuccess(List<NetTypeBean> languageList, List<NetTypeBean> styleList, List<NetTypeBean> EmotionList);

    /**
     * 根据type加载音乐成功
     *
     * @param total 总数
     * @param list  音乐列表
     */
    void loadMusicByTypeSuccess(int total, List<SongModel> list);

    /**
     * 加载收藏音乐成功
     *
     * @param total 总数
     * @param list  音乐列表
     */
    void loadCollectedMusicSuccess(int total, List<SongModel> list);

    /**
     * 加载历史搜索成功
     *
     * @param list 搜索关键字列表
     */
    void loadHistoryKeywordSuccess(List<SearchKeyWordBean> list);

    /**
     * 加载热词搜索成功
     *
     * @param list 热词列表
     */
    void loadHotSearchKeywordSuccess(List<SearchKeyWordBean> list);

    /**
     * 搜索成功
     *
     * @param total 总数
     * @param list  音乐列表
     */
    void searchMusicByKeyWordSuccess(int total, List<SongModel> list);

    /**
     * 分页加载数据
     *
     * @param currentPage 页码
     * @param list        音乐列表
     */
    void loadMusicByPageNumSuccess(int currentPage, List<SongModel> list, boolean isHeadPlay);

    /**
     * 网络出错
     *
     * @param errorMsg 错误信息
     */
    void onNetError(String errorMsg);

    /**
     * 分页加载数据出错
     *
     * @param errorMsg 错误信息
     */
    void loadMusicByPageNumError(String errorMsg);

    void netRequestQueryId(String queryId);

    void onAcrResult(List<AcrMusicBean> musicBeans);
}