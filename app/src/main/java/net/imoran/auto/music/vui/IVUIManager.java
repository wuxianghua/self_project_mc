package net.imoran.auto.music.vui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.imoran.auto.music.player.model.SongModel;
import net.imoran.sdk.bean.base.BaseContentEntity;

import java.util.List;

public interface IVUIManager {

    //处理方控按键
    void parseFlyContent(int keyCode);

    //处理NliVUI控制
    void parseNliVUIContent(BaseContentEntity contentEntity);
}
