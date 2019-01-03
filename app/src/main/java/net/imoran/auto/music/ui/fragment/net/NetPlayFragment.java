package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.NetMusicDefaultData;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.mvp.view.NetMusicView;
import net.imoran.auto.music.player.core.MusicRepeatMode;
import net.imoran.auto.music.player.manager.IMusicPlayCallBack;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.UseLoginUtils;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.auto.music.widgets.LoadingErrorView;
import net.imoran.auto.music.widgets.MusicPlayView;
import net.imoran.tv.common.lib.utils.ToastUtil;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import static net.imoran.auto.music.ui.fragment.net.NetPlayListFragment.TYPE_DEFAULT_LIST;
import static net.imoran.auto.music.vui.ContextSyncManager.PAGE_ID_NET_MUSIC_PLAY;

public class NetPlayFragment extends NetBaseFragment implements NetMusicView, IMusicPlayCallBack, View.OnClickListener {
    private NetPlayListFragment playListFragment;
    private MusicPlayView musicPlayView;
    private LoadingErrorView loadingErrorView;
    private ImageView ivMusicSearch;
    private ImageView ivMusicLib;
    private TextView tvSongName;
    private TextView tvAlbumSinger;
    private TextView perv;
    private TextView next;
    private TextView tvLyrics;
    private ImageView ivStart;
    private ImageView ivRepeatMode;
    private ImageView ivAcr;
    private ImageView ivPlayList;
    private SongModel currentSong;
    private int count = 2;
    private PageInfoBean currentPagInfo = new PageInfoBean();
    private int type = TYPE_DEFAULT_LIST;
    private boolean mIsNotAutoPlay;
    private boolean isFirstSwitch;

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_net_play;
    }

    public static NetPlayFragment newInstance(int count, NetMusicDefaultData defaultData, boolean isNotAutoPlay) {
        Bundle args = new Bundle();
        NetPlayFragment fragment = new NetPlayFragment();
        args.putSerializable("defaultData", defaultData);
        args.putSerializable("defaultDataCount", count);
        args.putBoolean("isNotAutoPlay", isNotAutoPlay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewCreated() {
        initView();
        NetMusicDefaultData defaultData = (NetMusicDefaultData) getArguments().getSerializable("defaultData");
        mIsNotAutoPlay = getArguments().getBoolean("isNotAutoPlay");
        int count = (int) getArguments().getSerializable("defaultDataCount");
        if (defaultData != null && ListUtils.isNotEmpty(defaultData.getList())) {
            getMusicSuccess(count, defaultData.getList());
        } else {
            presenter.loadNetMusic();
        }
    }

    @Override
    protected String getPageId() {
        // 和 NetPlayListFragment 共享pageid
        return PAGE_ID_NET_MUSIC_PLAY;
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    protected void pausePlay() {
        musicPlayView.stopPlay();
    }

    protected void resumePlay() {
        MusicPlayMangerImp.getInstance().setCallBack(this);
        musicPlayView.rePlay();
        if (isFirstSwitch == false) {
            isFirstSwitch = true;
        }
    }

    private void initView() {
        musicPlayView = (MusicPlayView) rootView.findViewById(R.id.musicPlayView);
        loadingErrorView = (LoadingErrorView) rootView.findViewById(R.id.loadingErrorView);
        ivMusicSearch = (ImageView) rootView.findViewById(R.id.ivMusicSearch);
        ivMusicLib = (ImageView) rootView.findViewById(R.id.ivMusicLib);
        tvSongName = (TextView) rootView.findViewById(R.id.tvSongName);
        perv = (TextView) rootView.findViewById(R.id.perv);
        next = (TextView) rootView.findViewById(R.id.next);
        tvAlbumSinger = (TextView) rootView.findViewById(R.id.tvAlbumSinger);
        tvLyrics = (TextView) rootView.findViewById(R.id.tvLyrics);
        ivStart = (ImageView) rootView.findViewById(R.id.ivStart);
        ivRepeatMode = (ImageView) rootView.findViewById(R.id.ivRepeatMode);
        ivAcr = (ImageView) rootView.findViewById(R.id.ivAcr);
        ivPlayList = (ImageView) rootView.findViewById(R.id.ivPlayList);

        perv.setOnClickListener(this);
        next.setOnClickListener(this);
        ivMusicSearch.setOnClickListener(this);
        ivMusicLib.setOnClickListener(this);
        ivStart.setOnClickListener(this);
        ivPlayList.setOnClickListener(this);
        ivRepeatMode.setOnClickListener(this);
        ivAcr.setOnClickListener(this);
        loadingErrorView.setRefreshListener(new LoadingErrorView.onRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadNetMusic();
            }
        });
        MusicPlayMangerImp.getInstance().setCallBack(this);
        musicPlayView.setOnPageEdgeListener(new MusicPlayView.OnPageEdgeListener() {
            @Override
            public void onHeader() {
                playPreviousPageSong();
            }

            @Override
            public void onFooter() {
                playNextPageSong();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void getMusicSuccess(int total, List<SongModel> list) {
        if (ListUtils.isNotEmpty(list)) {
            bindData(list, new PageInfoBean(total, 1), 0);
        } else {
            loadingErrorView.showErrorView();
        }
    }

    @Override
    public void netRequestQueryId(String queryId) {
        ContextSyncManager.getInstant().setCurrentListQueryId(queryId);
    }

    public void bindData(List<SongModel> list, @Nullable PageInfoBean pageInfoBean, int selectIndex) {
        if (ListUtils.isEmpty(list)) return;
        if (pageInfoBean != null) {
            this.currentPagInfo.setCurrentPageNum(pageInfoBean.getCurrentPageNum());
            this.currentPagInfo.setTotal(pageInfoBean.getTotal());
        }

        boolean addHeader = false, addFooter = false;
        if (pageInfoBean.getTotalPageNum() > 1) {
            if (pageInfoBean.getCurrentPageNum() > 1) {
                addHeader = true;
            }
            if (pageInfoBean.getCurrentPageNum() < pageInfoBean.getTotalPageNum()) {
                addFooter = true;
            }
        }
        musicPlayView.addHeaderAndFooter(addHeader, addFooter);
        musicPlayView.bindData(list, selectIndex, !mIsNotAutoPlay || isFirstSwitch);
        if (mIsNotAutoPlay == true) {
            mIsNotAutoPlay = false;
        }
        setPlayingInfo(list.get(selectIndex));
    }

    private int errorCount = 0;

    @Override
    public void getMusicError(String errorMsg) {
        errorCount++;
        if (errorCount >= 2) {
            loadingErrorView.showErrorView();
            errorCount = 0;
        } else {
            presenter.loadNetMusic();
        }
    }

    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.perv:
                musicPlayView.previous();
                break;
            case R.id.next:
                musicPlayView.next();
                break;
            case R.id.ivMusicLib:
                start(NetLibFragment.newInstance());
                break;
            case R.id.ivMusicSearch:
                start(NetSearchFragment.newInstance());
                break;
            case R.id.ivStart:
                collectSong();
                break;
            case R.id.ivPlayList:
                List<SongModel> list = MusicPlayMangerImp.getInstance().getPlayList();
                if (ListUtils.isNotEmpty(list)) {
                    playListFragment = NetPlayListFragment.newInstance(type,
                            musicPlayView.getCurrentSongIndex(), currentPagInfo);
                    start(playListFragment);
                }
                break;
            case R.id.ivRepeatMode:
                switchRepeatMode();
                break;
            case R.id.ivAcr:
                start(NetAcrFragment.newInstance());
                break;
        }
    }

    private void collectSong() {
        if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
            Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentSong == null) return;
        presenter.collectSong(currentSong, !currentSong.isCollection());
        ivStart.setSelected(!currentSong.isCollection());
    }

    @Override
    public void collectSongSuccess(SongModel songModel, boolean isCollect) {
        if (isCollect) {
            ToastUtil.shortShow(activity, "收藏成功");
        } else {
            ToastUtil.shortShow(activity, "取消收藏");
        }
        currentSong.setCollection(isCollect);
    }

    public void cancelCollectSuccess(SongModel songModel) {
        if (currentSong.getUuid().equals(songModel.getUuid())) {
            currentSong.setCollection(false);
            ivStart.setSelected(false);
        }
        musicPlayView.cancelCollectSuccess(songModel);
    }

    @Override
    public void showLoading() {
        loadingErrorView.showLoadingView();
    }

    @Override
    public void hideLoading() {
        loadingErrorView.hideLoadingView();
    }

    @Override
    public void onBuffer() {
        musicPlayView.setPlayStateIcon(MusicPlayView.STATUS_BUFFER);
    }

    @Override
    public void onPlay() {
        musicPlayView.setPlayStateIcon(MusicPlayView.STATUS_PLAY);
    }

    private Random random = new Random();

    @Override
    public void onPlayEnd() {
        playNextPageSong();
    }

    //一页的歌曲播放完毕，加载下一页数据
    private void playNextPageSong() {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (currentPage >= currentPagInfo.getTotalPageNum()) {
            if (currentPagInfo.getTotalPageNum() > 1) {
                currentPagInfo.setCurrentPageNum(1);
                presenter.loadMusicByPageNum(1, getPageId());
            } else {
                musicPlayView.rePlay(0, 0);
            }
            return;
        } else {
            int page = currentPage + 1;
            if (musicPlayView.isRandom()) {
                page = random.nextInt(currentPagInfo.getTotalPageNum()) + 1;
            }
            presenter.loadMusicByPageNum(page, getPageId());
        }
    }

    private void playPreviousPageSong() {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (currentPage > 1) {
            int page = currentPage - 1;
            if (musicPlayView.isRandom()) {
                page = random.nextInt(currentPagInfo.getTotalPageNum()) + 1;
            }
            presenter.loadMusicByPageNum(page, getPageId(), false);
        }
    }


    @Override
    public void loadMusicByPageNumSuccess(int currentPage, List<SongModel> list, boolean isHeadPlay) {
        int selectIndex = isHeadPlay ? 0 : list.size() - 1;
        bindData(list, new PageInfoBean(currentPagInfo.getTotal(), currentPage), selectIndex);
    }

    @Override
    public void loadMusicByPageNumError(String errorMsg) {
        super.loadMusicByPageNumError(errorMsg);
        musicPlayView.playIndex(0);
    }

    @Override
    public void onPause(long playPosition) {
        musicPlayView.setPlayStateIcon(MusicPlayView.STATUS_PAUSE);
    }

    @Override
    public void onError(String error) {
        ToastUtil.shortShow(activity, error);
    }

    @Override
    public void onProgress(float progress, long playPosition) {
        musicPlayView.setPlayProgress(progress);
    }

    @Override
    public void onPlaySongChange(SongModel song, int position) {
        musicPlayView.setSelect(position);
        setPlayingInfo(song);
        musicPlayView.setPlayProgress(0F);
        if (playListFragment != null)
            playListFragment.onPlaySongChange(song, position);
    }

    @Override
    public void onRepeatModeChange() {

    }

    private void setPlayingInfo(SongModel song) {
        currentSong = song;
        tvSongName.setText(song.getName());
        tvAlbumSinger.setText(DataConvertUtils.getAlbumSinger(song));
        ivStart.setSelected(song.isCollection());
    }

    private void switchRepeatMode() {
        count++;
        switch (count % 3) {
            case 0:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_random);
                musicPlayView.setRepeatMode(MusicRepeatMode.RANDOM_PLAY);
                break;
            case 1:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_singlecycle);
                musicPlayView.setRepeatMode(MusicRepeatMode.SINGLE_REPEAT_PLAY);
                break;
            case 2:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_listcycle);
                musicPlayView.setRepeatMode(MusicRepeatMode.ORDER_REPEAT_PLAY);
                break;
        }
    }

    @Override
    public void vuiPlay() {
        musicPlayView.play();
    }

    @Override
    public void vuiPause() {
        musicPlayView.pause();
    }

    @Override
    public void vuiNext() {
        if (musicPlayView.isPlayingLastSong()) {
            playNextPageSong();
        } else {
            musicPlayView.next();
        }
    }

    @Override
    public void vuiPrevious() {
        if (musicPlayView.isPlayingFirstSong()) {
            playPreviousPageSong();
        } else {
            musicPlayView.previous();
        }
    }

    @Override
    public void vuiPlayList(int total, List<SongModel> list) {
        bindData(list, new PageInfoBean(total, 1), 0);
        popTo(NetPlayFragment.class, false);
    }

    @Override
    public void openPlayList(String queryId) {
        updateQueryId(getPageId(), queryId);
        ivPlayList.performClick();
    }

    @Override
    public void vuiFastBack(long milliseconds) {
        musicPlayView.fastBack(milliseconds);
    }

    @Override
    public void vuiFastForward(long milliseconds) {
        musicPlayView.fastForward(milliseconds);
    }

    @Override
    public void vuiChangeRepeatMode(int mode) {
        musicPlayView.setRepeatMode(mode);
        switch (mode) {
            case 1:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_singlecycle);
                break;
            case 2:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_listcycle);
                break;
            case 0:
                ivRepeatMode.setImageResource(R.drawable.ic_music_musicplay_random);
                break;
        }
    }

    @Override
    public void vuiCollectSong() {
        if (isSupportVisible())
            collectSong();
    }

    @Override
    public void vuiCancelCollecSong() {
        if (isSupportVisible())
            cancelCollecSong();
    }

    private void cancelCollecSong() {
        if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
            Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentSong != null && currentSong.isCollection()) {
            presenter.collectSong(currentSong, !currentSong.isCollection());
            ivStart.setSelected(!currentSong.isCollection());
        }
    }

    @Override
    public void vuiPlayIndex(int index) {
        musicPlayView.playIndex(index);
    }
}
