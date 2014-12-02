package com.fight2.entity.engine.cardpack;

import java.util.Set;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.util.modifier.IModifier;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.engine.CardFrame;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.scene.BaseCardPackScene;
import com.fight2.util.SpriteUtils;

public class CardPackScrollDetectorListener implements IScrollDetectorListener {
    protected final BaseCardPackScene cardPackScene;
    protected final GameActivity activity;
    protected final CardPack cardPack;
    protected final IEntity cardZoom;;
    protected final float cardZoomX;
    protected float initPointerID;
    protected float initX;
    protected float initY;
    protected float initDistanceX;
    protected float initDistanceY;
    protected IEntity copyCard;
    protected boolean scrollable = true;
    protected final Card[] inGridCards;

    public CardPackScrollDetectorListener(final BaseCardPackScene cardPackScene, final CardPack cardPack, final IEntity cardZoom, final Card[] inGridCards) {
        this.cardPackScene = cardPackScene;
        this.activity = cardPackScene.getActivity();
        this.cardPack = cardPack;
        this.cardZoom = cardZoom;
        this.cardZoomX = cardZoom.getX();
        this.inGridCards = inGridCards;
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        initPointerID = pPointerID;
        if (cardPack.getChildCount() != 0) {
            final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
            final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
            initX = touchEvent.getX();
            initY = touchEvent.getY();
            initDistanceX = pDistanceX;
            initDistanceY = pDistanceY;
            final IEntity focusedCard = (IEntity) cardZoom.getUserData();
            if (scrollable && focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {
                // Debug.e("Create copyCard");
                focusedCard.setAlpha(0.5f);
                final Card sessionCard = (Card) focusedCard.getUserData();
                copyCard = new CardFrame(0, 0, focusedCard.getWidth(), focusedCard.getHeight(), sessionCard, activity);
                copyCard.setPosition(SpriteUtils.toContainerOuterX(focusedCard), SpriteUtils.toContainerOuterY(focusedCard));
                copyCard.setUserData(sessionCard);
                this.cardPackScene.attachChild(copyCard);

            }
        }
    }

    @Override
    public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if (cardPack.getChildCount() != 0 && scrollable) {
            final IEntity focusedCard = (IEntity) cardZoom.getUserData();
            if (pPointerID == initPointerID && copyCard != null && focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {
                final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
                final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
                copyCard.setX(touchEvent.getX());
                copyCard.setY(touchEvent.getY());
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
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        // Debug.e("Scroll Finished");
        if (cardPack.getChildCount() == 0 || !scrollable) {
            // Debug.e("cardPack.getChildCount = 0");
            return;
        }
        final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
        final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
        final float finishedY = touchEvent.getY();
        final IEntity focusedCardSprite = (IEntity) cardZoom.getUserData();
        if (focusedCardSprite.getScaleX() > 1.8 * CardUpdateHandler.SCALE_FACTOR) {
            // Debug.e("focusedCard.getScaleX() > 1.8");
            if (pPointerID == initPointerID && copyCard != null && focusedCardSprite.contains(initX, initY)
                    && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {

                if (finishedY < SpriteUtils.toContainerOuterY(focusedCardSprite)) {
                    // Debug.e("Y < focusedCard will revert");
                    revertCard(focusedCardSprite);
                } else {
                    boolean collidedWithGrid = false;
                    boolean isReplace = false;
                    IEntity beReplacedCardSprite = null;
                    IEntity cardGrid = null;
                    int cardGridIndex = 0;
                    int collidedCardTemplateId = -1;
                    final IEntity[] cardGrids = this.cardPackScene.getCardGrids();
                    for (; cardGridIndex < cardGrids.length; cardGridIndex++) {
                        if (cardGrids[cardGridIndex].contains(copyCard.getX(), copyCard.getY())) {
                            collidedWithGrid = true;
                            if (inGridCards[cardGridIndex] != null) {
                                collidedCardTemplateId = inGridCards[cardGridIndex].getTemplateId();
                            }
                            beReplacedCardSprite = cardPackScene.getInGridCardSprites()[cardGridIndex];
                            isReplace = (beReplacedCardSprite == null ? false : true);
                            break;
                        }
                    }

                    boolean hasPosition = false;
                    if (!collidedWithGrid) {
                        for (int partyCardIndex = 0; partyCardIndex < inGridCards.length; partyCardIndex++) {
                            final Card partyCard = inGridCards[partyCardIndex];
                            if (partyCard == null) {
                                hasPosition = true;
                                cardGridIndex = partyCardIndex;
                                break;
                            }
                        }
                    }

                    final Card focusedCard = (Card) focusedCardSprite.getUserData();
                    final int focusedCardTempalteId = focusedCard.getTemplateId();
                    final GameUserSession session = GameUserSession.getInstance();
                    final Set<Integer> inPartyCards = session.getInPartyCards();
                    if ((collidedWithGrid || hasPosition) && inPartyCards.contains(focusedCardTempalteId) && collidedCardTemplateId != focusedCardTempalteId) {
                        scrollable = false;
                        // Debug.e("Already had the template id will revert");
                        revertCard(focusedCardSprite);
                        cardPackScene.alert("该卡片已经在你的队伍中！");
                    } else if (collidedWithGrid || hasPosition) {
                        scrollable = false;
                        // Debug.e("Add card");

                        if (collidedWithGrid) {
                            inPartyCards.remove(collidedCardTemplateId);
                        }
                        inPartyCards.add(focusedCardTempalteId);
                        inGridCards[cardGridIndex] = focusedCard;
                        cardPackScene.onGridCardsChange();
                        cardGrid = cardGrids[cardGridIndex];
                        final IEntity toReplaceCardAvatar = cardPackScene.createCardAvatarSprite(focusedCard, 135, 135);
                        toReplaceCardAvatar.setPosition(cardGrid);
                        toReplaceCardAvatar.setUserData(copyCard.getUserData());
                        this.cardPackScene.getInGridCardSprites()[cardGridIndex] = toReplaceCardAvatar;

                        final IEntityModifierListener modifierListener = isReplace ? new ReplacePartyCardModifierListener(focusedCardSprite, focusedCard,
                                beReplacedCardSprite, toReplaceCardAvatar) : new AddPartyCardModifierListener(focusedCardSprite, focusedCard,
                                toReplaceCardAvatar);
                        final MoveModifier modifier = new MoveModifier(0.1f, copyCard.getX(), copyCard.getY(), cardGrid.getX(), cardGrid.getY(),
                                modifierListener);
                        cardPackScene.getActivity().runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                copyCard.clearEntityModifiers();
                                copyCard.registerEntityModifier(modifier);
                                copyCard = null;
                            }
                        });
                    } else {
                        scrollable = false;
                        // Debug.e("NoPosition will revert");
                        revertCard(focusedCardSprite);
                        cardPackScene.alert("队伍已满！");
                    }
                }
            } else if (copyCard != null) {
                // Debug.e("Directly revert");
                scrollable = false;
                revertCard(focusedCardSprite);
            }
        } else if (copyCard != null) {
            // Debug.e("CoDirectly revert");
            scrollable = false;
            revertCard(focusedCardSprite);
        }
    }

    protected void revertCard(final IEntity focusedCard) {
        final MoveModifier revertModifier = new MoveModifier(0.1f, copyCard.getX(), copyCard.getY(), SpriteUtils.toContainerOuterX(focusedCard),
                SpriteUtils.toContainerOuterY(focusedCard), new IEntityModifierListener() {
                    @Override
                    public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        scrollable = false;
                    }

                    @Override
                    public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        cardPackScene.getActivity().runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                pItem.detachSelf();
                                // Debug.e("revertCard");
                                copyCard = null;
                                focusedCard.setAlpha(1);
                                scrollable = true;
                            }
                        });
                    }

                });
        cardPackScene.getActivity().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                copyCard.clearEntityModifiers();
                copyCard.registerEntityModifier(revertModifier);
            }
        });
    }

    protected class AddPartyCardModifierListener implements IEntityModifierListener {
        private final IEntity focusedCard;
        private final Card cardEntry;
        private final IEntity avatar;

        protected AddPartyCardModifierListener(final IEntity focusedCard, final Card cardEntry, final IEntity avatar) {
            this.focusedCard = focusedCard;
            this.cardEntry = cardEntry;
            this.avatar = avatar;
        }

        @Override
        public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            scrollable = false;
        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
            focusedCard.setAlpha(0);
            final int focusedCardIndex = focusedCard.getTag();
            final boolean isFocusedCardRightMost = (focusedCardIndex == cardPack.getChildCount() - 1);

            if (isFocusedCardRightMost) {
                for (int cardPackIndex = focusedCardIndex - 1; cardPackIndex >= 0; cardPackIndex--) {
                    final IEntity previousCard = cardPack.getChildByIndex(cardPackIndex + 1);
                    final IEntity currentCard = cardPack.getChildByIndex(cardPackIndex);
                    if (cardPackIndex == focusedCardIndex - 1) {
                        cardZoom.setUserData(currentCard);
                    }
                    final float duration = (cardPackIndex == focusedCardIndex - 1 ? 0.1f : 0.2f);
                    final MoveModifier cardEditModifier = new MoveModifier(duration, currentCard.getX(), currentCard.getY(), previousCard.getX(),
                            previousCard.getY());
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            } else {
                for (int cardPackIndex = focusedCardIndex + 1; cardPackIndex < cardPack.getChildCount(); cardPackIndex++) {
                    final IEntity previousCard = cardPack.getChildByIndex(cardPackIndex - 1);
                    final IEntity currentCard = cardPack.getChildByIndex(cardPackIndex);
                    currentCard.setTag(cardPackIndex - 1);
                    if (cardPackIndex == focusedCardIndex + 1) {
                        cardZoom.setUserData(currentCard);
                    }
                    final float duration = (cardPackIndex == focusedCardIndex + 1 ? 0.1f : 0.2f);
                    final MoveModifier cardEditModifier = new MoveModifier(duration, currentCard.getX(), currentCard.getY(), previousCard.getX(),
                            previousCard.getY());
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            }
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    focusedCard.detachSelf();
                    cardPack.removedCard(cardEntry, focusedCard);
                    GameUserSession.getInstance().getCards().remove(cardEntry);
                    cardPackScene.attachChild(avatar);
                    pItem.detachSelf();
                    cardPackScene.sortChildren();
                }
            });

            scrollable = true;
        }
    }

    protected class ReplacePartyCardModifierListener implements IEntityModifierListener {
        private final IEntity focusedCard;
        private final Card cardEntry;
        private final IEntity beReplacedCardSprite;
        private final IEntity toReplaceCardAvatar;

        protected ReplacePartyCardModifierListener(final IEntity focusedCard, final Card cardEntry, final IEntity beReplacedCardSprite,
                final IEntity toReplaceCardAvatar) {
            this.focusedCard = focusedCard;
            this.cardEntry = cardEntry;
            this.beReplacedCardSprite = beReplacedCardSprite;
            this.toReplaceCardAvatar = toReplaceCardAvatar;
        }

        @Override
        public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            scrollable = false;
        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
            cardPackScene.sortChildren();
            focusedCard.setAlpha(0);
            final int focusedCardIndex = focusedCard.getTag();
            final boolean isFocusedCardRightMost = (focusedCardIndex == cardPack.getChildCount() - 1);

            if (isFocusedCardRightMost) {
                for (int cardPackIndex = focusedCardIndex - 1; cardPackIndex >= 0; cardPackIndex--) {
                    final IEntity previousCard = cardPack.getChildByIndex(cardPackIndex + 1);
                    final IEntity currentCard = cardPack.getChildByIndex(cardPackIndex);

                    final float duration = (cardPackIndex == focusedCardIndex - 1 ? 0.1f : 0.2f);
                    final MoveModifier cardEditModifier = new MoveModifier(duration, currentCard.getX(), currentCard.getY(), previousCard.getX(),
                            previousCard.getY());
                    if (cardPackIndex == focusedCardIndex - 1) {
                        cardZoom.setUserData(currentCard);
                        cardEditModifier.addModifierListener(new ReplacePartyCardFinishedModifierListener(beReplacedCardSprite));
                    }
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            } else {
                for (int cardPackIndex = focusedCardIndex + 1; cardPackIndex < cardPack.getChildCount(); cardPackIndex++) {
                    final IEntity previousCard = cardPack.getChildByIndex(cardPackIndex - 1);
                    final IEntity currentCard = cardPack.getChildByIndex(cardPackIndex);
                    currentCard.setTag(cardPackIndex - 1);

                    final float duration = (cardPackIndex == focusedCardIndex + 1 ? 0.1f : 0.2f);
                    final MoveModifier cardEditModifier = new MoveModifier(duration, currentCard.getX(), currentCard.getY(), previousCard.getX(),
                            previousCard.getY());
                    if (cardPackIndex == focusedCardIndex + 1) {
                        cardZoom.setUserData(currentCard);
                        cardEditModifier.addModifierListener(new ReplacePartyCardFinishedModifierListener(beReplacedCardSprite));
                    }

                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            }

            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    focusedCard.detachSelf();
                    cardPack.removedCard(cardEntry, focusedCard);
                    GameUserSession.getInstance().getCards().remove(cardEntry);
                    if (cardPack.getChildCount() == 0) {
                        cardPack.revertCardToCardPack(beReplacedCardSprite);
                        beReplacedCardSprite.detachSelf();
                    }
                    pItem.detachSelf();
                    cardPackScene.attachChild(toReplaceCardAvatar);
                    cardPackScene.sortChildren();
                    scrollable = true;
                }
            });

            // Revert the replace card to CardPack.

        }
    }

    protected class ReplacePartyCardFinishedModifierListener implements IEntityModifierListener {
        private final IEntity replaceCardSprite;

        public ReplacePartyCardFinishedModifierListener(final IEntity replaceCardSprite) {
            this.replaceCardSprite = replaceCardSprite;
        }

        @Override
        public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
            cardPack.revertCardToCardPack(replaceCardSprite);
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    replaceCardSprite.detachSelf();
                }
            });
            scrollable = true;
        }
    }

}