package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.util.TextureFactory;

public abstract class BaseCardPackScene extends BaseScene {
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 110;
    public final static int CARD_HEIGHT = 165;
    public final static float CARD_Y = CARD_HEIGHT * 0.5f;

    public BaseCardPackScene(final GameActivity activity) throws IOException {
        super(activity);
    }

    public Sprite createCardAvatarSprite(final Card card, final float x, final float y) {
        final float width = 135;
        final float height = 135;
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        Sprite sprite = null;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getAvatar());
        sprite = new Sprite(pX, pY, width, height, texture, vbom);

        return sprite;
    }

    public abstract void onGridCardsChange();

    public abstract IEntity[] getCardGrids();

    public abstract IEntity[] getInGridCardSprites();

}