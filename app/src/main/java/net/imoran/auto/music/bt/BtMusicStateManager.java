package net.imoran.auto.music.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.autochips.bluetooth.A2dpSinkProfile;
import com.autochips.bluetooth.AvrcpControllerProfile;
import com.autochips.bluetooth.AvrcpCtPlayerUtility;
import com.autochips.bluetooth.LocalBluetoothAdapter;

import java.util.List;

public class BtMusicStateManager {
    private static final String TAG = "BtMusicStateManager";
    private static boolean D = true;
    private Toast mToast;
    private LocalBluetoothAdapter mLocalAdapter;
    //a2dp/avrcp state
    public static int mA2dpsinkstate = BluetoothProfile.STATE_DISCONNECTED;
    public static int mAvrcpctstate = BluetoothProfile.STATE_DISCONNECTED;
    // cmdType for update music playing progress
    public static final byte CMD_UPDATE_PLAY_STATUS = (byte) 0x01;
    public static final byte CMD_UPDATE_PLAY_POSITION = (byte) 0x02;
    private int mDefaultMusicLong = 0; //00:00:00
    private int mDefaultPlayingTime = 0; //00:00:00
    private String musicTitle = null;
    private String musicArtist = null;
    private String musicAlbum = null;
    private int musicTotalLength = 0;
    private int musicPlayingTime = 0;
    private byte musicState = (byte) 0;

    private BtMusicStateListener stateListener;

    private static BtMusicStateManager instance;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BtUtils.MSG_SERVICE_ATTACH: {
                    regMetadataCallback();
                }
                break;

                case BtUtils.MSG_BT_STATE_CHANGED: {
                    handleBtStateChanged(msg.arg1);
                }
                break;

                case BtUtils.MSG_PROFILE_STATE_CHANGED: {
                    int profile_id = msg.arg1;
                    int profile_state = msg.arg2;
                    BluetoothDevice device = (BluetoothDevice) msg.obj;

                    if (11 == profile_id) {
                        handleA2dpSinkStateChanged(device, profile_state);
                    } else if (12 == profile_id) {
                        handleAvrcpCtStateChanged(device, profile_state);
                    }
                }
                break;

                case BtUtils.MSG_PLAY_STATE_CHANGED: {
                    Bundle b = msg.getData();
                    byte play_status = b.getByte("play_status");
                    int song_len = b.getInt("song_len");
                    int song_pos = b.getInt("song_pos");

                    updatePlaybackStatus(play_status, song_len, song_pos);
                }
                break;

                case BtUtils.MSG_META_DATA_CHANGED: {
                    Bundle b = msg.getData();
                    String title = b.getString("title");
                    String artist = b.getString("artist");
                    String album = b.getString("album");

                    updateMetadata(title, artist, album);
                }
                break;


            }
            super.handleMessage(msg);
        }
    };

    public static BtMusicStateManager getInstance() {
        if (instance == null) {
            synchronized (BtMusicStateManager.class) {
                if (instance == null) {
                    instance = new BtMusicStateManager();
                }
            }
        }
        return instance;
    }

    private BtMusicStateManager() {
        updatePlayPauseButton(musicState);

        updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, mDefaultMusicLong, mDefaultPlayingTime);
        updatePlayingStatus(mDefaultMusicLong, mDefaultPlayingTime, (byte) 0);

        //register msg notify
        BtUtils.addHandler(mHandler);
    }


    private void handleBtStateChanged(int btState) {
        switch (btState) {
            case BluetoothAdapter.STATE_ON:
                if (stateListener != null) {
                    stateListener.onBtStateOn();
                }
                Log.e("manager", " --------------------------------------------- 蓝牙状态on:" + btState);
                break;

            case BluetoothAdapter.STATE_OFF:
                //refresh ui
                Log.e("manager", " --------------------------------------------- 蓝牙状态off:" + btState);
                mA2dpsinkstate = BluetoothProfile.STATE_DISCONNECTED;
                mAvrcpctstate = BluetoothProfile.STATE_DISCONNECTED;
                if (stateListener != null) {
                    stateListener.onBtStateOff();
                }
                updatePlayPauseButton(AvrcpCtPlayerUtility.STOPPED);
                break;
        }
    }

    private void handleA2dpSinkStateChanged(BluetoothDevice device, int state) {
        int newState = state;
        //BluetoothDevice remoteDev = device;
        switch (newState) {
            case BluetoothProfile.STATE_DISCONNECTED:
                mA2dpsinkstate = BluetoothProfile.STATE_DISCONNECTED;
                if (stateListener != null) {
                    stateListener.onA2dpStateDisconnected();
                }
                updatePlayPauseButton(AvrcpCtPlayerUtility.STOPPED);
                break;

            case BluetoothProfile.STATE_CONNECTED:
                mA2dpsinkstate = BluetoothProfile.STATE_CONNECTED;
                if (stateListener != null) {
                    stateListener.onA2dpStateConnected();
                }
                break;
        }
    }

    private void handleAvrcpCtStateChanged(BluetoothDevice device, int state) {
        int newState = state;
        //BluetoothDevice remoteDev = device;
        switch (newState) {
            case BluetoothProfile.STATE_DISCONNECTED:
                mAvrcpctstate = BluetoothProfile.STATE_DISCONNECTED;
                if (stateListener != null) {
                    stateListener.onAvrcpStateDisconnected();
                }
                break;

            case BluetoothProfile.STATE_CONNECTED:
                mAvrcpctstate = BluetoothProfile.STATE_CONNECTED;
                if (stateListener != null) {
                    stateListener.onAvrcpStateConnected();
                }
                break;
        }
    }

    private void checkBtAvState(boolean isFirst) {
        A2dpSinkProfile a2dpProfile = (A2dpSinkProfile)
                BtUtils.getProfile(11);
        AvrcpControllerProfile avrcpProfile = (AvrcpControllerProfile)
                BtUtils.getProfile(12);

        //check a2dp
        if (a2dpProfile != null) {
            List<BluetoothDevice> deviceList = a2dpProfile.getConnectedDevices();
            if (deviceList == null || deviceList.size() == 0) {
                mA2dpsinkstate = BluetoothProfile.STATE_DISCONNECTED;
                ////a2dpsinkStateInfo.setText(R.string.a2dpsink_status_notconnected_info);
                if (stateListener != null) {
                    stateListener.onA2dpStateDisconnected();
                }

                if (!BtUtils.mDeviceLists.isEmpty()) {
                    a2dpProfile.connect(BtUtils.mDeviceLists.get(0));
                }
            } else if (isFirst) {
                ////a2dpsinkStateInfo.setText(R.string.a2dpsink_status_connected_info);
                mA2dpsinkstate = BluetoothProfile.STATE_CONNECTED;
                if (stateListener != null) {
                    stateListener.onA2dpStateConnected();
                }
            }
        }

        //check avrcp, when a2dp conneted, bluedroid will auto connect avrcp
        if (avrcpProfile != null) {
            List<BluetoothDevice> deviceList = avrcpProfile.getConnectedDevices();
            if (deviceList == null || deviceList.size() == 0) {
                ////avrcpctStateInfo.setText(R.string.avrcpct_status_notconnected_info);
                mAvrcpctstate = BluetoothProfile.STATE_DISCONNECTED;
                if (stateListener != null) {
                    stateListener.onAvrcpStateDisconnected();
                }

                if (mA2dpsinkstate == BluetoothProfile.STATE_CONNECTED) {
                    if (!BtUtils.mDeviceLists.isEmpty()) {
                        avrcpProfile.connect(BtUtils.mDeviceLists.get(0));
                    }
                }
            } else if (isFirst) {
                ////avrcpctStateInfo.setText(R.string.avrcpct_status_connected_info);
                mAvrcpctstate = BluetoothProfile.STATE_CONNECTED;
                if (stateListener != null) {
                    stateListener.onAvrcpStateConnected();
                }

                //check playback status
                byte play_status = avrcpProfile.getPlaybackStatus();
                if (play_status != musicState) {
                    updatePlayPauseButton(play_status);
                }
            }
        }

    }

    private void updatePlayPauseButton(byte playState) {
        switch (playState) {
            case AvrcpCtPlayerUtility.PLAYING:
                if (stateListener != null) {
                    stateListener.onStatePlaying();
                }
                break;

            case AvrcpCtPlayerUtility.PAUSED:
                if (stateListener != null) {
                    stateListener.onStatePause();
                }
                break;

            case AvrcpCtPlayerUtility.STOPPED:
                if (stateListener != null) {
                    stateListener.onStateStop();
                }
                break;
        }

        //update music state
        musicState = playState;

    }

    private void sendAvrcpCommand(int cmd) {
        AvrcpControllerProfile avrcpProfile = (AvrcpControllerProfile)
                BtUtils.getProfile(12);
        if (avrcpProfile == null) return;

        checkBtAvState(false);

        if (mAvrcpctstate != BluetoothProfile.STATE_CONNECTED) return;
        List<BluetoothDevice> deviceList = avrcpProfile.getConnectedDevices();
        if (deviceList.size() > 0) avrcpProfile.sendAvrcpCommand(deviceList.get(0), cmd);

    }

    void regMetadataCallback() {
        AvrcpControllerProfile avrcpProfile = (AvrcpControllerProfile)
                BtUtils.getProfile(12);

        if (avrcpProfile != null) {
            avrcpProfile.regMetaCallback(mMetadataCallback);
            avrcpProfile.setPlayerState(true);
        }
    }

    void unregMetadataCallback() {
        AvrcpControllerProfile avrcpProfile = (AvrcpControllerProfile)
                BtUtils.getProfile(12);

        if (avrcpProfile != null) {
            avrcpProfile.unregMetaCallback(mMetadataCallback);
            avrcpProfile.setPlayerState(false);
        }
    }

    AvrcpControllerProfile.MetadataCallback mMetadataCallback =
            new AvrcpControllerProfile.MetadataCallback() {

                @Override
                public void onMetadataChanged(String title, String artist, String album) {
                    Message msg = mHandler.obtainMessage(BtUtils.MSG_META_DATA_CHANGED);
                    Bundle b = new Bundle();
                    b.putString("title", title == null ? "" : title);
                    b.putString("artist", artist == null ? "" : artist);
                    b.putString("album", album == null ? "" : album);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onPlayStatusChanged(byte play_status, int song_len, int song_pos) {
                    Message msg = mHandler.obtainMessage(BtUtils.MSG_PLAY_STATE_CHANGED);
                    Bundle b = new Bundle();
                    b.putByte("play_status", play_status);
                    b.putInt("song_len", song_len);
                    b.putInt("song_pos", song_pos);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }

            };


    private void updateMetadata(String title, String artist, String album) {
        if (title == null) {
            musicTitle = null;
        } else if (!title.equals(musicTitle)) {
            musicTitle = title;
        }

        if (artist == null) {
            musicArtist = null;
        } else if (!artist.equals(musicArtist)) {
            musicArtist = artist;
        }

        if (album == null) {
            musicAlbum = null;
        } else if (!album.equals(musicAlbum)) {
            musicAlbum = album;
        }

        if (stateListener != null) {
            stateListener.onUpdateMetadata(musicTitle, musicArtist, musicAlbum);
        }
    }


    private void updatePlaybackStatus(byte play_status, int song_len, int song_pos) {
        if (D) Log.d(TAG, "updatePlaybackStatus playState=" + play_status +
                ",musicState=" + musicState);

        //check playback status
        if (play_status != musicState) {
            updatePlayPauseButton(play_status);
        }

        if (song_len == 0) {
            musicTotalLength = 0;
            updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, mDefaultMusicLong, mDefaultPlayingTime);
            updatePlayingStatus(mDefaultMusicLong, mDefaultPlayingTime, (byte) 0);
        } else if (song_len == musicTotalLength) {
            if (song_pos != musicPlayingTime) {
                musicPlayingTime = song_pos;
                updateMusicPlayingProgress(CMD_UPDATE_PLAY_POSITION, musicTotalLength, musicPlayingTime);
                updatePlayingProcess(musicPlayingTime);
            }

        } else {
            musicTotalLength = song_len;
            musicPlayingTime = song_pos;
        }

        if (stateListener != null) {
            stateListener.onUpdatePlaybackStatus(play_status, musicTotalLength, musicPlayingTime);
            updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, musicTotalLength, musicPlayingTime);
            updatePlayingStatus(musicTotalLength, musicPlayingTime, play_status);
        }
    }

    private void updatePlayingStatus(int song_length, int song_position, byte play_status) {

        if (stateListener == null) {
            return;
        }

        String length = null;
        String pos = null;

        if (song_length != 0xFFFFFFFF) {
            length = millSeconds2readableTime(song_length);
        } else {
            length = "00:00:00";
        }
        if (song_position != 0xFFFFFFFF) {
            pos = millSeconds2readableTime(song_position);
        } else {
            pos = "00:00:00";
        }
        stateListener.setMediaLengthInfo(length);
        stateListener.setMediaPlayingPositionInfo(pos);
        //mediaLengthInfo.setText(length);
        //mediaPlayingPositionInfo.setText(pos);
    }

    private void updatePlayingProcess(int pos) {

        if (stateListener == null) {
            return;
        }

        String position = null;
        if (pos != 0xFFFFFFFF) {
            position = millSeconds2readableTime(pos);
        } else {
            position = "00:00:00";
        }
        stateListener.setMediaPlayingPositionInfo(position);
        //mediaPlayingPositionInfo.setText(position);
    }

    private void updateMusicPlayingProgress(byte cmdType, int total_long, int playing_time) {

        if (stateListener == null) {
            return;
        }

        switch (cmdType) {
            case CMD_UPDATE_PLAY_STATUS: {
                if ((total_long == (int) 0xFFFFFFFF) || (playing_time == (int) 0xFFFFFFFF)) {
                    total_long = 0;
                    playing_time = 0;
                }
                stateListener.setMusicPlayingProgressBar_setMax(total_long);
                stateListener.setMusicPlayingProgressBar_setProgress(playing_time);
                //mMusicPlayingProgressBar.setMax(total_long);
                //mMusicPlayingProgressBar.setProgress(playing_time);
            }
            break;
            case CMD_UPDATE_PLAY_POSITION: {
                if (playing_time == (int) 0xFFFFFFFF)
                    playing_time = 0;
                stateListener.setMusicPlayingProgressBar_setProgress(playing_time);
                //mMusicPlayingProgressBar.setProgress(playing_time);
            }
            break;
            default:
                break;
        }
    }

    public String millSeconds2readableTime(int millseconds) {

        StringBuffer dateBf = new StringBuffer();
        int totalSeconds = millseconds / 1000;

        // HOUR_OF_DAY:24Hour
        int hour = (totalSeconds / 60) / 60;
        if (hour <= 9) {
            dateBf.append("0").append(hour + ":");
        } else {
            dateBf.append(hour + ":");
        }
        // Minute
        int minute = (totalSeconds / 60) % 60;
        if (minute <= 9) {
            dateBf.append("0").append(minute + ":");
        } else {
            dateBf.append(minute + ":");
        }
        // Seconds
        int second = totalSeconds % 60;
        if (second <= 9) {
            dateBf.append("0").append(second);
        } else {
            dateBf.append(second);
        }
        return dateBf.toString();

    }


    /*************************************** API ****************************************/
    public void setStateListener(BtMusicStateListener stateListener) {
        this.stateListener = stateListener;
    }


    /* 音乐控制 */

    /**
     * 播放
     */
    public void cmdMusicPlay() {
        sendAvrcpCommand(AvrcpCtPlayerUtility.CMD_PLAY);
    }

    /**
     * 暂停
     */
    public void cmdMusicPause() {
        sendAvrcpCommand(AvrcpCtPlayerUtility.CMD_PAUSE);
    }

    /**
     * 上一首
     */
    public void cmdMusicPrev() {
        sendAvrcpCommand(AvrcpCtPlayerUtility.CMD_PREV);
    }

    /**
     * 下一首
     */
    public void cmdMusicNext() {
        sendAvrcpCommand(AvrcpCtPlayerUtility.CMD_NEXT);
    }


//    public void cmdMusicForeward(){
//        sendAvrcpCommand(AvrcpCtPlayerUtility.KEY_FORWARD);
//    }
//
//
//    public void cmdMusicBackward(){
//        sendAvrcpCommand(AvrcpCtPlayerUtility.KEY_BACKWARD);
//    }


    public void resume() {
        if (D) Log.d(TAG, "+ onResume()+");

        //register metadata
        regMetadataCallback();

        checkBtAvState(true);
    }

    public void pause() {
        if (D) Log.d(TAG, "+ onPause()+");

        //unregister metadata
        unregMetadataCallback();
    }

    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
        BtUtils.removeHandler(mHandler);
    }

    public static final String ACTION_PAUSE_AUX_STATE = "com.mor.radio.stopPlay.aux.state";

    /**
     * radio停止
     */
    public void sendAuxStateBroadcast(Context mContext) {
        Intent intent = new Intent(ACTION_PAUSE_AUX_STATE);
        mContext.sendBroadcast(intent);
    }


}
