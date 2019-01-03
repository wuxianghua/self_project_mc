package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.bean.SearchKeyWordBean;
import net.imoran.auto.music.mvp.presenter.impl.NetMusicPresenter;
import net.imoran.auto.music.mvp.view.NetMusicView;
import net.imoran.auto.music.network.bean.AcrMusicBean;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.auto.music.vui.IVUICallBack;
import net.imoran.tv.common.lib.utils.LogUtils;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;

public abstract class NetBaseFragment extends BaseFragment<NetMusicPresenter> implements NetMusicView, IVUICallBack {
    NetMainFragment mainFragment;

    @Override
    protected NetMusicPresenter createPresenter() {
        return NetMusicPresenter.newInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = (NetMainFragment) getParentFragment();
    }

    protected abstract int getLayoutRes();

    //网络请求相关回调
    @Override
    public void getMusicSuccess(int total, List<SongModel> list) {

    }

    @Override
    public void getMusicError(String errorMsg) {

    }

    @Override
    public void collectSongSuccess(SongModel songModel, boolean isCollect) {

    }

    @Override
    public void loadNetTypeSuccess(List<NetTypeBean> languageList, List<NetTypeBean> styleList, List<NetTypeBean> EmotionList) {

    }

    @Override
    public void loadCollectedMusicSuccess(int total, List<SongModel> list) {

    }

    @Override
    public void loadMusicByTypeSuccess(int total, List<SongModel> list) {

    }

    @Override
    public void loadHistoryKeywordSuccess(List<SearchKeyWordBean> searchBeans) {

    }

    @Override
    public void loadHotSearchKeywordSuccess(List<SearchKeyWordBean> list) {

    }

    @Override
    public void searchMusicByKeyWordSuccess(int total, List<SongModel> list) {

    }

    @Override
    public void loadMusicByPageNumSuccess(int currentPage, List<SongModel> list, boolean isHeadPlay) {

    }

    @Override
    public void onAcrResult(List<AcrMusicBean> musicBeans) {

    }

    @Override
    public void onNetError(String errorMsg) {
        ToastUtil.shortShow(activity, errorMsg);
    }

    @Override
    public void loadMusicByPageNumError(String errorMsg) {
        ToastUtil.shortShow(activity, errorMsg);
    }

    @Override
    public void netRequestQueryId(String queryId) {

    }

    /**********************************通用的VUI控制*********************************
     *
     */

    @Override
    public void vuiPlay() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPlay();
    }

    @Override
    public void vuiPause() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPause();
    }

    @Override
    public void vuiNext() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiNext();
    }

    @Override
    public void vuiPrevious() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPrevious();
    }

    @Override
    public void vuiPlayList(int total, List<SongModel> list) {
        if (mainFragment != null) {
            mainFragment.getPlayFragment().vuiPlayList(total, list);
            popTo(NetPlayFragment.class, false);
        }
    }

    @Override
    public void vuiFastBack(long milliseconds) {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiFastBack(milliseconds);
    }

    @Override
    public void vuiFastForward(long milliseconds) {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiFastForward(milliseconds);
    }

    @Override
    public void vuiChangeRepeatMode(int mode) {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiChangeRepeatMode(mode);
    }

    @Override
    public void vuiCollectSong() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiCollectSong();
    }

    @Override
    public void vuiCancelCollecSong() {
        super.vuiCancelCollecSong();
        if (mainFragment != null) {
            mainFragment.getPlayFragment().vuiCancelCollecSong();
        }
    }

    @Override
    public void vuiListQueryId(String queryId) {
        LogUtils.e("listRequestId", " queryId = " + queryId);
        ContextSyncManager.getInstant().setCurrentListQueryId(queryId);
    }
}
