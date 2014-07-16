package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ConfigHelper;

public class PartyEditScene extends BaseScene {
    private final static int BASE_PX = 600;
    private final int factor;

    final Map<SceneEnum, Scene> scenes = this.activity.getScenes();
    private PhysicsHandler mPhysicsHandler;

    public PartyEditScene(final GameActivity activity) throws IOException {
        super(activity);
        final BigDecimal devicePX = BigDecimal.valueOf(ConfigHelper.getInstance().getInt(ConfigEnum.DPI));
        factor = BigDecimal.valueOf(BASE_PX).divide(devicePX, 2, RoundingMode.HALF_UP).intValue() * 300;
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
                // Debug.e("Action:" + action);
                switch (action) {
                case MotionEvent.ACTION_UP:
                    velocityTracker.computeCurrentVelocity(factor);
                    final float velocityX = velocityTracker.getXVelocity();
                    Debug.e("velocityX:" + velocityX);
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }

                    PartyEditScene.this.mPhysicsHandler.setVelocityX(velocityX);
                    PartyEditScene.this.mPhysicsHandler.setAccelerationX(velocityX > 0 ? -1300 : 1300);
                    break;

                }
                return true;
            }
        };
        cardPack.setColor(Color.TRANSPARENT);

        final Rectangle cardZoom = new Rectangle(300, 180, 240, 250, vbom);
        cardZoom.setColor(Color.BLACK);
        this.attachChild(cardZoom);

        for (int i = 0; i < 50; i++) {
            final Sprite card = createRealScreenImageSprite(TextureEnum.TEST_CARD1, 10, 20);
            card.setWidth(120);
            card.setHeight(180);
            card.setPosition(60 + 130 * i, 120);
            cardPack.attachChild(card);

            this.registerUpdateHandler(new BaseEntityUpdateHandler(card) {
                @Override
                protected void onUpdate(final float pSecondsElapsed, final IEntity pCard) {
                    final float distance = cardZoom.getWidth() * 0.5f + pCard.getWidth() * 0.5f;
                    final IEntity pCardPackp = pCard.getParent();
                    final float x = pCard.getX() + pCardPackp.getX() - pCardPackp.getWidth() * 0.5f;
                    final float diff = Math.abs(x - cardZoom.getX());
                    if (diff < distance) {
                        final BigDecimal bdDiff = BigDecimal.valueOf(distance - diff);
                        final BigDecimal bdDistance = BigDecimal.valueOf(distance);
                        pCard.setScale(1 + bdDiff.divide(bdDistance, 4, RoundingMode.HALF_UP).floatValue());
                    } else {
                        pCard.setScale(1);
                    }
                }
            });
        }

        mPhysicsHandler = new PhysicsHandler(cardPack) {
            @Override
            protected void onUpdate(final float pSecondsElapsed, final IEntity pEntity) {
                final float accelerationX = this.getAccelerationX();
                if (this.isEnabled() && accelerationX != 0) {
                    final float velocityX = this.getVelocityX();
                    final float testVelocityX = mVelocityX + accelerationX * pSecondsElapsed;
                    if (velocityX == 0) {
                        this.reset();
                    } else if (Math.abs(mVelocityX) + Math.abs(testVelocityX) > Math.abs(mVelocityX + testVelocityX)) {
                        this.reset();
                    }
                }
                super.onUpdate(pSecondsElapsed, pEntity);
            }
        };
        this.registerUpdateHandler(mPhysicsHandler);
        this.attachChild(cardPack);
        this.registerTouchArea(cardPack);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }
}
