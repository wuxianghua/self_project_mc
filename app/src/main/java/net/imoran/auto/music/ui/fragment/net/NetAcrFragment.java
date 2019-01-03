package net.imoran.auto.music.ui.fragment.net;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.network.bean.AcrMusicBean;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.record.AudioRecorder;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.GlideUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.vui.ContextSyncManager;
import net.imoran.auto.music.widgets.progress.CircleProgress;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetAcrFragment extends NetBaseFragment implements View.OnClickListener {
    private RelativeLayout rlContainer;
    private ImageView ivBack, ivStart;
    private ImageView ivListen, ivPlayState;
    private TextView tvTip, tvTipStart;
    private TextView tvSongName, tvArtist;
    private CircleProgress circleProgress;
    private AudioRecorder audioRecord;
    private ProgressBar pbSearch;
    private Timer timer;
    private int count = 0;
    private String filePath;
    private boolean isSet = false;
    private PageInfoBean currentPagInfo = new PageInfoBean();
    private List<SongModel> list = new ArrayList<>();
    private SongModel currentSong;
    private int maxSecond = 10;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    stopRecord();
                    pbSearch.setVisibility(View.VISIBLE);
                    ivListen.setImageResource(R.drawable.bg_music_musicplay_default);
                    tvTip.setText("正在识别中");
//                    filePath = new File(activity.getExternalCacheDir().getAbsolutePath() + "/" + "20181109_185854.pcm").getAbsolutePath();
                    presenter.acrByPcmFile(filePath);
                    break;
                case 2:
                    int progress = (int) msg.obj;
                    circleProgress.setValue(progress);
                    tvTip.setText("正在识别中 " + ((maxSecond - progress / maxSecond) < 1 ? "" : (maxSecond - progress / maxSecond) + "S"));
                    if (!isSet) {
                        tvTipStart.setText("点击屏幕暂停识别");
                        ivListen.setImageResource(R.drawable.ic_music_onlinemusic_identify_success);
                        isSet = true;
                    }
                    break;
            }
        }
    };

    public static NetAcrFragment newInstance() {
        Bundle args = new Bundle();
        NetAcrFragment fragment = new NetAcrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "acr";
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_net_acr;
    }

    @Override
    protected void onViewCreated() {
        if (mainFragment != null)
            mainFragment.getPlayFragment().vuiPause();

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        rlContainer = (RelativeLayout) rootView.findViewById(R.id.rlContainer);
        ivListen = (ImageView) rootView.findViewById(R.id.ivListen);
        ivPlayState = (ImageView) rootView.findViewById(R.id.ivPlayState);
        ivStart = (ImageView) rootView.findViewById(R.id.ivStart);
        tvTip = (TextView) rootView.findViewById(R.id.tvTip);
        tvTipStart = (TextView) rootView.findViewById(R.id.tvTipStart);
        tvSongName = (TextView) rootView.findViewById(R.id.tvSongName);
        tvArtist = (TextView) rootView.findViewById(R.id.tvArtist);
        circleProgress = (CircleProgress) rootView.findViewById(R.id.progressView);
        pbSearch = (ProgressBar) rootView.findViewById(R.id.pbSearch);

        ivBack.setOnClickListener(this);
        rlContainer.setOnClickListener(this);
        ivPlayState.setOnClickListener(this);
        ivStart.setOnClickListener(this);
        startRecord();
    }

    private void startRecord() {
        ivPlayState.setVisibility(View.GONE);
        ivStart.setVisibility(View.GONE);
        circleProgress.setValue(0);
        circleProgress.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvSongName.setText("");
        tvArtist.setText("");
        ivListen.setImageResource(R.drawable.ic_music_onlinemusic_identify_success);
        audioRecord = new AudioRecorder();
        audioRecord.createAudio();
        filePath = getPcmFileAbsolutePath();
        audioRecord.startRecord(filePath);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > maxSecond * 10) {
                    handler.sendEmptyMessage(1);
                } else {
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = count;
                    handler.sendMessage(message);
                }
            }
        };
        timer.schedule(task, 1000, 100);
    }

    private void stopRecord() {
        circleProgress.setVisibility(View.GONE);
        ivListen.setImageResource(R.drawable.bg_music_musicplay_default);

        isSet = false;
        audioRecord.stopRecord();
        count = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.ivBack:
                pop();
                if (mainFragment != null)
                    mainFragment.getPlayFragment().vuiPlay();
                break;
            case R.id.rlContainer:
                if (audioRecord.isRecording()) {
                    if (count <= 40) {
                        ToastUtil.shortShow(activity, "时间太短不能暂停");
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                } else {
                    startRecord();
                }
                break;
            case R.id.ivPlayState: {
                mainFragment.getPlayFragment().setType(NetPlayListFragment.TYPE_SEARCH);
                NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
                NetPlayFragment playFragment = mainFragment.getPlayFragment();
                playFragment.bindData(list, currentPagInfo, 0);
                popTo(NetPlayFragment.class, false);
            }
            break;
            case R.id.ivStart:
                if (currentSong != null)
                    presenter.collectSong(currentSong, true);
                break;
        }
    }

    @Override
    protected boolean isNliPage() {
        return false;
    }

    @Override
    public void onAcrResult(List<AcrMusicBean> musicBeans) {
        if (ListUtils.isNotEmpty(musicBeans)) {
            AcrMusicBean bean = musicBeans.get(0);
            if (bean != null && StringUtils.isNotEmpty(bean.getTitle())) {
                String searchKey = bean.getTitle();
                if (ListUtils.isNotEmpty(bean.getArtists()) && bean.getArtists().get(0) != null) {
                    if (StringUtils.isNotEmpty(bean.getArtists().get(0).getName())) {
                        searchKey += bean.getArtists().get(0).getName();
                    }
                }
                presenter.searchMusicByKeyWord(searchKey);
            }
        } else {
            pbSearch.setVisibility(View.GONE);
            tvTip.setText("未识别到歌曲");
            tvTipStart.setText("点击屏幕开始识别");
            circleProgress.setValue(0);
            ivListen.setImageResource(R.drawable.ic_music_onlinemusic_identify_fail);
        }
    }

    @Override
    public void searchMusicByKeyWordSuccess(int total, List<SongModel> list) {
        currentPagInfo.setTotal(total);

        pbSearch.setVisibility(View.GONE);
        if (ListUtils.isNotEmpty(list)) {
            currentSong = list.get(0);
            if (currentSong == null) return;

            ivPlayState.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.GONE);
            tvTipStart.setVisibility(View.GONE);

            this.list.clear();
            this.list.addAll(list);
            ivStart.setVisibility(View.VISIBLE);
            ivStart.setSelected(currentSong.isCollection());
            tvTip.setVisibility(View.INVISIBLE);
            tvTipStart.setText("点击屏幕开始识别");
            tvTipStart.setVisibility(View.VISIBLE);

            if (StringUtils.isNotEmpty(currentSong.getPicUrl()))
                GlideUtils.setImageView(activity, currentSong.getPicUrl(), ivListen);
            else
                GlideUtils.setImageView(activity, activity.getResources().getDrawable(R.drawable.bg_music_musicplay_default), ivListen);
            tvSongName.setText(currentSong.getName());
            tvArtist.setText(DataConvertUtils.getAlbumSinger(currentSong));
        } else {
            ivStart.setVisibility(View.GONE);
            tvTip.setVisibility(View.VISIBLE);
            tvTipStart.setVisibility(View.VISIBLE);

            ToastUtil.shortShow(activity, "未找到该歌曲");
            tvTip.setText("未识别到歌曲");
            tvTipStart.setText("点击屏幕开始识别");
            circleProgress.setValue(0);
            ivListen.setImageResource(R.drawable.ic_music_onlinemusic_identify_fail);
        }
    }

    @Override
    public void collectSongSuccess(SongModel songModel, boolean isCollect) {
        if (isCollect) {
            ToastUtil.shortShow(activity, "收藏成功");
        } else {
            ToastUtil.shortShow(activity, "取消收藏");
        }
        currentSong.setCollection(isCollect);
        ivStart.setSelected(currentSong.isCollection());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioRecord.stopRecord();
        audioRecord.release();
        handler.removeMessages(1);
        handler.removeMessages(2);
    }

    private String getPcmFileAbsolutePath() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String name = format.format(new Date(System.currentTimeMillis()));
        String fileName = new File(activity.getExternalCacheDir().getAbsolutePath() + "/" + name + ".pcm").getAbsolutePath();
        return fileName;
    }
}
