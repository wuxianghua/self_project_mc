package net.imoran.auto.music.ui.fragment.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.base.BasePresenter;

public class BleMainFragment extends BaseFragment {
    private BlePlayFragment playFragment;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static BleMainFragment newInstance() {
        Bundle args = new Bundle();
        BleMainFragment fragment = new BleMainFragment();
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
        initView();
    }

    public void pausePlay() {
        playFragment.pauseBtMusic();
    }

    public void resumePlay() {
        playFragment.playBtMusic();
    }


    private void initView() {
        playFragment = findChildFragment(BlePlayFragment.class);
        if (playFragment == null) {
            playFragment = BlePlayFragment.newInstance();
            loadRootFragment(R.id.flContainer, playFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_container;
    }

    @Override
    protected void onViewCreated() {

    }

    public BlePlayFragment getPlayFragment() {
        return playFragment;
    }
}
