package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.sprite.Sprite;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;

public class LoadingScene extends BaseScene {

    public LoadingScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        this.setBackgroundEnabled(false);

        final Sprite loadingSprite = createACImageSprite(TextureEnum.COMMON_LOADING, this.cameraCenterX, this.cameraCenterY);
        final IEntityModifier modifier = new LoopEntityModifier(new RotationByModifier(0.45f, 360));
        loadingSprite.registerEntityModifier(modifier);
        this.attachChild(loadingSprite);
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    public void leaveScene() {
    }
}
