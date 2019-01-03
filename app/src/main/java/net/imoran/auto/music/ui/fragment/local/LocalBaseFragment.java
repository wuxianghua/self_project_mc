package net.imoran.auto.music.ui.fragment.local;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.presenter.impl.LocalMusicPresenter;
import net.imoran.auto.music.mvp.view.LocalMusicView;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.auto.music.ui.fragment.net.NetMainFragment;
import net.imoran.auto.music.vui.VUIManager;

import java.util.List;

public abstract class LocalBaseFragment extends BaseFragment<LocalMusicPresenter> implements LocalMusicView {
    LocalMainFragment mainFragment;

    @Override
    protected LocalMusicPresenter createPresenter() {
        return LocalMusicPresenter.newInstance();
    }

    protected abstract int getLayoutRes();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFragment = (LocalMainFragment) getParentFragment();
    }

    @Override
    protected boolean isNliPage() {
        return false;
    }

    @Override
    public void loadLocalMusicSuccess(int total, int currentPage, List<SongModel> list) {

    }

    @Override
    public void onLoadError(String errorMsg) {

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
}
