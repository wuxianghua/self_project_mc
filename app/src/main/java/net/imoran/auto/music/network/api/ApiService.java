package net.imoran.auto.music.network.api;


import net.imoran.auto.music.network.bean.AcrRootBean;
import net.imoran.auto.music.network.bean.RadioAcrRootBean;
import net.imoran.auto.music.network.bean.RootBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Horizony on 2018/3/12.
 */

public interface ApiService {
    //收集车辆状态与音频文件的关联数据
    @Multipart
    @Headers("Content-Type:application/octet-stream")
    @POST("/api/voice/acr/pcm?")
    Observable<RootBean<AcrRootBean>> acrByPcmFile(@Query("key") String key,
                                                   @Query("deviceid") String deviceid,
                                                   @Query("pcmAudioBufferLen") String length,
                                                   @Part MultipartBody.Part filePart
    );

    //查询广播节目信息
    @GET("/api/voice/acr/radio/search?")
    Observable<RootBean<RadioAcrRootBean>> acrRadioSearch(@QueryMap Map<String, String> map);
}
