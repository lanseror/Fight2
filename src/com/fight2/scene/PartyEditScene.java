package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityComparator;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.CardUtils;
import com.fight2.util.ConfigHelper;
import com.fight2.util.ResourceManager;
import com.fight2.util.SpriteUtils;
import com.fight2.util.TextureFactory;

public class PartyEditScene extends BaseScene {
    private final static int BASE_PX = 600;
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 120;
    public final static int CARD_Y = 120;
    private final static float DISTANCE_15CARDS = (CARD_WIDTH + CARD_GAP) * 14.5f;
    private final static int ACCELERATION = 1300;
    private final float max_velocity;
    private final int factor;
    private F2ScrollDetector scrollDetector;
    int partyNumber;
    private final TextureEnum[] partyNumberTexts = { TextureEnum.PARTY_NUMBER_1, TextureEnum.PARTY_NUMBER_2, TextureEnum.PARTY_NUMBER_3 };
    final Sprite[] cardGrids = new Sprite[4];

    private PhysicsHandler mPhysicsHandler;
    private final Font mFont;
    final Map<Card, IEntity> removedCards = new HashMap<Card, IEntity>();
    final IEntity[] addedCards = new IEntity[4];

    final Rectangle cardZoom;
    final Rectangle cardPack;
    private final float topbarY = cameraHeight - TextureEnum.PARTY_TOPBAR.getHeight();
    private final float frameY = topbarY - TextureEnum.PARTY_EDIT_FRAME.getHeight() + 5;

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

        cardZoom = new Rectangle(180 + 240 * 0.5f, 180, 240, 250, vbom);
        cardPack = new Rectangle(300, 180, 21000, 250, vbom);
        init();
        updateScene();
    }

    @Override
    public void updateScene() {
        final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
        final Card[] partyCards = parties[partyNumber - 1].getCards();
        for (int i = 0; i < partyCards.length; i++) {
            final Card cardEntry = partyCards[i];
            if (cardEntry != null) {
                final IEntity card = createCardAvatarSprite(cardEntry, 10, 20);
                card.setPosition(cardGrids[i]);
                card.setUserData(cardEntry);
                this.attachChild(card);
                addedCards[i] = card;

                final Sprite removedCard = createRealScreenCardSprite(cardEntry, 10, 20);
                removedCard.setTag(i);
                removedCard.setWidth(CARD_WIDTH);
                removedCard.setHeight(180);
                removedCard.setPosition(0, CARD_Y);
                removedCard.setUserData(cardEntry);
                removedCards.put(cardEntry, removedCard);
                this.registerUpdateHandler(new CardUpdateHandler(cardZoom, removedCard));
            }

        }
        this.sortChildren();

    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.PARTY_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth(), topbarY);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.PARTY_EDIT_FRAME, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        final TextureEnum partyNumberText = partyNumberTexts[partyNumber - 1];
        final Sprite partyNumberSprite = createALBImageSprite(partyNumberText, 25, 140);
        frameSprite.attachChild(partyNumberSprite);
        cardPack.setColor(Color.TRANSPARENT);

        cardZoom.setColor(Color.TRANSPARENT);
        this.scrollDetector = new F2ScrollDetector(new CardPackScrollDetectorListener(this, cardPack, cardZoom));

        final TextureEnum gridEnum = TextureEnum.PARTY_EDIT_FRAME_GRID;
        final float gridGap = 153;
        final float gridStartX = 148;
        final float frameLeft = frameSprite.getX() - frameSprite.getWidth() * 0.5f;
        final float frameBottom = frameSprite.getY() - frameSprite.getHeight() * 0.5f;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion gridTexture = textureFactory.getAssetTextureRegion(gridEnum);
        final float gridWidth = gridEnum.getWidth();
        final float gridHeight = gridEnum.getHeight();
        for (int gridIndex = 0; gridIndex < 4; gridIndex++) {
            final int frameIndex = gridIndex;
            cardGrids[gridIndex] = new Sprite(frameLeft + gridStartX + gridGap * gridIndex, frameBottom + 161, gridWidth, gridHeight, gridTexture, vbom) {
                private IEntity movingCard = null;

                @Override
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    final float touchX = pSceneTouchEvent.getX();
                    final float touchY = pSceneTouchEvent.getY();
                    final MotionEvent motionEvent = pSceneTouchEvent.getMotionEvent();
                    final int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            movingCard = addedCards[frameIndex];
                            if (movingCard != null) {
                                movingCard.setZIndex(100);
                                PartyEditScene.this.sortChildren();
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (movingCard != null) {
                                movingCard.setPosition(touchX, touchY);
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (movingCard == null) {
                                break;
                            }
                            final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
                            final Card[] partyCards = parties[partyNumber - 1].getCards();
                            boolean collidedWithOthers = false;
                            for (int i = 0; i < cardGrids.length; i++) {
                                final IEntity cardFrame = cardGrids[i];
                                if (i != frameIndex && cardFrame.contains(touchX, touchY)) {
                                    collidedWithOthers = true;
                                    movingCard.setPosition(cardFrame);
                                    final IEntity toCard = addedCards[i];
                                    addedCards[frameIndex] = toCard;
                                    final Card tempPartyCard = partyCards[frameIndex];
                                    partyCards[frameIndex] = partyCards[i];
                                    partyCards[i] = tempPartyCard;
                                    if (toCard != null) {
                                        toCard.setPosition(this);
                                    }
                                    addedCards[i] = movingCard;
                                    break;
                                }
                            }
                            if (!collidedWithOthers) {
                                if (touchY < this.getY() - 50) {
                                    revertCardToCardPack(movingCard);
                                    partyCards[frameIndex] = null;
                                    activity.runOnUpdateThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            addedCards[frameIndex].detachSelf();
                                            addedCards[frameIndex] = null;
                                        }

                                    });

                                } else {
                                    movingCard.setPosition(this);
                                }
                            }
                            movingCard.setZIndex(IEntity.ZINDEX_DEFAULT);
                            PartyEditScene.this.sortChildren();
                            movingCard = null;
                            break;

                    }
                    return true;
                }
            };
            this.attachChild(cardGrids[gridIndex]);
            this.registerTouchArea(cardGrids[gridIndex]);
            cardGrids[gridIndex].setZIndex(10);
        }

        final float initCardX = cardZoom.getX() - (cardPack.getX() - 0.5f * cardPack.getWidth());

        final GameUserSession session = GameUserSession.getInstance();
        final List<Card> sessionCards = session.getCards();
        float appendX = initCardX;
        for (int i = 0; i < sessionCards.size(); i++) {
            final Card sessionCard = sessionCards.get(i);
            final Sprite card = createRealScreenCardSprite(sessionCard, 10, 20);
            card.setTag(i);
            card.setWidth(CARD_WIDTH);
            card.setHeight(180);
            card.setPosition(appendX, CARD_Y);
            card.setUserData(sessionCard);
            cardPack.attachChild(card);
            if (i == 0) {
                appendX += 1.5 * (CARD_GAP + CARD_WIDTH);
                cardZoom.setUserData(card);
            } else {
                appendX += CARD_GAP + CARD_WIDTH;
            }

            this.registerUpdateHandler(new CardUpdateHandler(cardZoom, card));
        }

        mPhysicsHandler = new PhysicsHandler(cardPack) {
            private final MoveFinishedListener moveFinishedListener = new MoveFinishedListener(cardPack, cardZoom);

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
        };
        this.registerUpdateHandler(mPhysicsHandler);

        final float touchAreaWidth = this.simulatedWidth - TextureEnum.COMMON_BACK_BUTTON_NORMAL.getWidth() - 80;
        final float touchAreaX = this.simulatedLeftX + touchAreaWidth * 0.5f;
        final Rectangle touchArea = new Rectangle(touchAreaX, 160, touchAreaWidth, 280, vbom) {
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

        this.registerTouchArea(touchArea);

        this.attachChild(touchArea);
        this.attachChild(cardPack);
        this.attachChild(cardZoom);

        final F2ButtonSprite backButton = createBackButton();
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite enhanceButton = createEnhanceButton();
        this.attachChild(enhanceButton);
        this.registerTouchArea(enhanceButton);

        final F2ButtonSprite switchButton = createSwitchButton();
        this.attachChild(switchButton);
        this.registerTouchArea(switchButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private F2ButtonSprite createBackButton() {
        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final boolean isSaveOk = CardUtils.saveParties();
                if (isSaveOk) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Party);
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "队伍保存失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return backButton;
    }

    private F2ButtonSprite createEnhanceButton() {
        final F2ButtonSprite enhanceButton = createALBF2ButtonSprite(TextureEnum.PARTY_ENHANCE_BUTTON, TextureEnum.PARTY_ENHANCE_BUTTON_PRESSED,
                this.simulatedRightX - 140, 220);
        enhanceButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "你点击了强化！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return enhanceButton;
    }

    private F2ButtonSprite createSwitchButton() {
        final F2ButtonSprite switchButton = createALBF2ButtonSprite(TextureEnum.PARTY_EDIT_SWITCH_BUTTON, TextureEnum.PARTY_EDIT_SWITCH_BUTTON_PRESSED,
                this.simulatedRightX - 140, 390);
        switchButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                try {
                    final Scene editScene = new PartyEditScene(activity, partyNumber++ % 3 + 1);
                    activity.getEngine().setScene(editScene);
                } catch (final IOException e) {
                    Debug.e(e);
                }
            }
        });
        return switchButton;
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

    protected Sprite createRealScreenCardSprite(final Card card, final float x, final float y) {
        final float width = CARD_WIDTH;
        final float height = 180;
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        Sprite sprite = null;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
        sprite = new Sprite(pX, pY, width, height, texture, vbom);

        return sprite;
    }

    protected Sprite createCardAvatarSprite(final Card card, final float x, final float y) {
        final float width = 135;
        final float height = 135;
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        Sprite sprite = null;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getTextureRegion(card.getAvatar());
        sprite = new Sprite(pX, pY, width, height, texture, vbom);

        return sprite;
    }

    protected void revertCardToCardPack(final IEntity inCardFrameSprite) {
        final Card replaceCardEntry = (Card) inCardFrameSprite.getUserData();
        final IEntity revertCard = removedCards.remove(replaceCardEntry);
        final IEntity focusedCard = (IEntity) cardZoom.getUserData();
        final float cardZoomX = cardZoom.getX();
        final float cardPackLeft = cardPack.getX() - cardPack.getWidth() * 0.5f;
        final float focusedX = cardZoomX - cardPackLeft;
        final float focusedLeftCardX = focusedX - 1.5f * PartyEditScene.CARD_WIDTH - PartyEditScene.CARD_GAP;
        final float focusedRightCardX = focusedX + 1.5f * (PartyEditScene.CARD_WIDTH + PartyEditScene.CARD_GAP);
        if (cardPack.getChildCount() == 0) {
            revertCard.setTag(0);
            revertCard.setPosition(focusedX, PartyEditScene.CARD_Y);
            cardZoom.setUserData(revertCard);
            cardPack.attachChild(revertCard);
        } else {
            cardPack.attachChild(revertCard);
            cardPack.sortChildren(new IEntityComparator() {
                @Override
                public int compare(final IEntity lhs, final IEntity rhs) {
                    final Card leftCard = (Card) lhs.getUserData();
                    final Card rightCard = (Card) rhs.getUserData();
                    return Integer.valueOf(leftCard.getId()).compareTo(rightCard.getId());
                }

            });
            // Find focusedCard index
            int focusedCardIndex = 0;
            for (; focusedCardIndex < cardPack.getChildCount(); focusedCardIndex++) {
                if (focusedCard == cardPack.getChildByIndex(focusedCardIndex)) {
                    break;
                }
            }
            focusedCard.setX(focusedX);
            focusedCard.setTag(focusedCardIndex);

            // Move left side cards
            float leftAdjustX = focusedLeftCardX;
            for (int i = focusedCardIndex - 1; i >= 0; i--) {
                final IEntity adjustCard = cardPack.getChildByIndex(i);
                adjustCard.setX(leftAdjustX);
                adjustCard.setTag(i);
                leftAdjustX -= PartyEditScene.CARD_WIDTH + PartyEditScene.CARD_GAP;
            }
            // Move right side cards
            float rightAdjustX = focusedRightCardX;
            for (int i = focusedCardIndex + 1; i < cardPack.getChildCount(); i++) {
                final IEntity adjustCard = cardPack.getChildByIndex(i);
                adjustCard.setX(rightAdjustX);
                adjustCard.setTag(i);
                rightAdjustX += PartyEditScene.CARD_WIDTH + PartyEditScene.CARD_GAP;
            }

        }

        revertCard.setAlpha(1f);
        revertCard.setScale(1f);
        GameUserSession.getInstance().getCards().add(replaceCardEntry);
    }
}