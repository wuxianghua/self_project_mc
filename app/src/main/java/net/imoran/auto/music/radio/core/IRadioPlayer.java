package net.imoran.auto.music.radio.core;


import net.imoran.auto.music.radio.manager.RadioBand;

import cn.flyaudio.sdk.manager.FlyRadioManager;

public interface IRadioPlayer {

    /**
     * 选择 am
     *
     * @param am             波段
     * @param widthFrequency 是否调到相应的频率
     */
    void chooseAM(boolean widthFrequency, double am);

    /**
     * 选择fm
     *
     * @param fm             波段
     * @param widthFrequency 是否调到相应的频率
     */
    void chooseFM(boolean widthFrequency, double fm);

    /**
     * 得到当前的波段
     *
     * @return
     */
    int getCurrentBand();

    /**
     * 得到当前的频率
     *
     * @return 频率
     */
    double getCurrentFrequency();

    /**
     * @param isMute true 禁音 false 不禁音
     */
    void mute(boolean isMute);

    /**
     * 逐步改变音量 步幅 1
     *
     * @param isInc true 增加 false 降低
     */
    void changeVolumeByStep(boolean isInc);

    /**
     * 设置音量
     *
     * @param volume 音量大小 Integer.MAX_VALUE 表示最大音量
     */
    void setVolume(int volume);

    /**
     * 循环搜索
     */
    void startScan();

    /**
     * 循环模式
     *
     * @param mode {@link RepeatMode}
     */
    void repeatMode(int mode);

    /**
     * 停止搜索电台
     */
    void stopScan();

    /**
     * 切换通道为收音机
     */
    void setRadioChannel();

    /**
     * 切换通道为第三方
     */
    void setThreeChannel();

    /**
     * 通知状态改变
     */
    void notifyPhoneStatus();

    /**
     * 收音机回调
     */
    interface Callback {
        /**
         * @param type      {@link FlyRadioManager.BAND_FM} {@link FlyRadioManager.BAND_AM}
         * @param frequency 频率
         */
        void onCurrentFrequency(int type, double frequency);

        /**
         * @param type      {@link FlyRadioManager.BAND_FM} {@link FlyRadioManager.BAND_AM}
         * @param frequency 频率
         */
        void onReceiveChannel(int type, double frequency);

        /**
         * 状态回调
         *
         * @param status 收音机状态    {@link FlyRadioManager}
         */
        void onScanStatus(int status);
    }

    /**
     * 设置收音机的回调
     *
     * @param callback 收音的回调
     */
    void setCallback(Callback callback);
}
