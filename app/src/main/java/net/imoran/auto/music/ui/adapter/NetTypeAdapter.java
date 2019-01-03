package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.ui.adapter.base.RVHolder;
import net.imoran.auto.music.utils.StringUtils;


public class NetTypeAdapter extends BaseRecycleAdapter<NetTypeBean, NetTypeAdapter.NetTypeViewHolder> {
    private final int type;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, NetTypeBean typeBean);
    }

    public NetTypeAdapter(Context mContext, int type) {
        super(mContext);
        this.type = type;
    }

    @Override
    public NetTypeViewHolder newViewHolder(ViewGroup parent, int viewType) {
        NetTypeViewHolder viewHolder = new NetTypeViewHolder(
                LayoutInflater.from(mContext).inflate(type == 0 ?
                        R.layout.adapter_item_net_type_language :
                        R.layout.adapter_item_net_type, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NetTypeViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, mDataList.get(position));
            }
        });
    }

    public static class NetTypeViewHolder extends RVHolder<NetTypeBean> {
        private final ImageView musicTypeIcon;
        private final TextView musicTypeName;
        private final Context mContext;

        public NetTypeViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            musicTypeIcon = (ImageView) itemView.findViewById(R.id.musicTypeIcon);
            musicTypeName = (TextView) itemView.findViewById(R.id.musicTypeName);
        }

        @Override
        public void bindData(NetTypeBean musicTypeBean, int position) {
            musicTypeName.setText(musicTypeBean.getName());
            if (StringUtils.isNotEmpty(musicTypeBean.getIconResUrl())) {
                GlideApp.with(mContext)
                        .load(musicTypeBean.getIconResUrl()).centerCrop()
                        .placeholder(R.drawable.bg_music_musicplay_default).circleCrop()
                        .thumbnail(0.4f)
                        .override(120, 120)
                        .into(musicTypeIcon);
            }
        }
    }

}
