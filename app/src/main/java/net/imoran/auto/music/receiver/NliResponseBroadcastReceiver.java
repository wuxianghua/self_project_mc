package net.imoran.auto.music.receiver;

import android.support.annotation.Nullable;

import net.imoran.auto.music.vui.VUIManager;
import net.imoran.auto.scenebase.lib.SceneBroadcastReceiver;
import net.imoran.sdk.bean.base.BaseContentEntity;

public class NliResponseBroadcastReceiver extends SceneBroadcastReceiver {
    @Nullable
    @Override
    public String onNliDispatch(BaseContentEntity baseContentEntity, String s) {
        VUIManager.getInstance().parseNliVUIContent(baseContentEntity);
        return null;
    }
}
