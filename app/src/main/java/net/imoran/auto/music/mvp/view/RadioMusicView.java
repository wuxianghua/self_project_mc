package net.imoran.auto.music.mvp.view;

import net.imoran.auto.music.mvp.base.BaseView;
import net.imoran.auto.music.network.bean.RadioAcrChildItemBean;
import net.imoran.auto.music.radio.model.RadioModel;
import net.imoran.sdk.bean.bean.BroadcastBean;

import java.util.List;

public interface RadioMusicView extends BaseView {
    void collectedRadioSuccess(boolean isAdd);

    void collectedRadioFail(String errorMsg);

    void getCollectedRadioListSuccess(List<RadioModel> list);

    void getCollectedRadioListFail(String errorMsg);

    void onRadioAcrResult(RadioAcrChildItemBean bean);
}
