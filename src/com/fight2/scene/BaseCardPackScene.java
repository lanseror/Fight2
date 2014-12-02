package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.engine.CardFrame;
import com.fight2.util.TextureFactory;

public abstract class BaseCardPackScene extends BaseScene {
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 110;
    public final static int CARD_HEIGHT = 165;
    public final static float CARD_Y = CARD_HEIGHT * 0.5f;

    public BaseCardPackScene(final GameActivity activity) throws IOException {
        super(activity);
    }

    public Sprite createCardAvatarSprite(final Card card, final float width, final float height) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getAvatar());
        final Sprite sprite = new Sprite(0, 0, width, height, texture, vbom);
        return sprite;
    }

    public IEntity createCardSprite(final Card card, final float width, final float height) {
        final IEntity cardSprite = new CardFrame(0, 0, width, height, card, activity);
        return cardSprite;
    }

    public abstract void onGridCardsChange();

    public abstract IEntity[] getCardGrids();

    public abstract IEntity[] getInGridCardSprites();

}