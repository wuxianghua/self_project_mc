package net.imoran.auto.music.ui.fragment.radio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.ui.fragment.net.NetPlayFragment;

import me.yokeyword.fragmentation.SupportFragment;

public class RadioMainFragment extends BaseFragment {
    private RadioPlayFragment playFragment;
    private static final String RADIO_FREQUENCY = "frequency";
    private static final String QUERYID = "queryId";
    private String frequency;
    private String queryId;

    public static RadioMainFragment newInstance(String frequency, String queryId) {
        Bundle args = new Bundle();
        RadioMainFragment fragment = new RadioMainFragment();
        args.putString(RADIO_FREQUENCY, frequency);
        args.putString(QUERYID, queryId);
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
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_container;
    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        frequency = getArguments().getString(RADIO_FREQUENCY);
        queryId = getArguments().getString(QUERYID);
        playFragment = findChildFragment(RadioPlayFragment.class);
        if (playFragment == null) {
            playFragment = RadioPlayFragment.newInstance(frequency, queryId);
            loadRootFragment(R.id.flContainer, playFragment);
        }
    }

    public void pausePlay() {
        playFragment.pausePlay();
    }

    public void resumePlay() {
        playFragment.resumePlay();
    }

    public void listeningBroadCast(String frequency, String queryId) {
        if (playFragment != null) {
            playFragment.listeningBroadCast(frequency, queryId);
        }
    }
}
