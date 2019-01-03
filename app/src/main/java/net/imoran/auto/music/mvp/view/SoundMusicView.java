package net.imoran.auto.music.mvp.view;


import net.imoran.auto.music.mvp.base.BaseView;

import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.LetingCatalogBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.PodcastCategoryBean;

import java.util.List;

/**
 * 有声节目契约类
 */
public interface SoundMusicView extends BaseView {
    /**
     * 随机获取20首有声节目
     *
     * @param audioProgramEntities 有声节目list
     */
    void showRandomResult(List<AudioProgramBean.AudioProgramEntity> audioProgramEntities);

    /**
     * 获取节目库的所有tag
     *
     * @param categoryEntities 节目库tag list
     */
    void showAllAudioTag(List<PodcastCategoryBean.PodcastCategoryEntity> categoryEntities);

    /**
     * 根据关键字搜索所得结果
     *
     * @param audioAlbumEntities 专辑 list
     */
    void showSearchResult(List<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities);

    /***
     * 获取乐听推荐结果
     * @param letingNewsBean
     */
    void showLetingNewsRec(LetingNewsBean letingNewsBean,String keyWord);

    /**
     * 获取乐听分类结果
     * @param catalogBean
     */
    void showLetingNewsCatalog(LetingCatalogBean catalogBean);

    /**
     * 根据Tag搜索所得结果
     *
     * @param audioAlbumBean 专辑对象
     */
    void showSearchTagResult(AudioAlbumBean audioAlbumBean);

    /**
     * 收藏结果
     *
     * @param audioProgramEntities 有声节目list
     * @param totalPage            总共收藏的节目数量
     */
    void showCollectionResult(List<AudioProgramBean.AudioProgramEntity> audioProgramEntities, int totalPage);

    /**
     * 专辑详情结果
     *
     * @param audioProgramBean 有声节目对象
     */
    void showAudioProgramDetail(AudioProgramBean audioProgramBean);

}
