package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.bean.NetMusicDefaultData;
import net.imoran.auto.music.mvp.base.BasePresenter;

import java.util.List;

public class NetMainFragment extends BaseFragment {
    NetPlayFragment playFragment;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static NetMainFragment newInstance(int total, NetMusicDefaultData defaultData, boolean isNotAutoPlay) {
        Bundle args = new Bundle();
        NetMainFragment fragment = new NetMainFragment();
        args.putSerializable("defaultData", defaultData);
        args.putBoolean("isNotAutoPlay", isNotAutoPlay);
        args.putSerializable("defaultDataCount", total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "main";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        playFragment = findChildFragment(NetPlayFragment.class);
        if (playFragment == null) {
            NetMusicDefaultData defaultData = (NetMusicDefaultData) getArguments().getSerializable("defaultData");
            int count = (int) getArguments().getSerializable("defaultDataCount");
            boolean isNotAutoPlay = getArguments().getBoolean("isNotAutoPlay");
            playFragment = NetPlayFragment.newInstance(count, defaultData, isNotAutoPlay);
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

    @Override
    protected boolean isNliPage() {
        return false;
    }

    @Override
    public void vuiPlayList(int total, List list) {
        playFragment.vuiPlayList(total, list);
    }

    public void pausePlay() {
        playFragment.pausePlay();
    }

    public void resumePlay() {
        playFragment.resumePlay();
    }

    public NetPlayFragment getPlayFragment() {
        return playFragment;
    }

    @Override
    public void updatePageId() {
        //该页面不需要更新pageId
    }
}
