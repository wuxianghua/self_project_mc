package net.imoran.auto.music.serivce;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.imoran.auto.music.player.core.MusicPlayer;
import net.imoran.auto.music.player.core.IMusicPlayer;

public class MusicService extends Service {
    private MusicPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MusicPlayer(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public void prepare(String... url) {
        player.prepare(url);
    }


    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.resume();
    }

    public void stop() {
        player.stop();
    }

    public void next() {
        player.next();
    }

    public void previous() {
        player.previous();
    }

    public void playIndex(int index, long positionMs) {
        player.playIndex(index,positionMs);
    }

    public void setSpeed(float speed) {
        player.setSpeed(speed);
    }

    public void release() {
        player.release();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void fastForward(long milliseconds) {
        player.fastForward(milliseconds);
    }

    public void fastBack(long milliseconds) {
        player.fastBack(milliseconds);
    }

    public void seekTo(long position) {
        player.seekTo(position);
    }

    public void setRepeatMode(int mode) {
        player.setRepeatMode(mode);
    }

    public long getPlayPosition() {
        return player.getPlayPosition();
    }

    public int getCurrentIndex() {
        return player.getCurrentIndex();
    }

    public void setCallBack(IMusicPlayer.Callback callback) {
        player.setCallback(callback);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
