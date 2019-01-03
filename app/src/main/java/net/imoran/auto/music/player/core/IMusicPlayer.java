package net.imoran.auto.music.player.core;

/**
 * 播放器接口
 */
public interface IMusicPlayer {
    /**
     * 初始化播放器，准备
     *
     * @param url 播放音频的 url
     */
    void prepare(String... url);

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 继续播放
     */
    void resume();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 下一首
     */
    void next();

    /**
     * 上一首
     */
    void previous();

    //开始播放，并且从positionMs

    /**
     * 播放
     *
     * @param index      播放的序列号
     * @param positionMs 播放的起始位置
     */
    void playIndex(int index, long positionMs);

    /**
     * 设置播放速度
     *
     * @param speed 播放速度
     */
    void setSpeed(float speed);


    /**
     * 释放播放器
     */
    void release();

    /**
     * 是否正在部播放
     *
     * @return true:正在播放 false 播放暂停
     */
    boolean isPlaying();

    /**
     * 快进
     *
     * @param milliseconds 快进的时间
     */
    void fastForward(long milliseconds);

    /**
     * 快退
     *
     * @param milliseconds 快退的时间
     */
    void fastBack(long milliseconds);


    /**
     * 播放制定的序列的音频
     *
     * @param position 序列号
     */
    void seekTo(long position);


    /**
     * 获取当前的播放序列
     */
    int getCurrentIndex();

    /**
     * 快退
     *
     * @param {@link MusicRepeatMode}
     */
    void setRepeatMode(int mode);


    /**
     * 得当前的播放进度
     *
     * @return 播放进度
     */
    long getPlayPosition();

    /**
     * 播放器回调接口
     */
    interface Callback {

        /**
         * 正在缓冲
         */
        void onBuffer();

        /**
         * 开始播放
         */
        void onPlay();

        /**
         * 暂停播放
         *
         * @param playPosition 当前暂停的音频序列号
         */
        void onPause(long playPosition);

        /**
         * 暂停结束
         *
         */
        void onStop( );

        /**
         * 播放出现异常
         *
         * @param error 异常信息
         */
        void onError(String error);

        /**
         * 播放进度
         *
         * @param progress     播放的百分比
         * @param playPosition 播放的位置
         */
        void onProgress(float progress, long playPosition);

        /**
         * 切换播放音频
         *
         * @param index 音频序列号
         */
        void onIndexChange(int index);

        /**
         * 循环模式改变
         */
        void onRepeatModeChange();
    }

    /**
     * 设置播放器的回调
     *
     * @param callback 播放的回调
     */
    void setCallback(Callback callback);
}
