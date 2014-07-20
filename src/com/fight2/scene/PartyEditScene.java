package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ConfigHelper;
import com.fight2.util.TextureFactory;

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

        final Rectangle cardPack = new Rectangle(100 + 10500 * 0.5f, 180, 10500, 250, vbom) {
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
        cardPack.setColor(Color.TRANSPARENT);

        final Rectangle cardZoom = new Rectangle(180 + 240 * 0.5f, 180, 240, 250, vbom);
        cardZoom.setColor(Color.TRANSPARENT);
        this.attachChild(cardZoom);
        this.scrollDetector = new SurfaceScrollDetector(new CardScrollDetectorListener(cardPack, cardZoom));

        final int initCardX = 200;
        final int cardWidth = 120;
        final int cardY = 120;
        final int cardGap = 20;
        float appendX = initCardX;
        for (int i = 0; i < 50; i++) {
            final Sprite card = createCardSprite(TextureEnum.TEST_CARD1, 10, 20);
            card.setTag(i);
            card.setWidth(cardWidth);
            card.setHeight(180);
            card.setPosition(appendX, cardY);
            cardPack.attachChild(card);
            if (i == 0) {
                appendX += 1.5 * (cardGap + cardWidth);
            } else {
                appendX += cardGap + cardWidth;
            }

            this.registerUpdateHandler(new BaseEntityUpdateHandler(card) {
                @Override
                protected void onUpdate(final float pSecondsElapsed, final IEntity currentCard) {
                    final float cardZoomX = cardZoom.getX();
                    final float distance = cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
                    // final float cardZoomLeft = cardZoom.getWidth() * 0.5f + currentCard.getWidth() * 0.5f;
                    final IEntity pCardPack = currentCard.getParent();
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
                            if (previousCard.collidesWith(cardZoom)) {
                                isLeftmostZoomCard = false;
                            }
                        }

                        if (isLeftmostZoomCard) {
                            float cardLeft = initCardX - cardWidth + (cardGap + cardWidth) * currentCardIndex;
                            final int maxAdjustCard = 8;
                            for (int indexDff = 0; indexDff < maxAdjustCard; indexDff++) {
                                final int cardIndex = currentCardIndex + indexDff;
                                if (cardIndex >= pCardPack.getChildCount()) {
                                    break;
                                }
                                final IEntity adjustCard = pCardPack.getChildByIndex(cardIndex);
                                final float adjustWidth = adjustCard.getScaleX() * currentCard.getWidth();
                                final float adjustCardX = cardLeft + adjustWidth * 0.5f;
                                adjustCard.setX(adjustCardX);
                                cardLeft += adjustWidth + cardGap * adjustCard.getScaleX();
                            }
                        }

                    } else {
                        currentCard.setScale(1);
                    }
                }
            });
        }

        mPhysicsHandler = new PhysicsHandler(cardPack) {
            private final MoveFinishedListener moveFinishedListener = new MoveFinishedListener(cardPack, cardZoom);

            @Override
            protected void onUpdate(final float pSecondsElapsed, final IEntity pCardPack) {
                final float accelerationX = this.getAccelerationX();
                final float velocityX = this.getVelocityX();
                final float cardZoomX = cardZoom.getX();
                final IEntity firstCard = pCardPack.getFirstChild();
                final IEntity lastCard = pCardPack.getLastChild();
                final float firstCardX = toContainerOuterX(firstCard);
                final float lastCardX = toContainerOuterX(lastCard);
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
        };
        this.registerUpdateHandler(mPhysicsHandler);
        this.attachChild(cardPack);
        this.registerTouchArea(cardPack);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private float toContainerOuterX(final IEntity entry) {
        final IEntity container = entry.getParent();
        final float outerX = entry.getX() + container.getX() - container.getWidth() * 0.5f;
        return outerX;
    }

    class MoveFinishedListener {
        private final IEntity cardPack;
        private final IEntity cardZoom;

        public MoveFinishedListener(final IEntity cardPack, final IEntity cardZoom) {
            this.cardPack = cardPack;
            this.cardZoom = cardZoom;
        }

        public void update() {
            final float cardZoomX = cardZoom.getX();
            final int cardCount = cardPack.getChildCount();

            float diffZoomX = Float.MAX_VALUE;
            int minDiffIndex = 0;
            for (int index = 0; index < cardCount; index++) {
                final IEntity tempCard = cardPack.getChildByIndex(index);
                final float tempCardX = toContainerOuterX(tempCard);
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
                final float minDiffZoomX = toContainerOuterX(minDiffCard) - cardZoomX;

                Debug.e("cardPackX:" + cardPackX);
                Debug.e("minDiffIndex:" + minDiffIndex);
                Debug.e("minDiffZoomX:" + minDiffZoomX);
                final MoveModifier modifier = new MoveModifier(0.5f, cardPackX, cardPackY, cardPackX - minDiffZoomX, cardPackY, new IEntityModifierListener() {
                    @Override
                    public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        Debug.e("minDiffCard Started:" + minDiffCard.getScaleX());
                    }

                    @Override
                    public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        Debug.e("minDiffCard Finished:" + minDiffCard.getScaleX());
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

    protected Sprite createCardSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
            @Override
            protected void applyRotation(final GLState pGLState) {
                final float rotation = this.mRotation;

                if (rotation != 0) {
                    final float localRotationCenterX = this.mLocalRotationCenterX;
                    final float localRotationCenterY = this.mLocalRotationCenterY;

                    pGLState.translateModelViewGLMatrixf(localRotationCenterX, localRotationCenterY, 0);
                    /* Note we are applying rotation around the y-axis and not the z-axis anymore! */
                    pGLState.rotateModelViewGLMatrixf(-rotation, 0, 1, 0);
                    pGLState.translateModelViewGLMatrixf(-localRotationCenterX, -localRotationCenterY, 0);
                }
            }
        };
        return sprite;
    }

    class CardScrollDetectorListener implements IScrollDetectorListener {
        private final IEntity cardPack;
        private final float cardZoomX;

        public CardScrollDetectorListener(final IEntity cardPack, final IEntity cardZoom) {
            this.cardPack = cardPack;
            this.cardZoomX = cardZoom.getX();
        }

        @Override
        public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        }

        @Override
        public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {

            final IEntity firstCard = cardPack.getFirstChild();
            final IEntity lastCard = cardPack.getLastChild();
            final float firstCardX = firstCard.getX() + cardPack.getX() - cardPack.getWidth() * 0.5f;
            final float lastCardX = lastCard.getX() + cardPack.getX() - cardPack.getWidth() * 0.5f;
            if (firstCardX >= cardZoomX && pDistanceX > 0) {
                Debug.e("firstCardX > cardZoomX:" + firstCardX + "->" + cardZoomX + "->" + firstCard.getScaleX());
            } else if (lastCardX <= cardZoomX && pDistanceX < 0) {
                Debug.e("lastCardX < cardZoomX:" + lastCardX + "->" + cardZoomX);
            } else {
                cardPack.setX(cardPack.getX() + pDistanceX);
            }

        }

        @Override
        public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        }

    }
}