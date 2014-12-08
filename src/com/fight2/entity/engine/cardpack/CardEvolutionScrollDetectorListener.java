package com.fight2.entity.engine.cardpack;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.util.modifier.IModifier;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.engine.CardFrame;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.scene.CardEvolutionScene;
import com.fight2.scene.BaseCardPackScene.GridChangeAction;
import com.fight2.util.SpriteUtils;

public class CardEvolutionScrollDetectorListener extends CardPackScrollDetectorListener {
    private final CardEvolutionScene cardPackScene;

    public CardEvolutionScrollDetectorListener(final CardEvolutionScene cardPackScene, final CardPack cardPack, final IEntity cardZoom, final Card[] inGridCards) {
        super(cardPackScene, cardPack, cardZoom, inGridCards);
        this.cardPackScene = cardPackScene;
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if (cardPack.getChildCount() == 0 || !scrollable) {
            return;
        }
        final F2ScrollDetector scrollDetector = (F2ScrollDetector) pScollDetector;
        final TouchEvent touchEvent = scrollDetector.getSceneTouchEvent();
        final float finishedY = touchEvent.getY();
        final CardFrame focusedCardSprite = (CardFrame) cardZoom.getUserData();
        if (focusedCardSprite.getScaleX() > 1.8 * CardUpdateHandler.SCALE_FACTOR) {
            // Debug.e("focusedCard.getScaleX() > 1.8");
            if (pPointerID == initPointerID && copyCard != null && focusedCardSprite.contains(initX, initY)
                    && Math.abs(initDistanceY) > Math.abs(initDistanceX)) {

                if (finishedY < SpriteUtils.toContainerOuterY(focusedCardSprite)) {
                    revertCard(focusedCardSprite);
                } else {
                    IEntity cardGrid = null;
                    int cardGridIndex = 0;
                    final IEntity[] cardGrids = this.cardPackScene.getCardGrids();

                    boolean hasPosition = false;
                    for (int partyCardIndex = 0; partyCardIndex < inGridCards.length; partyCardIndex++) {
                        final Card partyCard = inGridCards[partyCardIndex];
                        if (partyCard == null) {
                            hasPosition = true;
                            cardGridIndex = partyCardIndex;
                            break;
                        }
                    }

                    final Card focusedCard = (Card) focusedCardSprite.getUserData();
                    if (hasPosition) {
                        scrollable = false;
                        // Debug.e("Add card");

                        inGridCards[cardGridIndex] = focusedCard;
                        cardGrid = cardGrids[cardGridIndex];
                        final IEntity addCardAvatar = cardPackScene.createCardSprite(focusedCard, 206, 309);
                        addCardAvatar.setPosition(cardGrid);
                        addCardAvatar.setUserData(copyCard.getUserData());
                        this.cardPackScene.getInGridCardSprites()[cardGridIndex] = addCardAvatar;
                        cardPackScene.onGridCardsChange(cardGridIndex, GridChangeAction.Add);

                        final IEntityModifierListener modifierListener = new EvoAddCardModifierListener(focusedCardSprite, focusedCard, addCardAvatar);
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
                        cardPackScene.alert("队列已满！");
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

    protected class EvoAddCardModifierListener extends AddCardModifierListener {

        protected EvoAddCardModifierListener(final CardFrame focusedCardSprite, final Card card, final IEntity avatar) {
            super(focusedCardSprite, card, avatar);
        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {

            focusedCardSprite.setAlpha(0);
            final int focusedCardIndex = focusedCardSprite.getTag();
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
                    focusedCardSprite.detachSelf();
                    cardPack.removedCard(card, focusedCardSprite);
                    GameUserSession.getInstance().getCards().remove(card);
                    cardPackScene.attachChild(avatar);
                    pItem.detachSelf();
                    cardPackScene.sortChildren();
                }
            });

            scrollable = true;
        }
    }
}