package net.imoran.auto.music.ui.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by love on 2018/3/20.
 */

public abstract class RVHolder<T> extends RecyclerView.ViewHolder {

    public final View itemView;

    public RVHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public abstract void bindData(T t, int position);
}
