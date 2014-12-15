package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.engine.CardAvatar;
import com.fight2.entity.engine.CardFrame;

public abstract class BaseCardPackScene extends BaseScene {
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 110;
    public final static int CARD_HEIGHT = 165;
    public final static float CARD_Y = CARD_HEIGHT * 0.5f;

    public BaseCardPackScene(final GameActivity activity) throws IOException {
        super(activity);
    }

    public IEntity createCardAvatarSprite(final Card card, final float width, final float height) {
        final IEntity sprite = new CardAvatar(0, 0, width, height, card, activity);
        return sprite;
    }

    public IEntity createCardSprite(final Card card, final float width, final float height) {
        final IEntity cardSprite = new CardFrame(0, 0, width, height, card, activity);
        return cardSprite;
    }

    public abstract void onGridCardsChange(int changeIndex, GridChangeAction changeAction);

    public abstract IEntity[] getCardGrids();

    public abstract IEntity[] getInGridCardSprites();

    public enum GridChangeAction {
        Add,
        Remove;
    }

}