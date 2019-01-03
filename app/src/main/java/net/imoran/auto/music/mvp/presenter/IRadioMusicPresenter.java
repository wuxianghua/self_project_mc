package net.imoran.auto.music.mvp.presenter;

import java.util.Map;

public interface IRadioMusicPresenter {
    void collectedRadio(boolean isAdd, String type, String frequency, String name);

    void getCollectedRadioList();

    void acrRadioSearch(String frequency, String type);
}
