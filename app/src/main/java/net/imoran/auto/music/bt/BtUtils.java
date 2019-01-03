package net.imoran.auto.music.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.autochips.bluetooth.A2dpSinkProfile;
import com.autochips.bluetooth.AvrcpControllerProfile;
import com.autochips.bluetooth.BluetoothCallback;
import com.autochips.bluetooth.BluetoothEventManager;
import com.autochips.bluetooth.CachedBluetoothDevice;
import com.autochips.bluetooth.CachedBluetoothDeviceManager;
import com.autochips.bluetooth.HeadsetClientProfile;
import com.autochips.bluetooth.LocalBluetoothManager;
import com.autochips.bluetooth.LocalBluetoothProfile;
import com.autochips.bluetooth.LocalBluetoothProfileManager;

import java.util.ArrayList;
import java.util.List;

public class BtUtils {
    private static final String TAG = "BtUtils";
    private static boolean D = true;
    static Context mContext;
    public static BtUtils mInstance = null;
    //bt service message (0 ~ 9)
    public static final int MSG_SERVICE_ATTACH = 0;
    public static final int MSG_SERVICE_DETACH = 1;
    //bt gap message (10 ~ 29)
    public static final int MSG_BT_STATE_CHANGED = 10;
    public static final int MSG_SCAN_STATE_CHANGED = 11;
    public static final int MSG_DEVICE_ADD = 12;
    public static final int MSG_DEVICE_DELETE = 13;
    public static final int MSG_BOND_STATE_CHANGED = 14;
    public static final int MSG_CONNECT_STATE_CHANGED = 15;
    public static final int MSG_DEVICE_ATTR_CHANGED = 16;
    public static final int MSG_PROFILE_STATE_CHANGED = 17;
    public static final int MSG_DEVICE_ACL_DISCONNECTED = 18;
    //bt music message (30 ~ 59)
    public static final int MSG_PLAY_STATE_CHANGED = 30;
    public static final int MSG_META_DATA_CHANGED = 31;
    //bt phone call message (100 ~ 120)
    public static final int MSG_AUTO_ANSWER = 100;
    public static final int MSG_CALL_STATE_CHANGED = 101;
    public static final int MSG_AUDIO_STATE_CHANGED = 102;
    //bt Utils message (120~140)
    public static final int MSG_AUTO_CONNECT = 120;
    public static final int MSG_RECONNECT_HFP = 121;
    public static final int MSG_ATCMD_NO_RESPONSE = 122;
    public static final int MSG_RECONNECT_DEVICE = 123;
    //auto connect try delay.
    public static final int AUTO_CONNECT_TRY_DELAY = 10000;
    public static final int AUTO_CONNECT_TRY_COUNT = 3;
    public static final int RECONNECT_HFP_DELAY = 2000;
    public static final int RECONNECT_DEVICE_DELAY = 2000;

    private static List<Handler> mHandlerLists = new ArrayList<Handler>();
    public static List<BluetoothDevice> mDeviceLists = new ArrayList<BluetoothDevice>();
    public static LocalBluetoothManager mLocalBtManager;
    public static BluetoothEventManager mEventManager;
    public static CachedBluetoothDeviceManager mDeviceManager;
    public static LocalBluetoothProfileManager mLocalProfileManager;
    private static A2dpSinkProfile mA2dpSinkProfile;
    private static AvrcpControllerProfile mAvrcpCtProfile;

    private static Toast mToast;
    private static int mAutoConnectCount = 0;

    public static BtUtils getInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new BtUtils();
        }
        return (mInstance);
    }

    BtUtils() {
        if (D) Log.d(TAG, "BtUtils ");
        initLocalBluetooth(mContext);
        if (mEventManager != null) {
            mEventManager.registerCallback(mbtCallback);
        }
    }

    public static void close() {
        if (mEventManager != null) {
            mEventManager.unregisterCallback(mbtCallback);
        }
//        if (mPBManager != null) {
//            mPBManager.close();
//            mPBManager = null;
//        }
        //deinitLocalBluetooth();
        mContext = null;
        mInstance = null;
    }

    public static LocalBluetoothProfile getProfile(int profile) {
        switch (profile) {
//            case 16:
//                if (mHeadsetClientProfile != null &&
//                        mHeadsetClientProfile.isProfileReady()) {
//                    return mHeadsetClientProfile;
//                }
//                break;

            case 11:
                if (mA2dpSinkProfile != null &&
                        mA2dpSinkProfile.isProfileReady()) {
                    return mA2dpSinkProfile;
                }
                break;

            case 12:
                if (mAvrcpCtProfile != null &&
                        mAvrcpCtProfile.isProfileReady()) {
                    return mAvrcpCtProfile;
                }
                break;
        }

        return null;
    }

//    public static BluetoothPBManager getPBManager() {
//        return mPBManager;
//    }

    public static void initLocalBluetooth(Context context) {
        synchronized (mContext) {
            if (mLocalBtManager != null) return;

            mLocalBtManager = LocalBluetoothManager.getInstance(context);
            if (mLocalBtManager != null) {
                mEventManager = mLocalBtManager.getEventManager();
                mDeviceManager = mLocalBtManager.getCachedDeviceManager();
                mLocalProfileManager = mLocalBtManager.getProfileManager();
            }

            if (mLocalProfileManager != null) {
                mLocalProfileManager.addServiceListener(mServiceListener);
                mLocalProfileManager.addProfileCallback(mProfileCallback);
            }
        }
    }

    public static void deinitLocalBluetooth() {
        synchronized (mContext) {
            if (mLocalProfileManager != null) {
                mLocalProfileManager.removeServiceListener(mServiceListener);
                mLocalProfileManager.removeProfileCallback(mProfileCallback);
            }

            mLocalBtManager = null;
            mEventManager = null;
            mDeviceManager = null;
            mLocalProfileManager = null;
            mA2dpSinkProfile = null;
            //mHeadsetClientProfile = null;
        }
    }

    public static synchronized boolean isBluetoothReady() {
        return (mLocalBtManager != null);
    }

    public static void handleBtStateChanged(int btState) {
        switch (btState) {
            case BluetoothAdapter.STATE_ON:
                if (!isBluetoothReady()) {
                    initLocalBluetooth(mContext);
                }
                break;

            case BluetoothAdapter.STATE_OFF:
                if (isBluetoothReady()) {
                    //deinitLocalBluetooth();
                }
                break;

            default:
                break;
        }
    }

    public static void handleBtConnectStateChanged(BluetoothDevice device, int state) {
        switch (state) {
            case BluetoothProfile.STATE_CONNECTED:
//                synchronized (mDeviceLists) {
//                    for (int i = 0; i < mDeviceLists.size(); i++) {
//                        BluetoothDevice btdevice = mDeviceLists.get(i);
//                        if (!btdevice.isConnected())
//                            mDeviceLists.remove(btdevice);
//                    }
//
//                    if (!mDeviceLists.contains(device)) {
//                        mDeviceLists.add(device);
//                    }
//                }
//                writeLastConnectedDeviceData(device.getAddress());
//                stopAutoConnect();
                break;

            case BluetoothProfile.STATE_DISCONNECTED:
//                synchronized (mDeviceLists) {
//                    if (mDeviceLists.contains(device)) {
//                        mDeviceLists.remove(device);
//                    }
//                }
                break;

            default:
                break;
        }
    }

    public static void handleBondStateChanged(BluetoothDevice device, int state) {
        switch (state) {
            case BluetoothDevice.BOND_NONE:
//                if (readLastConnectedDeviceData().equals(device.getAddress())) {
//                    //clear last connect device when bond none.
//                    //writeLastConnectedDeviceData("");
//                }
                break;

            default:
                break;
        }
    }

    public static void handleDeviceSelected(BluetoothDevice device, boolean isSelected) {
        if (isSelected) {
            synchronized (mDeviceLists) {
                if (!mDeviceLists.contains(device)) {
                    mDeviceLists.add(device);
                }
            }
        } else {
            synchronized (mDeviceLists) {
                if (mDeviceLists.contains(device)) {
                    mDeviceLists.remove(device);
                }
            }
        }
    }

    public static synchronized void addHandler(Handler handler) {
        if (D) Log.d(TAG, "addHandler ");
        if (!mHandlerLists.contains(handler)) {
            mHandlerLists.add(handler);
        }
    }

    public static synchronized void removeHandler(Handler handler) {
        if (D) Log.d(TAG, "removeHandler ");
        if (mHandlerLists.contains(handler)) {
            mHandlerLists.remove(handler);
        }
    }

    private static final LocalBluetoothProfileManager.ServiceListener mServiceListener = new LocalBluetoothProfileManager.ServiceListener() {
        @Override
        public void onServiceConnected() {
            if (D) Log.d(TAG, "onServiceConnected ");
            synchronized (mContext) {
                //mLocalProfileManager.getHeadsetClientProfile();
                mA2dpSinkProfile = mLocalProfileManager.getA2dpSinkProfile();
                mAvrcpCtProfile = mLocalProfileManager.getAvrcpCtProfile();
                //mPBManager = BluetoothPBManager.getInstance(mContext);

                //if app contain bt player, call regPlayerUtility or  else discard
                if (mAvrcpCtProfile != null) mAvrcpCtProfile.regPlayerUtility();

//                if (mHeadsetClientProfile != null) {
//                    mHeadsetClientProfile.registerHeadsetClientCallCallback(mHeadsetClientCallCallback);
//                }
            }

            dispatchMessage(MSG_SERVICE_ATTACH, null, 0, 0);

            //checkAutoConnectSetting();
        }

        @Override
        public void onServiceDisconnected() {
            if (D) Log.d(TAG, "onServiceDisconnected ");
            synchronized (mContext) {
                //mHeadsetClientProfile = null;
                mA2dpSinkProfile = null;
                mAvrcpCtProfile = null;
            }

            dispatchMessage(MSG_SERVICE_DETACH, null, 0, 0);

            //stopAutoConnect();
        }
    };

    private static LocalBluetoothProfileManager.ProfileCallback mProfileCallback = new LocalBluetoothProfileManager.ProfileCallback() {
        @Override
        public void onProfileStateChanged(BluetoothDevice device, int profile, int state) {
            dispatchMessage(MSG_PROFILE_STATE_CHANGED, device, profile, state);
        }
    };

    private static HeadsetClientProfile.HeadsetClientCallCallback mHeadsetClientCallCallback = new HeadsetClientProfile.HeadsetClientCallCallback() {
        @Override
        public void onCallStateChanged(Intent intent) {
            if (D) Log.d(TAG, "onCallStateChanged");
//            synchronized (mContext) {
//                BluetoothReceiver.onActionCallStateChanged(mContext, intent);
//            }
//            dispatchMessage(MSG_CALL_STATE_CHANGED, intent, 0, 0);
        }

        @Override
        public void onAudioStatusChanged(Intent intent) {
            if (D) Log.d(TAG, "onAudioStatusChanged");
            dispatchMessage(MSG_AUDIO_STATE_CHANGED, intent, 0, 0);
        }
    };

    public static void dispatchMessage(int what, Object arg, int arg1, int arg2) {
        if (D) Log.d(TAG, "dispatchMessage(" + what + ")");
        for (Handler handler : mHandlerLists) {
            if (handler != null) {
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = arg;
                msg.arg1 = arg1;
                msg.arg2 = arg2;
                handler.sendMessage(msg);
            }
        }
    }

    static BluetoothCallback mbtCallback = new BluetoothCallback() {
        public void onBluetoothStateChanged(int bluetoothState) {
            dispatchMessage(MSG_BT_STATE_CHANGED, null, bluetoothState, 0);

            handleBtStateChanged(bluetoothState);
        }

        public void onScanningStateChanged(boolean started) {
            dispatchMessage(MSG_SCAN_STATE_CHANGED, null, started ? 0 : 1, 0);

        }

        public void onDeviceAdded(CachedBluetoothDevice cachedDevice) {
            dispatchMessage(MSG_DEVICE_ADD, cachedDevice, 0, 0);
        }

        public void onDeviceDeleted(CachedBluetoothDevice cachedDevice) {
            dispatchMessage(MSG_DEVICE_DELETE, cachedDevice, 0, 0);
        }

        public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {

            dispatchMessage(MSG_BOND_STATE_CHANGED, cachedDevice, bondState, 0);

            if (cachedDevice != null) {
                handleBondStateChanged(cachedDevice.getDevice(), bondState);
            }
        }

        public void onConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) {
            dispatchMessage(MSG_CONNECT_STATE_CHANGED, cachedDevice, state, 0);

            if (cachedDevice != null) {
                handleBtConnectStateChanged(cachedDevice.getDevice(), state);
            }
        }

    };

    public static void showToast(int resid) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(resid);
        mToast.show();
    }

    public static void writeDevicePin(String pin) {
        if (mContext != null) {
            SharedPreferences.Editor sharedata
                    = mContext.getSharedPreferences("device_pin_data", Context.MODE_PRIVATE).edit();
            sharedata.putString("PIN", pin);
            SharedPreferencesCommitor commitor = new SharedPreferencesCommitor(sharedata);
            new Thread(commitor).start();
        }
    }

    public static String readDevicePin() {
        String pinCode = "";
        if (mContext != null) {
            SharedPreferences sharedata = mContext.getSharedPreferences("device_pin_data", Context.MODE_PRIVATE);
            pinCode = sharedata.getString("PIN", "0000");
        }
        return pinCode;
    }

    public static void writeAutoAnswerData(boolean isAutoAnswer) {
        if (mContext != null) {
            SharedPreferences.Editor sharedata
                    = mContext.getSharedPreferences("bt.setting.autoanswer", Context.MODE_PRIVATE).edit();
            sharedata.putBoolean("IS_BT_AUTO_ANSWER", isAutoAnswer);
            SharedPreferencesCommitor commitor = new SharedPreferencesCommitor(sharedata);
            new Thread(commitor).start();
        }
    }

    public static boolean readAutoAnswerData() {
        boolean isAutoAnswer = false;
        if (mContext != null) {
            SharedPreferences sharedata
                    = mContext.getSharedPreferences("bt.setting.autoanswer", Context.MODE_PRIVATE);
            isAutoAnswer = sharedata.getBoolean("IS_BT_AUTO_ANSWER", false);
        }
        return isAutoAnswer;
    }

    public static void writeAutoConnectData(boolean isAutoAnswer) {
        if (mContext != null) {
            SharedPreferences.Editor sharedata
                    = mContext.getSharedPreferences("bt.setting.autoconnect", Context.MODE_PRIVATE).edit();
            sharedata.putBoolean("IS_BT_AUTO_CONNECT", isAutoAnswer);
            SharedPreferencesCommitor commitor = new SharedPreferencesCommitor(sharedata);
            new Thread(commitor).start();
        }
    }

    public static boolean readAutoConnectData() {
        boolean isAutoAnswer = false;
        if (mContext != null) {
            SharedPreferences sharedata
                    = mContext.getSharedPreferences("bt.setting.autoconnect", Context.MODE_PRIVATE);
            isAutoAnswer = sharedata.getBoolean("IS_BT_AUTO_CONNECT", false);
        }
        return isAutoAnswer;
    }

    public static void writeLastConnectedDeviceData(String BT_ADDR) {
        if (D) Log.d(TAG, "writeLastConnectedDeviceData: " + BT_ADDR);
        if (mContext != null) {
            SharedPreferences.Editor sharedata
                    = mContext.getSharedPreferences("preConnectedDevice_data", Context.MODE_PRIVATE).edit();
            sharedata.putString("BTADDR", BT_ADDR);
            SharedPreferencesCommitor commitor = new SharedPreferencesCommitor(sharedata);
            new Thread(commitor).start();
        }
    }

    public static String readLastConnectedDeviceData() {
        String addr = "";
        if (mContext != null) {
            SharedPreferences sharedata
                    = mContext.getSharedPreferences("preConnectedDevice_data", Context.MODE_PRIVATE);
            addr = sharedata.getString("BTADDR", "");
        }
        if (D) Log.d(TAG, "readLastConnectedDeviceData: " + addr);
        return addr;
    }

    public static class SharedPreferencesCommitor implements Runnable {
        private SharedPreferences.Editor mSharedata;

        public SharedPreferencesCommitor(SharedPreferences.Editor sharedata) {
            this.mSharedata = sharedata;
        }

        public void run() {
            if (mSharedata != null) {
                mSharedata.commit();
            }
        }
    }

//    private static Handler mMsgHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch(msg.what){
//                case MSG_AUTO_CONNECT:
//                    synchronized (mContext) {
//                        if (mLocalBtManager != null) {
//                            LocalBluetoothAdapter localAdapter = mLocalBtManager.getBluetoothAdapter();
//                            String addr = (String)msg.obj;
//                            BluetoothDevice device = localAdapter.getRemoteDevice(addr);
//                            CachedBluetoothDevice cachedDevice = mDeviceManager.findDevice(device);
//                            if (cachedDevice != null && !cachedDevice.isConnected()) {
//                                cachedDevice.connect();
//                            }
//                        }else {
//                            this.removeMessages(MSG_AUTO_CONNECT);
//                            mAutoConnectCount = 0;
//                            break;
//                        }
//                    }
//                    mAutoConnectCount++;
//                    if (mAutoConnectCount <= AUTO_CONNECT_TRY_COUNT) {
//                        Message msgDelay = Message.obtain();
//                        msgDelay.what = MSG_AUTO_CONNECT;
//                        msgDelay.obj = msg.obj;
//                        this.sendMessageDelayed(msgDelay, AUTO_CONNECT_TRY_DELAY);
//                    }
//                    break;
//
//                case MSG_RECONNECT_HFP:{
//                    if(D) Log.d(TAG,"reconnect hfp.");
//                    LocalBluetoothProfile hfProfile = getProfile(16);
//                    if (mDeviceManager != null && hfProfile != null) {
//                        BluetoothDevice device = (BluetoothDevice)msg.obj;
//                        CachedBluetoothDevice cachedDevice = mDeviceManager.findDevice(device);
//                        if (cachedDevice != null && !cachedDevice.isConnectedProfile(hfProfile)) {
//                            cachedDevice.connectProfile(hfProfile);
//                        }
//                    }
//                }
//                break;
//
//                case MSG_RECONNECT_DEVICE:{
//                    if(D) Log.d(TAG,"reconnect device.");
//                    if (mDeviceManager == null) {
//                        if(D) Log.d(TAG,"mDeviceManager is null");
//                        break;
//                    }
//                    BluetoothDevice device = (BluetoothDevice)msg.obj;
//                    CachedBluetoothDevice cachedDevice = mDeviceManager.findDevice(device);
//                    if (cachedDevice == null) {
//                        if(D) Log.d(TAG,"cachedDevice is null");
//                        break;
//                    }
//                    HeadsetClientProfile hfProfile = (HeadsetClientProfile)getProfile(16);
//                    A2dpSinkProfile a2dpProfile = (A2dpSinkProfile)getProfile(11);
//                    List<BluetoothDevice> devices = null;
//                    if (hfProfile != null) {
//                        //if hfp is not connect, connect hfp.
//                        devices = hfProfile.getConnectedDevices();
//                        if (devices == null || devices.size() == 0) {
//                            cachedDevice.connectProfile(hfProfile);
//                        }
//                    }
//
//                    if (a2dpProfile != null) {
//                        //if a2dp is not connect, connect a2dp.
//                        devices = a2dpProfile.getConnectedDevices();
//                        if (devices == null || devices.size() == 0) {
//                            cachedDevice.connectProfile(a2dpProfile);
//                        }
//                    }
//                }
//                break;
//
//                default:
//                    break;
//            }
//        }
//    };

//    public static void checkAutoConnectSetting () {
//        if(D) Log.d(TAG,"checkAutoConnectSetting.");
//        if (readAutoConnectData() == true && readLastConnectedDeviceData() != "") {
//            Message msg = Message.obtain();
//            msg.what = MSG_AUTO_CONNECT;
//            msg.obj = readLastConnectedDeviceData();
//            mMsgHandler.sendMessage(msg);
//        }
//        return;
//    }
//
//    public static void stopAutoConnect() {
//        if (mMsgHandler.hasMessages(MSG_AUTO_CONNECT)) {
//            mMsgHandler.removeMessages(MSG_AUTO_CONNECT);
//        }
//        mAutoConnectCount = 0;
//    }

    /* if AT cmd no response after 29.989 seconds, bluedroid will disconnect hfp.
     * here we will auto reconnect hfp.
     */
//    public static void handleATCmdNoResponse (BluetoothDevice device) {
//        if(D) Log.d(TAG,"handleATCmdNoResponse." + device);
//        Message msg = Message.obtain();
//        msg.what = MSG_RECONNECT_HFP;
//        msg.obj = device;
//        mMsgHandler.sendMessageDelayed(msg, RECONNECT_HFP_DELAY);
//
//        dispatchMessage(MSG_ATCMD_NO_RESPONSE, device, 0, 0);
//    }
//
//    public static void handleACLDisconnect(BluetoothDevice device, int reason) {
//        if(D) Log.d(TAG,"handleACLDisconnect." + device + ", reason:" + reason);
//
//        if (reason == LocalBluetoothProfile.HCI_ERR_CONNECTION_TIMEOUT ||
//                reason == LocalBluetoothProfile.HCI_ERR_LMP_RESPONSE_TIMEOUT) {
//            Message msg = Message.obtain();
//            msg.what = MSG_RECONNECT_DEVICE;
//            msg.obj = device;
//            mMsgHandler.sendMessageDelayed(msg, RECONNECT_DEVICE_DELAY);
//        }
//
//        dispatchMessage(BtUtils.MSG_DEVICE_ACL_DISCONNECTED, device, reason, 0);
//    }

}
