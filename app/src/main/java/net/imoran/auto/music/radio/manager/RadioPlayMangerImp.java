package net.imoran.auto.music.radio.manager;


import android.content.Intent;
import android.util.Log;

import net.imoran.auto.music.constant.BoardCastAction;

import net.imoran.auto.music.radio.core.IRadioPlayer;
import net.imoran.auto.music.serivce.RadioService;

import cn.flyaudio.sdk.manager.FlyRadioManager;

public class RadioPlayMangerImp implements IRadioPlayManger {
    private static RadioPlayMangerImp instance;
    private RadioService service;

    private RadioPlayMangerImp() {

    }

    public static RadioPlayMangerImp getInstance() {
        if (instance == null) {
            synchronized (RadioPlayMangerImp.class) {
                if (instance == null) {
                    instance = new RadioPlayMangerImp();
                }
            }
        }
        return instance;
    }

    public void init(RadioService service) {
        this.service = service;
    }

    @Override
    public void chooseAM(boolean widthFrequency, double am) {
        if (service != null) {
            service.chooseAM(widthFrequency, am);
        }
    }

    @Override
    public void chooseFM(boolean widthFrequency, double fm) {
        if (service != null) {
            service.chooseFM(widthFrequency, fm);
        }
    }

    @Override
    public RadioBand getCurrentBand() {
        if (service != null) {
            int band = service.getCurrentBand();
            if (band == FlyRadioManager.BAND_FM) {
                return RadioBand.FM;
            } else {
                return RadioBand.AM;
            }
        }
        return RadioBand.AM;
    }

    @Override
    public double getCurrentFrequency() {
        if (service != null) {
            return service.getCurrentFrequency();
        }
        return -1D;
    }

    @Override
    public void mute(boolean isMute) {
        if (service != null) {
            service.mute(isMute);
        }
    }

    @Override
    public void changeVolumeByStep(boolean isInc) {
        if (service != null) {
            service.changeVolumeByStep(isInc);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (service != null) {
            service.setVolume(volume);
        }
    }

    @Override
    public void startScan() {
        if (service != null) {
            service.startScan();
        }
    }

    @Override
    public void repeatMode(int mode) {
        if (service != null) {
            service.repeatMode(mode);
        }
    }

    @Override
    public void stopScan() {
        if (service != null) {
            service.stopScan();
        }
    }

    @Override
    public void setRadioChannel() {
        if (service != null) {
            service.setRadioChannel();
        }
    }

    @Override
    public void setThreeChannel() {
        if (service != null) {
            service.setThreeChannel();
        }
    }

    @Override
    public void notifyPhoneStatus() {
        if (service != null) {
            service.notifyPhoneStatus();
        }
    }

    public void setCallBack(final IRadioPlayCallBack callback) {
        if (service != null) {
            service.setCallback(new IRadioPlayer.Callback() {
                @Override
                public void onCurrentFrequency(int type, double frequency) {
                    int band = service.getCurrentBand();
                    RadioBand radioBand;
                    if (band == FlyRadioManager.BAND_FM) {
                        radioBand = RadioBand.FM;
                    } else {
                        radioBand = RadioBand.AM;
                    }
                    if (callback != null)
                        callback.onCurrentFrequency(radioBand, frequency);
                }

                @Override
                public void onReceiveChannel(int type, double frequency) {
                    int band = service.getCurrentBand();
                    RadioBand radioBand;
                    if (band == FlyRadioManager.BAND_FM) {
                        radioBand = RadioBand.FM;
                    } else {
                        radioBand = RadioBand.AM;
                    }
                    if (callback != null)
                        callback.onReceiveChannel(radioBand, frequency);
                }

                @Override
                public void onScanStatus(int status) {
                    if (callback != null)
                        callback.onScanStatus(status);
                }
            });
        }
    }

    //发送广播通知系统Music UI更新
    private void updateSystemMusic(String songName, boolean musicSwitch) {
        try {
            Intent intent = new Intent(BoardCastAction.UPDATE_SYSTEM_UI_MUSIC_TITLE_ACTION);
            intent.putExtra("musicTitle", songName);
            intent.putExtra("musicSwitch", musicSwitch);
            service.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送广播通知系统Music 播放状态
    private void updateSystemMusicStatus(boolean isPlay) {
        try {
            Intent intent = new Intent(BoardCastAction.UPDATE_SYSTEM_UI_MUSIC_TITLE_ACTION);
            intent.putExtra("playStatus", isPlay ? 0 : 1);
            service.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
