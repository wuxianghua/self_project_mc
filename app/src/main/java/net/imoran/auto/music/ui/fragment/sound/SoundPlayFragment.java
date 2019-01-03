package net.imoran.auto.music.ui.fragment.sound;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.SoundColectBean;
import net.imoran.auto.music.mvp.view.SoundMusicView;
import net.imoran.auto.music.player.manager.IMusicPlayCallBack;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.SharedPreferencesUtil;
import net.imoran.auto.music.utils.UseLoginUtils;
import net.imoran.auto.music.widgets.MusicPlayView;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by xinhua.shi on 2018/6/13.
 */

public class SoundPlayFragment extends SoundBaseFragment implements View.OnClickListener, SoundMusicView, IMusicPlayCallBack {
    private static final String TAG = "SoundPlayFragment";
    private FrameLayout flLoading;
    private ImageView musicButtonList;
    private ImageView musicButtonSearch;
    private ImageView musicButtonPlayList;
    private TextView songName;
    private TextView albumAuthor;
    private ImageView mPlaySave;
    private boolean isCurrentProgramStar;
    private String fromPage;
    private MusicPlayView playView;
    private SongModel currentSong;
    private TextView playNext;
    private TextView playPre;
    private ImageView playSpeed;
    private ImageView playFast;
    private ImageView playSlow;
    private int mCurrentSongPlayStatus;
    private Handler mHandler = new Handler();
    private boolean isFirst;
    private boolean mIsRestart = true;  //乐听新闻vui操作，不在记忆之前的状态
    private int currentPlaySpeed = 1;
    private final int PLAY_SPEED_ONE = 1;
    private final int PLAY_SPEED_TWO = 2;
    private final int PLAY_SPEED_ONE_QUARTER = 3;
    private final int PLAY_SPEED_ONE_HALF = 4;

    @Override
    protected void onViewCreated() {
        initView();
        initListener();
        initData();
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    private void initData() {
        Bundle arguments = getArguments();
        String catalogName = arguments.getString("catalogName");
        ArrayList<LetingNewsBean.LetingNewsEntity> entities = (ArrayList<LetingNewsBean.LetingNewsEntity>) arguments.getSerializable("entities");
        if (entities != null) {
            MusicPlayMangerImp.getInstance().setCallBack(this);
            playVuiLetingNews(entities, catalogName);
        }
        audioAlbumEntities = (ArrayList<AudioAlbumBean.AudioAlbumEntity>) arguments.getSerializable("audioentities");
        if (audioAlbumEntities != null && audioAlbumEntities.size() != 0) {
            String queryId = arguments.getString("queryId");
            initVuiControl(queryId, audioAlbumEntities);
        }
        audio_programs = (ArrayList<AudioProgramBean.AudioProgramEntity>) arguments.getSerializable("audio_program");
        if (audio_programs != null) {
            fromPage = arguments.getString("from");
            if (fromPage == null) {
                fromPage = "other";
            }
            bindData(DataConvertUtils.getSongModelList(audio_programs), 0, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", TAG);
    }

    private void initListener() {
        musicButtonList.setOnClickListener(this);
        musicButtonSearch.setOnClickListener(this);
        musicButtonPlayList.setOnClickListener(this);
        mPlaySave.setOnClickListener(this);
        playSpeed.setOnClickListener(this);
        playNext.setOnClickListener(this);
        playPre.setOnClickListener(this);
        playFast.setOnClickListener(this);
        playSlow.setOnClickListener(this);
    }

    public static SoundPlayFragment newInstance(String catalogName, ArrayList<LetingNewsBean.LetingNewsEntity> entities, ArrayList<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities, String queryId) {
        Bundle args = new Bundle();
        SoundPlayFragment fragment = new SoundPlayFragment();
        args.putString("catalogName", catalogName);
        args.putSerializable("entities", entities);
        args.putSerializable("audioentities", audioAlbumEntities);
        args.putString("queryId", queryId);
        fragment.setArguments(args);
        return fragment;
    }

    protected void pausePlay() {
        playView.stopPlay();
    }

    protected void resumePlay() {
        if (!isFirst) {
            presenter.getRandomAudio();
            isFirst = true;
        }
        MusicPlayMangerImp.getInstance().setCallBack(this);
        if (mIsRestart) {
            playView.rePlay();
        }
        mIsRestart = true;
    }

    public void resumeVuiPlay() {
        isFirst = true;
    }

    public void getRandomAudio() {
        resumePlay();
    }

    public void initVuiControl(String queryId, ArrayList<AudioAlbumBean.AudioAlbumEntity> entities) {
        MusicPlayMangerImp.getInstance().setCallBack(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio_album", entities);
        bundle.putString("queryId", queryId);
        start(SoundAlbumListFragment.newInstance(bundle), ISupportFragment.SINGLETOP);
    }

    public void jumpToAlbumListFragment() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pausePlay();
            }
        }, 600);
        MusicPlayMangerImp.getInstance().setCallBack(this);
        start(SoundAlbumListFragment.newInstance(null), ISupportFragment.SINGLETOP);
    }

    public void setmIsRestart(boolean isRestart) {
        mIsRestart = isRestart;
    }

    public void playVuiLetingNews(LetingNewsBean letingNewsBean, String calogName) {
        if (letingNewsBean == null || letingNewsBean.getLeting_news() == null || letingNewsBean.getLeting_news().size() == 0) return;
        isFirst = true;
        bindLeingData(letingNewsBean.getLeting_news(), 0, "leting", calogName);
        if (getTopFragment() instanceof SoundPlayFragment) return;
        popTo(SoundPlayFragment.class, false);
    }

    public void playVuiLetingNews(List<LetingNewsBean.LetingNewsEntity> letingNewsEntities, String calogName) {
        if (letingNewsEntities == null || letingNewsEntities.size() == 0) {
            getRandomAudio();
            return;
        }
        isFirst = true;
        hideLoading();
        bindLeingData(letingNewsEntities, 0, "leting", calogName);
        if (getTopFragment() instanceof SoundPlayFragment) return;
        popTo(SoundPlayFragment.class, false);
    }

    private void initView() {
        flLoading = (FrameLayout) rootView.findViewById(R.id.fl_loading);
        musicButtonList = (ImageView) rootView.findViewById(R.id.music_btn);
        musicButtonSearch = (ImageView) rootView.findViewById(R.id.search_btn);
        musicButtonPlayList = (ImageView) rootView.findViewById(R.id.play_list);
        songName = (TextView) rootView.findViewById(R.id.song_name);
        albumAuthor = (TextView) rootView.findViewById(R.id.songer_name);
        mPlaySave = (ImageView) rootView.findViewById(R.id.play_save);
        playView = (MusicPlayView) rootView.findViewById(R.id.musicPlayView);
        playNext = (TextView) rootView.findViewById(R.id.next);
        playPre = (TextView) rootView.findViewById(R.id.perv);
        playFast = (ImageView) rootView.findViewById(R.id.play_fast);
        playSlow = (ImageView) rootView.findViewById(R.id.play_slow);
        playSpeed = (ImageView) rootView.findViewById(R.id.play_speed);
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
    public int getLayoutRes() {
        return R.layout.fragment_music_sound;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_btn:
                start(SoundLibFragment.newInstance());
                break;
            case R.id.play_save:
                collectAlbum();
                break;
            case R.id.search_btn:
                start(SoundSearchFragment.newInstance());
                break;
            case R.id.play_list:
                if ("collection".equals(fromPage)) {
                    start(SoundCollectionFragment.newInstance());
                } else if ("leting".equals(fromPage)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("catalogName", keyWord);
                    bundle.putSerializable("leting_rec_news", letingNewsEntities);
                    start(SoundLetingDetailFragment.newInstance(bundle));
                } else {
                    if (audio_programs == null || audio_programs.size() == 0) return;
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("program", audio_programs);
                    bundle1.putSerializable("album", "");
                    bundle1.putInt("playPosition", playView.getCurrentSongIndex());
                    bundle1.putInt("playState", mCurrentSongPlayStatus);
                    bundle1.putString("lastfragment", SoundPlayFragment.class.getName());
                    start(SoundAlbumDetailFragment.newInstance(bundle1));
                }
                break;
            case R.id.next:
                playView.next();
                break;
            case R.id.perv:
                playView.previous();
                break;
            case R.id.play_fast:
                MusicPlayMangerImp.getInstance().fastForward(30000);
                break;
            case R.id.play_slow:
                MusicPlayMangerImp.getInstance().fastBack(30000);
                break;
            case R.id.play_speed:
                changePlaySpeed();
                break;
        }
    }

    //收藏节目
    @Override
    public void vuiCollectSong() {
        if (isSupportVisible())
            collectAlbum(true);
    }

    private void collectAlbum() {
        if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
            Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("leting".equals(fromPage)) {
            Toast.makeText(activity, "乐听新闻不支持收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentSong == null) return;
        SoundColectBean collectionsBean = new SoundColectBean();
        collectionsBean.setAlbumId(currentSong.getAlbumId());
        collectionsBean.setTrackId(currentSong.getTrackId());
        collectionsBean.setType("track");
        String s = new Gson().toJson(collectionsBean);
        if (isCurrentProgramStar) {
            isCurrentProgramStar = false;
            presenter.delAudioCollection(s);
            currentSong.setCollection(false);
            mPlaySave.setImageResource(R.drawable.ic_music_notcollected);
        } else {
            isCurrentProgramStar = true;
            presenter.addAudioCollection(s);
            currentSong.setCollection(true);
            mPlaySave.setImageResource(R.drawable.ic_music_collected);
        }
    }

    private void collectAlbum(boolean isCollection) {
        if ("".equals(UseLoginUtils.getInstance().getCurrentUid())) {
            Toast.makeText(activity, "您还没有登录，请到个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("leting".equals(fromPage)) {
            Toast.makeText(activity, "乐听新闻不支持收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentSong == null) return;
        SoundColectBean collectionsBean = new SoundColectBean();
        collectionsBean.setAlbumId(currentSong.getAlbumId());
        collectionsBean.setTrackId(currentSong.getTrackId());
        collectionsBean.setType("track");
        String s = new Gson().toJson(collectionsBean);
        if (isCurrentProgramStar && !isCollection) {
            isCurrentProgramStar = false;
            presenter.delAudioCollection(s);
            currentSong.setCollection(false);
            mPlaySave.setImageResource(R.drawable.ic_music_notcollected);
        } else if (!isCurrentProgramStar && isCollection) {
            isCurrentProgramStar = true;
            presenter.addAudioCollection(s);
            currentSong.setCollection(true);
            mPlaySave.setImageResource(R.drawable.ic_music_collected);
        }
    }

    //取消收藏节目
    @Override
    public void vuiCancelCollecSong() {
        if (isSupportVisible())
            collectAlbum(false);
    }

    public void updateCollection(String trackId) {
        if (currentSong != null && currentSong.getTrackId().equals(trackId)) {
            isCurrentProgramStar = false;
            currentSong.setCollection(false);
            mPlaySave.setImageResource(R.drawable.ic_music_notcollected);
        }
    }

    private void changePlaySpeed() {
        if (currentPlaySpeed == PLAY_SPEED_ONE) {
            currentPlaySpeed = PLAY_SPEED_ONE_QUARTER;
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_125times);
            MusicPlayMangerImp.getInstance().setSpeed(1.2f);
        } else if (currentPlaySpeed == PLAY_SPEED_ONE_QUARTER) {
            currentPlaySpeed = PLAY_SPEED_ONE_HALF;
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_15times);
            MusicPlayMangerImp.getInstance().setSpeed(1.27f);
        } else if (currentPlaySpeed == PLAY_SPEED_ONE_HALF) {
            currentPlaySpeed = PLAY_SPEED_TWO;
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_2times);
            MusicPlayMangerImp.getInstance().setSpeed(1.30f);
        } else if (currentPlaySpeed == PLAY_SPEED_TWO) {
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_1times);
            MusicPlayMangerImp.getInstance().setSpeed(1f);
            currentPlaySpeed = PLAY_SPEED_ONE;
        }
    }

    private ArrayList<AudioProgramBean.AudioProgramEntity> audio_programs;
    private ArrayList<AudioAlbumBean.AudioAlbumEntity> audioAlbumEntities;

    @Override
    public void showRandomResult(List<AudioProgramBean.AudioProgramEntity> audioProgramBean) {
        if (audioProgramBean == null || audioProgramBean.size() == 0) {
            Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }
        audio_programs = (ArrayList<AudioProgramBean.AudioProgramEntity>) audioProgramBean;
        bindData(DataConvertUtils.getSongModelList(audioProgramBean), 0, null);
        SharedPreferencesUtil.setDataList(activity, "audio_albums", audio_programs);
        //开始播放
        MusicPlayMangerImp.getInstance().setCallBack(this);
    }

    public void bindData(List<SongModel> list, int selectIndex, String source) {
        fromPage = source;
        playView.bindData(list, selectIndex, true);
        setPlayingInfo(list.get(selectIndex));
    }

    public void bindData(ArrayList<AudioProgramBean.AudioProgramEntity> audio_programs, int selectIndex, String source) {
        fromPage = source;
        this.audio_programs = audio_programs;
        List<SongModel> list = DataConvertUtils.getSongModelList(audio_programs);
        playView.bindData(list, selectIndex, true);
        setPlayingInfo(list.get(selectIndex));
        hideLoading();
    }

    private ArrayList<LetingNewsBean.LetingNewsEntity> letingNewsEntities;
    private String keyWord;

    public void bindLeingData(List<LetingNewsBean.LetingNewsEntity> audio_programs, int selectIndex, String source, String calogName) {
        keyWord = calogName;
        fromPage = source;
        letingNewsEntities = (ArrayList<LetingNewsBean.LetingNewsEntity>) audio_programs;
        List<SongModel> list = DataConvertUtils.getSongModelFromList(audio_programs);
        playView.bindData(list, selectIndex, true);
        setPlayingInfo(list.get(selectIndex));
        hideLoading();
    }

    private void setPlayingInfo(SongModel songModel) {
        currentSong = songModel;
        songName.setText(songModel.getName());
        if (!songModel.getSinger().isEmpty()) {
            if (songModel.getSinger().get(0) == null || "".equals(songModel.getSinger().get(0))) {
                albumAuthor.setText(songModel.getAlbum());
            } else {
                albumAuthor.setText(songModel.getAlbum() + "/" + songModel.getSinger().get(0));
            }
        } else {
            albumAuthor.setText("");
        }
        if (songModel.isCollection()) {
            mPlaySave.setImageResource(R.drawable.ic_music_collected);
            isCurrentProgramStar = true;
        } else {
            mPlaySave.setImageResource(R.drawable.ic_music_notcollected);
            isCurrentProgramStar = false;
        }
    }


    @Override
    public void onBuffer() {
        playView.setPlayStateIcon(MusicPlayView.STATUS_BUFFER);
    }

    @Override
    public void onPlay() {
        playView.setPlayStateIcon(MusicPlayView.STATUS_PLAY);
        mCurrentSongPlayStatus = 1;
    }

    @Override
    public void onPlayEnd() {

    }

    @Override
    public void onPause(long playPosition) {
        playView.setPlayStateIcon(MusicPlayView.STATUS_PAUSE);
        mCurrentSongPlayStatus = 0;
    }

    @Override
    public void onError(String error) {
        ToastUtil.shortShow(activity, error);
    }

    @Override
    public void onProgress(float progress, long playPosition) {
        playView.setPlayProgress(progress);
    }

    @Override
    public void onPlaySongChange(SongModel song, int position) {
        playView.setSelect(position);
        setPlayingInfo(song);
        playView.setPlayProgress(0F);
        playView.setPlayStateIcon(MusicPlayView.STATUS_PLAY);
    }

    @Override
    public void onRepeatModeChange() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void vuiPlay() {
        playView.play();
    }

    @Override
    public void vuiPause() {
        playView.pause();
    }

    @Override
    public void vuiNext() {
        playView.next();
    }

    @Override
    public void vuiPrevious() {
        playView.previous();
    }

    @Override
    public void openPlayList(String queryId) {
        updateQueryId(getPageId(), queryId);
        musicButtonPlayList.performClick();
    }

    @Override
    public void vuiFastBack(long milliseconds) {
        playView.fastBack(milliseconds);
    }

    @Override
    public void vuiFastForward(long milliseconds) {
        playView.fastForward(milliseconds);
    }

    @Override
    public void vuiChangeSpeed(float speed) {
        super.vuiChangeSpeed(speed);
        if (speed == 2f) {
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_2times);
            MusicPlayMangerImp.getInstance().setSpeed(1.30f);
        }else if (speed == 1.5f) {
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_15times);
            MusicPlayMangerImp.getInstance().setSpeed(1.27f);
        }else if (speed == 1.25) {
            playSpeed.setImageResource(R.drawable.ic_music_musicplay_speed_125times);
            MusicPlayMangerImp.getInstance().setSpeed(1.2f);
        }
    }
}
