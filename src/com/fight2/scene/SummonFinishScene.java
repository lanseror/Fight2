package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
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

import com.fight2.GameActivity;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.CardFrame;
import com.fight2.util.F2SoundManager;

public class SummonFinishScene extends BaseScene {
    private final static float CARD_WIDTH = 96.5f * 3;
    private final static float CARD_HEIGHT = CARD_WIDTH * 1.5f;
    // private final Sprite cardSprite;
    private final IEntity cardFrame;
    private final static float SCALE = 0.3333f;

    public SummonFinishScene(final Card card, final GameActivity activity) throws IOException {
        super(activity);
        if (card != null) {
            this.cardFrame = new CardFrame(cameraCenterX, cameraCenterY, CARD_WIDTH, CARD_HEIGHT, card, activity);
            cardFrame.setRotation(90);
            cardFrame.setScale(SCALE);
            this.attachChild(cardFrame);
        } else {
            cardFrame = null;
        }

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
        if (cardFrame != null) {
            F2SoundManager.getInstance().play(SoundEnum.SUMMON);
            final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(0.15f, SCALE, 1), new RotationByModifier(0.15f, -90));
            cardFrame.registerEntityModifier(modifier);
        } else {
            alert("可能服务器出错或者你召唤的卡片已经超过100张！");
        }
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
