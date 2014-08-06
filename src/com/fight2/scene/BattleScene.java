package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.BattlePartyFrame;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class BattleScene extends BaseScene {
    private final float TOP_PARTY_FRAME_Y = this.cameraHeight - TextureEnum.BATTLE_PARTY_TOP.getHeight();

    public BattleScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float width = TextureEnum.BATTLE_PARTY_BOTTOM.getWidth();
        final float frameStartX = 90;
        final float partyGap = 10;

        final BattlePartyFrame bottomPartyFrame1 = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, frameStartX, 0, true);
        this.attachChild(bottomPartyFrame1);
        bottomPartyFrame1.setFullPoint(284);

        final BattlePartyFrame bottomPartyFrame2 = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, frameStartX + width + partyGap, 0, true);
        this.attachChild(bottomPartyFrame2);
        bottomPartyFrame2.setFullPoint(284);
        bottomPartyFrame2.setCurrentPoint(150);

        final BattlePartyFrame bottomPartyFrame3 = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, frameStartX + (width + partyGap) * 2f, 0, true);
        this.attachChild(bottomPartyFrame3);
        bottomPartyFrame3.setFullPoint(284);
        bottomPartyFrame3.setCurrentPoint(10);

        final BattlePartyFrame topPartyFrame1 = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, frameStartX, TOP_PARTY_FRAME_Y, false);
        this.attachChild(topPartyFrame1);
        topPartyFrame1.setFullPoint(284);

        final BattlePartyFrame topPartyFrame2 = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, frameStartX + width + partyGap, TOP_PARTY_FRAME_Y, false);
        this.attachChild(topPartyFrame2);
        topPartyFrame2.setFullPoint(284);
        topPartyFrame2.setCurrentPoint(150);

        final BattlePartyFrame topPartyFrame3 = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, frameStartX + (width + partyGap) * 2f, TOP_PARTY_FRAME_Y, false);
        this.attachChild(topPartyFrame3);
        topPartyFrame3.setFullPoint(284);
        topPartyFrame3.setCurrentPoint(10);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 250);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);

    }

    @Override
    public void updateScene() {

    }

    /**
     * Anchor left bottom sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    protected BattlePartyFrame createPartyFrame(final TextureEnum textureEnum, final float x, final float y, final boolean isBottom) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final BattlePartyFrame partyFrame = new BattlePartyFrame(pX, pY, texture, vbom, isBottom);
        return partyFrame;
    }
}
