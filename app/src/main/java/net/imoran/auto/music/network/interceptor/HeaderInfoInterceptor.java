package net.imoran.auto.music.network.interceptor;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInfoInterceptor implements Interceptor {
    private static final String TAG = HeaderInfoInterceptor.class.getSimpleName();
    private String version;

    public HeaderInfoInterceptor(String version) {
        this.version = version;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newHeaderRequest = request.newBuilder()
                .addHeader("app-version", version)
                .build();

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(newHeaderRequest);
        } catch (Exception e) {
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        Log.e(TAG, "intercept: " + tookMs + "ms");
        return response;
    }
}
