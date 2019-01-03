package net.imoran.auto.music.player.manager;


import android.content.Intent;

import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.constant.BoardCastAction;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;

import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.player.core.IMusicPlayer;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.serivce.MusicService;
import net.imoran.auto.music.utils.NetworkUtils;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;

public class MusicPlayMangerImp implements IMusicPlayManger {
    private static MusicPlayMangerImp instance;
    private MusicPlayModeData currentData;
    private MusicService service;


    private MusicPlayMangerImp() {
        currentData = new MusicPlayModeData();
    }

    public static MusicPlayMangerImp getInstance() {
        if (instance == null) {
            synchronized (MusicPlayMangerImp.class) {
                if (instance == null) {
                    instance = new MusicPlayMangerImp();
                }
            }
        }
        return instance;
    }

    public void init(MusicService service) {
        this.service = service;
    }

    @Override
    public List<SongModel> getPlayList() {
        return currentData.getSongList();
    }

    @Override
    public SongModel getPlaySong() {
        return currentData.getPlaySong();
    }

    @Override
    public void prepare(List<SongModel> list) {
        if (service != null) {
            if (ListUtils.isNotEmpty(list)) {
                currentData.getSongList().clear();
                currentData.getSongList().addAll(list);

                String[] urls = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    SongModel model = list.get(i);
                    if (model != null && StringUtils.isNotEmpty(model.getSongUrl()))
                        urls[i] = list.get(i).getSongUrl();
                }
                service.prepare(urls);
                currentData.setPlaySong(null);
            }
        }
    }

    @Override
    public void play() {
        if (service != null) service.play();
    }

    @Override
    public void pause() {
        if (!NetworkUtils.isAvailable(MusicApp.instance)) {
            ToastUtil.shortShow(MusicApp.instance, "当前网络不可用");
        }
        if (service != null) service.pause();
    }

    @Override
    public void resume() {
        if (service != null) service.resume();
    }

    @Override
    public void stop() {
        if (service != null) {
            service.stop();
            currentData.setPlaySong(null);
        }
    }

    @Override
    public void next() {
        if (service != null) service.next();
    }

    @Override
    public void previous() {
        if (service != null) service.previous();
    }

    @Override
    public void playByUuid(String uuid) {
        if (service != null) {
            if (ListUtils.isNotEmpty(currentData.getSongList())) {
                for (int i = 0; i < currentData.getSongList().size(); i++) {
                    SongModel model = currentData.getSongList().get(i);
                    if (model.getUuid().equals(uuid)) {
                        playIndex(i, 0);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void playIndex(int index, long positionMs) {
        if (!NetworkUtils.isAvailable(MusicApp.instance)) {
            ToastUtil.shortShow(MusicApp.instance, "当前网络不可用");
        }
        if (service != null) {
            service.playIndex(index, positionMs);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (service != null) service.setSpeed(speed);
    }

    @Override
    public void release() {
        if (service != null) {
            service.release();
            currentData.setPlaySong(null);
        }
    }

    @Override
    public boolean isPlaying() {
        if (service != null) return service.isPlaying();
        else return false;
    }

    @Override
    public void fastForward(long milliseconds) {
        if (service != null) service.fastForward(milliseconds);
    }

    @Override
    public void fastBack(long milliseconds) {
        if (service != null) service.fastBack(milliseconds);
    }

    @Override
    public void seekTo(long position) {
        if (service != null) service.seekTo(position);
    }

    @Override
    public void setRepeatMode(int mode) {
        if (service != null) service.setRepeatMode(mode);
    }

    @Override
    public long getPlayPosition() {
        if (service != null) return service.getPlayPosition();
        else return 0L;
    }

    private float playProgress;

    public void setCallBack(final IMusicPlayCallBack callback) {
        if (service != null) {
            service.setCallBack(new IMusicPlayer.Callback() {
                @Override
                public void onBuffer() {
                    if (callback != null) callback.onBuffer();
                }

                @Override
                public void onPlay() {
                    updateReportStatus(true);
                    int index = service.getCurrentIndex();
                    if (ListUtils.isNotEmpty(currentData.getSongList())) {
                        SongModel playSong = currentData.getSongList().get(index);
                        currentData.setPlaySong(playSong);
                        updateSystemMusicStatus(true);
                        updateSystemMusic(playSong.getName(), true);
                        if (callback != null) callback.onPlay();
                    }
                }

                @Override
                public void onPause(long playPosition) {
                    updateReportStatus(false);
                    if (currentData.getPlaySong() != null) {
                        currentData.getPlaySong().setPlayPosition(playPosition);
                        currentData.getPlaySong().setPlayProgress(playProgress);
                    }
                    updateSystemMusicStatus(false);
                    if (callback != null) callback.onPause(playPosition);
                }

                @Override
                public void onStop() {
                    if (callback != null) callback.onPlayEnd();
                }

                @Override
                public void onError(String error) {
                    if (callback != null) callback.onError(error);
                }

                @Override
                public void onProgress(float progress, long playPosition) {
                    playProgress = progress;
                    if (callback != null) callback.onProgress(progress, playPosition);
                }

                @Override
                public void onIndexChange(int playIndex) {
                    int index = playIndex % currentData.getSongList().size();
                    SongModel playSong = currentData.getSongList().get(index);
                    currentData.setPlaySong(playSong);
                    updateSystemMusicStatus(true);
                    updateSystemMusic(playSong.getName(), true);
                    if (callback != null) callback.onPlaySongChange(playSong, index);
                }

                @Override
                public void onRepeatModeChange() {
                    if (callback != null) callback.onRepeatModeChange();
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

    //发送音乐播放的转态给report
    private void updateReportStatus(boolean play) {
        Intent intent = new Intent("net.imoran.auto.report.pcm.music");
        if (play) {
            intent.putExtra("music_status", 1);
        } else {
            intent.putExtra("music_status", 0);
        }
        service.sendBroadcast(intent);
    }
}
