package net.imoran.auto.music.mvp.base;

public interface BaseView {
    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void hideLoading();


    void showErrorView();
}
