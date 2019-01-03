package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.sdk.bean.bean.PodcastCategoryBean;

import java.util.List;

/**
 * Created by jingz on 2018/6/26.
 */

public class SoundListAdapter extends BaseAdapter {

    private List<PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity> mSubCategoryEntity;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public SoundListAdapter(Context context, List<PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity> subCategoryArrayListEntities) {
        mSubCategoryEntity = subCategoryArrayListEntities;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mSubCategoryEntity == null) return 0;
        return mSubCategoryEntity.size();
    }

    @Override
    public PodcastCategoryBean.PodcastCategoryEntity.SubCategoryArrayListEntity getItem(int i) {
        if (mSubCategoryEntity == null) return null;
        return mSubCategoryEntity.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_hot, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (getItem(i) != null) {
            holder.hotTextView.setText(getItem(i).getName());
            if ("".equals(getItem(i).getPic())) {
                GlideApp.with(mContext)
                        .load(R.drawable.ic_musiclibrary_leting).centerCrop().circleCrop()
                        .override(80, 80)
                        .thumbnail(0.4f)
                        .into(holder.hotImageView);
            }else {
                GlideApp.with(mContext)
                        .load(getItem(i).getPic()).centerCrop().circleCrop()
                        .override(80, 80)
                        .thumbnail(0.4f)
                        .into(holder.hotImageView);
            }
        }
        return view;
    }

    public static class ViewHolder {
        private ImageView hotImageView;
        private TextView hotTextView;

        public ViewHolder(View convertView) {
            hotImageView = (ImageView) convertView.findViewById(R.id.hot_image);
            hotTextView = (TextView) convertView.findViewById(R.id.hot_text);
            convertView.setTag(this);
        }
    }
}
