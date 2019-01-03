package net.imoran.auto.music.app;


public class MusicApi {
    public static final String key = "5B24CE667012B42F";
    public static final String baseUrl = "http://api.xiaomor.com/api/";
    public static final String musicBaseUrl = baseUrl + "music/";
    public static final String radioBaseUrl = baseUrl + "broadcast/";
    public static final String music_star = musicBaseUrl + "getcollects";
    public static final String music_type = musicBaseUrl + "song";
    //音乐加载更多
    public static final String music_list_load_more = musicBaseUrl + "song/paging";
    //star加载更多
    public static final String music_collects_load_more = musicBaseUrl + "getcollects/paging";
    public static final String radio_collect = radioBaseUrl + "addcollects";
    public static final String radio_delcollect = radioBaseUrl + "delcollects";

}
