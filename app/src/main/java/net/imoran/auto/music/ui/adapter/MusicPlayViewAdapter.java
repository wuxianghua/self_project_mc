package net.imoran.auto.music.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayViewAdapter extends PagerAdapter {
    private List<View> viewList = new ArrayList<>();

    public MusicPlayViewAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void destroyItem(View arg0, int arg1, Object arg2) {
        if (arg1 <= viewList.size() - 1) {
            ((ViewPager) arg0).removeView(viewList.get(arg1));
        }
    }

    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(viewList.get(arg1));
        return viewList.get(arg1);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
