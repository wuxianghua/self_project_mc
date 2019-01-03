package net.imoran.auto.music.ui.fragment.bluetooth;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import net.imoran.auto.music.bean.SongsBean;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bt.BtMusicStateManager;
import net.imoran.auto.music.ui.adapter.MusicPlayViewAdapter;
import net.imoran.auto.music.widgets.progress.CircleProgress;
import net.imoran.auto.music.widgets.viewPager.ScaleInTransformer;
import net.imoran.tv.common.lib.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicBTPlayManager {
    private ViewPager viewPager;
    private MusicPlayViewAdapter pagerAdapter;
    private int lastIndex = 0;
    private CircleProgress circleProgress;
    private ImageView ivPlayState;
    private boolean connectStatus = false;
    public Context mContext;
    public View mRootView;

    public MusicBTPlayManager(Context mContext, View mRootView) {
        this.mContext = mContext;
        this.mRootView = mRootView;
        initView();
        initData();
    }

    public void initView() {
        viewPager = (ViewPager) mRootView.findViewById(R.id.vp_page);
    }

    public void setConnectStatus(boolean connectStatus) {
        this.connectStatus = connectStatus;
    }

    public void initData() {
        final List<View> viewList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_pager_item, null);
            ImageView musicCover = (ImageView) itemView.findViewById(R.id.ivMusicCover);
            final ImageView ivPlayState = (ImageView) itemView.findViewById(R.id.ivPlayState);
            ivPlayState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isConnectedBt()) return;
                    setPlayState(!ivPlayState.isSelected());
                    if (ivPlayState.isSelected()) {
                        BtMusicStateManager.getInstance().cmdMusicPlay();
                    } else {
                        BtMusicStateManager.getInstance().cmdMusicPause();
                    }
                }
            });
            viewList.add(itemView);
        }
        int firstPage = viewList.size() / 2;
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new MusicPlayViewAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(80);

        viewPager.setCurrentItem(firstPage, true);
        viewPager.setPageTransformer(true, new ScaleInTransformer(0.8F));
        setStatus(viewList.get(firstPage), true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= viewList.size()) return;
                View selectView = viewList.get(position);
                setStatus(viewList.get(lastIndex), false);
                setStatus(viewList.get(position), true);
                SongsBean selectBean = (SongsBean) selectView.getTag();
                if (position < lastIndex) {
                    if (!isConnectedBt()) return;
                    BtMusicStateManager.getInstance().cmdMusicPrev();
                } else {
                    if (!isConnectedBt()) return;
                    BtMusicStateManager.getInstance().cmdMusicNext();
                }
                lastIndex = position;
                setPlayState(true);
                //实现无线循环
                if (position == 0) {
                    viewPager.setCurrentItem(viewList.size() - 2, true);
                } else if (position == (viewList.size() - 1)) {
                    viewPager.setCurrentItem(1, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setSeekBarProgress(float progress) {
        if (circleProgress == null) return;
        if (progress <= 1.0f) {
            circleProgress.setValue(1);
        } else if (progress >= 99) {
            circleProgress.setValue(100 * 100);
        } else {
            circleProgress.setValue(progress * 100);
        }
    }

    public void setPlayState(boolean select) {
        ivPlayState.setSelected(select);
        ivPlayState.setImageResource(getPlayStateIcon());
    }

    private int getPlayStateIcon() {
        return ivPlayState.isSelected() ? R.drawable.ic_music_musicplay_stop : R.drawable.ic_music_musicplay_played;
    }

    private void setStatus(View itemView, boolean isSelect) {
        ivPlayState = (ImageView) itemView.findViewById(R.id.ivPlayState);
        CircleProgress progress = (CircleProgress) itemView.findViewById(R.id.progressView);
        if (isSelect) {
            progress.setVisibility(View.VISIBLE);
            ivPlayState.setVisibility(View.VISIBLE);
            circleProgress = progress;
        } else {
            progress.setVisibility(View.INVISIBLE);
            ivPlayState.setVisibility(View.INVISIBLE);
            progress.setValue(0f);
        }
    }

    private boolean isConnectedBt() {
        if (!connectStatus) {//判断是否连接了蓝牙
            ToastUtil.shortShow(mContext, "请连接蓝牙");
        }
        return connectStatus;
    }

}
