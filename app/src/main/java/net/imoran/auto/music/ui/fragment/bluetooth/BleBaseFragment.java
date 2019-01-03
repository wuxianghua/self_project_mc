package net.imoran.auto.music.ui.fragment.bluetooth;

import com.google.gson.Gson;

import net.imoran.auto.music.base.BaseFragment;
import net.imoran.auto.music.mvp.base.BasePresenter;
import net.imoran.auto.music.mvp.view.BleMusicView;

import java.util.ArrayList;


public abstract class BleBaseFragment extends BaseFragment implements BleMusicView {
    protected Gson gson = new Gson();

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }


    protected ArrayList<String> getVisiblePages() {
        ArrayList<String> pages = new ArrayList<>();
        pages.add(getPageId());
        return pages;
    }
    @Override
    protected boolean isNliPage() {
        return false;
    }
    /**
     * 默认的页面id
     *
     * @return
     */
    protected String getDefaultPageId() {
        return this.getClass().getName();
    }

}
