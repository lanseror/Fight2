package com.fight2.entity.engine.cardpack;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.entity.IEntity;

import com.fight2.constant.SoundEnum;
import com.fight2.scene.BaseCardPackScene;
import com.fight2.util.F2SoundManager;

public class CardUpdateHandler extends BaseEntityUpdateHandler {
    public static float SCALE_FACTOR = 0.6f;
    private boolean beeped;
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
            final float scale = 1 + bdDiff.divide(bdDistance, 4, RoundingMode.HALF_UP).floatValue() * SCALE_FACTOR;
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
                float cardLeft = currentCard.getX() + currentCard.getScaleX() * BaseCardPackScene.CARD_WIDTH * 0.5f + BaseCardPackScene.CARD_GAP
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
                    cardLeft += adjustWidth + BaseCardPackScene.CARD_GAP * adjustCard.getScaleX();
                }
            }
            if (diff < 10 && !beeped) {
                F2SoundManager.getInstance().play(SoundEnum.CARDPACK);
                beeped = true;
            }

        } else {
            beeped = false;
            currentCard.setScale(1);
        }
    }
}