package net.imoran.auto.music.ui.fragment.local;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.bean.PageInfoBean;
import net.imoran.auto.music.player.manager.MusicPlayMangerImp;
import net.imoran.auto.music.player.model.SongModel;
import net.imoran.auto.music.ui.adapter.BaseRecycleAdapter;
import net.imoran.auto.music.ui.adapter.NetListAdapter;
import net.imoran.auto.music.utils.ClickUtils;

import net.imoran.auto.music.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class LocalPlayListFragment extends LocalBaseFragment implements View.OnClickListener {
    private ImageView ivBack;
    private TextView tvNum;
    private TextView tvEmpty;
    private RecyclerView rvMusicList;
    private ProgressBar pbList;
    private RelativeLayout rlPageContainer;
    private TextView tvPreviousPage;
    private TextView tvCurrentPage;
    private TextView tvNextPage;
    private NetListAdapter listAdapter;
    private PageInfoBean currentPagInfo = new PageInfoBean();
    private List<SongModel> list = new ArrayList<>();

    public static LocalPlayListFragment newInstance(int position, PageInfoBean pageInfoBean) {
        Bundle args = new Bundle();
        LocalPlayListFragment fragment = new LocalPlayListFragment();
        args.putSerializable("pageInfo", pageInfoBean);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_local_play_list;
    }

    @Override
    protected String getPageType() {
        return "list";
    }

    @Override
    protected void onViewCreated() {
        initView();
        initData();
    }

    private void initView() {
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        tvNum = (TextView) rootView.findViewById(R.id.tvNum);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        rvMusicList = (RecyclerView) rootView.findViewById(R.id.rvMusicList);
        pbList = (ProgressBar) rootView.findViewById(R.id.pbList);
        rlPageContainer = (RelativeLayout) rootView.findViewById(R.id.rlPageContainer);
        tvPreviousPage = (TextView) rootView.findViewById(R.id.tvPreviousPage);
        tvCurrentPage = (TextView) rootView.findViewById(R.id.tvCurrentPage);
        tvNextPage = (TextView) rootView.findViewById(R.id.tvNextPage);

        ivBack.setOnClickListener(this);
        tvPreviousPage.setOnClickListener(this);
        tvNextPage.setOnClickListener(this);

        rvMusicList.setLayoutManager(new LinearLayoutManager(activity));
        listAdapter = new NetListAdapter(activity);
        rvMusicList.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new NetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SongModel songModel) {
                LocalMainFragment mainFragment = (LocalMainFragment) getParentFragment();
                LocalPlayFragment playFragment = mainFragment.getPlayFragment();
                playFragment.bindData(list, currentPagInfo, position, true);
                popTo(LocalPlayFragment.class, false);
            }

            @Override
            public void onDeleteClick(int position, SongModel songModel) {
            }
        });
    }

    private void initData() {
        rlPageContainer.setVisibility(View.GONE);
        list = MusicPlayMangerImp.getInstance().getPlayList();
        for (SongModel songModel : list) {
            songModel.setPlay(false);
        }
        //设置正在播放的index
        if (getArguments().containsKey("position")) {
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
        tvNum.setText("播放列表（" + currentPagInfo.getTotal() + "）");
    }

    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastClick()) return;
        switch (view.getId()) {
            case R.id.ivBack:
                pop();
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
                presenter.loadLocalMusicByPageNum(page);
            }
        } else {
            if (currentPage <= 1) {
                return;
            } else {
                int page = currentPage - 1;
                presenter.loadLocalMusicByPageNum(page);
            }
        }
    }

    @Override
    public void showLoading() {
        rvMusicList.setVisibility(View.INVISIBLE);
        pbList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        rvMusicList.setVisibility(View.VISIBLE);
        pbList.setVisibility(View.INVISIBLE);
    }

    public void onPlaySongChange(SongModel song, int position) {
        listAdapter.onItemPlay(song);
    }

    @Override
    public void loadLocalMusicSuccess(int count, int currentPage, List<SongModel> list) {
        tvNum.setText("播放列表（" + count + "）");
        currentPagInfo.setTotal(count);
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
            rvMusicList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            listAdapter.setDataList(list);

            setPageStatus();
        } else {
            rvMusicList.setVisibility(View.INVISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
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
}
