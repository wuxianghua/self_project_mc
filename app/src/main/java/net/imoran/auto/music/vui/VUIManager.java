package net.imoran.auto.music.vui;

import android.app.Activity;
import android.os.Handler;

import net.imoran.auto.music.player.core.MusicRepeatMode;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.base.BaseSceneEntity;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.BaseReply;
import net.imoran.sdk.bean.bean.BroadcastBean;
import net.imoran.sdk.bean.bean.FastBackwardBean;
import net.imoran.sdk.bean.bean.FastForwardBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.LocalHotwordBean;
import net.imoran.sdk.bean.bean.MusicModeBean;
import net.imoran.sdk.bean.bean.OpenBean;
import net.imoran.sdk.bean.bean.SongBean;
import net.imoran.sdk.bean.entity.HotWordEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VUIManager implements IVUIManager {
    public static final String LETING_NEWS = "leting_news";
    public static final String ALBUM_PROGRAM = "album_program";
    private static final String TAG = "VUIManager";
    private static VUIManager instance;
    private IVUICallBack vuiCallBack;
    private String pageType;
    private Handler mHandler = new Handler();

    public static VUIManager getInstance() {
        if (instance == null) {
            synchronized (VUIManager.class) {
                if (instance == null) {
                    instance = new VUIManager();
                }
            }
        }
        return instance;
    }

    private VUIManager() {

    }

    public void setVuiCallBack(IVUICallBack vuiCallBack, String pageType) {
        this.vuiCallBack = vuiCallBack;
        this.pageType = pageType;
    }

    private MainActivity mainActivity;

    public void setMainActivity(Activity activity) {
        mainActivity = (MainActivity) activity;
    }


    @Override
    public void parseFlyContent(int keyCode) {
        switch (keyCode) {
            case 56:
                if (vuiCallBack != null) vuiCallBack.vuiNext();
                break;
            case 57:
                if (vuiCallBack != null) vuiCallBack.vuiPrevious();
                break;
        }
    }

    public void parseNliVUIContent(BaseContentEntity contentEntity) {
        if (contentEntity == null || contentEntity.getBaseSceneEntity() == null) return;
        BaseSceneEntity sceneEntity = contentEntity.getBaseSceneEntity();
        String scene = sceneEntity.domain + sceneEntity.intention + sceneEntity.type;
        if (handOnLineCmd(scene, contentEntity)) return;
        if (VUIConstant.CMD_OPEN_PLAYLIST.equals(scene)) {
            String queryid = sceneEntity.getQueryid();
            if (pageType.equals("play")) {
                if (vuiCallBack != null) vuiCallBack.openPlayList(queryid);
            }
        } else if (VUIConstant.CMD_LOCAL_HOTWORD.equals(scene)) {
            BaseReply baseReply = contentEntity.getBaseReply();
            if (baseReply != null && baseReply instanceof LocalHotwordBean) {
                LocalHotwordBean localHotwordBean = (LocalHotwordBean) baseReply;
                ArrayList<LocalHotwordBean.LocalHotwordEntity> localHotword = localHotwordBean.getLocal_hotword();
                if (ListUtils.isNotEmpty(localHotword)) {
                    LocalHotwordBean.LocalHotwordEntity entity = localHotword.get(0);
                    if (entity != null)
                        if (vuiCallBack != null) vuiCallBack.vuiHotWords(entity.getWord());
                }
            }
        } else if (VUIConstant.MUSIC_SEARCHING_SONG.equals(scene)) {
            handPlayList(contentEntity);
        } else if (VUIConstant.CMD_SAVE_MUSIC.equals(scene)) {
            if (vuiCallBack != null) {
                vuiCallBack.vuiCollectSong();
            }
        } else if (VUIConstant.CMD_CANCEL_SAVE_MUSIC.equals(scene)) {
            if (vuiCallBack != null) {
                vuiCallBack.vuiCancelCollecSong();
            }
        } else if (VUIConstant.CMD_SEARCHING_ALBUM.equals(scene)) {
            if (mainActivity == null) return;
            mainActivity.switchToSoundFragment(ALBUM_PROGRAM, null, null);
            final BaseContentEntity finalContentEntity = contentEntity;
            final String queryid = sceneEntity.getQueryid();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handSearchAlbumData(finalContentEntity, queryid);
                }
            }, 500);
        } else if (VUIConstant.AUDIO_LISTENING_PROGRAM.equals(scene)) {
            handSearchProgramData(contentEntity);
        } else if (VUIConstant.NEWS_LISTENING_LETING_NEWS.equals(scene)) {
            final String queryid = sceneEntity.getQueryid();
            handListeningLetingNew(contentEntity, queryid);
        } else if (VUIConstant.CMD_NEXT_PAGE.equals(scene)) {
            if (!pageType.equals("play"))
                handNextPageCmd();
        } else if (VUIConstant.CMD_LAST_PAGE.equals(scene)) {
            if (!pageType.equals("play"))
                handLastPageCmd();
        } else if (VUIConstant.MUSIC_LISTENERING_SONG.equals(scene)) {
            handListeningSong(contentEntity);
        } else if (VUIConstant.CMD_PLAY_BLE_MUSIC.equals(scene)) {
            changeToTargetPage(VUIConstant.OPEN_BLUE_MUSIC);
        } else if (VUIConstant.CMD_PLAY_LOCAL_MUSIC.equals(scene)) {
            changeToTargetPage(VUIConstant.OPEN_LOCAL_MUSIC);
        } else if (VUIConstant.CMD_PLAY_NET_MUSIC.equals(scene)) {
            changeToTargetPage(VUIConstant.OPEN_NET_MUSIC);
        } else if (VUIConstant.CMD_PLAY_RADIO_MUSIC.equals(scene)) {
            changeToTargetPage(VUIConstant.OPEN_RADIO_MUSIC);
        } else if (VUIConstant.CMD_PLAY_SOUND_MUSIC.equals(scene)) {
            changeToTargetPage(VUIConstant.OPEN_SOUND_MUSIC);
        } else if (VUIConstant.RADIO_LISTENING_BROADCAST.equals(scene)) {
            openBroadCast(contentEntity);
        } else {
            //打开音乐的特定模块
            BaseReply baseReply = contentEntity.getBaseReply();
            if (baseReply != null && baseReply instanceof OpenBean) {
                OpenBean openBean = (OpenBean) baseReply;
                List<OpenBean.OpenEntity> open = openBean.getOpen();
                if (open != null && open.size() != 0) {
                    String target = open.get(0).getTarget();
                    changeToTargetPage(target);
                }
            }
        }
    }

    private void openBroadCast(BaseContentEntity contentEntity) {
        BaseReply baseReply = contentEntity.getBaseReply();
        String queryId = contentEntity.getBaseSceneEntity().getQueryid();
        if (baseReply instanceof BroadcastBean) {
            BroadcastBean broadcastBean = (BroadcastBean) baseReply;
            if (broadcastBean != null) {
                ArrayList<BroadcastBean.BroadcastEntity> broadcast = broadcastBean.getBroadcast();
                if (broadcast != null && broadcast.size() != 0) {
                    String frequency = broadcast.get(0).getFrequency();
                    if (mainActivity != null) {
                        mainActivity.switchToRadioFragment(frequency, queryId);
                    }
                }
            }
        }
    }

    private void changeToTargetPage(String target) {
        if (mainActivity == null) return;
        if (VUIConstant.OPEN_LOCAL_MUSIC.equals(target)) {
            mainActivity.switchToLocalFragment();
        } else if (VUIConstant.OPEN_NET_MUSIC.equals(target)) {
            mainActivity.switchToNetFragment();
        } else if (VUIConstant.OPEN_BLUE_MUSIC.equals(target)) {
            mainActivity.switchToBlueToothFragment();
        } else if (VUIConstant.OPEN_RADIO_MUSIC.equals(target)) {
            mainActivity.switchToRadioFragment();
        } else if (VUIConstant.OPEN_SOUND_MUSIC.equals(target)) {
            mainActivity.switchToListenFragment();
        }
    }

    private void handLastPageCmd() {
        if (vuiCallBack != null) vuiCallBack.vuiPreviousPage();
    }

    private void handNextPageCmd() {
        if (vuiCallBack != null) vuiCallBack.vuiNextPage();
    }

    private void handSearchProgramData(BaseContentEntity contentEntity) {
        BaseReply baseReply = contentEntity.getBaseReply();
        if (baseReply instanceof AudioProgramBean) {
            AudioProgramBean programBean = (AudioProgramBean) baseReply;
            if (vuiCallBack != null) vuiCallBack.vuiProgramList(programBean, "");
        }
    }

    private void handListeningLetingNew(BaseContentEntity contentEntity, String queryId) {
        BaseReply baseReply = contentEntity.getBaseReply();
        String semantic = contentEntity.getSemantic();
        String calogName;
        if (semantic.contains("NewsType")) {
            calogName = semantic.substring(semantic.indexOf("NewsType") + 12, semantic.indexOf("NewsType") + 14);
        } else {
            calogName = "推荐";
        }
        if (baseReply instanceof LetingNewsBean) {
            LetingNewsBean letingBean = (LetingNewsBean) baseReply;
            if (mainActivity == null) return;
            mainActivity.switchToSoundFragment(LETING_NEWS, letingBean, calogName);
        }
    }

    private void handSearchAlbumData(BaseContentEntity contentEntity, String queryId) {
        BaseReply baseReply = contentEntity.getBaseReply();
        if (baseReply instanceof AudioAlbumBean) {
            AudioAlbumBean audioAlbumBean = (AudioAlbumBean) baseReply;
            if (vuiCallBack != null) vuiCallBack.vuiAlbumList(audioAlbumBean, queryId);
        }
    }

    /**
     * 在线指令词处理
     *
     * @param scene
     */
    private boolean handOnLineCmd(String scene, BaseContentEntity contentEntity) {
        boolean consume = false;
        switch (scene) {
            //多媒体播放控制命令集合
            case VUIConstant.CMD_PLAY_BT://播放
            case VUIConstant.CMD_VUI_PLAY:
            case VUIConstant.CMD_CONTINUE:
            case VUIConstant.CMD_PLAY:
                if (vuiCallBack != null) vuiCallBack.vuiPlay();
                consume = true;
                break;
            case VUIConstant.CMD_PAUSE://停止
            case VUIConstant.CMD_VUI_PAUSE:
            case VUIConstant.CMD_END:
            case VUIConstant.CMD_MUSIC_OFF:
                if (vuiCallBack != null) vuiCallBack.vuiPause();
                consume = true;
                break;
            case VUIConstant.CMD_NEXT://下一个
            case VUIConstant.CMD_NEXT_SONG:
                if (vuiCallBack != null) vuiCallBack.vuiNext();
                consume = true;
                break;
            case VUIConstant.CMD_LAST_SONG://上一个
            case VUIConstant.CMD_LAST:
            case VUIConstant.CMD_PREVIOUS:
                if (vuiCallBack != null) vuiCallBack.vuiPrevious();
                consume = true;
                break;
            case VUIConstant.CMD_MUSIC_MODE:
                BaseReply replyMode = contentEntity.getBaseReply();
                if (replyMode instanceof MusicModeBean) {
                    MusicModeBean musicModeBean = (MusicModeBean) replyMode;
                    if (musicModeBean != null && ListUtils.isNotEmpty(musicModeBean.getMusic_mode())) {
                        MusicModeBean.MusicModeEntity entity = musicModeBean.getMusic_mode().get(0);
                        if (entity != null) {
                            String mode = entity.getMode();
                            if ("single".equals(mode)) {
                                vuiCallBack.vuiChangeRepeatMode(MusicRepeatMode.SINGLE_REPEAT_PLAY);
                            } else if ("cycle".equals(mode)) {
                                vuiCallBack.vuiChangeRepeatMode(MusicRepeatMode.ORDER_REPEAT_PLAY);
                            } else if ("random".equals(mode)) {
                                vuiCallBack.vuiChangeRepeatMode(MusicRepeatMode.RANDOM_PLAY);
                            } else if ("fast_speed".equals(mode)) {
                                if (StringUtils.isNotEmpty(entity.getNum_value())) {
                                    try {
                                        float speed = Float.valueOf(entity.getNum_value());
                                        vuiCallBack.vuiChangeSpeed(speed);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            consume = true;
                        }
                    }
                }
                break;
            case VUIConstant.CMD_COLLECT_SONG:
                if (vuiCallBack != null) vuiCallBack.vuiCollectSong();
                consume = true;
                break;
            case VUIConstant.CMD_FAST_FORWARD://快进
                BaseReply replyForward = contentEntity.getBaseReply();
                if (replyForward instanceof FastForwardBean) {
                    FastForwardBean forwardBean = (FastForwardBean) replyForward;
                    if (forwardBean != null && ListUtils.isNotEmpty(forwardBean.getFast_forward())
                            && forwardBean.getFast_forward().get(0) != null) {
                        FastForwardBean.FastForwardEntity entity = forwardBean.getFast_forward().get(0);
                        if (entity != null && StringUtils.isNotEmpty(entity.getTime()) && StringUtils.isNumeric(entity.getTime())) {
                            String timeStr = entity.getTime();
                            long time = Long.parseLong(timeStr) * 1000L;
                            if (vuiCallBack != null) vuiCallBack.vuiFastForward(time);
                            consume = true;
                        }
                    } else {
                        if (vuiCallBack != null) vuiCallBack.vuiFastForward(30000);
                        consume = true;
                    }
                }
                break;
            case VUIConstant.CMD_FAST_BACKWARD://快退
                BaseReply replyBack = contentEntity.getBaseReply();
                if (replyBack instanceof FastBackwardBean) {
                    FastBackwardBean backwardBean = (FastBackwardBean) replyBack;
                    if (backwardBean != null && ListUtils.isNotEmpty(backwardBean.getFast_backward())
                            && backwardBean.getFast_backward().get(0) != null) {
                        FastBackwardBean.FastBackwardEntity entity = backwardBean.getFast_backward().get(0);
                        if (entity != null && StringUtils.isNotEmpty(entity.getTime()) && StringUtils.isNumeric(entity.getTime())) {
                            String timeStr = entity.getTime();
                            long time = Long.parseLong(timeStr) * 1000L;
                            if (vuiCallBack != null) vuiCallBack.vuiFastBack(time);
                            consume = true;
                        }
                    } else {
                        if (vuiCallBack != null) vuiCallBack.vuiFastBack(30000);
                        consume = true;
                    }
                }
                break;
        }
        return consume;
    }

    private void handPlayList(BaseContentEntity contentEntity) {
        BaseReply reply = contentEntity.getBaseReply();
        if (reply instanceof SongBean) {
            SongBean songBean = (SongBean) reply;
            List<SongModel> list = DataConvertUtils.getSongModelList(songBean);
            if (vuiCallBack != null) vuiCallBack.vuiListQueryId(contentEntity.getBaseSceneEntity().queryid);
            if (vuiCallBack != null) vuiCallBack.vuiPlayList(contentEntity.getBaseSceneEntity().getTotal_count(), list);
        }
    }

    private void handListeningSong(BaseContentEntity contentEntity) {
        if (contentEntity.getBaseReply() instanceof SongBean) {
            SongBean songBean = (SongBean) contentEntity.getBaseReply();
            if (ListUtils.isNotEmpty(songBean.getSong())) {
                SongBean.SongEntity songEntity = songBean.getSong().get(0);
                int index = -1;
                try {
                    JSONObject jsonObject = new JSONObject(contentEntity.getSemantic());
                    index = jsonObject.getInt("index_number");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (index >= 0) {
                    if (vuiCallBack != null) vuiCallBack.vuiPlayIndex(index - 1);
                } else {
                    handPlayList(contentEntity);
                }
            }
        }
    }
}
