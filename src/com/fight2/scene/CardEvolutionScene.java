package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.json.JSONArray;

import android.view.MotionEvent;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.cardpack.CardEvolutionScrollDetectorListener;
import com.fight2.entity.engine.cardpack.CardPack;
import com.fight2.entity.engine.cardpack.CardPackPhysicsHandler;
import com.fight2.entity.engine.cardpack.CardPackTouchArea;
import com.fight2.entity.engine.cardpack.CardUpdateHandler;
import com.fight2.entity.engine.cardpack.MoveFinishedListener;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.CardUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.PartyUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardEvolutionScene extends BaseCardPackScene {
    private F2ScrollDetector scrollDetector;
    private final Sprite[] cardGrids = new Sprite[2];

    private CardPackPhysicsHandler physicsHandler;
    private final Card[] inGridCards = new Card[2];
    private final IEntity[] inGridCardSprites = new IEntity[2];

    private final List<Card> cardPackCards = new ArrayList<Card>(CardUtils.getEvocards());

    private final Rectangle cardZoom;
    private final CardPack cardPack;
    private final float frameY = cameraHeight - TextureEnum.EVOLUTION_FRAME.getHeight() + 5;
    private final Font hpatkFont;
    private final Text hpText;
    private final Text atkText;
    private final List<CardUpdateHandler> cardUpdateHandlers = new ArrayList<CardUpdateHandler>();

    public CardEvolutionScene(final GameActivity activity) throws IOException {
        super(activity);
        hpatkFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        hpText = new Text(630, 301, hpatkFont, "+0123456789", vbom);
        hpText.setColor(0XFF5AD61E);
        hpText.setText("");
        atkText = new Text(630, 232, hpatkFont, "+0123456789", vbom);
        atkText.setColor(0XFF5AD61E);
        atkText.setText("");

        cardZoom = new Rectangle(250 + CARD_WIDTH * 0.7f, 145, CARD_WIDTH * 1.4f, CARD_HEIGHT * 1.4f, vbom);
        cardPack = new CardPack(300, 145, 21000, CARD_HEIGHT, activity, cardZoom);
        cardPack.setColor(Color.TRANSPARENT);
        cardZoom.setColor(Color.TRANSPARENT);
        init();
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        // Insert cards to card pack.
        cardPackCards.clear();
        final List<Card> userEvoCards = CardUtils.getEvocards();
        for (final Card userEvoCard : userEvoCards) {
            cardPackCards.add(userEvoCard);
        }
        updateCardPack();
    }

    public void updateCardPack() {
        // Insert cards to card pack.
        cardPack.detachChildren();
        for (final CardUpdateHandler cardUpdateHandler : cardUpdateHandlers) {
            this.unregisterUpdateHandler(cardUpdateHandler);
        }
        cardUpdateHandlers.clear();
        final float initCardX = cardZoom.getX() - (cardPack.getX() - 0.5f * cardPack.getWidth());
        float appendX = initCardX;
        for (int i = 0; i < cardPackCards.size(); i++) {
            final Card cardPackCard = cardPackCards.get(i);
            final IEntity card = new CardFrame(appendX, CARD_Y, CARD_WIDTH, CARD_HEIGHT, cardPackCard, activity);
            card.setTag(i);
            card.setWidth(CARD_WIDTH);
            card.setHeight(CARD_HEIGHT);
            card.setPosition(appendX, CARD_Y);
            card.setUserData(cardPackCard);
            cardPack.attachChild(card);
            if (i == 0) {
                appendX += 1.5 * (CARD_GAP + CARD_WIDTH);
                cardZoom.setUserData(card);
            } else {
                appendX += CARD_GAP + CARD_WIDTH;
            }
            final CardUpdateHandler cardUpdateHandler = new CardUpdateHandler(cardZoom, card);
            cardUpdateHandlers.add(cardUpdateHandler);
            this.registerUpdateHandler(cardUpdateHandler);
        }
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.EVOLUTION_FRAME, this.simulatedLeftX + 50, frameY);
        this.attachChild(frameSprite);
        frameSprite.attachChild(hpText);
        frameSprite.attachChild(atkText);

        final F2ButtonSprite evolutionButton = createEvolutionButton();
        frameSprite.attachChild(evolutionButton);
        this.registerTouchArea(evolutionButton);

        this.scrollDetector = new F2ScrollDetector(new CardEvolutionScrollDetectorListener(this, cardPack, cardZoom, inGridCards));

        final TextureEnum gridEnum = TextureEnum.PARTY_EDIT_FRAME_GRID;
        final float gridWidth = 240;
        final float gridHeight = 360;
        final float gridStartX = 129;
        final float frameLeft = frameSprite.getX() - frameSprite.getWidth() * 0.5f;
        final float gridY = frameY + frameSprite.getHeight() * 0.5f + 5;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion gridTexture = textureFactory.getAssetTextureRegion(gridEnum);

        for (int gridIndex = 0; gridIndex < 2; gridIndex++) {
            final int frameIndex = gridIndex;
            cardGrids[gridIndex] = new Sprite(frameLeft + gridStartX + (gridWidth - 15) * gridIndex, gridY, gridWidth, gridHeight, gridTexture, vbom) {
                private IEntity movingCardSprite = null;

                @Override
                public boolean onAreaTouched(final TouchEvent sceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    final float touchX = sceneTouchEvent.getX();
                    final float touchY = sceneTouchEvent.getY();
                    final MotionEvent motionEvent = sceneTouchEvent.getMotionEvent();
                    final int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            movingCardSprite = inGridCardSprites[frameIndex];
                            if (movingCardSprite != null) {
                                movingCardSprite.setZIndex(100);
                                CardEvolutionScene.this.sortChildren();
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (movingCardSprite != null) {
                                movingCardSprite.setPosition(touchX, touchY);
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (movingCardSprite == null) {
                                break;
                            }
                            if (touchY < this.getY() - 50) {
                                cardPack.revertCardToCardPack(movingCardSprite);
                                inGridCards[frameIndex] = null;
                                onGridCardsChange(frameIndex, GridChangeAction.Remove);
                                activity.runOnUpdateThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        inGridCardSprites[frameIndex].detachSelf();
                                        inGridCardSprites[frameIndex] = null;
                                    }

                                });

                            } else {
                                movingCardSprite.setPosition(this);
                            }
                            movingCardSprite.setZIndex(IEntity.ZINDEX_DEFAULT);
                            CardEvolutionScene.this.sortChildren();
                            movingCardSprite = null;
                            break;

                    }
                    return true;
                }
            };
            this.attachChild(cardGrids[gridIndex]);
            this.registerTouchArea(cardGrids[gridIndex]);
            cardGrids[gridIndex].setAlpha(0);
            cardGrids[gridIndex].setZIndex(10);
        }

        final MoveFinishedListener moveFinishedListener = new MoveFinishedListener(cardPack, cardZoom, activity);
        physicsHandler = new CardPackPhysicsHandler(cardPack, cardZoom, moveFinishedListener);
        this.registerUpdateHandler(physicsHandler);

        final float touchAreaWidth = this.simulatedWidth - TextureEnum.COMMON_BACK_BUTTON_NORMAL.getWidth() - 80;
        final float touchAreaX = this.simulatedLeftX + touchAreaWidth * 0.5f;
        final Rectangle touchArea = new CardPackTouchArea(touchAreaX, 160, touchAreaWidth, 280, vbom, scrollDetector, physicsHandler, cardPack);
        this.registerTouchArea(touchArea);
        this.attachChild(touchArea);
        this.attachChild(cardPack);
        this.attachChild(cardZoom);

        // Add cover and buttons.
        final Sprite leftCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_LEFT, 0, 0);
        final Sprite rightCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_RIGHT, this.cameraWidth - TextureEnum.PARTY_EDIT_COVER_RIGHT.getWidth(), 0);
        this.attachChild(leftCover);
        this.attachChild(rightCover);

        final F2ButtonSprite backButton = createBackButton();
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite enhanceButton = createEnhanceButton();
        this.attachChild(enhanceButton);
        this.registerTouchArea(enhanceButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void leaveScene() {
        revert();
    }

    private F2ButtonSprite createEvolutionButton() {
        final F2ButtonSprite evolutionButton = createALBF2ButtonSprite(TextureEnum.EVOLUTION_FRAME_BUTTON, TextureEnum.EVOLUTION_FRAME_BUTTON_FCS, 467, 40);
        evolutionButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (inGridCards[0] == null || inGridCards[1] == null) {
                    return;
                }
                final JSONArray cardIdsJson = new JSONArray();
                for (final Card card : inGridCards) {
                    cardIdsJson.put(card.getId());
                }

                final int result = CardUtils.evolution(cardIdsJson, inGridCards);
                if (result == 0 || result == 1) {
                    cardPack.revertCardToCardPack(inGridCardSprites[result]);
                    for (int i = 0; i < inGridCards.length; i++) {
                        inGridCards[i] = null;
                        final IEntity inGridCardSprite = inGridCardSprites[i];
                        inGridCardSprites[i] = null;
                        if (inGridCardSprite != null) {
                            activity.runOnUpdateThread(new Runnable() {
                                @Override
                                public void run() {
                                    inGridCardSprite.detachSelf();
                                }

                            });

                        }
                    }
                    hpText.setText("");
                    atkText.setText("");
                    cardPackCards.clear();
                    CardUtils.refreshUserCards();
                    final List<Card> userEvoCards = CardUtils.getEvocards();
                    for (final Card userEvoCard : userEvoCards) {
                        cardPackCards.add(userEvoCard);
                    }
                    updateCardPack();
                    PartyUtils.refreshPartyHpAtk();
                } else {
                    alert("出错了。");
                }

            }
        });
        return evolutionButton;
    }

    private F2ButtonSprite createBackButton() {
        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK);
                ResourceManager.getInstance().sceneBack();
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
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK);
                ResourceManager.getInstance().setCurrentScene(SceneEnum.CardUpgrade);
            }
        });
        return enhanceButton;
    }

    @Override
    public void onGridCardsChange(final int changeIndex, final GridChangeAction changeAction) {
        final List<Card> evoCards = new ArrayList<Card>(2);
        for (int i = 0; i < inGridCards.length; i++) {
            final Card evoCard = inGridCards[i];
            if (evoCard != null) {
                evoCards.add(evoCard);
            }
        }

        if (changeAction == GridChangeAction.Add) {
            final Card evoCard = inGridCards[changeIndex];
            final IEntity gridCardSprite = inGridCardSprites[changeIndex];
            final float spriteWidth = gridCardSprite.getWidth();
            final float spriteHeight = gridCardSprite.getHeight();
            final IEntity evoPercentFrame = new Rectangle(spriteWidth * 0.5f, spriteHeight * 0.5f, 200, 50, vbom);
            evoPercentFrame.setColor(Color.BLACK);
            evoPercentFrame.setAlpha(0.5f);
            final Sprite evoIcon = this.createACImageSprite(TextureEnum.EVOLUTION_ICON, 46, 25);
            evoPercentFrame.attachChild(evoIcon);
            final int evoPercent = CardUtils.getEvoPercent(evoCard);
            final Text evoPercentText = new Text(125, 25, hpatkFont, "+" + evoPercent + "%", vbom);
            evoPercentFrame.attachChild(evoPercentText);
            evoPercentFrame.setZIndex(100);
            gridCardSprite.attachChild(evoPercentFrame);

            if (evoCards.size() == 1) {
                cardPackCards.clear();
                final Card addedCard = evoCards.get(0);
                cardPackCards.add(addedCard);
                final Set<Card> userCards = CardUtils.getUsercardsByTemplateId(addedCard.getTemplateId());
                for (final Card userCard : userCards) {
                    if (userCard != addedCard) {
                        cardPackCards.add(userCard);
                    }
                }
                cardPack.filterCards(new IEntityMatcher() {

                    @Override
                    public boolean matches(final IEntity pEntity) {
                        final CardFrame cardSprite = (CardFrame) pEntity;
                        final Card card = cardSprite.getCard();
                        if (card.getTemplateId() == addedCard.getTemplateId()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            } else if (evoCards.size() == 2) {
                final Card mockEvoCard = CardUtils.mockEvolution(evoCards.get(0), evoCards.get(1));
                this.hpText.setText(String.valueOf(mockEvoCard.getHp()));
                this.atkText.setText(String.valueOf(mockEvoCard.getAtk()));
            }
        } else if (changeAction == GridChangeAction.Remove) {
            this.hpText.setText("");
            this.atkText.setText("");
            if (evoCards.size() == 0) {
                cardPackCards.clear();
                final List<Card> userEvoCards = CardUtils.getEvocards();
                for (final Card userEvoCard : userEvoCards) {
                    cardPackCards.add(userEvoCard);
                }
                this.updateCardPack();
            }
        }

    }

    private void revert() {
        for (int i = 0; i < inGridCardSprites.length; i++) {
            final int gridIndex = i;
            final IEntity cardSprite = inGridCardSprites[gridIndex];
            if (cardSprite == null) {
                continue;
            }
            cardPack.revertCardToCardPack(cardSprite);
            inGridCards[gridIndex] = null;
            this.hpText.setText("");
            this.atkText.setText("");
            activity.runOnUpdateThread(new Runnable() {

                @Override
                public void run() {
                    inGridCardSprites[gridIndex].detachSelf();
                    inGridCardSprites[gridIndex] = null;

                }

            });
        }
    }

    @Override
    public Sprite[] getCardGrids() {
        return cardGrids;
    }

    @Override
    public IEntity[] getInGridCardSprites() {
        return inGridCardSprites;
    }

}