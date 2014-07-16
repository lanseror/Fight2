package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;

public class PartyEditScene extends BaseScene {
    final Map<SceneEnum, Scene> scenes = this.activity.getScenes();
    private PhysicsHandler mPhysicsHandler;
    private final SurfaceScrollDetector scrollDetector = new SurfaceScrollDetector(new CardPackScrollDetectorListener());

    public PartyEditScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_EDIT_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createRealScreenF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, this.simulatedWidth - 100, 250);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                activity.getEngine().setScene(scenes.get(SceneEnum.Party));
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final Rectangle cardPack = new Rectangle(300, 180, 10500, 250, vbom) {
            private VelocityTracker velocityTracker;

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final MotionEvent motionEvent = pSceneTouchEvent.getMotionEvent();
                final int action = motionEvent.getAction();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                }
                velocityTracker.addMovement(motionEvent);
                Debug.e("Action:" + action);
                switch (action) {
                case MotionEvent.ACTION_UP:
                    velocityTracker.computeCurrentVelocity(220);
                    final float velocityX = velocityTracker.getXVelocity();
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }

                    PartyEditScene.this.mPhysicsHandler.setVelocityX(velocityX);
                    PartyEditScene.this.mPhysicsHandler.setAccelerationX(velocityX > 0 ? -1500 : 1500);
                    Debug.e("velocityX:" + velocityX);
                    break;

                }
                return true;
            }
        };
        cardPack.setColor(Color.TRANSPARENT);

        for (int i = 0; i < 50; i++) {
            final Sprite card = createRealScreenImageSprite(TextureEnum.TEST_CARD1, 10, 20);
            card.setWidth(120);
            card.setHeight(180);
            card.setPosition(60 + 130 * i, 120);
            cardPack.attachChild(card);
        }

        mPhysicsHandler = new PhysicsHandler(cardPack) {
            @Override
            protected void onUpdate(final float pSecondsElapsed, final IEntity pEntity) {
                final float accelerationX = this.getAccelerationX();
                if (this.isEnabled() && accelerationX != 0) {
                    final float velocityX = this.getVelocityX();
                    final BigDecimal bigVelocityX = BigDecimal.valueOf(velocityX);
                    final BigDecimal bigAccelerationX = BigDecimal.valueOf(accelerationX);
                    if (velocityX == 0) {
                        Debug.e("velocityX1:" + velocityX);
                        this.setAccelerationX(0);
                    } else if (Math.abs(bigVelocityX.divide(bigAccelerationX, 2, RoundingMode.HALF_UP)) < 2) {
                        Debug.e("velocityX2:" + velocityX);
                        this.setAccelerationX(0);
                        this.setVelocityX(0);
                    }
                }
                super.onUpdate(pSecondsElapsed, pEntity);
            }
        };
        this.registerUpdateHandler(mPhysicsHandler);
        this.attachChild(cardPack);
        this.registerTouchArea(cardPack);

        this.setTouchAreaBindingOnActionDownEnabled(true);
    }

    class CardPackScrollDetectorListener implements IScrollDetectorListener {

        @Override
        public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            // TODO Auto-generated method stub

        }

    }
}
