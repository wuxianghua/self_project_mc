package net.imoran.auto.music.ui.fragment.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autochips.bluetooth.HeadsetClientProfile;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bt.BluetoothProfile;
import net.imoran.auto.music.bt.BtMusicStateListener;
import net.imoran.auto.music.bt.BtMusicStateManager;
import net.imoran.auto.music.bt.BtUtils;
import net.imoran.auto.music.mvp.view.BleMusicView;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.utils.SysStatusBarUpdateUtils;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;

public class BlePlayFragment extends BleBaseFragment implements BleMusicView, BtMusicStateListener, View.OnClickListener {
    private static final String TAG = "MusicAuxFragment";
    private static final String ACTION_BT_CONNECTED = "net.imoran.action.bluetooth.connectstatus";
    private static final String ACTION_CONNECT_BLUETOOTH = "net.imoran.action.connectbluetooth";
    private int totalLong;
    private int playTime;
    private TextView connectedPhone;
    private boolean connectStatus = false;
    private HeadsetClientProfile hfProfile;
    //已连接蓝牙设备的名称
    private String mDeviceName;
    private String title = "";
    private LinearLayout llPhoneConnectState;//蓝牙连接页面
    private RelativeLayout rlAuxPlay;//音乐播放界面
    private TextView songName;
    private TextView albumSinger;
    private TextView perv;
    private TextView next;
    private MusicBTPlayManager btPlayManager;
    private Handler mHandler = new Handler();

    public static BlePlayFragment newInstance() {
        Bundle args = new Bundle();
        BlePlayFragment fragment = new BlePlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_music_aux;
    }

    @Override
    protected void onViewCreated() {
        initView();
        initListener();
        initBT();
        initBtBroadcast();
    }

    private void initView() {
        llPhoneConnectState = (LinearLayout) rootView.findViewById(R.id.llPhoneConnectState);
        rlAuxPlay = (RelativeLayout) rootView.findViewById(R.id.rlAuxPlay);
        connectedPhone = (TextView) rootView.findViewById(R.id.connectedPhone);
        songName = (TextView) rootView.findViewById(R.id.songName);
        albumSinger = (TextView) rootView.findViewById(R.id.albumSinger);
        perv = (TextView) rootView.findViewById(R.id.perv);
        next = (TextView) rootView.findViewById(R.id.next);
    }

    private void initListener() {
        BtMusicStateManager.getInstance().setStateListener(this);
        connectedPhone.setOnClickListener(this);
        perv.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    public boolean isConnectStatus() {
        return connectStatus;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connectedPhone) {
            Intent intent = new Intent();
            intent.setAction(ACTION_CONNECT_BLUETOOTH);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", "music");
            if (mDeviceName != null) {
                intent.putExtra("conn", 1);
            } else {
                intent.putExtra("conn", 0);
            }
            startActivity(intent);
        } else if (view.getId() == R.id.perv) {
            lastBtMusic();
        } else if (view.getId() == R.id.next) {
            nextBtMusic();
        }
    }

    private void nextBtMusic() {
        if (!isConnectedBt()) return;
        BtMusicStateManager.getInstance().cmdMusicNext();
    }

    public void pauseBtMusic() {
        if (!isConnectedBt()) return;
        setPlayState(false);
        BtMusicStateManager.getInstance().cmdMusicPause();
        isMusicPlay = false;
    }

    public void playBtMusic() {
        if (!isConnectedBt()) return;
        setPlayState(true);
        BtMusicStateManager.getInstance().cmdMusicPlay();
    }

    private void initBT() {
        if (hfProfile == null) {
            hfProfile = (HeadsetClientProfile) BtUtils.getProfile(BluetoothProfile.HEADSET_CLIENT);
        }
        setConnectState();
    }

    private void setConnectState() {
        if (connectStatus && btPlayManager == null) {
            btPlayManager = new MusicBTPlayManager(getActivity(), rootView);
        }
        rlAuxPlay.setVisibility(connectStatus ? View.VISIBLE : View.GONE);
        llPhoneConnectState.setVisibility(connectStatus ? View.GONE : View.VISIBLE);
    }

    private void initBtBroadcast() {
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(ACTION_BT_CONNECTED);
        usbDeviceStateFilter.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        activity.registerReceiver(mReceiver, usbDeviceStateFilter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (ACTION_BT_CONNECTED.equals(action)) {
                connectStatus = intent.getBooleanExtra("connectStatus", false);
                if (btPlayManager != null) btPlayManager.setConnectStatus(connectStatus);
                if (connectStatus) {
                    String deviceName = intent.getStringExtra("deviceName");
                    mDeviceName = deviceName;
                } else {
                    mDeviceName = null;
                }
                setConnectState();
            } else if (BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    return;
                }
                int newState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
                if ((newState == BluetoothProfile.STATE_CONNECTED)) {
                    connectStatus = true;
                    mDeviceName = device.getName();
                } else {
                    connectStatus = false;
                    mDeviceName = null;
                }
                if (!connectStatus) {
                    setPlayState(false);
                }
            }
        }
    };

    public void setPlayState(boolean select) {
        if (btPlayManager != null) btPlayManager.setPlayState(select);
    }


    private void lastBtMusic() {
        if (!isConnectedBt()) return;
        BtMusicStateManager.getInstance().cmdMusicPrev();
    }

    private boolean isConnectedBt() {
        if (!connectStatus)
            ToastUtil.shortShow(activity, "请连接蓝牙");
        return connectStatus;
    }

    @Override
    public void onA2dpStateDisconnected() {
    }

    @Override
    public void onA2dpStateConnected() {
    }

    @Override
    public void onAvrcpStateDisconnected() {
        songName.setText("");
        albumSinger.setText("");
    }

    @Override
    public void onAvrcpStateConnected() {
    }

    @Override
    public void onBtStateOn() {
    }

    @Override
    public void onBtStateOff() {
    }

    @Override
    public void onUpdatePlaybackStatus(byte play_status, int song_len, int song_pos) {
    }

    private boolean isMusicPlay = false;

    @Override
    public void onUpdateMetadata(String title, String artist, String album) {
        if (StringUtils.isEmpty(artist) && StringUtils.isEmpty(artist) && StringUtils.isEmpty(album)) {
            isMusicPlay = false;
            return;
        }
//        Log.e("onUpdateMetadata", "title " + (StringUtils.isEmpty(title) ? "空" : title)
//                + "| artist " + (StringUtils.isEmpty(artist) ? "空" : artist) + "| album " + (StringUtils.isEmpty(album) ? "空" : album));
        isMusicPlay = true;
        if (StringUtils.isEmpty(title)) title = "未知音频";
        if (StringUtils.isEmpty(artist)) artist = "未知歌手";
        if (StringUtils.isEmpty(album)) album = "未知专辑";

        this.title = title;
        songName.setText(title);
        albumSinger.setText(album + "/" + artist.replaceAll("/", ","));
        if (isSupportVisible()) {
            SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, title, true);
            SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 0);
        }
    }

    @Override
    public void onStatePlaying() {
        if (!isMusicPlay) return;
        ((MainActivity) activity).switchToBlueToothFragment();
        SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, title, true);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 0);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPlayState(true);
            }
        }, 150);
    }

    @Override
    public void onStatePause() {
        setPlayState(false);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 1);
    }

    @Override
    public void onStateStop() {
        setPlayState(false);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 1);
    }

    @Override
    public void setMediaLengthInfo(String length) {
    }

    @Override
    public void setMediaPlayingPositionInfo(String pos) {
    }

    @Override
    public void setMusicPlayingProgressBar_setMax(int totalLong) {
        this.totalLong = totalLong;
    }

    @Override
    public void setMusicPlayingProgressBar_setProgress(int playTime) {
        this.playTime = playTime;
        if (totalLong < 1) return;
        int currentProgress = (playTime * 100 / totalLong);
        if (btPlayManager != null) btPlayManager.setSeekBarProgress(currentProgress);
    }

    @Override
    public void onResume() {
        super.onResume();
        BtMusicStateManager.getInstance().resume();
        setConnectState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mReceiver);
    }

    /**********************************通用的VUI控制*********************************
     *
     */
    @Override
    public void vuiPlay() {
        playBtMusic();
    }

    @Override
    public void vuiPause() {
        pauseBtMusic();
    }

    @Override
    public void vuiPrevious() {
        lastBtMusic();
    }

    @Override
    public void vuiNext() {
        nextBtMusic();
    }

    @Override
    public void vuiPlayList(int total, List list) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.switchToNetFragment(total, list);
    }

}
