package net.imoran.auto.music.network.api;

import android.util.Log;


import net.imoran.auto.music.network.bean.BaseBean;
import net.imoran.auto.music.network.bean.RootBean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Horizony on 2018/3/12.
 */

public abstract class ApiObserver<T extends BaseBean> implements Observer<RootBean<T>> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(RootBean<T> rootBean) {
        if (rootBean != null && rootBean.getRet() == 200) {
            if (rootBean.getData() != null) {
                try {
                    T data = rootBean.getData();
                    if ("000000".equals(data.getRetcde())) {
                        if (data != null)
                            onSuccess(data);
                        else
                            onError(" 没有查询到数据");
                    } else {
                        Log.d("Http", data.getRetmsg());
                        onError(data.getRetmsg());
                    }
                } catch (ClassCastException e) {
                    Log.d("Http", "服务器返回数据处理出错了");
                    onError("服务器返回数据处理出错了");
                }
            } else {
                Log.d("Http", "服务器没有返回任何数据");
                onError("服务器没有返回任何数据");
            }
        } else {
            onError(rootBean.getMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e != null && e.getMessage() != null) {
            Log.d("Http", e.getMessage());
            onError(e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    public abstract void onError(String msg);

}
