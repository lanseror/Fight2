package com.fight2.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.util.modifier.IModifier;

import android.widget.Toast;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.util.SpriteUtils;

public class CardPackScrollDetectorListener implements IScrollDetectorListener {

    /**
     * 
     */
    private final PartyEditScene partyEditScene;
    private final IEntity cardPack;
    private final IEntity cardZoom;;
    private final float cardZoomX;
    private float initPointerID;
    private float initX;
    private float initY;
    private float initDistanceX;
    private float initDistanceY;
    private Sprite copyCard;
    private boolean scrollable = true;

    public CardPackScrollDetectorListener(final PartyEditScene partyEditScene, final IEntity cardPack, final IEntity cardZoom) {
        this.partyEditScene = partyEditScene;
        this.cardPack = cardPack;
        this.cardZoom = cardZoom;
        this.cardZoomX = cardZoom.getX();
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
                copyCard = this.partyEditScene.createRealScreenCardSprite(sessionCard, 10, 20);
                copyCard.setPosition(SpriteUtils.toContainerOuterX(focusedCard), SpriteUtils.toContainerOuterY(focusedCard));
                copyCard.setWidth(focusedCard.getWidth());
                copyCard.setHeight(focusedCard.getHeight());
                copyCard.setUserData(sessionCard);
                this.partyEditScene.attachChild(copyCard);

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
        final IEntity focusedCard = (IEntity) cardZoom.getUserData();
        if (focusedCard.getScaleX() > 1.8) {
            // Debug.e("focusedCard.getScaleX() > 1.8");
            if (pPointerID == initPointerID && copyCard != null && focusedCard.contains(initX, initY) && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {

                if (finishedY < SpriteUtils.toContainerOuterY(focusedCard)) {
                    // Debug.e("Y < focusedCard will revert");
                    revertCard(focusedCard);
                } else {
                    final Party[] parties = GameUserSession.getInstance().getPartyInfo().getParties();
                    final Card[] partyCards = parties[this.partyEditScene.partyNumber - 1].getCards();
                    boolean collidedWithGrid = false;
                    boolean isReplace = false;
                    IEntity beReplacedCardSprite = null;
                    Rectangle cardGrid = null;
                    int cardGridIndex = 0;
                    for (; cardGridIndex < this.partyEditScene.cardFrames.length; cardGridIndex++) {
                        if (this.partyEditScene.cardFrames[cardGridIndex].contains(copyCard.getX(), copyCard.getY())) {
                            collidedWithGrid = true;
                            beReplacedCardSprite = partyEditScene.addedCards[cardGridIndex];
                            isReplace = (beReplacedCardSprite == null ? false : true);
                            break;
                        }
                    }

                    boolean hasPosition = false;
                    if (!collidedWithGrid) {
                        for (int partyCardIndex = 0; partyCardIndex < partyCards.length; partyCardIndex++) {
                            final Card partyCard = partyCards[partyCardIndex];
                            if (partyCard == null) {
                                hasPosition = true;
                                cardGridIndex = partyCardIndex;
                                break;
                            }
                        }
                    }

                    if (collidedWithGrid || hasPosition) {
                        scrollable = false;
                        // Debug.e("Add card");
                        final Card cardEntry = (Card) focusedCard.getUserData();
                        partyCards[cardGridIndex] = cardEntry;
                        cardGrid = this.partyEditScene.cardFrames[cardGridIndex];
                        final IEntity toReplaceCardAvatar = partyEditScene.createCardAvatarSprite(cardEntry, 10, 20);
                        toReplaceCardAvatar.setPosition(cardGrid);
                        toReplaceCardAvatar.setUserData(copyCard.getUserData());
                        this.partyEditScene.addedCards[cardGridIndex] = toReplaceCardAvatar;

                        final IEntityModifierListener modifierListener = isReplace ? new ReplacePartyCardModifierListener(focusedCard, cardEntry,
                                beReplacedCardSprite, toReplaceCardAvatar) : new AddPartyCardModifierListener(focusedCard, cardEntry, toReplaceCardAvatar);
                        final MoveModifier modifier = new MoveModifier(0.1f, copyCard.getX(), copyCard.getY(), cardGrid.getX(), cardGrid.getY(),
                                modifierListener);
                        partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
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
                        revertCard(focusedCard);
                        partyEditScene.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(partyEditScene.getActivity(), "队伍已满！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else if (copyCard != null) {
                // Debug.e("Directly revert");
                scrollable = false;
                revertCard(focusedCard);
            }
        } else if (copyCard != null) {
            // Debug.e("CoDirectly revert");
            scrollable = false;
            revertCard(focusedCard);
        }
    }

    private void revertCard(final IEntity focusedCard) {
        final MoveModifier revertModifier = new MoveModifier(0.1f, copyCard.getX(), copyCard.getY(), SpriteUtils.toContainerOuterX(focusedCard),
                SpriteUtils.toContainerOuterY(focusedCard), new IEntityModifierListener() {
                    @Override
                    public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        scrollable = false;
                    }

                    @Override
                    public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                        partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
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
        partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
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
                    partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
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
                    partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            }
            partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    focusedCard.detachSelf();
                    CardPackScrollDetectorListener.this.partyEditScene.removedCards.put(cardEntry, focusedCard);
                    GameUserSession.getInstance().getCards().remove(cardEntry);
                    partyEditScene.attachChild(avatar);
                    pItem.detachSelf();

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
                    partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
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

                    partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            currentCard.clearEntityModifiers();
                            currentCard.registerEntityModifier(cardEditModifier);
                        }
                    });
                }
            }

            partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    focusedCard.detachSelf();
                    CardPackScrollDetectorListener.this.partyEditScene.removedCards.put(cardEntry, focusedCard);
                    GameUserSession.getInstance().getCards().remove(cardEntry);
                    if (cardPack.getChildCount() == 0) {
                        partyEditScene.revertCardToCardPack(beReplacedCardSprite);
                        beReplacedCardSprite.detachSelf();
                    }
                    pItem.detachSelf();
                    partyEditScene.attachChild(toReplaceCardAvatar);
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
            partyEditScene.revertCardToCardPack(replaceCardSprite);
            partyEditScene.getActivity().runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    replaceCardSprite.detachSelf();
                }
            });
            scrollable = true;
        }
    }

}