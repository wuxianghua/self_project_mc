package net.imoran.auto.music.serivce;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.imoran.auto.music.radio.core.IRadioPlayer;
import net.imoran.auto.music.radio.manager.RadioBand;
import net.imoran.auto.music.radio.core.RadioPlayer;

import cn.flyaudio.sdk.manager.FlyRadioManager;
import cn.flyaudio.sdk.manager.FlySystemManager;

public class RadioService extends Service {
    private RadioPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RadioBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        player = new RadioPlayer(this.getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void chooseAM(boolean widthFrequency, double am) {
        if (player != null) player.chooseAM(widthFrequency, am);
    }

    public void chooseFM(boolean widthFrequency, double fm) {
        if (player != null) player.chooseFM(widthFrequency, fm);
    }

    public int getCurrentBand() {
        if (player != null) {
            return player.getCurrentBand();
        }
        return -1;
    }

    public double getCurrentFrequency() {
        if (player != null) {
            return player.getCurrentFrequency();
        }
        return -1D;
    }

    public void mute(boolean isMute) {
        if (player != null) player.mute(isMute);
    }

    public void changeVolumeByStep(boolean isInc) {
        if (player != null) player.changeVolumeByStep(isInc);
    }

    public void setVolume(int volume) {
        if (player != null) player.setVolume(volume);
    }

    public void startScan() {
        if (player != null) player.startScan();
    }

    public void repeatMode(int mode) {
        if (player != null) player.repeatMode(mode);
    }


    public void stopScan() {
        if (player != null) player.stopScan();
    }

    public void setRadioChannel() {
        if (player != null) player.setRadioChannel();
    }

    public void setThreeChannel() {
        if (player != null) player.setThreeChannel();
    }

    public void notifyPhoneStatus() {
        if (player != null) player.notifyPhoneStatus();
    }

    public void setCallback(IRadioPlayer.Callback callback) {
        if (player != null) player.setCallback(callback);
    }

    public class RadioBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }
}
