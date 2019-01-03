package net.imoran.auto.music.radio.core;

import android.content.Context;
import android.util.Log;

import cn.flyaudio.sdk.FlySDKManager;
import cn.flyaudio.sdk.InitListener;
import cn.flyaudio.sdk.listener.RadioListener;
import cn.flyaudio.sdk.listener.SystemListener;
import cn.flyaudio.sdk.manager.FlyBluetoothManager;
import cn.flyaudio.sdk.manager.FlyRadioManager;
import cn.flyaudio.sdk.manager.FlySystemManager;


public class RadioPlayer implements IRadioPlayer {

    private static final String TAG = "RadioPlayer";

    public RadioPlayer(Context context) {
        initRadio(context);
    }

    private void initRadio(Context context) {
    }

    public void setSystemListener(SystemListener systemListener) {
        FlySystemManager.getInstance().setSystemListener(systemListener);
    }

    public void setRadioListener(RadioListener radioListener) {
        FlyRadioManager.getInstance().setListener(radioListener);
    }

    @Override
    public void chooseAM(boolean widthFrequency, double am) {
        if (widthFrequency) {
            FlyRadioManager.getInstance().switchBand(FlyRadioManager.BAND_AM, am);
        } else {
            FlyRadioManager.getInstance().switchBand(FlyRadioManager.BAND_AM);
        }
    }

    @Override
    public void chooseFM(boolean widthFrequency, double fm) {
        if (widthFrequency) {
            FlyRadioManager.getInstance().switchBand(FlyRadioManager.BAND_FM, fm);
        } else {
            FlyRadioManager.getInstance().switchBand(FlyRadioManager.BAND_FM);
        }
    }

    @Override
    public int getCurrentBand() {
        return FlyRadioManager.getInstance().getCurrentBand();
    }

    @Override
    public double getCurrentFrequency() {
        double currentFrequency = FlyRadioManager.getInstance().getCurrentFrequency();
        Log.e(TAG, "currentFrequency-----" + currentFrequency);
        return currentFrequency;
    }

    @Override
    public void mute(boolean isMute) {
        FlySystemManager.getInstance().mute(isMute);
    }

    @Override
    public void changeVolumeByStep(boolean isInc) {
        if (isInc) {
            FlySystemManager.getInstance().incVolume(1);
        } else {
            FlySystemManager.getInstance().decVolume(1);
        }
    }

    @Override
    public void setVolume(int volume) {
        int v = volume;
        if (volume == Integer.MAX_VALUE) {
            v = FlySystemManager.getInstance().getMaxVolume();
        }
        FlySystemManager.getInstance().setVolume(v);
    }

    @Override
    public void startScan() {
        FlyRadioManager.getInstance().startScan(FlyRadioManager.SCAN_STATUS_REPEAT);
    }

    @Override
    public void repeatMode(int mode) {
        if (mode == RepeatMode.REPEAT_DEC_PLAY) {
            FlyRadioManager.getInstance().startScan(FlyRadioManager.SCAN_STATUS_REPEAT_DEC);
        } else if (mode == RepeatMode.REPEAT_INC_PLAY) {
            FlyRadioManager.getInstance().startScan(FlyRadioManager.SCAN_STATUS_REPEAT_INC);
        }
    }

    @Override
    public void stopScan() {
        FlyRadioManager.getInstance().stopScan();
    }

    @Override
    public void setRadioChannel() {
        FlySystemManager.getInstance().setVoiceChannel(FlySystemManager.VOICE_RADIO);
    }

    @Override
    public void setThreeChannel() {
        FlySystemManager.getInstance().setVoiceChannel(FlySystemManager.VOICE_OTHER);
    }

    @Override
    public void notifyPhoneStatus() {
        FlyBluetoothManager.getInstance().notifyBTPhoneStatus(FlyBluetoothManager.BT_PHONE_STATUS_CALL_IN);
    }

    @Override
    public void setCallback(final Callback callback) {
        FlyRadioManager.getInstance().setListener(new RadioListener() {
            @Override
            public void onCurrentFrequency(int i, double v) {
                if (callback != null) callback.onCurrentFrequency(i, v);
            }

            @Override
            public void onReceiveChannel(int i, double v) {
                if (callback != null) callback.onReceiveChannel(i, v);
            }

            @Override
            public void onScanStatus(int i) {
                if (callback != null) callback.onScanStatus(i);
            }
        });
    }
}
