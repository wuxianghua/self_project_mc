package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.imoran.auto.music.ui.adapter.base.RVHolder;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecycleAdapter<T, V extends RVHolder> extends RecyclerView.Adapter<V> {
    protected String TAG = "BaseRecycleAdapter";
    public final Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDataList;

    public BaseRecycleAdapter(Context mContext) {
        this.mContext = mContext;
        this.mDataList = new ArrayList();
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        V holder = newViewHolder(parent, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        try {
            holder.bindData(this.getItem(position), position);
        } catch (Exception var4) {
            Log.e(this.TAG, "onBindViewHolder Exception:" + var4.getMessage());
        }
    }


    public int getItemCount() {
        return this.mDataList != null ? this.mDataList.size() : 0;
    }

    public T getItem(int position) {
        return this.mDataList != null ? this.mDataList.get(position) : null;
    }

    public void setDataList(List<T> mDataList) {
        this.mDataList.clear();
        if (mDataList != null) {
            this.mDataList.addAll(mDataList);
        }
        this.notifyDataSetChanged();
    }

    public void addDataList(List<T> mDataList) {
        if (mDataList != null) {
            this.mDataList.addAll(mDataList);
        }

        this.notifyDataSetChanged();
    }

    public void clearDataList(List<T> mDataList) {
        if (mDataList != null) {
            this.mDataList.clear();
        }

        this.notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return this.mDataList;
    }

    public void removeItem(T itemData) {
        if (this.mDataList != null) {
            this.mDataList.remove(itemData);
        }

        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (this.mDataList != null && this.mDataList.size() > position) {
            this.mDataList.remove(position);
        }
        this.notifyDataSetChanged();
    }


    public abstract V newViewHolder(ViewGroup parent, int viewType);

}
