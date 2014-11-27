package com.fight2.entity.engine.cardpack;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;

import com.fight2.util.SpriteUtils;

public class CardPackPhysicsHandler extends PhysicsHandler {
    private final MoveFinishedListener moveFinishedListener;
    private final IEntity cardZoom;

    public CardPackPhysicsHandler(final CardPack cardPack, final IEntity cardZoom, final MoveFinishedListener moveFinishedListener) {
        super(cardPack);
        this.cardZoom = cardZoom;
        this.moveFinishedListener = moveFinishedListener;
    }

    @Override
    protected void onUpdate(final float pSecondsElapsed, final IEntity pCardPack) {
        if (pCardPack.getChildCount() != 0) {
            final float accelerationX = this.getAccelerationX();
            final float velocityX = this.getVelocityX();
            final float cardZoomX = cardZoom.getX();
            final IEntity firstCard = pCardPack.getFirstChild();
            final IEntity lastCard = pCardPack.getLastChild();
            final float firstCardX = SpriteUtils.toContainerOuterX(firstCard);
            final float lastCardX = SpriteUtils.toContainerOuterX(lastCard);
            if (this.isEnabled() && accelerationX != 0) {
                final float testVelocityX = mVelocityX + accelerationX * pSecondsElapsed;
                if (velocityX == 0) {
                    this.reset();
                    moveFinishedListener.update();
                } else if (Math.abs(mVelocityX) + Math.abs(testVelocityX) > Math.abs(mVelocityX + testVelocityX)) {
                    // This make sure it will not go back automatically.
                    this.reset();
                    moveFinishedListener.update();
                } else if (firstCardX >= cardZoomX || lastCardX <= cardZoomX) {
                    this.reset();
                    moveFinishedListener.update();
                }
            }

            super.onUpdate(pSecondsElapsed, pCardPack);
        }
    }
}
