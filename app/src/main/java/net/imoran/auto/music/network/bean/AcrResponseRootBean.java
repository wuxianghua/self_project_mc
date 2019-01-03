package net.imoran.auto.music.network.bean;

/**
 * Created by Horizony on 2018/3/12.
 */

public class AcrResponseRootBean {
    private int ret;//响应码  000000:请求成功
    private String msg;//响应描述
    private AcrRootBean data;//响应数据

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AcrRootBean getData() {
        return data;
    }

    public void setData(AcrRootBean data) {
        this.data = data;
    }
}
