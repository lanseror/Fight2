package com.fight2.scene;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.entity.IEntity;

public class CardUpdateHandler extends BaseEntityUpdateHandler {
    /**
     * 
     */
    private final IEntity cardZoom;

    public CardUpdateHandler(final IEntity cardZoom, final IEntity card) {
        super(card);
        this.cardZoom = cardZoom;
    }

    @Override
    protected void onUpdate(final float pSecondsElapsed, final IEntity currentCard) {
        final float cardZoomX = this.cardZoom.getX();
        final float distance = this.cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
        // final float cardZoomLeft = cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
        final IEntity pCardPack = currentCard.getParent();
        if (pCardPack == null) {
            return;
        }
        final float x = currentCard.getX() + pCardPack.getX() - pCardPack.getWidth() * 0.5f;
        final float diff = Math.abs(x - cardZoomX);
        if (diff < distance) {
            final BigDecimal bdDiff = BigDecimal.valueOf(distance - diff);
            final BigDecimal bdDistance = BigDecimal.valueOf(distance);
            final float scale = 1 + bdDiff.divide(bdDistance, 4, RoundingMode.HALF_UP).floatValue();
            currentCard.setScale(scale);

            final int currentCardIndex = currentCard.getTag();
            boolean isLeftmostZoomCard = true;
            if (currentCardIndex > 0) {
                final IEntity previousCard = pCardPack.getChildByIndex(currentCardIndex - 1);
                if (previousCard.collidesWith(this.cardZoom)) {
                    isLeftmostZoomCard = false;
                }
            }

            if (isLeftmostZoomCard) {
                float cardLeft = currentCard.getX() + currentCard.getScaleX() * PartyEditScene.CARD_WIDTH * 0.5f + PartyEditScene.CARD_GAP
                        * currentCard.getScaleX();
                final int maxAdjustCard = pCardPack.getChildCount();
                for (int indexDff = 1; indexDff < maxAdjustCard; indexDff++) {
                    final int cardIndex = currentCardIndex + indexDff;
                    if (cardIndex >= pCardPack.getChildCount()) {
                        break;
                    }
                    final IEntity adjustCard = pCardPack.getChildByIndex(cardIndex);
                    final float adjustWidth = adjustCard.getScaleX() * currentCard.getWidth();
                    final float adjustCardX = cardLeft + adjustWidth * 0.5f;
                    adjustCard.setX(adjustCardX);
                    cardLeft += adjustWidth + PartyEditScene.CARD_GAP * adjustCard.getScaleX();
                }
            }

        } else {
            currentCard.setScale(1);
        }
    }
}