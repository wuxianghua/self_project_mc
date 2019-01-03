package net.imoran.auto.music.bt;

/**
 *
 * 蓝牙音乐-页面显示-状态
 */

public interface BtMusicStateListener {

    /**
     * A2dp 未连接
     */
    void onA2dpStateDisconnected();

    /**
     * A2dp 已连接
     */
    void onA2dpStateConnected();

    /**
     * Arvcp 未连接
     */
    void onAvrcpStateDisconnected();

    /**
     * Arvcp 已连接
     */
    void onAvrcpStateConnected();

    /**
     * Indicates the local Bluetooth adapter is on, and ready for use.
     */
    void onBtStateOn();

    /**
     * Indicates the local Bluetooth adapter is turning off. Local clients
     * should immediately attempt graceful disconnection of any remote links.
     */
    void onBtStateOff();

    /**
     * 更新播放进度信息
     * @param play_status
     * @param song_len
     * @param song_pos
     */
    void onUpdatePlaybackStatus(byte play_status, int song_len, int song_pos);

    /**
     * 更新歌曲信息
     * @param title
     * @param artist
     * @param album
     */
    void onUpdateMetadata(String title, String artist, String album);

    /**
     * 播放状态
     */
    void onStatePlaying();

    /**
     * 暂停状态
     */
    void onStatePause();

    /**
     * 停止状态
     */
    void onStateStop();


    /**
     * 播放进度条相关设置
     *
     * 设置播放歌曲总长度
     * @param length
     */
    void setMediaLengthInfo(String length);

    /**
     * 播放进度条相关设置
     *
     * 设置歌曲播放的位置
     * @param pos
     */
    void setMediaPlayingPositionInfo(String pos);

    /**
     * 播放进度条相关设置
     *
     * 进度条总长度
     * @param totalLong
     */
    void setMusicPlayingProgressBar_setMax(int totalLong);

    /**
     * 播放进度条相关设置
     *
     * 进度条当前播放长度
     * @param playTime
     */
    void setMusicPlayingProgressBar_setProgress(int playTime);

}
