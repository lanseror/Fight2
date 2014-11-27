package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.view.MotionEvent;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class PartyEditScene extends BaseScene {
    public final static int CARD_GAP = 20;
    public final static int CARD_WIDTH = 110;
    public final static int CARD_HEIGHT = 165;
    public final static int CARD_Y = 120;
    private F2ScrollDetector scrollDetector;
    int partyNumber;
    private final TextureEnum[] partyNumberTexts = { TextureEnum.PARTY_NUMBER_1, TextureEnum.PARTY_NUMBER_2, TextureEnum.PARTY_NUMBER_3 };
    final Sprite[] cardGrids = new Sprite[4];

    private PhysicsHandler physicsHandler;
    private final Font hpatkFont;
    final IEntity[] addedCards = new IEntity[4];

    final Rectangle cardZoom;
    private final CardPack cardPack;
    private final float topbarY = cameraHeight - TextureEnum.PARTY_TOPBAR.getHeight();
    private final float frameY = topbarY - TextureEnum.PARTY_EDIT_FRAME.getHeight() + 5;

    private final Text partyInfoHpText;
    private final Text partyInfoAtkText;

    private final Text partyHpText;
    private final Text partyAtkText;

    private final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
    private final Set<Integer> inPartyCards = GameUserSession.getInstance().getInPartyCards();

    public PartyEditScene(final GameActivity activity, final int partyNumber) throws IOException {
        super(activity);
        this.partyNumber = partyNumber;
        this.hpatkFont = ResourceManager.getInstance().getFont(FontEnum.Main, 20);

        cardZoom = new Rectangle(250 + CARD_WIDTH * 0.7f, 170, CARD_WIDTH * 1.4f, CARD_HEIGHT * 1.4f, vbom);
        cardPack = new CardPack(300, 170, 21000, 250, vbom, cardZoom);
        partyInfoHpText = new Text(this.simulatedLeftX + 360, topbarY + 48, hpatkFont, "0123456789", vbom);
        partyInfoAtkText = new Text(this.simulatedLeftX + 600, topbarY + 48, hpatkFont, "0123456789", vbom);
        partyHpText = new Text(this.simulatedLeftX + 165, frameY + 63, hpatkFont, "0123456789", vbom);
        partyAtkText = new Text(this.simulatedLeftX + 310, frameY + 63, hpatkFont, "0123456789", vbom);
        init();
        updateScene();
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
        final Card[] partyCards = parties[partyNumber - 1].getCards();
        for (int i = 0; i < partyCards.length; i++) {
            final Card card = partyCards[i];
            if (card != null) {
                final IEntity cardSprite = createCardAvatarSprite(card, 10, 20);
                cardSprite.setPosition(cardGrids[i]);
                cardSprite.setUserData(card);
                this.attachChild(cardSprite);
                addedCards[i] = cardSprite;

                final IEntity removedCardSprite = new CardFrame(0, CARD_Y, CARD_WIDTH, CARD_HEIGHT, card, activity);
                removedCardSprite.setTag(i);
                removedCardSprite.setUserData(card);
                cardPack.removedCard(card, removedCardSprite);
                this.registerUpdateHandler(new CardUpdateHandler(cardZoom, removedCardSprite));
            }

        }
        this.sortChildren();
        this.updatePartyHpAtk();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.PARTY_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.PARTY_EDIT_FRAME, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        this.attachChild(partyInfoHpText);
        this.attachChild(partyInfoAtkText);
        this.attachChild(partyHpText);
        this.attachChild(partyAtkText);

        final TextureEnum partyNumberText = partyNumberTexts[partyNumber - 1];
        final Sprite partyNumberSprite = createALBImageSprite(partyNumberText, 25, 140);
        frameSprite.attachChild(partyNumberSprite);
        cardPack.setColor(Color.TRANSPARENT);
        cardZoom.setColor(Color.TRANSPARENT);
        this.scrollDetector = new F2ScrollDetector(new CardPackScrollDetectorListener(this, activity, cardPack, cardZoom));

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
                                    inPartyCards.remove(partyCards[frameIndex].getTemplateId());
                                    cardPack.revertCardToCardPack(movingCard);
                                    partyCards[frameIndex] = null;
                                    calculatePartyHpAtk();
                                    updatePartyHpAtk();
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

        // Insert cards to card pack.
        final float initCardX = cardZoom.getX() - (cardPack.getX() - 0.5f * cardPack.getWidth());
        final GameUserSession session = GameUserSession.getInstance();
        final List<Card> sessionCards = session.getCards();
        float appendX = initCardX;
        for (int i = 0; i < sessionCards.size(); i++) {
            final Card sessionCard = sessionCards.get(i);
            final IEntity card = new CardFrame(appendX, CARD_Y, CARD_WIDTH, CARD_HEIGHT, sessionCard, activity);
            card.setTag(i);
            card.setWidth(CARD_WIDTH);
            card.setHeight(CARD_HEIGHT);
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

        final MoveFinishedListener moveFinishedListener = new MoveFinishedListener(cardPack, cardZoom, activity);
        physicsHandler = new CardPackPhysicsHandler(cardPack, cardZoom, moveFinishedListener);
        this.registerUpdateHandler(physicsHandler);

        final float touchAreaWidth = this.simulatedWidth - TextureEnum.COMMON_BACK_BUTTON_NORMAL.getWidth() - 80;
        final float touchAreaX = this.simulatedLeftX + touchAreaWidth * 0.5f;
        final Rectangle touchArea = new CardPackTouchArea(touchAreaX, 160, touchAreaWidth, 280, vbom, scrollDetector, physicsHandler);
        this.registerTouchArea(touchArea);
        this.attachChild(touchArea);
        this.attachChild(cardPack);
        this.attachChild(cardZoom);

        // Add cover and buttons.
        final Sprite leftCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_LEFT, 0, 80);
        final Sprite rightCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_RIGHT, this.cameraWidth - TextureEnum.PARTY_EDIT_COVER_RIGHT.getWidth(), 80);
        this.attachChild(leftCover);
        this.attachChild(rightCover);

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
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final Card[] cards = parties[0].getCards();
                if (cards[0] == null) {
                    alert("你必须要有一个领军人物！");
                } else {
                    final boolean isSaveOk = CardUtils.saveParties();
                    if (isSaveOk) {
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Party);
                    } else {
                        alert("队伍保存失败！");
                    }
                }
            }
        });
        return backButton;
    }

    private F2ButtonSprite createEnhanceButton() {
        final F2ButtonSprite enhanceButton = createALBF2ButtonSprite(TextureEnum.PARTY_ENHANCE_BUTTON, TextureEnum.PARTY_ENHANCE_BUTTON_PRESSED,
                this.simulatedRightX - 135, 220);
        enhanceButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                alert("你点击了强化！");
            }
        });
        return enhanceButton;
    }

    private F2ButtonSprite createSwitchButton() {
        final F2ButtonSprite switchButton = createALBF2ButtonSprite(TextureEnum.PARTY_EDIT_SWITCH_BUTTON, TextureEnum.PARTY_EDIT_SWITCH_BUTTON_PRESSED,
                this.simulatedRightX - 135, 390);
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

    public void calculatePartyHpAtk() {
        final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
        int partyInfoHp = 0;
        int partyInfoAtk = 0;
        for (final Party party : partyInfo.getParties()) {
            if (party == null) {
                continue;
            }
            int partyHp = 0;
            int partyAtk = 0;
            for (final Card card : party.getCards()) {
                if (card == null) {
                    continue;
                }
                partyHp += card.getHp();
                partyAtk += card.getAtk();
            }
            party.setHp(partyHp);
            party.setAtk(partyAtk);
            partyInfoHp += partyHp;
            partyInfoAtk += partyAtk;
        }
        partyInfo.setHp(partyInfoHp);
        partyInfo.setAtk(partyInfoAtk);
    }

    public void updatePartyHpAtk() {
        final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
        final Party party = partyInfo.getParties()[partyNumber - 1];
        this.partyInfoHpText.setText(String.valueOf(partyInfo.getHp()));
        this.partyInfoAtkText.setText(String.valueOf(partyInfo.getAtk()));
        this.partyHpText.setText(String.valueOf(party.getHp()));
        this.partyAtkText.setText(String.valueOf(party.getAtk()));

    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}