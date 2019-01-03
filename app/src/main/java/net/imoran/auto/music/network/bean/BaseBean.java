package net.imoran.auto.music.network.bean;

/**
 * Created by Horizony on 2018/3/14.
 */

public class BaseBean {
    private String retcde = "000000";
    private String retmsg;

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getRetcde() {
        return retcde;
    }

    public void setRetcde(String retcde) {
        this.retcde = retcde;
    }
}
