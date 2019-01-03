package net.imoran.auto.music.ui.fragment.local;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.base.BasePresenter;

public class LocalMainFragment extends BaseFragment {
    LocalPlayFragment playFragment;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static LocalMainFragment newInstance() {
        Bundle args = new Bundle();
        LocalMainFragment fragment = new LocalMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isNliPage() {
        return false;
    }

    @Override
    protected String getPageType() {
        return "main";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        playFragment = findChildFragment(LocalPlayFragment.class);
        if (playFragment == null) {
            playFragment = LocalPlayFragment.newInstance();
            loadRootFragment(R.id.flContainer, playFragment);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_container;
    }

    @Override
    protected void onViewCreated() {

    }

    public void pausePlay() {
        playFragment.pausePlay();
    }

    public void resumePlay() {
        playFragment.resumePlay();
    }

    public LocalPlayFragment getPlayFragment() {
        return playFragment;
    }
}
