package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.util.TiledTextureFactory;

public class UpgradeFinishScene extends BaseScene {
    public UpgradeFinishScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionDown()) {
                    back();
                }
                return false;
            }

        });

        final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.UPGRADE_EFFECT_BG);
        final AnimatedSprite effectSprite = new AnimatedSprite(this.cameraCenterX, this.cameraCenterY, tiledTextureRegion, vbom);
        this.attachChild(effectSprite);
        effectSprite.animate(100, true);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        // playAnimation();
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    protected void playAnimation() {
    }

    @Override
    public void leaveScene() {

    }

}
