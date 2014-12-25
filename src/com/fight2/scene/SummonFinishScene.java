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
    // private final IEntity cardAttributeFrame;
    // private final Font mFont;
    // private final Text hpText;
    // private final Text atkText;
    private final static float SCALE = 0.3333f;

    public SummonFinishScene(final Card card, final GameActivity activity) throws IOException {
        super(activity);
        this.cardFrame = new CardFrame(cameraCenterX, cameraCenterY, CARD_WIDTH, CARD_HEIGHT, card, activity);
        // this.mFont = ResourceManager.getInstance().getFont(FontEnum.Default, 27);
        // this.cardFrame = new Rectangle(cameraCenterX, cameraCenterY, CARD_WIDTH, CARD_HEIGHT, vbom);
        cardFrame.setRotation(90);
        cardFrame.setScale(SCALE);
        // cardFrame.setAlpha(0);
        this.attachChild(cardFrame);
        // final ITextureRegion texture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        // this.cardSprite = new Sprite(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
        // cardFrame.attachChild(cardSprite);
        // loadImageFromServer(card);
        //
        // this.cardAttributeFrame = new Rectangle(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, vbom);
        // cardFrame.attachChild(cardAttributeFrame);
        // cardAttributeFrame.setAlpha(0);
        // cardAttributeFrame.setVisible(false);
        // final ITextureRegion starTexture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_STAR);
        // for (int i = 0; i < card.getStar(); i++) {
        // final Sprite star = new Sprite(29 + 9f * i, CARD_HEIGHT - 8, starTexture, vbom);
        // star.setScale(SCALE);
        // cardAttributeFrame.attachChild(star);
        // }
        // final ITextureRegion hpAtkFrameTexture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_HPATK);
        // final Sprite hpAtkFrame = new Sprite(CARD_WIDTH - 30, 25, hpAtkFrameTexture, vbom);
        // hpAtkFrame.setScale(SCALE);
        // cardAttributeFrame.attachChild(hpAtkFrame);
        // hpText = new Text(70, 70, mFont, "0123456789", vbom);
        // hpText.setColor(0XFFFFE3B0);
        // atkText = new Text(70, 23, mFont, "0123456789", vbom);
        // atkText.setColor(0XFFFFE3B0);
        // hpText.setText(String.valueOf(card.getHp()));
        // atkText.setText(String.valueOf(card.getAtk()));
        // hpAtkFrame.attachChild(hpText);
        // hpAtkFrame.attachChild(atkText);
        // final Font levelFont = ResourceManager.getInstance().getFont(FontEnum.Bold, 32);
        // final Text levelText = new Text(10.5f, 8.5f, levelFont, String.valueOf(card.getLevel()), vbom);
        // levelText.setColor(0XFFFFE3B0);
        // levelText.setScale(SCALE);
        // cardAttributeFrame.attachChild(levelText);
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
        F2SoundManager.getInstance().play(SoundEnum.SUMMON);
        final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(0.3f, SCALE, 1), new RotationByModifier(0.3f, 270));
        cardFrame.registerEntityModifier(modifier);
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}
