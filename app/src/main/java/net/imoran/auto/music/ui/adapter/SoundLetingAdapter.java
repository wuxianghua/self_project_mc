package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.sdk.bean.bean.AudioAlbumBean;
import net.imoran.sdk.bean.bean.LetingCatalogBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;

import java.util.ArrayList;
import java.util.List;


public class SoundLetingAdapter extends RecyclerView.Adapter<SoundLetingAdapter.SoundAlbumHolder> {
    private List<LetingNewsBean.LetingNewsEntity> mList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String categoryId);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SoundLetingAdapter(Context mContext, List<LetingNewsBean.LetingNewsEntity> list) {
        this.mContext = mContext;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext.getApplicationContext());
    }

    @Override
    public SoundAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SoundAlbumHolder(mLayoutInflater.inflate(R.layout.leting_rec_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SoundAlbumHolder holder, final int position) {
        final LetingNewsBean.LetingNewsEntity entity = mList.get(position);
        holder.tv_id_album.setText(position + 1 + "");
        holder.tv_title_album.setText(entity.getTitle());
        GlideApp.with(mContext)
                .load(entity.getImage()).centerCrop()
                .override(80, 80)
                .thumbnail(0.4f)
                .into(holder.iv_pic_album);
        holder.tv_category_leting.setText(entity.getCatalog_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(entity.getCatalog_id());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SoundAlbumHolder extends RecyclerView.ViewHolder {
        private TextView tv_id_album;
        private ImageView iv_pic_album;
        private TextView tv_title_album;
        private TextView tv_category_leting;

        public SoundAlbumHolder(View itemView) {
            super(itemView);
            tv_id_album = (TextView) itemView.findViewById(R.id.subscribe_id);
            tv_title_album = (TextView) itemView.findViewById(R.id.subscribe_name);
            iv_pic_album = (ImageView) itemView.findViewById(R.id.subscirbe_pic);
            tv_category_leting = (TextView) itemView.findViewById(R.id.leting_category);
        }
    }
}
