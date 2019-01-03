package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import net.imoran.auto.music.R;

import java.util.List;

/**
 * Created by jingz on 2018/6/19.
 */

public class FlowLayoutAdapter extends TagAdapter {

    private LayoutInflater mInflater;
    private List<String> mData;

    public FlowLayoutAdapter(List datas, Context context) {
        super(datas);
        mInflater = LayoutInflater.from(context);
        mData = datas;
    }

    @Override
    public View getView(FlowLayout parent, int position, Object o) {
        TextView tv = (TextView) mInflater.inflate(R.layout.entertain_item,parent,false);
        tv.setText(mData.get(position));
        return tv;
    }
}
