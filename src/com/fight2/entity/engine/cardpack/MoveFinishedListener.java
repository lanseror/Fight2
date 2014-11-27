package com.fight2.entity.engine.cardpack;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.modifier.IModifier;

import com.fight2.GameActivity;
import com.fight2.util.SpriteUtils;

public class MoveFinishedListener {
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 110;
    private final CardPack cardPack;
    private final IEntity cardZoom;
    private final GameActivity activity;

    public MoveFinishedListener(final CardPack cardPack, final IEntity cardZoom, final GameActivity activity) {
        this.cardPack = cardPack;
        this.cardZoom = cardZoom;
        this.activity = activity;
    }

    public void update() {
        final float cardZoomX = cardZoom.getX();
        final int cardCount = cardPack.getChildCount();

        float diffZoomX = Float.MAX_VALUE;
        int minDiffIndex = 0;
        for (int index = 0; index < cardCount; index++) {
            final IEntity tempCard = cardPack.getChildByIndex(index);
            final float tempCardX = SpriteUtils.toContainerOuterX(tempCard);
            final float tempDiffZoomX = tempCardX - cardZoomX;
            if (Math.abs(tempDiffZoomX) < diffZoomX) {
                diffZoomX = Math.abs(tempDiffZoomX);
                minDiffIndex = index;
            }
        }
        if (diffZoomX != 0) {
            final float cardPackX = cardPack.getX();
            final float cardPackY = cardPack.getY();
            final IEntity minDiffCard = cardPack.getChildByIndex(minDiffIndex);
            cardZoom.setUserData(minDiffCard);
            final float minDiffZoomX = SpriteUtils.toContainerOuterX(minDiffCard) - cardZoomX;

            IEntity leftmostZoomCard = minDiffCard;
            boolean isLeftmostZoomCard = true;
            if (minDiffIndex > 0) {
                final IEntity previousCard = cardPack.getChildByIndex(minDiffIndex - 1);
                if (previousCard.collidesWith(cardZoom)) {
                    leftmostZoomCard = previousCard;
                    isLeftmostZoomCard = false;
                }
            }
            float moveDistance = minDiffZoomX;
            if (!isLeftmostZoomCard) {
                moveDistance = SpriteUtils.toContainerOuterX(leftmostZoomCard) - (cardZoomX - cardZoom.getWidth() * 0.5f - CARD_GAP - 0.5f * CARD_WIDTH);
            }

            // Debug.e("isLeftmostZoomCard:" + isLeftmostZoomCard);
            // Debug.e("Shoud move:" + moveDistance);
            final MoveModifier modifier = new MoveModifier(0.1f, cardPackX, cardPackY, cardPackX - moveDistance, cardPackY, new IEntityModifierListener() {
                @Override
                public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                    // Debug.e("cardZoomX:" + cardZoomX);
                    // Debug.e("minDiffCardX Started:" + toContainerOuterX(minDiffCard));
                    // Debug.e("minDiffCard Started:" + minDiffCard.getScaleX());
                }

                @Override
                public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                    // Debug.e("cardZoomX:" + cardZoomX);
                    // Debug.e("minDiffCardX Finished:" + toContainerOuterX(minDiffCard));
                    // Debug.e("minDiffCard Finished:" + minDiffCard.getScaleX());
                }

            });
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    cardPack.clearEntityModifiers();
                    cardPack.registerEntityModifier(modifier);
                }
            });
        }
    }
}