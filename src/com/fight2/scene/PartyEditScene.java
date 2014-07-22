package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Toast;

import com.fight2.F2ScrollDetector;
import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.util.ConfigHelper;
import com.fight2.util.TextureFactory;

public class PartyEditScene extends BaseScene {
    private final static int BASE_PX = 600;
    private final static int CARD_GAP = 20;
    private final static int CARD_WIDTH = 120;
    private final static float DISTANCE_15CARDS = (CARD_WIDTH + CARD_GAP) * 14.5f;
    private final static int ACCELERATION = 1300;
    private final float max_velocity;
    private final int factor;
    private F2ScrollDetector scrollDetector;
    private int partyNumber;
    private Text partyNumberText;
    private final Rectangle[] cardFrames = new Rectangle[4];

    final Map<SceneEnum, Scene> scenes = this.activity.getScenes();
    private PhysicsHandler mPhysicsHandler;
    private final Font mFont;

    public PartyEditScene(final GameActivity activity, final int partyNumber) throws IOException {
        super(activity);
        this.partyNumber = partyNumber;
        final BigDecimal devicePX = BigDecimal.valueOf(ConfigHelper.getInstance().getFloat(ConfigEnum.X_DPI));
        final BigDecimal bdFactor = BigDecimal.valueOf(BASE_PX).divide(devicePX, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(430));
        factor = bdFactor.intValue();
        max_velocity = BigDecimal.valueOf(Math.sqrt(2 * ACCELERATION * DISTANCE_15CARDS)).floatValue();
        this.mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                48, Color.WHITE_ARGB_PACKED_INT);
        this.mFont.load();

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

        final Sprite switchButton = createSwitchButton(this.simulatedWidth - 100, 380);
        this.attachChild(switchButton);
        this.registerTouchArea(switchButton);

        partyNumberText = new Text(110, 450, mFont, String.valueOf(partyNumber), vbom);
        this.attachChild(partyNumberText);

        for (int i = 0; i < 4; i++) {
            cardFrames[i] = new Rectangle(230 + 135 * i, 480, 120, 120, vbom);
            this.attachChild(cardFrames[i]);
        }

        final Rectangle cardPack = new Rectangle(300, 180, 21000, 250, vbom);
        cardPack.setColor(Color.TRANSPARENT);

        final Rectangle cardZoom = new Rectangle(180 + 240 * 0.5f, 180, 240, 250, vbom);
        cardZoom.setColor(Color.TRANSPARENT);
        this.scrollDetector = new F2ScrollDetector(new CardPackScrollDetectorListener(cardPack, cardZoom));

        final float initCardX = cardZoom.getX() - (cardPack.getX() - 0.5f * cardPack.getWidth());
        final int cardY = 120;

        float appendX = initCardX;
        for (int i = 0; i < 50; i++) {

            final Sprite card = createRealScreenImageSprite(TextureEnum.TEST_CARD1, 10, 20);
            card.setTag(i);
            card.setWidth(CARD_WIDTH);
            card.setHeight(180);
            card.setPosition(appendX, cardY);
            cardPack.attachChild(card);
            if (i == 0) {
                appendX += 1.5 * (CARD_GAP + CARD_WIDTH);
                cardZoom.setUserData(card);
            } else {
                appendX += CARD_GAP + CARD_WIDTH;
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
                            float cardLeft = currentCard.getX() + currentCard.getScaleX() * CARD_WIDTH * 0.5f + CARD_GAP * currentCard.getScaleX();
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
                                cardLeft += adjustWidth + CARD_GAP * adjustCard.getScaleX();
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

        final Rectangle touchArea = new Rectangle(1136 * 0.5f, 160, 1136, 320, vbom) {
            private VelocityTracker velocityTracker;

            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(pSceneTouchEvent);
                scrollDetector.setSceneTouchEvent(pSceneTouchEvent);
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
                        final float velocityY = velocityTracker.getYVelocity();

                        // Debug.e("velocityX:" + velocityX);
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            velocityTracker = null;
                        }
                        final int flag = velocityX > 0 ? 1 : -1;
                        if (Math.abs(velocityX) > Math.abs(velocityY)) {
                            PartyEditScene.this.mPhysicsHandler.setVelocityX(Math.abs(velocityX) > max_velocity ? flag * max_velocity : velocityX);
                        }
                        PartyEditScene.this.mPhysicsHandler.setAccelerationX(velocityX > 0 ? -ACCELERATION : ACCELERATION);
                        break;

                }
                return true;
            }
        };
        touchArea.setColor(Color.BLACK);

        this.registerTouchArea(backButton);
        this.registerTouchArea(touchArea);

        this.attachChild(touchArea);
        this.attachChild(cardPack);
        this.attachChild(cardZoom);
        this.attachChild(backButton);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private Sprite createSwitchButton(final float x, final float y) {
        final TextureEnum textureEnum = TextureEnum.PARTY_EDIT_SWITCH_BUTTON;
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
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    partyNumberText.setText(String.valueOf(partyNumber++ % 3 + 1));
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    private float toContainerOuterX(final IEntity entry) {
        final IEntity container = entry.getParent();
        final float outerX = entry.getX() + container.getX() - container.getWidth() * 0.5f;
        return outerX;
    }

    private float toContainerOuterY(final IEntity entry) {
        final IEntity container = entry.getParent();
        final float outerY = entry.getY() + container.getY() - container.getHeight() * 0.5f;
        return outerY;
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
                cardZoom.setUserData(minDiffCard);
                final float minDiffZoomX = toContainerOuterX(minDiffCard) - cardZoomX;

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
                    moveDistance = toContainerOuterX(leftmostZoomCard) - (cardZoomX - cardZoom.getWidth() * 0.5f - CARD_GAP - 0.5f * CARD_WIDTH);
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

    class CardPackScrollDetectorListener implements IScrollDetectorListener {
        private final IEntity cardPack;
        private final IEntity cardZoom;;
        private final float cardZoomX;
        private float initPointerID;
        private float initX;
        private float initY;
        private float initDistanceX;
        private float initDistanceY;
        private Sprite copyCard;

        public CardPackScrollDetectorListener(final IEntity cardPack, final IEntity cardZoom) {
            this.cardPack = cardPack;
            this.cardZoom = cardZoom;
            this.cardZoomX = cardZoom.getX();
        }

        @Override
        public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            initPointerID = pPointerID;
            final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
            final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
            initX = touchEvent.getX();
            initY = touchEvent.getY();
            initDistanceX = pDistanceX;
            initDistanceY = pDistanceY;
            final IEntity focusedCard = (IEntity) cardZoom.getUserData();
            if (focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {
                focusedCard.setAlpha(0.5f);
                final Card[] partyCards = GameUserSession.getInstance().getParties()[partyNumber - 1];
                boolean hasPosition = false;
                for (int partyCardIndex = 0; partyCardIndex < partyCards.length; partyCardIndex++) {
                    final Card partyCard = partyCards[partyCardIndex];
                    if (partyCard == null) {
                        hasPosition = true;
                        copyCard = createRealScreenImageSprite(TextureEnum.TEST_CARD1, 10, 20);
                        copyCard.setPosition(toContainerOuterX(focusedCard), toContainerOuterY(focusedCard));
                        copyCard.setWidth(focusedCard.getWidth());
                        copyCard.setHeight(focusedCard.getHeight());
                        PartyEditScene.this.attachChild(copyCard);
                        break;
                    }
                }
                if (!hasPosition) {
                    copyCard = null;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "队伍已满！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        @Override
        public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            final IEntity focusedCard = (IEntity) cardZoom.getUserData();
            if (pPointerID == initPointerID && copyCard != null && focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {
                copyCard.setX(copyCard.getX() + pDistanceX);
                copyCard.setY(copyCard.getY() - pDistanceY);
            } else if (Math.abs(pDistanceX) > Math.abs(pDistanceY)) {
                final IEntity firstCard = cardPack.getFirstChild();
                final IEntity lastCard = cardPack.getLastChild();
                final float firstCardX = firstCard.getX() + cardPack.getX() - cardPack.getWidth() * 0.5f;
                final float lastCardX = lastCard.getX() + cardPack.getX() - cardPack.getWidth() * 0.5f;
                if (firstCardX >= cardZoomX && pDistanceX > 0) {
                    // Debug.e("firstCardX > cardZoomX:" + firstCardX + "->" + cardZoomX + "->" + firstCard.getScaleX());
                } else if (lastCardX <= cardZoomX && pDistanceX < 0) {
                    // Debug.e("lastCardX < cardZoomX:" + lastCardX + "->" + cardZoomX);
                } else {
                    cardPack.setX(cardPack.getX() + pDistanceX);
                }
            }
        }

        @Override
        public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
            final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
            final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
            final float finishedY = touchEvent.getY();
            final IEntity focusedCard = (IEntity) cardZoom.getUserData();
            if (focusedCard.getScaleX() > 1.8) {
                if (pPointerID == initPointerID && copyCard != null && focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {
                    if (finishedY < toContainerOuterY(focusedCard)) {
                        final MoveModifier revertModifier = new MoveModifier(0.1f, copyCard.getX(), copyCard.getY(), toContainerOuterX(focusedCard),
                                toContainerOuterY(focusedCard), new IEntityModifierListener() {
                                    @Override
                                    public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                                        // Debug.e("cardZoomX:" + cardZoomX);
                                        // Debug.e("minDiffCardX Started:" + toContainerOuterX(minDiffCard));
                                        // Debug.e("minDiffCard Started:" + minDiffCard.getScaleX());
                                    }

                                    @Override
                                    public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                                        pItem.detachSelf();
                                        copyCard = null;
                                        focusedCard.setAlpha(1);
                                        // Debug.e("cardZoomX:" + cardZoomX);
                                        // Debug.e("minDiffCardX Finished:" + toContainerOuterX(minDiffCard));
                                        // Debug.e("minDiffCard Finished:" + minDiffCard.getScaleX());
                                    }

                                });
                        activity.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                copyCard.clearEntityModifiers();
                                copyCard.registerEntityModifier(revertModifier);
                            }
                        });
                    } else {
                        final Card[] partyCards = GameUserSession.getInstance().getParties()[partyNumber - 1];
                        for (int partyCardIndex = 0; partyCardIndex < partyCards.length; partyCardIndex++) {
                            final Card partyCard = partyCards[partyCardIndex];
                            if (partyCard == null) {
                                final Card cardEntry = new Card();
                                cardEntry.setImage("card/card1.jpg");
                                partyCards[partyCardIndex] = cardEntry;
                                final Rectangle cardFrame = cardFrames[partyCardIndex];
                                final MoveModifier modifier = new MoveModifier(0.2f, copyCard.getX(), copyCard.getY(), cardFrame.getX(), cardFrame.getY(),
                                        new IEntityModifierListener() {
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
                                        copyCard.clearEntityModifiers();
                                        copyCard.registerEntityModifier(modifier);
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
            }
        }

    }
}