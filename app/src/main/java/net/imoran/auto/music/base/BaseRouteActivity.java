package net.imoran.auto.music.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import net.imoran.auto.scenebase.lib.SceneUtils;
import net.imoran.sdk.bean.base.BaseContentEntity;
import net.imoran.sdk.util.ReplyUtils;

import java.util.ArrayList;


public abstract class BaseRouteActivity extends SupportActivity {
    private static final String TAG = "BaseRouteActivity";

    public BaseRouteActivity() {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onIntentCome(this.getIntent());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.onIntentCome(intent);
    }

    protected abstract boolean onNliDispatch(BaseContentEntity var1, String var2);

    protected void onIntentCome(Intent intent) {
        if (!this.checkIntent(intent)) {
            Log.e("BaseRouteActivity", "onNewIntent: intent is error");
        } else {
            String baseContentEntityStr = intent.getStringExtra("baseContentEntity");
            BaseContentEntity baseContentEntity = ReplyUtils.createResponseFromJson(baseContentEntityStr);
            if (baseContentEntity != null) {
                boolean dispatchResult = this.onNliDispatch(baseContentEntity, baseContentEntityStr);
                if (dispatchResult) {
                    SceneUtils.handleNliSceneInService(this.getApplicationContext(), baseContentEntity);
                }
            }

        }
    }

    private boolean checkIntent(Intent intent) {
        return intent != null && intent.getExtras() != null ? intent.getExtras().containsKey("baseContentEntity") : false;
    }
}
