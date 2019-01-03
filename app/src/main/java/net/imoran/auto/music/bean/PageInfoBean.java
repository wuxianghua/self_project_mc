package net.imoran.auto.music.bean;

import java.io.Serializable;

public class PageInfoBean implements Serializable {
    private int total = 0;
    private int totalPageNum = 1;
    private int currentPageNum = 1;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        if (total > 0) {
            if (total % 10 > 0) {
                totalPageNum = total / 10 + 1;
            } else {
                totalPageNum = total / 10;
            }
        } else {
            totalPageNum = 1;
        }
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public PageInfoBean(int total, int currentPageNum) {
        setTotal(total);
        setCurrentPageNum(currentPageNum);
    }

    public PageInfoBean() {

    }
}
