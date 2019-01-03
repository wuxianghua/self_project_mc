package net.imoran.auto.music.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.MusicApp;
import net.imoran.auto.music.base.BaseRouteActivity;
import net.imoran.auto.music.base.SupportFragment;
import net.imoran.auto.music.bean.NetMusicDefaultData;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.radio.manager.RadioPlayMangerImp;
import net.imoran.auto.music.receiver.FlyKeyBroadcastReceiver;
import net.imoran.auto.music.receiver.NliResponseBroadcastReceiver;
import net.imoran.auto.music.serivce.MusicService;
import net.imoran.auto.music.serivce.RadioService;
import net.imoran.auto.music.ui.fragment.bluetooth.BleMainFragment;
import net.imoran.auto.music.ui.fragment.local.LocalMainFragment;
import net.imoran.auto.music.ui.fragment.local.LocalPlayFragment;
import net.imoran.auto.music.ui.fragment.net.NetMainFragment;
import net.imoran.auto.music.ui.fragment.net.NetPlayFragment;
import net.imoran.auto.music.ui.fragment.radio.RadioMainFragment;
import net.imoran.auto.music.ui.fragment.sound.SoundMainFragment;
import net.imoran.auto.music.ui.fragment.sound.SoundPlayFragment;
import net.imoran.auto.music.utils.DataConvertUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.ReplyUtils;
import net.imoran.auto.music.vui.VUIManager;
import net.imoran.auto.music.widgets.LeftTabLayout;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.BaseReply;
import net.imoran.sdk.bean.bean.BroadcastBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.SongBean;
import net.imoran.tv.common.lib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;


public class MainActivity extends BaseRouteActivity {
    public static final int NET = 0;
    public static final int RADIO = 1;
    public static final int SOUND = 2;
    public static final int LOCAL = 3;
    public static final int BLUETOOTH = 4;
    private FragmentActivity activity;
    private LeftTabLayout tabLayout;
    private SupportFragment[] mFragments = new SupportFragment[5];
    private int prePosition = 0;
    private FlyKeyBroadcastReceiver flyKeyBroadcastReceiver;
    private NliResponseBroadcastReceiver nliRespBroadcastReceiver;
    private ServiceConnection musicServiceConnection;
    private ServiceConnection radioServiceConnection;
    private boolean mIsNotNetmusicAutoPlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        VUIManager.getInstance().setMainActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian);
        activity = this;
        initService();
        initVUIReceiver();
    }

    protected boolean isNliPage() {
        return false;
    }

    @Override
    protected boolean onNliDispatch(BaseContentEntity contentEntity, String var2) {
        VUIManager.getInstance().parseNliVUIContent(contentEntity);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        //处理默认的播放数据
        int defaultIndex = NET;
        NetMusicDefaultData defaultData = new NetMusicDefaultData(null);
        String frequency = null;
        ArrayList<LetingNewsBean.LetingNewsEntity> letingNewsEntities = null;
        String calogName = null;
        ArrayList<AudioAlbumBean.AudioAlbumEntity> audioAlbums = null;
        String queryId = null;
        int total = 0;
        Bundle bundle = getIntent().getExtras();
        tabLayout = (LeftTabLayout) activity.findViewById(R.id.tabLayout);
        if (bundle != null && bundle.containsKey("baseContentEntity")) {
            BaseContentEntity contentEntity = null;
            try {
                LogUtils.e("hezhiyun", bundle.getString("baseContentEntity"));
                contentEntity = ReplyUtils.createResponseFromJson(bundle.getString("baseContentEntity"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (contentEntity != null && contentEntity.getBaseSceneEntity() != null) {
                BaseReply reply = contentEntity.getBaseReply();
                queryId = contentEntity.getBaseSceneEntity().getQueryid();
                if (reply instanceof SongBean) {
                    SongBean songBean = (SongBean) reply;
                    total = contentEntity.getBaseSceneEntity().getTotal_count();
                    List<SongModel> list = DataConvertUtils.getSongModelList(songBean);
                    if (ListUtils.isNotEmpty(list))
                        defaultData = new NetMusicDefaultData(list);
                    defaultIndex = NET;
                } else if (reply instanceof BroadcastBean) {
                    BroadcastBean broadcastBean = (BroadcastBean) reply;
                    if (broadcastBean != null) {
                        ArrayList<BroadcastBean.BroadcastEntity> broadcast = broadcastBean.getBroadcast();
                        if (broadcast != null && broadcast.size() != 0) {
                            mIsNotNetmusicAutoPlay = true;
                            frequency = broadcast.get(0).getFrequency();
                            prePosition = 1;
                        }
                    }
                    tabLayout.setRadioSelect();
                    defaultIndex = RADIO;
                } else if (reply instanceof LetingNewsBean) {
                    BaseReply baseReply = contentEntity.getBaseReply();
                    String semantic = contentEntity.getSemantic();
                    if (semantic.contains("NewsType")) {
                        calogName = semantic.substring(semantic.indexOf("NewsType") + 12, semantic.indexOf("NewsType") + 14);
                    } else {
                        calogName = "推荐";
                    }
                    prePosition = 2;
                    mIsNotNetmusicAutoPlay = true;
                    tabLayout.setSoundSelect();
                    LetingNewsBean letingBean = (LetingNewsBean) baseReply;
                    letingNewsEntities = letingBean.getLeting_news();
                    defaultIndex = SOUND;
                } else if (reply instanceof AudioAlbumBean) {
                    AudioAlbumBean audioAlbumBean = (AudioAlbumBean) reply;
                    audioAlbums = audioAlbumBean.getAudio_album();
                    prePosition = 2;
                    mIsNotNetmusicAutoPlay = true;
                    tabLayout.setSoundSelect();
                    defaultIndex = SOUND;
                }
            }
        }

        tabLayout.setOnItemSelectListener(new LeftTabLayout.onItemSelectListener() {
            @Override
            public void onSelect(int position) {
                switchTab(position);
            }
        });
        SupportFragment firstFragment = findFragment(NetMainFragment.class);
        if (firstFragment == null) {
            mFragments[NET] = NetMainFragment.newInstance(total, defaultData, mIsNotNetmusicAutoPlay);
            mFragments[RADIO] = RadioMainFragment.newInstance(frequency, queryId);
            mFragments[SOUND] = SoundMainFragment.newInstance(letingNewsEntities, queryId, calogName, audioAlbums);
            mFragments[LOCAL] = LocalMainFragment.newInstance();
            mFragments[BLUETOOTH] = BleMainFragment.newInstance();
            loadMultipleRootFragment(R.id.flContainer, defaultIndex, mFragments[NET], mFragments[RADIO], mFragments[SOUND], mFragments[LOCAL], mFragments[BLUETOOTH]);
        } else {
            mFragments[NET] = firstFragment;
            mFragments[RADIO] = findFragment(RadioMainFragment.class);
            mFragments[SOUND] = findFragment(RadioMainFragment.class);
            mFragments[LOCAL] = findFragment(LocalMainFragment.class);
            mFragments[BLUETOOTH] = findFragment(BleMainFragment.class);
        }
    }

    private void initService() {
        MusicApp.getInstance().initVUiSync(this);
        //音乐后台服务
        Intent musicIntent = new Intent(this, MusicService.class);
        musicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                MusicService musicService = ((MusicService.MusicBinder) binder).getService();
                MusicPlayMangerImp.getInstance().init(musicService);
                initView();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(musicIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        //电台后台服务
        Intent radioIntent = new Intent(this, RadioService.class);
        radioServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                RadioService radioService = ((RadioService.RadioBinder) binder).getService();
                RadioPlayMangerImp.getInstance().init(radioService);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(radioIntent, radioServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void initVUIReceiver() {
        nliRespBroadcastReceiver = new NliResponseBroadcastReceiver();
        nliRespBroadcastReceiver.registerSelf(this);
        flyKeyBroadcastReceiver = new FlyKeyBroadcastReceiver();
        flyKeyBroadcastReceiver.registerSelf(this);
    }

    private SupportFragment switchTab(final int position) {
        showHideFragment(mFragments[position], mFragments[prePosition]);
        if (prePosition == position) return mFragments[prePosition];
        pauseOrResume(prePosition, false);
        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pauseOrResume(position, true);
            }
        }, 500);
        prePosition = position;
        return mFragments[prePosition];
    }

    private void pauseOrResume(int position, boolean isPlay) {
        SupportFragment fragment = mFragments[position];
        if (position == NET && fragment instanceof NetMainFragment) {
            NetMainFragment netMainFragment = (NetMainFragment) fragment;
            if (isPlay) {
                netMainFragment.resumePlay();
            } else {
                netMainFragment.pausePlay();
            }
        } else if (position == RADIO && fragment instanceof RadioMainFragment) {
            RadioMainFragment radioMainFragment = (RadioMainFragment) fragment;
            if (isPlay) {
                radioMainFragment.resumePlay();
            } else {
                radioMainFragment.pausePlay();
            }
        } else if (position == SOUND && fragment instanceof SoundMainFragment) {
            SoundMainFragment soundMainfragment = (SoundMainFragment) fragment;
            if (isPlay) {
                soundMainfragment.resumePlay();
            } else {
                soundMainfragment.pausePlay();
            }
        } else if (position == LOCAL && fragment instanceof LocalMainFragment) {
            LocalMainFragment localMainFragment = (LocalMainFragment) fragment;
            if (isPlay) {
                localMainFragment.resumePlay();
            } else {
                localMainFragment.pausePlay();
            }
        } else if (position == BLUETOOTH && fragment instanceof BleMainFragment) {
            BleMainFragment bleMainFragment = (BleMainFragment) fragment;
            if (isPlay) {
                bleMainFragment.resumePlay();
            } else {
                bleMainFragment.pausePlay();
            }
        }
    }

    public void switchToLocalFragment() {
        if (prePosition == LOCAL) {
            LocalMainFragment mLocalMainFragment = (LocalMainFragment) mFragments[LOCAL];
            mLocalMainFragment.resumePlay();
        } else {
            tabLayout.switchToLocalFragment();
        }
    }

    public void switchToBlueToothFragment() {
        if (prePosition == BLUETOOTH) {
            BleMainFragment mBleMainFragment = (BleMainFragment) mFragments[BLUETOOTH];
            mBleMainFragment.resumePlay();
        } else {
            tabLayout.switchToBlueToothFragment();
        }
    }

    public void switchToListenFragment() {
        if (prePosition == SOUND) {
            SoundMainFragment mSoundMainFragment = (SoundMainFragment) mFragments[SOUND];
            mSoundMainFragment.resumePlay();
        } else {
            tabLayout.switchToSoundToFragment();
        }
    }

    public void switchToNetFragment() {
        if (prePosition == NET) {
            NetMainFragment mNetMainFragment = (NetMainFragment) mFragments[NET];
            mNetMainFragment.resumePlay();
        } else {
            tabLayout.switchToNetToFragment();
        }
    }

    public void switchToRadioFragment() {
        if (prePosition == RADIO) {
            RadioMainFragment mRadioMainFragment = (RadioMainFragment) mFragments[RADIO];
            mRadioMainFragment.resumePlay();
        } else {
            tabLayout.switchToRadioFragment();
        }
    }

    public void switchToSoundFragment(String target, LetingNewsBean letingNewsBean, String calogName) {
        SoundMainFragment soundMainfragment = (SoundMainFragment) mFragments[SOUND];
        if (VUIManager.LETING_NEWS.equals(target)) {
            soundMainfragment.setIsRestart(false);
            tabLayout.switchToSoundToFragment();
            soundMainfragment.vuiLetingNews(letingNewsBean, calogName);
        } else {
            tabLayout.switchToSoundToFragment();
            soundMainfragment.vuiAlbumList();
        }
    }

    public void switchToRadioFragment(String frequency, String queryId) {
        tabLayout.switchToRadioFragment();
        RadioMainFragment radioMainFragment = (RadioMainFragment) mFragments[RADIO];
        radioMainFragment.listeningBroadCast(frequency, queryId);
    }

    public void switchToNetFragment(int total, List<SongModel> list) {
        tabLayout.switchToNetToFragment();
        NetMainFragment netMainFragment = (NetMainFragment) mFragments[NET];
        netMainFragment.vuiPlayList(total, list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nliRespBroadcastReceiver.unregisterSelf(this);
        flyKeyBroadcastReceiver.unregisterSelf(this);
        unbindService(musicServiceConnection);
        unbindService(radioServiceConnection);
        RadioPlayMangerImp.getInstance().setThreeChannel();
    }

    @Override
    protected ArrayList<String> getHotWords() {
        return null;
    }

    @Override
    public void onBackPressedSupport() {
        SupportFragment mainFragment = mFragments[prePosition];
        if (mainFragment == null) {
            backToHome();
        }
        ISupportFragment topTempFragment = mainFragment.getTopChildFragment();
        if (topTempFragment == null) {
            backToHome();
        }
        if (topTempFragment instanceof SupportFragment) {
            SupportFragment topFragment = (SupportFragment) topTempFragment;
            if (topFragment == null) {
                backToHome();
            }
            switch (prePosition) {
                case NET:
                    if (!(topFragment instanceof NetPlayFragment)) {
                        topFragment.pop();
                    } else {
                        backToHome();
                    }
                    break;
                case SOUND:
                    if (!(topFragment instanceof SoundPlayFragment)) {
                        topFragment.pop();
                    } else {
                        backToHome();
                    }
                    break;
                case LOCAL:
                    if (!(topFragment instanceof LocalPlayFragment)) {
                        topFragment.pop();
                    } else {
                        backToHome();
                    }
                    break;
                case BLUETOOTH:
                case RADIO:
                    backToHome();
                    break;
            }
        } else {
            backToHome();
        }
    }

    private void backToHome() {
        Intent intentHome = new Intent();
        intentHome.setAction(Intent.ACTION_MAIN);
        intentHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(intentHome);
    }
}
