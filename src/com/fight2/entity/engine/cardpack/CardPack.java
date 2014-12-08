package com.fight2.entity.engine.cardpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityComparator;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.engine.CardFrame;
import com.fight2.scene.BaseCardPackScene;

public class CardPack extends Rectangle {
    private final Map<Card, CardFrame> removedCards = new HashMap<Card, CardFrame>();
    private final IEntity cardZoom;
    private final GameActivity activity;
    private final List<CardFrame> unmatchFilterCards = new ArrayList<CardFrame>();

    public CardPack(final float pX, final float pY, final float pWidth, final float pHeight, final GameActivity activity, final IEntity cardZoom) {
        super(pX, pY, pWidth, pHeight, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.cardZoom = cardZoom;
    }

    public void removedCard(final Card card, final CardFrame removedCardSprite) {
        removedCards.put(card, removedCardSprite);
    }

    public void filterCards(final IEntityMatcher entityMatcher) {
        for (int i = 0; i < this.getChildCount(); i++) {
            final CardFrame cardSprite = (CardFrame) this.getChildByIndex(i);
            if (!entityMatcher.matches(cardSprite)) {
                this.unmatchFilterCards.add(cardSprite);
            }
        }
        final CardFrame focusedCardSprite = (CardFrame) cardZoom.getUserData();
        final int focusedCardIndex = focusedCardSprite.getTag();
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                for (final CardFrame cardSprite : unmatchFilterCards) {
                    final int cardIndex = cardSprite.getTag();
                    if (cardIndex < focusedCardIndex) {
                        for (int cardPackIndex = cardIndex - 1; cardPackIndex >= 0; cardPackIndex--) {
                            final IEntity previousCard = getChildByIndex(cardPackIndex + 1);
                            final IEntity currentCard = getChildByIndex(cardPackIndex);
                            currentCard.setPosition(previousCard);
                        }
                    }
                    cardSprite.detachSelf();
                }
                for (int i = 0; i < getChildCount(); i++) {
                    final CardFrame cardSprite = (CardFrame) getChildByIndex(i);
                    cardSprite.setTag(i);
                }
            }

        });

    }

    public void revertCardToCardPack(final IEntity inCardFrameSprite) {
        final Card replaceCard = (Card) inCardFrameSprite.getUserData();
        final CardFrame revertCard = removedCards.remove(replaceCard);
        revertCard.revertCardAttributes();
        final IEntity focusedCard = (IEntity) cardZoom.getUserData();
        final float cardZoomX = cardZoom.getX();
        final float cardPackLeft = this.getX() - this.getWidth() * 0.5f;
        final float focusedX = cardZoomX - cardPackLeft;
        final float focusedLeftCardX = focusedX - 1.5f * BaseCardPackScene.CARD_WIDTH - BaseCardPackScene.CARD_GAP;
        final float focusedRightCardX = focusedX + 1.5f * (BaseCardPackScene.CARD_WIDTH + BaseCardPackScene.CARD_GAP);
        if (this.getChildCount() == 0) {
            revertCard.setTag(0);
            revertCard.setPosition(focusedX, BaseCardPackScene.CARD_Y);
            cardZoom.setUserData(revertCard);
            this.attachChild(revertCard);
        } else {
            this.attachChild(revertCard);
            this.sortChildren(new IEntityComparator() {
                @Override
                public int compare(final IEntity lhs, final IEntity rhs) {
                    final Card leftCard = (Card) lhs.getUserData();
                    final Card rightCard = (Card) rhs.getUserData();
                    return Integer.valueOf(leftCard.getId()).compareTo(rightCard.getId());
                }

            });
            // Find focusedCard index
            int focusedCardIndex = 0;
            for (; focusedCardIndex < this.getChildCount(); focusedCardIndex++) {
                if (focusedCard == this.getChildByIndex(focusedCardIndex)) {
                    break;
                }
            }
            focusedCard.setX(focusedX);
            focusedCard.setTag(focusedCardIndex);

            // Move left side cards
            float leftAdjustX = focusedLeftCardX;
            for (int i = focusedCardIndex - 1; i >= 0; i--) {
                final IEntity adjustCard = this.getChildByIndex(i);
                adjustCard.setX(leftAdjustX);
                adjustCard.setTag(i);
                leftAdjustX -= BaseCardPackScene.CARD_WIDTH + BaseCardPackScene.CARD_GAP;
            }
            // Move right side cards
            float rightAdjustX = focusedRightCardX;
            for (int i = focusedCardIndex + 1; i < this.getChildCount(); i++) {
                final IEntity adjustCard = this.getChildByIndex(i);
                adjustCard.setX(rightAdjustX);
                adjustCard.setTag(i);
                rightAdjustX += BaseCardPackScene.CARD_WIDTH + BaseCardPackScene.CARD_GAP;
            }

        }

        revertCard.setAlpha(1f);
        revertCard.setScale(1f);
        GameUserSession.getInstance().getCards().add(replaceCard);
    }
}
