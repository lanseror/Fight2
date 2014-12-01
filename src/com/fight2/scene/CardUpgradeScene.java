package com.fight2.scene;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import android.view.MotionEvent;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.cardpack.CardPack;
import com.fight2.entity.engine.cardpack.CardPackPhysicsHandler;
import com.fight2.entity.engine.cardpack.CardPackScrollDetectorListener;
import com.fight2.entity.engine.cardpack.CardPackTouchArea;
import com.fight2.entity.engine.cardpack.CardUpdateHandler;
import com.fight2.entity.engine.cardpack.MoveFinishedListener;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardUpgradeScene extends BaseCardPackScene {
    private F2ScrollDetector scrollDetector;
    private final int partyNumber = 1;
    private final Sprite[] cardGrids = new Sprite[4];

    private CardPackPhysicsHandler physicsHandler;
    private final IEntity[] inGridCardSprites = new IEntity[4];

    private final Rectangle cardZoom;
    private final CardPack cardPack;
    private final float frameY = cameraHeight - TextureEnum.UPGRADE_FRAME.getHeight()+5;

    private final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
    private final Set<Integer> inPartyCards = GameUserSession.getInstance().getInPartyCards();

    public CardUpgradeScene(final GameActivity activity) throws IOException {
        super(activity);

        cardZoom = new Rectangle(250 + CARD_WIDTH * 0.7f, 145, CARD_WIDTH * 1.4f, CARD_HEIGHT * 1.4f, vbom);
        cardPack = new CardPack(300, 145, 21000, CARD_HEIGHT, vbom, cardZoom);
        init();
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        // final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
        // final Card[] partyCards = parties[partyNumber - 1].getCards();
        // for (int i = 0; i < partyCards.length; i++) {
        // final Card card = partyCards[i];
        // if (card != null) {
        // final IEntity avatarSprite = createCardAvatarSprite(card, 10, 20);
        // avatarSprite.setPosition(cardGrids[i]);
        // avatarSprite.setUserData(card);
        // this.attachChild(avatarSprite);
        // inGridCardSprites[i] = avatarSprite;
        //
        // final IEntity removedCardSprite = new CardFrame(0, CARD_Y, CARD_WIDTH, CARD_HEIGHT, card, activity);
        // removedCardSprite.setTag(i);
        // removedCardSprite.setUserData(card);
        // cardPack.removedCard(card, removedCardSprite);
        // this.registerUpdateHandler(new CardUpdateHandler(cardZoom, removedCardSprite));
        // }
        //
        // }
        // this.sortChildren();
        // this.updatePartyHpAtk();
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

        final Sprite frameSprite = createALBImageSprite(TextureEnum.UPGRADE_FRAME, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        cardPack.setColor(Color.TRANSPARENT);
        cardZoom.setColor(Color.TRANSPARENT);
        final Card[] partyCards = parties[this.partyNumber - 1].getCards();
        this.scrollDetector = new F2ScrollDetector(new CardPackScrollDetectorListener(this, cardPack, cardZoom, partyCards));

        final TextureEnum gridEnum = TextureEnum.PARTY_EDIT_FRAME_GRID;
        final float gridGap = 153;
        final float gridStartX = 148;
        final float frameLeft = frameSprite.getX() - frameSprite.getWidth() * 0.5f;
        final float frameBottom = frameSprite.getY() - frameSprite.getHeight() * 0.5f;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion gridTexture = textureFactory.getAssetTextureRegion(gridEnum);
        final float gridWidth = gridEnum.getWidth();
        final float gridHeight = gridEnum.getHeight();
        // for (int gridIndex = 0; gridIndex < 4; gridIndex++) {
        // final int frameIndex = gridIndex;
        // cardGrids[gridIndex] = new Sprite(frameLeft + gridStartX + gridGap * gridIndex, frameBottom + 161, gridWidth, gridHeight, gridTexture, vbom) {
        // private IEntity movingCard = null;
        //
        // @Override
        // public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        // final float touchX = pSceneTouchEvent.getX();
        // final float touchY = pSceneTouchEvent.getY();
        // final MotionEvent motionEvent = pSceneTouchEvent.getMotionEvent();
        // final int action = motionEvent.getAction();
        // switch (action) {
        // case MotionEvent.ACTION_DOWN:
        // movingCard = inGridCardSprites[frameIndex];
        // if (movingCard != null) {
        // movingCard.setZIndex(100);
        // CardUpgradeScene.this.sortChildren();
        // }
        // break;
        // case MotionEvent.ACTION_MOVE:
        // if (movingCard != null) {
        // movingCard.setPosition(touchX, touchY);
        // }
        // break;
        // case MotionEvent.ACTION_CANCEL:
        // case MotionEvent.ACTION_UP:
        // if (movingCard == null) {
        // break;
        // }
        // boolean collidedWithOthers = false;
        // for (int i = 0; i < cardGrids.length; i++) {
        // final IEntity cardFrame = cardGrids[i];
        // if (i != frameIndex && cardFrame.contains(touchX, touchY)) {
        // collidedWithOthers = true;
        // movingCard.setPosition(cardFrame);
        // final IEntity toCard = inGridCardSprites[i];
        // inGridCardSprites[frameIndex] = toCard;
        // final Card tempPartyCard = partyCards[frameIndex];
        // partyCards[frameIndex] = partyCards[i];
        // partyCards[i] = tempPartyCard;
        // if (toCard != null) {
        // toCard.setPosition(this);
        // }
        // inGridCardSprites[i] = movingCard;
        // break;
        // }
        // }
        // if (!collidedWithOthers) {
        // if (touchY < this.getY() - 50) {
        // inPartyCards.remove(partyCards[frameIndex].getTemplateId());
        // cardPack.revertCardToCardPack(movingCard);
        // partyCards[frameIndex] = null;
        // calculatePartyHpAtk();
        // updatePartyHpAtk();
        // activity.runOnUpdateThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // inGridCardSprites[frameIndex].detachSelf();
        // inGridCardSprites[frameIndex] = null;
        //
        // }
        //
        // });
        //
        // } else {
        // movingCard.setPosition(this);
        // }
        // }
        // movingCard.setZIndex(IEntity.ZINDEX_DEFAULT);
        // CardUpgradeScene.this.sortChildren();
        // movingCard = null;
        // break;
        //
        // }
        // return true;
        // }
        // };
        // this.attachChild(cardGrids[gridIndex]);
        // this.registerTouchArea(cardGrids[gridIndex]);
        // cardGrids[gridIndex].setZIndex(10);
        // }

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
        // final Sprite leftCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_LEFT, 0, 80);
        // final Sprite rightCover = createALBImageSprite(TextureEnum.PARTY_EDIT_COVER_RIGHT, this.cameraWidth - TextureEnum.PARTY_EDIT_COVER_RIGHT.getWidth(),
        // 80);
        // this.attachChild(leftCover);
        // this.attachChild(rightCover);

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
        // TODO Auto-generated method stub

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

    @Override
    public void onGridCardsChange() {
        calculatePartyHpAtk();
        updatePartyHpAtk();
    }

    private void calculatePartyHpAtk() {
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

    private void updatePartyHpAtk() {

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