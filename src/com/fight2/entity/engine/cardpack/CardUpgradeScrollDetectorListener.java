package com.fight2.entity.engine.cardpack;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.util.modifier.IModifier;

import com.fight2.entity.Card;
import com.fight2.entity.engine.CardFrame;
import com.fight2.input.touch.detector.F2ScrollDetector;
import com.fight2.scene.CardUpgradeScene;
import com.fight2.scene.BaseCardPackScene.GridChangeAction;
import com.fight2.util.SpriteUtils;

public class CardUpgradeScrollDetectorListener extends CardPackScrollDetectorListener {
    private final CardUpgradeScene cardPackScene;

    public CardUpgradeScrollDetectorListener(final CardUpgradeScene cardPackScene, final CardPack cardPack, final IEntity cardZoom, final Card[] inGridCards) {
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
                        final IEntity addCardAvatar = cardGridIndex == 0 ? cardPackScene.createCardSprite(focusedCard, 206, 309) : cardPackScene
                                .createCardAvatarSprite(focusedCard, 97, 97);
                        addCardAvatar.setPosition(cardGrid);
                        addCardAvatar.setUserData(copyCard.getUserData());
                        this.cardPackScene.getInGridCardSprites()[cardGridIndex] = addCardAvatar;
                        cardPackScene.onGridCardsChange(cardGridIndex, GridChangeAction.Add);

                        final IEntityModifierListener modifierListener = new UpgradeAddCardModifierListener(focusedCardSprite, focusedCard, addCardAvatar);
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

    protected class UpgradeAddCardModifierListener extends AddCardModifierListener {

        protected UpgradeAddCardModifierListener(final CardFrame focusedCardSprite, final Card card, final IEntity avatar) {
            super(focusedCardSprite, card, avatar);
        }

        @Override
        public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
            super.onModifierFinished(pModifier, pItem);
        }
    }
}