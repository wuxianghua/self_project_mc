package net.imoran.auto.music.ui.fragment.sound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.presenter.impl.SoundMusicPresenter;
import net.imoran.auto.music.mvp.view.SoundMusicView;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.rripc.lib.ResponseCallback;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.LetingCatalogBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.PodcastCategoryBean;

import java.util.List;

/**
 * Created by xinhuashi on 2018/8/24.
 */

public abstract class SoundBaseFragment extends BaseFragment<SoundMusicPresenter> implements SoundMusicView {
    protected Gson gson = new Gson();
    SoundMainFragment mainFragment;

    @Override
    protected SoundMusicPresenter createPresenter() {
        return new SoundMusicPresenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = (SoundMainFragment) getParentFragment();
    }


    @Override
    public void showRandomResult(List<AudioProgramBean.AudioProgramEntity> programEntities) {

    }

    @Override
    public void showAllAudioTag(List<PodcastCategoryBean.PodcastCategoryEntity> podcast_category) {

    }

    @Override
    public void showSearchResult(List<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities) {

    }

    @Override
    public void showSearchTagResult(AudioAlbumBean audioAlbumBean) {

    }

    @Override
    public void showCollectionResult(List<AudioProgramBean.AudioProgramEntity> audioProgramEntities, int totalPage) {

    }

    @Override
    public void showAudioProgramDetail(AudioProgramBean audioProgramBean) {

    }

    @Override
    public void showLetingNewsRec(LetingNewsBean letingNewsBean,String keyWord) {

    }

    @Override
    public void showLetingNewsCatalog(LetingCatalogBean catalogBean) {

    }

    /**********************************通用的VUI控制*********************************
     *
     */
    @Override
    public void vuiPause() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPause();
    }

    @Override
    public void vuiPlay() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPlay();
    }

    @Override
    public void vuiNext() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiNext();
    }

    @Override
    public void vuiCancelCollecSong() {
        if (mainFragment != null) {
            mainFragment.getPlayFragment().vuiCancelCollecSong();
        }
    }

    @Override
    public void vuiCollectSong() {
        if (mainFragment != null) {
            mainFragment.getPlayFragment().vuiCollectSong();
        }
    }

    @Override
    public void vuiPrevious() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPrevious();
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
    public void vuiPlayList(int total, List list) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.switchToNetFragment(total, list);
    }

    @Override
    public void vuiHotWords(String words) {

    }
}
