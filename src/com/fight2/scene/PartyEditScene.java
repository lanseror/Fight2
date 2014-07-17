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
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.adt.color.Color;

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
    private SurfaceScrollDetector scrollDetector;

    final Map<SceneEnum, Scene> scenes = this.activity.getScenes();
    private PhysicsHandler mPhysicsHandler;

    public PartyEditScene(final GameActivity activity) throws IOException {
        super(activity);
        final BigDecimal devicePX = BigDecimal.valueOf(ConfigHelper.getInstance().getFloat(ConfigEnum.X_DPI));
        factor = BigDecimal.valueOf(BASE_PX).divide(devicePX, 2, RoundingMode.HALF_UP).intValue() * 430;

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

        final int cardPackLeftX = 100;
        final Rectangle cardPack = new Rectangle(cardPackLeftX + 10500 * 0.5f, 180, 10500, 250, vbom) {
            private VelocityTracker velocityTracker;

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(pSceneTouchEvent);
                final MotionEvent motionEvent = pSceneTouchEvent.getMotionEvent();
                final int action = motionEvent.getAction();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                }
                velocityTracker.addMovement(motionEvent);
                // Debug.e("Action:" + action);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        PartyEditScene.this.mPhysicsHandler.reset();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        velocityTracker.computeCurrentVelocity(factor);
                        final float velocityX = velocityTracker.getXVelocity();
                        // Debug.e("velocityX:" + velocityX);
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
        this.scrollDetector = new SurfaceScrollDetector(new CardScrollDetectorListener(cardPack));
        cardPack.setColor(Color.TRANSPARENT);

        final Rectangle cardZoom = new Rectangle(180 + 240 * 0.5f, 180, 240, 250, vbom);
        cardZoom.setColor(Color.BLACK);
        this.attachChild(cardZoom);

        final int cardStartX = 200;
        final int cardWidth = 120;
        final int cardY = 120;
        final int cardGap = 10;
        int cardAppendX = cardStartX;
        for (int i = 0; i < 50; i++) {
            final Sprite card = createRealScreenImageSprite(TextureEnum.TEST_CARD1, 10, 20);
            card.setTag(i);
            card.setWidth(cardWidth);
            card.setHeight(180);
            card.setPosition(cardAppendX, cardY);
            cardPack.attachChild(card);

            if (i == 0) {
                cardAppendX += 2 * cardGap + 1.5f * cardWidth;
            } else {
                cardAppendX += cardGap + cardWidth;
            }

            this.registerUpdateHandler(new BaseEntityUpdateHandler(card) {
                private int leftmostZoomCardIndex = 0;
                private final int zoomGap = 2 * cardGap;

                @Override
                protected void onUpdate(final float pSecondsElapsed, final IEntity currentCard) {
                    final float cardZoomX = cardZoom.getX();
                    final float distance = cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
                    // final float cardZoomLeft = cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
                    final IEntity pCardPack = currentCard.getParent();
                    final float x = currentCard.getX() + pCardPack.getX() - pCardPack.getWidth() * 0.5f;
                    final float diff = Math.abs(x - cardZoomX);
                    final int currentCardIndex = currentCard.getTag();
                    if (diff < distance) {
                        final BigDecimal bdDiff = BigDecimal.valueOf(distance - diff);
                        final BigDecimal bdDistance = BigDecimal.valueOf(distance);
                        final float scale = 1 + bdDiff.divide(bdDistance, 4, RoundingMode.HALF_UP).floatValue();
                        currentCard.setScale(scale);

                        boolean isLeftmostZoomCard = true;
                        if (currentCardIndex > 0) {
                            final IEntity previousCard = pCardPack.getChildByIndex(currentCardIndex - 1);
                            if (previousCard.collidesWith(cardZoom)) {
                                isLeftmostZoomCard = false;
                            }
                        }

                        if (isLeftmostZoomCard) {
                            leftmostZoomCardIndex = currentCardIndex;

                            float cardLeftWithGap = currentCard.getX() + (cardGap + cardWidth * 0.5f) * currentCard.getScaleX();
                            final int maxAdjustCard = 10;
                            for (int indexDff = 1; indexDff < maxAdjustCard; indexDff++) {
                                final int cardIndex = currentCardIndex + indexDff;
                                if (cardIndex >= pCardPack.getChildCount()) {
                                    break;
                                }
                                final IEntity adjustCard = pCardPack.getChildByIndex(cardIndex);
                                final float adjustWidth = adjustCard.getScaleX() * currentCard.getWidth();
                                final float adjustCardX = cardLeftWithGap + adjustWidth * 0.5f;
                                adjustCard.setX(adjustCardX);
                                cardLeftWithGap += adjustWidth + cardGap * adjustCard.getScaleX();
                            }
                        } else {
                            final IEntity leftmostZoomCard = pCardPack.getChildByIndex(leftmostZoomCardIndex);
                            final float zoomCardCap = currentCard.getX() - leftmostZoomCard.getX() - cardWidth * leftmostZoomCard.getScaleX() - cardWidth
                                    * scale;
                            if (zoomCardCap < zoomGap) {
                                currentCard.setX(currentCard.getX() + 1f);
                            }
                        }
                    } else {
                        final int indexDiff = leftmostZoomCardIndex - currentCardIndex;
                        final IEntity leftmostZoomCard = pCardPack.getChildByIndex(leftmostZoomCardIndex);
                        final float leftmostZoomCardLeft = leftmostZoomCard.getX() - zoomGap - cardWidth * leftmostZoomCard.getScaleX() * 0.5f;
                        final float leftmostZoomCardRight = leftmostZoomCard.getX() + zoomGap + cardWidth * leftmostZoomCard.getScaleX() * 0.5f;

                        if (indexDiff > 2 || indexDiff < -8) {
                            currentCard.setX(cardStartX + cardGap + cardWidth * 0.5f + (cardGap + cardWidth) * currentCardIndex);
                        } else if (indexDiff == 1) {
                            currentCard.setX(leftmostZoomCardLeft - cardWidth * 0.5f);
                        } else if (indexDiff == 2) {
                            currentCard.setX(leftmostZoomCardLeft - cardWidth * 0.5f - cardWidth - cardGap);
                        } else if (indexDiff == -1) {
                            currentCard.setX(leftmostZoomCardRight + cardWidth * 0.5f);
                        } else if (indexDiff < -1) {
                            final IEntity previousCardCard = pCardPack.getChildByIndex(currentCardIndex - 1);
                            final float previousCardScale = previousCardCard.getScaleX();
                            float calculateX;
                            if (previousCardScale > 1.01f) {
                                calculateX = previousCardCard.getX() + zoomGap + cardWidth * previousCardScale * 0.5f + cardWidth * 0.5f;
                            } else {
                                calculateX = previousCardCard.getX() + cardGap + cardWidth;
                            }
                            currentCard.setX(calculateX);
                        }

                        currentCard.setScale(1);
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

    class CardScrollDetectorListener implements IScrollDetectorListener {
        private final IEntity cardPack;

        public CardScrollDetectorListener(final IEntity cardPack) {
            this.cardPack = cardPack;
        }

        @Override
        public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        }

        @Override
        public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            cardPack.setX(cardPack.getX() + pDistanceX);

        }

        @Override
        public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        }

    }
}
