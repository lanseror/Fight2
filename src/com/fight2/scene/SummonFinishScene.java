package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class SummonFinishScene extends BaseScene {

    private final Sprite cardSprite;

    public SummonFinishScene(final Card card, final GameActivity activity) throws IOException {
        super(activity);
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
        cardSprite = new Sprite(cameraCenterX, cameraCenterY, 100, 150, texture, vbom);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionDown()) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Summon);
                }
                return false;
            }

        });

        this.attachChild(cardSprite);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(1f, 1f, 4), new RotationByModifier(1f, 360 * 3));
        cardSprite.registerEntityModifier(modifier);
    }

}
