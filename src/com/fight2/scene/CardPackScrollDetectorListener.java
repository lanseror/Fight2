package com.fight2.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityComparator;
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
        if (cardPack.getChildCount() == 0 && !scrollable) {
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
                    final Card[] partyCards = GameUserSession.getInstance().getParties()[this.partyEditScene.partyNumber - 1];
                    boolean collidedWithGrid = false;
                    boolean isReplace = false;
                    IEntity replaceCardSprite = null;
                    Rectangle cardGrid = null;
                    int cardGridIndex = 0;
                    for (; cardGridIndex < this.partyEditScene.cardFrames.length; cardGridIndex++) {
                        if (this.partyEditScene.cardFrames[cardGridIndex].contains(copyCard.getX(), copyCard.getY())) {
                            collidedWithGrid = true;
                            replaceCardSprite = partyEditScene.addedCards[cardGridIndex];
                            isReplace = (replaceCardSprite == null ? false : true);
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
                        this.partyEditScene.addedCards[cardGridIndex] = copyCard;
                        cardGrid = this.partyEditScene.cardFrames[cardGridIndex];

                        final IEntityModifierListener modifierListener = isReplace ? new ReplacePartyCardModifierListener(focusedCard, cardEntry,
                                replaceCardSprite) : new AddPartyCardModifierListener(focusedCard, cardEntry);
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

        protected AddPartyCardModifierListener(final IEntity focusedCard, final Card cardEntry) {
            this.focusedCard = focusedCard;
            this.cardEntry = cardEntry;
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
                }
            });
            scrollable = true;
        }
    }

    protected class ReplacePartyCardModifierListener implements IEntityModifierListener {
        private final IEntity focusedCard;
        private final Card cardEntry;
        private final IEntity replaceCardSprite;

        protected ReplacePartyCardModifierListener(final IEntity focusedCard, final Card cardEntry, final IEntity replaceCardSprite) {
            this.focusedCard = focusedCard;
            this.cardEntry = cardEntry;
            this.replaceCardSprite = replaceCardSprite;
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
                        cardEditModifier.addModifierListener(new ReplacePartyCardFinishedModifierListener(replaceCardSprite));
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
                        cardEditModifier.addModifierListener(new ReplacePartyCardFinishedModifierListener(replaceCardSprite));
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
            {

                final Card replaceCardEntry = (Card) replaceCardSprite.getUserData();
                final IEntity revertCard = partyEditScene.removedCards.remove(replaceCardEntry);
                final Card revertCardEntry = (Card) revertCard.getUserData();
                int pCardIndex = 0;
                for (; pCardIndex < cardPack.getChildCount(); pCardIndex++) {
                    final IEntity tempCard = cardPack.getChildByIndex(pCardIndex);
                    final Card tempCardEntry = (Card) tempCard.getUserData();
                    if (revertCardEntry.getId() <= tempCardEntry.getId()) {
                        break;
                    }
                }
                final IEntity focusedCard = (IEntity) cardZoom.getUserData();
                final Card focusedCardEnty = (Card) (focusedCard != null ? focusedCard.getUserData() : null);
                final float cardZoomX = cardZoom.getX();
                final float cardPackLeft = cardPack.getX() - cardPack.getWidth() * 0.5f;
                final float focusedX = cardZoomX - cardPackLeft;
                if (cardPack.getChildCount() == 0) {
                    revertCard.setTag(0);
                    revertCard.setPosition(focusedX, PartyEditScene.CARD_Y);
                    cardZoom.setUserData(revertCard);
                } else if (focusedCardEnty != null) {
                    revertCard.setScale(1);
                    if (revertCardEntry.getId() <= focusedCardEnty.getId()) {
                        final IEntity leftMostCard = cardPack.getChildByIndex(0);
                        if (leftMostCard == focusedCard) {
                            final float revertX = focusedX - PartyEditScene.CARD_GAP - PartyEditScene.CARD_WIDTH * 1.5f;
                            revertCard.setPosition(revertX, revertCard.getY());
                        } else if (pCardIndex == 0) {
                            final float revertX = leftMostCard.getX() - PartyEditScene.CARD_GAP - PartyEditScene.CARD_WIDTH;
                            revertCard.setPosition(revertX, revertCard.getY());
                        } else {
                            revertCard.setPosition(cardPack.getChildByIndex(pCardIndex - 1));
                        }

                        for (int i = 0; i < pCardIndex; i++) {
                            final IEntity adjustCard = cardPack.getChildByIndex(i);
                            adjustCard.setPosition(adjustCard.getX() - PartyEditScene.CARD_GAP - PartyEditScene.CARD_WIDTH, adjustCard.getY());
                        }
                        revertCard.setTag(pCardIndex);
                        for (int i = pCardIndex; i < cardPack.getChildCount(); i++) {
                            final IEntity adjustCard = cardPack.getChildByIndex(i);
                            adjustCard.setTag(adjustCard.getTag() + 1);
                        }

                    } else {
                        final IEntity rightMostCard = cardPack.getChildByIndex(cardPack.getChildCount() - 1);
                        if (rightMostCard == focusedCard) {
                            final float revertX = focusedX + (PartyEditScene.CARD_GAP + PartyEditScene.CARD_WIDTH) * 1.5f;
                            revertCard.setPosition(revertX, revertCard.getY());
                        } else if (pCardIndex == cardPack.getChildCount()) {
                            final float revertX = rightMostCard.getX() + PartyEditScene.CARD_GAP + PartyEditScene.CARD_WIDTH;
                            revertCard.setPosition(revertX, revertCard.getY());
                        } else {
                            revertCard.setPosition(cardPack.getChildByIndex(pCardIndex));
                        }

                        for (int i = pCardIndex; i < cardPack.getChildCount(); i++) {
                            final IEntity adjustCard = cardPack.getChildByIndex(i);
                            adjustCard.setPosition(adjustCard.getX() + PartyEditScene.CARD_GAP - PartyEditScene.CARD_WIDTH, adjustCard.getY());
                            adjustCard.setTag(adjustCard.getTag() + 1);
                        }
                        revertCard.setTag(pCardIndex);

                    }
                }

                cardPack.attachChild(revertCard);
                revertCard.setAlpha(1f);
                GameUserSession.getInstance().getCards().add(replaceCardEntry);
                cardPack.sortChildren(new IEntityComparator() {
                    @Override
                    public int compare(final IEntity lhs, final IEntity rhs) {
                        final int leftTag = lhs.getTag();
                        final int rightTag = rhs.getTag();
                        return leftTag < rightTag ? -1 : 1;
                    }

                });

                partyEditScene.getActivity().runOnUpdateThread(new Runnable() {

                    @Override
                    public void run() {
                        replaceCardSprite.detachSelf();
                    }

                });

            }
            scrollable = true;
        }

    }
}