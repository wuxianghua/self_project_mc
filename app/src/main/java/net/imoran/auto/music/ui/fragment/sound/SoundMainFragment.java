package net.imoran.auto.music.ui.fragment.sound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;

import java.util.ArrayList;

import me.yokeyword.fragmentation.ISupportFragment;

public class SoundMainFragment extends BaseFragment {
    private static final String TAG = "SoundMainFragment";
    SoundPlayFragment playFragment;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static SoundMainFragment newInstance(ArrayList<LetingNewsBean.LetingNewsEntity> entities,String queryId, String catalogName, ArrayList<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities) {
        Bundle args = new Bundle();
        args.putString("queryId",queryId);
        args.putString("catalogName",catalogName);
        args.putSerializable("entities",entities);
        args.putSerializable("audioAlbumEntities",audioAlbumEntities);
        SoundMainFragment fragment = new SoundMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void pausePlay() {
        playFragment.pausePlay();
    }

    public void resumePlay() {
        playFragment.resumePlay();
    }

    private void initView() {
        playFragment = findChildFragment(SoundPlayFragment.class);
        if (playFragment == null) {
            Bundle arguments = getArguments();
            String queryId = arguments.getString("queryId");
            String catalogname = arguments.getString("catalogname");
            ArrayList<LetingNewsBean.LetingNewsEntity> entities = (ArrayList<LetingNewsBean.LetingNewsEntity>) arguments.getSerializable("entities");
            ArrayList<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities = (ArrayList<AudioAlbumBean.AudioAlbumEntity>) arguments.getSerializable("audioAlbumEntities");
            playFragment = SoundPlayFragment.newInstance(catalogname,entities,audioAlbumEntities,queryId);
            loadRootFragment(R.id.flContainer, playFragment);
        }
    }

    public void vuiAlbumList() {
        playFragment.jumpToAlbumListFragment();
    }

    public void setIsRestart(boolean isRestart) {
        playFragment.setmIsRestart(isRestart);
    }

    public void vuiLetingNews(LetingNewsBean letingNewsBean,String calogName) {
        playFragment.playVuiLetingNews(letingNewsBean,calogName);
    }

    @Override
    protected String getPageType() {
        return "main";
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

    public SoundPlayFragment getPlayFragment() {
        return playFragment;
    }
}
