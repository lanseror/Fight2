package com.fight2.entity.engine.cardpack;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.fight2.constant.ConfigEnum;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.ConfigHelper;

public class CardPackTouchArea extends Rectangle {
    private final static int BASE_PX = 600;
    private final static int ACCELERATION = 1300;
    public final static int CARD_WIDTH = 110;
    public final static int CARD_GAP = 20;
    private final static float DISTANCE_15CARDS = (CARD_WIDTH + CARD_GAP) * 14.5f;
    private final float MAX_VELOCITY = BigDecimal.valueOf(Math.sqrt(2 * ACCELERATION * DISTANCE_15CARDS)).floatValue();
    private final int factor;
    private VelocityTracker velocityTracker;
    private final F2ScrollDetector scrollDetector;
    private final PhysicsHandler physicsHandler;
    private final CardPack cardPack;

    public CardPackTouchArea(final float pX, final float pY, final float pWidth, final float pHeight, final VertexBufferObjectManager vbom,
            final F2ScrollDetector scrollDetector, final PhysicsHandler physicsHandler, final CardPack cardPack) {
        super(pX, pY, pWidth, pHeight, vbom);
        this.setColor(Color.TRANSPARENT);
        this.scrollDetector = scrollDetector;
        this.physicsHandler = physicsHandler;
        this.cardPack = cardPack;
        final BigDecimal devicePX = BigDecimal.valueOf(ConfigHelper.getInstance().getFloat(ConfigEnum.X_DPI));
        final BigDecimal bdFactor = BigDecimal.valueOf(BASE_PX).divide(devicePX, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(430));
        factor = bdFactor.intValue();
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        scrollDetector.onTouchEvent(pSceneTouchEvent);
        scrollDetector.setSceneTouchEvent(pSceneTouchEvent);
        if (!cardPack.isScrolling() && pSceneTouchEvent.isActionUp()) {
            return false;
        }
        final MotionEvent motionEvent = pSceneTouchEvent.getMotionEvent();
        final int action = motionEvent.getAction();
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(motionEvent);
        // Debug.e("Action:" + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                physicsHandler.reset();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(factor);
                final float velocityX = velocityTracker.getXVelocity();
                final float velocityY = velocityTracker.getYVelocity();

                // Debug.e("velocityX:" + velocityX);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                final int flag = velocityX > 0 ? 1 : -1;
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    physicsHandler.setVelocityX(Math.abs(velocityX) > MAX_VELOCITY ? flag * MAX_VELOCITY : velocityX);
                }
                physicsHandler.setAccelerationX(velocityX > 0 ? -ACCELERATION : ACCELERATION);
                cardPack.setScrolling(false);
                break;

        }
        return true;
    }
}
