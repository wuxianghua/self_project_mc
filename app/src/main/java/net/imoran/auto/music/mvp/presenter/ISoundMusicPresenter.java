package net.imoran.auto.music.mvp.presenter;

public interface ISoundMusicPresenter {
    /**
     * 随机获取有声节目
     */
    void getRandomAudio();

    /**
     * 获取收藏结果
     */
    void getCollectionList();

    /**
     * 添加收藏
     *
     * @param track 有声节目名称
     */
    void addAudioCollection(String track);

    /**
     * 删除收藏
     *
     * @param track 有声节目名称
     */
    void delAudioCollection(String track);

    //获取节目库所有tag
    void getAllAudioTag();

    /**
     * 根据tag进行搜索
     *
     * @param tag 标签名称
     */
    void getSearchAudioTag(String tag);

    /**
     *  获取乐听节目类别
     */
    void getLetingCatalog();

    /**
     * 获取乐听推荐节目
     */
    void getLetingNewsRec();

    /**
     * 根据关键字进行搜索
     *
     * @param key 关键字名称
     */
    void getSearchAudioKey(String key);

    /**
     * 获取专辑详情
     *
     * @param albumid 专辑id
     */
    void getAudioProgramDetail(String albumid);

    /**
     * 根据catalogid获取乐听新闻列表
     * @param categoryId
     */
    void getLetingDetail(String categoryId);

    /**
     * 根据keyWord获取乐听新闻列表
     * @param keyWord
     */
    void getLetingDetailByKeyWord(String keyWord);
}
