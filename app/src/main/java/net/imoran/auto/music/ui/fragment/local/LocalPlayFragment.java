package net.imoran.auto.music.ui.fragment.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.utils.ListUtils;

import net.imoran.auto.music.widgets.MusicPlayView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.mvp.view.LocalMusicView;
import net.imoran.auto.music.player.core.MusicRepeatMode;
import net.imoran.auto.music.player.manager.IMusicPlayCallBack;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;
import java.util.Random;

public class LocalPlayFragment extends LocalBaseFragment implements LocalMusicView, IMusicPlayCallBack, View.OnClickListener {
    //自定义U盘读写权限
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private LocalPlayListFragment playListFragment;
    private MusicPlayView musicPlayView;
    private FrameLayout flLoading;
    private FrameLayout flEmpty;
    private ImageView ivMusicSearch;
    private TextView tvSongName;
    private TextView tvAlbumSinger;
    private TextView perv;
    private TextView next;
    private ImageView ivRepeatMode;
    private ImageView ivPlayList;
    private SongModel currentSong;
    private int count = 2;
    private PageInfoBean currentPagInfo = new PageInfoBean();

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_local_play;
    }

    public static LocalPlayFragment newInstance() {
        Bundle args = new Bundle();
        LocalPlayFragment fragment = new LocalPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    @Override
    protected void onViewCreated() {
        presenter.loadLocalMusicAll(activity);
        initView();
        initUSBDeviceBroadcast();
    }


    protected void pausePlay() {
        musicPlayView.stopPlay();
    }

    protected void resumePlay() {
        MusicPlayMangerImp.getInstance().setCallBack(this);
        musicPlayView.rePlay();
    }

    private void initView() {
        musicPlayView = (MusicPlayView) rootView.findViewById(R.id.musicPlayView);
        flLoading = (FrameLayout) rootView.findViewById(R.id.flLoading);
        flEmpty = (FrameLayout) rootView.findViewById(R.id.flEmpty);
        ivMusicSearch = (ImageView) rootView.findViewById(R.id.ivMusicSearch);
        tvSongName = (TextView) rootView.findViewById(R.id.tvSongName);
        perv = (TextView) rootView.findViewById(R.id.perv);
        next = (TextView) rootView.findViewById(R.id.next);
        tvAlbumSinger = (TextView) rootView.findViewById(R.id.tvAlbumSinger);
        ivRepeatMode = (ImageView) rootView.findViewById(R.id.ivRepeatMode);
        ivPlayList = (ImageView) rootView.findViewById(R.id.ivPlayList);

        perv.setOnClickListener(this);
        next.setOnClickListener(this);
        ivMusicSearch.setOnClickListener(this);
        ivPlayList.setOnClickListener(this);
        ivRepeatMode.setOnClickListener(this);
    }

    private void initUSBDeviceBroadcast() {
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbDeviceStateFilter.addAction(ACTION_USB_PERMISSION);
        usbDeviceStateFilter.addDataScheme("file");
        activity.registerReceiver(mUsbReceiver, usbDeviceStateFilter);
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_MEDIA_MOUNTED://接收U盘挂载
                case Intent.ACTION_MEDIA_EJECT://接收U盘挂载
                case Intent.ACTION_MEDIA_REMOVED://接收U盘挂载
                    presenter.loadLocalMusicAll(activity);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void loadLocalMusicSuccess(int total, int currentPage, List<SongModel> list) {
        bindData(list, new PageInfoBean(total, currentPage), 0, false);
    }

    public void bindData(List<SongModel> list, @Nullable PageInfoBean pageInfoBean, int selectIndex, boolean autoPlay) {
        if (ListUtils.isNotEmpty(list)) {
            if (pageInfoBean != null) {
                this.currentPagInfo.setCurrentPageNum(pageInfoBean.getCurrentPageNum());
                this.currentPagInfo.setTotal(pageInfoBean.getTotal());
            }
            flEmpty.setVisibility(View.GONE);
            musicPlayView.bindData(list, selectIndex, autoPlay);
            setPlayingInfo(list.get(selectIndex));
        } else {
            flEmpty.setVisibility(View.VISIBLE);
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
            case R.id.ivMusicSearch:
                start(LocalSearchFragment.newInstance());
                break;
            case R.id.ivPlayList:
                playListFragment = LocalPlayListFragment.newInstance(musicPlayView.getCurrentSongIndex(), currentPagInfo);
                start(playListFragment);
                break;
            case R.id.ivRepeatMode:
                switchRepeatMode();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mUsbReceiver);
    }

    @Override
    public void showLoading() {
        flLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        flLoading.setVisibility(View.GONE);
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
                presenter.loadLocalMusicByPageNum(1);
            } else {
                musicPlayView.rePlay(0, 0);
            }
            return;
        } else {
            int page = currentPage + 1;
            if (musicPlayView.isRandom()) {
                page = random.nextInt(currentPagInfo.getTotalPageNum()) + 1;
            }
            presenter.loadLocalMusicByPageNum(page);
        }
    }

    private void playPreviousPageSong() {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (currentPage > 1) {
            int page = currentPage - 1;
            if (musicPlayView.isRandom()) {
                page = random.nextInt(currentPagInfo.getTotalPageNum()) + 1;
            }
            presenter.loadLocalMusicByPageNum(page);
        }
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
    public void openPlayList(String queryId) {
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
}
