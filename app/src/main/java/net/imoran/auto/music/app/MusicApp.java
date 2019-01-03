package net.imoran.auto.music.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.github.moduth.blockcanary.BlockCanary;
import com.tencent.bugly.crashreport.CrashReport;

import net.imoran.auto.music.BuildConfig;
import net.imoran.auto.music.R;
import net.imoran.auto.music.bt.BtUtils;
import net.imoran.auto.music.network.manager.RetrofitManager;
import net.imoran.auto.music.radio.manager.RadioPlayMangerImp;
import net.imoran.auto.music.utils.AppUtils;
import net.imoran.auto.music.utils.RestartCrashHandler;
import net.imoran.auto.music.utils.SysStatusBarUpdateUtils;
import net.imoran.auto.scenebase.lib.SceneAPI;
import net.imoran.auto.thread.tool.MorThreadHelper;
import net.imoran.auto.thread.tool.core.LooperMonitor;
import net.imoran.mor.log.MorReporter;
import net.imoran.mor.log.local.MainThreadMsg;
import net.imoran.personal.lib.SPHelper;
import net.imoran.rripc.lib.ConnectionStateListener;
import net.imoran.rripc.lib.RRClient;
import net.imoran.rripc.lib.RRClientFactory;
import net.imoran.tv.common.lib.utils.LogUtils;

import java.io.InputStream;

import cn.flyaudio.sdk.FlySDKManager;
import cn.flyaudio.sdk.InitListener;
import cn.flyaudio.sdk.manager.FlyRadioManager;
import cn.flyaudio.sdk.manager.FlySystemManager;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

import static com.mediatek.widget.DefaultAccountPickerDialog.TAG;


public class MusicApp extends Application {
    public static MusicApp instance;
    private Handler mHandler = new Handler();
    private RRClient rrClient;
    private boolean isRClientCon = false;
    public static String MOR_KEY = "5B24CE667012B42F";
    public static String ACR_BASE_URL = "https://api.xiaomor.com/";

    public static MusicApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initFlySDk();
        initFragment();
        RetrofitManager.getInstance().init(this, ACR_BASE_URL);
        //崩溃后App重新启动
        RestartCrashHandler restartCrashHandler = RestartCrashHandler.getInstance();
        restartCrashHandler.init(this);
        BtUtils.getInstance(this);
//        CrashReport.initCrashReport(getApplicationContext(), "5d2a5e86bc", false);
        BlockCanary.install(this, new MusicBlockCanaryContext()).start();
        RadioPlayMangerImp.getInstance().setThreeChannel();
        initThreadMonitor();

        InputStream inputStream = getResources().openRawResource(R.raw.nli_scenes);
        SceneAPI.initAll(this, inputStream);
    }

    private void initFragment() {
        Fragmentation.builder().stackViewMode(Fragmentation.NONE)
                .debug(BuildConfig.DEBUG).handleException(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                LogUtils.e("MusicApp", e.getMessage());
            }
        }).install();
    }

    /**
     * @param mContext 同步客户端的处理
     */
    public void initVUiSync(final Context mContext) {
        Bundle extra = new Bundle();
        extra.putString("name", "mor_music");
        rrClient = RRClientFactory.createRRClient(mContext, "net.imoran.morservice", extra);
        rrClient.connect(new ConnectionStateListener() {
            @Override
            public void onConnected(String s) {
                isRClientCon = true;
            }

            @Override
            public void onDisconnect(String s) {
                isRClientCon = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initVUiSync(mContext);
                    }
                }, 3000);
            }
        });
    }

    public RRClient getRrClient() {
        return rrClient;
    }

    public boolean isRClientCon() {
        return isRClientCon;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BtUtils.close();
        SysStatusBarUpdateUtils.updateSystemUiMusicTitle(this, "", false);
    }

    private void initFlySDk() {
        FlySDKManager.getInstance().initialize(this, new InitListener() {
            @Override
            public void onError() {

            }

            @Override
            public void onSucceed() {
                FlySystemManager.getInstance().registerCallBackListener();
                FlyRadioManager.getInstance().registerCallBackListener();
            }
        });
    }

    // 初始化线程检测组件
    private void initThreadMonitor() {
        String userid = "";
        try {
            userid = SPHelper.GetUser(this).uid;
        } catch (NullPointerException e) {
            net.imoran.rripc.lib.utils.LogUtils.e(TAG, "getBaseParams: NullPointerException: " + e.getMessage());
        }
        // 初始化日志上报的对象
        final MorReporter morReporter = MorReporter.getDefaultInstance(getApplicationContext());
        morReporter.initDefaultConf(this, "5B24CE667012B42F", userid, userid);
        MorThreadHelper morThreadHelper = new MorThreadHelper(100
                , 60000);
        morThreadHelper.setBlockListener(new LooperMonitor.BlockListener() {
            @Override
            public void onBlockEvent(long l, String s, String s1) {
                // 主线程卡顿，上报日志
                String appId = "net.imoran.auto.music";
                String versionName = AppUtils.getVersionName(MusicApp.this);
                MainThreadMsg msg = new MainThreadMsg(appId);
                msg.setHandle_stack(s);
                msg.setHandle_time(l);
                msg.setHandle_stack_top(s1);
                msg.setVersion_name(versionName);
                morReporter.addMainThreadMsg(msg);

            }
        });
    }

}
