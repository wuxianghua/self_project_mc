package net.imoran.auto.music.utils;

import net.imoran.auto.music.bean.NetTypeBean;
import net.imoran.auto.music.player.model.SongModel;

import net.imoran.auto.music.radio.manager.RadioBand;
import net.imoran.auto.music.radio.model.RadioModel;
import net.imoran.sdk.bean.bean.AudioProgramBean;
import net.imoran.sdk.bean.bean.BroadcastBean;
import net.imoran.sdk.bean.bean.LetingNewsBean;
import net.imoran.sdk.bean.bean.MusicCategoryBean;
import net.imoran.sdk.bean.bean.SongBean;

import java.util.ArrayList;
import java.util.List;

public class DataConvertUtils {

    public static List<SongModel> getSongModelList(SongBean songBean) {
        List<SongModel> list = new ArrayList<>();
        if (songBean != null && ListUtils.isNotEmpty(songBean.getSong())) {
            for (SongBean.SongEntity songEntity : songBean.getSong()) {
                SongModel songModel = songEntityToSongModel(songEntity);
                list.add(songModel);
            }
        }
        return list;
    }

    public static SongModel songEntityToSongModel(AudioProgramBean.AudioProgramEntity audioProgramEntity) {
        SongModel songModel = new SongModel();
        ArrayList<String> singer = new ArrayList<String>();
        singer.add(audioProgramEntity.getAuthor());
        songModel.setPicUrl(audioProgramEntity.getCover_url());
        songModel.setSongUrl(audioProgramEntity.getPlay_url());
        songModel.setName(audioProgramEntity.getTrack_title());
        songModel.setAlbum(audioProgramEntity.getAlbum_title());
        songModel.setAlbumId(audioProgramEntity.getAlbum_id());
        songModel.setTrackId(audioProgramEntity.getTrack_id());
        songModel.setCollection(audioProgramEntity.isIs_in_user_collection());
        songModel.setSinger(singer);
        songModel.setUuid(audioProgramEntity.getTrack_id());
        return songModel;
    }

    public static List<SongModel> getSongModelList(AudioProgramBean songBean) {
        List<SongModel> list = new ArrayList<>();
        if (songBean != null && ListUtils.isNotEmpty(songBean.getAudio_program())) {
            for (AudioProgramBean.AudioProgramEntity audioProgramEntity : songBean.getAudio_program()) {
                SongModel songModel = songEntityToSongModel(audioProgramEntity);
                list.add(songModel);
            }
        }
        return list;
    }

    public static List<SongModel> getSongModelList(List<AudioProgramBean.AudioProgramEntity> songBean) {
        List<SongModel> list = new ArrayList<>();
        if (songBean != null && ListUtils.isNotEmpty(songBean)) {
            for (AudioProgramBean.AudioProgramEntity audioProgramEntity : songBean) {
                SongModel songModel = songEntityToSongModel(audioProgramEntity);
                list.add(songModel);
            }
        }
        return list;
    }

    public static List<SongModel> getSongModelFromList(List<LetingNewsBean.LetingNewsEntity> letingBean) {
        List<SongModel> list = new ArrayList<>();
        if (letingBean != null && ListUtils.isNotEmpty(letingBean)) {
            for (LetingNewsBean.LetingNewsEntity letingNewsEntity : letingBean) {
                SongModel songModel = songEntityToSongModel(letingNewsEntity);
                list.add(songModel);
            }
        }
        return list;
    }

    public static SongModel songEntityToSongModel(SongBean.SongEntity songEntity) {
        SongModel songModel = new SongModel();
        songModel.setType("1");
        songModel.setUuid(songEntity.getMusic_id());
        songModel.setPicUrl(songEntity.getPic_url());
        songModel.setSongUrl(songEntity.getSong_url());
        songModel.setName(songEntity.getName());
        songModel.setAlbum(songEntity.getAlbum());
        songModel.setDuration(songEntity.getDuration());
        songModel.setCollection(songEntity.isIs_in_user_collection());
        songModel.setSinger(songEntity.getSinger());
        return songModel;
    }

    public static SongModel songEntityToSongModel(LetingNewsBean.LetingNewsEntity songEntity) {
        SongModel songModel = new SongModel();
        songModel.setType("1");
        songModel.setUuid(songEntity.getNews_id());
        songModel.setPicUrl(songEntity.getImage());
        songModel.setSongUrl(songEntity.getAudio());
        songModel.setName(songEntity.getTitle());
        songModel.setAlbum(songEntity.getSource());
        songModel.setDuration(songEntity.getDuration());
        songModel.setCollection(false);
        songModel.setSinger(new ArrayList<String>());
        return songModel;
    }

    public static String getAlbumSinger(SongModel songModel) {
        if (ListUtils.isNotEmpty(songModel.getSinger()) && songModel.getAlbum() != null) {
            if (songModel.getSinger().size() > 1) {
                return songModel.getAlbum() + "/" + songModel.getSinger().get(0) + "," + songModel.getSinger().get(1);
            } else {
                return songModel.getAlbum() + "/" + songModel.getSinger().get(0);
            }
        } else if (ListUtils.isNotEmpty(songModel.getSinger()) && songModel.getAlbum() == null) {
            if (songModel.getSinger().size() > 1) {
                return songModel.getSinger().get(0) + "," + songModel.getSinger().get(1);
            } else {
                return songModel.getSinger().get(0);
            }
        } else if ((!ListUtils.isNotEmpty(songModel.getSinger())) && songModel.getAlbum() != null) {
            return songModel.getAlbum();
        } else {
            return "";
        }
    }

    /**
     * @param entity
     * @return
     */
    public static RadioModel broadcastEntityToRadioModel(BroadcastBean.BroadcastEntity entity) {
        RadioModel radioModel = new RadioModel();
        radioModel.setBand(entity.getType().equals("am") ? RadioBand.AM : RadioBand.FM);
        radioModel.setPlayUrl(entity.getPlay_url());
        radioModel.setCoverUrl(entity.getCover_url());
        radioModel.setTitle(entity.getTitle());
        radioModel.setInfo(entity.getInfo());
        radioModel.setLocation(entity.getLocation());
        radioModel.setFrequency(Double.parseDouble(entity.getFrequency()));
        return radioModel;
    }

    public static List<RadioModel> getRadioModelList(BroadcastBean broadcastBean) {
        List<RadioModel> list = new ArrayList<>();
        if (broadcastBean != null && ListUtils.isNotEmpty(broadcastBean.getBroadcast())) {
            for (BroadcastBean.BroadcastEntity entity : broadcastBean.getBroadcast()) {
                RadioModel radioModel = broadcastEntityToRadioModel(entity);
                list.add(radioModel);
            }
        }
        return list;
    }

    public static List<NetTypeBean> getNetTypeBeanList(MusicCategoryBean categoryBean) {
        List<NetTypeBean> list = new ArrayList<>();
        if (categoryBean != null && ListUtils.isNotEmpty(categoryBean.getMusic_category())) {
            for (MusicCategoryBean.MusicCategoryEntity entity : categoryBean.getMusic_category()) {
                NetTypeBean netTypeBean = new NetTypeBean(entity.getCategory_name(), entity.getCategory_pic());
                list.add(netTypeBean);
            }
        }
        return list;
    }
}
