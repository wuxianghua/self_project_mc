package net.imoran.auto.music.widgets;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.utils.ListUtils;

import net.imoran.auto.music.R;
import net.imoran.auto.music.player.core.MusicRepeatMode;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.PlayViewPagerAdapter;

import net.imoran.auto.music.widgets.viewPager.ScaleInTransformer;
import net.imoran.tv.common.lib.utils.LogUtils;

import java.util.List;

public class MusicPlayView extends FrameLayout {
    private static final String TAG = "MusicPlayView";
    public final static int STATUS_BUFFER = -1;
    public final static int STATUS_PLAY = 1;
    public final static int STATUS_PAUSE = 0;
    private Context context;
    private ViewPager viewPager;
    private PlayViewPagerAdapter recyclePagerAdapter;
    private int lastIndex = 0, count = 0;
    private List<SongModel> soundList;
    private boolean isPlayPrepare = false;
    private long currentPlayPosition;
    private boolean isRandom = false;
    boolean addHeader = false, addFooter = false;
    private OnPageEdgeListener listener;
    private boolean smoothScroll = true;

    public interface OnPageEdgeListener {
        void onHeader();

        void onFooter();
    }

    public void setOnPageEdgeListener(OnPageEdgeListener listener) {
        this.listener = listener;
    }

    public MusicPlayView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }


    public MusicPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MusicPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void addHeaderAndFooter(boolean addHeader, boolean addFooter) {
        this.addFooter = addFooter;
        this.addHeader = addHeader;
    }

    private void init() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_music_play, null);
        viewPager = (ViewPager) rootView.findViewById(R.id.vpPage);
        viewPager.setOffscreenPageLimit(3);
        recyclePagerAdapter = new PlayViewPagerAdapter(context, null);
        viewPager.setAdapter(recyclePagerAdapter);
        viewPager.setPageMargin(60);
        viewPager.setPageTransformer(true, new ScaleInTransformer(0.8F));
        recyclePagerAdapter.setUpdatePlayProgressListener(new PlayViewPagerAdapter.UpdatePlayProgressListener() {
            @Override
            public void updatePlayProgress(long progress) {
                currentPlayPosition = progress;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setPlayProgress(0);
                if (position < soundList.size()) {
                    if (position == 0 && addHeader) {
                        //上一页
                        if (listener != null) {
                            listener.onHeader();
                        }
                    } else {
                        if (position < (addHeader ? lastIndex + 1 : lastIndex)) {
                            MusicPlayMangerImp.getInstance().previous();
                        } else if (position > (addHeader ? lastIndex + 1 : lastIndex)) {
                            MusicPlayMangerImp.getInstance().next();
                        }
                        lastIndex = (addHeader ? position - 1 : position);
                    }

                } else if (position >= (addHeader ? (soundList.size() + 1) : soundList.size())) {
                    //下一页
                    if (listener != null) {
                        listener.onFooter();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setClipChildren(false);
        addView(rootView);
    }

    public void bindData(List<SongModel> soundList, int selectIndex, boolean autoPlay) {
        if (!ListUtils.isNotEmpty(soundList)) return;
        if (ListUtils.isSameSongModelList(this.soundList, soundList)) {
            playIndex(selectIndex);
        } else {
            this.soundList = soundList;
            if (selectIndex > soundList.size() - 1)
                selectIndex = soundList.size() - 1;
            count = soundList.size();
            recyclePagerAdapter.update(soundList, addHeader, addFooter);
            int index = selectIndex >= 0 ? selectIndex : 0;
            lastIndex = index;
            if (addHeader && index == 0)
                viewPager.setCurrentItem(index + 1, smoothScroll);
            else
                viewPager.setCurrentItem(index, smoothScroll);
            if (autoPlay) {
                rePlay(selectIndex, 0);
            }
        }
    }

    public void rePlay(final int index, final long startPosition) {
        MusicPlayMangerImp.getInstance().prepare(soundList);
        isPlayPrepare = true;
        playIndex(index, startPosition);
    }

    public void rePlay() {
        if (!MusicPlayMangerImp.getInstance().isPlaying())
            Log.e(TAG, "currentPlayPosition22---" + currentPlayPosition);
        rePlay(lastIndex, currentPlayPosition);
    }

    public void stopPlay() {
        isPlayPrepare = false;
        currentPlayPosition = MusicPlayMangerImp.getInstance().getPlayPosition();
        Log.e(TAG, "currentPlayPosition11---" + currentPlayPosition);
        MusicPlayMangerImp.getInstance().release();
    }

    public void next() {
        if (!isPlayPrepare) return;
        if (lastIndex < count - 1) {
            setPlayProgress(0);
            lastIndex = lastIndex + 1;
            viewPager.setCurrentItem(addHeader ? lastIndex + 1 : lastIndex, smoothScroll);
            MusicPlayMangerImp.getInstance().next();
        }
    }

    public void previous() {
        if (!isPlayPrepare) return;
        if (lastIndex > 0) {
            setPlayProgress(0);
            lastIndex = lastIndex - 1;
            viewPager.setCurrentItem(addHeader ? lastIndex + 1 : lastIndex, smoothScroll);
            MusicPlayMangerImp.getInstance().previous();
        }
    }

    public void fastBack(long milliseconds) {
        MusicPlayMangerImp.getInstance().fastBack(milliseconds);
    }

    public void fastForward(long milliseconds) {
        MusicPlayMangerImp.getInstance().fastForward(milliseconds);
    }

    public void pause() {
        currentPlayPosition = MusicPlayMangerImp.getInstance().getPlayPosition();
        MusicPlayMangerImp.getInstance().pause();
    }

    public void play() {
        MusicPlayMangerImp.getInstance().playIndex(lastIndex, currentPlayPosition);
    }

    public void setSelect(int selectIndex) {
        setPlayProgress(0);
        int index = selectIndex >= 0 ? selectIndex : 0;
        lastIndex = index;
        if (addHeader)
            viewPager.setCurrentItem(index + 1, smoothScroll);
        else
            viewPager.setCurrentItem(index, smoothScroll);
    }

    public void playIndex(int index, long startPosition) {
        if (!isPlayPrepare) return;
        if (index >= 0 && index <= count - 1) {
            lastIndex = index;
            if (addHeader)
                viewPager.setCurrentItem(index + 1, smoothScroll);
            else
                viewPager.setCurrentItem(index, smoothScroll);
            MusicPlayMangerImp.getInstance().playIndex(index, startPosition);
        }
    }

    public void playIndex(int index) {
        playIndex(index, 0);
    }

    //-1:缓冲中 0 暂停 1 播放
    public void setPlayStateIcon(int type) {
        Log.e(TAG, "PLAYSTATUS" + type);
        PlayViewPagerAdapter.MorPagerViewHolder viewHolder = recyclePagerAdapter.getViewHolder(addHeader ? lastIndex + 1 : lastIndex);
        if (viewHolder != null) {
            int resId = R.drawable.ic_music_musicplay_stop;
            switch (type) {
                case STATUS_BUFFER:
                    resId = R.drawable.ic_music_musicplay_load;
                    break;
                case STATUS_PAUSE:
                    resId = R.drawable.ic_music_musicplay_played;
                    break;
                case STATUS_PLAY:
                    resId = R.drawable.ic_music_musicplay_stop;
                    break;
            }
            viewHolder.setPlayStateIcon(resId);
        }
    }

    public void cancelCollectSuccess(SongModel songModel) {
        for (SongModel model : soundList) {
            if (model.getUuid().equals(songModel.getUuid())) {
                model.setCollection(false);
                break;
            }
        }
    }

    public int getCurrentSongIndex() {
        return lastIndex;
    }

    public boolean isPlayingLastSong() {
        return lastIndex == soundList.size() - 1;
    }

    public boolean isPlayingFirstSong() {
        return lastIndex == 0;
    }

    public SongModel getCurrentSong() {
        return soundList.get(lastIndex);
    }

    public void setRepeatMode(int mode) {
        if (mode == MusicRepeatMode.RANDOM_PLAY) {
            isRandom = true;
            MusicPlayMangerImp.getInstance().setRepeatMode(mode);
        } else {
            int m = mode == MusicRepeatMode.ORDER_REPEAT_PLAY ? 0 : mode;
            MusicPlayMangerImp.getInstance().setRepeatMode(m);
            isRandom = false;
        }
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setPlayProgress(float progress) {
        PlayViewPagerAdapter.MorPagerViewHolder viewHolder = recyclePagerAdapter.getViewHolder(addHeader ? lastIndex + 1 : lastIndex);
        if (viewHolder != null)
            viewHolder.setPlayProgress(progress);
    }

}