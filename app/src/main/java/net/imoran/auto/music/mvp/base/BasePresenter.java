package net.imoran.auto.music.mvp.base;


public abstract class BasePresenter<V extends BaseView> {
    protected V view;

    public BasePresenter() {

    }

    public void setView(V view) {
        this.view = view;
    }
}
