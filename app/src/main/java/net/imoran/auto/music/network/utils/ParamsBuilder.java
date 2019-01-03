package net.imoran.auto.music.network.utils;


import java.util.HashMap;

public class ParamsBuilder {
    public static final String PARAM_KEY_DEVICE_ID = "deviceId";
    public static final String PARAM_NUM_PER_PAGE = "numPerPage";
    public static final String PARAM_PAGE_NUM = "pageNum";
    private HashMap<String, String> mParams;

    private ParamsBuilder() {
        mParams = new HashMap<>();
//        mParams.put(PARAM_KEY_DEVICE_ID, PhoneUtils.getDeviceID(App.appContext));
    }

    public static ParamsBuilder start() {
        ParamsBuilder paramsBuilder = new ParamsBuilder();
        return paramsBuilder;
    }

    public static ParamsBuilder page(int pageNum, int numPerPage) {
        ParamsBuilder paramsBuilder = new ParamsBuilder();
        paramsBuilder.put(PARAM_PAGE_NUM, pageNum);
        paramsBuilder.put(PARAM_NUM_PER_PAGE, numPerPage);
        return paramsBuilder;
    }

    public ParamsBuilder put(String k, String v) {
        if (v == null) return this;
        mParams.put(k, v);
        return this;
    }

    public ParamsBuilder put(String k, int v) {
        mParams.put(k, String.valueOf(v));
        return this;
    }

    public ParamsBuilder put(String k, long v) {
        mParams.put(k, String.valueOf(v));
        return this;
    }

    public ParamsBuilder mock(boolean mock) {
        if (mock) {
            mParams.clear();
        }
        return this;
    }

    public HashMap<String, String> build() {
        return mParams;
    }

}
