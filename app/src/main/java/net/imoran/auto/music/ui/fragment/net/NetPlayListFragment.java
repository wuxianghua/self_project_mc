package net.imoran.auto.music.ui.fragment.net;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.NetListAdapter;
import net.imoran.auto.music.utils.ClickUtils;
import net.imoran.auto.music.utils.ListUtils;
import net.imoran.auto.music.vui.ContextSyncManager;

import java.util.List;

import static net.imoran.auto.music.vui.ContextSyncManager.PAGE_ID_NET_MUSIC_PLAY;

public class NetPlayListFragment extends NetBaseFragment implements View.OnClickListener {
    public static int TYPE_DEFAULT_LIST = 0;
    public static int TYPE_LIB = 1;
    public static int TYPE_SEARCH = 2;
    public static int TYPE_COLLECT = 3;
    private ImageView ivBack;
    private TextView tvNum;
    private LinearLayout llOtherType;
    private TextView tvMusicType;
    private TextView tvPlayAll;
    private TextView tvEmpty;
    private RecyclerView rvMusicList;
    private ProgressBar pbList;
    private RelativeLayout rlPageContainer;
    private TextView tvPreviousPage;
    private TextView tvCurrentPage;
    private TextView tvNextPage;
    private NetListAdapter listAdapter;
    private PageInfoBean currentPagInfo = new PageInfoBean();
    private List<SongModel> list;
    private int type = TYPE_DEFAULT_LIST;
    private boolean formPlay = true;
    private boolean isCollectPage = false;

    //当前播放列表
    public static NetPlayListFragment newInstance(int type, int position, PageInfoBean pageInfoBean) {
        Bundle args = new Bundle();
        args.putBoolean("formPlay", true);
        args.putInt("type", type);
        args.putInt("position", position);
        args.putSerializable("pageInfo", pageInfoBean);
        NetPlayListFragment fragment = new NetPlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //lib 音乐库页面请求数据
    public static NetPlayListFragment newInstance(int type, NetTypeBean typeBean) {
        Bundle args = new Bundle();
        args.putBoolean("formPlay", false);
        args.putInt("type", type);
        args.putSerializable("tag", typeBean);
        NetPlayListFragment fragment = new NetPlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageType() {
        return "list";
    }

    @Override
    protected String getPageId() {
        // 和 NetPlayFragment 共享pageid
        return PAGE_ID_NET_MUSIC_PLAY;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_net_play_list;
    }

    @Override
    protected void onViewCreated() {
        initView();
        initData();
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        tvNum = (TextView) rootView.findViewById(R.id.tvNum);
        llOtherType = (LinearLayout) rootView.findViewById(R.id.llOtherType);
        tvMusicType = (TextView) rootView.findViewById(R.id.tvMusicType);
        tvPlayAll = (TextView) rootView.findViewById(R.id.tvPlayAll);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        rvMusicList = (RecyclerView) rootView.findViewById(R.id.rvMusicList);
        pbList = (ProgressBar) rootView.findViewById(R.id.pbList);
        rlPageContainer = (RelativeLayout) rootView.findViewById(R.id.rlPageContainer);
        tvPreviousPage = (TextView) rootView.findViewById(R.id.tvPreviousPage);
        tvCurrentPage = (TextView) rootView.findViewById(R.id.tvCurrentPage);
        tvNextPage = (TextView) rootView.findViewById(R.id.tvNextPage);

        ivBack.setOnClickListener(this);
        tvPlayAll.setOnClickListener(this);
        tvPreviousPage.setOnClickListener(this);
        tvNextPage.setOnClickListener(this);

        rvMusicList.setLayoutManager(new LinearLayoutManager(activity));
        listAdapter = new NetListAdapter(activity);
        rvMusicList.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new NetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SongModel songModel) {
                mainFragment.getPlayFragment().setType(type);

                NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
                NetPlayFragment playFragment = mainFragment.getPlayFragment();
                playFragment.bindData(list, currentPagInfo, position);
                popTo(NetPlayFragment.class, false);
            }

            @Override
            public void onDeleteClick(int position, SongModel songModel) {
                presenter.collectSong(songModel, false);
            }
        });
    }

    private int oldPageNum = 1;
    private boolean requestOldPage = false;

    @Override
    public void collectSongSuccess(SongModel songModel, boolean isCollect) {
        //取消收藏成功
        oldPageNum = currentPagInfo.getCurrentPageNum();
        currentPagInfo.setCurrentPageNum(1);
        presenter.loadCollectedMusic();
        requestOldPage = true;

        mainFragment.getPlayFragment().cancelCollectSuccess(songModel);
    }

    public void onPlaySongChange(SongModel song, int position) {
        listAdapter.onItemPlay(song);
    }

    private void initData() {
        type = getArguments().getInt("type");
        formPlay = getArguments().getBoolean("formPlay");
        if (formPlay) {
            //得到当前播放列表的数据
            list = MusicPlayMangerImp.getInstance().getPlayList();
            for (SongModel songModel : list) {
                songModel.setPlay(false);
            }
            //设置正在播放的index
            if (getArguments().containsKey("position") && ListUtils.isNotEmpty(list)) {
                int position = getArguments().getInt("position");
                list.get(position).setPlay(true);
            }
            if (getArguments().containsKey("pageInfo")) {
                PageInfoBean pageInfo = (PageInfoBean) getArguments().getSerializable("pageInfo");
                this.currentPagInfo.setTotal(pageInfo.getTotal());
                this.currentPagInfo.setCurrentPageNum(pageInfo.getCurrentPageNum());
                setPageStatus();
            }
            listAdapter.setDataList(list);
            llOtherType.setVisibility(View.GONE);
            tvNum.setText("播放列表（" + currentPagInfo.getTotal() + "）");
        } else {
            if (getArguments().containsKey("tag")) {
                NetTypeBean typeBean = (NetTypeBean) getArguments().getSerializable("tag");
                llOtherType.setVisibility(View.VISIBLE);
                tvNum.setVisibility(View.GONE);
                if (typeBean.getType() == 1) {
                    //音乐标签
                    tvMusicType.setText(typeBean.getName() + "音乐");
                    presenter.loadNetMusicByType(typeBean.getName());
                } else if (typeBean.getType() == 2) {
                    //收藏的音乐
                    tvMusicType.setText("我的收藏");
                    presenter.loadCollectedMusic();
                    isCollectPage = true;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.ivBack:
                pop();
                break;
            case R.id.tvPlayAll:
                if (ListUtils.isNotEmpty(list)) {
                    mainFragment.getPlayFragment().setType(type);

                    NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
                    NetPlayFragment playFragment = mainFragment.getPlayFragment();
                    playFragment.bindData(list, currentPagInfo, 0);
                    popTo(NetPlayFragment.class, false);
                }
                break;
            case R.id.tvPreviousPage:
                loadMusicByPageNum(false);
                break;
            case R.id.tvNextPage:
                loadMusicByPageNum(true);
                break;
        }
    }

    private void loadMusicByPageNum(boolean isNext) {
        int currentPage = currentPagInfo.getCurrentPageNum();
        if (isNext) {
            if (currentPage >= currentPagInfo.getTotalPageNum()) {
                return;
            } else {
                int page = currentPage + 1;
                presenter.loadMusicByPageNum(page, getPageId());
            }
        } else {
            if (currentPage <= 1) {
                return;
            } else {
                int page = currentPage - 1;
                presenter.loadMusicByPageNum(page, getPageId());
            }
        }
    }

    @Override
    public void showLoading() {
        rvMusicList.setVisibility(View.INVISIBLE);
        pbList.setVisibility(View.VISIBLE);
        rlPageContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoading() {
        rvMusicList.setVisibility(View.VISIBLE);
        pbList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void netRequestQueryId(String queryId) {
        ContextSyncManager.getInstant().setCurrentListQueryId(queryId);
    }

    @Override
    public void loadMusicByTypeSuccess(int total, List<SongModel> list) {
        currentPagInfo.setTotal(total);
        this.list = list;
        bindData(list);
    }

    @Override
    public void loadCollectedMusicSuccess(int total, List<SongModel> list) {
        currentPagInfo.setTotal(total);
        this.list = list;
        bindData(list);
        if (ListUtils.isNotEmpty(list) && requestOldPage) {
            requestOldPage = false;
            if (oldPageNum <= currentPagInfo.getTotalPageNum()) {
                presenter.loadMusicByPageNum(oldPageNum, getPageId());
            } else {
                presenter.loadMusicByPageNum(currentPagInfo.getTotalPageNum(), getPageId());
            }
        }
    }

    @Override
    public void loadMusicByPageNumSuccess(int currentPage, List<SongModel> list, boolean isHeadPaly) {
        currentPagInfo.setCurrentPageNum(currentPage);
        this.list = list;
        bindData(list);
    }

    private void bindData(List<SongModel> list) {
        if (ListUtils.isNotEmpty(list)) {
            //设置正在播放的index
            if (getArguments().containsKey("position") && getArguments().containsKey("pageInfo")) {
                int position = getArguments().getInt("position");
                PageInfoBean pageInfo = (PageInfoBean) getArguments().getSerializable("pageInfo");
                if (currentPagInfo.getCurrentPageNum() == pageInfo.getCurrentPageNum()) {
                    list.get(position).setPlay(true);
                }
            }
            if (isCollectPage) {
                tvMusicType.setText("我的收藏（" + currentPagInfo.getTotal() + "）");
            }
            rvMusicList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            tvPlayAll.setVisibility(View.VISIBLE);
            listAdapter.setDataList(list);

            setPageStatus();
        } else {
            if (isCollectPage) {
                tvMusicType.setText("我的收藏");
            }
            rvMusicList.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvPlayAll.setVisibility(View.GONE);
        }
    }

    //设置页面控制按钮状态
    private void setPageStatus() {
        if (currentPagInfo.getTotalPageNum() > 1) {
            rlPageContainer.setVisibility(View.VISIBLE);
            tvCurrentPage.setText(currentPagInfo.getCurrentPageNum() + "/" + currentPagInfo.getTotalPageNum());
            if (currentPagInfo.getCurrentPageNum() == 1) {
                tvPreviousPage.setSelected(false);
                tvPreviousPage.setClickable(false);

                tvNextPage.setClickable(true);
                tvNextPage.setSelected(true);
            } else if (currentPagInfo.getCurrentPageNum() == currentPagInfo.getTotalPageNum()) {
                tvPreviousPage.setSelected(true);
                tvPreviousPage.setClickable(true);

                tvNextPage.setClickable(false);
                tvNextPage.setSelected(false);
            } else {
                tvPreviousPage.setSelected(true);
                tvPreviousPage.setClickable(true);

                tvNextPage.setClickable(true);
                tvNextPage.setSelected(true);
            }
        } else {
            rlPageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void vuiNextPage() {
        tvNextPage.performClick();
    }

    @Override
    public void vuiPreviousPage() {
        tvPreviousPage.performClick();
    }

    @Override
    public void vuiPlayIndex(int index) {
        if (formPlay) {
            mainFragment.getPlayFragment().vuiPlayIndex(index);
            mainFragment.getPlayFragment().setType(type);
            popTo(NetPlayFragment.class, false);
        } else {
            mainFragment.getPlayFragment().setType(type);
            NetMainFragment mainFragment = (NetMainFragment) getParentFragment();
            NetPlayFragment playFragment = mainFragment.getPlayFragment();
            playFragment.bindData(list, currentPagInfo, index);
            popTo(NetPlayFragment.class, false);
        }
    }
}
