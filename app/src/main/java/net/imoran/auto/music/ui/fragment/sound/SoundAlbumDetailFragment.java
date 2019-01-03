package net.imoran.auto.music.ui.fragment.sound;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.auto.music.ui.adapter.SoundProgramAdapter;
import net.imoran.auto.music.utils.GlideUtils;
import net.imoran.auto.music.widgets.GlideRoundTransform;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.AudioProgramBean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by xinhua.shi on 2018/6/19.
 */

public class SoundAlbumDetailFragment extends SoundBaseFragment {
    private static final String TAG = "SoundAlbumDetailFragmen";
    private ImageView ivBack;
    private ImageView ivProgramDetail;
    private TextView tvProgramName;
    private TextView tvProgramJianjie;
    private TextView tvProggramTime;
    private RecyclerView lvProgram;
    private TextView tvProgramNmu;
    private LinearLayout llProgramOrder;
    private ImageView ivProgramPositive;
    private ImageView ivProgramNegative;
    private TextView tvProgramOrder;
    private TextView lastAudio;
    private TextView nextAudio;
    private TextView currentPage;
    private int mCurrentPage = 1;
    private int mTotalPage;
    private int mLastPageItem;
    private RelativeLayout programDetailControl;
    private boolean isListPositiveOrder = true;

    @SuppressLint("HandlerLeak") Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            soundProgramAdapter.startAnimation();
        }
    };

    public static SoundAlbumDetailFragment newInstance(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        SoundAlbumDetailFragment fragment = new SoundAlbumDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "list";
    }

    @Override
    protected void onViewCreated() {
        initView();
        initData();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume",TAG);
    }

    private void initListener() {
        lastAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPage();
            }
        });
        nextAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });
    }

    private void nextPage() {
        if (mCurrentPage < mTotalPage) {
            int position = mCurrentPage * 4;
            //lvProgram.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) lvProgram.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            mCurrentPage++;
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
            handler.sendMessageDelayed(Message.obtain(), 100);
        }

        if (mCurrentPage < mTotalPage) {
            nextAudio.setSelected(true);
        }
        if (mCurrentPage == mTotalPage) {
            nextAudio.setSelected(false);
            setViewGroupLayoutParams();
        }
        if (mCurrentPage > 1) {
            lastAudio.setSelected(true);
        }
    }

    @Override
    public void vuiNextPage() {
        super.vuiNextPage();
        nextPage();
    }

    @Override
    public void vuiPreviousPage() {
        super.vuiPreviousPage();
        lastPage();
    }

    private void lastPage() {
        if (mCurrentPage > 1) {
            mCurrentPage--;
            int position = (mCurrentPage - 1) * 4;
            //lvProgram.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) lvProgram.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
            currentPage.setText(mCurrentPage + "/" + mTotalPage);
            handler.sendMessageDelayed(Message.obtain(), 100);
        }
        if (mCurrentPage >= 1) {
            setViewGroupLayoutParamsNormal();
            if (mCurrentPage == 1) {
                lastAudio.setSelected(false);
            } else {
                lastAudio.setSelected(true);
            }
            if (mCurrentPage < mTotalPage) {
                nextAudio.setSelected(true);
            }
        } else if (mCurrentPage > 0) {
            lastAudio.setSelected(false);
            nextAudio.setSelected(true);
        }
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.back);
        ivProgramDetail = (ImageView) rootView.findViewById(R.id.iv_program_detail);
        tvProgramName = (TextView) rootView.findViewById(R.id.tv_program_name);
        tvProgramJianjie = (TextView) rootView.findViewById(R.id.tv_program_jianjie);
        tvProggramTime = (TextView) rootView.findViewById(R.id.tv_program_time);
        lvProgram = (RecyclerView) rootView.findViewById(R.id.ls_program_detail);
        tvProgramNmu = (TextView) rootView.findViewById(R.id.tv_program_number);
        llProgramOrder = (LinearLayout) rootView.findViewById(R.id.ll_program_order);
        ivProgramPositive = (ImageView) rootView.findViewById(R.id.tv_program_positive);
        ivProgramNegative = (ImageView) rootView.findViewById(R.id.tv_program_negative);
        tvProgramOrder = (TextView) rootView.findViewById(R.id.tv_program_order);
        currentPage = (TextView) rootView.findViewById(R.id.currentaudioPage);
        programDetailControl = (RelativeLayout) rootView.findViewById(R.id.program_detail_control);
        lastAudio = (TextView) rootView.findViewById(R.id.lastaudio);
        nextAudio = (TextView) rootView.findViewById(R.id.nextaudio);

        llProgramOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isListPositiveOrder) {
                    isListPositiveOrder = false;
                    ivProgramPositive.setImageResource(R.drawable.ic_music_special_upwardarrow);
                    ivProgramNegative.setImageResource(R.drawable.ic_music_special_downwardarrow_pitchon);
                    tvProgramOrder.setText("反序");
                    soundProgramAdapter.setOrder(false);
                    soundProgramAdapter.setPlayPosition(playPosition);
                    Collections.reverse(mlvProgram);
                    soundProgramAdapter.notifyDataSetChanged();
                } else {
                    isListPositiveOrder = true;
                    ivProgramPositive.setImageResource(R.drawable.ic_music_special_upwardarrow_pitchon);
                    ivProgramNegative.setImageResource(R.drawable.ic_music_special_downwardarrow);
                    tvProgramOrder.setText("正序");
                    soundProgramAdapter.setPlayPosition(playPosition);
                    soundProgramAdapter.setOrder(true);
                    Collections.reverse(mlvProgram);
                    soundProgramAdapter.notifyDataSetChanged();
                }
                handler.sendMessageDelayed(Message.obtain(), 100);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });
    }

    List<AudioProgramBean.AudioProgramEntity> mlvProgram = new ArrayList<>();
    Serializable program;
    SoundProgramAdapter soundProgramAdapter;
    int playPosition;
    int playState;

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            program = arguments.getSerializable("program");
            mlvProgram.addAll((List<AudioProgramBean.AudioProgramEntity>) program);
            playPosition = (int) arguments.get("playPosition");
            playState = arguments.getInt("playState");
            if (mlvProgram != null && mlvProgram.size() != 0) {
                String formatTime = formatTime(mlvProgram.get(0).getDate());
                tvProggramTime.setText("更新时间: " + formatTime);
                tvProgramNmu.setText("节目（" + mlvProgram.size() + ")");
                tvProgramJianjie.setText(mlvProgram.get(0).getInfo());
                tvProgramName.setText(mlvProgram.get(0).getAlbum_title());
                GlideApp.with(activity).load(mlvProgram.get(0).getCover_url()).transform(new RoundedCorners(10)).into(ivProgramDetail);
                programDetailControl.setVisibility(View.VISIBLE);
                mTotalPage = (mlvProgram.size() % 4 == 0 ? mlvProgram.size() / 4 : mlvProgram.size() / 4 + 1);
                mLastPageItem = (mlvProgram.size() % 4 == 0 ? 4 : (mlvProgram.size() % 4));
                if (mTotalPage < 2) {
                    nextAudio.setSelected(false);
                }else {
                    nextAudio.setSelected(true);
                }
                currentPage.setText(1 + "/" + mTotalPage);
            } else {
                AudioAlbumBean.AudioAlbumEntity album = (AudioAlbumBean.AudioAlbumEntity) arguments.get("album");
                tvProggramTime.setText("更新时间" + album.getDate());
                tvProgramNmu.setText("节目（" + mlvProgram.size() + ")");
                tvProgramJianjie.setText(album.getInfo());
                tvProgramName.setText(album.getAlbum_title());
                programDetailControl.setVisibility(View.GONE);
                GlideApp.with(activity).load(album.getCover_url()).centerCrop().override(80, 80).thumbnail(0.4f).into(ivProgramDetail);
            }
            soundProgramAdapter = new SoundProgramAdapter(activity, mlvProgram);
            soundProgramAdapter.setOnItemClickListener(new SoundProgramAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AudioProgramBean.AudioProgramEntity contact, int i) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("audio_program", program);
                    bundle.putInt("position", i);
                    SoundPlayFragment fragment = findFragment(SoundPlayFragment.class);
                    fragment.bindData((ArrayList<AudioProgramBean.AudioProgramEntity>) program, i, null);
                    popTo(SoundPlayFragment.class, false);
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            soundProgramAdapter.setPlayPosition(playPosition);
            soundProgramAdapter.setPlayState(playState);
            handler.sendMessageDelayed(Message.obtain(), 100);
            lvProgram.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity,R.drawable.custom_divider));
            lvProgram.addItemDecoration(dividerItemDecoration);
            lvProgram.setAdapter(soundProgramAdapter);
            if (playPosition == -1) return;
            dealJumpPage(playPosition);
        }
    }

    private void dealJumpPage(int playPosition) {
        mCurrentPage = (playPosition + 1) % 4 == 0? (playPosition + 1)/4 : (playPosition + 1)/4 + 1;
        int position = (mCurrentPage - 1)* 4;
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) lvProgram.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(position, 0);
        currentPage.setText(mCurrentPage + "/" + mTotalPage);
        handler.sendMessageDelayed(Message.obtain(), 100);
        if (mCurrentPage > 1) {
            lastAudio.setSelected(true);
        }
        if (mCurrentPage != mTotalPage) {
            nextAudio.setSelected(true);
        }else {
            nextAudio.setSelected(false);
            setViewGroupLayoutParams();
        }
    }

    public String formatTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(time);
            format = new SimpleDateFormat("yyyy/MM/dd");
            return format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_program_detail;
    }

    public void setViewGroupLayoutParams() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = lvProgram.getLayoutParams();
        layoutParams.height = mLastPageItem * 114 * scale;
        lvProgram.setLayoutParams(layoutParams);
    }

    public void setViewGroupLayoutParamsNormal() {
        int scale = (int) activity.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams layoutParams = lvProgram.getLayoutParams();
        layoutParams.height = 456 * scale;
        lvProgram.setLayoutParams(layoutParams);
    }
}
