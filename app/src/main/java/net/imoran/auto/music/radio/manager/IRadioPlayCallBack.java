package net.imoran.auto.music.radio.manager;

public interface IRadioPlayCallBack {
    /**
     * @param band
     * @param frequency 频率
     */
    void onCurrentFrequency(RadioBand band, double frequency);

    /**
     * @param band
     * @param frequency 频率
     */
    void onReceiveChannel(RadioBand band, double frequency);

    /**
     * 状态回调
     *
     * @param status 收音机状态
     */
    void onScanStatus(int status);
}
