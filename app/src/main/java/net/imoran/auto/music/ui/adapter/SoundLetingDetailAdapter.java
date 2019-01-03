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
import net.imoran.sdk.bean.bean.LetingNewsBean;

import java.util.ArrayList;
import java.util.List;


public class SoundLetingDetailAdapter extends RecyclerView.Adapter<SoundLetingDetailAdapter.SoundAlbumHolder> {
    private List<LetingNewsBean.LetingNewsEntity> mList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int i,List<LetingNewsBean.LetingNewsEntity> list);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SoundLetingDetailAdapter(Context mContext, List<LetingNewsBean.LetingNewsEntity> list) {
        this.mContext = mContext;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext.getApplicationContext());
    }

    @Override
    public SoundAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SoundAlbumHolder(mLayoutInflater.inflate(R.layout.leting_detail_item, parent, false));
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
        holder.tv_update_time.setText(entity.getHuman_time());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position,mList);
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
        private TextView tv_update_time;

        public SoundAlbumHolder(View itemView) {
            super(itemView);
            tv_id_album = (TextView) itemView.findViewById(R.id.subscribe_id);
            tv_title_album = (TextView) itemView.findViewById(R.id.subscribe_name);
            iv_pic_album = (ImageView) itemView.findViewById(R.id.subscirbe_pic);
            tv_update_time = (TextView) itemView.findViewById(R.id.tv_update_time);
        }
    }
}
