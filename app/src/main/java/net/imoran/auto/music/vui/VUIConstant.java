package net.imoran.auto.music.vui;

import android.content.Intent;

import java.io.Serializable;

public class VUIConstant {

    public static final String MUSIC_SEARCHING_SONG = Domain.MUSIC + Intention.SEARCHING + Type.SONG;
    public static final String CMD_OPEN_PLAYLIST = Domain.CMD + Intention.INSTRUCTING + Type.OPEN_PLAYLIST;
    //下一首
    public static final String CMD_NEXT = Domain.CMD + Intention.INSTRUCTING + Type.NEXT;
    public static final String CMD_NEXT_SONG = Domain.CMD + Intention.INSTRUCTING + Type.NEXT_SONG;
    //上一首
    public static final String CMD_LAST = Domain.CMD + Intention.INSTRUCTING + Type.LAST;
    public static final String CMD_LAST_SONG = Domain.CMD + Intention.INSTRUCTING + Type.LAST_SONG;
    public static final String CMD_PREVIOUS = Domain.CMD + Intention.INSTRUCTING + Type.PREVIOUS;
    //暂停
    public static final String CMD_VUI_PAUSE = Domain.CMD + Intention.INSTRUCTING + Type.VUI_PAUSE;
    public static final String CMD_PAUSE = Domain.CMD + Intention.INSTRUCTING + Type.PAUSE;
    public static final String CMD_END = Domain.CMD + Intention.INSTRUCTING + Type.END;
    public static final String CMD_MUSIC_OFF = Domain.CMD + Intention.INSTRUCTING + Type.MUSIC_OFF;
    //播放
    public static final String CMD_PLAY = Domain.CMD + Intention.INSTRUCTING + Type.PLAY;
    public static final String CMD_PLAY_BT = Domain.CMD + Intention.INSTRUCTING + Type.PLAY_BT;
    public static final String CMD_VUI_PLAY = Domain.CMD + Intention.INSTRUCTING + Type.VUI_PLAY;
    public static final String CMD_CONTINUE = Domain.CMD + Intention.INSTRUCTING + Type.CONTINUE;
    //快进
    public static final String CMD_FAST_FORWARD = Domain.CMD + Intention.INSTRUCTING + Type.FAST_FORWARD;
    //快退
    public static final String CMD_FAST_BACKWARD = Domain.CMD + Intention.INSTRUCTING + Type.FAST_BACKWARD;
    //播放模式
    public static final String CMD_MUSIC_MODE = Domain.CMD + Intention.INSTRUCTING + Type.MUSIC_MODE;
    //收藏歌曲
    public static final String CMD_COLLECT_SONG = Domain.CMD + Intention.INSTRUCTING + Type.COLLECT_SONG;
    public static final String CMD_SEARCHING_ALBUM = Domain.RADIO + Intention.SEARCHING + Type.AUDIO_ALBUM;
    public static final String AUDIO_LISTENING_PROGRAM = Domain.RADIO + Intention.LISTENERING + Type.AUDIO_PROGRAM;
    //下一页
    public static final String CMD_NEXT_PAGE = Domain.CMD + Intention.INSTRUCTING + Type.NEXT_PAGE;
    //上一页
    public static final String CMD_LAST_PAGE = Domain.CMD + Intention.INSTRUCTING + Type.LAST_PAGE;
    //选择播放的index
    public static final String MUSIC_LISTENERING_SONG = Domain.MUSIC + Intention.LISTENERING + Type.SONG;
    //播放网络音乐
    public static final String CMD_PLAY_NET_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.NET_MUSIC;
    //播放有声节目
    public static final String CMD_PLAY_SOUND_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.SOUND_MUSIC;
    //播放电台音频
    public static final String CMD_PLAY_RADIO_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.RADIO_MUSIC;
    //播放蓝牙音乐
    public static final String CMD_PLAY_BLE_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.BLE_MUSIC;
    //播放本地音乐
    public static final String CMD_PLAY_LOCAL_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.LOCAL_MUSIC;
    //收藏音乐
    public static final String CMD_SAVE_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.SAVE;
    //取消收藏音乐
    public static final String CMD_CANCEL_SAVE_MUSIC = Domain.CMD + Intention.INSTRUCTING + Type.CANCEL_SAVE;
    //我要听新闻
    public static final String NEWS_LISTENING_LETING_NEWS = Domain.NEWS + Intention.LISTENERING + Type.LETING_NEWS;
    //我要听广播
    public static final String RADIO_LISTENING_BROADCAST = Domain.RADIO + Intention.LISTENERING + Type.BROADCAST;
    //热词
    public static final String CMD_LOCAL_HOTWORD = Domain.CMD + Intention.INSTRUCTING + Type.LOCALHOTWORD;
    //打开网络音乐
    public static final String OPEN_NET_MUSIC = "internet_music";
    //打开本地音乐
    public static final String OPEN_LOCAL_MUSIC = "local_music";
    //打开有声节目
    public static final String OPEN_SOUND_MUSIC = "podcast";
    //打开电台音频
    public static final String OPEN_RADIO_MUSIC = "radio_music";
    //打开蓝牙音乐
    public static final String OPEN_BLUE_MUSIC = "bluetooth_music";


    public static class Domain {
        public static final String CMD = "cmd";
        public static final String MUSIC = "music";
        public static final String RADIO = "radio";
        public static final String NEWS = "news";

    }

    public static class Intention {
        public static final String INSTRUCTING = "instructing";
        public static final String SEARCHING = "searching";
        public static final String LISTENERING = "listening";
    }

    public static class Type {
        public static final String VUI_NEXT = "vuiNext";
        public static final String NEXT = "next";
        public static final String NEXT_SONG = "next_song";
        public static final String LAST = "last";
        public static final String LAST_SONG = "last_song";
        public static final String PREVIOUS = "previous";
        public static final String VUI_PREVIOUS = "vuiPrevious";
        public static final String VUI_PLAY = "vuiPlay";
        public static final String PAUSE = "pause";
        public static final String VUI_PAUSE = "vuiPause";
        public static final String SKIPTO_START = "skipto_start";
        public static final String CONTINUE = "continue";
        public static final String MUSIC_MODE = "music_mode";
        public static final String JUMP_TIME = "jump_time";
        public static final String FAST_FORWARD = "fast_forward";
        public static final String FAST_BACKWARD = "fast_backward";
        public static final String BACK = "back";
        public static final String SLEEP_MODE = "sleep_mode";
        public static final String CLOSE = "close";
        public static final String END = "end";
        public static final String MUSIC_OFF = "music_off";
        public static final String AUDIO_ALBUM = "audio_album";
        public static final String AUDIO_PROGRAM = "audio_program";
        public static final String SONG = "song";
        public static final String OPEN_PLAYLIST = "open_playlist";
        public static final String PLAY_BT = "play_bt";
        public static final String PLAY = "play";
        public static final String COLLECT_SONG = "collect_song";
        public static final String NEXT_PAGE = "next_page";
        public static final String LAST_PAGE = "last_page";
        //播放网络音乐
        public static final String NET_MUSIC = "play_internet_music";
        //播放有声节目
        public static final String SOUND_MUSIC = "play_podcast";
        //播放电台音频
        public static final String RADIO_MUSIC = "play_radio_audio";
        //播放蓝牙音乐
        public static final String BLE_MUSIC = "play_bluetooth_music";
        //播放本地音乐
        public static final String LOCAL_MUSIC = "play_local_music";
        //收藏音乐
        public static final String SAVE = "save";
        //取消收藏音乐
        public static final String CANCEL_SAVE = "cancel_save";
        //听新闻
        public static final String LETING_NEWS = "leting_news";
        //广播
        public static final String BROADCAST = "broadcast";
        //热词
        public static final String LOCALHOTWORD = "local_hotword";
    }


}
