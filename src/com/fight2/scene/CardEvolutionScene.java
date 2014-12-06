package com.fight2.scene;

import java.io.IOException;
import java.util.List;

import org.andengine.entity.IEntity;
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
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
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
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardEvolutionScene extends BaseCardPackScene {
    private F2ScrollDetector scrollDetector;
    private final Sprite[] cardGrids = new Sprite[2];

    private CardPackPhysicsHandler physicsHandler;
    private final Card[] inGridCards = new Card[2];
    private final IEntity[] inGridCardSprites = new IEntity[2];

    private final Rectangle cardZoom;
    private final CardPack cardPack;
    private final float frameY = cameraHeight - TextureEnum.EVOLUTION_FRAME.getHeight() + 5;
    private final Font hpatkFont;
    private final Text hpText;
    private final Text atkText;

    public CardEvolutionScene(final GameActivity activity) throws IOException {
        super(activity);
        hpatkFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        hpText = new Text(630, 295, hpatkFont, "+0123456789", vbom);
        hpText.setColor(0XFF5AD61E);
        hpText.setText("");
        atkText = new Text(630, 226, hpatkFont, "+0123456789", vbom);
        atkText.setColor(0XFF5AD61E);
        atkText.setText("");

        cardZoom = new Rectangle(250 + CARD_WIDTH * 0.7f, 145, CARD_WIDTH * 1.4f, CARD_HEIGHT * 1.4f, vbom);
        cardPack = new CardPack(300, 145, 21000, CARD_HEIGHT, vbom, cardZoom);
        cardPack.setColor(Color.TRANSPARENT);
        cardZoom.setColor(Color.TRANSPARENT);
        init();
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        // Insert cards to card pack.
        cardPack.detachChildren();
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
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite frameSprite = createALBImageSprite(TextureEnum.EVOLUTION_FRAME, this.simulatedLeftX + 50, frameY);
        this.attachChild(frameSprite);
        frameSprite.attachChild(hpText);
        frameSprite.attachChild(atkText);

        final F2ButtonSprite evolutionButton = createEvolutionButton();
        frameSprite.attachChild(evolutionButton);
//        this.registerTouchArea(evolutionButton);

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
                                onGridCardsChange();
                                if (frameIndex == 0) {
                                    revert(false);
                                }
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
        final Rectangle touchArea = new CardPackTouchArea(touchAreaX, 160, touchAreaWidth, 280, vbom, scrollDetector, physicsHandler);
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
        revert(true);
    }

    private F2ButtonSprite createEvolutionButton() {
        final F2ButtonSprite evolutionButton = createALBF2ButtonSprite(TextureEnum.EVOLUTION_FRAME_BUTTON, TextureEnum.EVOLUTION_FRAME_BUTTON, 467, 40);
        evolutionButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final Card mainCard = inGridCards[0];
                if (mainCard == null) {
                    return;
                }
                final JSONArray cardIdsJson = new JSONArray();
                for (final Card card : inGridCards) {
                    if (card == null) {
                        continue;
                    }
                    cardIdsJson.put(card.getId());
                }
                if (cardIdsJson.length() < 2) {
                    return;
                }

                final boolean isOk = CardUtils.upgrade(cardIdsJson, mainCard);
                if (isOk) {
                    for (int i = 1; i < inGridCards.length; i++) {
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
                    final CardFrame mainCardSprite = (CardFrame) inGridCardSprites[0];
                    mainCardSprite.revertCardAttributes();
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
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Party);
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
                ResourceManager.getInstance().setCurrentScene(SceneEnum.CardUpgrade);
            }
        });
        return enhanceButton;
    }

    @Override
    public void onGridCardsChange() {
        final Card mainCard = inGridCards[0];
        if (mainCard == null) {
            return;
        }
        final Card manCardCopy = new Card(mainCard);

        for (int i = 1; i < inGridCards.length; i++) {
            final Card supportCard = inGridCards[i];
            if (supportCard == null) {
                continue;
            }
            final int baseExp = CardUtils.getBaseExp(supportCard);
            final int exp = supportCard.getExp() / 2;
            final int addExp = baseExp + exp;
            manCardCopy.setExp(manCardCopy.getExp() + addExp);
        }

        CardUtils.mockUpgrade(manCardCopy);
        final int addHp = manCardCopy.getHp() - mainCard.getHp();
        final int addAtk = manCardCopy.getAtk() - mainCard.getAtk();

        final CardFrame mainCardSprite = (CardFrame) inGridCardSprites[0];
        if (manCardCopy.getLevel() == mainCard.getLevel()) {
            this.hpText.setText("");
            this.atkText.setText("");
            mainCardSprite.revertCardAttributes();
        } else {
            this.hpText.setText("+" + addHp);
            this.atkText.setText("+" + addAtk);
            mainCardSprite.updateCardAttributes(manCardCopy);
        }

    }

    private void revert(final boolean includedMainCard) {
        final int startIndex = includedMainCard ? 0 : 1;
        for (int i = startIndex; i < inGridCardSprites.length; i++) {
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