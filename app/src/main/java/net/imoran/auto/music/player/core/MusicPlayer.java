package net.imoran.auto.music.player.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.STREAM_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

/**
 * 播放器的具体实现类
 */
public class MusicPlayer implements IMusicPlayer {
    private String TAG = "MusicPlayer";
    private int PROGRESS_CODE = 100;
    private Context appContext;
    private SimpleExoPlayer mExoPlayer;//播放器实例
    private final ExoPlayerEventListener mEventListener;
    private Callback mCallback;
    private long position = 0l;//暂停是的播放位置
    private final Handler handler;
    private boolean isComplete = true;
    private int index = 0, maxIndex;
    private String[] urls;
    private int errorCount = 0;

    @SuppressLint("HandlerLeak")
    public MusicPlayer(Context context) {
        appContext = context.getApplicationContext();
        mEventListener = new ExoPlayerEventListener();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (PROGRESS_CODE == msg.what) {
                    if (isPlaying() && !isComplete && mCallback != null) {
                        long currentPosition = mExoPlayer.getCurrentPosition();
                        float progress = currentPosition * 100F / mExoPlayer.getDuration();
                        mCallback.onProgress(progress > 0 ? progress : 0F, currentPosition);
                    }
                    handler.sendEmptyMessageDelayed(PROGRESS_CODE, 50);
                }
            }
        };
    }

    @Override
    public synchronized void prepare(String... urls) {
        if (urls == null || urls.length == 0) return;
        this.urls = urls;
        maxIndex = urls.length - 1;
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(appContext), new DefaultTrackSelector(), new DefaultLoadControl());
            mExoPlayer.addListener(mEventListener);
            mExoPlayer.setAudioStreamType(STREAM_TYPE_MUSIC);
            mExoPlayer.setVolume(0.5F);
        }
        final AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC).setUsage(USAGE_MEDIA).build();
        mExoPlayer.setAudioAttributes(audioAttributes);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(appContext, Util.getUserAgent(appContext, MusicPlayer.class.getName()), new DefaultBandwidthMeter());
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        ExtractorMediaSource.Factory extractorMediaFactory = new ExtractorMediaSource.Factory(dataSourceFactory);
        extractorMediaFactory.setExtractorsFactory(extractorsFactory);
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (String url : urls) {
            if (url != null && !url.isEmpty()) {
                MediaSource mediaSource = extractorMediaFactory.createMediaSource(Uri.parse(url));
                concatenatingMediaSource.addMediaSource(mediaSource);
            }
        }
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(concatenatingMediaSource, 1);
        mExoPlayer.prepare(loopingMediaSource);
    }

    @Override
    public void play() {
        if (mExoPlayer != null && !mExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(true);

            isComplete = false;
            handler.sendEmptyMessage(PROGRESS_CODE);

            index = mExoPlayer.getCurrentWindowIndex();
            if (mCallback != null) mCallback.onIndexChange(index);
        }
    }

    @Override
    public void pause() {
        if (mExoPlayer != null && mExoPlayer.getPlayWhenReady()) {
            position = mExoPlayer.getCurrentPosition();
            mExoPlayer.setPlayWhenReady(false);
            isComplete = true;
        }
    }

    @Override
    public void resume() {
        if (mExoPlayer != null && !mExoPlayer.getPlayWhenReady()) {
            seekTo(position);
            isComplete = false;
            play();
        }
    }

    public void next() {
        if (mExoPlayer != null) {
            if (index >= maxIndex) return;
            mExoPlayer.seekToDefaultPosition(index + 1);

            if (!mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(true);
                handler.sendEmptyMessage(PROGRESS_CODE);
            }
            isComplete = false;
            if (mCallback != null) mCallback.onIndexChange(index + 1);
            index = index + 1;
        }
    }

    @Override
    public void previous() {
        if (mExoPlayer != null) {
            if (index <= 0) return;
            mExoPlayer.seekToDefaultPosition(index - 1);

            if (!mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(true);
                handler.sendEmptyMessage(PROGRESS_CODE);
            }
            isComplete = false;
            if (mCallback != null) mCallback.onIndexChange(index - 1);
            index = index - 1;
        }
    }

    @Override
    public void playIndex(int i, long positionMs) {
        if (mExoPlayer != null) {
            if (i < 0 || i > maxIndex) return;
            mExoPlayer.seekTo(i, positionMs);

            if (!mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(true);
                handler.sendEmptyMessage(PROGRESS_CODE);
            }
            isComplete = false;
            index = i;
            if (mCallback != null) mCallback.onIndexChange(i);
        }
    }

    @Override
    public void stop() {
        if (mExoPlayer != null && mExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.seekTo(0);
            position = 0;
            isComplete = true;
        }
    }

    @Override
    public void release() {
        if (mCallback != null) {
            mCallback.onPause(0);
        }
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;
        }
        handler.removeMessages(PROGRESS_CODE);
        isComplete = true;
    }

    @Override
    public boolean isPlaying() {
        return (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    @Override
    public void fastForward(long milliseconds) {
        if (mExoPlayer != null) {
            long pos = mExoPlayer.getCurrentPosition() + milliseconds;
            long max = mExoPlayer.getDuration();
            mExoPlayer.seekTo(pos >= max ? max : pos);
        }
    }

    @Override
    public void fastBack(long milliseconds) {
        if (mExoPlayer != null) {
            long pos = mExoPlayer.getCurrentPosition() - milliseconds;
            mExoPlayer.seekTo(pos <= 0 ? 0 : pos);
        }
    }

    @Override
    public void seekTo(long position) {
        if (mExoPlayer != null) {
            long max = mExoPlayer.getDuration();
            if (position < 0 || position > max) return;
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public int getCurrentIndex() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentWindowIndex();
        } else return -1;
    }

    @Override
    public void setRepeatMode(int mode) {
        if (mExoPlayer != null) {
            mExoPlayer.setRepeatMode(mode);
        }
    }

    @Override
    public long getPlayPosition() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentPosition();
        } else return 0L;
    }

    @Override
    public void setSpeed(float speed) {
        if (mExoPlayer != null) {
            PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
            mExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private final class ExoPlayerEventListener implements com.google.android.exoplayer2.Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            int newIndex = mExoPlayer.getCurrentWindowIndex();
            if (index > newIndex || index < newIndex) {
                if (mCallback != null) mCallback.onIndexChange(newIndex);
            }
            index = newIndex;
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case com.google.android.exoplayer2.Player.STATE_IDLE:
                    if (mCallback != null) {
                        mCallback.onPause(0);
                    }
                    break;
                case com.google.android.exoplayer2.Player.STATE_BUFFERING:
                    if (mCallback != null) {
                        if (!isPlaying())
                            mCallback.onBuffer();
                    }
                    break;
                case com.google.android.exoplayer2.Player.STATE_READY:
                    if (mCallback != null) {
                        if (playWhenReady) {
                            mCallback.onPlay();
                        } else {
                            mCallback.onPause(mExoPlayer.getCurrentPosition());
                        }
                    }
                    break;
                case com.google.android.exoplayer2.Player.STATE_ENDED:
                    isComplete = true;
                    if (mCallback != null) {
                        mCallback.onPause(0);
                        mCallback.onStop();
                    }
                    break;
            }
        }


        @Override
        public void onPlayerError(ExoPlaybackException error) {
            String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = "抱歉，该资源暂时没有版权";
                    Log.e(TAG, "onPlayerError(TYPE_SOURCE)：" + error.getSourceException().getMessage());
                    //地址无效自动切换到下一首
                    if (isNetAvailable()) {
                        release();
                        prepare(urls);
                        if (index >= maxIndex) {
                            index = 0;
                            playIndex(index, 0);
                        } else {
                            next();
                        }
                    } else {
                        what = "网络异常，请确认网络是否连接";
                    }
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = "播放器出错啦";
                    Log.e(TAG, "onPlayerError(TYPE_RENDERER)：" + error.getRendererException().getMessage());
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = "播放器出错啦";
                    Log.e(TAG, "onPlayerError(TYPE_UNEXPECTED)：" + error.getUnexpectedException().getMessage());
                    break;
                default:
                    Log.e(TAG, "onPlayerError(TYPE_UNEXPECTED)：" + "Unknown: " + error);
                    what = "未知错误";
            }

            if (mCallback != null) {
                mCallback.onError(what);
            }

            //出错重启
            if (errorCount <= 10 && error.type != ExoPlaybackException.TYPE_SOURCE) {
                if (urls == null || urls.length == 0) return;
                release();
                prepare(urls);
                playIndex(index, 0);
                seekTo(position);
                errorCount++;
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        @Override
        public void onSeekProcessed() {
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }
    }

    private boolean isNetAvailable() {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService("connectivity");
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }
}
