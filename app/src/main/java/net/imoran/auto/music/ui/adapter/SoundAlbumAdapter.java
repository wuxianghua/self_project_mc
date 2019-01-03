package net.imoran.auto.music.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import net.imoran.auto.music.R;
import net.imoran.auto.music.app.GlideApp;
import net.imoran.sdk.bean.bean.AudioAlbumBean;

import java.util.ArrayList;
import java.util.List;



public class SoundAlbumAdapter extends RecyclerView.Adapter<SoundAlbumAdapter.SoundAlbumHolder> {
    private List<AudioAlbumBean.AudioAlbumEntity> mList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AudioAlbumBean.AudioAlbumEntity contact);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SoundAlbumAdapter(Context mContext, List<AudioAlbumBean.AudioAlbumEntity> list) {
        this.mContext = mContext;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext.getApplicationContext());
    }

    @Override
    public SoundAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SoundAlbumHolder(mLayoutInflater.inflate(R.layout.subscirbe_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SoundAlbumHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(mList.get(position));
                }
            }
        });
        AudioAlbumBean.AudioAlbumEntity entity = mList.get(position);
        holder.tv_id_album.setText(position + 1 + "");
        holder.tv_title_album.setText(entity.getAlbum_title());
        holder.tv_date_album.setText(entity.getDate());
        int track_count = entity.getTrack_count();
        holder.tv_code_album.setText((track_count > 50?50:track_count) + " 期");
        GlideApp.with(mContext)
                .load(entity.getCover_url()).transform(new RoundedCorners(4))
                .into(holder.iv_pic_album);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SoundAlbumHolder extends RecyclerView.ViewHolder {
        private TextView tv_id_album;
        private ImageView iv_pic_album;
        private TextView tv_title_album;
        private TextView tv_date_album;
        private TextView tv_code_album;

        public SoundAlbumHolder(View itemView) {
            super(itemView);
            tv_code_album = (TextView) itemView.findViewById(R.id.subscribe_qihao);
            tv_date_album = (TextView) itemView.findViewById(R.id.subscribe_time);
            tv_id_album = (TextView) itemView.findViewById(R.id.subscribe_id);
            tv_title_album = (TextView) itemView.findViewById(R.id.subscribe_name);
            iv_pic_album = (ImageView) itemView.findViewById(R.id.subscirbe_pic);

        }
    }
}
