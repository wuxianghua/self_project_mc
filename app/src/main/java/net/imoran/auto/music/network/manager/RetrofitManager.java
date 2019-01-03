package net.imoran.auto.music.network.manager;

import android.content.Context;
import android.util.Log;

import net.imoran.auto.music.network.interceptor.CacheStrategyInterceptor;
import net.imoran.auto.music.utils.StringUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Horizony on 2018/3/9.
 */

public class RetrofitManager {
    private static RetrofitManager instance;
    private Retrofit retrofit;
    private Context context;
    private String baseUrl;
    private OkHttpClient okHttpClient;

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        if (context == null || StringUtils.isEmpty(baseUrl)) {
            throw new RuntimeException("RetrofitManager 未初始化");
        } else {
            init(context, baseUrl);
        }
        return retrofit;
    }

    public void init(Context context, String baseUrl) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl;
        initOkHttp();
        initRetrofit();
    }

    private void initOkHttp() {
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(10 * 1000, TimeUnit.MILLISECONDS);
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                Log.e("RetrofitManager", message);
//            }
//        });
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(loggingInterceptor);

        builder.addInterceptor(new CacheStrategyInterceptor(context));
        builder.cache(cache);
        okHttpClient = builder.build();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
