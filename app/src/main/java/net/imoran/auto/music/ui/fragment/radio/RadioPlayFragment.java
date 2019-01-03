package net.imoran.auto.music.ui.fragment.radio;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.presenter.impl.RadioMusicPresenter;
import net.imoran.auto.music.mvp.view.RadioMusicView;
import net.imoran.auto.music.network.bean.RadioAcrChildItemBean;
import net.imoran.auto.music.radio.core.RepeatMode;
import net.imoran.auto.music.radio.manager.IRadioPlayCallBack;
import net.imoran.auto.music.radio.manager.RadioBand;
import net.imoran.auto.music.radio.manager.RadioPlayMangerImp;
import net.imoran.auto.music.radio.model.RadioModel;
import net.imoran.auto.music.ui.MainActivity;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.utils.StringUtils;
import net.imoran.auto.music.utils.SysStatusBarUpdateUtils;
import net.imoran.auto.music.widgets.RadioStarView;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.flyaudio.sdk.manager.FlyRadioManager;


public class RadioPlayFragment extends BaseFragment<RadioMusicPresenter>
        implements RadioMusicView, View.OnClickListener, IRadioPlayCallBack {
    private static final String TAG = "RadioPlayFragment";
    private TextView tvFm, tvAm, tvTitle, tvContent;
    private TextView tvRadioValue;
    private ImageView ivRadioStar;
    private ImageView ivPreviousRadio;
    private ImageView ivRadioPlay;
    private ImageView ivNextRadio;
    private GridLayout glStar;
    private RadioStarView[] starViews = new RadioStarView[6];
    private List<RadioModel> currentList;
    private double curFrequency;
    private RadioBand curBand;
    private Timer timer;
    private String frequency;
    private String queryId;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                RadioAcrChildItemBean bean = (RadioAcrChildItemBean) msg.obj;
                tvTitle.setText(bean.getFmName());
                tvContent.setText(bean.getName());
                SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, bean.getFmName(), true);
            }
        }
    };

    public static RadioPlayFragment newInstance(String frequency, String queryId) {
        Bundle args = new Bundle();
        RadioPlayFragment fragment = new RadioPlayFragment();
        args.putString("frequency", frequency);
        args.putString("queryId", queryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isNliPage() {
        return false;
    }

    @Override
    protected String getPageType() {
        return "play";
    }

    @Override
    protected RadioMusicPresenter createPresenter() {
        return RadioMusicPresenter.newInstance();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_radio_play;
    }

    @Override
    protected void onViewCreated() {
        initView();
        initAmOrFm();
        startCheckTitle();
        frequency = getArguments().getString("frequency");
        queryId = getArguments().getString("queryId");
        if (frequency != null) {
            ivRadioPlay.setSelected(true);
            RadioPlayMangerImp.getInstance().setRadioChannel();
            listeningBroadCast(frequency, queryId);
            curFrequency = Double.valueOf(frequency);
        }
        presenter.getCollectedRadioList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
        tvFm = (TextView) rootView.findViewById(R.id.tvFm);
        tvAm = (TextView) rootView.findViewById(R.id.tvAm);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvContent = (TextView) rootView.findViewById(R.id.tvContent);
        tvRadioValue = (TextView) rootView.findViewById(R.id.tvRadioValue);
        ivRadioStar = (ImageView) rootView.findViewById(R.id.ivRadioStar);
        ivPreviousRadio = (ImageView) rootView.findViewById(R.id.ivPreviousRadio);
        ivRadioPlay = (ImageView) rootView.findViewById(R.id.ivRadioPlay);
        ivNextRadio = (ImageView) rootView.findViewById(R.id.ivNextRadio);
        glStar = (GridLayout) rootView.findViewById(R.id.glStar);

        tvFm.setOnClickListener(this);
        tvAm.setOnClickListener(this);
        ivRadioStar.setOnClickListener(this);
        ivPreviousRadio.setOnClickListener(this);
        ivRadioPlay.setOnClickListener(this);
        ivNextRadio.setOnClickListener(this);
        RadioPlayMangerImp.getInstance().setCallBack(this);

        for (int i = 0; i < glStar.getChildCount(); i++) {
            RadioStarView starView = (RadioStarView) glStar.getChildAt(i);
            starViews[i] = starView;
            starView.setText((i + 1) + "");
            starView.setSelected(false);
            starView.setTag(null);
            starView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioModel radioModel = (RadioModel) v.getTag();
                    if (radioModel == null) return;
                    if (radioModel.getBand() == RadioBand.AM) {
                        RadioPlayMangerImp.getInstance().chooseAM(true, radioModel.getFrequency());
                    } else {
                        RadioPlayMangerImp.getInstance().chooseFM(true, radioModel.getFrequency());
                    }
                    curBand = radioModel.getBand();
                    curFrequency = radioModel.getFrequency();
                    acrRadioSearch();
                }
            });
        }
    }

    private void initAmOrFm() {
        curBand = RadioPlayMangerImp.getInstance().getCurrentBand();
        if (curBand == RadioBand.AM) {
            tvAm.performClick();
        } else if (curBand == RadioBand.FM) {
            tvFm.performClick();
        }
        curFrequency = RadioPlayMangerImp.getInstance().getCurrentFrequency();
        tvRadioValue.setText(getBandString(curBand) + curFrequency + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFm:
                tvAm.setSelected(false);
                tvFm.setSelected(true);
                RadioPlayMangerImp.getInstance().chooseFM(false, 0);
                SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, "", true);
                tvTitle.setText("");
                tvContent.setText("");
                break;
            case R.id.tvAm:
                tvAm.setSelected(true);
                tvFm.setSelected(false);
                RadioPlayMangerImp.getInstance().chooseAM(false, 0);
                SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, "", true);
                tvTitle.setText("");
                tvContent.setText("");
                break;
            case R.id.ivRadioStar:
                if (curFrequency == 0D) {
                    ToastUtil.shortShow(activity, "无效的频率，无法收藏");
                    return;
                }
                ivRadioStar.setSelected(!ivRadioStar.isSelected());
                if (ivRadioStar.isSelected()) {
                    presenter.collectedRadio(true, getBandString(curBand).toUpperCase(), curFrequency + "", "");
                } else {
                    presenter.collectedRadio(false, getBandString(curBand).toUpperCase(), curFrequency + "", "");
                }
                break;
            case R.id.ivRadioPlay:
                ivRadioPlay.setSelected(!ivRadioPlay.isSelected());
                if (ivRadioPlay.isSelected()) {
                    RadioPlayMangerImp.getInstance().setRadioChannel();
                    acrRadioSearch();
                    SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 0);
                } else {
                    RadioPlayMangerImp.getInstance().setThreeChannel();
                    SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 1);
                }
                break;
            case R.id.ivNextRadio:
                RadioPlayMangerImp.getInstance().repeatMode(RepeatMode.REPEAT_INC_PLAY);
                tvTitle.setText("");
                tvContent.setText("");
                break;
            case R.id.ivPreviousRadio:
                RadioPlayMangerImp.getInstance().repeatMode(RepeatMode.REPEAT_DEC_PLAY);
                tvTitle.setText("");
                tvContent.setText("");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCheckTitle();
        RadioPlayMangerImp.getInstance().setThreeChannel();
        RadioPlayMangerImp.getInstance().stopScan();
    }

    @Override
    public void collectedRadioSuccess(boolean isAdd) {
        if (isAdd) {
            if (currentList != null && currentList.size() == 6) {
                RadioModel radioModel = currentList.get(0);
                presenter.collectedRadio(false, getBandString(radioModel.getBand()).toLowerCase(), getFrequency(radioModel.getFrequency() + ""), "");
            } else {
                presenter.getCollectedRadioList();
            }
        } else {
            presenter.getCollectedRadioList();
        }
    }

    @Override
    public void collectedRadioFail(String errorMsg) {
        ToastUtil.shortShow(activity, errorMsg);
    }

    @Override
    public void getCollectedRadioListSuccess(List<RadioModel> list) {
        this.currentList = list;
        if (list != null) {
            Log.e(TAG, "LISTSIZE" + list.size());
        }
        bindData(list);
    }

    private void bindData(List<RadioModel> list) {
        if (!ListUtils.isNotEmpty(list)) {
            RadioStarView starRadio = starViews[0];
            starRadio.setRadioLikeVisibility(View.VISIBLE);
            starRadio.setText(getFrequency(1 + ""));
            return;
        }
        int max = Math.min(6, list.size());
        for (int i = 0; i < max; i++) {
            RadioStarView starRadio = starViews[i];
            starRadio.setRadioLikeVisibility(View.GONE);
            starRadio.setText(getFrequency(list.get(i).getFrequency() + ""));
            starRadio.setTag(list.get(i));
        }
        ivRadioStar.setSelected(isFrequencySelected(curFrequency));
        for (int i = max; i < 6; i++) {
            RadioStarView starRadio = starViews[i];
            starRadio.setRadioLikeVisibility(View.VISIBLE);
            starRadio.setText(getFrequency(i + 1 + ""));
            starRadio.setTag(null);
        }
    }

    @Override
    public void getCollectedRadioListFail(String errorMsg) {
    }

    @Override
    public void onRadioAcrResult(RadioAcrChildItemBean bean) {
        if (bean != null) {
            tvTitle.setText(bean.getFmName());
            tvContent.setText(bean.getName());
            if (isSupportVisible())
                SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, bean.getFmName(), true);
        } else {
            tvTitle.setText("");
            tvContent.setText("");
        }
    }

    private void startCheckTitle() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isSupportVisible()) {
                    final RadioAcrChildItemBean bean = presenter.getCurrentItemBean(curFrequency + "");
                    if (bean == null) return;
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = bean;
                    handler.sendMessage(msg);
                }
            }
        };
        timer.schedule(task, 1000, 2000);
    }

    private void stopCheckTitle() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected void pausePlay() {
        RadioPlayMangerImp.getInstance().setThreeChannel();
        ivRadioPlay.setSelected(false);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 1);
    }

    protected void resumePlay() {
        RadioPlayMangerImp.getInstance().setRadioChannel();
        ivRadioPlay.setSelected(true);
        acrRadioSearch();
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 0);
        SysStatusBarUpdateUtils.updateSystemUiMusicTitle(activity, "电台", true);
    }

    @Override
    public void onCurrentFrequency(RadioBand band, double frequency) {
        this.curBand = band;
        this.curFrequency = frequency;
        boolean isAm = (band == RadioBand.AM);
        tvAm.setSelected(isAm);
        tvFm.setSelected(!isAm);
        tvRadioValue.setText(getBandString(band) + frequency);
        ivRadioStar.setSelected(isFrequencySelected(frequency));
    }

    @Override
    public void onReceiveChannel(RadioBand band, double frequency) {

    }

    @Override
    public void onScanStatus(int status) {
        if (status == FlyRadioManager.SCAN_STATUS_STOP) {
            this.curBand = RadioPlayMangerImp.getInstance().getCurrentBand();
            this.curFrequency = RadioPlayMangerImp.getInstance().getCurrentFrequency();
            acrRadioSearch();
        }
    }

    private void acrRadioSearch() {
        if (curFrequency < 0D) return;
        presenter.acrRadioSearch(curFrequency + "", getBandString(curBand).toLowerCase());
    }

    private String getBandString(RadioBand ban) {
        if (ban == RadioBand.AM) {
            return "AM";
        } else {
            return "FM";
        }
    }

    private boolean isFrequencySelected(double currentFrequency) {
        boolean isContain = false;
        if (currentList == null) return isContain;
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getFrequency() == currentFrequency) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    private String getFrequency(String frequency) {
        String tempFrequency = "";
        if (!(frequency != null && frequency.length() > 0)) return "";
        String[] temp = frequency.split("[.]");
        if (temp != null && temp.length > 1) {
            tempFrequency = temp[0] + "." + temp[1].substring(0, 1);
        } else if (temp != null && temp.length == 1) {
            tempFrequency = temp[0];
        } else {
            tempFrequency = "";
        }
        return tempFrequency;
    }

    /**********************************通用的VUI控制*********************************
     *
     */
    @Override
    public void vuiPlay() {
        RadioPlayMangerImp.getInstance().setRadioChannel();
        acrRadioSearch();
        ivRadioPlay.setSelected(true);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 0);
    }

    @Override
    public void vuiPause() {
        RadioPlayMangerImp.getInstance().setThreeChannel();
        ivRadioPlay.setSelected(false);
        SysStatusBarUpdateUtils.updateSystemUiMusicPlayState(activity, 1);
    }

    @Override
    public void vuiPrevious() {
        RadioPlayMangerImp.getInstance().repeatMode(RepeatMode.REPEAT_DEC_PLAY);
        tvTitle.setText("");
        tvContent.setText("");
    }

    @Override
    public void vuiNext() {
        RadioPlayMangerImp.getInstance().repeatMode(RepeatMode.REPEAT_INC_PLAY);
        tvTitle.setText("");
        tvContent.setText("");
    }

    @Override
    public void vuiPlayList(int total, List list) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.switchToNetFragment(total, list);
    }

    public void listeningBroadCast(String frequency, String queryId) {
        updateQueryId(getPageId(), queryId);
        if (StringUtils.isEmpty(frequency)) return;
        double mFrequency = Double.valueOf(frequency);
        curBand = RadioPlayMangerImp.getInstance().getCurrentBand();
        if (curBand == RadioBand.AM) {
            RadioPlayMangerImp.getInstance().chooseAM(true, mFrequency);
        } else {
            RadioPlayMangerImp.getInstance().chooseFM(true, mFrequency);
        }
    }
}
